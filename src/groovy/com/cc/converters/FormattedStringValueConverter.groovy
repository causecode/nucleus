/*
 * Copyright (c) 2011-Present, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */

package com.cc.converters


class FormattedStringValueConverter implements FormattedValueConverter {

    /**
     * A generic method to convert incoming data to either lower case or upper case.
     * @param value String  whose value is to be converted to lower case.
     * @param format String the required conversion format.
     * @return String converted format.
     */
    def convert(def value, String format) {
        if (format == 'LOWERCASE') {
            value = value.toLowerCase()
        } else if (format == 'UPPERCASE') {
            value = value.toUpperCase()
        }
        return value
    }

    /**
     * Method to Specify the type to which this converter may be applied.
     * @return Class
     */
    Class getTargetType() {
        return String
    }
}