package com.jakewharton.rxbinding.support.design.widget

import com.google.android.material.behavior.SwipeDismissBehavior
import android.view.View
import rx.Observable

/**
 * Create an observable which emits the dismiss events from `view` on
 * [SwipeDismissBehavior].
 *
 * *Warning:* The created observable keeps a strong reference to `view`. Unsubscribe
 * to free this reference.
 */
inline fun View.dismisses(): Observable<View> = RxSwipeDismissBehavior.dismisses(this)
