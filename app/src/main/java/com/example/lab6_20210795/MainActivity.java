package com.example.lab6_20210795;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private Toolbar toolbar;
    private Button btnLimaPass, btnLinea1, btnResumen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnLimaPass = findViewById(R.id.btnLimaPass);
        btnLinea1 = findViewById(R.id.btnLinea1);
        btnResumen = findViewById(R.id.btnResumen);

        btnLimaPass.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, LimaPassActivity.class)));
        btnLinea1.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Linea1Activity.class)));
        btnResumen.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ResumenActivity.class)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu); // infla menú con logout
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            firebaseAuth.signOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
