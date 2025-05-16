package com.example.infoleaf;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.infoleaf.dao.GastosDAO;

import java.util.Calendar;

public class AgregarGasto extends AppCompatActivity {

    private CalendarView calendarView;
    private EditText etDescripcion, etCantidad;
    private long selectedDate;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agregar_gasto);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        id = getIntent().getStringExtra("id");

        calendarView = findViewById(R.id.calendarView_AG);
        Calendar calendar = Calendar.getInstance();
        calendarView.setDate(calendar.getTimeInMillis(), false, true);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar selected = Calendar.getInstance();
            selected.set(year, month, dayOfMonth);
            selectedDate = selected.getTimeInMillis();
        });

        etDescripcion = findViewById(R.id.et_descripcion_AG);
        etCantidad = findViewById(R.id.en_AG);

    }

    public void guardarGasto(View view) {
        String descripcion = etDescripcion.getText().toString().trim();
        String cantidadTexto = etCantidad.getText().toString().trim();

        if (descripcion.isEmpty() || cantidadTexto.isEmpty()) {
            showCustomToast("Complete todos los campos");
            return;
        }

        double cantidad;
        try {

            cantidad = Double.parseDouble(cantidadTexto);
            if(cantidad < 0){
                showCustomToast("Cantidad inválida");
                return;
            }
        } catch (NumberFormatException e) {
            showCustomToast("Cantidad inválida");
            return;
        }

        GastosDAO dao = new GastosDAO();
        try {
            boolean ok = dao.insertarGasto(cantidad, descripcion, new java.util.Date(selectedDate), id);
            if (ok) {
                showCustomToast("Gasto guardado correctamente");
            } else {
                showCustomToast("No se pudo guardar el gasto");
            }
        } catch (Exception e) {
            showCustomToast("Error: " + e.getMessage());
        }
    }

    public void salir(View view) {
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