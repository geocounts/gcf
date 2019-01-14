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

/**
 * A utility class to convert text back and forth
 * Encodes the delimiter ',' into an escaped sequence: \,
 */
public class TextHeaderEscape {
  public static final String XX = "XX:";
  
  public static final TextHeaderEscape instance = new TextHeaderEscape();
  
  public enum DChars {
    /**
     * A comma ,
     */
    DELIM(','),
    /**
     * A backslash \
     */
    ECHAR('\\'),
    /**
     * A line feed \n 0x0A
     */
    LFCHAR('\n', 'n'),
    /**
     * A carriage return \r 0x0D
     */
    CRCHAR('\r', 'r');
    
    public final char raw;
    public final char escaped;
    private DChars(char c) {
      this.raw = c;
      this.escaped = c;
    }
    private DChars(char c, char e) {
      this.raw = c;
      this.escaped = e;
    }
  }
  
  /*
   * A carriage return 0x0D
   *
  private char CRCHAR = '\r';
  */
  
  private TextHeaderEscape() {}
  
  public void encode(String value, StringBuilder result) {
    if (value == null)
      return;

    for (int c=0; c<value.length(); c++) {
      if (!didEscape(value.charAt(c), result))
        result.append(value.charAt(c));
    }
  }
  
  public String decode(String data) {
    int pos = data.indexOf(DChars.ECHAR.raw);
    if (pos < 0)
      return data;

    StringBuilder result = new StringBuilder();
    for (int c=0; c<data.length(); c++) {
      if (data.charAt(c) == DChars.ECHAR.raw) {
        c++;
        if (c == data.length())
          break;
      }
      result.append(data.charAt(c));
    }
    
    return result.toString();
  }

  private boolean didEscape(char c, StringBuilder result) {
    if (c == DChars.DELIM.raw) {
      result.append(DChars.ECHAR.raw);
      result.append(DChars.DELIM.escaped);
      return true;
    }
    if (c == DChars.ECHAR.raw) {
      result.append(DChars.ECHAR.raw);
      result.append(DChars.ECHAR.escaped);
      return true;
    }
    if (c == DChars.LFCHAR.raw) {
      result.append(DChars.ECHAR.raw);
      result.append(DChars.LFCHAR.escaped);
      return true;
    }
    if (c == DChars.CRCHAR.raw) { // ignore this one
//      result.append(DChars.ECHAR.raw);
//      result.append(DChars.CRCHAR.escaped);
      return true;
    }
    return false;
  }
  
  public String[] splitDecode(String valueWithEscapes) {
    ArrayList<String> fields = new ArrayList<String>();
    char[] chars = valueWithEscapes.toCharArray();
    StringBuilder currentField = new StringBuilder();
    boolean isEscaped = false;
    for (int i=0; i<chars.length; i++) {
      if (chars[i] == DChars.DELIM.raw) { // if this is the comma
        if (isEscaped) {                // if the comma is being 'escaped', meaning its part of the text, not a separate field
          currentField.append(DChars.DELIM.raw);
          isEscaped = false;
        } else {
          fields.add(decode(currentField.toString()));  // the comma means we have ended the current field
          currentField = new StringBuilder();
        }
      } else {
        currentField.append(chars[i]);
        isEscaped = (chars[i] == DChars.ECHAR.raw);
      }
    }
    fields.add(decode(currentField.toString()));  // get the last field

    if (fields.size() == 0)
      return new String[0];
    String[] result = new String[fields.size()];
    fields.toArray(result);
    return result;
  }
}
