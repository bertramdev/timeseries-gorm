class TimeSeriesGORMGrailsPlugin {
    def version = "0.1"
    def grailsVersion = "2.0 > *"
    def title = "Time Series GORM Plugin"
    def author = "Jeremy Leng"
    def authorEmail = "jleng@bcap.com"
    def description = '''\
GORM implementation of time series.
'''
    def dependsOn = [timeSeries: "* > 0.1-SNAPSHOT"]
    def documentation = "https://github.com/bertramdev/timeseries-gorm"
    def license = "APACHE"
    def organization = [ name: "BertramLabs", url: "http://www.bertramlabs.com/" ]
    def issueManagement = [ system: "GIT", url: "https://github.com/bertramdev/timeseries-gorm" ]
    def scm = [ url: "https://github.com/bertramdev/timeseries-gorm" ]
    def doWithApplicationContext = { ctx ->
        ctx['timeSeriesService'].registerProvider(new grails.plugins.timeseries.gorm.GORMTimeSeriesProvider())
    }
}
