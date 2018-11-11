package br.com.proway.util;

/**
 * @author Vinicius Luis da Silva
 */
public abstract class Java {
    
    public static final boolean isPalavraReservada(String palavra) {
        return palavra.equals("void")
                || palavra.equals("boolean")
                || palavra.equals("byte")
                || palavra.equals("short")
                || palavra.equals("char")
                || palavra.equals("int")
                || palavra.equals("long")
                || palavra.equals("float")
                || palavra.equals("double")
                || palavra.equals("public")
                || palavra.equals("private")
                || palavra.equals("protected")
                || palavra.equals("abstract")
                || palavra.equals("final")
                || palavra.equals("return")
                || palavra.equals("package")
                || palavra.equals("extends")
                || palavra.equals("implements")
                || palavra.equals("interface")
                || palavra.equals("enum")
                || palavra.equals("native")
                || palavra.equals("new")
                || palavra.equals("strictfp")
                || palavra.equals("transient")
                || palavra.equals("volatile")
                || palavra.equals("break")
                || palavra.equals("case")
                || palavra.equals("default")
                || palavra.equals("do")
                || palavra.equals("else")
                || palavra.equals("for")
                || palavra.equals("if")
                || palavra.equals("instanceof")
                || palavra.equals("switch")
                || palavra.equals("while")
                || palavra.equals("assert")
                || palavra.equals("catch")
                || palavra.equals("finally")
                || palavra.equals("throw")
                || palavra.equals("throws")
                || palavra.equals("try")
                || palavra.equals("import")
                || palavra.equals("super")
                || palavra.equals("this")
                || palavra.equals("const")
                || palavra.equals("goto")
                || palavra.equals("null")
                || palavra.equals("true")
                || palavra.equals("false");
    } 
   
    public static final boolean isTipoPrimitivo(String palavra) {
        return palavra.equals("boolean")
                || palavra.equals("int")
                || palavra.equals("double")
                || palavra.equals("float")
                || palavra.equals("char")
                || palavra.equals("byte")
                || palavra.equals("short")
                || palavra.equals("long");
    }
    
}
