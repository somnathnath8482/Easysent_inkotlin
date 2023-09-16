package com.easy.kotlintest.Interface.Messages;

import java.util.List;

/**
 * Created by Somnath nath on 15,July,2023
 * Artix Development,
 * India.
 */
public interface LiveDatanonPage<T> {
    void allMessage(androidx.lifecycle.LiveData<List<T>> messages);
}
