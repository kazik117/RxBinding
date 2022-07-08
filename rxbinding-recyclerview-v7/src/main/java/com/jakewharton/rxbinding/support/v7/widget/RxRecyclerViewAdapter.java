package com.jakewharton.rxbinding.support.v7.widget;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import rx.Observable;

import static com.jakewharton.rxbinding.internal.Preconditions.checkNotNull;

/** Static factory methods for creating {@linkplain Observable observables} for {@link Adapter}. */
public final class RxRecyclerViewAdapter {
  /**
   * Create an observable of data change events for {@code RecyclerView.adapter}.
   * <p>
   * <em>Note:</em> A value will be emitted immediately on subscribe.
   */
  @CheckResult @NonNull
  public static <T extends Adapter<? extends ViewHolder>> Observable<T> dataChanges(
      @NonNull T adapter) {
    checkNotNull(adapter, "adapter == null");
    return Observable.create(new RecyclerAdapterDataChangeOnSubscribe<>(adapter));
  }

  private RxRecyclerViewAdapter() {
    throw new AssertionError("No instances.");
  }
}
