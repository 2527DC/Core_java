package com.mlt.ets.rider.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.mlt.ets.rider.databinding.FragmentProfileBinding;
import java.io.File;
import java.io.IOException;
import java.util.Date;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private de.hdodenhof.circleimageview.CircleImageView imgProfile;
    private TextView txtUserName;
    private CardView cardEditName;
    private EditText editTextName;
    private Button btnSubmitName;

    private Uri imageUri;
    private String currentPhotoPath;

    private final ActivityResultLauncher<String> cameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    takePicture();
                } else {
                    Toast.makeText(getActivity(), "Camera permission is required to take pictures", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<String> storagePermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    pickImageFromGallery();
                } else {
                    Toast.makeText(getActivity(), "Storage permission is required to pick images", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == getActivity().RESULT_OK) {
                    imgProfile.setImageURI(imageUri);
                }
            });

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    Uri selectedImage = result.getData().getData();
                    if (selectedImage != null) {
                        imgProfile.setImageURI(selectedImage);
                    }
                }
            });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        imgProfile = binding.imgProfile;
        txtUserName = binding.userNameValue;
        cardEditName = binding.cardEditName;
        editTextName = binding.editTextName;
        btnSubmitName = binding.btnSubmitName;

        Button btnEditName = binding.btnEditName;
        btnEditName.setOnClickListener(v -> {
            cardEditName.setVisibility(View.VISIBLE);
            editTextName.setText(txtUserName.getText().toString());
        });

        btnSubmitName.setOnClickListener(v -> {
            String newName = editTextName.getText().toString();
            if (!newName.isEmpty()) {
                txtUserName.setText(newName);
                cardEditName.setVisibility(View.GONE);
                hideKeyboard();
                Toast.makeText(getActivity(), "Name updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnUploadImage.setOnClickListener(v -> showImageSourceDialog());

        return root;
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(editTextName.getWindowToken(), 0);
        }
    }

    private void showImageSourceDialog() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Image Source")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // Camera
                            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
                            } else {
                                takePicture();
                            }
                            break;
                        case 1: // Gallery

                                pickImageFromGallery();

                            break;
                        default:
                            break;
                    }
                });
        builder.show();
    }

    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            try {
                File photoFile = createImageFile();
                imageUri = FileProvider.getUriForFile(getActivity(),
                        getActivity().getPackageName() + ".fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                cameraLauncher.launch(takePictureIntent);
            } catch (IOException e) {
                Toast.makeText(getActivity(), "Error creating image file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}


//package com.mlt.ets.rider.fragments;
//
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.net.Uri;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.appcompat.app.AlertDialog;
//import androidx.fragment.app.Fragment;
//import androidx.lifecycle.ViewModelProvider;
//
//import com.mlt.ets.rider.databinding.FragmentProfileBinding;
//
//import java.io.File;
//import java.io.IOException;
//
//public class ProfileFragment extends Fragment {
//
//    private FragmentProfileBinding binding;
//    private ImageView imgProfile;
//    private TextView txtUserName;
//
//    // Variable to hold the image URI
//    private Uri imageUri;
//
//    // ActivityResultLauncher for camera capture
//    private final ActivityResultLauncher<Intent> cameraLauncher =
//            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
//                if (result.getResultCode() == getActivity().RESULT_OK) {
//                    // Use the image captured from the camera
//                    imgProfile.setImageURI(imageUri); // Set the captured image to the ImageView
//                }
//            });
//
//    // ActivityResultLauncher for picking images from gallery
//    private final ActivityResultLauncher<Intent> imagePickerLauncher =
//            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
//                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
//                    Uri selectedImage = result.getData().getData();
//                    if (selectedImage != null) {
//                        imgProfile.setImageURI(selectedImage); // Set the selected image to the ImageView
//                    }
//                }
//            });
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             ViewGroup container, Bundle savedInstanceState) {
//        ProfileViewModel profileViewModel =
//                new ViewModelProvider(this).get(ProfileViewModel.class);
//
//        binding = FragmentProfileBinding.inflate(inflater, container, false);
//        View root = binding.getRoot();
//
//        imgProfile = binding.imgProfile;
//        txtUserName = binding.userNameValue;
//
//        // Set an OnClickListener on the upload button
//        Button btnUploadImage = binding.btnUploadImage;
//        btnUploadImage.setOnClickListener(v -> showImageSourceDialog());
//
//        // Optionally, set the user name TextView
//        txtUserName.setText("John Doe");
//
//        return root;
//    }
//
//    // Method to show dialog for selecting image source
//    private void showImageSourceDialog() {
//        String[] options = {"Camera", "Gallery"};
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle("Select Image Source")
//                .setItems(options, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        if (which == 0) {
//                            // Camera option selected
//                            takePicture();
//                        } else if (which == 1) {
//                            // Gallery option selected
//                            Toast.makeText(getActivity(), "unable provide permission", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//        builder.show();
//    }
//
//    // Method to capture image using camera
//    private void takePicture() {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        // Check if the camera app is available
//        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
//            try {
//                // Create a file to save the image
//                File photoFile = createImageFile();
//                imageUri = Uri.fromFile(photoFile); // Get the URI for the file
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); // Set the output file URI
//                cameraLauncher.launch(takePictureIntent); // Launch the camera intent
//            } catch (IOException e) {
//                Toast.makeText(getActivity(), "Error creating image file", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    // Method to create an image file
//    private File createImageFile() throws IOException {
//        // Create an image file name
//        String imageFileName = "profile_image_";
//        File storageDir = getActivity().getExternalFilesDir(null); // Use app-specific storage
//        return File.createTempFile(
//                imageFileName,  /* prefix */
//                ".jpg",         /* suffix */
//                storageDir      /* directory */
//        );
//    }
//
//    // Method to pick an image from gallery
//    private void pickImageFromGallery() {
//        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        imagePickerLauncher.launch(intent);
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        binding = null;
//    }
//}