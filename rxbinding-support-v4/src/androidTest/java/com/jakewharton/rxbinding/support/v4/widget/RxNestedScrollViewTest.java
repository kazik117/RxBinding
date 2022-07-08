package com.jakewharton.rxbinding.support.v4.widget;

import androidx.test.annotation.UiThreadTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.core.widget.NestedScrollView;

import com.jakewharton.rxbinding.RecordingObserver;
import com.jakewharton.rxbinding.view.ViewScrollChangeEvent;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import rx.Subscription;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public final class RxNestedScrollViewTest {
  @Rule public final ActivityTestRule<RxNestedScrollViewTestActivity> activityRule =
      new ActivityTestRule<>(RxNestedScrollViewTestActivity.class);

  private NestedScrollView view;

  @Before public void setUp() {
    RxNestedScrollViewTestActivity activity = activityRule.getActivity();
    view = activity.nestedScrollView;
  }

  @Test @UiThreadTest public void scrollChangeEvents() {
    RecordingObserver<ViewScrollChangeEvent> o = new RecordingObserver<>();
    Subscription subscription = RxNestedScrollView.scrollChangeEvents(view).subscribe(o);
    o.assertNoMoreEvents();

    view.scrollTo(1000, 0);
    ViewScrollChangeEvent event = o.takeNext();
    assertThat(event.view()).isSameAs(view);
    assertThat(event.scrollX()).isEqualTo(1000);
    assertThat(event.scrollY()).isEqualTo(0);
    assertThat(event.oldScrollX()).isEqualTo(0);
    assertThat(event.oldScrollY()).isEqualTo(0);

    subscription.unsubscribe();
    view.scrollTo(2000, 0);
    o.assertNoMoreEvents();
  }
}
