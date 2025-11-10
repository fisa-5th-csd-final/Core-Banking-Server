package com.fisa.bank.common.config.security.authorization;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import com.fasterxml.jackson.databind.ObjectMapper;

/** 클라이언트를 동적으로 등록하기 위한 엔드포인트를 관리하는 서블릿 필터 해당 필터는 Spring Security Filter Chain에 등록하지 않는다. */
@Component
public class DynamicClientRegisterFilter extends OncePerRequestFilter {

  private final String registrarClientId;
  private final String registrarClientSecret;
  private final String serverBaseUrl;
  private final SpringTemplateEngine templateEngine;
  private final ObjectMapper om;

  private DynamicClientRegisterFilter(
      @Value("${oauth2.registrar-client.id:registrar-client}") final String registrarClientId,
      @Value("${oauth2.registrar-client.secret:secret}") final String registrarClientSecret,
      @Value("${backend.endpoint}") final String serverBaseUrl,
      final SpringTemplateEngine templateEngine) {
    this.registrarClientId = registrarClientId;
    this.registrarClientSecret = registrarClientSecret;
    this.serverBaseUrl = serverBaseUrl;
    this.templateEngine = templateEngine;
    this.om = new ObjectMapper();
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {

    String path = request.getRequestURI();

    // /dcr-config -> JSON 바로 반환
    if ("/dcr-config".equals(path)) {
      response.setStatus(HttpServletResponse.SC_OK);
      response.setContentType("application/json;charset=UTF-8");
      // 노출 위험! 내부망/관리만 사용 권장. 운영에선 secret 미반환 + 서버 프록시 권장.
      Map<String, Object> body =
          Map.of(
              "registrationEndpoint", registrationEndpoint(),
              "registrarClientId", registrarClientId,
              "registrarClientSecret", registrarClientSecret);
      response.getWriter().write(om.writeValueAsString(body));
      return;
    }

    if ("/dcr".equals(path) || "/dcr/".equals(path) || "/dcr/index.html".equals(path)) {
      renderRegistrationPage(request, response);
      return;
    }

    if ("/dcr/dcr-guide.html".equals(path)) {
      renderGuidePage(request, response);
      return;
    }
    // 그 외는 필터 체인 계속 진행
    chain.doFilter(request, response);
  }

  private void renderRegistrationPage(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    var application = JakartaServletWebApplication.buildApplication(request.getServletContext());
    var exchange = application.buildExchange(request, response);
    WebContext ctx = new WebContext(exchange);
    ctx.setVariable("registrationEndpoint", registrationEndpoint());
    ctx.setVariable("registrarClientId", registrarClientId);
    ctx.setVariable("registrarClientSecret", registrarClientSecret);

    writeHtml(response, templateEngine.process("dcr-form", ctx));
  }

  private void renderGuidePage(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    var application = JakartaServletWebApplication.buildApplication(request.getServletContext());
    var exchange = application.buildExchange(request, response);
    WebContext ctx = new WebContext(exchange);
    writeHtml(response, templateEngine.process("dcr-guide", ctx));
  }

  private void writeHtml(HttpServletResponse response, String body) throws IOException {
    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType("text/html;charset=UTF-8");
    response.getWriter().write(body);
    response.flushBuffer();
  }

  private String registrationEndpoint() {
    return serverBaseUrl.endsWith("/")
        ? serverBaseUrl + "connect/register"
        : serverBaseUrl + "/connect/register";
  }
}
