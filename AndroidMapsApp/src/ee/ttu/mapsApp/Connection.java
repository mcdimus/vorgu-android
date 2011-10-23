package ee.ttu.mapsApp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.client.ClientProtocolException;

import ttu.vorgu2.hw1.Message;

public class Connection {

	// public static long userId;
	public Message message;

	public boolean connect(String parameters, String urlToConnect)
			throws ClientProtocolException, IOException, ClassNotFoundException {
		
		HttpURLConnection connection;
		OutputStreamWriter request = null;

		boolean connected = false;
		URL url = null;
		// Sending request to the server
		url = new URL(urlToConnect);
		connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		connection.setRequestMethod("POST");

		request = new OutputStreamWriter(connection.getOutputStream());
		request.write(parameters);
		request.flush();
		request.close();

		// Getting response from the server
		ObjectInputStream objectReader = new ObjectInputStream(connection
				.getInputStream());
		// Reading data from the server
		message = (Message) objectReader.readObject();
		if (message.isOk()) {
			if(message.getGroups() != null) {
				LocalData.setGroups(message.getGroups());
			}
			if(message.getUserId() != 0) {
				LocalData.setUserId(message.getUserId());
			}
			if (message.getPersons() != null) {
				LocalData.setPersons(message.getPersons());
			}
			if (message.getGroupname() != null) {
				LocalData.setMyGroup(message.getGroupname());
			}
			connected = true;
		}
		objectReader.close();

		return connected;
	}
}
