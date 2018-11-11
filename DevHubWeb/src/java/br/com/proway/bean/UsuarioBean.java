package br.com.proway.bean;

public class UsuarioBean {
    private int id, nivelDePermissao;
    private String usuario,senha,pergunta1,resposta1,pergunta2,resposta2,email, nome, imagem;
    private boolean primeiroLogin, ativo;
    
    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }
    public String getUsuario(){
        return usuario;
    }
    public void setUsuario(String usuario){
        this.usuario = usuario;
    }
    public String getSenha(){
        return senha;
    }
    public void setSenha(String senha){
        this.senha = senha;
    }
    public String getPergunta1(){
        return pergunta1;
    }
    public void setPergunta1(String pergunta1){
        this.pergunta1 = pergunta1;
    }
    public String getResposta1(){
        return resposta1;
    }
    public void setResposta1(String resposta1){
        this.resposta1 = resposta1;
    }
    public String getPergunta2(){
        return pergunta2;
    }
    public void setPergunta2(String pergunta2){
        this.pergunta2 = pergunta2;
    }
    public String getResposta2(){
        return resposta2;
    }
    public void setResposta2(String resposta2){
        this.resposta2 = resposta2;
    }
    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public boolean isPrimeiroLogin(){
        return primeiroLogin;
    }
    public void setPrimeiroLogin(boolean primeiro_login){
        this.primeiroLogin = primeiro_login;
    }
    public String getNome(){
        return nome;
    }
    public void setNome(String nome){
        this.nome = nome;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public int getNivelDePermissao() {
        return nivelDePermissao;
    }

    public void setNivelDePermissao(int nivelDePermissao) {
        this.nivelDePermissao = nivelDePermissao;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return "idUsuario: " + this.id; 
    }
    
}
