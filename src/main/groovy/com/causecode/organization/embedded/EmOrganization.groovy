package com.causecode.organization.embedded

import com.causecode.mongo.embeddable.EmbeddableDomain
import org.bson.types.ObjectId

/**
 * Embedded class for the Organization domain.
 */
class EmOrganization implements EmbeddableDomain {
    ObjectId instanceId

    String name
    String domain
}
