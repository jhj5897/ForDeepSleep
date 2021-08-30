package org.jhj.fordeepsleep

import java.util.*


class CountDownSubject : Observable() {
    var isThreadRunning:Boolean = true

    fun changeRunningValue(bool:Boolean) {
        isThreadRunning = bool
        setChanged()
        notifyObservers()
    }
}