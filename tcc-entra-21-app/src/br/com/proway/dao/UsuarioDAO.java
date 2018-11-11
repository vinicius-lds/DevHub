package br.com.proway.dao;

import br.com.proway.bean.login.Projeto;
import br.com.proway.bean.login.Usuario;
import br.com.proway.main.Main;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Vinícius Luis da Silva
 */
public class UsuarioDAO {

    public static List<Usuario> getUsuariosValidos() {
        List<Usuario> usuarios = new ArrayList();
        try {
            PreparedStatement pstmt = Main.DB_CONNECTION.get().prepareStatement("SELECT * FROM usuarios WHERE id != ? AND primeiro_login = 0 AND ativo = 1 AND primeiro_login = 0");
            pstmt.setInt(1, Main.USER.getId());
            pstmt.execute();
            ResultSet rs = pstmt.getResultSet();
            Usuario usuario;
            Object[] os;
            while (rs.next()) {
                usuario = new Usuario();
                os = new Object[]{
                    rs.getObject(1),
                    rs.getObject(2),
                    rs.getObject(3),
                    rs.getObject(4),
                    rs.getObject(5),
                    rs.getObject(6),
                    rs.getObject(7),
                    rs.getObject(8),
                    rs.getObject(9),
                    rs.getObject(10),
                    rs.getObject(11),
                    rs.getObject(12)
                };
                usuario.initialize(os);
                usuarios.add(usuario);
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
        return usuarios;
    }

    public static List<Usuario> getUsuariosFromProject(Projeto projeto) {
        List<Usuario> ret = new ArrayList<>();
        try {
            String sql = "SELECT * FROM usuarios INNER JOIN relacao_projeto_usuario ON relacao_projeto_usuario.idProjeto = ? WHERE usuarios.id = relacao_projeto_usuario.idUsuario AND usuarios.id != ?";
            PreparedStatement pstmt = Main.DB_CONNECTION.get().prepareCall(sql);
            pstmt.setInt(1, projeto.getId());
            pstmt.setInt(2, Main.USER.getId());
            pstmt.execute();
            ResultSet rs = pstmt.getResultSet();
            Usuario u;
            Object[] os;
            while (rs.next()) {
                u = new Usuario();
                os = new Object[]{
                    rs.getObject(1),
                    rs.getObject(2),
                    rs.getObject(3),
                    rs.getObject(4),
                    rs.getObject(5),
                    rs.getObject(6),
                    rs.getObject(7),
                    rs.getObject(8),
                    rs.getObject(9),
                    rs.getObject(10),
                    rs.getObject(11),
                    rs.getObject(12)
                };
                u.initialize(os);
                ret.add(u);
            }
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static List<Usuario> getUsuariosOutsideOfProject(Projeto projeto, List<Usuario> except) {
        List<Usuario> ret = new ArrayList<>();
        try {
            String sql = "SELECT * FROM usuarios WHERE id != " + Main.USER.getId() + " AND ";
            for(Usuario u: except) {
                sql += "id != " + u.getId() + " AND ";
            }
            sql += " usuarios.primeiro_login = 0 AND usuarios.ativo = 1";
            PreparedStatement pstmt = Main.DB_CONNECTION.get().prepareCall(sql);
            pstmt.execute();
            ResultSet rs = pstmt.getResultSet();
            Usuario u;
            Object[] os;
            while (rs.next()) {
                u = new Usuario();
                os = new Object[]{
                    rs.getObject(1),
                    rs.getObject(2),
                    rs.getObject(3),
                    rs.getObject(4),
                    rs.getObject(5),
                    rs.getObject(6),
                    rs.getObject(7),
                    rs.getObject(8),
                    rs.getObject(9),
                    rs.getObject(10),
                    rs.getObject(11),
                    rs.getObject(12)
                };
                u.initialize(os);
                ret.add(u);
            }
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

}
