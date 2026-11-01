package com.campusconnect;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import org.springframework.boot.jackson.JsonComponent;
@JsonComponent
public class TrimJsonConfig extends JsonDeserializer<String> {
 @Override public String deserialize(JsonParser parser,DeserializationContext context) throws IOException {
  if(!parser.hasToken(com.fasterxml.jackson.core.JsonToken.VALUE_STRING)) return (String)context.handleUnexpectedToken(String.class,parser);
  String value=parser.getText();
  if(value.length()>10000) throw com.fasterxml.jackson.databind.JsonMappingException.from(parser,"Text exceeds limit");
  return value.strip();
 }
}
