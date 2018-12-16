package data.core

/**
 * Interface used as a callback, executed after a page finishes loading
 */
interface OnJavaScriptCompleted {
    fun onJavaScriptCompleted(html: String)
}
