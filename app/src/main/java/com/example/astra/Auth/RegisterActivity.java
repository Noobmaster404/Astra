package com.example.astra.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.astra.Navigation.MainActivity;
import com.example.astra.R;
import com.example.astra.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        FirebaseAuth auth = FirebaseAuth.getInstance();//пока тестим


        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.emailEt.getText().toString().isEmpty() || binding.passwordEt.getText().toString().isEmpty() || binding.usernameEt.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Поля логина, пароля и имени пользователя не могут быть пустыми", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(binding.emailEt.getText().toString(),binding.passwordEt.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        HashMap<String, String> userInfo = new HashMap<>();
                                        userInfo.put("email", binding.emailEt.getText().toString());
                                        userInfo.put("username", binding.usernameEt.getText().toString());
                                        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .setValue(userInfo);
                                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                        finish();//пока не точно
                                    }
                                }
                            });

                }
            }
        });








        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Кнопка возвращает предыдущую страницу, а не создаёт новую
        ImageButton back = (ImageButton)findViewById(R.id.back_btn);//
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }
}