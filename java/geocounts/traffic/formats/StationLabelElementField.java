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
 * Represents the type of labels each {@link StationLabels.StationLabelRec station label} can have
 * @since 1.44
 * @see LabelElementType
 * @see EnumHeaderRecords#SL
 * @see Equipment
 */
public enum StationLabelElementField {
  /**
   * A general description. 
   * Use this if other label elements are not suited to the information.
   */
  description(true, true, true),
  /**
   * The name of a linear path this object sits on.
   * E.g. a road name, a river name, etc
   */
  pathname(true, true, true),
  /**
   * The ID of a linear path this object sits on.
   * E.g. a route number, a bus route ID, etc
   */
  pathid(true, true, true),
  /**
   * The classification of the linear path this object sits on.
   * For example, a road functional classification.
   * @since 1.263
   */
  pathclass(false, true, true),
  /**
   * The ID of a linear reference this object sits on. This can be paired with the pathdist
   * @see #pathdist
   */
  pathlrs(false, true, true),
  /**
   * Linear distance. This information should match the pathlrs
   * @see #pathlrs
   */
  pathdist(false, true, true),
  /**
   * The name of a region. For example, the name of a county or local government area
   */
  regionname(false, false, true),
  /**
   * The name of an urban area. For example, a city or town.
   * An urban area does not need a local government to exist.
   */
  urbanareaname(false, false, true),
  /**
   * The name of a group to which this station belongs. For example, a group of similar stations.
   */
  groupname(false, false, true),
  /**
   * A "from" label can be used to label where counted objects are arriving from.
   */
  from(true, true, true),
  /**
   * A "to" label can be used to label where counted objects are going towards.
   */
  to(true, true, true),
  /**
   * A map book page and grid reference. E.g. 15F07
   */
  mapref(false, false, true),
  /**
   * A posted speed limit.
   */
  speedlimit(true, false, true),
  /**
   * An exit number
   * @since 1.282
   */
  exitnum(false, true, true),
  /**
   * An ID that links this element to another external system database.
   * This uses two elements for the text fields.
   * <ol>
   * <li>The first identifies the external system. This can be any value</li>
   * <li>The second is the identifier value used by the external system</li>
   * </ol>
   * @see StationLabels.OtherSystemIDLabelTextValue
   * @see StationLabels.StationLabelRec#getText()
   */
  othersystemid(true, true, true) {
    @Override
    public int getNumberOfFields() {
      return 2;
    }
  },
  
  /**
   * Labels for the TC header record.
   * The label elements always begin with element 0 even if the TC defines a <keyword>lowest_id</keyword> of 1. 
   * The number of elements should therefore always equal "highest_id + 1", where <keyword>highest_id</keyword> is defined in the TC header record.
   * @see ClassifiedVehTypeDef
   * @see ClassifiedVehTypeDef#getMaximumBin()
   * @see EnumHeaderRecords#TC
   */
  TC(false, false, true) {
    @Override
    public boolean hasMultipleFields() {
      return true;
    }
  },
  
  /**
   * Labels for the TL header record.
   * The number of elements should equal the number of length bins, including the <keyword>implied final bin</keyword> defined by the TL header record
   * @see ClassifiedLengthDef
   * @see ClassifiedLengthDef#getMaximumBin()
   * @see EnumHeaderRecords#TL
   */
  TL(false, false, true) {
    @Override
    public boolean hasMultipleFields() {
      return true;
    }
  },
  
  /**
   * Labels for the TS header record.
   * The number of elements should equal the number of speed bins defined by the TS header record
   * @see ClassifiedSpeedDef
   * @see EnumHeaderRecords#TS
   */
  TS(false, false, true) {
    @Override
    public boolean hasMultipleFields() {
      return true;
    }
  };

  public final boolean channel;
  public final boolean direction;
  public final boolean station;
  
  private StationLabelElementField(boolean chn, boolean drn, boolean stn) {
    channel = chn;
    direction = drn;
    station = stn;
  }
  
  public boolean hasMultipleFields() {
    return getNumberOfFields() > 1;
  }
  
  public int getNumberOfFields() {
    return 1;
  }

  /**
   * Only works for labels with {@link #hasMultipleFields() multiple fields}
   * @param SL
   * @param cols
   */
  public void setLabels(StationLabels SL, String[] cols) {
    if (hasMultipleFields())
      SL.addStationLabel(this, cols);
  }
  
  /**
   * Never returns null
   * @param SL
   * @return Array of labels
   */
  public String[] getLabels(StationLabels SL) {
    if (!hasMultipleFields())
      return new String[0];
    String[] result = SL.getStationLabelFields(this);
    return result != null ? result : new String[0];
  }
}
