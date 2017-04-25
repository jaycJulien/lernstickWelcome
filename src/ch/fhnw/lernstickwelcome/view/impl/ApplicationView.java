/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.fhnw.lernstickwelcome.view.impl;

import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 *
 * @author sschw
 */
public class ApplicationView extends GridPane {
    private final Label title = new Label();
    private final Label description = new Label();
    private final ImageView icon = new ImageView();
    private final Button help = new Button("?");
    private final ToggleButton install = new ToggleButton();
    
    public ApplicationView(ResourceBundle rb) {
        super();
        init(rb);
    }

    private void init(ResourceBundle rb) {
        setVgap(10);
        setHgap(10);
        
        help.setVisible(false);
        
        title.setAlignment(Pos.CENTER_LEFT);
        title.setFont(Font.font(null, FontWeight.BOLD, 14));
        title.setPadding(new Insets(0, 0, 0, 10));
        description.setAlignment(Pos.CENTER_LEFT);
        description.setFont(Font.font(null, FontWeight.NORMAL, 13));
        description.setPadding(new Insets(0, 0, 0, 10));
        icon.setFitHeight(50);
        icon.setFitWidth(50);
        install.setMinWidth(100);
        install.setMaxWidth(100);
        
        add(icon, 0, 0, 1, 2);
        add(title, 1, 0, 1, 2);
        add(help, 2, 0, 1, 2);
        add(install, 3, 0, 1, 2);
        
        setValignment(help, VPos.CENTER);
        setValignment(install, VPos.CENTER);
        
        setHgrow(title, Priority.ALWAYS);
        setHgrow(description, Priority.ALWAYS);
        setHgrow(icon, Priority.NEVER);
        setHgrow(help, Priority.NEVER);
        setHgrow(install, Priority.NEVER);
        
        setPadding(new Insets(10, 5, 10, 5));
        
        install.setText(rb.getString("ApplicationView.install"));
        install.disabledProperty().addListener(evt -> install.setText(rb.getString("ApplicationView.installed")));
    }
    
    public void setTitle(String title) {
        this.title.setText(title);
    }
    
    public void setDescription(String description) {
        this.description.setText(description);
        setRowSpan(title, 1);
        add(this.description, 1, 1);
    }
    
    public void setIcon(Image icon) {
        this.icon.setImage(icon);
    }
    
    public void setHelpAction(EventHandler<ActionEvent> evt) {
        help.setOnAction(evt);
        help.setVisible(true);
    }
    
    public BooleanProperty installingProperty() {
        return install.selectedProperty();
    }
}
