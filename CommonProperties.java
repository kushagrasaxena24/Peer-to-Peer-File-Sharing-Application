package com.ptop.commons;

public class CommonProperties 
{
	public static int getNumOfPreferredNeighbr() {
		return numOfPreferredNeighbr;
	}
	public static void setNumOfPreferredNeighbr(int numOfPreferredNeighbr) {
		CommonProperties.numOfPreferredNeighbr = numOfPreferredNeighbr;
	}
	public static int getUnchokingInterval() {
		return unchokingInterval;
	}
	public static void setUnchokingInterval(int unchokingInterval) {
		CommonProperties.unchokingInterval = unchokingInterval;
	}
	public static int getOptUnchokingInterval() {
		return optUnchokingInterval;
	}
	public static void setOptUnchokingInterval(int optUnchokingInterval) {
		CommonProperties.optUnchokingInterval = optUnchokingInterval;
	}
	public static String getFileName() {
		return fileName;
	}
	public static void setFileName(String fileName) {
		CommonProperties.fileName = fileName;
	}
	public static int getFileSize() {
		return fileSize;
	}
	public static void setFileSize(int fileSize) {
		CommonProperties.fileSize = fileSize;
	}
	public static int getPieceSize() {
		return pieceSize;
	}
	public static void setPieceSize(int pieceSize) {
		CommonProperties.pieceSize = pieceSize;
	}
	public static int numOfPreferredNeighbr;
	public static int unchokingInterval;
	public static int optUnchokingInterval;
	public static String fileName;
	public static int fileSize;
	public static int pieceSize;
}
