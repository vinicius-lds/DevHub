package br.com.proway.controller.projetos.diagramadeclasse.relacoes;

import br.com.proway.enumeradores.Relacao;
import br.com.proway.util.FXUtil;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

/**
 * @author Vinícius Luis da Silva
 */
public class Implementacao implements RelacaoStrategy {
    
    private RelacaoController controller;
    private List<Node> nos = new ArrayList<>();

    public Implementacao(RelacaoController controller) {
        this.controller = controller;
    }

    @Override
    public void estabelecer() {
        Polygon triangulo = new Polygon(FXUtil.getTriagleCoordinates(controller.ln_1));
        triangulo.setFill(Color.WHITE);
        triangulo.setStroke(Color.BLACK);
        if(controller.root_pane.getChildren().size() > 3) {
            Polygon aux = (Polygon)controller.root_pane.getChildren().get(3);
            controller.getOrigin().getRoot().pn_desktop.getChildren().remove(aux);
            controller.root_pane.getChildren().remove(aux);
            controller.root_pane.getChildren().add(3, triangulo);
        } else {
            controller.root_pane.getChildren().add(triangulo);
        }
        nos.add(0, triangulo);
        double aux = 5;
        controller.ln_1.getStrokeDashArray().add(aux);
        controller.ln_2.getStrokeDashArray().add(aux);
        controller.ln_3.getStrokeDashArray().add(aux);
    }

    @Override
    public List<Node> getNodes() {
        return nos;
    }

    @Override
    public void setMultiplicidade(String parte, String todo) {}

    @Override
    public void setMultiplicidadeParte(String parte) {}

    @Override
    public void setMultiplicidadeTodo(String todo) {}
    
    @Override
    public void deestabelecer() {
        if(controller.root_pane.getChildren().size() > 3) {
            Polygon aux = (Polygon)controller.root_pane.getChildren().get(3);
            controller.getOrigin().getRoot().pn_desktop.getChildren().remove(aux);
            controller.root_pane.getChildren().remove(aux);
        }
    }

    @Override
    public Relacao getTipo() {
        return Relacao.IMPLEMENTACAO;
    }
    
}
