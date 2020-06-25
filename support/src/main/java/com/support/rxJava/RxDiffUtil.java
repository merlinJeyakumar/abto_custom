package com.support.rxJava;

import android.util.Pair;

import androidx.recyclerview.widget.DiffUtil;

import java.util.Collections;
import java.util.List;

import io.reactivex.ObservableTransformer;
import io.reactivex.functions.BiFunction;

public class RxDiffUtil {

  public static <T> ObservableTransformer<List<T>, Pair<List<T>, DiffUtil.DiffResult>> calculateDiff(
      BiFunction<List<T>, List<T>, DiffUtil.Callback> diffCallbacks)
  {
    Pair<List<T>, DiffUtil.DiffResult> initialPair = Pair.create(Collections.emptyList(), null);
    return upstream -> upstream
        .scan(initialPair, (latestPair, nextItems) -> {
          DiffUtil.Callback callback = diffCallbacks.apply(latestPair.first, nextItems);
          DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback, true);
          return Pair.create(nextItems, result);
        })
        .skip(1);  // downstream shouldn't receive seedPair.
  }
}
