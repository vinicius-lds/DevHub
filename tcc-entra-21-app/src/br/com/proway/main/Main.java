package br.com.proway.main;

import br.com.proway.bean.login.Usuario;
import br.com.proway.controller.login.LoginController;
import br.com.proway.controller.main.MainController;
import br.com.proway.util.FXMLFile;
import br.vo.dao.DAO;
import java.util.Arrays;
import java.util.List;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * @author Vinícius Luis da Silva
 */
public class Main extends Application {

    public static Stage STAGE_PRINCIPAL;
    public static MainController MAIN_CONTROLLER;
    public static DAO DB_CONNECTION;
    public static Usuario USER;
    public static String WEBSITE;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        DB_CONNECTION = new DAO("root", "", "jdbc:mysql://localhost:3306/tcc");
        STAGE_PRINCIPAL = primaryStage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLFile.LOGIN));
        AnchorPane ap = loader.load();
        Scene scene = new Scene(ap);
        STAGE_PRINCIPAL.initStyle(StageStyle.TRANSPARENT);
        STAGE_PRINCIPAL.setScene(scene);
        scene.setFill(Color.TRANSPARENT);
        STAGE_PRINCIPAL.setMinWidth(800);
        STAGE_PRINCIPAL.setMinHeight(600);
        STAGE_PRINCIPAL.show();
        USER = new Usuario();
//        USER.setNome("Vinícius Luis da Silva");
//        USER.setEmail("vinicius.lds.br@gmail.com");
        ((LoginController)loader.getController()).verificarCache();
        List<Object[]> website = Main.DB_CONNECTION.select(new String[]{"link"}, "website").toArrayList();
        if(!website.isEmpty()) {
            WEBSITE = website.get(0)[0].toString();
        } else {
            WEBSITE = new String();
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}
