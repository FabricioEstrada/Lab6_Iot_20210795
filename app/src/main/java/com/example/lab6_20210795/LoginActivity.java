package com.example.lab6_20210795;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton, registerButton, googleLoginButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    // ActivityResultLauncher para FirebaseUI
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        Toast.makeText(this, "Login con Google exitoso: " + user.getEmail(), Toast.LENGTH_SHORT).show();
                        // Verificamos si el usuario existe en Firestore y si no, lo creamos
                        checkAndCreateUserInFirestore(user);
                    }
                } else {
                    Toast.makeText(this, "Login con Google cancelado o fallido", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.setStatusBarColor(Color.WHITE);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);

        // Botón extra para login con Google, agrégalo en tu layout con id googleLoginButton
        googleLoginButton = findViewById(R.id.googleLoginButton);

        loginButton.setOnClickListener(v -> loginUser());
        registerButton.setOnClickListener(v -> registerUser());
        googleLoginButton.setOnClickListener(v -> loginWithGoogle());
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor ingresa email y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                        goToMainActivity();
                    } else {
                        Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void registerUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor ingresa email y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            // Crear usuario en Firestore
                            createUserInFirestore(user);
                        }
                    } else {
                        Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void loginWithGoogle() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false) // Opcional, para que no intente guardar credenciales automáticamente
                .build();

        signInLauncher.launch(signInIntent);
    }

    private void checkAndCreateUserInFirestore(FirebaseUser user) {
        db.collection("Usuarios").document(user.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        // No existe, crear documento con IDs aleatorios
                        createUserInFirestore(user);
                    } else {
                        // Ya existe usuario, vamos directo a Main
                        goToMainActivity();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al verificar usuario en Firestore: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void createUserInFirestore(FirebaseUser user) {
        String idLimaPass = UUID.randomUUID().toString();
        String idLinea1 = UUID.randomUUID().toString();

        Map<String, Object> userData = new HashMap<>();
        userData.put("correo", user.getEmail());
        userData.put("idLimaPass", idLimaPass);
        userData.put("idLinea1", idLinea1);

        db.collection("Usuarios").document(user.getUid())
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Usuario creado en Firestore exitosamente", Toast.LENGTH_SHORT).show();
                    goToMainActivity();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al crear usuario en Firestore: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void goToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, LimaPassActivity.class);
        startActivity(intent);
        finish();
    }
}
