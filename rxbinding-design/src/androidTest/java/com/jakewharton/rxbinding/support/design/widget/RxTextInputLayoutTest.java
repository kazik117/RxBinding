package com.jakewharton.rxbinding.support.design.widget;

import android.content.Context;
import com.google.android.material.textfield.TextInputLayout;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.annotation.UiThreadTest;
import androidx.test.rule.UiThreadTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import android.view.ContextThemeWrapper;
import android.widget.EditText;
import com.jakewharton.rxbinding.support.design.test.R;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class RxTextInputLayoutTest {
  @Rule public final UiThreadTestRule uiThread = new UiThreadTestRule();

  private final Context rawContext = ApplicationProvider.getApplicationContext();
  private final Context context = new ContextThemeWrapper(rawContext, R.style.Theme_AppCompat);
  private final TextInputLayout view = new TextInputLayout(context);

  @Before public void setUp() {
    view.addView(new EditText(context));
  }

  @Test @UiThreadTest public void counterEnabled() {
    RxTextInputLayout.counterEnabled(view).call(true);
    assertThat(view.isCounterEnabled()).isEqualTo(true);
  }

  @Test @UiThreadTest public void counterMaxLength() {
    RxTextInputLayout.counterMaxLength(view).call(100);
    assertThat(view.getCounterMaxLength()).isEqualTo(100);
  }

  @Test @UiThreadTest public void error() {
    RxTextInputLayout.error(view).call("Your error here");
    assertThat(view.getError().toString()).isEqualTo("Your error here");
  }

  @Test @UiThreadTest public void errorRes() {
    final String error = context.getString(R.string.error);
    RxTextInputLayout.errorRes(view).call(R.string.error);
    assertThat(view.getError().toString()).isEqualTo(error);
  }

  @Test @UiThreadTest public void hint() {
    RxTextInputLayout.hint(view).call("Your name here");
    assertThat(view.getHint().toString()).isEqualTo("Your name here");
  }

  @Test @UiThreadTest public void hintRes() {
    RxTextInputLayout.hintRes(view).call(R.string.hint);
    assertThat(view.getHint().toString()).isEqualTo("Your name here");
  }
}
