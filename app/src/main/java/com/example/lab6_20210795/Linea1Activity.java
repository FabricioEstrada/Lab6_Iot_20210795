package com.example.lab6_20210795;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Linea1Activity extends BaseActivity {

    private List<MovimientoLinea1> lista;
    private MovimientoLinea1Adapter adapter;
    private String uid;

    private ActivityResultLauncher<Intent> movimientoLauncher;
    private ActivityResultLauncher<Intent> editarLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentLayout(R.layout.activity_linea1);

        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        lista = new ArrayList<>();

        // Aquí creamos el adapter con el listener que manejará editar y eliminar
        adapter = new MovimientoLinea1Adapter(lista, new MovimientoLinea1Adapter.OnItemClickListener() {
            @Override
            public void onEditarClick(MovimientoLinea1 movimiento) {
                // Abrir actividad para editar
                Intent intent = new Intent(Linea1Activity.this, EditarMovimientoLinea1Activity.class);
                intent.putExtra("idMovimiento", movimiento.getIdMovimiento());
                editarLauncher.launch(intent);
            }

            @Override
            public void onEliminarClick(MovimientoLinea1 movimiento) {
                new androidx.appcompat.app.AlertDialog.Builder(Linea1Activity.this)
                        .setTitle("Eliminar Movimiento")
                        .setMessage("¿Seguro quieres eliminar este movimiento?")
                        .setPositiveButton("Sí", (dialog, which) -> {
                            eliminarMovimiento(movimiento.getIdMovimiento());
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerViewLinea1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        Button btnFiltrarFecha = findViewById(R.id.btnFiltrarFecha);
        Button btnLimpiarFiltro = findViewById(R.id.btnLimpiarFiltro);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        }

        // Lanzador para crear movimiento
        movimientoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        cargarMovimientos(null, null);
                    }
                }
        );

        // Lanzador para editar movimiento
        editarLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        cargarMovimientos(null, null);
                    }
                }
        );

        FloatingActionButton fab = findViewById(R.id.fabCrearMovimiento);
        if (fab != null) {
            fab.setOnClickListener(v -> {
                Intent intent = new Intent(Linea1Activity.this, CrearMovimientoLinea1Activity.class);
                movimientoLauncher.launch(intent);
            });
        }

        // Cargar movimientos al inicio sin filtro
        cargarMovimientos(null, null);

        // Filtro por fechas (igual que antes)
        btnFiltrarFecha.setOnClickListener(v -> {
            final String[] fechaInicio = new String[1];
            final String[] fechaFin = new String[1];

            DatePickerDialog dpInicio = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        fechaInicio[0] = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);

                        DatePickerDialog dpFin = new DatePickerDialog(this,
                                (view2, year2, month2, dayOfMonth2) -> {
                                    fechaFin[0] = String.format("%04d-%02d-%02d", year2, month2 + 1, dayOfMonth2);
                                    cargarMovimientos(fechaInicio[0], fechaFin[0]);
                                }, year, month, dayOfMonth);
                        dpFin.setTitle("Selecciona la fecha fin");
                        dpFin.show();

                    }, 2025, 5, 15);
            dpInicio.setTitle("Selecciona la fecha inicio");
            dpInicio.show();
        });

        btnLimpiarFiltro.setOnClickListener(v -> {
            cargarMovimientos(null, null);
        });
    }

    private void cargarMovimientos(String fechaInicio, String fechaFin) {
        if (uid == null) return;
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Usuarios")
                .document(uid)
                .collection("MovimientosLinea1")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    lista.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        MovimientoLinea1 m = doc.toObject(MovimientoLinea1.class);
                        m.setIdMovimiento(doc.getId());

                        if (fechaInicio != null && fechaFin != null) {
                            String fecha = m.getFecha(); // yyyy-MM-dd
                            if (fecha.compareTo(fechaInicio) >= 0 && fecha.compareTo(fechaFin) <= 0) {
                                lista.add(m);
                            }
                        } else {
                            lista.add(m);
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void eliminarMovimiento(String idMovimiento) {
        if (uid == null) return;
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Usuarios")
                .document(uid)
                .collection("MovimientosLinea1")
                .document(idMovimiento)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Movimiento eliminado", Toast.LENGTH_SHORT).show();
                    cargarMovimientos(null, null);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al eliminar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}

