package com.trinitywizards.Test.models

import java.util.ArrayList

data class Contacts(
    var status: Status,
    var contacts: ArrayList<Contact>?,
    var contact: Contact?
) {
    constructor(status: Status) : this(status, null, null)

    constructor(status: Status, contacts: ArrayList<Contact>) : this(status, contacts, null)

    constructor(status: Status, contact: Contact) : this(status, null, contact)

    companion object {
        enum class Status {
            INIT_COMPLETE,
            EDIT,
            SEARCH
        }
    }
}
