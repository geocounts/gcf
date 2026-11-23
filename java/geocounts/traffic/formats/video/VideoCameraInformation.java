package geocounts.traffic.formats.video;

import java.util.ArrayList;

import geocounts.traffic.formats.CoordinateAngle;
import geocounts.traffic.formats.CoordinateObject;
import geocounts.traffic.formats.IdDesc;
import geocounts.traffic.formats.LatLongCoordinate;


public class VideoCameraInformation implements CoordinateObject, IdDesc {
  private final ArrayList<VideoCameraFileInformation> files = new ArrayList<VideoCameraFileInformation>();
  private final LatLongCoordinate coord = new LatLongCoordinate();
  
  private final String id;
  private String description;
  
  public VideoCameraInformation(String id) {
    this.id = id;
  }
  
  @Override
  public String getId() {
    return id;
  }
  
  public void setDescription(String value) {
    this.description = value;
  }

  @Override
  public String getDescription() {
    return description;
  }
  
  @Override
  public LatLongCoordinate getCoordinate() {
    return coord;
  }
  
  /**
   * This is the Pan direction
   * @return Pan direction in degrees
   */
  public int facingDegrees() {
    return coord.directionDegrees();
  }
  
  /**
   * Field of view is how many degrees the camera can see. This is also the 'zoom' level
   */
  public final CoordinateAngle FOV = new CoordinateAngle(0);
  
  /**
   * Rotation Up or down
   */
  public final CoordinateAngle tilt = new CoordinateAngle(0);
  
  /**
   * Horizontal alignment.
   */
  public final CoordinateAngle roll = new CoordinateAngle(0);
  
  
  public VideoCameraFileInformation add(String fileName, long lengthBytes, long startTime, long durationMSecs) {
    VideoCameraFileInformation result = new VideoCameraFileInformation(fileName);
    result.setLength(lengthBytes);
    result.set(getId(), startTime, durationMSecs);
    this.add(result);
    return result;
  }
  
  public void add(VideoCameraFileInformation f) {
    files.add(f);
  }
  
  public Iterable<VideoCameraFileInformation> fileIterator() {
    return files;
  }
  
  public VideoCameraFileInformation getFile(int index) {
    return files.get(index);
  }
}
