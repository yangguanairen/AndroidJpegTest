package com.sena.jpegtest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.sena.jpegtest.databinding.ActivityMainBinding;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private String originalFilePath;
    private String compressFilePath;

    private ActivityResultLauncher<String> getPermission;
    private ActivityResultLauncher<Uri> takePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initData();

        initView();

    }

    private void initData() {
        originalFilePath = getExternalCacheDir() + File.separator + "originalImage.jpg";
        compressFilePath = getExternalCacheDir() + File.separator + "compressImage.jpg";

        getPermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
           if (isGranted) {
               takePictureLow();
           } else {
               Toast.makeText(this, "需要相机权限", Toast.LENGTH_SHORT).show();
           }
        });

        takePicture = registerForActivityResult(new ActivityResultContracts.TakePicture(), isSave -> {
            if (!isSave) {
                Toast.makeText(this, "拍照失败！！", Toast.LENGTH_SHORT).show();
                return ;
            }

            File file = new File(originalFilePath);
            Bitmap bitmap = BitmapFactory.decodeFile(originalFilePath);
            if (bitmap == null) {
                return ;
            }
            bitmap = ImageUtils.checkRotate(file, bitmap);

            boolean isSuccessful = JpegUtils.compressBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), compressFilePath, 20);
            showCompressResult(isSuccessful);
        });


    }

    private void initView() {
        binding.take.setOnClickListener(view -> takePicture());
    }

    private void takePicture() {
        boolean isGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        if (isGranted) {
            takePictureLow();
        } else {
            getPermission.launch(Manifest.permission.CAMERA);
        }
    }

    private void takePictureLow() {
        File file = new File(originalFilePath);
        String authority = getPackageName() + ".provider";
        Uri uri = FileProvider.getUriForFile(this, authority, file);
        if (uri == null) {
            Toast.makeText(this, "Uri为空!!", Toast.LENGTH_SHORT).show();
            return ;
        }
        takePicture.launch(uri);
    }

    private void showCompressResult(boolean isSuccess) {

        File originalFile = new File(originalFilePath);
        File compressFile = new File(compressFilePath);

        long originalSize = originalFile.length() / 1024;
        long compressSize = compressFile.length() / 1024;

        StringBuilder builder = new StringBuilder().append("原始图片：").append(originalFilePath)
                .append("  原始大小：" ).append(originalSize).append("Kb").append("\n")
                .append("压缩图片：").append(compressFilePath)
                .append("  压缩大小：").append(compressSize).append("Kb");

        binding.info.setText(builder.toString());

        Toast.makeText(this, isSuccess ? "压缩成功" : "压缩失败", Toast.LENGTH_SHORT).show();
    }


}