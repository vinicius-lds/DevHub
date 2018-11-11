<%-- 
    Document   : alterar_perfil
    Created on : 30/09/2018, 15:45:37
    Author     : Larissa Amanada Junges && Vinícius Luis da Silva
--%>

<%@page import="br.com.proway.dao.UsuarioDao"%>
<%@page import="br.com.proway.bean.UsuarioBean"%>
<%@page contentType="text/html" pageEncoding="ISO-8859-1"%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="css/bootstrap.min.css" rel="stylesheet">
        <link href="css/alterar_perfil.css" rel="stylesheet">
        <script src="js/alterar_perfil.js"></script>
        <script src="js/jquery-3.3.1.js"></script>
        <script src="js/popper.min.js"></script>
        <script src="js/bootstrap.min.js"></script>
        <title>Perfil</title>
    </head>
    <body>
        <form action="alterar_perfil" method="POST" enctype="multipart/form-data" id="form-alteracao" accept-charset="ISO-8859-1">
            <%
                if (request.getAttribute("erro") != null && !(Boolean) request.getAttribute("erro")) {
            %>
            <div class='alert alert-danger' id="erro">Erro ao fazer alteração de usuário!</div>
            <script>
                var msg = document.getElementById("erro");
                setTimeout(function () {
                    $(msg).hide();
                }, 5000);
            </script>
            <%
                }
            %>
            <div class="" style="text-align: center;">
                <img src="<%= request.getAttribute("imagem")%>" height="100" width="100" class="img-alteracao-perfil" onclick="adicionarImagem();"> 
                <input type="file" name="img-perfil" id="img-perfil" accept=".jpg" style="display: none" onchange="mudarImagem(this);">
            </div>
            <label id="label-imagem">Imagem selecionada!</label>
            <div class="form-group">
                <input type="text" class="form-control" id="nome-de-usuario" name="nome-de-usuario" placeholder="Nome de Usuário" onkeyup="validaNomeDeUsuario()" value="<%= request.getAttribute("nomeUsuario")%>">
            </div>
            <div class="form-group">
                <input type="text" class="form-control" id="nome-completo" name="nome-completo" placeholder="Nome Completo" onkeyup="validaNomeCompleto()" value="<%= request.getAttribute("nomeCompleto")%>">
            </div>
            <div class="form-group">
                <input type="email" class="form-control" id="email" name="email" placeholder="E-mail" onkeyup="validaEmail()" value="<%= request.getAttribute("email")%>">
            </div>
            <div class="form-group">
                <input type="password" class="form-control" id="senha" name="senha" placeholder="Senha">
            </div>
            <div class="form-group">
                <input type="password" class="form-control" id="confirmar-senha" placeholder="Confirmar Senha" onkeydown="validaSenha()">
            </div>
            <div class="form-group">
                <div class ="form-group">
                    <select id="pergunta1" name="pergunta1" class="form-control" value="" onchange="validaPergunta1()">
                        <option disabled selected hidden>Pergunta</option>
                        <option selected hidden><%= request.getAttribute("pergunta1")%></option>
                        <option>Qual o nome do hospital em que você nasceu?</option>
                        <option>Qual foi sua primeira linguagem de programação?</option>
                        <option>Qual seu banco de dados preferido?</option>
                        <option>Java ou C#?</option>
                        <option>Pior professor do entra-21?</option>
                    </select>
                </div>
            </div>
            <div class="form-group">
                <input type="text" class="form-control" id="resposta1" name="resposta1" placeholder="Resposta" onkeyup="validaResposta1()" value="<%= request.getAttribute("resposta1")%>">
            </div>
            <div class="form-group">
                <div class ="form-group">
                    <select id="pergunta2" name="pergunta2" class="form-control" value="" onchange="validaPergunta2()">
                        <option disabled selected hidden>Pergunta</option>
                        <option selected hidden><%= request.getAttribute("pergunta2")%></option>
                        <option>Qual o nome do hospital em que você nasceu?</option>
                        <option>Qual foi sua primeira linguagem de programação?</option>
                        <option>Qual seu banco de dados preferido?</option>
                        <option>Java ou C#?</option>
                        <option>Pior professor do entra-21?</option>
                    </select>
                </div>
            </div>       
            <div class="form-group">
                <input type="text" class="form-control" id="resposta2" name="resposta2" placeholder="Resposta" onkeyup="validaResposta2()" value="<%= request.getAttribute("resposta2")%>">
            </div>
        </form>
        <div class="col-md-6 offset-md-3 button-group"> 
            <button onclick="validaForm();" class ="btn btn-primary">Salvar Alterações</button>    
            <button onclick="window.location.reload();" class ="btn btn-primary">Cancelar</button>   
        </div>
    </div>
</body>
</html>