package com.reading7;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class Utils {

    public static void convertTxtToBook(final Context context) throws IOException {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
       for(String name:context.getAssets().list("")){
            if(!(name.contains(".")))
                continue;

           InputStream is = context.getAssets().open(name);
           BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("utf-16")));

           String st = "-1";
           String title = "";
           String num_pages = "-1";
           ArrayList<String> genres;
           String author = "";
           String publisher = "";
           String summary = "";
           String geners="";
           ArrayList<String> genersarray = new ArrayList<String>();

           while ((st = br.readLine()) != null) {

               if (st.startsWith("Title: ")) {
                   String[] sst = st.split("Title: ");
                   title = sst[1];
               }
               if (st.startsWith("Num Pages: ")) {
                   String[] sst = st.split("Num Pages: ");
                   num_pages = sst[1];
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
               if (st.startsWith("Genres: ")) {
                   String[] sst = st.split("Genres: ");
                   geners = sst[1];
                    genersarray = new ArrayList<String>(Arrays.asList(geners.split(", ", -1)));
               }
           }


           Book b = new Book("", title, genersarray, author, publisher, Integer.parseInt(num_pages), summary, 0,0);

           DocumentReference newBook = db.collection("Books").document();
           b.setId(newBook.getId());
           newBook.set(b).addOnCompleteListener(new OnCompleteListener<Void>() {
               @Override
               public void onComplete(@NonNull Task<Void> task) {
                   if (task.isSuccessful()) {
                       //Toast.makeText(LoginActivity, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                   }
                   else{

                   }

               }
           });
       }

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

    //use this when you want to load image of book to imageView, image_id is the name of the file in
    //firebase storage, view is where you want to load the image, activity pass the current activity-probably this
    public static void showImage(String image_id, final ImageView view, final Activity activity){
        StorageReference mStorageRef;
        mStorageRef = FirebaseStorage.getInstance().getReference("Images/"+image_id+".jpg");

        mStorageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful())
                {
                    Glide.with(activity)
                            .load(task.getResult())
                            .into(view);

                }
                else {
                    Toast.makeText(activity, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    public static void closeKeyboard(Context context){

        InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public static void openKeyboard(Context context){

        InputMethodManager imm = (InputMethodManager)context.getSystemService(context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
}









