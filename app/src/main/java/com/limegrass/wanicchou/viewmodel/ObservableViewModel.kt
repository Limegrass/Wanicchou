package com.limegrass.wanicchou.viewmodel

import android.app.Application
import androidx.lifecycle.*

abstract class ObservableViewModel<T>(application: Application) : AndroidViewModel(application){
    //TODO: MediatorLiveData with source setting.
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

