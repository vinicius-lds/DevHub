package br.com.proway.util;

import br.com.proway.bean.login.Usuario;
import br.com.proway.controller.login.PrimeiroLoginController;
import br.com.proway.main.Main;
import static br.com.proway.main.Main.STAGE_PRINCIPAL;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * @author Vinícius Luis da Silva
 */
public abstract class FXUtil {
    
    public static void centralizarStage(Stage stage) {
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX(primScreenBounds.getWidth() - stage.getWidth());
        stage.setY(primScreenBounds.getHeight() - stage.getHeight());
    }
    
    public static double[] getTriagleCoordinates(Line ln_1) {
        double[] pontos = new double[6];
        if(ln_1.getEndX() == ln_1.getStartX()) {
            //linha vertical
            if(ln_1.getEndY() > ln_1.getStartY()) {
                //Ponta para baixo
                pontos = new double[] {
                    ln_1.getEndX(), ln_1.getEndY(),
                    ln_1.getEndX() - 5, ln_1.getEndY() - 10,
                    ln_1.getEndX() + 5, ln_1.getEndY() - 10
                };
            } else {
                //Ponta para cima
                pontos = new double[] {
                    ln_1.getEndX(), ln_1.getEndY(),
                    ln_1.getEndX() - 5, ln_1.getEndY() + 10,
                    ln_1.getEndX() + 5, ln_1.getEndY() + 10
                };
            }
        } else {
            //linha horizontal
            if(ln_1.getEndX() > ln_1.getStartX()) {
                //Ponta para direita
                pontos = new double[] {
                    ln_1.getEndX(), ln_1.getEndY(),
                    ln_1.getEndX() - 10, ln_1.getEndY() + 5,
                    ln_1.getEndX() - 10, ln_1.getEndY() - 5
                };
            } else {
                //Tortão para esquerda
                pontos = new double[] {
                    ln_1.getEndX(), ln_1.getEndY(),
                    ln_1.getEndX() + 10, ln_1.getEndY() + 5,
                    ln_1.getEndX() + 10, ln_1.getEndY() - 5
                };
            }
        }
        return pontos;
    }
    
    public static double[] getDiamondCoordinates(Line ln_1) {
        double[] pontos = new double[8];
        if(ln_1.getEndX() == ln_1.getStartX()) {
            //linha vertical
            if(ln_1.getEndY() > ln_1.getStartY()) {
                //Ponta para baixo
                pontos = new double[] {
                    ln_1.getEndX(), ln_1.getEndY(),
                    ln_1.getEndX() - 10, ln_1.getEndY() - 10,
                    ln_1.getEndX(), ln_1.getEndY() - 20,
                    ln_1.getEndX() + 10, ln_1.getEndY() - 10
                };
            } else {
                //Ponta para cima
                pontos = new double[] {
                    ln_1.getEndX(), ln_1.getEndY(),
                    ln_1.getEndX() - 10, ln_1.getEndY() + 10,
                    ln_1.getEndX(), ln_1.getEndY() + 20,
                    ln_1.getEndX() + 10, ln_1.getEndY() + 10
                };
            }
        } else {
            //linha horizontal
            if(ln_1.getEndX() > ln_1.getStartX()) {
                //Ponta para direita
                pontos = new double[] {
                    ln_1.getEndX(), ln_1.getEndY(),
                    ln_1.getEndX() - 10, ln_1.getEndY() + 10,
                    ln_1.getEndX() - 20, ln_1.getEndY(),
                    ln_1.getEndX() - 10, ln_1.getEndY() - 10
                };
            } else {
                //Tortão para esquerda
                pontos = new double[] {
                    ln_1.getEndX(), ln_1.getEndY(),
                    ln_1.getEndX() + 10, ln_1.getEndY() + 10,
                    ln_1.getEndX() + 20, ln_1.getEndY(),
                    ln_1.getEndX() + 10, ln_1.getEndY() - 10
                };
            }
        }
        return pontos;
    }
    
    public static double[] getArrowCoordinates(Line ln_1) {
        double[] pontos = new double[6];
        if(ln_1.getEndX() == ln_1.getStartX()) {
            //linha vertical
            if(ln_1.getEndY() > ln_1.getStartY()) {
                //Ponta para baixo
                pontos = new double[] {
                    ln_1.getEndX() - 5, ln_1.getEndY() - 10,
                    ln_1.getEndX(), ln_1.getEndY(),
                    ln_1.getEndX() + 5, ln_1.getEndY() - 10
                };
            } else {
                //Ponta para cima
                pontos = new double[] {
                    ln_1.getEndX() - 5, ln_1.getEndY() + 10,
                    ln_1.getEndX(), ln_1.getEndY(),
                    ln_1.getEndX() + 5, ln_1.getEndY() + 10
                };
            }
        } else {
            //linha horizontal
            if(ln_1.getEndX() > ln_1.getStartX()) {
                //Ponta para direita
                pontos = new double[] {
                    ln_1.getEndX() - 10, ln_1.getEndY() - 5,
                    ln_1.getEndX(), ln_1.getEndY(),
                    ln_1.getEndX() - 10, ln_1.getEndY() + 5
                };
            } else {
                //Tortão para esquerda
                pontos = new double[] {
                    ln_1.getEndX() + 10, ln_1.getEndY() - 5,
                    ln_1.getEndX(), ln_1.getEndY(),
                    ln_1.getEndX() + 10, ln_1.getEndY() + 5
                };
            }
        }
        return pontos;
    }
    
    public static Label[] getNosMultiplicidade(String parte, String todo, Line ln_1, Line ln_2, Line ln_3) {
        Label lbl_parte = new Label();
        Label lbl_todo = new Label();
        if (todo != null ) {
            lbl_todo.setText(todo);
            if (ln_1.getEndX() == ln_1.getStartX()) {
                //linha vertical
                if (ln_1.getEndY() > ln_1.getStartY()) {
                    //Ponta para baixo
                    lbl_todo.setLayoutX(ln_1.getEndX() + 5);
                    lbl_todo.setLayoutY(ln_1.getEndY() - 35);
                } else {
                    //Ponta para cima
                    lbl_todo.setLayoutX(ln_1.getEndX() + 5);
                    lbl_todo.setLayoutY(ln_1.getEndY() + 20);
                }
            } else {
                //linha horizontal
                if (ln_1.getEndX() > ln_1.getStartX()) {
                    //Ponta para direita
                    lbl_todo.setLayoutX(ln_1.getEndX() - 40);
                    lbl_todo.setLayoutY(ln_1.getEndY() - 20);
                } else {
                    //Tortão para esquerda
                    lbl_todo.setLayoutX(ln_1.getEndX() + 20);
                    lbl_todo.setLayoutY(ln_1.getEndY() - 20);
                }
            }
        }
        if(parte != null ) {
            lbl_parte.setText(parte);
            Line ln = ln_3.isVisible() ? ln_3 : ln_2.isVisible() ? ln_2 : ln_1;
            if (ln.getEndX() == ln.getStartX()) {
                //linha vertical
                if (ln.getEndY() > ln.getStartY()) {
                    //Ponta para baixo
                    lbl_parte.setLayoutX(ln.getStartX() + 5);
                    lbl_parte.setLayoutY(ln.getStartY());
                } else {
                    //Ponta para cima
                    lbl_parte.setLayoutX(ln.getStartX() + 5);
                    lbl_parte.setLayoutY(ln.getStartY() - 15);
                }
            } else {
                //linha horizontal
                if (ln.getEndX() > ln.getStartX()) {
                    //Ponta para direita
                    lbl_parte.setLayoutX(ln.getStartX() + 5);
                    lbl_parte.setLayoutY(ln.getStartY());
                } else {
                    //Tortão para esquerda
                    lbl_parte.setLayoutX(ln.getStartX() - 30);
                    lbl_parte.setLayoutY(ln.getStartY());
                }
            }
        }
        return new Label[] {lbl_parte, lbl_todo};
    }
    
    public static void paintInvalid(JFXTextField jfxtf) {
        jfxtf.setStyle("-fx-prompt-text-fill: red");
        jfxtf.setFocusColor(Paint.valueOf("red"));
        jfxtf.setUnFocusColor(Paint.valueOf("red"));
    }
    
    public static void paintValid(JFXTextField jfxtf) {
        jfxtf.setStyle("-fx-prompt-text-fill: #4059a9");
        jfxtf.setFocusColor(Paint.valueOf("#4059a9"));
        jfxtf.setUnFocusColor(Paint.valueOf("#4d4d4d"));
    }
    
    public static void paintInvalid(JFXComboBox jfxcb) {
        jfxcb.setStyle("-fx-prompt-text-fill: red");
        jfxcb.setFocusColor(Paint.valueOf("red"));
        jfxcb.setUnFocusColor(Paint.valueOf("red"));
    }
    
    public static void paintValid(JFXComboBox jfxcb) {
        jfxcb.setStyle("-fx-prompt-text-fill: #4059a9");
        jfxcb.setFocusColor(Paint.valueOf("#4059a9"));
        jfxcb.setUnFocusColor(Paint.valueOf("#4d4d4d"));
    }
    
    public static void paintInvalid(JFXPasswordField jfxtf) {
        jfxtf.setStyle("-fx-prompt-text-fill: red");
        jfxtf.setFocusColor(Paint.valueOf("red"));
        jfxtf.setUnFocusColor(Paint.valueOf("red"));
    }
    
    public static void paintValid(JFXPasswordField jfxtf) {
        jfxtf.setStyle("-fx-prompt-text-fill: #4059a9");
        jfxtf.setFocusColor(Paint.valueOf("#4059a9"));
        jfxtf.setUnFocusColor(Paint.valueOf("#4d4d4d"));
    }
    
    public static void reopenStage() {
        Scene s = Main.STAGE_PRINCIPAL.getScene();
        Main.STAGE_PRINCIPAL.close();
        Main.STAGE_PRINCIPAL.show();
        /*Main.STAGE_PRINCIPAL = new Stage();
        Main.STAGE_PRINCIPAL.initStyle(StageStyle.TRANSPARENT);
        //s.setFill(Color.TRANSPARENT);
        Main.STAGE_PRINCIPAL.setScene(s);
        Main.STAGE_PRINCIPAL.show();*/
    }
    
    public static void paintInvalid(TextField tf) {
        tf.setStyle("-fx-text-fill: red;"
                    + "-fx-focus-color: red;"
                    + "-fx-text-box-border: red");
    }
    
    public static void paintValid(TextField tf) {
        tf.setStyle("");
    } 
    
}
