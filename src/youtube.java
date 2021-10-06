//package com.company;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;
import java.sql.*;

import static java.lang.System.exit;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


public class youtube {

    public static Scanner scanner = new Scanner(System.in);
    public static boolean signUp(Connection conn) {
        System.out.print("***Sign Up****\nEnter UserName:");
        String username = scanner.next();
        System.out.print("\nEnter PassWord:");
        int pass = scanner.nextInt();
        System.out.print("\nEnter E-mail:");
        String query, email = scanner.next();
        Statement stmt = null;
        ResultSet rs = null;
        int flag = 0,uid =0 ;
        while (flag != 10) {
            //get informatin from db
            try {
                query = "Select id from public.\"User\" where name = \'" + username +"\' ;";
                System.out.println("Query:"+query);
                stmt = conn.createStatement();
                if (stmt.execute(query)) {
                    rs = stmt.getResultSet();
                    if(rs.next()){
                        flag = 0 ;
                    }
                    else {
                        flag = 1;
                    }
                }
            }
            catch(Exception e){
                System.out.println("error in signUp");
            }
            if (flag == 0) { //username invalid
                System.out.print("\nUserName already exists.Enter 0 to go to login.Username:");
                username = scanner.next();
                if(username.equals("0")){
                    return login(conn);
                }
            }
            else if (flag == 1) {
                query = "INSERT INTO public.\"User\"(name,pass,email) VALUES (\'" + username +"\',"+pass + ",\'"+email+"\') ;";
                try {
                    System.out.println("Query:" + query);
                    stmt = conn.createStatement();
                    stmt.execute(query);
                    System.out.println(".................Well Done...................");
                    query = "Select * from public.\"User\" where name = \'" + username +"\';" ;
                    System.out.println("Query:"+query);
                    stmt = conn.createStatement();
                    if (stmt.execute(query)) {
                        rs = stmt.getResultSet();
                        while(rs.next()) {
                            uid = rs.getInt("id");
                        }
                    }
                }catch (Exception e){
                    System.out.println("error in signUp");
                }
                flag = 10;
                return menu(conn,uid);
            }
        }
        return true;
    }
    public static boolean login(Connection conn) {
        Statement stmt = null;
        ResultSet rs = null;
        String username;
        int pass, flag = -1;
        //get information
        System.out.println("***Login***\nUserName:");
        username = scanner.next();
        System.out.println("Password:");//just can be int
        pass = scanner.nextInt();
        String query = null;
        int uid = 0;
        boolean sw = true;
        while (sw) {
            //check username & pass from database
            try {
                query = "Select * from public.\"User\" where name = \'" + username + "\';";
                System.out.println("Query:" + query);
                stmt = conn.createStatement();
                if (stmt.execute(query)) {
                    rs = stmt.getResultSet();
                    if(rs.next()) {
                        int password = rs.getInt("pass");
                        uid = rs.getInt("id");
                        if (pass == password) {
                            // true pass & true user
                            System.out.println(".................Well Done...................");
                            menu(conn, uid);
                            return true;
                        } else {
                            //true user & wrong pass
                            System.out.print("Wrong Password\nIf you haven't an account Please Enter 0\nPassword:");
                            pass = scanner.nextInt();
                            if (pass == 0) {
                                return menuLogin(conn); //true?
                            }
                        }
                    }
                    else{
                        //wrong  user
                        System.out.print("Wrong Username!\nIf you haven't an account Please Enter 0\nUsername:");
                        username = scanner.next();
                        if (username.equals("0")) {
                            return menuLogin(conn); //true?
                        } else {
                            System.out.println("Enter password:");
                            pass = scanner.nextInt();
                        }
                    }
                }
                else{
                    System.out.println("error in login");
                    sw = false;
                }
            } catch (Exception e) {
                System.out.println("error in login");
            }
        }
        return true;
    }
    public static boolean menuLogin(Connection conn){
        int option = -1;
        while (option != 1 && option != 2) {
            System.out.print("1- Login\n2- Sign Up\n Select an option:");
            option = scanner.nextInt();
            if (option == 1) {
                return login(conn);
            } else if (option == 2) {
                return signUp(conn);
            }
        }
        return true;
    }
    public static boolean comment(int vid, int uid, Connection conn) {
        System.out.println("Select an option:(1: comment video / 2: reply comment / 3: delete comment)");
        Statement stmt = null;
        ResultSet rs = null;
        int cid = 0;
        String query;
        try {
            int flag = scanner.nextInt();
            if (flag == 1) {
                System.out.println("Enter your comment:");
                String comm = scanner.next();
                //save comment
                query = "Insert into  public.\"Comment\" (text,u_id,v_id) Values( \'" + comm + "\'," + uid + ", " + vid+ ");";
                System.out.println("Query:" + query);
                stmt = conn.createStatement();
                stmt.execute(query);
                System.out.println("Comment saved!");
                return menu(conn,uid);
            }else if(flag == 3){
                System.out.println("which comment is delete?");
                //int uid = 0;
                query = "DELETE from public.\"Comment\" where u_id = " + uid + " and v_id = " + vid +";";
                System.out.println("Query3: " + query);
                stmt = conn.createStatement();
                stmt.execute(query);
                rs = stmt.getResultSet();
                if (rs.next()) {
                    //show information about video
                    String thumb = rs.getString("thumbnail");
                    String description = rs.getString("description");
                    int len = rs.getInt("length");
                    Date date = rs.getDate("date");
                    vid = rs.getInt("id");
                    System.out.println("Video Information:\nThumbnail: " + thumb + " ,Desciption: " + description + " ,Length: " + len + ", Date: " + date);
                }
            }
             else if (flag == 2) {
                System.out.println("Enter your contact you want to reply:");
                String contact = scanner.next();
                query = "Select * from public.\"Comment\" where u_id in (Select id from public.\"User\" where name = \'" + contact + "\') and v_id = " + vid+";";
                System.out.println("Query:" + query);
                stmt = conn.createStatement();
                if (stmt.execute(query)) {
                    rs = stmt.getResultSet();
                    if(rs.next()) {
                        cid = rs.getInt("id");
                        System.out.println("id" + cid);
                        System.out.println("Enter your comment:");
                        String reply = scanner.next();
                        //save comment
                        query = "Insert into  public.\"Comment\" (text, u_id, v_id, c_id) Values(\'" + reply + "\'," + uid + ", " + vid +"," +cid +");";
                        System.out.println("Query:" + query);
                        stmt = conn.createStatement();
                        stmt.execute(query);
                        System.out.println("Comment saved!");
                        return menu(conn,uid);
                    }
                    else{
                        System.out.println("Contact not found!");
                        return true;
                    }
                }
            }
        }catch (Exception e) {
                System.out.println("Error in comment");
            }
        return true;
    }
    public static boolean showComments(Connection conn, int vid){
        //show comments
        try {
            String query = "Select * from public.\"Comment\" where v_id = " + vid + ";";
            Statement stmt = conn.createStatement();
            if (stmt.execute(query)) {
                System.out.println("Query:" + query);
                ResultSet rs = stmt.getResultSet();
                System.out.println("***Comments***");
                while (rs.next()) {
                    //show comments
                    int i = 1;
                    String text = rs.getString("text");
                    int uid = rs.getInt("u_id");
                    query = "Select * from public.\"User\" where id = " + uid + ";";
                    stmt = conn.createStatement();
                    stmt.execute(query);
                    rs = stmt.getResultSet();
                    String uname = null;
                    if(rs.next()){
                        uname = rs.getString("name");
                    }
                    System.out.println(i + "_"+uname+" say: " + text);
                    i++;
                }
            }
        }
        catch(Exception e){
            System.out.println("Error show Comments");
            }
        return true;
    }
    public static boolean like(int vid, int uid , Connection conn){
        Statement stmt = null;
        ResultSet rs = null;
        String query ;
        try {
            query = "Select * from public.\"View\" where  u_id = " + uid +" and v_id = "+ vid + ";" ;
            stmt = conn.createStatement();
            if (stmt.execute(query)) {
                System.out.println("Query:"+query);
                rs = stmt.getResultSet();
                if (rs.next()) {// dislike -1, like 1, neutral 0
                    int like = rs.getInt("lik");
                    int viewid = rs.getInt("id");
                   // System.out.println("like:" + like + " view: " + viewid);

                    if (like == 0) {
                        query = "Update public.\"View\" Set u_id = " + uid + ", v_id = " + vid + ", lik = " + 1 + " Where id =" + viewid + ";";
                        System.out.println("Query:" + query);
                        stmt = conn.createStatement();
                        stmt.execute(query);
                        System.out.println("added to likes!");

                    } else if (like == 1) {
                        query = "Update public.\"View\" Set u_id = " + uid + ", v_id = " + vid + ", lik= " + 0 + " Where id =" + viewid + ";";
                        System.out.println("Query:" + query);
                        stmt = conn.createStatement();
                        stmt.execute(query);
                        System.out.println("removed from likes!");
                    } else if (like == -1) {
                        query = "Update public.\"View\" Set u_id = " + uid + ", v_id = " + vid + ", lik = " + 1 + " Where id =" + viewid + ";";
                        stmt = conn.createStatement();
                        System.out.println("Query:" + query);
                        stmt.execute(query);
                        System.out.println("added to likes!");


                    }
                }
                else{//hasn't watched this video yet;
                    query = "Insert into public.\"View\" (u_id,v_id,lik) Values(" + uid + "," + vid + "," + 1 + ");"; //true?
                    System.out.println("Query:" + query);
                    stmt = conn.createStatement();
                    stmt.execute(query);
                    System.out.println("added to likes!");
                }
            }
        }
        catch(Exception e){
            System.out.println("error in like");
        }
        return true;
    }
    public static boolean dislike(int vid, int uid, Connection conn){
        Statement stmt = null;
        ResultSet rs = null;
        String query ;
        try {
            query = "Select * from public.\"View\" where  u_id = " + uid +" and v_id = "+ vid + ";" ;
            stmt = conn.createStatement();
            if (stmt.execute(query)) {
                System.out.println("Query:"+query);
                rs = stmt.getResultSet();
                if (rs.next()){ // dislike -1, like 1, neutral 0
                    int like = rs.getInt("like");
                    int viewid = rs.getInt("id");
                    if (like == 0) {
                        query = "Update public.\"View\" Set u_id = " + uid + ", v_id = " + vid + ", lik = " + -1 + " Where id =" + viewid + ";";
                        System.out.println("Query:" + query);
                        stmt = conn.createStatement();
                        stmt.execute(query);
                        System.out.println("added to dislikes!");

                    } else if (rs.getInt("like") == 1) {
                        query = "Update public.\"View\" Set u_id = " + uid + ", v_id = " + vid + ", lik =" + -1 + " Where id =" + viewid + ";";
                        System.out.println("Query:" + query);
                        stmt = conn.createStatement();
                        stmt.execute(query);
                        System.out.println("added to dislikes!");

                    } else if (rs.getInt("like") == -1) {
                        query = "Update public.\"View\" Set u_id = " + uid + ", v_id = " + vid + ", lik = " + 0 + " Where id =" + viewid + ";";
                        System.out.println("Query:" + query);
                        stmt = conn.createStatement();
                        stmt.execute(query);
                        System.out.println("removed from dislikes!");
                        }
                    }
                else{ //hasn't watched this yet
                    query = "Insert into  public.\"View\" (u_id , v_id, lik) Values( " + uid + ", " + vid + "," + -1 + ");"; //true?
                    System.out.println("Query:" + query);
                    stmt = conn.createStatement();
                    stmt.execute(query);
                    System.out.println("added to dislikes!");
                }
            }
        }
        catch(Exception e){
            System.out.println("error in dislike!");
        }
        return true;
    }
    public static boolean share(){
        return true;
    }
    public static boolean showVideo(int uid,int chid, Connection conn){
        //show thumbnail
        String vname = null;
        Statement stmt = null;
        ResultSet rs = null;
        String query;
        try {
            //find video for searching channel
            if (chid > 0) {
                query = "Select * from public.\"Video\" where ch_id = " + chid + ";";
                stmt = conn.createStatement();
                if (stmt.execute(query)) {
                    System.out.println("Query:" + query);
                    System.out.println("***Video***");
                    rs = stmt.getResultSet();
                    System.out.println("id |" + "    date   | " + "length | " + "name | " + "description |" + "thumbnail");
                    //show videos
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        Date date = rs.getDate("date");
                        int len = rs.getInt("length");
                        String name = rs.getString("name");
                        String desc = rs.getString("description");
                        String thumb = rs.getString("thumbnail");
                        System.out.println(id + " | " + date + " | " + len + " |  " + name + " | " + desc + " | " + thumb);
                    }
                }
            }
            //find video for searching name
            int flag = 0;
            System.out.println("Enter videoName:");
            String video = scanner.next();
            while (flag != 1) {
                int vid = 0;
                String query2 = "Select * from public.\"Video\" where name = \'" + video + "\';";
                System.out.println("Query:" + query2);
                stmt = conn.createStatement();
                stmt.execute(query2);
                System.out.println("376");
                rs = stmt.getResultSet();
                if (rs.next()) {
                    //show information about video
                    String thumb = rs.getString("thumbnail");
                    String description = rs.getString("description");
                    int len = rs.getInt("length");
                    Date date = rs.getDate("date");
                    vid = rs.getInt("id");
                    System.out.println("Video Information:\nThumbnail: " + thumb + " ,Desciption: " + description + " ,Length: " + len + ", Date: " + date);
                }
                    flag = 1;
                    showComments(conn, vid);
                    System.out.println("Enter 0 to like, 1 to dislike, 2 to comment, 3 to share, 4 to exit"); //like, dislike, comment, share
                    int voption = scanner.nextInt();
                    if (voption == 0) {
                        return like(vid, uid, conn);
                    } else if (voption == 1) {
                        //dislike
                        return dislike(vid, uid, conn);
                    } else if (voption == 2) {
                        //comment on video or comment on comment
                        return comment(vid, uid, conn);
                    } else if (voption == 3) {
                        //share video
                        return share();
                    } else if (voption == 4) {
                        //exit
                        return true;
                    }
                else {
                    System.out.print("\nVideo not found!Enter 0 to go to menu or video name:");
                    vname = scanner.next();
                    if (vname.equals("0")) {
                        return true;
                    }
                }
            }
        }
        catch (Exception e){
            System.out.println("error in show video");
        }
        return true;
    }
    public static boolean showChannel(int uid,int sw, Connection conn){
        Statement stmt = null;
        ResultSet rs = null;
        String query = null;
        try {
            int chid = 0;
            //show channel of user
            if(sw > 0) {
                query = "Select * from public.\"Channel\" where u_id = " + uid + ";";
                stmt = conn.createStatement();
                if (stmt.execute(query)) {
                    System.out.println("Query:" + query);
                    rs = stmt.getResultSet();
                    //show information about channel
                    while (rs.next()) {
                        //System.out.println("id");
                        chid = rs.getInt("id");
                        Date date = rs.getDate("date");
                        int sub = rs.getInt("subcribeNo");
                        uid = rs.getInt("u_id");
                        String name = rs.getString("name");
                        String prf = rs.getString("profile");
                        System.out.println("Channel Information:\nID: " + chid + " , Date: " + date + " , Subscribe Number: " + sub + " Channel Name: " + name + " ,Profile " + prf);
                    }
                }

            }
            //show channel with name
            else{
                System.out.println("Enter channelName");
                String channel = scanner.next();
                query = "Select * from public.\"Channel\" where name = \'" + channel + "\';";
                stmt = conn.createStatement();
                stmt.execute(query);
                    System.out.println("Query:" + query);
                    rs = stmt.getResultSet();
                    if(rs.next()) {
                        //System.out.println("id");
                        chid = rs.getInt("id");
                        Date date = rs.getDate("date");
                        int sub = rs.getInt("subcribeNo");
                        uid = rs.getInt("u_id");
                        String name = rs.getString("name");
                        String prf = rs.getString("profile");
                        System.out.println("Channel Information\nID: "+chid + " , Date: "  + date+" , Subscribe Number: " + sub+" Channel Name: " +name + " ,Profile " + prf);
                    }
                    else{
                         System.out.println("Channel not found!");
                    }

            }
            System.out.println("Select an option:(1:show videos/2:go to menu/3:delete videos)");
            int op = scanner.nextInt();
            if(op == 1){
                showVideo(uid,chid,conn);
            }else if(op == 3){
                System.out.println("which video is delete?");
                int vid = 0;
                query = "DELETE from public.\"Video\" where name = \' " + vid + "\';";
                System.out.println("Query3: " + query);
                stmt = conn.createStatement();
                stmt.execute(query);
                if (rs.next()) {
                    //show information about video
                    String thumb = rs.getString("thumbnail");
                    String description = rs.getString("description");
                    int len = rs.getInt("length");
                    Date date = rs.getDate("date");
                    vid = rs.getInt("id");
                    System.out.println("Video Information:\nThumbnail: " + thumb + " ,Desciption: " + description + " ,Length: " + len + ", Date: " + date);
                }
            }
            else{
                return menu(conn,uid);
            }
        }
        catch (Exception e){
            System.out.println("error in show channel");
        }

        return true;
    }
    public static boolean showPlaylist( int uid, int pid, Connection conn){
        String vname = null;
        Statement stmt = null;
        ResultSet rs = null;
        String query = null;
        try {
            int flag = 0;
            System.out.println("Enter playList Name:");
            String playList = scanner.next();
            while (flag != 1) {
                int vid = 0;
                query = "Select * from public.\"PlayList\" where name = \'" + playList + "\';";
                System.out.println("Query:" + query);
                stmt = conn.createStatement();
                stmt.execute(query);
                rs = stmt.getResultSet();
                if (rs.next()) {
                    //show information about video
                    int pr = rs.getInt("private");
                    String name = rs.getString("name");
                    pid = rs.getInt("id");
                    System.out.println("PlayList Information:\nprivate: " + pr + " ,name: " + name + " id: "+ pid );
                }
                flag = 1;
                System.out.println("Enter 0 to add, 1 to delete, 2 to exit");
                int poption = scanner.nextInt();
                if (poption == 0) { //add
                    System.out.println("Enter Video name");
                    String viname = scanner.next();
                    query= "Select * from public.\"Video\" where name = " + viname +";";
                    System.out.println("Query:" + query);
                    stmt = conn.createStatement();
                    stmt.execute(query);
                    rs = stmt.getResultSet();
                    if (rs.next()) {
                        vid = rs.getInt("id");
                    }
                    query = "Insert into  public.\"PlayList\" (name, private) Values( \'" + viname + "\'," + 1 + ");";
                    //query = "Select * from public.\"PlayList\" where name = \'" + playList + "\';";
                    System.out.println("Query:" + query);
                    stmt = conn.createStatement();
                    stmt.execute(query);
                    rs = stmt.getResultSet();
                    if (rs.next()) {
                        //show information about video
                        int pr = rs.getInt("private");
                        String name = rs.getString("name");
                        pid = rs.getInt("id");
                        System.out.println("PlayList Information:\nprivate: " + pr + " ,name: " + name + " id: "+ pid );
                    }
                } else if (poption == 1) { //delete
                    //query = "Delete From Playlist Where  ";
                    query = "Select * from public.\"AddTo\" where p_id = "+ pid +" ,v_id = "+ vid + ";";
                    System.out.println("Query:" + query);
                    stmt = conn.createStatement();
                    stmt.execute(query);
                    rs = stmt.getResultSet();
                    if (rs.next()) {
                        //show information about video
                        int pr = rs.getInt("private");
                        String name = rs.getString("name");
                        pid = rs.getInt("id");
                        System.out.println("PlayList Information:\nprivate: " + pr + " ,name: " + name + " id: "+ pid );
                    }
                } else if (poption == 2) { //exit
                    return true;
                }
                else {
                    System.out.print("\nVideo not found!Enter 0 to go to menu or video name:");
                    vname = scanner.next();
                    if (vname.equals("0")) {
                        return true;
                    }
                }
            }
        }
        catch (Exception e){
            System.out.println("error in show playList");
        }
        return true;

    }
    public static boolean menu(Connection conn, int uid) {
        System.out.println("***Menu***\n1.search users\n2.search video\n3.search channels\n4.search playlist\n" +
                "5.my channels\n6.my videos \n7.my playlists\n8.delete Channel\n9.exit");
        int flag, option = 1;
        Statement stmt = null;
        ResultSet rs = null;
        while (option<= 9 || option >= 0) {
            flag=0;
            System.out.println("Select an option");
            option = scanner.nextInt();
            switch (option) {
                case 1: {
                    System.out.println("Enter a Username:");
                    String user = scanner.next();
                    //search between users
                    try{
                        String query = "Select * from public.\"User\" where name = \'" + user +"\';" ;
                        stmt = conn.createStatement();
                        if (stmt.execute(query)) {
                            System.out.println("Query:"+query);
                            rs = stmt.getResultSet();
                            if(rs.next()) {
                                uid = rs.getInt("id");
                                //show user: profile,
                                Date date = rs.getDate("date");
                                String prf = rs.getString("profile");
                                System.out.println("User Information:");
                                System.out.println("Name: "+ user+" | Date: " + date + " | Profile: " + prf);
                                //show channel of user
                                showChannel(uid,1, conn);
                            }
                            else{
                                System.out.println("UserName not found!");
                            }
                        }
                    }
                    catch (Exception e){
                        System.out.println("error in search users");
                    }
                    break;
                }
                case 2: {
                    showVideo(uid, 0 ,conn);
                    break;
                }
                case 3: {
                    //search between channels
                    return showChannel(uid,0, conn);
                }
                case 4: {
                    //showplaylist
                    System.out.println("Enter playlistName");
                    String playList = scanner.next();
                    //search between playlist
                    if (flag == 1) {
                        System.out.println("Playlist not found!");
                    }
                    else if (flag == 2) {
                        //go to playlist
                        //return showPlaylist(playList, conn);
                    }
                    break;
                }
                case 5: {
                    //subscribed channels
                    //search channel
                    stmt = null;
                    rs = null;
                    int chid = 0;
                    try{
                        String query1 = "Select * from public.\"Channel\" where ch_id in (Select * from public.\"Subscribe\" where u_id = " + uid + ") and ch_id = " + chid+";";
                        System.out.println("Query1:"+query1);
                        stmt= conn.createStatement();
                        if(stmt.execute(query1)) {
                            System.out.println("Query:" + query1);
                            rs = stmt.getResultSet();
                            while (rs.next()) {
                                int id = rs.getInt("id");
                                chid = rs.getInt("ch_id");
                                System.out.println("Subscribed Channels:");
                                String chname = rs.getString("name"); //channel table
                                System.out.println("id: " + id + " | u_id: " + uid + " | ch_id: " + chid + "| ch_name: " + chname);
                            }
                            //show channel of user
                            showChannel(uid, 1, conn);
                        }
                    }
                    catch (Exception e){
                        System.out.println("error in showing my channel");
                    }

                    if (flag == 1) {
                        System.out.println("chosen channel not found");
                    } else if (flag == 2) {
                        return showChannel( uid,0,conn);
                    }
                    break;
                }
                case 6: {
                    //show videos of my own channel
                    System.out.println("Enter a video name");
                    String myVideo = scanner.next();
                    //choose video
                    if (flag == 1) {
                        System.out.println("chosen video not found");

                    } else if (flag == 2) {
                        return showVideo(uid, 0, conn); //or like,...
                    }
                    break;
                }
                case 7: {
                    //playlist made by me
                    System.out.println("Enter a playlist");
                    String myPlaylist = scanner.next();
                    //choose playlist
                    if (flag == 1) {
                        System.out.println("chosen playlist not found");
                    } else if (flag == 2) {
                        //showPlaylist(myPlaylist, conn);
                    }
                    break;
                }
                case 8:{
                    //delete my channel
                    System.out.println("Are you sure?");
                    try{
                    String query = "DELETE from public.\"Channel\" where u_id = " + uid + ";";
                    System.out.println("Query3: " + query);
                    stmt = conn.createStatement();
                    stmt.execute(query);
                    rs = stmt.getResultSet();
                    if (rs.next()) {
                        System.out.println("Your channel got deletefd successfully!");
                        return true;
                    }
                    else{
                        System.out.println("Your channel hasn't been deleted!");
                        return true;
                    }
                    }catch(Exception e){
                        System.out.println("Error in deleting your channel");
                        break;
                    }
                }
                case 9: {
                    //exit
                    System.out.println("Are You sure? Enter 0 to exit or go back to menu");
                    int fex = scanner.nextInt();
                    if( fex == 0){
                        System.out.println("GoodBye.");
                        exit(0);
                    } else{
                        System.out.println("You will be transfered to the menu.");
                        return menu(conn, uid);
                    }
                }
            }
        }
        return true;
    }
    public static void main(String[] args){
        final String url = "jdbc:postgresql://localhost:5432/";
        final String user = "postgres";
        final String password = "mona1379";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
        }catch (Exception e) {
            System.out.println("error in main for connection");
        }
        menuLogin(conn);
    }
}
