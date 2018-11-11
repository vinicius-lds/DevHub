<%-- 
    Document   : cadastro_usuarios
    Created on : 30/09/2018, 21:10:17
    Author     : Vinícius Luis da Silva
--%>

<%@page contentType="text/html" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <form method="POST" action="cadastro_usuarios" id="form-cadastro" accept-charset="ISO-8859-1">
            <%
                if (request.getAttribute("erro") != null && (Boolean) request.getAttribute("erro")) {
            %>
            <div class='alert alert-danger' id="erro">Erro ao realizar cadastro do Usuário!</div>
            <script>
                var msg = document.getElementById("erro");
                setTimeout(function () {
                    $(msg).hide();
                }, 5000);
            </script>
            <%
                }
            %>
            <%
                if (request.getAttribute("sucesso") != null && (Boolean) request.getAttribute("sucesso")) {
            %>
            <div class='alert alert-success' id="sucesso">Usuário cadastrado com sucesso!</div>
            <script>
                var msg = document.getElementById("sucesso");
                setTimeout(function () {
                    $(msg).hide();
                }, 5000);
            </script>
            <%
                }
            %>
            <div class="form-group">
                <input type="text" class="form-control" id="nome-de-usuario" name="nome-de-usuario" placeholder="Nome de Usuário" onkeyup="validaNomeDeUsuario()">
            </div>
            <div class="form-group">
                <input type="password" class="form-control" id="senha" name="senha" placeholder="Senha">
            </div>
            <div class="form-group">
                <input type="password" class="form-control" id="confirmar-senha" placeholder="Confirmar Senha" onkeydown="validaSenha()">
            </div>
        </form>
        <div class="col-md-6 offset-md-3 button-group"> 
            <button onclick="validaForm();" class ="btn btn-primary">Salvar Alterações</button>    
            <button onclick="window.location.reload();" class ="btn btn-primary">Cancelar</button>   
        </div>
    </body>
</html>
<script src="js/jquery-3.3.1.js"></script>
<script src="js/popper.min.js"></script>
<script src="js/bootstrap.min.js"></script>
<script src="js/cadastro_usuarios.js"></script>
<link rel='stylesheet' href='css/cadastro_usuarios.css'>
<link rel='stylesheet' href='css/bootstrap.min.css'>
