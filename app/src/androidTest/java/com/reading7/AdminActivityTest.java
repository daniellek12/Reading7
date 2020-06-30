package com.reading7;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import androidx.test.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiSelector;

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class AdminActivityTest {
    private Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

    @Rule
    public ActivityTestRule<AdminActivity> activityRule = new ActivityTestRule<>(AdminActivity.class);

    @Before
    public void setUp() throws Exception {
        UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject uiObject = mDevice.findObject(new UiSelector().text("Reading7"));
        if (uiObject.exists()) {  mDevice.pressBack(); }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertNotEquals(0, appContext.fileList().length);
    }

    public void FrameLayoutTest() {
        AdminActivity activity = activityRule.getActivity();
        View viewById = activity.findViewById(R.id.fragment_container);
        assertThat(viewById,notNullValue());
        assertThat(viewById, instanceOf(FrameLayout.class));
        FrameLayout frameLayout = (FrameLayout) viewById;
        assertNotEquals(0,frameLayout.getWidth());
        assertNotEquals(0,frameLayout.getBackground());
        assertNotEquals(0,frameLayout.getHeight());
    }
}