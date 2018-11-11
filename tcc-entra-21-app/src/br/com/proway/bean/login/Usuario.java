
package br.com.proway.bean.login;

import br.com.proway.main.Main;
import br.com.proway.util.Patterns;
import br.interfaces.Bean;
import br.vo.Condicao;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Usuario implements Bean {
    
    private int id;
    private String nome;
    private InputStream imagem;
    private File imgFile;
    private String usuario;
    private String email;
    private String senha;
    private String pergunta1;
    private String pergunta2;
    private String resposta1;
    private String resposta2;
    private Boolean primeiroLogin;
    private Integer nivelDePermissao;
    
    public void atualizarBanco() throws IllegalArgumentException, SQLException {
        if(this.id > 0) {
            String[] colunas = new String[] {"usuario", "nome", "senha", "pergunta1", "pergunta2", "resposta1", "resposta2", "primeiro_login", "email"};
            Object[] valores = new Object[] {this.usuario, this.nome, this.senha, this.pergunta1, this.pergunta2, this.resposta1, this.resposta2, "0", email};
            Condicao condicao = new Condicao().where().equals("id", this.id);
            Main.DB_CONNECTION.update("usuarios", colunas, valores, condicao);
            
            try {
                String sql = "UPDATE usuarios SET imagem = ?";
                PreparedStatement pstmt = Main.DB_CONNECTION.get().prepareCall(sql);
                pstmt.setBinaryStream(1, new FileInputStream(imgFile), imgFile.length());
                pstmt.execute();
            } catch (Exception e) {
                System.out.println("Exception no cadastro de imagem");
                e.printStackTrace();
            }
            return;
        }
        throw new IllegalArgumentException("Usuário não inicializado!");
    }
    
    @Override
    public void initialize(Object[] os) {
        this.id = (int)os[0];
        this.usuario = (String) os[1];
        this.email = (String) os[2];
        this.nome = (String) os[3];
        try {
            this.imagem = new ByteArrayInputStream((byte[])os[4]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.senha = (String) os[5];
        this.pergunta1 = (String) os[6];
        this.resposta1 = (String) os[7];
        this.pergunta2 = (String) os[8];
        this.resposta2 = (String) os[9];
        this.primeiroLogin = (Boolean)os[10];
        this.nivelDePermissao = (Integer)os[11];
    }

    public File getImgFile() {
        return imgFile;
    }

    public void setImgFile(File imgFile) {
        try {
            this.imagem = new FileInputStream(imgFile);
        } catch (Exception e) {
            System.out.println("iiii");
            e.printStackTrace();
        }
        this.imgFile = imgFile;
    }
    
    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getSenha() {
        return senha;
    }

    public String getPergunta1() {
        return pergunta1;
    }

    public String getPergunta2() {
        return pergunta2;
    }

    public String getResposta1() {
        return resposta1;
    }

    public String getResposta2() {
        return resposta2;
    }

    public boolean isPrimeiroLogin() {
        return primeiroLogin;
    }

    public void setNome(String nome) {
        if(nome == null || nome.isEmpty()) {
            throw new IllegalArgumentException("Nome Inválido!");
        }
        this.nome = nome;
    }

    public void setUsuario(String usuario) {
        if(usuario == null || usuario.isEmpty()) {
            throw new IllegalArgumentException("Nome de Usuário Inválido!");
        }
        this.usuario = usuario;
    }

    public void setSenha(String senha) {
        if(senha == null || senha.isEmpty()) {
            throw new IllegalArgumentException("Senha Inválida");
        }
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(senha.getBytes(), 0, senha.length());
            senha = new BigInteger(1, m.digest()).toString(16);
            this.senha = senha;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPergunta1(String pergunta1) {
        if(pergunta1 == null || pergunta1.isEmpty()) {
            throw new IllegalArgumentException("Pergunta Inválida");
        }
        this.pergunta1 = pergunta1;
    }

    public void setPergunta2(String pergunta2) {
        if(pergunta2 == null || pergunta2.isEmpty()) {
            throw new IllegalArgumentException("Pergunta Inválida");
        }
        this.pergunta2 = pergunta2;
    }

    public void setResposta1(String resposta1) {
        if(resposta1 == null || resposta1.isEmpty()) {
            throw new IllegalArgumentException("Resposta Inválida");
        }
        this.resposta1 = resposta1;
    }

    public void setResposta2(String resposta2) {
        if(resposta2 == null || resposta2.isEmpty()) {
            throw new IllegalArgumentException("Resposta Inválida");
        }
        this.resposta2 = resposta2;
    }

    public void setPrimeiroLogin(boolean primeiroLogin) {
        this.primeiroLogin = primeiroLogin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if(email == null || !Patterns.isValidEmail(email)) {
            throw new IllegalArgumentException("O e-mail é inválido!");
        }
        this.email = email;
    }

    public InputStream getImagem() {
        return imagem;
    }

    public void setImagem(InputStream imagem) {
        this.imagem = imagem;
    }

    public Integer getNivelDePermissao() {
        return nivelDePermissao;
    }

    public void setNivelDePermissao(Integer nivelDePermissao) {
        this.nivelDePermissao = nivelDePermissao;
    }
    
}
