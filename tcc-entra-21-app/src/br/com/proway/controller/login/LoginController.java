package br.com.proway.controller.login;

import br.com.proway.bean.login.Usuario;
import br.com.proway.main.Main;
import static br.com.proway.main.Main.STAGE_PRINCIPAL;
import br.com.proway.util.FXMLFile;
import br.com.proway.util.FXUtil;
import br.vo.Condicao;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import com.jfoenix.controls.JFXTextField;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.binding.StringBinding;
import javafx.beans.value.ObservableStringValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * @author Larissa Amanda Junges
 */
public class LoginController implements Initializable {

    private List<Usuario> usuarios;
    private boolean loginErrado;
    private Label msgErro;
    
    @FXML private JFXTextField tf_login;
    @FXML private JFXPasswordField tf_senha;
    @FXML private JFXButton btn_cancelar;
    @FXML private JFXButton btn_entrar;
    @FXML private VBox vb_form;
    @FXML private VBox vb_progresso;
    @FXML private Label lbl_progresso;
    @FXML private ProgressBar pb_progresso;
    @FXML private JFXCheckBox cb_permanecerConectado;

    @FXML
    void btn_cancelarOnAction(ActionEvent event) {
        Main.STAGE_PRINCIPAL.close();
    }

    @FXML
    void btn_entrarOnAction(ActionEvent event) {
        try {
            String usuario = tf_login.getText();
            String senha = tf_senha.getText();
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(senha.getBytes(), 0, senha.length());
            senha = new BigInteger(1, m.digest()).toString(16);
            this.login(usuario, senha);
        } catch (Exception e) {
            e.printStackTrace();
        }
   }
    
     @FXML
    void link_esqueciSenhaOnAction(ActionEvent event) {
        try {
            Condicao c = new Condicao().where().equals("usuario", tf_login.getText());
            usuarios = Main.DB_CONNECTION.select("usuarios", c).setBean(Usuario.class).toArrayList();
            if(usuarios.isEmpty()) {
                showMessage("O usuário " + tf_login.getText() + " não existe!");
                return;
            }
            Usuario user = usuarios.get(0);
            if(user.isPrimeiroLogin()) {
                showMessage("Esta conta ainda não foi inicializada!");
                return;
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLFile.RECUPERACAO_DE_SENHA_FORM));
            AnchorPane ap = loader.load();
            RecuperacaoDeSenhaController controller = loader.getController();

            controller.setUser(user);
            Parent root = ap;
            Scene s = new Scene(root);
            Main.STAGE_PRINCIPAL.close();
            Main.STAGE_PRINCIPAL = new Stage();
            STAGE_PRINCIPAL.initStyle(StageStyle.TRANSPARENT);
            Main.STAGE_PRINCIPAL.setScene(s);
            s.setFill(Color.TRANSPARENT);
            Main.STAGE_PRINCIPAL.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
        
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loginErrado = false;
        
    }
    
    public void verificarCache() {
        try {
            Path p = Paths.get("temp\\login.dat");
            if(Files.exists(p)) {
                ObjectInputStream ios = new ObjectInputStream(new FileInputStream(p.toFile()));
                String user = (String) ios.readObject();
                String pass = (String) ios.readObject();
                this.login(user, pass);
                ios.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void login(String usuario, String senha) {
        Service<Boolean> servico = new Service() {
            @Override
            protected Task createTask() {
                return new Task() {
                    @Override
                    protected Boolean call() throws Exception {
                        try {
                            vb_progresso.setVisible(true);
                            
                            updateMessage("Criptografando senha.");
                            Thread.sleep(500);

                            updateMessage("Autenticando credenciais.");
                            Thread.sleep(500);
                            Condicao c = new Condicao().where().equals("senha", senha).and().equals("usuario", usuario).and().equals("ativo", 1);
                            usuarios = 
                                    Main.DB_CONNECTION.select("usuarios", c)
                                    .setBean(Usuario.class).toArrayList();
                            if (!usuarios.isEmpty()) {
                                updateMessage("Carregando tela principal.");
                                Thread.sleep(500);
                                return true;
                            } else {
                                updateMessage("Falha na autenticação.");
                                Thread.sleep(500);
                                return false;
                            }
                        }catch(Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                };
            }
        };
        lbl_progresso.textProperty().bind(servico.messageProperty());
        pb_progresso.progressProperty().bind(servico.progressProperty());
        servico.restart();
        servico.onSucceededProperty().set((e) -> {
            try {
                if(!servico.getValue()) {
                    showMessage("Usuário e/ou senha inválidos!");
                    return;
                }
                Thread.sleep(500);
                Main.USER = usuarios.get(0);
                if(cb_permanecerConectado.isSelected()) {
                    Path p = Paths.get("temp").toAbsolutePath();
                    if(!Files.exists(p)) {
                        Files.createDirectory(p);
                    }
                    p = Paths.get("temp\\login.dat");
                    Files.deleteIfExists(p);
                    Files.createFile(p);
                    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(p.toFile()));
                    oos.writeObject(usuario);
                    oos.writeObject(senha);
                    oos.close();
                }

                if(usuarios.get(0).isPrimeiroLogin()) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLFile.PRIMEIRO_LOGIN));

                    AnchorPane ap = loader.load();
                    PrimeiroLoginController controller = loader.getController();

                    Usuario user = usuarios.get(0);
                    controller.setUsuario(user);
                    Parent root = ap;
                    Scene s = new Scene(root);
                    Main.STAGE_PRINCIPAL.close();
                    Main.STAGE_PRINCIPAL = new Stage();
                    STAGE_PRINCIPAL.initStyle(StageStyle.TRANSPARENT);
                    Main.STAGE_PRINCIPAL.setScene(s);
                    s.setFill(Color.TRANSPARENT);
                    Main.STAGE_PRINCIPAL.show();
                } else {
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
                }
            } catch (Exception err) {
                err.printStackTrace();
            }
        });
    }
    
    private void showMessage(String str) {
        if(msgErro == null) {
            msgErro = new Label(str);
            msgErro.setStyle("-fx-text-fill: red");
            this.vb_form.getChildren().add(0, msgErro);
            FXUtil.reopenStage();
            FXUtil.paintInvalid(tf_login);
            FXUtil.paintInvalid(tf_senha);
        } else {
            msgErro.setText(str);
        }
        vb_progresso.setVisible(false);
    }
    
}
