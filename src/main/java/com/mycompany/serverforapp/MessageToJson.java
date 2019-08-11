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
public class MessageToJson {
    String messageLogic;
    private int id;
    int tour;
    String date;
    private String team_name;
    private MessageRegister user_info;
    String responseFromServer;
    int settingForApp; 
    
    

    public MessageToJson(String messageLogic, int id_division, String id_team, MessageRegister user_info) {
        this.messageLogic = messageLogic;
        this.id = id_division;
        this.team_name = id_team;
        this.user_info = user_info;
        
    }  
    
     public MessageToJson(String messageLogic, MessageRegister user_info){
         this.messageLogic = messageLogic;
         this.user_info = user_info;
     }
     
     public MessageToJson(String response, int setting){
         this.responseFromServer = response;
         this.settingForApp = setting;
     }

    public MessageToJson(String responseFromServer) {
        this.responseFromServer = responseFromServer;
    }
     
    public String getMessageLogic() {
        return messageLogic;
    }
    
    public int getId() {
        return id;
    }

    public String getTeam_name() {
        return team_name;
    }

    public MessageRegister getUser_info() {
        return user_info;
    }

    public String getResponseFromServer() {
        return responseFromServer;
    }

    public int getSettingForApp() {
        return settingForApp;
    }

    public int getTour() {
        return tour;
    }

    public String getDate() {
        return date;
    }
    
    @Override
    public String toString() {
        return "MessageToJson{" + "messageLogic=" + messageLogic + ", id_division=" + id + ", id_team=" + team_name + ", user_info=" + user_info + ", responseFromServer=" + responseFromServer + ", settingForApp=" + settingForApp + '}';
    }


        
}
