package mobilepad.io.message.control;

public class KeyCombinationEvent extends ControlEvent
{
	public int[] keys;
	public boolean press;
	public boolean release;


	public KeyCombinationEvent() {
		super(Type.KEY_COMBINATION);
		keys = null;
	}


	public KeyCombinationEvent(int... keys) {
		super(Type.KEY_COMBINATION);
		this.keys = keys;
		this.press = true;
		this.release = true;
	}


	public KeyCombinationEvent(int[] keys, boolean press, boolean release) {
		super(Type.KEY_COMBINATION);
		this.keys = keys;
		this.press = press;
		this.release = release;
	}
}
