package cat.dcat.nexhelper.ui

import android.content.Intent
import android.os.Bundle
import android.preference.*
import cat.dcat.nexhelper.R


class SettingsActivity : AppCompatPreferenceActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActionBar()
    }

    private fun setupActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    override fun onIsMultiPane(): Boolean {
        return false
    }


    override fun onBuildHeaders(target: List<PreferenceActivity.Header>) {
        loadHeadersFromResource(R.xml.pref_headers, target)
    }

    override fun isValidFragment(fragmentName: String): Boolean {
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }


}
