package io.github.tmarsteel.networklayout

import java.util.concurrent.atomic.AtomicReference
import kotlin.reflect.KProperty

class LateinitOnce<T : Any> {
    private val value = AtomicReference<T?>(null)
    operator fun getValue(thisRef: Any?, prop: KProperty<*>): T {
        return value.get() ?: throw IllegalStateException("Field $prop hasn't been initialized yet")
    }

    operator fun setValue(thisRef: Any?, prop: KProperty<*>, value: T) {
        if (!this.value.compareAndSet(null, value)) {
            throw IllegalStateException("Field $prop has already been initialized")
        }
    }
}

fun <T : Any> lateinitOnce() = LateinitOnce<T>()