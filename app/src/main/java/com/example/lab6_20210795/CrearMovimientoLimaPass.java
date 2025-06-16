package com.example.lab6_20210795;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CrearMovimientoLimaPass extends AppCompatActivity {

    private EditText etIdMovimiento, etFecha, etParaderoEntrada, etParaderoSalida, etTiempoViaje;
    private Button btnGuardar;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.setStatusBarColor(Color.WHITE);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_crear_movimiento_lima_pass);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        etIdMovimiento = findViewById(R.id.etIdMovimiento);
        etFecha = findViewById(R.id.etFecha);
        etParaderoEntrada = findViewById(R.id.etParaderoEntrada);
        etParaderoSalida = findViewById(R.id.etParaderoSalida);
        etTiempoViaje = findViewById(R.id.etTiempoViaje);
        btnGuardar = findViewById(R.id.btnGuardar);

        // Hacer EditText idTarjeta no editable
        etIdMovimiento.setEnabled(false);
        etIdMovimiento.setFocusable(false);

        // Obtener idLimaPass del usuario y setearlo en etIdTarjeta
        String userId = auth.getCurrentUser().getUid();
        db.collection("Usuarios").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String idLimaPass = documentSnapshot.getString("idLimaPass");
                        if (idLimaPass != null) {
                            etIdMovimiento.setText(idLimaPass);
                        } else {
                            Toast.makeText(this, "ID de LimaPass no encontrado", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Documento de usuario no existe", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al obtener ID LimaPass: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });

        btnGuardar.setOnClickListener(v -> {
            String fecha = etFecha.getText().toString().trim();
            String paraderoEntrada = etParaderoEntrada.getText().toString().trim();
            String paraderoSalida = etParaderoSalida.getText().toString().trim();
            String tiempoViaje = etTiempoViaje.getText().toString().trim();

            if (fecha.isEmpty() || paraderoEntrada.isEmpty() || paraderoSalida.isEmpty() || tiempoViaje.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> movimiento = new HashMap<>();
            movimiento.put("fecha", fecha);
            movimiento.put("paraderoEntrada", paraderoEntrada);
            movimiento.put("paraderoSalida", paraderoSalida);
            movimiento.put("tiempoViaje", tiempoViaje);

            db.collection("Usuarios").document(userId)
                    .collection("MovimientosLimaPass")
                    .add(movimiento)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Movimiento guardado con éxito", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error al guardar movimiento: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });
    }
}
