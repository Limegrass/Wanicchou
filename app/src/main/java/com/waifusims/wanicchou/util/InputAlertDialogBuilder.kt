package com.waifusims.wanicchou.util

import android.content.Context
import android.text.InputType
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.waifusims.wanicchou.R

class InputAlertDialogBuilder(
        context : Context,
        viewGroup : ViewGroup?,
        title : String?,
        message : String?)
    : AlertDialog.Builder(context) {
    val input : EditText
    init {
        val dialogBuilder = this
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_text_input,
                viewGroup,
                false)
        input = view.findViewById(R.id.et_input)
        input.inputType = InputType.TYPE_CLASS_TEXT
        dialogBuilder.setView(view)
        if(!title.isNullOrBlank()){
            dialogBuilder.setTitle(title)
        }
        if(!message.isNullOrBlank()){
            dialogBuilder.setMessage(message)
        }
    }
}