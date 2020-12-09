package com.astend.android.photocutter.ui.finish;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.astend.android.photocutter.R;
import com.astend.android.photocutter.ui.crop.CropFragment;
import com.astend.android.photocutter.utils.ExtendedImageView;


public class FinishFragment extends Fragment {
  private final int GALLERY_REQUEST_CODE = 200;
  String photoPath;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    return inflater.inflate(R.layout.fragment_finish, container, false);

  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    photoPath = requireArguments().getString(CropFragment.PHOTO_PATH, "");
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    ImageView ivSend = view.findViewById(R.id.ivSend);
    ImageView ivGallery = view.findViewById(R.id.ivGallery);
    Button btnMenu = view.findViewById(R.id.btnMenu);
    ExtendedImageView extendedImageView = view.findViewById(R.id.ivFinish);
    extendedImageView.setImage(photoPath);

    ivSend.setOnClickListener(v -> {
      Intent shareIntent = new Intent();
      shareIntent.setAction(Intent.ACTION_SEND);
      shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(photoPath));
      shareIntent.setType("image/jpeg");
      startActivity(Intent.createChooser(shareIntent,getString(R.string.share_chose_app)));

    });

    ivGallery.setOnClickListener(v -> {
      Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
      intent.setType("image/*");
      this.startActivityForResult(
          Intent.createChooser(intent, getString(R.string.share_chose_app)),
          GALLERY_REQUEST_CODE
      );
    });


      btnMenu.setOnClickListener(v -> {
        Navigation.findNavController(view).navigate(R.id.action_finishFragment_to_splashFragment);
      });
  }
}