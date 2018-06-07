package su.edu.kax.mobilepad.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.*;
import in.championswimmer.sfg.lib.SimpleFingerGestures;
import su.edu.kax.mobilepad.R;
import su.edu.kax.mobilepad.io.message.Message;

public class MouseControllFragment extends Fragment {

    private final String DEBUG_TAG="MOUSE_DEBUG";
    private GestureDetector gestureDetector;
    public static Handler handler;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.mouse_layout, container, false);
        MyGestureListener myGestureListener=new MyGestureListener();
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


//        mDetector = new GestureDetector(R.layout.mouse_layout, this);
//        mDetector.setOnDoubleTapListener(this);

        return view;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }




//    public boolean onTouchEvent(MotionEvent event) {
//        if (this.mDetector.onTouchEvent(event)) {
//            return true;
//        }
//        return getActivity().onTouchEvent(event);
//    }


    class MyGestureListener implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener{


        @Override
        public boolean onDown(MotionEvent event) {
            Log.d(DEBUG_TAG, "onDown: " + event.toString());
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            //Log.d(DEBUG_TAG, "onFling: " + event1.toString() + event2.toString());

            if (velocityX > 30 || velocityY > 30) {
                String elo = "nie wiadomo gdzie";
                if (Math.abs(velocityX) > Math.abs(velocityY))
                    elo = "w poziomie";
                else
                    elo = "w pionie";
                return false;
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent event) {

        }

        @Override
        public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX,
                                float distanceY) {
            //Log.d(DEBUG_TAG, "onScroll: " + event1.toString() + event2.toString());
//            Log.d(DEBUG_TAG,"Po osi X "+distanceX);
//            Log.d(DEBUG_TAG,"Po osi Y "+distanceY);

            int[] arguments=new int[]{(int)distanceX,(int)distanceY};
            int type=Message.Type.MOUSE_MOVE.getValue();
            Message message=new Message(type,arguments);
            android.os.Message msg=handler.obtainMessage(type);
            msg.obj=message;
            msg.sendToTarget();
            return true;

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
            //Log.d(DEBUG_TAG,"Podwojne klikniecie");
            int[] arguments=new int[]{Message.MouseButton.DOUBLE_CLICK.getValue()};
            int type=Message.Type.MOUSE_BUTTON.getValue();
            Message message=new Message(type,arguments);
            android.os.Message msg=handler.obtainMessage(type);
            msg.obj=message;
            msg.sendToTarget();
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            //Log.d(DEBUG_TAG,"Pojedyncze klikniecie");
            int[] arguments=new int[]{Message.MouseButton.SINGLE_CLICK.getValue()};
            int type=Message.Type.MOUSE_BUTTON.getValue();
            Message message=new Message(type,arguments);
            android.os.Message msg=handler.obtainMessage(type);
            msg.obj=message;
            msg.sendToTarget();
            return true;
        }

    }

}
