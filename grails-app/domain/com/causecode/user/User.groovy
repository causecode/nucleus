package com.causecode.user

import grails.databinding.BindingFormat
import grails.plugin.springsecurity.SpringSecurityService
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * User groovy class used to specify person entity with default information.
 */
@ToString(includes = ['id', 'username'], includePackage = false)
@EqualsAndHashCode
@SuppressWarnings(['GrailsDomainWithServiceReference'])
class User {

    SpringSecurityService springSecurityService
    boolean accountExpired
    boolean accountLocked
    boolean enabled = true
    boolean passwordExpired

    @BindingFormat('MM/dd/yyyy')
    Date birthdate

    Date dateCreated
    Date lastUpdated

    String email
    String firstName
    String gender
    String lastName
    String password
    String username
    String pictureURL

    static transients = ['springSecurityService']

    static constraints = {
        email blank: false, email: true, unique: true
        gender inList: ['male', 'female', 'unspecified'], size: 4..11, nullable: true
        password blank: false, password: true
        username blank: false, unique: true
        birthdate nullable: true, max: new Date().clearTime()
        firstName maxSize: 100, nullable: true
        lastName maxSize: 100, nullable: true
        pictureURL nullable: true
        dateCreated bindable: false
        lastUpdated bindable: false
    }

    static mapping = {
        /*
         * In Grails 3.3.5 Domain class autowiring is disabled by default due to its impact on performance so enabling
         * it for this domain class only.
         */
        autowire true
        password column: '`password`'
    }

    Set<Role> getAuthorities() {
        UserRole.findAllByUser(this)*.role as Set
    }

    def beforeInsert() {
        this.email = this.email.toLowerCase()
        encodePassword()
    }

    def beforeUpdate() {
        this.email = this.email.toLowerCase()
        if (isDirty('password')) {
            encodePassword()
        }
    }

    protected void encodePassword() {
        password = springSecurityService.encodePassword(password)
    }

    String getFullName() {
       return firstName + ' ' + lastName
    }
}
