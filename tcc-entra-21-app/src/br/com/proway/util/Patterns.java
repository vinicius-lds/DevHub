package br.com.proway.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Vinícius Luis da Silva
 */
public abstract class Patterns {
    
    public static boolean isValidAttributeName(String str) {
        return str.matches("[\\s]*([+]|[-]|[#])[\\s]*[a-zA-Z]{1}[\\w]*[\\s]*[:][\\s]*[a-zA-Z]{1}[\\w]*[\\s]*([\\.][\\s]*[a-zA-Z]{1}[\\w]*[\\s]*)*[\\s]*(\\[[\\s]*\\][\\s]*)??");
    }
    
    public static boolean isValidMethodSignature(String str) {
        String regex;
//        regex  = "[\\s]*([+]|[-]|[#])";
//        regex += "[\\s]*[a-zA-Z]{1}[\\w]*[\\s]*"; //nome do método
//        regex += "[(]{1}";//Tem abrir parenteses
//        regex += "(";//Pode ter paramentros
//        regex += "[\\s]*[a-zA-Z]{1}[\\w]*([.]{1}[a-zA-Z]{1}[\\w]*)*[\\s]*[:][\\s]*[a-zA-Z]{1}[\\w]*[\\s]*";//Especifica o primeiro parametro
//        regex += "([,]{1}[\\s]*[a-zA-Z]{1}[\\w]*([.]{1}[a-zA-Z]{1}[\\w]*)*[\\s]*[:][\\s]*[a-zA-Z]{1}[\\w]*[\\s]*)*";//Especifica os outros parametros
//        regex += ")??[\\s]*";//Pode ter paramentros
//        regex += "(\\[[\\s]*\\])??[\\s]*"; //Pose ser array
//        regex += "[)]{1}";//Tem que fechar parenteses
//        //regex += "([;]|[\\s]*[{]{1}[\\s]*[}]??[\\s]*)??";
//        regex += "[\\s]*([:][\\s]*[a-zA-Z][\\w]*[\\s]*([\\.][\\s]*[a-zA-Z][\\w]*[\\s]*)*(\\[[\\s]*\\][\\s]*)??)??";
         regex = 
                "[\\s]*([+]|[-]|[#])"
                + "[\\s]*[a-zA-Z][\\w]*"
                + "[\\s]*[(]"
                + "[\\s]*[a-zA-Z][\\w]*"
                + "[\\s]*[:]"
                + "[\\s]*[a-zA-Z][\\w]*"
                + "[\\s]*([\\.][\\s]*[a-zA-Z][\\w]*[\\s]*)*"
                + "(\\[[\\s]*\\])??"
                + "[\\s]*"
                + "([,]"
                + "[\\s]*[a-zA-Z][\\w]*"
                + "[\\s]*[:]"
                + "[\\s]*[a-zA-Z][\\w]*"
                + "[\\s]*([\\.][\\s]*[a-zA-Z][\\w]*[\\s]*)*"
                + "(\\[[\\s]*\\])??"
                + "[\\s]*"
                + ")*"
                + "[)]"
                + "[\\s]*("
                + "[:]"
                + "[\\s]*[a-zA-Z][\\w]*"
                + "[\\s]*([\\.][\\s]*[a-zA-Z][\\w]*[\\s]*)*"
                + "(\\[[\\s]*\\])??"
                + "[\\s]*"
                + ")??";
        return str.matches(regex);
    }
    
    public static boolean isValidPackageName(String str) {
        String regex;
        regex  = "(";
        regex += "[a-z-A-Z]{1}[\\w]*";
        regex += "([.]{1}[a-z-A-Z]{1}[\\w]*)*";
        regex += ")??";
        return str.matches(regex);
    }
    
    public static boolean isValidEmail(String str) {
        String regex;
        regex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b"
              + "\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@"
              + "(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]"
              + "|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*"
              + "[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b"
              + "\\x0c\\x0e-\\x7f])+)\\])";
        return str.matches(regex);
    }
    
    public static boolean isValidClassName(String str) {
        String regex;
        regex = "[\\s]*[a-zA-Z][\\w]*[\\s]*";
        return str.matches(regex);
    }
    
    public static boolean isValidMultiplicidade(String str) {
        String regex;
        regex = "([\\s]*[\\d]*[\\s]*)|([\\s]*[\\d]+[\\s]*[.][.][\\s]*([\\d]+|[*])[\\s]*)";
        return str.matches(regex);
    }
    
    public static boolean isValidEnum(String str) {
        return isValidVariableName(str);
    }
    
    public static boolean isValidVariableName(String str) {
        String regex;
        regex = "[\\s]*[a-zA-Z][\\w]*[\\s]*";
        return str.matches(regex);
    }
    
    public static String find(String regex, String value) {
        Matcher m = Pattern.compile(regex).matcher(value);
        if(!m.find()) {
            return null;
        }
        return m.group();
    }
    
    public static String[] findAll(String regex, String value) {
        Matcher m = Pattern.compile(regex).matcher(value);
        if(!m.find()) {
            return null;
        }
        String[] ret = new String[m.groupCount()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = m.group();
            
        }
        return ret;
    }
    
}
