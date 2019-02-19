package cat.dcat.util

import android.content.Context
import android.content.res.Configuration
import android.preference.EditTextPreference
import android.preference.ListPreference
import android.preference.Preference
import android.preference.PreferenceManager

/**
 * Created by DCat on 2019/2/14.
 */

private val sBindPreferenceSummaryToValueListener = Preference.OnPreferenceChangeListener { preference, value ->
    val stringValue = "" + value.toString()

    if (preference is ListPreference) {
        // For list preferences, look up the correct display value in
        // the preference's 'entries' list.
        val listPreference = preference
        var index: Int
        try {
            index = listPreference.findIndexOfValue(stringValue)
        } catch (ignored: Throwable) {
            index = 0
        }

        // Set the summary to reflect the new value.
        preference.setSummary(
                if (index >= 0)
                    listPreference.entries[index]
                else
                    null)

    } else if (preference is EditTextPreference) {
        //Copied from Stack overflow :>
        val edit = preference.editText
        val pref = edit.transformationMethod.getTransformation(stringValue, edit).toString()
        preference.setSummary(pref)
    } else {
        // For all other preferences, set the summary to the value's
        // simple string representation.
        preference.summary = stringValue
    }
    true
}

fun bindPreferenceSummaryToValue(preference: Preference) {
    // Set the listener to watch for value changes.
    preference.onPreferenceChangeListener = sBindPreferenceSummaryToValueListener

    // Trigger the listener immediately with the preference's
    // current value.
    sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
            PreferenceManager
                    .getDefaultSharedPreferences(preference.context)
                    .getString(preference.key, ""))
}