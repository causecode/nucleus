package com.causecode.util

import java.lang.reflect.Constructor;

import grails.converters.JSON

import org.grails.web.converters.exceptions.ConverterException
import org.grails.web.converters.marshaller.ObjectMarshaller
import org.grails.web.json.JSONWriter
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.validation.Errors
import org.springframework.validation.FieldError

/**
 * A generic Marshaller for custom validation error.
 * @author Shashank Agrawal
 *
 */
class CustomValidationErrorMarshaller implements ObjectMarshaller<JSON>, ApplicationContextAware {

    /**
     * Dependency injection for the {@link ApplicationContext}
     */
    private ApplicationContext applicationContext

    /**
     * Constructor
     */
    CustomValidationErrorMarshaller() {
    }

    /**
     * Constructor
     * 
     * Set current scope application context.
     * @param applicationContext Interface provides configuration for an application.
     */
    CustomValidationErrorMarshaller(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext
    }

    /**
     * Checks Object instance is Error or not.
     * @param object Instance to be checked for errors
     * @return Boolean value checking object accepted is instance of Errors or not.
     */
    boolean supports(Object object) {
        return object instanceof Errors
    }

    /**
     * This method used to marshal object errors into JSON Object with customized messages.
     * @param object Instance containing errors passed to marshal.
     * @throws ConverterException
     */
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

    /**
     * This method used to set current scope application context.
     * @param applicationContext Interface provides configuration for an application.
     */
    void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext
    }

}
