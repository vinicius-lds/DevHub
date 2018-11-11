package br.com.proway.controller.login;

import br.com.proway.bean.login.Usuario;
import br.com.proway.main.Main;
import static br.com.proway.main.Main.STAGE_PRINCIPAL;
import br.com.proway.util.FXMLFile;
import br.com.proway.util.FXUtil;
import br.com.proway.util.Patterns;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * FXML Controller class
 *
 * @author Vinícius Luis da Silva
 */
public class PrimeiroLoginController implements Initializable {

    private Usuario user;
    private Label msgErro; 
    private File img;
    
    @FXML private JFXTextField tf_nomeCompleto;
    @FXML private JFXTextField tf_email;
    @FXML private JFXPasswordField tf_senha;
    @FXML private JFXPasswordField tf_confirmarSenha;
    @FXML private JFXComboBox<String> cb_perguntaSecreta1;
    @FXML private JFXTextField tf_resposta1;
    @FXML private JFXComboBox<String> cb_perguntaSecreta2;
    @FXML private JFXTextField tf_resposta2;
    @FXML private Label lbl_username;
    @FXML private VBox vb_form;
    @FXML private ImageView iv_perfil;
    @FXML private Label lbl_mudar;
    @FXML private Label lbl_imagem;
    @FXML private Pane pn_imagem;
    
    @FXML
    void salvarOnAction(ActionEvent event) {
        try {
            user.setNome(tf_nomeCompleto.getText());
            user.setEmail(tf_email.getText());
            if(!tf_senha.getText().equals(tf_confirmarSenha.getText())) {
                FXUtil.paintInvalid(tf_senha);
                FXUtil.paintInvalid(tf_confirmarSenha);
                throw new IllegalArgumentException("As senhas não são iguais!");
            }
            user.setSenha(tf_senha.getText());
            user.setPergunta1(cb_perguntaSecreta1.getSelectionModel().getSelectedItem());
            user.setResposta1(tf_resposta1.getText());
            user.setPergunta2(cb_perguntaSecreta2.getSelectionModel().getSelectedItem());
            user.setResposta2(tf_resposta2.getText());
            user.setImgFile(this.img);
            user.atualizarBanco();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLFile.MAIN));

            AnchorPane ap = loader.load();
            Parent root = ap;
            Scene s = new Scene(root);
            Main.STAGE_PRINCIPAL.close();
            Main.STAGE_PRINCIPAL = new Stage();
            Main.STAGE_PRINCIPAL.setMinWidth(736);
            Main.STAGE_PRINCIPAL.setMinHeight(519);
            Main.STAGE_PRINCIPAL.setScene(s);
            Main.STAGE_PRINCIPAL.show();
        } catch(Exception e) {
            if(msgErro == null) {
                Scene s = Main.STAGE_PRINCIPAL.getScene();
                Main.STAGE_PRINCIPAL.close();
                Main.STAGE_PRINCIPAL = new Stage();
                Main.STAGE_PRINCIPAL.initStyle(StageStyle.TRANSPARENT);
                s.setFill(Color.TRANSPARENT);
                Main.STAGE_PRINCIPAL.setScene(s);
                msgErro = new Label(e.getMessage());
                msgErro.setStyle("-fx-text-fill: red");
                this.vb_form.getChildren().add(0, msgErro);
                Main.STAGE_PRINCIPAL.show();
            } else {
                msgErro.setText(e.getMessage());
            }
        }
    }

    @FXML
    void voltarOnAction(ActionEvent event) throws Exception {
        STAGE_PRINCIPAL.close();
        STAGE_PRINCIPAL = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLFile.LOGIN));
        AnchorPane ap = loader.load();
        Scene scene = new Scene(ap);
        STAGE_PRINCIPAL.initStyle(StageStyle.TRANSPARENT);
        STAGE_PRINCIPAL.setScene(scene);
        scene.setFill(Color.TRANSPARENT);
        STAGE_PRINCIPAL.setMinWidth(736);
        STAGE_PRINCIPAL.setMinHeight(519);
        STAGE_PRINCIPAL.show();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.inicializarPerguntas();
        this.inicializarNotNullFieldListeners();
        this.inicializarNotNullComboBoxListeners();
        this.inicializarPasswordFieldsListeners();
        this.tf_email.onKeyReleasedProperty().set(new EmailField(tf_email));
        this.inicializarImagem();
    }
    
    public void setUsuario(Usuario user) {
        this.user = user;
        this.lbl_username.setText(this.user.getUsuario());
        if(!user.isPrimeiroLogin()) {
            tf_nomeCompleto.setText(user.getNome());
            tf_email.setText(user.getEmail());
            tf_resposta1.setText(user.getResposta1());
            tf_resposta2.setText(user.getResposta2());
            cb_perguntaSecreta1.getSelectionModel().select(user.getPergunta1());
            cb_perguntaSecreta2.getSelectionModel().select(user.getPergunta2());
            Platform.runLater(() -> tf_senha.requestFocus());
        }
    }
    
    private void inicializarPerguntas(){
        ArrayList<String> perguntas = new ArrayList();
        perguntas.add("Qual o nome do hospital em que você nasceu?");
        perguntas.add("Qual foi sua primeira linguagem de programação?");
        perguntas.add("Qual seu banco de dados preferido?");
        perguntas.add("Java ou C#?");
        perguntas.add("Pior professor do entra-21?");
        this.cb_perguntaSecreta1.setItems(FXCollections.observableArrayList(perguntas));
        this.cb_perguntaSecreta2.setItems(FXCollections.observableArrayList(perguntas));
    }
    
    private void inicializarNotNullComboBoxListeners() {
        this.cb_perguntaSecreta1.onActionProperty().set(new NotNullComboBox(cb_perguntaSecreta1));
        this.cb_perguntaSecreta2.onActionProperty().set(new NotNullComboBox(cb_perguntaSecreta2));
    }
    
    private void inicializarPasswordFieldsListeners() {
        this.tf_senha.onKeyReleasedProperty().set(new PasswordFields(tf_senha, tf_confirmarSenha));
        this.tf_confirmarSenha.onKeyReleasedProperty().set(new PasswordFields(tf_senha, tf_confirmarSenha));
    }
    
    public void inicializarNotNullFieldListeners() {
        this.tf_email.onKeyReleasedProperty().set(new NotNullField(tf_email));
        this.tf_nomeCompleto.onKeyReleasedProperty().set(new NotNullField(tf_nomeCompleto));
        this.tf_resposta1.onKeyReleasedProperty().set(new NotNullField(tf_resposta1));
        this.tf_resposta2.onKeyReleasedProperty().set(new NotNullField(tf_resposta2));
    }

    private void inicializarImagem() {
        this.img = new File("data/img/img-perfil-default.jpg");
        try {
            this.iv_perfil.setImage(new Image(new FileInputStream(img.getAbsolutePath())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.pn_imagem.setStyle("-fx-cursor: hand;");
        this.pn_imagem.onMouseExitedProperty().set(e -> {
            this.iv_perfil.setStyle("");
            this.lbl_mudar.setVisible(false);
            this.lbl_imagem.setVisible(false);
        });
        this.iv_perfil.onMouseEnteredProperty().set(e -> {
            this.iv_perfil.setStyle("-fx-opacity: 0.5;");
            this.lbl_mudar.setVisible(true);
            this.lbl_imagem.setVisible(true);
        });
        this.pn_imagem.onMouseClickedProperty().set(e -> {
            FileChooser fc = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Immagens JPG", "*.jpg");
            fc.getExtensionFilters().add(extFilter);
            File img = fc.showOpenDialog(STAGE_PRINCIPAL);
            if(img != null) {
                try {
                    this.img = img;
                    this.iv_perfil.setImage(new Image(new FileInputStream(this.img)));
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }
        });
    }
     
    private class NotNullField implements EventHandler<KeyEvent> {

        private JFXTextField comp;

        public NotNullField(JFXTextField comp) {
            this.comp = comp;
        }
        
        @Override
        public void handle(KeyEvent event) {
            if(this.comp.getText().isEmpty()) {
                FXUtil.paintInvalid(comp);
            } else {
                FXUtil.paintValid(comp);
            }
        }
        
    }
    
    private class NotNullComboBox implements EventHandler<ActionEvent> {
        private JFXComboBox<String> comp;

        public NotNullComboBox(JFXComboBox comp) {
            this.comp = comp;
        }
        
        @Override
        public void handle(ActionEvent event) {
            if(comp.getSelectionModel().getSelectedItem().isEmpty()) {
                FXUtil.paintInvalid(comp);
            } else {
                FXUtil.paintValid(comp);
            }
        }
        
    }
    
    private class PasswordFields implements EventHandler<KeyEvent> {

        private JFXPasswordField pass;
        private JFXPasswordField confirm;

        public PasswordFields(JFXPasswordField pass, JFXPasswordField confirm) {
            this.pass = pass;
            this.confirm = confirm;
        }
        
        @Override
        public void handle(KeyEvent event) {
            FXUtil.paintValid(pass);
            if(!pass.getText().equals(confirm.getText())) {
                if(!confirm.getText().isEmpty()) {
                    FXUtil.paintInvalid(confirm);
                }
            } else {
                FXUtil.paintValid(confirm);
            }
        }
        
    }

    private class EmailField implements EventHandler<KeyEvent> {

        private JFXTextField comp;

        public EmailField(JFXTextField comp) {
            this.comp = comp;
        }
        
        @Override
        public void handle(KeyEvent event) {
            if(Patterns.isValidEmail(comp.getText())) {
                FXUtil.paintValid(comp);
            } else {
                FXUtil.paintInvalid(comp);
            }
        }
        
    }
}
