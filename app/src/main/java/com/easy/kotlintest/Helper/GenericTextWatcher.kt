package com.easy.kotlintest.Helper

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText


class GenericTextWatcher( var editText: Array<EditText>, var position: Int) :TextWatcher {


    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun afterTextChanged(char: Editable?) {

        var text = char?.toString()?:""


        if (text != "") {
            if (position == 0) {
                 if (text.length > 0) {
                     editText[position + 1].requestFocus()
                 }
             } else if (position == editText.size - 1) {
                 if (text.length == 0) {
                     editText[position - 1].requestFocus()
                 }
             } else if (position < editText.size) {
                 if (text.length > 0) {
                     editText[position + 1].requestFocus()
                 } else if (text.length == 0) {
                     editText[position - 1].requestFocus()
                 }
             }
        }

    }
}