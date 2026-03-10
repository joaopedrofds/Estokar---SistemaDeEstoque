package com.studiomuda.estoque.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/exemplo-api")
public class ExemploApiController {

    @GetMapping
    public String exemploApi() {
        return "api-exemplo";
    }
}
