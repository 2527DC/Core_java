package com.mlt.ets.rider.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
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

import com.mlt.ets.rider.MainActivity;
import com.mlt.ets.rider.databinding.FragmentProfileBinding;
import com.mlt.ets.rider.Helper.UrlManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Locale;

public class ProfileFragment extends Fragment {
    private UrlManager urlManager;
    private FragmentProfileBinding binding;
    private de.hdodenhof.circleimageview.CircleImageView imgProfile;
    private TextView txtUserName, phoneValue, emailValue, genderValue, addressValue;
    private CardView cardEditName;
    private EditText editTextName;
    private Button btnSubmitName;
    private Uri imageUri;
    private String currentPhotoPath;

    // Permission launcher for camera
    private final ActivityResultLauncher<String> cameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    takePicture();
                } else {
                    Toast.makeText(getActivity(), "Camera permission is required to take pictures", Toast.LENGTH_SHORT).show();
                }
            });

    // Camera activity launcher
    private final ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == getActivity().RESULT_OK) {
                    imgProfile.setImageURI(imageUri);
                    saveImageUri(imageUri); // Save URI persistently
                    Toast.makeText(getActivity(), "Image saved in custom directory", Toast.LENGTH_SHORT).show();
                }
            });

    // Gallery image picker launcher
    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    Uri selectedImage = result.getData().getData();
                    if (selectedImage != null) {
                        try {
                            Uri savedImageUri = saveImageInAppDirectory(selectedImage);
                            updateProfileImage(savedImageUri);
                            Toast.makeText(getActivity(), "Image saved in custom directory", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            Toast.makeText(getActivity(), "Failed to save image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        urlManager = new UrlManager(getContext());

        imgProfile = binding.imgProfile;
        txtUserName = binding.userNameValue;
        cardEditName = binding.cardEditName;
        editTextName = binding.editTextName;
        btnSubmitName = binding.btnSubmitName;
        phoneValue = binding.phoneValue;
        emailValue = binding.emailValue;
        genderValue = binding.genderValue;
        addressValue = binding.addressValue;


        loadSavedProfileImage();
        txtUserName.setText(urlManager.getUserName());
        phoneValue.setText(urlManager.getPhoneCode() + " " + urlManager.getPhone());
        emailValue.setText(urlManager.getUserEmail());
        genderValue.setText(urlManager.getGender());
        addressValue.setText(urlManager.getEmployeAddress());

        // Edit name button and submission logic
        Button btnEditName = binding.btnEditName;
        btnEditName.setOnClickListener(v -> toggleEditNameView());
        btnSubmitName.setOnClickListener(v -> submitNameChange());

        // Set up image upload button
        binding.btnUploadImage.setOnClickListener(v -> showImageSourceDialog());

        return root;
    }

    private void toggleEditNameView() {
        if (cardEditName.getVisibility() == View.VISIBLE) {
            cardEditName.setVisibility(View.GONE); // Hide if visible
        } else {
            cardEditName.setVisibility(View.VISIBLE); // Show if hidden
            editTextName.setText(txtUserName.getText().toString()); // Set current name
        }
    }

    private void submitNameChange() {
        String newName = editTextName.getText().toString();
        if (!newName.isEmpty()) {
            txtUserName.setText(newName);
            cardEditName.setVisibility(View.GONE);
            hideKeyboard();
            Toast.makeText(getActivity(), "Name updated", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
        }
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
                    if (which == 0) { // Camera
                        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
                        } else {
                            takePicture();
                        }
                    } else { // Gallery
                        pickImageFromGallery();
                    }
                });
        builder.show();
    }

    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            try {
                File photoFile = createImageFile();
                imageUri = FileProvider.getUriForFile(
                        getActivity(),
                        getActivity().getPackageName() + ".fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                cameraLauncher.launch(takePictureIntent);
            } catch (IOException e) {
                Toast.makeText(getActivity(), "Error creating image file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyAppImages");
        if (!storageDir.exists() && !storageDir.mkdirs()) {
            Log.d("ProfileFragment", "Failed to create directory: " + storageDir.getAbsolutePath());
        }
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private Uri saveImageInAppDirectory(Uri sourceUri) throws IOException {
        File storageDir = getActivity().getFilesDir();
        File imageFile = new File(storageDir, "selected_image.jpg");
        try (InputStream inputStream = getActivity().getContentResolver().openInputStream(sourceUri);
             FileOutputStream outputStream = new FileOutputStream(imageFile)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
        return Uri.fromFile(imageFile);
    }

    private void loadSavedProfileImage() {

        String savedImageUriString =urlManager.getProfilePic();
        if (savedImageUriString != null) {
            Uri savedImageUri = Uri.parse(savedImageUriString);
            imgProfile.setImageURI(savedImageUri);
        }
    }

    private void saveImageUri(Uri uri) {
        urlManager.StoreProPic( uri.toString());
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).updateNavigationDrawerImage(uri);
        }
    }

    private void updateProfileImage(Uri uri) {
        imgProfile.setImageURI(uri);
        saveImageUri(uri); // Save URI persistently
    }
}
