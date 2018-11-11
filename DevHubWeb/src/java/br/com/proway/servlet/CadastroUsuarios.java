package br.com.proway.servlet;

import br.com.proway.bean.UsuarioBean;
import br.com.proway.dao.UsuarioDao;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Vinícius Luis da Silva
 */
public class CadastroUsuarios extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UsuarioBean usuarioLogado = (UsuarioBean) request.getSession().getAttribute("sessaoUsuario");
        PrintWriter out = response.getWriter();
        if (usuarioLogado == null) {
            request.getRequestDispatcher("login.jsp");
            return;
        } else if (usuarioLogado.isPrimeiroLogin() || usuarioLogado.getNivelDePermissao() < 2) {
            out.println("<script>location.href = history.go(-1);</script>");
            return;
        }
        request.getRequestDispatcher("/usuarios/cadastro_usuarios.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String nomeUsuario = request.getParameter("nome-de-usuario");
        String senha = request.getParameter("senha");
        if(!UsuarioDao.cadastrarUsuario(nomeUsuario, senha)) {
            request.setAttribute("erro", true);
        } else {
            request.setAttribute("sucesso", true);
        }
        processRequest(request, response);
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

}
