package br.com.proway.bean.login;

import br.interfaces.Bean;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author Vinícius Luis da Silva
 */
public class Projeto implements Bean {

    private int id;
    private String nomeProjeto;
    private InputStream imagem;
    private boolean ativo;
    private String repositorioGit;
    
    private String userGit;
    private String passGit;
    
    @Override
    public void initialize(Object[] os) {
        this.id = (int) os[0];
        this.nomeProjeto = (String)os[1];
        
        try {
            System.out.println(os[2]);
            this.imagem = (ByteArrayInputStream)os[2];
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.ativo = (Boolean) os[3];
        this.repositorioGit = (String)os[4];
    }

    public int getId() {
        return id;
    }

    public String getNomeProjeto() {
        return nomeProjeto;
    }

    public void setNomeProjeto(String nomeProjeto) {
        this.nomeProjeto = nomeProjeto;
    }

    public InputStream getImagem() {
        return imagem;
    }

    public void setImagem(InputStream imagem) {
        this.imagem = imagem;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public String getRepositorioGit() {
        return repositorioGit;
    }

    public void setRepositorioGit(String repositorioGit) {
        this.repositorioGit = repositorioGit;
    }

    public String getUserGit() {
        return userGit;
    }

    public void setUserGit(String userGit) {
        this.userGit = userGit;
    }

    public String getPassGit() {
        return passGit;
    }

    public void setPassGit(String passGit) {
        this.passGit = passGit;
    }
    
}
