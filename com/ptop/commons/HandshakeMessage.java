package com.ptop.commons;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.ptop.controller.ClientProcess;

public class HandshakeMessage {
	// Attributes
	private byte[] header = new byte[18];
	private int headerSize;
	private byte[] clientID = new byte[4];
	private int clientIDLength;
	private byte[] zeroBits = new byte[10];
	private int zeroBitsLength;
	private String messageHeader;
	private String messageClientID;
	public static int indexI;
	public static int indexJ;
	private boolean headerSizeFlag;
	
	public boolean isHeaderSizeFlag() {
		return headerSizeFlag;
	}

	public void setHeaderSizeFlag(boolean headerSizeFlag) {
		this.headerSizeFlag = headerSizeFlag;
	}

	public boolean isClientSizeFlag() {
		return clientSizeFlag;
	}

	public void setClientSizeFlag(boolean clientSizeFlag) {
		this.clientSizeFlag = clientSizeFlag;
	}

	public boolean isZeroBitsFlag() {
		return zeroBitsFlag;
	}

	public void setZeroBitsFlag(boolean zeroBitsFlag) {
		this.zeroBitsFlag = zeroBitsFlag;
	}

	private boolean clientSizeFlag;
	private boolean zeroBitsFlag;

	public int getHeaderSize() {
		return headerSize;
	}

	public void setHeaderSize(int headerSize) {
		this.headerSize = headerSize;
	}

	public int getClientIDLength() {
		return clientIDLength;
	}

	public void setClientIDLength(int clientIDLength) {
		this.clientIDLength = clientIDLength;
	}

	public int getZeroBitsLength() {
		return zeroBitsLength;
	}

	public void setZeroBitsLength(int zeroBitsLength) {
		this.zeroBitsLength = zeroBitsLength;
	}

	public String getMessageHeader() {
		return messageHeader;
	}

	public void setMessageHeader(String messageHeader) {
		this.messageHeader = messageHeader;
	}

	public String getMessageClientID() {
		return messageClientID;
	}

	public void setMessageClientID(String messageClientID) {
		this.messageClientID = messageClientID;
	}

	public int getIndexI() {
		return indexI;
	}

	public void setIndexI(int indexI) {
		HandshakeMessage.indexI = indexI;
	}

	public int getIndexJ() {
		return indexJ;
	}

	public void setIndexJ(int indexJ) {
		HandshakeMessage.indexJ = indexJ;
	}

	public HandshakeMessage() {

	}


	public HandshakeMessage(String Header, String PeerId) {
		
		this.setClientIDLength(4);
		this.setHeaderSize(18);
		this.setIndexI(0);
		this.setIndexJ(0);
		this.setZeroBitsLength(10);

		try {
			this.messageHeader = Header;
			this.header = Header.getBytes("UTF8");
			
			this.setHeaderSizeFlag(header.length>this.getHeaderSize());
			

			this.messageClientID = PeerId;
			this.clientID = PeerId.getBytes("UTF8");
			
			this.setClientSizeFlag(clientID.length>this.getClientIDLength());
			

			this.zeroBits = "0000000000".getBytes("UTF8");
			this.setZeroBitsFlag(zeroBits.length>this.getZeroBitsLength());
			int a = this.isHeaderSizeFlag() ? 1 : 0;
			int b = this.isClientSizeFlag() ? 1 : 0;
			int c = this.isZeroBitsFlag() ? 1 : 0;
			
			
			ClientProcess.showLog(String.valueOf(check(0, a, b, c)));
			
			
		} catch (Exception e) {
			ClientProcess.showLog(e.toString());
		}

	}
	
	public static int checkstat(int state, int header, int client, int zeroBits ) throws IOException {
		
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
	
	
public int check(int state, int header, int client, int zeroBits ) throws IOException {
		
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



	// Set the handShakeHeader
	public void setHeader(byte[] handShakeHeader) {
		try {
			this.messageHeader = (new String(handShakeHeader, "UTF8")).toString().trim();
			this.header = this.messageHeader.getBytes();
		} catch (UnsupportedEncodingException e) {
			ClientProcess.showLog(e.toString());
		}
	}

	// Set the setClientID
	public void setClientID(byte[] peerID) {
		try {
			this.messageClientID = (new String(peerID, "UTF8")).toString().trim();
			this.clientID = this.messageClientID.getBytes();

		} catch (UnsupportedEncodingException e) {
			ClientProcess.showLog(e.toString());
		}
	}

	// Set the messagePeerID
	public void setClientID(String messagePeerID) {
		try {
			this.messageClientID = messagePeerID;
			this.clientID = messagePeerID.getBytes("UTF8");
		} catch (UnsupportedEncodingException e) {
			ClientProcess.showLog(e.toString());
		}
	}

	// Set the messageHeader
	public void setHeader(String messageHeader) {
		try {
			this.messageHeader = messageHeader;
			this.header = messageHeader.getBytes("UTF8");
		} catch (UnsupportedEncodingException e) {
			ClientProcess.showLog(e.toString());
		}
	}

	// return the handShakeHeader
	public byte[] getHeader() {
		return header;
	}

	// return the peerID
	public byte[] getClientID() {
		return clientID;
	}

	// Set the zeroBits
	public void setZeroBits(byte[] zeroBits) {
		this.zeroBits = zeroBits;
	}

	// return the zeroBits
	public byte[] getZeroBits() {
		return zeroBits;
	}

	// return the messageHeader
	public String getHeaderString() {
		return messageHeader;
	}

	// return the messagePeerID
	public String getClientIDString() {
		return messageClientID;
	}

	// Return the toString method of the Object
	public String toString() {
		return ("[HandshakeMessage] : Peer Id - " + this.messageClientID + ", Header - " + this.messageHeader);
	}
	public static HandshakeMessage decodeMessage(byte[] receivedMessage){

		HandshakeMessage handshakeMessage = null;
		byte[] msgHeader = null;
		byte[] msgPeerID = null;
		
		try {
			checkstat(0, receivedMessage.length, 0, receivedMessage.length);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			handshakeMessage = new HandshakeMessage();
			msgHeader = new byte[18];
			msgPeerID = new byte[4];
			for(HandshakeMessage.indexI = 0; HandshakeMessage.indexI < 18; HandshakeMessage.indexI++)
				msgHeader[HandshakeMessage.indexI]= receivedMessage[HandshakeMessage.indexI];
			
			for(HandshakeMessage.indexJ = 28; HandshakeMessage.indexJ < 32; HandshakeMessage.indexJ++)
				msgPeerID[HandshakeMessage.indexJ-28]= receivedMessage[HandshakeMessage.indexJ];
			
			handshakeMessage.setHeader(msgHeader);
			handshakeMessage.setClientID(msgPeerID);
			
			checkstat(0, msgHeader.length, 0, msgPeerID.length);

		} catch (Exception e) {
			ClientProcess.showLog(e.toString());
			handshakeMessage = null;
		}
		return handshakeMessage;
	}

	// Encodes a given message in the format HandshakeMessage
	public static byte[] encodeMessage(HandshakeMessage handshakeMessage) {

		byte[] sendMessage = new byte[32];
		
		try {
			checkstat(0, handshakeMessage.getHeader().length, handshakeMessage.getZeroBits().length, handshakeMessage.getClientID().length);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			
				
				System.arraycopy(handshakeMessage.getHeader(), 0, sendMessage, 0, handshakeMessage.getHeader().length);

	
				System.arraycopy(handshakeMessage.getZeroBits(), 0, sendMessage, 18, 10 - 1);
				System.arraycopy(handshakeMessage.getClientID(), 0, sendMessage, 18 + 10,
						handshakeMessage.getClientID().length);
				
				checkstat(0, sendMessage.length-32, sendMessage.length-32, sendMessage.length-32);
		} catch (Exception e) {
			ClientProcess.showLog(e.toString());
			sendMessage = null;
		}

		return sendMessage;
	}
}
