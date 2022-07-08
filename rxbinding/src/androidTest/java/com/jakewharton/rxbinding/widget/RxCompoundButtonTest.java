package com.jakewharton.rxbinding.widget;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.annotation.UiThreadTest;
import androidx.test.rule.UiThreadTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import android.widget.CompoundButton;
import android.widget.ToggleButton;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import rx.Subscription;
import com.jakewharton.rxbinding.RecordingObserver;
import rx.functions.Action1;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public final class RxCompoundButtonTest {
  @Rule public final UiThreadTestRule uiThread = new UiThreadTestRule();

  private final Context context = ApplicationProvider.getApplicationContext();
  private final CompoundButton view = new ToggleButton(context);

  @Test @UiThreadTest public void checkedChanges() {
    view.setChecked(false);

    RecordingObserver<Boolean> o = new RecordingObserver<>();
    Subscription subscription = RxCompoundButton.checkedChanges(view).subscribe(o);
    assertThat(o.takeNext()).isFalse();

    view.setChecked(true);
    assertThat(o.takeNext()).isTrue();
    view.setChecked(false);
    assertThat(o.takeNext()).isFalse();

    subscription.unsubscribe();

    view.setChecked(true);
    o.assertNoMoreEvents();
  }

  @Test @UiThreadTest public void checked() {
    view.setChecked(false);
    Action1<? super Boolean> toggle = RxCompoundButton.checked(view);

    toggle.call(true);
    assertThat(view.isChecked()).isTrue();

    toggle.call(false);
    assertThat(view.isChecked()).isFalse();
  }

  @Test @UiThreadTest public void toggle() {
    view.setChecked(false);
    Action1<? super Object> toggle = RxCompoundButton.toggle(view);

    toggle.call(null);
    assertThat(view.isChecked()).isTrue();

    toggle.call("OMG TOGGLES");
    assertThat(view.isChecked()).isFalse();
  }
}
