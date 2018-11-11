package br.com.proway.controller.projetos.meusprojetos;

import br.com.proway.bean.login.Projeto;
import br.com.proway.controller.projetos.meusprojetos.MeusProjetosController;
import br.com.proway.bean.login.Usuario;
import br.com.proway.controller.login.PrimeiroLoginController;
import br.com.proway.controller.main.MainController;
import br.com.proway.controller.projetos.diagramadeclasse.CadastroDeProjetoController;
import br.com.proway.main.Main;
import static br.com.proway.main.Main.STAGE_PRINCIPAL;
import br.com.proway.util.FXMLFile;
import br.com.proway.vo.RepositorioGit;
import br.vo.Condicao;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import java.io.File;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * FXML Controller class
 *
 * @author Vinícius Luis da Silva e Larissa Amanda Junges
 */
public class RepositorioController implements Initializable {

    protected RepositorioGit repo;
    private List<File> files;
    private File currentDirectory;
    private List<String> branches;
    private String currentBranch;
    private ProgressBar pb;
    private AnchorPane ap_inputCredentials;
    private TextField tf_user;
    private PasswordField tf_pass;
    private AnchorPane ap;
    private TextField tf_message;
    private Label lbl;
    private int projectId;
    private String url;
    protected List<File> newFiles;
    protected List<File> conflicts;
    protected List<File> modified;
    protected List<File> toDelete;
    protected List<File> selectedFiles;
    protected final List<FileController> selected = new ArrayList<>();
    private MeusProjetosController root;
    private Projeto projeto;
    private Image img; 

    @FXML private JFXButton btn_voltar;
    @FXML private JFXComboBox<String> cb_branches;
    @FXML private JFXButton btn_sinc;
    @FXML private JFXButton btn_novoArquivo;
    @FXML private JFXButton btn_novaPasta;
    @FXML private JFXButton btn_editarProjeto;
    @FXML private JFXButton btn_editarRepo;
    @FXML private VBox vb_files;
    @FXML private GridPane gp_file;
    @FXML private ScrollPane sp_arquivos;
    @FXML private JFXButton btn_status;
    @FXML public AnchorPane ap_repo;

    @FXML
    void btn_redefinirLocalOnAction(ActionEvent event) {
        try {
            DirectoryChooser dc = new DirectoryChooser();
            dc.setTitle("Abrir Repositorio");
            File file = dc.showDialog(Main.STAGE_PRINCIPAL);
            if(file == null) {
                return;
            }
//            this.setProjeto(1);
            this.branches = new ArrayList<>();
            setRepositorio(file, this.url);
            Files.deleteIfExists(Paths.get("data" + File.separatorChar + "repo" + File.separatorChar + this.projectId + ".dat"));
            this.vb_files.getChildren().clear();
            this.cb_branches.getSelectionModel().clearSelection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    void btn_remotoOnAction(ActionEvent event) {
        RepositorioRemotoController rrc = (RepositorioRemotoController)
                Main.MAIN_CONTROLLER.setMainContent(FXMLFile.REPOSITORIO_REMOTO);
        rrc.setUrl(url);
        rrc.setLocal(this);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        pb = new ProgressBar();
        lbl = new Label();
        lbl.setAlignment(Pos.CENTER);
        pb.setMaxHeight(10);
        pb.setPrefHeight(10);
        pb.setMinHeight(10);
//        this.setProjeto(1);
        this.branches = new ArrayList<>();        
        this.cb_branches.onActionProperty().set((e) -> {
            if(cb_branches.getSelectionModel().getSelectedItem() == null) {
                vb_files.getChildren().clear();
            } else {
                currentBranch = cb_branches.getSelectionModel().getSelectedItem();
                currentDirectory = new File(repo.getRepositorio() + File.separatorChar + currentBranch);
                updateFiles(true);
            }
        });
        this.inicializarStatusButton();
        this.inicializarSincButton();
        this.inicializarInputCredentialsPane();
        this.inicializarCommitInput();
        this.inicializarBtnVoltar();
        this.inicializarBtnNovoArquivo();
        this.inicializarBtnNovaPasta();
        this.inicializarBtnEditarProjeto();
    }
    
    public void setAllFilesFromDirectorySelected(File directory, boolean value) {
        List<File> files = new ArrayList<>();
        String dirAbsolutePath = directory.toPath().toAbsolutePath().toString();
        String filAbsolutePath;
        for(File f: newFiles) {
            System.out.println(f);
            filAbsolutePath = f.getAbsolutePath();
            if(filAbsolutePath.startsWith(dirAbsolutePath + File.separatorChar)) {
                files.add(f);
            }
        }
        for(File f: conflicts) {
            System.out.println(f);
            filAbsolutePath = f.getAbsolutePath();
            if(filAbsolutePath.startsWith(dirAbsolutePath + File.separatorChar)) {
                files.add(f);
            }
        }
        for(File f: modified) {
            System.out.println(f);
            filAbsolutePath = f.getAbsolutePath();
            if(filAbsolutePath.startsWith(dirAbsolutePath + File.separatorChar)) {
                files.add(f);
            }
        }
        if(value) {
            this.selectedFiles.addAll(files);
        } else {
            this.selectedFiles.removeAll(files);
        }
    }
    
    public void inicializarCommitInput() {
        ap = new AnchorPane();
        tf_message = new TextField();
        tf_message.setPromptText("Mensagem do Commit");
        AnchorPane.setRightAnchor(tf_message, 0.0);
        AnchorPane.setLeftAnchor(tf_message, 0.0);
        tf_message.setAlignment(Pos.CENTER);
        ap.getChildren().add(tf_message);
    }
    
    public void inicializarInputCredentialsPane() {
        AnchorPane.setRightAnchor(pb, 0.0);
        AnchorPane.setLeftAnchor(pb, 0.0);
        ap_inputCredentials = new AnchorPane();
        tf_user = new TextField();
        tf_pass = new PasswordField();
        tf_user.setPromptText("Nome de Usuário");
        tf_pass.setPromptText("Senha");
        AnchorPane.setRightAnchor(tf_user, 0.0);
        AnchorPane.setLeftAnchor(tf_user, 0.0);
        AnchorPane.setTopAnchor(tf_pass, 30.0);
        AnchorPane.setRightAnchor(tf_pass, 0.0);
        AnchorPane.setLeftAnchor(tf_pass, 0.0);
        tf_pass.setAlignment(Pos.CENTER);
        tf_user.setAlignment(Pos.CENTER);
        ap_inputCredentials.getChildren().add(tf_user);
        ap_inputCredentials.getChildren().add(tf_pass);
    }
    
    public void inicializarSincButton() {
        this.btn_sinc.onActionProperty().set(e -> {
            String branch = cb_branches.getSelectionModel().getSelectedItem();
            if(branch == null) { return; }
            cb_branches.setDisable(true);
            
            if(this.selectedFiles.isEmpty()) {
                this.sinc("mensagem");
                return;
            }
            
            vb_files.getChildren().add(0, ap);
            tf_message.requestFocus();
            tf_message.onKeyPressedProperty().set(event -> {
                if(event.getCode() == KeyCode.ENTER) {
                    if(!tf_message.getText().isEmpty()) {
                        vb_files.getChildren().remove(ap);
                        this.sinc(tf_message.getText());
                    } else {
                        cb_branches.setDisable(false);
                    }                    
                }
            });
            tf_message.focusedProperty().addListener((obs, ov, nv) -> {
                if(!nv) {
                    vb_files.getChildren().remove(ap);
                    cb_branches.setDisable(false);
                }
            });
        });
    }
    
    public void sinc(String msg) {
        String branch = cb_branches.getSelectionModel().getSelectedItem();
        //if(selectedFiles.isEmpty() && toDelete.isEmpty()) { return; }
//        vb_files.getChildren().add(0, ap_inputCredentials);
        
//        tf_user.onKeyPressedProperty().set(this.getUserPassListener(msg, branch));
//        tf_pass.onKeyPressedProperty().set(this.getUserPassListener(msg, branch));
        
//        String user = tf_user.getText();
//        String pass = tf_pass.getText();
//        if(user.isEmpty() || pass.isEmpty()) {
//            updateFiles();
//            return;
//        }
        vb_files.getChildren().clear();
        AnchorPane ap = new AnchorPane(pb);
        AnchorPane.setRightAnchor(pb, 0.0);
        AnchorPane.setLeftAnchor(pb, 0.0);
        vb_files.getChildren().add(0, ap);
        Service<Boolean> servico = new Service() {
            @Override
            protected Task createTask() {
                return new Task() {
                    @Override
                    protected Boolean call() throws Exception {
                        try {
//                            if(selectedFiles.isEmpty() && toDelete.isEmpty()) { 
//                                repo.pull(branch, projeto.getUserGit(), projeto.getPassGit());
//                            } else {
                                conflicts = repo.sinc(branch, selectedFiles, toDelete, msg, 
                                        Main.USER.getNome(), Main.USER.getEmail(),
                                        projeto.getUserGit(), projeto.getPassGit());
                                
//                            }
                            return true;
                        } catch(Exception e) { 
                            return false; 
                        }
                    }
                };
            }
        };
        servico.restart();
        servico.onSucceededProperty().set((e) -> {
            updateFiles(true);
            cb_branches.setDisable(false);
            if(!servico.getValue()) {
                Label lbl = new Label("Ocorreu um erro ao sincronizar os arquivo locais com o repositório remoto! Tente novamente mais tarde! Se o repositório for público, pode ser um erro de autenticação.");
                lbl.setStyle("-fx-text-fill: red");
                AnchorPane ap2 = new AnchorPane(lbl);
                AnchorPane.setLeftAnchor(lbl, 0.0);
                AnchorPane.setRightAnchor(lbl, 0.0);
                lbl.setAlignment(Pos.CENTER);
                vb_files.getChildren().add(0, ap2);
            }
        });

        tf_user.requestFocus();
        
    }
    
    public EventHandler<KeyEvent> getUserPassListener(String msg, String branch) {
        return event -> {
            if(event.getCode() == KeyCode.ENTER) {
                
            }
            
        };
    }
    
    public void inicializarStatusButton() {
        selectedFiles = new ArrayList<>();
        newFiles = new ArrayList<>();
        conflicts = new ArrayList<>();
        modified = new ArrayList<>();
        toDelete = new ArrayList<>();
        
        this.btn_status.onActionProperty().set(e -> {
            updateFiles(true);
        });
    }
    
    public void findRepositorio() {
        ObjectInputStream ois = null;
        try {
            ois = 
                new ObjectInputStream(
                    new BufferedInputStream(
                        new FileInputStream(
                            new File("data" + File.separatorChar + "repo" + File.separatorChar + this.projectId + ".dat")
                        )
                    )
                );
            RepositorioGit rg = (RepositorioGit) ois.readObject();
            this.repo = rg;
            List<String> b = (List<String>) ois.readObject();
            this.branches = b;
            setRepositorio(new File(rg.getRepositorio()), url);
            //ois.close();
        } catch (Exception e) {
            //e.printStackTrace();
            DirectoryChooser dc = new DirectoryChooser();
            dc.setTitle("Abrir Repositorio");
            File file = dc.showDialog(Main.STAGE_PRINCIPAL);
            if(file == null) {
                return;
            }
            try {
                setRepositorio(file, this.url);
            } catch (Exception err) {
                err.printStackTrace();
            }
        }
        try {
            ois.close();
        } catch (Exception e) {
        }
        //this.updateFiles();
        this.updateBranches();
        
    }
    
    public void setProjeto(Projeto projeto) {
        this.projectId = projeto.getId();
        this.url = projeto.getRepositorioGit();
        this.projeto = projeto;
        this.findRepositorio();
    }
    
    public void setRepositorio(File repo, String url) throws NoSuchFileException {
        try {
            this.repo = new RepositorioGit(repo.getAbsolutePath(), url);
            //this.currentDirectory = new File(this.repo.getRepositorio() + "/" + currentBranch);
            //this.updateFiles();
        } catch(NoSuchFileException nsfe) {   
            throw new NoSuchFileException(nsfe.getFile());
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public void status() {
        if(cb_branches.getItems() == null || cb_branches.getItems().isEmpty()) {
            this.updateBranches();
            return;
        }
        String branch = cb_branches.getSelectionModel().getSelectedItem();
        if(branch == null) {
            return;
        }
        try {
            ArrayList<File>[] aux = repo.changes(branch);
            modified = aux[0];
            newFiles = aux[1];
            conflicts = aux[2];
            toDelete = aux[3];
            selectedFiles.clear();
            selectedFiles.addAll(modified);
            selectedFiles.addAll(newFiles);
            selectedFiles.addAll(conflicts);
        } catch (Exception err) {
            err.printStackTrace();
        }
    }
    
    public void updateFiles(boolean status) {
        ObservableList<Node> nodes = this.vb_files.getChildren();
        nodes.clear();
        if(status) { status(); }
        if(this.currentDirectory == null) { return; }
        if(!this.currentDirectory.exists()) {
            try{
                AnchorPane ap = new AnchorPane();
                ap.getChildren().add(lbl);
                ap.getChildren().add(pb);
                AnchorPane.setRightAnchor(lbl, 0.0);
                AnchorPane.setLeftAnchor(lbl, 0.0);
                AnchorPane.setTopAnchor(lbl, 10.0);
                AnchorPane.setRightAnchor(pb, 0.0);
                AnchorPane.setLeftAnchor(pb, 0.0);
                vb_files.getChildren().add(ap);
                ap.setVisible(true);
                pb.setVisible(true);
                lbl.setVisible(true);
                Service<Boolean> servico = new Service() {
                    @Override
                    protected Task createTask() {
                        return new Task() {
                            @Override
                            protected Boolean call() throws Exception {
                                try {
                                    updateMessage("Clonando a branch " + currentBranch + " para a sua máquina...");
                                    repo.cloneBranch(currentBranch, projeto.getUserGit(), projeto.getPassGit());
                                    return true;
                                }catch(Exception e) {
                                    updateMessage("Ocorreu um erro e não foi possivel clonar a branch " + currentBranch + " para sua máquina!");
                                    e.printStackTrace();
                                    //repo.deleteFolder(new File(currentDirectory + "/" + currentBranch));
                                    return false;
                                }
                            }
                        };
                    }
                };
                lbl.textProperty().bind(servico.messageProperty());
                pb.progressProperty().bind(servico.progressProperty());
                servico.onSucceededProperty().set((e) -> {
                    if(servico.getValue()) {
                        lbl.setVisible(false);
                    }
                    pb.setVisible(false);
                    updateFiles(true);
                });
                servico.restart();
            } catch(Exception e) {
                e.printStackTrace();
                cb_branches.getSelectionModel().clearSelection();
            }
            return;
        }
        files = this.repo.listBranch(this.currentBranch);
        List<File> content = Arrays.asList(this.currentDirectory.listFiles());
        nodes.clear();
        List<FileController> fileNodes = new ArrayList();
        boolean isBranch = false;
        for(File file: content) {
            if(file.toString().endsWith(File.separatorChar + ".git")) {
                isBranch = true;
                continue;
            }
            FileController fc = new FileController();
            fc.root = this;
            fileNodes.add(fc);
            try{
                fc.setFile(file.toString());
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        Collections.sort(fileNodes);
        for(FileController fc: fileNodes) {
            nodes.add(fc.ap_principal);
        }
        if(!isBranch) {
            FileController fc = new FileController();
            fc.root = this;
            nodes.add(0, fc.ap_principal);
            try{
                fc.setFile(this.currentDirectory.getParent(), "..");
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public void setCurrentDirectory(File dir) {
        currentDirectory = new File(dir.toString());
        updateFiles(false);
    }
    
    public void updateBranches() {
        cb_branches.setItems(FXCollections.observableArrayList(branches));
        Service<Boolean> servico = new Service() {
            @Override
            protected Task createTask() {
                return new Task() {
                    @Override
                    protected Boolean call() throws Exception {
                        try {
                            branches = repo.getBranches(projeto.getUserGit(), projeto.getPassGit());
                            return true;
                        }catch(Exception e) {
                            e.printStackTrace();
                            return false;
                        }
                    }
                };
            }
        };
        servico.restart();
        servico.onSucceededProperty().set((e) -> {
            if(servico.getValue()) {
                cb_branches.setItems(FXCollections.observableArrayList(branches));
                try {
                    Path p = Paths.get("data" + File.separatorChar + "repo" + File.separatorChar + this.projectId + ".dat");
                    Files.deleteIfExists(p);
                    Files.createFile(p);
                    ObjectOutputStream oos = 
                            new ObjectOutputStream(
                                new BufferedOutputStream(
                                    new FileOutputStream(
                                        p.toFile()
                                    )
                                )
                            );
                    oos.writeObject(this.repo);
                    oos.writeObject(this.branches);
                    oos.close();
                } catch (Exception err) {
                    err.printStackTrace();
                }
            } else {
                Label lbl = new Label("Não foi possível carregar o repositório! Verifique sua conexão com a internet e certifique-se de que as credenciais estão corretas!");
                vb_files.getChildren().add(lbl);
                AnchorPane.setLeftAnchor(lbl, 0.0);
                AnchorPane.setTopAnchor(lbl, 0.0);
                AnchorPane.setRightAnchor(lbl, 0.0);
                lbl.setStyle("-fx-text-fill: red");
                lbl.setAlignment(Pos.CENTER);
            }
        });
    }

    private void inicializarBtnVoltar() {
        this.btn_voltar.onActionProperty().set(e -> {
            this.root.getRoot().setRepositorioIniciado(null);
            this.root.getRoot().setMainContent(root.sp_projetos);
        });
    }
    
    public void setRoot(MeusProjetosController root) {
        this.root = root;
    }

    private void inicializarBtnNovoArquivo() {
        this.btn_novoArquivo.onActionProperty().set(e -> {
            if(this.currentDirectory == null) return;
            File newFile = new File(currentDirectory.toPath().toAbsolutePath().toString() + File.separatorChar + "Novo Arquivo.txt");
            int i = 1;
            while(newFile.exists()) {
                newFile = new File(currentDirectory.toPath().toAbsolutePath().toString() + File.separatorChar + "Novo Arquivo ("+i+").txt");
                i++;
            }
            try {
                Files.createFile(newFile.toPath().toAbsolutePath());
            } catch (Exception err) {
                err.printStackTrace();
            }
            this.updateFiles(false);
        });
    }

    private void inicializarBtnNovaPasta() {
        this.btn_novaPasta.onActionProperty().set(e -> {
            if(this.currentDirectory == null) return;
            File newFile = new File(currentDirectory.toPath().toAbsolutePath().toString() + File.separatorChar + "Nova Pasta");
            int i = 1;
            while(newFile.exists()) {
                newFile = new File(currentDirectory.toPath().toAbsolutePath().toString() + File.separatorChar + "Nova Pasta ("+i+")");
                i++;
            }
            try {
                Files.createDirectory(newFile.toPath().toAbsolutePath());
            } catch (Exception err) {
                err.printStackTrace();
            }
            this.updateFiles(false);
        });
    }

    private void inicializarBtnEditarProjeto() {
        if(Main.USER.getNivelDePermissao() < 3) {
            this.btn_editarProjeto.setDisable(true);
            return;
        }
        this.btn_editarProjeto.onActionProperty().set(e -> {
            CadastroDeProjetoController cdpc = (CadastroDeProjetoController)Main.MAIN_CONTROLLER.setMainContent(FXMLFile.CADASTRO_DE_PROJETO);
            cdpc.setProjeto(projeto, img);
        });
    }
    
    public void setImg(Image img) {
        this.img = img;
    }
    
}
