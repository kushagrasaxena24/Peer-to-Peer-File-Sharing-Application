package com.ptop.controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Vector;

import com.ptop.remote.ServerClientInfo;
import com.ptop.utils.Utility;

/*
 * The StartRemotePeers class begins remote peer processes. 
 * It reads configuration file ClientInfo.cfg and starts remote peer processes.
 */
public class StartServerClient {

	public void getConfiguration() {
		String st;
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader("ClientInfo.cfg"));
			int i = 0;
			while ((st = bufferedReader.readLine()) != null) {
				String[] tokens = st.split(" ");
				clientInfoVector.addElement(new ServerClientInfo(tokens[0], tokens[1], tokens[2], i));
				i++;
			}
			bufferedReader.close();
		} catch (Exception ex) {
			System.out.println("Exception:" + ex.toString());
		}
	}

	/**
	 * Checks if all peer has down loaded the file
	 */
	public static synchronized boolean isCompleted() {

		String line;
		int hasFileCount = 1;

		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader("ClientInfo.cfg"));

			while ((line = bufferedReader.readLine()) != null) {
				hasFileCount = hasFileCount * Integer.parseInt(line.trim().split(" ")[3]);
			}
			if (hasFileCount == 0) {
				bufferedReader.close();
				return false;
			} else {
				bufferedReader.close();
				return true;
			}

		} catch (Exception e) {

			return false;
		}

	}

	/**
	 * @param args
	 **/
	public static void main(String[] args) {
		try {
			StartServerClient myStart = new StartServerClient();
			myStart.getConfiguration();

			// get current path
			String path = System.getProperty("user.dir");

			// start clients at remote hosts
			for (int i = 0; i < myStart.clientInfoVector.size(); i++) {
				ServerClientInfo pInfo = (ServerClientInfo) myStart.clientInfoVector.elementAt(i);

				System.out.println("Start server client " + pInfo.clientId + " at " + pInfo.clientAddress);
				String command = "ssh " + pInfo.clientAddress + " cd " + path + "; java peerProcess " + pInfo.clientId;
				myStart.clientProcesses.add(Runtime.getRuntime().exec(command));
				System.out.println(command);
			}

			System.out.println("Waiting for remote peers to terminate..");

			boolean isCompleted = false;
			while (true) {
				// checks for termination
				isCompleted = isCompleted();
				if (isCompleted) {
					System.out.println("All peers are terminated!");
					break;
				} else {
					Utility.isSleep();
				}
			}

		} catch (Exception ex) {
			System.out.println("Exception: " + ex.toString());
		}
	}

	public Vector<ServerClientInfo> clientInfoVector = new Vector<ServerClientInfo>();
	public Vector<Process> clientProcesses = new Vector<Process>();
}