package com.jerboa.api

sealed class ApiState<out T> {
    abstract class Holder<T>(
        val data: T,
    ) : ApiState<T>()

    class Success<T>(
        data: T,
    ) : Holder<T>(data)

    class Appending<T>(
        data: T,
    ) : Holder<T>(data)

    class AppendingFailure<T>(
        data: T,
    ) : Holder<T>(data)

    class Failure(
        val msg: Throwable,
    ) : ApiState<Nothing>()

    data object Loading : ApiState<Nothing>()

    data object Refreshing : ApiState<Nothing>()

    data object Empty : ApiState<Nothing>()
}
