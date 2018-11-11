<%@page import="br.com.proway.connection.ConnectionFactory"%>
<%@page import="java.sql.PreparedStatement"%>
<%

int quantidade = Integer.parseInt(request.getParameter("quantidade"));
int idUsuario = Integer.parseInt(request.getParameter("idUsuario"));
int idProjeto = Integer.parseInt(request.getParameter("idProjeto"));
    
ConnectionFactory conn = new ConnectionFactory();
    
String sql = "UPDATE relacao_projeto_usuario SET msg_nao_lida = ?"
        + " WHERE idUsuario = ? AND idProjeto = ?";
PreparedStatement pstmt = conn.obterConexao().prepareCall(sql);
pstmt.setInt(1, quantidade);
pstmt.setInt(2, idUsuario);
pstmt.setInt(3, idProjeto);

pstmt.execute();
conn.encerrarConexao();

%>