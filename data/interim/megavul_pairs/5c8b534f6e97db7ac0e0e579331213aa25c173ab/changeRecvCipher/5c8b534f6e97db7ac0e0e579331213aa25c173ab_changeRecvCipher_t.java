class changeRecvCipher {
public void changeRecvCipher(BlockCipher bc, MAC mac)
	{
		tc.changeRecvCipher(bc, mac);
		if (km.isStrictKex())
			tc.resetReceiveSequenceNumber();
	}
}
