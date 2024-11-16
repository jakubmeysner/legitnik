package com.jakubmeysner.legitnik.data.parking

interface ClassSimpleNameLoggingTag {
    val tag: String
        get() = this::class.simpleName.toString()
}
