/*
 * Copyright (c) 2011-Present, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */
package com.causecode.converters

import grails.databinding.converters.FormattedValueConverter

/**
 * A convertor class used to provide String binding according to specified format in domain.
 * <code>
 *     @BindingFormat("LOWERCASE")
 *     String email
 * </code>
 *
 * @author Priyanshu Chauhan
 * @since 0.3.3
 */
class FormattedStringValueConverter implements FormattedValueConverter {

    /**
     * Field to Specify the type to which this converter may be applied.
     */
    final Class targetType = String

    /**
     * A generic method to convert incoming data to either lower case or upper case.
     * @param value String  whose value is to be converted to lower case.
     * @param format String the required conversion format.
     * @return String converted format.
     */
    Object convert(Object value, String format) {
        Object tempValue = value
        if (!tempValue) {
            return null
        }
        if (format == 'LOWERCASE') {
            tempValue = value.toLowerCase()
        } else {
            if (format == 'UPPERCASE') {
                tempValue = value.toUpperCase()
            }
        }
        return tempValue
    }
}
