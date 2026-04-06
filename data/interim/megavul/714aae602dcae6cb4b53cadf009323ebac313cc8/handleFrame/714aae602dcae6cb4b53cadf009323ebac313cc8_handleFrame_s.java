class handleFrame {
public void handleFrame(Frame frame) throws IOException {
        AMQCommand command = _command;
        if (command.handleFrame(frame)) { // a complete command has rolled off the assembly line
            _command = new AMQCommand(); // prepare for the next one
            handleCompleteInboundCommand(command);
        }
    }
}
