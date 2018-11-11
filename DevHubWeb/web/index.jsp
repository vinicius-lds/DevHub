<%-- 
    Author     : Larissa Junges
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!doctype html>
<%
if(session.getAttribute("sessaoUsuario") == null) {
    response.sendRedirect("login.jsp");
}
%>
<html>
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        <meta name="description" content="">
        <meta name="author" content="">

        <title>Principal</title>

        <!-- Bootstrap core CSS -->
        <link href="dist/css/bootstrap.min.css" rel="stylesheet">

        <!-- Custom styles for this template -->
        <link href="dist/css/signin.css" rel="stylesheet">
    </head>

    <body class="text-center">
        <a href="chat">Chat</a>
        <a href="usuarios">Usuarios</a>
        <a href="cadastro_usuarios">Cadastro de Usuarios</a>
        <a href="alterar_perfil">Alterar Perfil</a>
        <a href="sair.jsp">Sair</a>
    </body>
</html>