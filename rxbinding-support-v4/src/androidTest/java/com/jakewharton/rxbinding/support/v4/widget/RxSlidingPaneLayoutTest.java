package com.jakewharton.rxbinding.support.v4.widget;

import android.app.Instrumentation;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.slidingpanelayout.widget.SlidingPaneLayout;
import android.view.View;
import com.jakewharton.rxbinding.RecordingObserver;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
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
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class) public class RxSlidingPaneLayoutTest {
  @Rule public final ActivityTestRule<RxSlidingPaneLayoutTestActivity> activityRule =
      new ActivityTestRule<>(RxSlidingPaneLayoutTestActivity.class);

  private final Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();

  private SlidingPaneLayout view;

  private CountingIdlingResource idler;

  @Before public void setUp() {
    RxSlidingPaneLayoutTestActivity activity = activityRule.getActivity();
    view = activity.slidingPaneLayout;

    idler = new CountingIdlingResource("counting idler");
    IdlingRegistry.getInstance().register(idler);
  }

  @After public void teardown() {
    IdlingRegistry.getInstance().unregister(idler);
  }

  @Test public void paneOpen() {
    RecordingObserver<Boolean> o = new RecordingObserver<>();
    Subscription subscription =
        RxSlidingPaneLayout.panelOpens(view).subscribeOn(AndroidSchedulers.mainThread()).subscribe(o);
    assertThat(o.takeNext()).isFalse();

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        view.openPane();
      }
    });
    assertThat(o.takeNext()).isTrue();

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        view.closePane();
      }
    });
    assertThat(o.takeNext()).isFalse();

    subscription.unsubscribe();

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        view.openPane();
      }
    });
    o.assertNoMoreEvents();
  }

  @Test public void slides() {
    RecordingObserver<Float> o1 = new RecordingObserver<>();
    Subscription subscription1 =
        RxSlidingPaneLayout.panelSlides(view).subscribeOn(AndroidSchedulers.mainThread()).subscribe(o1);
    o1.assertNoMoreEvents();

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        view.openPane();
      }
    });
    instrumentation.waitForIdleSync();
    assertThat(o1.takeNext()).isGreaterThan(0f);

    subscription1.unsubscribe();
    o1.assertNoMoreEvents();

    RecordingObserver<Float> o2 = new RecordingObserver<>();
    Subscription subscription2 =
        RxSlidingPaneLayout.panelSlides(view).subscribeOn(AndroidSchedulers.mainThread()).subscribe(o2);
    o2.assertNoMoreEvents();

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        view.closePane();
      }
    });
    instrumentation.waitForIdleSync();
    assertThat(o2.takeNext()).isLessThan(1.0f);

    subscription2.unsubscribe();
    o2.assertNoMoreEvents();
  }

  @Test public void open() {
    final Action1<? super Boolean> open = RxSlidingPaneLayout.open(view);

    view.setPanelSlideListener(new SlidingPaneLayout.SimplePanelSlideListener() {
      @Override public void onPanelOpened(View panel) {
        idler.decrement();
      }

      @Override public void onPanelClosed(View panel) {
        idler.decrement();
      }
    });

    idler.increment();
    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        open.call(true);
      }
    });
    instrumentation.waitForIdleSync();
    onView(withId(view.getId())).check(matches(isOpen()));


    idler.increment();
    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        open.call(false);
      }
    });
    instrumentation.waitForIdleSync();
    onView(withId(view.getId())).check(matches(isClosed()));

    view.setPanelSlideListener(null);
  }

  private static Matcher<View> isOpen() {
    return new BoundedMatcher<View, SlidingPaneLayout>(SlidingPaneLayout.class) {
      @Override public void describeTo(Description description) {
        description.appendText("is pane open");
      }

      @Override public boolean matchesSafely(SlidingPaneLayout slidingPaneLayout) {
        return slidingPaneLayout.isOpen();
      }
    };
  }

  private static Matcher<View> isClosed() {
    return new BoundedMatcher<View, SlidingPaneLayout>(SlidingPaneLayout.class) {
      @Override public void describeTo(Description description) {
        description.appendText("is pane closed");
      }

      @Override public boolean matchesSafely(SlidingPaneLayout slidingPaneLayout) {
        return !slidingPaneLayout.isOpen();
      }
    };
  }
}
