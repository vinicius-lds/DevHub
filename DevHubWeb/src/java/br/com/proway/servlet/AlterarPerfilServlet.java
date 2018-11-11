package br.com.proway.servlet;

import br.com.proway.bean.UsuarioBean;
import br.com.proway.dao.UsuarioDao;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

/**
 * @author Vinícius Luis da Silva
 */
@MultipartConfig
public class AlterarPerfilServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UsuarioBean usuarioLogado = (UsuarioBean) request.getSession().getAttribute("sessaoUsuario");
        
        request.setAttribute("imagem", "RecuperarImagemServlet?tipo=usuario&id=" + usuarioLogado.getId());
        request.setAttribute("nomeUsuario", usuarioLogado.getUsuario());
        request.setAttribute("nomeCompleto", usuarioLogado.getNome());
        request.setAttribute("email", usuarioLogado.getEmail());
        request.setAttribute("pergunta1", usuarioLogado.getPergunta1());
        request.setAttribute("resposta1", usuarioLogado.getResposta1());
        request.setAttribute("pergunta2", usuarioLogado.getPergunta2());
        request.setAttribute("resposta2", usuarioLogado.getResposta2());
        
        request.getRequestDispatcher("/usuarios/alterar_perfil.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        int id = ((UsuarioBean) request.getSession().getAttribute("sessaoUsuario")).getId();
        String nomeUsuario = request.getParameter("nome-de-usuario");
        String nomeCompleto = request.getParameter("nome-completo");
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");
        String pergunta1 = request.getParameter("pergunta1");
        String pergunta2 = request.getParameter("pergunta2");
        String resposta1 = request.getParameter("resposta1");
        String resposta2 = request.getParameter("resposta2");
        Part imagem = request.getPart("img-perfil");
        request.setAttribute("erro", UsuarioDao.alterarUsuario(id, nomeUsuario, nomeCompleto, senha, email, pergunta1, resposta1, pergunta2, resposta2, imagem));
        
        processRequest(request, response);
    }
    
}
