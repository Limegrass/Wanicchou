package com.waifusims.wanicchou.viewmodel

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import data.room.entity.Tag

class TagViewModel(application: Application) : AndroidViewModel(application){
    companion object {
        private val TAG = TagViewModel::class.java.simpleName
    }
    private val tagLiveData : MutableLiveData<List<Tag>> = MutableLiveData()

    init{
        tagLiveData.value = listOf()
    }

    private val currentList : List<Tag>
        get() {
            return tagLiveData.value!!
        }

    val tags : List<Tag>
        get() {
            val currentLiveDataValue = currentList
            return if (currentLiveDataValue.isNotEmpty()){
                currentLiveDataValue
            }
            else{
                listOf()
            }
        }

    fun setObserver(lifecycleOwner: LifecycleOwner,
                    action : (View?) -> Unit,
                    view : View? = null){
        val definitionObserver = Observer<List<Tag>>{
            action(view)
        }
        tagLiveData.observe(lifecycleOwner, definitionObserver)
    }

    fun setTags(tags : List<Tag>){
        tagLiveData.value = tags
    }

}
