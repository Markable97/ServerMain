/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.serverforapp;

import java.util.Base64;
import java.util.Random;
import org.apache.commons.codec.digest.DigestUtils;

/**
 *
 * @author Markable
 */
public class UsersLogin {

    private String uniqId;
    private String salt;
    private String encodePassword;
    private String hashPassword;
    
    public UsersLogin(String password) {
        main(password);
    }
    
    public UsersLogin(String salt, String password){
        this.hashPassword = checkhashSSHA(salt, password);
    }
    
    private void main(String password){
        this.encodePassword = hashSSHA(password);
    }
    
    private char[] generateSalt(int len) {
        String charsCaps = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String nums = "0123456789";
        String charsNotCaps = charsCaps.toLowerCase();
        String passSymbols = charsCaps + nums + charsNotCaps;
        Random rnd = new Random();
        char[] saltBef = new char[len];
        for (int i = 0; i < len; i++) {
            saltBef[i] = passSymbols.charAt(rnd.nextInt(passSymbols.length()));
        }
        System.out.println("salt = " + String.valueOf(saltBef));
        return saltBef;
    }
    
    private String hashSSHA(String password){
        int length = 10; //длина соли
        uniqId = String.valueOf(generateUniqId(length + 13));
        salt = String.valueOf(generateSalt(length));
        salt = DigestUtils.sha1Hex(salt);
        salt = salt.substring(0,10);
        System.out.println("Hex SALT 10 chars = " + salt);
        System.out.println("Password = " + password);
        System.out.println("HEX password + salt = " + DigestUtils.sha1Hex(password + salt));
        byte[] encodedBytes = Base64.getEncoder().encode((DigestUtils.sha1Hex(password + salt) + salt).getBytes());
        String base64Password = new String(encodedBytes);
        //System.out.println("пароль в base64 = " + base64Password);
        return base64Password;
     }
    
    private char[] generateUniqId(int len){
        String charsCaps = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String nums = "0123456789";
        String charsNotCaps = charsCaps.toLowerCase();
        String passSymbols = charsCaps + nums + charsNotCaps;
        Random rnd = new Random();
        char[] uniqId = new char[len];
        for (int i = 0; i < len; i++) {
            uniqId[i] = passSymbols.charAt(rnd.nextInt(passSymbols.length()));
        }
        System.out.println("uniqId = " + String.valueOf(uniqId));
        return uniqId;
    }
    
    private String checkhashSSHA(String salt, String password){
         
         System.out.println("User's password  = " + password);
         System.out.println("Salt from db = " + salt);
         String str = DigestUtils.sha1Hex(password + salt);// + salt;//DigestUtils.sha1Hex(password + salt) + salt;
         byte[] encodedBytes = Base64.getEncoder().encode((str + salt).getBytes());
         String hash = new String(encodedBytes);
         //System.out.println("Данные из бд перед првоеркой = " + hash);
         return hash;
     }

    public String getSalt() {
        return salt;
    }
     
    public String getEncodePassword() {
        return encodePassword;
    }

    public String getHashPassword() {
        return hashPassword;
    }

    public String getUniqId() {
        return uniqId;
    }
    
}
