package br.com.proway.vo.diagramadeclasse;

import br.com.proway.enumeradores.TipoClasse;
import br.com.proway.util.Java;
import br.com.proway.util.Patterns;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author Vinícius Luis da Silva
 */
public class Classe implements Serializable{

    protected String pacote;
    protected Diagrama diagrama;
    private SortedSet<String> imports;
    private TipoClasse tipo;
    private String nome;
    private Set<Variavel> atributos;
    private Set<Metodo> metodos;
    private Classe heranca;
    private Set<Classe> implementacoes;
    private Set<Classe> dependencias;
    private Set<AgregacaoVo> agregacoes;
    private boolean valid;
    
    public Classe() {
        this.atributos = new LinkedHashSet();
        this.metodos = new LinkedHashSet<>();
        this.imports = new TreeSet<>();
        this.implementacoes = new LinkedHashSet<>();
        this.dependencias = new LinkedHashSet<>();
        this.agregacoes = new LinkedHashSet<>();
        this.nome = new String();
        this.pacote = new String();
    }
    
    public String getThisImport() {
        return this.pacote.isEmpty() ? new String() : this.pacote + "." + this.getNome();
    }
    
    public List<String> getLibraries() {
        List<String> libs = new ArrayList<>();
        Set<String> conteudoLib;
        for(String str: Variavel.possiveisImports.keySet()) {
            conteudoLib = Variavel.possiveisImports.get(str);
            for(String imp: this.imports) {
                if(conteudoLib.contains(imp)) {
                    libs.add(str);
                    break;
                }
            }
        }
        return libs;
    }
    
    public String getCode() {
        String code = new String();
        
        List<Variavel> varAgregacao = new ArrayList<>();
        SortedSet<String> imps = new TreeSet<>();
        Set<String> nomes = new HashSet<>();
        for(Variavel attr: this.atributos) {
            nomes.add(attr.getNome());
        }
        for(AgregacaoVo a: this.agregacoes) {
            for(Variavel v: a.getVariaveis(this)) {
                if(nomes.add(v.getNome())) {
                    varAgregacao.add(v);
                    if(!this.containsImport(v.getImpTipo())) {
                        imps.add(v.getImpTipo());
                        if(!this.containsImport(a.getOtherImport(this))) {
                            imps.add(a.getOtherImport(this));
                        }
                    }
                }
            }
        }
        
        if(this.pacote != null & !this.pacote.isEmpty()) {
            code += "package " + this.pacote + ";\r\n\r\n";
        }
        if(!this.imports.isEmpty() || !imps.isEmpty() || !dependencias.isEmpty() || !implementacoes.isEmpty() || heranca != null) {
            for(String imp: this.imports) {
                if(imp.isEmpty() || !imp.contains(".")) { continue; }
                code += "import " + imp + ";\r\n";
            }
            for(String imp: imps) {
                if(imp.isEmpty() || !imp.contains(".")) { continue; }
                
                code += "import " + imp + ";\r\n";
            }
            for(Classe classe: dependencias) {
                if(!classe.getPacote().equals(this.getPacote())) {
                    if(classe.getThisImport().isEmpty() || !classe.getThisImport().contains(".")) { continue; }
                    code += "import " + classe.getThisImport()+ ";\r\n";
                }
            }
            for(Classe classe: implementacoes) {
                if(!classe.getPacote().equals(this.getPacote())) {
                    if(classe.getThisImport().isEmpty() || !classe.getThisImport().contains(".")) { continue; }
                    code += "import " + classe.getThisImport()+ "\r\n";
                }
            }
            if(heranca != null) {
                if(!heranca.getPacote().equals(this.getPacote())) {
                    if(!heranca.getThisImport().isEmpty() || !heranca.getThisImport().contains(".")) { 
                        code += "import " + heranca.getThisImport()+ ";\r\n";
                    }
                }
            }
            code += "\r\n";
        }
        if(code.endsWith("\r\n\r\n\r\n")) { code = code.substring(0, code.length() - 1); }
        code += "public " + this.tipo.getCode() + this.getNome() + " ";
        if(this.tipo == TipoClasse.INTERFACE) {
            if(!this.implementacoes.isEmpty()) {
                code += "extends ";
                for(Classe implementacao: this.implementacoes) {
                    code += implementacao.getNome() + ", ";
                }
                code = code.substring(0, code.lastIndexOf(","));
            }
        } else {
            if(this.heranca != null) {
                code += "extends " + heranca.getNome() + " ";
            }
            if(!this.implementacoes.isEmpty()) {
                code += "implements ";
                for(Classe implementacao: this.implementacoes) {
                    code += implementacao.getNome() + ", ";
                }
                code = code.substring(0, code.lastIndexOf(","));
            }
        }
        code += " {\r\n";
        if(!this.atributos.isEmpty() || !varAgregacao.isEmpty()) {
            if(this.tipo == TipoClasse.ENUM) {
                for(Variavel attr: this.atributos) {
                    if(!attr.isValid()) {
                        throw new RuntimeException("Não é possível gerar código enquanto houver um enum inválido no diagrama!");
                    }
                    code += "\r\n\t" + attr.getNome() + ",";
                }
                if(code.endsWith(",")) {
                    code = code.substring(0, code.length() - 1);
                }
            } else {
                for(Variavel attr: this.atributos) {
                    code += "\r\n\t" + attr.getCode() + ";";
                }
            }
            for(Variavel agregacao: varAgregacao) {
                if(this.imports.contains(agregacao.getImpTipo())) {
                    code += "\r\n\t" + agregacao.getCode() + ";";
                } else {
                    if(this.containsImport(agregacao.getImpTipo())) {
                        code += "\r\n\t" + agregacao.getModificador().getCode() + agregacao.getImpTipo() + " " + agregacao.getNome() + ";";
                    } else {
                        code += "\r\n\t" + agregacao.getModificador().getCode() + agregacao.getAbvTipo() + " " + agregacao.getNome() + ";";
                    }
                }
            }
            code += "\r\n";
        }
        List<Metodo> metObrigatorio = new ArrayList<>();
        List<Metodo> metPai = new ArrayList<>();
        if(this.heranca != null) {
            this.heranca.getMetodosObrigatorios(metObrigatorio, metPai);
        }
        for(Classe impl: this.implementacoes) {
            impl.getMetodosObrigatorios(metObrigatorio, metPai);
        }
        for(Metodo met: this.metodos) {
            if(met.isAbstrata() || this.tipo == TipoClasse.INTERFACE) {
                code += "\r\n" + met.getCode(listContainsMethod(metPai, met)) + ";\r\n";
            } else {
                code += "\r\n" + met.getCode(listContainsMethod(metPai, met)) + "{\r\n\t\t\r\n\t}\r\n";
            }
        }
        if(this.tipo == TipoClasse.CONCRETE) {
            for(Metodo m: metObrigatorio) {
                code += "\r\n" + m.getCode(true) + "{\r\n\t\t\r\n\t}";
            }
        }
        code += "\r\n}";
        return code;
    }
    
    public boolean listContainsMethod(List<Metodo> metodos, Metodo metodo) {
        for(Metodo m: metodos) {
            if(m.equals(metodo)) {
                return true;
            }
        }
        return false;
    }
    
    public void getMetodosObrigatorios(List<Metodo> metodosObrigatorios, List<Metodo> todosMetodos) {
        todosMetodos.addAll(this.getMetodos());
        if(this.tipo == TipoClasse.ABSTRACT) {
            for(Metodo met: this.getMetodos()) {
                if(met.isAbstrata()) {
                    metodosObrigatorios.add(met);
                }
            }
        } else if(this.tipo == TipoClasse.INTERFACE) {
            metodosObrigatorios.addAll(this.getMetodos());
        }
        if(this.heranca != null) {
            this.heranca.getMetodosObrigatorios(metodosObrigatorios, todosMetodos);
        }
        for(Classe impl: this.implementacoes) {
            impl.getMetodosObrigatorios(metodosObrigatorios, todosMetodos);
        }
    }
    
    public void setDiagrama(Diagrama diagrama) {
        this.diagrama = diagrama;
    }

    public void setNome(String nome) {
        this.nome = new String();
        if (nome.isEmpty() || !Patterns.isValidClassName(nome) || Java.isPalavraReservada(nome)) {
            this.setValid(false);
        } else {
            this.setValid(true);
            List<Classe> classes = this.diagrama.pacotes.get(pacote);
            if(classes != null) {
                for(Classe c: classes) {
                    if(c.nome.equals(nome)) {
                        if(c == this) continue;
                        this.setValid(false);
                        return;
                    }
                }
            }
            this.nome = nome.trim();
            for(AgregacaoVo a: getAgregacoes()) {
                for(Variavel v: a.getVariaveis(a.getOtherClass(this))) {
                    v.setImpTipo(pacote.isEmpty() ? "" : pacote + "." + getNome());
                    v.setAbvTipo(getNome());
                }
            }
        }
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
    
    public boolean addMetodo(Metodo metodo) {
        if (metodo == null) {
            throw new NullPointerException();
        }
        return this.metodos.add(metodo);
    }

    public boolean addAtributo(Variavel atributo) {
        if (atributo == null) {
            throw new NullPointerException();
        }
        return this.atributos.add(atributo);
    }

    public String getNomeRepresentacao() {
        if(!this.isValid()) {
            return "Nome inválido!";
        }
        return nome;
    }
    
    public String getNome() {
        return nome;
    }
    
    public Set<Variavel> getAtributos() {
        return atributos;
    }

    public Set<Metodo> getMetodos() {
        return metodos;
    }
    
    public SortedSet<String> getImports() {
        return this.imports;
    }
    
    /**
     * 
     * @param imp
     * @return falso quando não foi possivel adicionar o import no set
     */
    public boolean addImport(String imp) {
        if(containsImport(imp)) {
            return false;
        } else {
            return this.imports.add(imp);
        }
    }
    
    public boolean containsImport(String imp) {
        String tipo = imp.contains(".") ? imp.substring(imp.lastIndexOf("."), imp.length()) : imp;
        String aux;
        for(String str: this.imports) {
            if(str.contains(".")) {
                aux = str.substring(str.lastIndexOf("."), str.length());
                if(aux.endsWith("." + tipo)) {
                    return true;
                }
            } else {
                aux = str;
                if(aux.equals(tipo)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * 
     * @param imp
     * @return true se foi removido com sucesso
     */
    public boolean removeImport(String imp) {
        return this.imports.remove(imp);
    }

    public TipoClasse getTipo() {
        return tipo;
    }

    public void setTipo(TipoClasse tipo) {
        if(tipo == null) {
            throw new NullPointerException("O tipo da classe não pode ser nulo!");
        }
        this.tipo = tipo;
    }
    
    public void setClassePai(Classe classePai) {
        if(classePai == null) {
            throw new NullPointerException();
        }
        if(classePai.getTipo() == TipoClasse.INTERFACE) {
            throw new IllegalArgumentException("A classe pai não pode ser uma Interface!");
        }
        if(classePai.getTipo() == TipoClasse.ENUM) {
            throw new IllegalArgumentException("A classe pai não pode ser um Enum!");
        }
        this.heranca = classePai;
    }

    public Classe getHeranca() {
        return heranca;
    }

    public void setHeranca(Classe heranca) {
        this.heranca = heranca;
    }

    public Set<Classe> getDependencias() {
        return dependencias;
    }

    public boolean addDependencia(Classe dependencia) {
        boolean aux = this.dependencias.add(dependencia);
        return aux;
    }

    public Set<AgregacaoVo> getAgregacoes() {
        return agregacoes;
    }

    public boolean addAgregacao(AgregacaoVo agregacao) {
        return this.agregacoes.add(agregacao);
    }
    
    public boolean addImplementacao(Classe implementacao) {
        return this.implementacoes.add(implementacao);
    }
    
    public Set<Classe> getImplementacoes() {
        return this.implementacoes;
    }

    public void setAtributos(Set<Variavel> atributos) {
        this.atributos = atributos;
    }

    public void setMetodos(Set<Metodo> metodos) {
        this.metodos = metodos;
    }

    public void removerAtributo(Variavel atributo) {
        this.atributos.remove(atributo);
    }
    
    public void removerMetodo(Metodo metodo) {
        this.metodos.remove(metodo);
    }

    public String getPacote() {
        return pacote;
    }

    public void setPacote(String pacote) {
        if(!pacote.isEmpty()) {
            String[] pNames = pacote.split("[\\s]*[\\.][\\s]*");
            for(String str: pNames) {
                if(Java.isPalavraReservada(str)) {
                    throw new IllegalArgumentException();
                }
            }
        }
        this.pacote = pacote.trim();
    }
    
    public boolean isDependenciaValida(Classe dependencia) {
        return !this.dependencias.contains(dependencia)
                && !this.containsAgregacao(dependencia)
                && !this.implementacoes.contains(dependencia)
                && this.heranca != dependencia;
    }
    
    public boolean isAgregacaoValida(Classe agregacao) {
        return !this.dependencias.contains(agregacao)
                && !this.containsAgregacao(agregacao)
                && !agregacao.containsAgregacao(this);
    }
    
    public boolean containsAgregacao(Classe agregacao) {
        for(AgregacaoVo a: this.agregacoes) {
            if(a.relacaoEqualsTo(this, agregacao)){
                return true;
            }
        }
        return false;
    }
    
    public boolean isHerancaValida(Classe heranca) {
        return this.heranca == null
                && !this.dependencias.contains(heranca);
    }
    
    public boolean isImplementacaoValida(Classe implementacao) {
        return !this.implementacoes.contains(implementacao)
                && !this.dependencias.contains(implementacao);
    }
    
    public void revalidaClasse() {
        this.revalidaNomeClasse();
        this.revalidaAtributos();
        this.revalidaMetodos();
    }
    
    public void revalidaNomeClasse() {
        this.setNome(this.nome);
    }
    
    public void revalidaAtributos() {
        for(Variavel attr: this.atributos) {
            attr.resetAssinatura();
        }
    }
    
    public void revalidaMetodos() {
        for(Metodo met: this.metodos) {
            met.resetAssinatura();
        }
    }

    public void desvincular(Classe classe) {
        this.dependencias.remove(classe);
        this.agregacoes.remove(classe);
        this.implementacoes.remove(classe);
        this.heranca = this.heranca == classe ? null : this.heranca;
    }
    
}
