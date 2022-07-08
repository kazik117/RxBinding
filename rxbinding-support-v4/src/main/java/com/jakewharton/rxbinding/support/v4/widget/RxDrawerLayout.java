package com.jakewharton.rxbinding.support.v4.widget;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import rx.Observable;
import rx.functions.Action1;

import static com.jakewharton.rxbinding.internal.Preconditions.checkNotNull;

public final class RxDrawerLayout {
  /**
   * Create an observable of the open state of the drawer of {@code view}.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   * <p>
   * <em>Note:</em> A value will be emitted immediately on subscribe.
   */
  @CheckResult @NonNull
  public static Observable<Boolean> drawerOpen(@NonNull DrawerLayout view, int gravity) {
    checkNotNull(view, "view == null");
    return Observable.create(new DrawerLayoutDrawerOpenedOnSubscribe(view, gravity));
  }

  /**
   * An action which sets whether the drawer with {@code gravity} of {@code view} is open.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   */
  @CheckResult @NonNull
  public static Action1<? super Boolean> open(@NonNull final DrawerLayout view, final int gravity) {
    checkNotNull(view, "view == null");
    return new Action1<Boolean>() {
      @Override public void call(Boolean aBoolean) {
        if (aBoolean) {
          view.openDrawer(gravity);
        } else {
          view.closeDrawer(gravity);
        }
      }
    };
  }

  private RxDrawerLayout() {
    throw new AssertionError("No instances.");
  }
}
