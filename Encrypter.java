package com.sivector.android.appointmentapplication;

import android.util.Log;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.util.Iterator;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;

public class Encrypter {

    private static Provider provider;
    private static KeyGenerator keyGenerator;
    private static SecretKey secretKey;
    private static Cipher cipher;


    private static final int BLOCK_SIZE = 16; // AES CBC block size 16 bytes
    private static byte[] ivBytes;
    private static SecureRandom secureRandom;
    private static IvParameterSpec ivParameterSpec;

    // Client Information
    private static Client client;



    public Encrypter(Client c){
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);
            secretKey = keyGenerator.generateKey();

            cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e ) {
            System.out.println(e.getMessage() + "here");
        }

        //create byte array. Let SecureRandom generate a Random IV values and pack ivBytes Array.
        ivBytes = new byte[BLOCK_SIZE];
        secureRandom = new SecureRandom();
        secureRandom.nextBytes(ivBytes);

        ivParameterSpec = new IvParameterSpec(ivBytes);

        // Client information to encrypt.
        client = c;

    }

  /*  public static void main(String[] args) {
        Encrypter encrypter = new Encrypter();
        encrypter.encrypt();
        encrypter.decrypt();
        //encrypter.listServices();

    }*/

    public byte[] encrypt() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(client.getFirstName());
        stringBuilder.append(client.getLastName());
        stringBuilder.append(client.getCompanyName());
        stringBuilder.append(client.getReasonForVisit());

        try

        {

            byte[] plainText = stringBuilder.toString().getBytes();
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
            byte[] cipherText = new byte[ cipher.getOutputSize(plainText.length) ];
            int cipherTextLength = cipher.update(plainText, 0, plainText.length, cipherText, 0);
            cipher.doFinal(cipherText, cipherTextLength);

            // Confirm data is encypted
            System.out.print("Encrypted: ");
            for (int i = 0; i < cipherText.length  ; i++) {
                Log.d("AppointmentActivity", "Encrypted: " + (char) cipherText[i] );
            }

            return cipherText;

        } catch (InvalidKeyException | InvalidAlgorithmParameterException
                | ShortBufferException | IllegalBlockSizeException | BadPaddingException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public void decrypt(){

        byte[] cipherText = encrypt();

        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            byte[] plainText = new byte[cipher.getOutputSize(cipherText.length)];
            int plainTextLength = cipher.update(cipherText, 0, cipherText.length, plainText,
            0 );
            cipher.doFinal(plainText, plainTextLength);


            //confirm data is decrypted
            for (int i = 0; i < plainText.length ; i++) {
                //System.out.print((char) plainText[i]);
                Log.d("AppointmentActivity", "Decrypted " + (char) plainText[i] );
            }



        } catch (InvalidKeyException | IllegalBlockSizeException
                | InvalidAlgorithmParameterException | ShortBufferException | BadPaddingException e) {
            System.out.println(e.getMessage());
        }


    }

    public void listServices(){
        System.out.println();
        provider = cipher.getProvider();
        System.out.printf("Provider Name: " + provider.getName());

        Set<Provider.Service> services = provider.getServices();
        Iterator<Provider.Service> iterator = services.iterator();
        
        while(iterator.hasNext()) {
            System.out.println(iterator.next().getAlgorithm());
        }

    }
}
