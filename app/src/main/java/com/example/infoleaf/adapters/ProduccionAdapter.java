package com.example.infoleaf.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.infoleaf.Gastos;
import com.example.infoleaf.Produccion;
import com.example.infoleaf.R;
import com.example.infoleaf.dao.GastosDAO;
import com.example.infoleaf.dao.ProduccionDAO;
import com.example.infoleaf.models.GastoModel;
import com.example.infoleaf.models.ProduccionModel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProduccionAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> tiposProduccion;
    private Map<String, Map<Integer, List<ProduccionModel>>> datos;
    private boolean modoEliminar = false;

    public ProduccionAdapter(Context context, Map<String, Map<Integer, List<ProduccionModel>>> datos) {
        this.context = context;
        this.datos = datos;
        this.tiposProduccion = new ArrayList<>(datos.keySet());
    }

    @Override
    public int getGroupCount() {
        return tiposProduccion.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        String tipo = tiposProduccion.get(groupPosition);
        return datos.get(tipo).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return tiposProduccion.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        String tipo = tiposProduccion.get(groupPosition);
        List<Integer> anios = new ArrayList<>(datos.get(tipo).keySet());
        return anios.get(childPosition);
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
        return true;
    }

    // Vista del grupo (tipo de producción)
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String tipo = (String) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(android.R.layout.simple_expandable_list_item_1, parent, false);
        }

        TextView text = convertView.findViewById(android.R.id.text1);
        text.setText(tipo);
        text.setTextSize(22f);

        return convertView;
    }

    // Vista del hijo (años)
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Integer anio = (Integer) getChild(groupPosition, childPosition);
        String tipo = tiposProduccion.get(groupPosition);
        List<ProduccionModel> items = datos.get(tipo).get(anio);


        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);
        }

        TextView text1 = convertView.findViewById(android.R.id.text1);
        TextView text2 = convertView.findViewById(android.R.id.text2);

        text1.setText("Año: " + anio);
        StringBuilder detalles = new StringBuilder();
        for (ProduccionModel item : items) {
            if (modoEliminar) {
                convertView.setBackgroundResource(R.drawable.fondo_eliminar);
                convertView.setOnClickListener(v -> mostrarDialogoConfirmacion(item.getId()));
            } else {
                convertView.setBackgroundResource(android.R.color.transparent);
                convertView.setOnClickListener(null);
            }

            detalles.append(item.getNombreTierra())
                    .append(" - ")
                    .append(item.getDetalles())
                    .append("\n");
        }

        text2.setText(detalles.toString().trim());



        return convertView;
    }

    private void mostrarDialogoConfirmacion(int id) {
        new android.app.AlertDialog.Builder(context)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Deseas eliminar esta Producción?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    eliminarProduccion(id);
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void eliminarProduccion(int id) {
        new Thread(() -> {
            try {
                ProduccionDAO produccionDAO = new ProduccionDAO();
                boolean eliminado = produccionDAO.eliminarProduccion(id);

                ((Activity) context).runOnUiThread(() -> {
                    if (eliminado) {
                        Toast.makeText(context, "Produccion eliminada", Toast.LENGTH_SHORT).show();
                        ((Produccion) context).cargarProducciones();
                    } else {
                        Toast.makeText(context, "Error al eliminar", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (SQLException e) {
                ((Activity) context).runOnUiThread(() -> {
                    Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    public void setModoEliminar(boolean modoEliminar) {
        this.modoEliminar = modoEliminar;
        notifyDataSetChanged();
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
