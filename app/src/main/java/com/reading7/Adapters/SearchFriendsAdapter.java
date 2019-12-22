package com.reading7.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.reading7.MainActivity;
import com.reading7.PublicProfileFragment;
import com.reading7.R;
import com.reading7.Objects.User;
import com.reading7.Utils;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchFriendsAdapter extends BaseAdapter implements Filterable {

    private ArrayList<User> users;
    private ArrayList<User> original;
    private Context mContext;


    public SearchFriendsAdapter(Context context, ArrayList<User> users) {

        this.users = users;
        this.mContext = context;
    }


    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public class ViewHolder {

        RelativeLayout container;
        CircleImageView image;
        TextView name;
        TextView toDelete;
    }


    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<User> results = new ArrayList<User>();
                if (original == null)
                    original = users;
                if (constraint != null) {
                    if (original != null && original.size() > 0) {
                        for (final User user : original) {
                            if (user.getFull_name().contains(constraint.toString()))
                                results.add(user);
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                users = (ArrayList<User>)results.values;
                notifyDataSetChanged();
            }
        };
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        SearchFriendsAdapter.ViewHolder holder;
        if (convertView == null) {

            convertView = LayoutInflater.from(mContext).inflate(R.layout.search_item, parent, false);
            holder = new SearchFriendsAdapter.ViewHolder();
            holder.container = convertView.findViewById(R.id.container);
            holder.name = convertView.findViewById(R.id.title);
            holder.toDelete = convertView.findViewById(R.id.author);
            holder.image = convertView.findViewById(R.id.cover);
            convertView.setTag(holder);

        } else {
            holder = (SearchFriendsAdapter.ViewHolder) convertView.getTag();
        }

        holder.name.setText(users.get(position).getFull_name());
        holder.toDelete.setVisibility(View.GONE);
        holder.image.setImageDrawable(mContext.getResources().getDrawable(R.drawable.avatar_layout));
        Utils.loadAvatar(mContext, holder.image, users.get(position).getAvatar_details());

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)mContext).addFragment(new PublicProfileFragment(users.get(position).getEmail()));
                Utils.closeKeyboard(mContext);
            }
        });
        return convertView;
    }

}
