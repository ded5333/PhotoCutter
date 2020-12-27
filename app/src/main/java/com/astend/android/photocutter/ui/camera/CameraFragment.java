package com.astend.android.photocutter.ui.camera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.astend.android.photocutter.App;
import com.astend.android.photocutter.R;
import com.astend.android.photocutter.ui.crop.CropFragment;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class CameraFragment extends Fragment {

  private static String TAG = "CameraXBasic";
  private static String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";
  private static int REQUEST_CODE_PERMISSIONS = 10;
  private String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};
  private ImageCapture imageCapture = null;

  private File outputDirectory;
  private ExecutorService cameraExecutor;

  private PreviewView viewFinder;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    outputDirectory = requireContext().getCacheDir();

    cameraExecutor = Executors.newSingleThreadExecutor();

  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    return inflater.inflate(R.layout.fragment_camera, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    Button btnPhoto = view.findViewById(R.id.camera_capture_button);
    viewFinder = view.findViewById(R.id.viewFinder);

    if (allPermissionsGranted())
      startCamera();
    else
      requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
    btnPhoto.setOnClickListener(v -> {
      takePhoto();
    });
  }

  private void takePhoto() {
    // Create time-stamped output file to hold the image
    File photoFile = new File(
        outputDirectory,
        new SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis()) + ".jpg");

    // Create output options object which contains file + metadata
    ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();

    // Set up image capture listener, which is triggered after photo has
    // been taken
    imageCapture.takePicture(
        outputOptions, ContextCompat.getMainExecutor(requireContext()),
        new ImageCapture.OnImageSavedCallback() {
          @Override
          public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {

            String msg = "Photo capture succeeded: " + photoFile.toString();
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
            Log.d(TAG, msg);

            Bundle bundle = new Bundle();
            bundle.putString(CropFragment.PHOTO_PATH, photoFile.toString());

            App.preferences.edit()
                .putString(CropFragment.PHOTO_PATH, photoFile.toString())
                .apply();

            Navigation.findNavController(CameraFragment.this.getView())
                .navigate(R.id.action_cameraFragment_to_cropFragment, bundle);

          }

          @Override
          public void onError(@NonNull ImageCaptureException exception) {
            Log.e(TAG, "Photo capture failed: " + exception.getMessage(), exception);
          }
        });
  }


  private void startCamera() {
    ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());

    cameraProviderFuture.addListener(() -> {
      // Preview
      Preview preview = new Preview.Builder().build();

      preview.setSurfaceProvider(viewFinder.getSurfaceProvider());
      imageCapture = new ImageCapture.Builder().build();

      // Select back camera as a default
      CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

      try {
        // Used to bind the lifecycle of cameras to the lifecycle owner
        ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
        // Unbind use cases before rebinding
        cameraProvider.unbindAll();

        // Bind use cases to camera
        cameraProvider.bindToLifecycle(
            getViewLifecycleOwner(), cameraSelector, preview, imageCapture);

      } catch (Exception exc) {
        Log.e(TAG, "Use case binding failed", exc);
      }

    }, ContextCompat.getMainExecutor(requireContext()));
  }

  private boolean allPermissionsGranted() {
    return ContextCompat.checkSelfPermission(
        requireContext(), REQUIRED_PERMISSIONS[0]) == PackageManager.PERMISSION_GRANTED;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    cameraExecutor.shutdown();
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    if (requestCode == REQUEST_CODE_PERMISSIONS) {
      if (allPermissionsGranted()) {
        startCamera();
      }
      else {

        Toast.makeText(requireContext(),
            "Permissions not granted by the user.",
            Toast.LENGTH_SHORT).show();
        if (getView() != null)
          Navigation.findNavController(getView()).popBackStack();

      }
    }
  }
}