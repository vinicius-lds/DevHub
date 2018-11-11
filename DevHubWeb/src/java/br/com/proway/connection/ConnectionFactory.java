package br.com.proway.connection;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionFactory {

    private static  Connection conexao;

    public static Connection obterConexao()  {
        String driver = "com.mysql.jdbc.Driver";
        String user = "root";
        String pass = "";
        String url = "jdbc:mysql://localhost/tcc";
        try {
            Class.forName(driver).newInstance();
            conexao = DriverManager.getConnection(url, user, pass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conexao;
    }

    public static void encerrarConexao() {
        try {
            conexao.close();
            conexao = null;
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }
}
