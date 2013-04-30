//=============================================================================
// Demo program "Bluetooth Messaging with External BT Device".
//
// Can be run under control of the ROBO TX Controller
// firmware in download (local) mode.
// Starts and stops motor, connected to outputs M1, by means
// of the button, connected to the input I8 on other
// ROBO TX Controller. Pulses from the motor are calculated
// by the counter C1. The motor is stopped after the counter
// reaches the value of 1000.
//
// Disclaimer - Exclusion of Liability
//
// This software is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE. It can be used and modified by anyone
// free of any license obligations or authoring rights.
//=============================================================================

#include "ROBO_TX_PRG.h"

#define MOTOR_NUMBER    1
#define MOTOR_IDX       (MOTOR_NUMBER - 1)

#define BT_CHANNEL      1

// Bluetooth address of external BT device (e.g. SmartPhone)
static UCHAR8 bt_addr[6] = {0x00,0x00,0x00,0x00,0x00,0x00};     // get it dynamically in PrgInit 
static UCHAR8 *bt_address = &bt_addr[0];

static enum
{
    START_LISTEN,
    PAUSE_1,
    WAIT_CONNECT,
    PAUSE_2,
    PAUSE_3,
    RECEIVE,
    PAUSE_4,
    EXIT
} stage;

enum IFCmdCode {

    ECHO_REQUEST          = 1,
    ECHO_REPLY            = 101,

    REM_IO_REQUEST        = 2,
    REM_IO_REPLY          = 102,

    REM_MONITOR_REQUEST   = 3,
    REM_MONITOR_REPLY     = 103,


    REM_CONFIG_WR_REQUEST = 5,
    REM_CONFIG_WR_REPLY   = 105,

    MSG_WR_REQUEST        = 8,
    MSG_WR_REPLY          = 108,
};

static unsigned long timer;
static enum bt_commands_e command;
static CHAR8 command_status;
static CHAR8 receive_command_status;


/*-----------------------------------------------------------------------------
 * Function Name       : BtCallback
 *
 * This callback function is called to inform the program about result (status)
 * of execution of any Bluetooth command except BtStartReceive command.
 *-----------------------------------------------------------------------------*/
static void BtCallback
(
    TA * p_ta_array,
    BT_CB * p_data
)
{
    command_status = p_data->status;
}



 void HandleRequest
(
	TA * p_ta,
	UCHAR8  rxmsg[],
	int   rxlen
	
)
{
	int code = rxmsg[0];
	
	switch (code)
    {
        case ECHO_REQUEST:
        {
            int len = 5;
			UCHAR8 msg[len];

			msg[0] = ECHO_REPLY;
			msg[1] = 1;
			msg[2] = 2;
			msg[3] = 3;
			msg[4] = 4;
			p_ta->hook_table.BtSend(BT_CHANNEL, len, msg, BtCallback);
			
		}
        break;

        case REM_IO_REQUEST:
        {
						
						int intDataPosition = 0;
						
						{ // FTX1_OUTPUT
							int i;
							for (i = 0; i < 4; i+= 1) {
								short val = ((rxmsg[intDataPosition + 1] << 8) + (rxmsg [intDataPosition + 0] << 0));
								p_ta->output.cnt_reset_cmd_id[i] = val; // Counter reset requests (increment each time by one)
								intDataPosition += 2;
							}
							
							for (i = 0; i < 4; i+= 1) {
								short val = (rxmsg [intDataPosition + 0] << 0);
								p_ta->output.master[i] = val; // If not 0, synchronize this channel with the given channel (1:channel 0, ..)
								intDataPosition += 1;
							}
							
							for (i = 0; i < 8; i+= 1) {
								short val = ((rxmsg[intDataPosition + 1] << 8) + (rxmsg [intDataPosition + 0] << 0));
								p_ta->output.duty[i] = val; // User program selected motor PWM values	
								intDataPosition += 2;
							}
							
							for (i = 0; i < 4; i+= 1) {
								short val = ((rxmsg[intDataPosition + 1] << 8) + (rxmsg [intDataPosition + 0] << 0));
								p_ta->output.distance[i] = val; // Selected distane (counter value) at which motor shall stop
								intDataPosition += 2;
							}
							
							for (i = 0; i < 4; i+= 1) {
								short val = ((rxmsg[intDataPosition + 1] << 8) + (rxmsg [intDataPosition + 0] << 0));
								p_ta->output.motor_ex_cmd_id[i] = val; // Increment by one each time motor_ex settings change
								intDataPosition += 2;
							}
						} // FTX1_OUTPUT
						
						{ // FTX1_INPUT
							int len = 49;
							UCHAR8 msg[len];

							msg[0] = REM_IO_REPLY;
			 
							
							int intResponsePosition = 1;
							
							int i;
							for (i = 0; i<8;i++){//uni 16 bytes
								int val = p_ta->input.uni[i]; 
								msg[intDataPosition + 0] = (UCHAR8) ((val >> 0) & 0x00FF);
								msg[intDataPosition + 1] = (UCHAR8) ((val >> 8) & 0x00FF);
								intResponsePosition += 2;
							}
							
							for (i = 0; i<4;i++){//cnt_in 4 bytes
							int val = p_ta->input.cnt_in[i]; 
								msg[intDataPosition + 0] = (UCHAR8) ((val >> 0) & 0x00FF);
								intResponsePosition += 1;
							}
							
							for (i = 0; i<4;i++){//counter 8 bytes
							int val = p_ta->input.counter[i]; 
								msg[intDataPosition + 0] = (UCHAR8) ((val >> 0) & 0x00FF);
								msg[intDataPosition + 1] = (UCHAR8) ((val >> 8) & 0x00FF);
								intResponsePosition += 2;
							}
							
							{// diplsay_button_left 2 bytes
							int val = p_ta->input.display_button_left; 
								msg[intDataPosition + 0] = (UCHAR8) ((val >> 0) & 0x00FF);
								msg[intDataPosition + 1] = (UCHAR8) ((val >> 8) & 0x00FF);
								intResponsePosition += 2; 
							}
							
							{// display_button_right 2 bytes
							int val = p_ta->input.display_button_right; 
								msg[intDataPosition + 0] = (UCHAR8) ((val >> 0) & 0x00FF);
								msg[intDataPosition + 1] = (UCHAR8) ((val >> 8) & 0x00FF);
								intResponsePosition += 2; 
							}
							
							for (i = 0; i<4;i++){//cnt_reset_cmd_id 8 bytes
							int val = p_ta->output.cnt_reset_cmd_id[i]; 
								msg[intDataPosition + 0] = (UCHAR8) ((val >> 0) & 0x00FF);
								msg[intDataPosition + 1] = (UCHAR8) ((val >> 8) & 0x00FF);
								intResponsePosition += 2;
							}
							
							for (i = 0; i<4;i++){//motor_ex_cmd_id 8 bytes
							int val = p_ta->output.motor_ex_cmd_id[i]; 
								msg[intDataPosition + 0] = (UCHAR8) ((val >> 0) & 0x00FF);
								msg[intDataPosition + 1] = (UCHAR8) ((val >> 8) & 0x00FF);
								intResponsePosition += 2;
							} 

							intResponsePosition += 1;
							
							p_ta->hook_table.BtSend(BT_CHANNEL, len, msg, BtCallback);
				}
        }
        break;


        case REM_CONFIG_WR_REQUEST:
        {
            
        }
        break;
		
		case MSG_WR_REQUEST:
		{
			UCHAR8 dispMsg[rxlen-1];
			int i ;
			for(i = 1; i< rxlen ;i++ ){
				dispMsg[i] = rxmsg[i];
			}
			
			p_ta->hook_table.DisplayMsg(p_ta, (char *)dispMsg);
			
			int len = 1;
			UCHAR8 msg[len];

			msg[0] = MSG_WR_REPLY;
			p_ta->hook_table.BtSend(BT_CHANNEL, len, msg, BtCallback);
		}
		break;

		
		default:
        break;
    }
}






/*-----------------------------------------------------------------------------
 * Function Name       : BtReceiveCallback
 *
 * This callback function is called to inform the program about result (status)
 * of execution of BtStartReceive command. It is also called when a message
 * arrives via Bluetooth.
 *-----------------------------------------------------------------------------*/
static void BtReceiveCallback
(
    TA * p_ta_array,
    BT_RECV_CB * p_data
)
{
    if (p_data->status == BT_MSG_INDICATION)
    {
        TA * p_ta = &p_ta_array[TA_LOCAL];
        
        UCHAR8 rxmsg[256];
        int rxlen=0;
        
        if(p_data->msg_len <= 256)
        {
            p_ta->hook_table.memcpy(rxmsg, p_data->msg, p_data->msg_len);
            rxlen = p_data->msg_len;
            rxmsg[p_data->msg_len] = 0;

			HandleRequest(p_ta, rxmsg, rxlen);
            //p_ta->hook_table.DisplayMsg(p_ta, (char *)rxmsg);
        }    

        if(rxlen > 0)
        {
            // Send BT message
            command_status = -1;
            //p_ta->hook_table.BtSend(BT_CHANNEL, rxlen, rxmsg, BtCallback);
        }    
    }
    else
    {
        receive_command_status = p_data->status;
    }
}


/*-----------------------------------------------------------------------------
 * Function Name       : PrgInit
 *
 * This it the program initialization.
 * It is called once.
 *-----------------------------------------------------------------------------*/
void PrgInit
(
    TA * p_ta_array,    // pointer to the array of transfer areas
    int ta_count        // number of transfer areas in array (equal to TA_COUNT)
)
{
    TA * p_ta = &p_ta_array[TA_LOCAL];

    // get BT address of registered external message device
    p_ta->hook_table.GetExtBtDevAddr(bt_addr);

    // Start listen to the controller with bt_address via Bluetooth channel BT_CHANNEL
    stage = START_LISTEN;
    command = CMD_START_LISTEN;
    command_status = -1;
    p_ta->hook_table.BtStartListen(BT_CHANNEL, bt_address, BtCallback);
}


/*-----------------------------------------------------------------------------
 * Function Name       : PrgTic
 *
 * This is the main function of this program.
 * It is called every tic (1 ms) realtime.
 *-----------------------------------------------------------------------------*/
int PrgTic
(
    TA * p_ta_array,    // pointer to the array of transfer areas
    int ta_count        // number of transfer areas in array (equal to TA_COUNT)
)
{
    int rc = 0x7FFF; // return code: 0x7FFF - program should be further called by the firmware;
                     //              0      - program should be normally stopped by the firmware;
                     //              any other value is considered by the firmware as an error code
                     //              and the program is stopped.
    TA * p_ta = &p_ta_array[TA_LOCAL];

    switch (stage)
    {
        case START_LISTEN:
            if (command_status >= 0)
            {
                if (BtDisplayCommandStatus(p_ta, bt_address, BT_CHANNEL, command, command_status))
                {
                    if (command_status != BT_SUCCESS)
                    {
                        stage = PAUSE_4;
                    }
                    else
                    {
                        stage = PAUSE_1;
                        command_status = -1;
                    }
                    timer = 0;
                }
            }
            break;

        case PAUSE_1: // to let a user to notice the "Started listening" display output
            if (++timer >= 3000) // wait for 3 seconds
            {
                stage = WAIT_CONNECT;
            }
            else
            {
                break;
            }

        case WAIT_CONNECT:
            if (command_status >= 0)
            {
                if (BtDisplayCommandStatus(p_ta, bt_address, BT_CHANNEL, command, command_status))
                {
                    if (command_status == BT_CON_INDICATION)
                    {
                        stage = PAUSE_2;

                        // Start receive from Bluetooth channel BT_CHANNEL
                        command = CMD_START_RECEIVE;
                        receive_command_status = -1;
                        p_ta->hook_table.BtStartReceive(BT_CHANNEL, BtReceiveCallback);
                    }
                    command_status = -1;
                    timer = 0;
                }
            }
            break;

        case PAUSE_2: // to let a user to notice the "Passive connection establishment
                      // (incoming connection)" display output
            if (++timer >= 3000) // wait for 3 seconds
            {
                stage = PAUSE_3;
                timer = 0;
            }
            else
            {
                break;
            }

        case PAUSE_3: // to let a user to notice the "Started receiving" display output
            if (receive_command_status >= 0)
            {
                if (BtDisplayCommandStatus(p_ta, bt_address, BT_CHANNEL, command, receive_command_status))
                {
                    if (receive_command_status == BT_SUCCESS)
                    {
                        // Successful result of BtStartReceive() - restart the timer to let a user to
                        // notice this display output
                        receive_command_status = -1;
                        timer = 0;
                    }
                    else
                    {
                        // Unsuccessful result of BtStartReceive() - make PAUSE_4 (to let a user to
                        // notice this display output) and exit the program
                        stage = PAUSE_4;
                        timer = 0;
                    }
                }
                break;
            }
            else if (++timer >= 3000) // wait for 3 seconds
            {
                if (!p_ta->hook_table.IsDisplayBeingRefreshed(p_ta)) // wait until display is refreshed
                {
                    // Drop all pop-up messages from display and return to the main frame
                    p_ta->hook_table.DisplayMsg(p_ta, NULL);

                    stage = RECEIVE;
                }
            }
            else
            {
                break;
            }

        case RECEIVE:
            if (command_status >= 0)
            {
                if (command_status != BT_SUCCESS)
                {
                    // Usually if we have come here, then this means the other controller
                    // has disconnected from us

                    /*
                    // This block of code should be used if we want that the program stops when other
                    // ROBO TX Controller disconnects from us
                    if (BtDisplayCommandStatus(p_ta, bt_address, BT_CHANNEL, command, command_status))
                    {
                        stage = PAUSE_4;
                        timer = 0;
                    }
                    */
                }
            }
            break;

        case PAUSE_4: // to let a user to notice the last display output
            if (++timer >= 3000) // wait for 3 seconds
            {
                stage = EXIT;
            }
            else
            {
                break;
            }

        case EXIT:
            if (!p_ta->hook_table.IsDisplayBeingRefreshed(p_ta)) // wait until display is refreshed
            {
                rc = 0; // stop program
            }
            break;

        default:
            break;
    }
    return rc;
}
