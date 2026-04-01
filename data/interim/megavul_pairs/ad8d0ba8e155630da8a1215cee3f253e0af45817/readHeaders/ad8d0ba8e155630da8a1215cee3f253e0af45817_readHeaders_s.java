class readHeaders {
private void readHeaders(long fileLength) throws IOException, RarException {
		markHead = null;
		newMhd = null;
		headers.clear();
		currentHeaderIndex = 0;
		int toRead = 0;

		while (true) {
			int size = 0;
			long newpos = 0;
			byte[] baseBlockBuffer = new byte[BaseBlock.BaseBlockSize];

			long position = rof.getPosition();

			// Weird, but is trying to read beyond the end of the file
			if (position >= fileLength) {
				break;
			}

			// logger.info("\n--------reading header--------");
			size = rof.readFully(baseBlockBuffer, BaseBlock.BaseBlockSize);
			if (size == 0) {
				break;
			}
			BaseBlock block = new BaseBlock(baseBlockBuffer);

			block.setPositionInFile(position);

			switch (block.getHeaderType()) {

			case MarkHeader:
				markHead = new MarkHeader(block);
				if (!markHead.isSignature()) {
					throw new RarException(
							RarException.RarExceptionType.badRarArchive);
				}
				headers.add(markHead);
				// markHead.print();
				break;

			case MainHeader:
				toRead = block.hasEncryptVersion() ? MainHeader.mainHeaderSizeWithEnc
						: MainHeader.mainHeaderSize;
				byte[] mainbuff = new byte[toRead];
				rof.readFully(mainbuff, toRead);
				MainHeader mainhead = new MainHeader(block, mainbuff);
				headers.add(mainhead);
				this.newMhd = mainhead;
				if (newMhd.isEncrypted()) {
					throw new RarException(
							RarExceptionType.rarEncryptedException);
				}
				// mainhead.print();
				break;

			case SignHeader:
				toRead = SignHeader.signHeaderSize;
				byte[] signBuff = new byte[toRead];
				rof.readFully(signBuff, toRead);
				SignHeader signHead = new SignHeader(block, signBuff);
				headers.add(signHead);
				// logger.info("HeaderType: SignHeader");

				break;

			case AvHeader:
				toRead = AVHeader.avHeaderSize;
				byte[] avBuff = new byte[toRead];
				rof.readFully(avBuff, toRead);
				AVHeader avHead = new AVHeader(block, avBuff);
				headers.add(avHead);
				// logger.info("headertype: AVHeader");
				break;

			case CommHeader:
				toRead = CommentHeader.commentHeaderSize;
				byte[] commBuff = new byte[toRead];
				rof.readFully(commBuff, toRead);
				CommentHeader commHead = new CommentHeader(block, commBuff);
				headers.add(commHead);
				// logger.info("method: "+commHead.getUnpMethod()+"; 0x"+
				// Integer.toHexString(commHead.getUnpMethod()));
				newpos = commHead.getPositionInFile()
						+ commHead.getHeaderSize();
				rof.setPosition(newpos);

				break;
			case EndArcHeader:

				toRead = 0;
				if (block.hasArchiveDataCRC()) {
					toRead += EndArcHeader.endArcArchiveDataCrcSize;
				}
				if (block.hasVolumeNumber()) {
					toRead += EndArcHeader.endArcVolumeNumberSize;
				}
				EndArcHeader endArcHead;
				if (toRead > 0) {
					byte[] endArchBuff = new byte[toRead];
					rof.readFully(endArchBuff, toRead);
					endArcHead = new EndArcHeader(block, endArchBuff);
					// logger.info("HeaderType: endarch\ndatacrc:"+
					// endArcHead.getArchiveDataCRC());
				} else {
					// logger.info("HeaderType: endarch - no Data");
					endArcHead = new EndArcHeader(block, null);
				}
				headers.add(endArcHead);
				// logger.info("\n--------end header--------");
				return;

			default:
				byte[] blockHeaderBuffer = new byte[BlockHeader.blockHeaderSize];
				rof.readFully(blockHeaderBuffer, BlockHeader.blockHeaderSize);
				BlockHeader blockHead = new BlockHeader(block,
						blockHeaderBuffer);

				switch (blockHead.getHeaderType()) {
				case NewSubHeader:
				case FileHeader:
					toRead = blockHead.getHeaderSize()
							- BlockHeader.BaseBlockSize
							- BlockHeader.blockHeaderSize;
					byte[] fileHeaderBuffer = new byte[toRead];
					rof.readFully(fileHeaderBuffer, toRead);

					FileHeader fh = new FileHeader(blockHead, fileHeaderBuffer);
					headers.add(fh);
					newpos = fh.getPositionInFile() + fh.getHeaderSize()
							+ fh.getFullPackSize();
					rof.setPosition(newpos);
					break;

				case ProtectHeader:
					toRead = blockHead.getHeaderSize()
							- BlockHeader.BaseBlockSize
							- BlockHeader.blockHeaderSize;
					byte[] protectHeaderBuffer = new byte[toRead];
					rof.readFully(protectHeaderBuffer, toRead);
					ProtectHeader ph = new ProtectHeader(blockHead,
							protectHeaderBuffer);

					newpos = ph.getPositionInFile() + ph.getHeaderSize()
							+ ph.getDataSize();
					rof.setPosition(newpos);
					break;

				case SubHeader: {
					byte[] subHeadbuffer = new byte[SubBlockHeader.SubBlockHeaderSize];
					rof.readFully(subHeadbuffer,
							SubBlockHeader.SubBlockHeaderSize);
					SubBlockHeader subHead = new SubBlockHeader(blockHead,
							subHeadbuffer);
					subHead.print();
					switch (subHead.getSubType()) {
					case MAC_HEAD: {
						byte[] macHeaderbuffer = new byte[MacInfoHeader.MacInfoHeaderSize];
						rof.readFully(macHeaderbuffer,
								MacInfoHeader.MacInfoHeaderSize);
						MacInfoHeader macHeader = new MacInfoHeader(subHead,
								macHeaderbuffer);
						macHeader.print();
						headers.add(macHeader);

						break;
					}
					// TODO implement other subheaders
					case BEEA_HEAD:
						break;
					case EA_HEAD: {
						byte[] eaHeaderBuffer = new byte[EAHeader.EAHeaderSize];
						rof.readFully(eaHeaderBuffer, EAHeader.EAHeaderSize);
						EAHeader eaHeader = new EAHeader(subHead,
								eaHeaderBuffer);
						eaHeader.print();
						headers.add(eaHeader);

						break;
					}
					case NTACL_HEAD:
						break;
					case STREAM_HEAD:
						break;
					case UO_HEAD:
						toRead = subHead.getHeaderSize();
						toRead -= BaseBlock.BaseBlockSize;
						toRead -= BlockHeader.blockHeaderSize;
						toRead -= SubBlockHeader.SubBlockHeaderSize;
						byte[] uoHeaderBuffer = new byte[toRead];
						rof.readFully(uoHeaderBuffer, toRead);
						UnixOwnersHeader uoHeader = new UnixOwnersHeader(
								subHead, uoHeaderBuffer);
						uoHeader.print();
						headers.add(uoHeader);
						break;
					default:
						break;
					}

					break;
				}
				default:
					logger.warning("Unknown Header");
					throw new RarException(RarExceptionType.notRarArchive);

				}
			}
			// logger.info("\n--------end header--------");
		}
	}
}
