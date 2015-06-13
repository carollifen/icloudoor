package com.icloudoor.cloudoor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.icloudoor.cloudoor.SlideView;
import com.icloudoor.cloudoor.SlideView.OnSlideListener;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;


public class MsgFragment extends Fragment implements OnItemClickListener, OnClickListener, OnSlideListener {
	
//	private MsgListView mMsgList;
	private List<MessageItem> mMessageItems = new ArrayList<MessageItem>();
//	private SlideView mLastSlideViewWithStatusOn;
	
	private TextView mPopupWindow;

	public MsgFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.msg_page, container,false);
		
//		mMsgList = (MsgListView) view.findViewById(R.id.msg_list);
		MessageItem item = new MessageItem();
		item.image = R.drawable.icon_boy_110;
		item.name = "Name";
		item.content = "message";
		item.time = "11:50";
		mMessageItems.add(item);
//		mMsgList.setAdapter(new SlideAdapter());
//		mMsgList.setOnItemClickListener(this);
		
		return view;
	}

	private class SlideAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        SlideAdapter() {
            super();
            mInflater = getLayoutInflater(null);
        }

        @Override
        public int getCount() {
            return mMessageItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mMessageItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            SlideView slideView = (SlideView) convertView;
            if (slideView == null) {
                View itemView = mInflater.inflate(R.layout.msg_list_item, null);

                slideView = new SlideView(getActivity().getApplicationContext());
                slideView.setContentView(itemView);

                holder = new ViewHolder(slideView);
                slideView.setOnSlideListener(MsgFragment.this);
                slideView.setTag(holder);
            } else {
                holder = (ViewHolder) slideView.getTag();
            }
            MessageItem item = mMessageItems.get(position);
            item.slideView = slideView;
            item.slideView.shrink();

            holder.image.setImageResource(item.image);
            holder.name.setText(item.name);
            holder.content.setText(item.content);
            holder.time.setText(item.time);
            holder.deleteHolder.setOnClickListener(MsgFragment.this);

            return slideView;
        }

    }
	
	public class MessageItem {
        public int image;
        public String name;
        public String content;
        public String time;
        public SlideView slideView;
    }

	private static class ViewHolder {
        public ImageView image;
        public TextView name;
        public TextView content;
        public TextView time;
        public ViewGroup deleteHolder;

        ViewHolder(View view) {
        	image = (ImageView) view.findViewById(R.id.msg_image);
            name = (TextView) view.findViewById(R.id.msg_name);
            content = (TextView) view.findViewById(R.id.msg_content);
            time = (TextView) view.findViewById(R.id.msg_time);
            deleteHolder = (ViewGroup)view.findViewById(R.id.holder);
        }
    }	
	
//	@Override
//	public void onSlide(View view, int status) {
//		if (mLastSlideViewWithStatusOn != null && mLastSlideViewWithStatusOn != view) {
//            mLastSlideViewWithStatusOn.shrink();
//        }
//
//        if (status == SLIDE_STATUS_ON) {
//            mLastSlideViewWithStatusOn = (SlideView) view;
//        }
//		
//	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.holder){
			Toast.makeText(getActivity(), "test", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
	}

	@Override
	public void onSlide(View view, int status) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
    public void onDetach() {
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        super.onDetach();

    }
	
}
