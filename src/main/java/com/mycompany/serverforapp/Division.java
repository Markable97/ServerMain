/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.com.mycompany.serverforapp;

/**
 *
 * @author march
 */
public class Division {
    
    int idDivision;
    String nameDivision;

    public Division(int idDivision, String nameDivision) {
        this.idDivision = idDivision;
        this.nameDivision = nameDivision;
    }

    @Override
    public String toString() {
        return "Division{" + "idDivision=" + idDivision + ", nameDivision=" + nameDivision + '}' + "\n";
    }
    
    
}
