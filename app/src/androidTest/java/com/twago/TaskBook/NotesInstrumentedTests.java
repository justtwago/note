package com.twago.TaskBook;

import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.twago.TaskBook.NoteMain.NoteMainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class NotesInstrumentedTests {
    private String titleToBeTyped;
    private String textToBeTyped;
    @Rule
    public ActivityTestRule<NoteMainActivity> mActivityRule = new ActivityTestRule<>(NoteMainActivity.class);

    @Before
    public void initValidString() {
        titleToBeTyped = "Example title";
        textToBeTyped = "Example text to be typed into Editor and checked if UI behaves correctly. More tests will be included soon.";
    }

    @Test
    public void typeTextsAndCheckCorrectness() {
        createNote();
    }

    @Test
    public void testDeleting(){
        createNote();
        createNote();
        createNote();
        createNote();

        onView(withId(R.id.note_list_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(2, ViewActions.longClick()));
        onView(withId(R.id.note_list_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(3, ViewActions.click()));
        onView(withId(R.id.note_list_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));
        onView(withId(R.id.note_list_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.longClick()));

    }

    private void checkIfCorrectlyTyped() {
        onView(withId(R.id.title_edit_note)).check(matches(withText(titleToBeTyped)));
        onView(withId(R.id.text_edit_note)).check(matches(withText(textToBeTyped)));
    }
    private void createNote(){
        onView(withId(R.id.button_create_note)).perform(click());
        onView(withId(R.id.title_edit_note)).perform(typeText(titleToBeTyped), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.text_edit_note)).perform(typeText(textToBeTyped), ViewActions.closeSoftKeyboard());
        checkIfCorrectlyTyped();
        onView(withId(R.id.button_close_note)).perform(click());
    }
}
