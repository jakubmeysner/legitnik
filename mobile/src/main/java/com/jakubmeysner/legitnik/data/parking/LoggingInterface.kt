package com.jakubmeysner.legitnik.data.parking

interface LoggingInterface {
    val TAG: String
        get() = this::class.simpleName.toString()
}
