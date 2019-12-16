package com.reading7;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
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
import com.reading7.Objects.Book;

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
        for (String name : context.getAssets().list("")) {
            if (!(name.contains(".")))
                continue;
            if (name.contains("huangli.idf")){
                continue;
            }
            if (name.contains("operators.dat")){
                continue;
            }if (name.contains("pinyinindex.idf")){
                continue;
            }if (name.contains("tel_uniqid_len8.dat")){
                continue;
            }if (name.contains("telocation.idf")){
                continue;
            }if (name.contains("xiaomi_mobile.dat")){
                continue;
            }
            InputStream is = context.getAssets().open(name);
            BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("utf-16")));

            String st = "-1";
            String title = "";
            String num_pages = "-1";
            ArrayList<String> genres;
            String author = "";
            String publisher = "";
            String summary = "";
            String geners = "";
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
            final String t = title;

            Book b = new Book("", title, genersarray, author, publisher, Integer.parseInt(num_pages), summary, 0, 0);
            if (b.getTitle().equals("")){
                throw new AssertionError(name);
            }
            DocumentReference newBook = db.collection("Books").document();
            b.setId(newBook.getId());
            newBook.set(b).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        //Toast.makeText(LoginActivity, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                    } else {
                        throw new AssertionError(t);
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
    public static String convertTitle(String t) {
        int l = t.length();
        String[] r = new String[l];
        for (int i = 0; i < l; i++) {
            char c = t.charAt(i);
            r[i] = Integer.toString((int) c);
        }
        return TextUtils.join(" ", r);
    }

    public static void showImage(final String imageFileName, final ImageView view, final Activity activity) {
        StorageReference mStorageRef;
        mStorageRef = FirebaseStorage.getInstance().getReference("images/" + convertTitle(imageFileName) + ".jpg");

        mStorageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Glide.with(activity)
                            .load(task.getResult())
                            .into(view);

                } else {
//                    throw new AssertionError("OPPS".concat(imageFileName));
                    Toast.makeText(activity, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

//    public static User getUserByMail(final Activity activity, final String user_email) {
//        final User[] user = {new User()};
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        DocumentReference userRef = db.collection("Users").document(user_email);
//
//        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()) {
////                        User user = new User();
//
//                        String full_name = document.getData().get("full_name").toString();
//
//                        String birth_date = document.getData().get("birth_date").toString();
//
//                        ArrayList<String> followers = (ArrayList<String>) document.getData().get("followers");
//
//                        ArrayList<String> following = (ArrayList<String>) document.getData().get("following");
//
//                        Queue<String> last_searches = (Queue<String>) document.getData().get("last_searches");
//
//                        user[0] = new User(full_name, user_email, birth_date, followers, following, last_searches);
//
//                    } else
//                        Toast.makeText(activity, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//
//                } else
//                    Toast.makeText(activity, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//        return user[0];
//    }

    public static void closeKeyboard(Context context) {

        InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
        View view = ((Activity)context).getCurrentFocus();
        if(view == null)
            view = new View((Activity)context);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void openKeyboard(Context context) {

        InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
}









