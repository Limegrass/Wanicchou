package com.waifusims.wanicchou.util

import android.content.Context
import android.text.InputType
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.waifusims.wanicchou.R
import data.arch.util.IFactory

class InputAlertDialogBuilderFactory(
        private val context : Context,
        private val viewGroup : ViewGroup?,
        private val title : String?,
        private val message : String?)
    : IFactory<AlertDialog>{
    override fun get(): AlertDialog {
        val dialogBuilder = AlertDialog.Builder(context).create()
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_text_input,
                                                        viewGroup,
                                                               false)
        val input = view.findViewById<EditText>(R.id.et_input)
        input.inputType = InputType.TYPE_CLASS_TEXT
        dialogBuilder.setView(view)
        if(!title.isNullOrBlank()){
            dialogBuilder.setTitle(title)
        }
        if(!message.isNullOrBlank()){
            dialogBuilder.setMessage(message)
        }
        return dialogBuilder
    }
}