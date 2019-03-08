package com.causecode.organization

import com.causecode.organization.embedded.EmOrganization
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.bson.types.ObjectId

/**
 * This domain represent an organization.
 *
 * @author Siddharth Shishulkar
 * @since 0.0.9
 */
@EqualsAndHashCode
@ToString(includes = ['id', 'name'], includePackage = false)
@SuppressWarnings('GrailsDomainReservedSqlKeywordName') // Not using SQL database. So reserved keyword won't matter.
class Organization {
    ObjectId id

    String name    // Name of the organization.
    String domain  // Domain of the organization.

    Date dateCreated
    Date lastUpdated

    static constraints = {
        dateCreated bindable: false
        lastUpdated bindable: false
    }

    /**
     * This getter returns an embedded instance of {link Organization}.
     * @return {@link com.causecode.organization.embedded.EmOrganization}
     */
    EmOrganization getEmbeddedInstance() {
        return new EmOrganization(name: name, domain: domain, instanceId: id)
    }
}