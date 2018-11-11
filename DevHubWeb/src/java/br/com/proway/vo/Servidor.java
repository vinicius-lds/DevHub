package br.com.proway.vo;

import br.com.proway.connection.ConnectionFactory;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Singleton;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.json.JSONObject;
//import org.json.JSONObject;

/**
 * @author Vinícius Luis da Silva && Larissa Amanda Junges
 */
@ServerEndpoint("/server")
@Singleton
public class Servidor {

    private static Set<Session> usuarios = Collections.synchronizedSet(new HashSet<Session>());

    @OnMessage
    public void recebeMensagem(String message, Session usuario) {
        JSONObject json = new JSONObject(message);

        int idUsuario = Integer.parseInt(json.get("idUsuario").toString());
        int idProjeto = Integer.parseInt(json.get("idProjeto").toString());
        String mensagem = json.get("mensagem").toString();

        json.remove("idUsuario");

        try {
            this.cadastraMensagem(idUsuario, idProjeto, mensagem);
            this.mandaPraGeral(message, usuario);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @OnOpen
    public void abrirConexao(Session usuario) {
        usuarios.add(usuario);
    }

    @OnClose
    public void fecharConexao(Session usuario) {
        usuarios.remove(usuario);
    }

    public void cadastraMensagem(int idUsuario, int idProjeto, String mensagem) throws SQLException {
        String sql = "INSERT INTO mensagem (mensagem, idProjeto, idUsuario, data)"
                + "VALUES (?, ?, ?, NOW())";
        PreparedStatement pstmt = ConnectionFactory.obterConexao().prepareCall(sql);
        pstmt.setString(1, mensagem);
        pstmt.setInt(2, idProjeto);
        pstmt.setInt(3, idUsuario);

        pstmt.execute();
        
        sql = "UPDATE relacao_projeto_usuario SET msg_nao_lida = msg_nao_lida + 1"
                + " WHERE idUsuario != ? AND idProjeto = ?";
        pstmt = ConnectionFactory.obterConexao().prepareCall(sql);
        pstmt.setInt(1, idUsuario);
        pstmt.setInt(2, idProjeto);

        pstmt.execute();
        ConnectionFactory.encerrarConexao();

    }

    private void mandaPraGeral(String message, Session usuario) {
        for (Session u : usuarios) {
            if (u != usuario) {
                u.getAsyncRemote().sendText(message);
            }
        }
    }

}
