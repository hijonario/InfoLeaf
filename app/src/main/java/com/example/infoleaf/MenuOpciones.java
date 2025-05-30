package com.example.infoleaf;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.infoleaf.dao.UsuarioDAO;

public class MenuOpciones extends AppCompatActivity {
    private TextView nombreUsuario;
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    private String id;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_opciones);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        id = getIntent().getStringExtra("comprobar");

        nombreUsuario = findViewById(R.id.tv_nombreMO);

        nombreUsuario.setText(usuarioDAO.obtenerNombre(id));
    }

    public void irTerrenos (View view){
        Intent intent = new Intent(this, Terrenos.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    public void irDiario (View view){
        Intent intent = new Intent(this, MoverPaneles.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    public void irGastos (View view){
        Intent intent = new Intent(this, Gastos.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    public void irProduccion (View view){
        Intent intent = new Intent(this, Produccion.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    public void cerrarSesion (View view){
        finish();
    }
}