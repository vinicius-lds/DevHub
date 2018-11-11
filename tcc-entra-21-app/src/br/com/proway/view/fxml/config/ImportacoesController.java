package br.com.proway.view.fxml.config;

import br.com.proway.controller.projetos.diagramadeclasse.DiagramaDeClasseController;
import br.com.proway.util.FXUtil;
import br.com.proway.util.FileUtil;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Vinícius Luis da Silva
 *
 */
public class ImportacoesController implements Initializable {

    @FXML private JFXTextField tf_jdk;
    @FXML private JFXTextField tf_lib;
    @FXML private VBox vb_lib;
    @FXML private JFXButton btn_cancelar;
    @FXML private JFXButton btn_salvar;

    private DiagramaDeClasseController root;
    private HashMap<String, Set> imps;
    private Stage stage;

    public void setRoot(DiagramaDeClasseController root) {
        this.root = root;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.inicializarJdkListener();
        this.inicializarLibListener();
        this.inicializarBtnSalvar();
        this.inicializarBtnCancelar();
        this.updateLibVbox();
        this.imps = new HashMap();
        try {
            imps.put("jdk", FileUtil.buscarJdk(tf_jdk));
            imps.putAll(FileUtil.buscarLibs());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void updateLibVbox() {
        vb_lib.getChildren().clear();
        File lib_imports = new File("data" + File.separatorChar + "java" + File.separatorChar + "lib_imports");
        File lib = new File("data" + File.separatorChar + "java" + File.separatorChar + "lib");
        Iterator<File> files_imports = Arrays.asList(lib_imports.listFiles()).iterator();
        Iterator<File> files_lib = Arrays.asList(lib.listFiles()).iterator();
        if(files_imports == null) return;
        ObservableList<String> list = FXCollections.observableArrayList();
        while(files_imports.hasNext()) {
            File imp_file = files_imports.next();
            File lib_file = files_lib.next();
            
            Pane pane = new Pane();
            Label label = new Label(imp_file.getName());
            pane.getChildren().add(label);
            pane.onContextMenuRequestedProperty().set(e -> {
                MenuItem del = new MenuItem("Excluir Biblioteca");
                del.onActionProperty().set(evt -> {
                    try {
                        if(imp_file.getName().replaceAll("[\\.][d][a][t]", "").equals(lib_file.getName().replaceAll("[\\.][j][a][r]", ""))) {
                            Files.deleteIfExists(lib_file.toPath().toAbsolutePath());
                        }
                        Files.deleteIfExists(imp_file.toPath().toAbsolutePath());
                    } catch (Exception err) {
                        err.printStackTrace();
                    }
                    updateLibVbox();
                });
                ContextMenu cm = new ContextMenu(del);
                cm.show(pane, e.getScreenX(), e.getScreenY());
            });
            vb_lib.getChildren().add(pane);
        }
    }

    private void inicializarLibListener() {
        tf_lib.onMouseClickedProperty().set(e -> {
            FileChooser fc = new FileChooser();
            FileChooser.ExtensionFilter extFilter
                    = new FileChooser.ExtensionFilter("Arquivos Jar(*.jar)", "*.jar");
            fc.getExtensionFilters().add(extFilter);
            File file = fc.showOpenDialog(null);
            if (file == null) {
                return;
            }
            try {
                Path destLib = Paths.get(
                          "data"
                        + File.separatorChar
                        + "java"
                        + File.separatorChar
                        + "lib"
                        + File.separatorChar
                        + file.getName()
                ).toAbsolutePath();
                Path destImports = Paths.get(
                          "data"
                        + File.separatorChar
                        + "java"
                        + File.separatorChar
                        + "lib_imports"
                        + File.separatorChar
                        + file.getName().replaceAll("[\\.][j][a][r]", ".dat")
                ).toAbsolutePath();
                
                Files.deleteIfExists(destLib);
                Files.copy(file.toPath().toAbsolutePath(), destLib);
                Set<String> libImports = FileUtil.getJarImports(new File(destLib.toString()));
                root.possiveisImports.put(file.getName().replaceAll("[\\.][j][a][r]", ""), libImports);
                Files.deleteIfExists(destImports);
                Files.createFile(destImports);
                ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(destImports.toFile())));
                oos.writeObject(libImports);
                oos.close();
            } catch (Exception err) {
                err.printStackTrace();
            }
            updateLibVbox();
        });
    }

    private void inicializarJdkListener() {
        tf_jdk.onMouseClickedProperty().set(e -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Localizar Arquivo \"src.zip\" do Java");
            FileChooser.ExtensionFilter extFilter
                    = new FileChooser.ExtensionFilter("Arquivos Zip(*.zip)", "*.zip");
            fc.getExtensionFilters().add(extFilter);
            File file = fc.showOpenDialog(null);
            if (file == null) {
                return;
            }
            tf_jdk.setText(file.toString());
            try {
                Set imps = new HashSet();
                imps.addAll(FileUtil.getZipImports(file));
                this.imps.put("jdk", imps);
                FXUtil.paintValid(tf_jdk);
                Path p = Paths.get("data" + File.separatorChar + "java" + File.separatorChar + "jdk.dat");
                if (!Files.exists(p)) {
                    Files.createFile(p);
                }
                File dest = new File(p.toString());
                ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(dest)));
                oos.writeObject(file.toPath().toAbsolutePath().toString());
                oos.writeObject(imps);
                oos.close();
            } catch (Exception err) {
                err.printStackTrace();
                FXUtil.paintInvalid(tf_jdk);
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle("Erro");
                a.setHeaderText("Erro ao abrir o arquivo \"src.zip\"");
                a.setContentText("Não foi possível localizar o arquivo \"src.zip\" no diretório: " + file.toPath().toAbsolutePath().toString());
                a.show();
            }
        });
    }

    private void inicializarBtnSalvar() {
        btn_salvar.onActionProperty().set(e -> {
            root.setPossiveisImports(imps);
            stage.close();
        });
    }

    private void inicializarBtnCancelar() {
        btn_cancelar.onActionProperty().set(e -> {
            stage.close();
        });
    }

}
