package com.ptop.client;

import java.util.Comparator;

import com.ptop.remote.ServerClientInfo;

public class ClientDataRateComparator implements Comparator<ServerClientInfo> {

	private boolean isTrue = true;

	public int compare(ServerClientInfo serverClientInfo, ServerClientInfo serverClientinfo2) {
		if (serverClientInfo == null && serverClientinfo2 == null) {
			return 0;
		}
		if (serverClientInfo == null) {
			return 1;
		}
		if (serverClientinfo2 == null) {
			return -1;
		}
		// Compare objects
		if (serverClientInfo instanceof Comparable) {
			if (isTrue)
				return serverClientInfo.compareTo(serverClientinfo2);
			else
				return serverClientinfo2.compareTo(serverClientInfo);
		} else {
			if (isTrue)
				return serverClientInfo.toString().compareTo(serverClientinfo2.toString());
			else 
				return serverClientinfo2.toString().compareTo(serverClientInfo.toString());
		}
	}
	
	public ClientDataRateComparator(boolean constructor) {
		this.isTrue = constructor;
	}

}
