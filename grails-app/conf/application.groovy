/*
 * Copyright (c) 2016, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */

environments {
    test {
        grails.test.appName = 'appName from application.groovy file'
        grails.test.appConfig = 'Configuration from application.groovy file'
        grails.gorm.autoFlush = true
    }
}