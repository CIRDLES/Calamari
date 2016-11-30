/*
 * IsotopeNames.java
 *
 *
 * Copyright 2006-2015 James F. Bowring and www.Earth-Time.org
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.cirdles.calamari.shrimp;

/**
 *
 * @author James F. Bowring
 */
/*
 *
 *
 * Created July 2011 as part of major refactoring to control magic strings
 */
public enum IsotopeNames {

    // IsotopeNames in atomic number order = acquisition order
    Hf176("Hf176", 176, "176Hf"),
    Zr2O196("Zr2O196", 196, "196Zr2O"),
    /**
     *
     */
    Hg202("Hg202", 202, "202Hg"),
    /**
     *
     */
    Pb204("Pb204", 204, "204Pb"),
    /**
     *
     */
    Pb206("Pb206", 206, "206Pb"),
    /**
     *
     */
    Pb207("Pb207", 207, "207Pb"),
    /**
     *
     */
    Pb208("Pb208", 208, "208Pb"),
    /**
     *
     */
    Th232("Th232", 232, "232Th"),
    /**
     *
     */
    U235("U235", 235, "235U"),
    /**
     *
     */
    U238("U238", 238, "238U"),
    ThO248("ThO248", 248, "248ThO"),
    UO254("UO254", 254, "254UO"),
    UO270("UO270", 270, "270UO"),
    BKGND("BKGND", 0, "BKGND");

    private String name;
    private int atomicMass;
    private String prawnName;

    private IsotopeNames(String name, int atomicMass, String prawnName) {
        this.name = name;
        this.atomicMass = atomicMass;
        this.prawnName = prawnName;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * @return the atomicMass
     */
    public int getAtomicMass() {
        return atomicMass;
    }

    /**
     * @return the prawnName
     */
    public String getPrawnName() {
        return prawnName;
    }

}
