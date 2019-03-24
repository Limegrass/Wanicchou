package com.waifusims.wanicchou.util

import android.content.Context
import android.text.InputType
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import data.arch.util.IFactory

class InputAlertDialogBuilderFactory(
        private val context : Context,
        private val title : String,
        private val message : String)
    : IFactory<AlertDialog>{
    override fun get(): AlertDialog {
        val dialogBuilder = AlertDialog.Builder(context).create()
        val input = EditText(context)
        input.inputType = InputType.TYPE_CLASS_TEXT
        dialogBuilder.setView(input)
        dialogBuilder.setTitle(title)
        dialogBuilder.setMessage(message)
        return dialogBuilder
    }
}