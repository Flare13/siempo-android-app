package co.minium.launcher3.notification;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import co.minium.launcher3.R;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by itc on 20/02/17.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    private Context mContext;
    private List<Notification> notificationList;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView _name, _text,_time;
        public ImageView overflow;
        public CircleImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            _name = (TextView) view.findViewById(R.id.text_name_notification);
            _text = (TextView) view.findViewById(R.id.text_mesage_notification);
            _time = (TextView) view.findViewById(R.id.text_time_notification);
            thumbnail = (CircleImageView) view.findViewById(R.id.thumbnail_notification);
            overflow = (ImageView) view.findViewById(R.id.image_checked_notification);
        }

        @Override
        public void onClick(View v) {
            System.out.println("Recycle view is clicked");
            _name.setVisibility(View.INVISIBLE);
            _text.setVisibility(View.INVISIBLE);
            _time.setVisibility(View.INVISIBLE);
            thumbnail.setVisibility(View.INVISIBLE);
            overflow.setVisibility(View.VISIBLE);
         //   v.setBackgroundColor(mContext.getColor(R.color.white));
        }
    }


    public NotificationAdapter(Context mContext, List<Notification> notificationList){
        this.mContext = mContext;
        this.notificationList = notificationList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_card,parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Notification notification  = notificationList.get(position);
        holder._name.setText(notification.get_name());
        holder._text.setText(notification.get_text());
        holder._time.setText(notification.get_time());
        holder.thumbnail.setImageResource(notification.get_image());

        // loading album cover using Glide library
//        Glide.with(mContext).load(album.getThumbnail()).into(holder.thumbnail);

    }



    @Override
    public int getItemCount() {
        return notificationList.size();
    }
}