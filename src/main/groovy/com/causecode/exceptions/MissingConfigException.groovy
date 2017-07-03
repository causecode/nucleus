package com.causecode.exceptions

/**
 * An exception class to represent missing configuration in config properties.
 *
 * @author Ankit Agrawal
 * @since 0.4.10
 */
class MissingConfigException extends Exception {

    MissingConfigException(String message = 'Missing configuration detected.', Throwable cause = new Throwable()) {
        super(message, cause)
    }
}
