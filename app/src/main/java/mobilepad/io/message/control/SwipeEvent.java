package mobilepad.io.message.control;

public class SwipeEvent extends ControlEvent
{
	public int fingers;
	public long duration;
	public double distance;

	public SwipeEvent() {
		super(Type.SWIPE);
	}


	public SwipeEvent(int fingers, long duration, double distance) {
		super(Type.SWIPE);
		this.fingers = fingers;
		this.duration = duration;
		this.distance = distance;
	}
}
