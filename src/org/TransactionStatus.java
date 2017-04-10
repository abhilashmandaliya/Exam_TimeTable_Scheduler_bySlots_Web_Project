package org;

public class TransactionStatus {
	private static String statusMessage = null;

	public static String getStatusMessage() {
		return statusMessage;
	}

	public static void setStatusMessage(String statusMessage) {
		TransactionStatus.statusMessage = statusMessage;
	}
	
	public static void setDefaultStatusMessage(){
		TransactionStatus.statusMessage = "Server didn't generate any response !";
	}
}
