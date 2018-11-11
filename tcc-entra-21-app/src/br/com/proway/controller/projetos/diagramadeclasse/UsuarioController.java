package br.com.proway.controller.projetos.diagramadeclasse;

import br.com.proway.bean.login.Usuario;
import br.com.proway.util.FXMLFile;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author Vinícius Luis da Silva
 */
public class UsuarioController implements Initializable {

    @FXML protected AnchorPane ap_usuario;
    @FXML protected ImageView iv_usuario;
    @FXML protected Label lbl_usuario;
    
    private Usuario usuario;
    private CadastroDeProjetoController root;

    public UsuarioController() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLFile.USUARIO));
            loader.setController(this);
            loader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.ap_usuario.onMouseClickedProperty().set(e -> {
            this.root.mudarSelecao(this);
        });
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        this.lbl_usuario.setText(this.usuario.getNome() + "(" + this.usuario.getUsuario() + ")");
        this.iv_usuario.setImage(new Image(this.usuario.getImagem()));
    }

    public CadastroDeProjetoController getRoot() {
        return root;
    }

    public void setRoot(CadastroDeProjetoController root) {
        this.root = root;
    }
    
}
