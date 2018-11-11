package br.com.proway.controller.main;

import br.com.proway.controller.paginasweb.PaginaWebController;
import br.com.proway.controller.projetos.diagramadeclasse.CadastroDeProjetoController;
import br.com.proway.controller.projetos.diagramadeclasse.DiagramaDeClasseController;
import br.com.proway.controller.projetos.meusprojetos.RepositorioController;
import br.com.proway.controller.projetos.meusprojetos.MeusProjetosController;
import br.com.proway.dao.UsuarioDAO;
import br.com.proway.main.Main;
import br.com.proway.util.FXMLFile;
import br.vo.Condicao;
import com.jfoenix.controls.JFXButton;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * FXMLFile Controller class
 *
 * @author Vinícius Luis da Silva
 */
public class MainController implements Initializable {

    @FXML private AnchorPane ap_main;
    @FXML private AnchorPane ap_chatNotificacao;
    @FXML private Label lbl_projetos;
    @FXML private Label lbl_diagramas;
    @FXML private Label lbl_chat;
    @FXML private ImageView iv_perfil;
    @FXML private AnchorPane ap_options;
    @FXML private Hyperlink hl_editarPerfil;
    @FXML private Hyperlink hl_cadastrarUsuario;
    @FXML private Hyperlink hl_usuarios;
    @FXML private Hyperlink hl_novoProjeto;
    @FXML private JFXButton btn_sair;
    
    private boolean proSelected = false;
    private boolean diaSelected = false;
    private boolean chaSelected = false;
    
    private DiagramaDeClasseController ddcAtual;
    private RepositorioController repoAtual;
    private Service<Void> threadChat;
    
    @FXML
    void ap_mainMouseEntered(MouseEvent event) {
        
    }
    
    @FXML
    void lbl_chatMouseClicked(MouseEvent event) {
        this.chaSelected = true;
        this.diaSelected = false;
        this.proSelected = false;
        lbl_diagramas.setStyle("-fx-opacity: 0.75");
        lbl_projetos.setStyle("-fx-opacity: 0.75");
        ((PaginaWebController)this.setMainContent(FXMLFile.PAGINA_WEB)).setPage("chat");
        this.ap_chatNotificacao.setVisible(false);
    }

    @FXML
    void lbl_chatMouseEntered(MouseEvent event) {
        lbl_chat.setStyle("");
    }

    @FXML
    void lbl_chatMouseExited(MouseEvent event) {
        lbl_chat.setStyle(chaSelected ? "" : "-fx-opacity: 0.75");
    }

    @FXML
    void lbl_diagramasMouseClicked(MouseEvent event) {
        this.startThreadChat();
        this.chaSelected = false;
        this.diaSelected = true;
        this.proSelected = false;
        lbl_chat.setStyle("-fx-opacity: 0.75");
        lbl_projetos.setStyle("-fx-opacity: 0.75");
        if(ddcAtual != null) {
            this.setMainContent(ddcAtual.ap_diagramaDeClasse);
        } else {
            ddcAtual = (DiagramaDeClasseController)this.setMainContent(FXMLFile.DIAGRAMA_DE_CLASSE);
        }
    }

    @FXML
    void lbl_diagramasMouseEntered(MouseEvent event) {
        lbl_diagramas.setStyle("");
    }

    @FXML
    void lbl_diagramasMouseExited(MouseEvent event) {
        lbl_diagramas.setStyle(diaSelected ? "" : "-fx-opacity: 0.75");
    }

    @FXML
    void lbl_projetosMouseClicked(MouseEvent event) {
        this.startThreadChat();
        this.chaSelected = false;
        this.diaSelected = false;
        this.proSelected = true;
        lbl_diagramas.setStyle("-fx-opacity: 0.75");
        lbl_chat.setStyle("-fx-opacity: 0.75");
        if(this.repoAtual != null) {
            this.setMainContent(this.repoAtual.ap_repo);
        } else {
            ((MeusProjetosController)this.setMainContent(FXMLFile.MEUS_PROJETOS)).setRoot(this);
        }
    }

    @FXML
    void lbl_projetosMouseEntered(MouseEvent event) {
        lbl_projetos.setStyle("");
    }

    @FXML
    void lbl_projetosMouseExited(MouseEvent event) {
        lbl_projetos.setStyle(proSelected ? "" : "-fx-opacity: 0.75");
    }
    
    @FXML
    void img_engrenagemMouseClicked(MouseEvent event) {
        
    }

    public Initializable setMainContent(String arquivo) {
        try {
            System.out.println(arquivo);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(arquivo));
            AnchorPane ap = loader.load();
            
            this.setMainContent(ap);
            
            return loader.getController();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void setMainContent(AnchorPane ap) {
        ap_main.getChildren().clear();
        ap_main.getChildren().add(ap);
    }
    
    public void setMainContent(ScrollPane sp) {
        ap_main.getChildren().clear();
        ap_main.getChildren().add(sp);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.setMainContent(FXMLFile.DIAGRAMA_DE_CLASSE);
        Main.MAIN_CONTROLLER = this;
        threadChat = new Service() {
            @Override
            protected Task createTask() {
                return new Task() {
                    @Override
                    protected Void call() throws Exception {
                        while(true) {
                            Condicao c = new Condicao();
                            c.where();
                            c.equals("idUsuario", Main.USER.getId());
                            List<Object[]> result = Main.DB_CONNECTION.select(new String[] {"msg_nao_lida"}, "relacao_projeto_usuario", c).toArrayList();
                            for(Object[] r: result) {
                                if(Integer.parseInt(r[0].toString()) > 0) {
                                    ap_chatNotificacao.setVisible(true);
                                    return null;
                                }
                            }
                            Thread.sleep(5000);
                        }
                    }
                };
            }
        };
        this.startThreadChat();
        this.inicializarIvPerfil();
        this.btn_sair.onActionProperty().set(e -> {
            try {
                Files.deleteIfExists(Paths.get("temp/login.dat"));
            } catch (Exception err) {
                err.printStackTrace();
            }
            Main.STAGE_PRINCIPAL.close();
        });
    }

    private void startThreadChat() {
        if(threadChat.isRunning()) {
            return;
        }
        threadChat.restart();
    }

    public void setRepositorioIniciado(RepositorioController rc) {
        this.repoAtual = rc;
    }

    private void inicializarIvPerfil() {
        
        iv_perfil.setImage(new Image(Main.USER.getImagem()));
        iv_perfil.onMouseClickedProperty().set(e -> {
            ap_options.setVisible(true);
        });
        ap_options.onMouseExitedProperty().set(e -> {
            ap_options.setVisible(false);
        });
        hl_editarPerfil.onMouseClickedProperty().set(e -> {
            ((PaginaWebController)setMainContent(FXMLFile.PAGINA_WEB)).setPage("alterar_perfil");
        });
        VBox vb = (VBox) this.ap_options.getChildren().get(0);
        if(Main.USER.getNivelDePermissao() > 1) {
            hl_cadastrarUsuario.onMouseClickedProperty().set(e -> {
                ((PaginaWebController)setMainContent(FXMLFile.PAGINA_WEB)).setPage("cadastro_usuarios");
            });
            if(Main.USER.getNivelDePermissao() > 2) {
                hl_usuarios.onMouseClickedProperty().set(e -> {
                    ((PaginaWebController)setMainContent(FXMLFile.PAGINA_WEB)).setPage("usuarios");
                });
                hl_novoProjeto.onMouseClickedProperty().set(e -> {
                    CadastroDeProjetoController cdp = (CadastroDeProjetoController) Main.MAIN_CONTROLLER.setMainContent(FXMLFile.CADASTRO_DE_PROJETO);
                    cdp.setUsuariosDisponiveis(UsuarioDAO.getUsuariosValidos());
                });
            } else {
                vb.getChildren().remove(hl_novoProjeto);
                vb.getChildren().remove(hl_usuarios);
            }
        } else {
            vb.getChildren().remove(hl_novoProjeto);
            vb.getChildren().remove(hl_usuarios);
            vb.getChildren().remove(hl_cadastrarUsuario);
        }
    }

}
