package com.lauwtj;

import java.util.Random;

public class PopulateCommunity {

	public PopulateCommunity() {

	}

	public static void main(String[] args) {
		RandomUsers user = new RandomUsers();
		RandomMessage msg = new RandomMessage();
		// Setup needed 
		//     - username/password/host in Community.java !!!
		//     - numTopics
		//     - forumBoardIdList - list of Forum board IDs to populate
		//     - blogBoardIdList - list of Blog board IDs to populate
		int numTopics = 10;
		String[] forumBoardIdList = {"HR-experience", "testboard1"};
		String[] blogBoardIdList = {"TestBlog1"};
		
        Community community = new Community();
        String messageId;
        String replyMessageId;        
        
        for (int j=0; j<forumBoardIdList.length; j++){
			for (int i=0; i<numTopics; i++)
			{
				String[] userList = user.getUserPair();
				String user1 = userList[0];
				String user2 = userList[1];
				System.out.println("user 1:" + user1);
				System.out.println("user 2:" + user2);
				String msgSubject = msg.getRandomMessage(5);
				String msgBody = msg.getRandomMessage(100);
				System.out.println("msgSubject:" + msgSubject);
				System.out.println("msgBody:" + msgBody);
		        messageId = community.postTopic(forumBoardIdList[j], msgSubject, msgBody, user1);
		        System.out.println("messageId:" + messageId);
		        try {
		            Thread.sleep(5000);                 //5000 milliseconds is five second.
		        } catch(InterruptedException ex) {
		            Thread.currentThread().interrupt();
		        }
		        
		        String replyBody = msg.getRandomMessage(100);
		        System.out.println("replyBody:" + replyBody);
		        replyMessageId = community.replyMessage(messageId, replyBody, user2);
		        System.out.println("replyMessageId:" + replyMessageId);
		        try {
		            Thread.sleep(5000);                 //5000 milliseconds is five second.
		        } catch(InterruptedException ex) {
		            Thread.currentThread().interrupt();
		        }
		        
		        int kudoFlag = new Random().nextInt(2);
		        System.out.println("kudoFlag:" + kudoFlag);
		        if ( kudoFlag == 1 )
		        {
		        	community.kudoMessage(replyMessageId, user1);
		        }
		        int markSolutionFlag = new Random().nextInt(2);
		        System.out.println("markSolutionFlag:" + markSolutionFlag);
		        if ( markSolutionFlag == 1)
		        {
		        	community.markSolution(replyMessageId, user1);
		        }
		        int tagFlag = new Random().nextInt(2);
		        System.out.println("tagFlag:" + tagFlag);
		        if ( tagFlag == 1 )
		        {
		        	community.tagMessage(replyMessageId, msg.getRandomWord(), user1);
		        }
				
			}
        }

        for (int k=0; k<blogBoardIdList.length; k++){
			for (int l=0; l<numTopics; l++)
			{
				String[] userList = user.getUserPair();
				String user1 = userList[0];
				String user2 = userList[1];
				System.out.println("user 1:" + user1);
				System.out.println("user 2:" + user2);
				String msgSubject = msg.getRandomMessage(5);
				String msgBody = msg.getRandomMessage(100);
				System.out.println("msgSubject:" + msgSubject);
				System.out.println("msgBody:" + msgBody);
		        messageId = community.postTopic(blogBoardIdList[k], msgSubject, msgBody, community.getBlogAuthor());
		        System.out.println("messageId:" + messageId);
		        try {
		            Thread.sleep(5000);                 //5000 milliseconds is five second.
		        } catch(InterruptedException ex) {
		            Thread.currentThread().interrupt();
		        }
		        
		        String replyBody = msg.getRandomMessage(100);
		        System.out.println("replyBody:" + replyBody);
		        replyMessageId = community.replyMessage(messageId, replyBody, user2);
		        System.out.println("replyMessageId:" + replyMessageId);
		        try {
		            Thread.sleep(5000);                 //5000 milliseconds is five second.
		        } catch(InterruptedException ex) {
		            Thread.currentThread().interrupt();
		        }
		        
		        int kudoFlag = new Random().nextInt(2);
		        System.out.println("kudoFlag:" + kudoFlag);
		        if ( kudoFlag == 1 )
		        {
		        	community.kudoMessage(messageId, user1);
		        }
		        int tagFlag = new Random().nextInt(2);
		        System.out.println("tagFlag:" + tagFlag);
		        if ( tagFlag == 1 )
		        {
		        	community.tagMessage(messageId, msg.getRandomWord(), user1);
		        }
				
			}
        }
        
	}

}
