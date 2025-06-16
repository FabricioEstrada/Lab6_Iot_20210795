package com.example.lab6_20210795;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MovimientoLimaPassAdapter extends RecyclerView.Adapter<MovimientoLimaPassAdapter.ViewHolder> {

    private List<MovimientoLimaPass> movimientos;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditarClick(MovimientoLimaPass movimiento);
        void onEliminarClick(MovimientoLimaPass movimiento);
    }

    // Constructor con listener
    public MovimientoLimaPassAdapter(List<MovimientoLimaPass> movimientos, OnItemClickListener listener) {
        this.movimientos = movimientos;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvIdMovimiento, tvFecha, tvParaderos, tvTiempo;
        ImageButton btnEditar, btnEliminar;

        public ViewHolder(View itemView) {
            super(itemView);
            tvIdMovimiento = itemView.findViewById(R.id.tvIdMovimiento);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvParaderos = itemView.findViewById(R.id.tvParaderos);
            tvTiempo = itemView.findViewById(R.id.tvTiempo);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }

        public void bind(MovimientoLimaPass movimiento, OnItemClickListener listener) {
            tvIdMovimiento.setText("ID: " + movimiento.getIdMovimiento());
            tvFecha.setText(movimiento.getFecha());
            tvParaderos.setText(movimiento.getParaderoEntrada() + " - " + movimiento.getParaderoSalida());
            tvTiempo.setText(movimiento.getTiempoViaje());

            btnEditar.setOnClickListener(v -> listener.onEditarClick(movimiento));
            btnEliminar.setOnClickListener(v -> listener.onEliminarClick(movimiento));
        }
    }

    @Override
    public MovimientoLimaPassAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movimiento_limapass, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovimientoLimaPassAdapter.ViewHolder holder, int position) {
        holder.bind(movimientos.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return movimientos.size();
    }
}
