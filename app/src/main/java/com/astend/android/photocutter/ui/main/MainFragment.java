package com.astend.android.photocutter.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.astend.android.photocutter.App;
import com.astend.android.photocutter.R;
import com.astend.android.photocutter.ui.crop.CropFragment;
import com.astend.android.photocutter.utils.Utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;


public class MainFragment extends Fragment {
  private final int GALLERY_REQUEST_CODE = 200;
  public File file;
  private static String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    return inflater.inflate(R.layout.fragment_main, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    Button btnCamera = view.findViewById(R.id.btnCamera);
    Button btnGallary = view.findViewById(R.id.btnGallery);

    btnCamera.setOnClickListener(v -> {
      Navigation.findNavController(view).navigate(R.id.action_mainFragment_to_cameraFragment);
    });
    btnGallary.setOnClickListener(v -> {
      Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
      intent.setType("image/*");
      this.startActivityForResult(
          Intent.createChooser(intent, "Выберите приложение:"),
          GALLERY_REQUEST_CODE
      );
    });

  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
       file = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
           new SimpleDateFormat(FILENAME_FORMAT,
               Locale.US).format(System.currentTimeMillis()) + ".jpg");
      Utils.copyFileByUri(getContext(), data.getData(),file);
    }
    Bundle bundle = new Bundle();
    bundle.putString(CropFragment.PHOTO_PATH,file.toString());
    App.preferences.edit()
        .putString(CropFragment.PHOTO_PATH, file.toString())
        .apply();
    Navigation.findNavController(getView())
        .navigate(R.id.action_mainFragment_to_cropFragment, bundle);

    Log.d("TAG", "requestcode: " + requestCode + "resultCode :" + resultCode);
  }

}