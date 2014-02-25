/**
 * Set up a connection to the server with IP and port number from the config file.
 * Contains a looped recive method which checks if any new data is available.
 * Contains a send method which takes in objects, put them into a linked list and
 * send it to the server.
 * 
 * @author Erik Hermansson, David Stromner
 * @version 2013-02-25
 */

package model;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Observable;

public class Communication extends Observable {

	private Socket socket;
	private BufferedInputStream checkIn;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private String ip;
	private int portNumber;
	private Boolean socketOpen;

	/**
	 * Read port number and IP from config.txt which the client needs to create
	 * a socket against.
	 */
	public Communication() {
		socketOpen = false;
	}

	/**
	 * Set up a new socket and connect to the given ip and port number.
	 */
	private void connect() {
		String s = FileManagement.getInstance().readLine("config.txt");
		String[] sArr = s.split(":");
		ip = sArr[0];
		portNumber = Integer.parseInt(sArr[1]);
		
		if (socket == null) {
			try {
				socket = new Socket(InetAddress.getByName(ip), portNumber);
				socket.setSoTimeout(10000);

				out = new ObjectOutputStream(socket.getOutputStream());
				socketOpen = true;

				new Thread() {
					public void run() {
						recieve();
					}
				}.start();
			} catch (IOException e) {
				e.printStackTrace();
				disconnect();
			}
		}
	}

	/**
	 * Close down the socket.
	 */
	public void disconnect() {
		try {
			// Possible null pointers exception can happen if these are not
			// checked
			out.close();
			out = null;
			in.close();
			in = null;
			socket.close();
			socket = null;
			socketOpen = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the socket's input stream if one of the following is met:
	 * 
	 * 1) Socket's input isn't connected.
	 * 
	 * 2) The object input stream is null.
	 */
	private void openInputStream() {
		if (in == null) {
			try {
				checkIn = new BufferedInputStream(socket.getInputStream());
				in = new ObjectInputStream(checkIn);
			} catch (IOException e) {
				e.printStackTrace();
				disconnect();
			}
		}
	}

	/**
	 * @param args
	 *            Additional arguments to be sent with the message.
	 */
	public void send(Object... args) {
		connect();

		LinkedList<Object> argsList = new LinkedList<Object>();
		for (Object o : args) {
			argsList.add(o);
		}

		try {
			out.writeObject(argsList);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
			disconnect();
		}
	}

	/**
	 * 
	 */
	public void recieve() {
		openInputStream();
		while (socketOpen) {
			try {
				// Naughty but works.
				while (checkIn.available() != 0) {
					// Check if there is any data waiting
					setChanged();
					notifyObservers(in.readObject());
				}
			} catch (Exception e) {
				e.printStackTrace();
				disconnect();
			}
		}
	}
}
