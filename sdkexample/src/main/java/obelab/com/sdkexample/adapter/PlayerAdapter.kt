package obelab.com.sdkexample.adapter

import android.content.res.AssetFileDescriptor
import obelab.com.sdkexample.model.Song
import obelab.com.sdkexample.utils.PlaybackInfoListener

/**
 * 播放器类需要实现的接口
 *
 * Created by anriku on 2018/10/31.
 */
interface PlayerAdapter {

    /**
     * 给播放器添加需要获取播放信息的接口类。
     *
     * @param listener 想获取播放信息的类
     */
    fun addPlaybackInfoListener(listener: PlaybackInfoListener)

    /**
     * 移除对应的播放器类
     *
     * @param listener 要被移除的获取播放信息的类
     */
    fun removePlaybackInfoListener(listener: PlaybackInfoListener)

    /**
     * 移除所有的播放器类
     */
    fun removeAllPlaybackInfoListener()

    /**
     * 用于切换音乐的。下面的情况下进行调用
     * 1. 点击上一首切换
     * 2. 点击下一首切换
     * 3. 一首歌完了进行切换
     *
     * @param isNext 如果是[SEQUENCE_PLAY]模式这个参数表示是下一首还是上一首。
     */
    fun loadAnotherMusic(isNext: Boolean = true)

    /**
     * 加载本地音乐列表指定的音乐。
     *
     * @param position 要播放的音乐在本地音乐列表的中的位置
     * @param isOnlyLoad 是否是仅加载不播放
     */
    fun loadMediaByPosition(position: Int, isOnlyLoad: Boolean = false)

    /**
     * 根据本地音乐的路径或者在线音乐的Uri进行音乐的加载。
     *
     * @param resourcePath 音乐路径或者Uri
     * @param isOnlyLoad 是否是仅加载不播放
     * @param isOnline 表示是否为在线音乐
     */
    fun loadMedia(resourcePath: String, isOnlyLoad: Boolean = false, isOnline: Boolean = false)

    fun loadMedia(fd: AssetFileDescriptor, isOnlyLoad: Boolean = false, isOnline: Boolean = false)

    fun play()

    fun pause()

    fun reset()

    fun isPlaying(): Boolean

    fun seekTo(position: Int)

    fun release()

    fun setSongs(songs: List<Song>)

    fun getSongs(): List<Song>?

    fun getCurrentPlayIndex(): Int
}