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

import java.util.GregorianCalendar;
import java.util.function.IntPredicate;

/**
 * Answers questions about time with respect to weeks and weekdays.
 * This is necessary because the {@link TallyDurationEnum} record does not understand weeks or weekdays.
 * @see TallyDurationEnum
 *
 */
public class WeekDayHelper {
  
  public int getDayOfWeek(VehicleTallyRec tally) {
    GregorianCalendar gc = RawFmtUtils.newGregorianCalendar(tally.getTime());
    return gc.get(GregorianCalendar.DAY_OF_WEEK);
  }
  
  public int getWeekOfYear(long time) {
    GregorianCalendar gc = RawFmtUtils.newGregorianCalendar(time);
    return gc.get(GregorianCalendar.WEEK_OF_YEAR);
  }
  
  
  public enum DayOrderEnum {
    sunday_start() {

      @Override
      public int[] getIDs() {
        return new int[]{1, 2, 3, 4, 5, 6, 7};
      }
      
      @Override
      public int getIndexFromDayID(int dowSunEq1) {
        return dowSunEq1-1;
      }

      @Override
      public boolean isIndexWeekdayOrWeekend(int index) {
        if ((index==0) || (index==6))
          return false;
        return true;
      }
    },
    
    sunday_end() {

      @Override
      public int[] getIDs() {
        return new int[]{2, 3, 4, 5, 6, 7, 1};   // sunday at end      
      }
      
      @Override
      public int getIndexFromDayID(int dowSunEq1) {
        int MEET_THE_PRESS = 6; // Because if its Sunday, its Meet the Press
        return dowSunEq1 == 1 ? MEET_THE_PRESS : dowSunEq1-2;
      }

      @Override
      public boolean isIndexWeekdayOrWeekend(int index) {
        if ((index==5) || (index==6))
          return false;
        return true;
      }

    };
    
    public abstract int[] getIDs();
    
    public abstract int getIndexFromDayID(int dowSunEq1);
    
    public abstract boolean isIndexWeekdayOrWeekend(int index);
  }
  
  
  
  
  public enum WorkdayPatternEnum {
    westernworld() {

      @Override
      public IntPredicate getWorkday() {
        return new WesternWorldWorkday();
      }
      
      final class WesternWorldWorkday implements IntPredicate {
        @Override
        public boolean test(int dowSunEq1) {
          return (dowSunEq1 > 1) && (dowSunEq1 < 7);
        }
      }
    },
    
    middleeast() {

      @Override
      public IntPredicate getWorkday() {
        return new MEWorkday();
      }
      
      final class MEWorkday implements IntPredicate {
        @Override
        public boolean test(int dowSunEq1) {
          return (dowSunEq1 >= 1) && (dowSunEq1 < 6);
        }
      }
    };
    
    /**
     * 
     * @return A function to compute whether a day is a workday. The first parameter is the day of week, where Sunday=1
     */
    public abstract IntPredicate getWorkday();
  }

}
