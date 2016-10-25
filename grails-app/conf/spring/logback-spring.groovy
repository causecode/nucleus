/*
 * Copyright (c) 2016, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */

import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import grails.util.BuildSettings
import grails.util.Environment

def dateFormat = "yyyy-MM-dd'T'HHmmss"
GString loggingPattern = "%d{${dateFormat}} %-5level [${hostname}] %logger - %msg%n"

// For logging to console.
appender('STDOUT', ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = loggingPattern
    }
}

def logfileDate = timestamp(dateFormat)
GString logFilePath = "${System.properties['user.home']}/logs/causecode/${logfileDate}.log"

if (Environment.current in [Environment.DEVELOPMENT, Environment.TEST]) {
    def targetDir = BuildSettings.TARGET_DIR
    logFilePath = "${targetDir}/stacktrace.log"

    // Enable Spring Framework logs by passing the argument like 'grails -Dspring.logs=1 run-app'.
    if (System.properties['spring.logs'] == '1') {
        logger('org.springframework', DEBUG, ['STDOUT'], false)
    }

    if (System.properties['sql.logs'] == '1') {
        logger('org.hibernate', DEBUG, ['STDOUT'], false)
    }
}

// For logging to files.
appender('LOG_FILE', FileAppender) {
    file = logFilePath
    encoder(PatternLayoutEncoder) {
        pattern = loggingPattern
    }
}

root(ERROR, ['STDOUT', 'LOG_FILE'])

logger('grails.app', DEBUG, ['STDOUT', 'LOG_FILE'], false)
logger('StackTrace', ERROR, ['LOG_FILE'], false)