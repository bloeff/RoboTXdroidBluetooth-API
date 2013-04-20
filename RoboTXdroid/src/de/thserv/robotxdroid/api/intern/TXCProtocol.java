package de.thserv.robotxdroid.api.intern;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import android.util.Log;

import de.thserv.robotxdroid.api.ApiEntry;

public class TXCProtocol {
	boolean boolValid;
	
	private int intFrom;
	private int intTo;
	private short shortTicket;
	private int intCommand; // bei read() REQUEST und bei write() REPLY
	private int intItems;
	short[] shortData;
	
	static short shortSession = 0;
	
	//Baut Telegrammrahmen auf
	public TXCProtocol(String strType, short[] shortContent) {
		
		if(!ApiEntry.getInstance().isOnlineMode()){
			if(shortContent != null){
				this.shortData = new short[shortContent.length];
				for (int i = 0; i < shortContent.length; i++) {
					this.shortData[i] = shortContent[i];
				}
				
				if (strType.equals("ECHO_REQUEST") == true) {
					this.intCommand = 1; // ECHO_REQUEST
				} else if (strType.equals("MSG_WR_REQUEST") == true) {
					this.intCommand = 8; // MSG_WR_REQUEST
				} else if (strType.equals("REM_IO_REQUEST") == true) {
					this.intCommand = 2; // REM_IO_REQUEST
				}else if (strType.equals("REM_CONFIG_WR_REQUEST") == true) {
					this.intCommand = 5; // REM_CONFIG_WR_REQUEST
				}
				
			}
		}else if (strType.equals("ECHO_REQUEST") == true) {
			this.intFrom = 2; // BUS_ADR_MASTER
			this.intTo = 1; // BUS_ADR_WILDCARD
			this.shortTicket = 1;
			this.intCommand = 1; // ECHO_REQUEST
			this.intItems = 1; // ShmIds = Anzahl der Nachrichtenblöcke

			{ // ECHO
				this.shortData = new short[8];

				int intDataPosition = 0;
				
				int intIdent = 0; // ShmIfId = TA_LOCAL
				this.shortData[intDataPosition + 0] = (short) ((intIdent >> 0) & 0x00FF);
				this.shortData[intDataPosition + 1] = (short) ((intIdent >> 8) & 0x00FF);
				this.shortData[intDataPosition + 2] = (short) ((intIdent >> 16) & 0x00FF);
				this.shortData[intDataPosition + 3] = (short) ((intIdent >> 24) & 0x00FF);
				intDataPosition += 4;
		
				{ // ECHO
					for (int i = 0; i < shortContent.length; i++) {
						this.shortData[intDataPosition] = shortContent[i];
						intDataPosition += 1;
					}
				} // ECHO
			} // ECHO
			
		} else if (strType.equals("MSG_WR_REQUEST") == true) {
			this.intFrom = 2; // BUS_ADR_MASTER
			this.intTo = 1; // BUS_ADR_WILDCARD
			this.shortTicket = 1;
			this.intCommand = 8; // MSG_WR_REQUEST
			this.intItems = 1; // ShmIds = Anzahl der Nachrichtenblöcke

			{ // FTX1_MSG_WR_REQ
				this.shortData = new short[104];

				int intDataPosition = 0;
				
				int intIdent = 0; // ShmIfId = TA_LOCAL
				this.shortData[intDataPosition + 0] = (short) ((intIdent >> 0) & 0x00FF);
				this.shortData[intDataPosition + 1] = (short) ((intIdent >> 8) & 0x00FF);
				this.shortData[intDataPosition + 2] = (short) ((intIdent >> 16) & 0x00FF);
				this.shortData[intDataPosition + 3] = (short) ((intIdent >> 24) & 0x00FF);
				intDataPosition += 4;
		
				{ // FTX1_MSG
					this.shortData[intDataPosition + 0] = 1; // id
					intDataPosition += 1;
		
					for (int i = 0; i < shortContent.length; i++) {
						this.shortData[intDataPosition] = shortContent[i];
						intDataPosition += 1;
					}
				} // FTX1_MSG
			} // FTX1_MSG_WR_REQ
			
		} else if (strType.equals("REM_IO_REQUEST") == true) {
			this.intFrom = 2; // BUS_ADR_MASTER
			this.intTo = 1; // BUS_ADR_WILDCARD
			this.shortTicket = 1;
			this.intCommand = 2; // REM_IO_REQUEST
			this.intItems = 1; // ShmIds = Anzahl der Nachrichtenblöcke
			
			{ // FTX1_REM_IO_REQ
				this.shortData = new short[48];

				int intDataPosition = 0;
				
				int intIdent = 0; // ShmIfId = TA_LOCAL
				this.shortData[intDataPosition + 0] = (short) ((intIdent >> 0) & 0x00FF);
				this.shortData[intDataPosition + 1] = (short) ((intIdent >> 8) & 0x00FF);
				this.shortData[intDataPosition + 2] = (short) ((intIdent >> 16) & 0x00FF);
				this.shortData[intDataPosition + 3] = (short) ((intIdent >> 24) & 0x00FF);
				intDataPosition += 4;

				{ // FTX1_OUTPUT
					for (int i = 0; i < shortContent.length; i++) {
						this.shortData[intDataPosition] = shortContent[i];
						intDataPosition += 1;
					}
				} // FTX1_OUTPUT
			} // FTX1_REM_IO_REQ
		}else if (strType.equals("REM_CONFIG_WR_REQUEST") == true) {
			this.intFrom = 2; // BUS_ADR_MASTER
			this.intTo = 1; // BUS_ADR_WILDCARD
			this.shortTicket = 1;
			this.intCommand = 5; // REM_CONFIG_WR_REQUEST
			this.intItems = 1; // ShmIds = Anzahl der Nachrichtenblöcke
			
			{ // REM_CONFIG_WR_REQUEST
				this.shortData = new short[52];
				
				int intDataPosition = 0;
				
				int intIdent = 0; // ShmIfId = TA_LOCAL
				this.shortData[intDataPosition + 0] = (short) ((intIdent >> 0) & 0x00FF);
				this.shortData[intDataPosition + 1] = (short) ((intIdent >> 8) & 0x00FF);
				this.shortData[intDataPosition + 2] = (short) ((intIdent >> 16) & 0x00FF);
				this.shortData[intDataPosition + 3] = (short) ((intIdent >> 24) & 0x00FF);
				intDataPosition += 4;

				{ // FTX1_CONFIG_COMPACT
					for (int i = 0; i < shortContent.length; i++) {
						this.shortData[intDataPosition] = shortContent[i];
						intDataPosition += 1;
					}
				} // FTX1_CONFIG_COMPACT
			} // REM_CONFIG_WR_REQUEST
		}
	}
	
	// Folgende Methoden sind zum Schreiben und zum Lesen vom und auf den Out- und Inputstream
	public void write(OutputStream os){
		if(ApiEntry.getInstance().isOnlineMode())
			writeOld(os);
		else
			writeNew(os);
	}
	
	public void read(InputStream is){
		if(ApiEntry.getInstance().isOnlineMode())
			readOld(is);
		else
			readNew(is);
	}
	
	private void writeNew(OutputStream os){
		try {
            byte[] msg = new byte[this.shortData.length + 5];
            msg[0] = (byte) 0xBE;
            msg[1] = (byte) 0xEF;
            msg[2] = (byte) (this.shortData.length +1) ;
            msg[3] = (byte) ((this.intCommand >> 0) & 0xFF);
            
			for (int i = 0; i < this.shortData.length; i++) {
				if (this.shortData[i] < 128) {
					msg[4+i] = (byte) (this.shortData[i] - 0);
				} else if (this.shortData[i] < 256) {
					msg[4+i] = (byte) (this.shortData[i] - 256);
				}
			}
            
            byte chk = 0;
            for(int i = 0; i< msg.length -1;i++){
            	chk = (byte) ((chk + msg[i]) & 0xFF);
            }
            msg[msg.length-1] = chk;
            
            os.write(msg);
			this.boolValid = true;
		} catch (IOException e) {
			this.boolValid = false;
		}
	}
	
 	private void writeOld(OutputStream os) {
		try {
			int intProtocol = 0;
			
			intProtocol += 1; // STX_BYTE
			intProtocol += 1; // CHR_MAGIC
			intProtocol += 2; // shortLength
			intProtocol += 4; // intFrom
			intProtocol += 4; // intTo
			intProtocol += 2; // shortTicket
			intProtocol += 2; // shortSession
			intProtocol += 4; // intCommand
			intProtocol += 4; // intItems
			intProtocol += this.shortData.length; // byteData
			intProtocol += 2; // shortChecksum
			intProtocol += 1; // ETX_BYTE

			short[] shortProtocol = new short[intProtocol];
			byte[] byteProtocol = new byte[shortProtocol.length];

			int intProtocolPosition = 0;
			
			shortProtocol[intProtocolPosition + 0] = 0x02; // STX_BYTE
			intProtocolPosition += 1;

			shortProtocol[intProtocolPosition + 0] = 0x55; // CHR_MAGIC
			intProtocolPosition += 1;

			short shortLength = (short) (20 + this.shortData.length); // HEADER + DATALENGTH
			shortProtocol[intProtocolPosition + 0] = (short) ((shortLength >> 8) & 0x00FF);
			shortProtocol[intProtocolPosition + 1] = (short) ((shortLength >> 0) & 0x00FF);
			intProtocolPosition += 2;

			shortProtocol[intProtocolPosition + 0] = (short) ((this.intFrom >> 0) & 0x00FF);
			shortProtocol[intProtocolPosition + 1] = (short) ((this.intFrom >> 8) & 0x00FF);
			shortProtocol[intProtocolPosition + 2] = (short) ((this.intFrom >> 16) & 0x00FF);
			shortProtocol[intProtocolPosition + 3] = (short) ((this.intFrom >> 24) & 0x00FF);
			intProtocolPosition += 4;

			shortProtocol[intProtocolPosition + 0] = (short) ((this.intTo >> 0) & 0x00FF);
			shortProtocol[intProtocolPosition + 1] = (short) ((this.intTo >> 8) & 0x00FF);
			shortProtocol[intProtocolPosition + 2] = (short) ((this.intTo >> 16) & 0x00FF);
			shortProtocol[intProtocolPosition + 3] = (short) ((this.intTo >> 24) & 0x00FF);
			intProtocolPosition += 4;

			shortProtocol[intProtocolPosition + 0] = (short) ((this.shortTicket >> 0) & 0x00FF);
			shortProtocol[intProtocolPosition + 1] = (short) ((this.shortTicket >> 8) & 0x00FF);
			intProtocolPosition += 2;

			shortProtocol[intProtocolPosition + 0] = (short) ((TXCProtocol.shortSession >> 0) & 0x00FF);
			shortProtocol[intProtocolPosition + 1] = (short) ((TXCProtocol.shortSession >> 8) & 0x00FF);
			intProtocolPosition += 2;

			shortProtocol[intProtocolPosition + 0] = (short) ((this.intCommand >> 0) & 0x00FF);
			shortProtocol[intProtocolPosition + 1] = (short) ((this.intCommand >> 8) & 0x00FF);
			shortProtocol[intProtocolPosition + 2] = (short) ((this.intCommand >> 16) & 0x00FF);
			shortProtocol[intProtocolPosition + 3] = (short) ((this.intCommand >> 24) & 0x00FF);
			intProtocolPosition += 4;

			shortProtocol[intProtocolPosition + 0] = (short) ((this.intItems >> 0) & 0x00FF);
			shortProtocol[intProtocolPosition + 1] = (short) ((this.intItems >> 8) & 0x00FF);
			shortProtocol[intProtocolPosition + 2] = (short) ((this.intItems >> 16) & 0x00FF);
			shortProtocol[intProtocolPosition + 3] = (short) ((this.intItems >> 24) & 0x00FF);
			intProtocolPosition += 4;

			for (int i = 0; i < this.shortData.length; i += 1) {
				shortProtocol[intProtocolPosition] = (short) this.shortData[i];
				intProtocolPosition += 1;
			}

			{
				short shortChecksum = 0;

				{
					for (int i = 2; i < intProtocolPosition; i++) {
						shortChecksum += shortProtocol[i];
					}

					shortChecksum = (short) ((~shortChecksum) + 1);
				}

				shortProtocol[intProtocolPosition + 0] = (short) ((shortChecksum >> 8) & 0x00FF);
				shortProtocol[intProtocolPosition + 1] = (short) ((shortChecksum >> 0) & 0x00FF);
				intProtocolPosition += 2;
			}

			shortProtocol[intProtocolPosition + 0] = 0x03; // ETX_BYTE
			intProtocolPosition += 1;

			for (int i = 0; i < shortProtocol.length; i++) {
				if (shortProtocol[i] < 128) {
					byteProtocol[i] = (byte) (shortProtocol[i] - 0);
					
				} else if (shortProtocol[i] < 256) {
					byteProtocol[i] = (byte) (shortProtocol[i] - 256);
					
				}
			}
			
			os.write(byteProtocol);
			
			this.boolValid = true;
		} catch (IOException e) {
			this.boolValid = false;
		}
	}
	
	private void readNew(InputStream is){
		try {
			{
				//if(!getConnAck(is))
				//	throw new Exception();
				
				byte ChecksumCalc = 0;
				short shortLength = -1;
	            
		            
		            {//Magic Token
						byte[] byteProtocol = new byte[2];
						short[] shortProtocol = new short[byteProtocol.length];
						
						int intRead = is.read(byteProtocol);
						
						if (intRead != byteProtocol.length) {
							throw new Exception();
						}
			
						for (int i = 0; i < byteProtocol.length; i++) {
							if (byteProtocol[i] < 0) {
								shortProtocol[i] = (short) (byteProtocol[i] + 256);
							} else if (byteProtocol[i] < 128) {
								shortProtocol[i] = (short) (byteProtocol[i] + 0);
							}
						}
						
						for (int i = 0; i < shortProtocol.length; i++) {
							ChecksumCalc = (byte) ((ChecksumCalc + shortProtocol[i]) & 0xFF);
						}
		            }
					
					
					{//Length
						byte[] byteProtocol = new byte[1];
						short[] shortProtocol = new short[byteProtocol.length];
						
						int intRead = is.read(byteProtocol);
						
						if (intRead != byteProtocol.length) {
							throw new Exception();
						}

						for (int i = 0; i < byteProtocol.length; i++) {
							if (byteProtocol[i] < 0) {
								shortProtocol[i] = (short) (byteProtocol[i] + 256);
								
							} else if (byteProtocol[i] < 128) {
								shortProtocol[i] = (short) (byteProtocol[i] + 0);
								
							}
						}

						for (int i = 0; i < shortProtocol.length; i++) {
							ChecksumCalc = (byte) ((ChecksumCalc + shortProtocol[i]) & 0xFF);
						}
			
						shortLength = (short) ((shortProtocol[0] << 0));
					}
		            
					if (shortLength < 0) {
						throw new Exception();
					}
					
					
					
					{// Data
						byte[] byteProtocol = new byte[shortLength];
						short[] shortProtocol = new short[byteProtocol.length];
						
						int intRead = 0;
						
						do {
							if (byteProtocol.length == intRead) {
								break;
							}
							
							int intPart = is.read(byteProtocol, intRead, byteProtocol.length - intRead);
							
							if (intPart == 0) {
								break;
								
							} else if (intPart == -1) {
								break;
								
							}
							
							intRead += intPart;
						} while (true);
						
						if (intRead != byteProtocol.length) {
							throw new Exception();
						}

						for (int i = 0; i < byteProtocol.length; i++) {
							if (byteProtocol[i] < 0) {
								shortProtocol[i] = (short) (byteProtocol[i] + 256);
								
							} else if (byteProtocol[i] < 128) {
								shortProtocol[i] = (short) (byteProtocol[i] + 0);
								
							}
						}
						
						for (int i = 0; i < shortProtocol.length; i++) {
							ChecksumCalc = (byte) ((ChecksumCalc + shortProtocol[i]) & 0xFF);
						}
						
						this.shortData = new short[shortLength-1];
						
						this.intCommand = shortProtocol[0];
						
						for (int i = 0; i < this.shortData.length; i += 1) {
							this.shortData[i] = shortProtocol[i+1];
						}
						
					}
					
					//shortChecksumCalc = (short) ((~shortChecksumCalc) + 1);
		            
					{//Checksum
						byte[] byteProtocol = new byte[1];
						short[] shortProtocol = new short[byteProtocol.length];
						
						int intRead = is.read(byteProtocol);
						
						if (intRead != byteProtocol.length) {
							throw new Exception();
						}

						for (int i = 0; i < byteProtocol.length; i++) {
							if (byteProtocol[i] < 0) {
								shortProtocol[i] = (short) (byteProtocol[i] + 256);
								
							} else if (byteProtocol[i] < 128) {
								shortProtocol[i] = (short) (byteProtocol[i] + 0);
								
							}
						}
						
						short shortChecksum = (short)  (shortProtocol[0] << 0);
						
						if (shortChecksum != ChecksumCalc) {
							//throw new Exception();
						}
					}
				}
			
			
			this.boolValid = true;
		} catch (Exception e) {
			this.boolValid = false;
		}
		
	}
	
	
	// Auslesen ob nach der Verbindungsherstellung ein CONN_ACH erfolgte
	public boolean getConnAck(InputStream is){
		byte ChecksumCalc = 0;
		short shortLength = -1;
        try{
        	
        
        {//Magic Token
			byte[] byteProtocol = new byte[2];
			short[] shortProtocol = new short[byteProtocol.length];
			
			int intRead = is.read(byteProtocol);
			
			if (intRead != byteProtocol.length) {
				throw new Exception();
			}

			for (int i = 0; i < byteProtocol.length; i++) {
				if (byteProtocol[i] < 0) {
					shortProtocol[i] = (short) (byteProtocol[i] + 256);
				} else if (byteProtocol[i] < 128) {
					shortProtocol[i] = (short) (byteProtocol[i] + 0);
				}
			}
			
			for (int i = 0; i < shortProtocol.length; i++) {
				ChecksumCalc = (byte) ((ChecksumCalc + shortProtocol[i]) & 0xFF);
			}
        }
		
		
		{//Length
			byte[] byteProtocol = new byte[1];
			short[] shortProtocol = new short[byteProtocol.length];
			
			int intRead = is.read(byteProtocol);
			
			if (intRead != byteProtocol.length) {
				throw new Exception();
			}

			for (int i = 0; i < byteProtocol.length; i++) {
				if (byteProtocol[i] < 0) {
					shortProtocol[i] = (short) (byteProtocol[i] + 256);
					
				} else if (byteProtocol[i] < 128) {
					shortProtocol[i] = (short) (byteProtocol[i] + 0);
					
				}
			}

			for (int i = 0; i < shortProtocol.length; i++) {
				ChecksumCalc = (byte) ((ChecksumCalc + shortProtocol[i]) & 0xFF);
			}

			shortLength = (short) ((shortProtocol[0] << 0));
		}
        
		if (shortLength < 0) {
			throw new Exception();
		}
		
		{// Data
			byte[] byteProtocol = new byte[shortLength];
			short[] shortProtocol = new short[byteProtocol.length];
			
			int intRead = 0;
			
			do {
				if (byteProtocol.length == intRead) {
					break;
				}
				
				int intPart = is.read(byteProtocol, intRead, byteProtocol.length - intRead);
				
				if (intPart == 0) {
					break;
					
				} else if (intPart == -1) {
					break;
					
				}
				
				intRead += intPart;
			} while (true);
			
			if (intRead != byteProtocol.length) {
				throw new Exception();
			}

			for (int i = 0; i < byteProtocol.length; i++) {
				if (byteProtocol[i] < 0) {
					shortProtocol[i] = (short) (byteProtocol[i] + 256);
					
				} else if (byteProtocol[i] < 128) {
					shortProtocol[i] = (short) (byteProtocol[i] + 0);
					
				}
			}
			
			for (int i = 0; i < shortProtocol.length; i++) {
				ChecksumCalc = (byte) ((ChecksumCalc + shortProtocol[i]) & 0xFF);
			}
			
			this.shortData = new short[shortLength];
			
			for (int i = 0; i < this.shortData.length; i += 1) {
				this.shortData[i] = shortProtocol[i];
			}
			
		}
		
		//ChecksumCalc = (char) ((~ChecksumCalc) + 1);
        
		{//Checksum
			byte[] byteProtocol = new byte[1];
			short[] shortProtocol = new short[byteProtocol.length];
			
			int intRead = is.read(byteProtocol);
			
			if (intRead != byteProtocol.length) {
				throw new Exception();
			}

			for (int i = 0; i < byteProtocol.length; i++) {
				if (byteProtocol[i] < 0) {
					shortProtocol[i] = (short) (byteProtocol[i] + 256);
					
				} else if (byteProtocol[i] < 128) {
					shortProtocol[i] = (short) (byteProtocol[i] + 0);
					
				}
			}
			
			short shortChecksum = (short)  (shortProtocol[0] << 0);
			
			if (shortChecksum != ChecksumCalc) {
				Log.d("Error", "Checksum "+shortChecksum +" --- "+ChecksumCalc);
				//throw new Exception();
			}
		}
        }catch(Exception e){
        	return false;
        }
		short[] connAck = {0x43, 0x4F, 0x4E, 0x4E, 0x5F, 0x41, 0x43, 0x4B};//CONN_ACK
		
		
		if (Arrays.equals(connAck, this.shortData) == true)
			return true;
		return false;
	}
 
	private void readOld(InputStream is) {
		try {
			{
				byte[] byteProtocol = new byte[2];
				short[] shortProtocol = new short[byteProtocol.length];
				
				int intRead = is.read(byteProtocol);
				
				if (intRead != byteProtocol.length) {
					throw new Exception();
				}

				for (int i = 0; i < byteProtocol.length; i++) {
					if (byteProtocol[i] < 0) {
						shortProtocol[i] = (short) (byteProtocol[i] + 256);
					} else if (byteProtocol[i] < 128) {
						shortProtocol[i] = (short) (byteProtocol[i] + 0);
					}
				}
				
				if (shortProtocol[0] != 0x02) { // STX_BYTE
					throw new Exception("STX_Byte stimmt nicht: "+shortProtocol[0]);
					
				} else if (shortProtocol[1] != 0x55) { // CHR_MAGIC
					throw new Exception();
					
				}
			}

			short shortChecksumCalc = 0;
			short shortLength = -1;
			
			{
				byte[] byteProtocol = new byte[2];
				short[] shortProtocol = new short[byteProtocol.length];
				
				int intRead = is.read(byteProtocol);
				
				if (intRead != byteProtocol.length) {
					throw new Exception();
				}

				for (int i = 0; i < byteProtocol.length; i++) {
					if (byteProtocol[i] < 0) {
						shortProtocol[i] = (short) (byteProtocol[i] + 256);
						
					} else if (byteProtocol[i] < 128) {
						shortProtocol[i] = (short) (byteProtocol[i] + 0);
						
					}
				}

				for (int i = 0; i < shortProtocol.length; i++) {
					shortChecksumCalc += shortProtocol[i];
				}
	
				shortLength = (short) ((shortProtocol[0] << 8) + (shortProtocol[1] << 0));
			}
			
			if (shortLength < 0) {
				throw new Exception();
			}
			
			{
				byte[] byteProtocol = new byte[shortLength];
				short[] shortProtocol = new short[byteProtocol.length];
				
				int intRead = 0;
				
				do {
					if (byteProtocol.length == intRead) {
						break;
					}
					
					int intPart = is.read(byteProtocol, intRead, byteProtocol.length - intRead);
					
					if (intPart == 0) {
						break;
						
					} else if (intPart == -1) {
						break;
						
					}
					
					intRead += intPart;
				} while (true);
				
				if (intRead != byteProtocol.length) {
					throw new Exception();
				}

				for (int i = 0; i < byteProtocol.length; i++) {
					if (byteProtocol[i] < 0) {
						shortProtocol[i] = (short) (byteProtocol[i] + 256);
						
					} else if (byteProtocol[i] < 128) {
						shortProtocol[i] = (short) (byteProtocol[i] + 0);
						
					}
				}
				
				for (int i = 0; i < shortProtocol.length; i++) {
					shortChecksumCalc += shortProtocol[i];
				}
				
				int intProtocolPosition = 0;
				
				this.intFrom = (int) ((shortProtocol[intProtocolPosition + 0] << 0) + (shortProtocol[intProtocolPosition + 1] << 8) + (shortProtocol[intProtocolPosition + 2] << 16) + (shortProtocol[intProtocolPosition + 3] << 24));
				intProtocolPosition += 4;
				
				this.intTo = (int) ((shortProtocol[intProtocolPosition + 0] << 0) + (shortProtocol[intProtocolPosition + 1] << 8) + (shortProtocol[intProtocolPosition + 2] << 16) + (shortProtocol[intProtocolPosition + 3] << 24));
				intProtocolPosition += 4;
				
				this.shortTicket = (short) ((shortProtocol[intProtocolPosition + 0] << 0) + (shortProtocol[intProtocolPosition + 1] << 8));
				intProtocolPosition += 2;
				
				TXCProtocol.shortSession = (short) ((shortProtocol[intProtocolPosition + 0] << 0) + (shortProtocol[intProtocolPosition + 1] << 8));
				intProtocolPosition += 2;
				
				this.intCommand = (int) ((shortProtocol[intProtocolPosition + 0] << 0) + (shortProtocol[intProtocolPosition + 1] << 8) + (shortProtocol[intProtocolPosition + 2] << 16) + (shortProtocol[intProtocolPosition + 3] << 24));
				intProtocolPosition += 4;
				
				this.intItems = (int) ((shortProtocol[intProtocolPosition + 0] << 0) + (shortProtocol[intProtocolPosition + 1] << 8) + (shortProtocol[intProtocolPosition + 2] << 16) + (shortProtocol[intProtocolPosition + 3] << 24));
				intProtocolPosition += 4;
				
				this.shortData = new short[shortLength - intProtocolPosition];
				
				for (int i = 0; i < this.shortData.length; i += 1) {
					this.shortData[i] = shortProtocol[intProtocolPosition];
					intProtocolPosition += 1;
				}
			}
			
			shortChecksumCalc = (short) ((~shortChecksumCalc) + 1);
			
			{
				byte[] byteProtocol = new byte[2];
				short[] shortProtocol = new short[byteProtocol.length];
				
				int intRead = is.read(byteProtocol);
				
				if (intRead != byteProtocol.length) {
					throw new Exception();
				}

				for (int i = 0; i < byteProtocol.length; i++) {
					if (byteProtocol[i] < 0) {
						shortProtocol[i] = (short) (byteProtocol[i] + 256);
						
					} else if (byteProtocol[i] < 128) {
						shortProtocol[i] = (short) (byteProtocol[i] + 0);
						
					}
				}
				
				short shortChecksum = (short) ((shortProtocol[0] << 8) + (shortProtocol[1] << 0));
				
				if (shortChecksum != shortChecksumCalc) {
					throw new Exception();
				}
			}
			
			{
				byte[] byteProtocol = new byte[1];
				short[] shortProtocol = new short[byteProtocol.length];
				
				int intRead = is.read(byteProtocol);
				
				if (intRead != byteProtocol.length) {
					throw new Exception();
				}

				for (int i = 0; i < byteProtocol.length; i++) {
					if (byteProtocol[i] < 0) {
						shortProtocol[i] = (short) (byteProtocol[i] + 256);
						
					} else if (byteProtocol[i] < 128) {
						shortProtocol[i] = (short) (byteProtocol[i] + 0);
						
					}
				}
				
				if (shortProtocol[0] != 0x03) { // ETX_BYTE
					throw new Exception();
				}
			}
			
			this.boolValid = true;
		} catch (Exception e) {
			this.boolValid = false;
			e.printStackTrace();
		}
	}
}
