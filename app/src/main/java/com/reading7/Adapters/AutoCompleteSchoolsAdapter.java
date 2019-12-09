package com.reading7.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class AutoCompleteSchoolsAdapter extends ArrayAdapter<String> {

    private List<String> schools;
    private Context mContext;


    public AutoCompleteSchoolsAdapter(Context context, List<String> schools) {
        super(context, 0, schools);
        this.schools = new ArrayList<String>(schools);
        mContext = context;
    }


    @NonNull
    @Override
    public Filter getFilter(){
        return schoolFilter;
    }


    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {

        if (view == null)
            view = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_dropdown_item_1line, parent, false);

        String school = getItem(position);
        if(school != null)
            ((TextView)view.findViewById(android.R.id.text1)).setText(school);

        return view;
    }



    private Filter schoolFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults results = new FilterResults();
            List<String> suggestions = new ArrayList<>();

            if(charSequence == null || charSequence.length() == 0)
                suggestions.addAll(schools);


            else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for(String school: schools){
                    if(school.contains(filterPattern))
                        suggestions.add(school);
                }
            }

            results.values = suggestions;
            results.count = suggestions.size();
            publishResults(charSequence, results);
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            clear();

            if(filterResults.values == null)
                performFiltering(charSequence);

            else addAll((List)filterResults.values);

            notifyDataSetChanged();
        }


        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((String)resultValue);
        }
    };



}
