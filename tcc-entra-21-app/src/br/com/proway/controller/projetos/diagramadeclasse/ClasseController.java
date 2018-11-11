package br.com.proway.controller.projetos.diagramadeclasse;

import br.com.proway.controller.projetos.diagramadeclasse.relacoes.RelacaoController;
import br.com.proway.enumeradores.Relacao;
import br.com.proway.enumeradores.TipoClasse;
import br.com.proway.util.FXMLFile;
import br.com.proway.vo.diagramadeclasse.Classe;
import br.com.proway.vo.diagramadeclasse.Metodo;
import br.com.proway.vo.diagramadeclasse.Variavel;
import java.io.Serializable;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Vinícius Luis da Silva
 */
public class ClasseController implements Initializable, Serializable {

    public final Classe classe = new Classe();
    protected DiagramaDeClasseController root;
    protected EdicaoDeClasseController editor;
    public final HashSet<RelacaoController> relacoesDestination = new HashSet();
    public final HashSet<RelacaoController> relacoesOrigin = new HashSet();
    public final Set<String> atributos = new HashSet();
    public final Set<String> metodos = new HashSet();
    private boolean relacaoDragCompleted;
    @FXML public AnchorPane ap_classe;
    @FXML private VBox vb_classe;
    @FXML private GridPane gp_titulo;
    @FXML private GridPane gp_atributos;
    @FXML private GridPane gp_metodos;
    
    @FXML
    void ap_classeContextMenuRequested(ContextMenuEvent event) {
        //this.contextMenu.show(this.ap_classe, event.getScreenX(), event.getScreenY());
    }

    @FXML
    void ap_classeDragDetected(MouseEvent event) {
        ClipboardContent cc = new ClipboardContent();
        if(this.root.relacao != null) {
            try{
                this.root.sp_desktop.onDragDoneProperty().set(null);
                FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLFile.RELACAO));
                Pane pn_relacao = loader.load();
                root.relacaoAtual = loader.getController();
                root.relacoes.add(root.relacaoAtual);
                root.relacaoAtual.setOrigin(this);
                //relacao.setTipo(root.relacao);
                root.pn_desktop.getChildren().add(0, root.relacaoAtual.getRoot_pane());
                root.relacaoAtual.bindToMouseMoviment();
                root.pn_desktop.onDragDoneProperty().set(e -> {
                    if(relacaoDragCompleted) {
                        relacaoDragCompleted = !relacaoDragCompleted;
                    } else {
                        if(root.relacaoAtual == null) return;
                        root.relacaoAtual.excluir();
                    }
                });
                root.relacaoAtual.setTipo(root.relacao);
                for(ClasseController c: this.root.classes) {
                    //Aceita drag do tipo relacao
                    c.ap_classe.onDragOverProperty().set((e) -> {
                        if(e.getDragboard().getUrl().equals(FXMLFile.RELACAO)) {
                            root.relacaoAtual.unbindToMouseMoviment();
                            root.relacaoAtual.setDestination(c);
                            root.relacaoAtual.setTipo(root.relacao);
                            root.relacaoAtual.updateLine();
                            if(isValidRelacao(this.root.relacao, c.classe)) {
                                e.acceptTransferModes(TransferMode.ANY);
                            }
                        }
                        e.consume();
                    });
                    //Seta o destino da relacao, o tipo e update
                    //tira o bind com o mouse
                    //tira os listeneres que foram colocados nas classes
                    c.ap_classe.onDragDroppedProperty().set((e) -> {
                        relacoesOrigin.add(root.relacaoAtual);
                        c.relacoesDestination.add(root.relacaoAtual);
                        root.relacaoAtual.unbindToMouseMoviment();
                        root.relacaoAtual.setDestination(c);
                        root.relacaoAtual.confirmaRelacao();
                        root.relacaoAtual.setTipo(root.relacao);
                        root.relacaoAtual.updateLine();
                        root.relacoes.add(root.relacaoAtual);
                        c.classe.revalidaClasse();
                        root.updateDiagrama();
                        c.ap_classe.onDragDroppedProperty().set(null);
                        c.ap_classe.onDragOverProperty().set(null);
                        c.ap_classe.onDragExitedProperty().set(null);
                        root.unsetRelacao();
                        root.relacaoAtual = null;
                        root.relacao = null;
                        relacaoDragCompleted = true;
                    });
                    //Tira o destination e da o bind na relacao com o mouse
                    //quando sai da classe
                    c.ap_classe.onDragExitedProperty().set((e) -> {
                        try {
                            root.relacaoAtual.setDestination(null);
                            root.relacaoAtual.bindToMouseMoviment();
                        } catch (Exception err) {
                        }
                    });
                }
                ap_classe.onDragOverProperty().set(null);
                ap_classe.onDragDroppedProperty().set(null);
                ap_classe.onDragExitedProperty().set(null);
                cc.putUrl(FXMLFile.RELACAO);
                this.ap_classe.startDragAndDrop(TransferMode.ANY).setContent(cc);
            } catch(Exception e) {
                e.printStackTrace();
            }
        } else {
            cc.putUrl(FXMLFile.CLASSE);
            this.root.classeDrag = this;
            this.ap_classe.setVisible(false);
            
            cc.putImage(ap_classe.getChildren().get(0).snapshot(null, null));
            
            this.setRelacoesVisible(false);
            
            this.ap_classe.startDragAndDrop(TransferMode.ANY).setContent(cc);
            this.root.pn_desktop.onDragDroppedProperty().set((e) -> {
                if(e.getDragboard().getUrl().equals(FXMLFile.CLASSE)) {
                    root.relocateToPoint(e.getX(), e.getY(), root.classeDrag.ap_classe);
                }
            });
            this.root.pn_desktop.onDragOverProperty().set((e) -> {
                if(e.getDragboard().getUrl().equals(FXMLFile.CLASSE)) {
                    e.acceptTransferModes(TransferMode.ANY);
                }
                e.consume();
            });
            this.root.sp_desktop.onDragDoneProperty().set((e) -> {
                setRelacoesVisible(true);
                root.classeDrag.ap_classe.setVisible(true);
                root.classeDrag = null;
                root.pn_desktop.onDragDoneProperty().set(null);
                root.pn_desktop.onDragDroppedProperty().set(null);
                root.pn_desktop.onDragOverProperty().set(null);
            });
        }
    }
    
    private void setRelacoesVisible(boolean value) {
        Iterator<RelacaoController> origin = this.relacoesOrigin.iterator();
        Iterator<RelacaoController> destin = this.relacoesDestination.iterator();
        boolean isOrigin = origin.hasNext();
        boolean isDestin = destin.hasNext();
        RelacaoController origem, destino;

        while(isOrigin || isDestin) {
            if(isOrigin) {
                origem = origin.next();
                origem.setVisible(value);
                origem.updateLine();
                isOrigin = origin.hasNext();
            }
            if(isDestin) {
                destino = destin.next();
                destino.setVisible(value);
                destino.updateLine();
                isDestin = destin.hasNext();
            }
        }
    }

    @FXML
    void ap_classeMouseClicked(MouseEvent event) {
        if(event.getButton() == MouseButton.PRIMARY) {
            List<Initializable> objSelecionados = this.root.objetosSelecionados;
            if(!objSelecionados.contains(this)) {
                if(!event.isControlDown()) {
                    root.clearSelection();
                }
                root.select(this);
                this.setOpacity(0.5);
            } else {
                if(!event.isControlDown()) {
                    root.clearSelection();
                } else {
                    root.removeSelection(this);
                    this.setOpacity(1);
                }
            }
        }
    }
    
    public void setOpacity(double value) {
        this.ap_classe.setStyle("-fx-opacity: " + value + ";"
                                + "-fx-background-color: white");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        relacaoDragCompleted = false;
        Platform.runLater(() -> {
            switch (this.classe.getTipo()){
                case CONCRETE:
                    break;
                case ABSTRACT:
                    break;
                case INTERFACE:
                    this.vb_classe.getChildren().remove(this.gp_atributos);
                    break;
                case ENUM:
                    this.vb_classe.getChildren().remove(this.gp_metodos);
                    break;
                default:
                    break;
            }
            this.updateClasse();
        });
    }
    
    protected void updateRelacoes() {
        for(RelacaoController c: root.relacoes) {
            if(c.getOrigin() == null || c.getDestination() == null) continue;
            if(c.getOrigin().ap_classe == ap_classe || c.getDestination().ap_classe == ap_classe) {
                c.updateLine();
            }
        }
    }
    
    protected void excluir() {
        this.root.diagrama.removeClasse(this.classe);
        this.root.classes.remove(this);
        for(ClasseController cc: this.root.classes) {
            cc.classe.desvincular(this.classe);
            cc.classe.revalidaClasse();
            cc.updateClasse();
        }
        for(RelacaoController rc: this.relacoesDestination) {
            rc.excluir();
        }
        for(RelacaoController rc: this.relacoesOrigin) {
            rc.excluir();
        }
        this.root.removerClasse(ap_classe);
        this.root.clearSelection();
    }
    
    public void inicializar(DiagramaDeClasseController root) {
        this.root = root;
        this.classe.setDiagrama(root.diagrama);
    }
    
    protected void updateClasse() {
        this.gp_titulo.getChildren().clear();
        this.gp_atributos.getChildren().clear();
        this.gp_metodos.getChildren().clear();

        switch (this.classe.getTipo()){
            case CONCRETE:
                this.updateConcrete();
                break;
            case ABSTRACT:
                this.updateAbstract();
                break;
            case INTERFACE:
                this.updateInterface();
                break;
            case ENUM:
                this.updateEnum();
                break;
            default:
                break;
        }
        
        this.updateRelacoes();
    }
    
    private void updateConcrete() {
        Label className = new Label(this.classe.getNomeRepresentacao());
        className.setStyle("-fx-font-size: 20px;");
        this.gp_titulo.addRow(0, className);
        
        int row = 0;
        for(Variavel v: this.classe.getAtributos()) {
            this.gp_atributos.addRow(row, new Label(v.getRepresentacao()));
            row++;
        }
        
        row = 0;
        Label label;
        for(Metodo m: this.classe.getMetodos()) {
            label = new Label(m.getRepresentacao());
            if(label.getText().isEmpty()) { continue; }
            label.setUnderline(m.isEstatica());
            if(m.isAbstrata()) {
                label.setStyle("-fx-font-style: italic");
            }
            this.gp_metodos.addRow(row, label);
            row++;
        }
    }

    private void updateAbstract() {
        Label className = new Label(this.classe.getNomeRepresentacao());
        className.setStyle("-fx-font-size: 20px;");
        this.gp_titulo.addRow(0, new Label("<<"+this.classe.getTipo().toString()+">>"));
        this.gp_titulo.addRow(1, className);
        
        int row = 0;
        for(Variavel v: this.classe.getAtributos()) {
            this.gp_atributos.addRow(row, new Label(v.getRepresentacao()));
            row++;
        }
        
        row = 0;
        Label label;
        for(Metodo m: this.classe.getMetodos()) {
            label = new Label(m.getRepresentacao());
            if(label.getText().isEmpty()) { continue; }
            label.setUnderline(m.isEstatica());
            if(m.isAbstrata()) {
                label.setStyle("-fx-font-style: italic");
            }
            this.gp_metodos.addRow(row, label);
            row++;
        }
    }
    
    private void updateInterface() {
        Label className = new Label(this.classe.getNomeRepresentacao());
        className.setStyle("-fx-font-size: 20px;");
        this.gp_titulo.addRow(0, new Label("<<"+this.classe.getTipo().toString()+">>"));
        this.gp_titulo.addRow(1, className);
        
        int row = 0;
        Label label;
        for(Metodo m: this.classe.getMetodos()) {
            label = new Label(m.getRepresentacao());
            if(label.getText().isEmpty()) { continue; }
            label.setUnderline(m.isEstatica());
            if(m.isAbstrata()) {
                label.setStyle("-fx-font-style: italic");
            }
            this.gp_metodos.addRow(row, label);
            row++;
        }
    }
    
    private void updateEnum() {
        Label className = new Label(this.classe.getNomeRepresentacao());
        className.setStyle("-fx-font-size: 20px;");
        this.gp_titulo.addRow(0, new Label("<<"+this.classe.getTipo().toString()+">>"));
        this.gp_titulo.addRow(1, className);
        
        int row = 0;
        for(Variavel a :this.classe.getAtributos()) {
            this.gp_atributos.addRow(row, new Label(a.getRepresentacao()));
            row++;
        }
    }
    
    public DiagramaDeClasseController getRoot() {
        return root;
    }

    private boolean isValidRelacao(Relacao relacao, Classe classeOrigem) {
        if(relacao == null || classeOrigem == null) return false;
        TipoClasse destino = this.classe.getTipo();
        TipoClasse origem = classeOrigem.getTipo();
        
        switch (relacao) {
            case IMPLEMENTACAO:
                if(origem != TipoClasse.INTERFACE
                        || classeOrigem == this.classe
                        || destino == TipoClasse.INTERFACE) {
                    return false;
                } else {
                    return this.classe.isImplementacaoValida(classeOrigem);
                }
            case HERANCA:
                if(classeOrigem == this.classe
                        || (!(
                               (origem == TipoClasse.CONCRETE && destino == TipoClasse.CONCRETE)
                            || (origem == TipoClasse.CONCRETE && destino == TipoClasse.ABSTRACT)
                            || (origem == TipoClasse.ABSTRACT && destino == TipoClasse.CONCRETE)
                            || (origem == TipoClasse.ABSTRACT && destino == TipoClasse.ABSTRACT)
                            || (origem == TipoClasse.INTERFACE && destino == TipoClasse.INTERFACE)
                        ))) {
                    return false;
                } else {
                    return this.classe.isHerancaValida(classeOrigem);
                }
            case AGREGACAO:
                if(!(destino == TipoClasse.CONCRETE
                        || destino == TipoClasse.ABSTRACT)) {
                    return false;
                } else {
                    return this.classe.isAgregacaoValida(classeOrigem);
                }
            case DEPENDENCIA:
                return this.classe.isDependenciaValida(classeOrigem);
            default:
                return false;
        }
    }
    
}
