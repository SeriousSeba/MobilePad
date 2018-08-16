package su.edu.kax.mobilepad.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.*;
import mobilepad.io.message.control.MouseDoubleClickEvent;
import mobilepad.io.message.control.MouseMoveEvent;
import su.edu.kax.mobilepad.R;

/**
 * Fragmend responsible for setting GUI and its Listeners awaiting for user interaction
 * with touchpad
 */
public class MouseControllFragment extends Fragment {

    private final String DEBUG_TAG="MOUSE_DEBUG";
    private GestureDetector gestureDetector;
    public static Handler handler;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.mouse_layout, container, false);
        final MyGestureListener myGestureListener = new MyGestureListener();
        gestureDetector=new GestureDetector(getActivity(),myGestureListener);
        gestureDetector.setOnDoubleTapListener(myGestureListener);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (gestureDetector.onTouchEvent(event)) {
                return true;
                }
            return getActivity().onTouchEvent(event);
            }
        });

//        Button buttonLeft=(Button)view.findViewById(R.id.button_left);
//        Button buttonMiddle=(ToggleButton)view.findViewById(R.id.button_midddle);
//        Button buttonRight=(Button)view.findViewById(R.id.button_right);
//
//        buttonLeft.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mobilepad.io.message.control.KeyEvent message=new mobilepad.io.message.control.KeyEvent(401);
//                android.os.Message msg=handler.obtainMessage();
//                msg.obj=message;
//                msg.sendToTarget();
//            }
//        });
//
//        buttonMiddle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                MouseButtonEvent message=new MouseButtonEvent(2048,true,false);
//                android.os.Message msg=handler.obtainMessage();
//                msg.obj=message;
//                msg.sendToTarget();
//            }
//        });
//
//        buttonRight.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                MouseButtonEvent message=new MouseButtonEvent(4096,true,false);
//                android.os.Message msg=handler.obtainMessage();
//                msg.obj=message;
//                msg.sendToTarget();
//            }
//        });



        return view;
    }


    /**
     * Class responsible for awaiting for user interactions and calculating them into properly formated data
     * Uses handler to send data to connection thread
     */
    class MyGestureListener implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener{

        private long previous = 0;
        private float x = 0;
        private float y = 0;
        private int times = 0;

        /**
         * Send MouseMoveEven based upon gathered data in previous moves of user
         */
        public void sendCoordinates() {
            MouseMoveEvent message = new MouseMoveEvent(2 * -(int) x, 2 * -(int) y);
            android.os.Message msg = handler.obtainMessage();
            msg.obj = message;
            msg.sendToTarget();
            x = 0;
            y = 0;
        }

        @Override
        public boolean onDown(MotionEvent event) {
            Log.d(DEBUG_TAG, "onDown: " + event.toString());
            return true;
        }


        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {

            return true;
        }

        @Override
        public void onLongPress(MotionEvent event) {

        }

        @Override
        public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX,
                                float distanceY) {

            x += distanceX;
            y += distanceY;

            long time = previous != 0 ? previous : event1.getEventTime();
            time = event2.getEventTime() - time;
            times++;

            if (times < 5 && time < 65) {
                return true;
            } else {
                previous = event2.getEventTime();
                times = 0;
                sendCoordinates();
                return true;
            }

        }

        @Override
        public void onShowPress(MotionEvent event) {
            Log.d(DEBUG_TAG, "onShowPress: " + event.toString());
        }

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            Log.d(DEBUG_TAG, "onSingleTapUp: " + event.toString());
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent event) {
            Log.d(DEBUG_TAG, "onDoubleTap: " + event.toString());
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent event) {
            MouseDoubleClickEvent message = new MouseDoubleClickEvent();
            android.os.Message msg=handler.obtainMessage();
            msg.obj=message;
            msg.sendToTarget();
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            return true;
        }

    }

}
