package com.lauwtj;

import java.util.Random;

public class RandomUsers {
	// pre-requisite: following users must be created on env
	private String[] userList = { 
			"normaluser0",
			"normaluser1",
			"normaluser2",
			"normaluser3",
			"normaluser4"
			};
	
	
	public RandomUsers() {
		// TODO Auto-generated constructor stub
	}
	
	public String[] getUserPair() {
		String[] userPair = new String[2];
		
		String firstUser = userList[new Random().nextInt(userList.length)];
		String secondUser = userList[new Random().nextInt(userList.length)];
		
		while( firstUser.equals(secondUser)){
			secondUser = userList[new Random().nextInt(userList.length)];
		}
		
		userPair[0] = firstUser;
		userPair[1] = secondUser;
		
		return(userPair);
	}
	
	public static void main(String[] args) {
		RandomUsers user = new RandomUsers();
		String[] userList = user.getUserPair();
		System.out.println("user 1:" + userList[0]);
		System.out.println("user 2:" + userList[1]);		
	}

}
