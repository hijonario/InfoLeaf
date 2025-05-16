package com.example.infoleaf;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.infoleaf.adapters.AdaptadorGastos;
import com.example.infoleaf.dao.GastosDAO;
import com.example.infoleaf.models.GastoModel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Gastos extends AppCompatActivity {

    private ExpandableListView expandableListView;
    private AdaptadorGastos adapter;
    private TextView textNoTierras;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gastos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        id = getIntent().getStringExtra("id");

        expandableListView = findViewById(R.id.list_gastos_AG);
        textNoTierras = findViewById(R.id.textNoTierras);

        cargarGastos();
    }

    protected void onResume(){
        super.onResume();
        cargarGastos();
    }

    private void cargarGastos() {
        GastosDAO gastosDAO = new GastosDAO();
        String dniUsuario = id;

        try {
            Map<String, List<GastoModel>> mapaGastos = gastosDAO.obtenerGastosPorAnio(dniUsuario);

            if (mapaGastos.isEmpty()) {
                textNoTierras.setVisibility(View.VISIBLE);
                expandableListView.setVisibility(View.GONE);
            } else {
                textNoTierras.setVisibility(View.GONE);
                expandableListView.setVisibility(View.VISIBLE);

                List<String> listaAnios = new ArrayList<>(mapaGastos.keySet());
                adapter = new AdaptadorGastos(this, listaAnios, mapaGastos);
                expandableListView.setAdapter(adapter);
            }

        } catch (SQLException e) {
            showCustomToast("Error al cargar gastos");
            e.printStackTrace();
        }
    }

    public void salir(View view) {
        finish();
    }

    public void agregarGasto(View view) {
        Intent intent = new Intent(this, AgregarGasto.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    private void showCustomToast(String mensaje) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.estilo_toast, null);

        TextView text = layout.findViewById(R.id.toast_text);
        text.setText(mensaje);

        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
}