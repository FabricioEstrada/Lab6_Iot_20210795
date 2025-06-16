package com.example.lab6_20210795;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public abstract class BaseActivity extends AppCompatActivity {

    protected Button btnLimaPass, btnLinea1, btnResumen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        // Habilita EdgeToEdge para manejar las ventanas de sistema
        EdgeToEdge.enable(this);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Ajusta el padding superior de la Toolbar para respetar la barra de estado
        ViewCompat.setOnApplyWindowInsetsListener(toolbar, (v, insets) -> {
            Insets statusBarsInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            v.setPadding(
                    v.getPaddingLeft(),
                    statusBarsInsets.top,
                    v.getPaddingRight(),
                    v.getPaddingBottom()
            );
            return insets;
        });

        btnLimaPass = findViewById(R.id.btnLimaPass);
        btnLinea1 = findViewById(R.id.btnLinea1);
        btnResumen = findViewById(R.id.btnResumen);

        btnLimaPass.setOnClickListener(v -> {
            if (!(this instanceof LimaPassActivity)) {
                startActivity(new Intent(this, LimaPassActivity.class));
                finish();
            }
        });

        btnLinea1.setOnClickListener(v -> {
            if (!(this instanceof Linea1Activity)) {
                startActivity(new Intent(this, Linea1Activity.class));
                finish();
            }
        });

        btnResumen.setOnClickListener(v -> {
            Intent intent = new Intent(BaseActivity.this, ResumenActivity.class);
            startActivity(intent);
            finish();
        });
    }

    // Método para que cada Activity hija ponga su layout en el FrameLayout content_frame
    protected void setContentLayout(int layoutResID) {
        FrameLayout contentFrame = findViewById(R.id.content_frame);
        getLayoutInflater().inflate(layoutResID, contentFrame, true);
    }
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            // Cierra sesión y vuelve a LoginActivity (cámbialo por tu activity de login real)
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            Toast.makeText(this, "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
