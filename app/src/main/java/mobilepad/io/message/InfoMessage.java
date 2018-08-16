package mobilepad.io.message;

public class InfoMessage extends Message
{
	public String text;


	public InfoMessage(String text) {
		super(Type.INFO);
		this.text = text;
	}


	public InfoMessage() {
		super(Type.INFO);
		this.text = "";
	}
}
