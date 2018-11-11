<%
    session.setAttribute("sessaoUsuario", null);

    if(session.getAttribute("sessaoUsuario") == null){
        Cookie[] cookies2 = request.getCookies();
        
    for(int i = 0; i < cookies2.length; i++){
        if(cookies2[i].getName().equals("loginUsuario")){
        cookies2[i].setMaxAge(0);
        response.addCookie(cookies2[i]);
        }
    }
        Cookie[] cookies3 = request.getCookies();
        
    for(int i = 0; i < cookies3.length; i++){
        if(cookies3[i].getName().equals("senhaUsuario")){
        cookies3[i].setMaxAge(0);
        response.addCookie(cookies3[i]);
        }
    }
    response.sendRedirect("index.jsp");    
    }
%>