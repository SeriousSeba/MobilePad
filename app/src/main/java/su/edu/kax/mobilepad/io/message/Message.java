package su.edu.kax.mobilepad.io.message;


import java.io.Serializable;

public class Message implements Serializable
{
	public enum Type{
		COMMAND(0), CUSTOM(1), DISCONNECT(2), KEY_COMBINATION(3), KEY(4), MOUSE_BUTTON(5), MOUSE_MOVE(6), INFO(7);

		private final int value;
		private Type(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	public enum MouseButton{
		SINGLE_CLICK(0),DOUBLE_CLICK(1);

		private final int value;
		private MouseButton(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	public enum CommandType{
		CHUJ(0);

		private final int value;
		private CommandType(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}




	public int type;
	private int[] arguments;


	public Message(int type,int[] arguments) {
		this.type = type;
		this.arguments=arguments;
	}
}
