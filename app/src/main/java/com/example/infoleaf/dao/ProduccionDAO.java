package com.example.infoleaf.dao;

import com.example.infoleaf.dataBase.ConexionMethods;
import com.example.infoleaf.models.ProduccionModel;

import java.sql.Date;
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
        String query = "SELECT A.Kilo_Arbol, A.Kilo_Suelo, A.variedad, P.Anio, T.Nombre " +
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
                    ", Kilo Suelo: " + resultSet.getDouble("Kilo_Suelo") +
                    ", Variedad: "+ resultSet.getString("variedad");

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

    public void insertarProduccionUva(java.util.Date fecha, int anio, int idTierra, String variedad, double kilos, double grado, String bodega) throws SQLException {
        if (!initDBConnection()) {
            throw new RuntimeException("Error al conectar con la base de datos");
        }
        String queryProduccion = "INSERT INTO Produccion (Fecha, Anio, Id_Tierra) VALUES (?, ?, ?) RETURNING Id";

        try {
            PreparedStatement psProduccion = connection.prepareStatement(queryProduccion);
            psProduccion.setDate(1, new java.sql.Date(fecha.getTime()));
            psProduccion.setInt(2, anio);
            psProduccion.setInt(3, idTierra);

            ResultSet rs = psProduccion.executeQuery();
            if (rs.next()) {
                int idProduccion = rs.getInt(1);

                String queryVina = "INSERT INTO Vina (Id_Uva, Variedad, Kilos, Grado, Kilogrado, Bodega) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";

                PreparedStatement psVina = connection.prepareStatement(queryVina);
                psVina.setInt(1, idProduccion);
                psVina.setString(2, variedad);
                psVina.setDouble(3, kilos);
                psVina.setDouble(4, grado);
                psVina.setDouble(5, kilos * grado);
                psVina.setString(6, bodega);

                psVina.executeUpdate();
            }

        } catch (Exception e) {
            throw new SQLException("Error al insertar: " + e.getMessage());
        } finally {
            closeDBConnection();
        }





    }

    public void insertarProduccionAceituna(java.util.Date fecha, int anio, int idTierra, double kiloArbol, double kiloSuelo, String variedad) throws SQLException {
        if (!initDBConnection()) {
            throw new RuntimeException("Error al conectar con la base de datos");
        }

        String queryProduccion = "INSERT INTO Produccion (Fecha, Anio, Id_Tierra) VALUES (?, ?, ?) RETURNING Id";

        try {
            PreparedStatement psProduccion = connection.prepareStatement(queryProduccion);
            psProduccion.setDate(1, new java.sql.Date(fecha.getTime()));
            psProduccion.setInt(2, anio);
            psProduccion.setInt(3, idTierra);

            ResultSet rs = psProduccion.executeQuery();
            if (rs.next()) {
                int idProduccion = rs.getInt(1);

                // Insertar en Aceituna
                String queryAceituna = "INSERT INTO Aceituna (Id_Aceituna, Kilo_Arbol, Kilo_Suelo, variedad) " +
                        "VALUES (?, ?, ?, ?)";

                PreparedStatement psAceituna = connection.prepareStatement(queryAceituna);
                psAceituna.setInt(1, idProduccion);
                psAceituna.setDouble(2, kiloArbol);
                psAceituna.setDouble(3, kiloSuelo);
                psAceituna.setString(4, variedad);

                psAceituna.executeUpdate();
            }

        } catch (Exception e) {
            throw new SQLException("Error al insertar: " + e.getMessage());
        } finally {
            closeDBConnection();
        }
    }

    public void insertarProduccionCereal(java.util.Date fecha, int anio, int idTierra, String tipoCereal, double kilos) throws SQLException {
        if (!initDBConnection()) {
            throw new RuntimeException("Error al conectar con la base de datos");
        }

        String queryProduccion = "INSERT INTO Produccion (Fecha, Anio, Id_Tierra) VALUES (?, ?, ?) RETURNING Id";

        try {
            PreparedStatement psProduccion = connection.prepareStatement(queryProduccion);
            psProduccion.setDate(1, new java.sql.Date(fecha.getTime()));
            psProduccion.setInt(2, anio);
            psProduccion.setInt(3, idTierra);

            ResultSet rs = psProduccion.executeQuery();
            if (rs.next()) {
                int idProduccion = rs.getInt(1);

                String queryCereal = "INSERT INTO Cereal (Id_Cereal, Tipo_Cereal, Kilos) VALUES (?, ?, ?)";

                PreparedStatement psCereal = connection.prepareStatement(queryCereal);
                psCereal.setInt(1, idProduccion);
                psCereal.setString(2, tipoCereal);
                psCereal.setDouble(3, kilos);

                psCereal.executeUpdate();
            }

        } catch (Exception e) {
            throw new SQLException("Error al insertar: " + e.getMessage());
        } finally {
            closeDBConnection();
        }

    }


}
