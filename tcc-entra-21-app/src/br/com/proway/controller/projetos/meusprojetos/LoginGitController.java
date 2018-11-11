package br.com.proway.controller.projetos.meusprojetos;

import br.com.proway.util.FXMLFile;
import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * FXML Controller class
 *
 * @author Vinícius Luis da Silva
 */
public class LoginGitController implements Initializable {

    @FXML private TextField tf_user;
    @FXML private PasswordField tf_pass;
    @FXML private JFXButton btn_canelar;
    @FXML private JFXButton brn_confirmar;
    
    private Stage stage;
    private Initializable root;

    public LoginGitController(Initializable root) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLFile.LOGIN_GIT));
            loader.setController(this);
            stage = new Stage(StageStyle.UTILITY); 
            AnchorPane ap = loader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setAlwaysOnTop(true);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.root = root;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btn_canelar.onActionProperty().set(e -> {
            this.stage.close();
        });
        brn_confirmar.onActionProperty().set(e -> {
            this.stage.close();
            if(this.root instanceof ProjetoController) {
                ((ProjetoController)this.root).carregarRepositorio(tf_user.getText(), tf_pass.getText());
            } else {
                //((CadastroDeProjetoController)this.root).gerarCodigo(this.tf_user.getText(), this.tf_pass.getText());
            }
        });
        tf_pass.onKeyReleasedProperty().set(e -> {
            if(e.getCode() == KeyCode.ENTER) {
                this.stage.close();
                if(this.root instanceof ProjetoController) {
                    ((ProjetoController)this.root).carregarRepositorio(tf_user.getText(), tf_pass.getText());
                } else {
                    //((CadastroDeProjetoController)this.root).gerarCodigo(this.tf_user.getText(), this.tf_pass.getText());
                }
            }
        });
        tf_user.onKeyReleasedProperty().set(e -> {
            if(e.getCode() == KeyCode.ENTER) {
                this.stage.close();
                if(this.root instanceof ProjetoController) {
                    ((ProjetoController)this.root).carregarRepositorio(tf_user.getText(), tf_pass.getText());
                } else {
                    //((CadastroDeProjetoController)this.root).gerarCodigo(this.tf_user.getText(), this.tf_pass.getText());
                }
            }
        });
    }    
    
}
