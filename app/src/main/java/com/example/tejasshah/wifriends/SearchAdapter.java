package com.example.tejasshah.wifriends;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jay on 4/17/16.
 */
public class SearchAdapter extends BaseAdapter implements Filterable {

    Context c;
    ArrayList<BaseSearchObject> friends;
    CustomFilter filter;
    ArrayList<BaseSearchObject> filterList;

    public SearchAdapter(Context ctx,ArrayList<BaseSearchObject> friends) {
        // TODO Auto-generated constructor stub

        this.c=ctx;
        this.friends=friends;
        this.filterList=friends;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return friends.size();
    }
    @Override
    public Object getItem(int pos) {
        // TODO Auto-generated method stub
        return friends.get(pos);
    }
    @Override
    public long getItemId(int pos) {
        // TODO Auto-generated method stub
        return friends.indexOf(getItem(pos));
    }


    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        LayoutInflater inflater=(LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView==null)
        {
            convertView=inflater.inflate(R.layout.view_search_friend, null);
        }

        TextView nameTxt=(TextView) convertView.findViewById(R.id.tvFriendName);
        TextView tvUserName = (TextView) convertView.findViewById(R.id.tvFriendUserName);
        ImageView img=(ImageView) convertView.findViewById(R.id.imgFriend);

        //SET DATA TO THEM
        nameTxt.setText(friends.get(pos).getFriendName());
        tvUserName.setText(friends.get(pos).getUsername());
        img.setImageResource(friends.get(pos).getImg());

        return convertView;
    }
    @Override
    public Filter getFilter() {
        // TODO Auto-generated method stub
        if(filter == null)
        {
            filter=new CustomFilter();
        }

        return filter;
    }

    //INNER CLASS
    class CustomFilter extends Filter
    {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            // TODO Auto-generated method stub

            FilterResults results=new FilterResults();

            if(constraint != null && constraint.length()>0)
            {
                //CONSTRAINT TO UPPER
                constraint=constraint.toString().toUpperCase();

                ArrayList<BaseSearchObject> filters=new ArrayList<BaseSearchObject>();

                //get specific items
                for(int i=0;i<filterList.size();i++)
                {
                    if(filterList.get(i).getFriendName().toUpperCase().contains(constraint) || filterList.get(i).getUsername().toUpperCase().contains(constraint))
                    {
                        BaseSearchObject p=new BaseSearchObject(filterList.get(i).getFriendName(), filterList.get(i).getImg(),filterList.get(i).getUsername());

                        filters.add(p);
                    }
                }

                results.count=filters.size();
                results.values=filters;

            }else
            {
                results.count=filterList.size();
                results.values=filterList;

            }

            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // TODO Auto-generated method stub

            friends=(ArrayList<BaseSearchObject>) results.values;
            notifyDataSetChanged();
        }

    }

}

