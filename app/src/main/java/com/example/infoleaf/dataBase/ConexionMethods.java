package com.example.infoleaf.dataBase;

import java.sql.Connection;
import java.sql.SQLException;

public class ConexionMethods {
    protected Connection connection;

    protected boolean initDBConnection(){
        connection = SQLDataBaseManagerRemoto.connect();
        return true;
    }

    protected boolean closeDBConnection(){
        try {
            SQLDataBaseManagerRemoto.disconnect();
            return true;
        }  catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
