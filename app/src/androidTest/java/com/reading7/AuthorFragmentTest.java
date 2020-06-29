package com.reading7;

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
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AuthorFragmentTest {

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

}