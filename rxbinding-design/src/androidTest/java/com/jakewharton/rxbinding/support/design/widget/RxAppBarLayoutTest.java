package com.jakewharton.rxbinding.support.design.widget;

import android.annotation.TargetApi;
import android.content.Context;
import com.google.android.material.appbar.AppBarLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.annotation.UiThreadTest;
import androidx.test.filters.SdkSuppress;
import androidx.test.rule.UiThreadTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import android.view.ContextThemeWrapper;
import android.view.View;
import com.jakewharton.rxbinding.RecordingObserver;
import com.jakewharton.rxbinding.support.design.R;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import rx.Subscription;

import static com.google.common.truth.Truth.assertThat;
import static android.os.Build.VERSION_CODES.JELLY_BEAN_MR1;

@RunWith(AndroidJUnit4.class)
public class RxAppBarLayoutTest {
  @Rule public final UiThreadTestRule uiThreadTestRule = new UiThreadTestRule();

  private final Context rawContext = ApplicationProvider.getApplicationContext();
  private final Context context = new ContextThemeWrapper(rawContext, R.style.Theme_AppCompat);
  private final CoordinatorLayout parent = new CoordinatorLayout(context);
  private final AppBarLayout view = new AppBarLayout(context);

  @Before public void setUp() {
    parent.addView(view);
  }

  @TargetApi(JELLY_BEAN_MR1)
  @SdkSuppress(minSdkVersion = JELLY_BEAN_MR1)
  @Test @UiThreadTest public void offsetChanges() {
    RecordingObserver<Integer> o = new RecordingObserver<>();
    Subscription subscription = RxAppBarLayout.offsetChanges(view).subscribe(o);
    o.assertNoMoreEvents();

    CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) view.getLayoutParams();
    AppBarLayout.Behavior behavior = new AppBarLayout.Behavior();
    params.setBehavior(behavior);
    behavior.onLayoutChild(parent, view, View.LAYOUT_DIRECTION_LTR);
    assertThat(o.takeNext()).isEqualTo(0);

    subscription.unsubscribe();

    behavior.onLayoutChild(parent, view, View.LAYOUT_DIRECTION_LTR);
    o.assertNoMoreEvents();
  }
}
