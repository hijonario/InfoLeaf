package com.example.infoleaf;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.infoleaf.dao.ProduccionDAO;
import com.example.infoleaf.dao.TierraDAO;
import com.example.infoleaf.models.TierrasModel;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AgregarProduccion extends AppCompatActivity {
    private TextInputEditText datePickerEditText;
    private Spinner spinnerTierras;
    private String id;
    private ProduccionDAO produccionDAO = new ProduccionDAO();

    private EditText variedad;
    private EditText kilos;
    private EditText kilosArbol;
    private EditText kilosSuelo;
    private EditText bodega;
    private EditText grado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String tipoProduccion = getIntent().getStringExtra("tipoProduccion");

        cargarVistaSegunTipo(tipoProduccion);

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        id = getIntent().getStringExtra("id");

        spinnerTierras = findViewById(R.id.combo_tierras);
        datePickerEditText = findViewById(R.id.DP_fechas);

        variedad = findViewById(R.id.et_variedad_AP);
        kilos = findViewById(R.id.et_kilos_AP);
        kilosArbol = findViewById(R.id.et_kilosArbol_AP);
        kilosSuelo = findViewById(R.id.et_kilosSuelo_AP);
        bodega = findViewById(R.id.et_bodega_AP);
        grado = findViewById(R.id.et_grado_AP);


        configurarDatePicker();
        configurarSpinnerTierras();
    }

    private void cargarVistaSegunTipo(String tipo) {
        if (tipo == null) tipo = "uva";

        switch (tipo) {
            case "olivo":
                setContentView(R.layout.activity_agregar_produccion_olivo);
                break;
            case "cereal":
                setContentView(R.layout.activity_agregar_produccion_cereal);
                break;
            case "uva":
            default:
                setContentView(R.layout.activity_agregar_produccion_uva);
        }
    }

    public void salir(View view) {
        finish();
    }

    private void configurarDatePicker() {
        datePickerEditText = findViewById(R.id.DP_fechas);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        datePickerEditText.setText(dateFormat.format(new Date()));

        datePickerEditText.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Selecciona una fecha")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build();

            datePicker.addOnPositiveButtonClickListener(selectedDate -> {
                String formattedDate = dateFormat.format(new Date(selectedDate));
                datePickerEditText.setText(formattedDate);
            });

            datePicker.show(getSupportFragmentManager(), "DATE_PICKER_TAG");
        });

    }

    private void configurarSpinnerTierras() {
        spinnerTierras = findViewById(R.id.combo_tierras);

        TierraDAO tierraDAO = new TierraDAO();
        try {
            List<TierrasModel> tierras = tierraDAO.obtenerTodasLasTierras(id);

            List<String> nombresTierras = new ArrayList<>();
            for (TierrasModel tierra : tierras) {
                nombresTierras.add(tierra.getNombre());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_spinner_item,
                    nombresTierras
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerTierras.setAdapter(adapter);

            // Cargar Trabajos
            spinnerTierras.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long idSpinner) {
                    int idTierraSeleccionada = tierras.get(position).getId();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insertarCereal(View view) {
        try {
            if (variedad.getText().toString().isEmpty()) {
                Toast.makeText(this, "El tipo de cereal es requerido", Toast.LENGTH_SHORT).show();
                return;
            }

            if (kilos.getText().toString().isEmpty()) {
                Toast.makeText(this, "Los kilos son requeridos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (datePickerEditText.getText().toString().isEmpty()) {
                Toast.makeText(this, "La fecha es requerida", Toast.LENGTH_SHORT).show();
                return;
            }

            java.util.Date fecha;
            try {
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                fecha = format.parse(datePickerEditText.getText().toString());
            } catch (Exception e) {
                Toast.makeText(this, "Formato de fecha inválido (use dd/MM/yyyy)", Toast.LENGTH_SHORT).show();
                return;
            }

            Calendar cal = Calendar.getInstance();
            cal.setTime(fecha);
            int anio = cal.get(Calendar.YEAR);

            String tipoCereal = variedad.getText().toString();
            double kilosCereal;
            try {
                kilosCereal = Double.parseDouble(kilos.getText().toString());
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Formato inválido para kilos", Toast.LENGTH_SHORT).show();
                return;
            }

            TierraDAO tierraDAO = new TierraDAO();
            List<TierrasModel> tierras = tierraDAO.obtenerTodasLasTierras(id);
            if (tierras == null || tierras.isEmpty()) {
                Toast.makeText(this, "No hay tierras disponibles", Toast.LENGTH_SHORT).show();
                return;
            }

            int posicionSeleccionada = spinnerTierras.getSelectedItemPosition();
            if (posicionSeleccionada < 0 || posicionSeleccionada >= tierras.size()) {
                Toast.makeText(this, "Seleccione una tierra válida", Toast.LENGTH_SHORT).show();
                return;
            }

            int idTierra = tierras.get(posicionSeleccionada).getId();

            new Thread(() -> {
                try {
                    produccionDAO.insertarProduccionCereal(fecha, anio, idTierra, tipoCereal, kilosCereal);

                    variedad.setText("");
                    kilos.setText("");

                    runOnUiThread(() -> {
                        Toast.makeText(this, "Producción de cereal añadida correctamente", Toast.LENGTH_SHORT).show();
                    });
                } catch (SQLException e) {
                    runOnUiThread(() -> {
                        Log.e("DB_ERROR", "Error al insertar producción", e);
                        Toast.makeText(this, "Error al insertar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
                }
            }).start();

        } catch (Exception e) {
            Log.e("APP_ERROR", "Error inesperado", e);
            Toast.makeText(this, "Error inesperado: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void insertarOlivo(View view) {
        try {
            if (variedad.getText().toString().isEmpty()) {
                Toast.makeText(this, "El tipo de aceituna es requerido", Toast.LENGTH_SHORT).show();
                return;
            }

            if (kilosArbol.getText().toString().isEmpty()) {
                Toast.makeText(this, "Los kilos arbol son requeridos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (kilosSuelo.getText().toString().isEmpty()) {
                kilosSuelo.setText("0");

            }

            java.util.Date fecha;
            try {
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                fecha = format.parse(datePickerEditText.getText().toString());
            } catch (Exception e) {
                Toast.makeText(this, "Formato de fecha inválido (use dd/MM/yyyy)", Toast.LENGTH_SHORT).show();
                return;
            }

            Calendar cal = Calendar.getInstance();
            cal.setTime(fecha);
            int anio = cal.get(Calendar.YEAR);

            String tipoAceituna = variedad.getText().toString();
            double kilosAceitunaArbol;
            double kilosAceitunaSuelo;
            try {
                kilosAceitunaArbol = Double.parseDouble(kilosArbol.getText().toString());
                kilosAceitunaSuelo = Double.parseDouble(kilosArbol.getText().toString());
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Formato inválido para kilos", Toast.LENGTH_SHORT).show();
                return;
            }

            TierraDAO tierraDAO = new TierraDAO();
            List<TierrasModel> tierras = tierraDAO.obtenerTodasLasTierras(id);
            if (tierras == null || tierras.isEmpty()) {
                Toast.makeText(this, "No hay tierras disponibles", Toast.LENGTH_SHORT).show();
                return;
            }

            int posicionSeleccionada = spinnerTierras.getSelectedItemPosition();
            if (posicionSeleccionada < 0 || posicionSeleccionada >= tierras.size()) {
                Toast.makeText(this, "Seleccione una tierra válida", Toast.LENGTH_SHORT).show();
                return;
            }

            int idTierra = tierras.get(posicionSeleccionada).getId();

            new Thread(() -> {
                try {
                    produccionDAO.insertarProduccionAceituna(fecha, anio, idTierra, kilosAceitunaArbol, kilosAceitunaSuelo, tipoAceituna);

                    variedad.setText("");
                    kilosArbol.setText("");
                    kilosSuelo.setText("");

                    runOnUiThread(() -> {
                        Toast.makeText(this, "Producción de aceituna añadida correctamente", Toast.LENGTH_SHORT).show();
                    });
                } catch (SQLException e) {
                    runOnUiThread(() -> {
                        Log.e("DB_ERROR", "Error al insertar producción", e);
                        Toast.makeText(this, "Error al insertar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
                }
            }).start();

        } catch (Exception e) {
            Log.e("APP_ERROR", "Error inesperado", e);
            Toast.makeText(this, "Error inesperado: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void insertarUva(View view) {
        try {
            if (variedad.getText().toString().isEmpty()) {
                Toast.makeText(this, "El tipo de uva es requerido", Toast.LENGTH_SHORT).show();
                return;
            }

            if (kilos.getText().toString().isEmpty()) {
                Toast.makeText(this, "Los kilos son requeridos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (datePickerEditText.getText().toString().isEmpty()) {
                Toast.makeText(this, "La fecha es requerida", Toast.LENGTH_SHORT).show();
                return;
            }

            if (bodega.getText().toString().isEmpty()){
                Toast.makeText(this, "La bodega es requerida", Toast.LENGTH_SHORT).show();
                return;
            }

            if (grado.getText().toString().isEmpty()){
                Toast.makeText(this, "El grado es requerido", Toast.LENGTH_SHORT).show();
                return;
            }

            java.util.Date fecha;
            try {
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                fecha = format.parse(datePickerEditText.getText().toString());
            } catch (Exception e) {
                Toast.makeText(this, "Formato de fecha inválido (use dd/MM/yyyy)", Toast.LENGTH_SHORT).show();
                return;
            }

            Calendar cal = Calendar.getInstance();
            cal.setTime(fecha);
            int anio = cal.get(Calendar.YEAR);

            String tipoUva = variedad.getText().toString();
            String nombreBodega = bodega.getText().toString();
            Double cantGrado = Double.valueOf(grado.getText().toString());
            double kilosUva;
            try {
                kilosUva = Double.parseDouble(kilos.getText().toString());
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Formato inválido para kilos", Toast.LENGTH_SHORT).show();
                return;
            }

            TierraDAO tierraDAO = new TierraDAO();
            List<TierrasModel> tierras = tierraDAO.obtenerTodasLasTierras(id);
            if (tierras == null || tierras.isEmpty()) {
                Toast.makeText(this, "No hay tierras disponibles", Toast.LENGTH_SHORT).show();
                return;
            }

            int posicionSeleccionada = spinnerTierras.getSelectedItemPosition();
            if (posicionSeleccionada < 0 || posicionSeleccionada >= tierras.size()) {
                Toast.makeText(this, "Seleccione una tierra válida", Toast.LENGTH_SHORT).show();
                return;
            }

            int idTierra = tierras.get(posicionSeleccionada).getId();

            new Thread(() -> {
                try {
                    produccionDAO.insertarProduccionUva(fecha, anio, idTierra, tipoUva, kilosUva, cantGrado, nombreBodega);

                    variedad.setText("");
                    kilos.setText("");
                    bodega.setText("");
                    grado.setText("");

                    runOnUiThread(() -> {
                        Toast.makeText(this, "Producción de uva añadida correctamente", Toast.LENGTH_SHORT).show();
                    });
                } catch (SQLException e) {
                    runOnUiThread(() -> {
                        Log.e("DB_ERROR", "Error al insertar producción", e);
                        Toast.makeText(this, "Error al insertar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
                }
            }).start();

        } catch (Exception e) {
            Log.e("APP_ERROR", "Error inesperado", e);
            Toast.makeText(this, "Error inesperado: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


}