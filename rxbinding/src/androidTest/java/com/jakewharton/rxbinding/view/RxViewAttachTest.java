package com.jakewharton.rxbinding.view;

import android.app.Instrumentation;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import android.view.View;
import android.widget.FrameLayout;

import com.jakewharton.rxbinding.RecordingObserver;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

import static com.google.common.truth.Truth.assertThat;
import static com.jakewharton.rxbinding.view.ViewAttachEvent.Kind.ATTACH;
import static com.jakewharton.rxbinding.view.ViewAttachEvent.Kind.DETACH;

@RunWith(AndroidJUnit4.class)
public final class RxViewAttachTest {
  @Rule public final ActivityTestRule<RxViewAttachTestActivity> activityRule =
      new ActivityTestRule<>(RxViewAttachTestActivity.class);

  private final Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
  private FrameLayout parent;
  private View child;

  @Before public void setUp() {
    RxViewAttachTestActivity activity = activityRule.getActivity();
    parent = activity.parent;
    child = activity.child;
  }

  @Test public void attaches() {
    RecordingObserver<Void> o = new RecordingObserver<>();
    Subscription subscription = RxView.attaches(child)
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(o);
    o.assertNoMoreEvents(); // No initial value.

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        parent.addView(child);
      }
    });
    assertThat(o.takeNext()).isNull();
    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        parent.removeView(child);
      }
    });
    o.assertNoMoreEvents();

    subscription.unsubscribe();

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        parent.addView(child);
        parent.removeView(child);
      }
    });
    o.assertNoMoreEvents();
  }

  @Test public void attachEvents() {
    RecordingObserver<ViewAttachEvent> o = new RecordingObserver<>();
    Subscription subscription = RxView.attachEvents(child)
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(o);
    o.assertNoMoreEvents(); // No initial value.

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        parent.addView(child);
      }
    });
    assertThat(o.takeNext().kind()).isEqualTo(ATTACH);
    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        parent.removeView(child);
      }
    });
    assertThat(o.takeNext().kind()).isEqualTo(DETACH);

    subscription.unsubscribe();

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        parent.addView(child);
        parent.removeView(child);
      }
    });
    o.assertNoMoreEvents();
  }

  @Test public void detaches() {
    RecordingObserver<Void> o = new RecordingObserver<>();
    Subscription subscription = RxView.detaches(child)
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(o);
    o.assertNoMoreEvents(); // No initial value.

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        parent.addView(child);
      }
    });
    o.assertNoMoreEvents();
    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        parent.removeView(child);
      }
    });
    assertThat(o.takeNext()).isNull();

    subscription.unsubscribe();

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        parent.addView(child);
        parent.removeView(child);
      }
    });
    o.assertNoMoreEvents();
  }
}
