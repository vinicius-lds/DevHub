package br.com.proway.servlet.util;

import br.com.proway.dao.ImagemDAO;
import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Vinícius Luis da Silva
 */
@WebServlet(name = "RecuperarImagemServlet")
public class RecuperarImagemServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        byte[] imagem = null;
        switch (request.getParameter("tipo")) {
            case "projeto":
                imagem = ImagemDAO.getImagemProjeto(Integer.parseInt(request.getParameter("id")));
                break;
            case "usuario":
                imagem = ImagemDAO.getImagemUsuario(Integer.parseInt(request.getParameter("id")));
                break;
            default:
                break;
        }
        response.setContentType("image/jpg");
        OutputStream out = response.getOutputStream();
        out.write(imagem);
        out.flush();
    }
}
