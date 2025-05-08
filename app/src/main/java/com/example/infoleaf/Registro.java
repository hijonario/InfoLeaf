package com.example.infoleaf;

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

public class Registro extends AppCompatActivity {
    private EditText et_nombre;
    private EditText et_contrasena;
    private EditText et_dni;
    private EditText et_email;
    private EditText et_telefono;

    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        this.et_nombre = findViewById(R.id.et_nombre);
        this.et_dni = findViewById(R.id.et_dni);
        this.et_contrasena = findViewById(R.id.et_contrasenaIS);
        this.et_email = findViewById(R.id.et_email);
        this.et_telefono = findViewById(R.id.et_telefono);
    }

    public void iniciarSesion(View view){
        finish();
    }

    public void registrarse(View view){
        String nombre = String.valueOf(this.et_nombre.getText());
        String dni = String.valueOf(this.et_dni.getText());
        String contrasena = String.valueOf(this.et_contrasena.getText());
        String email = String.valueOf(this.et_email.getText());
        String telefono = String.valueOf(this.et_telefono.getText());

        if (nombre.isEmpty()){
            Toast.makeText(this, "Debes de rellenar el campo de nombre", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dni.isEmpty()){
            Toast.makeText(this, "Debes de rellenar el campo de dni", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!dni.matches("^[0-9]{8}[A-Z]$")) {
            Toast.makeText(this, "El DNI debe contener 8 números seguidos de una letra mayúscula", Toast.LENGTH_SHORT).show();
            return;
        }

        if (contrasena.isEmpty()){
            Toast.makeText(this, "Debes de rellenar el campo de contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.isEmpty()){
            Toast.makeText(this, "Debes de rellenar el campo de email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
            Toast.makeText(this, "El DNI debe contener 8 números seguidos de una letra mayúscula", Toast.LENGTH_SHORT).show();
            return;
        }

        if (telefono.isEmpty()){
            Toast.makeText(this, "Debes de rellenar el campo de telefono", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int i = usuarioDAO.registrar(nombre, dni, contrasena, telefono, email);
            if(i == 1){
                Toast.makeText(this, "Nombre y contraseña ya utilizada, modifica alguno de esos campos", Toast.LENGTH_LONG).show();
            }else if(i == 2){
                Toast.makeText(this, "El DNI utilizada ya está en uso", Toast.LENGTH_LONG).show();
            }else if(i == 0){
                Toast.makeText(this, "Cuenta creada con exito", Toast.LENGTH_LONG).show();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        finish();
    }
}