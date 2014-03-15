
hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = false
    cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory' // Hibernate 3
//    cache.region.factory_class = 'org.hibernate.cache.ehcache.EhCacheRegionFactory' // Hibernate 4
}

dataSource {
    pooled = true
    url = System.getProperty('bertramlabs.db.url', "jdbc:mysql://localhost/timeseries")
    driverClassName = "com.mysql.jdbc.Driver"
    username = 'root'
    password = ''
    dbCreate = "update"
    logSql = false//System.getProperty('bertramlabs.db.logSql') == 'true' ? true : false
    dialect = org.hibernate.dialect.MySQL5InnoDBDialect
    println "db url: "+ url
    println "db username: "+username
    println "db password: "+password
    println "dbCreate: "+dbCreate
    println "logSql: "+logSql
    properties {
      validationQuery = 'select 1'
      testOnBorrow = true
      testOnReturn = false
      testWhileIdle = true
      timeBetweenEvictionRunsMillis = 300000
      numTestsPerEvictionRun = 3
      minEvictableIdleTimeMillis = 600000
      initialSize = 1
      minIdle = 1
      maxActive = 10
      maxIdle = 10000
      maxWait = 90000
      removeAbandoned = true
      removeAbandonedTimeout = 6000
      logAbandoned = true
    }
}
