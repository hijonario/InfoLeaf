package com.example.infoleaf.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.infoleaf.R;
import com.example.infoleaf.models.GastoModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdaptadorGastos extends BaseExpandableListAdapter {
    private Context context;
    private List<String> listAnios;
    private Map<String, List<GastoModel>> mapGastos;

    public AdaptadorGastos(Context context, List<String> listAnios, Map<String, List<GastoModel>> mapGastos) {
        this.context = context;
        this.listAnios = listAnios;
        this.mapGastos = mapGastos;
    }

    @Override
    public int getGroupCount() {
        return listAnios.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        String anio = listAnios.get(groupPosition);
        return mapGastos.get(anio).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listAnios.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        String anio = listAnios.get(groupPosition);
        return mapGastos.get(anio).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) { return groupPosition; }

    @Override
    public long getChildId(int groupPosition, int childPosition) { return childPosition; }

    @Override
    public boolean hasStableIds() { return false; }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String anio = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(android.R.layout.simple_expandable_list_item_1, parent, false);
        }

        TextView txtAnio = (TextView) convertView.findViewById(android.R.id.text1);
        txtAnio.setText("Año: " + anio);
        txtAnio.setTextSize(20f);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        GastoModel gasto = (GastoModel) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_gasto, parent, false);
        }

        TextView txtDinero = convertView.findViewById(R.id.txt_dinero);
        TextView txtDescripcion = convertView.findViewById(R.id.txt_descripcion);
        TextView txtFecha = convertView.findViewById(R.id.txt_fecha);

        txtDinero.setText("Dinero: " + gasto.getDinero() + " €");
        txtDescripcion.setText("Descripción: " + gasto.getDescripcion());
        txtFecha.setText("Fecha: " + gasto.getFecha());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
