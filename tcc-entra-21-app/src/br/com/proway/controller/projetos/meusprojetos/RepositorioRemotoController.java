package br.com.proway.controller.projetos.meusprojetos;

import br.com.proway.main.Main;
import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.Observable;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;

/**
 * FXML Controller class
 *
 * @author Vinícius Luis da Silva
 */
public class RepositorioRemotoController implements Initializable {

    @FXML private WebView wv_repo;
    @FXML private HBox hb_menu;
    @FXML private JFXButton btn_local;
    @FXML private JFXButton btn_voltar;
    @FXML private JFXButton btn_atualizar;

    
    private String url;
    private RepositorioController local;
    
        boolean aux = false;
    @Override
    public void initialize(URL u, ResourceBundle rb) {
        this.setUrl("https://github.com/vinicius-lds/jgit");
        this.btn_atualizar.onActionProperty().set(e -> {
            wv_repo.getEngine().reload();
        });
        this.btn_voltar.onActionProperty().set(e -> {
            ObservableList<WebHistory.Entry> ol = wv_repo.getEngine().getHistory().getEntries();
            if(ol.size() > 1) {
                wv_repo.getEngine().load(ol.get(ol.size() - 2).getUrl());
            }
        });
        this.btn_local.onActionProperty().set(e -> {
            Main.MAIN_CONTROLLER.setMainContent(local.ap_repo);
        });
    }
    
    public void setUrl(String url) {
        this.url = url;
        wv_repo.getEngine().load(url);
    }
    
    public void setLocal(RepositorioController rc) {
        this.local = rc;
    } 
    
}
