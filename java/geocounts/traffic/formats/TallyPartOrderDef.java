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
import java.util.Collection;

/**
 * The collection of {@link RawTrafficDataHeaderElement} in the order they appear in each tally record
 * @see TallyRecordsDef#TO
 */
public class TallyPartOrderDef extends RawTrafficDataHeaderElement {
  protected final ArrayList<TallyPartOrder> parts;
  
  protected TallyPartOrderDef() {
    parts = new ArrayList<TallyPartOrder>();
  }
  
  @Override
  public EnumHeaderRecords getSourceRecord() {
    return EnumHeaderRecords.TO;
  }
  
  public final void clear() {
    parts.clear();
  }
  
  protected boolean hasSpeedByLength() {
    return hasPart(TallyPartOrderEnum.TP);
  }
  
  public boolean hasPart(TallyPartOrderEnum po) {
    return parts.contains(po);
  }
  
  public boolean addPart(TallyPartOrderEnum po) {
    if (!hasPart(po)) {
      parts.add(po);
      return true;
    }
    return false;
  }
  
  public void set(TallyPartOrder... values) {
    parts.clear();
    for (TallyPartOrder p: values) {
      parts.add(p);
    }
  }

  @Override
  protected String[] write() {
    String[] result = new String[parts.size()];
    for (int i=0; i<parts.size(); i++)
      result[i] = parts.get(i).getID();
    return result;
  }

  @Override
  protected void read(String[] data) {
    for (String p: data) {
      try {
        addPart(TallyPartOrderEnum.valueOf(p));
      } catch (Exception ex) {
        parts.add(new TallyPartOrderUnknownImpl(p));
      }
    }
  }
  
  static class TallyPartOrderUnknownImpl implements TallyPartOrder {
    private String index;
    public TallyPartOrderUnknownImpl(String id) {
      index = id;
    }

    @Override
    public String getID() {
      return "TO" + index;
    }
    
    @Override
    public void setTally(String COUNTS, RawTrafficDataHeader header, DecodingOptions opts, VehicleTallyRec result) throws Exception {
      // TODO Auto-generated method stub
    }

    @Override
    public void encode(VehicleTallyRec tally, RawTrafficDataHeader header, StringBuilder result) throws Exception {
      // TODO Auto-generated method stub
    }

    @Override
    public Number[] toArray(VehicleTallyRec tally, RawTrafficDataHeader header) throws Exception {
      return new Number[0];
    }

  }
  
  public Collection<TallyPartOrder> getAll() {
    ArrayList<TallyPartOrder> result = new ArrayList<TallyPartOrder>();
    for (TallyPartOrder tpo: parts) {
      result.add(tpo);
    }
    return result;
  }
  
  /**
   * If the TO record is not defined, use this
   * @author scropley
   */
  public static void setDefaultPartOrder(TallyRecordsDef parent, java.util.List<TallyPartOrder> result) {
    if (parent.TF.isRecording()) {
      result.add(TallyPartOrderEnum.TF);
    }
    if (parent.TC.isRecording()) {
      result.add(TallyPartOrderEnum.TC);
    }
    if (parent.TS.isRecording()) {
      result.add(TallyPartOrderEnum.TS);
    }
    if (parent.TL.isRecording()) {
      result.add(TallyPartOrderEnum.TL);
    }
  }
  
  public final void copyFrom(TallyPartOrderDef other) {
    parts.clear();
    parts.addAll(other.parts);
  }
  
  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    for (TallyPartOrder tpo: parts) {
      if (result.length() > 0)
        result.append(' ');
      result.append(tpo.getID());
    }
    return result.toString();
  }

}
