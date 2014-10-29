/*
 * Copyright (c) 2011, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */

package com.cc.core.currency

import org.springframework.dao.DataIntegrityViolationException
import grails.plugin.springsecurity.annotation.Secured

import com.cc.currency.Currency

@Secured(["ROLE_ADMIN"])
class CurrencyController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def beforeInterceptor = [action: this.&validate]

    Currency currencyInstance

    private validate() {
        if(!params.id) return true;

        currencyInstance = Currency.get(params.id)
        if(!currencyInstance) {
            flash.message = g.message(code: 'default.not.found.message', args: [message(code: 'currency.label'), params.id])
            redirect(action: "list")
            return false
        }
        return true
    }

    def index() {
        redirect(action: "list", params: params)
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        [currencyInstanceList: Currency.list(params), currencyInstanceTotal: Currency.count()]
    }

    def create() {
        [currencyInstance: new Currency(params)]
    }

    def save() {
        currencyInstance = new Currency(params)
        if (!currencyInstance.save(flush: true)) {
            render(view: "create", model: [currencyInstance: currencyInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'currency.label'), currencyInstance.id])
        redirect(action: "list", id: currencyInstance.id)
    }

    def edit(Long id) {
        [currencyInstance: currencyInstance]
    }

    def show(Long id) {
        [currencyInstance: currencyInstance]
    }

    def update(Long id, Long version) {
        if(version != null) {
            if (currencyInstance.version > version) {
                currencyInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                        [message(code: 'currency.label')] as Object[],
                        "Another user has updated this Currency while you were editing")
                render(view: "edit", model: [currencyInstance: currencyInstance])
                return
            }
        }

        currencyInstance.properties = params

        if (!currencyInstance.save(flush: true)) {
            render(view: "edit", model: [currencyInstance: currencyInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'currency.label'), currencyInstance.id])
        redirect(action: "list", id: currencyInstance.id)
    }

    def delete(Long id) {
        try {
            currencyInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'currency.label'), id])
            redirect(action: "list")
        } catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'currency.label'), id])
            redirect(action: "list", id: id)
        }
    }
}
