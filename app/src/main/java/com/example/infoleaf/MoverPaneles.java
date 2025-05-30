package com.example.infoleaf;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.infoleaf.dao.DiarioDAO;
import com.example.infoleaf.dao.TierraDAO;
import com.example.infoleaf.models.TierrasModel;
import com.example.infoleaf.models.TrabajoModel;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MoverPaneles extends AppCompatActivity {
    private TextInputEditText datePickerEditText;
    private Spinner spinnerTierras;
    private String id;

    private String tierraSeleccionadaAnterior = "";
    private List<String> bloquesAnteriores = new ArrayList<>();

    private TextView tvDescripcion;
    private Spinner spinner;

    private ScrollView leftScrollContainer;
    private ScrollView rightScrollContainer;
    private LinearLayout leftContainer;
    private LinearLayout rightContainer;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mover_paneles);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        id = getIntent().getStringExtra("id");

        // Cambiamos a ScrollView y obtenemos los LinearLayout internos
        leftScrollContainer = findViewById(R.id.left_scroll_container);
        rightScrollContainer = findViewById(R.id.right_scroll_container);
        leftContainer = (LinearLayout) leftScrollContainer.getChildAt(0);
        rightContainer = (LinearLayout) rightScrollContainer.getChildAt(0);

        tvDescripcion = findViewById(R.id.tv_descripcion);
        spinner = findViewById(R.id.combo_tierras);

    //            TextView block1 = findViewById(R.id.block_1);
    //            TextView block2 = findViewById(R.id.block_2);

        configurarDatePicker();
        configurarSpinnerTierras();

        // Listener para iniciar el arrastre
        View.OnLongClickListener dragStartListener = view -> {
            ClipData clipData = ClipData.newPlainText("label", ((TextView) view).getText());
            View.DragShadowBuilder dragShadow = new View.DragShadowBuilder(view);
            view.startDragAndDrop(clipData, dragShadow, view, 0);
            return true;
        };

    //            block1.setOnLongClickListener(dragStartListener);
    //            block2.setOnLongClickListener(dragStartListener);

        // Listener para manejar el evento de arrastre
        View.OnDragListener dragListener = (view, event) -> {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return true;

                case DragEvent.ACTION_DRAG_ENTERED:
                    view.setBackgroundResource(android.R.color.holo_blue_light);
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:
                    view.setBackgroundResource(android.R.color.transparent);
                    return true;

                case DragEvent.ACTION_DROP:
                    View draggedView = (View) event.getLocalState();

                    // Asegurarse de que el elemento arrastrado se elimine de su contenedor anterior
                    LinearLayout owner = (LinearLayout) draggedView.getParent();
                    owner.removeView(draggedView);

                    // Agregar el bloque al nuevo contenedor
                    LinearLayout container = (LinearLayout) view;
                    container.addView(draggedView);

                    // Reiniciar el listener para que el bloque pueda ser movido nuevamente
                    draggedView.setOnLongClickListener(dragStartListener);

                    view.setBackgroundResource(android.R.color.transparent);
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:
                    view.setBackgroundResource(android.R.color.transparent);
                    return true;

                default:
                    return false;
            }
        };

        leftContainer.setOnDragListener(dragListener);
        rightContainer.setOnDragListener(dragListener);
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
                    cargarTrabajosEnBloques(idTierraSeleccionada);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    private void cargarTrabajosEnBloques(int idTierra) {
        leftContainer.removeAllViews();

        TierraDAO tierraDAO = new TierraDAO();
        try {
            List<TrabajoModel> trabajos = tierraDAO.obtenerTrabajosPorTierra(idTierra);

            for (TrabajoModel trabajo : trabajos) {
                TextView bloque = new TextView(this);
                bloque.setText(trabajo.getNombre());
                bloque.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
                bloque.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                bloque.setPadding(20, 20, 20, 20);
                bloque.setTextSize(16);
                bloque.setBackgroundResource(R.drawable.fondo_boton);

                Typeface tipoFuente = ResourcesCompat.getFont(this, R.font.poppins_medium);
                bloque.setTypeface(tipoFuente);
                bloque.setPadding(bloque.getPaddingLeft(), 0, bloque.getPaddingRight(), bloque.getPaddingBottom());

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

                int marginBottomPx = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()
                );

                params.setMargins(0, 0, 0, marginBottomPx);
                bloque.setLayoutParams(params);

                bloque.setOnLongClickListener(view -> {
                    ClipData clipData = ClipData.newPlainText("label", ((TextView) view).getText());
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                    view.startDragAndDrop(clipData, shadowBuilder, view, 0);
                    return true;
                });

                leftContainer.addView(bloque);
            }
        } catch (SQLException e) {
            showCustomToast("Error al cargar trabajos");
        }
    }



    public void escribirDescripcion(View view) {
        LinearLayout rightContainer = findViewById(R.id.right_container);
        String tierraActual = spinner.getSelectedItem().toString();

        List<String> bloquesActuales = new ArrayList<>();
        for (int i = 0; i < rightContainer.getChildCount(); i++) {
            View v = rightContainer.getChildAt(i);
            if (v instanceof TextView) {
                bloquesActuales.add(((TextView) v).getText().toString());
            }
        }

        if (bloquesActuales.isEmpty()) {
            return;
        }

        String textoPrevio = tvDescripcion.getText().toString();
        StringBuilder textoActual = new StringBuilder(textoPrevio);

        boolean descripcionIncompleta = !textoPrevio.contains("En la parcela " + tierraActual);

        if (!tierraActual.equals(tierraSeleccionadaAnterior) || descripcionIncompleta) {
            if (!textoActual.toString().isEmpty()) {
                textoActual.append("\n\n");
            }

            textoActual.append("En la parcela ")
                    .append(tierraActual)
                    .append(" se han realizado los siguientes trabajos: ");

            for (String bloque : bloquesActuales) {
                if (bloque != null) {
                    textoActual.append(bloque).append(", ");
                }
            }

            textoActual.setLength(textoActual.length() - 2);

            bloquesAnteriores = new ArrayList<>(bloquesActuales);
            tierraSeleccionadaAnterior = tierraActual;
        } else {
            for (String bloque : bloquesActuales) {
                if (!bloquesAnteriores.contains(bloque)) {
                    textoActual.append(" ").append(bloque);
                    bloquesAnteriores.add(bloque);
                }
            }
        }
        tvDescripcion.setText(textoActual.toString());
    }


    public void agregarEntradaDiario(View view) {
        TextView descripcionView = findViewById(R.id.tv_descripcion);
        String descripcion = descripcionView.getText().toString();

        if (descripcion.isEmpty()) {
            showCustomToast("La descripción está vacía");
            return;
        }

        java.util.Date fecha;
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            fecha = format.parse(datePickerEditText.getText().toString());
        } catch (Exception e) {
            showCustomToast("Fecha inválida");
            return;
        }

        DiarioDAO diarioDAO = new DiarioDAO();
        try {
            boolean insertado = diarioDAO.insertarOActualizarEntradaDiario(descripcion, fecha, id);
            if (insertado) {
                this.tvDescripcion.setText("");
                rightContainer.removeAllViews();
                showCustomToast("Entrada agregada correctamente");
            } else {
                showCustomToast("No se pudo insertar la entrada");
            }
        } catch (SQLException e) {
            showCustomToast("Error: " + e.getMessage());
        }
    }

    public void irVistaDiario (View view){
        Intent intent = new Intent(this, Diario.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }


    public void limpiar(View view){
        this.tvDescripcion.setText("");
        rightContainer.removeAllViews();
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