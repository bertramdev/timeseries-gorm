package grails.plugins.timeseries.gorm

import grails.plugins.timeseries.*
import grails.converters.*
import groovy.transform.PackageScope

class GORMTimeSeriesProvider extends AbstractTimeSeriesProvider {
	public GORMTimeSeriesProvider() {
		super()
	}


	@Override
	void manageStorage(groovy.util.ConfigObject config) {
		// select distinct metric names
		def metrics = TimeSeriesMeasurement.executeQuery('select distinct metric from TimeSeriesMeasurement'),
			now = System.currentTimeMillis()
		log.debug(metrics.toString())
		metrics.each {metric->
			def expiration = getMillisecondExpirations(metric, config),
				oldest = new Date(now - expiration)
			TimeSeriesMeasurement.executeUpdate("delete TimeSeriesMeasurement a where a.metric = :metric and a.end < :end", [metric:metric, end:oldest])
			def aggInfo = getAggregateMillisecondExpirations(metric, config)
			aggInfo.each {res, dur->
				def aggOldest = new Date(now - dur)
				TimeSeriesAggregate.executeUpdate("delete TimeSeriesAggregate a where a.resolution = :res and a.metric = :metric and a.end < :end", [res:res, metric:metric, end:aggOldest])
			}
		}
		
	}

	@Override
	void init(groovy.util.ConfigObject config) {

	}

	@Override
	void shutDown(groovy.util.ConfigObject config) {
	}

	@Override
	void flush(groovy.util.ConfigObject config) {
		TimeSeriesMeasurement.list().each {
			it.delete()
		}
		TimeSeriesAggregate.list().each {
			it.delete()
		}
	}


	@Override
	String getName() {
		return 'gorm'
	}

	@Override
	String toString() {
		super.toString()
	}

	@Override
	void saveCounters(String referenceId, Map<String, Double> counters, Date timestamp, groovy.util.ConfigObject config) {
		def startAndInterval,
			aggregates
		counters.each {k, v->
			startAndInterval = getCounterStartAndInterval(k, timestamp, config)
			def rec = TimeSeriesCounter.findWhere(aggregate:false, resolution:startAndInterval.resolution, refId: referenceId, counter:k, start:startAndInterval.start, end: startAndInterval.end)
			if (!rec) {
				rec = new TimeSeriesCounter(aggregate:false, duration: startAndInterval.intervalSecs, resolution:startAndInterval.resolution, refId: referenceId, counter:k, start:startAndInterval.start, end: startAndInterval.end)
			}

			rec.total += v
			rec."count${startAndInterval.interval}" = rec."count${startAndInterval.interval}" != null ? rec."count${startAndInterval.interval}" : 0d
			rec."count${startAndInterval.interval}" += v
			rec.save()

			aggregates = getCounterAggregateStartsAndIntervals(k, timestamp, config)
			aggregates?.each {agg->
				def rec2 = TimeSeriesCounter.findWhere(aggregate:true, resolution:agg.resolution, refId: referenceId, counter:k, start:agg.start, end: agg.end)
				if (!rec2) {
					rec2 = new TimeSeriesCounter(aggregate:true, duration: agg.intervalSecs, resolution:agg.resolution, refId: referenceId, counter:k, start:agg.start, end: agg.end)
				}
				rec2."count${startAndInterval.interval}" = rec2."count${startAndInterval.interval}" != null ? rec2."count${startAndInterval.interval}" : 0d
				rec2."count${agg.interval}" = rec2."count${agg.interval}" ?: 0i
				rec2.total += v
				rec2."count${startAndInterval.interval}" += v
				if (!rec2.save()) {
					println rec2.errors
				}
			}
		}
	}


	@Override
	void saveMetrics(String referenceId, Map<String, Double> metrics, Date timestamp, groovy.util.ConfigObject config) {
		def startAndInterval,
			aggregates
		metrics.each {k, v->
			startAndInterval = getMetricStartAndInterval(k, timestamp, config)
			def prevValue = 0d,
			    rec = TimeSeriesMeasurement.findWhere(resolution:startAndInterval.resolution, refId: referenceId, metric:k, start:startAndInterval.start, end: startAndInterval.end)
			if (!rec) {
				rec = new TimeSeriesMeasurement(duration: startAndInterval.intervalSecs, resolution:startAndInterval.resolution, refId: referenceId, metric:k, start:startAndInterval.start, end: startAndInterval.end)
			}
			if (rec."col${startAndInterval.interval}") prevValue = rec."col${startAndInterval.interval}"

			if (prevValue) {
				rec.count--
				rec.total = rec.total - prevValue
			}
			rec.count++
			rec.total += v
			rec."col${startAndInterval.interval}" = v
			rec.save()

			aggregates = getAggregateStartsAndIntervals(k, timestamp, config)
			aggregates?.each {agg->
				def rec2 = TimeSeriesAggregate.findWhere(resolution:agg.resolution, refId: referenceId, metric:k, start:agg.start, end: agg.end)
				if (!rec2) {
					rec2 = new TimeSeriesAggregate(duration: agg.intervalSecs, resolution:agg.resolution, refId: referenceId, metric:k, start:agg.start, end: agg.end)
				}
				if (prevValue && rec2."count${agg.interval}") {
					rec2.count--
					rec2.total = rec.total - prevValue
					rec2."count${agg.interval}"--
					rec2."total${agg.interval}" = rec2."total${agg.interval}" = prevValue
				}
				rec2."count${agg.interval}" = rec2."count${agg.interval}" ?: 0i
				rec2."total${agg.interval}" = rec2."total${agg.interval}" ?: 0d
				rec2."count${agg.interval}"++
				if (rec2."max${agg.interval}" == null || v > rec2."max${agg.interval}")  rec2."max${agg.interval}" = v // overwrites can mess up max
				if (rec2."min${agg.interval}" == null || v < rec2."min${agg.interval}")  rec2."min${agg.interval}" = v // overwrite can mess up min
				if (rec2.high == null || v > rec2.high)  rec2.high = v // overwrites can mess up max
				if (rec2.low == null || v < rec2.low)  rec2.low = v // overwrite can mess up min
				rec2."total${agg.interval}"+=v
				if (!rec2.save()) {
					println rec2.errors
				}


			}
		}
	}

	@Override
	void bulkSaveCounters(String referenceId, List<Map<Date, Map<String, Double>>> countersByTime, groovy.util.ConfigObject config) {
		countersByTime.each {timestamp, counters->
			saveCounters(referenceId, counters, timestamp, config)
		}
	}

	@Override
	void bulkSaveMetrics(String referenceId, List<Map<Date, Map<String, Double>>> metricsByTime, groovy.util.ConfigObject config) {
		metricsByTime.each {timestamp, metrics->
			saveMetrics(referenceId, metrics, timestamp, config)
		}
	}

	@Override
	Map getCounters(Date start, Date end, String referenceIdQuery, String counterNameQuery, Map<String, Object> options, groovy.util.ConfigObject config) {
		def rtn = [:],
			res,
			recs = TimeSeriesCounter.createCriteria().list {
				eq('aggregate', false)
				if (referenceIdQuery) ilike('refId', referenceIdQuery)
				if (counterNameQuery) ilike('counter', counterNameQuery)
				gte('end', start)
				lte('start', end)
			}

		recs.each {rec->
			rtn[rec.refId] = rtn[rec.refId] ?: [:]
			rtn[rec.refId][rec.counter] = rtn[rec.refId][rec.counter] ?: []
			(0..95).each {idx->
				if (rec."count${idx}") {
					//println new Date(rec.start.time + (Long)(idx*rec.duration*1000))					
					rtn[rec.refId][rec.counter] << [timestamp:new Date(rec.start.time + (Long)(idx*rec.duration*1000)), count: rec."count${idx}"]
				}

			}
		}
		def items =[]
//		println new JSON(rtn).toString(true)
		rtn.each {k, v->
			def tmp = [referenceId: k, series:[]]
			v.each {m, vals->
				tmp.series << [name:m, values:vals]
			}
			items << tmp
		}
		[start:start, end:end, items:items]
	}


	@Override
	Map getMetrics(Date start, Date end, String referenceIdQuery, String metricNameQuery, Map<String, Object> options, groovy.util.ConfigObject config) {
		def rtn = [:],
			res,
			recs = TimeSeriesMeasurement.createCriteria().list {
				if (referenceIdQuery) ilike('refId', referenceIdQuery)
				if (metricNameQuery) ilike('metric', metricNameQuery)
				gte('end', start)
				lte('start', end)
			}

		recs.each {rec->
			rtn[rec.refId] = rtn[rec.refId] ?: [:]
			rtn[rec.refId][rec.metric] = rtn[rec.refId][rec.metric] ?: []
			(0..95).each {idx->
				if (rec."col${idx}") {
					//println new Date(rec.start.time + (Long)(idx*rec.duration*1000))					
					rtn[rec.refId][rec.metric] << [timestamp:new Date(rec.start.time + (Long)(idx*rec.duration*1000)), value: rec."col${idx}"]
				}

			}
		}
		def items =[]
//		println new JSON(rtn).toString(true)
		rtn.each {k, v->
			def tmp = [referenceId: k, series:[]]
			v.each {m, vals->
				tmp.series << [name:m, values:vals]
			}
			items << tmp
		}
		[start:start, end:end, items:items]
	}

	@Override
	Map getCounterAggregates(String resolution, Date start, Date end, String referenceIdQuery, String counterNameQuery, Map<String, Object> options, groovy.util.ConfigObject config) {
		def rtn = [:],
			res,
			recs = TimeSeriesCounter.createCriteria().list {
				eq('aggregate', true)
				if (referenceIdQuery) ilike('refId', referenceIdQuery)
				if (counterNameQuery) ilike('counter', counterNameQuery)
				eq('resolution', resolution)
				gte('end', start)
				lte('start', end)
			}
		recs.each {rec->
			rtn[rec.refId] = rtn[rec.refId] ?: [:]
			rtn[rec.refId][rec.counter] = rtn[rec.refId][rec.counter] ?: []
			(0..95).each {idx->
				if (rec."count${idx}") {
					rtn[rec.refId][rec.counter] << [start:new Date(rec.start.time + (Long)(idx*rec.duration*1000)), count: rec."count${idx}"]
				}
			}
		}
		def items =[]
//		println new JSON(rtn).toString(true)
		rtn.each {k, v->
			def tmp = [referenceId: k, series:[]]
			v.each {m, vals->
				tmp.series << [name:m, values:vals]
			}
			items << tmp
		}
		[start:start, end:end, items:items, resolution:resolution]

	}


	@Override
	Map getMetricAggregates(String resolution, Date start, Date end, String referenceIdQuery, String metricNameQuery, Map<String, Object> options, groovy.util.ConfigObject config) {
		def rtn = [:],
			res,
			recs = TimeSeriesAggregate.createCriteria().list {
				if (referenceIdQuery) ilike('refId', referenceIdQuery)
				if (metricNameQuery) ilike('metric', metricNameQuery)
				eq('resolution', resolution)
				gte('end', start)
				lte('start', end)
			}
		recs.each {rec->
			rtn[rec.refId] = rtn[rec.refId] ?: [:]
			rtn[rec.refId][rec.metric] = rtn[rec.refId][rec.metric] ?: []
			(0..95).each {idx->
				if (rec."count${idx}") {
					rtn[rec.refId][rec.metric] << [start:new Date(rec.start.time + (Long)(idx*rec.duration*1000)), average:(rec."total${idx}"/rec."count${idx}"), count: rec."count${idx}", sum: rec."total${idx}", max: rec."max${idx}", min: rec."min${idx}"]
				}
			}
		}
		def items =[]
//		println new JSON(rtn).toString(true)
		rtn.each {k, v->
			def tmp = [referenceId: k, series:[]]
			v.each {m, vals->
				tmp.series << [name:m, values:vals]
			}
			items << tmp
		}
		[start:start, end:end, items:items, resolution:resolution]

	}


}