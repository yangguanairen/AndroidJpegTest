package com.sena.jpegtest;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.File;

/**
 * FileName: ImageUtils
 * Author: JiaoCan
 * Date: 2022/4/8 13:32
 */

public class ImageUtils {

    public static Bitmap checkRotate(File file, Bitmap bitmap) {
        Bitmap finalBitmap = bitmap;
        int degree = calculateDegree(file);
        if (degree != 0) {
            finalBitmap = rotateBitmap(degree, bitmap);
        }
        return finalBitmap;
    }

    public static int calculateDegree(File file) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(file);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static Bitmap rotateBitmap(int angle, Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return rotatedBitmap;
    }

}
