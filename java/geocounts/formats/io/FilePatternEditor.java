/*
 * Copyright 2014 Transmetric America Inc. All rights reserved.
 * Transmetric America Inc PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package geocounts.formats.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;


/**
 * Operations to search and replace text data.
 * Use this when a bunch of files have been systematically corrupted.
 * 
 * @author scropley
 *
 */
public class FilePatternEditor {
  private int parseStrings(Reader reader, ChangeHeaderReceiver receiver) throws IOException {
    BufferedReader r = reader instanceof BufferedReader ? (BufferedReader)reader : new BufferedReader(reader);
    
    String line = r.readLine();
    int lineNumber = 0;
    while (line != null) {
      lineNumber++;
      boolean shouldContinue = receiver.receive(lineNumber, line);
      if (!shouldContinue)
        return lineNumber;
      line = r.readLine();
    }
    return lineNumber;
  }
  
  /**
   * Search for a string within a given header and replace
   * @param f The GC file to read
   * @param headerRecID The 2-digit header record
   * @param matchStr The string to match
   * @param caseSensitiveMatch Whether the match string should be considered case-sensitive. True means the match is case-sensitive. False means ignore case.
   * @param replace The string that replaces the match
   * @param writeToDir Where to write the new file. If this is null, no files are written.
   * @param checkExists Check if the new file exists and throw an error if it does
   * @return Number of changes to make. If zero, nothing will be written
   * @throws Exception
   */
  public int replaceBrokenHeaderString(File f, String headerRecID, String matchStr, boolean caseSensitiveMatch, String replace, File writeToDir, boolean checkExists) throws Exception {
    ChangeHeaderReceiver receiver = new ChangeHeaderReceiver(headerRecID, matchStr, caseSensitiveMatch, replace);

    FileReader fileReader = new FileReader(f);
    try {
      parseStrings(fileReader, receiver);
    } finally {
      fileReader.close();
    }
    
    
    if (writeToDir != null)
      if (receiver.changes > 0) {
        File result = new File(writeToDir, f.getName());
        print(receiver.allLines, result, checkExists);
      }
    return receiver.changes;
  }
  
  private void print(List<String> lines, File toFile, boolean checkExists) throws Exception {
    if (checkExists && toFile.exists())
      throw new IOException("File exists " + toFile.getParent() + "/" + toFile.getName());
    
    PrintWriter out = new PrintWriter(toFile);
    try {
      for (String line: lines)
        out.println(line);
      out.flush();
    } finally {
      out.close();
    }
  }
  
  class ChangeHeaderReceiver {
    private final String headerRecID;
    private final String match;
    private final boolean caseSensitiveMatch;
    private final String replace;
    private int changes = 0;
    
    private final ArrayList<String> allLines = new ArrayList<String>();
    
    ChangeHeaderReceiver(String headerRecID, String match, boolean caseSensitiveMatch, String replace) {
      this.headerRecID = headerRecID;
      this.match = match;
      this.caseSensitiveMatch = caseSensitiveMatch;
      this.replace = replace;
    }

    public boolean receive(int lineNumber, String line) {
      if ((line.length() > 3) && (line.charAt(2) == ' ') && line.startsWith(headerRecID)) {
        int pos1 = 0;
        if (!caseSensitiveMatch) {
          pos1 = line.toLowerCase().indexOf(match.toLowerCase(), 3);
        } else
          pos1 = line.indexOf(match, 3);
        
        if (pos1 > 0) {
          int pos2 = pos1 + match.length();
          line = line.substring(0, pos1) + replace + line.substring(pos2);
          changes++;
        }
      }
      allLines.add(line);
      return true;
    }
    
  }
}
