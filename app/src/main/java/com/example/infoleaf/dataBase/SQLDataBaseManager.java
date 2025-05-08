package com.example.infoleaf.dataBase;

import android.os.StrictMode;

import java.sql.Connection;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLDataBaseManager {

    private static Connection conexion = null;
    private static final String DRIVER = "org.postgresql.Driver";
    private static final String URL = "jdbc:postgresql://10.0.2.2/InfoLeafDataBase";
    private static final String USUARIO = "postgres";
    private static final String PASSWORD = "1234";

    public static Connection connect(){
        try{
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Class.forName(DRIVER);
            conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);

        }catch(SQLException e){
            System.out.println("Error en la conexión a BBDD");
            e.printStackTrace();
        }catch(ClassNotFoundException e){
            System.out.println("Error con el DRIVER de la base de datos");
            e.printStackTrace();
        }
        return conexion;
    }

    protected static void disconnect(Connection conexion)throws Exception{
        conexion.close();
    }


}


