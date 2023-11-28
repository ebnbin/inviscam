package dev.ebnbin.android.core

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit

fun Fragment.remove() {
    parentFragmentManager.commit(allowStateLoss = true) {
        remove(this@remove)
    }
}

fun FragmentManager.createFragment(
    fragmentClassName: String,
    arguments: Bundle? = null,
): Fragment {
    return fragmentFactory.instantiate(coreApp.classLoader, fragmentClassName).apply {
        this.arguments = arguments
    }
}

inline fun <reified T : Fragment> FragmentManager.createFragment(
    arguments: Bundle? = null,
): T {
    return createFragment(
        fragmentClassName = T::class.java.name,
        arguments = arguments,
    ) as T
}
