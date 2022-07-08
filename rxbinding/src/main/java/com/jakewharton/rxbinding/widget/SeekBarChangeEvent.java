package com.jakewharton.rxbinding.widget;

import androidx.annotation.NonNull;
import android.widget.SeekBar;
import com.jakewharton.rxbinding.view.ViewEvent;

public abstract class SeekBarChangeEvent extends ViewEvent<SeekBar> {
  SeekBarChangeEvent(@NonNull SeekBar view) {
    super(view);
  }
}
