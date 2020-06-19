package com.reading7;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.reading7.Adapters.BestBooksAdapter;
import com.reading7.Adapters.CommentsAdapter;
import com.reading7.Objects.Book;
import com.reading7.Objects.Comment;
import com.reading7.Objects.Review;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ArrayList<Book> maxReads;
    private ArrayList<Book> maxRate;
    private HashMap<String,Integer> numUsersHist;
    private HashMap<String,Integer> numPagesHist;
    private HashMap<String,HashMap<String,Integer>> genreMap;
    private ArrayList<String>names;
    private ArrayList<String>ranges;



    private int NUM_BOOKS = 3;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.statistics_fragment, null);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((BottomNavigationView) getActivity().findViewById(R.id.navigation)).setSelectedItemId(R.id.navigation_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        maxReads=new ArrayList<>();
        maxRate=new ArrayList<>();
        numPagesHist= new HashMap<>();
        numUsersHist=new HashMap<>();
        genreMap=new HashMap<>();
        names=new ArrayList<>();
        ranges=new ArrayList<>();

        initLists();

        getStatistics();

        for(Map.Entry<String,Integer> entry : numPagesHist.entrySet()){
                int avg= numPagesHist.get(entry.getKey())/numUsersHist.get(entry.getKey());
                entry.setValue(avg);
        }

        initBestBooks();
        setUpBarChart(numPagesHist,R.id.titleBar1,"מספר העמודים הממוצע שקוראים משתמשים על פי טווח גילאים",R.id.chartBar1);
        setUpPie(numUsersHist,"מספר המשתמשים על פי טווח גילאים",R.id.title0,R.id.pieBar1);
        final Spinner spinner = getActivity().findViewById(R.id.spinner);

        //setUpPie(numUsersHist,"מספר המשתמשים על פי טווח גילאים",R.id.pieBar2);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.genres, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                setUpPie(genreMap.get(spinner.getSelectedItem()),"מספר משתמשים שקראו ספרים של הז'אנר הנבחר על פי טווח גילאים",R.id.title01,R.id.pieBar2);
            }


            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                setUpPie(genreMap.get(spinner.getSelectedItem()),"מספר משתמשים שקראו ספרים של הז'אנר הנבחר על פי טווח גילאים",R.id.title01,R.id.pieBar2);

            }

        });
        spinner.setSelection(0);


    }

    public void initLists() {
        names.add("בשבילך");
        names.add("הרפתקאות");
        names.add("מדע בדיוני");
        names.add("היסטוריה");
        names.add("דרמה");
        names.add("אהבה");
        names.add("אימה");
        names.add("מדע");
        names.add("קומדיה");
        names.add("מתח");

        ranges.add( "0-6");
        ranges.add( "7-9");
        ranges.add( "10-13");
        ranges.add( "14-16");
        ranges.add( "17-18");
        ranges.add( "18+");


        for (String name : names) {
            HashMap<String, Integer> temp = new HashMap<>();
            for (String r : ranges) {
                temp.put(r, 0);

                genreMap.put(name, temp);
            }
        }
        for (String range : ranges) {


                numPagesHist.put(range, 0);
                numUsersHist.put(range, 0);
        }


    }


    private void getStatistics() {
        Utils.enableDisableClicks(getActivity(), (ViewGroup) getView(), false);

        CollectionReference collection = db.collection("Books");
        Query query = collection;
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Book b = (doc.toObject(Book.class));
                        updateMaxReads(b);
                        updateMaxRate(b);
                        updateGenreHist(b);
                        updateNumPagesHist(b);
                        updateNumUsers(b);
                    }



                Utils.enableDisableClicks(getActivity(), (ViewGroup) getView(), true);
            }
        }});
    }


    class SortbyCountReads implements Comparator<Book>
    {
        // Used for sorting in ascending order of
        // roll number
        public int compare(Book a, Book b)
        {
            return a.getRaters_count() - b.getRaters_count();
        }
    }

    class SortbyRate implements Comparator<Book>
    {
        // Used for sorting in ascending order of
        // roll name
        public int compare(Book a, Book b)
        {
            return (int)(a.getAvg_rating()-(b.getAvg_rating()));
        }
    }

    public void  updateMaxReads(Book b){
        if(maxReads.size()<=NUM_BOOKS)
            maxReads.add(b);
        else
            maxReads.set(NUM_BOOKS,b);
       Collections.sort(maxReads,new SortbyCountReads());

   }
    public void  updateMaxRate(Book b){
        if(maxRate.size()<=NUM_BOOKS)
            maxRate.add(b);
        else
            maxRate.set(NUM_BOOKS,b);
        Collections.sort(maxRate,new SortbyRate());

    }
    public void updateGenreHist(Book b){

        for(String genre :names){
            if(b.getActual_genres().contains(genre)){
                int curr_count=genreMap.get(genre).get(Utils.calcRangeOfAge(b.getAvg_age()));
                genreMap.get(genre).put(Utils.calcRangeOfAge(b.getAvg_age()),curr_count+1);
            }

        }
    }

    public void updateNumUsers(Book b){



            int curr_count=numUsersHist.get(Utils.calcRangeOfAge(b.getAvg_age()));
            numUsersHist.put(Utils.calcRangeOfAge(b.getAvg_age()),curr_count+1);
}


    public void updateNumPagesHist(Book b){



                int curr_count=numPagesHist.get(Utils.calcRangeOfAge(b.getAvg_age()));
            numPagesHist.put(Utils.calcRangeOfAge(b.getAvg_age()),curr_count+b.getNum_pages());


        }




    private void setUpPie(Map<String, Integer> hist,String title,int title_id,int chart_id) {
        final List<PieEntry> pieEntries = new ArrayList<>();
        for ( HashMap.Entry<String, Integer> entry : hist.entrySet()) {
            String key = (entry.getKey());
            Integer value = entry.getValue();
            if (value > 0)
                pieEntries.add(new PieEntry(value, key));
        }

        TextView title1 = getView().findViewById(title_id);
        title1.setText(title);
        PieDataSet dataSet = new PieDataSet(pieEntries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setColors(ColorTemplate.LIBERTY_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(10f);
        dataSet.setSelectionShift(10f);
        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLinePart1Length(0.5f);
        dataSet.setValueLinePart2Length(0.35f);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        PieData data = new PieData(dataSet);
        data.setDrawValues(false);

        //Get the Chart
        PieChart chart = (PieChart)getView().findViewById(chart_id);
        chart.setDrawHoleEnabled(false);
        chart.setEntryLabelColor(Color.BLACK);
        chart.setData(data);
        chart.animateY(1000);
        chart.getLegend().setEnabled(false);
        chart.getDescription().setEnabled(false);

        chart.invalidate();
    }



    private void setUpBarChart(HashMap<String, Integer> hist, int title_id, String role, int chart_id) {
        List<BarEntry> barEntries = new ArrayList<>();

        final ArrayList<String> labels = new ArrayList<>(hist.keySet());
        Collections.sort(labels);

        for (int i = 0; i < labels.size(); i++){
            int label = getLabel(labels.get(i));
            int value = hist.get(labels.get(i));
            if (value > 0 ){
                barEntries.add(new BarEntry(label, value, label));
            }
        }


        TextView title = getView().findViewById(title_id);
        title.setText(role);
        BarDataSet dataSet = new BarDataSet(barEntries, "אחוזים");
        dataSet.setColors(ColorTemplate.LIBERTY_COLORS);
//        dataSet.setStackLabels(labels.toArray(new String[labels.size()]));
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(8f);

        BarData data = new BarData(dataSet);
        data.setBarWidth(2f); // set custom bar width


        //Get the Chart
        BarChart chart = (BarChart)getView().findViewById(chart_id);
        chart.setData(data);
        chart.setFitBars(true);
        chart.animateY(1000);
        chart.getLegend().setEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);
        chart.setDrawBorders(false);
        chart.getXAxis().setLabelRotationAngle(0);
        chart.setTouchEnabled(false);

        //X AXIS
        XAxis xl = chart.getXAxis();
        xl.setGranularity(1f);
        xl.setCenterAxisLabels(false);
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setAxisMinimum(0);
        xl.setAxisMaximum(100);
        xl.setLabelCount(10);
        xl.isForceLabelsEnabled();
        xl.setLabelRotationAngle(0);
        labels.add("0");
        Collections.sort(labels);
        xl.setValueFormatter(new IndexAxisValueFormatter(){
            @Override
            public String getFormattedValue(float value) {
                if (value >= 0) {
                    return labels.get((int) value/10);
                }
                return "";
            }
        });

        //Y AXIS
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setLabelCount(5);
        leftAxis.setSpaceTop(30f);
        leftAxis.setAxisMinimum(0f);

        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setEnabled(true);

        chart.invalidate();
    }

    private int getLabel(String key) {
        String[] split = key.split("-");
        return Integer.parseInt(split[0]) + 10;
    }

    private void initBestBooks() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        RecyclerView maxReadsRV = getActivity().findViewById(R.id.maxReadsRV);
        maxReadsRV.setLayoutManager(layoutManager);

        BestBooksAdapter adapterReads = new BestBooksAdapter(getActivity(), this, maxReads);
        maxReadsRV.setAdapter(adapterReads);

        RecyclerView maxRatedRV= getActivity().findViewById(R.id.maxRatedRV);
        maxRatedRV.setLayoutManager(layoutManager);

        BestBooksAdapter adapterRate = new BestBooksAdapter(getActivity(), this, maxRate);
        maxRatedRV.setAdapter(adapterRate);
    }



}
