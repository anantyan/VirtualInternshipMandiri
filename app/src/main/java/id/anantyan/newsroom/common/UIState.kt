package id.anantyan.newsroom.common

sealed class UIState<T>(
    val data: T? = null,
    val message: String? = null,
    val code: Int? = null
) {
    class Success<T>(data: T) : UIState<T>(data = data)
    class Error<T>(code: Int?, message: String) : UIState<T>(code = code, message = message)
    class Loading<T> : UIState<T>()
}