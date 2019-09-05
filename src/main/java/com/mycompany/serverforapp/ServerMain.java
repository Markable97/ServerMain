package com.mycompany.serverforapp;


import com.google.gson.Gson;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Markable ветка не твоя. Моя!
 */
public class ServerMain {
    
    static ExecutorService executeIt = Executors.newFixedThreadPool(1);
   
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
      // Scanner scanner = new Scanner(System.in);
       //String serverComand;
       System.out.println("Enabling the server");
       ServerSocket server = new ServerSocket(55555);
       int number = 0;
       DataBaseRequest dbr = DataBaseRequest.getInstance();
        try {
            while(!server.isClosed()){
                System.out.println("Waiting for a response from the client");
                Socket fromclient = server.accept();
                executeIt.execute(new ThreadClient(fromclient, number, dbr));
                number++; 
                //executeIt.shutdown();
            }
                
        } catch (IOException ex) {
            Logger.getLogger(ServerMain.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            server.close();
        }
        
    }
    
}

class ThreadClient implements Runnable {

    Socket fromclient;
    
    String input;
    String messageLogic;
    int id;
    int tour;
    String id_team;
    String date;
    MessageRegister user_info;
    
    ArrayList<TournamentTable> tournamentArray;//турнирная таблица в виде массива
    ArrayList<PrevMatches> prevMatchesArray;//список прошедшего тура в виде массива
    ArrayList<NextMatches> nextMatchesArray;//список на следующие игры
    ArrayList<Player> playersArray;//список игроков одной команды
    
    DataInputStream in;
    DataOutputStream out;
    //DataOutputStream outTournamentTable;
    DataBaseRequest dbr;
    MessageToJson messageToJson;
    
    Gson gson = new Gson();
    
    public ThreadClient(Socket client, int numberUser, DataBaseRequest dbr) throws IOException{
        this.fromclient = client;
        this.dbr = dbr;
        //this.dbr.openConnection();
        System.out.println(client.getInetAddress() + " connection number = " + numberUser);
        in = new DataInputStream(fromclient.getInputStream());
        out = new DataOutputStream(fromclient.getOutputStream());
    }
    
    @Override
    public void run() {
        try {
            System.out.println("Сlient is connected");
            exit:
            while(fromclient.isConnected()){
                System.out.println("Wait message..."); 
                input = in.readUTF();
                //System.out.println("new branch locig server");
               
                System.out.println("String received from the client = \n" + input);
                messageToJson = gson.fromJson(input, MessageToJson.class);
                System.out.println(messageToJson.toString());
                
                messageLogic = messageToJson.getMessageLogic();
                
                String forClientJSON; 
                switch(messageLogic){
                    case "close":
                        System.out.println("Client closes the connection");
                        fromclient.close();
                        break exit;
                    case "division":
                        id = messageToJson.getId();
                        dbr.connection_main_activity(id);
                        tournamentArray = dbr.getTournamentTable();
                        String tournamentTableToJson = gson.toJson(tournamentArray);
                
                        //prevMatchesArray = baseQuery.getResultsPrevMatches();
                        prevMatchesArray = dbr.getPrevMatches();
                        String prevMatchesToJson = gson.toJson(prevMatchesArray);
                
                        //nextMatchesArray = baseQuery.getCalendar();
                        nextMatchesArray = dbr.getNextMatches();
                        String nextMatchesToJson = gson.toJson(nextMatchesArray);
                
                        System.out.println("[1]Array of object from DB to JSON");
                        System.out.println(tournamentTableToJson);
                        System.out.println("[2]Array of object from DB to JSON");
                        System.out.println(prevMatchesToJson);
                        System.out.println("[3]Array of object from DB to JSON");
                        System.out.println(nextMatchesToJson);
                        System.out.println("New branch");
                        out.writeUTF(tournamentTableToJson);
                        out.writeUTF(prevMatchesToJson);
                        out.writeUTF(nextMatchesToJson);
                        //отправка на клиент
                        sentFile(tournamentArray);

                        break;
                    case "team":
                        System.out.println("Case team");
                        id_team = messageToJson.getTeam_name();
                        System.out.println("New id_team = " + id_team);
                        dbr.connection_squad_info(id_team);
                        //DataBaseRequest baseRequest1 = new DataBaseRequest(id_team,messageLogic, 0);
                        playersArray = dbr.getSquadInfo();
                        forClientJSON = gson.toJson(playersArray);
                        System.out.println("[4]Array of object from DB to JSON");
                        System.out.println(forClientJSON);
                        out.writeUTF(forClientJSON);
                        sentFilePlayer(playersArray);
                        break;
                    case "player":
                        System.out.println("Case matches for player");
                        id = messageToJson.getId();
                        dbr.connection_playerInMatch(id);
                        playersArray = dbr.getSquadInfo();
                        forClientJSON = gson.toJson(playersArray);
                        System.out.println("[6]Array of object from DB to JSON");
                        System.out.println(forClientJSON);
                        out.writeUTF(forClientJSON);
                        break;
                    case "matches":
                        System.out.println("Case matches for team " + messageToJson.getTeam_name());
                        String teamName = messageToJson.getTeam_name();
                        dbr.connection_allMatches(teamName);
                        prevMatchesArray = dbr.getPrevMatches();
                        forClientJSON = gson.toJson(prevMatchesArray);
                        System.out.println("[5]Array of object from DB to JSON");
                        System.out.println(forClientJSON);
                        out.writeUTF(forClientJSON);
                        System.out.println("Поток для фоток");
                        sentFileAllMatches(prevMatchesArray, teamName);
                        //out.write(listImage.size()); 
                        break;
                    case "register":
                        System.out.println("CASE register");
                        user_info = messageToJson.getUser_info();
                        System.out.println(user_info);
                        UsersLogin regist = new UsersLogin(user_info.password);
                        System.out.println("Encoded password = " + regist.getEncodePassword());
                        dbr.connection_register(regist,user_info.name, user_info.email);
                        System.out.println("Message from db = " + dbr.getMessage());
                        forClientJSON = gson.toJson(new MessageToJson(dbr.getMessage()));
                        out.writeUTF(forClientJSON);
                        break;
                    case "login":
                        System.out.println("CASE login");
                        user_info = messageToJson.getUser_info();
                        System.out.println(user_info);
                        dbr.connection_login(user_info.email, user_info.password);
                        forClientJSON = gson.toJson(new MessageToJson(dbr.getMessage(),dbr.getSettingForApp()));
                        System.out.println("Message from db = " + dbr.getMessage());
                        out.writeUTF(forClientJSON);
                        break;
                    case "getTour":
                        System.out.println("CASE getTour");
                        id = messageToJson.getId();
                        tour = messageToJson.getTour();
                        System.out.println("CASE getTour id = " + id + " tour = " + tour);
                        dbr.connection_getTour(id, tour);
                        forClientJSON = gson.toJson(dbr.getNextMatches());
                        System.out.println("Message from db = " + forClientJSON);
                        out.writeUTF(forClientJSON);
                        break;
                    case "getTourAddResult":
                        System.out.println("CASE getTourAddResult");
                        id = messageToJson.getId();
                        date = messageToJson.getDate();
                        System.out.println("CASE getTour id = " + id + " date = " + date);
                        dbr.connection_getTourAddResults(id, date);
                        forClientJSON = gson.toJson(dbr.getNextMatches());
                        System.out.println("Message from db = " + forClientJSON);
                        out.writeUTF(forClientJSON);
                        break;
                    /*case "getCntStadium":                       
                        id = messageToJson.getId();
                        tour = messageToJson.getTour();
                        System.out.println("CSAE getCntStadium tour = " + tour);
                        int cntStadiums = dbr.getCntStadium(tour);
                        System.out.println("Count stadiums = " + cntStadiums);
                        out.writeUTF(String.valueOf(cntStadiums));
                        break;*/
                    case "getStadiumList":
                        /*id = messageToJson.getId();
                        tour = messageToJson.getTour();*/
                        date = messageToJson.getDate();
                        System.out.println("CSAE getStadiumList date = " + date);
                        forClientJSON = gson.toJson(dbr.getNameStadium(date));
                        System.out.println("Name stadiums = " + forClientJSON);
                        out.writeUTF(forClientJSON);
                        break;
                    case "getScheduleList":
                        /*id = messageToJson.getId();
                        tour = messageToJson.getTour();*/
                        date = messageToJson.getDate();
                        System.out.println("CSAE getScheduleList date = " + date);
                        forClientJSON = gson.toJson(dbr.getSchedule(date));
                        System.out.println("Message from db = " + forClientJSON);
                        out.writeUTF(forClientJSON);
                        break;
                    case "setSchedule":
                        System.out.println("CASE Schedule");
                        ArrayList<Schedule> list = messageToJson.getSchedule();
                        System.out.println("Schedule from client: \n" + list.toString());
                        dbr.setSchedule(list);
                        out.writeUTF(dbr.message);
                        break;
                    case "getPlayersProtocol":
                        System.out.println("CASE getPlayersProtocol");
                        id_team = messageToJson.getTeam_name();
                        System.out.println("New id_team = " + id_team);
                        String[] teams = id_team.split(";");
                        dbr.connectiom_playersProtocol(teams[0]);
                        forClientJSON = gson.toJson(dbr.getSquadInfo());
                        System.out.println("Players protocol JSON: \n" + forClientJSON);
                        out.writeUTF(forClientJSON);
                        dbr.connectiom_playersProtocol(teams[1]);
                        forClientJSON = gson.toJson(dbr.getSquadInfo());
                        System.out.println("Players protocol JSON: \n" + forClientJSON);
                        out.writeUTF(forClientJSON);
                        break;
                    case "setResults":
                        System.out.println("CASE setResults");
                        PrevMatches match = messageToJson.getMatch();
                        ArrayList<Player> players = messageToJson.getPlayers();
                        System.out.println("Information from client: \n" + match + "\n"+ players);
                        forClientJSON = dbr.setResults(match, players);
                        System.out.println("Response from DB - " + forClientJSON);
                        out.writeUTF(forClientJSON);
                        break;
                }//case 
            }//while 
           
            //System.out.println("Disconnect client, close channels....");
            //System.out.println("waiting for a new client*********");
            //in.close();
            //out.close();
            //outTournamentTable.close();
            //fromclient.close();
        } catch (IOException ex) {
            System.out.println("User turn off: " + ex.getLocalizedMessage());
            
        } catch (SQLException ex) {
            Logger.getLogger(ThreadClient.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            //dbr.closeConnection();
            try {
                System.out.println("Disconnect client, close channels....");
                in.close();
                out.close();
                fromclient.close();
                System.out.println("waiting for a new client*********");
            } catch (IOException ex) {
                Logger.getLogger(ThreadClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    void sentFile(ArrayList<TournamentTable> list) throws IOException{
        System.out.println("Добавляю потоки для файлов");                        
        String pathBig = "D:\\Pictures\\"; 
        int cnt_photo = 0; //кол-во существующих фоток
        for(int i = 0; i < list.size(); i++){                            
            File imageBig = new File(pathBig + list.get(i).getUrlImage());                            
            if(imageBig.exists()){
                System.out.println("Файлы существует " + imageBig.getName());
                cnt_photo++;    
            }else{
                System.out.println("BIG Файл "+ list.get(i).getUrlImage() +" не сущуствует!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }                            
        }
        out.writeInt(cnt_photo); //кол-во файлов
        if(cnt_photo > 0){
            for(int i = 0; i < list.size(); i++){                                
                File imageBig = new File(pathBig + list.get(i).getUrlImage()); 
                if(imageBig.exists()){
                    System.out.println("Файлы существует " + imageBig.getName());
                    String nameImage = tournamentArray.get(i).getUrlImage().replace(".png",""); 
                    out.writeUTF(nameImage);
                    byte[] byteArrayBig = new byte[(int)imageBig.length()];
                    BufferedInputStream streamBig = new BufferedInputStream(new FileInputStream(imageBig));
                    streamBig.read(byteArrayBig, 0, byteArrayBig.length);
                    streamBig.close();
                    out.writeInt(byteArrayBig.length);
                    out.write(byteArrayBig);
                    //out.flush();
               }else{
                    System.out.println("BIG Файл не сущуствует!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    }                                
            }
        }
    }
    
    void sentFileAllMatches(ArrayList<PrevMatches> list,String teamName) throws IOException{
        String teamPath = "D:\\Pictures\\"; 
        ArrayList<String> listImage = new ArrayList<>();
        File imageStart = new File(teamPath + teamName + ".png");
        if(imageStart.exists()){
            System.out.println("ImageStart = " + imageStart.getName() );
            listImage.add(imageStart.getName());
        }else{
            System.out.println("Image not found");
        }
        for(int i = 0; i< list.size(); i++){
            File imH = new File(teamPath + list.get(i).getUrlImageHome());
            //System.out.println("Название файла = " + imH.getName() );
            File imG = new File(teamPath + list.get(i).getUrlImageGuest());
            //System.out.println("Название файла = " + imG.getName());
            if(imH.exists()&& imG.exists()){
                if( imH.getName().equals(imageStart.getName()) == false ){
                    System.out.println(imH.getName());
                    listImage.add(imH.getName());
                }
                if( imG.getName().equals(imageStart.getName()) == false ){
                    System.out.println(imG.getName());
                    listImage.add(imG.getName());
                }
            }
            else{
                System.out.println("Файлы не сущуствует!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
        }
        System.out.println("Кол-во файлов = " + listImage.size() + listImage);
        out.writeInt(listImage.size());
        if(listImage.size()!=0){
            for(int i = 0; i < listImage.size(); i++){
                File file = new File(teamPath+listImage.get(i));
                if(file.exists()){
                    System.out.println("Файл существует " + file.getName());
                    String nameImage = listImage.get(i).replace(".png","");
                    out.writeUTF(nameImage);
                    byte[] byteImageTeam = new byte[(int)file.length()];
                    BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file));
                    stream.read(byteImageTeam, 0, byteImageTeam.length);
                    stream.close();
                    out.writeInt(byteImageTeam.length);
                    out.write(byteImageTeam);
                }
            }
        }
        else{
            System.out.println("Файл не сущуствует!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
    }
    
    void sentFilePlayer(ArrayList<Player> list) throws IOException{
        String pathPlayer = "D:\\Учеба\\Диплом\\Фотки игроков\\";
        int countImage = 0;
        for(int i = 0; i < list.size(); i++){
            File image = new File(pathPlayer + list.get(i).getPlayerTeam() + "\\" +
                    list.get(i).getPlayerUrlImage());
            System.out.println(image.getPath());
            if(image.exists()){
                System.out.println("Файл существует " + image.getName());
                countImage++;
            }else{
                System.out.println("Файл не сущуствует!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
        }
        System.out.println("Кол-во найденных фотографий = " + countImage);
        out.writeInt(countImage);//кол-во фоток
        if(countImage > 0){
            for(int i = 0; i < list.size(); i++){
                File image = new File(pathPlayer + list.get(i).getPlayerTeam() + "\\" +
                        list.get(i).getPlayerUrlImage());
                System.out.println(image.getPath());
                if(image.exists()){
                    System.out.println("Файл существует " + image.getName());
                    String nameImage = image.getName();
                    out.writeUTF(nameImage);
                    byte[] byteImagePlayer = new byte[(int)image.length()];
                    BufferedInputStream stream = new BufferedInputStream(new FileInputStream(image));
                    stream.read(byteImagePlayer, 0, byteImagePlayer.length);
                    stream.close();
                    out.writeInt(byteImagePlayer.length);
                    out.write(byteImagePlayer);
                }else{
                    System.out.println("Файл не сущуствует!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                }
            }
        }
        
    }
}