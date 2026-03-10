package utils;

public class ValidarCNPJ {

    public static boolean isCNPJ(String CNPJ) {
        if (CNPJ == null || CNPJ.length() != 14)
            return false;

        if (CNPJ.equals("00000000000000") || CNPJ.equals("11111111111111") ||
                CNPJ.equals("22222222222222") || CNPJ.equals("33333333333333") ||
                CNPJ.equals("44444444444444") || CNPJ.equals("55555555555555") ||
                CNPJ.equals("66666666666666") || CNPJ.equals("77777777777777") ||
                CNPJ.equals("88888888888888") || CNPJ.equals("99999999999999"))
            return false;

        try {
            char dig13, dig14;
            int sm, i, r, num, peso;

            // Cálculo do 1º dígito verificador
            sm = 0;
            peso = 2;
            for (i = 11; i >= 0; i--) {
                num = (int)(CNPJ.charAt(i) - 48);
                sm += num * peso;
                peso = (peso == 9) ? 2 : peso + 1;
            }

            r = sm % 11;
            dig13 = (r < 2) ? '0' : (char)((11 - r) + 48);

            // Cálculo do 2º dígito verificador
            sm = 0;
            peso = 2;
            for (i = 12; i >= 0; i--) {
                num = (int)(CNPJ.charAt(i) - 48);
                sm += num * peso;
                peso = (peso == 9) ? 2 : peso + 1;
            }

            r = sm % 11;
            dig14 = (r < 2) ? '0' : (char)((11 - r) + 48);

            return (dig13 == CNPJ.charAt(12) && dig14 == CNPJ.charAt(13));
        } catch (Exception e) {
            return false;
        }
    }

    public static String imprimeCNPJ(String CNPJ) {
        return CNPJ.substring(0, 2) + "." + CNPJ.substring(2, 5) + "." +
                CNPJ.substring(5, 8) + "/" + CNPJ.substring(8, 12) + "-" +
                CNPJ.substring(12, 14);
    }
}
