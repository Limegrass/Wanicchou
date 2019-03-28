package com.waifusims.wanicchou.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

abstract class ObservableListViewModel<T>(application: Application) : AndroidViewModel(application){
    private val listLiveData : MutableLiveData<List<T>> = MutableLiveData()

    var list : List<T>?
        get() {
            return listLiveData.value
        }
        set(value){
            listLiveData.value = value
        }

    fun setObserver(lifecycleOwner: LifecycleOwner,
                    action : () -> Unit){
        val observer = Observer<List<T>>{
            action()
        }
        listLiveData.observe(lifecycleOwner, observer)
    }
}
