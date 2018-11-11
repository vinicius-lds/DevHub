package br.com.proway.vo.diagramadeclasse;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vinícius Luis da Silva
 */
public class AgregacaoVo {
    
    private final String splitRegex = "[\\.][\\.]";
    private Classe classeParte;
    private Classe classeTodo;
    private String parte;
    private String todo;
    private List<Variavel> varParte;
    private List<Variavel> varTodo;

    public AgregacaoVo(Classe parte, Classe todo) {
        this.classeParte = parte;
        this.classeTodo = todo;
        this.todo = new String();
        this.parte = new String();
        this.varParte = new ArrayList<>();
        this.varTodo = new ArrayList<>();
    }
    
    private void setMultiplicidade(String parte, String todo) {
        this.parte = parte.trim();
        this.todo = todo.trim();
    }
    
    public void setMultiplicidadeTodo(String todo) {
        int min = getMinMult(todo);
        int max = getMaxMult(todo);
        if(min > max && max != -1) {
            throw new IllegalArgumentException("Todo: " + todo);
        }
        this.setMultiplicidade(parte, todo);
        if(max >= 0) {
            if(max < this.varTodo.size()) {
                for(int i = this.varTodo.size() - 1; i >= max; i--) {
                    this.varTodo.remove(i);
                }
            }
        }
    }
    
    public void setMultiplicidadeParte(String parte) {
        int min = getMinMult(parte);
        int max = getMaxMult(parte);
        if(min > max && max != -1) {
            throw new IllegalArgumentException("Parte: " + parte);
        }
        this.setMultiplicidade(parte, todo);
    }
    
    public void addVariavelParte() {
        int max = this.getMaxMult(parte);
        if(max <= this.varParte.size() && max != -1) return;
        this.varParte.add(new Variavel(classeParte));
    }
    
    public void addVariavelTodo() {
        int max = this.getMaxMult(todo);
        if(max <= this.varTodo.size() && max != -1) return;
        this.varTodo.add(new Variavel(classeTodo));
    }
    
    public boolean isPossivelVariavelParte() {
        int aux = getMaxMult(parte);
        if(aux < 0) {
            return true;
        } else {
            return varParte.size() >= aux;
        }
    }
    
    public boolean isPossivelVariavelTodo() {
        int aux = getMaxMult(todo);
        if(aux < 0) {
            return true;
        } else {
            return varTodo.size() >= aux;
        }
    }
    
    public List<Variavel> getVariaveisParte() {
        int min = this.getMinMult(parte);
        int max = this.getMaxMult(parte);
        if(min > this.varParte.size()) {
            for (int i = this.varParte.size(); i < min; i++) {
                this.varParte.add(new Variavel(classeParte));
            }
        }
        if(max >= 0) {
            if(max < this.varParte.size()) {
                for(int i = this.varParte.size() - 1; i >= max; i--) {
                    this.varParte.remove(i);
                }
            }
        }
        return this.varParte;
    }
    
    public List<Variavel> getVariaveisTodo() {
        int min = this.getMinMult(todo);
        int max = this.getMaxMult(todo);
        if(min > this.varTodo.size()) {
            for (int i = this.varTodo.size(); i < min; i++) {
                this.varTodo.add(new Variavel(classeTodo));
            }
        }
        if(max >= 0) {
            if(max < this.varTodo.size()) {
                for(int i = this.varTodo.size() - 1; i >= max; i--) {
                    this.varTodo.remove(i);
                }
            }
        }
        return this.varTodo;
    }
    
    private int getMinMult(String mult) {
        if(mult.isEmpty()) return 0;
        try {
            return Integer.parseInt(mult);
        } catch (Exception e) {}
        return Integer.parseInt(mult.split(splitRegex)[0]);
    }
    
    private int getMaxMult(String mult) {
        if(mult.isEmpty()) return 0;
        try {
            return Integer.parseInt(mult);
        } catch (Exception e) {}
        try {
            return Integer.parseInt(mult.split(splitRegex)[1]);
        } catch (Exception e) {
            return -1;
        }
    }

    public String getMultiplicidadeParte() {
        return parte;
    }

    public String getMultiplicidadeTodo() {
        return todo;
    }
    
    public void excluirVariavelParte(Variavel v) {
        if(this.varParte.size() > this.getMinMult(parte)) {
            this.varParte.remove(v);
        }
    }
    
    public void excluirVariavelTodo(Variavel v) {
        if(this.varTodo.size() > this.getMinMult(todo)) {
            this.varTodo.remove(v);
        }
    }

    public Classe getClasseParte() {
        return classeParte;
    }

    public Classe getClasseTodo() {
        return classeTodo;
    }
    
    public String getOtherImport(Classe notThisOne) {
        String pacote;
        if(this.classeParte != notThisOne) {
            pacote = this.classeParte.getPacote();
            return pacote.isEmpty() ? this.classeParte.getNome() 
                    : 
                   pacote + "." + this.classeParte.getNome();
        } else {
            pacote = this.classeTodo.getPacote();
            return pacote.isEmpty() ? this.classeTodo.getNome() 
                    : 
                   pacote + "." + this.classeTodo.getNome();
        }
    }

    public List<Variavel> getVariaveis(Classe thisClass) {
        if(thisClass == classeParte) {
            return this.getVariaveisParte();
        } else {
            return this.getVariaveisTodo();
        }
    }

    public boolean relacaoEqualsTo(Classe c1, Classe c2) {
        return (c1 == classeTodo && c2 == classeParte) 
                || (c1 == classeParte && c2 == classeTodo);
    }

    public Classe getOtherClass(Classe classe) {
        if(classeParte == classe) {
            return classeTodo;
        } else {
            return classeParte;
        }
    }
    
}
