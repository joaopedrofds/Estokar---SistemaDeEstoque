package com.studiomuda.estoque.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class CustomErrorController implements ErrorController {

    private static final Logger logger = LoggerFactory.getLogger(CustomErrorController.class);
    private static final Map<Integer, String> STATUS_MESSAGES = new HashMap<>();
    
    static {
        STATUS_MESSAGES.put(HttpStatus.NOT_FOUND.value(), "Página não encontrada");
        STATUS_MESSAGES.put(HttpStatus.FORBIDDEN.value(), "Acesso negado");
        STATUS_MESSAGES.put(HttpStatus.UNAUTHORIZED.value(), "Não autorizado");
        STATUS_MESSAGES.put(HttpStatus.BAD_REQUEST.value(), "Requisição inválida");
        STATUS_MESSAGES.put(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Erro interno do servidor");
        STATUS_MESSAGES.put(HttpStatus.METHOD_NOT_ALLOWED.value(), "Método não permitido");
        STATUS_MESSAGES.put(HttpStatus.SERVICE_UNAVAILABLE.value(), "Serviço indisponível");
        STATUS_MESSAGES.put(HttpStatus.GATEWAY_TIMEOUT.value(), "Tempo limite excedido");
    }

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Exception exception = (Exception) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        String errorMessage = (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        String requestUri = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        
        int statusCode = 500;
        if (status != null) {
            statusCode = Integer.parseInt(status.toString());
        }
        
        String errorTitle = STATUS_MESSAGES.getOrDefault(statusCode, "Ocorreu um erro");
        String errorDescription = getErrorDescription(statusCode, exception, errorMessage);
        
        // Log detalhado do erro
        logError(statusCode, requestUri, exception, errorMessage);
        
        // Popular modelo para a visualização
        model.addAttribute("statusCode", statusCode);
        model.addAttribute("errorTitle", errorTitle);
        model.addAttribute("errorDescription", errorDescription);
        model.addAttribute("requestUri", requestUri);
        
        return "error/error";
    }

    private void logError(int statusCode, String requestUri, Exception exception, String errorMessage) {
        logger.error("Erro {} em URI={}: {}", statusCode, requestUri, errorMessage);
        if (exception != null) {
            logger.error("Detalhes da exceção: ", exception);
        }
    }
    
    private String getErrorDescription(int statusCode, Exception exception, String errorMessage) {
        switch (statusCode) {
            case 404:
                return "A página solicitada não foi encontrada no sistema.";
            case 403:
                return "Você não tem permissão para acessar este recurso.";
            case 401:
                return "Autenticação é necessária para acessar este recurso.";
            case 400:
                return "A requisição enviada é inválida ou malformada.";
            case 405:
                return "O método HTTP utilizado não é permitido para este endpoint.";
            case 500:
                return exception != null ? 
                       "Erro interno do servidor: " + exception.getMessage() :
                       "Ocorreu um erro interno no servidor.";
            case 503:
                return "O serviço está temporariamente indisponível. Tente novamente mais tarde.";
            default:
                return errorMessage != null && !errorMessage.isEmpty() ? 
                       errorMessage : 
                       "Ocorreu um erro inesperado. Por favor, tente novamente ou contate o suporte.";
        }
    }
}
