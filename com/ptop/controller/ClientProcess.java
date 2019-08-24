package com.ptop.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import com.ptop.client.ClientDataRateComparator;
import com.ptop.client.DataProcessor;
import com.ptop.commons.BitField;
import com.ptop.commons.CommonProperties;
import com.ptop.commons.DataMessage;
import com.ptop.commons.DataMessageWrapper;
import com.ptop.commons.LogGenerator;
import com.ptop.remote.ServerClientHandler;
import com.ptop.remote.ServerClientInfo;
import com.ptop.thread.ListeningThread;
import com.ptop.utils.Utility;

public class ClientProcess {

	public ServerSocket getListeningSocket() {
		return listeningSocket;
	}

	public void setListeningSocket(ServerSocket listeningSocket) {
		this.listeningSocket = listeningSocket;
	}

	public int getListeningPort() {
		return listeningPort;
	}

	public void setListeningPort(int listeningPort) {
		this.listeningPort = listeningPort;
	}

	public String getClientIP() {
		return clientIP;
	}

	public void setClientIP(String clientIP) {
		this.clientIP = clientIP;
	}

	public static String getClientID() {
		return clientID;
	}

	public static void setClientID(String clientID) {
		ClientProcess.clientID = clientID;
	}

	public int getClientIndex() {
		return clientIndex;
	}

	public void setClientIndex(int clientIndex) {
		this.clientIndex = clientIndex;
	}

	public Thread getListeningThread() {
		return listeningThread;
	}

	public void setListeningThread(Thread listeningThread) {
		this.listeningThread = listeningThread;
	}

	public static BitField getOwnBitField() {
		return ownBitField;
	}

	public static void setOwnBitField(BitField ownBitField) {
		ClientProcess.ownBitField = ownBitField;
	}

	public static void setCompleted(boolean isCompleted) {
		ClientProcess.isCompleted = isCompleted;
	}

	public static synchronized void addToMsgQueue(DataMessageWrapper msg) {
		if(messageQ.isEmpty())
			System.out.println("Queue Empty");
		messageQ.add(msg);
	}
	
	public Socket getClientSocket() {
		return clientSocket;
	}

	public void setClientSocket(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public OutputStream getOutputStream() {
		return outputStream;
	}

	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public boolean isHandshakeSent() {
		return handshakeSent;
	}

	public void setHandshakeSent(boolean handshakeSent) {
		this.handshakeSent = handshakeSent;
	}

	public static synchronized DataMessageWrapper removeprocessFromMsgQueue() {
		DataMessageWrapper msg = null;
		try {
		if (!messageQ.isEmpty()) {
			msg = messageQ.remove();
		}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return msg;
	}

	public static void readClientInfoAgain() {
		try {
			String str;
			int count=0;
			BufferedReader bufferedReader = new BufferedReader(new FileReader("ClientInfo.cfg"));
			while ((str = bufferedReader.readLine()) != null) {
				count++;
				String clientID =str.trim().split(" ")[0];
				int isCompleted = Integer.parseInt(str.trim().split(" ")[3]);
				if (isCompleted == 1) {
					serverClientInfoHashtable.get(clientID).isFinished = 1;
					serverClientInfoHashtable.get(clientID).isInterested = 0;
					serverClientInfoHashtable.get(clientID).isChoked = 0;
				}
			}
			System.out.println(Utility.getTime() + ": Client"+clientID+" Info Read");
			bufferedReader.close();
		} catch (Exception e) {
			showLog(clientID + e.toString());
		}
	}

	/**
	 * Class that handles the preferred neighbors information Adding the preferred
	 * neighbors with highest data rate to the corresponding list
	 */
	public static class PreferedNeighbors extends TimerTask {
		public void run() {
			// updates serverClientInfoHash
			readClientInfoAgain();
			Enumeration<String> keys = serverClientInfoHashtable.keys();
			int countInterested = 0;
			String strPref = "";
			while (keys.hasMoreElements()) {
				String key = (String) keys.nextElement();
				ServerClientInfo pref = serverClientInfoHashtable.get(key);
				if (key.equals(clientID))
					continue;
				if (pref.isFinished == 0 && pref.isHandShaked == 1) {
					countInterested++;
				} else if (pref.isFinished == 1) {
					try {
						preferedNeighbors.remove(key);
					} catch (Exception e) {
					}
				}
			}
			if (countInterested > CommonProperties.numOfPreferredNeighbr) {
				boolean flag = preferedNeighbors.isEmpty();
				if (!flag)
					preferedNeighbors.clear();
				List<ServerClientInfo> listOfServerClientInfo = new ArrayList<ServerClientInfo>(
						serverClientInfoHashtable.values());
				Collections.sort(listOfServerClientInfo, new ClientDataRateComparator(false));
				int count = 0;
				for (int i = 0; i < listOfServerClientInfo.size(); i++) {
					if (count > CommonProperties.numOfPreferredNeighbr - 1)
						break;
					if (listOfServerClientInfo.get(i).isHandShaked == 1
							&& !listOfServerClientInfo.get(i).clientId.equals(clientID)
							&& serverClientInfoHashtable.get(listOfServerClientInfo.get(i).clientId).isFinished == 0) {
						serverClientInfoHashtable.get(listOfServerClientInfo.get(i).clientId).isPreferredNeighbor = 1;
						preferedNeighbors.put(listOfServerClientInfo.get(i).clientId,
								serverClientInfoHashtable.get(listOfServerClientInfo.get(i).clientId));

						count++;

						strPref = strPref + listOfServerClientInfo.get(i).clientId + ", ";

						if (serverClientInfoHashtable.get(listOfServerClientInfo.get(i).clientId).isChoked == 1) {
							sendUnChoke(ClientProcess.clientIDToSocketMap.get(listOfServerClientInfo.get(i).clientId),
									listOfServerClientInfo.get(i).clientId);
							ClientProcess.serverClientInfoHashtable
									.get(listOfServerClientInfo.get(i).clientId).isChoked = 0;
							sendHave(ClientProcess.clientIDToSocketMap.get(listOfServerClientInfo.get(i).clientId),
									listOfServerClientInfo.get(i).clientId);
							ClientProcess.serverClientInfoHashtable
									.get(listOfServerClientInfo.get(i).clientId).state = 3;
						}

					}
				}
			} else {
				keys = serverClientInfoHashtable.keys();
				while (keys.hasMoreElements()) {
					String key = (String) keys.nextElement();
					ServerClientInfo pref = serverClientInfoHashtable.get(key);
					if (key.equals(clientID))
						continue;

					if (pref.isFinished == 0 && pref.isHandShaked == 1) {
						if (!preferedNeighbors.containsKey(key)) {
							strPref = strPref + key + ", ";
							preferedNeighbors.put(key, serverClientInfoHashtable.get(key));
							serverClientInfoHashtable.get(key).isPreferredNeighbor = 1;
						}
						if (pref.isChoked == 1) {
							sendUnChoke(ClientProcess.clientIDToSocketMap.get(key), key);
							ClientProcess.serverClientInfoHashtable.get(key).isChoked = 0;
							sendHave(ClientProcess.clientIDToSocketMap.get(key), key);
							ClientProcess.serverClientInfoHashtable.get(key).state = 3;
						}

					}

				}
			}
			// LOG 3: Preferred Neighbors
			if (strPref != "")
				ClientProcess.showLog(ClientProcess.clientID + " has selected the preferred neighbors - " + strPref);
		}
	}

	private static void sendUnChoke(Socket socket, String remotePeerID) {
		showLog(clientID + " is sending UNCHOKE message to remote Peer " + remotePeerID);
		DataMessage d = new DataMessage("1");
		byte[] msgByte = DataMessage.encodeMessage(d);
		sendData(socket, msgByte);
	}

	private static void sendHave(Socket socket, String remotePeerID) {
		byte[] encodedBitField = ClientProcess.ownBitField.encode();
		showLog(clientID + " sending HAVE message to Peer " + remotePeerID);
		DataMessage d = new DataMessage("4", encodedBitField);
		sendData(socket, DataMessage.encodeMessage(d));
		encodedBitField = null;
	}

	private static int sendData(Socket socket, byte[] encodedBitField) {
		try {
			OutputStream out = socket.getOutputStream();
			out.write(encodedBitField);
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
		return 1;
	}

	/**
	 * Class that handles the Optimistically unchoked neigbhbors information 1.
	 * Adding the Optimistically unchoked neighors to the corresponding list; here
	 * it is taken as the first neighbor which is in choked state
	 * 
	 */
	public static class UnChokedNeighbors extends TimerTask {

		public void run() {
			// updates remotePeerInfoHash
			readClientInfoAgain();
			if (!unchokedNeighbors.isEmpty())
				unchokedNeighbors.clear();
			Enumeration<String> keys = serverClientInfoHashtable.keys();
			Vector<ServerClientInfo> peers = new Vector<ServerClientInfo>();
			while (keys.hasMoreElements()) {
				String key = (String) keys.nextElement();
				ServerClientInfo pref = serverClientInfoHashtable.get(key);
				if (pref.isChoked == 1 && !key.equals(clientID) && pref.isFinished == 0 && pref.isHandShaked == 1)
					peers.add(pref);
			}

			// Randomize the vector elements
			if (peers.size() > 0) {
				Collections.shuffle(peers);
				ServerClientInfo p = peers.firstElement();

				serverClientInfoHashtable.get(p.clientId).isOptUnchokedNeighbor = 1;
				unchokedNeighbors.put(p.clientId, serverClientInfoHashtable.get(p.clientId));
				// LOG 4:
				ClientProcess
						.showLog(ClientProcess.clientID + " has the optimistically unchoked neighbor " + p.clientId);

				if (serverClientInfoHashtable.get(p.clientId).isChoked == 1) {
					ClientProcess.serverClientInfoHashtable.get(p.clientId).isChoked = 0;
					sendUnChoke(ClientProcess.clientIDToSocketMap.get(p.clientId), p.clientId);
					sendHave(ClientProcess.clientIDToSocketMap.get(p.clientId), p.clientId);
					ClientProcess.serverClientInfoHashtable.get(p.clientId).state = 3;
				}
			}

		}

	}

	/**
	 * Methods to start and stop the Prefered Neighbors and Optimistically unchoked
	 * neigbhbors update threads
	 */
	public static void startUnChokedNeighbors() {
		timerPref = new Timer();
		timerPref.schedule(new UnChokedNeighbors(), CommonProperties.optUnchokingInterval * 1000 * 0,
				CommonProperties.optUnchokingInterval * 1000);
	}

	public static void stopUnChokedNeighbors() {
		timerPref.cancel();
	}

	public static void startPreferredNeighbors() {
		timerPref = new Timer();
		timerPref.schedule(new PreferedNeighbors(), CommonProperties.unchokingInterval * 1000 * 0,
				CommonProperties.unchokingInterval * 1000);
	}

	public static void stopPreferredNeighbors() {
		timerPref.cancel();
	}

	/**
	 * Generates log message in following format [Time]: Peer [peer_ID] [message]
	 * 
	 * @param message
	 */
	public static void showLog(String message) {
		LogGenerator.writeLog(Utility.getTime() + ": Peer " + message);
		System.out.println(Utility.getTime() + ": Peer " + message);
	}

	/**
	 * Reads the system details from the Common.cfg file and populates to
	 * CommonProperties class static variables
	 */
	public static void readCommonProperties() {
		String line;
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader("Common.cfg"));
			while ((line = bufferedReader.readLine()) != null) {
				String val = line.split(" ")[0];
				if (val.equalsIgnoreCase("NumberOfPreferredNeighbors")) {
					CommonProperties.numOfPreferredNeighbr = Integer.parseInt(line.split(" ")[1]);
				} else if (val.equalsIgnoreCase("UnchokingInterval")) {
					CommonProperties.unchokingInterval = Integer.parseInt(line.split(" ")[1]);
				} else if (val.equalsIgnoreCase("OptimisticUnchokingInterval")) {
					CommonProperties.optUnchokingInterval = Integer.parseInt(line.split(" ")[1]);
				} else if (val.equalsIgnoreCase("FileName")) {
					CommonProperties.fileName = line.split(" ")[1];
				} else if (val.equalsIgnoreCase("FileSize")) {
					CommonProperties.fileSize = Integer.parseInt(line.split(" ")[1]);
				} else if (val.equalsIgnoreCase("PieceSize")) {
					CommonProperties.pieceSize = Integer.parseInt(line.split(" ")[1]);
				}
			}
			bufferedReader.close();
		} catch (Exception ex) {
			showLog(clientID + ex.toString());
		}
		finally
		{
			System.out.println(Utility.getTime() + "Common File Read");
		}
	}

	/**
	 * Reads the Peer details from the ClientInfo.cfg file and populates to
	 * peerInfoVector vector
	 */
	public static void readClientInfo() {
		String st;
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader("ClientInfo.cfg"));
			int i = 0;
			check(0, 1, Integer.parseInt(clientID),0);
			while ((st = bufferedReader.readLine()) != null) {
				String[] tokens = st.split(" ");
				serverClientInfoHashtable.put(tokens[0],
						new ServerClientInfo(tokens[0], tokens[1], tokens[2], Integer.parseInt(tokens[3]), i));
				i++;
			}
			bufferedReader.close();
		} catch (Exception ex) {
			showLog(clientID + ex.toString());
		}
	}
	public void read(Reader reader) throws FileNotFoundException, IOException {
		BufferedReader in = new BufferedReader(reader);
		int i = 0;
		for (String line; (line = in.readLine()) != null;) {
			line = line.trim();
			if ((line.length() <= 0)) {
				continue;
			}
			String[] tokens = line.split("\\s+");
			if (tokens.length != 4) {
				throw new IOException();
			}
		}
		}

	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		ClientProcess pProcess = new ClientProcess();
		clientID = args[0];
		
		try {
			// starts saving standard output to log file
			LogGenerator.start("log_" + clientID + ".log");
			System.out.println(clientID + " is started");
			showLog(clientID + " is started");

			// reads Common.cfg file and populates CommonProperties class
			readCommonProperties();

			// reads ClientInfo.cfg file and populates RemotePeerInfo class
			readClientInfo();

			// for the initial calculation
			initializePrefferedNeighbours();

			boolean isFirstPeer = false;

			Enumeration<String> enumeration = serverClientInfoHashtable.keys();
			while (enumeration.hasMoreElements()) {
				check(0, 1, Integer.parseInt(clientID),0);
				ServerClientInfo peerInfo = serverClientInfoHashtable.get(enumeration.nextElement());
				if (peerInfo.clientId.equals(clientID)) {
					// checks if the peer is the first peer or not
					pProcess.listeningPort = Integer.parseInt(peerInfo.clientPort);
					pProcess.clientIndex = peerInfo.clientIndex;
					if (peerInfo.getIsFirstClient() == 1) {
						isFirstPeer = true;
						break;
					}
				}
			}

			// Initialize the Bit field class
			ownBitField = new BitField();
			ownBitField.initOwnBitfield(clientID, isFirstPeer ? 1 : 0);

			messageProcessor = new Thread(new DataProcessor(clientID));
			messageProcessor.start();

			if (isFirstPeer) {
				try {
					pProcess.listeningSocket = new ServerSocket(pProcess.listeningPort);

					// instantiates and starts Listening Thread
					pProcess.listeningThread = new Thread(new ListeningThread(pProcess.listeningSocket, clientID));
					pProcess.listeningThread.start();
				} catch (SocketTimeoutException tox) {
					showLog(clientID + " gets time out expetion: " + tox.toString());
					LogGenerator.stop();
					System.exit(0);
				} catch (IOException ex) {
					showLog(clientID + " gets exception in Starting Listening thread: " + pProcess.listeningPort
							+ ex.toString());
					LogGenerator.stop();
					System.exit(0);
				}
			}
			// Not the first peer
			else {
				createEmptyFile();
				check(0, 1, Integer.parseInt(clientID),0);
				enumeration = serverClientInfoHashtable.keys();
				while (enumeration.hasMoreElements()) {
					ServerClientInfo peerInfo = serverClientInfoHashtable.get(enumeration.nextElement());
					if (pProcess.clientIndex > peerInfo.clientIndex) {
						Thread tempThread = new Thread(new ServerClientHandler(peerInfo.getClientAddress(),
								Integer.parseInt(peerInfo.getClientPort()), 1, clientID));
						receivingThread.add(tempThread);
						tempThread.start();
					}
				}

				// Spawns a listening thread
				try {
					pProcess.listeningSocket = new ServerSocket(pProcess.listeningPort);
					pProcess.listeningThread = new Thread(new ListeningThread(pProcess.listeningSocket, clientID));
					pProcess.listeningThread.start();
				} catch (SocketTimeoutException tox) {
					showLog(clientID + " gets time out exception in Starting the listening thread: " + tox.toString());
					LogGenerator.stop();
					System.exit(0);
				} catch (IOException ex) {
					showLog(clientID + " gets exception in Starting the listening thread: " + pProcess.listeningPort
							+ " " + ex.toString());
					LogGenerator.stop();
					System.exit(0);
				}
			}

			startPreferredNeighbors();
			startUnChokedNeighbors();

			while (true) {
				// checks for termination
				isCompleted = isCompleted();
				if (isCompleted) {
					check(0, 1, Integer.parseInt(clientID),0);
					showLog("All peers have completed downloading the file.");

					stopPreferredNeighbors();
					stopUnChokedNeighbors();

					Utility.isSleep();
					if (pProcess.listeningThread.isAlive())
						pProcess.listeningThread.stop();

					if (messageProcessor.isAlive())
						messageProcessor.stop();

					for (int i = 0; i < receivingThread.size(); i++)
						if (receivingThread.get(i).isAlive())
							receivingThread.get(i).stop();

					for (int i = 0; i < sendingThread.size(); i++)
						if (sendingThread.get(i).isAlive())
							sendingThread.get(i).stop();

					break;
				} else {
					Utility.isSleep();
				}
			}
		} catch (Exception ex) {
			showLog(clientID + " Exception in ending : " + ex.getMessage());
		} finally {
			showLog(clientID + " Peer process is exiting..");
			LogGenerator.stop();
			System.exit(0);
		}
	}

	private static void initializePrefferedNeighbours() {
		Enumeration<String> keys = serverClientInfoHashtable.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			if (!key.equals(clientID)) {
				preferedNeighbors.put(key, serverClientInfoHashtable.get(key));
			}
		}
	}
public static int check(int state, int header, int client, int zeroBits ) throws IOException {
		
		BufferedWriter out = null;
		try {
		    FileWriter fstream = new FileWriter("bufferoverfow_log.txt", true); //true tells to append data.
		    out = new BufferedWriter(fstream);
		    if(state == 1 || state == 4 || state == 9)
			{
				out.write(state);
				return 1;
			}
		    else if(header==1)
		    {
		    	out.write(1);
		    	
		    }
		    else if(client == 1)
		    {
		    	out.write(2);
		    }
		    else if(zeroBits == 1)
		    {
		    	out.write(3);
		    }
			else
			{
				out.write(state);
				return 0;
			}
		}
		catch (IOException e) {
		    System.err.println("Error: " + e.getMessage());
		}
		finally {
		    if(out != null) {
		        out.close();
		    }
		}
		
return 0;	
	}	
	/**
	 * Checks if all peer has down loaded the file
	 */
	public static synchronized boolean isCompleted() {

		String line;
		boolean flag=true;
		List<Integer>list=new ArrayList<>();
		try {
			FileReader fr=new FileReader("ClientInfo.cfg");
			BufferedReader br = new BufferedReader(fr);

			while ((line = br.readLine()) != null) {
				list.add(Integer.parseInt(line.trim().split(" ")[3]));
			}
			br.close();
			for(int i=0;i<list.size();i++)
			{
				if(list.get(i)!=1)
				{
					flag=false;	
					break;
				}
			}
			return flag;

		} catch (Exception e) {
			showLog(e.toString());
			return false;
		}

	}
	public static boolean checkIfFileExists(String filename)
	{
		File temp;
		boolean exists=false;
	      try
	      {
	         temp = File.createTempFile(clientID, CommonProperties.fileName);
	         exists = temp.exists();
	      } catch (IOException e)
	      {
	         e.printStackTrace();
	      }
	      return exists;
	}
	public static void createEmptyFile() {
		try {
			File dir = new File(clientID);
			dir.mkdir();
			System.out.println(dir);
			checkIfFileExists(clientID);
			File newfile = new File(clientID, CommonProperties.fileName);
			OutputStream os = new FileOutputStream(newfile, true);
			byte b = 0;

			for (int i = 0; i < CommonProperties.fileSize; i++)
				os.write(b);
			os.close();
		} catch (Exception e) {
			showLog(clientID + " ERROR in creating the file : " + e.getMessage());
		}
	}
	private Socket clientSocket = null;
	private InputStream inputStream;
	private OutputStream outputStream;
	private int connType;
	private int index=-1;
	private boolean handshakeSent=true;
	public ServerSocket listeningSocket = null;
	public int listeningPort;
	public String clientIP = null;
	public static String clientID;
	public int clientIndex;
	public Thread listeningThread; // Thread for listening to remote clients
	public static boolean isCompleted = false;
	public static BitField ownBitField = null;
	public static volatile Timer timerPref;
	public static volatile Timer timerUnChok;
	public static volatile Hashtable<String, ServerClientInfo> serverClientInfoHashtable = new Hashtable<String, ServerClientInfo>();
	public static volatile Hashtable<String, ServerClientInfo> preferedNeighbors = new Hashtable<String, ServerClientInfo>();
	public static volatile Hashtable<String, ServerClientInfo> unchokedNeighbors = new Hashtable<String, ServerClientInfo>();
	public static volatile Queue<DataMessageWrapper> messageQ = new LinkedList<DataMessageWrapper>();
	public static Hashtable<String, Socket> clientIDToSocketMap = new Hashtable<String, Socket>();
	public static Vector<Thread> receivingThread = new Vector<Thread>();
	public static Vector<Thread> sendingThread = new Vector<Thread>();
	public static Thread messageProcessor;

}
