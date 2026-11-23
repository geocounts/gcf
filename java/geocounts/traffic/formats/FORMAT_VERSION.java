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
 * Versioning and initialization
 */
public final class FORMAT_VERSION {
  public final static int MAJOR_VERSION = 2;
  
  /**
   * Version of the GC Format API
   */
  public final static String RAWCOUNT_VERSION = "1.52";
  /**
   * Version of this java API code
   */
  public final static double API_VERSION = 2.30;
  
  public static void addWKClassificationSystemNameSynonym(String synonym, WKClassificationSystemName value) {
    WKClassificationSystemName.synonyms_lwrcase.put(synonym.toLowerCase(), value);
  }

}
