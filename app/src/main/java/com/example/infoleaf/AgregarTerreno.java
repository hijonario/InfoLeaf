package com.example.infoleaf;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
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

    private EditText etNombre;
    private CheckBox cbPozo;


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
        etNombre = findViewById(R.id.et_nombreAT);
        cbPozo = findViewById(R.id.cb_pozo_AT);

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
        }else{
            showCustomToast("Agrega correctamente todos los campos");
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
            showCustomToast("Error cargando plantaciones");
        }
    }

    public void guardarTierra(View view) {
        String nombreTierra = etNombre.getText().toString().trim();
        boolean tienePozo = cbPozo.isChecked();

        String nombrePlantacion = (String) spinner.getSelectedItem();
        Integer idPlantacion = obtenerIdPlantacionPorNombre(nombrePlantacion);

        String idUsuario = id;

        if (listaTerrenos.isEmpty()) {
            showCustomToast("Debes añadir al menos un poligono");
            return;
        }

        if (nombreTierra.isEmpty()) {
            showCustomToast("El nombre de la tierra no puede estar vacío");
            return;
        }

        TierraDAO dao = new TierraDAO();
        try {
            boolean insertado = dao.insertarTierraConTerrenos(nombreTierra, idUsuario, idPlantacion, tienePozo, listaTerrenos);
            if (insertado) {
                showCustomToast( "Tierra guardada correctamente");
                finish(); // o limpiar campos
            } else {
                showCustomToast("No se pudo guardar la tierra");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showCustomToast("Error guardando tierra");
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
                showCustomToast("Error al interpretar los datos");
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