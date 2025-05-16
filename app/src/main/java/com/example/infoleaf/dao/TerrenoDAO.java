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
            String query = "SELECT t.Id, t.Poligono, t.Num_Parcela, t.Ubicacion, t.Superficie " +
                    "FROM Terrenos t JOIN Tierra_Terreno tt ON t.Id = tt.Id_Terreno " +
                    "WHERE tt.Id_Tierra = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, idTierra);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                int id = resultSet.getInt("id");
                String poligono = resultSet.getString("poligono");
                String numParcela = resultSet.getString("num_parcela");
                String ubicacion = resultSet.getString("ubicacion");
                double superficie = resultSet.getDouble("superficie");

                listaTerrenos.add(new TerrenosModel(id,superficie, ubicacion,numParcela,poligono));
            }
        }catch(Exception e){
            throw new SQLException("Error al consultar");
        }finally {
            closeDBConnection();
        }


        return listaTerrenos;
    }

    public boolean eliminarTerreno(TerrenosModel terreno) throws SQLException {
        if (!initDBConnection()) return false;

        try {
            String query = "DELETE FROM terrenos WHERE poligono = ? AND num_parcela = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, terreno.getPoligono());
            ps.setString(2, terreno.getNum_parcela());
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } finally {
            closeDBConnection();
        }
    }


}
