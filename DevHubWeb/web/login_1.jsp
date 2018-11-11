<%-- 
    Document   : index
    Created on : 09/08/2018, 13:28:45
    Author     : 104875
--%>
<%@page import="br.com.proway.bean.UsuarioBean"%>
<%@page import="java.sql.Connection"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Cookie[] cookies = request.getCookies();
    for (Cookie atual : cookies) {
        if (atual.getName().equals("loginUsuario") && atual.getName().equals("senhaUsuario")) {
            response.sendRedirect("index.jsp");
        }
    }
    Cookie[] cookies1 = request.getCookies();
    for (Cookie atual : cookies1) {
        if (atual.getName().equals("senhaUsuario")) {
            response.sendRedirect("index.jsp");
        }
    }
%>
<html>
    <head>
        <title>Tela de login</title>
        <link rel="stylesheet" type="text/css" href="css/login.css">
        <script src="js/jquery-3.3.1.js"></script>
        <script src="js/login.js"></script>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
    </head>
    <body style="overflow-y: hidden; overflow-x: hidden">
        <form  action="logar.jsp">
            <div class="form-area">
                <h2>Bem Vindo ao DevHub</h2>
                <%
                    Object erro = request.getParameter("erro");
                    if (erro != null) {
                        int e = Integer.parseInt(erro.toString());
                        String msg = null;
                        switch (e) {
                            case 1:
                                msg = "Erro ao realizar login!";
                                break;
                            default:
                                break;
                        }
                        if (msg != null) {
                            %><div class='alert alert-danger' id="msg-erro"><%
                                out.print(msg);
                            %></div><%
                            }
                    }
                                //<div class='alert alert-danger msg'>Sessão expirada, faça o login novamente.</div>
%>
                <div class="login">
                    <div class="grupo">      
                        <input type="text" name="login" id="login" required>
                        <span class="highlight"></span>
                        <span class="bar"></span>
                        <label>Login</label>
                    </div>
                </div>
                <div class="senha">
                    <div class="grupo">      
                        <input type="password" name="senha" id="senha" required>
                        <span class="highlight"></span>
                        <span class="bar"></span>
                        <label>Senha</label>  
                    </div>
                </div>
                <input class="btn" type="submit" name="btn" placeholder="Logar" value="Entrar">
                <a class="esque" href='esqueceusenha/email.jsp'>Esqueceu a senha?</a>
                <div>
                    <div>
                        <input class="caixasele" type="checkbox" name="caixa" value="caixa"/>
                    </div>
                    <div>
                        <h9 class="manter">Manter-se </h9>
                        <h9 class="conectado">conectado</h9>
                    </div>
                </div>
            </div>
        </form>
    </body>
</html>