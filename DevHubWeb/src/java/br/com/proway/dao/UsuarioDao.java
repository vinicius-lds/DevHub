package br.com.proway.dao;

import br.com.proway.bean.UsuarioBean;
import br.com.proway.connection.ConnectionFactory;
import br.com.proway.util.StringUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.Part;

public class UsuarioDao {

    public static void setNivelUsuario(int id, int nivel) {
        System.out.println("setNivelUsuario: " + id + " nivel: " + nivel);
        if (nivel > 3 || nivel < 1) {
            return;
        }
        String sql = "UPDATE usuarios SET nivel_de_permissao = ? WHERE id = ?";
        try {
            PreparedStatement pstmt = ConnectionFactory.obterConexao().prepareCall(sql);
            pstmt.setInt(1, nivel);
            pstmt.setInt(2, id);
            pstmt.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ConnectionFactory.encerrarConexao();
    }

    public static void setUsuarioAtivo(int id, int ativo) {
        System.out.println("setUsuarioAtivo: " + id + " ativo: " + ativo);
        if (ativo > 1 || ativo < 0) {
            return;
        }
        String sql = "UPDATE usuarios SET ativo = ? WHERE id = ?";
        try {
            PreparedStatement pstmt = ConnectionFactory.obterConexao().prepareCall(sql);
            pstmt.setInt(1, ativo);
            pstmt.setInt(2, id);
            pstmt.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ConnectionFactory.encerrarConexao();
    }

    public static List<UsuarioBean> getUsuariosExceto(int id) {
        String sql = "SELECT id, usuario, nome, nivel_de_permissao, ativo FROM usuarios WHERE id != ?";
        List<UsuarioBean> ret = new ArrayList<>();
        try {
            PreparedStatement pstmt = ConnectionFactory.obterConexao().prepareCall(sql);
            pstmt.setInt(1, id);
            pstmt.execute();
            ResultSet rs = pstmt.getResultSet();
            UsuarioBean u;
            while (rs.next()) {
                u = new UsuarioBean();
                u.setId(rs.getInt(1));
                u.setUsuario(rs.getString(2));
                u.setImagem("RecuperarImagemServlet?tipo=usuario&id=" + u.getId());
                u.setNome(rs.getString(3));
                u.setNivelDePermissao(rs.getInt(4));
                u.setAtivo(rs.getBoolean(5));
                ret.add(u);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ConnectionFactory.encerrarConexao();
        return ret;
    }

    public UsuarioBean logar(UsuarioBean ub) {
        UsuarioBean obj = new UsuarioBean();
        String sql = "SELECT * FROM usuarios WHERE usuario = ? AND senha = ? AND ativo = 1 AND primeiro_login = 0";
        try {
            PreparedStatement pstmt = ConnectionFactory.obterConexao().prepareStatement(sql);
            pstmt.setString(1, ub.getUsuario());
            pstmt.setString(2, ub.getSenha());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                obj.setId(rs.getInt(1));
                obj.setUsuario(rs.getString(2));
                obj.setEmail(rs.getString(3));
                obj.setNome(rs.getString(4));
                //obj.setImagem(rs.getString(5));
                obj.setSenha(rs.getString(6));
                obj.setPergunta1(rs.getString(7));
                obj.setResposta1(rs.getString(8));
                obj.setPergunta2(rs.getString(9));
                obj.setResposta2(rs.getString(10));
                obj.setPrimeiroLogin(rs.getBoolean(11));
                obj.setNivelDePermissao(rs.getInt(12));
            }
        } catch (Exception e) {
            System.out.println("Falha ao obter dados" + e.getMessage());
        }
        ConnectionFactory.encerrarConexao();
        return obj;
    }

    public static boolean cadastrarUsuario(String nomeUsuario, String senha) {

        boolean situacao = false;

        try {
            String sql = "INSERT INTO usuarios (usuario,senha,imagem,primeiro_login,nivel_de_permissao,ativo,email,nome,pergunta1,resposta1,pergunta2,resposta2) "
                    + "VALUES(?,?,?,1,1,1,?,?,?,?,?,?)";
            PreparedStatement pstmt;
            pstmt = ConnectionFactory.obterConexao().prepareStatement(sql);
            
            pstmt.setString(1, nomeUsuario);
            pstmt.setString(2, StringUtil.toMD5(senha));
            File file = new File("img-perfil-default.jpg");
            pstmt.setBlob(3, new FileInputStream(file), file.length());
            pstmt.setString(4, "");
            pstmt.setString(5, "");
            pstmt.setString(6, "");
            pstmt.setString(7, "");
            pstmt.setString(8, "");
            pstmt.setString(9, "");
            pstmt.execute();
            situacao = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        ConnectionFactory.encerrarConexao();
        return situacao;
    }

    public static boolean alterarUsuario(int id, String nomeUsuario, String nomeCompleto, String senha, String email, String pergunta1, String resposta1, String pergunta2, String resposta2, Part imagem) {
        boolean ret;
        try {
            String sql = "UPDATE usuarios SET usuario = ?,"
                    + " email = ?,"
                    + " nome = ?,"
                    + " senha = ?,"
                    + " pergunta1 = ?,"
                    + " resposta1 = ? ,"
                    + "pergunta2 = ?, "
                    + "resposta2 = ? ";
            if(imagem != null && imagem.getSize() > 0) {
                sql += ", imagem = ?";
            }
            sql += " WHERE id = ?";

            PreparedStatement pstmt = ConnectionFactory.obterConexao().prepareStatement(sql);

            pstmt.setString(1, nomeUsuario);
            pstmt.setString(2, email);
            pstmt.setString(3, nomeCompleto);
            pstmt.setString(4, StringUtil.toMD5(senha));
            pstmt.setString(5, pergunta1);
            pstmt.setString(6, resposta1);
            pstmt.setString(7, pergunta2);
            pstmt.setString(8, resposta2);
            if(imagem != null && imagem.getSize() > 0) {
                pstmt.setBinaryStream(9, imagem.getInputStream(), imagem.getSize());
                pstmt.setInt(10, id);
            } else {
                pstmt.setInt(9, id);
            }
            pstmt.execute();
            ret = true;
        } catch (Exception e) {
            e.printStackTrace();
            ret = false;
        }
        ConnectionFactory.encerrarConexao();
        return ret;
    }

    public UsuarioBean dadosUsuario(int idUsuario) {
        UsuarioBean obj = new UsuarioBean();

        try {
            String sql = "SELECT * FROM usuarios WHERE id = ?";

            PreparedStatement pstmt = ConnectionFactory.obterConexao().prepareStatement(sql);
            pstmt.setInt(1, idUsuario);
            ResultSet rs = pstmt.executeQuery();
            rs.last();
            obj.setId(rs.getInt(1));
            obj.setEmail(rs.getString(2));
            obj.setNome(rs.getString(3));
            obj.setPergunta1(rs.getString(4));
            obj.setPergunta2(rs.getString(5));
            obj.setResposta1(rs.getString(6));
            obj.setResposta2(rs.getString(7));
            obj.setSenha(rs.getString(8));
            obj.setUsuario(rs.getString(9));
        } catch (Exception e) {
            System.out.println("Falha ao selecionar dados");
        }
        ConnectionFactory.encerrarConexao();
        return obj;
    }

    public UsuarioBean email(UsuarioBean ub) {
        UsuarioBean obj = new UsuarioBean();
        String sql = "SELECT * FROM usuarios WHERE email = ?";
        try {
            PreparedStatement pstmt = ConnectionFactory.obterConexao().prepareStatement(sql);
            pstmt.setString(1, ub.getEmail());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                obj.setId(rs.getInt(1));
                obj.setUsuario(rs.getString(2));
                obj.setEmail(rs.getString(3));
                obj.setNome(rs.getString(4));
                obj.setSenha(rs.getString(5));
                obj.setPergunta1(rs.getString(6));
                obj.setResposta1(rs.getString(7));
                obj.setPergunta2(rs.getString(8));
                obj.setResposta2(rs.getString(9));
                obj.setPrimeiroLogin(rs.getBoolean(10));
            }
        } catch (Exception e) {
            System.out.println("Falha ao obter dados" + e.getMessage());
        }
        ConnectionFactory.encerrarConexao();
        return obj;
    }

    public void alterarUsuario(UsuarioBean obj) {
        try {
            String sql = "UPDATE usuarios SET usuario = ?,"
                    + " email = ?,"
                    + " nome = ?,"
                    + " senha = ?,"
                    + " pergunta1 = ?,"
                    + " resposta1 = ? ,"
                    + "pergunta2 = ?, "
                    + "resposta2 = ?"
                    + " WHERE id = ?";

            PreparedStatement pstmt = ConnectionFactory.obterConexao().prepareStatement(sql);

            pstmt.setString(1, obj.getUsuario());
            pstmt.setString(2, obj.getEmail());
            pstmt.setString(3, obj.getNome());
            pstmt.setString(4, obj.getSenha());
            pstmt.setString(5, obj.getPergunta1());
            pstmt.setString(6, obj.getResposta1());
            pstmt.setString(7, obj.getPergunta2());
            pstmt.setString(8, obj.getResposta2());
            pstmt.setInt(9, obj.getId());

            pstmt.execute();
        } catch (SQLException e) {
            System.out.println("Falha ao alterar");
        }
        ConnectionFactory.encerrarConexao();
    }

    public UsuarioBean pergunta(UsuarioBean ub) {
        UsuarioBean obj = new UsuarioBean();
        String sql = "SELECT * FROM usuarios WHERE resposta1 = ? ";
        try {
            PreparedStatement pstmt = ConnectionFactory.obterConexao().prepareStatement(sql);
            pstmt.setString(1, ub.getUsuario());
            pstmt.setString(2, ub.getSenha());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                obj.setId(rs.getInt(1));
                obj.setUsuario(rs.getString(2));
                obj.setEmail(rs.getString(3));
                obj.setNome(rs.getString(4));
                obj.setSenha(rs.getString(5));
                obj.setPergunta1(rs.getString(6));
                obj.setResposta1(rs.getString(7));
                obj.setPergunta2(rs.getString(8));
                obj.setResposta2(rs.getString(9));
                obj.setPrimeiroLogin(rs.getBoolean(10));
            }
        } catch (Exception e) {
            System.out.println("Falha ao obter dados" + e.getMessage());
        }
        ConnectionFactory.encerrarConexao();
        return obj;
    }

}
