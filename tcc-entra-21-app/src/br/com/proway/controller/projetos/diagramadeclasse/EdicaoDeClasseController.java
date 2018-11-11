package br.com.proway.controller.projetos.diagramadeclasse;

import br.com.proway.enumeradores.TipoClasse;
import br.com.proway.util.FXMLFile;
import br.com.proway.util.FXUtil;
import br.com.proway.util.GridPaneUtil;
import br.com.proway.util.Patterns;
import br.com.proway.vo.diagramadeclasse.AgregacaoVo;
import br.com.proway.vo.diagramadeclasse.Classe;
import br.com.proway.vo.diagramadeclasse.Metodo;
import br.com.proway.vo.diagramadeclasse.Variavel;
import br.com.proway.vo.diagramadeclasse.Enumerador;
import com.jfoenix.controls.JFXButton;
import java.awt.Font;
import java.io.Serializable;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Vinícius Luis da Silva
 */
public class EdicaoDeClasseController implements Initializable, Serializable {
    
    @FXML protected AnchorPane ap_principal;
    @FXML private ScrollPane sp_grade;
    @FXML private JFXButton btn_metodo;
    @FXML private JFXButton btn_atributo;
    @FXML private JFXButton btn_delete;
    @FXML private JFXButton btn_options;
    @FXML private GridPane gp_atributos;
    @FXML private GridPane gp_metodos;
    @FXML private TextField tf_className;
    @FXML private TextField tf_pacote;
    @FXML private HBox hb_botoes;
    
    private final Set<TextField> campos = new HashSet<>();
    protected ClasseController classeController;
    
    public EdicaoDeClasseController(ClasseController classeController) {
        this.classeController = classeController;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLFile.EDITOR_CLASSE));
        loader.setController(this);
        try {
            loader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        inicializarBtnMetodo();
        inicializarBtnAtributo();
        inicializarBtnDelete();
        inicializarBtnOptions();
        this.tf_className.textProperty().addListener((obs, ov, nv)-> {
            validaNomeDeClasse(nv);
        });
        this.tf_className.focusedProperty().addListener((obs, ov, nv) -> {
            validaNomeDeClasse(tf_className.getText());
        });
        this.inicializarPacoteListener();
    }
    
    public void inicializarCampos() {
        TipoClasse tipo = classeController.classe.getTipo();
        this.tf_pacote.setText(classeController.classe.getPacote());
        this.tf_className.setText(classeController.classe.getNomeRepresentacao().equals("Nome inválido!") ? "" : classeController.classe.getNomeRepresentacao());
        if(tipo == TipoClasse.ENUM) {
            Iterator<Variavel> enumeradores = classeController.classe.getAtributos().iterator();
            Enumerador enumerador;
            while(enumeradores.hasNext()) {
                enumerador = (Enumerador) enumeradores.next();
                this.inicializarCampoEnum(enumerador);
            }
        } else {
            if(tipo != TipoClasse.INTERFACE) {
                for(Variavel atributo: this.classeController.classe.getAtributos()) {
                    this.inicializarCampoAtributo(atributo);
                }
            }
            for(Metodo metodo: this.classeController.classe.getMetodos()) {
                this.inicializarCampoMetodo(metodo);
            }
        }
        
    }
    
    private void inicializarPacoteListener() {
        this.tf_pacote.textProperty().addListener((obs, ov, nv) -> {
            if(Patterns.isValidPackageName(nv)) {
                FXUtil.paintValid(tf_pacote);
            } else {
                FXUtil.paintInvalid(tf_pacote);
            }
        });
        this.tf_pacote.focusedProperty().addListener((obs, ov, nv) -> {
            if(!nv) {
               if(tf_pacote.getText().isEmpty() || Patterns.isValidPackageName(tf_pacote.getText())) {
                    try {
                        classeController.root.diagrama.removeClasse(classeController.classe);
                        classeController.classe.setPacote(tf_pacote.getText());
                        classeController.root.diagrama.addClasse(classeController.classe);
                        for(AgregacaoVo a: classeController.classe.getAgregacoes()) {
                            for(Variavel v: a.getVariaveis(a.getOtherClass(classeController.classe))) {
                                v.setImpTipo(tf_pacote.getText().isEmpty() ? "" : tf_pacote.getText() + "." + classeController.classe.getNome());
                                v.setAbvTipo(classeController.classe.getNome());
                            }
                        }
                        FXUtil.paintValid(tf_pacote);
                    } catch (Exception e) {
                        e.printStackTrace();
                        tf_pacote.setText(classeController.classe.getPacote());
                        classeController.root.diagrama.addClasse(classeController.classe);
                        FXUtil.paintInvalid(tf_pacote);
                    }
                } else {
                    FXUtil.paintInvalid(tf_pacote);
                }
                for(ClasseController aux: this.classeController.root.classes) {
                    aux.classe.revalidaAtributos();
                    aux.classe.revalidaMetodos();
                    aux.updateClasse();
                    aux.updateRelacoes();
                }
            } 
        });
    }

    protected void revalidaCampos() {
        String qlqrCoisa = "^?";
        for(TextField tf: campos) {
            tf.setText(qlqrCoisa);
        }
    }
    
    private void inicializarBtnMetodo() {
        TipoClasse tc = this.classeController.classe.getTipo();
        if(tc == TipoClasse.ENUM) {
            this.hb_botoes.getChildren().remove(this.btn_metodo);
            return;
        }
        this.btn_metodo.onActionProperty().set(evt -> {
            this.inicializarCampoMetodo(new Metodo(this.classeController.classe));
        });
    }
    
    private void inicializarCampoMetodo(Metodo metodo) {
        this.classeController.classe.addMetodo(metodo);
        TextField tf = new TextField();
        campos.add(tf);
        tf.setText(metodo.getAssinatura());
        tf.setPromptText("Método");
        JFXButton btn = new JFXButton("..");
        tf.textProperty().addListener((obs, ov, nv) -> {
            if(nv.equals("^?")) {
                tf.setText(ov);
                return;
            }
            validaMetodo(metodo, tf);
        });
        btn.onMouseClickedProperty().set(e -> {
            getContextMenu(metodo, tf).show(btn, e.getScreenX(), e.getScreenY());
        });
        this.gp_metodos.addRow(GridPaneUtil.getRowCount(gp_metodos), tf, btn);
        this.classeController.updateClasse();
        this.classeController.updateRelacoes();
        this.revalidaCampos();
    }
    
    private void validaMetodo(Metodo metodo, TextField tf) {
        String regexNomeParam = "[\\s]*[a-zA-Z][\\w]*[\\s]*[:]";
        String regexTipoRetorno = "[)][\\s]*[:][\\s]*[a-zA-Z][\\w]*";
        String regexModificador = "[+]|[-]|[#]";
        String aux = metodo.getAssinatura().replaceAll(regexNomeParam, "").replaceAll(regexTipoRetorno, ")").replaceAll(regexModificador, "");
        if(metodo.isValid()) {
            this.classeController.metodos.remove(aux);
        }
        metodo.setAssinatura(tf.getText());
        aux = metodo.getAssinatura().replaceAll(regexNomeParam, "").replaceAll(regexTipoRetorno, ")").replaceAll(regexModificador, "");
        if(!metodo.isValid() || !this.classeController.metodos.add(aux)) {
            metodo.setValid(false);
            FXUtil.paintInvalid(tf);
        } else {
            FXUtil.paintValid(tf);
        }
        this.classeController.updateClasse();
        this.classeController.updateRelacoes();
        
    }
    
    private void validaNomeDeClasse(String value) {
        this.classeController.classe.setNome(value);
        if(this.classeController.classe.isValid()) {
            FXUtil.paintValid(tf_className);
        } else {
            FXUtil.paintInvalid(tf_className);
        }
        for(ClasseController aux: this.classeController.root.classes) {
            aux.classe.revalidaAtributos();
            aux.classe.revalidaMetodos();
            aux.updateClasse();
            aux.updateRelacoes();
        }
        this.classeController.updateClasse();
        this.classeController.updateRelacoes();
        this.revalidaCampos();
    }
    
    private void inicializarBtnAtributo() {
        TipoClasse tc = this.classeController.classe.getTipo();
        if(tc == TipoClasse.INTERFACE) {
            this.hb_botoes.getChildren().remove(this.btn_atributo);
            return;
        }
        if(tc == TipoClasse.ENUM) {
            this.btn_atributo.setText("Enumerador");
            this.btn_atributo.onActionProperty().set(evt -> {
                inicializarCampoEnum(new Enumerador(this.classeController.classe));
            });
        } else {
            this.btn_atributo.onActionProperty().set(evt -> {
                inicializarCampoAtributo(new Variavel(this.classeController.classe));
            });
        }
        this.btn_atributo.focusedProperty().addListener((obs, ov, nv) -> {
            if(!nv) {
                this.revalidaCampos();
            }
        });
    }
    
    private void inicializarCampoEnum(Enumerador enumerador) {
        TextField tf = new TextField();
        campos.add(tf);
        JFXButton btn = new JFXButton("..");
        tf.setText(enumerador.getAssinatura());
        this.classeController.classe.addAtributo(enumerador);
        tf.setPromptText("Enumerador");
        tf.textProperty().addListener((obs, ov, nv) -> {
            if(nv.equals("^?")) {
                tf.setText(ov);
                return;
            }
            validaEnum(enumerador, tf);
        });
        btn.onMouseClickedProperty().set(e -> {
            getContextMenu(enumerador, tf).show(btn, e.getScreenX(), e.getScreenY());
        });
        this.gp_atributos.addRow(GridPaneUtil.getRowCount(gp_atributos), tf, btn);
        this.classeController.updateClasse();
        this.classeController.updateRelacoes();
        this.revalidaCampos();
    }
    
    private void inicializarCampoAtributo(Variavel atributo) {
        TextField tf = new TextField();
        campos.add(tf);
        tf.setText(atributo.getAssinatura());
        JFXButton btn = new JFXButton("..");
        this.classeController.classe.addAtributo(atributo);
        tf.setPromptText("Atributo");
        tf.textProperty().addListener((obs, ov, nv) -> {
            if(nv.equals("^?")) {
                tf.setText(ov);
                return;
            }
            validaAtributo(atributo, tf);
        });
        btn.onMouseClickedProperty().set(e -> {
            getContextMenu(atributo, tf).show(btn, e.getScreenX(), e.getScreenY());
        });
        this.gp_atributos.addRow(GridPaneUtil.getRowCount(gp_atributos), tf, btn);
        this.classeController.updateClasse();
        this.classeController.updateRelacoes();
        this.revalidaCampos();
    }
    
    private void validaEnum(Enumerador enumerador, TextField tf) {
        if(enumerador.isValid()) {
            this.classeController.atributos.remove(enumerador.getAssinatura());
        }
        enumerador.setAssinatura(tf.getText());
        if(!enumerador.isValid() || !this.classeController.atributos.add(enumerador.getAssinatura())) {
            enumerador.setValid(false);
            FXUtil.paintInvalid(tf);
        } else {
            FXUtil.paintValid(tf);
        }
        this.classeController.updateClasse();
        this.classeController.updateRelacoes();
    }
    
    private void validaAtributo(Variavel atributo, TextField tf) {
        String regexModificador = "[+]|[-]|[#][\\s]*";
        String regexTipo = "[\\s]*[:][\\s]*[a-zA-Z][\\w]*[\\s]*";
        String aux = atributo.getAssinatura().replaceAll(regexModificador, "").replaceAll(regexTipo, "");
        if(atributo.isValid()) {
            this.classeController.atributos.remove(aux);
        }
        atributo.setAssinatura(tf.getText());
        aux = atributo.getAssinatura().replaceAll(regexModificador, "").replaceAll(regexTipo, "");
        System.out.println("Auxiliar -" + aux);
        System.out.println(this.classeController.atributos);
        if(!atributo.isValid() || !this.classeController.atributos.add(aux)) {
            atributo.setValid(false);
            FXUtil.paintInvalid(tf);
        } else {
            FXUtil.paintValid(tf);
        }
        this.classeController.updateClasse();
        this.classeController.updateRelacoes();
    }
    
    private void inicializarBtnDelete() {
        this.btn_delete.onActionProperty().set(e -> {
            this.classeController.excluir();
        });
    }
    
    public void setClasseController(ClasseController cc) {
        classeController = cc;
    }
    
    private ContextMenu getContextMenu(Metodo metodo, TextField tf) {
        ContextMenu cm = new ContextMenu();
        MenuItem rem = new MenuItem("Remover");
        rem.onActionProperty().set(e -> {
            String regexNomeParam = "[\\s]*[a-zA-Z][\\w]*[\\s]*[:]";
            String regexTipoRetorno = "[)][\\s]*[:][\\s]*[a-zA-Z][\\w]*";
            String regexModificador = "[+]|[-]|[#]";
            String aux = metodo.getAssinatura().replaceAll(regexNomeParam, "").replaceAll(regexTipoRetorno, ")").replaceAll(regexModificador, "");
            this.classeController.metodos.remove(aux);
            this.classeController.classe.removerMetodo(metodo);
            this.campos.remove(tf);
            GridPaneUtil.deleteRow(gp_metodos, GridPane.getRowIndex(tf));
            this.classeController.updateClasse();
            this.classeController.updateRelacoes();
        });
        MenuItem doc = new MenuItem("Documetação");
        doc.onActionProperty().set(e -> {
            new DocumentacaoController(metodo);
        });
        cm.getItems().addAll(rem, doc);
        if(this.classeController.classe.getTipo() != TipoClasse.INTERFACE) {
            MenuItem sta = new MenuItem("Estatico");
            MenuItem abs = new MenuItem("Abstrato");
            sta.onActionProperty().set(e -> {
                if(metodo.isAbstrata()) {
                    return;
                }
                metodo.setEstatica(!metodo.isEstatica());
                classeController.updateClasse();
                this.classeController.updateRelacoes();
            });
            abs.onActionProperty().set(e -> {
                if(metodo.isEstatica()) {
                    return;
                }
                metodo.setAbstrata(!metodo.isAbstrata());
                classeController.updateClasse();
                this.classeController.updateRelacoes();
            });
            cm.getItems().addAll(sta, abs);
        }
        return cm;
    }
    
    private ContextMenu getContextMenu(Variavel atributo, TextField tf) {
        ContextMenu cm = new ContextMenu();
        MenuItem rem = new MenuItem("Remover");
        rem.onActionProperty().set(e -> {
            String regexModificador = "[+]|[-]|[#][\\s]*";
            String regexTipo = "[\\s]*[:][\\s]*[a-zA-Z][\\w]*[\\s]*";
            String aux = atributo.getAssinatura().replaceAll(regexModificador, "").replaceAll(regexTipo, "");
            this.classeController.atributos.remove(aux);
            classeController.classe.removerAtributo(atributo);
            campos.remove(tf);
            GridPaneUtil.deleteRow(gp_atributos, GridPane.getRowIndex(tf));
            classeController.updateClasse();
            this.classeController.updateRelacoes();
        });
        cm.getItems().add(rem);
        if(this.classeController.classe.getTipo() != TipoClasse.ENUM) {
            MenuItem doc = new MenuItem("Documetação");
            doc.onActionProperty().set(e -> {
                new DocumentacaoController(atributo);
            });
            MenuItem sta = new MenuItem("Estatico");
            sta.onActionProperty().set(e -> {
                atributo.setEstatica(!atributo.isEstatica());
                classeController.updateClasse();
                this.classeController.updateRelacoes();
            });
            MenuItem fin = new MenuItem("Final");
            fin.onActionProperty().set(e -> {
                atributo.setConstante(!atributo.isConstante());
                classeController.updateClasse();
                this.classeController.updateRelacoes();
            });
            cm.getItems().addAll(doc, fin, sta);
        }
        return cm;
    }

    private void inicializarBtnOptions() {
        MenuItem imports = new MenuItem("Importações");
        imports.setOnAction(e -> {
            try {
                Stage stage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLFile.ADD_IMPORT));
                AnchorPane ap = loader.load();
                AddImportController controller = loader.getController();
                controller.setStage(stage);
                controller.setRoot(this);
                Scene scene = new Scene(ap);
                stage.setScene(scene);
                stage.show();
            } catch (Exception err) {
                err.printStackTrace();
            }
        });
        ContextMenu cm = new ContextMenu(imports);
        btn_options.onMouseClickedProperty().set(e -> {
            cm.show(btn_options, e.getScreenX(), e.getScreenY());
        });
    }
    
}
