package com.astend.android.photocutter.ui.crop;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.astend.android.photocutter.BuildConfig;
import com.astend.android.photocutter.R;
import com.astend.android.photocutter.ui.crop.view.CropView;
import com.astend.android.photocutter.utils.ExtendedImageView;

public class CropFragment extends Fragment {

  public static final String PHOTO_PATH = "photoPath";
  private Bitmap bitmap;
  private String photoPath = "test";

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    return inflater.inflate(R.layout.fragment_crop, container, false);
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null){
      photoPath = getArguments().getString(CropFragment.PHOTO_PATH);
    }

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

      bitmap = cropView.cropBitmap(
          imageView.getBitmap(),
          imageView.getFilePath(),
          imageView.getImgSrcWidth(),
          imageView.getImgSrcHeight(),
          imageView.getInnerBitmapWidth(),
          imageView.getInnerBitmapHeight()
      );

      cropView.setVisibility(View.INVISIBLE);
      imageView.setImageBitmap(bitmap);
    });

    textCancel.setOnClickListener(v -> {
      btnCrop.setEnabled(true);
      textOk.setVisibility(View.INVISIBLE);
      textCancel.setVisibility(View.INVISIBLE);
      cropView.setVisibility(View.VISIBLE);

      if (BuildConfig.FLAVOR.equalsIgnoreCase("full")) {
        imageView.setImage(photoPath);
      }
      else
        imageView.setImage("test");

    });

    textOk.setOnClickListener(v -> {
      Bundle bundle = new Bundle();
      bundle.putString(PHOTO_PATH, cropView.getCroppedFile().getAbsolutePath());

//      MediaScannerConnection.scanFile(getContext(),
//          new String[] { cropView.getCroppedFile().getAbsolutePath() }, null,
//          (path, uri) -> {
//            Log.i("ExternalStorage", "Scanned " + path + ":");
//            Log.i("ExternalStorage", "-> uri=" + uri);
//          });

      Navigation.findNavController(view).navigate(
          R.id.action_cropFragment_to_finishFragment,
          bundle
      );

    });

    if (BuildConfig.FLAVOR.equalsIgnoreCase("full")) {
      String photoPath = getArguments().getString(CropFragment.PHOTO_PATH);
      imageView.setImage(photoPath);
    }
    else
      imageView.setImage("test");
  }
}