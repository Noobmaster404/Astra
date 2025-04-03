package com.example.astra.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.astra.R;
import com.example.astra.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

//Test
public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private boolean isProcessing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseAuth auth = FirebaseAuth.getInstance();

        binding.loginBtn.setOnClickListener(v -> {
            if (isProcessing) return;

            String email = binding.emailEt.getText().toString().trim();
            String password = binding.passwordEt.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Поля логина и пароля не могут быть пустыми", Toast.LENGTH_SHORT).show();
                return;
            }

            setProcessing(true);

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        setProcessing(false);

                        if (task.isSuccessful()) {
                            navigateToMain();
                        } else {
                            Toast.makeText(this, "Ошибка входа: " +
                                            Objects.requireNonNull(task.getException()).getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        binding.goToRegisterActivityTv.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

        // Устойчивая авторизация
        auth.addAuthStateListener(authListener -> {
            if (auth.getCurrentUser() != null && !isFinishing()) {
                navigateToMain();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void setProcessing(boolean processing) {
        isProcessing = processing;
        binding.loginBtn.setEnabled(!processing);
        binding.progressBar.setVisibility(processing ? View.INVISIBLE : View.GONE);//в этой строке я сделал progressBar невидимым
    }
}