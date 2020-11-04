package com.astend.android.photocutter.ui.splash;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astend.android.photocutter.App;
import com.astend.android.photocutter.BuildConfig;
import com.astend.android.photocutter.R;
import com.astend.android.photocutter.ui.crop.CropFragment;


public class SplashFragment extends Fragment {

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    return inflater.inflate(R.layout.fragment_splash, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    //todo  избавиться от postDelayed
    view.postDelayed(() -> {

      if (BuildConfig.FLAVOR.equalsIgnoreCase("demo")
          && App.preferences.getString(CropFragment.PHOTO_PATH, null) != null)
        Navigation.findNavController(view).navigate(R.id.action_splashFragment_to_cropFragment);
      else
        Navigation.findNavController(view).navigate(R.id.action_splashFragment_to_mainFragment2);

    }, 2000);
  }
}