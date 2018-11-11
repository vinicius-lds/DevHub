package br.com.proway.controller.login;

import br.com.proway.bean.login.Usuario;
import br.com.proway.main.Main;
import static br.com.proway.main.Main.STAGE_PRINCIPAL;
import br.com.proway.util.FXMLFile;
import br.com.proway.util.FXUtil;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * @author Larissa Amanda Junges
 */
public class RecuperacaoDeSenhaController implements Initializable {
    
    private Usuario user;
    
    @FXML private Label lbl_p1;
    @FXML private JFXTextField lbl_r1;
    @FXML private Label lbl_p2;
    @FXML private JFXTextField lbl_r2;
    
    @FXML
    void btn_mudancaSenhaOnAction(ActionEvent event) {
        if(!user.getResposta1().equals(lbl_r1.getText())) {
            FXUtil.paintInvalid(lbl_r1);
            return;
        }
        if(!user.getResposta2().equals(lbl_r2.getText())) {
            FXUtil.paintInvalid(lbl_r2);
            return;
        }
        goToPrimeiroLogin();
    }

    @FXML
    void btn_voltarOnAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLFile.LOGIN));
            AnchorPane ap = loader.load();
            LoginController controller = loader.getController();

            Parent root = ap;
            Scene s = new Scene(root);
            Main.STAGE_PRINCIPAL.close();
            Main.STAGE_PRINCIPAL = new Stage();
            STAGE_PRINCIPAL.initStyle(StageStyle.TRANSPARENT);
            Main.STAGE_PRINCIPAL.setScene(s);
            s.setFill(Color.TRANSPARENT);
            Main.STAGE_PRINCIPAL.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public Usuario getUser() {
        return user;
    }

    public void setUser(Usuario user) {
        this.user = user;
        lbl_p1.setText(user.getPergunta1());
        lbl_p2.setText(user.getPergunta2());
    }
    
    private void goToPrimeiroLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLFile.PRIMEIRO_LOGIN));

            AnchorPane ap = loader.load();
            PrimeiroLoginController controller = loader.getController();

            controller.setUsuario(user);
            Parent root = ap;
            Scene s = new Scene(root);
            Main.STAGE_PRINCIPAL.close();
            Main.STAGE_PRINCIPAL = new Stage();
            STAGE_PRINCIPAL.initStyle(StageStyle.TRANSPARENT);
            Main.STAGE_PRINCIPAL.setScene(s);
            s.setFill(Color.TRANSPARENT);
            Main.STAGE_PRINCIPAL.show();
        } catch (Exception e) {}
    }
    
}
