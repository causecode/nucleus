package com.causecode.util

import grails.plugin.asyncmail.AsynchronousMailService
import groovy.util.logging.Slf4j
import org.springframework.mail.MailException

/**
 * This class is used for sending emails using the AsynchronousMailPlugin. It is a utility class that handles exception
 * while sending emails.
 *
 * @author Nikhil Sharma
 * @since 2.0.0
 */
@Slf4j
class GenericEmailService {

    AsynchronousMailService asynchronousMailService

    boolean sendEmail(Closure closure, String eventName) {
        try {
            asynchronousMailService.sendMail(closure)
        } catch (MailException e) {
            log.warn "Error sending email for $eventName", e

            return false
        }

        return true
    }
}
