package br.com.proway.vo.diagramadeclasse;

import br.com.proway.enumeradores.Modificador;
import br.com.proway.util.Java;
import br.com.proway.util.Patterns;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author Vinícius Luis da Silva
 */
public class Metodo implements Comparable<Metodo>{
    
    private Classe classe;
    private Modificador modificador;
    private boolean abstrata;
    private boolean estatica;
    private String nome;
    private String retorno;
    private Set<Variavel> parametros;
    private String documentacao;
    private String assinatura;
    private boolean valid;
    
    public Metodo(Classe classe) {
        this.parametros = new LinkedHashSet<>();
        this.documentacao = new String();
        this.classe = classe;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if(nome.isEmpty() || Java.isPalavraReservada(nome)) {
            throw new IllegalArgumentException();
        }
        this.nome = nome;
    }

    public String getRetorno() {
        return retorno;
    }

    public void setRetorno(String retorno) {
        retorno = retorno.replaceAll("[\\s]*", "");
        if(retorno.equals("void")) {
            this.retorno = retorno;
        } else {
            new Variavel(this.classe).setTipo(retorno);
            this.retorno = retorno;
        }
    }
    
    public String getCode(boolean override) {
        if(!isValid()) {
            throw new IllegalArgumentException("Não é possível gerar código enquanto houver um método inválido no diagrama!");
        }
        String code = new String();
        if(!this.isValid()) return code;
        String doc = this.getDocumentacao();
        if(!doc.isEmpty()) {
            String[] docLines = doc.split("\r\n");
            code += "\t/**\r\n";
            for(String str: docLines) {
                code += "\t  * " + str + "\r\n";
            }
            code += "\t  */\r\n";
        }
        if(override) {
            code += "\t@Override\r\n";
        }
        code += "\t";
        code += this.modificador.getCode();
        code += ((this.isAbstrata() && !override) ? "abstract " : "");
        code += (this.isEstatica() ? "static " : "");
        code += this.getRetorno();
        code += " ";
        code += this.getNome();
        code += "(";
        for(Variavel param: this.parametros) {
            code += param.getCode();
            code += ", ";
        }
        if(code.substring(code.length() - 2, code.length()).equals(", ")) {
            code = code.substring(0, code.length() - 2);
        }
        code += ")";
        return code;
    }

    public Set<Variavel> getParametros() {
        return parametros;
    }
    
    public void addParamentro(Variavel parametro) {
        if(parametro == null) {
            throw new NullPointerException();
        }
        this.parametros.add(parametro);
    }

    public String getDocumentacao() {
        if(documentacao.equals(getDefaultDoc())) {
            return new String();
        } else {
            return documentacao;
        }
    }

    public void setDocumentacao(String documentacao) {
        this.documentacao = documentacao;
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

    public boolean isAbstrata() {
        return abstrata;
    }

    public void setAbstrata(boolean abstrata) {
        this.abstrata = abstrata;
    }

    public boolean isEstatica() {
        return estatica;
    }

    public void setEstatica(boolean estatica) {
        this.estatica = estatica;
    }
    
    public void setAssinatura(String assinatura) {
        try {
            this.assinatura = assinatura;
            this.setModificador(Patterns.find("([+]|[-]|[#])", assinatura).charAt(0));
            this.setNome(Patterns.find("[a-zA-Z]+", assinatura));
            this.findParametros(assinatura);
            
            if(assinatura.replaceAll("[\\s]*", "").contains("):")) {
                String ret = Patterns.find("[)][\\s]*"
                                            + "[:]"
                                            + "[\\s]*[a-zA-Z][\\w]*"
                                            + "[\\s]*([\\.][\\s]*[a-zA-Z][\\w]*[\\s]*)*"
                                            + "[\\s]*", assinatura);
                if(assinatura.replaceAll("[\\s]*", "").endsWith("[]")) {
                    ret += "[]";
                }
                
                ret = ret.replaceAll("[)][\\s]*[:][\\s]*", "");
                this.setRetorno(ret);
            } else {
                if(this.nome.equals(classe.getNomeRepresentacao())) {
                    this.retorno = null;
                } else {
                    throw new IllegalArgumentException("Se o método não tiver retorno, deve ser um construtor!");
                }
            }
            this.setValid(true);
        } catch (Exception e) {
            this.modificador = null;
            this.nome = null;
            this.parametros.clear();
            this.retorno = null;
            this.setValid(false);
        }
    }
    
    public void resetAssinatura() {
        this.setAssinatura(assinatura);
    }
    
    public boolean isValid() {
        return valid;
    }
    
    public void setValid(boolean value) {
        this.valid = value;
    }
    
    private void findParametros(String assinatura) {
        this.parametros.clear();
        assinatura = assinatura.replaceAll("[\\s]*([+]|[-]|[#])[\\s]*[\\w]+[\\s]*[(][\\s]*", "");
        assinatura = assinatura.replaceAll("[\\s]*[)][\\s]*[:][\\s]*[\\w]+[\\s]*([\\.][\\s]*[a-zA-Z][\\w]*[\\s]*)*\\[[\\s]*\\][\\s]*", "");
        assinatura = assinatura.replaceAll("[\\s]*[)][\\s]*[:][\\s]*[\\w]+[\\s]*([\\.][\\s]*[a-zA-Z][\\w]*[\\s]*)*", "");
        assinatura = assinatura.replaceAll("[\\s]*[)][\\s]*", "");
        if(assinatura.replaceAll("[\\s]*", "").replaceAll("\\[\\]", "").isEmpty()) {
            return;
        }
        if(!assinatura.matches(
                "[\\s]*[a-zA-Z][\\w]*[\\s]*[:][\\s]*[a-zA-Z][\\w]*[\\s]*([\\.][\\s]*[a-zA-Z][\\w]*[\\s]*)*"
                + "[\\s]*(\\[[\\s]*\\])??[\\s]*([,][\\s]*[a-zA-Z][\\w]*[\\s]*[:][\\s]*[a-zA-Z][\\w]*[\\s]*"
                + "([\\.][\\s]*[a-zA-Z][\\w]*[\\s]*)*[\\s]*(\\[[\\s]*\\])??)*")) {
            throw new IllegalArgumentException();
        }
        assinatura = assinatura.replaceAll("[\\s]*", "");
        
        
        String[] params = assinatura.split(",");
        Variavel parametro;
        String nome;
        String tipo;
        Set<String> nomes = new LinkedHashSet<>();
        for (int i = 0; i < params.length; i++) {
            if(params[i].matches("[a-zA-Z][\\w]*[:][a-zA-Z][\\w]*([\\.][a-zA-Z][\\w]*)*(\\[\\])??")) {
                nome = Patterns.find("[a-zA-Z][\\w]*", params[i]);
                tipo = Patterns.find("[:][a-zA-Z][\\w]*([\\.][a-zA-Z][\\w]*)*", params[i]);
                tipo += params[i].endsWith("[]") ? "[]" : "";
                tipo = tipo.replaceFirst("[:]", "");
                if(nomes.add(nome)) {
                    parametro = new Variavel(classe);
                    parametro.setNome(nome);
                    parametro.setTipo(tipo);
                    parametros.add(parametro);
                } else {
                    throw new IllegalArgumentException();
                }
            } else {
                throw new IllegalArgumentException();
            }
            
        }
            
    }
    
    public String getAssinatura() {
        String ret = new String();
        if((nome == null || nome.isEmpty())
                && (this.parametros == null || this.parametros.isEmpty())
                && (this.retorno == null || this.retorno.isEmpty())) {
            return ret;
        }
        if(modificador != null) {
            switch(this.modificador) {
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
        ret += "(";
        for(Variavel param: this.parametros) {
            ret += param.getNome();
            ret += " : ";
            ret += param.getAbvTipo();
            ret += ", ";
        }
        if(this.parametros.size() > 0) {
            ret = ret.substring(0, ret.length() - 2);
        }
        ret += ")";
        if(this.retorno != null) {
            ret += " : " + this.retorno.toString();
        }
        
        return ret;
    }

    public String getRepresentacao() {
        if(!this.isValid()) {
            return "Método Inválido!";
        } else {
            return getAssinatura();
        }
    }
    
    public String getDefaultDoc() {
        String content = "\n";
        for(Variavel p: this.getParametros()) {
            content += "@param ";
            content += p.getNome();
            content += " \n";
        }
        if(this.getRetorno() != null && !this.getRetorno().equals("void")) {
            content += "@return ";
        }
        return content.equals("\n") ? new String() : content;
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
        final Metodo other = (Metodo) obj;
        if (this.abstrata != other.abstrata) {
            return false;
        }
        if (this.estatica != other.estatica) {
            return false;
        }
        if (!Objects.equals(this.nome, other.nome)) {
            return false;
        }
        if (!Objects.equals(this.retorno, other.retorno)) {
            return false;
        }
        if(this.parametros.size() != other.parametros.size()) {
            return false;
        }
        Iterator<Variavel> thisParamentros = this.parametros.iterator();
        Iterator<Variavel> otherParamentros = other.parametros.iterator();
        Variavel p1;
        Variavel p2;
        while(thisParamentros.hasNext()) {
            p1 = thisParamentros.next();
            p2 = otherParamentros.next();
            if(!p1.equals(p2)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return this.getNome();
    }

    @Override
    public int compareTo(Metodo o) {
        return 1;
    }
   
}
