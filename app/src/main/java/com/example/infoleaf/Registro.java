package com.example.infoleaf;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
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
            showCustomToast("Debes de rellenar el campo de nombre");
            return;
        }

        if (dni.isEmpty()){
            showCustomToast("Debes de rellenar el campo de dni");
            return;
        }

        if (!dni.matches("^[0-9]{8}[A-Z]$")) {
            showCustomToast("El DNI debe contener 8 números seguidos de una letra mayúscula");
            return;
        }

        if (contrasena.isEmpty()){
            showCustomToast("Debes de rellenar el campo de contraseña");
            return;
        }

        if (email.isEmpty()){
            showCustomToast("Debes de rellenar el campo de email");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
            showCustomToast("El formato del correo no es válido");
            return;
        }

        if (telefono.isEmpty()){
            showCustomToast("Debes de rellenar el campo de telefono");
            return;
        }

        try {
            int i = usuarioDAO.registrar(nombre, dni, contrasena, telefono, email);
            if(i == 1){
                showCustomToast("Nombre y contraseña ya utilizada, modifica alguno de esos campos");
            }else if(i == 2){
                showCustomToast("El DNI ya está en uso");
            }else if(i == 0){
                showCustomToast("Cuenta creada con éxito");
            }
        } catch (SQLException e) {
            showCustomToast("Error al registrar usuario");
            throw new RuntimeException(e);
        }

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
