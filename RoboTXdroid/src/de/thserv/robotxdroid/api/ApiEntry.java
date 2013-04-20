package de.thserv.robotxdroid.api;

import android.util.Log;
import de.thserv.robotxdroid.api.intern.*;
import de.thserv.robotxdroid.api.intern.TXCShmem.shm_if_s;

public class ApiEntry {

	//Eigenes Singleton Objekt
	private static ApiEntry instance;

	// Verbindungsobjekt
	private TXCConnection connection;

	//TransferArea
	private TXCShmem shmem = new TXCShmem();
	private shm_if_s ta;

	// Online- oder Downloadmodus
	private boolean isOnlineMode = true;

	//Privater Konstruktor (Singleton)
	private ApiEntry() {
	}

	//Liefert das Singletonobjekt
	public synchronized static ApiEntry getInstance() {
		if (instance == null)
			instance = new ApiEntry();
		return instance;
	}

	//Erstellen einer Verbindung fals keine vorhanden ist
	private TXCConnection connectionOperation() {
		if (connection == null)
			connection = new TXCConnection();
		return connection;
	}

	//Erstellen der TansferArea falls es diese noch nicht gibt
	public synchronized shm_if_s getTransferArea() {
		if (ta == null)
			ta = shmem.FISH_X1_TRANSFER;
		return ta;
	}

	// Wechseln ind den Downloadmodus
	public void changeToDownloadMode() {
		isOnlineMode = false;
	}

	//Wechseln in den Onlinemodus
	public void changeToOnlineMode() {
		isOnlineMode = true;
	}

	// Abfragen ob Online- oder Downloadmodus
	public boolean isOnlineMode() {
		return isOnlineMode;
	}

	// Setzen der MAC-Adresse
	public void setMacAdress(String mac){
		this.connectionOperation().mac = mac;
	}
	
	// Abfrage des Verbindungsversuchs
	public String getConnectionStatus(){
		return this.connectionOperation().status;
	}
	
	// Herstellen der Verbindung
	public void open() {
		if (isOnlineMode)
			this.connectionOperation().openOld();
		else
			this.connectionOperation().openNew();
	}

	// Schließen der Verbindung
	public void close() {
		this.connectionOperation().close();
	}
	
	// Starten der TransferArea
	public void startTransferArea(){
		this.connectionOperation().startTransferArea();
	}

	// Stoppen der TransferArea
	public void stopTransferArea(){
		this.connectionOperation().stopTransferArea();
	}
	
	// Eigens für die Steuerung entwickelte Funktion zum setzen der Linken und Rechten Geschwindigkeit
	// Wenn die Werte annähernd gleich sind werden die Motoren synchronisiert angesteuert
	public void doMovement(int left, int right) {
		int div = left - right;
		if(div > -25 && div < 25){
			try {
				ApiEntry.getInstance().connection.semaTransferArea.acquire();
				if(right > 0){
					getTransferArea().ftX1out.duty[0] = (short) (right);
					getTransferArea().ftX1out.duty[1] = 0;
				}else{
					getTransferArea().ftX1out.duty[0] = 0;
					getTransferArea().ftX1out.duty[1] = (short) (right * -1);
				}
				
				if(left > 0){
					getTransferArea().ftX1out.duty[2] = (short) (left);
					getTransferArea().ftX1out.duty[3] = 0;
				}else{
					getTransferArea().ftX1out.duty[2] = 0;
					getTransferArea().ftX1out.duty[3] = (short) (left * -1);
				}
					
				getTransferArea().ftX1out.master[1] = 1;
					
				getTransferArea().ftX1out.motor_ex_cmd_id[0]++;
				getTransferArea().ftX1out.motor_ex_cmd_id[1]++;
			
	
				ApiEntry.getInstance().connection.semaTransferArea.release();
			} catch (Exception e) {
				Log.d("ApiEntry", e.getLocalizedMessage());
				// e.printStackTrace();
			}
		}else{
			try {
				ApiEntry.getInstance().connection.semaTransferArea.acquire();
				if(right > 0){
					getTransferArea().ftX1out.duty[0] = (short) (right);
					getTransferArea().ftX1out.duty[1] = 0;
				}else{
					getTransferArea().ftX1out.duty[0] = 0;
					getTransferArea().ftX1out.duty[1] = (short) (right * -1);
				}
				
				if(left > 0){
					getTransferArea().ftX1out.duty[2] = (short) (left);
					getTransferArea().ftX1out.duty[3] = 0;
				}else{
					getTransferArea().ftX1out.duty[2] = 0;
					getTransferArea().ftX1out.duty[3] = (short) (left * -1);
				}
					
				getTransferArea().ftX1out.master[1] = 0;
					
				getTransferArea().ftX1out.motor_ex_cmd_id[0]++;
				getTransferArea().ftX1out.motor_ex_cmd_id[1]++;
			
	
				ApiEntry.getInstance().connection.semaTransferArea.release();
			} catch (Exception e) {
				Log.d("ApiEntry", e.getLocalizedMessage());
				// e.printStackTrace();
			}
			
		}

	}

	
	// Folgende Funktionen stammen aus der ftMscLib.dll und tragen lediglich Werte in die TransferArea ein
	// und Validieren diese ggf.
	
	// set fish.X1 output structure
	public int StartCounterReset(int cnt_index) {
		if ( cnt_index < 0 || cnt_index >= TXCShmem.IZ_COUNTER )
	        return 1;

	    //  set 'cnt_reset_cmd_id' in output structure
		try {
			ApiEntry.getInstance().connection.semaTransferArea.acquire();
			
			getTransferArea().ftX1in.cnt_reset_cmd_id[cnt_index]  += 1;

			getTransferArea().ftX1in.cnt_resetted[cnt_index] = false;
		    
		    ApiEntry.getInstance().connection.semaTransferArea.release();
		} catch (InterruptedException e) {
			return 1;
		}
		
		
		return 0;
	}


	public int SetOutMotorValues(int motorId, int duty_p, int duty_m) {
		int idx = motorId * 2;
		SetOutPwmValues(idx,   (short)duty_p);
		SetOutPwmValues(idx+1, (short)duty_m);
		    
		return 0;
	}

	public int SetOutPwmValues(int channel, short duty) {
		if (duty < 0 || channel > TXCShmem.IZ_PWM_CHAN)
			return 1;
		try {
			ApiEntry.getInstance().connection.semaTransferArea.acquire();
			if (duty > 512)
				getTransferArea().ftX1out.duty[channel] = 512;
			else
				getTransferArea().ftX1out.duty[channel] = duty;
			ApiEntry.getInstance().connection.semaTransferArea.release();
		} catch (Exception e) {
			return 1;
		}
		return 0;
	}

	public int StartMotorExCmd(int mIdx, int duty, int mDirection, int sIdx, int sDirection, int pulseCnt) {

	    int chanMaster = mIdx * 2;
	    try{
	    	ApiEntry.getInstance().connection.semaTransferArea.acquire();
	    	getTransferArea().ftX1out.duty[chanMaster]     = 0;
	    	getTransferArea().ftX1out.duty[chanMaster + 1] = 0;
	
		    if (mDirection > 0) 
		    	chanMaster++;
	
		    getTransferArea().ftX1out.duty[chanMaster] = (short) duty;
		    getTransferArea().ftX1out.distance[mIdx]   = (short) pulseCnt;
		    getTransferArea().ftX1out.motor_ex_cmd_id[mIdx]++;
	
		    getTransferArea().ftX1in.motor_ex_reached[mIdx] = false;
	
		   
		    for(int i = 0 ; i <  getTransferArea().ftX1out.master.length; i++){
		    	getTransferArea().ftX1out.master[i] = 0;
		    }
		    
		    if (sIdx != 255) {
	
		        //  motor sync run with slave
		        int chanSlave = sIdx * 2;
		        getTransferArea().ftX1out.duty[chanSlave]     = 0;
		        getTransferArea().ftX1out.duty[chanSlave + 1] = 0;
	
		        if (sDirection > 0) 
		        	chanSlave++;
	
		        getTransferArea().ftX1out.duty[chanSlave] = (short) duty;
		        getTransferArea().ftX1out.distance[sIdx]  = (short) pulseCnt;
		        getTransferArea().ftX1out.master[sIdx]    = (byte) (mIdx + 1);
		        getTransferArea().ftX1out.motor_ex_cmd_id[sIdx]++;
	
		        getTransferArea().ftX1in.motor_ex_reached[sIdx] = false;
		    }
		    ApiEntry.getInstance().connection.semaTransferArea.release();
	    }catch(Exception e){
	    	return 1;
	    }
	    return 0;
	}

	public int StopAllMotorExCmd() {
		try{
			ApiEntry.getInstance().connection.semaTransferArea.acquire();
			
			for(int i = 0; i< getTransferArea().ftX1out.master.length; i++){
				getTransferArea().ftX1out.master[i] = 0;
			}
			for(int i = 0; i< getTransferArea().ftX1out.duty.length; i++){
				getTransferArea().ftX1out.duty[i] = 0;
			}
			for(int i = 0; i< getTransferArea().ftX1out.distance.length; i++){
				getTransferArea().ftX1out.distance[i] = 0;
			}
			
			ApiEntry.getInstance().connection.semaTransferArea.release();
		}catch(Exception e){
			return 1;
		}
		
		return 0;
	}

	public int StopMotorExCmd(int mtrIdx) {
		try{
			ApiEntry.getInstance().connection.semaTransferArea.acquire();
			
			getTransferArea().ftX1out.distance[mtrIdx]    = 0;
			getTransferArea().ftX1out.duty[mtrIdx*2]      = 0;
			getTransferArea().ftX1out.duty[mtrIdx*2+1]    = 0;
		    
			ApiEntry.getInstance().connection.semaTransferArea.release();
		}catch(Exception e){
			return 1;
		}
		return 0;
	}

	public int SetRoboTxMessage(String msg) {
		try{
			ApiEntry.getInstance().connection.semaTransferArea.acquire();
			
		    
			getTransferArea().ftX1display.display_msg.id = 0;
		    //  init display structure
			for(int i = 0 ; i< getTransferArea().ftX1display.display_msg.text.length;i++){
				getTransferArea().ftX1display.display_msg.text[i] = 0;
			}
		   
		    int msglen = msg.length();
		    if ( msglen > TXCShmem.DISPL_MSG_LEN_MAX )
		        msglen = TXCShmem.DISPL_MSG_LEN_MAX;

		    short[] shortMessage = new short[msglen];
		    for(int i = 0; i < msglen; i++){
		    	getTransferArea().ftX1display.display_msg.text[i] = msg.toCharArray()[i];
		    	shortMessage[i] = (short)msg.toCharArray()[i];
		    }

		    //  set request flag for message write
		    //ApiEntry.this.connection.semaStream.acquire();
			
		    
			TXCProtocol txcWrite = new TXCProtocol("MSG_WR_REQUEST", shortMessage);
			txcWrite.write(ApiEntry.this.connection.os);

			TXCProtocol txcRead = new TXCProtocol("", null);
			txcRead.read(ApiEntry.this.connection.is);
			
			//ApiEntry.this.connection.semaStream.release();
			
			ApiEntry.getInstance().connection.semaTransferArea.release();
		}catch(Exception e){
			return 1;
		}
		return 0;
	}

	// set fish.X1 config structure
	public int SetFtUniConfig(int ioId, int mode, boolean digital) {
		try{
			ApiEntry.getInstance().connection.semaTransferArea.acquire();
			
			if ( ioId < 0 || ioId >= TXCShmem.IZ_UNI_INPUT )
		        return 1;

		    

		    //Log(LOGPRINT,"SetFishX1ConfigUni, idx= %d, mode= %d, digital= %d", idx, mode, digital);

		    //  set mode (U/R), analog/digital for uni io's in config structure
		    getTransferArea().ftX1config.uni[ioId].mode = (byte) mode;
		    getTransferArea().ftX1config.uni[ioId].digital = (boolean) digital;

		    //  clean uni value in order not to get overrun at the very first moment after mode change
		    getTransferArea().ftX1in.uni[ioId] = 0;

		    //  config change, increment config_id
		    getTransferArea().ftX1state.config_id += 1;
		    
			ApiEntry.getInstance().connection.semaTransferArea.release();
		}catch(Exception e){
			return 1;
		}
		return 0;
	}

	public int SetFtCntConfig(int cntId, int mode) {
		try{
			ApiEntry.getInstance().connection.semaTransferArea.acquire();
			
			if ( cntId < 0 || cntId >= TXCShmem.IZ_COUNTER )
		        return 1;

		    //  set mode (U/R) for counter in config structure
		    getTransferArea().ftX1config.cnt[cntId].mode = (byte) mode;

		    //  config change, increment config_id
		    getTransferArea().ftX1state.config_id += 1;
		    
			ApiEntry.getInstance().connection.semaTransferArea.release();
		}catch(Exception e){
			return 1;
		}
		return 0;
	}

	public int SetFtMotorConfig(int motorId, boolean status) {
		try{
			ApiEntry.getInstance().connection.semaTransferArea.acquire();
			
			if ( motorId < 0 || motorId >= TXCShmem.IZ_MOTOR ) {
		        return 1;
		    }

		    //  set motor active
		    getTransferArea().ftX1config.motor[motorId] = (boolean) status;

		    //  config change, increment config_id
		    getTransferArea().ftX1state.config_id += 1;
		    
			ApiEntry.getInstance().connection.semaTransferArea.release();
		}catch(Exception e){
			return 1;
		}
		return 0;
	}

	// get values from fish.X1 TransferArea, input structure
	public int GetInIOValue(int ioId) {
		try{
			ApiEntry.getInstance().connection.semaTransferArea.acquire();
			
			if ( ioId < 0 || ioId >= TXCShmem.IZ_UNI_INPUT )
		        return 0;


		    //  get universal IO value from input structure
		    int ioVal = getTransferArea().ftX1in.uni[ioId];

		   

		    
			ApiEntry.getInstance().connection.semaTransferArea.release();
			
			return ioVal;
		}catch(Exception e){
			return 0;
		}
		
	}

	public int GetInIOOverrun(int ioId) {
		try{
			ApiEntry.getInstance().connection.semaTransferArea.acquire();
			int overrun;
			
			switch(getTransferArea().ftX1config.uni[ioId].mode) {
	        case TXCShmem.MODE_R      : 
	        	if(getTransferArea().ftX1in.uni[ioId] == TXCShmem.R_OVR); 
	        	overrun = 1;
	        	break;
	        case TXCShmem.MODE_R2     : 
	        	if(getTransferArea().ftX1in.uni[ioId] == TXCShmem.R2_OVR);  
	        	overrun = 1;
	        	break;
	        case TXCShmem.MODE_U      : 
	        	if(getTransferArea().ftX1in.uni[ioId] == TXCShmem.U_OVR); 
	        	overrun = 1;
	        	break;
	        case TXCShmem.MODE_ULTRASONIC : 
	        	if(getTransferArea().ftX1in.uni[ioId] == TXCShmem.ULTRASONIC_OVR);
	        	overrun = 1;
	        	break;
	        default          : overrun = 0;
	    }	
			
			ApiEntry.getInstance().connection.semaTransferArea.release();
			
			return overrun;
		}catch(Exception e){
			return 0;
		}

	}

	public int GetInCounterValue(int cntId) {
		try{
			ApiEntry.getInstance().connection.semaTransferArea.acquire();
			
			if ( cntId < 0 || cntId >= TXCShmem.IZ_COUNTER )
		        return 0;

		    //  get counter values from input structure
		    
		    int ftValue = getTransferArea().ftX1in.cnt_in[cntId];

		    
		    
			ApiEntry.getInstance().connection.semaTransferArea.release();
			
			return ftValue;
			
		}catch(Exception e){
			return 1;
		}
	}

	public int GetInCounterState(int cntId) {
		try{
			ApiEntry.getInstance().connection.semaTransferArea.acquire();
			

			if ( cntId < 0 || cntId >= TXCShmem.IZ_COUNTER )
		        return 0;

			int count   = getTransferArea().ftX1in.counter[cntId];
			
			
			ApiEntry.getInstance().connection.semaTransferArea.release();
			return count;
		}catch(Exception e){
			return 1;
		}
	}

	public int GetInDisplayButtonValueLeft() {
		try{
			ApiEntry.getInstance().connection.semaTransferArea.acquire();
			
			int left = getTransferArea().ftX1in.display_button_left;
		    
			ApiEntry.getInstance().connection.semaTransferArea.release();
			return left;
		}catch(Exception e){
			return 1;
		}
	}

	public int GetInDisplayButtonValueRight() {
		try{
			ApiEntry.getInstance().connection.semaTransferArea.acquire();
			
		    int right = getTransferArea().ftX1in.display_button_right;
		    
			ApiEntry.getInstance().connection.semaTransferArea.release();
			return right;
		}catch(Exception e){
			return 1;
		}
	}

}
