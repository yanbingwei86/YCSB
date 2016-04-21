//
// Built on Thu Apr 21 03:23:48 UTC 2016 by logback-translator
// For more information on configuration files in Groovy
// please see http://logback.qos.ch/manual/groovy.html

// For assistance related to this tool or configuration files
// in general, please contact the logback user mailing list at
//    http://qos.ch/mailman/listinfo/logback-user

// For professional support please see
//   http://www.qos.ch/shop/products/professionalSupport

import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender

import static ch.qos.logback.classic.Level.DEBUG
import static ch.qos.logback.classic.Level.WARN

appender("A1", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%-d{yyyy-MM-dd HH:mm:ss} [%c]-[%p] %m%n"
    }
}
logger("org.mongodb", WARN, ["A1"])
root(DEBUG, ["A1"])
