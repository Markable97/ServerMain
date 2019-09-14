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
public class NextMatchesWithGoal {
    private int played;
    private int goalHome;
    private int goalGuest;
    
    public NextMatchesWithGoal(){
        
    }
    
    public NextMatchesWithGoal(int played, int goalHome, int goalGuest){
        this.played = played;
        this.goalHome = goalHome;
        this.goalGuest = goalGuest;
    }
    
}
