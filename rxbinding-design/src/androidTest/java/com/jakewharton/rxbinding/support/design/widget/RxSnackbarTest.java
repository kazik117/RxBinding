package com.jakewharton.rxbinding.support.design.widget;

import android.app.Instrumentation;
import android.content.Context;
import com.google.android.material.snackbar.Snackbar;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import android.view.ContextThemeWrapper;
import android.widget.FrameLayout;
import com.jakewharton.rxbinding.RecordingObserver;
import com.jakewharton.rxbinding.support.design.R;
import org.junit.Test;
import org.junit.runner.RunWith;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

import static com.google.android.material.snackbar.Snackbar.Callback.DISMISS_EVENT_MANUAL;
import static com.google.android.material.snackbar.Snackbar.LENGTH_SHORT;
import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public final class RxSnackbarTest {
  private final Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
  private final Context rawContext = ApplicationProvider.getApplicationContext();
  private final Context context = new ContextThemeWrapper(rawContext, R.style.Theme_AppCompat);
  private final FrameLayout parent = new FrameLayout(context);

  @Test public void dismisses() {
    final Snackbar view = Snackbar.make(parent, "Hey", LENGTH_SHORT);

    RecordingObserver<Integer> o = new RecordingObserver<>();
    Subscription subscription = RxSnackbar.dismisses(view)
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(o);
    o.assertNoMoreEvents();

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        view.show();
      }
    });
    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        view.dismiss();
      }
    });
    assertThat(o.takeNext()).isEqualTo(DISMISS_EVENT_MANUAL);

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        view.show();
      }
    });
    subscription.unsubscribe();
    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        view.dismiss();
      }
    });
    o.assertNoMoreEvents();
  }
}
