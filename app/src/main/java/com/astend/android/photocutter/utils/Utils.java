package com.astend.android.photocutter.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Utils {
  public static int calculateInSampleSize(
      BitmapFactory.Options options, int reqWidth, int reqHeight) {
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {

      final int halfHeight = height / 2;
      final int halfWidth = width / 2;

      // Calculate the largest inSampleSize value that is a power of 2 and keeps both
      // height and width larger than the requested height and width.
      while ((halfHeight / inSampleSize) >= reqHeight
          && (halfWidth / inSampleSize) >= reqWidth) {
        inSampleSize *= 2;
      }
    }

    return inSampleSize;
  }

  public static void copyFileFromAssets(Context context, String assetFilePath, File destFile) {
    InputStream inputStream = null;
    OutputStream outputStream = null;

    try {
      if (!destFile.exists()) {
        inputStream = context.getAssets().open(assetFilePath);
        outputStream = new FileOutputStream(destFile);
        copyFile(inputStream, outputStream);
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    } finally {
      try {
        inputStream.close();
        outputStream.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public static void copyFileByUri(Context context, Uri uriFile, File destFile) {
    InputStream inputStream = null;
    OutputStream outputStream = null;

    try {
      if (!destFile.exists()) {

        inputStream = context.getContentResolver().openInputStream(uriFile);
        outputStream = new FileOutputStream(destFile);
        copyFile(inputStream, outputStream);
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    } finally {
      try {
        inputStream.close();
        outputStream.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private static void copyFile(InputStream input, OutputStream output) throws IOException {
    byte[] buffer = new byte[1024];
    int read = input.read(buffer);
    while (read != -1) {
      output.write(buffer, 0, read);
      read = input.read(buffer);
    }
    output.flush();
  }
  public static void addImageToGallery(Context context,File file){
    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
    //Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider" , file);
//    File file = new File()
    Uri uri = Uri.fromFile(file);
    intent.setData(uri);
    context.sendBroadcast(intent);
  }


}
