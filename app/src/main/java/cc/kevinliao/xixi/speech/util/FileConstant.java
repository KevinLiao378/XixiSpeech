package cc.kevinliao.xixi.speech.util;

import android.os.Environment;

import java.io.File;

import cc.kevinliao.xixi.BuildConfig;

public class FileConstant {

    public static String OutPutlistFileName = "output.json";
    public static String OutPutlistZipFileName = "SpeechAudios.zip";

    public static String getSpeechRootPath() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + BuildConfig.APPLICATION_ID  + File.separator + "speech";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    public static String getJsonRootPath() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + BuildConfig.APPLICATION_ID  + File.separator + "jsons";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

}
