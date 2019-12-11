package com.reading7.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.reading7.Book;
import com.reading7.R;
import com.reading7.Utils;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class SearchAdapter extends BaseAdapter implements Filterable {

    private ArrayList<Book> books;
    private ArrayList<Book> original;
    private Context mContext;


    public SearchAdapter(Context context, ArrayList<Book> books) {

        this.books = books;
        this.mContext = context;
    }


    @Override
    public int getCount() {
        return books.size();
    }

    @Override
    public Object getItem(int position) {
        return books.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public class ViewHolder {

        CircleImageView cover;
        TextView title;
    }


    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<Book> results = new ArrayList<Book>();
                if (original == null)
                    original = books;
                if (constraint != null) {
                    if (original != null && original.size() > 0) {
                        for (final Book book : original) {
                            if (book.getTitle().contains(constraint.toString()))
                                results.add(book);
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
                books = (ArrayList<Book>) results.values;
                notifyDataSetChanged();
            }
        };
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {

            convertView = LayoutInflater.from(mContext).inflate(R.layout.search_item, parent, false);
            holder = new ViewHolder();
            holder.title = convertView.findViewById(R.id.title);
            holder.cover = convertView.findViewById(R.id.cover);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(books.get(position).getTitle());
        Utils.showImage(books.get(position).getTitle(), holder.cover, (Activity) mContext);

        return convertView;
    }

}
