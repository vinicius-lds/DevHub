package br.com.proway.bean;

/**
 * @author Vinícius Luis da Silva
 */
public class Projeto {
    
    private int id, mensagensNaoLidas, idUltimoAutor;
    private String nome, ultimaMensagem, ultimoAutor, imagem;

    public boolean hasUltimaMensagem() {
        return !(this.ultimoAutor == null || ultimaMensagem == null);
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMensagensNaoLidas() {
        return mensagensNaoLidas;
    }

    public void setMensagensNaoLidas(int mensagensNaoLidas) {
        this.mensagensNaoLidas = mensagensNaoLidas;
    }

    public int getIdUltimoAutor() {
        return idUltimoAutor;
    }

    public void setIdUltimoAutor(int idUltimoAutor) {
        this.idUltimoAutor = idUltimoAutor;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUltimaMensagem() {
        return ultimaMensagem == null ? "" : ultimaMensagem;
    }

    public void setUltimaMensagem(String ultimaMensagem) {
        this.ultimaMensagem = ultimaMensagem;
    }

    public String getUltimoAutor() {
        return ultimoAutor;
    }

    public void setUltimoAutor(String ultimoAutor) {
        this.ultimoAutor = ultimoAutor;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public String getImagem() {
        return imagem;
    }
    
}
