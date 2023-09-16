package com.easy.kotlintest.Interface.Messages;

import androidx.paging.PagingData;

/**
 * Created by Somnath nath on 15,July,2023
 * Artix Development,
 * India.
 */
public interface LiveData_Messages<T> {
     void allMessage(androidx.lifecycle.LiveData<PagingData<T>> messages);
}
