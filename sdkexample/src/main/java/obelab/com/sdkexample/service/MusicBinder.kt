package obelab.com.sdkexample.service

import android.content.Context
import android.content.Intent
import android.content.res.AssetFileDescriptor
import obelab.com.sdkexample.adapter.CherryMusicPlayer
import obelab.com.sdkexample.adapter.PlayerAdapter
import obelab.com.sdkexample.model.Song
import obelab.com.sdkexample.utils.IMusicBinder
import obelab.com.sdkexample.utils.PlaybackInfoListener

/**
 * Created by anriku on 2018/11/2.
 */

/**
 * 用于与Activity或者Notification通信的Binder.并且还充当这Activity操作[PlayerAdapter]的中介
 */
class MusicBinder(private val mContext: Context) : IMusicBinder() {

    companion object {
        private const val TAG = "MusicBinder"
    }

    private val mPlayerAdapter: PlayerAdapter by lazy(LazyThreadSafetyMode.NONE) { CherryMusicPlayer(mContext) }

    /**
     * 用于对播放相关的内容设置
     *
     * @param intent startService所传入的intent
     */
    override fun playSet(intent: Intent) {
    }


    /**
     * 用于记录这次的播放的播放记录
     */
    override fun storeSongsAndIndex() {

    }

    override fun setSongs(songs: List<Song>) {
        mPlayerAdapter.setSongs(songs)
    }

    override fun getSongs(): List<Song>? = mPlayerAdapter.getSongs()

    override fun addPlaybackInfoListener(listener: PlaybackInfoListener) {
        mPlayerAdapter.addPlaybackInfoListener(listener)
    }

    override fun removePlaybackInfoListener(listener: PlaybackInfoListener) {
        mPlayerAdapter.removePlaybackInfoListener(listener)
    }

    override fun removeAllPlaybackInfoListener() {
        mPlayerAdapter.removeAllPlaybackInfoListener()
    }

    override fun loadAnotherMusic(isNext: Boolean) {
        mPlayerAdapter.loadAnotherMusic(isNext)
    }

    override fun loadMediaByPosition(position: Int, isOnlyLoad: Boolean) {
        mPlayerAdapter.loadMediaByPosition(position, isOnlyLoad)
    }

    override fun loadMedia(resourcePath: String, isOnlyLoad: Boolean, isOnline: Boolean) {
        mPlayerAdapter.loadMedia(resourcePath, isOnlyLoad, isOnline)
    }

    override fun play() {
        mPlayerAdapter.play()
    }

    override fun pause() {
        mPlayerAdapter.pause()
    }

    override fun reset() {
        mPlayerAdapter.reset()
    }

    override fun isPlaying(): Boolean = mPlayerAdapter.isPlaying() ?: false

    override fun seekTo(position: Int) {
        mPlayerAdapter.seekTo(position)
    }

    override fun release() {
        mPlayerAdapter.release()
    }

    override fun getCurrentPlayIndex(): Int = mPlayerAdapter.getCurrentPlayIndex()

    override fun loadMedia(fd: AssetFileDescriptor, isOnlyLoad: Boolean, isOnline: Boolean) {
        mPlayerAdapter.loadMedia(fd)
    }

}