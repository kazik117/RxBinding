package com.jakewharton.rxbinding.support.v7.widget;

import static com.google.common.truth.Truth.assertThat;

import android.app.Instrumentation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.jakewharton.rxbinding.RecordingObserver;
import com.jakewharton.rxbinding.ViewDirtyIdlingResource;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

@RunWith(AndroidJUnit4.class)
public final class RxRecyclerViewTest {
  @Rule public final ActivityTestRule<RxRecyclerViewTestActivity> activityRule =
      new ActivityTestRule<>(RxRecyclerViewTestActivity.class);

  private final Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();

  private RecyclerView view;
  private ViewInteraction interaction;
  private ViewDirtyIdlingResource viewDirtyIdler;
  private View child;

  @Before public void setUp() {
    RxRecyclerViewTestActivity activity = activityRule.getActivity();
    view = activity.recyclerView;
    child = new View(activityRule.getActivity());
    interaction = Espresso.onView(ViewMatchers.withId(android.R.id.primary));
    viewDirtyIdler = new ViewDirtyIdlingResource(activity);
    IdlingRegistry.getInstance().register(viewDirtyIdler);
  }

  @After public void tearDown() {
    IdlingRegistry.getInstance().unregister(viewDirtyIdler);
  }

  @Test public void childAttachEvents() {
    RecordingObserver<RecyclerViewChildAttachStateChangeEvent> o = new RecordingObserver<>();
    Subscription subscription = RxRecyclerView.childAttachStateChangeEvents(view)
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(o);
    o.assertNoMoreEvents();

    final SimpleAdapter adapter = new SimpleAdapter(child);

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        view.setAdapter(adapter);
      }
    });
    assertThat(o.takeNext()).isEqualTo(RecyclerViewChildAttachEvent.create(view, child));

    subscription.unsubscribe();

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        view.setAdapter(adapter);
      }
    });

    o.assertNoMoreEvents();
  }

  @Test public void childDetachEvents() {
    final SimpleAdapter adapter = new SimpleAdapter(child);

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        view.setAdapter(adapter);
      }
    });

    RecordingObserver<RecyclerViewChildAttachStateChangeEvent> o = new RecordingObserver<>();
    Subscription subscription = RxRecyclerView.childAttachStateChangeEvents(view)
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(o);
    o.assertNoMoreEvents();

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        view.setAdapter(null);
      }
    });
    assertThat(o.takeNext()).isEqualTo(RecyclerViewChildDetachEvent.create(view, child));

    subscription.unsubscribe();

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        view.setAdapter(adapter);
      }
    });

    o.assertNoMoreEvents();
  }

  @Test public void scrollEventsVertical() {
    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        view.setAdapter(new Adapter());
      }
    });

    RecordingObserver<RecyclerViewScrollEvent> o = new RecordingObserver<>();
    Subscription subscription = RxRecyclerView.scrollEvents(view)
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(o);
    o.assertNoMoreEvents();

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        view.scrollBy(0, 50);
      }
    });
    RecyclerViewScrollEvent event1 = o.takeNext();
    assertThat(event1).isNotNull();
    assertThat(event1.dy()).isEqualTo(50);

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        view.scrollBy(0, 0);
      }
    });
    o.assertNoMoreEvents();

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        view.scrollBy(0, -50);
      }
    });
    RecyclerViewScrollEvent event2 = o.takeNext();
    assertThat(event2).isNotNull();
    assertThat(event2.dy()).isEqualTo(-50);

    // Back at position 0. Trying to scroll earlier shouldn't fire any events
    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        view.scrollBy(0, -50);
      }
    });
    o.assertNoMoreEvents();

    subscription.unsubscribe();

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        view.scrollBy(0, 50);
      }
    });
    o.assertNoMoreEvents();
  }

  @Test public void scrollEventsHorizontal() {
    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        view.setAdapter(new Adapter());
        ((LinearLayoutManager) view.getLayoutManager()).setOrientation(LinearLayoutManager.HORIZONTAL);
      }
    });

    instrumentation.waitForIdleSync();
    RecordingObserver<RecyclerViewScrollEvent> o = new RecordingObserver<>();
    Subscription subscription = RxRecyclerView.scrollEvents(view)
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(o);
    o.assertNoMoreEvents();

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        view.scrollBy(50, 0);
      }
    });
    RecyclerViewScrollEvent event3 = o.takeNext();
    assertThat(event3).isNotNull();
    assertThat(event3.dx()).isEqualTo(50);

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        view.scrollBy(0, 0);
      }
    });
    o.assertNoMoreEvents();

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        view.scrollBy(-50, 0);
      }
    });
    RecyclerViewScrollEvent event4 = o.takeNext();
    assertThat(event4).isNotNull();
    assertThat(event4.dx()).isEqualTo(-50);

    // Back at position 0. Trying to scroll earlier shouldn't fire any events
    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        view.scrollBy(-50, 0);
      }
    });
    o.assertNoMoreEvents();

    subscription.unsubscribe();

    instrumentation.runOnMainSync(new Runnable() {
      @Override public void run() {
        view.scrollBy(50, 0);
      }
    });
    o.assertNoMoreEvents();
  }

  private class SimpleAdapter extends RecyclerView.Adapter {
    private final View child;

    public SimpleAdapter(View child) {
      this.child = child;
    }

    @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      return new RecyclerView.ViewHolder(child) {
      };
    }

    @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    }

    @Override public int getItemCount() {
      return 1;
    }
  }

  private static class Adapter extends RecyclerView.Adapter<ViewHolder> {
    public Adapter() {
      setHasStableIds(true);
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
      TextView v = (TextView) LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
      return new ViewHolder(v);
    }

    @Override public void onBindViewHolder(ViewHolder holder, int position) {
      holder.textView.setText(String.valueOf(position));
    }

    @Override public int getItemCount() {
      return 100;
    }

    @Override public long getItemId(int position) {
      return position;
    }
  }

  private static class ViewHolder extends RecyclerView.ViewHolder {

    TextView textView;

    public ViewHolder(TextView itemView) {
      super(itemView);
      this.textView = itemView;
    }
  }
}
