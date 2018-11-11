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
public class Composicao implements RelacaoStrategy {
    
    private RelacaoController controller;
    private List<Node> nos = new ArrayList<>(3);
    private String parte;
    private String todo;
    
    @Override
    public void setMultiplicidadeParte(String parte) {
        this.parte = parte;
        this.setMultiplicidade(parte, this.todo);
    }

    @Override
    public void setMultiplicidadeTodo(String todo) {
        this.todo = todo;
        this.setMultiplicidade(this.parte, todo);
    }

    public Composicao(RelacaoController controller) {
        this.controller = controller;
    }

    @Override
    public void estabelecer() {
        Polygon diamond = new Polygon(FXUtil.getDiamondCoordinates(controller.ln_1));
        diamond.setFill(Color.BLACK);
        if(controller.root_pane.getChildren().size() > 3) {
            Polygon aux = (Polygon)controller.root_pane.getChildren().get(3);
            controller.getOrigin().getRoot().pn_desktop.getChildren().remove(aux);
            controller.root_pane.getChildren().remove(aux);
            controller.root_pane.getChildren().add(3, diamond);
        } else {
            controller.root_pane.getChildren().add(diamond);
        }
        nos.add(0, diamond);
        this.setMultiplicidade(parte, todo);
    }

    @Override
    public List<Node> getNodes() {
        return nos;
    }

    @Override
    public void setMultiplicidade(String parte, String todo) {
        Node[] nos = FXUtil.getNosMultiplicidade(parte, todo, controller.ln_1, controller.ln_2, controller.ln_3);
        if(this.nos.size() >= 2) {
            controller.root_pane.getChildren().remove(this.nos.get(1));
            controller.root_pane.getChildren().remove(this.nos.get(2));
        }
        this.nos.add(1, nos[0]);
        this.nos.add(2, nos[1]);
        controller.root_pane.getChildren().add(this.nos.get(1));
        controller.root_pane.getChildren().add(this.nos.get(2));
    }

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
        return Relacao.COMPOSICAO;
    }
    
}
