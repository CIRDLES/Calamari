/*
 * Copyright 2015 CIRDLES.
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
 *
 *
 * This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
 * See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
 * Any modifications to this file will be lost upon recompilation of the source schema. 
 * Generated on: 2015.10.25 at 07:31:08 AM EDT 
 */
package org.cirdles.calamari.prawn;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for RunParameterNames.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * <p>
 * <
 * pre>
 * &lt;simpleType name="RunParameterNames"&gt; &lt;restriction
 * base="{http://www.w3.org/2001/XMLSchema}string"&gt; &lt;enumeration
 * value="title"/&gt; &lt;enumeration value="sets"/&gt; &lt;enumeration
 * value="measurements"/&gt; &lt;enumeration value="scans"/&gt; &lt;enumeration
 * value="dead_time_ns"/&gt; &lt;enumeration value="sbm_zero_cps"/&gt;
 * &lt;enumeration value="autocentering"/&gt; &lt;enumeration
 * value="qt1y_mode"/&gt; &lt;enumeration
 * value="deflect_beam_between_peaks"/&gt; &lt;enumeration
 * value="autocenter_method"/&gt; &lt;enumeration value="stage_x"/&gt;
 * &lt;enumeration value="stage_y"/&gt; &lt;enumeration value="stage_z"/&gt;
 * &lt;enumeration value="stage_map_quad_cal"/&gt; &lt;enumeration
 * value="stage_map_cal"/&gt; &lt;enumeration value="detector_gains"/&gt;
 * &lt;enumeration value="detector_zero_cps"/&gt; &lt;enumeration
 * value="eisie_1_cps"/&gt; &lt;enumeration value="eisie_2_cps"/&gt;
 * &lt;enumeration value="eisie_3_cps"/&gt; &lt;enumeration
 * value="eisie_1_date_time"/&gt; &lt;enumeration value="eisie_2_date_time"/&gt;
 * &lt;enumeration value="eisie_3_date_time"/&gt; &lt;enumeration
 * value="primary_names"/&gt; &lt;enumeration value="primary_bits"/&gt;
 * &lt;enumeration value="primary_volts"/&gt; &lt;enumeration
 * value="secondary_names_bits"/&gt; &lt;enumeration value="secondary_bits"/&gt;
 * &lt;enumeration value="secondary_names_volts"/&gt; &lt;enumeration
 * value="secondary_volts"/&gt; &lt;/restriction&gt; &lt;/simpleType&gt;
 * </pre>
 *
 */
@XmlType(name = "RunParameterNames")
@XmlEnum
public enum RunParameterNames implements Serializable {

    /**
     *
     */
    @XmlEnumValue("title")
    TITLE("title"),
    /**
     *
     */
    @XmlEnumValue("sets")
    SETS("sets"),
    /**
     *
     */
    @XmlEnumValue("measurements")
    MEASUREMENTS("measurements"),
    /**
     *
     */
    @XmlEnumValue("scans")
    SCANS("scans"),
    /**
     *
     */
    @XmlEnumValue("dead_time_ns")
    DEAD_TIME_NS("dead_time_ns"),
    /**
     *
     */
    @XmlEnumValue("sbm_zero_cps")
    SBM_ZERO_CPS("sbm_zero_cps"),
    /**
     *
     */
    @XmlEnumValue("autocentering")
    AUTOCENTERING("autocentering"),
    /**
     *
     */
    @XmlEnumValue("qt1y_mode")
    QT_1_Y_MODE("qt1y_mode"),
    /**
     *
     */
    @XmlEnumValue("deflect_beam_between_peaks")
    DEFLECT_BEAM_BETWEEN_PEAKS("deflect_beam_between_peaks"),
    /**
     *
     */
    @XmlEnumValue("autocenter_method")
    AUTOCENTER_METHOD("autocenter_method"),
    /**
     *
     */
    @XmlEnumValue("stage_x")
    STAGE_X("stage_x"),
    /**
     *
     */
    @XmlEnumValue("stage_y")
    STAGE_Y("stage_y"),
    /**
     *
     */
    @XmlEnumValue("stage_z")
    STAGE_Z("stage_z"),
    /**
     *
     */
    @XmlEnumValue("stage_map_quad_cal")
    STAGE_MAP_QUAD_CAL("stage_map_quad_cal"),
    /**
     *
     */
    @XmlEnumValue("stage_map_cal")
    STAGE_MAP_CAL("stage_map_cal"),
    /**
     *
     */
    @XmlEnumValue("detector_gains")
    DETECTOR_GAINS("detector_gains"),
    /**
     *
     */
    @XmlEnumValue("detector_zero_cps")
    DETECTOR_ZERO_CPS("detector_zero_cps"),
    @XmlEnumValue("EISIE_measured")
    EISIE_measured("EISIE_measured"),
    @XmlEnumValue("EISIE_sets")
    EISIE_sets("EISIE_sets"),
    @XmlEnumValue("detector1_zero_sub_cps")
    detector1_zero_sub_cps("detector1_zero_sub_cps"),
    @XmlEnumValue("detector2_zero_sub_cps")
    detector2_zero_sub_cps("detector2_zero_sub_cps"),
    @XmlEnumValue("detector3_zero_sub_cps")
    detector3_zero_sub_cps("detector3_zero_sub_cps"),
    @XmlEnumValue("detector4_zero_sub_cps")
    detector4_zero_sub_cps("detector4_zero_sub_cps"),
    @XmlEnumValue("detector5_zero_sub_cps")
    detector5_zero_sub_cps("detector5_zero_sub_cps"),
    /**
     *
     */
    @XmlEnumValue("eisie_1_cps")
    EISIE_1_CPS("eisie_1_cps"),
    /**
     *
     */
    @XmlEnumValue("eisie_2_cps")
    EISIE_2_CPS("eisie_2_cps"),
    /**
     *
     */
    @XmlEnumValue("eisie_3_cps")
    EISIE_3_CPS("eisie_3_cps"),
    /**
     *
     */
    @XmlEnumValue("eisie_1_date_time")
    EISIE_1_DATE_TIME("eisie_1_date_time"),
    /**
     *
     */
    @XmlEnumValue("eisie_2_date_time")
    EISIE_2_DATE_TIME("eisie_2_date_time"),
    /**
     *
     */
    @XmlEnumValue("eisie_3_date_time")
    EISIE_3_DATE_TIME("eisie_3_date_time"),
    /**
     *
     */
    @XmlEnumValue("primary_names")
    PRIMARY_NAMES("primary_names"),
    /**
     *
     */
    @XmlEnumValue("primary_bits")
    PRIMARY_BITS("primary_bits"),
    /**
     *
     */
    @XmlEnumValue("primary_volts")
    PRIMARY_VOLTS("primary_volts"),
    /**
     *
     */
    @XmlEnumValue("secondary_names_bits")
    SECONDARY_NAMES_BITS("secondary_names_bits"),
    /**
     *
     */
    @XmlEnumValue("secondary_bits")
    SECONDARY_BITS("secondary_bits"),
    /**
     *
     */
    @XmlEnumValue("secondary_names_volts")
    SECONDARY_NAMES_VOLTS("secondary_names_volts"),
    /**
     *
     */
    @XmlEnumValue("secondary_volts")
    SECONDARY_VOLTS("secondary_volts"),
    /**
     *
     */
    @XmlEnumValue("faraday_configuration")
    faraday_configuration("faraday_configuration"),
    /**
     *
     */
    @XmlEnumValue("left_mount_name")
    left_mount_name("left_mount_name"),
    /**
     *
     */
    @XmlEnumValue("left_mount_owner")
    left_mount_owner("left_mount_owner"),
    /**
     *
     */
    @XmlEnumValue("right_mount_name")
    right_mount_name("right_mount_name"),
    /**
     *
     */
    @XmlEnumValue("right_mount_owner")
    right_mount_owner("right_mount_owner");
    private final String value;

    RunParameterNames(String v) {
        value = v;
    }

    /**
     *
     * @return
     */
    public String value() {
        return value;
    }

    /**
     *
     * @param v
     * @return
     */
    public static RunParameterNames fromValue(String v) {
        for (RunParameterNames c : RunParameterNames.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
