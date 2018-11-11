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
public class UsuariosServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        UsuarioBean usuarioLogado = (UsuarioBean) request.getSession().getAttribute("sessaoUsuario");
        if (usuarioLogado == null) {
            request.getRequestDispatcher("login.jsp");
            return;
        } else if (usuarioLogado.isPrimeiroLogin() || usuarioLogado.getNivelDePermissao() < 3) {
            out.println("<script>location.href = history.go(-1);</script>");
            return;
        }
        request.setAttribute("usuarios", UsuarioDao.getUsuariosExceto(usuarioLogado.getId()));
        request.getRequestDispatcher("/usuarios/usuarios.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String toDeactivate = request.getParameter("toDeactivate");
            String toActivate = request.getParameter("toActivate");
            String niveisUsuarios = request.getParameter("niveisUsuarios");

            String[] splitPontoVirgula = toDeactivate.split(";");
            for (String str : splitPontoVirgula) {
                if (str.isEmpty()) {
                    break;
                }
                UsuarioDao.setUsuarioAtivo(Integer.parseInt(str), 0);
            }
            splitPontoVirgula = toActivate.split(";");
            for (String str : splitPontoVirgula) {
                if (str.isEmpty()) {
                    break;
                }
                UsuarioDao.setUsuarioAtivo(Integer.parseInt(str), 1);
            }
            String[] splitUsuarioNivel;
            splitPontoVirgula = niveisUsuarios.split(";");
            for (String str : splitPontoVirgula) {
                if (str.isEmpty()) {
                    break;
                }
                splitUsuarioNivel = str.split("[-][>]");
                UsuarioDao.setNivelUsuario(Integer.parseInt(splitUsuarioNivel[0]), Integer.parseInt(splitUsuarioNivel[1]));
            }

            processRequest(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

}
