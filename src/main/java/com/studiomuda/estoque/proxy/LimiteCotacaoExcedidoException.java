package com.studiomuda.estoque.proxy;
public class LimiteCotacaoExcedidoException extends IllegalStateException { public LimiteCotacaoExcedidoException(){super("Limite de 50 cotacoes externas por hora atingido. Utilize a contingencia ou solicite liberacao gerencial.");} }
