package com.reading7;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

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
public class SignUpActivityTest {
    private Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

    @Rule
    public ActivityTestRule<SignUpActivity> activityRule = new ActivityTestRule<>(SignUpActivity.class);

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

    public void ImageButtonTest() {
        SignUpActivity activity = activityRule.getActivity();
        View viewById = activity.findViewById(R.id.backButton);
        assertThat(viewById,notNullValue());
        assertThat(viewById, instanceOf(FrameLayout.class));
        ImageButton imageButton = (ImageButton) viewById;
        assertNotEquals(0,imageButton.getWidth());
        assertNotEquals(0,imageButton.getBackground());
        assertNotEquals(0,imageButton.getHeight());
    }

    public void LinearLayoutTest() {
        SignUpActivity activity = activityRule.getActivity();
        View viewById = activity.findViewById(R.id.email_exists);
        assertThat(viewById,notNullValue());
        assertThat(viewById, instanceOf(FrameLayout.class));
        LinearLayout linearLayout = (LinearLayout) viewById;
        assertNotEquals(0,linearLayout.getWidth());
        assertNotEquals(0,linearLayout.getBackground());
        assertNotEquals(0,linearLayout.getHeight());
    }
}