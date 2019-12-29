package com.reading7;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import de.hdodenhof.circleimageview.CircleImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Utils {


//    public static void updateBooks() {
//        final FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//        db.collection("Books").get()//VERY BAD!!!!!!!!!!!!!!!!!
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (final QueryDocumentSnapshot document : task.getResult()) {
//                                db.collection("Books").document(document.getId()).update("avg_age", 0).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void aVoid) {
//                                        Log.d("Utils", "Updated ".concat(document.get("title").toString()));
//                                    }
//                                });
//                            }
//                        } else {
//                            Log.d("Utils", "Error getting documents: ", task.getException());
//                        }
//                    }
//                });
//    }

    public static void convertTxtToBook(final Context context) throws IOException {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        for (String name : context.getAssets().list("")) {
            if (!(name.contains(".")))
                continue;
            if (name.contains("huangli.idf")) {
                continue;
            }
            if (name.contains("operators.dat")) {
                continue;
            }
            if (name.contains("pinyinindex.idf")) {
                continue;
            }
            if (name.contains("tel_uniqid_len8.dat")) {
                continue;
            }
            if (name.contains("telocation.idf")) {
                continue;
            }
            if (name.contains("xiaomi_mobile.dat")) {
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
                    num_pages = sst[1].replace("\t","").replace(" ","");
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

            Book b = new Book("", title, genersarray,new ArrayList<String>(), author, publisher, Integer.parseInt(num_pages), summary, 0,0);
            ArrayList<String> actual_genres=MapGenreToBook(b);

            b.setActual_genres(actual_genres);
            if (b.getTitle().equals("")) {
                throw new AssertionError(name);
            }
            DocumentReference newBook = db.collection("Books").document();
            b.setId(newBook.getId());
            newBook.set(b).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d("Utils", "Uploaded book: ".concat(t));
                        //Toast.makeText(LoginActivity, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                    } else {
                        throw new AssertionError(t);
                    }

                }
            });
        }

    }

    public static String convertTitle(String t) {
        int l = t.length();
        String[] r = new String[l];
        for (int i = 0; i < l; i++) {
            char c = t.charAt(i);
            r[i] = Integer.toString((int) c);
        }
        return TextUtils.join(" ", r);
    }


    /**
     * Loads image of book into an imageView.
     *
     * @param imageFileName is the name of the file in firebase storage
     * @param view          is where you want to load the image
     * @param activity      is the current activity (probably this)
     */
    public static void showImage(final String imageFileName, final ImageView view, final Activity activity) {
        StorageReference mStorageRef;
        final String img_path = "test/" + convertTitle(imageFileName) + ".jpg";
        mStorageRef = FirebaseStorage.getInstance().getReference(img_path);

        mStorageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Glide.with(activity)
                            .load(task.getResult()).thumbnail(0.5f).apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .skipMemoryCache(true).dontAnimate()
                    ).into(view);//caching dowloaded files

                } else {
                    throw new AssertionError("OPPS ".concat(img_path).concat(imageFileName));
//                    Toast.makeText(activity, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    /**
     * Returns the user's age based on it's birth date.
     *
     * @param birthday is of format "dd/mm/yyyy"
     */
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


    /**
     * Loads the correct avatar into the image.
     *
     * @param avatar_details holds the wanted avatar specifications:
     *                       index 0 - skin index (if the wanted drawable is skin1, then the value is 1)
     *                       index 1 - eyes color index
     *                       index 2 - hair color index
     *                       index 3 - hair type index
     *                       index 4 - shirt color index
     * @param image          should have "avatar_layout" drawable set as it's drawable.
     */
    public static void loadAvatar(Context context, CircleImageView image, ArrayList<Integer> avatar_details) {

        LayerDrawable layer = (LayerDrawable) image.getDrawable();

        String skin = "skin" + avatar_details.get(0);
        Drawable skinDrawable = getDrawable(context, skin);
        layer.setDrawableByLayerId(R.id.skin, skinDrawable);

        String eyes = "eyes" + avatar_details.get(1);
        Drawable eyesDrawable = layer.findDrawableByLayerId(R.id.eyes);
        eyesDrawable.setTint(getColor(context, eyes));
        layer.setDrawableByLayerId(R.id.eyes, eyesDrawable);

        String hairType = "hair" + avatar_details.get(2);
        Drawable hairTypeDrawable = getDrawable(context, hairType);
        layer.setDrawableByLayerId(R.id.hair, hairTypeDrawable);

        String hairColor = "hair" + avatar_details.get(3);
        Drawable hairDrawable = layer.findDrawableByLayerId(R.id.hair);
        hairDrawable.setTint(getColor(context, hairColor));
        layer.setDrawableByLayerId(R.id.hair, hairDrawable);

        String shirt = "shirt" + avatar_details.get(4);
        Drawable shirtDrawable = layer.findDrawableByLayerId(R.id.shirt);
        shirtDrawable.setTint(getColor(context, shirt));
        layer.setDrawableByLayerId(R.id.shirt, shirtDrawable);

        image.setImageDrawable(layer);
    }


    /**
     * Returns a color from colors.xml based on it's name.
     */
    public static int getColor(Context context, String color_name) {
        return context.getResources().getColor(context.getResources().getIdentifier(color_name, "color", context.getPackageName()));
    }


    /**
     * Returns a drawable based on it's name.
     */
    public static Drawable getDrawable(Context context, String drawable_name) {
        return context.getResources().getDrawable(context.getResources().getIdentifier(drawable_name, "drawable", context.getPackageName()));
    }


    public static void closeKeyboard(Context context) {

        InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
        View view = ((Activity) context).getCurrentFocus();
        if (view == null)
            view = new View((Activity) context);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void openKeyboard(Context context) {

        InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }



    public static ArrayList<String> MapGenreToBook(Book b){
        ArrayList<String> genres=new ArrayList<String>();
        ArrayList<String> names = new ArrayList<String>();
        names.add("דרמה");
        names.add("אהבה");
        names.add("אימה");
        names.add("מדע");
        names.add("קומדיה");
        names.add("היסטוריה");
        names.add("מתח");
        names.add("מדע בדיוני");
        names.add("הרפתקאות");
        for (String genre:names){
            if(isBookFromGenre(b,genre))
                genres.add(genre);

        }
        return genres;
    }
    public static   String RelativeDateDisplay(long timeDifferenceMilliseconds) {
        long diffSeconds = timeDifferenceMilliseconds / 1000;
        long diffMinutes = timeDifferenceMilliseconds / (60 * 1000);
        long diffHours = timeDifferenceMilliseconds / (60 * 60 * 1000);
        long diffDays = timeDifferenceMilliseconds / (60 * 60 * 1000 * 24);
        long diffWeeks = timeDifferenceMilliseconds / (60 * 60 * 1000 * 24 * 7);
        long diffMonths = (long) (timeDifferenceMilliseconds / (60 * 60 * 1000 * 24 * 30.41666666));
        long diffYears = timeDifferenceMilliseconds / ((long)60 * 60 * 1000 * 24 * 365);

        if (diffMinutes < 1) {
            return "הרגע";
        } else if (diffHours < 1) {
            if (diffMinutes  == 1)
                return "לפני דקה";
            return  "לפני " + diffMinutes + " דקות";
        } else if (diffDays < 1) {
            if (diffHours == 1)
                return "לפני שעה";
            if (diffHours == 2)
                return "לפני שעתיים";
            return "לפני " + diffHours + " שעות";
        } else if (diffWeeks < 1) {
            if (diffDays == 1)
                return "לפני יום";
            if (diffDays == 2)
                return "לפני יומיים";
            return "לפני " + diffDays + " ימים";
        } else if (diffMonths < 1) {
            if (diffWeeks == 1)
                return "לפני שבוע";
            return "לפני " + diffWeeks + " שבועות";
        } else if (diffYears < 1) {
            if (diffMonths == 1)
                return "לפני חודש";
            if (diffMonths == 2)
                return "לפני חודשיים";
            return "לפני " + diffMonths + "חודשים";
        } else {
            if (diffYears == 1)
                return "לפני שנה";
            if (diffYears == 2)
                return "לפני שנתיים";
            return "לפני " + diffYears + " שנים";
        }
    }

    public static boolean isBookFromGenre(Book book, String g){
        if(g.equals(""))
            return true;
        ArrayList<String> generes = new ArrayList<>();
        switch (g){
            case "הרפתקאות": generes.add("סדרת הרפתקאותיו של מייקל ויי");
                generes.add("הרפתקאות לילדים");
                generes.add("הרפתקאות");break;
            case "מדע בדיוני": generes.add("פנטזיה");
                generes.add("מדע בדיוני לילדים");
                generes.add("מדע בדיוני צעיר");
                generes.add("פנטזיה ישראלית");
                generes.add("מותחן מדע בדיוני");
                generes.add("מד\"ב ופנטזיה");
                generes.add("פנטזיה צעירה");break;

            case "היסטוריה": generes.add("היסטוריה");
                generes.add("היסטוריה לילדים");
                generes.add("היסטוריה ופוליטיקה");
                generes.add("היסטוריה אלטרנטיבית");
                generes.add("היסטוריה של העת העתיקה וימי הביניים");break;

            case "אהבה": generes.add("אהבה ראשונה");
                generes.add("אהבה נכזבת");
                generes.add("אהבה");
                generes.add("אהבה מאוחרת");
                generes.add("אהבה ממבט ראשון");
                generes.add("אהבה ממבט שני");break;

            case "מתח": generes.add("מתח מושלג");
                generes.add("מתח צעיר");
                generes.add("מתח ישראלי");
                generes.add("דואטים, טרילוגיות וסדרות מתח");
                generes.add("מתח");
                generes.add("מותחן מדע בדיוני");
                generes.add("מותחן עתידני");
                generes.add("מתח ופעולה");break;

                case "אימה": generes.add("אימה");
                generes.add("מותחן אימה");break;

            case "מדע": generes.add("מדעים");
                generes.add("מדע לילדים");
                generes.add("סדרת סיירת המדע");
                generes.add("מדע בדיוני");
                generes.add("מדע פופולארי");
                break;
            case "קומדיה": generes.add("קומדיה");
                generes.add("קומדיה רומנטית");
                generes.add("ילדי הקומדיה");
                generes.add("ספרים מצחיקים");
                break;

            case "דרמה": generes.add("דרמה");
                generes.add("ספרים מרגשים");break;

                default:return false;



        }



        if(Collections.disjoint(book.getGenres(),generes))
            return false;
        return true;
    }

    public static void addToEachBookTheFieldGenres(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference requestCollectionRef = db.collection("Books");
        requestCollectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        Book book = document.toObject(Book.class);
                        book.setActual_genres(MapGenreToBook(book));
                        final DocumentReference bookRef = FirebaseFirestore.getInstance().collection("Books").document(book.getId());
                        requestCollectionRef.add(book);
                    }

                }
            }});





    }

}









