package com.ptop.remote;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.ptop.commons.DataMessage;
import com.ptop.commons.DataMessageWrapper;
import com.ptop.commons.HandshakeMessage;
import com.ptop.controller.ClientProcess;
import com.ptop.utils.Utility;

public class ServerClientHandler implements Runnable {

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
	public int getConnType() {
		return connType;
	}
	public void setConnType(int connType) {
		this.connType = connType;
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
	public boolean isHandshakeReceived() {
		return handshakeReceived;
	}
	public void setHandshakeReceived(boolean handshakeReceived) {
		this.handshakeReceived = handshakeReceived;
	}
	public HandshakeMessage getHandshakeMessage() {
		return handshakeMessage;
	}
	public void setHandshakeMessage(HandshakeMessage handshakeMessage) {
		this.handshakeMessage = handshakeMessage;
	}
	public String getOwnClientId() {
		return ownClientId;
	}
	public void setOwnClientId(String ownClientId) {
		this.ownClientId = ownClientId;
	}
	public String getServerClientId() {
		return serverClientId;
	}
	public void setServerClientId(String serverClientId) {
		this.serverClientId = serverClientId;
	}
	public int getActiveConn() {
		return activeConn;
	}
	public int getPassiveConn() {
		return passiveConn;
	}
	public void Close(InputStream i, Socket socket)
	{
		try {
			i.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	public ServerClientHandler(Socket clientSocket, int connType, String ownPeerID) {

		this.clientSocket = clientSocket;
		this.connType = connType;
		this.ownClientId = ownPeerID;
		try {
			inputStream = clientSocket.getInputStream();
			outputStream = clientSocket.getOutputStream();
		} catch (Exception ex) {
			ClientProcess.showLog(this.ownClientId + " Error : " + ex.getMessage());
		}
	}
	
	public ServerClientHandler(Socket clientSocket, int connType, String ownPeerID,int index) throws SocketException {

		this.clientSocket = clientSocket;
		this.connType = connType;
		this.ownClientId = ownPeerID;
		this.index=index;
		if(index==1)
		{
			try {
				inputStream = clientSocket.getInputStream();
				outputStream = clientSocket.getOutputStream();
			} catch (Exception ex) {
				ClientProcess.showLog(this.ownClientId + " Error : " + ex.getMessage());
			}
		}
		else if(index==0)
		{
			throw new SocketException();
		}
	}
	
	public void openClose(InputStream i, Socket socket) {
		try {
			i.close();
			i = socket.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public ServerClientHandler(String add, int port, int connType, String ownClientID) {
		try {
			this.connType = connType;
			this.ownClientId = ownClientID;
			this.clientSocket = new Socket(add, port);
		} catch (UnknownHostException e) {
			ClientProcess.showLog(ownClientID + " ServerClientHandler : " + e.getMessage());
		} catch (IOException e) {
			ClientProcess.showLog(ownClientID + " ServerClientHandler : " + e.getMessage());
		}
		this.connType = connType;

		try {
			inputStream = clientSocket.getInputStream();
			outputStream = clientSocket.getOutputStream();
		} catch (Exception ex) {
			ClientProcess.showLog(ownClientID + " ServerClientHandler : " + ex.getMessage());
		}
	}

	public boolean SendHandshake() {
		try {
			outputStream.write(
					HandshakeMessage.encodeMessage(new HandshakeMessage("P2PFILESHARINGPROJ", this.ownClientId)));
			handshakeSent=true;
		} catch (IOException e) {
			ClientProcess.showLog(this.ownClientId + " SendHandshake : " + e.getMessage());
			return false;
		}
		return true;
	}
	public boolean checkHandshakeCompleted() {
		if(handshakeSent && handshakeReceived)
		{
			return true;
		}
		else 
			return false;
	}

	public boolean ReceiveHandshake() {
		byte[] receivedHandshakeByte = new byte[32];
		try {
			inputStream.read(receivedHandshakeByte);
			HandshakeMessage obj = HandshakeMessage.decodeMessage(receivedHandshakeByte);
			serverClientId = handshakeMessage.getClientIDString();
			handshakeReceived=true;

			// populate peerID to socket mapping
			ClientProcess.clientIDToSocketMap.put(serverClientId, this.clientSocket);
		} catch (IOException e) {
			ClientProcess.showLog(this.ownClientId + " ReceiveHandshake : " + e.getMessage());
			return false;
		}
		return true;
	}

	public boolean sendRequest(int index) {
		checkHandshakeCompleted();
		try {
			outputStream.write(DataMessage.encodeMessage(new DataMessage(""+request, Utility.intToByteArray(index))));
		} catch (IOException e) {
			ClientProcess.showLog(this.ownClientId + " SendRequest : " + e.getMessage());
			return false;
		}
		return true;
	}

	public boolean SendInterested() {
		checkHandshakeCompleted();
		try {
			outputStream.write(DataMessage.encodeMessage(new DataMessage(""+interested)));
		} catch (IOException e) {
			ClientProcess.showLog(this.ownClientId + " SendInterested : " + e.getMessage());
			return false;
		}
		return true;
	}

	public boolean sendNotInterested() {
		checkHandshakeCompleted();
		try {
			outputStream.write(DataMessage.encodeMessage(new DataMessage(""+not_interested)));
		} catch (IOException e) {
			ClientProcess.showLog(this.ownClientId + " SendNotInterested : " + e.getMessage());
			return false;
		}

		return true;
	}

	public boolean receiveUnchoke() {
		checkHandshakeCompleted();
		byte[] receiveUnchokeByte = null;

		try {
			inputStream.read(receiveUnchokeByte);
		} catch (IOException e) {
			ClientProcess.showLog(this.ownClientId + " ReceiveUnchoke : " + e.getMessage());
			return false;
		}

		DataMessage dataMessage = DataMessage.decodeMessage(receiveUnchokeByte);
		if (dataMessage.getMessageTypeString().equals(""+unchoke)) {
			ClientProcess.showLog(ownClientId + "is unchoked by " + serverClientId);
			return true;
		} else
			return false;
	}

	public boolean receiveChoke() {
		checkHandshakeCompleted();
		byte[] receiveChokeByte = null;

		// Check whether the in stream has data to be read or not.
		try {
			if (inputStream.available() == 0)
				return false;
		} catch (IOException e) {
			ClientProcess.showLog(this.ownClientId + " ReceiveChoke : " + e.getMessage());
			return false;
		}
		try {
			inputStream.read(receiveChokeByte);
		} catch (IOException e) {
			ClientProcess.showLog(this.ownClientId + " ReceiveChoke : " + e.getMessage());
			return false;
		}
		DataMessage dataMessage = DataMessage.decodeMessage(receiveChokeByte);
		if (dataMessage.getMessageTypeString().equals(""+choke)) {
			ClientProcess.showLog(ownClientId + " is CHOKED by " + serverClientId);
			return true;
		} else
			return false;
	}

	public boolean receivePeice() {
		checkHandshakeCompleted();
		byte[] receivePeice = null;

		try {
			inputStream.read(receivePeice);
		} catch (IOException e) {
			ClientProcess.showLog(this.ownClientId + " receivePeice : " + e.getMessage());
			return false;
		}

		DataMessage m = DataMessage.decodeMessage(receivePeice);
		if (m.getMessageTypeString().equals(""+unchoke)) {
			// LOG 5:
			ClientProcess.showLog(ownClientId + " is UNCHOKED by " + serverClientId);
			return true;
		} else
			return false;

	}

	public void run() {
		byte[] handshakeBuff = new byte[32];
		byte[] dataBuffWithoutPayload = new byte[4 + 1];
		byte[] msgLength;
		byte[] msgType;
		DataMessageWrapper dataMsgWrapper = new DataMessageWrapper();

		try {
			if (this.connType == activeConn) {
				if (!SendHandshake()) {
					ClientProcess.showLog(ownClientId + " HANDSHAKE sending failed.");
					System.exit(0);
				} else {
					ClientProcess.showLog(ownClientId + " HANDSHAKE has been sent...");
				}
				while (true) {
					inputStream.read(handshakeBuff);
					handshakeMessage = HandshakeMessage.decodeMessage(handshakeBuff);
					if (handshakeMessage.getHeaderString().equals("P2PFILESHARINGPROJ")) {

						serverClientId = handshakeMessage.getClientIDString();

						ClientProcess.showLog(ownClientId + " makes a connection to Peer " + serverClientId);

						ClientProcess
								.showLog(ownClientId + " Received a HANDSHAKE message from Peer " + serverClientId);

						// populate peerID to socket mapping
						ClientProcess.clientIDToSocketMap.put(serverClientId, this.clientSocket);
						break;
					} else {
						continue;
					}
				}

				// Sending BitField...
				DataMessage d = new DataMessage(""+bitfield, ClientProcess.ownBitField.encode());
				byte[] b = DataMessage.encodeMessage(d);
				outputStream.write(b);
				ClientProcess.serverClientInfoHashtable.get(serverClientId).state = 8;
			}
			// Passive connection
			else {
				while (true) {
					inputStream.read(handshakeBuff);
					handshakeMessage = HandshakeMessage.decodeMessage(handshakeBuff);
					if (handshakeMessage.getHeaderString().equals("P2PFILESHARINGPROJ")) {
						serverClientId = handshakeMessage.getClientIDString();

						ClientProcess.showLog(ownClientId + " makes a connection to Peer " + serverClientId);
						ClientProcess
								.showLog(ownClientId + " Received a HANDSHAKE message from Peer " + serverClientId);

						// populate peerID to socket mapping
						ClientProcess.clientIDToSocketMap.put(serverClientId, this.clientSocket);
						break;
					} else {
						continue;
					}
				}
				if (!SendHandshake()) {
					ClientProcess.showLog(ownClientId + " HANDSHAKE message sending failed.");
					System.exit(0);
				} else {
					ClientProcess.showLog(ownClientId + " HANDSHAKE message has been sent successfully.");
				}

				ClientProcess.serverClientInfoHashtable.get(serverClientId).state = 2;
			}
			// receive data messages continuously
			while (true) {

				int headerBytes = inputStream.read(dataBuffWithoutPayload);

				if (headerBytes == -1)
					break;

				msgLength = new byte[4];
				msgType = new byte[4];
				System.arraycopy(dataBuffWithoutPayload, 0, msgLength, 0, 4);
				System.arraycopy(dataBuffWithoutPayload, 4, msgType, 0, 1);
				DataMessage dataMessage = new DataMessage();
				dataMessage.setMessageLength(msgLength);
				dataMessage.setMessageType(msgType);
				if (dataMessage.getMessageTypeString().equals(""+choke) || dataMessage.getMessageTypeString().equals(""+unchoke)
						|| dataMessage.getMessageTypeString().equals(""+interested)
						|| dataMessage.getMessageTypeString().equals(""+not_interested)) {
					dataMsgWrapper.dataMessage = dataMessage;
					dataMsgWrapper.fromClientID = this.serverClientId;
					ClientProcess.addToMsgQueue(dataMsgWrapper);
				} else {
					int bytesAlreadyRead = 0;
					int bytesRead;
					byte[] dataBuffPayload = new byte[dataMessage.getMessageLengthInt() - 1];
					while (bytesAlreadyRead < dataMessage.getMessageLengthInt() - 1) {
						bytesRead = inputStream.read(dataBuffPayload, bytesAlreadyRead,
								dataMessage.getMessageLengthInt() - 1 - bytesAlreadyRead);
						if (bytesRead == -1)
							return;
						bytesAlreadyRead += bytesRead;
					}

					byte[] dataBuffWithPayload = new byte[dataMessage.getMessageLengthInt() + 4];
					System.arraycopy(dataBuffWithoutPayload, 0, dataBuffWithPayload, 0, 4 + 1);
					System.arraycopy(dataBuffPayload, 0, dataBuffWithPayload, 4 + 1, dataBuffPayload.length);

					DataMessage dataMsgWithPayload = DataMessage.decodeMessage(dataBuffWithPayload);
					dataMsgWrapper.dataMessage = dataMsgWithPayload;
					dataMsgWrapper.fromClientID = serverClientId;
					ClientProcess.addToMsgQueue(dataMsgWrapper);
					dataBuffPayload = null;
					dataBuffWithPayload = null;
					bytesAlreadyRead = 0;
					bytesRead = 0;
				}
			}
		} catch (IOException e) {
			ClientProcess.showLog(ownClientId + " run exception: " + e);
		}

	}

	public void releaseSocket() {
		try {
			if (this.connType == passiveConn && this.clientSocket != null) {
				this.clientSocket.close();
			}
			if (inputStream != null) {
				inputStream.close();
			}
			if (outputStream != null)
				outputStream.close();
		} catch (IOException e) {
			ClientProcess.showLog(ownClientId + " Release socket IO exception: " + e);
		}
	}

	private Socket clientSocket = null;
	private InputStream inputStream;
	private OutputStream outputStream;
	private int connType;
	private int index=-1;
	private boolean handshakeSent=true;
	

	private boolean handshakeReceived=true;

	private HandshakeMessage handshakeMessage;

	String ownClientId, serverClientId;

	final int activeConn = 1;
	final int passiveConn = 0;
	final int DATA_MSG_TYPE = 1;
	final int choke = 0;
	final int unchoke = 1;
	final int interested= 2;
	final int not_interested = 3;
	final int have = 4;
	final int bitfield = 5;
	final int request = 6;
	final int piece = 7;
}