package cat.dcat.nexhelper.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import cat.dcat.nexhelper.R
import cat.dcat.util.d
import org.jetbrains.anko.defaultSharedPreferences


class NotificationFilterService : NotificationListenerService() {
    companion object {
        private val TAG = NotificationFilterService::class.java.simpleName
        //TODO instance lock
        var instance: NotificationFilterService? = null

    }

    private fun filteNotification(sbNoti: StatusBarNotification) {
        val noti = sbNoti.notification
        if (noti == null) return
        val pkgName = sbNoti.packageName
        val extras = noti.extras
        if (extras == null) return
        val title = (extras.get("android.title") ?: "").toString()
        val text = (extras.get("android.text") ?: "").toString()
        TAG.d("${pkgName} --> ${title} ${text}")
        when (sbNoti.packageName) {
            "com.vivo.daemonService" -> {
                if (defaultSharedPreferences.getBoolean(getString(R.string.noti_remove_dev_warn), false)) {
                    TAG.d("Canceling vivo's shit. ${sbNoti.key}")
                    snoozeNotification(sbNoti.key, 24 * 60 * 60 * 1000)
                }
            }
            else -> {
                if (sbNoti.isClearable) {
                    if (defaultSharedPreferences.getBoolean(getString(R.string.noti_remove_title), false)) {
                        if (title.trim().isEmpty() && text.trim().isEmpty()) {
                            TAG.d("Removing notification's title. ${sbNoti.key} ${sbNoti.groupKey} ${sbNoti.overrideGroupKey}")
                            cancelNotification(sbNoti.key)
                        }

                    }
                }

            }
        }
    }

    public fun filteAllNotifications() {
        TAG.d("Attached~")
        val msg = getActiveNotifications()
        for (sbNoti in msg) {
            filteNotification(sbNoti)
        }
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        instance = this
        filteAllNotifications()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        if (sbn == null) return
        filteNotification(sbn)

    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        //filteAllNotifications()
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        instance = null
    }
}
