import ch.qos.logback.classic.Level
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy

def PATTERN = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
def location = "build/logs/versions.log"

appender("CONSOLE", ConsoleAppender) {
  encoder(PatternLayoutEncoder) {
    pattern = PATTERN
  }
}

appender("ROLLING", RollingFileAppender) {
  file = location
  encoder(PatternLayoutEncoder) {
    pattern = PATTERN
  }

  rollingPolicy(TimeBasedRollingPolicy) {
    fileNamePattern = location.minus(".log") + "-%d{MM-dd}.log"
  }
}

root(Level.INFO, ["CONSOLE", "ROLLING"])

logger("com.vevo.versions", Level.INFO)
logger("com.getsentry.raven", Level.WARN)
logger("org.eclipse.jetty", Level.ERROR)
