package com.jakewharton.rxbinding.support.design.widget;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import com.google.android.material.behavior.SwipeDismissBehavior;
import android.view.View;
import rx.Observable;

import static com.jakewharton.rxbinding.internal.Preconditions.checkNotNull;

/**
 * Static factory methods for creating {@linkplain Observable observables}
 * for {@link SwipeDismissBehavior} on (@link View).
 */
public final class RxSwipeDismissBehavior {
  /**
   * Create an observable which emits the dismiss events from {@code view} on
   * {@link SwipeDismissBehavior}.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   */
  @CheckResult @NonNull
  public static Observable<View> dismisses(@NonNull View view) {
    checkNotNull(view, "view == null");
    return Observable.create(new SwipeDismissBehaviorOnSubscribe(view));
  }

  private RxSwipeDismissBehavior() {
    throw new AssertionError("No instances.");
  }
}
