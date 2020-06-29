package com.reading7;

import android.content.Context;

import androidx.test.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiSelector;

import org.junit.Before;
import org.junit.Rule;

import static org.junit.Assert.assertNotEquals;

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
}