package com.reading7;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;

public class Utils {

    public static void convertTxtToBook(final Context context) throws IOException {
        //System.out.println("Hello Adva!");

//        File file = new File("lion.txt");
        //File file = new File(".");

        //System.out.println(file.getPath());
        InputStream is = context.getAssets().open("lion.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("Windows-1255")));

        String st = "";
        String title = "";
        String image_url = "";
        ArrayList<Book.BookGenre> genres;
        String author = "";
        String publisher = "";
        String summary = "";

        while ((st = br.readLine()) != null) {

            if (st.startsWith("Title: ")) {
                String[] sst = st.split("Title: ");
                title = sst[1];
            }
            if (st.startsWith("Image URL: ")) {
                String[] sst = st.split("Image URL: ");
                image_url = sst[1];
            }
            if (st.startsWith("Author: ")) {
                String[] sst = st.split("Author: ");
                author = sst[1];
            }
            if (st.startsWith("Publisher: ")) {
                String[] sst = st.split("Publisher: ");
                publisher = sst[1];
            }
            if (st.startsWith("Description: ")) {
                String[] sst = st.split("Description: ");
                summary = sst[1];
            }
        }

        Book b = new Book("", title, image_url, new ArrayList<Book.BookGenre>(), author, publisher, -1, -1, summary);


        //System.out.println("Bye Adva!");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ;

        DocumentReference newBook = db.collection("Books").document();
        b.setId(newBook.getId());
        newBook.set(b).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    //Toast.makeText(LoginActivity, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

    //Birthday = string of format "dd/mm/yyyy"
    public static int calculateAge(String birthday) {

        int day = Integer.parseInt(birthday.substring(0, 2));
        int month = Integer.parseInt(birthday.substring(3, 5));
        int year = Integer.parseInt(birthday.substring(6, 10));

        Calendar today = Calendar.getInstance();
        Calendar birth = Calendar.getInstance();

        birth.set(year, month - 1, day); //month starts from 0

        int age = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < birth.get(Calendar.DAY_OF_YEAR))
            age--;

        return age;
    }
}









