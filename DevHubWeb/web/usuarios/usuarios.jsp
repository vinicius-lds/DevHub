<%-- 
    Document   : usuarios
    Created on : 29/09/2018, 20:11:55
    Author     : Vinícius Luis da Silva
--%>

<%@page import="java.util.List"%>
<%@page import="br.com.proway.bean.UsuarioBean"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Cadastro de Usuários</title>
    </head>
    <body>
        <form method="POST" action="usuarios">
            <div class="input-group">
                <input type="text" class="form-control" placeholder="Pesquisar usuário..." onkeyup="filtrarUsuarios(this)">
                <span class="input-group-btn input-group-btn-salvar">
                    <button class="btn btn-primary" type="submit">Salvar Alterações!</button>
                </span>
            </div>
            <input type="hidden" name="toDeactivate" id="toDeactivate"/>
            <input type="hidden" name="toActivate" id="toActivate"/>
            <input type="hidden" name="niveisUsuarios" id="niveisUsuarios"/>
        </form>

        <table class="table table-hover">
            <thead>
                <tr>
                    <th scope="col" class="td-usuario">Imagem de Perfil</th>
                    <th scope="col" class="td-usuario">Nome de Usuário</th>
                    <th scope="col" class="td-usuario">Nome Completo</th>
                    <th scope="col" class="td-usuario">Nível de Permissão</th>
                    <th scope="col"></th>
                </tr>
            </thead>
            <tbody>
                <%
                    for(UsuarioBean usuario: (List<UsuarioBean>)request.getAttribute("usuarios")) {
                %>
                <tr id="<%= usuario.getId()%>" class="tr-usuario">
                    <th class="td-usuario">
                        <img src="<%= usuario.getImagem()%>" class="foto-de-perfil">
                    </th>
                    <td class="td-usuario" id="nomeUsuario"><%= usuario.getUsuario()%></td>
                    <td class="td-usuario"><%= usuario.getNome()%></td>
                    <td class="td-usuario">
                        <div class="form-group form-group-select">
                            <select class="form-control" id="exampleFormControlSelect1" onchange="nivelChange(this);">
                                <option value="1" <%out.print(usuario.getNivelDePermissao() == 1 ? "selected" : "");%>>Básico</option>
                                <option value="2" <%out.print(usuario.getNivelDePermissao() == 2 ? "selected" : "");%>>Intermediario</option>
                                <option value="3" <%out.print(usuario.getNivelDePermissao() == 3 ? "selected" : "");%>>Avançado</option>
                            </select>
                        </div>
                    </td>
                    <td class="td-usuario">
                        <button type="button" class="<%out.print(usuario.isAtivo() ? "btn btn-danger btn-custom" : "btn btn-success btn-custom");%>" onclick="switchSelection(this);"><%out.print(usuario.isAtivo() ? "X" : "V");%></button>
                    </td>
                </tr>
                <%
                    }
                %>
                
            </tbody>
        </table>
    </body>
</html>
<script src="js/jquery-3.3.1.js"></script>
<script src="js/usuarios.js"></script>
<script src="js/popper.min.js"></script>
<script src="js/bootstrap.min.js"></script>
<link rel='stylesheet' href='css/usuarios.css'>
<link rel='stylesheet' href='css/bootstrap.min.css'>
<!--
https://scontent.ffln2-2.fna.fbcdn.net/v/t1.0-1/c29.0.100.100/p100x100/10354686_10150004552801856_220367501106153455_n.jpg?_nc_cat=1&oh=1ef5352da48074f2c411d22dc5dfdd66&oe=5C540B77
-->