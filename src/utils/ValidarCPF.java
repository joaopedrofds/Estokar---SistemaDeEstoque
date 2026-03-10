package utils;

public class ValidarCPF {

    public static boolean isCPF(String CPF) {
        if (CPF == null || CPF.length() != 11 || CPF.matches("(\\d)\\1{10}")) {
            return false;
        }

        char dig10, dig11;
        int sm, r, num, peso;

        try {
            sm = 0;
            peso = 10;
            for (int i = 0; i < 9; i++) {
                num = CPF.charAt(i) - '0';
                sm += (num * peso--);
            }

            r = 11 - (sm % 11);
            dig10 = (r == 10 || r == 11) ? '0' : (char)(r + '0');

            sm = 0;
            peso = 11;
            for (int i = 0; i < 10; i++) {
                num = CPF.charAt(i) - '0';
                sm += (num * peso--);
            }

            r = 11 - (sm % 11);
            dig11 = (r == 10 || r == 11) ? '0' : (char)(r + '0');

            return dig10 == CPF.charAt(9) && dig11 == CPF.charAt(10);
        } catch (Exception e) {
            return false;
        }
    }

    public static String imprimeCPF(String CPF) {
        if (CPF == null || CPF.length() != 11) return CPF;
        return CPF.substring(0, 3) + "." + CPF.substring(3, 6) + "." +
                CPF.substring(6, 9) + "-" + CPF.substring(9, 11);
    }
}
