package com.astend.android.photocutter.ui.crop;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.astend.android.photocutter.App;
import com.astend.android.photocutter.BuildConfig;
import com.astend.android.photocutter.ExtendedImageView;
import com.astend.android.photocutter.R;
import com.astend.android.photocutter.Utils;
import com.astend.android.photocutter.ui.camera.CameraFragment;
import com.astend.android.photocutter.ui.crop.view.CropView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;

import static android.app.Activity.RESULT_OK;


public class CropFragment extends Fragment {


  public static final String PHOTO_PATH = "photoPath";

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    return inflater.inflate(R.layout.fragment_crop, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    TextView textOk = view.findViewById(R.id.ok);
    TextView textCancel = view.findViewById(R.id.cancel);
    Button btnCrop = view.findViewById(R.id.btnCrop);
    CropView cropView = view.findViewById(R.id.imagePhoto);
    ExtendedImageView imageView = view.findViewById(R.id.imageView);


    btnCrop.setOnClickListener(v -> {
      btnCrop.setEnabled(false);
      textOk.setVisibility(View.VISIBLE);
      textCancel.setVisibility(View.VISIBLE);
      cropView.cropBitmap();
    });

    textCancel.setOnClickListener(v -> {
      btnCrop.setEnabled(true);
      textOk.setVisibility(View.INVISIBLE);
      textCancel.setVisibility(View.INVISIBLE);

    });


    textOk.setOnClickListener(v -> {
      Navigation.findNavController(view).navigate(R.id.action_cropFragment_to_finishFragment);
    });
    String photoPath;

    if (BuildConfig.FLAVOR.equalsIgnoreCase("full")) {
      photoPath = getArguments().getString(CropFragment.PHOTO_PATH);
//        myBitmap = BitmapFactory.decodeFile(photoPath);
    }
    else {
      photoPath = App.preferences.getString(PHOTO_PATH, null);
//       myBitmap = BitmapFactory.decodeFile(photoPath);
    }

    //   imageView.setImageBitmap(myBitmap);
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeFile(photoPath, options);
    int imgSrcHeight = options.outHeight;
    int imgSrcWidth = options.outWidth;
    //todo избавиться от Post;
    imageView.post(() -> {
      Log.d("TAG", "Source: " + imgSrcWidth + " " + imgSrcHeight);
      Log.d("TAG", "View: " + imageView.getWidth() + " " + imageView.getHeight());
      options.inSampleSize = Utils.calculateInSampleSize(options, imageView.getWidth(), imageView.getHeight());
      options.inJustDecodeBounds = false;
      Bitmap myBitmap = BitmapFactory.decodeFile(photoPath, options);
      Log.d("TAG", "View: " + myBitmap.getWidth() + " " + myBitmap.getHeight());
      imageView.setImageBitmap(myBitmap);
//      Drawable drawable = imageView.getDrawable();
//      Rect rect = drawable.getBounds();
//      Log.d("TAG", "Rect: " + rect.right + " " + rect.bottom);

    });

  }


}