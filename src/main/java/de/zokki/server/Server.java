package de.zokki.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Date;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import de.zokki.server.Utils.Constants;

public class Server {

    public static void main(String[] args) throws IOException {
	HttpServer server = HttpServer.create(new InetSocketAddress(1337), 0);
	server.createContext("/", new HttpHandler() {

	    @Override
	    public void handle(HttpExchange httpExchange) throws IOException {
		BufferedInputStream input = new BufferedInputStream(httpExchange.getRequestBody());
		ByteArrayOutputStream received = new ByteArrayOutputStream();
		byte[] buffer = new byte[32768];
		int length;
		while ((length = input.read(buffer)) != -1) {
		    received.write(buffer, 0, length);
		}
		if (received.size() != 0) {
		    String receivedString = received.toString(Constants.RECEIVED_CHARSET);
		    System.out.println(receivedString);
		}

		httpExchange.getResponseHeaders().add("content-type", "text/html; charset=" + Constants.SEND_CHARSET);
		String response = "<b>" + new Date() + "</b> for " + httpExchange.getRequestURI();
		byte[] responseBytes = response.getBytes(Constants.SEND_CHARSET);
		httpExchange.sendResponseHeaders(200, responseBytes.length);

		BufferedOutputStream output = new BufferedOutputStream(httpExchange.getResponseBody());
		output.write(responseBytes);
		output.flush();
		output.close();
		input.close();
	    }
	});
	server.start();
    }
}
