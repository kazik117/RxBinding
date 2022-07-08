package com.jakewharton.rxbinding.support.v4.widget;

import android.app.Instrumentation;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.drawerlayout.widget.DrawerLayout;
import com.jakewharton.rxbinding.RecordingObserver;
import com.jakewharton.rxbinding.ViewDirtyIdlingResource;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.contrib.DrawerMatchers.isOpen;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static android.view.Gravity.RIGHT;
import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public final class RxDrawerLayoutTest {
  @Rule public final ActivityTestRule<RxDrawerLayoutTestActivity> activityRule =
      new ActivityTestRule<>(RxDrawerLayoutTestActivity.class);

  private final Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();

  private DrawerLayout view;
  private ViewDirtyIdlingResource viewDirtyIdler;

  @Before public void setUp() {
    RxDrawerLayoutTestActivity activity = activityRule.getActivity();
    view = activity.drawerLayout;

    viewDirtyIdler = new ViewDirtyIdlingResource(activity);
    IdlingRegistry.getInstance().register(viewDirtyIdler);
  }

  @After public void tearDown() {
    IdlingRegistry.getInstance().unregister(viewDirtyIdler);
  }

  @Test public void drawerOpen() {
    RecordingObserver<Boolean> o = new RecordingObserver<>();
    Subscription subscription = RxDrawerLayout.drawerOpen(view, RIGHT) //
        .subscribeOn(AndroidSchedulers.mainThread()) //
        .subscribe(o);
    assertThat(o.takeNext()).isFalse();

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        view.openDrawer(RIGHT);
      }
    });
    assertThat(o.takeNext()).isTrue();

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        view.closeDrawer(RIGHT);
      }
    });
    assertThat(o.takeNext()).isFalse();

    subscription.unsubscribe();

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        view.openDrawer(RIGHT);
      }
    });
    o.assertNoMoreEvents();
  }

  @Test public void open() {
    final Action1<? super Boolean> open = RxDrawerLayout.open(view, RIGHT);

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        open.call(true);
      }
    });
    onView(withId(view.getId())).check(matches(isOpen(RIGHT)));

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        open.call(false);
      }
    });
    onView(withId(view.getId())).check(matches(isClosed(RIGHT)));
  }
}
