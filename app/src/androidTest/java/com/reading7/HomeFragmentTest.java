package com.reading7;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.test.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiSelector;

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
public class HomeFragmentTest {

    @Rule
    public ActivityTestRule<MainActivity> activityRule
            = new ActivityTestRule<>(MainActivity.class);
    @Before
    public void setUp()  {
        UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject uiObject = mDevice.findObject(new UiSelector().text("Reading7"));
        if (uiObject.exists()) {  mDevice.pressBack(); }
        Fragment HomeFragmentTest = new HomeFragment();
        activityRule.getActivity().getSupportFragmentManager().beginTransaction().
                replace(R.id.fragment_container, HomeFragmentTest, HomeFragmentTest.toString())
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
        assertEquals(R.string.app_name, textView.getText().toString());
    }

    public void ProgressBarTest() {
        MainActivity activity = activityRule.getActivity();
        View viewById = activity.findViewById(R.id.home_progress_bar);
        assertThat(viewById,notNullValue());
        assertThat(viewById, instanceOf(ProgressBar.class));
        ProgressBar progressBar = (ProgressBar) viewById;
        assertNotEquals(0,progressBar.getWidth());
        assertNotEquals(0,progressBar.getBackground());
        assertNotEquals(0,progressBar.getHeight());
    }
}