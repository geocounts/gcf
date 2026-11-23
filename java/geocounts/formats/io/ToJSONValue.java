package geocounts.formats.io;

import java.util.function.Function;

public class ToJSONValue implements Function<Object, String> {
  private final char QUOTE;
  public ToJSONValue() {
    this('"');
  }
  
  public ToJSONValue(char c) {
    this.QUOTE = c;
  }
  
  /**
   * 
   * @param value Must never be null
   * @return A JSON string with the nasty chars escaped
   */
  public static String toJSONString(String s) {
    StringBuilder result = new StringBuilder();
    for (int i=0; i<s.length(); i++) {
      if (s.charAt(i) < ' ') {
        result.append(' ');
      } else if (s.charAt(i) == '"') {   // double quote
        result.append('\\');
        result.append('"');
      } else if (s.charAt(i) == '\\') {  // backslash
        result.append("\\\\");
      } else if (s.charAt(i) == '\n') {  // new line    ... OR replace('\n', ' ')
        result.append("\\n");
      } else if (s.charAt(i) == '\r') {  // carriage return
        result.append("\\r");
      } else if (s.charAt(i) == '\t') {  // tab
        result.append("\\t");
      } else
        result.append(s.charAt(i));
    }
    return result.toString();
  }
  
  @Override
  public String apply(Object value) {
    if (value == null)
      return "null";
    if (value instanceof Number)
      return value.toString();
    if (value instanceof Boolean)
      return value.toString();
    if (value instanceof Enum) {
      Enum e = (Enum)value;
      return QUOTE + toJSONString(e.name()) + QUOTE;
    }
    return QUOTE + toJSONString(value.toString()) + QUOTE;
  }
}