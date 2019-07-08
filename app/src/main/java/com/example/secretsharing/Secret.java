package com.example.secretsharing;

import java.util.Random;

public class Secret {

    private char[] charList =

            {' ', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
            'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
            'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y',
            'z', '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9'};

    private String[] numberList =

            {"00", "01", "02", "03", "04", "05", "06", "07",
            "08", "09", "10", "11", "12", "13", "14", "15", "16",
            "17", "18", "19", "20", "21", "22", "23", "24", "25",
            "26", "27", "28", "29", "30", "31", "32", "33", "34",
            "35", "36"};


    public String hideSecret(String secretCode) {

        int length = secretCode.length();
        Double num = Math.pow(10, length);

//        System.out.println(num.longValue());


        System.out.println(secretConversion('l'));

        Random r = new Random();
        long randomN = 0;
        do {
            randomN = (long)(r.nextDouble() * num.longValue());
        }while (randomN < (num.longValue() / 10));

        System.out.println("SecretCode: " + secretCode);
        System.out.println(randomN);

        String rand = String.valueOf(randomN);

        String share1 = "";

        String share2 = "";
        for (int i = 0;i <= length-1;i++) {
            String secC = secretConversion(secretCode.charAt(i));
            String ranC = secretConversion(rand.charAt(i));

            share1 = share1 + ranC;

            if (secC != null && ranC != null) {

                for (int l=0;l<=1;l++) {

                    char secCC = secC.charAt(l);
                    char ranCC = ranC.charAt(l);

                    int numResult = (Integer.valueOf(secCC) < Integer.valueOf(ranCC)) ? (Integer.valueOf(secCC)+10) - Integer.valueOf(ranCC) : (Integer.valueOf(secCC) - Integer.valueOf(ranCC));

                    share2 = share2 + "" + numResult;
                }
            }
        }
        System.out.println("Share1: " + share1);
        System.out.println("Share2: " + share2);

        return "";
    }

    public String secretConversion(char c) {

        for (int i=0; i<charList.length;i++) {
            if (charList[i] == c) {
                return numberList[i];
            }
        }
        return null;
    }
}
