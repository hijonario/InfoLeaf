package com.example.infoleaf;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.infoleaf.adapters.TerrenoAdapter;
import com.example.infoleaf.dao.TierraDAO;
import com.example.infoleaf.models.TerrenosModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AgregarTerreno extends AppCompatActivity {
    private ArrayList<TerrenosModel> listaTerrenos = new ArrayList<>();
    private TerrenoAdapter terrenoAdapter;
    private Spinner spinner;
    private ArrayAdapter<String> spinnerAdapter;
    private String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agregar_terreno);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        id = getIntent().getStringExtra("id");

        ListView listView = findViewById(R.id.list_parcela_AT);
        terrenoAdapter = new TerrenoAdapter(this, listaTerrenos);
        listView.setAdapter(terrenoAdapter);

        spinner = findViewById(R.id.spinner_AT);
        cargarPlantacionesEnSpinner();

    }

    public void agregarTerreno(View view) {
        EditText etPoligono = findViewById(R.id.et_poligono_AT);
        EditText etParcela = findViewById(R.id.et_parcela_AT);
        EditText etUbicacion = findViewById(R.id.et_ubicacion_AT);
        EditText etSuperficie = findViewById(R.id.et_superficie_AT);

        String poligono = etPoligono.getText().toString();
        String parcela = etParcela.getText().toString();
        String ubicacion = etUbicacion.getText().toString();
        String superficieStr = etSuperficie.getText().toString();

        if (!poligono.isEmpty() && !parcela.isEmpty() && !ubicacion.isEmpty() && !superficieStr.isEmpty()) {
            double superficie = Double.parseDouble(superficieStr);
            TerrenosModel nuevoTerreno = new TerrenosModel(superficie, ubicacion, parcela, poligono);
            listaTerrenos.add(nuevoTerreno);
            terrenoAdapter.notifyDataSetChanged();


            etPoligono.setText("");
            etParcela.setText("");
            etUbicacion.setText("");
            etSuperficie.setText("");
        }
    }

    private void cargarPlantacionesEnSpinner() {
        TierraDAO dao = new TierraDAO();
        try {
            List<String> nombresPlantaciones = dao.obtenerNombresPlantaciones();
            spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombresPlantaciones);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(spinnerAdapter);
        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error cargando plantaciones", Toast.LENGTH_SHORT).show();
        }
    }

    public void guardarTierra(View view) {
        EditText etNombre = findViewById(R.id.et_nombreAT);
        CheckBox cbPozo = findViewById(R.id.cb_pozo_AT);
        String nombreTierra = etNombre.getText().toString().trim();
        boolean tienePozo = cbPozo.isChecked();

        String nombrePlantacion = (String) spinner.getSelectedItem();
        Integer idPlantacion = obtenerIdPlantacionPorNombre(nombrePlantacion);

        String idUsuario = id;

        if (listaTerrenos.isEmpty()) {
            Toast.makeText(this, "Debes añadir al menos un terreno", Toast.LENGTH_SHORT).show();
            return;
        }

        if (nombreTierra.isEmpty()) {
            Toast.makeText(this, "El nombre de la tierra no puede estar vacío", Toast.LENGTH_SHORT).show();
            return;
        }

        TierraDAO dao = new TierraDAO();
        try {
            boolean insertado = dao.insertarTierraConTerrenos(nombreTierra, idUsuario, idPlantacion, tienePozo, listaTerrenos);
            if (insertado) {
                Toast.makeText(this, "Tierra guardada correctamente", Toast.LENGTH_SHORT).show();
                finish(); // o limpiar campos
            } else {
                Toast.makeText(this, "No se pudo guardar la tierra", Toast.LENGTH_SHORT).show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error guardando tierra", Toast.LENGTH_SHORT).show();
        }
    }

    private Integer obtenerIdPlantacionPorNombre(String nombrePlantacion) {
        TierraDAO dao = new TierraDAO();
        try {
            return dao.obtenerIdPlantacionPorNombre(nombrePlantacion);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void abrirMapa(View view) {
        VistaMapa mapDialog = new VistaMapa(this);
        mapDialog.setOnParcelaDataReceivedListener(datos -> {
            try {
                JSONObject json = new JSONObject(datos);

                String poligono = json.getString("poligono");
                String parcela = json.getString("parcela");
                String ubicacion = json.getString("ubicacion");
                String superficie = json.getString("superficie");

                ((EditText) findViewById(R.id.et_poligono_AT)).setText(poligono);
                ((EditText) findViewById(R.id.et_parcela_AT)).setText(parcela);
                ((EditText) findViewById(R.id.et_ubicacion_AT)).setText(ubicacion);
                ((EditText) findViewById(R.id.et_superficie_AT)).setText(superficie);

            } catch (JSONException e) {
                Toast.makeText(this, "Error al interpretar los datos", Toast.LENGTH_SHORT).show();
            }
        });

        mapDialog.show();

        Window window = mapDialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }
    }




    public void salir (View view){
        finish();
    }
}