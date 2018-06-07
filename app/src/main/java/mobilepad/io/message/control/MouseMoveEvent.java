package mobilepad.io.message.control;

import mobilepad.io.message.Message;

public class MouseMoveEvent extends ControlEvent
{
	public int x;
	public int y;


	public MouseMoveEvent(int x, int y) {
		super(Message.Type.MOUSE_MOVE);
		this.x = x;
		this.y = y;
	}
}
