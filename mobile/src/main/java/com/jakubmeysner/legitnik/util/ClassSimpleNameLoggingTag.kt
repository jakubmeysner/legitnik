package com.jakubmeysner.legitnik.util

interface ClassSimpleNameLoggingTag {
    val tag: String
        get() = this::class.simpleName.toString()
}
