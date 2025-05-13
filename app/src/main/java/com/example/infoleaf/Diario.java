package com.example.infoleaf;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.infoleaf.dao.DiarioDAO;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Diario extends AppCompatActivity {
    private TextInputEditText datePickerEditText;
    private String id;
    private String formattedDate;
    private TextView tvDescripcion;

    private DiarioDAO diarioDAO = new DiarioDAO();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_diario);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        id = getIntent().getStringExtra("id");
        tvDescripcion = findViewById(R.id.tv_descripcion);

        configurarDatePicker();
    }

    public void salir(View view) {
        finish();
    }

    private void configurarDatePicker() {
        datePickerEditText = findViewById(R.id.DP_fechas);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date hoy = new Date();

        formattedDate = dateFormat.format(hoy);
        datePickerEditText.setText(formattedDate);

        datePickerEditText.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Selecciona una fecha")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build();

            datePicker.addOnPositiveButtonClickListener(selectedDate -> {
                formattedDate = dateFormat.format(new Date(selectedDate));
                datePickerEditText.setText(formattedDate);
            });

            datePicker.show(getSupportFragmentManager(), "DATE_PICKER_TAG");
        });
    }


    public void descripcionDelDia(View view){
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date fecha = null;
        try {
            fecha = formato.parse(formattedDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        try {
            tvDescripcion.setText(diarioDAO.obtenerDescripcionPorFechaYUsuario(fecha, id));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}