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
package geocounts.traffic.formats.operators;

import java.util.function.Consumer;

import geocounts.traffic.formats.IdDesc;

public class DelimitedLineBuilder {
  
  public interface ConsumerWithLength extends Consumer<String> {
    public int length();
    public int numberOfFields();
  }
  
  public static ConsumerWithLength createCountedFrom(String delim) {
    return new StringDelimitedByCount(delim);
  }
  
  public static ConsumerWithLength createCountedFrom(char delim) {
    return new CharDelimitedByCount(delim);
  }
  
  public static ConsumerWithLength createFromLength(String delim) {
    return new AppendStrings(delim);
  }
  
  public static ConsumerWithLength createFromLengthSpaceDelimited() {
    return new AppendStrings(" ");
  }
  
  public static CountedAsQuotedStrings createCountedAsQuotedStringsFrom(char delim, String replaceNull) {
    return new CountedAsQuotedStrings(delim, replaceNull);
  }
  
  public static ConsumerWithLength createForSQLParams() {
    return new AppendStringParamsIgnoreNulls('\'');
  }
  
  public static String asSQLParams(Iterable<String> params) {
    AppendStringParamsIgnoreNulls result = new AppendStringParamsIgnoreNulls('\'');
    result.appendAll(params);
    return result.toString();
  }
  
  public static String asSQLParams(String[] params) {
    AppendStringParamsIgnoreNulls result = new AppendStringParamsIgnoreNulls('\'');
    result.appendAll(params);
    return result.toString();
  }
  
  public static String asQuotedStrings(char delim, Iterable<?> values) {
    CountedAsQuotedStrings result = createCountedAsQuotedStringsFrom(delim, "");
    for (Object v: values)
      result.addObject(v);
    return result.toString();
  }

  public static String arrayToString(char delim, Object[] fields) {
    ConsumerWithLength result = createCountedFrom(delim);
    for (Object f: fields)
      result.accept(f != null ? f.toString() : null);
    return result.toString();
  }
  
  public static String arrayToString(char delim, Iterable<? extends Object> fields) {
    ConsumerWithLength result = createCountedFrom(delim);
    for (Object f: fields)
      result.accept(f != null ? f.toString() : null);
    return result.toString();
  }
  
  public static String idsToString(String delim, Iterable<? extends IdDesc> items) {
    ConsumerWithLength result = createCountedFrom(delim);
    for (IdDesc item: items) {
      result.accept(item.getId());
    }
    return result.toString();
  }
  
  public static String descriptionsToString(String delim, IdDesc... items) {
    ConsumerWithLength result = createCountedFrom(delim);
    for (IdDesc item: items) {
      result.accept(item.getDescription());
    }
    return result.toString();
  }
  
  
  public abstract static class ConsumerWithLengthImpl implements ConsumerWithLength {
    protected final StringBuilder result = new StringBuilder();
    protected int fields;

    @Override
    public final int numberOfFields() {
      return fields;
    }

    @Override
    public final String toString() {
      return result.toString();
    }

    @Override
    public final int length() {
      return result.length();
    }
  }
  
  static abstract class AbstractCountedAppender extends ConsumerWithLengthImpl {
    protected abstract void appendDelimiter();
    
    @Override
    public void accept(String value) {
      if (fields > 0)
        appendDelimiter();
      fields++;
      if (value != null)
        result.append(value);
    }
  }
  
  static class StringDelimitedByCount extends AbstractCountedAppender {
    private final String delim;
    public StringDelimitedByCount(String DELIM) {
      delim = DELIM;
    }

    @Override
    protected void appendDelimiter() {
      result.append(delim);
    }
  }
  
  static class CharDelimitedByCount extends AbstractCountedAppender {
    private final char delim;
    public CharDelimitedByCount(char DELIM) {
      delim = DELIM;
    }

    @Override
    protected void appendDelimiter() {
      result.append(delim);
    }
  }
  
  static class AppendStrings extends ConsumerWithLengthImpl {
    private final String DELIM;
    
    public AppendStrings(String delim) {
      DELIM = delim;
    }
    
    @Override
    public void accept(String value) {
      if (value == null)
        return;
      if (result.length() > 0)
        result.append(DELIM);
      result.append(value);
      fields++;
    }
  }
  
  /**
   * Used for SQL
   */
  public static class AppendStringParamsIgnoreNulls extends ConsumerWithLengthImpl {
    private final char QUOTEMARK;
    private final String FIELD_DELIM = ", ";
    
    AppendStringParamsIgnoreNulls(char quotemark) {
      QUOTEMARK = quotemark;
    }
    
    public void appendAll(String[] values) {
      for (int i=0; i<values.length; i++)
        accept(values[i]);
    }
    
    public void appendAll(Iterable<String> values) {
      for (String value: values)
        accept(value);
    }
    
    @Override
    public void accept(String value) {
      if (value == null)
        return;
      
      if (result.length() > 0)
        result.append(FIELD_DELIM);
      result.append(QUOTEMARK);
      result.append(value);
      result.append(QUOTEMARK);
      
      fields++;
    }
  }
  
  public static class CountedAsQuotedStrings extends CharDelimitedByCount {
    private final String replaceNull;
    protected CountedAsQuotedStrings(char DELIM, String replaceNull) {
      super(DELIM);
      this.replaceNull = replaceNull;
    }

    public void addObject(Object v) {
      if (v != null) {
        if (v instanceof String) {
          StringBuilder field = new StringBuilder();
          field.append('"');
          field.append((String)v);
          field.append('"');
          this.accept(field.toString());
        } else
          this.accept(v.toString());
      } else
        this.accept(replaceNull);
    }
  }
}
