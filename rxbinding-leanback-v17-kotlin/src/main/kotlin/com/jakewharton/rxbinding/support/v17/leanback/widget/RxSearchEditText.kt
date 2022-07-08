package com.jakewharton.rxbinding.support.v17.leanback.widget

import androidx.leanback.widget.SearchEditText
import rx.Observable
import com.jakewharton.rxbinding.internal.VoidToUnit

/**
 * Create an observable which emits the keyboard dismiss events from `view`.
 *
 * *Warning:* The created observable keeps a strong reference to `view`. Unsubscribe
 * to free this reference.
 */
inline fun SearchEditText.keyboardDismisses(): Observable<Unit> = RxSearchEditText.keyboardDismisses(this).map(VoidToUnit)
