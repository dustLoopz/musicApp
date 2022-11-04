package com.example;

import static javax.sound.sampled.AudioSystem.*;
import javax.sound.sampled.*;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;

import java.io.*;
import java.util.*;
import java.io.IOException;

import org.json.simple.*;
import org.json.simple.parser.*;

/*
    To compile: javac SpotifyLikeApp.java
    To run: java SpotifyLikeApp
 */

// declares a class for the app
public class App 
{

    // global variables for the app
    static String status="main";
    static Long position;
    static Integer songIndex=0;
    static Clip audioClip;

    AudioInputStream audioInputStream;

    /*
    *** IMPORTANT NOTE  ***
    This next line of code is a "path" that students will need to change in order to play music on their
    computer.  The current path is for my laptop, not yours.
    */
    private static String basePath =
    "C:\\Users\\vk102789\\.vscode\\projects\\musicApp\\musicapp\\src\\main\\java\\com\\example";
    
    // "main" makes this class a java app that can be executed
    public static void main( String[] args )
    {
        // reading audio library from json file
        JSONArray library = readAudioLibrary();

        // create a scanner for user input
        Scanner input = new Scanner(System.in);

        String userInput = "";
        while (!userInput.equals("q") || status == "quit") {
          
          menu();

          // get input
          userInput = input.nextLine();
  
          // accept upper or lower case commands
          userInput = userInput.toLowerCase();

          if (userInput.equals("l") ){
            songIndex = playList(input, library);
            userInput = "p";
          }

          handleMenu(userInput, library, songIndex);
          
          
        }

        // close the scanner
        input.close();
    }

  /*
   * displays the menu for the app
   */
  public static void menu() {
    System.out.println("---- SpotifyLikeApp ----");
    System.out.println("[H]ome");
    
    System.out.println("[F]ind by title");
    System.out.println("[L]ibrary");

    if(status == "main"){
      System.out.println("[P]lay");
    } else if (status == "play"){
      System.out.println("[P]ause");
      System.out.println("[S]top");
    } else if (status == "paused"){
      System.out.println("[P]lay");
      System.out.println("[S]top");
    }

    System.out.println("[Q]uit");

    System.out.println("");
    System.out.print("Enter q to Quit:");
  }

  /*
   * handles the user input for the app
   */
  public static void handleMenu(String userInput, JSONArray library, Integer songIndex) {
    switch (userInput) {
      case "h":
        System.out.println("-->Home<--");
        break;
      case "f":
        System.out.println("-->Search by title<--");
        break;
      case "l":
        System.out.println("-->Library<--");
        //playList(library);
        break;
      case "p":
        System.out.println("-->Play<--");
        if(status == "play"){
          pause();
        } else{
          play(library, songIndex);
        }
        break;
      case "s":
        System.out.println("-->Stop<--");
        stop();
        break;
      case "q":
        System.out.println("-->Quit<--");
        break;
      default:
        break;
    }
  }

  /*
   * playlist function
   */
  public static Integer playList(Scanner userInput, JSONArray library){

    int max=library.size();
    System.out.println("Max: " + max);
    Integer index;

    System.out.println("--------->Playlist<---------");
    for(int i = 1; i < max; i++){
      System.out.println("[" + i + "]: " + library.get(i-1).toString());
    }
    System.out.println("\nEnter number of song to play:");
    index = getNumber(userInput, max);

    return index;
  }

  /*
   * plays spefic song in playlist
   * Overloaded function takes a library and song number
   */

  // Method to play the audio
  public static void play(JSONArray library) {

    //start the clip
    audioClip.start();
      
    status = "play";
  }

// Method to stop the audio
public static void stop() 
//throws UnsupportedAudioFileException, IOException, LineUnavailableException 
{
    position = 0L;
    audioClip.stop();
    audioClip.close();

    status = "main";
}

// Method to pause the audio
public static void pause() 
{
    if (status.equals("paused")) 
    {
        System.out.println("audio is already paused");
        return;
    }
    position = audioClip.getMicrosecondPosition();
    audioClip.stop();
    status = "paused";
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
      audioClip.setMicrosecondPosition(0);
      audioClip.loop(Clip.LOOP_CONTINUOUSLY);
    } catch (Exception e) {
      e.printStackTrace();
    }

    System.out.println(library.get(songIndex).toString());

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


  // Get value from user
	private static Integer getNumber(Scanner userInput, int max) {
		Boolean success = true;
		Integer output=0;

		do{
			try {
				System.out.println("Please enter song number to play:");
				output = userInput.nextInt();
        if (output < 0 || output > max-1){
          System.out.println("That song doesn't exist!");
          success=false;
        } else{
          success = true;
        }
				
			//Invalid input exception caught to prevent program crashing
			} catch(Exception e) {                                   			
				System.out.println("Invalid Input!");
				success = false;                                     //Setting false flag
				userInput.next();                                    //Clearing Buffer
			}   
		} while(success == false);
		userInput.nextLine(); 
		return output;
    }
}
