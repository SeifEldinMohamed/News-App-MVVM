package com.seif.newsappmvvm.utils

// will be used to wrap around our network response.
// it's very useful to differentiate bet successful and error response also helping in loading state when we make a response.

// sealed class : it's a kind of abstract class but we can define which classes are allowed to inherit form that resource class.

sealed class Resource<T>(
        val data: T? = null,
        val message: String? = null
) {
    // the allowed classes to inherit from our Resource class
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    // class loading that will be returned when our request was fired off then we will emit that loading state
    // and when the response come we will emit that success or error state.
    class Loading<T>:Resource<T>()

}