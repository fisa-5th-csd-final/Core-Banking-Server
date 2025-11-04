package com.fisa.bank.common.config.security.authorization;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

/** 클라이언트를 동적으로 등록하기 위한 엔드포인트를 관리하는 서블릿 필터 해당 필터는 Spring Security Filter Chain에 등록하지 않는다. */
@Component
public class DynamicClientRegisterFilter extends OncePerRequestFilter {

  private final String registrationEndpoint;
  private final String registrarClientId;
  private final String registrarClientSecret;
  private final ObjectMapper om;

  private DynamicClientRegisterFilter(
      @Value("${app.dcr.registration-endpoint:http://localhost:8080/connect/register}")
          final String registrationEndpoint,
      @Value("${oauth2.registrar-client.id:registrar-client}") final String registrarClientId,
      @Value("${oauth2.registrar-client.secret:secret}") final String registrarClientSecret) {
    this.registrarClientId = registrarClientId;
    this.registrarClientSecret = registrarClientSecret;
    this.registrationEndpoint = registrationEndpoint;
    this.om = new ObjectMapper();
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {

    String path = request.getRequestURI();

    // /dcr -> /dcr/index.html로 내부 forward
    if ("/dcr".equals(path) || "/dcr/".equals(path)) {
      request.getRequestDispatcher("/dcr/index.html").forward(request, response);
      return;
    }

    // /dcr-config -> JSON 바로 반환
    if ("/dcr-config".equals(path)) {
      response.setStatus(HttpServletResponse.SC_OK);
      response.setContentType("application/json;charset=UTF-8");
      // 노출 위험! 내부망/관리만 사용 권장. 운영에선 secret 미반환 + 서버 프록시 권장.
      Map<String, Object> body =
          Map.of(
              "registrationEndpoint", registrationEndpoint,
              "registrarClientId", registrarClientId,
              "registrarClientSecret", registrarClientSecret);
      response.getWriter().write(om.writeValueAsString(body));
      return;
    }

    // /dcr/index.html을 직접 치는 경우도 가능
    if ("/dcr/index.html".equals(path)) {
      ClassPathResource html = new ClassPathResource("static/dcr/index.html");
      sendHtmlResource(html, response);
      return;
    }

    if ("/dcr/guide.html".equals(path)) {
      ClassPathResource html = new ClassPathResource("static/dcr/guide.html");
      sendHtmlResource(html, response);
      return;
    }
    // 그 외는 필터 체인 계속 진행
    chain.doFilter(request, response);
  }

  private void sendHtmlResource(Object resource, HttpServletResponse response) throws IOException {
    if (resource instanceof ClassPathResource classPathResource) {
      if (classPathResource.exists()) {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/html;charset=UTF-8");
        try (InputStream in = classPathResource.getInputStream()) {
          in.transferTo(response.getOutputStream());
        }
        return;
      }
    }
    throw new IllegalArgumentException("Invalid Resource");
  }
}
