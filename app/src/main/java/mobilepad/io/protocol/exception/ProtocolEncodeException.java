package mobilepad.io.protocol.exception;

import java.io.IOException;

public class ProtocolEncodeException extends IOException
{
	public ProtocolEncodeException() {
	}


	public ProtocolEncodeException(String message) {
		super(message);
	}


	public ProtocolEncodeException(String message, Throwable cause) {
		super(message, cause);
	}


	public ProtocolEncodeException(Throwable cause) {
		super(cause);
	}
}
