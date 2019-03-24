package com.waifusims.wanicchou.ui.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.waifusims.wanicchou.R
import com.waifusims.wanicchou.ui.adapter.TagAdapter
import com.waifusims.wanicchou.util.InputAlertDialogBuilderFactory
import com.waifusims.wanicchou.viewmodel.TagViewModel

class TagFragment : Fragment() {
    companion object {
        private val TAG : String = TagFragment::class.java.simpleName
    }

    private val tagViewModel : TagViewModel by lazy {
        //TODO: Make sure this assert isn't problematic
        ViewModelProviders.of(activity!!)
                .get(TagViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val attachToRoot = false
        val view = inflater.inflate(R.layout.fragment_tag,
                container,
                attachToRoot)
        setRelatedObserver(view)
        setAddTagButtonOnClick(view)
        return view
    }

    private fun setRelatedObserver(view : View){
        val lifecycleOwner : LifecycleOwner = activity as LifecycleOwner
        tagViewModel.setObserver(lifecycleOwner, ::setTags, view)
    }
    private fun setAddTagButtonOnClick(view : View) {
        view.findViewById<AppCompatImageButton>(R.id.iv_btn_add_tag).setOnClickListener {
            val context = context!!
            val title = "Add Tag"
            val message = "Add a word tag"
            val dialogBuilder = InputAlertDialogBuilderFactory(context, title, message).get()
            dialogBuilder.setButton(AlertDialog.BUTTON_POSITIVE, "Add") { dialog, which ->
                dialog.dismiss()
            }
            dialogBuilder.show()
        }
    }

    private fun setTags(view : View?) {
        val recyclerView = view!!.findViewById<RecyclerView>(R.id.rv_tag)
        Log.v(TAG, "LiveData emitted.")
        val tags = tagViewModel.tags
        if(!tags.isNullOrEmpty()){
            Log.v(TAG, "Result size: [${tags.size}].")
            val layoutManager = FlexboxLayoutManager(context)
            layoutManager.flexDirection = FlexDirection.ROW
            layoutManager.justifyContent = JustifyContent.SPACE_AROUND

            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = TagAdapter(tags)
        }
    }
}
