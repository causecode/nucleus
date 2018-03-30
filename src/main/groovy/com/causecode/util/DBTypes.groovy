package com.causecode.util

/**
 * enum to hold different database types.
 *
 * @author Ankit Agrawal
 * @since v0.4.10
 */
enum DBTypes {
    MYSQL(0),
    MONGO(1)

    final int id

    DBTypes(int id) {
        this.id = id
    }

    @Override
    String toString() {
        return "${this.name()}($id)"
    }
}
