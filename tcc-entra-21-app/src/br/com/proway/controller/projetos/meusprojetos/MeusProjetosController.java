package br.com.proway.controller.projetos.meusprojetos;

import br.com.proway.bean.login.Projeto;
import br.com.proway.bean.login.Usuario;
import br.com.proway.controller.main.MainController;
import br.com.proway.controller.projetos.diagramadeclasse.CadastroDeProjetoController;
import br.com.proway.dao.ProjetoDAO;
import br.com.proway.main.Main;
import br.com.proway.util.FXMLFile;
import br.vo.Condicao;
import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class MeusProjetosController implements Initializable {

    @FXML protected ScrollPane sp_projetos;
    @FXML private VBox vb_projetos;
    @FXML private JFXButton btn_novoProjeto;
    @FXML private AnchorPane ap_principal;
    
    private MainController root;
    protected ProjetoController projetoSelecionado;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            ProjetoController projeto;
            for (Projeto p : ProjetoDAO.getProjetos()) {
                projeto = new ProjetoController();
                projeto.setProjeto(p);
                vb_projetos.getChildren().add(projeto.ap_projeto);
                AnchorPane.setRightAnchor(projeto.ap_projeto, 0.0);
                AnchorPane.setLeftAnchor(projeto.ap_projeto, 0.0);
                projeto.setRoot(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void select(ProjetoController pc) {
        if(this.projetoSelecionado != null) {
            this.projetoSelecionado.ap_projeto.setStyle("");
            this.projetoSelecionado.lbl_projeto.setStyle("");
        }
        this.projetoSelecionado = pc;
        this.projetoSelecionado.ap_projeto.setStyle("-fx-background-color: blue;");
        this.projetoSelecionado.lbl_projeto.setStyle("-fx-text-fill: white;");
    }

    public MainController getRoot() {
        return root;
    }

    public void setRoot(MainController root) {
        this.root = root;
    }
    
}
