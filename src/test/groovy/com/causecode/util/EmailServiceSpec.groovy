package com.causecode.util

import com.causecode.logger.ReplaceSlf4jLogger
import grails.plugin.asyncmail.AsynchronousMailService
import grails.test.mixin.TestFor
import org.junit.Rule
import org.slf4j.Logger
import org.springframework.mail.MailSendException
import spock.lang.Specification

/**
 * This class specifies unit test cases for {@link GenericEmailService}.
 */
@TestFor(GenericEmailService)
class EmailServiceSpec extends Specification {

    Logger logger = Mock(Logger)
    @Rule ReplaceSlf4jLogger replaceSlf4jLogger = new ReplaceSlf4jLogger(GenericEmailService, logger)

    void "test sendEmail method for exception"() {
        given: 'AsynchronousMailService is mocked to return true'
        service.asynchronousMailService = [sendMail: { Closure closure ->
            return true
        } ] as AsynchronousMailService

        when: 'sendEmail method is called with a template closure and eventName'
        boolean result = service.sendEmail({ }, 'testEvent')

        then: 'true is returned as no exception is thrown'
        result

        when: 'AsynchronousMailService is mocked to throw exception'
        service.asynchronousMailService = [sendMail: { Closure closure ->
            throw new MailSendException('Test Exception')
        } ] as AsynchronousMailService

        result = service.sendEmail({ }, 'testEvent')

        then: 'result should be false and logStatement should reflect the message'
        !result
        logger.warn('Error sending email for testEvent')
    }
}
