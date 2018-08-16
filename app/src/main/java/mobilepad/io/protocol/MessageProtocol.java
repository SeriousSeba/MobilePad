package mobilepad.io.protocol;



import mobilepad.io.message.InfoMessage;
import mobilepad.io.message.Message;
import mobilepad.io.message.control.CommandEvent;
import mobilepad.io.message.control.CustomEvent;
import mobilepad.io.protocol.exception.ProtocolDecodeException;
import mobilepad.io.protocol.exception.ProtocolEncodeException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class MessageProtocol implements Protocol
{
	private static final int BUFFER_SIZE = 128;
	private ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
	private byte[] data = new byte[BUFFER_SIZE];


	private void flush(OutputStream out) throws IOException {
		out.write(buffer.array());
		out.flush();
		buffer.clear();
	}

	private void read(byte[] a, InputStream in, int size) throws IOException {
		int bytesRead = 0;
		int n = 0;
		while (bytesRead < size){
			n = in.read(a, bytesRead, size - bytesRead);
			if (n == -1)
				break;
			else
				bytesRead += n;
		}
	}

	private void read(byte[] a, InputStream in) throws IOException {
		read(a, in, a.length);
	}


	private void encodeString(String s) throws IOException{
		byte[] data = s.getBytes();
		buffer.putInt(data.length);
		buffer.put(data);
	}

	private String decodeString(InputStream in) throws IOException {
		int size = decodeInt(in);
		byte[] strBytes = new byte[size];
		read(strBytes, in);
		return new String(strBytes, "UTF-8");
	}

	private void encodeInt(int i) throws IOException{
		buffer.putInt(i);
	}

	private int decodeInt(InputStream in) throws IOException{
		read(data, in, 4);
		return ByteBuffer.wrap(data).getInt();
	}

	@Override
	public <T> T decode(InputStream in) throws IOException {
		System.out.println("available: " + in.available());
		Message.Type type = Message.Type.fromOrdinal(decodeInt(in));
		switch (type){
			case COMMAND:
				break;
			case CUSTOM:
				break;
			case DISCONNECT:
				break;
			case KEY_COMBINATION:
				break;
			case KEY:
				break;
			case MOUSE_BUTTON:
				break;
			case MOUSE_MOVE:
				break;
			case SWIPE:
				break;
			case INFO:
				return (T) new InfoMessage(decodeString(in));
		}
		throw new ProtocolDecodeException("Invalid type in received message");
	}


	@Override
	public <T> void encode(T o, OutputStream out) throws IOException {
		Message message;
		try{
			message = (Message)o;
		} catch (ClassCastException e){
			throw new ProtocolEncodeException("Encoded object was not subclass of Message", e);
		}
		encodeInt(message.type.ordinal());
		switch (message.type){
			case COMMAND: {
				CommandEvent m = (CommandEvent) message;
				buffer.put(m.runThread ? (byte) 1 : (byte) 0);
				buffer.put(m.command.getBytes());
				break;
			}
			case CUSTOM: {
				CustomEvent m = (CustomEvent) message;
				encodeInt(m.id);
				break;
			}
			case DISCONNECT:
				break;
			case KEY_COMBINATION:
				break;
			case KEY:
				break;
			case MOUSE_BUTTON:
				break;
			case MOUSE_MOVE:
				break;
			case SWIPE:
				break;
			case INFO:
				InfoMessage m = (InfoMessage)message;
				encodeString(m.text);
				break;
		}
		flush(out);
	}
}
