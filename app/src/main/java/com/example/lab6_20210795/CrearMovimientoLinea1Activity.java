package com.example.lab6_20210795;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CrearMovimientoLinea1Activity extends AppCompatActivity {

    private EditText etIdTarjeta, etFecha, etEstacionEntrada, etEstacionSalida, etTiempoViaje;
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
        setContentView(R.layout.activity_crear_movimiento_linea1);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        etIdTarjeta = findViewById(R.id.etIdTarjeta);
        etFecha = findViewById(R.id.etFecha);
        etEstacionEntrada = findViewById(R.id.etEstacionEntrada);
        etEstacionSalida = findViewById(R.id.etEstacionSalida);
        etTiempoViaje = findViewById(R.id.etTiempoViaje);
        btnGuardar = findViewById(R.id.btnGuardar);

        // Hacer el EditText no editable
        etIdTarjeta.setEnabled(false);
        etIdTarjeta.setFocusable(false);

        // Obtener idLinea1 del usuario y setearlo en etIdTarjeta
        String userId = auth.getCurrentUser().getUid();
        db.collection("Usuarios").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String idLinea1 = documentSnapshot.getString("idLinea1");
                        if (idLinea1 != null) {
                            etIdTarjeta.setText(idLinea1);
                        } else {
                            Toast.makeText(this, "ID de tarjeta no encontrado", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Documento de usuario no existe", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al obtener ID de tarjeta: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });

        btnGuardar.setOnClickListener(v -> {
            String fecha = etFecha.getText().toString().trim();
            String estacionEntrada = etEstacionEntrada.getText().toString().trim();
            String estacionSalida = etEstacionSalida.getText().toString().trim();
            String tiempoViaje = etTiempoViaje.getText().toString().trim();

            if (fecha.isEmpty() || estacionEntrada.isEmpty() || estacionSalida.isEmpty() || tiempoViaje.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> movimiento = new HashMap<>();
            movimiento.put("fecha", fecha);
            movimiento.put("estacionEntrada", estacionEntrada);
            movimiento.put("estacionSalida", estacionSalida);
            movimiento.put("tiempoViaje", tiempoViaje);

            db.collection("Usuarios").document(userId)
                    .collection("MovimientosLinea1")
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