package com.reading7;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
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
public class EditProfileFragmentTest {

    @Rule
    public ActivityTestRule<MainActivity> activityRule
            = new ActivityTestRule<>(MainActivity.class);
    @Before
    public void setUp() {
        UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject uiObject = mDevice.findObject(new UiSelector().text("Reading7"));
        if (uiObject.exists()) {  mDevice.pressBack(); }
        Fragment editProfileFragment = new EditProfileFragment(true);
        activityRule.getActivity().getSupportFragmentManager().beginTransaction().
                replace(R.id.fragment_container, editProfileFragment, editProfileFragment.toString())
                .commit();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void FirstImageButtonTest() {
        MainActivity activity = activityRule.getActivity();
        View viewById = activity.findViewById(R.id.backButton);
        assertThat(viewById,notNullValue());
        assertThat(viewById, instanceOf(ImageButton.class));
        ImageButton imageButton = (ImageButton) viewById;
        assertNotEquals(0,imageButton.getWidth());
        assertNotEquals(0,imageButton.getBackground());
        assertNotEquals(0,imageButton.getHeight());
    }


    @Test
    public void TextViewTest() {
        MainActivity activity = activityRule.getActivity();
        View viewById = activity.findViewById(R.id.save);
        assertThat(viewById,notNullValue());
        assertThat(viewById, instanceOf(TextView.class));
        TextView textView = (TextView) viewById;
        assertNotEquals(0, textView.getWidth());
        assertNotEquals(0, textView.getBackground());
        assertNotEquals(0,textView.getHeight());
        assertEquals(R.string.save, textView.getText().toString());
    }

    @Test
    public void SecondImageButtonTest() {
        MainActivity activity = activityRule.getActivity();
        View viewById = activity.findViewById(R.id.editIcon);
        assertThat(viewById,notNullValue());
        assertThat(viewById, instanceOf(ImageButton.class));
        ImageButton imageButton = (ImageButton) viewById;
        assertNotEquals(0,imageButton.getWidth());
        assertNotEquals(0,imageButton.getBackground());
        assertNotEquals(0,imageButton.getHeight());
    }

    @Test
    public void FirstiewTest() {
        MainActivity activity = activityRule.getActivity();
        View viewById = activity.findViewById(R.id.divider12);
        assertThat(viewById,notNullValue());
        assertThat(viewById, instanceOf(View.class));
        assertNotEquals(0,viewById.getWidth());
        assertNotEquals(0,viewById.getBackground());
        assertNotEquals(0,viewById.getHeight());
    }

    @Test
    public void FirstEditText() {
        MainActivity activity = activityRule.getActivity();
        View viewById = activity.findViewById(R.id.name_edit);
        assertThat(viewById,notNullValue());
        assertThat(viewById, instanceOf(EditText.class));
        EditText editText = (EditText) viewById;
        assertNotEquals(0,editText.getWidth());
        assertNotEquals(0,editText.getBackground());
        assertNotEquals(0,editText.getHeight());
        assertEquals(R.string.user_name, editText.getHint().toString());
    }

    @Test
    public void SecondViewTest() {
        MainActivity activity = activityRule.getActivity();
        View viewById = activity.findViewById(R.id.divider15);
        assertThat(viewById,notNullValue());
        assertThat(viewById, instanceOf(View.class));
        assertNotEquals(0,viewById.getWidth());
        assertNotEquals(0,viewById.getBackground());
        assertNotEquals(0,viewById.getHeight());
    }

    @Test
    public void SecondEditText() {
        MainActivity activity = activityRule.getActivity();
        View viewById = activity.findViewById(R.id.birth_date_edit);
        assertThat(viewById,notNullValue());
        assertThat(viewById, instanceOf(EditText.class));
        EditText editText = (EditText) viewById;
        assertNotEquals(0,editText.getWidth());
        assertNotEquals(0,editText.getBackground());
        assertNotEquals(0,editText.getHeight());
        assertEquals(R.string.age, editText.getHint().toString());

    }

    @Test
    public void ThirdViewTest() {
        MainActivity activity = activityRule.getActivity();
        View viewById = activity.findViewById(R.id.divider16);
        assertThat(viewById,notNullValue());
        assertThat(viewById, instanceOf(View.class));
        assertNotEquals(0,viewById.getWidth());
        assertNotEquals(0,viewById.getBackground());
        assertNotEquals(0,viewById.getHeight());
    }
    @Test
    public void ForthViewTest() {
        MainActivity activity = activityRule.getActivity();
        View viewById = activity.findViewById(R.id.divider18);
        assertThat(viewById,notNullValue());
        assertThat(viewById, instanceOf(View.class));
        assertNotEquals(0,viewById.getWidth());
        assertNotEquals(0,viewById.getBackground());
        assertNotEquals(0,viewById.getHeight());
    }

}