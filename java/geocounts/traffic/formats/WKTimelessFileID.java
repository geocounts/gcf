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
 * 
 * A set of well known IDs used to represent a 'timeless' file's {@link RawTrafficDataFilename#fileID id field}
 *
 */
public enum WKTimelessFileID {
  /**
   * (0) Most recent day of real time data.<br />
   * This is typically a file of 15 or 60 minute tallies, representing the complete day when the most recent time real time data was active.<br />
   * The data file may have arrived at any time in the past so it may not correspond to 'now'.
   */
  recentday(0) {
    @Override
    public boolean isValid(TallyDurationEnum TD) {
      return TD.minutes <= TallyDurationEnum.hr.minutes;
    }
  },
  
  /**
   * (1) The most recent real time data (packet) received.<br />
   * This is typically a file containing the most recent 15 or 60 minute data received.<br />
   * The data file may have arrived at any time in the past so it may not correspond to 'now'.
   */
  recentpacket(1) {
    @Override
    public boolean isValid(TallyDurationEnum TD) {
      return TD.minutes <= TallyDurationEnum.hr.minutes;
    }
  },
  
  /**
   * (10) A complete annual history of the station.<br />
   * For example a file of AADT.
   */
  annualhistory(10) {
    @Override
    public boolean isValid(TallyDurationEnum TD) {
      return TD == TallyDurationEnum.year;
    }
  },
  
  /**
   * (11) A history of monthly data.<br />
   * For example a file of MADT.
   */
  monthlyhistory(11) {
    @Override
    public boolean isValid(TallyDurationEnum TD) {
      return TD == TallyDurationEnum.month;
    }
  },
  
  /**
   * (12) A history of traffic factors: seasonal, daily and axle correction factors.<br />
   * This is provided as months.
   */
  trafficfactors(12) {
    @Override
    public boolean isValid(TallyDurationEnum TD) {
      return TD == TallyDurationEnum.month;
    }
  };
  public final long ID;
  
  private WKTimelessFileID(long id) {
    ID = id;
  }
  
  public abstract boolean isValid(TallyDurationEnum TD);
  
  public static boolean isRecognized(long fileID) {
    for (WKTimelessFileID f: WKTimelessFileID.values())
      if (f.ID == fileID)
        return true;
    
    return false;
  }
  
  /**
   * 
   * @param fileID
   * @return The {@link WKTimelessFileID} corresponding to the fileID, or null if not found
   */
  public static WKTimelessFileID fromID(long fileID) {
    for (WKTimelessFileID f: WKTimelessFileID.values())
      if (f.ID == fileID)
        return f;
    
    return null;
  }
}
