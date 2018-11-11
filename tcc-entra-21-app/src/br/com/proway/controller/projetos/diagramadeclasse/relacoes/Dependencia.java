package br.com.proway.controller.projetos.diagramadeclasse.relacoes;

import br.com.proway.enumeradores.Relacao;
import br.com.proway.util.FXUtil;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

/**
 * @author Vinícius Luis da Silva
 */
public class Dependencia implements RelacaoStrategy {
    
    private RelacaoController controller;
    private List<Node> nos = new ArrayList<>();
    private Label parte = new Label();
    private Label todo = new Label();
    
    @Override
    public void setMultiplicidadeParte(String parte) {
//        this.parte = parte;
        this.setMultiplicidade(parte, this.todo.getText());
    }

    @Override
    public void setMultiplicidadeTodo(String todo) {
//        this.todo = todo;
        this.setMultiplicidade(this.parte.getText(), todo);
    }

    public Dependencia(RelacaoController controller) {
        this.controller = controller;
    }

    @Override
    public void estabelecer() {
        double[] coords = FXUtil.getArrowCoordinates(controller.ln_1);
        Line ln_1 = new Line(coords[0], coords[1], coords[2], coords[3]);
        Line ln_2 = new Line(coords[2], coords[3], coords[4], coords[5]);
        if(controller.root_pane.getChildren().size() > 3) {
            Line ln_aux1 = (Line)controller.root_pane.getChildren().get(3);
            Line ln_aux2 = (Line)controller.root_pane.getChildren().get(4);
            controller.getOrigin().getRoot().pn_desktop.getChildren().remove(ln_aux1);
            controller.getOrigin().getRoot().pn_desktop.getChildren().remove(ln_aux2);
            controller.root_pane.getChildren().remove(ln_aux1);
            controller.root_pane.getChildren().remove(ln_aux2);
            controller.root_pane.getChildren().add(3, ln_1);
            controller.root_pane.getChildren().add(4, ln_2);
        } else {
            controller.root_pane.getChildren().add(ln_1);
            controller.root_pane.getChildren().add(ln_2);
        }
        nos.add(0, ln_1);
        nos.add(1, ln_2);
        this.setMultiplicidade(parte.getText(), todo.getText());
    }

    @Override
    public List<Node> getNodes() {
        return nos;
    }

    @Override
    public void setMultiplicidade(String parte, String todo) {
//        Node[] nos = FXUtil.getNosMultiplicidade(parte, todo, controller.ln_1, controller.ln_2, controller.ln_3);
//        if(this.nos.size() >= 3) {
//            controller.root_pane.getChildren().remove(this.nos.get(2));
//            controller.root_pane.getChildren().remove(this.nos.get(3));
//        }
//        this.nos.add(2, nos[0]);
//        this.nos.add(3, nos[1]);
//        controller.root_pane.getChildren().add(this.nos.get(2));
//        controller.root_pane.getChildren().add(this.nos.get(3));
        Label[] nos = FXUtil.getNosMultiplicidade(parte, todo, controller.ln_1, controller.ln_2, controller.ln_3);
        controller.root_pane.getChildren().remove(this.parte);
        controller.root_pane.getChildren().remove(this.todo);
        this.parte = nos[0];
        this.todo = nos[1];
        controller.root_pane.getChildren().add(this.parte);
        controller.root_pane.getChildren().add(this.todo);
    }

    @Override
    public void deestabelecer() {
        if(controller.root_pane.getChildren().size() > 3) {
            Line ln_1 = (Line)controller.root_pane.getChildren().get(3);
            Line ln_2 = (Line)controller.root_pane.getChildren().get(4);
            controller.getOrigin().getRoot().pn_desktop.getChildren().remove(ln_1);
            controller.getOrigin().getRoot().pn_desktop.getChildren().remove(ln_2);
            controller.root_pane.getChildren().remove(ln_1);
            controller.root_pane.getChildren().remove(ln_2);
            
        }
    }

    @Override
    public Relacao getTipo() {
        return Relacao.DEPENDENCIA;
    }

}
