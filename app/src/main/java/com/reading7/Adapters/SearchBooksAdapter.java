package com.reading7.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.reading7.Objects.Book;
import com.reading7.BookFragment;
import com.reading7.MainActivity;
import com.reading7.R;
import com.reading7.Utils;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchBooksAdapter extends BaseAdapter implements Filterable {

    private ArrayList<Book> books;
    private ArrayList<Book> original;
    private Context mContext;


    public SearchBooksAdapter(Context context, ArrayList<Book> books) {

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
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {

            convertView = LayoutInflater.from(mContext).inflate(R.layout.search_item, parent, false);
            holder = new ViewHolder();
            holder.container = convertView.findViewById(R.id.container);
            holder.title = convertView.findViewById(R.id.title_search);
            holder.author = convertView.findViewById(R.id.author_search);
            holder.cover = convertView.findViewById(R.id.cover_search);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(books.get(position).getTitle());
        holder.author.setText(books.get(position).getAuthor());
        Utils.showImage(books.get(position).getTitle(), holder.cover, (Activity) mContext);

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)mContext).addFragment(new BookFragment(books.get(position)));
                Utils.closeKeyboard(mContext);
            }
        });

        return convertView;
    }

}
