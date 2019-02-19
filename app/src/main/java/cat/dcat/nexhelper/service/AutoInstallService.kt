package cat.dcat.nexhelper.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import cat.dcat.nexhelper.R
import cat.dcat.util.d
import org.jetbrains.anko.defaultSharedPreferences


class AutoInstallService : AccessibilityService() {
    private val TAG = AutoInstallService::class.java.simpleName

    companion object {
        private var instance: AutoInstallService? = null
        public fun isEnabled(): Boolean {
            return instance != null
        }
    }

    override fun onInterrupt() {

    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        TAG.d("Service connected.")
        instance = this
    }

    override fun onUnbind(intent: Intent): Boolean {
        TAG.d("Service disconnected.")
        instance = null
        return super.onUnbind(intent)
    }

    //TODO
    fun dumpView(source: AccessibilityNodeInfo, level: Int = 2) {
        TAG.d("--------------------------")
        for (x in 0..source.childCount - 1) {
            val child = source.getChild(x)
            TAG.d("Index:${x} ${child.text}")
            for (y in 0..child.childCount - 1) {
                TAG.d("------Index:${y} ${child.getChild(y).text}")
            }

        }
        TAG.d("--------------------------")
    }

    private fun isAutoInputPwEnabled() = defaultSharedPreferences.getBoolean(getString(R.string.auto_input_passwd), false)

    private fun handlePackageInstallerEvents(event: AccessibilityEvent) {
        if (event.source == null) {
            return
        }

        val source = event.source
        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                if (source.childCount != 3) {
                    return
                }
                if (isAutoInputPwEnabled()) {
                    val kwVivoAccount = getString(R.string.keyword_use_password)
                    if (kwVivoAccount.equals(source.getChild(2).text)) {
                        source.getChild(2).performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    }
                }
            }
            AccessibilityEvent.TYPE_VIEW_CLICKED -> {
                /*
                val text = "" + source.text
                TAG.d("Clicked ${text}")
                dumpView(source)
                */
            }
            AccessibilityEvent.TYPE_VIEW_SCROLLED -> {
            }
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                if (source.childCount < 4) {
                    return
                }
                val autoInstallEnabled = defaultSharedPreferences.getBoolean(getString(R.string.autoinstall_enable), false)
                if (!autoInstallEnabled) {
                    TAG.d("Disabled")
                    return
                }
                val appName = source.getChild(0).text
                var from = source.getChild(1).text
                val kwFrom = getString(R.string.keyword_from)
                val kwVersion = getString(R.string.keyword_version)
                if (from == null || from.length < kwFrom.length + 2 || !from.startsWith(kwFrom)) {
                    return
                }
                from = from.substring(kwFrom.length + 1, from.length - 1).trim()
                var version = source.getChild(2).text

                if (version == null || version.length < kwVersion.length + 2 || !version.startsWith(kwVersion)) {
                    return
                }
                version = version.substring(kwVersion.length + 1).trim()
                TAG.d("Try to install ${appName} from ${from} version:${version}")
                val kwInstallDone = getString(R.string.keyword_install_done)
                val kwInstallFailed = getString(R.string.keyword_install_failed)
                val installStateText = source.getChild(4).text

                var afterInstalled = false
                if (kwInstallDone.equals(installStateText)) {
                    TAG.d("Installed ${appName} from ${from} version:${version}")
                    afterInstalled = true
                } else if (kwInstallFailed.equals(installStateText)) {
                    TAG.d("Failed to install ${appName} from ${from} version:${version}")
                    afterInstalled = true
                }
                if (afterInstalled) {
                    val kwDone = getString(R.string.keyword_done)
                    for (x in source.childCount - 1 downTo 2) {
                        val child = source.getChild(x)
                        if (kwDone.equals(child.text)) {
                            child.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                        }
                    }
                } else {
                    val kwInstall1 = getString(R.string.keyword_install1)
                    val kwInstall2 = getString(R.string.keyword_install2)

                    for (x in source.childCount - 1 downTo 2) {
                        val child = source.getChild(x)
                        if (kwInstall1.equals(child.text)) {
                            child.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                        }
                        if (kwInstall2.equals(child.text)) {
                            try {
                                dumpView(source)
                                val clickNode = source.getChild(4).getChild(0)
                                val text = "" + clickNode.getChild(0).text
                                val kwRemoveApk = getString(R.string.keyword_remove_apk)
                                if (text.startsWith(kwRemoveApk)) {
                                    val dontRemoveApk = defaultSharedPreferences.getBoolean(getString(R.string.auto_remove_apk), true)
                                    TAG.d("------------do not remove apk:${dontRemoveApk}")
                                    if (dontRemoveApk) {
                                        clickNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                                    }
                                }
                            } catch (ignored: Throwable) {

                            }
                            val acceptSource = defaultSharedPreferences.getString(getString(R.string.autoinstall_accept_source), "0")
                            val kwUnknownSources = getString(R.string.keyword_unknown_source)
                            if (acceptSource == "1" || kwUnknownSources.equals(from)) {
                                child.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                            }

                        }
                    }
                }
            }

        }
    }

    private fun handleFingerPrintUIEvents(event: AccessibilityEvent) {

    }

    private fun handleLoginUIEvents(event: AccessibilityEvent) {
        if (event.source == null) {
            return
        }
        if (!isAutoInputPwEnabled()) return
        val source = event.source
        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                if (source.childCount < 4) {
                    return
                }
                val kwTryToInstall = getString(R.string.keyword_try_to_install)
                val kwOK = getString(R.string.keyword_ok)
                val title = source.getChild(0).text
                if (title != null && title.contains(kwTryToInstall)) {
                    val editBox = source.getChild(1)
                    if (!editBox.className.contains("EditText")) {
                        return
                    }
                    val password = defaultSharedPreferences.getString(getString(R.string.vivo_account_password), "")
                    if ("".equals(password)) {
                        return
                    }
                    val arguments = Bundle().apply {
                        putCharSequence(AccessibilityNodeInfo
                                .ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, password)
                    }
                    editBox.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
                    for (x in source.childCount - 1 downTo 0) {
                        val child = source.getChild(x)
                        if (kwOK.equals(child.text)) {
                            child.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                        }

                    }

                }
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        //android:packageNames="com.android.packageinstaller,com.bbk.account,com.vivo.fingerprintui"
        if (event == null) return
        TAG.d("Got event from ${event.packageName},id:${event.eventType}")
        when (event.packageName) {
            "com.android.packageinstaller" -> {
                handlePackageInstallerEvents(event)
            }
            "com.vivo.fingerprintui" -> {
                handleFingerPrintUIEvents(event)
            }
            "com.bbk.account" -> {
                handleLoginUIEvents(event)
            }

        }

    }


}
