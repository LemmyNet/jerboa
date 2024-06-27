package com.jerboa.api

sealed class ApiAction<out T>(
    val data: T,
) {
    class Ok<T>(
        data: T,
    ) : ApiAction<T>(data)

    class Loading<T>(
        data: T,
    ) : ApiAction<T>(data)

    class Failed<T>(
        data: T,
        val err: Throwable,
    ) : ApiAction<T>(data)
}
