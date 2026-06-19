package com.studiomuda.estoque.controller;

import com.studiomuda.estoque.model.Cupom;
import com.studiomuda.estoque.model.CupomUso;
import com.studiomuda.estoque.repository.CupomRepository;
import com.studiomuda.estoque.repository.CupomUsoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/cupons/uso")
public class CupomUsoController {

    @Autowired
    private CupomUsoRepository cupomUsoRepository;

    @Autowired
    private CupomRepository cupomRepository;

    @GetMapping
    public String historicoGeral(Model model) {
        List<CupomUso> usos = cupomUsoRepository.findAllByOrderByDataUsoDesc();
        // Enriquecer com dados do cupom
        for (CupomUso uso : usos) {
            Cupom cupom = cupomRepository.findById(uso.getCupomId()).orElse(null);
            if (cupom != null) {
                uso.setCupomCodigo(cupom.getCodigo());
                uso.setTipoCupom(cupom.getTipoDesconto());
            }
        }
        model.addAttribute("usos", usos);
        model.addAttribute("modoGeral", true);
        return "cupons/uso";
    }

    @GetMapping("/{cupomId}")
    public String historicoPorCupom(@PathVariable int cupomId, Model model) {
        Cupom cupom = cupomRepository.findById(cupomId).orElse(null);
        if (cupom == null) return "redirect:/cupons";
        List<CupomUso> usos = cupomUsoRepository.findByCupomIdOrderByDataUsoDesc(cupomId);
        // Enriquecer com tipo do cupom
        for (CupomUso uso : usos) {
            uso.setTipoCupom(cupom.getTipoDesconto());
        }
        model.addAttribute("cupom", cupom);
        model.addAttribute("usos", usos);
        model.addAttribute("modoGeral", false);
        return "cupons/uso";
    }
}