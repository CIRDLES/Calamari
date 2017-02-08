/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cirdles.calamari;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.cirdles.calamari.tasks.expressions.ExpressionTree;
import org.cirdles.calamari.tasks.expressions.ExpressionTreeInterface;
import org.cirdles.calamari.tasks.expressions.ExpressionWriterMathML;
import org.cirdles.calamari.tasks.expressions.builtinExpressions.CustomExpression1;
import org.cirdles.calamari.tasks.expressions.builtinExpressions.CustomExpression2;
import org.cirdles.calamari.tasks.expressions.builtinExpressions.SquidExpressionMinus1;
import org.cirdles.calamari.tasks.expressions.builtinExpressions.SquidExpressionMinus3;
import org.cirdles.calamari.tasks.expressions.builtinExpressions.SquidExpressionMinus4;
import org.cirdles.calamari.tasks.expressions.constants.ConstantNode;
import org.cirdles.calamari.tasks.expressions.parsing.ExpressionParser;

/**
 *
 * @author bowring
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    private ListView<ExpressionTreeInterface> listView;
    @FXML
    public WebView browser;
    @FXML
    private TextField expressionText;

    WebEngine webEngine;
    @FXML
    private Button parseExpressionButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ObservableList<ExpressionTreeInterface> items = FXCollections.observableArrayList(
                CustomExpression1.EXPRESSION,
                CustomExpression2.EXPRESSION,
                SquidExpressionMinus1.EXPRESSION,
                SquidExpressionMinus3.EXPRESSION,
                SquidExpressionMinus4.EXPRESSION);

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
        if (result instanceof ExpressionTree) {
            ((ExpressionTree) result).setName("User expression");
        } 
//        else if (result instanceof ConstantNode) {
//            ((ConstantNode) result).setName("User constant");
//        }
        webEngine.loadContent(
                ExpressionWriterMathML.toStringBuilderMathML(result).toString());
    }
}
