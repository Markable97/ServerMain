/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.serverforapp;

/**
 *
 * @author march
 */
public class Stadiums {
    int idStadium;
    String nameStadium;

    public Stadiums(int idStadium, String nameStadium) {
        this.idStadium = idStadium;
        this.nameStadium = nameStadium;
    }

    @Override
    public String toString() {
        return "Stadiums{" + "idStadium=" + idStadium + ", nameStadium=" + nameStadium + '}';
    }
    
    
}
