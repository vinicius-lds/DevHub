package br.com.proway.util;

import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Vinícius Luis da Silva
 */
public abstract class FileUtil {

    public static Set<String> getZipImports(File zipfile) throws IOException {
        Set<String> imps = new HashSet<>();
        ZipInputStream zin = new ZipInputStream(new FileInputStream(zipfile));
        ZipEntry entry;
        String name;
        while ((entry = zin.getNextEntry()) != null) {
            name = entry.getName().replaceAll("([/]|[\\\\])", ".");
            if (name.matches("([\\w]|[\\.])*[\\.][j][a][v][a]")) {
                imps.add(name.replaceAll("[\\.][j][a][v][a]", ""));
            }
        }
        zin.close();
        return imps;
    }

    public static Set<String> getJarImports(File jarFile) throws IOException {
        Set<String> imps = new HashSet();
        JarInputStream jar = new JarInputStream(new FileInputStream(jarFile));
        JarEntry entry;
        String name;
        while ((entry = jar.getNextJarEntry()) != null) {
            name = entry.getName().replaceAll("([/]|[\\\\])", ".");
            if (name.matches("([\\w]|[\\.])*[\\.][c][l][a][s][s]")) {
                imps.add(name.replaceAll("[\\.][c][l][a][s][s]", ""));
            }
        }
        jar.close();
        return imps;
    }

    public static Set<String> buscarJdk() throws IOException, ClassNotFoundException {
        return buscarJdk(null);
    }
    
    public static Set<String> buscarJdk(JFXTextField tf_jdk) throws IOException, ClassNotFoundException {
        Path p = Paths.get("data" + File.separatorChar + "java" + File.separatorChar + "jdk.dat");
        Set<String> imps = new HashSet();
        if (!Files.exists(p)) return imps;
        File file = p.toFile();
        ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
        String local = (String) ois.readObject();
        imps.addAll((Set<String>) ois.readObject());
        if (tf_jdk != null) {
            tf_jdk.setText(local);
        }
        ois.close();
        return imps;
    }
    
    public static HashMap<String, Set> buscarLibs() throws IOException, ClassNotFoundException {
        HashMap<String, Set> map = new HashMap();
        Path path = Paths.get("data" + File.separatorChar + "java" + File.separatorChar + "lib_imports");
        if(!Files.exists(path)) return map;
        
        ObjectInputStream ois;
        
        File lib_imports = path.toFile();
        File[] files = lib_imports.listFiles();
        
        for(int i = 0; i < files.length; i++) {
            if(Files.isDirectory(files[i].toPath())) continue;
            ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(files[i])));
            map.put(files[i].getName().replaceAll("[\\.][j][a][r]", ""), (Set<String>) ois.readObject());
            ois.close();
        } 
        
        return map;
    }

}
