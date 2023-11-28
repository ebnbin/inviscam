package dev.ebnbin.android.core

import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.crashlytics

val firebaseAnalytics: FirebaseAnalytics
    get() = Firebase.analytics

val firebaseCrashlytics: FirebaseCrashlytics
    get() = Firebase.crashlytics
