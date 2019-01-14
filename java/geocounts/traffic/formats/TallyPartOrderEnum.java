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
 * A tally record consists of optional parts which are defined in the header.
 * If there is no definition, the order is {@link TallyPartOrderDef#setDefaultPartOrder(TallyRecordsDef, java.util.List) implicitly defined}.
 * @author scropley
 *
 */
public enum TallyPartOrderEnum implements TallyPartOrder {
  /**
   * Component related to TF: total count fields
   * @see TallyRecordsDef#TF
   */
  TF() {

    @Override
    public void setTally(String COUNTS, RawTrafficDataHeader header, DecodingOptions opts, VehicleTallyRec result) throws Exception {
      String[] counts = RawFmtUtils.split(COUNTS, ',');
      for (int i=0; i<counts.length; i++) {
        try {
          header.T.TF.getField(i).set(Double.parseDouble(counts[i]), header.F.FU, result);
        } catch (Exception ex) {
          if (opts.assertTFValues)
            throw ex;
        }
      }
    }
    
    @Override
    public void encode(VehicleTallyRec tally, RawTrafficDataHeader header, StringBuilder result) throws Exception {
      for (int i=0; i<header.T.TF.numberOfFields(); i++) {
        if (i > 0)
          result.append(',');
        String v = header.T.TF.getField(i).format(tally, header.F.FU);
        if (v != null)
          result.append(v);
        else
          result.append("0");
      }
    }

    @Override
    public Number[] toArray(VehicleTallyRec tally, RawTrafficDataHeader header) throws Exception {
      Number[] result = new Number[header.T.TF.numberOfFields()];
      for (int i=0; i<header.T.TF.numberOfFields(); i++) {
        result[i] = header.T.TF.getField(i).get(tally, header.F.FU);
      }
      return result;
    }
  },
  
  /**
   * Component related to TC: tallied type classifications
   * @see TallyRecordsDef#TC
   */
  TC() {

    @Override
    public void setTally(String COUNTS, RawTrafficDataHeader header, DecodingOptions opts, VehicleTallyRec result) throws Exception {
      int[] counts = RawFmtUtils.splitInt(COUNTS, ',');
      if (opts.assertTCBins)
        assertBinLength(counts, header.T.TC);
      
      for (int i=0; i<counts.length; i++) {
        result.countsVehType[i] = counts[i];
      }
    }

    @Override
    public void encode(VehicleTallyRec tally, RawTrafficDataHeader header, StringBuilder result) throws Exception {
      for (int bin=0; bin<=header.T.TC.getMaximumBin(); bin++) {
        if (bin > 0)
          result.append(',');
        result.append((int)header.T.TC.getTally(tally, bin));
      }
    }

    @Override
    public Number[] toArray(VehicleTallyRec tally, RawTrafficDataHeader header) throws Exception {
      ClassifiedDef C = header.T.TC;
      int bins = 1 + C.getMaximumBin();
      Integer[] result = new Integer[bins];
      for (int bin=0; bin<=C.getMaximumBin(); bin++) {
        result[bin] = (int)C.getTally(tally, bin);
      }
      return result;
    }
  },

  /**
   * Component related to TS: tallied speed classifications
   * @see TallyRecordsDef#TS
   */
  TS() {

    @Override
    public void setTally(String COUNTS, RawTrafficDataHeader header, DecodingOptions opts, VehicleTallyRec result) throws Exception {
      int[] counts = RawFmtUtils.splitInt(COUNTS, ',');
      if (opts.assertTSBins)
        assertBinLength(counts, header.T.TS);

      for (int i=0; i<counts.length; i++) {
        result.countsSpeed[i] = counts[i];
      }
    }

    @Override
    public void encode(VehicleTallyRec tally, RawTrafficDataHeader header, StringBuilder result) throws Exception {
      for (int bin=0; bin<=header.T.TS.getMaximumBin(); bin++) {
        if (bin > 0)
          result.append(',');
        result.append((int)header.T.TS.getTally(tally, bin));
      }
    }

    @Override
    public Number[] toArray(VehicleTallyRec tally, RawTrafficDataHeader header) throws Exception {
      ClassifiedDef C = header.T.TS;
      int bins = 1 + C.getMaximumBin() - C.getMinimumBin();
      Number[] result = new Number[bins];
      for (int bin=C.getMinimumBin(); bin<=C.getMaximumBin(); bin++) {
        result[bin] = (int)C.getTally(tally, bin);
      }
      return result;
    }
  },

  /**
   * Component related to TL: tallied length classifications
   * @see TallyRecordsDef#TL
   */
  TL() {

    @Override
    public void setTally(String COUNTS, RawTrafficDataHeader header, DecodingOptions opts, VehicleTallyRec result) throws Exception {
      int[] counts = RawFmtUtils.splitInt(COUNTS, ',');
      if (opts.assertTLBins)
        assertBinLength(counts, header.T.TL);
      
      for (int i=0; i<counts.length; i++) {
        result.countsLength[i] = counts[i];
      }
    }

    @Override
    public void encode(VehicleTallyRec tally, RawTrafficDataHeader header, StringBuilder result) throws Exception {
      for (int bin=0; bin<=header.T.TL.getMaximumBin(); bin++) {
        if (bin > 0)
          result.append(',');
        result.append((int)header.T.TL.getTally(tally, bin));
      }
    }

    @Override
    public Number[] toArray(VehicleTallyRec tally, RawTrafficDataHeader header) throws Exception {
      ClassifiedDef C = header.T.TL;
      int bins = 1 + C.getMaximumBin() - C.getMinimumBin();
      Number[] result = new Number[bins];
      for (int bin=C.getMinimumBin(); bin<=C.getMaximumBin(); bin++) {
        result[bin] = (int)C.getTally(tally, bin);
      }
      return result;
    }
  },
  
  /**
   * Component related to speed by length (TP)
   * @see TallyRecordsDef#TP
   */
  TP() {

    @Override
    public void setTally(String COUNTS, RawTrafficDataHeader header, DecodingOptions opts, VehicleTallyRec result) throws Exception {
      ClassifiedDef L = header.T.TL;
      int lengthbins = 1 + L.getMaximumBin() - L.getMinimumBin();
      if (lengthbins == 0)
        return;
      
      int speedbin = 0;
      int lengthbin = 0;
      
      int[] counts = RawFmtUtils.splitInt(COUNTS, ',');
      for (int i=0; i<counts.length; i++) {
        result.countsSpeedByLength[speedbin][lengthbin] = counts[i];
        lengthbin++;
        if (lengthbin == lengthbins) {
          lengthbin = 0;
          speedbin++;
        }
      }
    }

    @Override
    public void encode(VehicleTallyRec tally, RawTrafficDataHeader header, StringBuilder result) throws Exception {
      Number[] data = toArray(tally, header);

      for (int bin=0; bin<data.length; bin++) {
        if (bin>0)
          result.append(',');
        result.append(data[bin].intValue());
      }
    }

    @Override
    public Number[] toArray(VehicleTallyRec tally, RawTrafficDataHeader header) throws Exception {
      ClassifiedSpeedDef S = header.T.TS;
      ClassifiedDef L = header.T.TL;
      int speedbins = 1 + S.getMaximumBin() - S.getMinimumBin();
      int lengthbins = 1 + L.getMaximumBin() - L.getMinimumBin();
      
      Number[] result = new Number[speedbins * lengthbins];

      int bin = 0;
      for (int speedbin=S.getMinimumBin(); speedbin<=S.getMaximumBin(); speedbin++) {
        for (int lengthbin=L.getMinimumBin(); lengthbin<=L.getMaximumBin(); lengthbin++) {
          result[bin] = tally.countsSpeedByLength[speedbin][lengthbin];
          bin++;
        }
      }
      return result;
    }
    
  };
  
  protected void assertBinLength(int[] counts, ClassifiedDef def) throws GCTrafficFormatException {
    if (counts.length != def.getNumberOfBinsInRecord())
      RawFmtUtils.throwException(this, counts.length + " elements in the " + def.getSourceRecord().name() + " classification part does not match " + def.getSourceRecord().name() + " definition requirements of " + def.getNumberOfBinsInRecord(), counts.length + " <> " + def.getNumberOfBinsInRecord());
  }

  @Override
  public final String getID() {
    return name();
  }
  
}