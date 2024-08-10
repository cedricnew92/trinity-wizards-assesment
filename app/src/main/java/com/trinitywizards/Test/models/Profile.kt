package com.trinitywizards.Test.models

data class Profile(
    var status: Status,
    var contact: Contact?
) {
    constructor(status: Status) : this(status, null)

    companion object {
        enum class Status {
            INIT_COMPLETE,
            UPDATE,
            LOGGED_OUT
        }
    }
}
