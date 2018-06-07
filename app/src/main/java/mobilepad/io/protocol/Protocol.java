package mobilepad.io.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Protocol
{
	<T> T decode(InputStream in) throws IOException;
	<T> void encode(T o, OutputStream out) throws IOException;
}
