package com.example.infoleaf.dao;

import com.example.infoleaf.dataBase.ConexionMethods;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO extends ConexionMethods {

    public String iniciarSesion(String _nombre, String _passwd) throws SQLException{
        if(!initDBConnection()){
            throw new RuntimeException("Fallo al conectar");
        }
        try{
            String query = "SELECT dni FROM usuarios WHERE nombre = ? AND passwd = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, _nombre);
            preparedStatement.setString(2, _passwd);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                String id = resultSet.getString("dni");
                return id;
            }

            return null;
        }catch (Exception e){
            throw new SQLException("Error al consultar");
        }finally {
            closeDBConnection();
        }
    }

    public String obtenerNombre(String _id) {
        if(!initDBConnection()){
            throw new RuntimeException("Fallo al conectar");
        }
        try{
            String query = "SELECT nombre FROM usuarios WHERE dni = ? ";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, _id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                return resultSet.getString("nombre");
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeDBConnection();
        }
    }

    public int registrar(String _nombre, String _dni, String _passwd, String _telefono, String _email) throws SQLException{
        if(!initDBConnection()){
            throw new RuntimeException("Fallo al conectar");
        }
        try{
            String query1 = "SELECT dni FROM usuarios WHERE nombre = ? AND passwd = ? ";
            PreparedStatement preparedStatement = connection.prepareStatement(query1);
            preparedStatement.setString(1, _nombre);
            preparedStatement.setString(2, _passwd);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                return 1;
            }

            String query2 = "SELECT dni FROM usuarios WHERE dni = ?";
            PreparedStatement preparedStatement1 = connection.prepareStatement(query2);
            preparedStatement1.setString(1, _dni);
            ResultSet resultSet2 = preparedStatement1.executeQuery();
            while(resultSet2.next()){
                return 2;
            }

            String query3 = "INSERT INTO usuarios (nombre,dni,passwd,telefono,email) VALUES (?,?,?,?,?)";
            PreparedStatement preparedStatement2 = connection.prepareStatement(query3);

            preparedStatement2.setString(1, _nombre);
            preparedStatement2.setString(2, _dni);
            preparedStatement2.setString(3, _passwd);
            preparedStatement2.setInt(4, Integer.parseInt(_telefono));
            preparedStatement2.setString(5, _email);

            preparedStatement2.executeQuery();

            return 0;
        }catch (Exception e){
            throw new SQLException("Error al consultar");
        }finally {
            closeDBConnection();
        }
    }
}
