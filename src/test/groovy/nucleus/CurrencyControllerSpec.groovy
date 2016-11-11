/*
 * Copyright (c) 2011-Present CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */
package nucleus

import com.causecode.core.currency.CurrencyController
import com.causecode.currency.Currency
import com.causecode.util.NucleusUtils
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.springframework.dao.DataIntegrityViolationException
import spock.lang.Specification
import spock.util.mop.ConfineMetaClassChanges

import static org.springframework.http.HttpStatus.NOT_FOUND

@TestFor(CurrencyController)
@Mock(Currency)
class CurrencyControllerSpec extends Specification {

    void 'test list action with valid parameter'() {
        when: 'action list is called with parameter greater than 100'
        controller.params.max = 1000
        controller.request.method = 'POST'
        controller.list()

        then: 'params.max must contain 100'
        params.max == 100

        when: 'action list is called with parameter less than 100'
        controller.params.max = 10
        controller.request.method = 'POST'
        controller.list()

        then: 'params.max must contain 10'
        params.max == 10
    }

    void 'test redirect from index to list'() {
        when: 'user visits the base url'
        controller.index()

        then: 'user is redirected to action list'
        response.redirectedUrl == '/currency/list'
    }

    def 'test currency create action'() {
        when: 'action create is called'
        controller.create()

        then: 'screen to create currency should be displayed'
        view == '/currency/create'
    }

    void 'test save action'() {
        given: 'valid currency instance to save'
        int currencyTotal = Currency.count()

        when: 'action create is called'
        controller.request.method = 'POST'
        controller.params.currencyInstance = [dateCreated: new Date(), dateUpdated: new Date(), code: 'GBP',
        name : 'Pound']
        controller.save()

        then: 'user must be redirected to action list'
        Currency.count() == currencyTotal + 1
        controller.request.error == null
        response.redirectedUrl == '/currency/list'
    }

    def 'test save with null instance of Currency'() {
        when: 'action create is called'
        int currencyTotal = Currency.count()
        controller.request.method = 'POST'
        controller.save()

        then: 'user must be redirected to action list'
        view == '/currency/create'
        Currency.count() == currencyTotal
    }

    def 'test save with invalid instance of Currency'() {
        given: 'Total count of currencyInstances'
        int currencyTotal = Currency.count()

        when: 'action save is called'
        controller.request.method = 'POST'
        controller.params.currencyInstance = [dateCreated: new Date(), dateUpdated: new Date(), code: null, name: 'Pound']
        controller.save()

        then: 'create view must be rendered'
        view == '/currency/create'
        Currency.count() == currencyTotal
    }

    void 'test edit with null instance of Currency'() {
        when: 'action edit is called'
        controller.request.method = 'PUT'
        controller.edit()

        then: 'user must be redirected to action list'
        response.redirectedUrl == '/currency/list'
    }

    def 'test edit action for valid instance of Currency'() {
        when: 'action edit is called'
        Currency currencyInstance = new Currency([dateCreated: new Date(), dateUpdated: new Date(), code: 'USD', name: 'US Dollar'])
        assert currencyInstance.save(failOnError: true, flush: true)

        controller.request.method = 'PUT'
        controller.request.json = [id: currencyInstance.id.toString()]
        controller.edit()

        then: 'edit view must be rendered'
        view == '/currency/edit'
        controller.modelAndView.model.currencyInstance.code == 'USD'
    }

    void 'test show with null instance of Currency'() {
        when: 'action show is called'
        controller.request.method = 'GET'
        controller.show()

        then: 'Not Found(404) status code must be returned'
        response.status == NOT_FOUND.value()
    }

    void 'test show action for valid instance of Currency'() {
        when: 'action show is called'
        Currency currency = new Currency([dateCreated: new Date(), dateUpdated: new Date(), code: 'USD', name: 'US Dollar'])
        assert currency.save(failOnError: true, flush: true)
        controller.request.json = [id: currency.id.toString()]
        controller.request.method = 'GET'
        controller.show()

        then: 'show view must be rendered'
        response.json.currencyInstance.code == 'USD'
    }

    void 'test update action for null instance of Currency'() {
        when: 'action update is called'
        controller.request.method = 'PUT'
        controller.update()

        then: 'user must be redirected to action list'
        response.redirectedUrl == '/currency/list'
    }

    @ConfineMetaClassChanges(NucleusUtils)
    def 'test update action when instance of Currency cannot be saved'() {
        given: 'Valid instance of Currency'
        Currency currency = new Currency([dateCreated: new Date(), dateUpdated: new Date(), code: 'USD', name: 'US Dollar'])
        assert currency.save(failOnError: true, flush: true)

        NucleusUtils.metaClass.'static'.save = { Object domainInstance, boolean flush, def log = logger ->
            return false
        }

        when: 'action update is called'
        controller.request.method = 'PUT'
        controller.request.json = [id: currency.id.toString()]
        controller.update()

        then: 'User must be redirected to List'
        view == '/currency/edit'
    }

    void 'test update action for valid instance of Currency'() {
        given: 'Valid instance of Currency'
        Currency currency = new Currency([dateCreated: new Date(), dateUpdated: new Date(), code: 'INR', name: 'Indian Rupees'])
        assert currency.save(failOnError: true, flush: true)
        controller.request.json = [id: currency.id.toString()]
        controller.request.method = 'PUT'

        when: 'action update is called'
        controller.update()

        then: 'user must be redirected to action list'
        response.redirectedUrl == '/currency/list'
        Currency.countByCode('INR') == 1
    }

    void 'test delete with null instance of Currency'() {
        when: 'action delete is called'
        controller.request.method = 'DELETE'
        controller.request.json = [id: null]
        controller.delete()

        then: 'user must be redirected to action list'
        response.redirectedUrl == '/currency/list'
    }

    void 'test delete action for invalid instance of Currency'() {
        given: 'non-existing currency instance to delete'
        int currencyTotal = Currency.count()

        when: 'action delete is called'
        controller.request.method = 'DELETE'
        controller.request.params = [dateCreated: new Date(), dateUpdated: new Date(), code: 'GBP', name: 'Pound']
        controller.delete()

        then: 'user must be redirected to action list'
        response.redirectedUrl == '/currency/list'
        Currency.count() == currencyTotal
    }


    void 'test delete action for valid instance of Currency'() {
        given: 'existing instance of Currency'

        new Currency([dateCreated: new Date(), dateUpdated: new Date(),code: 'GBP',name: 'Pound']).save(failOnError: true, flush: true)

        Currency currencyInstance = new Currency([dateCreated: new Date(), dateUpdated: new Date(),
        code: 'eu', name: 'Euro']).save(failOnError: true, flush: true)

        int currencyTotal = Currency.count()

        when: 'action delete is called'
        controller.request.json = [id: currencyInstance.id.toString()]
        controller.request.method = 'DELETE'
        controller.delete()

        then: 'user must be redirected to action list'
        response.redirectedUrl == '/currency/list'
        Currency.count() == currencyTotal - 1
    }

    void 'test delete action when exception is thrown'() {
         given: 'CurrencyInstance to delete'
         Currency currencyInstance = new Currency([dateCreated: new Date(), dateUpdated: new Date(),
                       code: 'GBP', name: 'Pound']).save(failOnError: true, flush: true)
         assert Currency.count() == 1

         Currency.metaClass.delete = { Map params ->
             throw new DataIntegrityViolationException()
         }

         when: 'action delete is called'
         controller.request.method = 'DELETE'
         controller.request.json = [id : currencyInstance.id.toString()]
         controller.delete()

         then: 'User is redirected to action list'
         response.redirectedUrl == '/currency/list'
         Currency.count() == 0
     }
}