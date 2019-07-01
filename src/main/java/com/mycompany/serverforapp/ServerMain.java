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
 * @author Markable ветка братишки
 */
public class ServerMain {
    
    static ExecutorService executeIt = Executors.newFixedThreadPool(10);
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
      // Scanner scanner = new Scanner(System.in);
       //String serverComand;
       System.out.println("Enabling the server");
       ServerSocket server = new ServerSocket(55555);
       int number = 0;
        try {
            while(!server.isClosed()){
                System.out.println("Waiting for a response from the client");
                Socket fromclient = server.accept();
                executeIt.execute(new ThreadClient(fromclient, number));
                number++;
                //executeIt.shutdown();
            }
                
        } catch (IOException ex) {
            Logger.getLogger(ServerMain.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            server.close();
        }
        
    }
    
}//mark loh

class ThreadClient implements Runnable {

    Socket fromclient;
    
    String input;
    String messageLogic;
    int id_division;
    String id_team;
    MessageRegister user_info;
    
    ArrayList<TournamentTable> tournamentArray;//турнирная таблица в виде массива
    ArrayList<PrevMatches> prevMatchesArray;//список прошедшего тура в виде массива
    ArrayList<NextMatches> nextMatchesArray;//список на следующие игры
    ArrayList<Player> playersArray;//список игроков одной команды
    
    DataInputStream in;
    DataOutputStream out;
    //DataOutputStream outTournamentTable;
    
    MessageToJson messageToJson;
    
    Gson gson = new Gson();
    
    public ThreadClient(Socket client, int numberUser) throws IOException{
        this.fromclient = client;
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
               
                System.out.println("String received from the client = " + input);
                messageToJson = gson.fromJson(input, MessageToJson.class);
                System.out.println(messageToJson.toString());
                
                messageLogic = messageToJson.getMessageLogic();
                
                
                switch(messageLogic){
                    case "close":
                        System.out.println("Client closes the connection");
                        fromclient.close();
                        break exit;
                    case "division":
                        id_division = messageToJson.getId_division();
                        DataBaseRequest baseRequest = new DataBaseRequest(id_division);
                        tournamentArray = baseRequest.getTournamentTable();
                        String tournamentTableToJson = gson.toJson(tournamentArray);
                
                        //prevMatchesArray = baseQuery.getResultsPrevMatches();
                        prevMatchesArray = baseRequest.getPrevMatches();
                        String prevMatchesToJson = gson.toJson(prevMatchesArray);
                
                        //nextMatchesArray = baseQuery.getCalendar();
                        nextMatchesArray = baseRequest.getNextMatches();
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
                        //начало ветки
                        System.out.println("Добавляю потоки для файлов");
                        String path = "D:\\Учеба\\Диплом\\Логотипы команд\\";
                        String pathBig = "D:\\Учеба\\Диплом\\Логотипы команд\\BigImage\\"; 
                        int cnt_photo = 0; //кол-во существующих фоток
                        for(int i = 0; i < tournamentArray.size(); i++){
                            File image = new File(path + tournamentArray.get(i).getUrlImage());
                            File imageBig = new File(pathBig + tournamentArray.get(i).getUrlImage());
                            if(image.exists()){
                                if(imageBig.exists()){
                                    System.out.println("Файлы существует " + image.getName() + " " + imageBig.getName());
                                    cnt_photo++;    
                                }else{
                                    System.out.println("BIG Файл "+ tournamentArray.get(i).getUrlImage() +" не сущуствует!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                                    }
                            }else{
                                System.out.println("Файл "+tournamentArray.get(i).getUrlImage() +"не сущуствует!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                            }
                        }
                        out.writeInt(cnt_photo);//кол-во фоток
                        if(cnt_photo > 0){
                            for(int i = 0; i < tournamentArray.size(); i++){
                                File image = new File(path + tournamentArray.get(i).getUrlImage());
                                File imageBig = new File(pathBig + tournamentArray.get(i).getUrlImage());
                                if(image.exists()){
                                    if(imageBig.exists()){
                                        System.out.println("Файлы существует " + image.getName() + " " + imageBig.getName());
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
                                }else{
                                    System.out.println("Файл не сущуствует!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                                }
                            }
                        }

                        break;
                    case "team":
                        System.out.println("Case team");
                        id_team = messageToJson.getId_team();
                        byte[] decode = Base64.getDecoder().decode(id_team.getBytes());
                        id_team = new String(decode);
                        System.out.println("New id_team = " + id_team);
                        //DataBaseQuery baseQuery1 = new DataBaseQuery(id_division, id_team);
                        //playersArray = baseQuery1.getPlayerArray();
                        DataBaseRequest baseRequest1 = new DataBaseRequest(id_team,messageLogic, 0);
                        playersArray = baseRequest1.getSquadInfo();
                        String playersToJson = gson.toJson(playersArray);
                        System.out.println("[4]Array of object from DB to JSON");
                        System.out.println(playersToJson);
                        out.writeUTF(playersToJson);
                        System.out.println("Открываю потоки для загрузок фоток игроков ");
                        String pathPlayer = "D:\\Учеба\\Диплом\\Фотки игроков\\";
                        int countImage = 0;
                        for(int i = 0; i < playersArray.size(); i++){
                            File image = new File(pathPlayer + playersArray.get(i).getPlayerTeam() + "\\" +
                                    playersArray.get(i).getPlayerUrlImage());
                            System.out.println(image.getPath());
                            if(image.exists()){
                                System.out.println("Файл существует " + image.getName());
                                countImage++;
                            }else{
                                System.out.println("Файл не сущуствует!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                            }
                        }
                        out.writeInt(countImage);//кол-во фоток
                        for(int i = 0; i < playersArray.size(); i++){
                            File image = new File(pathPlayer + playersArray.get(i).getPlayerTeam() + "\\" +
                                    playersArray.get(i).getPlayerUrlImage());
                            System.out.println(image.getPath());
                            if(image.exists()){
                                System.out.println("Файл существует " + image.getName());
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
                        break;
                    case "player":
                        System.out.println("Case matches for player");
                        id_division = messageToJson.getId_division();
                        DataBaseRequest request = new DataBaseRequest(null, messageLogic, id_division);
                        playersArray = request.getSquadInfo();
                        String playerInMatchToJson = gson.toJson(playersArray);
                        System.out.println("[6]Array of object from DB to JSON");
                        System.out.println(playerInMatchToJson);
                        out.writeUTF(playerInMatchToJson);
                        break;
                    case "matches":
                        System.out.println("Case matches for team");
                        DataBaseRequest dbr = new DataBaseRequest(id_team, messageLogic, 0);
                        //DataBaseQuery baseQueryAllMatches = new DataBaseQuery(id_division, id_team);
                        //allMatchesArray = baseQueryAllMatches.getAllMatches();
                        prevMatchesArray = dbr.getPrevMatches();
                        String prevAllMatchesForTeamToJson = gson.toJson(prevMatchesArray);
                        System.out.println("[5]Array of object from DB to JSON");
                        System.out.println(prevAllMatchesForTeamToJson);
                        out.writeUTF(prevAllMatchesForTeamToJson);
                        System.out.println("Поток для фоток");
                        String teamPath = "D:\\Учеба\\Диплом\\Логотипы команд\\BigImage\\";
                        ArrayList<String> listImage = new ArrayList<>();
                        File imageStart = new File(teamPath + id_team + ".png");
                        if(imageStart.exists()){
                            System.out.println("ImageStart = " + imageStart.getName() );
                            listImage.add(imageStart.getName());
                        }else{
                            System.out.println("Image not found");
                        }
                        for(int i = 0; i< prevMatchesArray.size(); i++){
                            File imH = new File(teamPath + prevMatchesArray.get(i).getUrlImageHome());
                            //System.out.println("Название файла = " + imH.getName() );
                            File imG = new File(teamPath + prevMatchesArray.get(i).getUrlImageGuest());
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
                        //out.write(listImage.size());
                        
                        break;
                    case "register":
                        System.out.println("CASE register");
                        user_info = messageToJson.getUser_info();
                        System.out.println(user_info);
                        //byte[] decodeByte = Base64.getDecoder().decode(user_info.team.getBytes());
                        //String team = new String(decodeByte);
                        UsersLogin regist = new UsersLogin(user_info.password);
                        System.out.println("Encoded password = " + regist.getEncodePassword());
                        //byte[] decodeByte = Base64.getDecoder().decode(login.getEncodePassword().getBytes());
                        //System.out.println(new String(decodeByte));
                        DataBaseRequest addUser = new DataBaseRequest(regist, user_info.name, user_info.email);
                        System.out.println("Message from db = " + addUser.getMessage());
                        out.writeUTF(addUser.getMessage());
                        break;
                    case "login":
                        System.out.println("CASE login");
                        user_info = messageToJson.getUser_info();
                        System.out.println(user_info);
                        DataBaseRequest checkUser = new DataBaseRequest(user_info.email, user_info.password);
                        System.out.println("Message from db = " + checkUser.getMessage());
                        out.writeUTF(checkUser.getMessage());
                        break;
                }//case 
            }//while 
           
            System.out.println("Disconnect client, close channels....");
            System.out.println("waiting for a new client*********");
            in.close();
            out.close();
            //outTournamentTable.close();
            fromclient.close();
        } catch (IOException ex) {
            
        } catch (SQLException ex) {
            Logger.getLogger(ThreadClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}