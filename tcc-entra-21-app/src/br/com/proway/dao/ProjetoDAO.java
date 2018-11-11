package br.com.proway.dao;

import br.com.proway.bean.login.Projeto;
import br.com.proway.controller.projetos.diagramadeclasse.UsuarioController;
import br.com.proway.main.Main;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Vinícius Luis da Silva
 */
public class ProjetoDAO {

    public static void cadastrarProjeto(String nomeProjeto, File imagem, String repoGit, List<UsuarioController> usuarios) {
        try {
            String sql = "INSERT INTO projeto (nome_projeto, imagem, ativo, repositorio_git) VALUES (?, ?, 1, ?)";
            PreparedStatement pstmt = Main.DB_CONNECTION.get().prepareCall(sql);
            pstmt.setString(1, nomeProjeto);

            byte[] vFileBytes = new byte[(int) imagem.length()];
            int vOffSet = 0;
            int vNumRead = 0;

            try {
                InputStream vIS = new FileInputStream(imagem);
                while (vOffSet < vFileBytes.length && (vNumRead = vIS.read(vFileBytes, vOffSet, vFileBytes.length - vOffSet)) >= 0) {
                    vOffSet += vNumRead;
                }
            } catch (FileNotFoundException ex) {
            }

            //converte o objeto file em array de bytes
            InputStream is = new FileInputStream(imagem);
            byte[] bytes = new byte[(int) imagem.length()];
            int offset = 0;
            int numRead = 0;
            while (offset < bytes.length
                    && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }

            pstmt.setBytes(2, bytes);

            // pstmt.setBinaryStream(2, imagem);
            pstmt.setString(3, repoGit);
            pstmt.execute();
            sql = "SELECT * FROM projeto ORDER BY id DESC LIMIT 1";
            pstmt = Main.DB_CONNECTION.get().prepareCall(sql);
            pstmt.execute();

            ResultSet rs = pstmt.getResultSet();
            int idProjeto = 0;
            while (rs.next()) {
                idProjeto = rs.getInt(1);
            }

            sql = "INSERT INTO relacao_projeto_usuario (idUsuario, idProjeto, msg_nao_lida) VALUES "
                    + "(" + Main.USER.getId() + ", " + idProjeto + ", 0),";
            for (UsuarioController uc : usuarios) {
                sql += "(" + uc.getUsuario().getId() + ", " + idProjeto + ", 0),";
            }
            sql = sql.substring(0, sql.length() - 1);
            pstmt = Main.DB_CONNECTION.get().prepareCall(sql);
            pstmt.execute();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Projeto> getProjetos() {
        try {
            String sql = "SELECT projeto.id, projeto.nome_projeto, projeto.imagem, projeto.ativo, projeto.repositorio_git\n"
                    + "FROM projeto \n"
                    + "INNER JOIN relacao_projeto_usuario ON projeto.id = relacao_projeto_usuario.idProjeto \n"
                    + "AND relacao_projeto_usuario.idUsuario = ? \n"
                    + "WHERE projeto.ativo = 1 "
                    + "ORDER BY projeto.id";
            PreparedStatement pstmt = Main.DB_CONNECTION.get().prepareCall(sql);
            pstmt.setInt(1, Main.USER.getId());
            pstmt.execute();
            ResultSet rs = pstmt.getResultSet();
            List<Projeto> projetos = new ArrayList<>();
            Projeto pr;
            while (rs.next()) {
                pr = new Projeto();
                pr.initialize(new Object[]{
                    rs.getInt(1),
                    rs.getString(2),
                    new ByteArrayInputStream(rs.getBytes(3)),
                    rs.getBoolean(4),
                    rs.getString(5)
                });
                projetos.add(pr);
            }
            pstmt.close();
            return projetos;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void updateProjeto(Projeto projeto, File imagem, List<UsuarioController> adicionados) {
        try {
            String sql = "UPDATE projeto SET nome_projeto = ? ";
            if(imagem != null) {
                sql += ", imagem = ?";
            }
            sql += "WHERE id = ?";
            PreparedStatement pstmt = Main.DB_CONNECTION.get().prepareCall(sql);
            pstmt.setString(1, projeto.getNomeProjeto());
            if(imagem != null) {
                pstmt.setBinaryStream(2, new FileInputStream(imagem), imagem.length());
                pstmt.setInt(3, projeto.getId());
            } else {
                pstmt.setInt(2, projeto.getId());
            }
            pstmt.execute();
            
            sql = "DELETE FROM relacao_projeto_usuario WHERE idProjeto = ?";
            pstmt = Main.DB_CONNECTION.get().prepareCall(sql);
            pstmt.setInt(1, projeto.getId());
            pstmt.execute();
            
            sql = "INSERT INTO relacao_projeto_usuario(idProjeto, idusuario) VALUES (?, ?)";
            pstmt = Main.DB_CONNECTION.get().prepareCall(sql);
            pstmt.setInt(1, projeto.getId());
            pstmt.setInt(2, Main.USER.getId());
            pstmt.execute();
            
            for (UsuarioController uc : adicionados) {
                sql = "INSERT INTO relacao_projeto_usuario(idProjeto, idusuario) VALUES (?, ?)";
                pstmt = Main.DB_CONNECTION.get().prepareCall(sql);
                pstmt.setInt(1, projeto.getId());
                pstmt.setInt(2, uc.getUsuario().getId());
                pstmt.execute();
            }
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
