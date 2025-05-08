package com.example.infoleaf.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.infoleaf.R;
import com.example.infoleaf.models.TerrenosModel;

import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> listTierras;
    private HashMap<String, List<TerrenosModel>> listTerrenos;

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
        return listTerrenos.get(listTierras.get(groupPosition)).size();
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

    //grupo (Tierras)
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String tierraNombre = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(android.R.layout.simple_expandable_list_item_1, null);
        }

        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(tierraNombre);
        return convertView;
    }

    //hijos (Terrenos)
    @SuppressLint("ResourceType")
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        TerrenosModel terreno = (TerrenosModel) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.drawable.terreno_item, null);
        }

        TextView textPoligono = convertView.findViewById(R.id.textPoligono);
        TextView textNumParcela = convertView.findViewById(R.id.textNumParcela);
        TextView textUbicacion = convertView.findViewById(R.id.textUbicacion);
        TextView textSuperficie = convertView.findViewById(R.id.textSuperficie);

        textPoligono.setText("Polígono: " + terreno.getPoligono());
        textNumParcela.setText("Parcela: " + terreno.getNum_parcela());
        textUbicacion.setText("Ubicación: " + terreno.getUbicacion());
        textSuperficie.setText("Superficie: " + terreno.getSuperficie() + " ha");

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
