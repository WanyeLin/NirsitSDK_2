package obelab.com.sdkexample.utils

import android.content.Intent
import android.os.Binder
import obelab.com.sdkexample.adapter.PlayerAdapter

/**
 * Created by anriku on 2018/11/2.
 */

abstract class IMusicBinder : Binder(), PlayerAdapter {

    companion object {
        const val SEQUENCE_PLAY = 0
        const val RANDOM_PLAY = 1
        const val SINGLE_PLAY = 2
    }

    abstract fun playSet(intent: Intent)

    abstract fun storeSongsAndIndex()
}