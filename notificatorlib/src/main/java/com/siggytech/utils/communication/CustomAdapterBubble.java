package com.siggytech.utils.communication;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


public class CustomAdapterBubble extends BaseAdapter {

    private List<ChatModel> lstChat;
    private Context context;
    private LayoutInflater inflater;

    public CustomAdapterBubble(List<ChatModel> lstChat, Context context) {
        this.lstChat = lstChat;
        this.context = context;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return lstChat.size();
    }

    @Override
    public Object getItem(int position) {
        return lstChat.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;

        ViewHolder holder;

        if (convertView == null) {

            vi = inflater.inflate(R.layout.talk_cell_layout, null);

            holder = new ViewHolder();

            holder.text_cell = vi.findViewById(R.id.text_cell);
            holder.image = vi.findViewById(R.id.cell_icon);

            vi.setTag(holder);
        }
        else
            holder = (ViewHolder)vi.getTag();

        holder.text_cell.setText(lstChat.get(position).getMessage());
        holder.image.setImageResource(R.drawable.ic_launcher_round);


        return vi;
    }

    private Bitmap getFromByteArray(byte[] bytes){
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
    class ViewHolder {

        TextView text_cell;
        ImageView image;

    }

}