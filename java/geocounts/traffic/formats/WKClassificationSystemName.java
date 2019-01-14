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

/**
 * Well known classification system names and corresponding IDs from TheOpen
 *
 */
public enum WKClassificationSystemName {
  /**
   * @see <a href="http://geocounts.com/visual/cls.jsp?id=1462772971356">Austroads 94</a>
   */
  _austroads94(1462772971356l) {
    @Override
    public void set(TallyRecordsDef def) {
      def.TC.setAll(getName(), 13, 1);
    }

    @Override
    public EnumHeaderRecords relatesTo() {
      return EnumHeaderRecords.TC;
    }

    @Override
    public SpecialClassificationCounter getSpecialClassificationCounter() {
      TruckClassCounter result = new TruckClassCounter(3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
      result.setOtherMotorizedClasses(1, 2);
      return result;
    }
    
    @Override
    public String getRuleClassname() {
      return "geocounts.traffic.formats.operators.aus.Austroads94Classifier";
    }
  },
  
  /**
   * org.austroads.wimnet.ARXClassifier
   * @see <a href="http://geocounts.com/visual/cls.jsp?id=1462771861580">Austroads 94 X</a>
   */
  _austroads94x(1462771861580l) {
    @Override
    public void set(TallyRecordsDef def) {
      def.TC.setAll(getName(), 14, 1);
    }

    @Override
    public EnumHeaderRecords relatesTo() {
      return EnumHeaderRecords.TC;
    }

    @Override
    public SpecialClassificationCounter getSpecialClassificationCounter() {
      TruckClassCounter result = new TruckClassCounter(3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
      result.setOtherMotorizedClasses(1, 2, 14);
      return result;
    }
  },
  
  /**
   * Scheme F (14 classes)
   * @see <a href="http://geocounts.com/visual/cls.jsp?id=1393823272770">Scheme F</a>
   */
  _fhwaschemef(1393823272770l) {
    @Override
    public void set(TallyRecordsDef def) {
      def.TC.setAll(getName(), 14, 1);
    }

    @Override
    public EnumHeaderRecords relatesTo() {
      return EnumHeaderRecords.TC;
    }

    @Override
    public SpecialClassificationCounter getSpecialClassificationCounter() {
      TruckClassCounter result = new TruckClassCounter(4, 5, 6, 7, 8, 9, 10, 11, 12, 13);
      result.setOtherMotorizedClasses(1, 2, 3);
      return result;
    }

    @Override
    public String getRuleClassname() {
      return "geocounts.traffic.formats.operators.tmg.SchemeFClassifier";
    }
  },
  
  /**
   * Scheme F (15 classes)
   */
  _fhwaschemef15(0l) {
    @Override
    public void set(TallyRecordsDef def) {
      def.TC.setAll(getName(), 15, 1);
    }

    @Override
    public EnumHeaderRecords relatesTo() {
      return EnumHeaderRecords.TC;
    }

    @Override
    public SpecialClassificationCounter getSpecialClassificationCounter() {
      return _fhwaschemef.getSpecialClassificationCounter();
    }
  },
  
  _hpmsvehsum(1489023592426l) {
    @Override
    public void set(TallyRecordsDef def) {
      def.TC.setAll(getName(), 6, 1);
    }

    @Override
    public EnumHeaderRecords relatesTo() {
      return EnumHeaderRecords.TC;
    }

    @Override
    public SpecialClassificationCounter getSpecialClassificationCounter() {
      TruckClassCounter result = new TruckClassCounter(3, 4, 5, 6);
      result.setOtherMotorizedClasses(1, 2);
      return result;
    }
  },
  
  _5kph(0) {
    @Override
    public void set(TallyRecordsDef def) {
      def.TS.setFrom(getName(), 30, 5);
    }

    @Override
    public EnumHeaderRecords relatesTo() {
      return EnumHeaderRecords.TS;
    }

    @Override
    public SpecialClassificationCounter getSpecialClassificationCounter() {
      return null;
    }
  },
  
  _10kph(0) {
    @Override
    public void set(TallyRecordsDef def) {
      def.TS.setFrom(getName(), 15, 10);
    }

    @Override
    public EnumHeaderRecords relatesTo() {
      return EnumHeaderRecords.TS;
    }

    @Override
    public SpecialClassificationCounter getSpecialClassificationCounter() {
      return null;
    }
  },
  
  _5mph(0) {
    @Override
    public void set(TallyRecordsDef def) {
      def.TS.setFrom(getName(), 20, 5*RawFmtUtils.KILOMETERS_PER_MILE);
    }

    @Override
    public EnumHeaderRecords relatesTo() {
      return EnumHeaderRecords.TS;
    }

    @Override
    public SpecialClassificationCounter getSpecialClassificationCounter() {
      return null;
    }
  },
  
  _10mph(0) {
    @Override
    public void set(TallyRecordsDef def) {
      def.TS.setFrom(getName(), 10, 10*RawFmtUtils.KILOMETERS_PER_MILE);
    }

    @Override
    public EnumHeaderRecords relatesTo() {
      return EnumHeaderRecords.TS;
    }

    @Override
    public SpecialClassificationCounter getSpecialClassificationCounter() {
      return null;
    }
  },
  
  /**
   * The Minnesota Department of Transportation (MnDOT), with funding assistance and technical guidance from the 
   * 15 pooled fund project members, conducted a study of length-based vehicle classification (LBVC) [TPF-5(192)].
   * @see <a href="http://www.dot.state.mn.us/research/TS/2012/201233.pdf">Mn DOT Research 201233</a>
   * @see <a href="http://www.pooledfund.org/details/study/416">Pooled Fund 416</a>
   */
  _us_tpf_5_192(0) {
    @Override
    public void set(TallyRecordsDef def) {
      def.TL.setFrom(getName(),  6.5*RawFmtUtils.METERS_PER_FOOT, // Motorcycle: 0 to 6.5 feet
                                21.5*RawFmtUtils.METERS_PER_FOOT, // Short vehicle: 6.5 to 21.5 feet   OTHER OPTIONS INCLUDE: 18', 19.5', 20', 21', 21.8' and 22.5' 
                                49.0*RawFmtUtils.METERS_PER_FOOT  // Medium vehicle: 21.5 feet to 49 feet
                                 );                               // Long vehicle: 49 feet and larger
    }

    @Override
    public EnumHeaderRecords relatesTo() {
      return EnumHeaderRecords.TL;
    }

    @Override
    public SpecialClassificationCounter getSpecialClassificationCounter() {
      return null;
    }
  },
  
  _cartruck(0) {
    @Override
    public void set(TallyRecordsDef def) {
      def.TC.setAll(getName(), 2, 0);
    }

    @Override
    public EnumHeaderRecords relatesTo() {
      return EnumHeaderRecords.TC;
    }

    @Override
    public SpecialClassificationCounter getSpecialClassificationCounter() {
      TruckClassCounter result = new TruckClassCounter(2);
      result.setOtherMotorizedClasses(1);
      return result;
    }
  },
  
  _cartruckbikeped(0) {
    @Override
    public void set(TallyRecordsDef def) {
      def.TC.setAll(getName(), 4, 0);
    }

    @Override
    public EnumHeaderRecords relatesTo() {
      return EnumHeaderRecords.TC;
    }

    @Override
    public SpecialClassificationCounter getSpecialClassificationCounter() {
      TruckClassCounter result = new TruckClassCounter(2);
      result.setOtherMotorizedClasses(1);
      return result;
    }
  };
  
  public final long WKID;
  private WKClassificationSystemName(long id) {
    WKID = id;
  }
  
  public String getName() {
    return name().substring(1); // remove the underscore
  }
  
  public static WKClassificationSystemName fromName(String name) {
    return WKClassificationSystemName.valueOf("_" + name.toLowerCase()); // add the underscore
  }
  
  public static WKClassificationSystemName fromID(long ID) {
    for (WKClassificationSystemName result: values())
      if (result.WKID == ID)
        return result;
    throw new RuntimeException("Not a well known ID " + ID);
  }
  
  public abstract void set(TallyRecordsDef def);

  /**
   * 
   * @return Which header record this relates to
   */
  public abstract EnumHeaderRecords relatesTo();

  /**
   * 
   * @return A {@link SpecialClassificationCounter}
   */
  public abstract SpecialClassificationCounter getSpecialClassificationCounter();
  
  public String getRuleClassname() {
    return null;
  }
}
