package com.jakewharton.rxbinding.support.design.widget

import com.google.android.material.appbar.AppBarLayout
import rx.Observable

/**
 * Create an observable which emits the offset change in `view`.
 *
 * *Warning:* The created observable keeps a strong reference to `view`. Unsubscribe
 * to free this reference.
 */
inline fun AppBarLayout.offsetChanges(): Observable<Int> = RxAppBarLayout.offsetChanges(this)
