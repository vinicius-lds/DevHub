package br.com.proway.vo.diagramadeclasse;

import br.com.proway.enumeradores.Modificador;
import br.com.proway.util.Patterns;
import br.com.proway.vo.diagramadeclasse.Classe;
import br.com.proway.vo.diagramadeclasse.Variavel;
import javafx.scene.control.Label;

/**
 * @author Vinícius Luis da Silva
 */
public class Enumerador extends Variavel {
    
    public Enumerador(Classe classe) {
        super(classe);
    }

    @Override
    public void setNome(String nome) {
        this.setAssinatura(nome);
    }
    
    @Override
    public void setAssinatura(String assinatura) {
        super.setValid(Patterns.isValidEnum(assinatura));
        super.nome = assinatura.trim();
        super.assinatura = assinatura;
    }

    @Override
    public String getRepresentacao() {
        if(!super.isValid()) {
            return "ENUM inválido!";
        } else {
            return super.nome;
        }
    }

    @Override
    public String getAssinatura() {
        return super.nome;
    }
    
    @Override
    public void setTipo(String tipo) {}

    @Override
    public void setModificador(Modificador modificador) {}

    @Override
    public void setAbvTipo(String abvTipo) {}

    @Override
    public void setAssinatura(String assinatura, String impTipo) {}

    @Override
    public void setDocumentacao(String documentacao) {}

    @Override
    public void setImpTipo(String impTipo) {}

    @Override
    public void setModificador(char modificador) {}

    @Override
    public void setConstante(boolean constante) {}

    @Override
    public void setEstatica(boolean estatica) {}
    
}
