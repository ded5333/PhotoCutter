package com.astend.android.photocutter.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.astend.android.photocutter.R;


public class MainFragment extends Fragment {
    private final int GALLERY_REQUEST_CODE = 200;
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
                    Intent.createChooser(intent, "Выберите приложение:" ),
                    GALLERY_REQUEST_CODE
            );
        });

    }

}