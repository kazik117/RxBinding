package com.jakewharton.rxbinding.widget;

import android.app.Instrumentation;
import androidx.test.annotation.UiThreadTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import android.widget.RatingBar;
import com.jakewharton.rxbinding.RecordingObserver;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;
import static com.google.common.truth.Truth.assertThat;
import static com.jakewharton.rxbinding.MotionEventUtil.motionEventAtPosition;

@RunWith(AndroidJUnit4.class) public final class RxRatingBarTest {
  @Rule public final ActivityTestRule<RxRatingBarTestActivity> activityRule =
      new ActivityTestRule<>(RxRatingBarTestActivity.class);

  private final Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();

  private RatingBar view;

  @Before public void setUp() {
    view = activityRule.getActivity().ratingBar;
  }

  @Test public void ratingChanges() {
    RecordingObserver<Float> o = new RecordingObserver<>();
    Subscription subscription = RxRatingBar.ratingChanges(view) //
        .subscribeOn(AndroidSchedulers.mainThread()) //
        .subscribe(o);
    assertThat(o.takeNext()).isEqualTo(0f);

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        view.setRating(1f);
      }
    });
    assertThat(o.takeNext()).isEqualTo(1f);

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        view.setRating(2f);
      }
    });
    assertThat(o.takeNext()).isEqualTo(2f);

    subscription.unsubscribe();

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        view.setRating(1f);
      }
    });
    o.assertNoMoreEvents();
  }

  @Test public void ratingChangeEvents() {
    RecordingObserver<RatingBarChangeEvent> o = new RecordingObserver<>();
    Subscription subscription = RxRatingBar.ratingChangeEvents(view) //
        .subscribeOn(AndroidSchedulers.mainThread()) //
        .subscribe(o);
    assertThat(o.takeNext()).isEqualTo(RatingBarChangeEvent.create(view, 0f, false));

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        view.setRating(5f);
      }
    });
    assertThat(o.takeNext()).isEqualTo(RatingBarChangeEvent.create(view, 5f, false));

    instrumentation.sendPointerSync(motionEventAtPosition(view, ACTION_DOWN, 0, 50));
    instrumentation.sendPointerSync(motionEventAtPosition(view, ACTION_UP, 0, 50));
    instrumentation.waitForIdleSync();
    assertThat(o.takeNext()).isEqualTo(RatingBarChangeEvent.create(view, 1f, true));

    subscription.unsubscribe();

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        view.setRating(1f);
      }
    });
    o.assertNoMoreEvents();
  }

  @Test @UiThreadTest public void rating() {
    Action1<? super Float> action = RxRatingBar.rating(view);
    assertThat(view.getRating()).isEqualTo(0f);
    action.call(1f);
    assertThat(view.getRating()).isEqualTo(1f);
    action.call(2f);
    assertThat(view.getRating()).isEqualTo(2f);
  }

  @Test @UiThreadTest public void isIndicator() {
    Action1<? super Boolean> action = RxRatingBar.isIndicator(view);
    assertThat(view.isIndicator()).isFalse();
    action.call(true);
    assertThat(view.isIndicator()).isTrue();
    action.call(false);
    assertThat(view.isIndicator()).isFalse();
  }
}
