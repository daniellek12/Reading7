package com.reading7;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.test.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiSelector;

import com.reading7.Objects.Book;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class BookFragmentTest {

    @Rule
    public ActivityTestRule<MainActivity> activityRule
            = new ActivityTestRule<>(MainActivity.class);
    @Before
    public void setUp() {
        UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject uiObject = mDevice.findObject(new UiSelector().text("Reading7"));
        if (uiObject.exists()) {  mDevice.pressBack(); }
        Book book = new Book();
        Fragment bookFragment = new BookFragment(book);
        activityRule.getActivity().getSupportFragmentManager().beginTransaction().
                replace(R.id.fragment_container, bookFragment, bookFragment.toString())
                .commit();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void TextViewTest() {
        MainActivity activity = activityRule.getActivity();
        View viewById = activity.findViewById(R.id.toolbar_title);
        assertThat(viewById,notNullValue());
        assertThat(viewById, instanceOf(TextView.class));
        TextView textView = (TextView) viewById;
        assertNotEquals(0, textView.getWidth());
        assertNotEquals(0, textView.getBackground());
        assertNotEquals(0,textView.getHeight());
        assertEquals(R.string.book_details, textView.getText().toString());
    }

    @Test
    public void FirstImageViewTest() {
        MainActivity activity = activityRule.getActivity();
        View viewById = activity.findViewById(R.id.editButton);
        assertThat(viewById,notNullValue());
        assertThat(viewById, instanceOf(ImageView.class));
        ImageView imageView = (ImageView) viewById;
        assertNotEquals(0,imageView.getWidth());
        assertNotEquals(0,imageView.getBackground());
        assertNotEquals(0,imageView.getHeight());
    }

    @Test
    public void SecondImageViewTest() {
        MainActivity activity = activityRule.getActivity();
        View viewById = activity.findViewById(R.id.saveButton);
        assertThat(viewById,notNullValue());
        assertThat(viewById, instanceOf(ImageView.class));
        ImageView imageView = (ImageView) viewById;
        assertNotEquals(0,imageView.getWidth());
        assertNotEquals(0,imageView.getBackground());
        assertNotEquals(0,imageView.getHeight());
    }

    @Test
    public void ImageButtonTest() {
        MainActivity activity = activityRule.getActivity();
        View viewById = activity.findViewById(R.id.bookBackButton);
        assertThat(viewById,notNullValue());
        assertThat(viewById, instanceOf(ImageButton.class));
        ImageButton imageButton = (ImageButton) viewById;
        assertNotEquals(0,imageButton.getWidth());
        assertNotEquals(0,imageButton.getBackground());
        assertNotEquals(0,imageButton.getHeight());
    }

}