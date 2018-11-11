package br.com.proway.controller.projetos.diagramadeclasse.relacoes;

import br.com.proway.enumeradores.Relacao;
import java.util.List;
import javafx.scene.Node;

/**
 * @author Vinícius Luis da Silva
 */
public interface RelacaoStrategy {
    
    public void estabelecer();
    public void deestabelecer();
    public List<Node> getNodes();
    public void setMultiplicidade(String parte, String todo);
    public void setMultiplicidadeParte(String parte);
    public void setMultiplicidadeTodo(String todo);
    public Relacao getTipo();
    
}
