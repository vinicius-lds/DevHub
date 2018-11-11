package br.com.proway.controller.projetos.diagramadeclasse;

import br.com.proway.util.FXMLFile;
import br.com.proway.view.comp.JFXDocButton;
import br.com.proway.vo.diagramadeclasse.Metodo;
import br.com.proway.vo.diagramadeclasse.Tipo;
import br.com.proway.vo.diagramadeclasse.Variavel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * FXML Controller class
 *
 * @author Vinícius Luis da Silva
 */
public class DocumentacaoController {

    private Stage stage;
    private JFXDocButton docBtn;
    private Metodo metodo;
    private Variavel atributo;
    @FXML private TextArea ta_documentacao;

    public DocumentacaoController(Metodo met) {
        this.metodo = met;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLFile.DOCUMENTACAO));
        loader.setController(this);
        try {
            stage = new Stage();
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setAlwaysOnTop(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.stage.initStyle(StageStyle.UTILITY);
        this.stage.onCloseRequestProperty().set((e) -> this.metodo.setDocumentacao(this.ta_documentacao.getText()));
        stage.show();
        this.stage.setTitle(metodo.getNome());
        if(metodo.getDocumentacao().isEmpty()) {
            this.ta_documentacao.setText(metodo.getDefaultDoc());
        } else {
            this.ta_documentacao.setText(metodo.getDocumentacao());
        }
    }
    
    public DocumentacaoController(Variavel atributo) {
        this.atributo = atributo;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLFile.DOCUMENTACAO));
        loader.setController(this);
        try {
            stage = new Stage();
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.stage.initStyle(StageStyle.UTILITY);
        this.stage.onCloseRequestProperty().set((e) -> this.atributo.setDocumentacao(this.ta_documentacao.getText()));
        stage.show();
        this.stage.setTitle(atributo.getNome());
        this.ta_documentacao.setText(atributo.getDocumentacao());
    }
    
}
