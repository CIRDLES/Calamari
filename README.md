Calamari
========

[![Build Status](https://travis-ci.org/bowring/Calamari.svg?branch=master)](https://travis-ci.org/bowring/Calamari)

**Calamari** is a joint cyber infrastructure product of [CIRDLES](http://cirdles.org),
the Cyber Infrastructure Research and Development Lab for the Earth Sciences, 
an undergraduate research lab at the College of Charleston in Charleston, South Carolina and GeoSciences Australia.

**Calamari** is gradually replicating the mathematics of the Excel-based data reduction portion of Ludwig's Squid 2.5 for SHRIMP mass
spectrometers.  The work is documented on a wiki located [here](https://github.com/CIRDLES/ET_Redux/wiki/SHRIMP:-Intro).
Our goal is to demonstrate the efficacy of our work in engineering a replacement for Squid 2.5.

**Calamari** demonstrates that it is possible to replicate the math of Squid 2.5 but does not yet attempt to replicate all of it or
any of its special cases.  **Calamari** currently handles well-behaved 9- and 10-peak zircon runs where the background is the 
third acquisition, as is often the case, where the SBM is positive, and where there are no range changes.

**Calamari** reads in a SHRIMP Prawn XML file and reports if the file does not conform to our
existing [schema](https://github.com/bowring/XSD/blob/master/SHRIMP/SHRIMP_PRAWN.xsd).  If it does conform, then
**Calamari** will produce a folder of 6 reports as ".csv" files that are explained [here]().

**Requirements:**  

1. 64-bit Operating System - Linux, Mac, Windows  
2. [Java 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html) 
3. Latest [**Calamari** release](https://github.com/bowring/Calamari/releases) - you want the ".jar" file  

**Instructions:**  

1. Launch **Calamari** by double-clicking the ".jar" file.  If it won't open, be sure to associate the ".jar" extension with
the java runtime.  Otherwise, open a terminal window and navigate to the folder with the ".jar" file and type:
"java -jar %the name of your calamari jar file%" and then the return key.  

2. Click the "Select Prawn XML File" button and you will be given two choices of files that we supply - you are welcome to try your
Prawn XML file:  
  1. 100142_G6147_10111109.43.xml - a 10-peak zircon  
  2. GA6030_070322.xml - a 9-peak zircon  

3. The default location for the reports folder is in the same folder that contains the **Calamari** ".jar" file.  

4. Click the "Reduce Data and Produce Reports" button and the progress bar will show reporting progress.  

5. Navigate to the reports folder and open the ".csv" files.




Getting Started for Developers:
---
Join Github by getting an account.  Fork and clone the repository and explore the code.  We value
your contributions, whether they are to the code, documentation, tests, help engine,
issues, or planning.  If you want more involvement, please contact 
[Jim Bowring](mailto://bowringj@cofc.edu).

