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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditarMovimientoLimaPass extends AppCompatActivity {

    private EditText etFecha, etParaderoEntrada, etParaderoSalida, etTiempoViaje, etIdMovimiento;
    private Button btnGuardar;

    private FirebaseFirestore db;
    private String uid;
    private String idMovimiento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.setStatusBarColor(Color.WHITE);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_editar_movimiento_lima_pass);

        etFecha = findViewById(R.id.etFecha);
        etParaderoEntrada = findViewById(R.id.etParaderoEntrada);
        etParaderoSalida = findViewById(R.id.etParaderoSalida);
        etTiempoViaje = findViewById(R.id.etTiempoViaje);
        btnGuardar = findViewById(R.id.btnGuardar);
        etIdMovimiento = findViewById(R.id.etIdMovimiento);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        uid = user.getUid();
        db = FirebaseFirestore.getInstance();

        // Obtener idMovimiento del intent
        idMovimiento = getIntent().getStringExtra("idMovimiento");
        if (idMovimiento == null) {
            Toast.makeText(this, "ID de movimiento no recibido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        etIdMovimiento.setText(idMovimiento);

        // Cargar datos del movimiento y llenar formulario
        cargarMovimiento();

        btnGuardar.setOnClickListener(v -> actualizarMovimiento());
    }

    private void cargarMovimiento() {
        db.collection("Usuarios")
                .document(uid)
                .collection("MovimientosLimaPass")
                .document(idMovimiento)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        MovimientoLimaPass m = documentSnapshot.toObject(MovimientoLimaPass.class);
                        if (m != null) {
                            etFecha.setText(m.getFecha());
                            etParaderoEntrada.setText(m.getParaderoEntrada());
                            etParaderoSalida.setText(m.getParaderoSalida());
                            etTiempoViaje.setText(m.getTiempoViaje());
                        }
                    } else {
                        Toast.makeText(this, "Movimiento no encontrado", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    finish();
                });
    }

    private void actualizarMovimiento() {
        String fecha = etFecha.getText().toString().trim();
        String entrada = etParaderoEntrada.getText().toString().trim();
        String salida = etParaderoSalida.getText().toString().trim();
        String tiempo = etTiempoViaje.getText().toString().trim();

        if (fecha.isEmpty() || entrada.isEmpty() || salida.isEmpty() || tiempo.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> datos = new HashMap<>();
        datos.put("fecha", fecha);
        datos.put("paraderoEntrada", entrada);
        datos.put("paraderoSalida", salida);
        datos.put("tiempoViaje", tiempo);

        db.collection("Usuarios")
                .document(uid)
                .collection("MovimientosLimaPass")
                .document(idMovimiento)
                .update(datos)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Movimiento actualizado", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al actualizar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
