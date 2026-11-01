package com.campusconnect;

import java.io.IOException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/** Bound admin JSON before parsing, including requests sent without Content-Length. */
@Component
@Order(org.springframework.core.Ordered.LOWEST_PRECEDENCE - 10)
public class RequestSizeFilter extends OncePerRequestFilter {
 private static final int MAX_BYTES=65536;
 @Override protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain chain) throws ServletException,IOException {
  if(!request.getRequestURI().startsWith("/api/") || !java.util.Set.of("POST","PUT","PATCH").contains(request.getMethod())) {
   chain.doFilter(request,response);return;
  }
  if(request.getContentLengthLong()>MAX_BYTES){ApiErrors.write(response,413,"Request body too large");return;}
  // Read at most the limit plus one byte. Do not allocate based on untrusted Content-Length.
  byte[] body=request.getInputStream().readNBytes(MAX_BYTES+1);
  if(body.length>MAX_BYTES){ApiErrors.write(response,413,"Request body too large");return;}
  var wrapped=new HttpServletRequestWrapper(request) {
   @Override public ServletInputStream getInputStream() {
    var input=new java.io.ByteArrayInputStream(body);
    return new ServletInputStream() {
     @Override public int read(){return input.read();}
     @Override public boolean isFinished(){return input.available()==0;}
     @Override public boolean isReady(){return true;}
     @Override public void setReadListener(ReadListener listener){throw new UnsupportedOperationException("Synchronous JSON requests only");}
    };
   }
   @Override public java.io.BufferedReader getReader(){return new java.io.BufferedReader(new java.io.InputStreamReader(getInputStream(),java.nio.charset.StandardCharsets.UTF_8));}
  };
  chain.doFilter(wrapped,response);
 }
}
