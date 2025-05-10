package com.example.infoleaf;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.infoleaf.adapters.ProduccionAdapter;
import com.example.infoleaf.dao.ProduccionDAO;
import com.example.infoleaf.models.ProduccionModel;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class Produccion extends AppCompatActivity {

    private ExpandableListView expandableListView;
    private ProduccionAdapter adapter;
    private TextView textNoProduccion;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_produccion);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        id = getIntent().getStringExtra("id");

        expandableListView = findViewById(R.id.list_gastos_AP);
        textNoProduccion = findViewById(R.id.textNoProduccion);

        cargarProducciones();
    }

    protected void onResume(){
        super.onResume();
        cargarProducciones();
    }

    private void cargarProducciones() {
        ProduccionDAO produccionDAO = new ProduccionDAO();
        String dniUsuario = id;

        try {
            Map<String, Map<Integer, List<ProduccionModel>>> datos = produccionDAO.obtenerProduccionesPorTipo(dniUsuario);

            if (datos.isEmpty()) {
                expandableListView.setVisibility(View.GONE);
                textNoProduccion.setVisibility(View.VISIBLE);
            } else {
                expandableListView.setVisibility(View.VISIBLE);
                textNoProduccion.setVisibility(View.GONE);

                adapter = new ProduccionAdapter(this, datos);
                expandableListView.setAdapter(adapter);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error cargando producciones", Toast.LENGTH_SHORT).show();
        }
    }

    public void salir(View view) {
        finish();
    }

    public void mostrarPopupBotones(View anchorView) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_opciones_produccion);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);

        }

        Button btnOption1 = dialog.findViewById(R.id.btnUva);
        Button btnOption2 = dialog.findViewById(R.id.btnOlivo);
        Button btnOption3 = dialog.findViewById(R.id.btnCereal);

        btnOption1.setOnClickListener(v -> {
            Intent intent = new Intent(this, AgregarProduccion.class);
            intent.putExtra("tipoProduccion", "uva");
            intent.putExtra("id", id);
            startActivity(intent);
        });

        btnOption2.setOnClickListener(v -> {
            Intent intent = new Intent(this, AgregarProduccion.class);
            intent.putExtra("tipoProduccion", "olivo");
            intent.putExtra("id", id);
            startActivity(intent);
        });

        btnOption3.setOnClickListener(v -> {
            Intent intent = new Intent(this, AgregarProduccion.class);
            intent.putExtra("tipoProduccion", "cereal");
            intent.putExtra("id", id);
            startActivity(intent);
        });
        dialog.show();
    }


}