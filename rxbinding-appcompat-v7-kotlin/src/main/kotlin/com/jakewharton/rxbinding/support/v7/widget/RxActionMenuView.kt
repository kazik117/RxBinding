package com.jakewharton.rxbinding.support.v7.widget

import androidx.appcompat.widget.ActionMenuView
import android.view.MenuItem
import rx.Observable

/**
 * Create an observable which emits the clicked menu item in `view`.
 *
 * *Warning:* The created observable keeps a strong reference to `view`.
 * Unsubscribe to free this reference.
 */
inline fun ActionMenuView.itemClicks(): Observable<MenuItem> = RxActionMenuView.itemClicks(this)
