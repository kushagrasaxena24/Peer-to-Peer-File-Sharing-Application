package com.ptop.remote;

import java.util.Date;

import com.ptop.commons.BitField;

public class ServerClientInfo implements Comparable<ServerClientInfo> {

	public ServerClientInfo(String cId, String cAddress, String cPort, int cIndex) {
		clientId = cId;
		clientAddress = cAddress;
		clientPort = cPort;
		bitField = new BitField();
		clientIndex = cIndex;
	}

	public ServerClientInfo(String cId, String cAddress, String cPort, int cIsFirstClient, int pIndex) {
		clientId = cId;
		clientAddress = cAddress;
		clientPort = cPort;
		isFirstClient = cIsFirstClient;
		bitField = new BitField();
		clientIndex = pIndex;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientAddress() {
		return clientAddress;
	}

	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}

	public String getClientPort() {
		return clientPort;
	}

	public void setClientPort(String clientPort) {
		this.clientPort = clientPort;
	}

	public int getIsFirstClient() {
		return isFirstClient;
	}

	public void setIsFirstClient(int isFirstClient) {
		this.isFirstClient = isFirstClient;
	}

	public int compareTo(ServerClientInfo o1) {

		if (this.dataValue > o1.dataValue)
			return 1;
		else if (this.dataValue == o1.dataValue)
			return 0;
		else
			return -1;
	}

	public String clientId;
	public String clientAddress;
	public String clientPort;
	public int isFirstClient;
	public double dataValue = 0;
	public int isInterested = 1;
	public int isPreferredNeighbor = 0;
	public int isOptUnchokedNeighbor = 0;
	public int isChoked = 1;
	public BitField bitField;
	public int state = -1;
	public int clientIndex;
	public int isFinished = 0;
	public int isHandShaked = 0;
	public Date startTime;
	public Date endTime;

}
