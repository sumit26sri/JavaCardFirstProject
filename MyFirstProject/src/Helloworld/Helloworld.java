package Helloworld;

import javacard.framework.*;

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

		byte[] buf = apdu.getBuffer();
		switch (buf[ISO7816.OFFSET_INS])
		{
		case (byte)0x40:
			Util.arrayCopy(hello, (byte)0, buf, ISO7816.OFFSET_CDATA, (byte)12);
				apdu.setOutgoingAndSend(ISO7816.OFFSET_CDATA, (byte)12);

			break;
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}

}
