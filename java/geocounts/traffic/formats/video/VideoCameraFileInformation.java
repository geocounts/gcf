package geocounts.traffic.formats.video;

import geocounts.traffic.formats.Timeframe;

public class VideoCameraFileInformation extends Timeframe implements VideoCameraEvent {

  public VideoCameraFileInformation(String fileName) {
    this.label = fileName;
  }
  
  public VideoCameraFileInformation(java.io.File f) {
    this(f.getName());
    setLength(f.length());
  }

  @Override
  public final String toString() {
    return label;
  }
  
  public final String getName() {
    return label;
  }
  
  public void setLength(long value) {
    this.lengthBytes = value;
  }
  
  public final void set(String cameraID, long startTime, long durationMSecs) {
    this.cameraID = cameraID;
    this.set(startTime, startTime+durationMSecs);
  }
  
  private String cameraID;
  @Override
  public String getCameraID() {
    return cameraID;
  }

  @Override
  public final void setCameraID(String value) {
    cameraID = value;
  }
  
  private long lengthBytes = -1;
  public long length() {
    return lengthBytes;
  }
  
  public final long getLength() {
    return length();
  }
}
