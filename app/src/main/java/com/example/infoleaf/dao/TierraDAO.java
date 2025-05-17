package com.example.infoleaf.dao;

import com.example.infoleaf.dataBase.ConexionMethods;
import com.example.infoleaf.models.TerrenosModel;
import com.example.infoleaf.models.TierrasModel;
import com.example.infoleaf.models.TrabajoModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class TierraDAO extends ConexionMethods {

    public List<TierrasModel> obtenerTodasLasTierras(String idUsuario) throws SQLException {
        List<TierrasModel> listaTierras = new ArrayList<>();

        if(!initDBConnection()){
            throw new RuntimeException("Fallo al conectar");
        }
        try{
            String query = "SELECT nombre, id_tierra FROM tierras WHERE id_usuario = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, idUsuario);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                int idTierra = resultSet.getInt("id_tierra");
                String nombre = resultSet.getString("nombre");
                listaTierras.add(new TierrasModel(nombre, idTierra));
            }
        }catch(Exception e){
            throw new SQLException("Error al consultar");
        }finally {
            closeDBConnection();
        }
        return listaTierras;
    }

    public boolean insertarTierraConTerrenos(String nombreTierra, String idUsuario, Integer idPlantacion, boolean disAgua, List<TerrenosModel> terrenos) throws SQLException {
        if (!initDBConnection()) {
            throw new RuntimeException("Fallo al conectar");
        }

        String insertTierraQuery = "INSERT INTO Tierras (Nombre, Id_Usuario, Id_Plantacion, Dis_Agua) VALUES (?, ?, ?, ?) RETURNING Id_Tierra";
        String insertTerrenoQuery = "INSERT INTO Terrenos (Poligono, Num_Parcela, Ubicacion, Superficie) VALUES (?, ?, ?, ?) RETURNING Id";
        String insertTierraTerrenoQuery = "INSERT INTO Tierra_Terreno (Id_Tierra, Id_Terreno) VALUES (?, ?)";

        try {
            connection.setAutoCommit(false);

            // Insertar Tierra
            PreparedStatement psTierra = connection.prepareStatement(insertTierraQuery);
            psTierra.setString(1, nombreTierra);
            psTierra.setString(2, idUsuario);
            if (idPlantacion != null) {
                psTierra.setInt(3, idPlantacion);
            } else {
                psTierra.setNull(3, Types.INTEGER);
            }
            psTierra.setBoolean(4, disAgua);
            ResultSet rsTierra = psTierra.executeQuery();

            int idTierra;
            if (rsTierra.next()) {
                idTierra = rsTierra.getInt("Id_Tierra");
            } else {
                connection.rollback();
                throw new SQLException("Error al insertar Tierra");
            }

            // Insertar terrenos y asociarlos a la Tierra
            for (TerrenosModel terreno : terrenos) {
                PreparedStatement psTerreno = connection.prepareStatement(insertTerrenoQuery);
                psTerreno.setString(1, terreno.getPoligono());
                psTerreno.setString(2, terreno.getNum_parcela());
                psTerreno.setString(3, terreno.getUbicacion());
                psTerreno.setDouble(4, terreno.getSuperficie());
                ResultSet rsTerreno = psTerreno.executeQuery();

                int idTerreno;
                if (rsTerreno.next()) {
                    idTerreno = rsTerreno.getInt("Id");
                } else {
                    connection.rollback();
                    throw new SQLException("Error al insertar Terreno");
                }

                // Insertar relación Tierra-Terreno
                PreparedStatement psTierraTerreno = connection.prepareStatement(insertTierraTerrenoQuery);
                psTierraTerreno.setInt(1, idTierra);
                psTierraTerreno.setInt(2, idTerreno);
                psTierraTerreno.executeUpdate();
            }

            connection.commit();
            return true;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
            closeDBConnection();
        }
    }

    public List<String> obtenerNombresPlantaciones() throws SQLException {
        List<String> nombres = new ArrayList<>();

        if (!initDBConnection()) {
            throw new RuntimeException("Fallo al conectar");
        }

        try {
            String query = "SELECT nombre FROM plantaciones";
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                nombres.add(rs.getString("nombre"));
            }
        } finally {
            closeDBConnection();
        }

        return nombres;
    }


    public Integer obtenerIdPlantacionPorNombre(String nombre) throws SQLException {
        if (!initDBConnection()) {
            throw new RuntimeException("Fallo al conectar");
        }

        try {
            String query = "SELECT id_plantacion FROM plantaciones WHERE nombre = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, nombre);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_plantacion");
            } else {
                return null;
            }
        } finally {
            closeDBConnection();
        }
    }

    public List<TrabajoModel> obtenerTrabajosPorTierra(int idTierra) throws SQLException {
        List<TrabajoModel> listaTrabajos = new ArrayList<>();

        if (!initDBConnection()) {
            throw new RuntimeException("Fallo al conectar");
        }

        try {
            String query = "SELECT t.Id_Trabajo, t.Nombre FROM Tierras ti JOIN Plantacion_Trabajo pt ON ti.Id_Plantacion = pt.Id_Plantacion JOIN Trabajos t ON pt.Id_Trabajo = t.Id_Trabajo WHERE ti.Id_Tierra = ? ";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, idTierra);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int idTrabajo = resultSet.getInt("Id_Trabajo");
                String nombre = resultSet.getString("Nombre");
                listaTrabajos.add(new TrabajoModel(idTrabajo, nombre)); // Asegúrate de tener este modelo
            }

        } catch (Exception e) {
            throw new SQLException("Error al consultar trabajos por tierra", e);
        } finally {
            closeDBConnection();
        }

        return listaTrabajos;
    }

    public boolean eliminarTierra(int idTierra) throws SQLException {
        if (!initDBConnection()) {
            throw new RuntimeException("Fallo al conectar con la base de datos");
        }

        try {
            connection.setAutoCommit(false);

            String deleteTierraTerrenoQuery = "DELETE FROM Tierra_Terreno WHERE Id_Tierra = ?";
            PreparedStatement psTierraTerreno = connection.prepareStatement(deleteTierraTerrenoQuery);
            psTierraTerreno.setInt(1, idTierra);
            psTierraTerreno.executeUpdate();

            String deleteTierraQuery = "DELETE FROM Tierras WHERE Id_Tierra = ?";
            PreparedStatement psTierra = connection.prepareStatement(deleteTierraQuery);
            psTierra.setInt(1, idTierra);

            int filasAfectadas = psTierra.executeUpdate();

            connection.commit();

            return filasAfectadas > 0;

        } catch (SQLException e) {
            connection.rollback();
            throw new SQLException("Error al eliminar la tierra y sus relaciones: " + e.getMessage());
        } finally {
            connection.setAutoCommit(true);
            closeDBConnection();
        }
    }

}
