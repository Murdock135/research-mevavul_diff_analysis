class decodeXid {
public static Xid decodeXid(final ActiveMQBuffer in) {
      int formatID = in.readInt();
      byte[] bq = new byte[in.readInt()];
      in.readBytes(bq);
      byte[] gtxid = new byte[in.readInt()];
      in.readBytes(gtxid);
      return new XidImpl(bq, formatID, gtxid);
   }
}
