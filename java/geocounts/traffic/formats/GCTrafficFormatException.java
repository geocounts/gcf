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

public class GCTrafficFormatException extends Exception {
  private static final long serialVersionUID = 1L;

  public final Object caller;
  public final Object context;
  public GCTrafficFormatException(Object _caller, String message, Object _context) {
    super(message);
    this.caller = _caller;
    if (_context instanceof Throwable) {
      super.initCause((Throwable)_context);
      this.context = null;
    } else
      this.context = _context;
  }
  
  @Override
  public String getMessage() {
    StringBuilder result = new StringBuilder();
    if (caller != null) {
      result.append('[');
      result.append(caller.toString());
      result.append(']');
    }
    
    if (super.getMessage() != null)
      result.append(super.getMessage());
    
    if (context != null)
      if ((context instanceof String) || (context instanceof Number) || (context instanceof Enum)) {
        result.append(": ");
        result.append(context.toString());
      }
    
    return result.toString();
  }
}
