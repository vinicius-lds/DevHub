package br.com.proway.controller.projetos.diagramadeclasse.relacoes;

import br.com.proway.controller.projetos.diagramadeclasse.ClasseController;
import br.com.proway.enumeradores.Relacao;
import br.com.proway.enumeradores.TipoClasse;
import br.com.proway.vo.diagramadeclasse.AgregacaoVo;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

/**
 * FXML Controller class
 *
 * @author Vinícius Luis da Silva
 */
public class RelacaoController implements Initializable {

    private ClasseController origin;
    private ClasseController destination;
    private int originPoint;
    private int destinationPoint;
    private RelacaoStrategy strategy;
    private Relacao tipo;
    private AgregacaoVo agregacao;

    @FXML
    protected Pane root_pane;
    @FXML
    protected Line ln_1;
    @FXML
    protected Line ln_2;
    @FXML
    protected Line ln_3;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.root_pane.onMouseClickedProperty().set(e -> {
            this.setSelected(e);
        });
//        this.ln_1.onMouseClickedProperty().set(e -> {
//            if(e.getButton() == MouseButton.PRIMARY) {
//                this.setSelected(e);
//            }
//        });
//        this.ln_2.onMouseClickedProperty().set(e -> {
//            if(e.getButton() == MouseButton.PRIMARY) {
//                this.setSelected(e);
//            }
//        });
//        this.ln_3.onMouseClickedProperty().set(e -> {
//            if(e.getButton() == MouseButton.PRIMARY) {
//                this.setSelected(e);
//            }
//        });
    }
    
    public void setVisible(boolean value) {
        this.root_pane.setVisible(value);
    }

    public void updateLine() {
        Point2D[] conexoes = getLocaisDeConexaoMaisProximos(getLocaisDeConexao(origin.ap_classe), getLocaisDeConexao(destination.ap_classe));
        this.updateLine(conexoes);
    }
    
    private void updateLine(Point2D[] conexoes) {
        this.ln_1.setVisible(true);
        
        if ((conexoes[0].getX() == conexoes[1].getX()) || (conexoes[0].getY() == conexoes[1].getY())) {
            //Linha reta
            //Somanete uma line
            this.ln_1.setStartX(conexoes[0].getX());
            this.ln_1.setStartY(conexoes[0].getY());

            this.ln_1.setEndX(conexoes[1].getX());
            this.ln_1.setEndY(conexoes[1].getY());
        } else {
            this.ln_2.setVisible(true);
            if (!this.ln_3.isVisible()) {
                // É necessario somente duas linhas para completas a relação
                this.ln_2.setStartX(conexoes[0].getX());
                this.ln_2.setStartY(conexoes[0].getY());

                Point2D meio;
                if (isPar(originPoint)) {
                    meio = new Point2D(conexoes[0].getX(), conexoes[1].getY());
                } else {
                    meio = new Point2D(conexoes[1].getX(), conexoes[0].getY());
                }

                this.ln_2.setEndX(meio.getX());
                this.ln_2.setEndY(meio.getY());

                this.ln_1.setStartX(meio.getX());
                this.ln_1.setStartY(meio.getY());

                this.ln_1.setEndX(conexoes[1].getX());
                this.ln_1.setEndY(conexoes[1].getY());
            } else {
                // É necessário tres linhas para completar a ligação
                this.ln_3.setStartX(conexoes[0].getX());
                this.ln_3.setStartY(conexoes[0].getY());

                Point2D primeiro;
                Point2D segundo;

                if (!((destinationPoint == 0 || destinationPoint == 2) && (originPoint == 0 || originPoint == 2))) {
                    primeiro = new Point2D(getModulo((conexoes[0].getX() + conexoes[1].getX()) / 2), conexoes[0].getY());
                    segundo = new Point2D(getModulo((conexoes[0].getX() + conexoes[1].getX()) / 2), conexoes[1].getY());
                } else {
                    primeiro = new Point2D(conexoes[0].getX(), (getModulo(conexoes[0].getY() + conexoes[1].getY()) / 2));
                    segundo = new Point2D(conexoes[1].getX(), (getModulo(conexoes[0].getY() + conexoes[1].getY()) / 2));
                }

                this.ln_3.setEndX(primeiro.getX());
                this.ln_3.setEndY(primeiro.getY());

                this.ln_2.setStartX(primeiro.getX());
                this.ln_2.setStartY(primeiro.getY());

                this.ln_2.setEndX(segundo.getX());
                this.ln_2.setEndY(segundo.getY());

                this.ln_1.setStartX(segundo.getX());
                this.ln_1.setStartY(segundo.getY());

                this.ln_1.setEndX(conexoes[1].getX());
                this.ln_1.setEndY(conexoes[1].getY());
            }
        }
        if(strategy != null) {
            this.strategy.estabelecer();
//            for(Node n: strategy.getNodes()) {
//                n.onMouseClickedProperty().set(e -> {
//                    if(e.getButton() == MouseButton.PRIMARY) {
//                        this.setSelected(e);
//                    }
//                });
//            }
        }
        //this.tipo.setMultiplicidade("0..1", "0..*");
    }

    private Point2D[] getCantos(AnchorPane ap) {
        Bounds b = ap.getBoundsInParent();

        Point2D point1 = new Point2D(b.getMinX(), b.getMinY());
        Point2D point2 = new Point2D(b.getMinX() + b.getWidth(), b.getMinY());
        Point2D point3 = new Point2D(b.getMaxX() - b.getWidth(), b.getMaxY());
        Point2D point4 = new Point2D(b.getMaxX(), b.getMaxY());

        return new Point2D[]{point1, point2, point3, point4};
    }

    private Point2D[] getLocaisDeConexao(AnchorPane ap) {
        Point2D[] cantos = this.getCantos(ap);

        Point2D p1 = new Point2D((cantos[1].getX() + cantos[0].getX()) / 2, cantos[0].getY());
        Point2D p2 = new Point2D(cantos[1].getX(), (cantos[3].getY() + cantos[1].getY()) / 2);
        Point2D p3 = new Point2D((cantos[3].getX() + cantos[2].getX()) / 2, cantos[2].getY());
        Point2D p4 = new Point2D(cantos[0].getX(), (cantos[2].getY() + cantos[0].getY()) / 2);

        return new Point2D[]{p1, p2, p3, p4};
    }

    private Point2D[] getLocaisDeConexaoMaisProximos(Point2D[] node1, Point2D[] node2) {
        Point2D[] menorDistancia = new Point2D[]{node1[0], node2[0]};

        for (int i = 0; i < node1.length; i++) {
            for (int j = 0; j < node2.length; j++) {
                if ((getModulo(menorDistancia[0].getX() - menorDistancia[1].getX()) + getModulo(menorDistancia[0].getY() - menorDistancia[1].getY()))
                        > (getModulo(node1[i].getX() - node2[j].getX()) + getModulo(node1[i].getY() - node2[j].getY()))) {
                    menorDistancia[0] = node1[i];
                    menorDistancia[1] = node2[j];
                    originPoint = i;
                    destinationPoint = j;
                    if (isPar(originPoint) != isPar(destinationPoint)) {
                        ln_3.setVisible(false);
                    } else {
                        ln_3.setVisible(true);
                    }
                }
            }
        }

        return menorDistancia;
    }
    
    public void bindToMouseMoviment() {
//        if(this.tipo != null) {
//            tipo.deestabelecer();
//        }
//        this.tipo = null;
        Point2D[] aux = getLocaisDeConexao(origin.ap_classe);
        origin.getRoot().pn_desktop.onDragOverProperty().setValue((e) -> {
            updateLine(getLocaisDeConexaoMaisProximos(aux, new Point2D[] {new Point2D(e.getX(), e.getY())}));
        });
    }
    
    public void excluir() {
        this.origin.getRoot().pn_desktop.getChildren().remove(this.root_pane);
        this.origin.getRoot().relacoes.remove(this);
        if(this.origin != null && this.destination != null) {
            this.origin.classe.desvincular(this.destination.classe);
            this.destination.classe.desvincular(this.origin.classe);
        }
        this.origin.getRoot().clearSelection();
        this.origin.getRoot().updateDiagrama();
//        ObservableList nodes = this.origin.getRoot().pn_desktop.getChildren();
//        this.root_pane.setVisible(false);
//        this.ln_1.setVisible(false);
//        this.ln_2.setVisible(false);
//        this.ln_3.setVisible(false);
//        nodes.remove(this.root_pane);
//        nodes.remove(this.ln_1);
//        nodes.remove(this.ln_2);
//        nodes.remove(this.ln_3);
//        List<Node> extraNodes = this.strategy.getNodes();
//        for(Node n: extraNodes) {
//            this.origin.getRoot().pn_desktop.getChildren().remove(n);
//            n.setVisible(false);
//        }
//        this.strategy = null;
    }
    
    public void unbindToMouseMoviment() {
        this.root_pane.onMouseMovedProperty().setValue(null);
    }

    public RelacaoStrategy getStrategy() {
        return strategy;
    }

    public void setTipo(Relacao tipo) {
        switch (tipo) {
            case HERANCA:
                this.strategy = new Heranca(this);
                break;
            case AGREGACAO:
                this.strategy = new Agregacao(this);
                break;
            case IMPLEMENTACAO:
                this.strategy = new Implementacao(this);
                break;
            case COMPOSICAO:
                //this.strategy = new Composicao(this);
                throw new IllegalArgumentException("Relação de composição não é suportada pela Linguagem Java!");
            case DEPENDENCIA:
                this.strategy = new Dependencia(this);
                break;
            default:
                this.strategy = null;
                break;
        }
        this.tipo = tipo;
    }
    
    public void confirmaRelacao() {
        switch (tipo) {
            case HERANCA:
                if(origin.classe.getTipo() == TipoClasse.INTERFACE
                        && destination.classe.getTipo() == TipoClasse.INTERFACE) {
                    origin.classe.addImplementacao(destination.classe);
                } else {
                    origin.classe.setHeranca(destination.classe);
                }
                break;
            case AGREGACAO:
                this.agregacao = new AgregacaoVo(origin.classe, destination.classe);
                origin.classe.addAgregacao(this.agregacao);
                destination.classe.addAgregacao(this.agregacao);
                break;
            case IMPLEMENTACAO:
                origin.classe.addImplementacao(destination.classe);
                break;
            case DEPENDENCIA:
                origin.classe.addDependencia(destination.classe);
                break;
            default:
                this.strategy = null;
                break;
        }
    }
    
    public Relacao getTipo() {
        return tipo;
    }
    
    private double getModulo(double valor) {
        if (valor < 0) {
            return valor * (-1);
        }
        return valor;
    }
    
    public void setOpacity(double value) {
        this.root_pane.setOpacity(value);
    }

    private boolean isPar(double valor) {
        return valor % 2 == 0;
    }

    public ClasseController getOrigin() {
        return origin;
    }

    public void setOrigin(ClasseController origin) {
        this.origin = origin;
    }

    public ClasseController getDestination() {
        return destination;
    }

    public void setDestination(ClasseController destination) {
        this.destination = destination;
    }
    
    public Pane getRoot_pane() {
        return root_pane;
    }

    public AgregacaoVo getAgregacao() {
        return agregacao;
    }

    private void setSelected(MouseEvent e) {
        List<Initializable> aux = this.origin.getRoot().objetosSelecionados;
        if(!aux.contains(this)) {
            if(!e.isControlDown()) {
                origin.getRoot().clearSelection();
            }
            origin.getRoot().select(this);
            this.setOpacity(0.5);
        } else {
            if(!e.isControlDown()) {
                origin.getRoot().clearSelection();
            } else {
                aux.remove(this);
                this.setOpacity(1);
            }
        }
    }
    
}
