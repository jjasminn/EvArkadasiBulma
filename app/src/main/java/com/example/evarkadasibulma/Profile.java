package com.example.evarkadasibulma;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;


public class Profile extends AppCompatActivity {
    private ImageView profilePhoto;
    private ImageView whatsapp;
    private  ImageView mail;
    private TextView profileName;
    private TextView profileDepartment;
    private TextView profileClass;
    private TextView profileDistance;
    private TextView profileDuration;
    private TextView profileStatus;
    private TextView profileContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // XML öğelerini Java sınıfına bağla
        profilePhoto = findViewById(R.id.profile_photo);
        mail = findViewById(R.id.emailIcon);
        whatsapp = findViewById(R.id.whatsapp);
        profileName = findViewById(R.id.profile_name);
        profileDepartment = findViewById(R.id.profile_department);
        profileClass = findViewById(R.id.profile_class);
        profileDistance = findViewById(R.id.profile_distance);
        profileDuration = findViewById(R.id.profile_duration);
        profileStatus = findViewById(R.id.profile_status);
        profileContact = findViewById(R.id.profile_contact);

        // Intent'ten User nesnesini al
        User user = (User) getIntent().getSerializableExtra("user");

        // Kullanıcının profil bilgilerini göster
        profileName.setText(user.getName());
        profileDepartment.setText(user.getDepartment());
        profileClass.setText(user.getStudentClass());
        profileDistance.setText(user.getDistance());
        profileDuration.setText(user.getDuration());
        profileStatus.setText(user.getStatus());
        profileContact.setText(user.getContact());
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//        StorageReference storageRef = storage.getReference();
//        StorageReference photoRef = storageRef.child("users/" + user.getUid() + "/profilePhoto.jpg");

//        photoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri downloadUrl) {
//
//                Picasso.get().load(downloadUrl).into(profilePhoto);
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//
//                Toast.makeText(Profile.this,"Fotograf Yuklenemedi",Toast.LENGTH_SHORT).show();
//            }
//        });

        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean is = isWhatsappInstalled();
                System.out.println(is);
                String phoneNumber ="+90"+ user.getContact();

                String phoneNumber2 = "+905454122899"; // Mesaj göndermek istediğiniz telefon numarasını buraya girin

                // WhatsApp Intent'i oluşturma
                Intent whatsappIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phoneNumber));
                whatsappIntent.setPackage("com.whatsapp"); // Hedef uygulama olarak WhatsApp'ı belirleme

                // Mesajı eklemek isterseniz:
                whatsappIntent.putExtra(Intent.EXTRA_TEXT, "Merhaba! Mesajınız burada yer alacak.");

                // WhatsApp açılmıyorsa diğer mesajlaşma uygulamalarını da seçebilirsiniz
                Intent chooser = Intent.createChooser(whatsappIntent, "WhatsApp ile Mesaj Gönder");
                if (whatsappIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(chooser);
                } else {
                    Toast.makeText(Profile.this, "WhatsApp yüklü değil.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isGmailInstalled()) {
                    // Gmail yüklü ise e-posta gönderme işlemini gerçekleştir
                    sendEmail(user.getEmail());
                } else {
                    // Gmail yüklü değilse alternatif bir işlem yap
                    // Örneğin, başka bir e-posta istemcisi uygulamasını açabilirsiniz
                }


            }
        });


    }
    private void sendEmail(String emailAddress) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + emailAddress));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Ev Arkadasi Bulma");

        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
        boolean isIntentSafe = activities.size() > 0;

        if (isIntentSafe) {
            startActivity(Intent.createChooser(intent, "E-posta uygulamasını seçin"));
        } else {
            // Gmail uygulaması yoksa buraya alternatif bir işlem yapabilirsiniz
        }
    }

    private boolean isWhatsappInstalled(){
        PackageManager packageManager=getPackageManager();
        boolean whatsappInstalled;
        try{
            packageManager.getPackageInfo("com.whatsapp",PackageManager.GET_ACTIVITIES);
            whatsappInstalled=true;
        }
        catch (PackageManager.NameNotFoundException e){
            whatsappInstalled=false;

        }
        return whatsappInstalled;


    }
    private boolean isGmailInstalled() {
        PackageManager packageManager = getPackageManager();
        try {
            packageManager.getPackageInfo("com.google.android.gm", PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {

            return false;
        }
    }

}
