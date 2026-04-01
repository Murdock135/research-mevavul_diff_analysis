class executeFixedCostOperation {
@Override
  public Operation.OperationResult executeFixedCostOperation(
      final MessageFrame frame, final EVM evm) {

    Bytes shiftAmount = frame.popStackItem();
    final Bytes value = leftPad(frame.popStackItem());
    final boolean negativeNumber = value.get(0) < 0;
    if (shiftAmount.size() > 4 && (shiftAmount = shiftAmount.trimLeadingZeros()).size() > 4) {
      frame.pushStackItem(negativeNumber ? ALL_BITS : UInt256.ZERO);
    } else {
      final int shiftAmountInt = shiftAmount.toInt();

      if (shiftAmountInt >= 256 || shiftAmountInt < 0) {
        frame.pushStackItem(negativeNumber ? ALL_BITS : UInt256.ZERO);
      } else {
        // first perform standard shift right.
        Bytes result = value.shiftRight(shiftAmountInt);

        // if a negative number, carry through the sign.
        if (negativeNumber) {
          final Bytes32 significantBits = ALL_BITS.shiftLeft(256 - shiftAmountInt);
          result = result.or(significantBits);
        }
        frame.pushStackItem(result);
      }
    }
    return successResponse;
  }
}
