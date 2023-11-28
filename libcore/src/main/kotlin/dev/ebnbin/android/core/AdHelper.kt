package dev.ebnbin.android.core

import android.view.ViewGroup
import androidx.core.view.doOnLayout
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

object AdHelper {
    fun adaptiveBanner(adContainer: ViewGroup, adUnitId: String = "ca-app-pub-3940256099942544/9214589741") {
        adContainer.doOnLayout {
            val adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                adContainer.context, adContainer.width.pxToDpInt)
            val adView = AdView(adContainer.context)
            adView.setAdSize(adSize)
            adView.adUnitId = adUnitId
            adContainer.addView(adView)
            adView.loadAd(AdRequest.Builder().build())
        }
    }
}
