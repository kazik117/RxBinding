package com.jakewharton.rxbinding.support.v4.widget;

import androidx.test.annotation.UiThreadTest;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.GeneralLocation;
import androidx.test.espresso.action.GeneralSwipeAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Swipe;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.jakewharton.rxbinding.RecordingObserver;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import com.jakewharton.rxbinding.support.v4.test.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public final class RxSwipeRefreshLayoutTest {
  @Rule public final ActivityTestRule<RxSwipeRefreshLayoutTestActivity> activityRule =
      new ActivityTestRule<>(RxSwipeRefreshLayoutTestActivity.class);

  private SwipeRefreshLayout view;

  @Before public void setUp() {
    RxSwipeRefreshLayoutTestActivity activity = activityRule.getActivity();
    view = activity.swipeRefreshLayout;
  }

  @Test public void refreshes() throws InterruptedException {
    RecordingObserver<Void> o = new RecordingObserver<>();
    Subscription subscription = RxSwipeRefreshLayout.refreshes(view)
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(o);
    o.assertNoMoreEvents();

    onView(withId(R.id.swipe_refresh_layout)).perform(swipeDown());
    o.takeNext();

    subscription.unsubscribe();
    onView(withId(R.id.swipe_refresh_layout)).perform(swipeDown());
    o.assertNoMoreEvents();
  }

  @Test @UiThreadTest public void refreshing() {
    Action1<? super Boolean> action = RxSwipeRefreshLayout.refreshing(view);
    assertThat(view.isRefreshing()).isFalse();

    action.call(true);
    assertThat(view.isRefreshing()).isTrue();

    action.call(false);
    assertThat(view.isRefreshing()).isFalse();
  }

  private static ViewAction swipeDown() {
    return new GeneralSwipeAction(Swipe.SLOW, GeneralLocation.TOP_CENTER,
        GeneralLocation.BOTTOM_CENTER, Press.FINGER);
  }
}
