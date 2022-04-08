package com.sena.jpegtest;

import android.graphics.Bitmap;

/**
 * FileName: JpegUtils
 * Author: JiaoCan
 * Date: 2022/4/8 11:12
 */

public class JpegUtils {
    static {
        System.loadLibrary("native-lib");
    }

    public static native boolean compressBitmap(Bitmap bitmap, int width, int height, String fileName, int quality);

}
