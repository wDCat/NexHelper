package cat.dcat.nexhelper.ui

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceFragment
import android.preference.SwitchPreference
import android.provider.Settings
import android.support.v4.app.NotificationManagerCompat
import cat.dcat.nexhelper.R
import cat.dcat.nexhelper.service.NotificationFilterService
import org.jetbrains.anko.toast


/**
 * Created by DCat on 2019/2/14.
 */
class NotificationFilterPreferenceFragment : PreferenceFragment() {
    companion object {
        val TAG = NotificationFilterPreferenceFragment::class.java.simpleName
        val A_NUMBER = 0x114514
    }

    lateinit var permissionPref: SwitchPreference
    private fun updatePermissionState() {
        val packageNames = NotificationManagerCompat.getEnabledListenerPackages(activity)
        permissionPref.isChecked = packageNames.contains(activity.packageName)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == A_NUMBER) updatePermissionState()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_notifilter)
        setHasOptionsMenu(true)
        permissionPref = findPreference(getString(R.string.noti_permission)) as SwitchPreference
        permissionPref.setOnPreferenceChangeListener { preference, newValue ->
            startActivityForResult(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS), A_NUMBER)

            false
        }
        findPreference(getString(R.string.noti_remove_draw_warn)).setOnPreferenceClickListener {
            val intent = Intent()
            intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            intent.putExtra("android.provider.extra.APP_PACKAGE", "android")
            startActivity(intent)
            true
        }
        findPreference("noti_refresh").setOnPreferenceClickListener {
            if (NotificationFilterService.instance != null) {
                NotificationFilterService.instance!!.filteAllNotifications()
                activity.toast("OK").show()
            }
            true
        }
        updatePermissionState()
    }
}