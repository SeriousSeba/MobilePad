package io.message.control;

import io.message.Message;

public class CommandEvent extends ControlEvent
{
	public String command;
	public boolean runThread;


	public CommandEvent(String command){
		super(Message.Type.COMMAND);
		this.command = command;
		this.runThread = false;
	}


	public CommandEvent(String command, boolean runThread) {
		super(Message.Type.COMMAND);
		this.command = command;
		this.runThread = runThread;
	}
}
