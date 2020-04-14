package com.insurance.controller;

import javacard.framework.*;

public class InsuranceApplet extends Applet
{

	public static void install(byte[] bArray, short bOffset, byte bLength) 
	{
		new InsuranceApplet().register(bArray, (short) (bOffset + 1), bArray[bOffset]);
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
		case (byte)0x00:
			break;
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}

}
