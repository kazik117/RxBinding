package com.jakewharton.rxbinding.support.v7.widget;

import android.content.Context;
import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jakewharton.rxbinding.view.ViewEvent;

/**
 * A scroll event on a recyclerView.
 * <p>
 * <strong>Warning:</strong> Instances keep a strong reference to the recyclerView. Operators that
 * cache instances have the potential to leak the associated {@link Context}.
 */
public final class RecyclerViewScrollEvent extends ViewEvent<RecyclerView> {
  @CheckResult @NonNull
  public static RecyclerViewScrollEvent create(@NonNull RecyclerView recyclerView, int dx, int dy) {
    return new RecyclerViewScrollEvent(recyclerView, dx, dy);
  }

  private final int dx;
  private final int dy;

  private RecyclerViewScrollEvent(@NonNull RecyclerView recyclerView, int dx, int dy) {
    super(recyclerView);
    this.dx = dx;
    this.dy = dy;
  }

  public int dx() {
    return dx;
  }

  public int dy() {
    return dy;
  }

  @Override public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof RecyclerViewScrollEvent)) return false;
    RecyclerViewScrollEvent other = (RecyclerViewScrollEvent) o;
    return other.view() == view()
        && dx == other.dx
        && dy == other.dy;
  }

  @Override public int hashCode() {
    int result = 17;
    result = result * 37 + view().hashCode();
    result = result * 37 + dx;
    result = result * 37 + dy;
    return result;
  }

  @Override public String toString() {
    return "RecyclerViewScrollEvent{view="
        + view()
        + ", dx="
        + dx
        + ", dy="
        + dy
        + '}';
  }
}
