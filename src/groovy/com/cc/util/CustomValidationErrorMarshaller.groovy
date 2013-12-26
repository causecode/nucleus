package com.cc.util

import grails.converters.JSON

import org.codehaus.groovy.grails.web.converters.exceptions.ConverterException
import org.codehaus.groovy.grails.web.converters.marshaller.ObjectMarshaller
import org.codehaus.groovy.grails.web.json.JSONWriter
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.validation.Errors
import org.springframework.validation.FieldError

class CustomValidationErrorMarshaller implements ObjectMarshaller<JSON>, ApplicationContextAware {

    private ApplicationContext applicationContext

    CustomValidationErrorMarshaller() {
    }

    CustomValidationErrorMarshaller(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext
    }

    boolean supports(Object object) {
        return object instanceof Errors
    }

    void marshalObject(Object object, JSON json) throws ConverterException {
        Errors errors = (Errors) object
        JSONWriter writer = json.getWriter()

        try {
            writer.object()
            writer.key("errors")
            writer.array()

            for (Object o : errors.getAllErrors()) {
                if (o instanceof FieldError) {
                    FieldError fe = (FieldError) o
                    writer.object()
                    json.property("field", fe.getField())
                    json.property("rejected-value", fe.getRejectedValue())
                    Locale locale = LocaleContextHolder.getLocale()
                    if (applicationContext != null) {
                        json.property("message", applicationContext.getMessage(fe, locale))
                    }
                    else {
                        json.property("message", fe.getDefaultMessage())
                    }
                    writer.endObject()
                }
            }
            writer.endArray()
            writer.endObject()
        }
        catch (ConverterException ce) {
            throw ce
        }
        catch (Exception e) {
            throw new ConverterException("Error converting Bean with class " + object.getClass().getName(), e)
        }
    }

    void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext
    }

}