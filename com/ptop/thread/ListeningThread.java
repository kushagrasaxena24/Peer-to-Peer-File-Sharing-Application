package com.ptop.thread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.ptop.controller.ClientProcess;
import com.ptop.remote.ServerClientHandler;

public class ListeningThread implements Runnable 
{

	
	public ListeningThread(ServerSocket socket, String clientID) 
	{
		this.serverSocketListening = socket;
		this.clientID = clientID;
	}
	
	public void run() 
	{
		while(true)
		{
			try
			{
				serverSocket = serverSocketListening.accept();
				sendingThread = new Thread(new ServerClientHandler(serverSocket,0,clientID));
				ClientProcess.showLog(clientID + " Connection is established");
				ClientProcess.sendingThread.add(sendingThread);
				sendingThread.start(); 
			}
			catch(Exception e)
			{
				ClientProcess.showLog(this.clientID + " Exception in connection: " + e.toString());
			}
		}
	}
	
	public void releaseSocket()
	{
		try 
		{
			if(!serverSocket.isClosed())
			serverSocket.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	private ServerSocket serverSocketListening;
	private String clientID;
	Socket serverSocket;
	Thread sendingThread;
}


