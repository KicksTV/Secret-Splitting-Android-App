package com.example.secretsharing;

import java.math.BigInteger;
import java.util.Random;

public class Secret {


    public String hideSecret(String secretCode) {

        int length = secretCode.length();

        byte[] b = new byte[length];


        Random r = new Random();
        r.nextBytes(b);
        BigInteger i = new BigInteger(b);

        i.mod(BigInteger.valueOf(length));

        System.out.println("SecretCode: " + secretCode);
        System.out.println(i);

//        Double randomN = randLength + (int)(r.nextFloat() * randLength);

//        System.out.println("RandomCode: " + randomN);

//        String rand = String.valueOf(randomN);
//
//        String result = "";
//        for (int i = 0;i <= 7;i++) {
//            char secC = secretCode.charAt(i);
//            char ranC = rand.charAt(i);
//
//            int num = (Integer.valueOf(secC) < Integer.valueOf(ranC)) ? (Integer.valueOf(secC)+10) - Integer.valueOf(ranC) : (Integer.valueOf(secC) - Integer.valueOf(ranC));
//
//            result = result + "" + num;
//        }
//        System.out.println(result);

        return "";
    }
}
