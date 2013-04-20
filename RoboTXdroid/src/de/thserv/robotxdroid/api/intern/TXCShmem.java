package de.thserv.robotxdroid.api.intern;

// Nachbildung der TransferArea

public class TXCShmem {

	public static final int FIRMWARE_VER         = 0x011E;      // firmware version is 1.30
	
	public static final int IZ_COUNTER          = 4;           // number of counter
	public static final int IZ_PWM_CHAN         = 8;           // number of pwm channels
	public static final int IZ_MOTOR            = 4;           // number of motor
	public static final int IZ_UNI_INPUT        = 8;           // number of universal ios
	
	
	//5 kOhm Range
	public static final int R_MIN               = 10;          // [Ohm]
	public static final int R_MAX               = 4999;        // [Ohm]
	public static final int R_OVR               = 5000;        // [Ohm]
	
	//15 kOhm Range
	public static final int R2_MIN              = 30;          // [Ohm]
	public static final int R2_MAX              = 14999;       // [Ohm]
	public static final int R2_OVR              = 15000;       // [Ohm]
	
	//10V Range
	public static final int U_MAX               = 9999;        // [mV]
	public static final int U_OVR               = 10000;       // [mV]
	
	//Ultrasonic Sensor Range
	public static final int ULTRASONIC_MIN      = 2;           // [cm]
	public static final int ULTRASONIC_MAX      = 1023;        // [cm]
	public static final int ULTRASONIC_OVR      = 1024;        // [cm]
	public static final int NO_ULTRASONIC       = 4096;        // not present
	
	
	
	
	public static final int MODE_U				= 0;
	public static final int MODE_R				= 1;
	public static final int MODE_R2				= 2;
	public static final int MODE_ULTRASONIC		= 3;
	public static final int MODE_INVALID		= 4;
	
	//Fish.X1 Bus Address Identifier
	//[fish_x1_bus_adr_e]
	enum IFBusAdr
	{
		BUS_ADR_INVALID,
		BUS_ADR_WILDCARD,
		BUS_ADR_MASTER,
		BUS_ADR_SLAVE1,
		BUS_ADR_SLAVE2,
		BUS_ADR_SLAVE3,
		BUS_ADR_SLAVE4,
		BUS_ADR_SLAVE5,
		BUS_ADR_SLAVE6,
		BUS_ADR_SLAVE7,
		BUS_ADR_SLAVE8
	};


	//mult-interface-mode status (slave)
	public static final int SLAVE_OFFLINE       = 0;
	public static final int SLAVE_ONLINE        = 1;
	
	//length of strings
	public static final int HOSTNAME_LEN        = 16;          // "ROBO-IF-xxxxxxxx"
	public static final int BLUETOOTH_ADR_LEN   = 17;          // "xx:xx:xx:xx:xx:xx"
	public static final int DISPL_MSG_LEN_MAX   = 98;
	public static final int VERSION_STRLEN      = 15;
	
	public static final int INVALID_VALUE       = -32768;


	
	//define IF role (Application - I/O), (Local or Slave)
	//[shm_if_id_e]
	enum ShmIfId
	{
		LOCAL_IO,               // Local I/O
		REMOTE_IO_1,            // Remote I/O Slave #1
		REMOTE_IO_2,            // Remote I/O Slave #2
		REMOTE_IO_3,            // Remote I/O Slave #3
		REMOTE_IO_4,            // Remote I/O Slave #4
		REMOTE_IO_5,            // Remote I/O Slave #5
		REMOTE_IO_6,            // Remote I/O Slave #6
		REMOTE_IO_7,            // Remote I/O Slave #7
		REMOTE_IO_8,            // Remote I/O Slave #8
		SHM_IF_CNT;
	};

	//member of IF slaves
	public static final int SLAVE_CNT_MAX       = (ShmIfId.SHM_IF_CNT.ordinal()-1);  // only Slaves = 8
	public static final int IF08_MAX            = ShmIfId.SHM_IF_CNT.ordinal();      // Master + Slaves = 9


	//[inp_mode_e]
	enum InputMode
	{
		MODE_U,
		MODE_R,
		MODE_R2,
		MODE_ULTRASONIC,
		MODE_INVALID
	};


	//[pgm_state_e]
	enum PgmState
	{
		PGM_STATE_INVALID,
		PGM_STATE_RUN,
		PGM_STATE_STOP
	};


	//Timer units for GetSystemTime hook function
	enum TimerUnit
	{
		TIMER_UNIT_INVALID(0),
		TIMER_UNIT_SECONDS(2),
		TIMER_UNIT_MILLISECONDS(3),
		TIMER_UNIT_MICROSECONDS(4);
		
		private final int index;   
		
		TimerUnit(int index) {
		    this.index = index;
		}
		
		public int index() { 
		    return index; 
		}
	};


	//[pgm_info_s], 8 bytes
	public class pgm_info_s
	{
		public String          name;
		public byte           	state;        // enum PgmState    state;
		public char[]         	dummy = new char[3];
	}
	
	pgm_info_s PGM_INFO = new pgm_info_s();


	//[display_msg_s], 100 bytes
	public class display_msg_s
	{
		public byte           id;
		public char[]         text = new char[DISPL_MSG_LEN_MAX + 1];
	}

	display_msg_s FTX1_MSG = new display_msg_s();

	//structure for received message, 36 bytes
	public class shm_if_msgrec_s
	{
		public display_msg_s        display_msg = new display_msg_s();
	}

	shm_if_msgrec_s FTX1_MSGREC = new shm_if_msgrec_s();

	//[display_frame_s], 8 bytes
	public class display_frame_s
	{
		public byte        	frame;
		public short          	id;
		public boolean        	is_pgm_master_of_display;
	}

	display_frame_s FTX1_FRAME = new display_frame_s();

//define version, [version_u], 4 bytes
	public class version_u
	{
		public int          abcd;
		public class part
		{
			public byte      a;
			public byte      b;
			public byte      c;
			public byte      d;
		};
	}

	version_u FT_VERSION = new version_u();

	//fish.X1 version structure
	//[shm_if_version_s], 16 bytes
	public class shm_if_version_s
	{
		public version_u      hardware = new version_u();
		public version_u      firmware = new version_u();
		public version_u      shm_if = new version_u();
		public version_u      fish_x1 = new version_u();
	} 

	shm_if_version_s FTX1_SHMVER = new shm_if_version_s();

	//===========================================================================
	//Bluetooth API
	
	
	//Number of Bluetooth channels
	public static final int BT_CNT_MAX              = 8;
	
	//Allowed values for channel index are 1...8
	public static final int BT_CHAN_IDX_MIN         = 1;
	public static final int BT_CHAN_IDX_MAX         = 8;
	
	public static final int BT_ADDR_LEN             = 6;           // Bluetooth address length
	public static final int BT_MSG_LEN              = 16;          // max. Bluetooth message length
	
	
	//Bluetooth connection states
	enum BtConnState
	{
		BT_STATE_IDL,              // BT channel is disconnected
		BT_STATE_CONN_ONGOING,          // BT channel is being connected
		BT_STATE_CONNECTED,             // BT channel is connected
		BT_STATE_DISC_ONGOING           // BT channel is being disconnected
	};
	
	
	//Status of Bluetooth inquiry scan
	enum BtInquiryScanStatus
	{
		BT_INQUIRY_SCAN_NOT_POSSIBLE,
		BT_INQUIRY_SCAN_START,
		BT_INQUIRY_SCAN_RESULT,
		BT_INQUIRY_SCAN_BUSY,
		BT_INQUIRY_SCAN_TIMEOUT,
		BT_INQUIRY_SCAN_END
	};
	
	
	//Status codes for status field in Bluetooth callback functions
	enum CB_BtStatus
	{
		/*  0 */  BT_SUCCESS,         // Successful end of command
		/*  1 */  BT_CON_EXIST,           // Already connected
		/*  2 */  BT_CON_SETUP,           // Establishing of connection is ongoing
		/*  3 */  BT_SWITCHED_OFF,        // Cannot connect/listen, Bluetooth is set to off
		/*  4 */  BT_ALL_CHAN_BUSY,       // Cannot connect, no more free Bluetooth channels
		/*  5 */  BT_NOT_ROBOTX,          // Cannot connect/listen, device is not a ROBO TX Controller
		/*  6 */  BT_CON_TIMEOUT,         // Cannot connect, timeout, no device with such a BT address
		/*  7 */  BT_CON_INVALID,         // Connection does not exist
		/*  8 */  BT_CON_RELEASE,         // Disconnecting is ongoing
		/*  9 */  BT_LISTEN_ACTIVE,       // Listen is already active
		/* 10 */  BT_RECEIVE_ACTIVE,      // Receive is already active
		/* 11 */  BT_CON_INDICATION,      // Passive connection establishment (incoming connection)
		/* 12 */  BT_DISCON_INDICATION,   // Passive disconnection (initiated by remote end)
		/* 13 */  BT_MSG_INDICATION,      // Received data (incoming message)
		/* 14 */  BT_CHANNEL_BUSY,        // No connect command is allowed when listen is active or
		                        // no listen command is allowed when connected
		/* 15 */  BT_BTADDR_BUSY,         // BT address is already used by another channel
		/* 16 */  BT_NO_LISTEN_ACTIVE     // Cannot connect, no active listen on remote end
	};
	
	
	//Bluetooth inquiry scan status
	public class bt_scan_status_s
	{
		public short          status;         // status code, see enum BtInquiryScanStatus
	
	// Bluetooth device info, valid only when status == BT_INQUIRY_SCAN_RESULT
		public byte[] bt_addr = new byte[BT_ADDR_LEN];
		public char[] dummy_1 = new char[2];
		public char[] bt_name = new char[HOSTNAME_LEN + 1];
		public char[] dummy_2 = new char[3];
	}
	
	bt_scan_status_s BT_SCAN_STATUS = new bt_scan_status_s(); 
	
	//Structure for Bluetooth callback functions (other than receive)
	public class bt_cb_s
	{
		public short          chan_idx;       // channel index
		public short          status;         // status code, see enum CB_BtStatus
	}
	
	bt_cb_s BT_CB = new bt_cb_s();
	
	//Structure for Bluetooth receive callback function
	public class bt_receive_cb_s
	{
		public short          chan_idx;       // channel index
		public short          status;         // status code, see enum CB_BtStatus
		public short          msg_len;        // message length
		public byte[] msg = new byte[BT_MSG_LEN];// Bluetooth message
	} 
	
	bt_receive_cb_s BT_RECV_CB = new bt_receive_cb_s();
	
	//Bluetooth connection status structure, 8 bytes
	public class btstatus_s
	{
		public short         conn_state;     // see enum BtConnState
		public boolean       is_listen;      // if TRUE - BT channel is waiting for incoming connection (listening)
		public boolean       is_receive;     // if TRUE - BT channel is ready to receive incoming messages
		public short         link_quality;   // 0...31, 0 - the worst, 31 - the best signal quality
	}
	
	btstatus_s BT_STATUS = new btstatus_s();
	
	//struct shm_if_s;
	
	//Pointer to the Bluetooth callback function (other than receive)
	//typedef void (*P_CB_FUNC)(struct shm_if_s *, BT_CB *);
	
	//Pointer to the Bluetooth receive callback function
	//typedef void (*P_RECV_CB_FUNC)(struct shm_if_s *, BT_RECV_CB *);
	
	
	//===========================================================================
	//I2C API
	
	//Status codes for status field in I2C callback functions
	enum CB_I2cStatus
	{
		/*  0 */  I2C_SUCCESS,        // Successful end of command
		/*  1 */  I2C_READ_ERROR,         // read error
		/*  2 */  I2C_WRITE_ERROR         // write error
	};
	
	//Structure for I2C callback functions
	public class i2c_cb_s
	{
		public short          value;          // read/write value
		public short          status;         // status code, see enum CB_I2Status
	} 
	
	i2c_cb_s I2C_CB = new i2c_cb_s();
	
	//Pointer to the I2C callback function
	//typedef void (*P_I2C_CB_FUNC)(struct shm_if_s *, I2C_CB *);
	
	
	//===========================================================================
	//structures for Transfer Area
	
	
	//fish.X1 shared memory interface info structure
	//[ftX1info_s], 64 bytes
	public class ftX1info_s
	{
		public char[] 		hostname = new char[HOSTNAME_LEN + 1];
		public char[] 		btaddr = new char[BLUETOOTH_ADR_LEN + 1];
		public char        dummy;
		public int         SharedMemoryStart;
		public int         AppAreaStart;
		public int         AppAreaSize;
		public shm_if_version_s   version = new shm_if_version_s();
	}
	
	ftX1info_s FTX1_SHMIFINFO = new ftX1info_s();
	
	
	//fish.X1 structure [ftX1state_s], 100 bytes
	public class ftX1state_s
	{
		// used by local application
		public boolean           init;
		public boolean           config;
		public char[] dummy = new char[2];
		public int          trace;
	
		// public state info
		public boolean         io_mode;
		public byte           	id;
		public byte           	info_id;
		public byte           	config_id;
		public boolean[]    	io_slave_alive = new boolean[SLAVE_CNT_MAX];
		public btstatus_s[] 	btstatus = new btstatus_s[BT_CNT_MAX];
		public pgm_info_s 		master_pgm = new pgm_info_s();
		public pgm_info_s 		local_pgm = new pgm_info_s();
	} 
	
	ftX1state_s FTX1_STATE = new ftX1state_s();
	
	
	//fish.X1 [struct uni_inp_config], 4 bytes
	public class uni_inp_config
	{
		public byte           	mode;        // enum InputMode  mode
		public boolean         digital;
		public char[]          dummy = new char[2];
	} 
	
	uni_inp_config UNI_CONFIG = new uni_inp_config();
	
	
	//fish.X1 [struct cnt_inp_config], 4 bytes
	public class cnt_inp_config
	{
		public byte           	mode;        // enum InputMode  mode;
		public char[]          dummy = new char[3];
	} 
	
	cnt_inp_config CNT_CONFIG = new cnt_inp_config();
	
	
	//fish.X1 config structure
	//[shm_if_config_s], 88 bytes
	public class ftX1config
	{
		public byte           			pgm_state_req;        // enum PgmState    pgm_state_req;
		public boolean          	 	old_FtTransfer;
		public char[]            		dummy = new char[2];
		public boolean[]           	motor = new boolean[IZ_MOTOR];
		public uni_inp_config[]      	uni = {new uni_inp_config(),new uni_inp_config(),new uni_inp_config(),new uni_inp_config(),new uni_inp_config(),new uni_inp_config(),new uni_inp_config(),new uni_inp_config()};

		public cnt_inp_config[]      	cnt = {new cnt_inp_config(),new cnt_inp_config(),new cnt_inp_config(),new cnt_inp_config()};
		public short[][]           	motor_config = new short[IZ_MOTOR][4];
	}
	
	ftX1config FTX1_CONFIG = new ftX1config();
	
	//fish.X1 input structure
	//[shm_if_input_s], 68 bytes
	public class ftX1input
	{
		public short[]           	uni = new short[IZ_UNI_INPUT];
		public short[]           	cnt_in = new short[IZ_COUNTER];
		public short[]           	counter = new short[IZ_COUNTER];
		public short           	display_button_left;
		public short           	display_button_right;
		// Set to 1 when last requested counter reset was fulfilled
		public boolean[]          cnt_resetted = new boolean[IZ_COUNTER];
		// Set to 1 by motor control if target position is reached
		public boolean[]          motor_ex_reached = new boolean[IZ_MOTOR];
		// Counter reset command id of the last fulfilled counter reset
		public short[]          	cnt_reset_cmd_id = new short[IZ_COUNTER];
		// Motor extended command id of the last fulfilled motor_ex command
		public short[]          	motor_ex_cmd_id = new short[IZ_MOTOR];
	}
	
	ftX1input FTX1_INPUT = new ftX1input();
	
	
	//fish.X1 output structure, only out values, 44 bytes
	public class ftX1output
	{
		// Counter reset requests (increment each time by one)
		public short[]          cnt_reset_cmd_id = new short[IZ_COUNTER];
		// If not 0, synchronize this channel with the given channel (1:channel 0, ..)
		public byte[]           master = new byte[IZ_MOTOR];
		// User program selected motor PWM values
		public short[]           duty = new short[IZ_PWM_CHAN];
		// Selected distane (counter value) at which motor shall stop
		public short[]          distance = new short[IZ_MOTOR];
		// Increment by one each time motor_ex settings change
		public short[]          motor_ex_cmd_id = new short[IZ_MOTOR];
	}
	
	ftX1output FTX1_OUTPUT = new ftX1output();
	
	
	//fish.X1 output structure, with display message, 108 bytes
	public class _ftX1display
	{
		public display_msg_s	        display_msg = new display_msg_s();
		public display_frame_s      	display_frame = new display_frame_s();
	} 
	
	_ftX1display FTX1_DISPLAY = new _ftX1display();
	
	
	//status of transferarea (ftMscLib), 4 bytes
	public class _transfer_status
	{
		public byte           status;             //  status transfer area (X1)
		public byte           iostatus;           //  status io communication
		public short          ComErr;             //  system error code by connection error
	} 
	
	_transfer_status TRANSFER_STATUS = new _transfer_status();
	
	
	//change fields for UniIO, Counter, Timer, Update status, 8 bytes
	public class _change_state
	{
		public short          UpdInterface;
		public byte           ChangeStatus;
		public byte           ChangeUni;
		public byte           ChangeCntIn;
		public byte           ChangeCounter;
		public byte           ChangeTimer;
		public byte           reserved;
	} 
	
	_change_state CHANGE_STATE = new _change_state();
	
	
	//16-bit timers used by RoboPro, 12 bytes
	public class _rp_timer
	{
		public short          Timer1ms;
		public short          Timer10ms;
		public short          Timer100ms;
		public short          Timer1s;
		public short          Timer10s;
		public short          Timer1min;
	} 
	
	_rp_timer RP_TIMER = new _rp_timer();
	
	
	//motor values and debug, 24 bytes
	public class _motor
	{
		// Motor PWM values
		public short[]           duty = new short[IZ_PWM_CHAN];
		// Values used for debugging motor control
		public short[]           debug = new short[IZ_MOTOR];
	} 
	_motor MOTOR = new _motor();
	
	
	//Button input simulation, 4 bytes
	public class _input_sim
	{
		public short[]           simButtons = new short[2];
	} 
	
	_input_sim INPUT_SIM = new _input_sim();
	
	
	//Hook table with pointers to the functions,
	//that can be called by RoboPro, 132 bytes
	public class _hook_table
	{
//	BOOL32  (*IsRunAllowed)             (void);
//	int  (*GetSystemTime)            (enum TimerUnit unit);
//	void    (*DisplayMsg)               (struct shm_if_s * p_shm, char * p_msg);
//	BOOL32  (*IsDisplayBeingRefreshed)  (struct shm_if_s * p_shm);
//	void    (*BtConnect)                (int channel, byte * btaddr, P_CB_FUNC p_cb_func);
//	void    (*BtDisconnect)             (int channel, P_CB_FUNC p_cb_func);
//	void    (*BtSend)                   (int channel, int len, byte * p_msg, P_CB_FUNC p_cb_func);
//	void    (*BtStartReceive)           (int channel, P_RECV_CB_FUNC p_cb_func);
//	void    (*BtStopReceive)            (int channel, P_RECV_CB_FUNC p_cb_func);
//	void    (*BtStartListen)            (int channel, byte * btaddr, P_CB_FUNC p_cb_func);
//	void    (*BtStopListen)             (int channel, P_CB_FUNC p_cb_func);
//	char   *(*BtAddrToStr)              (byte * btaddr, char * str);
//	void    (*I2cRead)                  (byte devaddr, int offset, byte flags, P_I2C_CB_FUNC p_cb_func);
//	void    (*I2cWrite)                 (byte devaddr, int offset, short data, byte flags, P_I2C_CB_FUNC p_cb_func);
//	INT32   (*sprintf)                  (char * s, const char * format, ...);
//	INT32   (*memcmp)                   (const void * s1, const void * s2, int n);
//	void   *(*memcpy)                   (void * s1, const void * s2, int n);
//	void   *(*memmove)                  (void * s1, const void * s2, int n);
//	void   *(*memset)                   (void * s, INT32 c, int n);
//	char   *(*strcat)                   (char * s1, const char * s2);
//	char   *(*strncat)                  (char * s1, const char * s2, int n);
//	char   *(*strchr)                   (const char * s, INT32 c);
//	char   *(*strrchr)                  (const char * s, INT32 c);
//	INT32   (*strcmp)                   (const char * s1, const char * s2);
//	INT32   (*strncmp)                  (const char * s1, const char * s2, int n);
//	INT32   (*stricmp)                  (const char * s1, const char * s2);
//	INT32   (*strnicmp)                 (const char * s1, const char * s2, int n);
//	char   *(*strcpy)                   (char * s1, const char * s2);
//	char   *(*strncpy)                  (char * s1, const char * s2, int n);
//	int  (*strlen)                   (const char * s);
//	char   *(*strstr)                   (const char * s1, const char * s2);
//	char   *(*strtok)                   (char * s1, const char * s2);
//	char   *(*strupr)                   (char * s);
//	char   *(*strlwr)                   (char * s);
//	INT32   (*atoi)                     (const char * nptr);
	} 
	
	_hook_table HOOK_TABLE = new _hook_table(); 
	
	
	
	//============================================================================
	//transferarea of ROBO TX Controller
	//-----------------------------------------------------------------------------
	//private static final int RESERVE_SIZE (1024 - ( sizeof(FTX1_SHMIFINFO)  + sizeof(FTX1_STATE) + sizeof(FTX1_CONFIG) + sizeof(FTX1_INPUT) + sizeof(FTX1_OUTPUT) + \sizeof(FTX1_DISPLAY)    + sizeof(TRANSFER_STATUS) + sizeof(CHANGE_STATE)    + sizeof(RP_TIMER)        + sizeof(MOTOR)           + sizeof(INPUT_SIM)       + sizeof(HOOK_TABLE)        ))
	
	
	public class shm_if_s
	{
		public ftX1info_s 		ftX1info   		= FTX1_SHMIFINFO;         // info structure
		public ftX1state_s 		ftX1state  		= FTX1_STATE;             // state structure
		public ftX1config 		ftX1config 		= FTX1_CONFIG;            // config structure   
		public ftX1input 			ftX1in     		= FTX1_INPUT;             // input structure
		public ftX1output 		ftX1out    		= FTX1_OUTPUT;            // output structure
		public _ftX1display 		ftX1display 	= FTX1_DISPLAY;           // display structure
	
	
		public _transfer_status 	IFStatus 		= TRANSFER_STATUS;    
		public _change_state 		IFChange   		= CHANGE_STATE;           // change state of Input, Counter, Timer
		public _rp_timer 			IFTimer    		= RP_TIMER;               // 16-Bit timer variables
		public _motor 			IFMotor    		= MOTOR;                  // motors control
		public _input_sim 		IFInputSim 		= INPUT_SIM;              // input simulation
		public _hook_table 		IFHookTable 	= HOOK_TABLE;            // hook table with functions pointers
	
	//char                reserved[RESERVE_SIZE];
	} 
	
	public shm_if_s FISH_X1_TRANSFER = new shm_if_s();

}
