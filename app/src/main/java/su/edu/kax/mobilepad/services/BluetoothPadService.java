package su.edu.kax.mobilepad.services;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import su.edu.kax.mobilepad.Constants;
import su.edu.kax.mobilepad.fragments.CommandControllFragment;
import su.edu.kax.mobilepad.fragments.MouseControllFragment;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothPadService {

    public static final int STATE_NONE=0;
    public static final int STATE_LISTEN=1;
    public static final int STATE_CONNECTING=2;
    public static final int STATE_CONNECTED=3;


    private static final String TAG = "BluetoothPadService";
    private static final UUID MY_UUID_SECURE =
            UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");


    private Handler mHandler = null;
    private ConnectedThread mConnectedThread;
    private int mState;
    private Context context;
    private BluetoothSocket bluetoothSocket;


    public BluetoothPadService(Context context, Handler handler) {
        mHandler = handler;
        this.context=context;
        MouseControllFragment.handler=commandHandler;
        CommandControllFragment.handler=commandHandler;
    }

    public synchronized int getState() {
        return mState;
    }



    public synchronized void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "start");

                if (mConnectedThread != null) {
                    mConnectedThread.cancel();
                    mConnectedThread = null;
                }

                mConnectedThread=new ConnectedThread(bluetoothSocket);
                mConnectedThread.start();
                updateUserInterfaceTitle();
            }
        }).start();

    }

    public synchronized void stop(){
        if(mConnectedThread!=null){
            mConnectedThread.cancel();
            mState=STATE_NONE;
        }
        updateUserInterfaceTitle();
    }

    public void setBluetoothSocket(BluetoothSocket bluetoothSocket) {
        this.bluetoothSocket = bluetoothSocket;
    }

    private void connectionLost() {
        Message msg = mHandler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, "Device connection was lost");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        mConnectedThread.cancel();
        mState = STATE_NONE;
        updateUserInterfaceTitle();
    }

    private void updateUserInterfaceTitle() {
        mHandler.obtainMessage(Constants.MESSAGE_STATE_CHANGE,mState).sendToTarget();
    }


    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "create ConnectedThread: ");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
            mState = STATE_CONNECTED;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;


            while (mState == STATE_CONNECTED) {
                try {
                    bytes = mmInStream.read(buffer);
                    mHandler.obtainMessage(Constants.MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget();
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    break;
                }catch (NullPointerException e){
                    Log.e(TAG,"Nie udalo sie tworzenie kanalu");
                    break;
                }
            }
            connectionLost();
            Log.d(TAG,"Koncze polaczenia");
        }


        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);
                mHandler.obtainMessage(Constants.MESSAGE_WRITE, -1, -1, buffer)
                        .sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }


        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }


    public Handler getCommandHandler() {
        return commandHandler;
    }

    private Handler commandHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.COMMAND_COMMAND:
                    Toast.makeText(context,"Komenda",Toast.LENGTH_LONG).show();
//                    switch (msg.arg1) {
//
//                    }
                    break;
                case Constants.COMMAND_MOUSE_MOVE:
                    Toast.makeText(context,"Ruch myszka",Toast.LENGTH_LONG).show();
//                    switch (msg.arg1) {
//
//                    }
                    break;
            }
        }
    };


}
