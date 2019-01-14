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

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Format is {@link #stationID stationid}_yyyyMMddHHmm_{@link #fileID fileid}.txt
 * @see WKTimelessFileID
 */
public class RawTrafficDataFilename {
  public final static String EXT = "txt";
  
  public RawTrafficDataFilename() {
  }
  
  public RawTrafficDataFilename(String value) {
    this.fromString(value);
  }
  
  /**
   * Creates a 'timeless' filename, which means a file's time is zeroed out.
   * You can distinguish between two timeless files at the same station using the {@link #fileID}
   * @see WKTimelessFileID
   * @param stationID
   * @param fileID A {@link WKTimelessFileID timeless file ID}
   * @return {@link RawTrafficDataFilename}
   */
  public static RawTrafficDataFilename createTimeless(String stationID, WKTimelessFileID fileID) {
    RawTrafficDataFilename result = new RawTrafficDataFilename();
    result.setStationID(stationID);
    result.fileID = fileID.ID;
    return result;
  }
  
  protected static final char PART_DELIM = '_';
  
  private final StationID stationID = new StationID();
  
  public String getStationID() {
    return stationID.getValue();
  }
  
  public void setStationID(Object value) {
    stationID.set(value != null ? value.toString() : null);
  }
  
  public void setStationID(long value) {
    stationID.set(Long.toString(value));
  }
  
  /**
   * yyyyMMddHHmm
   * For the month field, Jan = 1
   */
  public final int[] startDateTime = new int[5];
  
  /**
   * Does any information appear after the {@link #startDateTime} and before the {@link #fileID}?
   * If so, this is an error
   */
  public boolean errorInfoAfterDate = false;
  /**
   * Is there a {@link #fileID}?
   * If so, this is an error
   */
  public boolean errorMissingFileID = false;
  
  /**
   * This may be any positive value. 
   * If the file is 'timeless', you can use a {@link WKTimelessFileID} to distinguish between files containing different content
   */
  public long fileID = 0;
  
  private String ext;
  
  /**
   * 
   * @return The extension without a period, normally 'txt'
   */
  public String getExt() {
    return ext;
  }

  public boolean fromString(String value) {
    int pos1 = value.indexOf(PART_DELIM);
    if (pos1 < 0)
      return false;
    int pos2 = value.indexOf(PART_DELIM, pos1+1);
    int pos3 = value.indexOf('.', pos2+1);
    if (pos3 < 0)
      return false;

    stationID.set(value.substring(0, pos1));
    
    startDateTime[0] = Integer.parseInt(value.substring(pos1+1, pos1+5));
    startDateTime[1] = Integer.parseInt(value.substring(pos1+5, pos1+7));
    startDateTime[2] = Integer.parseInt(value.substring(pos1+7, pos1+9));
    startDateTime[3] = Integer.parseInt(value.substring(pos1+9, pos1+11));
    startDateTime[4] = Integer.parseInt(value.substring(pos1+11, pos1+13));

    fileID = 0;
    if (pos2 > 0) {
      fileID = Long.parseLong(value.substring(pos2+1, pos3));
      this.errorInfoAfterDate = pos1+13 != pos2;
    } else
      this.errorMissingFileID = true;
    
    ext = value.substring(pos3+1);
    return true;
  }
  
  public GregorianCalendar getStartDate() {
    GregorianCalendar gc = RawFmtUtils.newGregorianCalendar();
    gc.set(GregorianCalendar.YEAR, startDateTime[0]);
    gc.set(GregorianCalendar.MONTH, startDateTime[1]-1);
    gc.set(GregorianCalendar.DATE, startDateTime[2]);
    gc.set(GregorianCalendar.HOUR_OF_DAY, startDateTime[3]);
    gc.set(GregorianCalendar.MINUTE, startDateTime[4]);
    gc.set(GregorianCalendar.SECOND, 0);
    gc.set(GregorianCalendar.MILLISECOND, 0);
    return gc;
  }
  
  public GregorianCalendar getStartDateNoTime() {
    return RawFmtUtils.newGregorianCalendar(startDateTime[0], startDateTime[1]-1, startDateTime[2], 0);
  }
  
  /**
   * This will return zero if the file {@link #isTimeless()}
   * @return The year
   */
  public int getYear() {
    return startDateTime[0];
  }
  
  /**
   * This will return zero if the file {@link #isTimeless()}
   * @return The month, where Jan = 1
   */
  public int getMonth() {
    return startDateTime[1];
  }
  
  /**
   * This will return zero if the file {@link #isTimeless()}
   * @return The day of the month, where the first day = 1
   */
  public int getDate() {
    return startDateTime[2];
  }
  
  /**
   * This will always return zero if the file {@link #isTimeless()}
   * @return The hour of the day, where the first hour = 0
   */
  public int getHour() {
    return startDateTime[3];
  }
  
  /**
   * This will always return zero if the file {@link #isTimeless()}
   * @return The minute of the hour, where the first minute = 0
   */
  public int getMinute() {
    return startDateTime[4];
  }
  
  public void setStartDate(Calendar gc) {
    startDateTime[0] = gc.get(Calendar.YEAR);
    startDateTime[1] = gc.get(Calendar.MONTH)+1;
    startDateTime[2] = gc.get(Calendar.DATE);
    startDateTime[3] = gc.get(Calendar.HOUR_OF_DAY);
    startDateTime[4] = gc.get(Calendar.MINUTE);
  }
  
  public String toStringNoExt() {
    StringBuilder sb = new StringBuilder();
    sb.append(stationID);
    sb.append(PART_DELIM);
    toDatePart(sb);
    toTimePart(sb);
    sb.append(PART_DELIM);
    sb.append(fileID);
    return sb.toString();
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    result.append(toStringNoExt());
    result.append('.');
    result.append(EXT);
    return result.toString();
  }
  
  /**
   * 
   * @return The date as 'yyyyMMddHHmm'
   */
  public String getDateTimePart() {
    StringBuilder sb = new StringBuilder();
    toDatePart(sb);
    toTimePart(sb);
    return sb.toString();
  }
  
  public String getDatePart() {
    StringBuilder sb = new StringBuilder();
    toDatePart(sb);
    return sb.toString();
  }
  
  public String getTimePart() {
    StringBuilder sb = new StringBuilder();
    toTimePart(sb);
    return sb.toString();
  }
  
  /**
   * @deprecated Use {@link #getDateTimePart()}
   * @return Same as {@link #getDateTimePart()}
   */
  public String toDatePart() {
    return getDateTimePart();
  }
  
  private void toDatePart(StringBuilder sb) {
    // year
    int year = (startDateTime[0] < 0) ? -startDateTime[0] : startDateTime[0];
    if (year < 1000)
      sb.append('0');
    if (year < 100)
      sb.append('0');
    if (year < 10)
      sb.append('0');
    sb.append(year); 
    
    // month
    if (startDateTime[1] < 10)
      sb.append('0');
    sb.append(startDateTime[1]);
    
    // day
    if (startDateTime[2] < 10)
      sb.append('0');
    sb.append(startDateTime[2]);
  }
  
  private void toTimePart(StringBuilder sb) {
    // hour
    if (startDateTime[3] < 10)
      sb.append('0');
    sb.append(startDateTime[3]);
    
    // minute
    if (startDateTime[4] < 10)
      sb.append('0');
    sb.append(startDateTime[4]);
  }
  
  public boolean isTimeless() {
    return (startDateTime[0] == 0) && 
           (startDateTime[1] == 0) && 
           (startDateTime[2] == 0) && 
           (startDateTime[3] == 0) && 
           (startDateTime[4] == 0);
  }
  
  public boolean matches(WKTimelessFileID timelessID) {
    return (isTimeless() && (fileID == timelessID.ID));
  }

  /**
   * 
   * @param stationID Must not be null
   * @param startTime Must not be null
   * @param fileID May be zero
   * @return {@link RawTrafficDataFilename}
   * @since 1.11
   */
  public static RawTrafficDataFilename create(Object stationID, Calendar startTime, long fileID) {
    RawTrafficDataFilename fn = new RawTrafficDataFilename();
    fn.setStationID(stationID);
    fn.setStartDate(startTime);
    fn.fileID = fileID > -1 ? fileID : 0;
    return fn;
  }
}
