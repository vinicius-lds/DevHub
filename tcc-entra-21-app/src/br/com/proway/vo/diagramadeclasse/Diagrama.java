package br.com.proway.vo.diagramadeclasse;

import br.com.proway.vo.RepositorioGit;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Vinícius Luis da Silva
 */
public class Diagrama implements Serializable {

    private final String pastaTemp = "temp/repositorio_criacao_projeto/";
    protected HashMap<String, List<Classe>> pacotes;
    private List<Classe> semPacote;

    public Diagrama() {
        pacotes = new HashMap();
        semPacote = new ArrayList<>();
    }

    public void addClasse(Classe classe) {
        String pacote = classe.getPacote();
        if (pacote.isEmpty()) {
            semPacote.add(classe);
        } else {
            List<Classe> classes = pacotes.get(pacote);
            if (classes == null) {
                classes = new ArrayList<>();
            }
            for (Classe c : classes) {
                if (c == classe) {
                    return;
                }
            }
            classes.add(classe);
            pacotes.put(pacote, classes);
        }
        this.trimm();
    }

    private void trimm() {
        List<Classe> classes;
        List<String> toRemove = new ArrayList();
        for (String pacote : pacotes.keySet()) {
            classes = pacotes.get(pacote);
            if (classes == null || classes.isEmpty()) {
                toRemove.add(pacote);
                //pacotes.remove(pacote);
            }
        }
        for (String pacote : toRemove) {
            pacotes.remove(pacote);
        }
    }

    public void removeClasse(Classe classe) {
        List<Classe> classes;
        if (classe.getPacote().isEmpty()) {
            for (Classe c : semPacote) {
                if (c == classe) {
                    semPacote.remove(c);
                    return;
                }
            }
        } else {
            for (String pacote : pacotes.keySet()) {
                classes = pacotes.get(pacote);
                if (classes == null || classes.isEmpty()) {
                    pacotes.remove(pacote);
                } else {
                    for (Classe c : classes) {
                        if (c == classe) {
                            classes.remove(c);
                            return;
                        }
                    }
                }
            }
        }
    }

    public void generateCode(File repositorio) {
        Set<String> pacotes = this.pacotes.keySet();
        List<Classe> classes;
        Set<String> libs = new TreeSet();
        for (Classe classe : semPacote) {
            if (!classe.isValid()) {
                throw new IllegalArgumentException("Não é possível gerar código enquanto houver algo inválido em uma das classes do diagrama!");
            }
            libs.addAll(classe.getLibraries());
            try {
                createJavaFile(repositorio, classe.getPacote(), classe.getNome(), classe.getCode());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (String pacote : pacotes) {
            classes = this.pacotes.get(pacote);
            for (Classe classe : classes) {
                if (!classe.isValid()) {
                    throw new IllegalArgumentException("Não é possível gerar código enquanto houver algo inválido em uma das classes do diagrama!");
                }
                libs.addAll(classe.getLibraries());
                try {
                    createJavaFile(repositorio, classe.getPacote(), classe.getNome(), classe.getCode());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        moveLibsTo(libs, repositorio);
    }

    public void createJavaFile(File repositorio, String local, String nome, String content) throws IOException {
        if (!repositorio.exists()) {
            Files.createDirectory(repositorio.toPath());
        }
        String[] dirs = local.split("\\.");
        String d = repositorio + "/src/";
        if (!new File(d).exists()) {
            Files.createDirectory(new File(d).toPath());
        }
        for (String dir : dirs) {
            d += dir + "/";
            repositorio = new File(d);
            if (!repositorio.exists()) {
                Files.createDirectory(repositorio.toPath());
            }
        }
        repositorio = new File(d + nome + ".java");
        Writer w = new BufferedWriter(new FileWriter(repositorio));
        w.write(content);
        w.close();
    }

    private void moveLibsTo(Set<String> libs, File repositorio) {
        final String local = "data/java/lib/";
        repositorio = new File(repositorio.toString() + "/lib/");
        if (!Files.exists(repositorio.toPath().toAbsolutePath())) {
            try {
                Files.createDirectory(repositorio.toPath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        File library;
        for (String lib : libs) {
            lib = lib.substring(0, lib.length() - 4) + ".jar";
            library = new File(local + lib);
            try {
                Files.copy(library.getAbsoluteFile().toPath(), Paths.get(repositorio.getAbsoluteFile().toPath().toString() + File.separatorChar + lib));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
