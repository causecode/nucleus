/*
 * Copyright (c) 2011, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */

package com.cc.currency

/**
 * Used to store currency code and name information.
 *
 */

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