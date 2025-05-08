package com.example.infoleaf.dao;

import com.example.infoleaf.dataBase.ConexionMethods;
import com.example.infoleaf.models.GastoModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GastosDAO extends ConexionMethods {
    public Map<String, List<GastoModel>> obtenerGastosPorAnio(String dniUsuario) throws SQLException {
        Map<String, List<GastoModel>> gastosPorAnio = new LinkedHashMap<>();

        if (!initDBConnection()) {
            throw new RuntimeException("Fallo al conectar con la base de datos");
        }

        try {
            String query = "SELECT EXTRACT(YEAR FROM fecha) AS anio, dinero, descripcion, fecha " +
                    "FROM gastos WHERE id_usuario = ? " +
                    "ORDER BY anio DESC, fecha ASC";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, dniUsuario);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String anio = resultSet.getString("anio");
                double dinero = resultSet.getDouble("dinero");
                String descripcion = resultSet.getString("descripcion");
                String fecha = resultSet.getDate("fecha").toString(); // yyyy-MM-dd

                GastoModel gasto = new GastoModel(dinero, descripcion, fecha);

                if (!gastosPorAnio.containsKey(anio)) {
                    gastosPorAnio.put(anio, new ArrayList<>());
                }
                gastosPorAnio.get(anio).add(gasto);
            }

        } catch (Exception e) {
            throw new SQLException("Error al consultar los gastos", e);
        } finally {
            closeDBConnection();
        }

        return gastosPorAnio;
    }

    public boolean insertarGasto(double dinero, String descripcion, java.util.Date fecha, String idUsuario) throws SQLException {
        if (!initDBConnection()) {
            throw new RuntimeException("Error al conectar con la base de datos");
        }

        boolean insertado = false;
        String query = "INSERT INTO Gastos (Dinero, Descripcion, Fecha, Id_Usuario) VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setDouble(1, dinero);
            statement.setString(2, descripcion);
            statement.setDate(3, new java.sql.Date(fecha.getTime()));
            statement.setString(4, idUsuario);

            insertado = statement.executeUpdate() > 0;

        } catch (Exception e) {
            throw new SQLException("Error al insertar el gasto: " + e.getMessage());
        } finally {
            closeDBConnection();
        }

        return insertado;
    }
}
