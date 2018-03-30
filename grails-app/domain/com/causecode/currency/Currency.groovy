package com.causecode.currency

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * Used to store currency code and name information.
 *
 */
@ToString(includes = ['code'], includePackage = false)
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
}
