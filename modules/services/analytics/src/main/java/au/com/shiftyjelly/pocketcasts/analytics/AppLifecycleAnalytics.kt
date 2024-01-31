package au.com.shiftyjelly.pocketcasts.analytics

import android.content.Context
import au.com.shiftyjelly.pocketcasts.preferences.Settings
import au.com.shiftyjelly.pocketcasts.utils.PackageUtil
import au.com.shiftyjelly.pocketcasts.utils.timeIntervalSinceNow
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AppLifecycleAnalytics @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val settings: Settings,
    private val packageUtil: PackageUtil,
    private val analyticsTracker: AnalyticsTrackerWrapper
) {
    /* The date the app was last opened, used for calculating time in app */
    private var applicationOpenedDate: Date? = null

    fun onApplicationInstalledOrUpgraded() {
        // Track app upgrade and install
        val versionCode = packageUtil.getVersionCode(appContext)
        val oldVersionCode = settings.getMigratedVersionCode()

        if (oldVersionCode == 0) {
            // Track application installed if there isn't old version code
            analyticsTracker.track(AnalyticsEvent.APPLICATION_INSTALLED)
        } else if (oldVersionCode < versionCode) {
            // app upgraded
            analyticsTracker.track(AnalyticsEvent.APPLICATION_UPDATED, mapOf(KEY_PREVIOUS_VERSION_CODE to oldVersionCode))
        }
    }

    fun onApplicationEnterForeground() {
        applicationOpenedDate = Date()
        analyticsTracker.track(AnalyticsEvent.APPLICATION_OPENED)
    }

    fun onApplicationEnterBackground() {
        val properties: MutableMap<String, Any> = HashMap()
        applicationOpenedDate?.let {
            properties[KEY_TIME_IN_APP] = TimeUnit.MILLISECONDS.toSeconds(it.timeIntervalSinceNow()).toInt()
            applicationOpenedDate = null
        }
        analyticsTracker.track(AnalyticsEvent.APPLICATION_CLOSED, properties)
    }

    companion object {
        const val KEY_PREVIOUS_VERSION_CODE = "previous_version_code"
        const val KEY_TIME_IN_APP = "time_in_app" // time in seconds
    }
}
