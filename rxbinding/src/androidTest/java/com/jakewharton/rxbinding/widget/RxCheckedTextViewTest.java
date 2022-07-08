package com.jakewharton.rxbinding.widget;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.annotation.UiThreadTest;
import androidx.test.rule.UiThreadTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import android.widget.CheckedTextView;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class RxCheckedTextViewTest {
  @Rule public final UiThreadTestRule uiThread = new UiThreadTestRule();

  private final Context context = ApplicationProvider.getApplicationContext();
  private final CheckedTextView view = new CheckedTextView(context);

  @Test @UiThreadTest public void check() {
    view.setChecked(false);
    RxCheckedTextView.check(view).call(true);
    assertThat(view.isChecked()).isEqualTo(true);
  }
}
