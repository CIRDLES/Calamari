/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.calamari;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
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

        File exampleFolder = new File("imagesFX");

//        try {this works +++++++++++++++++++
//            File image = new File(exampleFolder.getCanonicalPath() + File.separator + "SquidLogo.png");
//            String imageFileString = image.toURI().toURL().toString();
//            imageview.setImage(new Image(imageFileString));
//        } catch (IOException iOException) {
//        }
//        try {
//            if (exampleFolder.exists()) {
//                FileUtilities.recursiveDelete(exampleFolder.toPath());
//            }
//            if (exampleFolder.mkdir()) {
//                try {
//                    File prawnFileResource = prawnFileResourceExtractor.extractResourceAsFile("./images/SquidLogo.png");
//                    String imageFileString = prawnFileResource.toURI().toURL().toString();
//                    imageview.setImage(new Image(imageFileString));
//                } catch (MalformedURLException malformedURLException) {
//                }
////                File prawnFile = new File(exampleFolder.getCanonicalPath() + File.separator + "SquidLogo.png");
////                prawnFileResource.renameTo(prawnFile);
////
////                //imageview.setImage(new Image(exampleFolder.getCanonicalPath() + File.separator + "SquidLogo.png"));
////                imageview.setImage(new Image(prawnFile.toURI().toString()));
//            }
//        } catch (IOException iOException) {
//        }
        try {
//            final ImageReader rdr = ImageIO.getImageReadersByFormatName("PNG").next();
//            InputStream initialStream = FXMLDocumentTestController.class.getResourceAsStream("./images/3.png");
//            final ImageInputStream imageInput = ImageIO.createImageInputStream(initialStream);
//            rdr.setInput(imageInput);
//            final BufferedImage imBuff = rdr.read(0);
//            initialStream.close();


            InputStream initialStream = FXMLDocumentTestController.class.getResourceAsStream("./images/SquidLogo.png");
            File prawnFileResource = prawnFileResourceExtractor.extractResourceAsFile("./images/SquidLogo.png");
            File prawnFile = new File(exampleFolder.getCanonicalPath() + File.separator + "SquidLogo.png");
                prawnFileResource.renameTo(prawnFile);

            BufferedImage imBuff = ImageIO.read(prawnFile);
            File outputFile = new File(exampleFolder.getCanonicalPath() + File.separator + "SquidLogo.png");
            ImageIO.write(imBuff, "PNG", outputFile);
        } catch (IOException iOException) {
        }

//            byte[] buffer = new byte[initialStream.available()];
//            initialStream.read(buffer);
//
//            File targetFile = new File(exampleFolder.getCanonicalPath() + File.separator + "SquidLogo.png");
//            Files.write(buffer, targetFile);
//        BufferedImage image = null;
//        try {
//            InputStream fin = FXMLDocumentTestController.class.getResourceAsStream("./images/SquidLogo.png");
//            byte[] buffer = new byte[initialStream.available()];
//            initialStream.read(buffer);
//
//            File targetFile = new File("src/main/resources/targetFile.tmp");
//            OutputStream outStream = new FileOutputStream(targetFile);
//            outStream.write(buffer);
//
//            image = ImageIO.read(FXMLDocumentTestController.class.getResourceAsStream("./images/SquidLogo.png"));
//        } catch (IOException iOException) {
//        }
//
//        javafx.scene.image.WritableImage fxImage = SwingFXUtils.toFXImage(image, null);
//        imageview.setImage(fxImage);
    }

    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        label.setText("Hello World!");
    }

}
