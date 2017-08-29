import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender

scan()

appender("GLOBAL-CONSOLE", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "| %thread | %d{yyyy-MM-dd HH:mm:ss.SSS} | %-5level | %class{0}:%line - %msg%n"
    }
}

appender("GLOBAL-FILE", RollingFileAppender) {
    file = "pcu.log"
    append = true
    encoder(PatternLayoutEncoder) {
        pattern = "| %thread | %d{yyyy-MM-dd HH:mm:ss.SSS} | %-5level | %class{0}:%line - %msg%n"
    }
    rollingPolicy(TimeBasedRollingPolicy) {
        maxHistory = 14
        fileNamePattern = "pcu.%d{yyyy-MM-dd}.log"
    }
}

root(INFO, ["GLOBAL-CONSOLE", "GLOBAL-FILE"])


// Spring :
//logger("org.springframework", DEBUG)
//logger("org.springframework.beans", INFO)
//logger("org.springframework.beans.factory.support.DefaultListableBeanFactory", DEBUG)
//logger("org.springframework.core", INFO)
//logger("org.springframeworkcontext", INFO)
//logger("org.springframework.test", INFO)
//logger("org.springframework.web", INFO)
//logger("org.springframework", INFO)

// Spring Security :
//logger("org.springframework.security", INFO)
//logger("org.springframework.security.access.intercept.AbstractSecurityInterceptor", INFO) // USELESS?

// Apache CXF :
//logger("org.apache.cxf", DEBUG)
//logger("org.apache.cxf.jaxrs.utils.JAXRSUtils", INFO)
//logger("org.apache.cxf.phase.PhaseInterceptorChain", INFO)
// cxf request / response logging (enabled at WARNING even if code says INFO, but writes too much outside single tests) :
logger("org.apache.cxf.interceptor.LoggingInInterceptor", ERROR)
logger("org.apache.cxf.interceptor.LoggingOutInterceptor", ERROR)