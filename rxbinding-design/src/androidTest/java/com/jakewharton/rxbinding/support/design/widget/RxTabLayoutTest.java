package com.jakewharton.rxbinding.support.design.widget;

import android.content.Context;
import com.google.android.material.tabs.TabLayout;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.annotation.UiThreadTest;
import androidx.test.rule.UiThreadTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import android.view.ContextThemeWrapper;
import com.jakewharton.rxbinding.RecordingObserver;
import com.jakewharton.rxbinding.support.design.R;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import rx.Subscription;
import rx.functions.Action1;

import static com.google.common.truth.Truth.assertThat;
import static com.jakewharton.rxbinding.support.design.widget.TabLayoutSelectionEvent.Kind.RESELECTED;
import static com.jakewharton.rxbinding.support.design.widget.TabLayoutSelectionEvent.Kind.SELECTED;
import static com.jakewharton.rxbinding.support.design.widget.TabLayoutSelectionEvent.Kind.UNSELECTED;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public final class RxTabLayoutTest {
  @Rule public final UiThreadTestRule uiThreadTestRule = new UiThreadTestRule();

  private final Context rawContext = ApplicationProvider.getApplicationContext();
  private final Context context = new ContextThemeWrapper(rawContext, R.style.Theme_AppCompat);
  private final TabLayout view = new TabLayout(context);
  private final TabLayout.Tab tab1 = view.newTab();
  private final TabLayout.Tab tab2 = view.newTab();

  @Before public void setUp() {
    view.addTab(tab1);
    view.addTab(tab2);
  }

  @Test @UiThreadTest public void selectionEvents() {
    RecordingObserver<TabLayoutSelectionEvent> o = new RecordingObserver<>();
    Subscription subscription = RxTabLayout.selectionEvents(view).subscribe(o);
    assertThat(o.takeNext()).isEqualTo(TabLayoutSelectionEvent.create(view, SELECTED, tab1));

    tab2.select();
    assertThat(o.takeNext()).isEqualTo(TabLayoutSelectionEvent.create(view, UNSELECTED, tab1));
    assertThat(o.takeNext()).isEqualTo(TabLayoutSelectionEvent.create(view, SELECTED, tab2));

    tab2.select(); // Reselection
    assertThat(o.takeNext()).isEqualTo(TabLayoutSelectionEvent.create(view, RESELECTED, tab2));

    tab1.select();
    assertThat(o.takeNext()).isEqualTo(TabLayoutSelectionEvent.create(view, UNSELECTED, tab2));
    assertThat(o.takeNext()).isEqualTo(TabLayoutSelectionEvent.create(view, SELECTED, tab1));

    subscription.unsubscribe();

    tab2.select();
    o.assertNoMoreEvents();
  }

  @Test @UiThreadTest public void selectionEventsNoInitial() {
    TabLayout empty = new TabLayout(context);

    RecordingObserver<TabLayoutSelectionEvent> o = new RecordingObserver<>();
    RxTabLayout.selectionEvents(empty).subscribe(o);
    o.assertNoMoreEvents();
  }

  @Test @UiThreadTest public void selections() {
    RecordingObserver<TabLayout.Tab> o = new RecordingObserver<>();
    Subscription subscription = RxTabLayout.selections(view).subscribe(o);
    assertThat(o.takeNext()).isSameAs(tab1);

    tab2.select();
    assertThat(o.takeNext()).isSameAs(tab2);

    tab2.select(); // Reselection
    o.assertNoMoreEvents();

    tab1.select();
    assertThat(o.takeNext()).isSameAs(tab1);

    subscription.unsubscribe();

    tab2.select();
    o.assertNoMoreEvents();
  }

  @Test @UiThreadTest public void selectionsNoInitial() {
    TabLayout empty = new TabLayout(context);

    RecordingObserver<TabLayout.Tab> o = new RecordingObserver<>();
    RxTabLayout.selections(empty).subscribe(o);
    o.assertNoMoreEvents();
  }

  @Test @UiThreadTest public void select() {
    Action1<? super Integer> action = RxTabLayout.select(view);
    assertThat(view.getSelectedTabPosition()).isEqualTo(0);
    action.call(1);
    assertThat(view.getSelectedTabPosition()).isEqualTo(1);
    action.call(0);
    assertThat(view.getSelectedTabPosition()).isEqualTo(0);
  }

  @Test @UiThreadTest public void selectInvalidValueThrows() {
    Action1<? super Integer> action = RxTabLayout.select(view);
    try {
      action.call(2);
      fail();
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("No tab for index 2");
    }
    try {
      action.call(-1);
      fail();
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("No tab for index -1");
    }
  }
}
