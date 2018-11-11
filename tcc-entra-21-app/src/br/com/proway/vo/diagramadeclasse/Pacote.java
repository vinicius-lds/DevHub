package br.com.proway.vo.diagramadeclasse;

import br.com.proway.util.Patterns;
import java.util.ArrayList;
import java.util.Objects;

/**
 * @author Vinícius Luis da Silva
 */
public class Pacote {

    private static final ArrayList<Pacote> pacotes = new ArrayList<>();

    private static void addPacote(Pacote p) {
        if (pacotes.contains(p)) {
            throw new IllegalArgumentException("O pacote " + p.getInfo() + " já existe!");
        }
        pacotes.add(p);
    }

    public static ArrayList<Pacote> getPacotes() {
        return pacotes;
    }

    private String info;

    public Pacote(String info) {
        this.setInfo(info);
        addPacote(this);
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        if (!Patterns.isValidPackageName(info)) {
            throw new IllegalArgumentException("O nome do pacote é Inválido!");
        }
        this.info = info;
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
        final Pacote other = (Pacote) obj;
        if (!Objects.equals(this.info, other.info)) {
            return false;
        }
        return true;
    }

}
