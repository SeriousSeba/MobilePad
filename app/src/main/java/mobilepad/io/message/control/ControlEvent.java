package mobilepad.io.message.control;


import mobilepad.io.message.Message;

public abstract class ControlEvent extends Message
{
	public ControlEvent(Type type) {
		super(type);
	}
}
