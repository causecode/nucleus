/*
 * Copyright (c) 2016, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */
package com.causecode.core.currency

import static org.springframework.http.HttpStatus.NOT_FOUND
import com.causecode.util.NucleusUtils
import org.springframework.dao.DataIntegrityViolationException
import grails.plugin.springsecurity.annotation.Secured
import com.causecode.currency.Currency

/**
 * Provides end point and CRUD operations for Currency
 */

@Secured('ROLE_ADMIN')
class CurrencyController {

    static responseFormats = ['json']

    static allowedMethods = [save: 'POST', update: 'POST', delete: 'POST']

    def index() {
        redirect([action: 'list'])
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        [currencyInstanceList: Currency.list(params), currencyInstanceTotal: Currency.count()]
    }

    def create() {
        render(view: 'create')
    }

    def save(Currency currencyInstance) {
        if (!NucleusUtils.save(currencyInstance, true)) {
            flash.error = 'Cannot save invalid currency'
            render(view: 'create')
            return
        }
        redirect([action: 'list'])
    }

    def edit(Currency currencyInstance) {
        if (currencyInstance && currencyInstance.id) {
            render(view: 'edit', model: [currencyInstance: currencyInstance])
        } else {
            redirect([action: 'list'])
        }
    }

    def show(Currency currencyInstance) {
        if (currencyInstance && currencyInstance.id) {
            respond([currencyInstance: currencyInstance])
            return
        }
        render status: NOT_FOUND
    }

    def update(Currency currencyInstance) {
        if (currencyInstance && currencyInstance.id) {
            if (!NucleusUtils.save(currencyInstance, true)) {
                render(view: 'edit', model: [currencyInstance: currencyInstance])
                return
            }
        } else {
            flash.error = 'Selected currency does not exist'
        }
        redirect(action: 'list')
    }

    def delete(Currency currencyInstance) {
        if (currencyInstance && currencyInstance.id) {
            try {
                currencyInstance.delete([flush: true])
                redirect([action: 'list'])
            } catch (DataIntegrityViolationException e) {
                redirect([action: 'list'])
            }
        } else {
            flash.error = 'Selected currency does not exist'
            redirect([action: 'list'])
        }
    }
}
