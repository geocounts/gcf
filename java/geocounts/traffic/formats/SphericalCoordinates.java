package geocounts.traffic.formats;

/**
 * https://math.libretexts.org/Courses/Mount_Royal_University/MATH_2200%3A_Calculus_for_Scientists_II/7%3A_Vector_Spaces/5.7%3A_Cylindrical_and_Spherical_Coordinates#section_2
 */
public class SphericalCoordinates {
  /**
   * Rho
   * This is the distance from the point to the camera. Its always positive
   */
  private double radius;
  
  /**
   * Horizontal radius (along the ground)
   */
  public double horizontalDistance() {
    double x = getX();
    double y = getY();
    return Math.sqrt(x*x + y*y);
  }
  
  /**
   * Vertical radius (straight up)
   */
  public double verticalDistance() {
    double h = horizontalDistance();
    return Math.sqrt(radius*radius - h*h);
  }
  
  public double getX() {
    double s = Math.cos(verticalAngle.getRadians());
    double c = Math.cos(horizontalAngle.getRadiansUsingEngineeringCoordinates());
    return radius * s * c;
  }
  
  public double getY() {
    double s = Math.cos(verticalAngle.getRadians());
    double c = Math.sin(horizontalAngle.getRadiansUsingEngineeringCoordinates());
    return radius * s * c;
  }
  
  public double getZ() {
    return radius * Math.sin(verticalAngle.getRadians());
  }
  
  /**
   * Angular direction (theta) from North, clockwise
   */
  public final CoordinateAngle horizontalAngle = new CoordinateAngle(0);
  
  /**
   * Angular direction (phi) from facing the horizon. Tilting up towards the sky is positive; pointing directly up is 90 degrees and directly down is -90 degrees
   */
  public final CoordinateAngle verticalAngle = new CoordinateAngle(0);
  
  public void setVertical(double horizontalDist, double verticalDist) {
    radius = Math.sqrt(horizontalDist*horizontalDist + verticalDist*verticalDist);
    
    if ((verticalDist == 0) && (horizontalDist == 0)) {
      verticalAngle.setRadians(0);
      return;
    }
    
    double rad = Math.atan2(verticalDist, horizontalDist);
    verticalAngle.setRadians(rad);
  }
  
  /**
   * 
   * @return {@link XYZPoint} in Camera XZY coordinates: x and z are horizontal, y is vertical (up)
   */
  public XYZPoint toXZYPoint() {
    XYZPoint result = new XYZPoint();
    result.x = getX();
    result.z = getY();
    result.y = getZ();
    return result;
  }
}
