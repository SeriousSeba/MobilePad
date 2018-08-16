package mobilepad.io.message;


import java.io.Serializable;

public abstract class Message implements Serializable
{
	public enum Type{
		COMMAND, CUSTOM, DISCONNECT, KEY_COMBINATION, KEY, MOUSE_BUTTON, MOUSE_MOVE, SWIPE, INFO, DOUBLE_CLICK;

		public static final Type[] values = values();

		public static Type fromOrdinal(int o){
			return values[o];
		}
	}

	public Type type;

	public Message(Type type) {
		this.type = type;
	}
}
