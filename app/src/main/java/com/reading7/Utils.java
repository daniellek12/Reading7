package com.reading7;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.reading7.Objects.Book;
import com.reading7.Objects.Review;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class Utils {

    public static boolean clicksEnabled = true;
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
        /*Map<String,Integer> counts= new HashMap<String,Integer>();//for random genres
        counts.put("הרפתקאות",0);
        counts.put("דרמה",0);
        counts.put("אהבה",0);
        counts.put("אימה",0);
        counts.put("מדע",0);
        counts.put("קומדיה",0);
        counts.put("היסטוריה",0);
        counts.put("מדע בדיוני",0);
        counts.put("מתח",0);*/

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        for (String name : context.getAssets().list("")) {
            //for random genres
            /*int run=0;
            for (String key : counts.keySet()) {
                if ((!(key.equals("אימה")))&&counts.get(key) <= 50) ;
                    run = 1;
            }
            if(run==0)
                break;
            //for random genres*/
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
                    num_pages = sst[1].replace("\t", "").replace(" ", "");
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

            Book b = new Book("", title, genersarray, new ArrayList<String>(), author, publisher, Integer.parseInt(num_pages), summary, 0, 0);
            ArrayList<String> actual_genres = MapGenreToBook(b);

            /*//for random genres
            int add= 0;
            for(String genre: actual_genres){
                if((counts.get(genre) + 1)<=50) {
                    add = 1;
                    counts.put(genre,counts.get(genre) + 1);
                }

            }
            if(add==0)
                continue;
            //for random genres*/


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
                        //Log.d("Utils", "Uploaded book: ".concat(t));
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
     * Returns a color from colors.xml based on it's name.
     */
    public static int getColor(Context context, String color_name) {
        try {
            return ContextCompat.getColor(context, context.getResources().getIdentifier(color_name, "color", context.getPackageName()));
        } catch (Exception e) {
            throw new AssertionError("OOPS, you tried getting a color that doesnt exist");
        }
    }


    /**
     * Returns a drawable based on it's name.
     */
    public static Drawable getDrawable(Context context, String drawable_name) {
        try {
            return ContextCompat.getDrawable(context,context.getResources().getIdentifier(drawable_name, "drawable", context.getPackageName()));
        } catch (Exception e) {
            throw new AssertionError("OOPS, you tried getting a drawable that doesnt exist");
        }
    }


    /**
     * Returns the genre icon drawable.
     */
    public static Drawable getDrawableForGenre(Context context, String genre, Boolean filled) {

        Drawable drawable = null;

        switch (genre) {
            case "בשבילך":
                drawable = filled ? context.getResources().getDrawable(R.drawable.star_filled) :
                        context.getResources().getDrawable(R.drawable.star);
                break;
            case "אהבה":
                drawable = filled ? context.getResources().getDrawable(R.drawable.genre_romance_filled) :
                        context.getResources().getDrawable(R.drawable.genre_romance);
                break;
            case "הרפתקאות":
                drawable = filled ? context.getResources().getDrawable(R.drawable.genre_adventures_filled) :
                        context.getResources().getDrawable(R.drawable.genre_adventures);
                break;
            case "דרמה":
                drawable = filled ? context.getResources().getDrawable(R.drawable.genre_drama_filled) :
                        context.getResources().getDrawable(R.drawable.genre_drama);
                break;
            case "מדע בדיוני":
                drawable = filled ? context.getResources().getDrawable(R.drawable.genre_fantasy_filled) :
                        context.getResources().getDrawable(R.drawable.genre_fantasy);
                break;
            case "קומדיה":
                drawable = filled ? context.getResources().getDrawable(R.drawable.genre_comedy_filled) :
                        context.getResources().getDrawable(R.drawable.genre_comedy);
                break;
            case "היסטוריה":
                drawable = filled ? context.getResources().getDrawable(R.drawable.genre_history_filled) :
                        context.getResources().getDrawable(R.drawable.genre_history);
                break;
            case "מדע":
                drawable = filled ? context.getResources().getDrawable(R.drawable.genre_science_filled) :
                        context.getResources().getDrawable(R.drawable.genre_science);
                break;
            case "מתח":
                drawable = filled ? context.getResources().getDrawable(R.drawable.genre_thriller_filled) :
                        context.getResources().getDrawable(R.drawable.genre_thriller);
                break;
            case "אימה":
                drawable = filled ? context.getResources().getDrawable(R.drawable.genre_horror_filled) :
                        context.getResources().getDrawable(R.drawable.genre_horror);
                break;
            default:
                drawable = filled ? context.getResources().getDrawable(R.drawable.genre_adventures_filled) :
                        context.getResources().getDrawable(R.drawable.genre_adventures);
                break;
        }

        return drawable;
    }


    /**
     * Enables/Disables all child views in a view group.
     * Sets clicksEnabled field (needed for back button)
     *
     * @param viewGroup the view group
     * @param enabled   true to enable, false to disable
     */
    public static void enableDisableClicks(Activity activity, ViewGroup viewGroup, boolean enabled) {
        clicksEnabled = enabled;
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = viewGroup.getChildAt(i);
            view.setEnabled(enabled);
            if (view instanceof ViewGroup) {
                enableDisableClicks(activity, (ViewGroup) view, enabled);
            }
        }

        ((MainActivity) activity).setBottomNavigationEnabled(enabled);
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


    public static ArrayList<String> MapGenreToBook(Book b) {
        ArrayList<String> genres = new ArrayList<String>();
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
        for (String genre : names) {
            if (isBookFromGenre(b, genre))
                genres.add(genre);

        }
        return genres;
    }

    public static String RelativeDateDisplay(long timeDifferenceMilliseconds) {
        long diffSeconds = timeDifferenceMilliseconds / 1000;
        long diffMinutes = timeDifferenceMilliseconds / (60 * 1000);
        long diffHours = timeDifferenceMilliseconds / (60 * 60 * 1000);
        long diffDays = timeDifferenceMilliseconds / (60 * 60 * 1000 * 24);
        long diffWeeks = timeDifferenceMilliseconds / (60 * 60 * 1000 * 24 * 7);
        long diffMonths = (long) (timeDifferenceMilliseconds / (60 * 60 * 1000 * 24 * 30.41666666));
        long diffYears = timeDifferenceMilliseconds / ((long) 60 * 60 * 1000 * 24 * 365);

        if (diffMinutes < 1) {
            return "הרגע";
        } else if (diffHours < 1) {
            if (diffMinutes == 1)
                return "לפני דקה";
            return "לפני " + diffMinutes + " דקות";
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
            return "לפני " + diffMonths + " חודשים";
        } else {
            if (diffYears == 1)
                return "לפני שנה";
            if (diffYears == 2)
                return "לפני שנתיים";
            return "לפני " + diffYears + " שנים";
        }
    }

    public static boolean isBookFromGenre(Book book, String g) {
        if (g.equals(""))
            return true;

        ArrayList<String> generes = new ArrayList<>();
        switch (g) {
            case "הרפתקאות":
                generes.add("סדרת הרפתקאותיו של מייקל ויי");
                generes.add("הרפתקאות לילדים");
                generes.add("הרפתקאות");
                generes.add("סדרת הקאובויים");
                generes.add("סדרת הנסיכה סופיה הראשונה");
                generes.add("סדרת פרנקלין הצב");
                generes.add("סיפורי מסע");
                generes.add("מסעות לילדים");
                generes.add("פיות");
                generes.add("ספורט לילדים");
                generes.add("סדרת להתבגר");
                generes.add("קומיקס לילדים");
                generes.add("לוחמים");
                generes.add("סדרת טולי תעלולי");
                generes.add("דינוזאורים");
                generes.add("סדרת ספטימוס היפ");
                generes.add("סדרת סיירת המדע");
                generes.add("אומץ לב");
                generes.add("גיבורות בלתי נשכחות");
                generes.add("ילדי הפרחים");
                generes.add("סדרת השרביט והחרב");
                generes.add("אוטו פיקשן");
                generes.add("סדרת בלי מעצורים - 39 רמזים");
                generes.add("סרטי דיסני");
                generes.add("נסיכות");
                generes.add("סדרת שר הטבעות");
                generes.add("סדרות אנימציה");
                generes.add("סדרת בני לוריאן");
                generes.add("סדרת בגידה - 39 רמזים");
                generes.add("FBI");
                break;
            case "מדע בדיוני":
                generes.add("פנטזיה");
                generes.add("מדע בדיוני לילדים");
                generes.add("מדע בדיוני צעיר");
                generes.add("פנטזיה ישראלית");
                generes.add("מותחן מדע בדיוני");
                generes.add("מד\"ב ופנטזיה");
                generes.add("טרילוגיית העולם שמעבר");
                generes.add("מבוסס על סיפורי אגדות");
                generes.add("סדרת בני לוריאן");
                generes.add("מדע בדיוני לילדים");
                generes.add("סדרת עולמטה");
                generes.add("סדרת השרביט והחרב");
                generes.add("מותחן מדע בדיוני");
                generes.add("סדרת פייבלהייבן");
                generes.add("סדרת שר הטבעות");
                generes.add("סדרת המעבר");
                generes.add("ממלכות קסומות");
                generes.add("מדע בדיוני");
                generes.add("דמיון וקסם");
                generes.add("רומנטיקה על-טבעית");
                generes.add("מותחן על-טבעי");
                generes.add("ללכת לאיבוד");
                generes.add("תולעי ספרים");
                generes.add("מיתולוגיה");
                generes.add("פנטזיה ישראלית");
                generes.add("ערפדים");
                generes.add("גיבורי על");
                generes.add("סגולות");
                generes.add("חדי קרן");
                generes.add("סדרת קהילים נגד וספרים");
                generes.add("סדרת הקמע");
                generes.add("סדרת ספטימוס היפ");
                generes.add("סדרת צמרמורת");
                generes.add("פנטזיה צעירה");
                break;

            case "היסטוריה":
                generes.add("היסטוריה");
                generes.add("היסטוריה לילדים");
                generes.add("היסטוריה ופוליטיקה");
                generes.add("היסטוריה אלטרנטיבית");
                generes.add("היסטוריה של העת העתיקה וימי הביניים");
                generes.add("דור שלישי לשואה");
                generes.add("חסידי אומות העולם");
                generes.add("יהדות הונגריה");
                generes.add("קובה");
                generes.add("ציונות");
                generes.add("יהדות גרמניה");
                generes.add("סן פרנסיסקו");
                generes.add("צרפת");
                generes.add("סין");
                generes.add("קנדה");
                generes.add("חגים לילדים");
                generes.add("תאילנד");
                generes.add("העלייה השנייה");
                generes.add("בודהיזם");
                generes.add("קוריאה");
                generes.add("ישראל");
                generes.add("אפגניסטן");
                generes.add("מרי בשואה");
                generes.add("המזרח הרחוק");
                generes.add("אנגליה הויקטוריאנית");
                generes.add("גרמניה");
                generes.add("ארץ ישראל");
                generes.add("היסטוריה יהודית");
                generes.add("מלחמת יום כיפור");
                generes.add("סדרת מנהרת הזמן");
                generes.add("מלחמת העצמאות");
                generes.add("רומן היסטורי");
                generes.add("העלייה הראשונה");
                generes.add("המלחמה הקרה");
                generes.add("ילדים בשואה");
                generes.add("ניצולי שואה");
                generes.add("נאצים ועוזריהם");
                generes.add("רב־תרבותיות לילדים");
                generes.add("נשים בשואה");
                generes.add("יהדות המזרח");
                generes.add("שואה מזוית לא יהודית");
                generes.add("פילוסופיה עתיקה");
                generes.add("מבוסס על סיפורי התנ\"ך");
                generes.add("מלחמות ישראל");
                generes.add("שואה וגבורה");
                generes.add("מיתולוגיה לילדים");
                generes.add("דור שני לשואה");
                generes.add("סיפורים של חיילים");
                generes.add("היסטוריה של העת העתיקה וימי הביניים");
                generes.add("משפחות בשואה");
                generes.add("תולדות ישראל");
                generes.add("מלחמת העולם הראשונה");
                generes.add("שואה - העולם שלפני");
                generes.add("מלחמת לבנון");
                generes.add("מסע בזמן");
                generes.add("קבצי מכתבים");
                generes.add("איראן");
                generes.add("דינוזאורים");
                generes.add("ארגנטינה");
                generes.add("העת העתיקה");
                generes.add("זכרונות וביוגרפיה");
                generes.add("שנות השמונים");
                generes.add("פלסטינים");
                generes.add("רנסנס והעת החדשה");
                generes.add("קולוניאליזם");
                generes.add("הגירה");
                generes.add("המאה ה־19");
                generes.add("היסטוריה");
                generes.add("ההתנתקות");
                generes.add("דרום ארה\"ב");
                generes.add("פריז");
                generes.add("לוחמים");
                generes.add("סדרת נוכרייה (זרה)");
                break;

            case "אהבה":
                generes.add("אהבה ראשונה");
                generes.add("אהבה נכזבת");
                generes.add("אהבה");
                generes.add("אהבה מאוחרת");
                generes.add("מותחן רומנטי");
                generes.add("רומן רומנטי");
                generes.add("אהבה");
                generes.add("סדרת גיבורים אמיתיים");
                generes.add("רומן גרפי צעיר");
                generes.add("סוד רומנטי");
                generes.add("רומנטיקה בהוליווד");
                generes.add("רומנטיקה עכשווית");
                generes.add("רומנטיקת אהבה/שנאה");
                generes.add("הזדמנות נוספת לרומנטיקה");
                generes.add("רומנטיקה ספורטיבית");
                generes.add("תעלומה רומנטית");
                generes.add("רומנטיקה אסורה");
                generes.add("מתאבקים רומנטים");
                generes.add("רומנטיקה תנ\"כית");
                generes.add("אהבה בשואה");
                generes.add("אהבה ממבט ראשון");
                generes.add("אהבה ממבט שני");
                generes.add("להיות מתבגר");
                generes.add("חג מולד רומנטי");
                generes.add("רומנטיקה בפריז");
                generes.add("בריטים רומנטיים");
                generes.add("קומדיה רומנטית");
                generes.add("רומנטיקה במאפיה");
                generes.add("רומנטיקה בעבודה");
                generes.add("רומנטיקה על-טבעית");
                generes.add("רומנים קצרצרים");
                generes.add("מיליונרים רומנטיים");
                generes.add("רומנטיקה שמחממת את הלב");
                generes.add("רומן משפחתי");
                generes.add("רומנטיקה גדולה ויפה");
                generes.add("אהבה ממבט ראשון");
                generes.add("צרפתים רומנטיים");
                generes.add("רומן פסיכולוגי");
                generes.add("רומנטיקה ממבט ראשו");
                generes.add("רוסים רומנטיים");
                generes.add("סדרת יומני הנסיכה");
                break;

            case "מתח":
                generes.add("מתח מושלג");
                generes.add("מתח צעיר");
                generes.add("סדרת סודות אפלים");
                generes.add("המוסד");
                generes.add("בלש לילדים");
                generes.add("מותחן משפטי");
                generes.add("מאפיה");
                generes.add("\"שב\"כ");
                generes.add("תעלומה");
                generes.add("טרילוגיות וסדרות מתח");
                generes.add("מתח ישראלי");
                generes.add("דואטים, טרילוגיות וסדרות מתח");
                generes.add("מתח");
                generes.add("מותחן מדע בדיוני");
                generes.add("מותחן עתידני");
                generes.add("מתח ופעולה");
                generes.add("מותחן פשע");
                generes.add("סדרת משימה עולמית");
                generes.add("קרימינולוגיה");
                generes.add("בלשים ישראליים");
                generes.add("מותחן חופשה");
                generes.add("אקשן ישראלי");
                generes.add("מותחן שוד");
                generes.add("מותחן על-טבעי");
                generes.add("אקשן");
                generes.add("CIA");
                generes.add("פשע אמיתי");
                generes.add("עמים ילידים");
                generes.add("לוחמים");
                generes.add("רצח");
                generes.add("מותחן סקנדינבי");
                generes.add("סדרת בני לוריאן");
                generes.add("מותחן פסיכולוגי");
                generes.add("סדרת שרלוק הולמס");
                generes.add("בלשי");
                generes.add("מותחן פוליטי");
                break;

            case "אימה":
                generes.add("אימה");
                generes.add("מותחן אימה");
                generes.add("רצח");
                break;


            case "מדע":
                generes.add("מדעים");
                generes.add("מדע לילדים");
                generes.add("סדרת סיירת המדע");
                generes.add("מדע בדיוני");
                generes.add("מדע פופולארי");
                generes.add("מוח");
                generes.add("טבעונות");
                generes.add("בורסה");
                generes.add("כסף");
                generes.add("סדרת ממציאים ומגלים");
                generes.add("פוליטיקה");
                generes.add("דיפלומטיה");
                generes.add("יוגה ומיינדפולנס לילדים");
                generes.add("אוריינות לילדים");
                generes.add("כלכלה התנהגותית");
                generes.add("נפש");
                generes.add("דינוזאורים");
                generes.add("שירה חברתית פוליטית");
                generes.add("ארכיטקטורה");
                generes.add("כלכלה לילדים");
                generes.add("פיזיקה");
                generes.add("אנתרופולוגיה");
                generes.add("הייטק ודיגיטל");
                generes.add("מתמטיקה");
                generes.add("קבלת החלטות");
                generes.add("רפואה");
                generes.add("מספרים ראשונים");
                generes.add("מדעים");
                generes.add("ספרים עם זיקה לטבע");
                generes.add("אקולוגיה לילדים");
                generes.add("גוף האדם");
                generes.add("עתידנות");
                generes.add("חנונים");
                break;

            case "קומדיה":
                generes.add("קומדיה");
                generes.add("קומדיה רומנטית");
                generes.add("ילדי הקומדיה");
                generes.add("ספרים מצחיקים");
                generes.add("הומור שחור");
                generes.add("חנונים");
                generes.add("הומור");
                generes.add("הומור ונונסנס לילדים");
                generes.add("סאטירה");
                generes.add("קומדיה רומנטית");
                generes.add("הומור בריטי");
                generes.add("קומיקס לילדים");
                generes.add("ספרים מצחיקים");
                break;

            case "דרמה":
                generes.add("דרמה");
                generes.add("ספרים מרגשים");
                generes.add("סדרת יומני הנסיכה");
                generes.add("נפש");
                generes.add("מבעד לעיני ילדים");
                generes.add("סדרת המאבק שלי");
                generes.add("יתומים");
                generes.add("להיות מתבגר");
                generes.add("חטיפה");
                generes.add("שכול");
                generes.add("הורים מתגרשים");
                generes.add("זהות מגדרית");
                generes.add("סאגה משפחתית");
                generes.add("אבות ובנים");
                generes.add("התבגרות");
                generes.add("נוער בסיכון");
                generes.add("פרגמנטים");
                generes.add("העצמה נשית");
                generes.add("נוער בסיכון");
                generes.add("סרטן והחלמה");
                generes.add("מודעות");
                generes.add("שמנופוביה");
                generes.add("סליחות וחשבונות נפש");
                generes.add("מבוסס על שייקספיר");
                generes.add("אימוץ לילדים");
                generes.add("רגשות לילדים");
                generes.add("אוטיזם");
                generes.add("שירות צבאי");
                generes.add("ערבים ישראלים");
                generes.add("דיכאון");
                generes.add("התאבדות");
                generes.add("גאווה");
                generes.add("ספרות נשית");
                generes.add("אלימות במשפחה");
                generes.add("דילמות של ילדים");
                break;

            default:
                return false;

        }


        if (Collections.disjoint(book.getGenres(), generes))
            return false;
        return true;
    }


    public static void addToEachBookTheFieldGenres() {

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
                        bookRef.set(book);
                    }

                }
            }
        });
    }

    public static void addFieldToUsers() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference requestCollectionRef = db.collection("Users");
        requestCollectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                }
            }
        });
    }

    public static void addFieldToReviews() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference requestCollectionRef = db.collection("Reviews");
        requestCollectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        Review review = document.toObject(Review.class);
                        final DocumentReference reviewRef = FirebaseFirestore.getInstance().collection("Reviews").document(review.getReview_id());
                        reviewRef.update("is_notify", true);
                    }
                }
            }
        });
    }

    public static void deleteDoubleBooks() {

        final ArrayList<Book> list = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference requestCollectionRef = db.collection("Books");
        requestCollectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        Book book = document.toObject(Book.class);
                        if (!(list.contains(book)))
                            list.add(book);
                        else {
                            final DocumentReference bookRef = FirebaseFirestore.getInstance().collection("Books").document(book.getId());
                            bookRef.delete();
                        }
                    }
                }
            }
        });
    }


    /**
     * ******************************** OnClick Listeners ****************************************
     */

    public static class OpenProfileOnClick implements View.OnClickListener {

        private Context mContext;
        private String user_email;

        public OpenProfileOnClick(Context context, String user_email) {
            this.mContext = context;
            this.user_email = user_email;
        }

        @Override
        public void onClick(View v) {
            if (user_email.equals(FirebaseAuth.getInstance().getCurrentUser().getEmail()))
                ((MainActivity) mContext).loadFragment(new ProfileFragment());
            else
                ((MainActivity) mContext).addFragment(new PublicProfileFragment(user_email));
        }
    }

    public static class OpenBookOnClick implements View.OnClickListener {

        private Context mContext;
        private String book_title;

        public OpenBookOnClick(Context context, String book_title) {
            this.mContext = context;
            this.book_title = book_title;
        }

        @Override
        public void onClick(View v) {
            Query bookRef = FirebaseFirestore.getInstance().collection("Books").whereEqualTo("title", this.book_title);
            bookRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot doc : task.getResult()) {
                            ((MainActivity) mContext).addFragment(new BookFragment(doc.toObject(Book.class)));
                            break;
                        }
                    } else
                        Toast.makeText(mContext, "הספר לא קיים יותר במאגר", Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

    public static class OpenChallengeOnBookOnClick implements View.OnClickListener {

        private Context mContext;
        private String question_content;
        private ArrayList<String> possible_answers;
        private String right_answer;
        private String book_title;

        public OpenChallengeOnBookOnClick(Context context,String book_title,String question_content, ArrayList<String> possible_answers, String right_answer) {
            this.mContext = context;
            this.question_content = question_content;
            this.possible_answers=possible_answers;
            this.right_answer = right_answer;
            this.book_title= book_title;
        }

        @Override
        public void onClick(View v) {
            ((MainActivity) mContext).addFragment(new ChallengeFragment(book_title,question_content,possible_answers,right_answer));
            Toast.makeText(mContext, "HOLA", Toast.LENGTH_SHORT).show();
                }
            }




}









