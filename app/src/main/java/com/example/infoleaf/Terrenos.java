package com.example.infoleaf;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.infoleaf.adapters.ExpandableListAdapter;
import com.example.infoleaf.dao.TerrenoDAO;
import com.example.infoleaf.dao.TierraDAO;
import com.example.infoleaf.models.TerrenosModel;
import com.example.infoleaf.models.TierrasModel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Terrenos extends AppCompatActivity {
    private ExpandableListView expandableListView;
    private ExpandableListAdapter listAdapter;
    private List<String> listTierras;
    private HashMap<String, List<TerrenosModel>> listTerrenos;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_terrenos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        id = getIntent().getStringExtra("id");

        expandableListView = findViewById(R.id.list_parcela_AT);

        cargarDatos();
    }

    protected void onResume(){
        super.onResume();
        cargarDatos();
    }

    private void cargarDatos() {
        TierraDAO tierraDAO = new TierraDAO();
        TerrenoDAO terrenoDAO = new TerrenoDAO();

        listTierras = new ArrayList<>();
        listTerrenos = new HashMap<>();
        TextView textNoTierras = findViewById(R.id.textNoTierras);

        try {
            String idUsuario = id;
            List<TierrasModel> tierras = tierraDAO.obtenerTodasLasTierras(idUsuario);

            if (tierras == null || tierras.isEmpty()) {

                textNoTierras.setVisibility(View.VISIBLE);
                expandableListView.setVisibility(View.GONE);
            } else {

                textNoTierras.setVisibility(View.GONE);
                expandableListView.setVisibility(View.VISIBLE);

                for (TierrasModel tierra : tierras) {
                    listTierras.add(tierra.getNombre());


                    List<TerrenosModel> terrenos = terrenoDAO.obtenerTerrenos(tierra.getId());
                    listTerrenos.put(tierra.getNombre(), terrenos);
                }


                listAdapter = new ExpandableListAdapter(this, listTierras, listTerrenos);
                expandableListView.setAdapter(listAdapter);
            }
        } catch (SQLException e) {
            Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
        }
    }

    public void salir (View view){
        finish();
    }

    public void irAgregarTerreno(View view){
        Intent intent = new Intent(this, AgregarTerreno.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

}