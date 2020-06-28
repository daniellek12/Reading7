package com.reading7;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.reading7.Adapters.BooksLeaderboardAdapter;
import com.reading7.Adapters.UsersLeaderboardAdapter;
import com.reading7.Objects.Book;
import com.reading7.Objects.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class StatisticsDialog extends AppCompatDialogFragment {

    private FirebaseFirestore db;
    private View dialogView;

    private ArrayList<Book> maxReads;
    private ArrayList<Book> maxRate;
    private HashMap<String, Integer> numUsersHist;
    private HashMap<String, Integer> numPagesHist;
    private HashMap<String, HashMap<String, Integer>> genreMap;
    private ArrayList<String> names;
    private ArrayList<String> ranges;
    private ArrayList<User> bestUsers;

    private int NUM_BOOKS = 3;
    private int NUM_USERS = 3;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        dialogView = getActivity().getLayoutInflater().inflate(R.layout.statistics_dialog, null);
        builder.setView(dialogView);

        db = FirebaseFirestore.getInstance();
        maxReads = new ArrayList<>();
        maxRate = new ArrayList<>();
        numPagesHist = new HashMap<>();
        numUsersHist = new HashMap<>();
        genreMap = new HashMap<>();
        names = new ArrayList<>();
        ranges = new ArrayList<>();
        bestUsers = new ArrayList<>();

        Animation slide_up = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);
        dialogView.findViewById(R.id.scrollView).startAnimation(slide_up);

        ((BarChart) dialogView.findViewById(R.id.chartBar1)).setNoDataText("");
        ((PieChart) dialogView.findViewById(R.id.pieBar2)).setNoDataText("");

        initLists();
        getStatistics();

        Dialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return dialog;
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

        ranges.add("0-6");
        ranges.add("7-9");
        ranges.add("10-13");
        ranges.add("14-16");
        ranges.add("17-18");
        ranges.add("19-100");


        for (String name : names) {
            HashMap<String, Integer> temp = new HashMap<>();
            for (String r : ranges) {
                temp.put(r, 0);

                genreMap.put(name, temp);
            }
        }
        for (String range : ranges) {


            numPagesHist.put(range, 0);
            numUsersHist.put(range, 1);
        }


    }


    public void initUI() {
        for (Map.Entry<String, Integer> entry : numPagesHist.entrySet()) {
            int avg = numPagesHist.get(entry.getKey()) / numUsersHist.get(entry.getKey());
            entry.setValue(avg);
        }

        dialogView.findViewById(R.id.maxUsersProgressBar).setVisibility(View.GONE);
        dialogView.findViewById(R.id.maxRatedProgressBar).setVisibility(View.GONE);
        dialogView.findViewById(R.id.maxReadsProgressBar).setVisibility(View.GONE);
        dialogView.findViewById(R.id.piaBar2ProgressBar).setVisibility(View.GONE);
        dialogView.findViewById(R.id.chartBar1ProgressBar).setVisibility(View.GONE);

        initBestBooks();
        initBestUsers();

        setUpBarChart(numPagesHist, R.id.titleBar1, getString(R.string.pages_by_age_statistics_title), R.id.chartBar1);

        final Spinner spinner = dialogView.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.genres, R.layout.genre_spinner_style);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                setUpPie(genreMap.get(spinner.getSelectedItem()), getString(R.string.genre_by_age_statistics_title), R.id.title01, R.id.pieBar2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                setUpPie(genreMap.get(spinner.getSelectedItem()), "מספר משתמשים שקראו ספרים של הז'אנר הנבחר על פי טווח גילאים", R.id.title01, R.id.pieBar2);

            }

        });
        spinner.setSelection(0);
    }

    private void getStatistics() {
        Utils.enableDisableClicks(getActivity(), (ViewGroup) dialogView, false);

        CollectionReference collection = db.collection("Books");
        Query query = collection.orderBy("avg_rating", Query.Direction.DESCENDING);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int count = 0;
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Book b = (doc.toObject(Book.class));
                        updateMaxReads(b);
                        //updateMaxRate(b);
                        updateGenreHist(b);
                        updateNumPagesHist(b);
                        updateNumUsers(b);
                        if (count < NUM_BOOKS) {
                            maxRate.add(b);
                            count++;
                        }
                    }
                    getBestUsers();
                }
            }
        });
    }


    private void getBestUsers() {

        CollectionReference collection = db.collection("Users");
        Query query = collection.orderBy("points", Query.Direction.DESCENDING).limit(NUM_USERS);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        User u = (doc.toObject(User.class));
                        bestUsers.add(u);
                    }

                    initUI();

                    Utils.enableDisableClicks(getActivity(), (ViewGroup) dialogView, true);
                }
            }
        });
    }

    public void updateMaxReads(Book b) {
        if (maxReads.size() <= NUM_BOOKS)
            maxReads.add(b);
        else
            maxReads.set(NUM_BOOKS, b);
        Collections.sort(maxReads, new SortbyCountReads());

    }

    public void updateMaxRate(Book b) {
        if (maxRate.size() <= NUM_BOOKS)
            maxRate.add(b);
        else
            maxRate.set(NUM_BOOKS, b);
        Collections.sort(maxRate, new SortbyRate());

    }

    public void updateGenreHist(Book b) {

        for (String genre : b.getActual_genres()) {
            int curr_count = genreMap.get(genre).get(Utils.calcRangeOfAge(b.getAvg_age()));
            genreMap.get(genre).put(Utils.calcRangeOfAge(b.getAvg_age()), curr_count + 1);
        }


    }

    public void updateNumUsers(Book b) {


        int curr_count = numUsersHist.get(Utils.calcRangeOfAge(b.getAvg_age()));
        numUsersHist.put(Utils.calcRangeOfAge(b.getAvg_age()), curr_count + 1);
    }

    public void updateNumPagesHist(Book b) {


        int curr_count = numPagesHist.get(Utils.calcRangeOfAge(b.getAvg_age()));
        numPagesHist.put(Utils.calcRangeOfAge(b.getAvg_age()), curr_count + b.getNum_pages());


    }

    private void setUpPie(Map<String, Integer> hist, String title, int title_id, int chart_id) {
        final List<PieEntry> pieEntries = new ArrayList<>();
        for (HashMap.Entry<String, Integer> entry : hist.entrySet()) {
            String key = (entry.getKey());
            Integer value = entry.getValue();
            if (value > 0)
                pieEntries.add(new PieEntry(value, key));
        }

        TextView title1 = dialogView.findViewById(title_id);
        title1.setText(title);
        PieDataSet dataSet = new PieDataSet(pieEntries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setColors(getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorPrimaryDark),
                getResources().getColor(R.color.textPrimary),
                getResources().getColor(R.color.transparentPrimaryDark));
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(10f);
        dataSet.setSelectionShift(10f);
        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLinePart1Length(0.5f);
        dataSet.setValueLinePart2Length(0.35f);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return "" + ((int) value);
            }
        };
        dataSet.setValueFormatter(formatter);
        PieData data = new PieData(dataSet);
        data.setDrawValues(true);

        //Get the Chart
        PieChart chart = dialogView.findViewById(chart_id);
        chart.setNoDataText("");
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
        TextView title = dialogView.findViewById(title_id);
        title.setText(role);
        final ArrayList<String> labels = new ArrayList<>(hist.keySet());
        Collections.sort(labels, new SortRange());

        for (int i = 0; i < labels.size(); i++) {
            int value = hist.get(labels.get(i));
            if (value > 0) {
                barEntries.add(new BarEntry(i, value));
            }
        }

        BarDataSet barDataSet = new BarDataSet(barEntries, "");
        barDataSet.setColors(getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorPrimaryDark),
                getResources().getColor(R.color.textPrimary),
                getResources().getColor(R.color.transparentPrimaryDark));
        BarChart barChart = dialogView.findViewById(chart_id);
        BarData barData = new BarData(barDataSet);
        barChart.setDescription(null);
        barChart.setData(barData);
        barChart.getLegend().setEnabled(false);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(labels.size());
        barChart.getAxisRight().setDrawLabels(false);
        barChart.getAxisRight().setDrawAxisLine(false);
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.animateY(2000);
        barChart.invalidate();
    }

    private int getLabel(String key) {
        if (key == "")
            return -1;
        String[] split = key.split("-");
        return Integer.parseInt(split[0]) + 10;
    }

    private int convertValueToLable(float value) {
        int x = (int) value;
        if (x == 0)
            return 0;
        if (x == 10)
            return 1;
        if (x == 20)
            return 2;
        if (x == 30)
            return 3;
        if (x == 40)
            return 4;
        if (x == 50)
            return 5;

        return 6;
    }

    private void initBestBooks() {

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        RecyclerView maxReadsRV = dialogView.findViewById(R.id.maxReadsRV);
        maxReadsRV.setLayoutManager(layoutManager1);
        maxReads.remove(NUM_BOOKS);

        BooksLeaderboardAdapter adapterReads = new BooksLeaderboardAdapter(getActivity(), this, maxReads, BooksLeaderboardAdapter.BooksLeaderboardType.MAX_READS);
        maxReadsRV.setAdapter(adapterReads);

        RecyclerView maxRatedRV = dialogView.findViewById(R.id.maxRatedRV);
        maxRatedRV.setLayoutManager(layoutManager2);

        BooksLeaderboardAdapter adapterRate = new BooksLeaderboardAdapter(getActivity(), this, maxRate, BooksLeaderboardAdapter.BooksLeaderboardType.MAX_RATED);
        maxRatedRV.setAdapter(adapterRate);
    }

    private void initBestUsers() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        RecyclerView maxUsersRV = dialogView.findViewById(R.id.maxUsersRV);
        maxUsersRV.setLayoutManager(layoutManager);

        UsersLeaderboardAdapter adapterUsers = new UsersLeaderboardAdapter(getActivity(), this, bestUsers);
        maxUsersRV.setAdapter(adapterUsers);
    }


    static class SortbyCountReads implements Comparator<Book> {
        // Used for sorting in ascending order of
        // roll number
        public int compare(Book a, Book b) {
            if (b.getRaters_count() == a.getRaters_count())
                return (int) (b.getAvg_rating() - a.getAvg_rating());
            return b.getRaters_count() - a.getRaters_count();
        }
    }

    static class SortRange implements Comparator<String> {
        // Used for sorting in ascending order of
        // roll number
        public int compare(String a, String b) {
            int numb = 0;
            int numa = 0;
            if (a == "")
                numa = -1;
            else {
                numa = a.charAt(0);
                if (a.charAt(1) != '-')
                    numa = numa * 10 + a.charAt(1);
            }
            if (b == "")
                numb = -1;
            else {
                numb = b.charAt(0);

                if (b.charAt(1) != '-')
                    numb = numb * 10 + b.charAt(1);
            }


            return numa - numb;
        }
    }

    static class SortbyRate implements Comparator<Book> {
        // Used for sorting in ascending order of
        // roll name
        public int compare(Book a, Book b) {
            return (int) (b.getAvg_rating() - (a.getAvg_rating()));
        }
    }

}
