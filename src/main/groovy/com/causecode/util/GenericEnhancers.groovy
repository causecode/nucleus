/*
 * Copyright (c) 2011-Present, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */
package com.causecode.util

import grails.artefact.Enhances
import org.grails.core.artefact.ControllerArtefactHandler
import org.grails.core.artefact.ServiceArtefactHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext

/**
 * This Trait is used to inject utility methods inside Controller and Service classes at compile time.
 *
 * @author Hardik Modha
 * @since v0.4.3
 */
@Enhances([ControllerArtefactHandler.TYPE, ServiceArtefactHandler.TYPE])
trait GenericEnhancers {

    @Autowired
    ApplicationContext applicationContext

    /**
     * This method is used to inject save method of NucleusUtils.
     * @param domainInstance instance to be saved
     * @param flush flush session when saving the instance or not
     * @return boolean indicating successful save or not
     */
    boolean save(Object domainInstance, boolean flush) {
        return NucleusUtils.save(domainInstance, flush)
    }

    /**
     * This method is used to inject sendEmail method of EmailService.
     * @param closure contains information about sending the mail.
     * @param eventName On Which event mail is being sent.
     */
    boolean sendEmail(Closure mailSettingsClosure, String eventName) {
        EmailService emailService = applicationContext.getBean(EmailService)
        return (emailService ? emailService.sendEmail(mailSettingsClosure, eventName) : false)
    }
}
