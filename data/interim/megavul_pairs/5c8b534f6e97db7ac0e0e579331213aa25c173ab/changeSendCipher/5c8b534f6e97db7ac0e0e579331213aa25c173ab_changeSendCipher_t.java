class changeSendCipher {
public void changeSendCipher(BlockCipher bc, MAC mac)
	{
		tc.changeSendCipher(bc, mac);
		if (km.isStrictKex())
			tc.resetSendSequenceNumber();
	}
}
