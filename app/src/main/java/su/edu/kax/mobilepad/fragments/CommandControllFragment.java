package su.edu.kax.mobilepad.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import mobilepad.io.message.control.KeyCombinationEvent;
import mobilepad.io.message.control.KeyEvent;
import su.edu.kax.mobilepad.Constants;
import su.edu.kax.mobilepad.R;

import java.util.LinkedList;
import java.util.List;

/**
 * Fragmend responsible for managing avaiable commands to be properly placed
 * showed and handled upon user interaction with GUI
 */
public class CommandControllFragment extends Fragment {

    private MessageAdapter messageAdapter;
    public static Handler handler;

    private CheckBox checkBox;
    private Button button;
    private boolean checked=false;

    private List<Integer> sequence=new LinkedList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.command_layout,container,false);
        final GridView gridView=(GridView) view.findViewById(R.id.gridview);

        checkBox=(CheckBox)view.findViewById(R.id.sequenceKeys);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){
                    sequence.clear();
                    checked=false;
                    messageAdapter.notifyDataSetInvalidated();

                }else{
                    checked=true;
                }
            }
        });

        button=(Button)view.findViewById(R.id.sendButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sequence.size()!=0) {
                    KeyCombinationEvent keyCombinationEvent = new KeyCombinationEvent();
                    int[] tab = getInt();
                    keyCombinationEvent.keys = tab;
                    Message message = handler.obtainMessage();
                    message.obj = keyCombinationEvent;
                    message.sendToTarget();
                    checkBox.setChecked(false);
                    messageAdapter.notifyDataSetInvalidated();
                }
            }
        });


        messageAdapter=new MessageAdapter(getActivity());
        gridView.setAdapter(messageAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(checked){
                    Integer result = new Integer(Constants.keysValues[position]);
                    TextView view1 = (TextView) view;
                    if(!sequence.contains(result)) {
                        sequence.add(result);
                        view1.setBackgroundColor(R.color.colorWhite);
                        parent.invalidate();

                    }else {
                        sequence.remove(result);
                        view1.setBackgroundColor(0);
                        parent.invalidate();
                    }
                }else {
                    KeyEvent keyEvent = new KeyEvent(Constants.keysValues[position]);
                    Message message=handler.obtainMessage();
                    message.obj=keyEvent;
                    message.sendToTarget();
                }

            }
        });

        return view;
    }


    /**
     * @return Returns gathered sequence keys picked by user
     */
    private int[] getInt(){
        int[] tab=new int[sequence.size()];
        for(int i = 0; i<sequence.size(); i++){
            tab[i]=sequence.get(i).intValue();
        }
        sequence.clear();
        return tab;
    }

    /**
     * Adapter taking care of GridView
     * Coresponds with Constand table for proper command typing
     */
    class MessageAdapter extends BaseAdapter {
        private Context mContext;

        public MessageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return mThumbIds.length;
        }

        public Object getItem(int position) {

            return position+3;
        }

        public long getItemId(int position) {

            return 0;
        }


        private String[] mThumbIds = {
                "ESC", "CTRL", "BACK",
                "TAB", "SHIFT", "ENTER",
                "CAPS", "ALT", "SPACE",
                "END", "DEL", "INSERT"
        };

        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView;
            if (convertView == null) {
                textView = new TextView(mContext);
                textView.setLayoutParams(new ViewGroup.LayoutParams(120, 120));
                textView.setPadding(8, 8, 8, 8);
            } else {
                textView = (TextView) convertView;
            }
            if(!checked)
                textView.setBackgroundColor(0);
            textView.setTextSize(10);
            textView.setText(mThumbIds[position]);
            return textView;
        }
    }


}
