package geocounts.formats.units;

import geocounts.traffic.formats.LatLongCoordinate;

public class OpenLocationCode {
  enum Base20CharsEnum {
    _2,
    _3,
    _4,
    _5,
    _6,
    _7,
    _8,
    _9,
    _C,
    _F,
    _G,
    _H,
    _J,
    _M,
    _P,
    _Q,
    _R,
    _V,
    _W,
    _X;

    public int getRow() {
      return ordinal()/4;
    }
    
    public int getCol() {
      return ordinal()%4;
    }
  }
  
  enum BlockSizeEnum {
    A,
    B,
    C, 
    D,
    E;
    
    public double getFactor() {
      return Math.pow(20, ordinal());
    }
    
    public double getBlockSizeInDegrees() {
      return 1.0/getFactor();
    }
  }
  
  public LatLongCoordinate parse(String line) {
    LatLongCoordinate result = new LatLongCoordinate();
    parse(line, result);
    return result;
  }
  
  public void parse(String line, LatLongCoordinate result) {
    // 6PH57VP3+PR6
    int levels = 0;
    Base20CharsEnum[][] blocks = new Base20CharsEnum[5][2];
    Base20CharsEnum lastBlock = null;
    int length = 0;
    boolean firstOrSecond = true;
    for (int i=0; i<line.length(); i+=2) {
      char c = line.charAt(i);
      if (c == '+') {
        if (length != 8) {
          // this is a shortened code which cant be used!
          throw new RuntimeException("Cannot decode " + line);
        }
      } else {
        Base20CharsEnum b = Base20CharsEnum.valueOf("_" + c);
        if (levels < 5)
          blocks[levels][firstOrSecond ? 0 : 1] = b;
        else
          lastBlock = b;
      }
      
      if (firstOrSecond) {
        firstOrSecond = false;
      } else {
        firstOrSecond = true;
        levels++;
      }
      length++;
    }

    
    for (int level=0; level<levels; level++) {
      Base20CharsEnum lat = blocks[level][0];
      Base20CharsEnum lng = blocks[level][1];
      BlockSizeEnum levelSize = BlockSizeEnum.values()[level];
    }
  }

  public String format(LatLongCoordinate point) {
    return null;
  }
}
