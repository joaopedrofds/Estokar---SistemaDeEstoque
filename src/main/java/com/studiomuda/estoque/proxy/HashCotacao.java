package com.studiomuda.estoque.proxy;
import java.math.BigDecimal; import java.nio.charset.StandardCharsets; import java.security.MessageDigest;
public final class HashCotacao {
 private HashCotacao(){}
 public static String gerar(String cep,BigDecimal peso,BigDecimal comprimento,BigDecimal largura,BigDecimal altura){
  String base=cep.replaceAll("\\D","")+"|"+n(peso)+"|"+n(comprimento)+"|"+n(largura)+"|"+n(altura);
  try{byte[] bytes=MessageDigest.getInstance("SHA-256").digest(base.getBytes(StandardCharsets.UTF_8));StringBuilder s=new StringBuilder();for(byte b:bytes)s.append(String.format("%02x",b));return s.toString();}catch(Exception e){throw new IllegalStateException("Nao foi possivel gerar o hash da cotacao.",e);}
 }
 private static String n(BigDecimal v){return v.stripTrailingZeros().toPlainString();}
}
