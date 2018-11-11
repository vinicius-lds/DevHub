package br.com.proway.servlet;

import br.com.proway.bean.Projeto;
import br.com.proway.bean.UsuarioBean;
import br.com.proway.connection.ConnectionFactory;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Vinícius Luis da Silva
 */
@WebServlet(name = "chat", urlPatterns = {"/chat"})
public class ChatServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UsuarioBean usuarioLogado = (UsuarioBean) request.getSession().getAttribute("sessaoUsuario");
        if (usuarioLogado == null) {
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }

        //1 - idProjeto, 2 - nome_projeto, 3 - msg_nao_lida, 4 - ultima_mensagem, 5 - ultimo_autor, 6 - id_ultimo_autor
        String sql = "SELECT @idProjeto := relacao_projeto_usuario.idProjeto, projeto.nome_projeto, relacao_projeto_usuario.msg_nao_lida, "
                + "(SELECT mensagem.mensagem FROM mensagem WHERE mensagem.idProjeto = @idProjeto ORDER BY mensagem.data DESC LIMIT 1) ultima_mensagem, "
                + "(SELECT usuarios.nome FROM usuarios INNER JOIN mensagem ON mensagem.idUsuario = usuarios.id WHERE mensagem.idProjeto = @idProjeto ORDER BY mensagem.data DESC LIMIT 1) ultimo_autor, "
                + "(SELECT usuarios.id FROM usuarios INNER JOIN mensagem ON mensagem.idUsuario = usuarios.id WHERE mensagem.idProjeto = @idProjeto ORDER BY mensagem.data DESC LIMIT 1) id_ultimo_autor "
                + "FROM relacao_projeto_usuario "
                + "INNER JOIN projeto ON relacao_projeto_usuario.idProjeto = projeto.id "
                + "INNER JOIN usuarios ON relacao_projeto_usuario.idUsuario = usuarios.id AND usuarios.id = ? "
                + "ORDER BY idProjeto";
        
        List<Projeto> projetos = new ArrayList<>();
        try {
            PreparedStatement pstmt = ConnectionFactory.obterConexao().prepareCall(sql);
            pstmt.setInt(1, usuarioLogado.getId());
            pstmt.execute();
            ResultSet rs = pstmt.getResultSet();
            Projeto projeto;

            while (rs.next()) {
                projeto = new Projeto();
                projetos.add(projeto);

                projeto.setId(rs.getInt(1));
                projeto.setNome(rs.getString(2));
                projeto.setMensagensNaoLidas(rs.getInt(3));
                projeto.setUltimaMensagem(rs.getString(4));
                projeto.setUltimoAutor(rs.getString(5));
                projeto.setIdUltimoAutor(rs.getInt(6));
                projeto.setImagem("RecuperarImagemServlet?tipo=projeto&id=" + projeto.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        request.setAttribute("projetos", projetos);
        request.setAttribute("idUsuario", usuarioLogado.getId());
        request.setAttribute("imagem", usuarioLogado.getImagem());
        request.setAttribute("imagem", "RecuperarImagemServlet?tipo=usuario&id=" + usuarioLogado.getId());
        request.setAttribute("nome", usuarioLogado.getNome());
        request.getRequestDispatcher("chat.jsp").forward(request, response);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
