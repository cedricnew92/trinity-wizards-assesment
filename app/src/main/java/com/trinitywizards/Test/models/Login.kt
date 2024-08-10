package com.trinitywizards.Test.models

data class Login(
    var status: Status
) {
    companion object {
        enum class Status {
            INIT_COMPLETE,
            CACHED,
            NOT_CACHED,
            PASSED,
            FAILED
        }
    }
}