package sn.l2gl.girls.daara.model.models;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connexion {

    private Connexion(){
        throw new RuntimeException("Cette classe ne peut etre instantie");
    }
    private static final String URL =
            "jdbc:mysql://127.0.0.1:3306/daara?serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASS = "";

    public static Connection getConnection() {
        try {

            return DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException e) {
            System.err.println("Échec de connexion : " + e.getMessage());
            System.err.println("SQLState : " + e.getSQLState());
            System.err.println("ErrorCode : " + e.getErrorCode());
            throw new RuntimeException("Erreur de connexion : " + e.getMessage());
        }
    }
    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            System.out.println("Connecté à MySQL — base : " + conn.getCatalog());
            System.out.println("Pilote : " + conn.getMetaData().getDriverName());
        } catch (SQLException e) {
            System.err.println("Échec de connexion : " + e.getMessage());
            System.err.println("SQLState : " + e.getSQLState());
            System.err.println("ErrorCode : " + e.getErrorCode());
        }
    }

}