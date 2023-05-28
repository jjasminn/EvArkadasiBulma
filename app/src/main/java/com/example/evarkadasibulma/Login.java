package com.example.evarkadasibulma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseFirestore db;
    FirebaseUser currentUser;

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView forgetPasswordTextView;
    private TextView goSignInTextView;
    private ProgressBar progressBar;
    public void onStart() {
        super.onStart();

        currentUser = auth.getCurrentUser();
        if (currentUser != null && currentUser.isEmailVerified()) {

            Toast.makeText(getApplicationContext(),"Zaten giris yapilmisolacak "+currentUser.getEmail(), Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db = FirebaseFirestore.getInstance();

        auth = FirebaseAuth.getInstance();

        // XML dosyasındaki bileşenlere erişim sağlama
        progressBar = findViewById(R.id.progressBar);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        forgetPasswordTextView = findViewById(R.id.forget_password);
        goSignInTextView = findViewById(R.id.goRegister);

        // Giriş butonuna tıklama işlevi
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String mail, password;
                mail = String.valueOf(emailEditText.getText());
                password = String.valueOf(passwordEditText.getText());
                if (TextUtils.isEmpty(mail) || TextUtils.isEmpty(password)) {
                    Toast.makeText(Login.this, "Mail veya sifre bos!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    auth.signInWithEmailAndPassword(mail, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = auth.getCurrentUser();
                                        if (isYildizMail(mail)||user.isEmailVerified()) {
                                            Toast.makeText(Login.this, "Giris Yapildi", Toast.LENGTH_SHORT).show();

                                            Intent intent = new Intent(Login.this, ProfileSettings.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(Login.this, "Mailinizin dogrulamasi yapilmadigi icin giris yapamamaktasiniz. Lutfen mail hesabinizi kontrol ediniz", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(Login.this, "Giris Gerceklesemedi.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                progressBar.setVisibility(View.GONE);
            }
        });

        // "Forget password" metnine tıklama işlevi
        forgetPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, ForgetPassword.class);
                startActivity(intent);
                finish();
            }
        });

        // "Click To Sign In" metnine tıklama işlevi
        goSignInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private boolean isYildizMail(String email) {
        String emailSuffix = "@std.yildiz.edu.tr";
        if(email.endsWith(emailSuffix)){
            Toast.makeText(getApplicationContext(),"Yildiz mailiyle giris yapildi, verification maili gonderilemeyecek",Toast.LENGTH_SHORT).show();
            return true;
        }else{

            return  false;
        }
    }
}
