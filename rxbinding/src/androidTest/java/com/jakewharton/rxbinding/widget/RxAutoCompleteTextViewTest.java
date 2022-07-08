package com.jakewharton.rxbinding.widget;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.google.common.truth.Truth.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.startsWith;

import android.annotation.TargetApi;
import android.app.Instrumentation;
import android.os.Build;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.test.annotation.UiThreadTest;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SdkSuppress;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.jakewharton.rxbinding.RecordingObserver;
import com.jakewharton.rxbinding.test.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

@RunWith(AndroidJUnit4.class)
public final class RxAutoCompleteTextViewTest {
  @Rule public final ActivityTestRule<RxAutoCompleteTextViewTestActivity> activityRule =
      new ActivityTestRule<>(RxAutoCompleteTextViewTestActivity.class);

  private final Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();

  private RxAutoCompleteTextViewTestActivity activity;
  private AutoCompleteTextView autoCompleteTextView;

  @Before public void setUp() {
    activity = activityRule.getActivity();
    autoCompleteTextView = activity.autoCompleteTextView;
  }

  @Test public void itemClickEvents() {
    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        autoCompleteTextView.setThreshold(1);

        List<String> values = Arrays.asList("Two", "Three", "Twenty");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(autoCompleteTextView.getContext(),
            android.R.layout.simple_list_item_1, values);
        autoCompleteTextView.setAdapter(adapter);
      }
    });

    RecordingObserver<AdapterViewItemClickEvent> o = new RecordingObserver<>();
    Subscription subscription = RxAutoCompleteTextView.itemClickEvents(autoCompleteTextView) //
      .subscribeOn(AndroidSchedulers.mainThread()) //
      .subscribe(o);
    o.assertNoMoreEvents();

    onView(withId(R.id.auto_complete)).perform(typeText("Tw"));
    onData(startsWith("Twenty"))
        .inRoot(withDecorView(not(is(activity.getWindow().getDecorView()))))
        .perform(click());

    AdapterViewItemClickEvent event = o.takeNext();
    assertThat(event.view()).isNotNull();
    assertThat(event.clickedView()).isNotNull();
    assertThat(event.position()).isEqualTo(1); // Second item in two-item filtered list.
    assertThat(event.id()).isEqualTo(1); // Second item in two-item filtered list.

    subscription.unsubscribe();

    onView(withId(R.id.auto_complete)).perform(clearText(), typeText("Tw"));
    onData(startsWith("Twenty"))
        .inRoot(withDecorView(not(is(activity.getWindow().getDecorView()))))
        .perform(click());

    o.assertNoMoreEvents();
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
  @SdkSuppress(minSdkVersion = Build.VERSION_CODES.JELLY_BEAN)
  @Test @UiThreadTest public void completionHint() {
    RxAutoCompleteTextView.completionHint(autoCompleteTextView).call("Test hint");
    assertThat(autoCompleteTextView.getCompletionHint()).isEqualTo("Test hint");
  }

  @Test @UiThreadTest public void threshold() {
    RxAutoCompleteTextView.threshold(autoCompleteTextView).call(10);
    assertThat(autoCompleteTextView.getThreshold()).isEqualTo(10);
  }
}
