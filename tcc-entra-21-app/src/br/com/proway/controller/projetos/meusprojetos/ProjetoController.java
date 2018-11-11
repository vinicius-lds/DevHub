package br.com.proway.controller.projetos.meusprojetos;

import br.com.proway.bean.login.Projeto;
import br.com.proway.util.FXMLFile;
import br.com.proway.vo.RepositorioGit;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author Vinícius Luis da Silva
 */
public class ProjetoController implements Initializable {

    @FXML protected AnchorPane ap_projeto;
    @FXML protected ImageView iv_projeto;
    @FXML protected Label lbl_projeto;
    
    private MeusProjetosController root;
    private Projeto projeto;
    private Image img;
    
    public ProjetoController() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLFile.PROJETO));
            loader.setController(this);
            loader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.ap_projeto.onMouseClickedProperty().set(e -> {
            if(e.getClickCount() == 2 && e.getButton() == MouseButton.PRIMARY) {
                new LoginGitController(this);
            } else {
                root.select(this);
            }
        });
    }

    public void setProjeto(Projeto projeto) {
        this.projeto = projeto;
        this.lbl_projeto.setText(projeto.getNomeProjeto().isEmpty() ? "sem nome" : projeto.getNomeProjeto());
        this.img = new Image(projeto.getImagem());
        this.iv_projeto.setImage(img);
    }

    public void setRoot(MeusProjetosController aThis) {
        this.root = aThis;
    }
    
    public void carregarRepositorio(String user, String pass) {
        new Service() {
            @Override
            protected Task createTask() {
                return new Task() {
                    @Override
                    protected Void call() throws Exception {
                        Platform.runLater(() -> {
                            if((user.isEmpty() && pass.isEmpty()) || RepositorioGit.isValidCredentials(projeto.getRepositorioGit(), user, pass)) {
                                projeto.setUserGit(user);
                                projeto.setPassGit(pass);
                                RepositorioController rc = (RepositorioController) root.getRoot().setMainContent(FXMLFile.REPOSITORIO);
                                rc.setProjeto(projeto);
                                rc.setImg(img);
                                rc.setRoot(root);
                                root.getRoot().setRepositorioIniciado(rc);
                            } else {
                                Alert a = new Alert(Alert.AlertType.ERROR);
                                a.setTitle("Erro");
                                a.setHeaderText("Erro de autenticação!");
                                a.setContentText("Usuário e/ou senha incorretos!");
                                a.show();
                            }
                        });
                        return null;
                    }
                };
            }
        }.restart();
    }
    
}
