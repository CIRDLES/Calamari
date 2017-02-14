/*
 * Copyright 2006-2017 CIRDLES.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cirdles.calamari.userInterface;

import java.awt.Color;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javax.swing.border.LineBorder;
import org.cirdles.calamari.tasks.expressions.ExpressionTreeInterface;
import org.cirdles.calamari.tasks.expressions.ExpressionWriterMathML;

/**
 *
 * @author James F. Bowring
 */
public class ExpressionsFX extends JFXPanel {

    public ExpressionsFX() {
        super();
    }

    public void initFX(ExpressionTreeInterface expression) {
        // This method is invoked on a JavaFX thread

        final WebView browser = new WebView();
        browser.setMaxSize(200, 200);

        final WebEngine webEngine = browser.getEngine();

        Scene scene = new Scene(browser);

        webEngine.loadContent(
                ExpressionWriterMathML.toStringBuilderMathML(expression).toString());
        
        setScene(scene);
        setBorder(new LineBorder(Color.black, 1));
    }
}
