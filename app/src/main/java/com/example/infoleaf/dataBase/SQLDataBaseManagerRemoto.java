package com.example.infoleaf.dataBase;

import android.os.StrictMode;
import android.util.Log;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class SQLDataBaseManagerRemoto {
    private static Connection conexion = null;

    private static final String DRIVER = "org.postgresql.Driver";
    private static final String HOST = "ep-muddy-salad-a2ux3hc6-pooler.eu-central-1.aws.neon.tech";
    private static final int PORT = 5432;
    private static final String DATABASE = "neondb";
    private static final String USER = "neondb_owner";
    private static final String PASSWORD = "npg_UzrNy25camwW"; // reemplaza esto
    private static final String URL = "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DATABASE;

    public static Connection connect() {
        try {
            // Permitir red en el hilo principal (solo para pruebas, no recomendado en producción)
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            // Registrar el driver
            Class.forName(DRIVER);

            // Establecer conexión
            conexion = DriverManager.getConnection(URL, USER, PASSWORD);

            System.out.println("Conexión exitosa a Neon PostgreSQL.");
        } catch (SQLException e) {
            System.out.println("Error al conectar a la base de datos Neon:");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("No se encontró el driver PostgreSQL:");
            e.printStackTrace();
        }
        return conexion;
    }

    public static void disconnect() {
        if (conexion != null) {
            try {
                conexion.close();
                System.out.println("Conexión cerrada.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
