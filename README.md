![logo](./app/src/main/resources/org/cirdles/calamari/squidoo-icon.png)Calamari
========

[![Build Status](https://travis-ci.org/bowring/Calamari.svg?branch=master)](https://travis-ci.org/bowring/Calamari)

**Calamari** is a joint cyber infrastructure product of [CIRDLES](http://cirdles.org),
the Cyber Infrastructure Research and Development Lab for the Earth Sciences,
an undergraduate research lab at the College of Charleston in Charleston, South Carolina and GeoScience Australia.

**Calamari** presents progress to date in replicating the mathematics of Ken Ludwig's Excel 2003-based add-in SQUID 2.50,  which handles isotopic data reduction for SHRIMP mass spectrometers.  The work is documented on a wiki located [here](https://github.com/CIRDLES/ET_Redux/wiki/SHRIMP:-Intro).
Our goal is to demonstrate the efficacy of our work in engineering a replacement for Squid 2.50.

**Calamari** demonstrates that it is possible to replicate the earliest stages of the math of SQUID 2.50, for 'mainstream' U-Pb zircon applications.  **Calamari** currently handles  'well-behaved' 9-peak (mass 196-254) and 10-peak (mass 196-270) zircon run-tables, where the background is acquired at the third mass-station (as is often the case). At present, **Calamari** does not contain any of SQUID's data-integrity checks relating to run-table changes mid-session, or SBM data that are sensible relative to the measured SBM-zero value.

**Calamari** reads in a SHRIMP Prawn XML file and reports if the file does not conform to our
existing [schema](https://github.com/bowring/XSD/blob/master/SHRIMP/SHRIMP_PRAWN.xsd).  If it does conform, then
**Calamari** will produce a folder of 6 reports as ".csv" files that are explained [here]().

**Requirements:**  

1. 64-bit Operating System - Linux, Mac, Windows  
2. [Java 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html) or later
3. Latest [**Calamari** release](https://github.com/bowring/Calamari/releases) - you want the ".jar" file  

**Instructions to run Java ".jar" file:**  

1. Launch **Calamari** by double-clicking the ".jar" file.  If it won't open, be sure to associate the ".jar" extension with
the java runtime.  Otherwise, open a terminal window, navigate to the folder with the ".jar" file, type:
"java -jar %the name of your calamari jar file%" and press the return key.  

2. Click the "Select Prawn XML File" button and you will be given the choice of two example XML files (although you are welcome to try your own Prawn XML file if you believe it conforms to the guidelines above):   
  1. 100142_G6147_10111109.43.xml - 10-peak zircon (mass 196-270, background is third mass-station)  
  2. GA6030_070322.xml - 9-peak zircon (mass 196-254, background is third mass-station)

3. The default location for the reports folder is in the same folder that contains the **Calamari** ".jar" file.  

4. Set your parameter choices (see below) for normalizing to SBM, using linear regression for ratio calculations, and selecting the first letter (case-insensitive) of your reference material name.  

5. Click the "Reduce Data and Produce Reports" button and the progress bar will show reporting progress.  

6. Navigate to the reports folder and open the ".csv" files.

**Instructions to use experimental web-service:**  

1. Open a terminal window and be sure you have cURL installed.  If not, get it [here](https://curl.haxx.se/download.html).  cURL is a command-line tool for transferring data with URLs.  
2. Navigate to the folder on your machine containing the Prawn XML file you want to process (recommended).  
3. Type (note backslash allows multiple lines for readability):
```
curl -X POST  \
-F "prawnFile=@<name of your prawn.xml file>" \
cirdlesserver.cs.cofc.edu:80/prawn  \
-F "useSBM=true" \
-F "userLinFits=false" \
-F "firstLetterRM=T" \
> reports.zip
```

4. Parameters:  
  1. Set "useSBM" to true if you want to normalize counts to SBM, to false otherwise.  
  2. Set "useLinFits" to true if you want ratios calculated by linear regression to mid burn-time, to false if you want the time-invariant spot average.
  3. Set "firstLetterRM" to the first letter (case-insensitive) of the name of your reference material.  The default is "T" for Temora.
  4. Note that if you want the defaults of useSBM = true, userLinFits = false, firstLetterRM = T, then you can simply type:
```
curl -X POST  \
-F "prawnFile=@<name of your prawn.xml file>" \
 cirdlesserver.cs.cofc.edu:80/prawn  \
 > reports.zip
```  

5. The web-service returns a zipped archive, but you can name it anything you like, such as myReports.zip.  This archive contains the six ".csv" files that the Java executable does as explained above.  The web service is experimental and does not return errors if encountered, rather it just quits quietly and returns a badly formed ".zip" file or just hangs, requiring a control-c to exit.



Getting Started for Developers:
---
Join Github by getting an account.  Fork and clone the repository and explore the code.  We value
your contributions, whether they are to the code, documentation, tests, help engine,
issues, or planning.  If you want more involvement, please contact
[Jim Bowring](mailto://bowringj@cofc.edu).
