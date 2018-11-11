
<%@page import="java.io.FileReader"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.util.ArrayList"%>
<%@page import="br.com.proway.connection.ConnectionFactory"%>
<%@page import="java.sql.Statement"%>
<%@page import="java.sql.PreparedStatement"%>
<%

    int id = Integer.parseInt(request.getParameter("idProjeto"));

    ConnectionFactory cf = new ConnectionFactory();
    String sql = "SELECT idUsuario, mensagem, usuarios.imagem FROM mensagem "
            + "INNER JOIN usuarios ON usuarios.id = mensagem.idUsuario WHERE idProjeto = ? "
            + "ORDER BY data";
    PreparedStatement pstmt = cf.obterConexao().prepareCall(sql);
    pstmt.setInt(1, id);
    pstmt.execute();
    ResultSet rs = pstmt.getResultSet();

    //mensagem, usuario
    ArrayList<String> mensagens = new ArrayList();
    ArrayList<Integer> usuarios = new ArrayList();
    while (rs.next()) {
        usuarios.add(rs.getInt(1));
        mensagens.add(rs.getString(2));
    }

    rs.close();
    cf.encerrarConexao();
    pstmt.close();

    org.json.JSONObject json = new org.json.JSONObject();

    //projetos
    //mensagens
    
    ArrayList aux = new ArrayList();
    for (int key = 0; key < mensagens.size(); key++) {
        aux.add(usuarios.get(key));
        aux.add("RecuperarImagemServlet?tipo=usuario&id=" + usuarios.get(key));
        aux.add(mensagens.get(key));
        json.put(String.valueOf(key), aux);
        aux.clear();
    }

    out.print(json.toString());

%>