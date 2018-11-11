package br.com.proway.controller.projetos.meusprojetos;

import br.com.proway.util.FXMLFile;
import br.com.proway.util.GridPaneUtil;
import br.com.proway.util.IconFile;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;

/**
 * @author Vinícius Luis da Silva
 */
public class FileController implements Initializable, Comparable<FileController> {
    
    protected File file;
    protected RepositorioController root;
    private String fontColor = "black";
    
    @FXML protected AnchorPane ap_principal;
    @FXML private GridPane gp_file;
    @FXML private ImageView iv_icon;
    @FXML private Label lbl_nome;
    @FXML private Label lbl_tamanho;
    @FXML private Label lbl_extensao;
    @FXML private JFXCheckBox cb_add;
    
    public FileController() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLFile.FILE));
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    void ap_principalOnClick(MouseEvent event) {
        if(event.isControlDown()) {
            this.setSelected(
                    true, 
                    false //Única selecionada
            );
        } else {
            this.setSelected(
                    true, 
                    true //Única selecionada
            );
        }
        if(event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
            if(file.isDirectory()) {
                root.setCurrentDirectory(file);
            } else {
                try {
                    Desktop.getDesktop().open(file);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cb_add.setText("");
        this.setContextMenu();
        this.initializeCheckBoxListener();
    }
    
    public void initializeCheckBoxListener() {
        this.cb_add.onActionProperty().set(e -> {
            if(this.file.isDirectory()) {
                this.root.setAllFilesFromDirectorySelected(this.file, cb_add.isSelected());
            } else if(root.modified.contains(this.file)
                    || root.newFiles.contains(this.file)
                    || root.conflicts.contains(this.file)) {
                if(cb_add.isSelected()) {
                    root.selectedFiles.add(file);
                } else {
                    root.selectedFiles.remove(file);
                }
            } else {
                cb_add.setSelected(false);
            }
        });
    }
    
    public void setSelected(boolean value, boolean onlyOne) {
        if(value) {
            if(onlyOne) {
                for(FileController file: this.root.selected) {
                    file.setSelected(false, false);
                }
                this.root.selected.clear();
            }
            this.root.selected.add(this);
            this.ap_principal.setStyle("-fx-background-color: blue");
            this.lbl_extensao.setStyle("-fx-text-fill: white;");
            this.lbl_nome.setStyle("-fx-text-fill: white;");
            this.lbl_tamanho.setStyle("-fx-text-fill: white;");
        } else {
            this.ap_principal.setStyle("-fx-background-color: none");
            this.lbl_extensao.setStyle("-fx-text-fill: " + fontColor);
            this.lbl_nome.setStyle("-fx-text-fill: " + fontColor);
            this.lbl_tamanho.setStyle("-fx-text-fill: " + fontColor);
        }
    }
    
    public void setFile(String file, String name) throws IOException {
        Path p = Paths.get(file);
        if(!Files.exists(p)) {
            return;
        }
        this.file = p.toFile();
        if(Files.isDirectory(p)) {
            String[] aux = p.toAbsolutePath().toString().split("\\\\");
            File img;
            if(name == null) {
                name = p.getName(p.getNameCount() - 1).toString();
                img = Paths.get(IconFile.FOLDER_DOWN).toFile();
            } else {
                img = Paths.get(IconFile.FOLDER_UP).toFile();
            }
            lbl_nome.setText(name);
            lbl_extensao.setText("-");
            lbl_tamanho.setText("-");
            InputStream is = new FileInputStream(img);
            iv_icon.setImage(new Image(is));
            for(File f: root.selectedFiles) {
                if(f.getAbsolutePath().startsWith(this.file.toPath().toAbsolutePath().toString() + File.separatorChar)) {
                    this.cb_add.setSelected(true);
                }
            }
        } else {
            String fileName = p.toAbsolutePath().getName(p.getNameCount() - 1).toString();
            int lastIndexDot = fileName.lastIndexOf(".");
            String extensao = lastIndexDot == -1 ? "" : fileName.substring(lastIndexDot + 1);
            lbl_extensao.setText(extensao.toUpperCase());
            lbl_nome.setText(fileName.substring(0, lastIndexDot));
            lbl_tamanho.setText(this.getTamanho(Files.size(p)));
            InputStream is = new FileInputStream(Paths.get(IconFile.getIcon(extensao)).toFile());
            iv_icon.setImage(new Image(is));
            
            if(root.selectedFiles.contains(this.file)) {
                this.cb_add.setSelected(true);
            }
            
            if(root.modified.contains(this.file)) {
                fontColor = "blue";
                this.lbl_nome.setStyle("-fx-text-fill: " + fontColor);
            }
            
            if(root.newFiles.contains(this.file)) {
                fontColor = "green";
                this.lbl_nome.setStyle("-fx-text-fill: " + fontColor);
            }
            
            if(root.conflicts == null) {
                return;
            }
            if(root.conflicts.contains(this.file)) {
                fontColor = "red";
                this.lbl_nome.setStyle("-fx-text-fill: " + fontColor);
            }
            
        }
        
    }
    
    public void setFile(String file) throws IOException {
        this.setFile(file, null);
    }
    
    public String getTamanho(long bytes) {
        if(bytes < 1024) {
            return bytes + " BYTES";
        }
        bytes /= 1024;
        if(bytes < 1024) {
            return bytes + "KB";
        }
        bytes /= 1024;
        if(bytes < 1024) {
            return bytes + "MB";
        }
        bytes /= 1024;
        if(bytes < 1024) {
            return bytes + "GB";
        }
        bytes /= 1024;
        return bytes + "TB";
    }
    
    public void setNome(String nome) {
        this.lbl_nome.setText(nome);
    }
    
    public void setIcon(String file) {
        this.iv_icon.setImage(new Image(file));
    }
    
    public void setContextMenu() {
        this.ap_principal.onContextMenuRequestedProperty().set((e) -> {
            setSelected(true, /*ùnica seleção*/true);
            MenuItem abrir = new MenuItem("Abir");
            MenuItem editar = new MenuItem("Editar");
            MenuItem renomear = new MenuItem("Renomear");
            MenuItem trocarExtensao = new MenuItem("Trocar Extensão");
            MenuItem excluir = new MenuItem("Excluir");
            abrir.onActionProperty().set((event) -> {
                try {
                    Desktop.getDesktop().open(file);
                } catch (Exception err) {
                    err.printStackTrace();
                }
            });
            editar.onActionProperty().set((event) -> {
                try {
                    Desktop.getDesktop().edit(file);
                } catch (Exception err) {
                    err.printStackTrace();
                }
            });
            renomear.onActionProperty().set((event) -> {
                TextField tf =this.renomear();
                tf.focusedProperty().addListener((obs, oldValue, newValue) -> {
                    if(!newValue) {
                        gp_file.getChildren().remove(tf);
                        gp_file.add(lbl_nome, 2, 0);
                        root.updateFiles(false);
                    }
                });
            });
            trocarExtensao.onActionProperty().set((event) -> {
                TextField tf = this.trocarExtensao();
                tf.focusedProperty().addListener((obs, oldValue, newValue) -> {
                    if(!newValue) {
                        gp_file.getChildren().remove(tf);
                        gp_file.add(lbl_extensao, 4, 0);
                        root.updateFiles(false);
                    }
                });
            });
            excluir.onActionProperty().set((event) -> {
                try {
                    if(file.isDirectory()) {
                        root.repo.deleteFolder(file);
                    } else {
                        Files.deleteIfExists(Paths.get(file.toString()));
                    }
                    root.updateFiles(false);
                } catch (Exception err) {
                    err.printStackTrace();
                }
            });
            ContextMenu cm = new ContextMenu(abrir, editar, renomear, trocarExtensao, excluir);
            cm.show(ap_principal, e.getScreenX(), e.getScreenY());
        });
    }

    @Override
    public int compareTo(FileController o) {
        if(this.file.isDirectory() && !o.file.isDirectory()) {
            return -1;
        }
        if(!this.file.isDirectory() && o.file.isDirectory()) {
            return 1;
        }
        String thisFileName = this.file.getName();
        String oFileName = o.file.getName();
        return thisFileName.compareTo(oFileName);
    }

    private TextField renomear() {
        gp_file.getChildren().remove(lbl_nome);
        JFXTextField tf = new JFXTextField();
        tf.setStyle("-fx-text-fill: white");
        gp_file.add(tf, 2, 0);
        tf.setVisible(true);
        tf.requestFocus();
        tf.onKeyPressedProperty().set((ev) -> {
            if(ev.getCode() == KeyCode.ENTER) {
                String fileName;
                if(tf.getText().matches("([\\s]*[\\w]+[\\s]*)+")) {
                    fileName = tf.getText();
                    if(!this.file.isDirectory()) {
                        fileName +=  "." + lbl_extensao.getText().toLowerCase();
                    }
                    String path = file.getAbsolutePath().split("\\\\")[0] + "\\";
                    for(int i = 0; i < file.toPath().getNameCount() - 1; i++) {
                        path += file.toPath().getName(i).toString() + File.separatorChar;
                    }
                    file.renameTo(new File(path + fileName));
                }
                gp_file.getChildren().remove(tf);
                //gp_file.add(lbl_nome, 2, 0);
                root.updateFiles(false);
            }

        });
        return tf;
    }

    private TextField trocarExtensao() {
        gp_file.getChildren().remove(lbl_extensao);
        JFXTextField tf = new JFXTextField();
        tf.setStyle("-fx-text-fill: white");
        gp_file.add(tf, 4, 0);
        tf.setVisible(true);
        tf.requestFocus();
        tf.onKeyPressedProperty().set((ev) -> {
            if(ev.getCode() == KeyCode.ENTER) {
                String fileName;
                if(tf.getText().matches("[\\w]*")) {
                    fileName = lbl_nome.getText() + (tf.getText().isEmpty() ? "" : "." + tf.getText().toLowerCase());
                    String path = file.getAbsolutePath().split("\\\\")[0] + "\\";
                    for(int i = 0; i < file.toPath().getNameCount() - 1; i++) {
                        path += file.toPath().getName(i).toString() + File.separatorChar;
                    }
                    file.renameTo(new File(path + fileName));
                }
                gp_file.getChildren().remove(tf);
                root.updateFiles(false);
            }

        });
        return tf;
    }
    
}
