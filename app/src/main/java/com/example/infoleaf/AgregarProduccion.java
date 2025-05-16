package com.example.infoleaf;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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
                showCustomToast("El tipo de cereal es requerido");
                return;
            }

            if (kilos.getText().toString().isEmpty()) {
                showCustomToast("Los kilos son requeridos");
                return;
            }

            if (datePickerEditText.getText().toString().isEmpty()) {
                showCustomToast("La fecha es requerida");
                return;
            }

            java.util.Date fecha;
            try {
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                fecha = format.parse(datePickerEditText.getText().toString());
            } catch (Exception e) {
                showCustomToast("Formato de fecha inválido (use dd/MM/yyyy)");
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
                showCustomToast("Formato inválido para kilos");
                return;
            }

            TierraDAO tierraDAO = new TierraDAO();
            List<TierrasModel> tierras = tierraDAO.obtenerTodasLasTierras(id);
            if (tierras == null || tierras.isEmpty()) {
                showCustomToast("No hay tierras disponibles");
                return;
            }

            int posicionSeleccionada = spinnerTierras.getSelectedItemPosition();
            if (posicionSeleccionada < 0 || posicionSeleccionada >= tierras.size()) {
                showCustomToast("Seleccione una tierra válida");
                return;
            }

            int idTierra = tierras.get(posicionSeleccionada).getId();

            new Thread(() -> {
                try {
                    produccionDAO.insertarProduccionCereal(fecha, anio, idTierra, tipoCereal, kilosCereal);

                    variedad.setText("");
                    kilos.setText("");

                    runOnUiThread(() -> {
                        showCustomToast("Producción de cereal añadida correctamente");
                    });
                } catch (SQLException e) {
                    runOnUiThread(() -> {
                        Log.e("DB_ERROR", "Error al insertar producción", e);
                        showCustomToast("Error al insertar: " + e.getMessage());
                    });
                }
            }).start();

        } catch (Exception e) {
            Log.e("APP_ERROR", "Error inesperado", e);
            showCustomToast("Error inesperado: " + e.getMessage());
        }
    }

    public void insertarOlivo(View view) {
        try {
            if (variedad.getText().toString().isEmpty()) {
                showCustomToast("El tipo de aceituna es requerido");
                return;
            }

            if (kilosArbol.getText().toString().isEmpty()) {
                showCustomToast("Los kilos arbol son requeridos");
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
                showCustomToast("Formato de fecha inválido (use dd/MM/yyyy)");
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
                showCustomToast("Formato inválido para kilos");
                return;
            }

            TierraDAO tierraDAO = new TierraDAO();
            List<TierrasModel> tierras = tierraDAO.obtenerTodasLasTierras(id);
            if (tierras == null || tierras.isEmpty()) {
                showCustomToast("No hay tierras disponibles");
                return;
            }

            int posicionSeleccionada = spinnerTierras.getSelectedItemPosition();
            if (posicionSeleccionada < 0 || posicionSeleccionada >= tierras.size()) {
                showCustomToast("Seleccione una tierra válida");
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
                        showCustomToast("Producción de aceituna añadida correctamente");
                    });
                } catch (SQLException e) {
                    runOnUiThread(() -> {
                        Log.e("DB_ERROR", "Error al insertar producción", e);
                        showCustomToast("Error al insertar: " + e.getMessage());
                    });
                }
            }).start();

        } catch (Exception e) {
            Log.e("APP_ERROR", "Error inesperado", e);
            showCustomToast("Error inesperado: " + e.getMessage());
        }
    }

    public void insertarUva(View view) {
        try {
            if (variedad.getText().toString().isEmpty()) {
                showCustomToast("El tipo de uva es requerido");
                return;
            }

            if (kilos.getText().toString().isEmpty()) {
                showCustomToast("Los kilos son requeridos");
                return;
            }

            if (datePickerEditText.getText().toString().isEmpty()) {
                showCustomToast("La fecha es requerida");
                return;
            }

            if (bodega.getText().toString().isEmpty()){
                showCustomToast("La bodega es requerida");
                return;
            }

            if (grado.getText().toString().isEmpty()){
                showCustomToast("El grado es requerido");
                return;
            }

            java.util.Date fecha;
            try {
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                fecha = format.parse(datePickerEditText.getText().toString());
            } catch (Exception e) {
                showCustomToast("Formato de fecha inválido (use dd/MM/yyyy)");
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
                showCustomToast("Formato inválido para kilos");
                return;
            }

            TierraDAO tierraDAO = new TierraDAO();
            List<TierrasModel> tierras = tierraDAO.obtenerTodasLasTierras(id);
            if (tierras == null || tierras.isEmpty()) {
                showCustomToast("No hay tierras disponibles");
                return;
            }

            int posicionSeleccionada = spinnerTierras.getSelectedItemPosition();
            if (posicionSeleccionada < 0 || posicionSeleccionada >= tierras.size()) {
                showCustomToast("Seleccione una tierra válida");
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
                        showCustomToast("Producción de uva añadida correctamente");
                    });
                } catch (SQLException e) {
                    runOnUiThread(() -> {
                        Log.e("DB_ERROR", "Error al insertar producción", e);
                        showCustomToast("Error al insertar: " + e.getMessage());
                    });
                }
            }).start();

        } catch (Exception e) {
            Log.e("APP_ERROR", "Error inesperado", e);
            showCustomToast("Error inesperado: " + e.getMessage());
        }
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