package com.studiomuda.estoque.security;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class AcessoExceptionHandler {
    @ExceptionHandler(AcessoNegadoException.class)
    public Object tratarAcessoNegado(AcessoNegadoException ex, HttpServletRequest request) {
        if (ehRequisicaoApi(request)) {
            Map<String, String> body = new HashMap<>();
            body.put("erro", "Acesso insuficiente para esta operação.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
        }

        ModelAndView modelAndView = new ModelAndView("error/error");
        modelAndView.setStatus(HttpStatus.FORBIDDEN);
        modelAndView.addObject("statusCode", HttpStatus.FORBIDDEN.value());
        modelAndView.addObject("errorTitle", "Acesso negado");
        modelAndView.addObject("errorDescription", "Você não possui permissão suficiente para acessar este recurso.");
        modelAndView.addObject("requestUri", request.getRequestURI());
        return modelAndView;
    }

    private boolean ehRequisicaoApi(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String accept = request.getHeader("Accept");
        String requestedWith = request.getHeader("X-Requested-With");
        return uri.startsWith("/api/")
                || uri.contains("/api/")
                || (accept != null && accept.contains("application/json"))
                || "XMLHttpRequest".equalsIgnoreCase(requestedWith);
    }
}
