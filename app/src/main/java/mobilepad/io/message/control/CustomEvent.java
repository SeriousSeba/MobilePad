package mobilepad.io.message.control;

public class CustomEvent extends ControlEvent
{
	public int id;
	public Object argument;


	public CustomEvent(int id){
		super(Type.CUSTOM);
		this.id= id;
		this.argument = null;
	}

	public CustomEvent(int id, Object argument) {
		super(Type.CUSTOM);
		this.id = id;
		this.argument = argument;
	}
}
