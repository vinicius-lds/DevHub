package br.com.proway.controller.projetos.diagramadeclasse;

import br.com.proway.bean.login.Usuario;
import br.com.proway.controller.projetos.diagramadeclasse.relacoes.EdicaoDeRelacaoController;
import br.com.proway.controller.projetos.diagramadeclasse.relacoes.RelacaoController;
import br.com.proway.enumeradores.Relacao;
import br.com.proway.enumeradores.TipoClasse;
import br.com.proway.main.Main;
import br.com.proway.util.FXMLFile;
import br.com.proway.util.FileUtil;
import br.com.proway.view.fxml.config.ImportacoesController;
import br.com.proway.vo.diagramadeclasse.Diagrama;
import br.com.proway.vo.diagramadeclasse.Variavel;
import com.jfoenix.controls.JFXButton;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Vinícius Luis da Silva
 */
public class DiagramaDeClasseController implements Initializable, Serializable {

    public final Diagrama diagrama = new Diagrama();
    public final List<ClasseController> classes = new ArrayList<>();
    public final List<RelacaoController> relacoes = new ArrayList<>();
    public final List<Initializable> objetosSelecionados = new ArrayList<>();
    public final HashMap<String, Set> possiveisImports = new HashMap();
    protected ClasseController classeDrag;
    private Accordion ac_comp;
    protected Relacao relacao;
    protected RelacaoController relacaoAtual;
    private double scale = 1;
    
    @FXML public AnchorPane ap_diagramaDeClasse;
    @FXML public Pane pn_desktop;
    @FXML public ScrollPane sp_desktop;

    public void setPossiveisImports(Map<String, Set> map) {
        this.possiveisImports.clear();
        this.possiveisImports.putAll(map);
        Variavel.setPossiveisImports(possiveisImports);
    }
    
    @FXML
    void compOnAction(ActionEvent event) {
        if(this.ac_comp == null) {
            JFXButton btn_classe = new JFXButton("Classe");
            JFXButton btn_classeAbstrata = new JFXButton("Classe Abstrata");
            JFXButton btn_interface = new JFXButton("Interface");
            JFXButton btn_enumerador = new JFXButton("Enumerador");
            
            VBox vb_classe = new VBox(btn_classe, btn_classeAbstrata, btn_interface, btn_enumerador);
            
            JFXButton btn_agregacao = new JFXButton("Agregação");
            //JFXButton btn_composicao = new JFXButton("Composição");
            JFXButton btn_dependencia = new JFXButton("Dependencia");
            JFXButton btn_heranca = new JFXButton("Herança");
            JFXButton btn_implementacao = new JFXButton("Implementação");
            VBox vb_relacao = new VBox(btn_agregacao, /*btn_composicao,*/ btn_dependencia, btn_heranca, btn_implementacao);
            
            TitledPane tp_classe = new TitledPane("Tipos de Classe", vb_classe);
            TitledPane tp_relacao = new TitledPane("Tipos de Relação", vb_relacao);
            TitledPane tp_edicao = new TitledPane("Edição", new AnchorPane());
            
            this.ac_comp = new Accordion(tp_classe, tp_relacao, tp_edicao);
            
            this.ac_comp.setPrefWidth(300);
            this.ac_comp.setMinWidth(300);
            this.ac_comp.setMaxWidth(300);
            
            btn_classe.setPrefWidth(this.ac_comp.getPrefWidth());
            btn_classeAbstrata.setPrefWidth(this.ac_comp.getPrefWidth());
            btn_interface.setPrefWidth(this.ac_comp.getPrefWidth());
            btn_enumerador.setPrefWidth(this.ac_comp.getPrefWidth());
            btn_classe.onDragDetectedProperty().set(getStartClassDrag(btn_classe, TipoClasse.CONCRETE));
            btn_classeAbstrata.onDragDetectedProperty().set(getStartClassDrag(btn_classe, TipoClasse.ABSTRACT));
            btn_interface.onDragDetectedProperty().set(getStartClassDrag(btn_classe, TipoClasse.INTERFACE));
            btn_enumerador.onDragDetectedProperty().set(getStartClassDrag(btn_classe, TipoClasse.ENUM));
            
            btn_agregacao.setPrefWidth(this.ac_comp.getPrefWidth());
            //btn_composicao.setPrefWidth(this.ac_comp.getPrefWidth());
            btn_dependencia.setPrefWidth(this.ac_comp.getPrefWidth());
            btn_heranca.setPrefWidth(this.ac_comp.getPrefWidth());
            btn_implementacao.setPrefWidth(this.ac_comp.getPrefWidth());
            btn_agregacao.onActionProperty().set(this.getRelacaoListener(Relacao.AGREGACAO, btn_agregacao, vb_relacao));
            //btn_composicao.onActionProperty().set(this.getRelacaoListener(Relacao.COMPOSICAO, btn_composicao, vb_relacao));
            btn_dependencia.onActionProperty().set(this.getRelacaoListener(Relacao.DEPENDENCIA, btn_dependencia, vb_relacao));
            btn_heranca.onActionProperty().set(this.getRelacaoListener(Relacao.HERANCA, btn_heranca, vb_relacao));
            btn_implementacao.onActionProperty().set(this.getRelacaoListener(Relacao.IMPLEMENTACAO, btn_implementacao, vb_relacao));
            
            String style = "-fx-cursor: hand";
            btn_classe.setStyle(style);
            btn_classeAbstrata.setStyle(style);
            btn_interface.setStyle(style);
            btn_enumerador.setStyle(style);
            btn_agregacao.setStyle(style);
            //btn_composicao.setStyle(style);
            btn_dependencia.setStyle(style);
            btn_heranca.setStyle(style);
            btn_implementacao.setStyle(style);
            
            //AnchorPane.setLeftAnchor(this.pn_desktop, 200.0);
            this.ap_diagramaDeClasse.getChildren().add(ac_comp);
            AnchorPane.setLeftAnchor(this.ac_comp, 0.0);
            AnchorPane.setTopAnchor(this.ac_comp, 25.0);
            AnchorPane.setBottomAnchor(this.ac_comp, 0.0);
            
        } else {
            this.ap_diagramaDeClasse.getChildren().remove(ac_comp);
            AnchorPane.setLeftAnchor(this.pn_desktop, 0.0);
            ac_comp = null;
        }
    }
    
    public EventHandler<? super MouseEvent> getStartClassDrag(Node n, TipoClasse t) {
        return (e) ->{
            //this.ac_comp.setDisable(true);
            ClipboardContent cc = new ClipboardContent();
            cc.putUrl(FXMLFile.CLASSE);
            n.startDragAndDrop(TransferMode.ANY).setContent(cc);

            pn_desktop.onDragDroppedProperty().set((evet) -> {
                if(evet.getDragboard().getUrl().equals(FXMLFile.CLASSE)) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLFile.CLASSE));
                    try {
                        loader.load();
                    } catch (Exception err) { err.printStackTrace(); }
                    ClasseController controller = loader.getController();
                    controller.inicializar(this);
                    controller.classe.setTipo(t);
                    classes.add(controller);
                    diagrama.addClasse(controller.classe);
                    
                    controller.ap_classe.setVisible(true);
                    relocateToPoint(evet.getX(), evet.getY(), controller.ap_classe);
                  
                }
            });

            pn_desktop.onDragOverProperty().set((evento) -> {
                if(evento.getDragboard().getUrl().equals(FXMLFile.CLASSE)) {
                    evento.acceptTransferModes(TransferMode.ANY);
                }
                evento.consume();
            });

            n.onDragDoneProperty().set((aux) ->{
                pn_desktop.onDragDroppedProperty().set(null);
                pn_desktop.onDragOverProperty().set(null);
                ac_comp.setDisable(false);
                
            });
        };
    }

    private EventHandler<ActionEvent> getRelacaoListener(Relacao r, JFXButton btn, VBox vb_relacao) {
        return e -> {
            relacao = relacao == r ? null : r;
            if(this.relacao == null) {
                pn_desktop.setStyle("-fx-cursor: default");
                btn.setStyle("");
                for(Node n: vb_relacao.getChildren()) {
                    n.setDisable(false);
                }
                for(TitledPane tp: this.ac_comp.getPanes()) {
                    tp.setDisable(false);
                }
            } else {
                //btn.setStyle("-fx-background-color: lightgray");
                pn_desktop.setStyle("-fx-cursor: crosshair");
                for(Node n: vb_relacao.getChildren()) {
                    if(n == btn) { continue; }
                    n.setDisable(true);
                }
                for(TitledPane tp: this.ac_comp.getPanes()) {
                    if(tp.getText().equals("Tipos de Relação")) { continue; }
                    tp.setDisable(true);
                }
            }
        };    
    }
    
    protected void unsetRelacao() {
        VBox vb_relacao = (VBox) this.ac_comp.getPanes().get(1).getContent();
        JFXButton btn;
        pn_desktop.setStyle("-fx-cursor: default");
        relacao = null;
        for(Node n: vb_relacao.getChildren()) {
            btn = (JFXButton) n;
            btn.setDisable(false);
        }
        for(Node n: this.ac_comp.getPanes()) {
            n.setDisable(false);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.relacao = null;
        try {
            this.possiveisImports.put("jdk", FileUtil.buscarJdk());
            this.possiveisImports.putAll(FileUtil.buscarLibs());
            Variavel.setPossiveisImports(possiveisImports);
        } catch (Exception e) {
            e.printStackTrace();
        }
        sp_desktop.addEventFilter(ScrollEvent.SCROLL, e -> {
            if(e.getDeltaY() > 0) {
                if(scale >= 3) {
                    e.consume();
                } else {
                    scale += 0.1;
                }
            } else {
                if(scale <= 0.5) {
                    e.consume();
                } else {
                    scale -= 0.1;
                }
            }
            pn_desktop.setScaleX(scale);
            pn_desktop.setScaleY(scale);
            e.consume();
        });
    }
    
    protected void setRelacao(Relacao value) {
        relacao = value;
        if(relacao != null) {
            this.pn_desktop.setCursor(Cursor.CROSSHAIR);
        
        } else {
            this.pn_desktop.setCursor(Cursor.DEFAULT);
        
        }
    }

    protected void relocateToPoint(double x, double y, AnchorPane no) {
        no.setVisible(true);
        pn_desktop.getChildren().remove(no);
        pn_desktop.getChildren().add(no);
        no.setLayoutX(x - (no.getWidth() / 2));
        no.setLayoutY(y - (no.getHeight() / 2));
    }
    
    protected void removerClasse(AnchorPane no) {
        no.setVisible(false);
        this.pn_desktop.getChildren().remove(no);
    }
    
    public void removeSelection(Initializable controller) {
        this.objetosSelecionados.remove(controller);
        this.clearCampoEdicao();
        if(controller instanceof ClasseController) {
            ((ClasseController)controller).setOpacity(1);
        } else if(controller instanceof RelacaoController) {
            ((RelacaoController)controller).setOpacity(1);
        }
    }

    public void select(Initializable controller) {
        this.objetosSelecionados.add(controller);
        this.clearCampoEdicao();
        AnchorPane ap = (AnchorPane) this.ac_comp.getPanes().get(2).getContent();
        if(this.objetosSelecionados.size() == 1) {
            if(controller instanceof ClasseController) {
                ClasseController cc = (ClasseController) this.objetosSelecionados.get(0);
                EdicaoDeClasseController aux = new EdicaoDeClasseController(cc);
                ap.getChildren().add(aux.ap_principal);
                AnchorPane.setLeftAnchor(aux.ap_principal, 0.5);
                AnchorPane.setRightAnchor(aux.ap_principal, 0.5);
                AnchorPane.setBottomAnchor(aux.ap_principal, 0.5);
                AnchorPane.setTopAnchor(aux.ap_principal, 0.5);
                aux.inicializarCampos();
            } else {
                RelacaoController rc = (RelacaoController) this.objetosSelecionados.get(0);
                EdicaoDeRelacaoController aux = new EdicaoDeRelacaoController(rc);
                ap.getChildren().add(aux.ap_principal);
                aux.setAgregacao(rc.getAgregacao());
                AnchorPane.setLeftAnchor(aux.ap_principal, 0.5);
                AnchorPane.setRightAnchor(aux.ap_principal, 0.5);
                AnchorPane.setBottomAnchor(aux.ap_principal, 0.5);
                AnchorPane.setTopAnchor(aux.ap_principal, 0.5);
            }
        }
    }
    
    private void clearCampoEdicao() {
        AnchorPane ap = (AnchorPane) this.ac_comp.getPanes().get(2).getContent();
        ap.getChildren().clear();
    }
    
    public void clearSelection() {
        this.clearCampoEdicao();
        for(Initializable init: objetosSelecionados) {
            if(init instanceof ClasseController) {
                ((ClasseController)init).setOpacity(1);
            } else {
                ((RelacaoController)init).setOpacity(1);
            }
        }
        objetosSelecionados.clear();
    }
    
    public List<ClasseController> getClasses() {
        return classes;
    }

    public boolean addClasse(ClasseController classe) {
        return this.classes.add(classe);
    }

    public void updateDiagrama() {
        for(ClasseController classe: this.classes) {
            classe.classe.revalidaClasse();
            classe.updateClasse();       
        }
        for(RelacaoController relacao: this.relacoes) {
            relacao.updateLine();
        }
    }
    
    @FXML
    void abrirOnAction(ActionEvent event) {

    }

    @FXML
    void desfazerOnAction(ActionEvent event) {

    }

    @FXML
    void documentacaoOnAction(ActionEvent event) {

    }

    @FXML
    void importacoesOnAction(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLFile.IMPORTACOES));
        Scene scene = new Scene(loader.load());
        ((ImportacoesController)loader.getController()).setRoot(this);
        ((ImportacoesController)loader.getController()).setStage(stage);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void iniciarProjetoOnAction(ActionEvent event) {
        //this.diagrama.generateCode();
        try {
            File local = new DirectoryChooser().showDialog(Main.STAGE_PRINCIPAL);
            if(local != null) {
                this.diagrama.generateCode(local);
            }
        } catch (Exception err) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Erro");
            a.setHeaderText("Diagrama de Classe com erro!");
            a.setContentText("Ocorreu um erro na geração de código a partir desse Diagrama de Classe!");
            a.show();
        }
    }
 
    @FXML
    void refazerOnAction(ActionEvent event) {

    }

    @FXML
    void salvarComoOnAction(ActionEvent event) {

    }

    @FXML
    void salvarOnAction(ActionEvent event) {

    }

}
 