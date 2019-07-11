package obelab.com.sdkexample.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import obelab.com.sdkexample.BaseApp
import obelab.com.sdkexample.MainActivity
import obelab.com.sdkexample.R

/**
 * Created by anriku on 2019-06-13.
 */

class MusicService: Service() {

    private val MUSIC_NOTIFICATION_ID = 1

    private val mMusicBinder: MusicBinder by lazy {
        MusicBinder(this)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return mMusicBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        addNotification()
        return super.onStartCommand(intent, flags, startId)
    }


    /**
     * 添加通知并将服务置于前台
     */
    private fun addNotification() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent,
                0
        )
        // 将服务置于前台
        startForeground(MUSIC_NOTIFICATION_ID, NotificationCompat.Builder(this, BaseApp.MUSIC_SERVICE_CHANNEL_ID)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_music_note_black_24dp)
                .setOngoing(true)
                .build())
    }
}