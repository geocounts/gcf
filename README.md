# GCF
The GEOCOUNTS Format

This is used by the traffic counting industry to transmit all kinds of data including  
vehicles, pedestrians, bikes, both on or off road.

See [the specification docs][example].

  [example]: https://geocounts.com/api/format


## To decode:

import java.io.File;  
import geocounts.traffic.formats.RawTrafficData;  
import geocounts.formats.io.RawTrafficDataFileTXTDecoder;  


File theInputFile = new File("path to your GC file.txt");  
RawTrafficData rawdata = RawTrafficDataFileTXTDecoder.loadFromFile(theFile, true);  


## To encode:

import java.io.File;  
import geocounts.traffic.formats.RawTrafficData;  
import geocounts.traffic.formats.RawTrafficDataFilename;  
import geocounts.formats.io.RawTrafficDataFileTXTEncoder;  

RawTrafficDataFilename fileName = new RawTrafficDataFilename();  
fileName.setStationID("THESTATIONID");  
fileName.startDateTime[0] = 2019;  
fileName.startDateTime[1] = 1;  
fileName.startDateTime[2] = 13;  

File dir = new File("C:\\");  
File theOutputFile = new File(dir, fileName.toString());  
RawTrafficDataFileTXTEncoder.writeData(theOutputFile, rawdata);  
