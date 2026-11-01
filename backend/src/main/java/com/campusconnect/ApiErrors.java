package com.campusconnect;
import java.io.IOException;
import java.util.Map;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
@RestControllerAdvice
public class ApiErrors {
 public static void write(HttpServletResponse res,int code,String message) throws IOException {
  res.setStatus(code); res.setContentType("application/json"); res.getWriter().write("{\"status\":"+code+",\"message\":\""+message+"\"}");
 }
 private ResponseEntity<?> error(int status,String message) {return ResponseEntity.status(status).body(Map.of("status",status,"message",message));}
 @ExceptionHandler(ResponseStatusException.class) ResponseEntity<?> status(ResponseStatusException ex) {
  int status=ex.getStatusCode().value(); return error(status,status==404?"Resource not found":"Invalid request");
 }
 @ExceptionHandler({org.springframework.web.bind.MethodArgumentNotValidException.class,
  org.springframework.http.converter.HttpMessageNotReadableException.class,
  org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class,
  jakarta.validation.ConstraintViolationException.class,org.springframework.web.method.annotation.HandlerMethodValidationException.class,
  org.springframework.web.bind.MissingServletRequestParameterException.class})
 ResponseEntity<?> invalid(Exception ex) {return error(400,"Invalid request; check field values and limits");}
 @ExceptionHandler(DataIntegrityViolationException.class) ResponseEntity<?> conflict(Exception ex) {return error(409,"Resource conflicts with existing data");}
 @ExceptionHandler(org.springframework.web.servlet.NoResourceFoundException.class) ResponseEntity<?> missing(Exception ex) {return error(404,"Resource not found");}
 @ExceptionHandler(org.springframework.web.HttpRequestMethodNotSupportedException.class) ResponseEntity<?> method(Exception ex) {return error(405,"Method not allowed");}
 @ExceptionHandler(Exception.class) ResponseEntity<?> unexpected(Exception ex) {
  LoggerFactory.getLogger(ApiErrors.class).error("Unhandled API failure",ex); return error(500,"Unable to complete request");
 }
}
