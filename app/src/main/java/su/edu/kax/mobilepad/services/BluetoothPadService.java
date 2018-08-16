package su.edu.kax.mobilepad.services;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import mobilepad.io.protocol.DefaultBinarySerializationProtocol;
import su.edu.kax.mobilepad.Constants;
import su.edu.kax.mobilepad.fragments.CommandControllFragment;
import su.edu.kax.mobilepad.fragments.MouseControllFragment;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Service taking care of managing thread that sends and receives data from bluetooth device
 */
public class BluetoothPadService {

    public static final int STATE_NONE=0;
    public static final int STATE_LISTEN=1;
    public static final int STATE_CONNECTING=2;
    public static final int STATE_CONNECTED=3;


    private static final String TAG = "BluetoothPadService";

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


    /**
     * Handler responsible for trapnsporting events between user interaction with application GUI
     * and Connection thread writing serialized data to device
     */
    @SuppressLint("HandlerLeak")
    private Handler commandHandler = new Handler() {
        private DefaultBinarySerializationProtocol serializationProtocol = new DefaultBinarySerializationProtocol();

        @Override
        public void handleMessage(Message msg) {
            Object message = msg.obj;
            try {
                serializationProtocol.encode(message, mConnectedThread.getMmOutStream());
                mConnectedThread.getMmOutStream().flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.i(TAG, "Pomyslnie wpisano");

        }
    };

    /**
     * Starts new connection thread based on given BluetoothSocket
     * If previous thread is still running function terminates it
     */
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

    public void setBluetoothSocket(BluetoothSocket bluetoothSocket) {
        this.bluetoothSocket = bluetoothSocket;
    }

    /**
     * Stops running connection thread and closes socket
     */
    public synchronized void stop(){
        if(mConnectedThread!=null){
            mConnectedThread.cancel();
            mState=STATE_NONE;
        }
        try {
            if (bluetoothSocket != null)
                bluetoothSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        updateUserInterfaceTitle();
    }

    /**
     * Function called upon losted connection
     * Sends message to GUI handler and sets connection state
     */
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

    /**
     * Sends request to GUI for interface update
     */
    private void updateUserInterfaceTitle() {
        mHandler.obtainMessage(Constants.MESSAGE_STATE_CHANGE,mState).sendToTarget();
    }

    /**
     * Thread responsible for communicating with paired bluetooth device
     * Sends serialized objects thorough socket to device RX server
     */
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


        public OutputStream getMmOutStream() {
            return mmOutStream;
        }

        /**
         * Writes givenn buffer to input stream of bluetooth socket
         *
         * @param buffer Byte buffer to be written
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);
                mHandler.obtainMessage(Constants.MESSAGE_WRITE, -1, -1, buffer)
                        .sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }


        /**
         * Cancels connection between application and device server
         */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }


}
