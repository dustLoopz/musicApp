package com.example;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.io.*;
import java.util.*;
import java.io.IOException;

import org.json.simple.*;
import org.json.simple.parser.*;

public class gui {

    // global variables for the app
    static String status="main";
    static Long position=0L;
    static Integer songIndex=0;
    static Clip audioClip;
    static Map<Integer, Boolean> favorites = new HashMap<Integer, Boolean>();

    AudioInputStream audioInputStream;

    /*
    *** IMPORTANT NOTE  ***
    This next line of code is a "path" that will need to change in order to play music on their
    computer.  The current path is for my laptop, not yours.
    */
    private static String basePath =
    "C:\\Users\\vk102789\\.vscode\\projects\\musicApp\\musicapp\\src\\main\\java\\com\\example";

    public static void musicApp(Scanner input, final JSONArray library){
          final JFrame frame = new JFrame("Java Music App");
          frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
          frame.setSize(700,330);

          // Text Area at the Center
          final JTextArea ta = new JTextArea();

          //Creating the panel at bottom and adding components
          JPanel panel = new JPanel(); // the panel is not visible in output 
          JLabel label = new JLabel("Song");
          final JTextField tf = new JTextField(10); // accepts upto 10 characters
          tf.setText("Enter Song");
          
          JButton play = new JButton("Play");
          JButton stop = new JButton("Stop");
          JButton pause = new JButton("Pause");
          JButton forward = new JButton("FWD");
          JButton rewind = new JButton("RWD");
          JButton search = new JButton("Search");
          JButton quit = new JButton("Quit");
          JButton list = new JButton("List");
          JButton fav = new JButton("Favorite");

          final JMenuBar mb = new JMenuBar();
          final JMenu m1 = new JMenu("List");
          mb.add(m1);
          
          //Initialize Favorites to False
          for(int i = 0; i < 9; i++){
               favorites.put(songIndex,false);
          }

          play.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){

               if (songIndex != -1){
                    play(library, songIndex);
                    ta.setText("Song Playing: " + library.get(songIndex).toString()+
                               "\nFavorite: " + favorites.get(songIndex));
               } else{
                    ta.setText("Song not Found");
               }
               
          }});

          stop.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){
               stop();
               ta.setText("Song Stopped");
          }});

          pause.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){
               pause();
               ta.setText("Song Paused");
          }});

          forward.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){
               forward();
               ta.setText("Song Advanced");
          }});

          rewind.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){
               rewind();
               ta.setText("Song Rewinded");
          }});

          quit.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){
               System.exit(0);
          }});

          fav.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){
               
               if(favorites.get(songIndex) == null){
                    favorites.put(songIndex,true);
               } else if(favorites.get(songIndex)){
                    favorites.put(songIndex,false);
               } else{
                    favorites.put(songIndex,true);
               }
               
          }});

          quit.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){
               System.exit(0);
          }});

          search.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){
               String searchSong = tf.getText();
               int nSong = findSong(library, searchSong);
               
               if (nSong != -1){
                    songIndex = nSong;
                    play(library, songIndex);
                    ta.setText("Song Playing: " + library.get(songIndex).toString() +
                               "\nFavorite: " + favorites.get(songIndex));
               } else{
                    ta.setText("Song not Found");
               }
          }});
          
          list.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){
               //JTable playListTable = new JTable();
               //Creating the MenuBar and adding components

               if (m1.getMenuComponentCount() > 1){
                    m1.removeAll();
               }

               int max=library.size();
               String playList = "--------------------->Playlist<---------------------\n";
               
               for(int i = 1; i < max; i++){
                    playList += "[" + i + "]: " + library.get(i-1).toString() + "\n" ;
                    
                    m1.add("[" + i + "]: " + library.get(i-1).toString());
                    final int songNumber = i-1; 
                    JMenuItem item = m1.getItem(i-1);
                    item.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){
                         ta.setText("Button Pressed");
                         play(library, songNumber);
                         ta.setText(library.get(songNumber).toString());
                         songIndex = songNumber;
                    }});
               }
               
               ta.setText(playList);
               
          }});

          // Components Added using Flow Layout
          panel.add(list);
          panel.add(search);
          panel.add(label);
          panel.add(tf);
          panel.add(fav);       
          panel.add(play);
          panel.add(rewind);
          panel.add(pause);
          panel.add(forward);
          panel.add(stop);
          panel.add(quit);

          //Adding Components to the frame.
          frame.getContentPane().add(BorderLayout.SOUTH, panel);
          frame.getContentPane().add(BorderLayout.NORTH, mb);
          frame.getContentPane().add(BorderLayout.CENTER, ta);
          frame.setVisible(true);
     }

     public static void main(String args[]){
          // reading audio library from json file
          JSONArray library = readAudioLibrary();

          // create a scanner for user input
          Scanner input = new Scanner(System.in);

          musicApp(input, library);
     }

     void logComment(String location, String comment ){
          //https://www.tutorialspoint.com/how-to-write-create-a-json-file-using-java
          //Creating a JSONObject object
          //JSONObject jsonObject = new JSONObject();
          //jsonObject.put("key", "value");
          // FileWriter file = new FileWriter("C:/output.json");
          //file.write(jsonObject.toJSONString());
          //file.close();
     }
     
     /*
     * Find Song function
     */
     public static Integer findSong(JSONArray library, String name){

          int max=library.size();
          Integer index=-1;

          for(int i = 0; i < max-1; i++){
               if(library.get(i).toString().contains(name)){
                    index = i;
               }
          }
     
          return index;
     }

 
     // Method to stop the audio
     public static void stop()
     {
          position = 0L;
          audioClip.stop();
          audioClip.close();
     }
 
     // Method to pause the audio
     public static void pause() 
     {
          position = audioClip.getMicrosecondPosition();
          //System.out.println(position);
          audioClip.stop();
  
     }
 
     // Method to pause the audio
     public static void rewind() 
     {
          position = audioClip.getMicrosecondPosition();
          //System.out.println("Start Position: " +position);
          if (position < 5000000L){
               position = 0L;
          } else {
               position -= 5000000L;
          }
          audioClip.setMicrosecondPosition(position);
          
  
     }

     // Method to pause the audio
     public static void forward() 
     {
          position = audioClip.getMicrosecondPosition();
          //System.out.println("Start Position: " +position);
          if (position > audioClip.getMicrosecondLength()-5000000L){
               position = audioClip.getMicrosecondLength()-1000000L;
          } else{
               position += 5000000L;
          }
          audioClip.setMicrosecondPosition(position);
          
          
     }

     /*
     * plays an audio files
     */
   public static void play(JSONArray library, Integer songIndex) {
     // open the audio file
 
     JSONObject obj = (JSONObject) library.get(songIndex);
     final String filename = (String) obj.get("filename");
     final String filePath = basePath + "/wav/" + filename;
     final File file = new File(filePath);
     
     // stop the current song from playing, before playing the next one
     if (audioClip != null) {
       audioClip.close();
     }
 
     try {
       // create clip
       audioClip = AudioSystem.getClip();
 
       // get input stream
       final AudioInputStream in = AudioSystem.getAudioInputStream(file);
          
       audioClip.open(in);
       
       if(position == audioClip.getMicrosecondLength()){
          position=0L;
       }

       audioClip.setMicrosecondPosition(position);
       System.out.println(position);
       
       audioClip.start();
       //audioClip.loop(Clip.LOOP_CONTINUOUSLY);
     } catch (Exception e) {
       e.printStackTrace();
     }
 
     System.out.println(library.get(songIndex).toString());
     position=0L;
     status = "play";
   }

   //
   // Func: readJSONFile
   // Desc: Reads a json file storing an array and returns an object
   // that can be iterated over
   //
   public static JSONArray readJSONArrayFile(String fileName) {
     // JSON parser object to parse read file
     JSONParser jsonParser = new JSONParser();
 
     JSONArray dataArray = null;
 
     try (FileReader reader = new FileReader(fileName)) {
       // Read JSON file
       Object obj = jsonParser.parse(reader);
 
       dataArray = (JSONArray) obj;
       // System.out.println(dataArray);
 
     } catch (FileNotFoundException e) {
       e.printStackTrace();
     } catch (IOException e) {
       e.printStackTrace();
     } catch (ParseException e) {
       e.printStackTrace();
     }
 
     return dataArray;
   }
 
   // read the audio library of music
   public static JSONArray readAudioLibrary() {
     final String jsonFileName = "audio-library.json";
     final String filePath = basePath + "/" + jsonFileName;
 
     JSONArray jsonData = readJSONArrayFile(filePath);
 
     System.out.println("Reading the file " + filePath);
 
     return jsonData;
   }
}