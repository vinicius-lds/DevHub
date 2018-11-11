package br.com.proway.enumeradores;

/**
 * @author Vinícius Luis da Silva
 */
public enum Modificador {
    
    PUBLIC("Public", "public "), 
    PRIVATE("Private", "private "), 
    PROTECTED("Protected", "protected ");
    
    private final String REPRESENTATION, CODE;
    
    private Modificador(String representation, String code) {
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
