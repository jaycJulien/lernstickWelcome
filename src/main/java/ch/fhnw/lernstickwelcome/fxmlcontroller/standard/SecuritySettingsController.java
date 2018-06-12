/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.fhnw.lernstickwelcome.fxmlcontroller.standard;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

/**
 *
 * @author user
 */
public class SecuritySettingsController implements Initializable{
    
    //FXMLLoader loader = new FXMLLoader(lernstickWel)
    
   final ToggleGroup personalPhassphrasesGroup = new ToggleGroup();
   
   
   @FXML
   private RadioButton noPassPhraseRadio= new RadioButton();
   
   @FXML
   private RadioButton yesPassPhraseRadio = new RadioButton();
      
   @FXML
   private PasswordField passPhraseField = new PasswordField();
     
   @FXML
   private PasswordField passPhraseFieldRepeat = new PasswordField();


   
   
   
   
   
    
        /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        passPhraseField.setPromptText("Personal Passphrase...");
         passPhraseFieldRepeat.setPromptText("Repeated Personal Passphrase");

    }
    
    
}
