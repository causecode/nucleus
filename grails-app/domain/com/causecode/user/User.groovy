package com.causecode.user

import com.causecode.organization.embedded.EmOrganization
import com.causecode.user.embedded.EmUser
import grails.databinding.BindingFormat
import grails.plugin.springsecurity.SpringSecurityService
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.bson.types.ObjectId

/**
 * User groovy class used to specify person entity with default information.
 */
@ToString(includes = ['id', 'username'], includePackage = false)
@EqualsAndHashCode
@SuppressWarnings(['GrailsDomainWithServiceReference'])
class User {

    ObjectId id
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

    EmOrganization organization

    static transients = ['springSecurityService']

    static embedded = ['organization']

    static constraints = {
        email blank: false, email: true, unique: true
        gender inList: ['male', 'female', 'unspecified'], size: 4..11, nullable: true
        password blank: false, password: true
        username blank: false, unique: true
        birthdate nullable: true, max: new Date().clearTime()
        firstName maxSize: 100, nullable: true
        lastName maxSize: 100, nullable: true
        pictureURL nullable: true
        organization nullable: true
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

    /**
     * Method to get embedded instance of User
     */
    EmUser getEmbeddedInstance() {
        return new EmUser([instanceId: this.id, accountExpired: this.accountExpired, accountLocked: this.accountLocked,
                           enabled: this.enabled, email: this.email, firstName: this.firstName, lastName: this.lastName,
                           username: this.username])
    }
}
