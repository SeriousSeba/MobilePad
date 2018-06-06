package su.edu.kax.mobilepad.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.*;
import in.championswimmer.sfg.lib.SimpleFingerGestures;
import su.edu.kax.mobilepad.R;

public class MouseControllFragment extends Fragment{

    private final String DEBUG_TAG="MOUSE_DEBUG";
    public static Handler handler;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.mouse_layout, container, false);
        SimpleFingerGestures simpleFingerGestures=new SimpleFingerGestures();
        simpleFingerGestures.setOnFingerGestureListener(new SimpleGestures());
        view.setOnTouchListener(simpleFingerGestures);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(DEBUG_TAG,"Clicked");
            }
        });
//        mDetector = new GestureDetector(R.layout.mouse_layout, this);
//        mDetector.setOnDoubleTapListener(this);

        return view;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }


    private class SimpleGestures implements SimpleFingerGestures.OnFingerGestureListener {
        @Override
        public boolean onSwipeUp(int fingers, long gestureDuration, double gestureDistance) {
            Log.d(DEBUG_TAG,"swiped " + fingers + " up");
            return true;
        }

        @Override
        public boolean onSwipeDown(int fingers, long gestureDuration, double gestureDistance) {
            Log.d(DEBUG_TAG,"swiped " + fingers + " down");
            return true;
        }

        @Override
        public boolean onSwipeLeft(int fingers, long gestureDuration, double gestureDistance) {
            Log.d(DEBUG_TAG,"swiped " + fingers + " left");
            return true;
        }

        @Override
        public boolean onSwipeRight(int fingers, long gestureDuration, double gestureDistance) {
            Log.d(DEBUG_TAG,"swiped " + fingers + " right");
            return true;
        }

        @Override
        public boolean onPinch(int fingers, long gestureDuration, double gestureDistance) {
            Log.d(DEBUG_TAG,"pinch");
            return true;
        }

        @Override
        public boolean onUnpinch(int fingers, long gestureDuration, double gestureDistance) {
            Log.d(DEBUG_TAG,"unpinch");
            return true;
        }


        @Override
        public boolean onDoubleTap(int fingers) {
            return true;
        }
    }





//    public boolean onTouchEvent(MotionEvent event) {
//        if (this.mDetector.onTouchEvent(event)) {
//            return true;
//        }
//        return getActivity().onTouchEvent(event);
//    }
//
//
//
//    class MouseTouchListener implements View.OnTouchListener{
//
//        @Override
//        public boolean onTouch(View v, MotionEvent event) {
//
//            MotionEvent.ActionMo
//            return false;
//        }
//    }
//
//
//    @Override
//    public boolean onDown(MotionEvent event) {
//        Log.d(DEBUG_TAG, "onDown: " + event.toString());
//        return true;
//    }
//
//    @Override
//    public boolean onFling(MotionEvent event1, MotionEvent event2,
//                           float velocityX, float velocityY) {
//        Log.d(DEBUG_TAG, "onFling: " + event1.toString() + event2.toString());
//
//        if (velocityX > 30 || velocityY > 30) {
//            String elo = "nie wiadomo gdzie";
//            if (Math.abs(velocityX) > Math.abs(velocityY))
//                elo = "w poziomie";
//            else
//                elo = "w pionie";
//            toast.cancel();
//            toast = Toast.makeText(getActivity(), "Machniecie " + elo, Toast.LENGTH_SHORT);
//            toast.show();
//            return false;
//        }
//        return true;
//    }
//
//    @Override
//    public void onLongPress(MotionEvent event) {
//        toast.cancel();
//        toast = Toast.makeText(getActivity(), "Dlugie przytrzymanie", Toast.LENGTH_SHORT);
//        toast.show();
//    }
//
//    @Override
//    public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX,
//                            float distanceY) {
//        Log.d(DEBUG_TAG, "onScroll: " + event1.toString() + event2.toString());
////        if(distanceX>10 || distanceY>10)
////        {
////       String elo="nie wiadomo gdzie";
////       if(distanceX>distanceY)
////           elo="w poziomie";
////       else
////           elo="w pionie";
////       toast.cancel();
////        toast=Toast.makeText(getApplicationContext(),"Scrollowanie "+elo,Toast.LENGTH_SHORT);
////        toast.show();
////        return true;}
//
//        return true;
//
//    }
//
//    @Override
//    public void onShowPress(MotionEvent event) {
//        Log.d(DEBUG_TAG, "onShowPress: " + event.toString());
//    }
//
//    @Override
//    public boolean onSingleTapUp(MotionEvent event) {
//        Log.d(DEBUG_TAG, "onSingleTapUp: " + event.toString());
//        return true;
//    }
//
//    @Override
//    public boolean onDoubleTap(MotionEvent event) {
//        Log.d(DEBUG_TAG, "onDoubleTap: " + event.toString());
//        toast.cancel();
//        toast = Toast.makeText(getActivity(), "Podwojne klikniecie", Toast.LENGTH_SHORT);
//        toast.show();
//        return true;
//    }
//
//    @Override
//    public boolean onDoubleTapEvent(MotionEvent event) {
//        toast.cancel();
//        toast = Toast.makeText(getActivity(), "Podwojne klikniecie", Toast.LENGTH_SHORT);
//        toast.show();
//        return true;
//    }
//
//    @Override
//    public boolean onSingleTapConfirmed(MotionEvent event) {
//        toast.cancel();
//        toast = Toast.makeText(getActivity(), "Pojedyncze klikniecie", Toast.LENGTH_SHORT);
//        toast.show();
//        return true;
//    }

}
