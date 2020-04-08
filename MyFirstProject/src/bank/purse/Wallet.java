package bank.purse;

import javacard.framework.*;

public class Wallet extends Applet
{
/* constants declaration */

// code of CLA byte in the command APDU header
final static byte Wallet_CLA =(byte)0xB0;

// codes of INS byte in the command APDU header
final static byte Deposit = (byte) 0x10;
final static byte Debit = (byte) 0x20;
final static byte Balance = (byte) 0x30;
final static byte Validate = (byte) 0x40;


/*maximum number of incorrect tries before the PIN is blocked*/
final static byte PinTryLimit =(byte)0x03;

// maximum size PIN
final static byte MaxPinSize =(byte)0x04;

// status word (SW1-SW2) to signal that the balance becomes negative;
final static short SW_NEGATIVE_BALANCE = (short)0x6910;

/* instance variables declaration */
OwnerPIN pin;
byte balance;
byte buffer[];

private Wallet(){
	
pin = new OwnerPIN(PinTryLimit, MaxPinSize);
balance = 0;
register();
}//Constructor end

public static void install(APDU apdu){
// create a Wallet applet instance
new Wallet();
} // end of install method

public boolean select() {
// reset validation flag in the PIN object to
// false
pin.reset();
// returns true to JCRE to indicate that the
// applet is ready to accept incoming APDUs.
return true;
}// end of select method



	public static void install(byte[] bArray, short bOffset, byte bLength) 
	{
		new Wallet().register(bArray, (short) (bOffset + 1), bArray[bOffset]);
	}

	public void process(APDU apdu) {

                  buffer = apdu.getBuffer();
                        if (buffer[ISO.OFFSET_CLA] !== Wallet_CLA)
                        	ISOException.throwIt
                            (ISO.SW_CLA_NOT_SUPPORTED);
                                 switch (buffer[ISO.OFFSET_INS]) {
                                              case Balance: getBalance(apdu); return;
                                              case Debit: debit(apdu); return;
                                              case Deposit: deposit(apdu);return;
                                              case Validate: validate(apdu);return
                                              default: ISOException.throwIt
                                              (ISO.SW_INS_NOT_SUPPORTED);
                                  }
                      }
                      
private void deposit(APDU apdu) {
	// access authentication
if ( ! pin.isValidated() )
ISOException.throwIt (ISO.SW_PIN_REQUIRED);

// Lc byte denotes the number of bytes in the
// data field of the comamnd APDU
byte numBytes = (byte) (buffer[ISO.OFFSET_LC]);

// indicate that this APDU has incoming data and
// receive data starting from the offset
// ISO.OFFSET_CDATA
byte byteRead =
(byte)(apdu.setIncomingAndReceive());

// it is an error if the number of data bytes
// read does not match the number in Lc byte
if (byteRead != 1)
ISOException.throwIt(ISO.SW_WRONG_LENGTh);

// increase the balance by the amount specified
// in the data field of the command APDU.
balance = (byte)
(balance + buffer[ISO.OFFSET_CDATA]);

// return successfully
return;
} // end of deposit method


private void debit(APDU apdu) {
// access authentication
if ( ! pin.isValidated() )
	ISOException.throwIt(ISO.SW_PIN_REQUIRED);

byte numBytes = (byte)(buffer[ISO.OFFSET_LC]);
byte byteRead = (byte)(apdu.setIncomingAndReceive());

if (byteRead != 1)
	ISOException.throwIt(ISO.SW_WRONG_LENGTH);
	
	// balance can not be negative
if ( ( balance - buffer[ISO.OFFSET_CDATA]) < 0 )
	ISOException.throwIt(SW_NEGATIVE_BALANCE);

balance = (byte)
   (balance - buffer[ISO.OFFSET_CDATA]);
} // end of debit method

private void getBalance(APDU apdu) {
// access authentication
if ( ! pin.isValidated() )
	ISOException.throwIt(ISO.SW_PIN_REQUIRED);

// inform system that the applet has finished
// processing the command and the system should
// now prepare to construct a response APDU
// which contains data field
apdu.setOutgoing();

//indicate the number of bytes in the data field
apdu.setOutgoingLength((byte)1);

// move the data into the APDU buffer starting
// at offset 0
buffer[0] = balance;
// send 1 byte of data at offset 0 in the APDU
// buffer
apdu.sendBytes((short)0, (short)1);
   } // end of getBalance method


private void validate(APDU apdu) {
// retrieve the PIN data which requires to be
// valid ated. The user interface data is
// stored in the data field of the APDU
byte byteRead = (byte)(apdu.setIncomingAndReceive());

// validate user interface and set the
// validation flag in the user interface
// object to be true if the validation.
// succeeds. If user interface validation
// fails, PinException would be thrown from
// the pin.check() method.
pin.check(buffer, ISO.OFFSET_CDATA, byteRead);
    } // end of validate method

                   
}
