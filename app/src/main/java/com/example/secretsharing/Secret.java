package com.example.secretsharing;

import java.util.Random;

public class Secret {


    public String hideSecret(String secretCode) {

        int length = secretCode.length();


        Double num = Math.pow(10, length);


        Random r = new Random();
        int randomN = r.nextInt(num.intValue());



        System.out.println("SecretCode: " + secretCode);
        System.out.println(num);
        System.out.println(randomN);

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
