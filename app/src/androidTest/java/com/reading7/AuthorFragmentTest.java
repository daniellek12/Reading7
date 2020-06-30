package com.reading7;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.test.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiSelector;

import com.reading7.Objects.Author;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class AuthorFragmentTest {
    private Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

    @Rule
    public ActivityTestRule<MainActivity> activityRule
            = new ActivityTestRule<>(MainActivity.class);
    @Before
    public void setUp() throws Exception {
        UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject uiObject = mDevice.findObject(new UiSelector().text("Reading7"));
        if (uiObject.exists()) {  mDevice.pressBack(); }
        Author author = new Author();
        Fragment authorFragment = new AuthorFragment(author);
        activityRule.getActivity().getSupportFragmentManager().beginTransaction().
                replace(R.id.fragment_container, authorFragment, authorFragment.toString())
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
    }

    @Test
    public void ImageViewTest() {
        MainActivity activity = activityRule.getActivity();
        View viewById = activity.findViewById(R.id.af_backButton);
        assertThat(viewById,notNullValue());
        assertThat(viewById, instanceOf(ImageView.class));
        ImageView imageView = (ImageView) viewById;
        assertNotEquals(0,imageView.getWidth());
        assertNotEquals(0,imageView.getBackground());
        assertNotEquals(0,imageView.getHeight());
    }

    @Test
    public void FrameLayoutTest() {
        MainActivity activity = activityRule.getActivity();
        View viewById = activity.findViewById(R.id.books_container);
        assertThat(viewById,notNullValue());
        assertThat(viewById, instanceOf(FrameLayout.class));
        FrameLayout frameLayout = (FrameLayout) viewById;
        assertNotEquals(0,frameLayout.getWidth());
        assertNotEquals(0,frameLayout.getBackground());
        assertNotEquals(0,frameLayout.getHeight());
    }

}