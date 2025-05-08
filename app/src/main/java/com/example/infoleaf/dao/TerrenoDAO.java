package com.example.infoleaf.dao;

import com.example.infoleaf.dataBase.ConexionMethods;
import com.example.infoleaf.models.TerrenosModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TerrenoDAO extends ConexionMethods {

    public List<TerrenosModel> obtenerTerrenos(int idTierra) throws SQLException {
        List<TerrenosModel> listaTerrenos = new ArrayList<>();

        if(!initDBConnection()){
            throw new RuntimeException("Fallo al conectar");
        }
        try{
            String query = "SELECT t.poligono, t.num_parcela, t.ubicacion, t.superficie " +
                    "FROM terrenos t " +
                    "JOIN tierra_terreno tt ON t.id = tt.id_terreno " +
                    "WHERE tt.id_tierra = ? ";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, idTierra);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                String poligono = resultSet.getString("poligono");
                String numParcela = resultSet.getString("num_parcela");
                String ubicacion = resultSet.getString("ubicacion");
                double superficie = resultSet.getDouble("superficie");

                listaTerrenos.add(new TerrenosModel(superficie, ubicacion,numParcela,poligono));
            }
        }catch(Exception e){
            throw new SQLException("Error al consultar");
        }finally {
            closeDBConnection();
        }


        return listaTerrenos;
    }

}
