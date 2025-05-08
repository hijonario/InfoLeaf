package com.example.infoleaf.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.infoleaf.R;
import com.example.infoleaf.models.TerrenosModel;

import java.util.List;

public class TerrenoAdapter extends ArrayAdapter<TerrenosModel> {

    private final List<TerrenosModel> listaTerrenos;

    public TerrenoAdapter(Context context, List<TerrenosModel> terrenos) {
        super(context, 0, terrenos);
        this.listaTerrenos = terrenos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TerrenosModel terreno = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_terreno, parent, false);
        }

        TextView tvUbicacion = convertView.findViewById(R.id.tv_ubicacion);
        TextView tvPoligono = convertView.findViewById(R.id.tv_poligono);
        TextView tvParcela = convertView.findViewById(R.id.tv_parcela);
        TextView tvSuperficie = convertView.findViewById(R.id.tv_superficie);
        Button btnEliminar = convertView.findViewById(R.id.btn_eliminar);

        tvUbicacion.setText("Ubicación: " + terreno.getUbicacion());
        tvPoligono.setText("Polígono: " + terreno.getPoligono());
        tvParcela.setText("Parcela: " + terreno.getNum_parcela());
        tvSuperficie.setText("Superficie: " + terreno.getSuperficie() + " m²");

        btnEliminar.setOnClickListener(v -> {
            listaTerrenos.remove(position);
            notifyDataSetChanged();
        });

        return convertView;
    }
}

