package com.trinitywizards.Test.models

import java.io.Serializable

class Contact(
    var type: Int,
    var text: String,
    var id: String,
    var firstname: String,
    var lastname: String,
    var email: String?,
    var dob: String?,
    var you: Boolean = false
) : Serializable {

    constructor(id: String, firstname: String, lastname: String, email: String, dob: String) : this(
        CELL, "", id, firstname, lastname, email, dob)

    constructor(type: Int, text: String) : this(type, text, "", "", "", "", "")

    constructor() : this(0, "", "", "", "", null, null)

    companion object {
        const val HEADER = 0
        const val CELL = 1
    }
}
