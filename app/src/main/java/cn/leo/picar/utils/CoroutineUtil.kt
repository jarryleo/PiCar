package cn.leo.pi.utils

import kotlinx.coroutines.*

object CoroutineUtil {
    /**
     * io协程，运行在io线程
     */
    fun io(block: suspend CoroutineScope.() -> Unit): Job {
        GlobalScope.launch {  }
        return GlobalScope.launch(Dispatchers.IO) {
            block()
        }
    }


    /**
     * 运行在io线程的有返回值的协程
     */
    fun <T> wait(block: suspend CoroutineScope.() -> T): Deferred<T> {
        return GlobalScope.async(Dispatchers.IO) {
            block()
        }
    }
}