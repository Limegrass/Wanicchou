package com.waifusims.wanicchou.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

abstract class ObservableViewModel<T>(application: Application) : AndroidViewModel(application){
    private val liveData : MutableLiveData<T> = MutableLiveData()

    var value : T?
        get() {
            return liveData.value
        }
        set(value){
            liveData.value = value
        }

    fun setObserver(lifecycleOwner: LifecycleOwner,
                    action : () -> Unit){
        val observer = Observer<T>{
            action()
        }
        liveData.observe(lifecycleOwner, observer)
    }
}

