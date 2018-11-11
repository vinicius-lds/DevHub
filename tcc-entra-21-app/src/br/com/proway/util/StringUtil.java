package br.com.proway.util;

/**
 * @author Vinícius Luis da Silva
 */
public abstract class StringUtil {

    public static final boolean isPacoteValido(String pacote) {
        String aux = "";
        for (int i = 0; i < pacote.length(); i++) {
            if (pacote.charAt(i) == '.') {
                if (Java.isPalavraReservada(aux)) {
                    return false;
                }
                continue;
            }
            if (!Character.isLetter(pacote.charAt(i))) {
                if(pacote.charAt(i) == '_') {
                    continue;
                }
                if (Character.isDigit(pacote.charAt(i))) {
                    if (aux.length() == 0) {
                        return false;
                    }
                    continue;
                }
                return false;
            }
        }
        return true;
    }

}
