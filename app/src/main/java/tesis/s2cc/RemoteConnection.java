package tesis.s2cc;

import android.os.AsyncTask;
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
		mSocket = new Socket();
	}

	public void connect(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				InetSocketAddress socketAddress = new InetSocketAddress(mIp, mPort);
				try {
					mSocket.connect(socketAddress);
					mSocketOutput = mSocket.getOutputStream();
					mSocketInput = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
					(new Thread() {
						@Override
						public void run() {
							try {
								String message;
								while((message = mSocketInput.readLine()) != null) {
									Log.i(TAG, "Received message from server: " + message);
								}
							} catch (IOException e) {
								Log.i(TAG, "Receiver thread stopping due to: " + e.getMessage());
							}
						}
					}).start();
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
		(new AsyncTask<String, Void, Void>() {
			@Override
			protected Void doInBackground(String... params) {
				try {
					mSocketOutput.write(params[0].getBytes());
				} catch (IOException e) {
					Log.e(TAG, "Fail to send message to server: " + e.getMessage());
				}
				return null;
			}
		}).execute(message);
	}

	public boolean isConnected() {
		return mSocket.isConnected();
	}
}