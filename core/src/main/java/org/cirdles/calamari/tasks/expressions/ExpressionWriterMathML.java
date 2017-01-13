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
package org.cirdles.calamari.tasks.expressions;

import java.io.File;
import java.io.IOException;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.nio.file.Files;

/**
 *
 * @author CIRDLES.org
 */
public class ExpressionWriterMathML {

    public static StringBuilder toStringBuilderMathML(ExpressionTreeInterface expression){
                StringBuilder fileContents = new StringBuilder();
        fileContents.append(
                "<!DOCTYPE html>\n"
                + "<html>\n"
                + "    <head>\n"
                + "        <title>" + expression.getName() + "</title>\n"
                + "        <meta charset=\"UTF-8\">\n"
                + "        <script type=\"text/javascript\"\n"
                + "                src=\"https://cdn.mathjax.org/mathjax/latest/MathJax.js?config=TeX-MML-AM_SVG\">\n"
                + "        </script>\n"
                + "    </head>\n"
                + "    <body>\n"
                + "        <math xmlns=\"http://www.w3.org/1998/Math/MathML\" display=\"block\">\n"
                + "        <mstyle  mathsize='100%'\n>"
        );

        fileContents.append(expression.toStringMathML());

        fileContents.append(
                "          </mstyle>\n"
                + "        </math>\n"
                + "    </body>\n"
                + "</html>");
        
        return fileContents;

    }
    
    public static void writeExpressionToFileHTML(ExpressionTreeInterface expression)
            throws IOException {
        File expFile = new File(expression.getName() + ".html");
        Files.write(
                expFile.toPath(), 
                toStringBuilderMathML(expression).toString().getBytes(UTF_8));

    }

}
