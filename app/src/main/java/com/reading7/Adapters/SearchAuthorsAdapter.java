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

import com.reading7.AuthorFragment;
import com.reading7.MainActivity;
import com.reading7.R;
import com.reading7.Utils;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchAuthorsAdapter extends BaseAdapter implements Filterable {

    private ArrayList<String> authors;
    private ArrayList<String> original;
    private Context mContext;


    public SearchAuthorsAdapter(Context context, ArrayList<String> authors) {

        this.authors = authors;
        this.mContext = context;
    }


    @Override
    public int getCount() {
        return authors.size();
    }

    @Override
    public Object getItem(int position) {
        return authors.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public class ViewHolder {

        RelativeLayout container;
        CircleImageView cover;
        TextView title;
        TextView author;
    }


    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<String> results = new ArrayList<String>();
                if (original == null)
                    original = authors;
                if (constraint != null) {
                    if (original != null && original.size() > 0) {
                        for (final String author : original) {
                            if (author.contains(constraint.toString()))
                                results.add(author);
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
                authors = (ArrayList<String>) results.values;
                notifyDataSetChanged();
            }
        };
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        SearchAuthorsAdapter.ViewHolder holder;
        if (convertView == null) {

            convertView = LayoutInflater.from(mContext).inflate(R.layout.search_item, parent, false);
            holder = new SearchAuthorsAdapter.ViewHolder();
            holder.container = convertView.findViewById(R.id.container);
            holder.title = convertView.findViewById(R.id.title);
            holder.author = convertView.findViewById(R.id.author);
            holder.cover = convertView.findViewById(R.id.cover);
            convertView.setTag(holder);

        } else {
            holder = (SearchAuthorsAdapter.ViewHolder) convertView.getTag();
        }

        holder.title.setText(authors.get(position));
        holder.author.setVisibility(View.GONE);
        holder.cover.setImageDrawable(mContext.getResources().getDrawable(R.drawable.round_user));

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)mContext).addFragment(new AuthorFragment(authors.get(position)));
                Utils.closeKeyboard(mContext);
            }
        });

        return convertView;
    }

}
