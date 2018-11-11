package br.com.proway.dao;

import br.com.proway.connection.ConnectionFactory;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Vinícius Luis da Silva
 */
public class ImagemDAO {

    public static byte[] getImagemProjeto(int id) {
        byte[] imagem = null;
        try {
            String sql = "SELECT imagem FROM projeto WHERE id = ?";
            PreparedStatement pstmt = ConnectionFactory.obterConexao().prepareCall(sql);
            pstmt.setInt(1, id);
            pstmt.execute();
            ResultSet rs = pstmt.getResultSet();
            while (rs.next()) {
                imagem = rs.getBytes(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imagem;
    }
    
    public static byte[] getImagemUsuario(int id) {
        byte[] imagem = null;
        try {
            String sql = "SELECT imagem FROM usuarios WHERE id = ?";
            PreparedStatement pstmt = ConnectionFactory.obterConexao().prepareCall(sql);
            pstmt.setInt(1, id);
            pstmt.execute();
            ResultSet rs = pstmt.getResultSet();
            while (rs.next()) {
                imagem = rs.getBytes(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imagem;
    }

}
