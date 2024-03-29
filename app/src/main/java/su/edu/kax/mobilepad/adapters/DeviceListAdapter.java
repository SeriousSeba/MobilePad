package su.edu.kax.mobilepad.adapters;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import su.edu.kax.mobilepad.R;

import java.util.ArrayList;

/**
 * Adapter with Bluetooth devices used for listing found devices in ConnectionActivity
 * Contains devices infromations stored in BluetoothDevice class
 */
public class DeviceListAdapter extends BaseAdapter {
    private ArrayList<BluetoothDevice> mLeDevices;
    private LayoutInflater mInflator;


    public DeviceListAdapter(Activity parentActivity) {
        super();
        mLeDevices = new ArrayList<>();
        mInflator = parentActivity.getLayoutInflater();
    }


    public void addDevice(BluetoothDevice device) {
        if(!mLeDevices.contains(device)) {
            mLeDevices.add(device);
        }
    }


    public BluetoothDevice getDevice(int position) {
        return mLeDevices.get(position);
    }


    public void clear() {
        mLeDevices.clear();
    }

    @Override
    public int getCount() {
        return mLeDevices.size();
    }

    @Override
    public Object getItem(int i) {
        return mLeDevices.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = mInflator.inflate(R.layout.list_item_device, null);
            viewHolder = new ViewHolder();
            viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
            viewHolder.deviceName =  (TextView) view.findViewById(R.id.device_name);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        BluetoothDevice device = mLeDevices.get(i);
        final String deviceName = device.getName();
        if (deviceName != null && deviceName.length() > 0)
            viewHolder.deviceName.setText(deviceName);
        else
            viewHolder.deviceName.setText(R.string.error_unknown_device);
        viewHolder.deviceAddress.setText(device.getAddress());
        return view;
    }

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }

}
