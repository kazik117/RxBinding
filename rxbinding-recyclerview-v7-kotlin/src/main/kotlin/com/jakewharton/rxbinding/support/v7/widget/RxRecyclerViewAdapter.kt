package com.jakewharton.rxbinding.support.v7.widget

import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import rx.Observable

/**
 * Create an observable of data change events for `RecyclerView.adapter`.
 *
 * *Note:* A value will be emitted immediately on subscribe.
 */
inline fun <T : Adapter<out ViewHolder>> T.dataChanges(): Observable<T> = RxRecyclerViewAdapter.dataChanges(this)
