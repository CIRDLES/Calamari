/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.calamari;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.cirdles.calamari.shrimp.RawRatioNamesSHRIMP;
import org.cirdles.calamari.tasks.expressions.ExpressionTreeInterface;
import org.cirdles.calamari.tasks.expressions.ExpressionWriterMathML;
import org.cirdles.calamari.tasks.expressions.builtinExpressions.CustomExpression1;
import org.cirdles.calamari.tasks.expressions.builtinExpressions.CustomExpression2;
import org.cirdles.calamari.tasks.expressions.builtinExpressions.SquidExpressionMinus1;
import org.cirdles.calamari.tasks.expressions.builtinExpressions.SquidExpressionMinus3;
import org.cirdles.calamari.tasks.expressions.builtinExpressions.SquidExpressionMinus4;
import org.cirdles.calamari.tasks.expressions.parsing.ExpressionParser;
import static org.cirdles.calamari.tasks.expressions.parsing.ExpressionParser.NAMED_EXPRESSIONS_MAP;

/**
 *
 * @author bowring
 */
public class FXMLDocumentController implements Initializable {
    
    private WebEngine webEngine;
    
    @FXML
    private ListView<ExpressionTreeInterface> listView;
    @FXML
    private WebView browser;
    @FXML
    private TextField expressionText;
    @FXML
    private AnchorPane ExpressionsPane;
    @FXML
    private MenuItem fileMenuSelectPrawnFile;
    @FXML
    private TextField prawnFilePathText;
    @FXML
    private MenuItem fileMenuSelectCalamariReportsLocation;
    @FXML
    private TextField calamariReportsFolderText;
    @FXML
    private MenuItem menuExit;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //initialize Calamari tab
        try {
            calamariReportsFolderText.setText(
                    Calamari.getPrawnFileHandler().getReportsEngine().getFolderToWriteCalamariReports().getCanonicalPath());
        } catch (IOException iOException) {
        }

        // initialize expressions tab
        ObservableList<ExpressionTreeInterface> items = FXCollections.observableArrayList(
                CustomExpression1.EXPRESSION,
                CustomExpression2.EXPRESSION,
                SquidExpressionMinus1.EXPRESSION,
                SquidExpressionMinus3.EXPRESSION,
                SquidExpressionMinus4.EXPRESSION);
        
        for (RawRatioNamesSHRIMP ratioName : RawRatioNamesSHRIMP.values()) {
            items.add(ratioName.getExpression());
        }
        
        listView.setItems(items);
        
        webEngine = browser.getEngine();
        
        listView.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends ExpressionTreeInterface> ov, ExpressionTreeInterface old_val, ExpressionTreeInterface new_val) -> {
            webEngine.loadContent(
                    ExpressionWriterMathML.toStringBuilderMathML(new_val).toString());
        });
    }
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        ExpressionParser dr = new ExpressionParser();
        ExpressionTreeInterface result = dr.parseExpression(expressionText.getText());
        
        webEngine.loadContent(
                ExpressionWriterMathML.toStringBuilderMathML(result).toString());
    }
    
    @FXML
    private void fileMenuSelectPrawnFileAction(ActionEvent event) {
        File prawnFile = Calamari.selectPrawnFile();
        if (prawnFile != null) {
            try {
                prawnFilePathText.setText(prawnFile.getCanonicalPath());
            } catch (IOException iOException) {
            }
        }
    }
    
    @FXML
    private void fileMenuSelectCalamariReportsLocationAction(ActionEvent event) {
        File reportsFolder = Calamari.selectCalamariReportsLocation();
        if (reportsFolder != null) {
            try {
                calamariReportsFolderText.setText(reportsFolder.getCanonicalPath());
            } catch (IOException iOException) {
            }
        }
    }

    @FXML
    private void menuExitAction(ActionEvent event) {
        Stage stage = (Stage) browser.getScene().getWindow();
        stage.close();
    }
    
}
