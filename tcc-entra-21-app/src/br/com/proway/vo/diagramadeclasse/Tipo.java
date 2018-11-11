package br.com.proway.vo.diagramadeclasse;

import java.util.ArrayList;
import java.util.Objects;

/**
 * @author Vinícius Luis da Silva
 */
public class Tipo implements Comparable<Tipo>{
    
    private static ArrayList<Tipo> tipos = new ArrayList<>();
    
    public static Tipo getTipo(String tipo) {
        Tipo retorno = null;
        for(Tipo t: tipos) {
            if(t.getTipo().equals(tipo)) {
                if(retorno != null) {
                    throw new IllegalArgumentException("Existem dois tipos repesentados pela mesma String."
                            + " Você deve propcurá-lo pelo import");
                }
                retorno = t;
            }
        }
        return retorno;
    }
    
    public static ArrayList<Tipo> getTipos() {
        return tipos;
    }
    
    private String imp;
    private String tipo;

    public Tipo(String tipo) {
        if(tipos.contains(tipo)) {
            return;
        }
        this.setTipo(tipo);
        this.setImp(tipo);
        tipos.add(this);
    }
    
    public Tipo(String tipo, String imp) {
        if(tipos.contains(tipo)) {
            return;
        }
        this.setImp(imp);
        this.setTipo(tipo);
        tipos.add(this);
    }
    
    public String getImport() {
        return imp;
    }
    
    public String getTipo() {
        return tipo;
    }
    
    public void setImp(String imp) {
        if(imp.isEmpty()) {
            throw new IllegalArgumentException("O import não pode ser vazio!");
        }
        this.imp = imp;
    }
    
    public void setTipo(String tipo) {
        if(tipo.isEmpty()) {
            throw new IllegalArgumentException("O tipo da variavel não pode ser vazio!");
        }
        this.tipo = tipo;
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
        final Tipo other = (Tipo) obj;
        if (!Objects.equals(this.imp, other.imp)) {
            return false;
        }
        if (!Objects.equals(this.tipo, other.tipo)) {
            return false;
        }
        return true;
    }
    
    @Override
    public int compareTo(Tipo o) {
        return this.tipo.compareTo(o.getImport());
    }

    @Override
    public String toString() {
        return this.tipo;
    }
    
}
