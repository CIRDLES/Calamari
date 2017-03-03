/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.calamari;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.cirdles.commons.util.ResourceExtractor;

/**
 * FXML Controller class
 *
 * @author James F. Bowring
 */
public class FXMLDocumentTestController implements Initializable {

    @FXML
    private Button button;
    @FXML
    private Label label;
    @FXML
    private ImageView imageview;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

        ResourceExtractor prawnFileResourceExtractor
                = new ResourceExtractor(Calamari.class);

        File prawnFileResource = prawnFileResourceExtractor.extractResourceAsFile("./images/SquidLogo.png");
        File prawnFile = new File("SquidLogo.png");
        prawnFileResource.renameTo(prawnFile);
        imageview.setImage(new Image("../../../../../../../../SquidLogo.png"));

    }

    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        label.setText("Hello World!");
    }

}
