package com.jakewharton.rxbinding.support.v4.widget;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;

import com.jakewharton.rxbinding.view.ViewScrollChangeEvent;

import rx.Observable;

import static com.jakewharton.rxbinding.internal.Preconditions.checkNotNull;

public final class RxNestedScrollView {
  /**
   * Create an observable of scroll-change events for {@code view}.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}.
   * Unsubscribe to free this reference.
   */
  @CheckResult @NonNull public static Observable<ViewScrollChangeEvent> scrollChangeEvents(
      @NonNull NestedScrollView view) {
    checkNotNull(view, "view == null");
    return Observable.create(new NestedScrollViewScrollChangeEventOnSubscribe(view));
  }

  private RxNestedScrollView() {
    throw new AssertionError("No instances.");
  }
}
