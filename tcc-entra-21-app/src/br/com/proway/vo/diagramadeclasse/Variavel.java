package br.com.proway.vo.diagramadeclasse;

import br.com.proway.enumeradores.Modificador;
import br.com.proway.util.Java;
import br.com.proway.util.Patterns;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author Vinícius Luis da Silva
 */
public class Variavel implements Comparable<Variavel>{

    protected static HashMap<String, Set> possiveisImports;
    
    public static void setPossiveisImports(HashMap<String, Set> imps) {
        possiveisImports = imps;
    }
    
    private Classe classe;
    protected String nome;
    private String abvTipo;
    private String impTipo;
    private Modificador modificador;
    private String documentacao;
    private boolean estatica;
    private boolean constante;
    protected String assinatura;
    private boolean valid;

    public Variavel(Classe classe) {
        this.nome = new String();
        this.classe = classe;
    }

    public Classe getClasse() {
        return classe;
    }
    
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if(nome.isEmpty() || Java.isPalavraReservada(nome)) {
            this.valid = false;
            throw new IllegalArgumentException();
        }
        this.nome = nome;
        this.valid = true;
    }
    
    public String getTipo() {
        if(impTipo == null || impTipo.isEmpty()) {
            return abvTipo;
        } else {
            if(classe.getImports().contains(impTipo)) {
                return abvTipo;
            } else {
                return impTipo;
            }
        }
    }
    
    public String getCode() {
        if(!isValid()) {
            throw new IllegalArgumentException("Não é possível gerar código enquanto houver uma variavel inválida no diagrama!");
        }
        return (this.modificador != null ? this.modificador.getCode() : "")
                + (this.isEstatica() ? "static " : "")
                + (this.isConstante() ? "final " : "")
                + this.getTipo()
                + " "
                + this.getNome();
    }
    
    public String getCodeAgregacao() {
        String ret = (this.modificador != null ? this.modificador.getCode() : "");
        if(!this.classe.containsImport(this.impTipo)) {
            ret += abvTipo;
        } else {
            ret += impTipo;
        }
        ret += " ";
        ret += this.getNome();
        return ret;
    }

    public String getAbvTipo() {
        return abvTipo;
    }

    public void setAbvTipo(String abvTipo) {
        this.abvTipo = abvTipo;
    }

    public String getImpTipo() {
        return impTipo;
    }

    public void setImpTipo(String impTipo) {
        this.impTipo = impTipo;
    }

    public boolean isEstatica() {
        return estatica;
    }

    public void setEstatica(boolean estatica) {
        this.estatica = estatica;
    }

    public boolean isConstante() {
        return constante;
    }

    public void setConstante(boolean constante) {
        this.constante = constante;
    }

    public Modificador getModificador() {
        return modificador;
    }

    public void setModificador(Modificador modificador) {
        this.modificador = modificador;
    }

    public void setModificador(char modificador) {
        switch(modificador) {
            case '+':
                this.modificador = Modificador.PUBLIC;
                break;
            case '-':
                this.modificador = Modificador.PRIVATE;
                break;
            case '#':
                this.modificador = Modificador.PROTECTED;
                break;
            default:
                throw new IllegalArgumentException(modificador + " não é uma representação de modificador válida!");
        }   
    }
    
    public void setAssinatura(String assinatura) {
        this.setAssinatura(assinatura, null);
    }
    
    public void resetAssinatura() {
        this.setAssinatura(assinatura);
    }
    
    public void setAssinatura(String assinatura, String impTipo) {
        try {
            this.assinatura = assinatura;
            if(!Patterns.isValidAttributeName(assinatura)) {
                throw new IllegalArgumentException();
            }
            this.setModificador(Patterns.find("([+]|[-]|[#])", assinatura).charAt(0));
            this.setNome(Patterns.find("[a-zA-Z]+", assinatura));
            String tipo = Patterns
                    .find("[:][\\s]*[a-zA-Z][\\w]*([\\s]*[\\.][\\s]*[a-zA-Z][\\w]*)*([\\w]|[\\s]*\\[[\\s]*\\])[\\s]*", assinatura)
                    .replaceAll("[:][\\s]*", "")
                    .replaceAll("[\\s]*[\\.]", "\\.")
                    .replaceAll("[\\.][\\s]*", "\\.")
                    .replaceAll("[\\s]*\\[[\\s]*", "\\[")
                    .replaceAll("[\\s]*\\][\\s]*", "\\]")
                    .replaceAll("[\\s]*", "");
            this.setTipo(tipo);
            this.setValid(true);
        } catch (Exception e) {
            this.setValid(false);
        }
    }
    
    public String getAssinatura() {
        String ret = new String();
        if(getModificador() == null 
                && (getNome() == null || getNome().isEmpty()) 
                && (getAbvTipo() == null || getAbvTipo().isEmpty())) {
            return ret;
        }
        if(getModificador() != null) {
            switch(getModificador()) {
                case PUBLIC:
                    ret += "+";
                    break;
                case PRIVATE:
                    ret += "-";
                    break;
                case PROTECTED:
                    ret += "#";
                    break;
                default:
                    break;
            }
        }
        ret += " ";
        ret += this.getNome() == null ? "" : this.getNome();
        ret += " : ";
        ret += this.getAbvTipo() == null ? "" : this.getAbvTipo();
        return ret;
    }
    
    public String getRepresentacao() {
        if(!this.isValid()) {
            return "Variavel Inválida!";
        } else {
            return getAssinatura();
        }
    }
    
    public boolean isValid() {
        return valid;
    }
    
    public void setValid(boolean value) {
        this.valid = value;
    }

    public void setTipo(String tipo) {
        boolean isArray = false;
        if(tipo.endsWith("[]")) {
            isArray = true;
            tipo = tipo.replaceAll("[\\s]*[\\[][\\s]*[\\]][\\s]*", "");
        }
        if(Java.isTipoPrimitivo(tipo) || tipo.equals(this.classe.getNome())) {
            this.abvTipo = tipo + (isArray ? "[]" : "");
        } else {
            //Variavel de classe no mesmo pacote
            List<Classe> aux2 = this.classe.diagrama.pacotes.get(this.classe.pacote.replace(/*aux + */tipo, ""));
            if(aux2 != null) {
                String aux3 = tipo.replace(".", "");
                for(Classe c: aux2) {
                    if(c.getNome().equals(aux3)) {
                        this.abvTipo = tipo + (isArray ? "[]" : "");
                        return;
                    }
                }
            }
            
            List<String> imp = getPossiveisImports(tipo);
            if(imp.isEmpty()) {
                throw new IllegalArgumentException("Tipo de dados inválido! " + tipo);
            } else {
                this.abvTipo = tipo + (isArray ? "[]" : "");
                if(!imp.get(0).startsWith("java.lang") && !imp.get(0).equals(this.abvTipo)) {
                    this.classe.addImport(imp.get(0));
                }
            }
            
        }
    }
    
    public void setTipo(String tipo, String imp) {
        this.impTipo = imp;
        this.abvTipo = tipo;
    }
    
    public String getDocumentacao() {
        return documentacao;
    }

    public void setDocumentacao(String documentacao) {
        this.documentacao = documentacao;
    }
    
    
    @Override
    public int compareTo(Variavel o) {
        return this.nome.compareTo(o.nome);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Variavel other = (Variavel) obj;
        if (!Objects.equals(this.nome, other.nome)) {
            return false;
        }
        return true;
    }

    private List<String> getPossiveisImports(String tipo) {
        final String aux;
//        aux = new String();
        if(!tipo.contains(".")) {
            aux = ".";
        } else {
            aux = new String();
        }
        List<String> imp = new ArrayList<>();
        Classe heranca = classe.getHeranca();
        
        if(heranca != null && (heranca.getPacote() + "." + heranca.getNome()).endsWith(aux + tipo)) {
            if((heranca.getPacote() + "." + heranca.getNome()).startsWith("java.lang")) {
                imp.clear();
            }
            imp.add(heranca.getPacote() + "." + heranca.getNome());
            return imp;
        }
        
        imp.addAll(this.aux(classe.getDependencias(), aux, tipo));
        imp.addAll(this.aux(classe.getImplementacoes(), aux, tipo));
        
        if(!imp.isEmpty()) {
            return imp;
        }
        
        String importacao;
        for(AgregacaoVo agregacao: classe.getAgregacoes()) {
            importacao = agregacao.getOtherImport(classe);
            if(importacao.endsWith(aux + tipo)) {
                if(importacao.startsWith("java.lang")) {
                    imp.clear();
                    imp.add(importacao);
                    return imp;
                }
                imp.add(importacao);
            }
        }
        
        Iterator<Set> keys = possiveisImports.values().iterator();
        Object[] imps;
        while(keys.hasNext()) {
            imps = keys.next().toArray();
            for (int i = 0; i < imps.length; i++) {
                if(((String)imps[i]).endsWith(aux + tipo)) {
                    if(((String)imps[i]).startsWith("java.lang")) {
                        imp.clear();
                        imp.add((String)imps[i]);
                        break;
                    }
                    imp.add((String) imps[i]);
                }
            }
        }
        return imp;
    }
    
    public List<String> aux(Set<Classe> list, String aux, String tipo) {
        List<String> imp = new ArrayList();
        for(Classe classe: list) {
            if((classe.getPacote() + "." + classe.getNome()).endsWith(aux + tipo)) {
                if((classe.getPacote() + "." + classe.getNome()).startsWith("java.lang")) {
                    imp.clear();
                    imp.add(classe.getPacote() + "." + classe.getNome());
                    break;
                }
                imp.add(classe.getPacote() + "." + classe.getNome());
            }
        }
        return imp;
    }
    
}
