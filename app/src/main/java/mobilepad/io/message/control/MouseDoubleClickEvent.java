package mobilepad.io.message.control;

public class MouseDoubleClickEvent extends ControlEvent
{
    public int button;

    public MouseDoubleClickEvent() {
        super(Type.DOUBLE_CLICK);
        this.button = MouseButtonEvent.Button.LEFT;
    }

    public MouseDoubleClickEvent(int button) {
        super(Type.DOUBLE_CLICK);
        this.button = button;
    }
}
