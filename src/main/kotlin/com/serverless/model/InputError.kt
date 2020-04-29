package com.serverless.model

class InputError(val errors: Map<String, List<String>>) : Throwable("InputError") {
    companion object {
        fun build(field: String, error: String) = InputError(mapOf(field to listOf(error)))

        fun build(vararg pairs: Pair<String, List<String>>) = InputError(mapOf(*pairs))
    }
}
