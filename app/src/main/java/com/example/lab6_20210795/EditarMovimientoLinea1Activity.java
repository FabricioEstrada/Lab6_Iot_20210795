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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditarMovimientoLinea1Activity extends AppCompatActivity {

    private EditText etFecha, etEstacionEntrada, etEstacionSalida, etTiempoViaje, etIdMovimiento;
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
        setContentView(R.layout.activity_editar_movimiento_linea1);

        etFecha = findViewById(R.id.etFecha);
        etEstacionEntrada = findViewById(R.id.etEstacionEntrada);
        etEstacionSalida = findViewById(R.id.etEstacionSalida);
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

        btnGuardar.setOnClickListener(v -> {
            actualizarMovimiento();
        });
    }

    private void cargarMovimiento() {
        db.collection("Usuarios")
                .document(uid)
                .collection("MovimientosLinea1")
                .document(idMovimiento)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        MovimientoLinea1 m = documentSnapshot.toObject(MovimientoLinea1.class);
                        if (m != null) {
                            etFecha.setText(m.getFecha());
                            etEstacionEntrada.setText(m.getEstacionEntrada());
                            etEstacionSalida.setText(m.getEstacionSalida());
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
        String entrada = etEstacionEntrada.getText().toString().trim();
        String salida = etEstacionSalida.getText().toString().trim();
        String tiempo = etTiempoViaje.getText().toString().trim();

        if (fecha.isEmpty() || entrada.isEmpty() || salida.isEmpty() || tiempo.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> datos = new HashMap<>();
        datos.put("fecha", fecha);
        datos.put("estacionEntrada", entrada);
        datos.put("estacionSalida", salida);
        datos.put("tiempoViaje", tiempo);

        db.collection("Usuarios")
                .document(uid)
                .collection("MovimientosLinea1")
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
