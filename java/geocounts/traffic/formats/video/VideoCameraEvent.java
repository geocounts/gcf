package geocounts.traffic.formats.video;

import geocounts.traffic.formats.TimedObject;

public interface VideoCameraEvent extends TimedObject {

  /**
   * @see VideoCameraDef#getCameraID()
   */
  public String getCameraID();

  public void setCameraID(String value);
}
