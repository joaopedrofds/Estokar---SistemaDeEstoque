package com.studiomuda.estoque.proxy;
import java.math.BigDecimal;
public class ParametrosCotacao {
 private Integer clienteId,pedidoId; private String cepDestino; private BigDecimal peso,comprimento,largura,altura;
 public Integer getClienteId(){return clienteId;} public void setClienteId(Integer v){clienteId=v;} public Integer getPedidoId(){return pedidoId;} public void setPedidoId(Integer v){pedidoId=v;} public String getCepDestino(){return cepDestino;} public void setCepDestino(String v){cepDestino=v;} public BigDecimal getPeso(){return peso;} public void setPeso(BigDecimal v){peso=v;} public BigDecimal getComprimento(){return comprimento;} public void setComprimento(BigDecimal v){comprimento=v;} public BigDecimal getLargura(){return largura;} public void setLargura(BigDecimal v){largura=v;} public BigDecimal getAltura(){return altura;} public void setAltura(BigDecimal v){altura=v;}
}
