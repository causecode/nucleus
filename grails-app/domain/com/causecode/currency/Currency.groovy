/*
 * Copyright (c) 2016, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */
package com.causecode.currency

import groovy.transform.EqualsAndHashCode

/**
 * Used to store currency code and name information.
 *
 */
@EqualsAndHashCode
class Currency {

    Date dateCreated
    Date lastUpdated

    String code
    String name

    static constraints = {
        dateCreated bindable: false
        lastUpdated bindable: false
        code blank: false
        name blank: false
    }

    @Override
    String toString() {
        code
    }
}
