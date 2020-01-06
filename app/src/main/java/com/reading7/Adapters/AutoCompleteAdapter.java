package com.reading7.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.reading7.Objects.Book;
import com.reading7.Utils;
import com.reading7.R;


import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import de.hdodenhof.circleimageview.CircleImageView;

//public class AutoCompleteAdapter extends BaseAdapter implements Filterable {
//
//    private ArrayList<Book> books;
//    private ArrayList<Book> suggestions;
//    private ArrayList<Book> tempItems;
//    private Context mContext;
//
//
//    public AutoCompleteAdapter(Context context, ArrayList<Book> books) {
//
//        this.books = books;
//        this.mContext = context;
//    }
//
//    @Override
//    public int getCount() {
//        return books.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return books.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//
//    public class ViewHolder {
//
//        RelativeLayout container;
//        CircleImageView cover;
//        TextView title;
//        TextView author;
//    }
//
//    public Filter getFilter() {
//
//        return nameFilter;
////        return new Filter() {
////
////            @Override
////            protected FilterResults performFiltering(CharSequence constraint) {
////                final FilterResults oReturn = new FilterResults();
////                final ArrayList<Book> results = new ArrayList<Book>();
////                if (original == null)
////                    original = books;
////                if (constraint != null) {
////                    if (original != null && original.size() > 0) {
////                        for (final Book book : original) {
////                            if (book.getTitle().contains(constraint.toString()))
////                                results.add(book);
////                        }
////                    }
////                    oReturn.values = results;
////                }
////                return oReturn;
////            }
////
////            @SuppressWarnings("unchecked")
////            @Override
////            protected void publishResults(CharSequence constraint,
////                                          FilterResults results) {
////                books = (ArrayList<Book>) results.values;
////                notifyDataSetChanged();
////            }
////        };
//    }
//
//
//    @Override
//    public View getView(final int position, View convertView, ViewGroup parent) {
//
//        final ViewHolder holder;
//        if (convertView == null) {
//
//            convertView = LayoutInflater.from(mContext).inflate(R.layout.search_item, parent, false);
//            holder = new ViewHolder();
//            holder.container = convertView.findViewById(R.id.container);
//            holder.title = convertView.findViewById(R.id.title);
//            holder.author = convertView.findViewById(R.id.author);
//            holder.cover = convertView.findViewById(R.id.cover);
//            convertView.setTag(holder);
//
//        } else {
//            holder = (ViewHolder) convertView.getTag();
//        }
//
//        holder.title.setText(books.get(position).getTitle());
//        holder.author.setText(books.get(position).getAuthor());
//        Utils.showImage(books.get(position).getTitle(), holder.cover, (Activity) mContext);
//
//        return convertView;
//    }
//
//
//    Filter nameFilter = new Filter() {
//        @Override
//        public CharSequence convertResultToString(Object resultValue) {
//            String str = ((Book) resultValue).getTitle();
//            return str;
//        }
//
//        @Override
//        protected FilterResults performFiltering(CharSequence constraint) {
//            if (constraint != null) {
//                suggestions.clear();
//                for (Book book : tempItems) {
//                    if (book.getTitle().contains(constraint.toString())) {
//                        suggestions.add(book);
//                    }
//                }
//                FilterResults filterResults = new FilterResults();
//                filterResults.values = suggestions;
//                filterResults.count = suggestions.size();
//                return filterResults;
//            } else {
//                return new FilterResults();
//            }
//        }
//
//        @Override
//        protected void publishResults(CharSequence constraint, FilterResults results) {
//            List<Book> filterList = (ArrayList<Book>) results.values;
//            if (results != null && results.count > 0) {
//                clear();
//                for (Book book : filterList) {
//                    add(people);
//                    notifyDataSetChanged();
//                }
//            }
//        }
//    };
//
//}


public class AutoCompleteAdapter extends ArrayAdapter<Book> {
    private List<Book> booksListFull;
    private Context mContext;

    public AutoCompleteAdapter(@NonNull Context context, @NonNull List<Book> booksList) {
        super(context, 0, booksList);
        this.booksListFull = new ArrayList<>(booksList);
        this.mContext = context;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return booksFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.search_item, parent, false
            );
        }

        TextView title = convertView.findViewById(R.id.title);
        TextView author = convertView.findViewById(R.id.author);
        CircleImageView cover = convertView.findViewById(R.id.cover);

        Book book = getItem(position);

        if (book != null) {
            title.setText(book.getTitle());
            author.setText(book.getAuthor());
            Utils.showImage(book.getTitle(), cover, (Activity) mContext);
        }

        return convertView;
    }

    private Filter booksFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<Book> suggestions = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                suggestions.addAll(booksListFull);
            } else {
                String filterPattern = constraint.toString().trim();

                for (Book book : booksListFull) {
                    if (book.getTitle().contains(filterPattern)) {
                        suggestions.add(book);
                    }
                }
            }

            results.values = suggestions;
            results.count = suggestions.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            addAll((List) results.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((Book) resultValue).getTitle();
        }
    };
}