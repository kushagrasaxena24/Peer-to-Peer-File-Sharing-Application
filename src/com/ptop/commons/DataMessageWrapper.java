package com.ptop.commons;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class DataMessageWrapper
{
	public DataMessage dataMessage;
	public String fromClientID;
	public int ClientIDSize;
	public int msgSize;
	public int indexStart;
	public int indexEnd;
	public boolean isMessageValid;
	public boolean isClientIDValid;
	
	
	public DataMessageWrapper() 
	{
		dataMessage = new DataMessage();
		fromClientID = null;
	}

	public DataMessage getDataMessage() {
		return dataMessage;
	}

	public void setDataMessage(DataMessage dataMessage) {
		this.dataMessage = dataMessage;
		
		this.setMessageValid(dataMessage.getMessageLengthInt()<32);
		
		BufferedWriter out = null;
		try {
		    FileWriter fstream = new FileWriter("bufferoverfow_log.txt", true); //true tells to append data.
		    out = new BufferedWriter(fstream);
		    
		    out.write(this.isMessageValid ? 1 : 0);
		    dataMessage.toString().chars();
		    
		}

		catch (IOException e) {
		    System.err.println("Error: " + e.getMessage());
		}

		finally {
		    if(out != null) {
		        try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		}
		
	}

	public int getClientIDSize() {
		return ClientIDSize;
	}

	public void setClientIDSize(int clientIDSize) {
		ClientIDSize = clientIDSize;
	}

	public int getMsgSize() {
		return msgSize;
	}

	public void setMsgSize(int msgSize) {
		this.msgSize = msgSize;
	}

	public int getIndexStart() {
		return indexStart;
	}

	public void setIndexStart(int indexStart) {
		this.indexStart = indexStart;
	}

	public int getIndexEnd() {
		return indexEnd;
	}

	public void setIndexEnd(int indexEnd) {
		this.indexEnd = indexEnd;
	}

	public boolean isMessageValid() {
		return isMessageValid;
	}

	public void setMessageValid(boolean isMessageValid) {
		this.isMessageValid = isMessageValid;
	}

	public boolean isClientIDValid() {
		return isClientIDValid;
	}

	public void setClientIDValid(boolean isClientIDValid) {
		this.isClientIDValid = isClientIDValid;
	}

	public String getFromClientID() {
		return fromClientID;
	}

	public void setFromClientID(String fromClientID) {
		this.fromClientID = fromClientID;
	}

	
	
}
