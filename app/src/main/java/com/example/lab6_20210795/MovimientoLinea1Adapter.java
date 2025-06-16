package com.example.lab6_20210795;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

    public class MovimientoLinea1Adapter extends RecyclerView.Adapter<MovimientoLinea1Adapter.ViewHolder> {

        private List<MovimientoLinea1> movimientos;
        private OnItemClickListener listener;

        public interface OnItemClickListener {
            void onEditarClick(MovimientoLinea1 movimiento);
            void onEliminarClick(MovimientoLinea1 movimiento);
        }

        // Constructor con listener
        public MovimientoLinea1Adapter(List<MovimientoLinea1> movimientos, OnItemClickListener listener) {
            this.movimientos = movimientos;
            this.listener = listener;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvIdMovimiento, tvFecha, tvEstaciones, tvTiempo;
            ImageButton btnEditar, btnEliminar;

            public ViewHolder(View itemView) {
                super(itemView);
                tvIdMovimiento = itemView.findViewById(R.id.tvIdMovimiento);
                tvFecha = itemView.findViewById(R.id.tvFecha);
                tvEstaciones = itemView.findViewById(R.id.tvEstaciones);
                tvTiempo = itemView.findViewById(R.id.tvTiempo);
                btnEditar = itemView.findViewById(R.id.btnEditar);
                btnEliminar = itemView.findViewById(R.id.btnEliminar);
            }

            public void bind(MovimientoLinea1 movimiento, OnItemClickListener listener) {
                tvIdMovimiento.setText("ID: " + movimiento.getIdMovimiento());
                tvFecha.setText(movimiento.getFecha());
                tvEstaciones.setText(movimiento.getEstacionEntrada() + " - " + movimiento.getEstacionSalida());
                tvTiempo.setText(movimiento.getTiempoViaje());

                btnEditar.setOnClickListener(v -> listener.onEditarClick(movimiento));
                btnEliminar.setOnClickListener(v -> listener.onEliminarClick(movimiento));
            }
        }

        @Override
        public MovimientoLinea1Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movimiento_linea1, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MovimientoLinea1Adapter.ViewHolder holder, int position) {
            holder.bind(movimientos.get(position), listener);
        }

        @Override
        public int getItemCount() {
            return movimientos.size();
        }
    }

