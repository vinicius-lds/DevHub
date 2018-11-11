package br.com.proway.enumeradores;

import java.io.Serializable;

/**
 * @author Vinícius Luis da Silva
 */
public enum TipoClasse implements Serializable{
    
    ABSTRACT("Classe Abstrata", "abstract class "), 
    CONCRETE("Classe Concreta", "class "), 
    INTERFACE("Interface", "interface "), 
    ENUM("Enumerador", "enum ");
    
    private final String REPRESENTATION, CODE;
    
    private TipoClasse(String representation, String code) {
        this.REPRESENTATION = representation;
        this.CODE = code;
    }

    @Override
    public String toString() {
        return this.REPRESENTATION;
    }
    
    public String getCode() {
        return this.CODE;
    }
    
}
