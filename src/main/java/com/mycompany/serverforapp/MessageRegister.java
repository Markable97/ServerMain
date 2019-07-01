/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.serverforapp;

/**
 *
 * @author Markable
 */
public class MessageRegister {
    
    String name;
    String email;
    String team;
    String password;
    
    public MessageRegister(String name, String email, String team, String password) {
        this.name = name;
        this.email = email;
        this.team = team;
        this.password = password;
    }

    @Override
    public String toString() {
        return "MessageRegister{" + "name=" + name + ", email=" + email + ", team=" + team + ", password=" + password + '}';
    }
    
    
    
}
