package com.jakewharton.rxbinding.support.v17.leanback.widget;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.leanback.widget.SearchBar;
import rx.Observable;
import rx.functions.Action1;

import static com.jakewharton.rxbinding.internal.Preconditions.checkNotNull;

/**
 * Static factory methods for creating {@linkplain Observable observables} and {@linkplain Action1
 * actions} for {@link SearchBar}.
 */
public final class RxSearchBar {
  /**
   * Create an observable of {@linkplain SearchBarSearchQueryEvent search query events} on {@code
   * view}.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   * <p>
   */
  @CheckResult
  @NonNull
  public static Observable<SearchBarSearchQueryEvent> searchQueryChangeEvents(
          @NonNull SearchBar view) {
    checkNotNull(view, "view == null");
    return Observable.create(new SearchBarSearchQueryChangeEventsOnSubscribe(view));
  }

  /**
   * Create an observable of String values for search query changes on {@code view}.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   * <p>
   */
  @CheckResult @NonNull
  public static Observable<String> searchQueryChanges(@NonNull SearchBar view) {
    checkNotNull(view, "view == null");
    return Observable.create(new SearchBarSearchQueryChangesOnSubscribe(view));
  }

  /**
   * An action which sets the searchQuery property of {@code view} with String values.
   * <p>
   * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
   * to free this reference.
   */
  @CheckResult @NonNull
  public static Action1<? super String> searchQuery(@NonNull final SearchBar view) {
    checkNotNull(view, "view == null");
    return new Action1<String>() {
      @Override public void call(String text) {
        view.setSearchQuery(text);
      }
    };
  }

  private RxSearchBar() {
    throw new AssertionError("No instances.");
  }
}

