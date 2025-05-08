package com.example.infoleaf.dataBase;

import java.sql.Connection;
import java.sql.SQLException;

public class ConexionMethods {
    protected Connection connection;

    protected boolean initDBConnection(){
        connection = SQLDataBaseManager.connect();
        return true;
    }

    protected boolean closeDBConnection(){
        try {
            SQLDataBaseManager.disconnect(connection);
            return true;
        } catch (SQLException e) {
            System.err.println("Error al desconectar con la base de datos");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}
