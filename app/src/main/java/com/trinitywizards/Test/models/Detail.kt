package com.trinitywizards.Test.models

data class Detail(
    var status: Status,
    var contact: Contact?
) {
    constructor(status: Status) : this(status, null)

    companion object {
        enum class Status {
            INIT_COMPLETE,
            INIT_COMPLETE_WITH_CONTACT,
            SAVED_OR_UPDATED,
            DELETED
        }
    }
}