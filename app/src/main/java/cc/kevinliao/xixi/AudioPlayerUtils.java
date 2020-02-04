package cc.kevinliao.xixi;

import android.content.Context;
import android.content.ContextWrapper;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 * MediaPlayer简单封装，针对项目中频繁的音频播放逻辑
 */
public class AudioPlayerUtils {

    private volatile static AudioPlayerUtils mInstance;
    private MediaPlayer mPlayer;
    private boolean isPause;

    /**
     * 单例
     * @return AudioPlayerUtils
     */
    public static AudioPlayerUtils getInstance() {
        if (mInstance == null) {
            synchronized (AudioPlayerUtils.class) {
                if (mInstance == null) {
                    mInstance = new AudioPlayerUtils();
                }
            }
        }
        return mInstance;
    }

    /**
     * 播放
     * @param voicePath 音频地址
     * @param onCompletionListener 结束监听
     */
    public void play(Context context, String voicePath, MediaPlayer.OnCompletionListener onCompletionListener) {
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
            //设置报错监听
            mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    mPlayer.reset();
                    return false;
                }
            });
        } else {
            mPlayer.reset();//恢复
        }
        try {
            Uri uri = uriFromPath(context, voicePath);
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.setOnCompletionListener(onCompletionListener);
            if (uri != null) {
                if ("http".equals(uri.getScheme()) || "https".equals(uri.getScheme())) {// 异步播放网络音频
                    mPlayer.setDataSource(context, uri);
                    mPlayer.prepareAsync();
                    mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            // 准备完成后, 开始播放音频文件
                            mp.start();
                        }
                    });
                } else {// 本地音频
                    mPlayer.setDataSource(context, uri);
                    mPlayer.prepare();
                    mPlayer.start();
                }
            }
        } catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 暂停
     */
    public void pause() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
            isPause = true;
        }
    }

    /**
     * 继续
     */
    public void resume() {
        if (mPlayer != null && isPause) {
            mPlayer.start();
            isPause = false;
        }
    }

    public void release() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    /**
     * 解析地址
     * @param context 上下文
     * @param path 资源路径
     * @return Uri
     */
    private Uri uriFromPath(Context context, String path) {
        File file = null;
        String fileNameWithoutExt;
        String extPath;

        // 尝试在"raw"目录寻找资源
        if (path.lastIndexOf('.') != -1) {
            fileNameWithoutExt = path.substring(0, path.lastIndexOf('.'));
        } else {
            fileNameWithoutExt = path;
        }

        int resId = context.getResources().getIdentifier(fileNameWithoutExt,
                "raw", context.getPackageName());
        if (resId != 0) {
            return Uri.parse("android.resource://" + context.getPackageName() + "/" + resId);
        }

        // 尝试在app data目录寻找资源
        extPath = new ContextWrapper(context).getFilesDir() + "/" + path;
        file = new File(extPath);
        if (file.exists()) {
            return Uri.fromFile(file);
        }

        // 尝试在sdcard寻找资源
        extPath = Environment.getExternalStorageDirectory() + "/" + path;
        file = new File(extPath);
        if (file.exists()) {
            return Uri.fromFile(file);
        }

        // 尝试在完整目录寻找资源
        file = new File(path);
        if (file.exists()) {
            return Uri.fromFile(file);
        }

        // 网络资源
        return Uri.parse(path);
    }
}
