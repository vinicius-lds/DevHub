package br.com.proway.controller.projetos.diagramadeclasse.relacoes;

import br.com.proway.enumeradores.Modificador;
import br.com.proway.enumeradores.Relacao;
import br.com.proway.enumeradores.TipoClasse;
import br.com.proway.util.FXMLFile;
import br.com.proway.util.FXUtil;
import br.com.proway.util.GridPaneUtil;
import br.com.proway.util.Patterns;
import br.com.proway.vo.diagramadeclasse.AgregacaoVo;
import br.com.proway.vo.diagramadeclasse.Variavel;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

/**
 * FXML Controller class
 *
 * @author Vinícius Luis da Silva
 */
public class EdicaoDeRelacaoController implements Initializable {

    @FXML public AnchorPane ap_principal;
    @FXML private ToggleGroup classe;
    @FXML private JFXButton add_variavel;
    @FXML private TextField tf_mulTodo;
    @FXML private TextField tf_mulParte;
    @FXML private GridPane gp_variaveis;
    @FXML private GridPane gp_colecoes;
    @FXML private JFXRadioButton rb_parte;
    @FXML private JFXRadioButton rb_todo;
    @FXML private JFXButton btn_exluir;
    private AgregacaoVo agregacao;
    
    private RelacaoController relacaoController;

    public EdicaoDeRelacaoController(RelacaoController rc) {
        this.relacaoController = rc;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLFile.EDICAO_DE_RELACAO));
        loader.setController(this);
        try {
            loader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.btn_exluir.onActionProperty().set(e -> excluirRelacao());
        Relacao relacao = relacaoController.getStrategy().getTipo();
        if(relacao != Relacao.AGREGACAO/* || relacao == Relacao.COMPOSICAO*/) {
            rb_parte.setDisable(true);
            rb_todo.setDisable(true);
        }
    }
    
    private void excluirRelacao() {
        this.relacaoController.excluir();
    }
    
    public void setAgregacao(AgregacaoVo agregacao) {
        if(agregacao == null) return;
        this.agregacao = agregacao;
        if(this.agregacao.getClasseTodo().getTipo() != TipoClasse.ENUM) {
            this.rb_todo.setSelected(true);
            inicializarMulTodoListener();
            inicializarMulParteListener();
        } else {
            this.rb_todo.setDisable(true);
            this.rb_parte.setSelected(true);
            inicializarMulParteListener();
            
        }
        this.rb_parte.onActionProperty().set(e -> updateCampos());
        this.rb_todo.onActionProperty().set(e -> updateCampos());
        this.add_variavel.onActionProperty().set(e -> {
            if(rb_parte.isSelected()) {
                this.agregacao.addVariavelParte();
            } else {
                this.agregacao.addVariavelTodo();
            }
            this.updateCampos();
        });
        this.updateCampos();
    }
    
    private void updateCampos() {
        this.gp_variaveis.getChildren().clear();
        if(this.rb_parte.isSelected()) {
            //Carregar variaveis da agregacao na classe parte
            this.tf_mulTodo.setDisable(true);
            this.tf_mulParte.setDisable(false);
            int count = 0;
            for(Variavel v: this.agregacao.getVariaveisParte()) {
                this.addField(v, "variavel" + count);
                count++;
            }
        } else {
            //Carregar variaveis da agregacao na classe todo
            this.tf_mulTodo.setDisable(false);
            this.tf_mulParte.setDisable(true);
            int count = 0;
            for(Variavel v: this.agregacao.getVariaveisTodo()) {
                this.addField(v, "variavel" + count);
                count++;
            }
        }
    }

    private ArrayList<String> getColecoesDisponiveis() {
        ArrayList<String> arr = new ArrayList();
        arr.add("ArrayList");
        arr.add("LinkedList");
        arr.add("HashSet");
        arr.add("LinkedHashSet");
        arr.add("TreeSet");
//        arr.add("HashMap");
//        arr.add("TreeMap");
        return arr;
    }

    private void inicializarMulTodoListener() {
        tf_mulParte.setDisable(true);
        tf_mulParte.setText(agregacao.getMultiplicidadeParte());
        tf_mulTodo.textProperty().addListener((obs, ov, nv) -> {
            if(!Patterns.isValidMultiplicidade(nv)) {
                FXUtil.paintInvalid(this.tf_mulTodo);
            } else {
                FXUtil.paintValid(this.tf_mulTodo);
                try {
                    this.agregacao.setMultiplicidadeTodo(nv);
                    relacaoController.getStrategy().setMultiplicidadeParte(this.agregacao.getMultiplicidadeTodo());
                } catch (Exception e) {
                    FXUtil.paintInvalid(this.tf_mulTodo);
                }
            }
        });
        tf_mulTodo.focusedProperty().addListener((obs, ov, nv) -> {
            if(!nv) {
                this.updateCampos();
            }
        });
    }

    private void inicializarMulParteListener() {
        tf_mulTodo.setDisable(true);
        tf_mulTodo.setText(agregacao.getMultiplicidadeTodo());
        tf_mulParte.textProperty().addListener((obs, ov, nv) -> {
            if(!Patterns.isValidMultiplicidade(nv)) {
                FXUtil.paintInvalid(this.tf_mulParte);
            } else {
                FXUtil.paintValid(this.tf_mulParte);
                try {
                    this.agregacao.setMultiplicidadeParte(nv);
                    relacaoController.getStrategy().setMultiplicidadeTodo(this.agregacao.getMultiplicidadeParte());
                } catch (Exception e) {
                    FXUtil.paintInvalid(this.tf_mulParte);
                }
            }
        });
        tf_mulParte.focusedProperty().addListener((obs, ov, nv) -> {
            if(!nv) {
                this.updateCampos();
            }
        });
    }

    private void addField(Variavel v, String defaultName) {
        TextField tf = new TextField();
        JFXButton btn = new JFXButton("..");
        tf.setPromptText("Nome da Varíavel");
        tf.textProperty().addListener((obs, ov, nv) -> {
            if(!Patterns.isValidVariableName(nv)) {
                FXUtil.paintInvalid(tf);
                v.setValid(false);
            } else {
                try {
                    v.setNome(nv);
                    FXUtil.paintValid(tf);
                } catch (Exception e) {
                    FXUtil.paintInvalid(tf);
                }
            }
        });
        tf.focusedProperty().addListener((obs, ov, nv) -> {
            if(!nv) {
                if(!v.isValid()) {
                    tf.setText(defaultName);
                }
            }
        });
        ContextMenu cm = new ContextMenu();
        MenuItem excluir = new MenuItem("Excluir");
        excluir.onActionProperty().set(e -> {
            if(this.rb_parte.isSelected()) {
                this.agregacao.excluirVariavelParte(v);
            } else {
                this.agregacao.excluirVariavelTodo(v);
            }
            this.updateCampos();
        });
        
        cm.getItems().addAll(excluir, this.getMenuItems(v));
        btn.onMouseClickedProperty().set(e -> {
            cm.show(btn, e.getScreenX(), e.getScreenY());
        });
        tf.setText(v.isValid() ? v.getNome() : defaultName);
        this.gp_variaveis.addRow(GridPaneUtil.getRowCount(gp_variaveis), tf, btn);
    }

    private Menu getMenuItems(Variavel v) {
        Menu tipoDaVariavel = new Menu("Tipo da Variavel");
        String varImport = this.agregacao.getOtherImport(v.getClasse());
        String varTipo = varImport.contains(".") ? varImport.substring(varImport.lastIndexOf(".") + 1, varImport.length()) : varImport;
        ToggleGroup grupo = new ToggleGroup();
        RadioMenuItem varItem = new RadioMenuItem(varTipo);
        v.getClasse().addImport(varTipo);
        varItem.onActionProperty().set(e -> {
            if(varItem.isSelected()) {
                v.setTipo(varTipo, varImport);
            }
        });
        tipoDaVariavel.getItems().add(varItem);
        varItem.setToggleGroup(grupo);
        List<String> tipoDeColecoes = this.getColecoesDisponiveis();
        
        for (String tipoDeColecao : tipoDeColecoes) {
            String aux = tipoDeColecao + "<" + varTipo + ">";
            RadioMenuItem varColecao = new RadioMenuItem(aux);
            varColecao.setToggleGroup(grupo);
            varColecao.onActionProperty().set(e -> {
                if(varColecao.isSelected()) {
                    v.setTipo(aux , "java.util." + tipoDeColecao);
                }
            });
            tipoDaVariavel.getItems().add(varColecao);
        }
        varItem.setSelected(true);
        v.setModificador(Modificador.PRIVATE);
        v.setTipo(varTipo, varImport);
        return tipoDaVariavel;
    }
    
}
