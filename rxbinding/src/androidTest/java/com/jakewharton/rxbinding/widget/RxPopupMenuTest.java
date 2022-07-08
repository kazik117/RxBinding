package com.jakewharton.rxbinding.widget;

import android.app.Instrumentation;

import androidx.test.annotation.UiThreadTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.PopupMenu;

import com.jakewharton.rxbinding.RecordingObserver;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public final class RxPopupMenuTest {
    @Rule
    public final ActivityTestRule<RxPopupMenuTestActivity> activityRule =
            new ActivityTestRule<>(RxPopupMenuTestActivity.class);

    private final Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();

    private PopupMenu view;

    @Before
    public void setUp() {
        view = activityRule.getActivity().popupMenu;
    }

    @Test
    @UiThreadTest
    public void itemClicks() {
        Menu menu = view.getMenu();
        MenuItem item1 = menu.add(0, 1, 0, "Hi");
        MenuItem item2 = menu.add(0, 2, 0, "Hey");

        RecordingObserver<MenuItem> o = new RecordingObserver<>();
        Subscription subscription = RxPopupMenu.itemClicks(view).subscribe(o);
        o.assertNoMoreEvents();

        menu.performIdentifierAction(2, 0);
        assertThat(o.takeNext()).isSameAs(item2);

        menu.performIdentifierAction(1, 0);
        assertThat(o.takeNext()).isSameAs(item1);

        subscription.unsubscribe();

        menu.performIdentifierAction(2, 0);
        o.assertNoMoreEvents();
    }

    @Test
    public void dismisses() {
        RecordingObserver<Void> o = new RecordingObserver<>();
        Subscription subscription =
                RxPopupMenu.dismisses(view).subscribeOn(AndroidSchedulers.mainThread()).subscribe(o);
        o.assertNoMoreEvents(); // No initial value.

        instrumentation.runOnMainSync(new Runnable() {
            @Override
            public void run() {
                view.show();
            }
        });
        o.assertNoMoreEvents();

        instrumentation.runOnMainSync(new Runnable() {
            @Override
            public void run() {
                view.dismiss();
            }
        });
        assertThat(o.takeNext()).isNull();

        subscription.unsubscribe();
        instrumentation.runOnMainSync(new Runnable() {
            @Override
            public void run() {
                view.show();
            }
        });
        instrumentation.runOnMainSync(new Runnable() {
            @Override
            public void run() {
                view.dismiss();
            }
        });

        o.assertNoMoreEvents();
    }
}
