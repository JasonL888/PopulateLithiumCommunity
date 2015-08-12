package com.lauwtj;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

public class RandomMessage {
	private List<String> lines;

	public RandomMessage() {
		// TODO Auto-generated constructor stub
		try {
			lines = Files.readAllLines(Paths.get("./src/com/lauwtj/dictionary.txt"), Charset.defaultCharset());
		}
		catch( IOException ex) {
			ex.printStackTrace();
		}
		
	}
	
	public void printAllLines() {
		for (String line: lines )
		{
			System.out.println(line);
		}
	}
	
	public String getRandomMessage() {
		int i;
		String message = "";
		for (i=0; i<5; i++){
			message = lines.get(new Random().nextInt(lines.size())) + (i==0?message:("%20" + message)) ;
		}
		return message;
	}
	
	public String getRandomMessage(int length) {
		int i;
		String message = "";
		for (i=0; i<length; i++){
			message = lines.get(new Random().nextInt(lines.size())) + (i==0?message:("%20" + message)) ;
		}
		return message;
	}
	
	public String getRandomWord() {
		return( lines.get(new Random().nextInt(lines.size()))); 
	}
	
	public static void main(String[] args) {
		RandomMessage msg = new RandomMessage();
//		msg.printAllLines();
		for(int i=0; i<10; i++){
			System.out.println("line " + i + ":" + msg.getRandomMessage() );
		}
		
		System.out.println("random word:" + msg.getRandomWord());	
		System.out.println("random message of 30 words:" + msg.getRandomMessage(30));
		
	}

}
