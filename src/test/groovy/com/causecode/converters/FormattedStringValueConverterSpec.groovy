package com.causecode.converters

import spock.lang.Specification

/**
 * This class describes unit test cases for {@link com.causecode.converters.FormattedStringValueConverter}
 */
class FormattedStringValueConverterSpec extends Specification {

    FormattedStringValueConverter converter

    def setup() {
        converter = new FormattedStringValueConverter()
    }

    void 'test convert method'() {
        when: 'converter method is called with null value'
        String result = converter.convert(null, 'LOWERCASE')

        then: 'result must be null'
        result == null

        when: 'convert method is called with format as lowercase'
        result = converter.convert('CAUSECODE', 'LOWERCASE')

        then: 'result must be in lowercase'
        result == 'causecode'

        when: 'convert method is called with format as uppercase'
        result = converter.convert('causecode', 'UPPERCASE')

        then: 'result must be in uppercase'
        result == 'CAUSECODE'
    }

    void 'test getTargetType method'() {
        when: 'getTargetType method is called'
        Class targetType = converter.targetType

        then: 'It must return targetType as String'
        targetType == String
    }
}
