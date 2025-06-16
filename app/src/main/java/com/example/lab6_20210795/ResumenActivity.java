package com.example.lab6_20210795;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResumenActivity extends BaseActivity {

    private BarChart barChartMovimientos;
    private PieChart pieChartUsoTarjetas;
    private Button btnFiltrarFecha;

    private String uid;
    private FirebaseFirestore db;

    private String fechaInicio = null;
    private String fechaFin = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_resumen);

        barChartMovimientos = findViewById(R.id.barChartMovimientos);
        pieChartUsoTarjetas = findViewById(R.id.pieChartUsoTarjetas);
        btnFiltrarFecha = findViewById(R.id.btnFiltrarFecha);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();

        btnFiltrarFecha.setOnClickListener(v -> seleccionarRangoFechas());

        cargarDatos(fechaInicio, fechaFin);
    }

    private void seleccionarRangoFechas() {
        final Calendar c = Calendar.getInstance();

        DatePickerDialog dpInicio = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    fechaInicio = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);

                    DatePickerDialog dpFin = new DatePickerDialog(this,
                            (view2, year2, month2, dayOfMonth2) -> {
                                fechaFin = String.format("%04d-%02d-%02d", year2, month2 + 1, dayOfMonth2);
                                cargarDatos(fechaInicio, fechaFin);
                            }, year, month, dayOfMonth);
                    dpFin.setTitle("Selecciona fecha fin");
                    dpFin.show();

                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dpInicio.setTitle("Selecciona fecha inicio");
        dpInicio.show();
    }

    private void cargarDatos(String fechaInicio, String fechaFin) {
        // Limpiar gráficos previos
        barChartMovimientos.clear();
        pieChartUsoTarjetas.clear();

        Map<String, Integer> conteoLinea1PorMes = new HashMap<>();
        Map<String, Integer> conteoLimaPassPorMes = new HashMap<>();

        final int[] viajesLinea1Tren = {0};
        final int[] viajesLimaPassBus = {0}; // Solo bus para Lima Pass

        // Leer datos MovimientosLinea1 (tren)
        db.collection("Usuarios").document(uid).collection("MovimientosLinea1")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        String fecha = doc.getString("fecha");
                        if (fecha == null) continue;

                        if (fechaInicio != null && fechaFin != null) {
                            if (fecha.compareTo(fechaInicio) < 0 || fecha.compareTo(fechaFin) > 0)
                                continue;
                        }

                        String mes = fecha.substring(0,7);
                        conteoLinea1PorMes.put(mes, conteoLinea1PorMes.getOrDefault(mes, 0) + 1);

                        viajesLinea1Tren[0]++;
                    }

                    // Leer datos MovimientosLimaPass (solo bus)
                    db.collection("Usuarios").document(uid).collection("MovimientosLimaPass")
                            .get()
                            .addOnSuccessListener(qSnap -> {
                                for (QueryDocumentSnapshot doc2 : qSnap) {
                                    String fecha = doc2.getString("fecha");
                                    if (fecha == null) continue;

                                    if (fechaInicio != null && fechaFin != null) {
                                        if (fecha.compareTo(fechaInicio) < 0 || fecha.compareTo(fechaFin) > 0)
                                            continue;
                                    }

                                    String mes = fecha.substring(0,7);
                                    conteoLimaPassPorMes.put(mes, conteoLimaPassPorMes.getOrDefault(mes, 0) + 1);

                                    // Solo bus para Lima Pass
                                    viajesLimaPassBus[0]++;
                                }

                                mostrarGraficoBarras(conteoLinea1PorMes, conteoLimaPassPorMes);
                                mostrarGraficoTorta(viajesLinea1Tren[0], viajesLimaPassBus[0]);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error al cargar LimaPass: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar Línea 1: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }


    private void mostrarGraficoBarras(Map<String, Integer> linea1, Map<String, Integer> limaPass) {
        List<BarEntry> entriesLinea1 = new ArrayList<>();
        List<BarEntry> entriesLimaPass = new ArrayList<>();

        List<String> meses = new ArrayList<>(linea1.keySet());
        // Asegurarse que todos los meses estén en ambos mapas (puedes mejorar esto)
        for (String mes : limaPass.keySet()) {
            if (!meses.contains(mes)) meses.add(mes);
        }
        meses.sort(String::compareTo);

        for (int i = 0; i < meses.size(); i++) {
            String mes = meses.get(i);
            entriesLinea1.add(new BarEntry(i, linea1.getOrDefault(mes, 0)));
            entriesLimaPass.add(new BarEntry(i, limaPass.getOrDefault(mes, 0)));
        }

        BarDataSet setLinea1 = new BarDataSet(entriesLinea1, "Línea 1");
        setLinea1.setColor(0xFF3F51B5); // Azul
        BarDataSet setLimaPass = new BarDataSet(entriesLimaPass, "Lima Pass");
        setLimaPass.setColor(0xFFFF5722); // Naranja

        BarData data = new BarData(setLinea1, setLimaPass);
        data.setBarWidth(0.4f);

        barChartMovimientos.setData(data);
        barChartMovimientos.getXAxis().setGranularity(1f);
        barChartMovimientos.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= 0 && index < meses.size()) {
                    return meses.get(index);
                } else {
                    return "";
                }
            }
        });

        barChartMovimientos.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChartMovimientos.getAxisRight().setEnabled(false);
        barChartMovimientos.groupBars(0f, 0.2f, 0f);
        barChartMovimientos.invalidate();
    }

    private void mostrarGraficoTorta(int viajesLinea1Tren, int viajesLimaPassBus) {
        List<PieEntry> entries = new ArrayList<>();
        int total = viajesLinea1Tren + viajesLimaPassBus;
        if (total == 0) return;

        if (viajesLinea1Tren > 0)
            entries.add(new PieEntry(viajesLinea1Tren, "Línea 1 - Tren"));
        if (viajesLimaPassBus > 0)
            entries.add(new PieEntry(viajesLimaPassBus, "Lima Pass - Bus"));

        PieDataSet dataSet = new PieDataSet(entries, "Uso de Tarjetas");
        dataSet.setColors(new int[]{0xFF3F51B5, 0xFFFF5722}); // Azul y naranja
        PieData data = new PieData(dataSet);

        data.setValueTextColor(Color.BLACK);

        pieChartUsoTarjetas.setData(data);
        pieChartUsoTarjetas.invalidate();
    }

}
