package com.example.infoleaf.dao;

import com.example.infoleaf.dataBase.ConexionMethods;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class DiarioDAO extends ConexionMethods {
    public boolean insertarOActualizarEntradaDiario(String descripcion, java.util.Date fecha, String idUsuario) throws SQLException {
        if (!initDBConnection()) {
            throw new RuntimeException("Error al conectar con la base de datos");
        }

        boolean resultado = false;
        String selectQuery = "SELECT Id FROM Diario WHERE Fecha = ? AND Id_Usuario = ?";
        String insertQuery = "INSERT INTO Diario (Fecha, Id_Usuario, Descripcion) VALUES (?, ?, ?)";
        String updateQuery = "UPDATE Diario SET Descripcion = ? WHERE Fecha = ? AND Id_Usuario = ?";

        try {
            PreparedStatement selectStmt = connection.prepareStatement(selectQuery);
            selectStmt.setDate(1, new java.sql.Date(fecha.getTime()));
            selectStmt.setString(2, idUsuario);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
                updateStmt.setString(1, descripcion);
                updateStmt.setDate(2, new java.sql.Date(fecha.getTime()));
                updateStmt.setString(3, idUsuario);
                resultado = updateStmt.executeUpdate() > 0;
            } else {
                PreparedStatement insertStmt = connection.prepareStatement(insertQuery);
                insertStmt.setDate(1, new java.sql.Date(fecha.getTime()));
                insertStmt.setString(2, idUsuario);
                insertStmt.setString(3, descripcion);
                resultado = insertStmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            throw new SQLException("Error al insertar/actualizar en Diario: " + e.getMessage());
        } finally {
            closeDBConnection();
        }

        return resultado;
    }

    public String obtenerDescripcionPorFechaYUsuario(Date fecha, String idUsuario) throws SQLException {
        if (!initDBConnection()) {
            throw new RuntimeException("Error al conectar con la base de datos");
        }

        String descripcion = null;
        String query = "SELECT Descripcion FROM Diario WHERE Fecha = ? AND Id_Usuario = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setDate(1, new java.sql.Date(fecha.getTime()));
            statement.setString(2, idUsuario);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                descripcion = rs.getString("Descripcion");
            }

        } catch (Exception e) {
            throw new SQLException("Error al obtener la descripción: " + e.getMessage());
        } finally {
            closeDBConnection();
        }

        return descripcion;
    }

}
