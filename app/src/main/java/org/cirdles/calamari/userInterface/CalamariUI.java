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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javax.swing.AbstractButton;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import org.cirdles.calamari.Calamari;
import org.cirdles.calamari.core.CalamariReportsEngine;
import org.cirdles.calamari.core.PrawnFileHandler;
import org.cirdles.calamari.prawn.PrawnFileFilter;
import org.cirdles.calamari.shrimp.RawRatioNamesSHRIMP;
import org.cirdles.calamari.tasks.expressions.ConstantNode;
import org.cirdles.calamari.tasks.expressions.ExpressionTree;
import org.cirdles.calamari.tasks.expressions.ExpressionTreeInterface;
import org.cirdles.calamari.tasks.expressions.ExpressionTreeWithRatiosInterface;
import org.cirdles.calamari.tasks.expressions.ExpressionWriterMathML;
import org.cirdles.calamari.tasks.expressions.builtinExpressions.SquidExpressionMinus3;
import org.cirdles.calamari.tasks.expressions.operations.Operation;
import org.cirdles.calamari.tasks.storedTasks.SquidBodorkosTask1;

/**
 *
 * @author James F. Bowring
 */
public class CalamariUI extends javax.swing.JFrame {

    private transient PrawnFileHandler prawnFileHandler;
    private transient JFXPanel fxPanel = new JFXPanel();
    private boolean normalizeIonCountsToSBM;
    private boolean useLinearRegressionToCalculateRatios;

    /**
     * Creates new form Calamari
     *
     * @param prawnFileHandler
     */
    public CalamariUI(PrawnFileHandler prawnFileHandler) {
        this.prawnFileHandler = prawnFileHandler;

        normalizeIonCountsToSBM = true;
        useLinearRegressionToCalculateRatios = false;

        initComponents();
        initUI();
    }

    private void initUI() {

        this.setPreferredSize(new Dimension(800, 575));
        CalamariUI.setDefaultLookAndFeelDecorated(true);
        UIManager.getLookAndFeelDefaults().put("defaultFont", new Font("SansSerif", Font.PLAIN, 12));

       // center me
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

        int w = this.getSize().width;
        int h = this.getSize().height;
        int x = (dim.width - w) / 2;
        int y = (dim.height - h) / 2;

        this.setLocation(x, y);

        this.setTitle("Calamari Raw Data Processing for SHRIMP");
        calamariInfo.setText("Calamari version " + Calamari.VERSION + "   built on " + Calamari.RELEASE_DATE);
        updateCurrentPrawnFileLocation();
        updateReportsFolderLocationText();

        fileMenu.setVisible(false);

        initExpressionsFX();

    }

    private void initExpressionsFX() {

        expressionsPane.add(fxPanel);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                initFX(fxPanel);
            }
        });
    }

    private static void initFX(JFXPanel fxPanel) {
        // This method is invoked on the JavaFX thread

        final WebView browser = new WebView();
        final WebEngine webEngine = browser.getEngine();
        browser.setMaxSize(200, 200);

        Scene scene = new Scene(browser);

        ExpressionTreeInterface EXPRESSION = new ExpressionTree("test");

        ((ExpressionTreeWithRatiosInterface) EXPRESSION).getRatiosOfInterest().add(RawRatioNamesSHRIMP.r238_196w);
        ExpressionTreeInterface r238_196w = ExpressionTreeWithRatiosInterface.buildRatioExpression(RawRatioNamesSHRIMP.r238_196w);

        ((ExpressionTreeWithRatiosInterface) EXPRESSION).getRatiosOfInterest().add(RawRatioNamesSHRIMP.r254_238w);
        ExpressionTreeInterface r254_238w = ExpressionTreeWithRatiosInterface.buildRatioExpression(RawRatioNamesSHRIMP.r254_238w);

        ExpressionTreeInterface r254_238wPow = new ExpressionTree("254/238^0.66", r254_238w, new ConstantNode("0.66", 0.66), Operation.pow());

        ((ExpressionTree) EXPRESSION).setLeftET(r238_196w);
        ((ExpressionTree) EXPRESSION).setRightET(r254_238wPow);
        ((ExpressionTree) EXPRESSION).setOperation(Operation.divide());

        ExpressionTreeInterface EXPRESSION2 = new ExpressionTree("test");
        ((ExpressionTree) EXPRESSION2).setLeftET(EXPRESSION);
        ((ExpressionTree) EXPRESSION2).setRightET(SquidExpressionMinus3.EXPRESSION);
        ((ExpressionTree) EXPRESSION2).setOperation(Operation.add());

        ((ExpressionTree) EXPRESSION2).setRootExpressionTree(true);
        webEngine.loadContent(
                ExpressionWriterMathML.toStringBuilderMathML(
                        EXPRESSION2).toString()
        +  ExpressionWriterMathML.toStringBuilderMathML(
                        EXPRESSION2).toString());
        
        fxPanel.setScene(scene);
    }

    private void updateCurrentPrawnFileLocation() {
        currentPrawnFileLocation.setText(prawnFileHandler.getCurrentPrawnFileLocation());
    }

    private void updateReportsFolderLocationText() {
        try {
            this.outputFolderLocation.setText(prawnFileHandler.getReportsEngine().getFolderToWriteCalamariReports().getCanonicalPath());
        } catch (IOException iOException) {
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        normalizeIonCounts = new javax.swing.ButtonGroup();
        selectRatioCalculationMethod = new javax.swing.ButtonGroup();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        prawnDataPane = new javax.swing.JLayeredPane();
        inputFileLocationLabel = new javax.swing.JLabel();
        outputFileLocationLabel = new javax.swing.JLabel();
        reduceDataButton = new javax.swing.JButton();
        selectReportsLocationButton = new javax.swing.JButton();
        outputFolderLocation = new javax.swing.JLabel();
        selectPrawnFileLocationButton = new javax.swing.JButton();
        currentPrawnFileLocation = new javax.swing.JLabel();
        calamariInfo = new javax.swing.JLabel();
        reduceDataProgressBar = new javax.swing.JProgressBar();
        normalizeCountsLabel = new javax.swing.JLabel();
        normalizeIonCountsYes = new javax.swing.JRadioButton();
        normalizeIonCountsNo = new javax.swing.JRadioButton();
        selectRatioCalcluationMethodLabel = new javax.swing.JLabel();
        useLinearRegression = new javax.swing.JRadioButton();
        useSpotAverage = new javax.swing.JRadioButton();
        normalizeCountsLabel1 = new javax.swing.JLabel();
        readMeButton = new javax.swing.JButton();
        selectReferenceMaterialInitialLetterLabel = new javax.swing.JLabel();
        referenceMaterialFirstLetterComboBox = new javax.swing.JComboBox<>();
        expressionsPane = new javax.swing.JLayeredPane();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        openMenuItem = new javax.swing.JMenuItem();
        saveMenuItem = new javax.swing.JMenuItem();
        saveAsMenuItem = new javax.swing.JMenuItem();
        exitMenuItem = new javax.swing.JMenuItem();
        calamariMenu = new javax.swing.JMenu();
        documentationMenuItem = new javax.swing.JMenuItem();
        aboutMenuItem = new javax.swing.JMenuItem();
        exitTwoMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(700, 475));
        setSize(new java.awt.Dimension(700, 475));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });

        jTabbedPane1.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jTabbedPane1ComponentResized(evt);
            }
        });

        prawnDataPane.setBackground(new java.awt.Color(255, 231, 228));
        prawnDataPane.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        prawnDataPane.setOpaque(true);
        prawnDataPane.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        inputFileLocationLabel.setBackground(new java.awt.Color(255, 231, 228));
        inputFileLocationLabel.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        inputFileLocationLabel.setText("Selected Prawn XML file path:");
        inputFileLocationLabel.setOpaque(true);
        prawnDataPane.add(inputFileLocationLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 90, 250, 20));

        outputFileLocationLabel.setBackground(new java.awt.Color(255, 231, 228));
        outputFileLocationLabel.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        outputFileLocationLabel.setText("Selected CalamariReports folder location:");
        outputFileLocationLabel.setOpaque(true);
        prawnDataPane.add(outputFileLocationLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 170, 330, 20));

        reduceDataButton.setBackground(new java.awt.Color(255, 255, 255));
        reduceDataButton.setFont(new java.awt.Font("Lucida Grande", 3, 14)); // NOI18N
        reduceDataButton.setText("Reduce Data and Produce Reports");
        reduceDataButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reduceDataButtonActionPerformed(evt);
            }
        });
        prawnDataPane.add(reduceDataButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 375, 390, 25));

        selectReportsLocationButton.setBackground(new java.awt.Color(255, 255, 255));
        selectReportsLocationButton.setFont(new java.awt.Font("Lucida Grande", 3, 14)); // NOI18N
        selectReportsLocationButton.setText("Select location for CalamariReports Folder");
        selectReportsLocationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectReportsLocationButtonActionPerformed(evt);
            }
        });
        prawnDataPane.add(selectReportsLocationButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 143, 420, 25));

        outputFolderLocation.setBackground(new java.awt.Color(255, 255, 255));
        outputFolderLocation.setText("path");
        outputFolderLocation.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        outputFolderLocation.setOpaque(true);
        prawnDataPane.add(outputFolderLocation, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 192, 630, -1));

        selectPrawnFileLocationButton.setBackground(new java.awt.Color(255, 255, 255));
        selectPrawnFileLocationButton.setFont(new java.awt.Font("Lucida Grande", 3, 14)); // NOI18N
        selectPrawnFileLocationButton.setText("Select Prawn XML File");
        selectPrawnFileLocationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectPrawnFileLocationButtonActionPerformed(evt);
            }
        });
        prawnDataPane.add(selectPrawnFileLocationButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 63, 220, 25));

        currentPrawnFileLocation.setBackground(new java.awt.Color(255, 255, 255));
        currentPrawnFileLocation.setText("path");
        currentPrawnFileLocation.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        currentPrawnFileLocation.setOpaque(true);
        prawnDataPane.add(currentPrawnFileLocation, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 112, 630, -1));

        calamariInfo.setBackground(new java.awt.Color(255, 8, 9));
        calamariInfo.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        calamariInfo.setForeground(new java.awt.Color(255, 255, 255));
        calamariInfo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        calamariInfo.setText("jLabel1");
        calamariInfo.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));
        calamariInfo.setOpaque(true);
        prawnDataPane.add(calamariInfo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 720, -1));

        reduceDataProgressBar.setBackground(new java.awt.Color(255, 231, 228));
        reduceDataProgressBar.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        reduceDataProgressBar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        reduceDataProgressBar.setOpaque(true);
        reduceDataProgressBar.setStringPainted(true);
        prawnDataPane.add(reduceDataProgressBar, new org.netbeans.lib.awtextra.AbsoluteConstraints(475, 375, 170, 25));

        normalizeCountsLabel.setBackground(new java.awt.Color(255, 231, 228));
        normalizeCountsLabel.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        normalizeCountsLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        normalizeCountsLabel.setText("Normalise Ion Counts to SBM?");
        normalizeCountsLabel.setOpaque(true);
        prawnDataPane.add(normalizeCountsLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 225, 290, 30));

        normalizeIonCountsYes.setBackground(new java.awt.Color(255, 231, 228));
        normalizeIonCounts.add(normalizeIonCountsYes);
        normalizeIonCountsYes.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        normalizeIonCountsYes.setSelected(true);
        normalizeIonCountsYes.setText("Yes");
        normalizeIonCountsYes.setOpaque(true);
        normalizeIonCountsYes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                normalizeIonCountsYesActionPerformed(evt);
            }
        });
        prawnDataPane.add(normalizeIonCountsYes, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 215, -1, -1));

        normalizeIonCountsNo.setBackground(new java.awt.Color(255, 231, 228));
        normalizeIonCounts.add(normalizeIonCountsNo);
        normalizeIonCountsNo.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        normalizeIonCountsNo.setText("No");
        normalizeIonCountsNo.setOpaque(true);
        normalizeIonCountsNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                normalizeIonCountsNoActionPerformed(evt);
            }
        });
        prawnDataPane.add(normalizeIonCountsNo, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 238, -1, -1));

        selectRatioCalcluationMethodLabel.setBackground(new java.awt.Color(255, 231, 228));
        selectRatioCalcluationMethodLabel.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        selectRatioCalcluationMethodLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        selectRatioCalcluationMethodLabel.setText("Select Ratio Calculation Method:");
        selectRatioCalcluationMethodLabel.setOpaque(true);
        prawnDataPane.add(selectRatioCalcluationMethodLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 265, 290, 30));

        useLinearRegression.setBackground(new java.awt.Color(255, 231, 228));
        selectRatioCalculationMethod.add(useLinearRegression);
        useLinearRegression.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        useLinearRegression.setText("Linear Regression to burn mid-time");
        useLinearRegression.setOpaque(true);
        useLinearRegression.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useLinearRegressionActionPerformed(evt);
            }
        });
        prawnDataPane.add(useLinearRegression, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 261, -1, -1));

        useSpotAverage.setBackground(new java.awt.Color(255, 231, 228));
        selectRatioCalculationMethod.add(useSpotAverage);
        useSpotAverage.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        useSpotAverage.setSelected(true);
        useSpotAverage.setText("Spot Average (time-invariant)");
        useSpotAverage.setOpaque(true);
        useSpotAverage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useSpotAverageActionPerformed(evt);
            }
        });
        prawnDataPane.add(useSpotAverage, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 284, -1, -1));

        normalizeCountsLabel1.setBackground(new java.awt.Color(255, 231, 228));
        normalizeCountsLabel1.setFont(new java.awt.Font("Lucida Grande", 1, 12)); // NOI18N
        normalizeCountsLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        normalizeCountsLabel1.setText("Calamari will validate the selected Prawn XML file.");
        normalizeCountsLabel1.setOpaque(true);
        prawnDataPane.add(normalizeCountsLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 63, 360, 25));

        readMeButton.setBackground(new java.awt.Color(255, 255, 255));
        readMeButton.setFont(new java.awt.Font("Lucida Grande", 3, 14)); // NOI18N
        readMeButton.setForeground(new java.awt.Color(255, 8, 9));
        readMeButton.setText("Please click here for Information about Calamari");
        readMeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                readMeButtonActionPerformed(evt);
            }
        });
        prawnDataPane.add(readMeButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 30, 520, 25));

        selectReferenceMaterialInitialLetterLabel.setBackground(new java.awt.Color(255, 231, 228));
        selectReferenceMaterialInitialLetterLabel.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        selectReferenceMaterialInitialLetterLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        selectReferenceMaterialInitialLetterLabel.setText("<html><p style=\"text-align:right\">Select Case-Insensitive First Letter</br> of Reference Material Name:</p></html>");
        selectReferenceMaterialInitialLetterLabel.setOpaque(true);
        prawnDataPane.add(selectReferenceMaterialInitialLetterLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 310, 290, 30));

        referenceMaterialFirstLetterComboBox.setBackground(new java.awt.Color(255, 255, 255));
        referenceMaterialFirstLetterComboBox.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        referenceMaterialFirstLetterComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" }));
        referenceMaterialFirstLetterComboBox.setSelectedIndex(19);
        referenceMaterialFirstLetterComboBox.setFocusCycleRoot(true);
        referenceMaterialFirstLetterComboBox.setName("T"); // NOI18N
        prawnDataPane.add(referenceMaterialFirstLetterComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 315, -1, -1));

        jTabbedPane1.addTab("Data", prawnDataPane);

        javax.swing.GroupLayout expressionsPaneLayout = new javax.swing.GroupLayout(expressionsPane);
        expressionsPane.setLayout(expressionsPaneLayout);
        expressionsPaneLayout.setHorizontalGroup(
            expressionsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 720, Short.MAX_VALUE)
        );
        expressionsPaneLayout.setVerticalGroup(
            expressionsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 414, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Expressions", expressionsPane);

        fileMenu.setMnemonic('f');
        fileMenu.setText("File");

        openMenuItem.setMnemonic('o');
        openMenuItem.setText("Open");
        fileMenu.add(openMenuItem);

        saveMenuItem.setMnemonic('s');
        saveMenuItem.setText("Save");
        fileMenu.add(saveMenuItem);

        saveAsMenuItem.setMnemonic('a');
        saveAsMenuItem.setText("Save As ...");
        saveAsMenuItem.setDisplayedMnemonicIndex(5);
        fileMenu.add(saveAsMenuItem);

        exitMenuItem.setMnemonic('x');
        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        calamariMenu.setMnemonic('h');
        calamariMenu.setText("Calamari");

        documentationMenuItem.setText("Documentation");
        documentationMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                documentationMenuItemActionPerformed(evt);
            }
        });
        calamariMenu.add(documentationMenuItem);

        aboutMenuItem.setMnemonic('a');
        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        calamariMenu.add(aboutMenuItem);

        exitTwoMenuItem.setText("Exit");
        exitTwoMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitTwoMenuItemActionPerformed(evt);
            }
        });
        calamariMenu.add(exitTwoMenuItem);

        menuBar.add(calamariMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exitMenuItemActionPerformed

    private void reduceDataButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reduceDataButtonActionPerformed
        if (prawnFileHandler.currentPrawnFileLocationIsFile()) {
            prawnFileHandler.initReportsEngineWithCurrentPrawnFileName();
            new ReduceDataWorker(
                    prawnFileHandler,
                    normalizeIonCountsToSBM,
                    useLinearRegressionToCalculateRatios,
                    (String) referenceMaterialFirstLetterComboBox.getSelectedItem(),
                    new SquidBodorkosTask1(), // temporarily hard-wired
                    reduceDataProgressBar).execute();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Please specify a Prawn XML file for processing.",
                    "Calamari Warning",
                    JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_reduceDataButtonActionPerformed

    private void selectReportsLocationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectReportsLocationButtonActionPerformed
        CalamariReportsEngine reportsEngine = prawnFileHandler.getReportsEngine();
        File reportFolder
                = FileHelper.allPlatformGetFolder("Select location to write reports",
                        reportsEngine.getFolderToWriteCalamariReports());
        if (reportFolder != null) {
            reportsEngine.setFolderToWriteCalamariReports(reportFolder);
            updateReportsFolderLocationText();
        }
    }//GEN-LAST:event_selectReportsLocationButtonActionPerformed

    private void selectPrawnFileLocationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectPrawnFileLocationButtonActionPerformed

        File prawnFile = FileHelper.allPlatformGetFile(//
                "Select Prawn file", //
                new File(prawnFileHandler.getCurrentPrawnFileLocation()), //
                "*.xml", new PrawnFileFilter(), false, this)[0];
        if (prawnFile != null) {
            try {
                prawnFileHandler.setCurrentPrawnFileLocation(prawnFile.getCanonicalPath());
                updateCurrentPrawnFileLocation();
            } catch (IOException iOException) {
            }
        }


    }//GEN-LAST:event_selectPrawnFileLocationButtonActionPerformed

    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
        AboutBox.getInstance().setVisible(true);
    }//GEN-LAST:event_aboutMenuItemActionPerformed

    private void documentationMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_documentationMenuItemActionPerformed
        BrowserControl.showURI("https://github.com/CIRDLES/ET_Redux/wiki/SHRIMP:-Intro");
    }//GEN-LAST:event_documentationMenuItemActionPerformed

    private void normalizeIonCountsYesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_normalizeIonCountsYesActionPerformed
        normalizeIonCountsToSBM = ((AbstractButton) evt.getSource()).isSelected();
    }//GEN-LAST:event_normalizeIonCountsYesActionPerformed

    private void normalizeIonCountsNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_normalizeIonCountsNoActionPerformed
        normalizeIonCountsToSBM = !((AbstractButton) evt.getSource()).isSelected();
    }//GEN-LAST:event_normalizeIonCountsNoActionPerformed

    private void useLinearRegressionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useLinearRegressionActionPerformed
        useLinearRegressionToCalculateRatios = ((AbstractButton) evt.getSource()).isSelected();
    }//GEN-LAST:event_useLinearRegressionActionPerformed

    private void useSpotAverageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useSpotAverageActionPerformed
        useLinearRegressionToCalculateRatios = !((AbstractButton) evt.getSource()).isSelected();
    }//GEN-LAST:event_useSpotAverageActionPerformed

    private void readMeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_readMeButtonActionPerformed
        BrowserControl.showURI("https://github.com/bowring/Calamari/blob/master/README.md");
    }//GEN-LAST:event_readMeButtonActionPerformed

    private void exitTwoMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitTwoMenuItemActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exitTwoMenuItemActionPerformed

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        
    }//GEN-LAST:event_formComponentResized

    private void jTabbedPane1ComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jTabbedPane1ComponentResized
        fxPanel.setBounds(
                25, expressionsPane.getHeight()/2, 
                expressionsPane.getWidth() - 50, expressionsPane.getHeight()/2);
    }//GEN-LAST:event_jTabbedPane1ComponentResized


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel calamariInfo;
    private javax.swing.JMenu calamariMenu;
    private javax.swing.JLabel currentPrawnFileLocation;
    private javax.swing.JMenuItem documentationMenuItem;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenuItem exitTwoMenuItem;
    private javax.swing.JLayeredPane expressionsPane;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JLabel inputFileLocationLabel;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JLabel normalizeCountsLabel;
    private javax.swing.JLabel normalizeCountsLabel1;
    private javax.swing.ButtonGroup normalizeIonCounts;
    private javax.swing.JRadioButton normalizeIonCountsNo;
    private javax.swing.JRadioButton normalizeIonCountsYes;
    private javax.swing.JMenuItem openMenuItem;
    private javax.swing.JLabel outputFileLocationLabel;
    private javax.swing.JLabel outputFolderLocation;
    private javax.swing.JLayeredPane prawnDataPane;
    private javax.swing.JButton readMeButton;
    private javax.swing.JButton reduceDataButton;
    private javax.swing.JProgressBar reduceDataProgressBar;
    private javax.swing.JComboBox<String> referenceMaterialFirstLetterComboBox;
    private javax.swing.JMenuItem saveAsMenuItem;
    private javax.swing.JMenuItem saveMenuItem;
    private javax.swing.JButton selectPrawnFileLocationButton;
    private javax.swing.JLabel selectRatioCalcluationMethodLabel;
    private javax.swing.ButtonGroup selectRatioCalculationMethod;
    private javax.swing.JLabel selectReferenceMaterialInitialLetterLabel;
    private javax.swing.JButton selectReportsLocationButton;
    private javax.swing.JRadioButton useLinearRegression;
    private javax.swing.JRadioButton useSpotAverage;
    // End of variables declaration//GEN-END:variables

}
