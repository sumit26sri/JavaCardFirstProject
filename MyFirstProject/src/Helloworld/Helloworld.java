package Helloworld;

import javacard.framework.*;
import Addition.Addition;
public class Helloworld extends Applet
{
	
	
	private final static byte[] hello=
	{0x48, 0x65, 0x6c, 0x6c, 0x6f, 0x20, 0x72, 0x6f, 0x62, 0x65, 0x72, 0x74} ;

	public static void install(byte[] bArray, short bOffset, byte bLength) 
	{
		new Helloworld().register();
	}

	public void process(APDU apdu)
	{
		if (selectingApplet())
		{
			return;
		}

       // byte[] send_buffer;
		byte[] buf = apdu.getBuffer();
		
		switch (buf[ISO7816.OFFSET_INS])
		{
		case (byte)0x00:
			short rcvdataLen = apdu.setIncomingAndReceive();
			short offset = (short)0;
			send buffer = new byte[256];
			while(rcvDataLen > 0){
				
				Util.arrayCopyNonAtomic(buf, ISO7816.OFFSET_CDATA, send_buffer,offset,rcvdataLen);
				offset += rcvdataLen;
				rcvdataLen = apdu.receiveBytes(ISO7816.OFFSET_CDATA);
		
			} 
			apdu.setOutgoing();
			apdu.setOutgoingLength((short) (offset+5));
			apdu.sendBytes((short)0,(short)5);
			apdu.sendBytesLong(send_buffer, (short)0, offset);
			break;
		case(byte)0x01:
			Addition temp = new Addition();
			temp.addfun(apdu);
			break;
			
			
			
			
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}

}
