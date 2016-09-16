package tesis.s2cc;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class RemoteConnection {

	private static final String TAG = "RemoteConnection";

	private Socket mSocket;
	private OutputStream mSocketOutput;
	private BufferedReader mSocketInput;

	private String mIp;
	private int mPort;

	public RemoteConnection( String ip, int port ) {
		mIp = ip;
		mPort = port;
	}

	public void connect(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				mSocket = new Socket();
				InetSocketAddress socketAddress = new InetSocketAddress(mIp, mPort);
				try {
					mSocket.connect(socketAddress);
					mSocketOutput = mSocket.getOutputStream();
					mSocketInput = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
					new ReceiveThread().start();
				}
				catch (IOException e) {
					Log.e(TAG, "Fail to connect to server on: " + mIp + ":" + mPort);
				}
			}
		}).start();
	}

	public void disconnect(){
		try {
			mSocket.close();
		} catch (IOException e) {
			Log.i(TAG, "Disconnected from server: " + e.getMessage());
		}
	}

	public void send(String message){
		try {
			mSocketOutput.write(message.getBytes());
		} catch (IOException e) {
			Log.e(TAG, "Fail to send message to server: " + e.getMessage());
		}
	}

	private class ReceiveThread extends Thread implements Runnable{
		public void run(){
			try {
				String message;
				while((message = mSocketInput.readLine()) != null) {
					Log.i(TAG, "Received message from server: " + message);
				}
			} catch (IOException e) {
				Log.e(TAG, "Error while receiving message from server: " + e.getMessage());
			}
		}
	}
}