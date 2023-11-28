package dev.ebnbin.android.core

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

fun <T> LiveData<T>.get(): T {
    @Suppress("UNCHECKED_CAST")
    return value as T
}

fun <T> MutableLiveData<T>.set(value: T, diff: Boolean = true) {
    if (diff && get() == value) {
        return
    }
    this.value = value
}

fun <T> LiveData<T>.observeOnce(
    lifecycleOwner: LifecycleOwner,
    condition: (T) -> Boolean,
    observer: Observer<in T>,
) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(value: T) {
            if (condition(value)) {
                removeObserver(this)
                observer.onChanged(value)
            }
        }
    })
}

//*********************************************************************************************************************

fun <X, Y> LiveData<X>.switchMap(
    diff: Boolean = true,
    transform: (X) -> LiveData<Y>,
): LiveData<Y> {
    val mediatorLiveData = MediatorLiveData(transform(get()).get())
    mediatorLiveData.addSource(this, object : Observer<X> {
        private var liveData: LiveData<Y> = transform(get()).also {
            mediatorLiveData.addSource(it) { y -> mediatorLiveData.set(y, diff) }
        }

        override fun onChanged(value: X) {
            val oldLiveData = liveData
            val newLiveData = transform(value)
            if (oldLiveData === newLiveData) {
                return
            }
            mediatorLiveData.removeSource(oldLiveData)
            liveData = newLiveData
            mediatorLiveData.addSource(newLiveData) { y -> mediatorLiveData.set(y, diff) }
        }
    })
    return mediatorLiveData
}

fun <X, Y> LiveData<X>.map(
    diff: Boolean = true,
    transform: (X) -> Y,
): LiveData<Y> {
    return combine(
        sourceList = listOf(this),
        diff = diff,
    ) {
        @Suppress("UNCHECKED_CAST")
        transform(it[0] as X)
    }
}

fun <S1, S2> combine(
    source1: LiveData<S1>,
    source2: LiveData<S2>,
    diff: Boolean = true,
): LiveData<Pair<S1, S2>> {
    return combine(
        sourceList = listOf(
            source1,
            source2,
        ),
        diff = diff,
    ) {
        @Suppress("UNCHECKED_CAST")
        Pair(
            first = it[0] as S1,
            second = it[1] as S2,
        )
    }
}

fun <S1, S2, S3> combine(
    source1: LiveData<S1>,
    source2: LiveData<S2>,
    source3: LiveData<S3>,
    diff: Boolean = true,
): LiveData<Triple<S1, S2, S3>> {
    return combine(
        sourceList = listOf(
            source1,
            source2,
            source3,
        ),
        diff = diff,
    ) {
        @Suppress("UNCHECKED_CAST")
        Triple(
            first = it[0] as S1,
            second = it[1] as S2,
            third = it[2] as S3,
        )
    }
}

fun <S1, S2, S3, S4> combine(
    source1: LiveData<S1>,
    source2: LiveData<S2>,
    source3: LiveData<S3>,
    source4: LiveData<S4>,
    diff: Boolean = true,
): LiveData<Tuple4<S1, S2, S3, S4>> {
    return combine(
        sourceList = listOf(
            source1,
            source2,
            source3,
            source4,
        ),
        diff = diff,
    ) {
        @Suppress("UNCHECKED_CAST")
        Tuple4(
            v1 = it[0] as S1,
            v2 = it[1] as S2,
            v3 = it[2] as S3,
            v4 = it[3] as S4,
        )
    }
}

fun <S1, S2, S3, S4, S5> combine(
    source1: LiveData<S1>,
    source2: LiveData<S2>,
    source3: LiveData<S3>,
    source4: LiveData<S4>,
    source5: LiveData<S5>,
    diff: Boolean = true,
): LiveData<Tuple5<S1, S2, S3, S4, S5>> {
    return combine(
        sourceList = listOf(
            source1,
            source2,
            source3,
            source4,
            source5,
        ),
        diff = diff,
    ) {
        @Suppress("UNCHECKED_CAST")
        Tuple5(
            v1 = it[0] as S1,
            v2 = it[1] as S2,
            v3 = it[2] as S3,
            v4 = it[3] as S4,
            v5 = it[4] as S5,
        )
    }
}

fun <S1, S2, S3, S4, S5, S6> combine(
    source1: LiveData<S1>,
    source2: LiveData<S2>,
    source3: LiveData<S3>,
    source4: LiveData<S4>,
    source5: LiveData<S5>,
    source6: LiveData<S6>,
    diff: Boolean = true,
): LiveData<Tuple6<S1, S2, S3, S4, S5, S6>> {
    return combine(
        sourceList = listOf(
            source1,
            source2,
            source3,
            source4,
            source5,
            source6,
        ),
        diff = diff,
    ) {
        @Suppress("UNCHECKED_CAST")
        Tuple6(
            v1 = it[0] as S1,
            v2 = it[1] as S2,
            v3 = it[2] as S3,
            v4 = it[3] as S4,
            v5 = it[4] as S5,
            v6 = it[5] as S6,
        )
    }
}

fun <S1, S2, S3, S4, S5, S6, S7> combine(
    source1: LiveData<S1>,
    source2: LiveData<S2>,
    source3: LiveData<S3>,
    source4: LiveData<S4>,
    source5: LiveData<S5>,
    source6: LiveData<S6>,
    source7: LiveData<S7>,
    diff: Boolean = true,
): LiveData<Tuple7<S1, S2, S3, S4, S5, S6, S7>> {
    return combine(
        sourceList = listOf(
            source1,
            source2,
            source3,
            source4,
            source5,
            source6,
            source7,
        ),
        diff = diff,
    ) {
        @Suppress("UNCHECKED_CAST")
        Tuple7(
            v1 = it[0] as S1,
            v2 = it[1] as S2,
            v3 = it[2] as S3,
            v4 = it[3] as S4,
            v5 = it[4] as S5,
            v6 = it[5] as S6,
            v7 = it[6] as S7,
        )
    }
}

fun <S1, S2, S3, S4, S5, S6, S7, S8> combine(
    source1: LiveData<S1>,
    source2: LiveData<S2>,
    source3: LiveData<S3>,
    source4: LiveData<S4>,
    source5: LiveData<S5>,
    source6: LiveData<S6>,
    source7: LiveData<S7>,
    source8: LiveData<S8>,
    diff: Boolean = true,
): LiveData<Tuple8<S1, S2, S3, S4, S5, S6, S7, S8>> {
    return combine(
        sourceList = listOf(
            source1,
            source2,
            source3,
            source4,
            source5,
            source6,
            source7,
            source8,
        ),
        diff = diff,
    ) {
        @Suppress("UNCHECKED_CAST")
        Tuple8(
            v1 = it[0] as S1,
            v2 = it[1] as S2,
            v3 = it[2] as S3,
            v4 = it[3] as S4,
            v5 = it[4] as S5,
            v6 = it[5] as S6,
            v7 = it[6] as S7,
            v8 = it[7] as S8,
        )
    }
}

fun <S1, S2, S3, S4, S5, S6, S7, S8, S9> combine(
    source1: LiveData<S1>,
    source2: LiveData<S2>,
    source3: LiveData<S3>,
    source4: LiveData<S4>,
    source5: LiveData<S5>,
    source6: LiveData<S6>,
    source7: LiveData<S7>,
    source8: LiveData<S8>,
    source9: LiveData<S9>,
    diff: Boolean = true,
): LiveData<Tuple9<S1, S2, S3, S4, S5, S6, S7, S8, S9>> {
    return combine(
        sourceList = listOf(
            source1,
            source2,
            source3,
            source4,
            source5,
            source6,
            source7,
            source8,
            source9,
        ),
        diff = diff,
    ) {
        @Suppress("UNCHECKED_CAST")
        Tuple9(
            v1 = it[0] as S1,
            v2 = it[1] as S2,
            v3 = it[2] as S3,
            v4 = it[3] as S4,
            v5 = it[4] as S5,
            v6 = it[5] as S6,
            v7 = it[6] as S7,
            v8 = it[7] as S8,
            v9 = it[8] as S9,
        )
    }
}

fun combine(
    sourceList: List<LiveData<*>>,
    diff: Boolean = true,
): LiveData<List<*>> {
    return combine(
        sourceList = sourceList,
        diff = diff,
        transform = { it },
    )
}

private fun <T> combine(
    sourceList: List<LiveData<*>>,
    diff: Boolean = true,
    transform: (List<*>) -> T,
): LiveData<T> {
    val valueList = sourceList.map { it.get() }
    val mediatorLiveData = MediatorLiveData(transform(valueList))
    sourceList.forEach { source ->
        mediatorLiveData.addSource(source) { value ->
            val sourceValueList = sourceList.map { if (source == it) value else it.get() }
            mediatorLiveData.set(
                value = transform(sourceValueList),
                diff = diff,
            )
        }
    }
    return mediatorLiveData
}
