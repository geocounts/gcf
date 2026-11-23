package geocounts.traffic.formats;

public interface AbstractRegionCode extends CoordinateObject, IdDesc {

}

class AbstractRegionCodeImpl implements AbstractRegionCode {
  private final String code;
  private final LatLongCoordinate coordinate = new LatLongCoordinate();
  AbstractRegionCodeImpl(String c) {
    code = c;
  }
  
  @Override
  public LatLongCoordinate getCoordinate() {
    return coordinate;
  }

  @Override
  public String getId() {
    return code;
  }

  @Override
  public String getDescription() {
    return code;
  }

  public static AbstractRegionCodeImpl valueOf(String code) {
    AbstractRegionCodeImpl result = new AbstractRegionCodeImpl(code);
    return result;
  }
  
}