package com.example.infoleaf;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.infoleaf.dao.UsuarioDAO;

import java.sql.SQLException;

public class MainActivity extends AppCompatActivity {
    private EditText et_nombre;
    private EditText et_contrasena;

    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        this.et_nombre = findViewById(R.id.et_usuarioIS);
        this.et_contrasena = findViewById(R.id.et_contrasenaIS);
    }



    public void mover(View view){
        Intent intent = new Intent(this,  MenuOpciones.class);
        startActivity(intent);
    }

    public void iniciarSesion(View view){
        String nombre = String.valueOf(this.et_nombre.getText());
        String contrasena = String.valueOf(this.et_contrasena.getText());

        if(nombre.isEmpty()){
            Toast.makeText(this, "Debes de rellenar el campo de usuario", Toast.LENGTH_SHORT).show();
            return;
        }

        if(contrasena.isEmpty()){
            Toast.makeText(this, "Debes de rellenar el campo de contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String comprobar = usuarioDAO.iniciarSesion(nombre, contrasena);
            if(comprobar != null){
                Intent intent = new Intent(this, MenuOpciones.class);
                intent.putExtra("comprobar", comprobar);
                startActivity(intent);
            }else{
                Toast.makeText(this, "Nombre o Contraseña incorrctas", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void registrar(View view){
        Intent intent = new Intent(this, Registro.class);
        startActivity(intent);
    }
}