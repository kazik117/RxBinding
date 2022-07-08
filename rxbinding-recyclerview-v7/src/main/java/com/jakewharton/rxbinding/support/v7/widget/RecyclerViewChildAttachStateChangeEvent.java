package com.jakewharton.rxbinding.support.v7.widget;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import com.jakewharton.rxbinding.view.ViewEvent;

public abstract class RecyclerViewChildAttachStateChangeEvent extends ViewEvent<RecyclerView> {
  private final View child;

  RecyclerViewChildAttachStateChangeEvent(@NonNull RecyclerView view, @NonNull View child) {
    super(view);
    this.child = child;
  }

  /** The child from which this event occurred. */
  @NonNull public final View child() {
    return child;
  }
}
