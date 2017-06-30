/*
 * Copyright (c) 2011-Present, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */
package com.causecode.exceptions

/**
 * An exception class to represent invalid or missing database configuration in config properties.
 * @author Hardik Desai
 * @since 0.4.10
 */
class DBTypeNotFoundException extends Exception {
      DBTypeNotFoundException(String message, Throwable cause = new Throwable()) {
          super(message, cause)
      }
}
