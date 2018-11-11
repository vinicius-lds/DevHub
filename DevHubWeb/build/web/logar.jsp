
<%@page import="br.com.proway.dao.UsuarioDao"%>
<%@page import="br.com.proway.bean.UsuarioBean"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String usuario = request.getParameter("login");
    String senha = request.getParameter("senha");

    UsuarioBean ub = new UsuarioBean();
    ub.setUsuario(usuario);

    UsuarioDao ud = new UsuarioDao();
    if (request.getParameter("pagina") != null) {
        String pagina = request.getParameter("pagina");
        ub.setSenha(senha);
        ub = ud.logar(ub);

        if (ub.getId() != 0) {
            session.setAttribute("sessaoUsuario", ub);
            response.sendRedirect(pagina);
        } else {
            //out.print("deu merda");
        }
    } else {
        String md5 = br.com.proway.util.StringUtil.toMD5(senha);
        ub.setSenha(md5);
        ub = ud.logar(ub);

        if (ub.getId() != 0) {
            if (request.getParameter("caixa") != null) {
                if (usuario != null) {
                    Cookie cookie = new Cookie("loginUsuario", usuario);
                    cookie.setMaxAge(60 * 24 * 30 * 12 * 10);
                    response.addCookie(cookie);
                }
                Cookie[] cookies = request.getCookies();
                for (Cookie atual : cookies) {
                    if (atual.getName().equals("loginUsuario")) {
                        usuario = atual.getValue();
                    }
                }
                if (md5 != null) {
                    Cookie cookie = new Cookie("senhaUsuario", md5);
                    cookie.setMaxAge(60 * 24 * 30 * 12 * 10);
                    response.addCookie(cookie);
                }
                Cookie[] cookies1 = request.getCookies();
                for (Cookie atual : cookies1) {
                    if (atual.getName().equals("senhaUsuario")) {
                        md5 = atual.getValue();
                    }
                }
            }

            session.setAttribute("sessaoUsuario", ub);
            response.sendRedirect("index.jsp");
            //telaperfil/perfil.jsp

        } else {
            response.sendRedirect("login.jsp?erro=1");
        }
    }

%>