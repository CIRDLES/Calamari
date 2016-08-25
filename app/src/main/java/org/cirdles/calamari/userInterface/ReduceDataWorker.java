/*
 * Copyright 2016 CIRDLES.
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

import java.io.IOException;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import javax.xml.bind.JAXBException;
import org.cirdles.calamari.core.PrawnFileHandler;

/**
 * Reduces data in the background, updating the UI as it works.
 *
 * @author John Zeringue
 */
public class ReduceDataWorker extends SwingWorker<Void, Integer> {

    private final PrawnFileHandler prawnFileHandler;
    private final boolean useSBM;
    private final boolean userLinFits;
    private final String referenceMaterialLetter;
    private final JProgressBar progressBar;

    /**
     *
     * @param prawnFileHandler the value of prawnFileHandler
     * @param useSBM the value of useSBM
     * @param userLinFits the value of userLinFits
     * @param referenceMaterialLetter the value of referenceMaterialLetter
     * @param progressBar the value of progressBar
     */
    public ReduceDataWorker(
            PrawnFileHandler prawnFileHandler, 
            boolean useSBM, 
            boolean userLinFits, 
            String referenceMaterialLetter, 
            JProgressBar progressBar) {

        this.prawnFileHandler = prawnFileHandler;
        this.useSBM = useSBM;
        this.userLinFits = userLinFits;
        this.referenceMaterialLetter = referenceMaterialLetter;
        this.progressBar = progressBar;
    }

    @Override
    protected Void doInBackground() throws Exception {
        prawnFileHandler.setProgressSubscriber(progress -> publish(progress));

        try {
            prawnFileHandler.writeReportsFromPrawnFile(prawnFileHandler.getCurrentPrawnFileLocation(),
                    useSBM,
                    userLinFits, 
                    referenceMaterialLetter);
        } catch (IOException | JAXBException exception) {
            System.out.println("Exception extracting data: "
                    + exception.getStackTrace()[0].toString());
            String []message = 
                    ("Exception extracting data.;"
                    + "Please alert the development team.;"
                    + exception.toString()).split(";");
            JOptionPane.showMessageDialog(
                    null,
                    message,
                    "Calamari Warning",
                    JOptionPane.WARNING_MESSAGE);
        }

        prawnFileHandler.setProgressSubscriber(null);

        return null;
    }

    @Override
    protected void process(List<Integer> chunks) {
        int latestValue = chunks.get(chunks.size() - 1);
        progressBar.setValue(latestValue);
    }

    @Override
    protected void done() {
        progressBar.setValue(0);
    }

}
