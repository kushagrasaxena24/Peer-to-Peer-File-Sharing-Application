package com.ptop.commons;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.ptop.controller.ClientProcess;

public class BitField {
	private static final char[] _hex = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E',
	'F' };
	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

	public Piece[] pieces;
	public int size;
	public int pieceLength;
	public int firstDifference;
	public int pieceInfo;
	public int greater;
	byte[] DataPayloadData;
	long PayloadDataSize;
	long dataStream;
	String fileStream;
	public boolean BitfieldMatch(long PayloadDataSize)
    {
    	if(DataPayloadData.length==PayloadDataSize)
    		return true;
		
    	else
    	return false;	
    } 
	
	public int getPieceLength() {
		return pieceLength;
	}

	public void setPieceLength(int pieceLength) {
		this.pieceLength = pieceLength;
	}

	public int getFirstDifference() {
		return firstDifference;
	}

	public void setFirstDifference(int firstDifference) {
		this.firstDifference = firstDifference;
	}

	public int getPieceInfo() {
		return pieceInfo;
	}

	public void setPieceInfo(int pieceInfo) {
		this.pieceInfo = pieceInfo;
	}


	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public Piece[] getPieces() {
		return pieces;
	}

	public void setPieces(Piece[] pieces) {
		this.pieces = pieces;
	}

	public byte[] encode() {
		return this.getBytes();
	}

	public static BitField decode(byte[] b) {
		BitField returnBitField = new BitField();
		boolean flag=true;
		for (int i = 0; i < b.length; i++) {
			int count = 7,k=0;
			while (count >= 0) {
				int test = 1 << count;
				int var=8*i+7-count;
				if (var < returnBitField.size) {
					if ((b[i] & (test)) != 0)
						returnBitField.pieces[var].isPresent = 1;
					else
						returnBitField.pieces[var].isPresent = 0;
				}
				count--;
			}
			for(int j=0;j<b.length;j++)
			{
				if(returnBitField.pieces[k].isPresent == 1)
				{
					flag=false;
					break;
				}
			}
			if(flag)
			{
				System.out.println(" Nothing to decode : Empty BitField");
			}
		}
		return returnBitField;
	}
	
	
	public BitField() {
		size = (int) Math.ceil(((double) CommonProperties.fileSize / (double) CommonProperties.pieceSize));
		this.pieces = new Piece[size];
		
		for (int i = 0; i < this.size; i++)
			this.pieces[i] = new Piece();
	}
	public BitField(int size,int pieceLength,int firstDifference,int pieceInfo) {
		size = (int) Math.ceil(((double) CommonProperties.fileSize / (double) CommonProperties.pieceSize));
		this.pieces = new Piece[size];
		this.firstDifference=0;
		this.pieceInfo=0;
		this.pieceLength=size;
		this.size=this.pieceLength;
		
		for (int i = 0; i < this.size; i++)
			this.pieces[i] = new Piece();
	}
	public void setFileStream(String fileStream) {
		boolean match=BitfieldMatch(PayloadDataSize);
		this.fileStream = fileStream;
	}
	public synchronized boolean compare(BitField yourBitField) {
		boolean flag=true;
		if(yourBitField.getSize()==0)
			System.out.println("BitField empty");
		int yourSize = yourBitField.getSize();
		int size=yourSize;
		for (int i = 0; i < yourSize; i++) {
			if (yourBitField.getPieces()[i].getIsPresent() == 1 && this.getPieces()[i].getIsPresent() == 0) {
				return true;
			} else
				continue;
		}	
		for(int i=0;i<size;i++)
		{
			if(yourBitField.getPieces()[i].getIsPresent() == 0)
			{
				flag=true;
			}
			else
			{
				flag=false;
				break;
			}
		}
		if(flag==false)
		{
			System.out.println(" Bitfield: Not present");
		}
		return false;
	}

	public synchronized int returnFirstDiff(BitField yourBitField) {
		int mySize = this.getSize();
		int yourSize = yourBitField.getSize();

		if (mySize >= yourSize) {
			greater=mySize;
			for (int i = 0; i < yourSize; i++) {
				if (yourBitField.getPieces()[i].getIsPresent() == 1 && this.getPieces()[i].getIsPresent() == 0) {
					return i;
				}
			}
		} else {
			greater=yourSize;
			for (int i = 0; i < mySize; i++) {
				if (yourBitField.getPieces()[i].getIsPresent() == 1 && this.getPieces()[i].getIsPresent() == 0) {
					return i;
				}
			}
		}
		
		return -1;
	}

	public byte[] getBytes() {
		int s = this.size / 8;
		if (size % 8 != 0)
			s = s + 1;
		byte[] iP = new byte[s];
		int tempInt = 0;
		int count = 0;
		int Cnt;
		for (Cnt = 1; Cnt <= this.size; Cnt++) {
			int tempP = this.pieces[Cnt - 1].isPresent;
			tempInt = tempInt << 1;
			if (tempP == 1) {
				tempInt = tempInt + 1;
			} else
				tempInt = tempInt + 0;

			if (Cnt % 8 == 0 && Cnt != 0) {
				iP[count] = (byte) tempInt;
				count++;
				tempInt = 0;
			}

		}
		if ((Cnt - 1) % 8 != 0) {
			int tempShift = ((size) - (size / 8) * 8);
			tempInt = tempInt << (8 - tempShift);
			iP[count] = (byte) tempInt;
		}
		return iP;
	}

	static String byteArrayToHexString(byte in[]) {
		
	    char[] hexChars = new char[in.length * 2];
	    for ( int j = 0; j < in.length; j++ ) {
	        int v = in[j] & 0xFF22;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	   return new String(hexChars);
	
}

	public void initOwnBitfield(String OwnPeerId, int hasFile) {

		if (hasFile != 1) {

			for (int i = 0; i < this.size; i++) {
				this.pieces[i].setIsPresent(0);
				this.pieces[i].setFromClientID(OwnPeerId);
			}

		} else {

			for (int i = 0; i < this.size; i++) {
				this.pieces[i].setIsPresent(1);
				this.pieces[i].setFromClientID(OwnPeerId);
			}

		}

	}

	public static int byteArrayToInt(byte[] b, int offset) {
		int value = 0;
		ByteBuffer wrapped = ByteBuffer.wrap(b); // big-endian by default
		value = wrapped.getInt();
		return value;
	}
	public synchronized void updateBitField(String peerId, Piece piece) {
		try {
			if (ClientProcess.ownBitField.pieces[piece.pieceIndex].isPresent == 1) {
				ClientProcess.showLog(peerId + " Piece already present/received");
			} else {
				String fileName = CommonProperties.fileName;
				File file = new File(ClientProcess.clientID, fileName);
				int off = piece.pieceIndex * CommonProperties.pieceSize;
				RandomAccessFile raf = new RandomAccessFile(file, "rw");
				byte[] byteWrite;
				byteWrite = piece.filePiece;

				raf.seek(off);
				raf.write(byteWrite);

				this.pieces[piece.pieceIndex].setIsPresent(1);
				this.pieces[piece.pieceIndex].setFromClientID(peerId);
				raf.close();

				ClientProcess.showLog(ClientProcess.clientID + " has downloaded the PIECE " + piece.pieceIndex
						+ " from Peer " + peerId + ". Now the number of pieces it has is "
						+ ClientProcess.ownBitField.ownPieces());

				if (ClientProcess.ownBitField.isCompleted()) {
					ClientProcess.serverClientInfoHashtable.get(ClientProcess.clientID).isInterested = 0;
					ClientProcess.serverClientInfoHashtable.get(ClientProcess.clientID).isFinished = 1;
					ClientProcess.serverClientInfoHashtable.get(ClientProcess.clientID).isChoked = 0;
					updatePeerInfo(ClientProcess.clientID, 1);

					ClientProcess.showLog(ClientProcess.clientID + " has DOWNLOADED the complete file.");
				}
			}

		} catch (Exception e) {
			ClientProcess.showLog(ClientProcess.clientID + " EROR in updating bitfield " + e.getMessage());
		}

	}

	public int ownPieces() {
		int count = 0;
		for (int i = 0; i < this.size; i++)
			if (this.pieces[i].isPresent == 1)
				count++;

		return count;
	}

	public boolean isCompleted() {

		for (int i = 0; i < this.size; i++) {
			if (this.pieces[i].isPresent == 0) {
				return false;
			}
		}
		return true;
	}

	public void updatePeerInfo(String clientID, int hasFile) {

		try {
			BufferedReader in = new BufferedReader(new FileReader("ClientInfo.cfg"));
			List<String>list=new ArrayList<>();
			String line;
			StringBuffer buffer = new StringBuffer();

			while ((line = in.readLine()) != null) {
				list.add(line.trim().split(" ")[0]+ " " + hasFile);
				if (line.trim().split(" ")[0].equals(clientID)) {
					buffer.append(line.trim().split(" ")[0] + " " + line.trim().split(" ")[1] + " "
							+ line.trim().split(" ")[2] + " " + hasFile);
				} else {
					buffer.append(line);

				}
				buffer.append("\n");
			}

			in.close();
			for(int i=0;i<list.size();i++)
			{
				System.out.println(list.get(i));
				isCompleted();
			}
				
			BufferedWriter out = new BufferedWriter(new FileWriter("ClientInfo.cfg"));
			out.write(buffer.toString());

			out.close();
		} catch (Exception e) {
			ClientProcess.showLog(clientID + " Error in updating the ClientInfo.cfg " + e.getMessage());
		}
	}

}
