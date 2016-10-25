/*
 * Copyright (c) 2011, CauseCode Technologies Pvt Ltd, India.
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

    private static final String ACTION_LIST = 'list'
    private static final String VIEW_EDIT = 'edit'
    private static final String VIEW_CREATE = 'create'
    static allowedMethods = [save: 'POST', update: 'PUT', delete: 'DELETE']
    private static final Map FLUSH_TRUE = [flush: true]
    private static final Map ACTION_LIST_MAP = [action: ACTION_LIST]

    def index() {
        redirect(action: ACTION_LIST)
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        [currencyInstanceList: Currency.list(params), currencyInstanceTotal: Currency.count()]
    }

    def create() {
        render(view: VIEW_CREATE)
    }

    def save(Currency currencyInstance) {
        if (!NucleusUtils.save(currencyInstance, true)) {
            flash.error = 'Cannot save invalid currency'
            render(view: VIEW_CREATE)
            return
        }
        redirect(action: ACTION_LIST)
    }

    def edit(Currency currencyInstance) {
        if (currencyInstance) {
            render(view: VIEW_EDIT, model: [currencyInstance: currencyInstance])
        } else {
            redirect(action: ACTION_LIST)
        }
    }

    def show(Currency currencyInstance) {
        if (currencyInstance) {
            respond([currencyInstance: currencyInstance])
            return
        }
        render status: NOT_FOUND
    }

    def update(Currency currencyInstance) {
        if (currencyInstance) {
            if (!NucleusUtils.save(currencyInstance, true)) {
                render(view: VIEW_EDIT, model: [currencyInstance: currencyInstance])
                return
            }
        } else {
            flash.error = 'Selected currency does not exist'
        }
        redirect(action: ACTION_LIST)
    }

    def delete(Currency currencyInstance) {
        if (currencyInstance) {
            try {
                currencyInstance.delete(FLUSH_TRUE)
                redirect(ACTION_LIST_MAP)
            } catch (DataIntegrityViolationException e) {
                redirect(action: ACTION_LIST)
            }
        } else {
            flash.error = 'Selected currency does not exist'
            redirect(ACTION_LIST_MAP)
        }
    }
}
