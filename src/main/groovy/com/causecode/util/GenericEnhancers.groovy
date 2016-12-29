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

/**
 * trait to inject methods inside Controllers and Service classes at Compile time.
 * @author Hardik Modha
 * @since v0.4.3
 */
@Enhances([ControllerArtefactHandler.TYPE, ServiceArtefactHandler.TYPE])
trait GenericEnhancers {

    /**
     * This method is injected in all controllers and services to provide easy access to save instance.
     * @param domainInstance instance to be saved
     * @param flush flush session when saving the instance or not
     * @return boolean indicating successful save or not
     */
    boolean save(Object domainInstance, boolean flush) {
        NucleusUtils.save(domainInstance, flush)
    }
}
