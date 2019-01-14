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
 * MAC, Media Access Control, address is a globally unique identifier assigned to network devices, and therefore it is often referred to as hardware or physical address. 
 * MAC addresses are 6-byte (48-bits) in length, and are written in MM:MM:MM:SS:SS:SS format.<br />
 * The leftmost 6 digits (24 bits) called a "{@link #getPrefix() prefix}" is associated with the adapter manufacturer. 
 * Each vendor registers and obtains MAC prefixes as assigned by the IEEE. Vendors often possess many prefix numbers associated with their different products. 
 * For example, the prefixes 00:13:10, 00:25:9C and 68:7F:74 (plus many others) all belong to Linksys (Cisco Systems).<br />
 * The rightmost digits of a MAC address represent an {@link #getIdentificationNumber() identification number} for the specific device. 
 * Among all devices manufactured with the same vendor prefix, each is given their own unique 24-bit number. 
 * Note that hardware from different vendors may happen to share the same device portion of the address.<br />
 * 
 * @author scropley
 * @see https://en.wikipedia.org/wiki/MAC_address
 * @see https://www.lifewire.com/introduction-to-mac-addresses-817937
 * @see <a href="http://aruljohn.com/mac/A0CBFD">Samsung Electronics Co.,Ltd</a>
 * @see <a href="http://aruljohn.com/mac/8489AD">Apple, Inc.</a>
 */
public class MACAddress {
  /**
   * 
   * @param hex Takes the format: D0.03.4B.3B.53.EC or D0034B3B53EC
   */
  public static long toLong(String hex) {
    if (hex.length() == 12)
      return fromHex12(hex);
    if (hex.length() == 17)
      return fromHex17(hex);
    throw new java.lang.NumberFormatException(hex);
  }
  
  public static long fromHex12(String hex12) {
    return Long.valueOf(hex12, 16);
  }
  
  public static long fromHex17(String hex17) {
    StringBuilder hex = new StringBuilder(hex17.substring(0, 2));
    hex.append(hex17.substring(3, 5));
    hex.append(hex17.substring(6, 8));
    hex.append(hex17.substring(9, 11));
    hex.append(hex17.substring(12, 14));
    hex.append(hex17.substring(15, 17));
    return Long.valueOf(hex.toString(), 16);
  }
  
  public MACAddress(String hex_6) {
    this(toLong(hex_6));
  }

  public MACAddress(long mac) {
    value = mac;
  }
  
  private final long value;
  public long getValue() {
    return value;
  }
  
  public long getPrefix() {
    return value >> 24;
  }
  
  public long getIdentificationNumber() {
    return value & 0xFFFFFF;
  }
}
