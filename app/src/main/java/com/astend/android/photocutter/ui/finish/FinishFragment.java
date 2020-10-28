package com.astend.android.photocutter.ui.finish;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.astend.android.photocutter.R;


public class FinishFragment extends Fragment {
    private final int GALLERY_REQUEST_CODE = 200;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_finish, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView ivSend = view.findViewById(R.id.ivSend);
        ImageView ivGallery = view.findViewById(R.id.ivGallery);
        Button btnMenu = view.findViewById(R.id.btnMenu);

        ivGallery.setImageResource(R.drawable.gallery);
        ivSend.setImageResource(R.drawable.pngwing);

        ivGallery.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            this.startActivityForResult(
                    Intent.createChooser(intent, "Выберите приложение:" ),
                    GALLERY_REQUEST_CODE
            );
        });

        ivSend.setOnClickListener(v -> {

        });
    }
}