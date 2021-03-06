/*
 * Copyright (C) 2017 FHNW
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ch.fhnw.lernstickwelcome.controller.binder;

import ch.fhnw.lernstickwelcome.controller.WelcomeApplication;
import ch.fhnw.lernstickwelcome.controller.WelcomeController;
import ch.fhnw.lernstickwelcome.fxmlcontroller.MainController;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Binder class to init binings between view components and backend (model)
 * properties
 *
 * @author sschw
 */
public class MainBinder {

    private final WelcomeController controller;
    private final MainController welcomeApplicationStart;

    /**
     * Constructor of MainBinder class
     *
     * @param controller is needed to provide access to the backend properties
     * @param welcomeApplicationStart FXML controller which proviedes the view
     * properties
     */
    public MainBinder(WelcomeController controller, MainController welcomeApplicationStart) {
        this.controller = controller;
        this.welcomeApplicationStart = welcomeApplicationStart;
    }

    /**
     * Method to initialize the handlers for this class.
     *
     * @param progressDialog the progressDialog that should be shown when
     * clicking on save.
     */
    public void initHandlers(Stage progressDialog) {
        welcomeApplicationStart.getBtSaveButton().setOnAction((ActionEvent evt) -> {
                        
            //this if condition checks if the panel in which the save button 
            //is clicked is the security panel, if not do what you used to do before
            if(MainController.currentViewIndex != 3) {
                        
            controller.startProcessingTasks();
            progressDialog.showAndWait();    
                
            //if yes, then go to the SecuritySettingsContoller and execute the
            //doActionSave method that we created
            } else if (MainController.currentViewIndex == 3) {
                try {
                    controller.getGuiLoaderInstance().getSecuritySettingsController().doActionOnSave();
                    
                   
                } catch (IOException ex) {
                    Logger.getLogger(MainBinder.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            

        });
        welcomeApplicationStart.getBtFinishButton().setOnAction(evt -> {
            Stage s =((Stage) ((Node) evt.getSource()).getScene().getWindow());
            s.fireEvent(
                new WindowEvent(
                    s,
                    WindowEvent.WINDOW_CLOSE_REQUEST
                )
            );
        });
    }
}
