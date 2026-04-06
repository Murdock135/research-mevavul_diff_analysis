class executeFixedCostOperation_1 {
@Override
  public Operation.OperationResult executeFixedCostOperation(
      final MessageFrame frame, final EVM evm) {
    Bytes shiftAmount = frame.popStackItem();
    if (shiftAmount.size() > 4 && (shiftAmount = shiftAmount.trimLeadingZeros()).size() > 4) {
      frame.popStackItem();
      frame.pushStackItem(UInt256.ZERO);
    } else {
      final int shiftAmountInt = shiftAmount.toInt();
      final Bytes value = leftPad(frame.popStackItem());

      if (shiftAmountInt >= 256 || shiftAmountInt < 0) {
        frame.pushStackItem(UInt256.ZERO);
      } else {
        frame.pushStackItem(value.shiftRight(shiftAmountInt));
      }
    }
    return successResponse;
  }
}
