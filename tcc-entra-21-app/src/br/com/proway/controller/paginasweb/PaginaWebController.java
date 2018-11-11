package br.com.proway.controller.paginasweb;

import br.com.proway.main.Main;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebView;

/**
 * FXML Controller class
 *
 * @author Vinícius Luis da Silva
 */
public class PaginaWebController implements Initializable {
    
    @FXML private WebView wv_chat;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {}    
    
    public void setPage(String page) {
        System.out.println(Main.WEBSITE + "logar.jsp?login=" + Main.USER.getUsuario()+ "&senha=" + Main.USER.getSenha() + "&pagina=" + page);
        wv_chat.getEngine().load(Main.WEBSITE + "logar.jsp?login=" + Main.USER.getUsuario()+ "&senha=" + Main.USER.getSenha() + "&pagina=" + page);
    }
    
}
