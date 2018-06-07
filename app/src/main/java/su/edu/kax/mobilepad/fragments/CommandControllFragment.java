package su.edu.kax.mobilepad.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import su.edu.kax.mobilepad.Constants;
import su.edu.kax.mobilepad.R;

public class CommandControllFragment extends Fragment {

    private MessageAdapter messageAdapter;
    public static Handler handler;

    private CheckBox checkBox;
    private Button button;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.command_layout,container,false);
        final GridView gridView=(GridView) view.findViewById(R.id.gridview);

        checkBox=(CheckBox)view.findViewById(R.id.sequenceKeys);
        button=(Button)view.findViewById(R.id.sendButton);

        messageAdapter=new MessageAdapter(getActivity());
        gridView.setAdapter(messageAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handler.obtainMessage(Constants.COMMAND_COMMAND).sendToTarget();
            }
        });

        return view;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }


    class MessageAdapter extends BaseAdapter {
        private Context mContext;

        public MessageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return mThumbIds.length;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return 0;
        }


        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView;
            if (convertView == null) {
                textView = new TextView(mContext);
                textView.setLayoutParams(new ViewGroup.LayoutParams(85, 85));
                textView.setPadding(8, 8, 8, 8);
            } else {
                textView = (TextView) convertView;
            }

            textView.setText(mThumbIds[position]);
            return textView;
        }


        private String[] mThumbIds = {
                "Alt","Tab",
                "Space","Enter"
        };
    }
}
