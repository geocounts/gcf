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

public enum GCFieldType {
  /**
   * A String
   */
  STRING {
    @Override
    public boolean isNumeric() {
      return false;
    }

    @Override
    public boolean isInteger() {
      return false;
    }

    @Override
    public Object fromString(String value) {
      return value;
    }
  },
  
  /**
   * 64 bit signed Integer
   */
  INT {
    @Override
    public boolean isNumeric() {
      return true;
    }

    @Override
    public boolean isInteger() {
      return true;
    }

    @Override
    public Object fromString(String value) {
      if (value == null)
        return null;
      return (int)Double.parseDouble(value);
    }
  },
  
  /**
   * 32 bit signed Long
   */
  LONG {
    @Override
    public boolean isNumeric() {
      return true;
    }

    @Override
    public boolean isInteger() {
      return true;
    }

    @Override
    public Object fromString(String value) {
      if (value == null)
        return null;
      return (long)Double.parseDouble(value);
    }
  },
  /**
   * 64 bit signed Double
   */
  DOUBLE {
    @Override
    public boolean isNumeric() {
      return true;
    }

    @Override
    public boolean isInteger() {
      return false;
    }

    @Override
    public Object fromString(String value) {
      if (value == null)
        return null;
      return Double.parseDouble(value);
    }
  },
  
  /**
   * An enumerated String
   */
  ENUM_STRING {
    @Override
    public boolean isNumeric() {
      return false;
    }

    @Override
    public boolean isInteger() {
      return false;
    }

    @Override
    public Object fromString(String value) {
      return value;
    }
  },
  
  /**
   * An enumerated Integer
   */
  ENUM_INT {
    @Override
    public boolean isNumeric() {
      return true;
    }

    @Override
    public boolean isInteger() {
      return true;
    }

    @Override
    public Object fromString(String value) {
      if (value == null)
        return null;
      return (int)Double.parseDouble(value);
    }
  };
  
  public abstract boolean isNumeric();
  
  public abstract boolean isInteger();

  public abstract Object fromString(String value);
}
