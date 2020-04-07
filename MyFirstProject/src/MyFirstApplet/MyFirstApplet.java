package MyFirstApplet;

import javacard.framework.*;

public class MyFirstApplet extends Applet
{

	public static void install(byte[] bArray, short bOffset, byte bLength) 
	{
		new MyFirstApplet().register(bArray, (short) (bOffset + 1), bArray[bOffset]);
	}

	public void process(APDU apdu)
	{
		if (selectingApplet())
		{
			return;
		}

		byte[] buf = apdu.getBuffer();
		apdu.setIncomingAndReceive();
		short res = 0;
		switch (buf[ISO7816.OFFSET_INS])
		{
		case (byte)0x01:
			short temp1 = buf[ISO7816.OFFSET_P1];
			short temp2 = buf[ISO7816.OFFSET_P2];
			
			res = (short)(temp1+temp2);
			Util.setShort(buf,(short)0, res);
            apdu.setOutgoingAndSend((short)0, (short)2);
			break;
			
		case (byte)0x02:
			apdu.setOutgoingAndSend((short)0,(short)5);
			break;
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}

}
