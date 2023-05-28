package com.example.evarkadasibulma;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.InputStream;

public class ProfileSettings extends AppCompatActivity {
    FirebaseUser currentUser;
    User user;
    FirebaseAuth auth;
    FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageRef;
    Uri resultUri;
    boolean isPhotoUploaded;
    private ImageView profileImageView;
    ProgressBar progressBar;
    private EditText profileName, profileEmail;
    private EditText departmentEditText, classEditText, distanceEditText, durationEditText, contactEditText;
    private Spinner statusSpinner;
    private Button saveButton;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                try{
                    isPhotoUploaded=true;
                    InputStream stream= getContentResolver().openInputStream(resultUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(stream);
                    profileImageView.setImageBitmap(bitmap);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);
        auth=FirebaseAuth.getInstance();
        currentUser=auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        Intent intent = getIntent();
        user = intent.getParcelableExtra("user");
        //bir tek loginden gelirken user null olabilir

        if (user != null) {
            getDbUser(false);
        }
        else{

            getDbUser(true);

        }





        progressBar=findViewById(R.id.progressBar);
        profileName=findViewById(R.id.profileName);
        profileEmail=findViewById(R.id.profileEmail);
        profileImageView = findViewById(R.id.profileImageView);
        departmentEditText = findViewById(R.id.departmentEditText);
        classEditText = findViewById(R.id.classEditText);
        distanceEditText = findViewById(R.id.distanceEditText);
        durationEditText = findViewById(R.id.durationEditText);
        statusSpinner = findViewById(R.id.statusSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.status_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);
        contactEditText = findViewById(R.id.contactEditText);
        saveButton = findViewById(R.id.saveButton);
        profileEmail.setText(currentUser.getEmail());

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                        requestStoragePermission();

                    } else if(!checkStoragePermission()){
                        requestStoragePermission();
                    }
                    else {
                        CropImage.activity().start(ProfileSettings.this);//fotografi alma

                    }


            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });

    }
    private void getDbUser(boolean isfromLogin){
        try {
            db.collection("User").document(currentUser.getUid()).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                user = documentSnapshot.toObject(User.class);
                                if (user != null ) {
                                    // Set the retrieved user information to the corresponding EditTexts
                                    if(user.isUserUpdated()&& isfromLogin){
                                        Intent intent = new Intent(getApplicationContext(), Profile.class);
                                        intent.putExtra("user", user);
                                        startActivity(intent);


                                    }
                                    profileName.setText(user.getName());
                                    departmentEditText.setText(user.getDepartment());
                                    classEditText.setText(user.getStudentClass());
                                    distanceEditText.setText(user.getDistance());
                                    durationEditText.setText(user.getDuration());
                                    contactEditText.setText(user.getContact());

                                    // Set the status Spinner selection
                                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ProfileSettings.this, R.array.status_array, android.R.layout.simple_spinner_item);
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    statusSpinner.setAdapter(adapter);
                                    if (user.getStatus() != null) {
                                        int spinnerPosition = adapter.getPosition(user.getStatus());
                                        statusSpinner.setSelection(spinnerPosition);
                                    }
                                }
                                else{
                                    user = new User(currentUser.getEmail(),currentUser.getUid());
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileSettings.this, "Failed to retrieve user information", Toast.LENGTH_SHORT).show();
                        }
                    });

        }
        catch (Exception e ){
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();

        }

    }

    private void saveProfile() {
        progressBar.setVisibility(View.VISIBLE);
        String name=profileName.getText().toString().trim();

        String department = departmentEditText.getText().toString().trim();
        String studentClass = classEditText.getText().toString().trim();
        String distance = distanceEditText.getText().toString().trim();
        String duration = durationEditText.getText().toString().trim();
        String status = statusSpinner.getSelectedItem().toString();
        String contact = contactEditText.getText().toString().trim();
        if (TextUtils.isEmpty(department) || TextUtils.isEmpty(studentClass) || TextUtils.isEmpty(distance)
                || TextUtils.isEmpty(duration) || TextUtils.isEmpty(status)
                || TextUtils.isEmpty(contact) ) {
            Toast.makeText(getApplicationContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show();
        }
        else{
//            StorageReference photoRef = storageRef.child("users/" + currentUser.getUid() + "/profilePhoto.jpg");
//            photoRef.putFile(resultUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//                @Override
//                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                    if (!task.isSuccessful()) {
//                        throw task.getException();
//                    }
//
//
//                    return photoRef.getDownloadUrl();
//                }
//            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                @Override
//                public void onComplete(@NonNull Task<Uri> task) {
//                    if (task.isSuccessful()) {
//                        user.UpdateUser(name,department,studentClass,distance,duration,status,contact,task.getResult().toString());

            user.UpdateUser(name,department,studentClass,distance,duration,status,contact,null);



            db.collection("User").document(currentUser.getUid()).set(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        progressBar.setVisibility(View.VISIBLE);

                                    Intent intent = new Intent(getApplicationContext(), Profile.class);
                                    intent.putExtra("user", user);
                                    startActivity(intent);
                                    finish();




                                        Toast.makeText(ProfileSettings.this, "Profil kaydedildi", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        Toast.makeText(ProfileSettings.this, "Profil güncellenirken bir hata oluştu", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
//        else {
//                        Toast.makeText(ProfileSettings.this, "task is unsuccesfull", Toast.LENGTH_SHORT).show();
//
//
//
//                    }
                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Toast.makeText(ProfileSettings.this, "photo yuklenirken hta", Toast.LENGTH_SHORT).show();
//
//                }
//            });
//
//        }
//
//    }

    private void requestCameraPermission() {

        requestPermissions(new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);

        System.out.println("adi");
    }

    private boolean checkStoragePermission() {
        boolean res2 = ContextCompat.checkSelfPermission(getApplicationContext(),android.Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED;
        return res2;
    }
    private boolean checkCameraPermission() {
        boolean res1 = ContextCompat.checkSelfPermission(getApplicationContext(),android.Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED;
        boolean res2 = ContextCompat.checkSelfPermission(getApplicationContext(),android.Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED;
        return res1&&res2;
    }
    private void requestStoragePermission() {
        requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},100);

        checkStoragePermission();
    }

}
