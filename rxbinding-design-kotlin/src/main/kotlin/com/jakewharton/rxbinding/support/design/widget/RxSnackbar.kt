package com.jakewharton.rxbinding.support.design.widget

import com.google.android.material.snackbar.Snackbar
import rx.Observable

/**
 * Create an observable which emits the dismiss events from `view`.
 *
 * *Warning:* The created observable keeps a strong reference to `view`. Unsubscribe
 * to free this reference.
 */
inline fun Snackbar.dismisses(): Observable<Int> = RxSnackbar.dismisses(this)
