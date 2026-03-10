package com.studiomuda.estoque.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index";
    }
    
    @GetMapping("/erro")
    public String erro(@RequestParam(required = false) String mensagem, Model model) {
        model.addAttribute("mensagemErro", mensagem != null ? mensagem : "Ocorreu um erro inesperado.");
        return "erro";
    }
}
