package dev.ebnbin.android.core

import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import androidx.core.content.ContentProviderCompat
import com.google.android.gms.ads.MobileAds

lateinit var coreApp: Application
    private set

internal class CoreInitializer : ContentProvider() {
    override fun onCreate(): Boolean {
        coreApp = ContentProviderCompat.requireContext(this).applicationContext as Application
        MobileAds.initialize(coreApp)
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?,
    ): Cursor? {
        return null
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }
}
