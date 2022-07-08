package com.jakewharton.rxbinding.widget;

import androidx.annotation.NonNull;
import android.widget.AdapterView;
import com.jakewharton.rxbinding.view.ViewEvent;

public abstract class AdapterViewSelectionEvent extends ViewEvent<AdapterView<?>> {
  AdapterViewSelectionEvent(@NonNull AdapterView<?> view) {
    super(view);
  }
}
