package com.ptop.client;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.util.Date;
import java.util.Enumeration;
import java.util.Random;
import java.util.Scanner;
import java.util.Date;
import java.util.List;
import java.util.Calendar;
import java.util.Collection;
import java.util.Arrays;
import java.util.regex.MatchResult;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.Callable;
import java.nio.ByteBuffer;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Serializable;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.CharConversionException;

import com.ptop.commons.BitField;
import com.ptop.commons.CommonProperties;
import com.ptop.commons.DataMessage;
import com.ptop.commons.DataMessageWrapper;
import com.ptop.commons.Piece;
import com.ptop.controller.ClientProcess;
import com.ptop.remote.ServerClientInfo;
import com.ptop.utils.Utility;

public class DataProcessor implements Runnable {
	
	//ankit
	private int stateFlag;
	
	
	
	public int getStateFlag() {
		return stateFlag;
	}

	public void setStateFlag(int stateFlag) {
		this.stateFlag = stateFlag;
	}

	public DataProcessor(String thisClientId) {
		DataProcessor.thisClientId = thisClientId;
		this.setBitFeildSent(false);
		this.setChoked(false);
		this.setDataSent(false);
		this.setDecompressed(false);
		this.setHaveSent(false);
		this.setInterested(false);
		this.setInterestedInExtraData(false);
		this.setNotInterested(false);
		this.setPeiceSent(false);
		this.setRequestSent(false);
		this.setUnchoked(false);
		this.setNotAsleep(1);
		
	}

	public void cts(String dataType, int state) {
		ClientProcess.showLog("Data Processor result : msgType = " + dataType + " State = " + state);
	}

	public void run() {

		DataMessage dataMessageObj;
		DataMessageWrapper dataWrapperObj;
		String msgTypeString;
		String rClientId;

		while (isThreadRunning) {
			dataWrapperObj = ClientProcess.removeprocessFromMsgQueue();
			while (dataWrapperObj == null) {
				Thread.currentThread();
				this.setNotAsleep(0);
				sleep();
				dataWrapperObj = ClientProcess.removeprocessFromMsgQueue();
			}
			dataMessageObj = dataWrapperObj.getDataMessage();
			msgTypeString = dataMessageObj.getMessageTypeString();
			rClientId = dataWrapperObj.getFromClientID();
			int state = ClientProcess.serverClientInfoHashtable.get(rClientId).state;
			if (state != 14 && msgTypeString.equals("4")) {
				ClientProcess.showLog(ClientProcess.clientID + " receieved message from server " + rClientId);
				if (isInterestedInExtraData(dataMessageObj, rClientId)) {
					sendInterested(ClientProcess.clientIDToSocketMap.get(rClientId), rClientId);
					ClientProcess.serverClientInfoHashtable.get(rClientId).state = 9;
				} else {
					sendNotInterested(ClientProcess.clientIDToSocketMap.get(rClientId), rClientId);
					ClientProcess.serverClientInfoHashtable.get(rClientId).state = 13;
				}
			} else {
				if (state == 2) {
					try {
						setStateFlag(this.check(2));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					ifCase2(msgTypeString, state, rClientId);
				}
				if (state == 3) {
					try {
						setStateFlag(this.check(3));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();}
					
					ifCase3(msgTypeString, state, rClientId);
				}
				if (state == 4) {
					try {
						setStateFlag(this.check(4));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					
				}
					ifCase4(msgTypeString, state, rClientId, dataMessageObj);
				}
				if (state == 8) {
					try {
						setStateFlag(this.check(8));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					
				}
					ifCase8(msgTypeString, state, rClientId, dataMessageObj);
				}
				if (state == 9) {
					try {
						setStateFlag(this.check(9));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					
				}
					ifCase9(msgTypeString, state, rClientId);
				}
				if (state == 11) {
					try {
						setStateFlag(this.check(11));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					
				}
					ifCase11(msgTypeString, state, rClientId, dataMessageObj);
				}
				if (state == 14) {
					try {
						setStateFlag(this.check(14));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					
				}
					ifCase14(msgTypeString, state, rClientId, dataMessageObj);

				}
			}
				
		}
	}

	public static boolean isThreadRunning() {
		return isThreadRunning;
	}

	public static void setThreadRunning(boolean isThreadRunning) {
		DataProcessor.isThreadRunning = isThreadRunning;
	}

	public static String getThisClientId() {
		return thisClientId;
	}

	public static void setThisClientId(String thisClientId) {
		DataProcessor.thisClientId = thisClientId;
	}

	public static int getCurrentClientState() {
		return currentClientState;
	}

	public static void setCurrentClientState(int currentClientState) {
		DataProcessor.currentClientState = currentClientState;
	}

	public RandomAccessFile getRandomAccessFile() {
		return randomAccessFile;
	}

	public void setRandomAccessFile(RandomAccessFile randomAccessFile) {
		this.randomAccessFile = randomAccessFile;
	}

	private void sleep() {
		try {
			if(this.getNotAsleep()==0)
			{
			this.setNotAsleep(1);
			Thread.sleep(500);
			}
			else
			{
				this.setNotAsleep(1);
				Thread.sleep(500);	
			}
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	private void ifCase14(String msgType, int state, String rClientId, DataMessage dataMessage) {
		if (msgType.equals("4")) {
			
			ifCase114(msgType, state, rClientId, dataMessage);
		
		} else if (msgType.equals("1")) {
			ifCase014(msgType, state, rClientId, dataMessage);
		}
		else
		{
			ifCase14notexecuted();
		}

	}
	
	
	
	private void ifCase114(String msgType, int state, String rClientId, DataMessage dataMessage) {
			if (isInterestedInExtraData(dataMessage, rClientId)) {
				sendInterested(ClientProcess.clientIDToSocketMap.get(rClientId), rClientId);
				ClientProcess.serverClientInfoHashtable.get(rClientId).state = 9;
			} else {
				sendNotInterested(ClientProcess.clientIDToSocketMap.get(rClientId), rClientId);
				ClientProcess.serverClientInfoHashtable.get(rClientId).state = 13;
			}

	}
	
	private void ifCase014(String msgType, int state, String rClientId, DataMessage dataMessage) {
	
			ClientProcess.showLog(ClientProcess.clientID + " is UNCHOKED by Peer " + rClientId);
			ClientProcess.serverClientInfoHashtable.get(rClientId).state = 14;
	
	}
	
	private void ifCase14notexecuted() {
		
		ClientProcess.showLog(ClientProcess.clientID + "falied to send");

}


	
	
	//ankit
	
	public int check(int state) throws IOException {
		
		BufferedWriter out = null;
		try {
		    FileWriter fstream = new FileWriter("track_state.txt", true); //true tells to append data.
		    out = new BufferedWriter(fstream);
		    if(state == 1 || state == 4 || state == 9)
			{
				out.write(state);
				out.close();
				return 1;
			}
			else
			{
				out.write(state);
				out.close();
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
	//
	
	
	
	

	private void ifCase11(String msgType, int state, String rClientId, DataMessage dataMessage) {
		if (msgType.equals("7")) {
			try {
				setStateFlag(this.check(state));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();}
			
			byte[] buffer = dataMessage.getPayload();
			ClientProcess.serverClientInfoHashtable.get(rClientId).endTime = new Date();
			long timeLapse = ClientProcess.serverClientInfoHashtable.get(rClientId).endTime.getTime()
					- ClientProcess.serverClientInfoHashtable.get(rClientId).startTime.getTime();

			ClientProcess.serverClientInfoHashtable
					.get(rClientId).dataValue = ((double) (buffer.length + 4 + 1) / (double) timeLapse) * 100;

			Piece p = Piece.decodePiece(buffer);
			ClientProcess.ownBitField.updateBitField(rClientId, p);

			int toGetPeiceIndex = ClientProcess.ownBitField
					.returnFirstDiff(ClientProcess.serverClientInfoHashtable.get(rClientId).bitField);
			if (toGetPeiceIndex != -1) {
				sendRequest(ClientProcess.clientIDToSocketMap.get(rClientId), toGetPeiceIndex, rClientId);
				ClientProcess.serverClientInfoHashtable.get(rClientId).state = 11;
				ClientProcess.serverClientInfoHashtable.get(rClientId).startTime = new Date();
			} else
				ClientProcess.serverClientInfoHashtable.get(rClientId).state = 13;

			ClientProcess.readClientInfoAgain();
			Enumeration<String> keys = ClientProcess.serverClientInfoHashtable.keys();
			while (keys.hasMoreElements()) {
				String key = (String) keys.nextElement();
				ServerClientInfo pref = ClientProcess.serverClientInfoHashtable.get(key);

				if (key.equals(ClientProcess.clientID))
					continue;
				if (pref.isFinished == 0 && pref.isChoked == 0 && pref.isHandShaked == 1) {
					sendHave(ClientProcess.clientIDToSocketMap.get(key), key);
					ClientProcess.serverClientInfoHashtable.get(key).state = 3;
				}
			}

			buffer = null;
			dataMessage = null;

		} else if (msgType.equals("0")) {
			ClientProcess.showLog(ClientProcess.clientID + " is CHOKED by Peer " + rClientId);
			ClientProcess.serverClientInfoHashtable.get(rClientId).state = 14;
		}

	}

	private void ifCase9(String msgType, int state, String rClientId) {
		if (msgType.equals("0")) {
			try {
				setStateFlag(this.check(state));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();}
			
			ClientProcess.showLog(ClientProcess.clientID + " is CHOKED by Peer " + rClientId);
			ClientProcess.serverClientInfoHashtable.get(rClientId).state = 14;
		} else if (msgType.equals("1")) {
			ClientProcess.showLog(ClientProcess.clientID + " is UNCHOKED by Peer " + rClientId);
			int firstdiff = ClientProcess.ownBitField
					.returnFirstDiff(ClientProcess.serverClientInfoHashtable.get(rClientId).bitField);
			if (firstdiff != -1) {
				sendRequest(ClientProcess.clientIDToSocketMap.get(rClientId), firstdiff, rClientId);
				ClientProcess.serverClientInfoHashtable.get(rClientId).state = 11;
				ClientProcess.serverClientInfoHashtable.get(rClientId).startTime = new Date();
			} else
				ClientProcess.serverClientInfoHashtable.get(rClientId).state = 13;
		}

	}

	private void ifCase8(String msgType, int state, String rClientId, DataMessage dataMessage) {
		if (msgType.equals("5")) {
			try {
				setStateFlag(this.check(state));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();}
			
			if (isInterestedInExtraData(dataMessage, rClientId)) {
				sendInterested(ClientProcess.clientIDToSocketMap.get(rClientId), rClientId);
				ClientProcess.serverClientInfoHashtable.get(rClientId).state = 9;
			} else {
				sendNotInterested(ClientProcess.clientIDToSocketMap.get(rClientId), rClientId);
				ClientProcess.serverClientInfoHashtable.get(rClientId).state = 13;
			}
		}

	}

	private void ifCase4(String msgType, int state, String rClientId, DataMessage dataMessage) {
		if (msgType.equals("6")) {
			try {
				setStateFlag(this.check(state));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();}
			
			sendPeice(ClientProcess.clientIDToSocketMap.get(rClientId), dataMessage, rClientId);
			if (!ClientProcess.preferedNeighbors.containsKey(rClientId)
					&& !ClientProcess.unchokedNeighbors.containsKey(rClientId)) {
				sendChoke(ClientProcess.clientIDToSocketMap.get(rClientId), rClientId);
				ClientProcess.serverClientInfoHashtable.get(rClientId).isChoked = 1;
				ClientProcess.serverClientInfoHashtable.get(rClientId).state = 6;
			}
		}

	}

	private void ifCase3(String msgType, int state, String rClientId) {
		if (msgType.equals("3")) {
			try {
				setStateFlag(this.check(state));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();}
			
			ClientProcess
					.showLog(ClientProcess.clientID + " receieved a NOT INTERESTED message from Peer " + rClientId);
			ClientProcess.serverClientInfoHashtable.get(rClientId).isInterested = 0;
			ClientProcess.serverClientInfoHashtable.get(rClientId).state = 5;
			ClientProcess.serverClientInfoHashtable.get(rClientId).isHandShaked = 1;
		} else if (msgType.equals("2")) {
			ClientProcess.showLog(ClientProcess.clientID + " receieved an INTERESTED message from Peer " + rClientId);
			ClientProcess.serverClientInfoHashtable.get(rClientId).isInterested = 1;
			ClientProcess.serverClientInfoHashtable.get(rClientId).isHandShaked = 1;

			if (!ClientProcess.preferedNeighbors.containsKey(rClientId)
					&& !ClientProcess.unchokedNeighbors.containsKey(rClientId)) {
				sendChoke(ClientProcess.clientIDToSocketMap.get(rClientId), rClientId);
				ClientProcess.serverClientInfoHashtable.get(rClientId).isChoked = 1;
				ClientProcess.serverClientInfoHashtable.get(rClientId).state = 6;
			} else {
				ClientProcess.serverClientInfoHashtable.get(rClientId).isChoked = 0;
				sendUnChoke(ClientProcess.clientIDToSocketMap.get(rClientId), rClientId);
				ClientProcess.serverClientInfoHashtable.get(rClientId).state = 4;
			}
		}

	}

	private void ifCase2(String msgType, int state, String rClientId) {
		if (msgType.equals("5")) {
			try {
				setStateFlag(this.check(state));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();}
			ClientProcess.showLog(ClientProcess.clientID + " receieved a BITFIELD message from Peer " + rClientId);
			sendBitField(ClientProcess.clientIDToSocketMap.get(rClientId), rClientId);
			ClientProcess.serverClientInfoHashtable.get(rClientId).state = 3;
		}
	}

	private void sendRequest(Socket socket, int pieceNo, String serverClientID) {

		byte[] pieceByte = new byte[4];
		for (int i = 0; i < 4; i++) {
			pieceByte[i] = 0;
		}
		byte[] pieceIndexByte = Utility.intToByteArray(pieceNo);
		System.arraycopy(pieceIndexByte, 0, pieceByte, 0, pieceIndexByte.length);
		DataMessage d = new DataMessage("6", pieceByte);
		byte[] b = DataMessage.encodeMessage(d);
		sendData(socket, b);
		pieceByte = null;
		pieceIndexByte = null;
		b = null;
		d = null;
		this.setRequestSent(true);
	}

	private void sendPeice(Socket socket, DataMessage d, String serverClientID) // d == requestmessage
	{
		byte[] bytePieceIndex = d.getPayload();
		int pieceIndex = Utility.byteArrayToInt(bytePieceIndex);
		ClientProcess.showLog(ClientProcess.clientID + " sending a PIECE message for piece " + pieceIndex + " to Peer "
				+ serverClientID);
		byte[] byteRead = new byte[CommonProperties.pieceSize];
		int noBytesRead = 0;
		File file = new File(ClientProcess.clientID, CommonProperties.fileName);
		try {
			randomAccessFile = new RandomAccessFile(file, "r");
			randomAccessFile.seek(pieceIndex * CommonProperties.pieceSize);
			noBytesRead = randomAccessFile.read(byteRead, 0, CommonProperties.pieceSize);
		} catch (IOException e) {
			ClientProcess.showLog(ClientProcess.clientID + " ERROR in reading the file : " + e.toString());
		}
		if (noBytesRead == 0) {
			ClientProcess.showLog(ClientProcess.clientID + " ERROR :  Zero bytes read from the file !");
		} else if (noBytesRead < 0) {
			ClientProcess.showLog(ClientProcess.clientID + " ERROR : File could not be read properly.");
		}
		byte[] buffer = new byte[noBytesRead + 4];
		System.arraycopy(bytePieceIndex, 0, buffer, 0, 4);
		System.arraycopy(byteRead, 0, buffer, 4, noBytesRead);

		DataMessage sendMessage = new DataMessage("7", buffer);
		byte[] b = DataMessage.encodeMessage(sendMessage);
		sendData(socket, b);

		buffer = null;
		byteRead = null;
		b = null;
		bytePieceIndex = null;
		sendMessage = null;
		this.setPeiceSent(true);

		try {
			randomAccessFile.close();
		} catch (Exception e) {
			this.setPeiceSent(false);
			e.printStackTrace();
		}
	}
	private void sendInsMsg(Socket socket, String serverClientID, boolean flag) {
		if(flag==false)
		{
			ClientProcess.showLog(ClientProcess.clientID + " sending a NOT INTERESTED message to Peer " + serverClientID);
			DataMessage d = new DataMessage("3");
			byte[] msgByte = DataMessage.encodeMessage(d);
			sendData(socket, msgByte);
			this.setNotInterested(true);
		}
		else
		{
			ClientProcess.showLog(ClientProcess.clientID + " sending an INTERESTED message to Peer " + serverClientID);
			DataMessage d = new DataMessage("2");
			byte[] msgByte = DataMessage.encodeMessage(d);
			sendData(socket, msgByte);
			this.setInterested(true);
		}
	}
	private void sendNotInterested(Socket socket, String serverClientID) {
		sendInsMsg(socket, serverClientID, false);
		this.setNotInterested(true);

	}

	private void sendInterested(Socket socket, String serverClientID) {
		sendInsMsg(socket, serverClientID, true);
		this.setInterested(false);

	}

	private boolean isInterestedInExtraData(DataMessage d, String rClientId) {

		BitField b = BitField.decode(d.getPayload());
		ClientProcess.serverClientInfoHashtable.get(rClientId).bitField = b;

		if (ClientProcess.ownBitField.compare(b))
		{
			this.setInterestedInExtraData(true);
			return true;
		}
		else
		{
			this.setInterestedInExtraData(false);
			return false;
		}
	}

	private void sendUnChoke(Socket socket, String serverClientID) {

		ClientProcess.showLog(ClientProcess.clientID + " sending UNCHOKE message to Peer " + serverClientID);
		DataMessage d = new DataMessage("1");
		byte[] msgByte = DataMessage.encodeMessage(d);
		sendData(socket, msgByte);
		this.setUnchoked(true);
	}

	private void sendChoke(Socket socket, String serverClientID) {
		ClientProcess.showLog(ClientProcess.clientID + " sending CHOKE message to Peer " + serverClientID);
		DataMessage d = new DataMessage("0");
		byte[] msgByte = DataMessage.encodeMessage(d);
		sendData(socket, msgByte);
		this.setChoked(true);
	}

	private void sendBitField(Socket socket, String serverClientID) {

		ClientProcess.showLog(ClientProcess.clientID + " sending BITFIELD message to Peer " + serverClientID);
		byte[] encodedBitField = ClientProcess.ownBitField.encode();

		DataMessage d = new DataMessage("5", encodedBitField);
		sendData(socket, DataMessage.encodeMessage(d));

		encodedBitField = null;
		
		this.setBitFeildSent(true);
	}

	private void sendHave(Socket socket, String serverClientID) {

		ClientProcess.showLog(ClientProcess.clientID + " sending HAVE message to Peer " + serverClientID);
		byte[] encodedBitField = ClientProcess.ownBitField.encode();
		DataMessage d = new DataMessage("4", encodedBitField);
		sendData(socket, DataMessage.encodeMessage(d));

		encodedBitField = null;
		
		this.setHaveSent(true);
	}

	public String decompressGZIP(byte[] gzip) {
		try {
			java.io.ByteArrayInputStream bytein = new java.io.ByteArrayInputStream(gzip);
			java.util.zip.GZIPInputStream gzin = new java.util.zip.GZIPInputStream(bytein);
			java.io.ByteArrayOutputStream byteout = new java.io.ByteArrayOutputStream();
			int res = 0;
			byte buf[] = new byte[1024];
			while (res >= 0) {
				res = gzin.read(buf, 0, buf.length);
				if (res > 0) {
					byteout.write(buf, 0, res);
				}
			}
			byte uncompressed[] = byteout.toByteArray();
			this.setDecompressed(true);
			return (uncompressed.toString());
		} catch (Exception e) {
			this.setDecompressed(false);
			System.out.println("Exception in zip");
		}
		this.setDecompressed(false);
		return null;
	}

	public static byte[] asBytes(String s) {
		String tmp;
		byte[] b = new byte[s.length() / 2];
		int i;
		for (i = 0; i < s.length() / 2; i++) {
			tmp = s.substring(i * 2, i * 2 + 2);
			b[i] = (byte) (Integer.parseInt(tmp, 16) & 0xff);
		}
		return b;
	}

	private int sendData(Socket socket, byte[] encodedBitField) {
		int attempts = 9;
		int flag = 1;
		while(sendDataEncoded(socket, encodedBitField) == 0 && attempts >= 0)
		{
			flag = 0;
			ClientProcess.showLog("Attempt to send Data failed, try nbumber" + (10-attempts));
			attempts--;
			continue;
		}
		if(flag==1)
			return 1;
		else
		{
		return sendDataEncoded(socket, encodedBitField);
		}
	}
	
	private int sendDataEncoded(Socket socket, byte[] encodedBitField) {
		try {
			OutputStream out = socket.getOutputStream();
			out.write(encodedBitField);
		} catch (IOException e) {
			e.printStackTrace();
			this.setDataSent(false);
			return 0;
		}
		this.setDataSent(true);
		return 1;
	}

	private static boolean isThreadRunning = true;
	private boolean isDataSent;
	private boolean isDecompressed;
	private boolean isHaveSent;
	private boolean isBitFeildSent;
	private boolean isChoked;
	private boolean isUnchoked;
	private boolean isInterestedInExtraData;
	private boolean isInterested;
	private boolean isNotInterested;
	private boolean isPeiceSent;
	private boolean isRequestSent;
	public boolean isDataSent() {
		return isDataSent;
	}

	public void setDataSent(boolean isDataSent) {
		this.isDataSent = isDataSent;
	}

	public boolean isDecompressed() {
		return isDecompressed;
	}

	public void setDecompressed(boolean isDecompressed) {
		this.isDecompressed = isDecompressed;
	}

	public boolean isHaveSent() {
		return isHaveSent;
	}

	public void setHaveSent(boolean isHaveSent) {
		this.isHaveSent = isHaveSent;
	}

	public boolean isBitFeildSent() {
		return isBitFeildSent;
	}

	public void setBitFeildSent(boolean isBitFeildSent) {
		this.isBitFeildSent = isBitFeildSent;
	}

	public boolean isChoked() {
		return isChoked;
	}

	public void setChoked(boolean isChoked) {
		this.isChoked = isChoked;
	}

	public boolean isUnchoked() {
		return isUnchoked;
	}

	public void setUnchoked(boolean isUnchoked) {
		this.isUnchoked = isUnchoked;
	}

	public boolean isInterestedInExtraData() {
		return isInterestedInExtraData;
	}

	public void setInterestedInExtraData(boolean isInterestedInExtraData) {
		this.isInterestedInExtraData = isInterestedInExtraData;
	}

	public boolean isInterested() {
		return isInterested;
	}

	public void setInterested(boolean isInterested) {
		this.isInterested = isInterested;
	}

	public boolean isNotInterested() {
		return isNotInterested;
	}

	public void setNotInterested(boolean isNotInterested) {
		this.isNotInterested = isNotInterested;
	}

	public boolean isPeiceSent() {
		return isPeiceSent;
	}

	public void setPeiceSent(boolean isPeiceSent) {
		this.isPeiceSent = isPeiceSent;
	}

	public boolean isRequestSent() {
		return isRequestSent;
	}

	public void setRequestSent(boolean isRequestSent) {
		this.isRequestSent = isRequestSent;
	}

	private static String thisClientId = null;
	public static int currentClientState = -1;
	RandomAccessFile randomAccessFile;
	private int notAsleep;



	public int getNotAsleep() {
		return notAsleep;
	}

	public void setNotAsleep(int notAsleep) {
		this.notAsleep = notAsleep;
	}
}
