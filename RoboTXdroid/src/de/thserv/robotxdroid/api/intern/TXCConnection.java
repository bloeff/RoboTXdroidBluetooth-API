package de.thserv.robotxdroid.api.intern;


import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.Semaphore;
import de.thserv.robotxdroid.api.*;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class TXCConnection {
	public String mac;
	
	public String status;
	
	public BluetoothSocket socket;
	public OutputStream os;
	public InputStream is;
	
	public Semaphore semaTransferArea;
	public Thread threadTransferArea;
	

	// Repräsentiert eine Verbindung zum ROBO TX Controller
	public TXCConnection() {
		TXCConnection.this.status = "NONE";
	}
	
	
	// Öffnen der Verbindung im Onlinemodus
	public void openOld() {
    	try {
			TXCConnection.this.status = "CONNECTING";
			
			// get remote device by mac, we assume these two devices are already  paired
			BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(TXCConnection.this.mac);

			// UUID for serial connection
			Method m;
			m = device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
			TXCConnection.this.socket = (BluetoothSocket)m.invoke(device, Integer.valueOf(1)); 
			
			//final UUID SERIAL_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
			
			//TXCConnection.this.socket = device.createRfcommSocketToServiceRecord(SERIAL_UUID);

			TXCConnection.this.socket.connect();
			TXCConnection.this.os = TXCConnection.this.socket.getOutputStream();
			TXCConnection.this.is = TXCConnection.this.socket.getInputStream();
			
			{	
				TXCProtocol echo_request = new TXCProtocol("ECHO_REQUEST", new short[] {1, 2, 3, 4});
    			echo_request.write(TXCConnection.this.os);
    			
    			TXCProtocol echo_reply = new TXCProtocol("", null);
    			echo_reply.read(TXCConnection.this.is);

    			if (Arrays.equals(echo_request.shortData, echo_reply.shortData) == true) {
    				TXCConnection.this.status = "CONNECTED";
    				
    			} else if (Arrays.equals(echo_request.shortData, echo_reply.shortData) == false) {
    				TXCConnection.this.status = "INVALID";
    				
    			}
			}
    	} catch (Exception e) {
    		TXCConnection.this.status = "NOT CONNECTED";
    	}
	}
	
	// Öffnen der Verbindung im Downloadmodus
	public void openNew() {
        try {  
    		TXCConnection.this.status = "CONNECTING";
    		
    		// get remote device by mac, we assume these two devices are already  paired
    		BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(TXCConnection.this.mac);
    		
           
    		Method m;
			m = device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
			TXCConnection.this.socket = (BluetoothSocket)m.invoke(device, Integer.valueOf(1));
			
			
    		//final UUID SERIAL_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //UUID for serial connection
 
            //TXCConnection.this.socket = device.createRfcommSocketToServiceRecord(SERIAL_UUID);
        	
            TXCConnection.this.socket.connect();
			TXCConnection.this.os = TXCConnection.this.socket.getOutputStream();
			TXCConnection.this.is = TXCConnection.this.socket.getInputStream();
            
			{	
				//TXCConnection.this.semaStream.acquire();
				TXCProtocol echo_request = new TXCProtocol("ECHO_REQUEST", new short[] {1, 2, 3, 4});
				TXCProtocol echo_reply1 = new TXCProtocol("", null);
				TXCProtocol echo_reply = new TXCProtocol("", null);
    			if(echo_reply.getConnAck(TXCConnection.this.is)){
    			
    				echo_request.write(TXCConnection.this.os);
    				echo_reply1.read(TXCConnection.this.is);
    			}
    			//TXCConnection.this.semaStream.release();

    			if (Arrays.equals(echo_request.shortData, echo_reply1.shortData) == true) {
    				TXCConnection.this.status = "CONNECTED";
    				
    			} else if (Arrays.equals(echo_request.shortData, echo_reply1.shortData) == false) {
    				TXCConnection.this.status = "INVALID";
    				
    			}
			}
			
			
        } catch (Exception e) {
        	TXCConnection.this.status = "NOT CONNECTED";
        	e.printStackTrace();
        }
	}
	
	// Schließen der Verbindung sowohl Online- als auch Downloadmodus
	public void close() {
    	try {
    		this.socket.close();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
		
    	TXCConnection.this.status = "CLOSED";
	}
	int oldConfigId = 0;
	
	// starten der TransferArea
	public void startTransferArea() {
		this.semaTransferArea = new Semaphore(1);
		
		this.threadTransferArea = new Thread() {
			public void run() {
				try {
					do {
						short shortData[] = new short[44];
						
						int intDataPosition = 0;
						
						{ // FTX1_OUTPUT
							TXCConnection.this.semaTransferArea.acquire();
							
							for (int i = 0; i < 4; i+= 1) {
								int intReset = ApiEntry.getInstance().getTransferArea().ftX1out.cnt_reset_cmd_id[i]; // Counter reset requests (increment each time by one)
								shortData[intDataPosition + 0] = (short) ((intReset >> 0) & 0x00FF);
								shortData[intDataPosition + 1] = (short) ((intReset >> 8) & 0x00FF);
								intDataPosition += 2;
							}
							
							for (int i = 0; i < 4; i+= 1) {
								int intMaster = ApiEntry.getInstance().getTransferArea().ftX1out.master[i]; // If not 0, synchronize this channel with the given channel (1:channel 0, ..)
								shortData[intDataPosition + 0] = (short) ((intMaster >> 0) & 0x00FF);
								intDataPosition += 1;
							}
							
							for (int i = 0; i < 8; i+= 1) {
								int intDuty = ApiEntry.getInstance().getTransferArea().ftX1out.duty[i]; // User program selected motor PWM values
								shortData[intDataPosition + 0] = (short) ((intDuty >> 0) & 0x00FF);
								shortData[intDataPosition + 1] = (short) ((intDuty >> 8) & 0x00FF);
								intDataPosition += 2;
							}
							
							for (int i = 0; i < 4; i+= 1) {
								int intDistance = ApiEntry.getInstance().getTransferArea().ftX1out.distance[i]; // Selected distane (counter value) at which motor shall stop
								shortData[intDataPosition + 0] = (short) ((intDistance >> 0) & 0x00FF);
								shortData[intDataPosition + 1] = (short) ((intDistance >> 8) & 0x00FF);
								intDataPosition += 2;
							}
							
							for (int i = 0; i < 4; i+= 1) {
								int intMotor = ApiEntry.getInstance().getTransferArea().ftX1out.motor_ex_cmd_id[i]; // Increment by one each time motor_ex settings change
								shortData[intDataPosition + 0] = (short) ((intMotor >> 0) & 0x00FF);
								shortData[intDataPosition + 1] = (short) ((intMotor >> 8) & 0x00FF);
								intDataPosition += 2;
							}
							
							TXCConnection.this.semaTransferArea.release();
						} // FTX1_OUTPUT
						
						TXCProtocol request = new TXCProtocol("REM_IO_REQUEST", shortData);
						request.write(TXCConnection.this.os);
	
						TXCProtocol response = new TXCProtocol("", null);
						response.read(TXCConnection.this.is);
						
						{ // FTX1_INPUT
							TXCConnection.this.semaTransferArea.acquire();
							
							short[] shortResponse = response.shortData;
							
							int intResponsePosition = 0;

							if(ApiEntry.getInstance().isOnlineMode())
								intResponsePosition += 4; // ShmIfId 4 bytes
							
							for (int i = 0; i<8;i++){//uni 16 bytes
								short val = (short) (((short)shortResponse[intResponsePosition + 1] << 8) + ((short)shortResponse [intResponsePosition + 0] << 0));
								ApiEntry.getInstance().getTransferArea().ftX1in.uni[i] = val;
								intResponsePosition += 2;
							}
							
							for (int i = 0; i<4;i++){//cnt_in 4 bytes
								short val = (short) (((short)shortResponse [intResponsePosition + 0] << 0));
								ApiEntry.getInstance().getTransferArea().ftX1in.cnt_in[i] = val;
								intResponsePosition += 1;
							}
							
							for (int i = 0; i<4;i++){//counter 8 bytes
								short val = (short) (((short)shortResponse[intResponsePosition + 1] << 8) + ((short)shortResponse [intResponsePosition + 0] << 0));
								ApiEntry.getInstance().getTransferArea().ftX1in.counter[i] = val;
								intResponsePosition += 2;
							}
							
							{// diplsay_button_left 2 bytes
								short val = (short) (((short)shortResponse[intResponsePosition + 1] << 8) + ((short)shortResponse [intResponsePosition + 0] << 0));
								ApiEntry.getInstance().getTransferArea().ftX1in.display_button_left = val;
								intResponsePosition += 2; 
							}
							
							{// display_button_right 2 bytes
								short val = (short) (((short)shortResponse[intResponsePosition + 1] << 8) + ((short)shortResponse [intResponsePosition + 0] << 0));
								ApiEntry.getInstance().getTransferArea().ftX1in.display_button_right = val;
								intResponsePosition += 2; 
							}
							
							for (int i = 0; i<4;i++){//cnt_reset_cmd_id 8 bytes
								short val = (short) (((short)shortResponse[intResponsePosition + 1] << 8) + ((short)shortResponse [intResponsePosition + 0] << 0));
								ApiEntry.getInstance().getTransferArea().ftX1in.cnt_reset_cmd_id[i] = val;
								intResponsePosition += 2;
							}
							
							for (int i = 0; i<4;i++){//motor_ex_cmd_id 8 bytes
								short val = (short) (((short)shortResponse[intResponsePosition + 1] << 8) + ((short)shortResponse [intResponsePosition + 0] << 0));
								ApiEntry.getInstance().getTransferArea().ftX1in.motor_ex_cmd_id[i] = val;
								intResponsePosition += 2;
							} 

							intResponsePosition += 1;
							
							TXCConnection.this.semaTransferArea.release();
						} // FTX1_INPUT
						
						TXCConnection.this.semaTransferArea.acquire();
						int newId = ApiEntry.getInstance().getTransferArea().ftX1state.config_id;
						TXCConnection.this.semaTransferArea.release();
						if( newId > oldConfigId){//Prüfen ob config geändert
							oldConfigId = newId;

							shortData = new short[48];
							
							intDataPosition = 0;
							
							{ // FTX1_OUTPUT
								TXCConnection.this.semaTransferArea.acquire();
								
								for (int i = 0; i < 4; i+= 1) {
									int motor;
									if(ApiEntry.getInstance().getTransferArea().ftX1config.motor[i])
										motor = 1; // Counter reset requests (increment each time by one)
									else
										motor = 0;
									shortData[intDataPosition + 0] = (short) ((motor >> 0) & 0x00FF);
									intDataPosition += 1;
								}
								
								for (int i = 0; i < 4; i+= 1) {
									for(int j = 0; j < 4 ; j++){
										int motor_config = ApiEntry.getInstance().getTransferArea().ftX1config.motor_config[i][j];
										shortData[intDataPosition + 0] = (short) ((motor_config >> 0) & 0x00FF);
										intDataPosition += 1;
									}
								}
								
								for (int i=0 ; i < 8 ; i++) {
							        int mode = ApiEntry.getInstance().getTransferArea().ftX1config.uni[i].mode;
							        shortData[intDataPosition + 0] = (short) ((mode >> 0) & 0x00FF);
							        if (ApiEntry.getInstance().getTransferArea().ftX1config.uni[i].digital) 
							        	shortData[intDataPosition + 0] |= (1 << 7); 
							    }
							    for (int i=0 ; i < 4 ; i++) {
							    	int cnt = ApiEntry.getInstance().getTransferArea().ftX1config.cnt[i].mode;
							    	 shortData[intDataPosition + 0] = (short) ((cnt >> 0) & 0x00FF);
							    }
								
								TXCConnection.this.semaTransferArea.release();
							} // FTX1_OUTPUT
							
							TXCProtocol configRequest = new TXCProtocol("REM_IO_REQUEST", shortData);
							configRequest.write(TXCConnection.this.os);
		
							TXCProtocol configResponse = new TXCProtocol("", null);
							configResponse.read(TXCConnection.this.is);
							
							{ // FTX1_INPUT
								TXCConnection.this.semaTransferArea.acquire();
								
								//short[] shortResponse = configResponse.shortData;
								
								//int intResponsePosition = 0;


								//intResponsePosition += 4; // ShmIfId 4 bytes
								
								

								//intResponsePosition += 1;
								
								TXCConnection.this.semaTransferArea.release();
							} // FTX1_INPUT
						}
						Thread.sleep(10);
					} while (true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		
		this.threadTransferArea.start();
	}
	
	// stoppen der TransferArea
	public void stopTransferArea() {
    	try {
    		this.threadTransferArea.interrupt();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
	}
	
	
}
