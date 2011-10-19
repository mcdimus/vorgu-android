package ee.ttu.mapsApp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.client.ClientProtocolException;

import ttu.vorgu2.hw1.Message;

public class Connection {
	public boolean connect(String parameters, String urlToConnect)
			throws ClientProtocolException, IOException, ClassNotFoundException {
		
		HttpURLConnection connection;
		OutputStreamWriter request = null;

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
		Message message = (Message) objectReader.readObject();
		if (message.isOk()) {
			if(message.getGroups() != null) {
				ListViewActivity.groups = message.getGroups();
			}
			if(message.getUserId() != 0) {
				ListViewActivity.setUserId(message.getUserId());
			}

			return true;
		}
		objectReader.close();

		return false;
	}

}
