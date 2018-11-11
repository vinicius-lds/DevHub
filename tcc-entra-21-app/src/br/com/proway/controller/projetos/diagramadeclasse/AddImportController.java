package br.com.proway.controller.projetos.diagramadeclasse;

import br.com.proway.view.comp.AutocompletionTextField;
import br.com.proway.util.GridPaneUtil;
import br.com.proway.util.StringUtil;
import br.com.proway.vo.diagramadeclasse.Classe;
import br.com.proway.vo.diagramadeclasse.Tipo;
import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Vinícius Luis da Silva
 */
public class AddImportController implements Initializable {

    @FXML private JFXButton btn_adc;
    @FXML private JFXButton btn_excluir;
    @FXML private JFXButton btn_cancelar;
    @FXML private JFXButton btn_salvar;
    @FXML private JFXButton btn_excluirTudo;
    @FXML private VBox vb_imps;
    
    private Stage stage;
    private EdicaoDeClasseController root;
    private Classe classe;
    private List<AnchorPane> imps = new ArrayList<>();
    private AnchorPane impSelecionado;
    private Set<String> toRemove = new TreeSet();
    private Set<String> toAdd = new TreeSet<>();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.inicializarBtnAdc();
        this.inicializarBtnExcluir();
        this.inicializarBtnSalvar();
        this.inicializarBtnCancelar();
        this.inicializarBtnExcluirTudo();
    }
    
    private void inicializarBtnSalvar() {
        this.btn_salvar.onActionProperty().set(e -> {
            Iterator<String> toAdd = this.toAdd.iterator();
            Iterator<String> toRemove = this.toRemove.iterator();
            SortedSet<String> imps = classe.getImports();
            boolean boolToAdd = toAdd.hasNext();
            boolean boolToRemove = toRemove.hasNext();
            while(boolToAdd || boolToRemove) {
                if(boolToAdd) {
                    imps.add(toAdd.next());
                    boolToAdd = toAdd.hasNext();
                }
                if(boolToRemove) {
                    imps.remove(toRemove.next());
                    boolToRemove = toRemove.hasNext();
                }
            }
            this.root.revalidaCampos();
            stage.close();
        });
    }
    
    private void inicializarBtnCancelar() {
        this.btn_cancelar.onActionProperty().set(e -> {
            this.stage.close();
        });
    }

    private void inicializarBtnAdc() {
        btn_adc.onActionProperty().set(e -> {
            updateVbox();
            addImport();
        });
    }
    
    private void inicializarBtnExcluir() {
        btn_excluir.onActionProperty().set(e -> {
            if(impSelecionado != null) {
                String aux = ((Label)impSelecionado.getChildren().get(0)).getText();
                this.vb_imps.getChildren().remove(impSelecionado);
                this.toAdd.remove(aux);
                this.toRemove.add(aux);
                updateVbox();
            }
        });
    }
    
    private void inicializarBtnExcluirTudo() {
        btn_excluirTudo.onActionProperty().set(e -> {
            for(AnchorPane ap: this.imps) {
                String aux = ((Label)ap.getChildren().get(0)).getText();
                this.toAdd.remove(aux);
                this.toRemove.add(aux);
            }
            updateVbox();
        });
    }
    
    private void addImport() { this.addImport(null); }
    private void addImport(String imp) {
        Node node_imp;
        AnchorPane ap = new AnchorPane();
        this.imps.add(ap);
        if(imp != null) {
            node_imp = new Label(imp);
        } else {
            HashMap<String, Set> posImp = this.root.classeController.root.possiveisImports;
            Set<String> keySet = posImp.keySet();
            final SortedSet<String> finalSet = new TreeSet<>();
            for(String key: keySet) {
                finalSet.addAll(posImp.get(key));
            }
            node_imp = new AutocompletionTextField(finalSet);
            ((AutocompletionTextField)node_imp).requestFocus();
            node_imp.focusedProperty().addListener((obs, ov, nv) -> {
                if(!nv) {
                    String aux = ((AutocompletionTextField)node_imp).getText();
                    if(finalSet.contains(aux)) {
                        this.toAdd.add(aux);
                        this.toRemove.remove(aux);
                    }
                    updateVbox();
                }
            });
        }
        ap.getChildren().add(node_imp);
        AnchorPane.setLeftAnchor(node_imp, 15.0);
        AnchorPane.setRightAnchor(node_imp, 15.0);
        AnchorPane.setTopAnchor(node_imp, 15.0);
        AnchorPane.setBottomAnchor(node_imp, 15.0);
        ap.onMouseClickedProperty().set(e -> {
            for(AnchorPane p: imps) {
                p.setStyle("");
                p.getChildren().get(0).setStyle("");
            }
            ap.setStyle("-fx-background-color: blue;");
            ap.getChildren().get(0).setStyle("-fx-text-fill: white;");
            impSelecionado = ap;
        });
        vb_imps.getChildren().add(ap);
    }
    
    private void updateVbox() {
        this.imps.clear();
        this.vb_imps.getChildren().clear();
        for(String str: classe.getImports()) {
            if(this.toRemove.contains(str)) continue;
            this.addImport(str);
        }
        for(String str: toAdd) {
            this.addImport(str);
        }
    }
    
    protected void setRoot(EdicaoDeClasseController root) {
        this.root = root;
        this.classe = this.root.classeController.classe;
        this.updateVbox();
        this.root.classeController.root.possiveisImports.size();
    }

    protected void setStage(Stage stage) {
        this.stage = stage;
    }

}
