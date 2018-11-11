package br.com.proway.view.comp;

import com.jfoenix.controls.JFXButton;
import javafx.scene.Node;

/**
 * @author Vinícius Luis da Silva
 */
public class JFXDocButton extends JFXButton {

    private String doc;
    
    public JFXDocButton() {
    }

    public JFXDocButton(String text) {
        super(text);
    }

    public JFXDocButton(String text, Node graphic) {
        super(text, graphic);
    }
    
    public void setDoc(String doc) {
        this.doc = doc;
    }
    
    public String getDoc() {
        return this.doc;
    }
    
}
