package com.limegrass.wanicchou.viewmodel

import androidx.lifecycle.*

abstract class ObservableViewModel<T> : ViewModel(){
    //TODO: MediatorLiveData with source setting.
    private val liveData : MutableLiveData<T> = MutableLiveData()

    open var value : T?
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

