package mobilepad.io.message.control;

public class KeyEvent extends ControlEvent
{
	public int key;
	public boolean press;
	public boolean release;


	public KeyEvent(int key) {
		super(Type.KEY);
		this.key = key;
		press = true;
		release = true;
	}


	public KeyEvent(int key, boolean press, boolean release) {
		super(Type.KEY);
		this.key = key;
		this.press = press;
		this.release = release;
	}
}
