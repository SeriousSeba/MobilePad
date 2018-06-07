package io.message.control;


import io.message.Message;

public abstract class ControlEvent extends Message
{
	public ControlEvent(Type type) {
		super(type);
	}
}
