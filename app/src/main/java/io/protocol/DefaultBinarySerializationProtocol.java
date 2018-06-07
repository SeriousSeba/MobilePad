package io.protocol;



import io.protocol.exception.ProtocolDecodeException;

import java.io.*;

public class DefaultBinarySerializationProtocol implements Protocol
{
	@Override
	public <T> T decode(InputStream in) throws IOException {
		try {
			return (T) new ObjectInputStream(in).readObject();
		}
		catch (ClassNotFoundException e) {
			throw new ProtocolDecodeException("Received object could not be decoded as expected type; " + e.getMessage());
		}
		catch (ClassCastException e){
			throw new ProtocolDecodeException("Could not decode data because of invalid class name; " + e.getMessage());
		}
	}


	@Override
	public <T> void encode(T o, OutputStream out) throws IOException {
		new ObjectOutputStream(out).writeObject(o);
	}
}
