package com.example.infoleaf.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.infoleaf.R;
import com.example.infoleaf.dao.TerrenoDAO;
import com.example.infoleaf.dao.TierraDAO;
import com.example.infoleaf.models.TerrenosModel;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> listTierras;
    private HashMap<String, List<TerrenosModel>> listTerrenos;
    private boolean modoEliminar = false;
    private TerrenoDAO terrenoDAO = new TerrenoDAO(); // Asegúrate de tener acceso al DAO
    private TierraDAO tierraDAO = new TierraDAO();

    public ExpandableListAdapter(Context context, List<String> listTierras, HashMap<String, List<TerrenosModel>> listTerrenos) {
        this.context = context;
        this.listTierras = listTierras;
        this.listTerrenos = listTerrenos;
    }

    @Override
    public int getGroupCount() {
        return listTierras.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        String tierra = listTierras.get(groupPosition);
        List<TerrenosModel> terrenos = listTerrenos.get(tierra);
        return terrenos != null ? terrenos.size() : 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listTierras.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listTerrenos.get(listTierras.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    // Vista del grupo (Tierra)
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String tierraNombre = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(android.R.layout.simple_expandable_list_item_1, parent, false);
        }

        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(tierraNombre);
        textView.setPadding(100, 40, 0, 40);
        textView.setTextSize(18);

        return convertView;
    }

    // Vista del hijo (Terreno)
    @SuppressLint("InflateParams")
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        TerrenosModel terreno = (TerrenosModel) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.terreno_item, parent, false); // ✅ Asegúrate de tener este layout
        }

        // Referencias a los TextViews
        TextView textPoligono = convertView.findViewById(R.id.textPoligono);
        TextView textNumParcela = convertView.findViewById(R.id.textNumParcela);
        TextView textUbicacion = convertView.findViewById(R.id.textUbicacion);
        TextView textSuperficie = convertView.findViewById(R.id.textSuperficie);

        // Asignación de valores
        textPoligono.setText("Polígono: " + terreno.getPoligono());
        textNumParcela.setText("Parcela: " + terreno.getNum_parcela());
        textUbicacion.setText("Ubicación: " + terreno.getUbicacion());
        textSuperficie.setText("Superficie: " + terreno.getSuperficie() + " ha");

        // Configuración del click en el terreno (si está en modo eliminar)
        if (modoEliminar) {
            convertView.setOnClickListener(v -> {
            });
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

}
