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
 * ISO 3166-1 alpha-2 codes
 * @see https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2
 * @see java.util.Locale.IsoCountryCode
 */
public enum CountryCode implements AbstractRegionCode {
  AD, 
  AE("United Arab Emirates", 24.333921, 54.512346), 
  AF, 
  AG, 
  AI, 
  AL, 
  AM, 
  AO, 
// AQ("Antarctica"), 
  AR, 
  AS, 
  AT("Austria", 47.610010, 14.757256), 
  AU("Australia", -25.788806, 134.364293) {
    @Override
    public String getCurrency() {
      return "$AUD";
    }
    
    @Override
    public int getTZMinutes(String stateCode) {
      return 10*60;
    }

    @Override
    public AbstractRegionCode[] getStateCodes() {
      return new AbstractRegionCode[] {AbstractRegionCodeImpl.valueOf("QLD"), AbstractRegionCodeImpl.valueOf("NSW"), AbstractRegionCodeImpl.valueOf("VIC"), AbstractRegionCodeImpl.valueOf("TAS"), AbstractRegionCodeImpl.valueOf("SA"), AbstractRegionCodeImpl.valueOf("WA"), AbstractRegionCodeImpl.valueOf("NT"), AbstractRegionCodeImpl.valueOf("ACT")};
    }
  }, 
  AW, 
  AX, 
  AZ, 
  BA, 
  BB, 
  BD, 
  BE, 
  BF, 
  BG, 
  BH, 
  BI, 
  BJ, 

  BM, 
  BN, 
  BO, 
// BQ("Bonaire, Sint Eustatius and Saba"), 
  BR, 
  BS, 
  BT, 
  BV, 
  BW, 
  BY, 
  BZ,
  CA("Canada", 55.121810, -96.472367), 
  CC, 
  CD, 
  CF, 
  CG, 
  CH("Switzerland", 46.957360, 8.327395), 
  CI, 
  CK, 
  CL, 
  CM, 
  CN("China", 33.946823, 108.554217) {
    @Override
    public String getCurrency() {
      return "CNY";
    }
    @Override
    public int getTZMinutes(String stateCode) {
      return 8*60;
    }
  }, 
  CO("Colombia", 4.270371, -72.887282) {
    @Override
    public String getCurrency() {
      return "COP";
    }
    
    @Override
    public int getTZMinutes(String stateCode) {
      return -5*60;
    }
  }, 
  CR, 
  CU, 
  CV, 

  CX, 
  CY, 
  CZ,
  DE, 
  DJ, 
  DK, 
  DM, 
  DO, 
  DZ,
  EC, 
  EE, 
  EG, 
  EH, 
  ER, 
  ES("Spain", 40.4169, -3.703) {
    @Override
    public int getTZMinutes(String stateCode) {
      return 1*60;
    }
  }, 
  ET,
  FI, 
  FJ, 
  FK, 
  FM, 
  FO, 
  FR("France", 46.985669, 3.010815) {
    @Override
    public int getTZMinutes(String stateCode) {
      return 2*60;
    }
  },
  GA, 
  GB("Great Britain", 53.125085, -1.241097), 
  GD, 
  GE, 
  GF, 
// GG("Guernsey"), 
  GH("Ghana", 7.756166, -0.995665), 
  GI, 
  GL, 
  GM, 
  GN, 
  GP, 
  GQ, 
  GR, 
  GS, 
  GT, 
  GU, 
  GW, 
  GY,
  HK("Hong Kong", 22.408185, 114.166250), 
  HM, 
  HN, 
  HR, 
  HT, 
  HU,
  ID, 
  IE, 
  IL("Israel", 31.703135, 35.049844), 
// IM("Isle of Man"), 
  
  /**
   * India is composed of 28 states and 8 union territories (including a national capital territory).
   * https://en.wikipedia.org/wiki/Administrative_divisions_of_India
   */
  IN("India", 22.213677, 80.142793) {
    @Override
    public int getTZMinutes(String stateCode) {
      return 5*60 + 30;  // https://www.timeanddate.com/time/zone/india
    }
/*
States
 Name              Code  Capital
---------------------------------------------------------------------------------
1 Andhra Pradesh    AP    Visakhapatnam (Administrative) Amaravati (Legislative)
2 Arunachal Pradesh AR    Itanagar
3 Assam             AS    Dispur
4 Bihar             BR    Patna
5 Chhattisgarh      CG    Raipur[a]
6 Goa               GA    Panaji
7 Gujarat           GJ    Gandhinagar
8 Haryana           HR    Chandigarh (shared with Punjab, also a Union Territory)
9 Himachal Pradesh  HP    Shimla (summer), Dharamshala (winter)
10 Jharkhand         JH    Ranchi
11 Karnataka         KA    Bangalore
12 Kerala            KL    Thiruvananthapuram
13 Madhya Pradesh    MP    Bhopal
14 Maharashtra       MH    Mumbai (summer), Nagpur (winter)
15 Manipur           MN    Imphal
16 Meghalaya         ML    Shillong
17 Mizoram           MZ    Aizawl
18 Nagaland          NL    Kohima
19 Odisha            OD    Bhubaneshwar
20 Punjab            PB    Chandigarh (shared with Haryana, also a Union Territory)
21 Rajasthan         RJ    Jaipur
22 Sikkim            SK    Gangtok
23 Tamil Nadu        TN    Chennai
24 Telangana[8]      TS    Hyderabad
25 Tripura           TR    Agartala
26 Uttar Pradesh     UP    Lucknow
27 Uttarakhand       UT    Gairsain (summer), Dehradun (winter)
28 West Bengal       WB    Kolkata

Union territories
 Name                                      Code  Capital
---------------------------------------------------------------------------------
A Andaman and Nicobar Islands               AN    Port Blair
B Chandigarh                                CH    Chandigarh (also the capital of Haryana and Punjab)
C Dadra and Nagar Haveli and Daman and Diu  DD    Daman
D Jammu and Kashmir                         JK    Srinagar (summer), Jammu (winter)
E Ladakh                                    LA    Leh and Kargil
F Lakshadweep                               LD    Kavaratti
G National Capital Territory of Delhi       DL    New Delhi
H Puducherry                                PY    Pondicherry
*/
  }, 
  IO, 
  IQ, 
  IR, 
  IS, 
  IT("Italy", 42.476454, 13.294001),
// JE("Jersey"), 
  JM, 
  JO, 
  JP("Japan", 37.447435, 139.696374),
  KE("Kenya", 0.531653, 38.324378), 
  KG, 
  KH, 
  KI, 
  KM, 
  KN, 
  KP, 
  KR, 
  KW, 
  KY, 
  KZ,
  LA, 
  LB("Lebanon", 33.905176, 35.837402), 
  LC, 
  LI, 
  LK, 
  LR, 
  LS, 
  LT,
  LU, 
  LV, 
  LY,
  MA, 
  MC, 
  MD, 
  ME, 
// MF("Saint Martin"), 
  MG, 
  MH, 
  MK, 
  ML, 
  MM, 
  MN, 
  MO, 
  MP, 
  MQ, 
  MR, 
  MS, 
  MT, 
  MU, 
  MV, 
  MW, 
  MX("Mexico", 23.269648, -101.538914), 
  MY, 
  MZ,
  NA, 
  NC, 
  NE, 
  NF, 
  NG("Nigeria", 9.612712, 8.719145), 
  NI, 
  NL("Netherlands", 52.194897, 5.713814), 
  NO, 
  NP, 
  NR, 
  NU, 
  NZ("New Zealand", -41.939658, 172.964044) {
    @Override
    public String getCurrency() {
      return "$NZD";
    }
    
    @Override
    public int getTZMinutes(String stateCode) {
      return 12*60;
    }
  },
  OM,
  PA, 
  PE, 
  PF, 
  PG, 
  PH("Philippines", 12.217582, 123.359668), 
  PK("Pakistan", 29.5, 69.1) {
    @Override
    public String getCurrency() {
      return "PKR";
    }
  }, 
  PL,
  PM, 
  PN, 
  PR, 
  PS, 
  PT, 
  PW, 
  PY,
  QA("Qatar", 25.259389, 51.235713),
  RE, 
  RO, 
  RS, 
  RU, 
  RW,
  SA, 
  SB, 
  SC, 
  SD, 
  SE, 
  SG, 
  SH, 
  SI("Slovenia", 46.043147, 14.588327), 
  SJ, 
  SK, 
  SL, 
  SM, 
  SN, 
  SO, 
  SR, 
// SS("South Sudan"), 
  ST, 
  SV,
// SX("Sint Maarten"), 
  SY("Syria", 35.280391, 38.610962), 
  SZ,
  TC, 
  TD, 
  TF, 
  TG, 
  TH, 
  TJ, 
  TK, 
  TL, 
  TM, 
  TN, 
  TO, 
  TR, 
  TT, 
  TV, 
  TW, 
  TZ,
  UA, 
  UG, 
  UM, 
  US("United States of America", 40.478818, -95.129182) {
    @Override
    public String getCurrency() {
      return "$USD";
    }
  }, 
  UY, 
  UZ,
  VA, 
  VC, 
  VE, 
  VG, 
  VI, 
  VN, 
  VU,
  WF, 
  WS,
  YE("Yemen", 15.567405, 48.009560), 
  YT,
  ZA("South Africa", -30.770448, 24.513690), 
  ZM("Zambia", -14.593322, 28.235406) {
    /**
     * https://en.wikipedia.org/wiki/Zambian_kwacha
     */
    @Override
    public String getCurrency() {
      return "ZMW";
    }
  }, 
  ZW;
  
  private CountryCode() {
    this(null, 0, 0);
  }
  
  public final String en;
  
  @Override
  public String getDescription() {
    return en != null ? en : name();
  }
  
  private CountryCode(String en, double lat, double lng) {
    this.en = en;
    coordinate = new LatLongCoordinate(lat, lng);
  }
  
  private final LatLongCoordinate coordinate;
  
  /**
   * https://en.wikipedia.org/wiki/ISO_4217
   * @return Currency name
   */
  public String getCurrency() {
    return "$" + name();
  }
  
  public String toISO_3166_Wiki() {
    return "https://en.wikipedia.org/wiki/ISO_3166-2:" + name();
  }
  
  public String getFlagIcon() {
    return "https://geocounts.com/static/fsm/" + name().toLowerCase() + ".png";
  }

  @Override
  public LatLongCoordinate getCoordinate() {
    return coordinate;
  }

  /**
   * The standard time zone
   * https://www.timeanddate.com/time/standard-time.html
   * @param stateCode
   */
  public int getTZMinutes(String stateCode) {
    return 0;
  }

  @Override
  public String getId() {
    return name();
  }
  
  public AbstractRegionCode[] getStateCodes() {
    return new AbstractRegionCode[0];
  }
}