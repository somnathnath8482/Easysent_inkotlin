package com.easy.kotlintest.Interface.Messages;

import androidx.lifecycle.LiveData;

/**
 * Created by Somnath nath on 15,July,2023
 * Artix Development,
 * India.
 */
public interface LiveData_Item <T>{
    void onItem(LiveData<T> item);
}
