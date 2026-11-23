/*
 * Licensed to Transmetric America Inc (TAI) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * TAI licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * <a href="http://geocounts.com">geocounts.com</a>
 */
package geocounts.traffic.formats;

import java.util.ArrayList;
import java.util.List;

/**
 * Well known classification system names and corresponding IDs from API
 * @see ClassifiedVehTypeDef
 */
public enum WKClassificationSystemName {
  /**
   * @see <a href="https://geocounts.com/api/cls?f=html&clsid=1462772971356">Austroads 94</a>
   */
  _austroads94(1462772971356l) {

    @Override
    public int getHighIndex() {
      return 14;
    }
    
    @Override
    public void set(TallyRecordsDef def) {
      def.TC.setAll(getName(), getHighIndex(), 1);
    }

    @Override
    public EnumHeaderRecords relatesTo() {
      return EnumHeaderRecords.TC;
    }

    @Override
    public int getMostCommon() {
      return 1;
    }

    @Override
    public SpecialClassificationCounter getWKTypeClassifier(WKTypeClass typeClass) {
      switch (typeClass) {
      case motorcycle:     return new SingleClassificationCounter(typeClass, 14);
      case wimcalibration: return new SingleClassificationCounter(typeClass, 9);
      case truck:          return new MultiClassificationCounter(typeClass, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
      case truckartic:     return new MultiClassificationCounter(typeClass, 6, 7, 8, 9, 10, 11, 12);
      case truckmulti:     return new MultiClassificationCounter(typeClass, 10, 11, 12);
      case motorized:      return new MultiClassificationCounter(typeClass, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
      case error:          return new SingleClassificationCounter(typeClass, 13);
      }
      return null;
    }

    @Override
    public String getRuleClassname() {
      return "geocounts.traffic.formats.operators.aus.Austroads94Classifier";
    }
  },

  /**
   * Alias for {@link #_austroads94}
   * @see #_austroads94
   */
  _austroads(1462772971356l) {
    @Override
    public int getHighIndex() {
      return _austroads94.getHighIndex();
    }
    
    @Override
    public void set(TallyRecordsDef def) {
      _austroads94.set(def);
    }
    
    @Override
    public EnumHeaderRecords relatesTo() {
      return _austroads94.relatesTo();
    }
    
    @Override
    public int getMostCommon() {
      return _austroads94.getMostCommon();
    }
    
    @Override
    public SpecialClassificationCounter getWKTypeClassifier(WKTypeClass typeClass) {
      return _austroads94.getWKTypeClassifier(typeClass);
    }
    
    @Override
    public String getRuleClassname() {
      return _austroads94.getRuleClassname();
    }
  },
  
  /**
   * org.austroads.wimnet.ARXClassifier
   * @see <a href="https://geocounts.com/api/cls?f=html&clsid=1462771861580">Austroads 94 X</a>
   */
  _austroads94x(1462771861580l) {

    @Override
    public int getHighIndex() {
      return 15;
    }

    @Override
    public void set(TallyRecordsDef def) {
      def.TC.setAll(getName(), getHighIndex(), 1);
    }

    @Override
    public EnumHeaderRecords relatesTo() {
      return _austroads94.relatesTo();
    }

    @Override
    public int getMostCommon() {
      return _austroads94.getMostCommon();
    }

    @Override
    public SpecialClassificationCounter getWKTypeClassifier(WKTypeClass typeClass) {
      switch (typeClass) {
      case motorized: return new MultiClassificationCounter(typeClass, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 14);
      case truck: return new MultiClassificationCounter(typeClass, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
      case truckartic: return new MultiClassificationCounter(typeClass, 6, 7, 8, 9, 10, 11, 12);
      case truckmulti: return new MultiClassificationCounter(typeClass, 10, 11, 12);
      case wimcalibration: return new SingleClassificationCounter(typeClass, 9);
      case motorcycle: return new SingleClassificationCounter(typeClass, 14);
      case bicycle: return new SingleClassificationCounter(typeClass, 15);
      case error: return new SingleClassificationCounter(typeClass, 13);
      }
      
      return null;
    }

    @Override
    public String getRuleClassname() {
      return _austroads94.getRuleClassname();
    }

  },
  
  _austroads2023(0l) {

    @Override
    public int getHighIndex() {
      return _austroads2023_3.getHighIndex();
    }
    
    @Override
    public void set(TallyRecordsDef def) {
      _austroads2023_3.set(def);
    }

    @Override
    public EnumHeaderRecords relatesTo() {
      return _austroads2023_3.relatesTo();
    }

    @Override
    public int getMostCommon() {
      return _austroads2023_3.getMostCommon();
    }

    @Override
    public SpecialClassificationCounter getWKTypeClassifier(WKTypeClass typeClass) {
      return _austroads2023_3.getWKTypeClassifier(typeClass);
    }

    @Override
    public String getRuleClassname() {
      return _austroads2023_3.getRuleClassname();
    }
  },
  
  /**
   * @see geocounts.traffic.formats.operators.aus.Austroads2023Classifier
   */
  _austroads2023_3(0l) {

    @Override
    public int getHighIndex() {
      return 20;
    }
    
    @Override
    public void set(TallyRecordsDef def) {
      _austroads94.set(def);
    }

    @Override
    public EnumHeaderRecords relatesTo() {
      return _austroads94.relatesTo();
    }

    @Override
    public int getMostCommon() {
      return _austroads94.getMostCommon();
    }

    @Override
    public SpecialClassificationCounter getWKTypeClassifier(WKTypeClass typeClass) {
      return _austroads94.getWKTypeClassifier(typeClass);
    }

    @Override
    public String getRuleClassname() {
      return "geocounts.traffic.formats.operators.aus.Austroads2023Classifier";
    }
  },

  _austroads2023_4(0l) {

    @Override
    public int getHighIndex() {
      return 2009;
    }
    
    @Override
    public void set(TallyRecordsDef def) {
      _austroads2023_3.set(def);
    }

    @Override
    public EnumHeaderRecords relatesTo() {
      return _austroads2023_3.relatesTo();
    }

    @Override
    public int getMostCommon() {
      return _austroads2023_3.getMostCommon();
    }

    @Override
    public SpecialClassificationCounter getWKTypeClassifier(WKTypeClass typeClass) {
      return _austroads2023_3.getWKTypeClassifier(typeClass);
    }

    @Override
    public String getRuleClassname() {
      return _austroads2023_3.getRuleClassname();
    }
  },
  
  /**
   * <a href="https://www.nhvr.gov.au/files/201707-0577-common-heavy-freight-vehicles-combinations.pdf">Australian NHVR</a>
   * @see <a href="https://geocounts.com/api/cls?f=html&clsid=1462771861581">Austroads 4 Length Bins</a>
   * @since 2.003
   */
  _austroads4bin(1462771861581l) {

    @Override
    public int getHighIndex() {
      return 4;
    }
    
    @Override
    public void set(TallyRecordsDef def) {
      def.TL.setFrom(getName(), 1, 6, 12, 21, 50);
    }

    @Override
    public EnumHeaderRecords relatesTo() {
      return EnumHeaderRecords.TL;
    }

    @Override
    public int getMostCommon() {
      return 1;
    }
    
    @Override
    public SpecialClassificationCounter getWKTypeClassifier(WKTypeClass typeClass) {
      switch (typeClass) {
      case motorized: return new MultiClassificationCounter(typeClass, 1, 2, 3, 4);
      case truck: return new MultiClassificationCounter(typeClass, 2, 3, 4);
      case truckmulti: return new SingleClassificationCounter(typeClass, 4);
      case error: return new SingleClassificationCounter(typeClass, 0);
      }
      return null;
    }

    @Override
    public String getRuleClassname() {
      return "geocounts.traffic.formats.operators.aus.AustroadsLength4Classifier";
    }

    @Override
    public String[] getLabels() {
      return new String[]{"Error", "Short", "Med", "Long", "Xtra"};
    }

    @Override
    public boolean setLabels(StationLabels SL) {
      StationLabelElementField.TL.setLabels(SL, getLabels());
      return true;
    }
  },
  
  /**
   * Scheme F (14 classes)
   * @see <a href="https://geocounts.com/api/cls?f=html&clsid=1393823272770">Scheme F</a>
   */
  _fhwaschemef(1393823272770l) {

    @Override
    public int getHighIndex() {
      return 14;
    }

    @Override
    public void set(TallyRecordsDef def) {
      def.TC.setAll(getName(), getHighIndex(), 1);
    }

    @Override
    public EnumHeaderRecords relatesTo() {
      return EnumHeaderRecords.TC;
    }

    @Override
    public int getMostCommon() {
      return 2;
    }

    @Override
    public SpecialClassificationCounter getWKTypeClassifier(WKTypeClass typeClass) {
      switch (typeClass) {
      case motorcycle: return new SingleClassificationCounter(typeClass, 1);
      case wimcalibration: return new SingleClassificationCounter(typeClass, 9);
      case bus: return new SingleClassificationCounter(typeClass, 4);
      case truck: return new MultiClassificationCounter(typeClass, 5, 6, 7, 8, 9, 10, 11, 12, 13);
      case truckartic: return new MultiClassificationCounter(typeClass, 8, 9, 10, 11, 12, 13);
      case truckmulti: return new MultiClassificationCounter(typeClass, 11, 12, 13);
      case motorized: return new MultiClassificationCounter(typeClass, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13);
      case error: return new MultiClassificationCounter(typeClass, 14, 15);
      }
      return null;
    }

    @Override
    public String getRuleClassname() {
      return "geocounts.traffic.formats.operators.usa.SchemeFClassifier";
    }
  },
  
  /**
   * Scheme F (15 classes)
   */
  _fhwaschemef15(0l) {

    @Override
    public int getHighIndex() {
      return 15;
    }
    
    @Override
    public void set(TallyRecordsDef def) {
      def.TC.setAll(getName(), getHighIndex(), 1);
    }

    @Override
    public EnumHeaderRecords relatesTo() {
      return EnumHeaderRecords.TC;
    }

    @Override
    public int getMostCommon() {
      return _fhwaschemef.getMostCommon();
    }
    
    @Override
    public SpecialClassificationCounter getWKTypeClassifier(WKTypeClass typeClass) {
      return _fhwaschemef.getWKTypeClassifier(typeClass);
    }

    @Override
    public String getRuleClassname() {
      return _fhwaschemef.getRuleClassname();
    }

  },
  
  /**
   * <a href="https://geocounts.com/api/cls?f=html&clsid=1489023592426">HPMS</a>
   */
  _hpmsvehsum(1489023592426l) {

    @Override
    public int getHighIndex() {
      return 6;
    }
    
    @Override
    public void set(TallyRecordsDef def) {
      def.TC.setAll(getName(), getHighIndex(), 1);
    }

    @Override
    public EnumHeaderRecords relatesTo() {
      return EnumHeaderRecords.TC;
    }

    @Override
    public int getMostCommon() {
      return 1;
    }
    
    @Override
    public SpecialClassificationCounter getWKTypeClassifier(WKTypeClass typeClass) {
      switch (typeClass) {
      case motorized: return new MultiClassificationCounter(typeClass, 1, 2, 3, 4, 5, 6);
      case motorcycle: return new SingleClassificationCounter(typeClass, 1);
      case bus: return new SingleClassificationCounter(typeClass, 4);
      case truck: return new MultiClassificationCounter(typeClass, 3, 4, 5, 6);
      case error: return new SingleClassificationCounter(typeClass, 0);
      }
      return null;
    }

    @Override
    public String[] getLabels() {
      return new String[]{null, "Motorcycle", "Car", "Light Truck", "Bus", "Single Unit Truck", "Combination Unit Truck"};
    }
  },

  /**
   * See com.transmetric.traffic.codecs.innovarytech.InnoTechTMClassSystem
   */
  _hpmsvehsumpbp(0) {

    @Override
    public int getHighIndex() {
      return 8;
    }

    @Override
    public void set(TallyRecordsDef def) {
      def.TC.setAll(getName(), getHighIndex(), 0);
    }

    @Override
    public EnumHeaderRecords relatesTo() {
      return EnumHeaderRecords.TC;
    }

    @Override
    public int getMostCommon() {
      return 2;
    }
    
    @Override
    public SpecialClassificationCounter getWKTypeClassifier(WKTypeClass typeClass) {
      switch (typeClass) {
      case motorized:  return new MultiClassificationCounter(typeClass, 1, 2, 3, 4, 5, 6);
      case motorcycle: return new SingleClassificationCounter(typeClass, 1);
      case bus:        return new SingleClassificationCounter(typeClass, 4);
      case truck:      return new MultiClassificationCounter(typeClass, 3, 4, 5, 6);  // CAREFUL - THIS CURRENTLY INCLUDES LGV!
      case error:      return new SingleClassificationCounter(typeClass, 0);
      case pedestrian: return new SingleClassificationCounter(typeClass, 7);
      case bicycle:    return new SingleClassificationCounter(typeClass, 8);
      }
      return null;
    }
    
    @Override
    public String[] getLabels() {
      //                                                   Panel Van, Pickup, Ute
      return new String[]{"Error", "Motorcycles", "Cars", "Light Goods Vehicles", "Buses", "Single-Unit Trucks", "Articulated Trucks", "Pedestrians", "Bicycles"};
    }
  },
  
  /*
   * @see org.ec.wimnet.Euro6Classifier
   */
  _eur6(0) {

    @Override
    public int getHighIndex() {
      return 6;
    }
    
    @Override
    public void set(TallyRecordsDef def) {
      def.TC.setAll(getName(), getHighIndex(), 0);
    }

    @Override
    public EnumHeaderRecords relatesTo() {
      return EnumHeaderRecords.TC;
    }

    @Override
    public int getMostCommon() {
      return 2;
    }

    @Override
    public SpecialClassificationCounter getWKTypeClassifier(WKTypeClass typeClass) {
      switch (typeClass) {
      case motorized: return new MultiClassificationCounter(typeClass, 1, 2, 3, 4, 5, 6);
      case motorcycle: return new SingleClassificationCounter(typeClass, 1);
      case bus: return new SingleClassificationCounter(typeClass, 6);
      case truck: return new MultiClassificationCounter(typeClass, 4, 5, 6);
      case truckartic: return new SingleClassificationCounter(typeClass, 5);
      case error: return new SingleClassificationCounter(typeClass, 0);
      }
      return null;
    }

    @Override
    public String[] getLabels() {
      return new String[]{"Unclassified", "Motorcycle", "Car", "Car and trailer", "Rigid", "Articulated", "Bus"};
    }
  },
  
  _siwim6(0) {
    @Override
    public int getHighIndex() {
      return 5;
    }
    
    @Override
    public void set(TallyRecordsDef def) {
      def.TC.setAll(getName(), getHighIndex(), 0);
    }

    @Override
    public EnumHeaderRecords relatesTo() {
      return EnumHeaderRecords.TC;
    }

    @Override
    public int getMostCommon() {
      return 0;
    }
    
    @Override
    public SpecialClassificationCounter getWKTypeClassifier(WKTypeClass typeClass) {
      switch (typeClass) {
      case motorized: return new MultiClassificationCounter(typeClass, 0, 1, 2, 3, 4, 5);
      case bus: return new SingleClassificationCounter(typeClass, 5);
      case truck: return new MultiClassificationCounter(typeClass, 1, 2, 3, 4, 5);
      case truckartic: return new MultiClassificationCounter(typeClass, 3, 4);
//      case truckmulti: return new SingleClassificationCounter(typeClass, 0);
      }
      return null;
    }
    
    @Override
    public String[] getLabels() {
      return new String[]{"car_or_van", "light_rigid", "heavy_rigid", "truck_with_trailer", "semitrailer", "Bus"};
    }
  },
  
  _5kph(0) {

    @Override
    public int getHighIndex() {  // is this correct, or should it be 31?
      return 30;
    }
    
    @Override
    public void set(TallyRecordsDef def) {
      def.TS.setFrom(getName(), getHighIndex(), 5);
    }

    @Override
    public EnumHeaderRecords relatesTo() {
      return EnumHeaderRecords.TS;
    }
  },
  
  _10kph(0) {

    @Override
    public int getHighIndex() {  // is this correct, or should it be 16?
      return 15;
    }
    
    @Override
    public void set(TallyRecordsDef def) {
      def.TS.setFrom(getName(), getHighIndex(), 10);
    }

    @Override
    public EnumHeaderRecords relatesTo() {
      return EnumHeaderRecords.TS;
    }
  },
  
  _5mph(0) {

    @Override
    public int getHighIndex() {  // is this correct, or should it be 21?
      return 20;
    }
    
    @Override
    public void set(TallyRecordsDef def) {
      def.TS.setFrom(getName(), getHighIndex(), 5*RawFmtUtils.KILOMETERS_PER_MILE);
    }

    @Override
    public EnumHeaderRecords relatesTo() {
      return EnumHeaderRecords.TS;
    }
  },
  
  _5mph_15(0) {

    @Override
    public int getHighIndex() {  // is this correct, or should it be 16?
      return 15;
    }
    
    @Override
    public void set(TallyRecordsDef def) {
      def.TS.setFrom(getName(), getHighIndex(), 5*RawFmtUtils.KILOMETERS_PER_MILE);
    }

    @Override
    public EnumHeaderRecords relatesTo() {
      return EnumHeaderRecords.TS;
    }
  },
  
  _10mph(0) {

    @Override
    public int getHighIndex() {  // is this correct, or should it be 11?
      return 10;
    }
    
    @Override
    public void set(TallyRecordsDef def) {
      def.TS.setFrom(getName(), getHighIndex(), 10*RawFmtUtils.KILOMETERS_PER_MILE);
    }

    @Override
    public EnumHeaderRecords relatesTo() {
      return EnumHeaderRecords.TS;
    }
  },
  
  /**
   * The Minnesota Department of Transportation (MnDOT), with funding assistance and technical guidance from the 
   * 15 pooled fund project members, conducted a study of length-based vehicle classification (LBVC) [TPF-5(192)].
   * @see <a href="http://www.dot.state.mn.us/research/TS/2012/201233.pdf">Mn DOT Research 201233</a>
   * @see <a href="http://www.pooledfund.org/details/study/416">Pooled Fund 416</a>
   */
  _us_tpf_5_192(1578940179155l) {

    @Override
    public int getHighIndex() {
      return 3;
    }
    
    @Override
    public void set(TallyRecordsDef def) {
      def.TL.setFrom(getName(),  6.5*RawFmtUtils.METERS_PER_FOOT, // Motorcycle: 0 to 6.5 feet
                                21.5*RawFmtUtils.METERS_PER_FOOT, // Short vehicle: 6.5 to 21.5 feet   OTHER OPTIONS INCLUDE: 18', 19.5', 20', 21', 21.8' and 22.5' 
                                49.0*RawFmtUtils.METERS_PER_FOOT  // Medium vehicle: 21.5 feet to 49 feet
                                 );                               // Long vehicle: 49 feet and larger
    }

    @Override
    public SpecialClassificationCounter getWKTypeClassifier(WKTypeClass typeClass) {
      switch (typeClass) {
      case motorcycle: return new SingleClassificationCounter(typeClass, 0);
      case motorized: return new MultiClassificationCounter(typeClass, 0, 1, 2, 3);
      case truck: return new MultiClassificationCounter(typeClass, 2, 3);
      }
      return null;
    }

    @Override
    public EnumHeaderRecords relatesTo() {
      return EnumHeaderRecords.TL;
    }

    @Override
    public int getMostCommon() {
      return 2;
    }
  },
  
  _fhwamicro2022_primary(0) {

    @Override
    public void set(TallyRecordsDef def) {
      def.TC.setAll(getName(), getHighIndex(), 1);
    }

    @Override
    public int getHighIndex() {
      return 8;
    }

    @Override
    public EnumHeaderRecords relatesTo() {
      return EnumHeaderRecords.TC;
    }
    
  },
  
  _fhwamicro2022_tc(0) {

    @Override
    public void set(TallyRecordsDef def) {
      def.TC.setAll(getName(), getHighIndex(), 0);
    }

    @Override
    public int getHighIndex() {
      return 22;
    }

    @Override
    public EnumHeaderRecords relatesTo() {
      return EnumHeaderRecords.TC;
    }
    
  },
  
  _volume(0) {

    @Override
    public int getHighIndex() {
      return 1;
    }
    
    @Override
    public void set(TallyRecordsDef def) {
      def.TC.setAll(getName(), getHighIndex(), 0);
    }

    @Override
    public EnumHeaderRecords relatesTo() {
      return EnumHeaderRecords.TC;
    }

    @Override
    public int getMostCommon() {
      return 1;
    }

    @Override
    public String[] getLabels() {
      return new String[]{null, "observations"};
    }
    
    @Override
    public SpecialClassificationCounter getWKTypeClassifier(WKTypeClass typeClass) {
      switch (typeClass) {
      case motorized: return new MultiClassificationCounter(typeClass, 1);
      }
      return null;
    }
  },
  
  /**
   * This is synonyms with {@link #_volume}.  Traffic Servers vehicle classification system for 
   * volume only is called "noclassifier".  At various time Traffic Server creates a {@link RawTrafficData} 
   * object in the background for things like computing annual stats and exporting to TMAS.
   */
  _noclassifier(0) {

    @Override
    public int getHighIndex() {
      return 1;
    }
    
    @Override
    public void set(TallyRecordsDef def) {
      def.TC.setAll(getName(), getHighIndex(), 0);
    }

    @Override
    public EnumHeaderRecords relatesTo() {
      return EnumHeaderRecords.TC;
    }

    @Override
    public int getMostCommon() {
      return 1;
    }

    @Override
    public String[] getLabels() {
      return new String[]{null, "observations"};
    }
    
    @Override
    public SpecialClassificationCounter getWKTypeClassifier(WKTypeClass typeClass) {
      switch (typeClass) {
      case motorized: return new MultiClassificationCounter(typeClass, 1);
      }
      return null;
    }
  },
  
  /**
   * Motorized vs non-motorized
   */
  _motorized(0) {

    @Override
    public int getHighIndex() {
      return 2;
    }

    @Override
    public void set(TallyRecordsDef def) {
      def.TC.setAll(getName(), getHighIndex(), 0);
    }

    @Override
    public EnumHeaderRecords relatesTo() {
      return EnumHeaderRecords.TC;
    }

    @Override
    public int getMostCommon() {
      return 1;
    }
    
    @Override
    public SpecialClassificationCounter getWKTypeClassifier(WKTypeClass typeClass) {
      if (typeClass == WKTypeClass.motorized)
        return new SingleClassificationCounter(typeClass, 1);
      if (typeClass == WKTypeClass.error)
        return new SingleClassificationCounter(typeClass, 0);
      return null;
    }

    @Override
    public String[] getLabels() {
      return new String[]{null, "motorized", "nonmotorized"};
    }
  },
  
  _cartruck(1578938759108l) {

    @Override
    public int getHighIndex() {
      return 2;
    }

    @Override
    public void set(TallyRecordsDef def) {
      def.TC.setAll(getName(), getHighIndex(), 0);
    }

    @Override
    public EnumHeaderRecords relatesTo() {
      return EnumHeaderRecords.TC;
    }

    @Override
    public int getMostCommon() {
      return 1;
    }

    @Override
    public SpecialClassificationCounter getWKTypeClassifier(WKTypeClass typeClass) {
      switch (typeClass) {
      case motorized: return new MultiClassificationCounter(typeClass, 1, 2);
      case truck: return new SingleClassificationCounter(typeClass, 2);
      case error: return new SingleClassificationCounter(typeClass, 0);
      }
      return null;
    }

    @Override
    public String[] getLabels() {
      return new String[]{null, "car", "truck"};
    }
  },
  
  _cartruckerror(1654705091582L) {

    @Override
    public int getHighIndex() {
      return 3;
    }
    
    @Override
    public void set(TallyRecordsDef def) {
      def.TC.setAll(getName(), getHighIndex(), 0);
    }

    @Override
    public EnumHeaderRecords relatesTo() {
      return EnumHeaderRecords.TC;
    }

    @Override
    public int getMostCommon() {
      return 1;
    }

    @Override
    public SpecialClassificationCounter getWKTypeClassifier(WKTypeClass typeClass) {
      switch (typeClass) {
      case motorized: return new MultiClassificationCounter(typeClass, 1, 2);
      case truck: return new SingleClassificationCounter(typeClass, 2);
      case error: return new SingleClassificationCounter(typeClass, 3);
      }
      return null;
    }

    @Override
    public String[] getLabels() {
      return new String[]{null, "car", "truck", "error"};
    }
  },

  _cartruckped(0l) {

    @Override
    public int getHighIndex() {
      return 3;
    }
    
    @Override
    public void set(TallyRecordsDef def) {
      def.TC.setAll(getName(), getHighIndex(), 0);
    }

    @Override
    public EnumHeaderRecords relatesTo() {
      return EnumHeaderRecords.TC;
    }

    @Override
    public int getMostCommon() {
      return 1;
    }

    @Override
    public SpecialClassificationCounter getWKTypeClassifier(WKTypeClass typeClass) {
      switch (typeClass) {
      case motorized: return new MultiClassificationCounter(typeClass, 1, 2);
      case truck: return new SingleClassificationCounter(typeClass, 2);
      case pedestrian: return new SingleClassificationCounter(typeClass, 3);
      case error: return new SingleClassificationCounter(typeClass, 0);
      }
      return null;
    }

    @Override
    public String[] getLabels() {
      return new String[]{null, "car", "truck", "pedestrian"};
    }
  },
  
  _cartruckbusped(0l) {

    @Override
    public int getHighIndex() {
      return 4;
    }
    
    @Override
    public void set(TallyRecordsDef def) {
      def.TC.setAll(getName(), getHighIndex(), 0);
    }

    @Override
    public EnumHeaderRecords relatesTo() {
      return EnumHeaderRecords.TC;
    }

    @Override
    public int getMostCommon() {
      return 1;
    }

    @Override
    public SpecialClassificationCounter getWKTypeClassifier(WKTypeClass typeClass) {
      switch (typeClass) {
      case motorized: return new MultiClassificationCounter(typeClass, 1, 2, 3);
      case truck: return new MultiClassificationCounter(typeClass, 2, 3);
      case bus: return new SingleClassificationCounter(typeClass, 3);
      case pedestrian: return new SingleClassificationCounter(typeClass, 4);
      case error: return new SingleClassificationCounter(typeClass, 0);
      }
      return null;
    }

    @Override
    public String[] getLabels() {
      return new String[]{null, "car", "truck", "bus", "pedestrian"};
    }
  },
  
  _cartruckbikeped(1578938184935l) {

    @Override
    public int getHighIndex() {
      return 4;
    }
    
    @Override
    public void set(TallyRecordsDef def) {
      def.TC.setAll(getName(), getHighIndex(), 0);
    }

    @Override
    public EnumHeaderRecords relatesTo() {
      return EnumHeaderRecords.TC;
    }

    @Override
    public int getMostCommon() {
      return 1;
    }

    @Override
    public SpecialClassificationCounter getWKTypeClassifier(WKTypeClass typeClass) {
      switch (typeClass) {
      case motorized: return new MultiClassificationCounter(typeClass, 1, 2);
      case truck: return new SingleClassificationCounter(typeClass, 2);
      case bicycle: return new SingleClassificationCounter(typeClass, 3);
      case pedestrian: return new SingleClassificationCounter(typeClass, 4);
      case error: return new SingleClassificationCounter(typeClass, 0);
      }
      return null;
    }

    @Override
    public String[] getLabels() {
      return new String[]{null, "car", "truck", "bicycle", "pedestrian"};
    }
  },
  
  _motorcyclecartruck(0l) {

    @Override
    public int getHighIndex() {
      return 3;
    }
    
    @Override
    public void set(TallyRecordsDef def) {
      def.TC.setAll(getName(), getHighIndex(), 0);
    }

    @Override
    public EnumHeaderRecords relatesTo() {
      return EnumHeaderRecords.TC;
    }

    @Override
    public int getMostCommon() {
      return 2;
    }

    @Override
    public SpecialClassificationCounter getWKTypeClassifier(WKTypeClass typeClass) {
      switch (typeClass) {
      case motorized: return new MultiClassificationCounter(typeClass, 1, 2, 3);
      case motorcycle: return new SingleClassificationCounter(typeClass, 1);
      case truck: return new SingleClassificationCounter(typeClass, 3);
      case error: return new SingleClassificationCounter(typeClass, 0);
      }
      return null;
    }

    @Override
    public String[] getLabels() {
      return new String[]{null, "motorcycle", "car", "truck"};
    }
  },
  
  /**
   * pedestrian,bike,motorized
   */
  _pedbike(1569303299000l) {

    @Override
    public int getHighIndex() {
      return 3;
    }
    
    @Override
    public void set(TallyRecordsDef def) {
      def.TC.setAll(getName(), getHighIndex(), 0);
    }

    @Override
    public EnumHeaderRecords relatesTo() {
      return EnumHeaderRecords.TC;
    }

    @Override
    public SpecialClassificationCounter getWKTypeClassifier(WKTypeClass typeClass) {
      switch (typeClass) {
      case motorized: return new SingleClassificationCounter(typeClass, 3);
      case bicycle: return new SingleClassificationCounter(typeClass, 2);
      case pedestrian: return new SingleClassificationCounter(typeClass, 1);
      case error: return new SingleClassificationCounter(typeClass, 0);
      }
      return null;
    }

    @Override
    public int getMostCommon() {
      return 3;
    }

    @Override
    public String[] getLabels() {
      return new String[]{null, "pedestrian", "bicycle", "motorized"};
    }
  },
  
  _pedcrossing1(1693919412000l) {
    @Override
    public int getHighIndex() {
      return 5;
    }
    
    @Override
    public void set(TallyRecordsDef def) {
      def.TC.setAll(getName(), getHighIndex(), 0);
    }

    @Override
    public EnumHeaderRecords relatesTo() {
      return EnumHeaderRecords.TC;
    }

    @Override
    public int getMostCommon() {
      return 1;
    }

    @Override
    public SpecialClassificationCounter getWKTypeClassifier(WKTypeClass typeClass) {
      switch (typeClass) {
      case motorized: return new MultiClassificationCounter(typeClass, 1, 2);
      case truck: return new SingleClassificationCounter(typeClass, 2);
      case pedestrian: return new MultiClassificationCounter(typeClass, 3, 4, 5);
      case error: return new SingleClassificationCounter(typeClass, 0);
      }
      return null;
    }

    @Override
    public String[] getLabels() {
      return new String[]{null, "car", "truck", "ped_primary", "ped_secondary", "ped_adult"};
    }
  },
  _miovision5bin(1712252248672l) {
    @Override
    public int getHighIndex() {
      return 5;
    }
    
    @Override
    public void set(TallyRecordsDef def) {
      def.TC.setAll(getName(), getHighIndex(), 0);
    }

    @Override
    public EnumHeaderRecords relatesTo() {
      return EnumHeaderRecords.TC;
    }

    @Override
    public int getMostCommon() {
      return 2;
    }
  },
  _nps8bin(1728401079748L) {
    @Override
    public int getHighIndex() {
      return 8;
    }
    
    @Override
    public void set(TallyRecordsDef def) {
      def.TC.setAll(getName(), getHighIndex(), 1);
    }

    @Override
    public EnumHeaderRecords relatesTo() {
      return EnumHeaderRecords.TC;
    }

    @Override
    public int getMostCommon() {
      return 2;
    }
  
    @Override
    public SpecialClassificationCounter getWKTypeClassifier(WKTypeClass typeClass) {
      switch (typeClass) {
      case motorcycle: return new SingleClassificationCounter(typeClass, 1);
//      case wimcalibration: return new SingleClassificationCounter(typeClass, 9);
      case bus: return new MultiClassificationCounter(typeClass, 5, 6);
      case truck: return new MultiClassificationCounter(typeClass, 3, 4, 5, 6, 7, 8);
      case motorized: return new MultiClassificationCounter(typeClass, 1, 2, 3, 4, 5, 6, 7, 8);
      }
      return null;
    }
    /*
    @Override
    public String getRuleClassname() {
      return "geocounts.traffic.formats.operators.usa.NPS8BinClassifier";
    }*/
  },
  
  _nps9bin(1750882118031L) {
    @Override
    public int getHighIndex() {
      return 9;
    }
    
    @Override
    public void set(TallyRecordsDef def) {
      def.TC.setAll(getName(), getHighIndex(), 1);
    }

    @Override
    public EnumHeaderRecords relatesTo() {
      return EnumHeaderRecords.TC;
    }

    @Override
    public int getMostCommon() {
      return 2;
    }
  
    @Override
    public SpecialClassificationCounter getWKTypeClassifier(WKTypeClass typeClass) {
      switch (typeClass) {
      case motorcycle: return new SingleClassificationCounter(typeClass, 1);
      case bus: return new MultiClassificationCounter(typeClass, 5, 6);
      case truck: return new MultiClassificationCounter(typeClass, 3, 4, 5, 6, 7, 8);
      case motorized: return new MultiClassificationCounter(typeClass, 1, 2, 3, 4, 5, 6, 7, 8);
      }
      return null;
    }
  };
  /*
   // https://transport.vic.gov.au/road-rules-and-safety/oversize-light-vehicle-restrictions
  _vicau_dim_height(0l) {
    @Override
    public int getHighIndex() {
      return 2;
    }
    
    @Override
    public void set(TallyRecordsDef def) {
      def.TH.setAll(getName(), getHighIndex(), 0);
    }

    @Override
    public EnumHeaderRecords relatesTo() {
      return EnumHeaderRecords.TH;
    }

    @Override
    public int getMostCommon() {
      return 0;
    }
  
    @Override
    public SpecialClassificationCounter getWKTypeClassifier(WKTypeClass typeClass) {
      return null;
    }
    
    @Override
    public boolean setLabels(StationLabels SL) {
      StationLabelElementField.TH.setLabels(SL, new String[]{"regulation", "overheight", "illegal"});
      return true;
    }
  }
  */
  ;
  
  public final long WKID;
  private WKClassificationSystemName(long id) {
    WKID = id;
  }
  
  public String getName() {
    return name().substring(1); // remove the underscore
  }
  
  public static WKClassificationSystemName fromName(String name) {
    String lwr = name.toLowerCase(); // check as lowercase
    
    String c = "_" + lwr; // add the underscore
    for (WKClassificationSystemName result: values())
      if (result.name().equals(c))
        return result;
    
    WKClassificationSystemName synonym = synonyms_lwrcase.get(lwr);
    if (synonym != null)
      return synonym;
    
    RawFmtUtils.throwException(WKClassificationSystemName.class, "Not a well known name", name);
    return null;
  }
  
  public static WKClassificationSystemName fromID(long ID) {
    for (WKClassificationSystemName result: values())
      if (result.WKID == ID)
        return result;
    RawFmtUtils.throwException(WKClassificationSystemName.class, "Not a well known ID", ID);
    return null;
  }
  
  /**
   * 
   * @param compare The {@link EnumHeaderRecords} to compare to
   * @return List of {@link WKClassificationSystemName}
   */
  public static List<WKClassificationSystemName> getRelatesTo(EnumHeaderRecords compare) {
    ArrayList<WKClassificationSystemName> result = new ArrayList<WKClassificationSystemName>();
    for (WKClassificationSystemName s: WKClassificationSystemName.values())
      if (s.relatesTo() == compare)
        result.add(s);
    return result;
  }
  
  public abstract void set(TallyRecordsDef def);
  
  public String[] getLabels() {
    return null;
  }
  
  public boolean setLabels(StationLabels SL) {
    String[] labels = getLabels();
    if (labels == null)
      return false;
    StationLabelElementField.TC.setLabels(SL, labels);
    return true;
  }

  public int getMostCommon() {
    return -1;
  }
  
  public abstract int getHighIndex();
  
  /**
   * 
   * @return Which header record this relates to
   */
  public abstract EnumHeaderRecords relatesTo();

  /**
   * @param typeClass The {@link WKTypeClass type classification}
   * @return A {@link SpecialClassificationCounter}
   */
  public SpecialClassificationCounter getWKTypeClassifier(WKTypeClass typeClass) {
    return null;
  }
  
  /**
   * 
   * @return An ObservationClassifier (or A VehicleClassificationSystem?)
   */
  public String getRuleClassname() {
    return null;
  }

  protected static final java.util.Map<String, WKClassificationSystemName> synonyms_lwrcase = new java.util.HashMap<String, WKClassificationSystemName>();

}
