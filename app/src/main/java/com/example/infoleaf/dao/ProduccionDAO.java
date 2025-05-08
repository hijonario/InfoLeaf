package com.example.infoleaf.dao;

import com.example.infoleaf.dataBase.ConexionMethods;
import com.example.infoleaf.models.ProduccionModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProduccionDAO extends ConexionMethods {

    public Map<String, Map<Integer, List<ProduccionModel>>> obtenerProduccionesPorTipo(String dniUsuario) throws SQLException {
        Map<String, Map<Integer, List<ProduccionModel>>> produccionesPorTipo = new LinkedHashMap<>();

        if (!initDBConnection()) {
            throw new RuntimeException("Fallo al conectar con la base de datos");
        }

        try {
            cargarCereal(dniUsuario, produccionesPorTipo);
            cargarAceituna(dniUsuario, produccionesPorTipo);
            cargarVina(dniUsuario, produccionesPorTipo);
        } catch (Exception e) {
            throw new SQLException("Error al consultar las producciones", e);
        } finally {
            closeDBConnection();
        }

        return produccionesPorTipo;
    }

    private void cargarCereal(String dniUsuario, Map<String, Map<Integer, List<ProduccionModel>>> mapa) throws SQLException {
        String query = "SELECT C.Tipo_Cereal, C.Kilos, P.Anio, T.Nombre " +
                "FROM Cereal C " +
                "JOIN Produccion P ON C.Id_Cereal = P.Id " +
                "JOIN Tierras T ON P.Id_Tierra = T.Id_Tierra " +
                "JOIN Usuarios U ON T.Id_Usuario = U.DNI " +
                "WHERE U.DNI = ? " +
                "ORDER BY P.Anio DESC";

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, dniUsuario);
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            String tipo = "Cereal";
            int anio = resultSet.getInt("Anio");
            String nombreTierra = resultSet.getString("Nombre");
            String detalles = "Tipo: " + resultSet.getString("Tipo_Cereal") +
                    ", Kilos: " + resultSet.getDouble("Kilos");

            ProduccionModel item = new ProduccionModel(nombreTierra, detalles);

            mapa.computeIfAbsent(tipo, k -> new LinkedHashMap<>());
            mapa.get(tipo).computeIfAbsent(anio, k -> new ArrayList<>());
            mapa.get(tipo).get(anio).add(item);
        }
    }

    private void cargarAceituna(String dniUsuario, Map<String, Map<Integer, List<ProduccionModel>>> mapa) throws SQLException {
        String query = "SELECT A.Kilo_Arbol, A.Kilo_Suelo, P.Anio, T.Nombre " +
                "FROM Aceituna A " +
                "JOIN Produccion P ON A.Id_Aceituna = P.Id " +
                "JOIN Tierras T ON P.Id_Tierra = T.Id_Tierra " +
                "JOIN Usuarios U ON T.Id_Usuario = U.DNI " +
                "WHERE U.DNI = ? " +
                "ORDER BY P.Anio DESC";

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, dniUsuario);
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            String tipo = "Aceituna";
            int anio = resultSet.getInt("Anio");
            String nombreTierra = resultSet.getString("Nombre");
            String detalles = "Kilo Árbol: " + resultSet.getDouble("Kilo_Arbol") +
                    ", Kilo Suelo: " + resultSet.getDouble("Kilo_Suelo");

            ProduccionModel item = new ProduccionModel(nombreTierra, detalles);

            mapa.computeIfAbsent(tipo, k -> new LinkedHashMap<>());
            mapa.get(tipo).computeIfAbsent(anio, k -> new ArrayList<>());
            mapa.get(tipo).get(anio).add(item);
        }
    }

    private void cargarVina(String dniUsuario, Map<String, Map<Integer, List<ProduccionModel>>> mapa) throws SQLException {
        String query = "SELECT V.Variedad, V.Kilos, V.Grado, V.Kilogrado, V.Bodega, P.Anio, T.Nombre " +
                "FROM Vina V " +
                "JOIN Produccion P ON V.Id_Uva = P.Id " +
                "JOIN Tierras T ON P.Id_Tierra = T.Id_Tierra " +
                "JOIN Usuarios U ON T.Id_Usuario = U.DNI " +
                "WHERE U.DNI = ? " +
                "ORDER BY P.Anio DESC";

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, dniUsuario);
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            String tipo = "Viña";
            int anio = resultSet.getInt("Anio");
            String nombreTierra = resultSet.getString("Nombre");
            String detalles = "Variedad: " + resultSet.getString("Variedad") +
                    ", Kilos: " + resultSet.getDouble("Kilos") +
                    ", Grado: " + resultSet.getDouble("Grado") +
                    ", Kilogrado: " + resultSet.getDouble("Kilogrado") +
                    ", Bodega: " + resultSet.getString("Bodega");

            ProduccionModel item = new ProduccionModel(nombreTierra, detalles);

            mapa.computeIfAbsent(tipo, k -> new LinkedHashMap<>());
            mapa.get(tipo).computeIfAbsent(anio, k -> new ArrayList<>());
            mapa.get(tipo).get(anio).add(item);
        }
    }
}
