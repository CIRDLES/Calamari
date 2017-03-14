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
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import org.cirdles.calamari.shrimp.RawRatioNamesSHRIMP;
import org.cirdles.calamari.tasks.expressions.ExpressionTreeInterface;
import org.cirdles.calamari.tasks.expressions.ExpressionWriterMathML;
import org.cirdles.calamari.tasks.expressions.builtinExpressions.SquidExpressionMinus1;
import org.cirdles.calamari.tasks.expressions.builtinExpressions.SquidExpressionMinus3;
import org.cirdles.calamari.tasks.expressions.builtinExpressions.SquidExpressionMinus4;
import org.cirdles.calamari.tasks.expressions.customExpressions.CustomExpression_LnPbR_U;
import org.cirdles.calamari.tasks.expressions.customExpressions.CustomExpression_LnUO_U;
import org.cirdles.calamari.tasks.expressions.customExpressions.CustomExpression_Net204cts_sec;
import org.cirdles.calamari.tasks.expressions.parsing.ExpressionParser;
import org.cirdles.calamari.tasks.storedTasks.SquidBodorkosTask1;

/**
 *
 * @author bowring
 */
public class FXMLDocumentController implements Initializable {

    private WebEngine webEngine;

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
    @FXML
    private Label versionAndDateLabel;
    @FXML
    private ToggleGroup toggleGroupSMB;
    @FXML
    private ToggleGroup toggleGroupRatioCalcMethod;
    @FXML
    private ChoiceBox<String> referenceMaterialFistLetterChoiceBox;
    @FXML
    private ListView<ExpressionTreeInterface> expressionListView;
    @FXML
    private Button reduceDataButton;
    @FXML
    private EventTarget reduceDataProgressIndicator;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        versionAndDateLabel.setText("Calamari version " + Calamari.VERSION + "   built on " + Calamari.RELEASE_DATE);

        //initialize Calamari tab
        try {
            calamariReportsFolderText.setText(
                    Calamari.getPrawnFileHandler().getReportsEngine().getFolderToWriteCalamariReports().getCanonicalPath());
        } catch (IOException iOException) {
        }

        ObservableList<String> refMatFistLetterChoiceBoxItems = FXCollections.observableArrayList("A", "T");
        referenceMaterialFistLetterChoiceBox.setItems(refMatFistLetterChoiceBoxItems);
        referenceMaterialFistLetterChoiceBox.setValue("T");

        // initialize expressions tab
        ObservableList<ExpressionTreeInterface> items = FXCollections.observableArrayList(
                CustomExpression_LnPbR_U.EXPRESSION,
                CustomExpression_LnUO_U.EXPRESSION,
                CustomExpression_Net204cts_sec.EXPRESSION,
                SquidExpressionMinus1.EXPRESSION,
                SquidExpressionMinus3.EXPRESSION,
                SquidExpressionMinus4.EXPRESSION);

        for (RawRatioNamesSHRIMP ratioName : RawRatioNamesSHRIMP.values()) {
            items.add(ratioName.getExpression());
        }

        expressionListView.setItems(items);

        webEngine = browser.getEngine();

        expressionListView.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends ExpressionTreeInterface> ov, ExpressionTreeInterface old_val, ExpressionTreeInterface new_val) -> {
            webEngine.loadContent(
                    ExpressionWriterMathML.toStringBuilderMathML(new_val).toString());
        });
    }

    @FXML
    private void handleParseButtonAction(ActionEvent event) {
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

    @FXML
    private void handleReducDataButtonAction(ActionEvent event) {
        if (Calamari.getPrawnFileHandler().currentPrawnFileLocationIsFile()) {
            Calamari.getPrawnFileHandler().initReportsEngineWithCurrentPrawnFileName();
            new ReduceDataWorker(
                    Calamari.getPrawnFileHandler(),
                    true,//normalizeIonCountsToSBM,
                    false,//useLinearRegressionToCalculateRatios,
                    "T",//(String) referenceMaterialFirstLetterComboBox.getSelectedItem(),
                    new SquidBodorkosTask1(), // temporarily hard-wired
                    reduceDataProgressIndicator).execute();
        } else {
            JOptionPane.showMessageDialog(
                    null,
                    "Please specify a Prawn XML file for processing.",
                    "Calamari Warning",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

}
