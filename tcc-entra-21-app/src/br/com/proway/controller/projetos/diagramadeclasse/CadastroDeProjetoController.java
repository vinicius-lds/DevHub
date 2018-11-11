package br.com.proway.controller.projetos.diagramadeclasse;

import br.com.proway.bean.login.Projeto;
import br.com.proway.bean.login.Usuario;
import br.com.proway.controller.projetos.meusprojetos.LoginGitController;
import br.com.proway.controller.projetos.meusprojetos.MeusProjetosController;
import br.com.proway.dao.ProjetoDAO;
import br.com.proway.dao.UsuarioDAO;
import br.com.proway.main.Main;
import br.com.proway.util.FXMLFile;
import br.com.proway.util.FXUtil;
import br.com.proway.vo.RepositorioGit;
import br.com.proway.vo.diagramadeclasse.Diagrama;
import br.vo.Condicao;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;

/**
 * FXML Controller class
 *
 * @author Vinícius Luis da Silva
 */
public class CadastroDeProjetoController implements Initializable {

    @FXML private ImageView iv_imagem;
    @FXML private Label lbl_imagem;
    @FXML private JFXTextField tf_repo;
    @FXML private JFXTextField tf_nomeProjeto;
    @FXML private JFXButton btn_code;
    @FXML private JFXButton brn_iniciarProjeto;
    @FXML private JFXButton btn_removerUsuario;
    @FXML private JFXButton btn_addUsuario;
    @FXML private VBox vb_usuariosDisponiveis;
    @FXML private VBox vb_usuariosSelecionados;
    @FXML private Pane pn_lbl;
    
    //private Diagrama diagrama;
    private boolean validRepo = false;
    private File imagemSelecionada = null;
    private RepositorioGit repoTemp;
    private Projeto projeto;
    
    private List<UsuarioController> adicionar = new ArrayList();
    private List<UsuarioController> remover = new ArrayList();
    private List<UsuarioController> adicionados = new ArrayList();
    private List<UsuarioController> disponiveis = new ArrayList();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.projeto = null;
        this.btn_code.setVisible(false);
        this.inicializarAdicao();
        this.inicializarRemocao();
        this.inicializarTfRepo();
        this.inicializarBtnIniciarProjeto();
        this.inicializarIvImagem();
        try {
            this.imagemSelecionada = new File("resources\\br\\com\\proway\\view\\img\\icon\\icon-projeto-default.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void setUsuariosDisponiveis(List<Usuario> usuarios) {
        this.disponiveis.clear();
        UsuarioController uc;
        for(Usuario u: usuarios) {
            uc = new UsuarioController();
            uc.setUsuario(u);
            uc.setRoot(this);
            this.disponiveis.add(uc);
        }
        this.updateListas();
    }
    
    public void setUsuariosAdicionados(List<Usuario> usuarios) {
        this.adicionados.clear();
        UsuarioController uc;
        for(Usuario u: usuarios) {
            uc = new UsuarioController();
            uc.setUsuario(u);
            uc.setRoot(this);
            this.adicionados.add(uc);
        }
        this.updateListas();
    }

    public void setProjeto(Projeto projeto, Image img) {
        this.projeto = projeto;
        imagemSelecionada = null;
        tf_repo.focusedProperty().addListener((obs, ov, nv) -> {});
        tf_repo.setText(projeto.getRepositorioGit());
        tf_repo.setDisable(true);
        tf_nomeProjeto.setText(projeto.getNomeProjeto());
        iv_imagem.setImage(img);
        brn_iniciarProjeto.onActionProperty().set(e -> {});
        brn_iniciarProjeto.setText("Confirmar Alterações");
        new Service() {
            @Override
            protected Task createTask() {
                return new Task() {
                    @Override
                    protected Void call() throws Exception {
                        List<Usuario> aux = UsuarioDAO.getUsuariosFromProject(projeto);
                        Platform.runLater(() -> {
                            setUsuariosAdicionados(aux);
                            setUsuariosDisponiveis(UsuarioDAO.getUsuariosOutsideOfProject(projeto, aux));
                        });
                        brn_iniciarProjeto.onActionProperty().set(e -> {
                            if(tf_nomeProjeto.getText().isEmpty()) {
                                Alert a = new Alert(Alert.AlertType.ERROR);
                                a.setTitle("Erro");
                                a.setHeaderText("Nome de Projeto Inválido!");
                                a.setContentText("O nome de projeto fornecido é inválido!");
                                a.show();
                                return;
                            }
                            projeto.setNomeProjeto(tf_nomeProjeto.getText());
                            if(imagemSelecionada != null) {
                                try {
                                    projeto.setImagem(new FileInputStream(imagemSelecionada));
                                } catch (Exception err) {
                                    err.printStackTrace();
                                }
                            }
                            ProjetoDAO.updateProjeto(projeto, imagemSelecionada, adicionados);
                            ((MeusProjetosController)Main.MAIN_CONTROLLER.setMainContent(FXMLFile.MEUS_PROJETOS)).setRoot(Main.MAIN_CONTROLLER);
                        });
                        return null;
                    }
                };
            }
        }.restart();
        
    }
    
//    private InputStream getImageInputStream() {
//        BufferedImage bImage = SwingFXUtils.fromFXImage(iv_imagem.getImage(), null);
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        try {
//            ImageIO.write(bImage, "jpg", outputStream);
//            byte[] res  = outputStream.toByteArray();
//            InputStream inputStream = new ByteArrayInputStream(res);
//            return inputStream;
//        } catch (IOException err) {
//            err.printStackTrace();
//        }
//        return null;
//    }
    
    public void mudarSelecao(UsuarioController uc) {
        boolean aux = false;
        if(isAdicionado(uc)) {
            if(!remover.remove(uc)) {
                uc.ap_usuario.setStyle("-fx-background-color: blue");
                uc.lbl_usuario.setStyle("-fx-text-fill: white");
                remover.add(uc);
                aux = true;
            } else {
                adicionar.remove(uc);
            }
        } else {
            if(!adicionar.remove(uc)) {
                uc.ap_usuario.setStyle("-fx-background-color: blue");
                uc.lbl_usuario.setStyle("-fx-text-fill: white");
                adicionar.add(uc);
                aux = true;
            } else {
                remover.remove(uc);
            }
        }
        if(!aux) {
            uc.ap_usuario.setStyle("");
            uc.lbl_usuario.setStyle("");
        }
    }
    
    public boolean isAdicionado(UsuarioController uc) {
        for(UsuarioController u: adicionados) {
            if(u == uc) {
                return true;
            }
        }
        return false;
    }
    
    private void inicializarAdicao() {
        this.btn_addUsuario.onActionProperty().set(e -> {
            this.adicionados.addAll(this.adicionar);
            this.disponiveis.removeAll(this.adicionar);
            this.adicionar.clear();
            this.updateListas();
        });
    }

    private void inicializarRemocao() {
        this.btn_removerUsuario.onActionProperty().set(e -> {
            this.disponiveis.addAll(this.remover);
            this.adicionados.removeAll(this.remover);
            this.remover.clear();
            this.updateListas();
        });
    }

    private void updateListas() {
        vb_usuariosDisponiveis.getChildren().clear();
        vb_usuariosSelecionados.getChildren().clear();
        for(UsuarioController uc: this.disponiveis) {
            vb_usuariosDisponiveis.getChildren().add(uc.ap_usuario);
            uc.ap_usuario.setStyle("");
            uc.lbl_usuario.setStyle("");
        }
        for(UsuarioController uc: this.adicionados) {
            vb_usuariosSelecionados.getChildren().add(uc.ap_usuario);
            uc.ap_usuario.setStyle("");
            uc.lbl_usuario.setStyle("");
        }
    }

    private void inicializarTfRepo() {
        this.tf_repo.focusedProperty().addListener((obs, ov, nv) -> {
            Service<Void> servico = new Service() {
                @Override
                protected Task createTask() {
                    return new Task() {
                        @Override
                        protected Void call() throws Exception {
                            if(!nv) {
                                if(RepositorioGit.isValidRemoteRepository(tf_repo.getText())) {
                                    FXUtil.paintValid(tf_repo);
                                    validRepo = true;
                                } else {
                                    FXUtil.paintInvalid(tf_repo);
                                    validRepo = false;
                                }
                            }
                            return null;
                        }
                    };
                }
            };
            servico.restart();
        });
        
    }

    private void inicializarBtnIniciarProjeto() {
        brn_iniciarProjeto.onActionProperty().set(e -> {
            if(!validRepo) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle("Erro");
                a.setHeaderText("Repositório Inválido!");
                a.setContentText("O Link fornecido como repositório git é inválido!");
                a.show();
                return;
            }
            if(this.tf_nomeProjeto.getText().isEmpty()) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle("Erro");
                a.setHeaderText("Nome de Projeto Inválido!");
                a.setContentText("O nome de projeto fornecido é inválido!");
                a.show();
                return;
            }
            ProjetoDAO.cadastrarProjeto(tf_nomeProjeto.getText(), imagemSelecionada, tf_repo.getText(), this.adicionados);
            ((MeusProjetosController)Main.MAIN_CONTROLLER.setMainContent(FXMLFile.MEUS_PROJETOS)).setRoot(Main.MAIN_CONTROLLER);
        });
    }

    private void inicializarIvImagem() {
        this.iv_imagem.onMouseEnteredProperty().set(e -> {
            this.iv_imagem.setStyle("-fx-opacity: 0.75");
            this.pn_lbl.setStyle("-fx-cursor: hand");
            this.lbl_imagem.setVisible(true);
        });
        this.pn_lbl.onMouseExitedProperty().set(e -> {
            this.iv_imagem.setStyle("-fx-opacity: 1");
            this.lbl_imagem.setVisible(false);
        });
        this.pn_lbl.onMouseClickedProperty().set(e -> {
            FileChooser fc = new FileChooser();
            fc.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Imagens", Arrays.asList("JPG", "JPEG", "PNG")));
            File imagem = fc.showOpenDialog(Main.STAGE_PRINCIPAL);
            if(imagem != null) {
                FileInputStream is = null;
                try {
                    is = new FileInputStream(imagem);
                } catch (Exception err) {
                }
                iv_imagem.setImage(new Image(is));
                this.imagemSelecionada = imagem;
            }
        });
    }
    
}
