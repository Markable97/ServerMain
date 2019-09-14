package com.mycompany.serverforapp;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Markable
 */
public class Player {
    
    private int idPlayer;
    private String playerTeam;
    private String playerName;
    String birhtday;
    String amplua;
    private int number;
    private int games;
    private int goal;
    private int penalty;
    private int assist;
    private int yellowCard;
    private int redCard;
    private int penalty_out;
    private int own_goal;
    private int inGame;
    transient String playerUrlImage;

    public Player(String playerTeam, String playerName, String amplua, String birhtday, int number, 
            int games, int goal, int penalty, int assist, int yellowCard, int redCard,String playerUrlImage) {
        //this.idPlayer = idPlayer;
        this.playerTeam = playerTeam;
        this.playerName = playerName;
        this.playerUrlImage = playerUrlImage;
        this.birhtday = birhtday;
        this.amplua = amplua;
        this.number = number;
        this.games = games;
        this.goal = goal;
        this.penalty = penalty;
        this.assist = assist;
        this.yellowCard = yellowCard;
        this.redCard = redCard;
    }

    //����������� ��� ������� � �����
    public Player(int idPlayer, String playerTeam, String playerName, int number, int goal, int penalty, int assist, int yellowCard, int redCard, int penalty_out, int own_goal) {
        this.idPlayer = idPlayer;
        this.playerTeam = playerTeam;
        this.playerName = playerName;
        this.number = number;
        this.goal = goal;
        this.penalty = penalty;
        this.assist = assist;
        this.yellowCard = yellowCard;
        this.redCard = redCard;
        this.penalty_out = penalty_out;
        this.own_goal = own_goal;
    }

    public Player(int idPlayer, String playerTeam, String playerName) {
        this.idPlayer = idPlayer;
        this.playerTeam = playerTeam;
        this.playerName = playerName;
    }

    public Player(int idPlayer, String playerTeam, String playerName, int inGame, int goal, int assist, int yellow, int red){
        this.idPlayer = idPlayer;
        this.playerTeam = playerTeam;
        this.playerName = playerName;
        this.inGame = inGame;
        this.goal = goal;
        this.assist = assist;
        this.yellowCard = yellow;
        this.redCard = red;
    }
    
    @Override
    public String toString() {
        return "Player{" + "idPlayer=" + idPlayer + ", playerTeam=" + playerTeam + ", playerName=" + 
                playerName + ", birhtday=" + birhtday + ", amplua=" + amplua + ", number=" + number + 
                ", games=" + games + " inGame = " + inGame +", goal=" + goal + ", assist=" + assist + ", yellowCard=" + 
                yellowCard + ", redCard=" + redCard + ", playerUrlImage=" + playerUrlImage + "}\n";
    }

    
    public int getIdPlayer() {
        return idPlayer;
    }

    public String getPlayerTeam() {
        return playerTeam;
    }

    
    public String getPlayerName() {
        return playerName;
    }

    public String getPlayerUrlImage() {
        return playerUrlImage;
    }

    public String getBirhtday() {
        return birhtday;
    }

    public String getAmplua() {
        return amplua;
    }

    public int getNumber() {
        return number;
    }

    
    public int getGoal() {
        return goal;
    }

    public int getGames() {
        return games;
    }

    public int getAssist() {
        return assist;
    }

    public int getYellowCard() {
        return yellowCard;
    }

    public int getRedCard() {
        return redCard;
    }

    public int getPenalty() {
        return penalty;
    }
}
