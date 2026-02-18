package com.bettafish.flarent.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SuspendCommand<T1>(val func: suspend (arg1:T1) -> Unit, val coroutineScope: CoroutineScope, var onCompleted: () -> Unit = {}){
    val canExecute = MutableStateFlow(true)

    fun execute(arg1:T1){
        if(!canExecute.value) return
        canExecute.value = false
        coroutineScope.launch {
            try {
                func(arg1)
                onCompleted()
            }
            catch (e: Exception) {
            }
            finally {
                canExecute.value = true
            }
        }
    }
}

class SuspendCommand2<T1,T2>(val func: suspend (arg1:T1, arg2:T2) -> Unit, val coroutineScope: CoroutineScope){
    val canExecute = MutableStateFlow(true)

    fun execute(arg1:T1, arg2:T2){
        if(!canExecute.value) return
        canExecute.value = false
        coroutineScope.launch {
            try {
                func(arg1,arg2)
            }
            catch (e: Exception) {
            }
            finally {
                canExecute.value = true
            }
        }
    }
}


class SuspendCommand3<T1,T2,T3>(val func: suspend (arg1:T1, arg2:T2, arg3:T3) -> Unit, val coroutineScope: CoroutineScope){
    val canExecute = MutableStateFlow(true)

    fun execute(arg1:T1, arg2:T2, arg3:T3){
        if(!canExecute.value) return
        canExecute.value = false
        coroutineScope.launch {
            try {
                func(arg1, arg2, arg3)
            }
            catch (e: Exception) {
            }
            finally {
                canExecute.value = true
            }
        }
    }
}