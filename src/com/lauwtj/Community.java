package com.lauwtj;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Community {
	// setup - verify the following
	private String userLogin = "RestApi";
	private String userPassword = "XX-UPDATE-XX";
	// setup - for user above, permissions required
	//       - User Management - switch to another user
	//       - Accepted Solutions - Accept or retract solutions
	//       - REST API - make REST API calls with read access, write access
	//       - Add BlogAuthor role
	static private String communityHost = "XX-UPDATE-XX.stage.lithium.com"; 
	private String[][] userCreds = {
			{ "normaluser0", "XX-UPDATE-XX" },
			{ "normaluser1", "XX-UPDATE-XX" },
			{ "normaluser2", "XX-UPDATE-XX" },
			{ "normaluser3", "XX-UPDATE-XX" },
			{ "normaluser4", "XX-UPDATE-XX" }
	};
	private String blogAuthor = "blogauthor0";

	
	private String restUrlBase = "http://" + communityHost + "/restapi/vc/";
	private String restAuthUrl = restUrlBase + "authentication/sessions/";
	private String restBoardUrl = restUrlBase + "boards/id/";
	private String restMessageUrl = restUrlBase + "messages/id/";
	private String restApiKey;


	static {
		javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(new javax.net.ssl.HostnameVerifier() {
			public boolean verify(String hostname, javax.net.ssl.SSLSession sslSession){
				if (hostname.equals(communityHost)) {
					return true;
				}
				return false;
			}
		});
	}

	
	public Community()
	{
		restApiKey = getUserRestApiKey(userLogin, userPassword);
	}
	
	public String getUserCreds( String user ) {
		String password = "";
		for (int i=0; i<userCreds.length;i++)
		{
			if ( userCreds[i][0].equals(user)) {
				password = userCreds[i][1];
			}
		}
		return password;
	}
	
	public String getBlogAuthor() {
		return blogAuthor;
	}
	
	public String getUserRestApiKey( String user, String password )
	{
		String userRestApiKey = "";
		try {

            System.out.println("\n\nAuthentication ...");

			// Login
			URL url = new URL( restAuthUrl + "/login?user.login=" + user + "&user.password=" + password); 
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");

			OutputStream os = conn.getOutputStream();
			os.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(conn.getInputStream());
			doc.getDocumentElement().normalize();
			printDocument(doc, System.out);
			System.out.println("Root element:" + doc.getDocumentElement().getNodeName());
			userRestApiKey = doc.getElementsByTagName("value").item(0).getTextContent();

			conn.disconnect();
		} catch (Exception ex) {
			ex.printStackTrace();
		} 
		return userRestApiKey;
	}
	
	public void printDocument( Document doc, OutputStream out ) throws IOException, TransformerException {
	    TransformerFactory tf = TransformerFactory.newInstance();
	    Transformer transformer = tf.newTransformer();
	    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
	    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
	    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

	    transformer.transform(new DOMSource(doc), 
	         new StreamResult(new OutputStreamWriter(out, "UTF-8")));
	}
	
	// author in "normaluser0" format
	// messageSubject and messageBody are HTML encoded, eg. %20 for whitespace
	public String postTopic( String boardId, String messageSubject, String messageBody, String author ) {
		String messageId = "";
		
		try {
	        System.out.println("\n\nFirst Topic Post ...");
	        String urlString = restBoardUrl + boardId + "/messages/post/?message.subject=" + messageSubject + "&message.body=" + messageBody + "&message.author=login/" + author + "&restapi.session_key=" + restApiKey;
			System.out.println("urlString:" + urlString);
	        URL url = new URL( urlString );
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
	
			OutputStream os = conn.getOutputStream();
			os.flush();
	
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK && conn.getResponseCode() != HttpURLConnection.HTTP_MOVED_TEMP) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(conn.getInputStream());
			doc.getDocumentElement().normalize();
			printDocument(doc, System.out);
			System.out.println("Root element:" + doc.getDocumentElement().getNodeName());
			NodeList nList = doc.getElementsByTagName("message");
			for(int i=0; i<nList.getLength(); i++){
				Node nNode = nList.item(i);
				System.out.println("Current element:" + nNode.getNodeName());
				if (nNode.getNodeType() == Node.ELEMENT_NODE){
					Element eElement = (Element)nNode;
					System.out.println("id:" + eElement.getElementsByTagName("id").item(0).getTextContent());
					messageId = eElement.getElementsByTagName("id").item(0).getTextContent();
				}
			}
	
			conn.disconnect();

		} catch (Exception ex ){
			ex.printStackTrace();
		}
		
		return messageId;
	}

	// author in "login/normaluser0" format
	// messageBody are HTML encoded, eg. %20 for whitespace
	public String replyMessage( String messageId, String messageBody, String author ) {
		String replyMessageId = "";
		
		try {
	        System.out.println("\n\nReplying ...");
	        String urlString = restMessageUrl + messageId + "/reply/?message.body=" + messageBody + "&message.author=login/" + author + "&restapi.session_key=" + restApiKey;
			URL url = new URL( urlString );
			System.out.println( "urlString:" + urlString );
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
	
			OutputStream os = conn.getOutputStream();
			os.flush();
	
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK && conn.getResponseCode() != HttpURLConnection.HTTP_MOVED_TEMP) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(conn.getInputStream());
			doc.getDocumentElement().normalize();
			System.out.println("Root element:" + doc.getDocumentElement().getNodeName());
			NodeList nList = doc.getElementsByTagName("message");
			for(int i=0; i<nList.getLength(); i++){
				Node nNode = nList.item(i);
				System.out.println("Current element:" + nNode.getNodeName());
				if (nNode.getNodeType() == Node.ELEMENT_NODE){
					Element eElement = (Element)nNode;
					System.out.println("id:" + eElement.getElementsByTagName("id").item(0).getTextContent());
					replyMessageId = eElement.getElementsByTagName("id").item(0).getTextContent();
				}
			}
	
			conn.disconnect();

		} catch (Exception ex ){
			ex.printStackTrace();
		}
		
		return replyMessageId;
	}

	// author in "normaluser0" format
	public void kudoMessage( String messageId, String author ) {
		try {
	        System.out.println("\n\nKudoing ...");
	        String urlString = restMessageUrl + messageId + "/kudos/give/?message.author=login/" + author + "&restapi.session_key=" + this.getUserRestApiKey(author, this.getUserCreds(author));
			URL url = new URL( urlString );
			System.out.println("urlString:" + urlString );
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
	
			OutputStream os = conn.getOutputStream();
			os.flush();
	
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK && conn.getResponseCode() != HttpURLConnection.HTTP_MOVED_TEMP) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}
	
			conn.disconnect();

		} catch (Exception ex ){
			ex.printStackTrace();
		}
		
		return;
	}
	
	// author in "normaluser0" format
	public void tagMessage( String messageId, String tagString, String author ) {
		try {
	        System.out.println("\nTagging  ...");
	        String urlString = restMessageUrl + messageId + "/tagging/tags/add?tag.add=" + tagString + "&message.author=login/" + author + "&restapi.session_key=" + this.getUserRestApiKey(author, this.getUserCreds(author));
			URL url = new URL( urlString );
			System.out.println("urlString:" + urlString );
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
	
			OutputStream os = conn.getOutputStream();
			os.flush();
	
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK && conn.getResponseCode() != HttpURLConnection.HTTP_MOVED_TEMP) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}
	
			conn.disconnect();

		} catch (Exception ex ){
			ex.printStackTrace();
		}
		
		return;
	}

	public void markSolution( String messageId, String author ) {
		try {
	        System.out.println("\n\nMark solution ...");
	        String urlString = restMessageUrl + messageId + "/solutions/solution/mark/?message.author=login/" + author + "&restapi.session_key=" + this.getUserRestApiKey(author, this.getUserCreds(author));
			URL url = new URL( urlString );
			System.out.println("urlString:" + urlString );
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
	
			OutputStream os = conn.getOutputStream();
			os.flush();
	
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK && conn.getResponseCode() != HttpURLConnection.HTTP_MOVED_TEMP) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}
	
			conn.disconnect();

		} catch (Exception ex ){
			ex.printStackTrace();
		}
		
		return;
	}
	
	public static void main(String[] args) {
        Community community = new Community();
        String messageId;
        String replyMessageId;
        
        // Forum Board
        messageId = community.postTopic("HR-experience", "myYY%20ok%20post%20Y", "my%20body", "normaluser0");
    
        System.out.println("messageId:" + messageId);
        
        replyMessageId = community.replyMessage(messageId, "myYY%20very%20good%20yippee", "normaluser1");
        System.out.println("replyMessageId:" + replyMessageId);
        
        community.kudoMessage(replyMessageId, "normaluser0");
        community.markSolution(replyMessageId, "normaluser0");
        community.tagMessage(replyMessageId, "newTag", "normaluser0");
        
        // Blogs
        /*
        messageId = community.postTopic("TestBlog1", "myXX%20new%20blog%20post", "this%20just%awesome", "blogauthor0");    
        replyMessageId = community.replyMessage(messageId, "IXX%20agree", "normaluser1");
        System.out.println("replyMessageId:" + replyMessageId);
        
        community.kudoMessage(messageId, "normaluser0");
        community.tagMessage(messageId, "myTag", "normaluser0");
        */
        
        System.out.println( "user rest api key:" + community.getUserRestApiKey("normaluser0", "lithium123"));
        System.out.println("User creds:" + community.getUserCreds("normaluser2"));

	}

}
