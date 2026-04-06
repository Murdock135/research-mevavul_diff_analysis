class addExtraKexAlgorithms {
private static void addExtraKexAlgorithms(CryptoWishList cwl) {
		String[] oldKexAlgorithms = cwl.kexAlgorithms;
		List<String> kexAlgorithms = new ArrayList<>(oldKexAlgorithms.length + 2);
		for (String algo : oldKexAlgorithms)
		{
			if (!algo.equals(EXT_INFO_C) && !algo.equals(KEX_STRICT_C_OPENSSH))
				kexAlgorithms.add(algo);
		}
		kexAlgorithms.add(EXT_INFO_C);
		kexAlgorithms.add(KEX_STRICT_C_OPENSSH);
		cwl.kexAlgorithms = kexAlgorithms.toArray(new String[0]);
	}
}
