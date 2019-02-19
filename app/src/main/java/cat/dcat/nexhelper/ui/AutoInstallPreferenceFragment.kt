package cat.dcat.nexhelper.ui

import android.content.Intent
import android.os.Bundle
import android.preference.EditTextPreference
import android.preference.ListPreference
import android.preference.PreferenceFragment
import android.preference.SwitchPreference
import android.provider.Settings
import cat.dcat.nexhelper.service.AutoInstallService
import cat.dcat.nexhelper.R
import cat.dcat.util.bindPreferenceSummaryToValue

/**
 * Created by DCat on 2019/2/14.
 */
class AutoInstallPreferenceFragment : PreferenceFragment() {
    companion object {
        val A_NUMBER = 0x114514
    }

    lateinit var statePref: SwitchPreference
    lateinit var passwdPref: EditTextPreference
    lateinit var acceptSourcePref: ListPreference

    private fun updateServiceState() {
        statePref.isChecked = AutoInstallService.isEnabled()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == A_NUMBER) updateServiceState()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_autoinstall)
        setHasOptionsMenu(true)

        statePref = findPreference("autoinstall_service_state") as SwitchPreference
        statePref.setOnPreferenceClickListener {
            startActivityForResult(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS), A_NUMBER)
            false
        }

        passwdPref = findPreference(getString(R.string.vivo_account_password)) as EditTextPreference
        bindPreferenceSummaryToValue(passwdPref)

        acceptSourcePref = findPreference(getString(R.string.autoinstall_accept_source)) as ListPreference
        bindPreferenceSummaryToValue(acceptSourcePref)

        updateServiceState()
    }
}