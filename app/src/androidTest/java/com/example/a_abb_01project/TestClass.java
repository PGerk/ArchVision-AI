/*
 * MIT License
 *
 * Copyright (c) 2024 RUB-SE-LAB-2024
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.example.a_abb_01project;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNot.not;

import android.view.View;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class TestClass {

    @Rule
    public ActivityScenarioRule<MainActivity> rule = new ActivityScenarioRule<>(MainActivity.class);

    /**
     * Method for waiting a certain time
     *
     * @param millis    Milliseconds to wait
     * @return          ViewAction
     */
    public static ViewAction waitFor(final long millis) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "Wait for " + millis + " milliseconds.";
            }

            @Override
            public void perform(UiController uiController, final View view) {
                uiController.loopMainThreadForAtLeast(millis);
            }
        };
    }

    /**
     * Test for setting the apikey for ChatGPT4o
     */
    @Test
    public void setApikeyForGPT4o(){
        onView(withId(R.id.nav_settings)).perform(click());
        onView(withId(R.id.spinner_recommendation)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("ChatGPT_4.o (Recommended)"))).perform(click());
        onView(withId(R.id.spinner_recommendation)).check(matches(withSpinnerText(containsString("ChatGPT_4.o (Recommended)"))));
        onView(withId(R.id.api_key_field)).perform(replaceText("APIKEY"));
        onView(withId(R.id.button_save_settings)).perform(click());
        onView(isRoot()).perform(waitFor(1000));
    }

    /**
     * Test for setting the apikey for Gemini
     */
    @Test
    public void setApikeyForGemini(){
        onView(withId(R.id.nav_settings)).perform(click());
        onView(withId(R.id.spinner_recommendation)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Gemini (Recommended)"))).perform(click());
        onView(withId(R.id.spinner_recommendation)).check(matches(withSpinnerText(containsString("Gemini (Recommended)"))));
        onView(withId(R.id.api_key_field)).perform(replaceText("APIKEY"));
        onView(withId(R.id.button_save_settings)).perform(click());
        onView(isRoot()).perform(waitFor(1000));
    }

    /**
     * Test for the navigation between the main fragments
     */
    @Test
    public void bottomNavigation() {

        onView(withId(R.id.nav_camera)).perform(click());

        onView(withId(R.id.nav_recommendation)).perform(click());

        onView(withId(R.id.nav_settings)).perform(click());

        onView(withId(R.id.nav_home)).perform(click());
    }

    /**
     * Test for camera fragment
     */
    @Test
    public void cameraFragementIsDisplayed() {
        // Zu Camera Fragment wechseln
        onView(withId(R.id.nav_camera)).perform(click());

        // Prüfen, ob alle Elemente angezeigt werden
        onView(withId(R.id.text_header_text)).check(matches(isDisplayed()));
        onView(withId(R.id.text_header_text)).check(matches(withText("Gallery")));
        onView(withId(R.id.text_header_descr)).check(matches(isDisplayed()));
        onView(withId(R.id.button_change_to_micro_fragment)).check(matches(isDisplayed()));
        onView(withId(R.id.recyclerView_photo)).check(matches(isDisplayed()));
        onView(withId(R.id.button_take_picture)).check(matches(isDisplayed()));
        onView(withId(R.id.button_upload_image)).check(matches(isDisplayed()));
        onView(withId(R.id.button_create_new_uml)).check(matches(isDisplayed()));
    }

    /**
     * Test for home fragment
     */
    @Test
    public void homeFragementIsDisplayed() {
        // Zu Home Fragment wechseln
        onView(withId(R.id.nav_home)).perform(click());

        // Prüfen, ob alle Elemente angezeigt werden
        onView(withId(R.id.text_header_text)).check(matches(isDisplayed()));
        onView(withId(R.id.text_header_text)).check(matches(withText("Software Architectures")));
        onView(withId(R.id.text_header_descr)).check(matches(isDisplayed()));
        onView(withId(R.id.recyclerView_Main_Menu)).check(matches(isDisplayed()));
        onView(withId(R.id.button_create_new_uml)).check(matches(isDisplayed()));
    }

    /**
     * Test for audio fragement
     */
    @Test
    public void audioFragementIsDisplayed() {
        // Zu Camera Fragment wechseln
        onView(withId(R.id.nav_camera)).perform(click());
        // Zu Audio Fragment wechseln
        onView(withId(R.id.button_change_to_micro_fragment)).perform(click());

        // Prüfen, ob alle Elemente angezeigt werden
        onView(withId(R.id.text_header_text)).check(matches(isDisplayed()));
        onView(withId(R.id.text_header_text)).check(matches(withText("Audio")));
        onView(withId(R.id.text_header_descr)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonSwitchToCamera)).check(matches(isDisplayed()));
        onView(withId(R.id.textTv)).check(matches(isDisplayed()));
        onView(withId(R.id.voiceBtn)).check(matches(isDisplayed()));
        onView(withId(R.id.button_create_new_uml)).check(matches(isDisplayed()));
    }

    /**
     * test for recommendation fragment
     */
    @Test
    public void recommendationFragementIsDisplayed() {
        // Zu Recommendation Fragment wechseln
        onView(withId(R.id.nav_recommendation)).perform(click());

        // Progressbar, Bild und Historiebuttons werdem nicht angezeigt
        onView(withId(R.id.progessbar_llm)).check(matches(not(isDisplayed())));
        onView(withId(R.id.imageView_result)).check(matches(isDisplayed()));
        onView(withId(R.id.button_view_right)).check(matches(not(isDisplayed())));
        onView(withId(R.id.button_view_left)).check(matches(not(isDisplayed())));

        onView(withId(R.id.cardview_button_show_plant)).check(matches(isDisplayed()));
        onView(withId(R.id.text_button_plantuml)).check(matches(isDisplayed()));
        onView(withId(R.id.textView_plantUML)).check(matches(not(isDisplayed())));

        onView(withId(R.id.cardview_button_show_adjustments)).check(matches(isDisplayed()));
        onView(withId(R.id.text_button_adjustments)).check(matches(isDisplayed()));
        onView(withId(R.id.editText_adjustment)).check(matches(not(isDisplayed())));

        onView(withId(R.id.cardview_button_show_response)).check(matches(isDisplayed()));
        onView(withId(R.id.text_button_response)).check(matches(isDisplayed()));
        onView(withId(R.id.text_response_llm)).check(matches(not(isDisplayed())));

        // Prüfen, ob alle Elemente angezeigt werden
        onView(withId(R.id.text_header_text)).check(matches(isDisplayed()));
        onView(withId(R.id.text_header_text)).check(matches(withText("Result")));
        onView(withId(R.id.text_header_descr)).check(matches(isDisplayed()));

        onView(withId(R.id.button_optimize)).check(matches(isDisplayed()));
        onView(withId(R.id.button_save_uml)).check(matches(isDisplayed()));
        onView(withId(R.id.button_send_to_llm)).check(matches(isDisplayed()));
    }

    /**
     * Test for settings fragment
     */
    @Test
    public void settingsFragementIsDisplayed() {
        // Zu Settings Fragment wechseln
        onView(withId(R.id.nav_settings)).perform(click());

        // Prüfen, ob alle Elemente angezeigt werden
        onView(withId(R.id.text_header_text)).check(matches(isDisplayed()));
        onView(withId(R.id.text_header_text)).check(matches(withText("Settings")));
        onView(withId(R.id.text_header_descr)).check(matches(isDisplayed()));
        onView(withId(R.id.spinner_recommendation)).check(matches(isDisplayed()));
        onView(withId(R.id.api_key_field)).check(matches(isDisplayed()));
        onView(withId(R.id.button_save_settings)).check(matches(isDisplayed()));
    }

    /**
     * Test for classification tag fragment
     */
    @Test
    public void tagClassificationFragementIsDisplayed() {
        // Zu Settings Fragment wechseln
        onView(withId(R.id.nav_camera)).perform(click());
        // Auf Audio umschalten
        onView(withId(R.id.button_change_to_micro_fragment)).perform(click());
        // Neues Diagramm erstellen auswählen
        onView(withId(R.id.button_create_new_uml)).perform(click());

        // Prüfen, ob alle Elemente angezeigt werden
        onView(withId(R.id.text_header_text)).check(matches(isDisplayed()));
        onView(withId(R.id.text_header_text)).check(matches(withText("Classification")));
        onView(withId(R.id.text_header_descr)).check(matches(isDisplayed()));
        onView(withId(R.id.card_view_class)).check(matches(isDisplayed()));
        onView(withId(R.id.card_view_comp)).check(matches(isDisplayed()));
        onView(withId(R.id.card_view_sequenz)).check(matches(isDisplayed()));
        onView(withId(R.id.card_view_activity)).check(matches(isDisplayed()));
        onView(withId(R.id.card_view_usecase)).check(matches(isDisplayed()));
        onView(withId(R.id.card_view_additional)).check(matches(isDisplayed()));
    }

    /**
     * Integration test for sending a request to the llm
     * Works ONLY with valid key!!!
     */
    @Test
    public void createAudioTextRequest() {
        // Api key setzen
        setApikeyForGemini();

        onView(withId(R.id.nav_home)).perform(click());
        // Auf neues Projekt klicken
        onView(withId(R.id.button_create_new_uml)).perform(click());
        // Auf Audio umschalten
        onView(withId(R.id.button_change_to_micro_fragment)).perform(click());
        // Text eingeben
        onView(withId(R.id.textTv)).perform(typeText("Es gibt eine Klasse Person mit dem String Attribute Name und dem" + " Integer Attribute Alter"), closeSoftKeyboard());
        // Neues Diagramm erstellen auswählen
        onView(withId(R.id.button_create_new_uml)).perform(click());
        // Tag auswählen
        onView(withId(R.id.card_view_class)).perform(click());
        // Auf Antwort warten
        onView(isRoot()).perform(waitFor(5000));

        // PlantUML Code testen
        onView(withId(R.id.cardview_button_show_plant)).perform(click());
        onView(withId(R.id.textView_plantUML)).check(matches(withSubstring("Person")));
        onView(withId(R.id.cardview_button_show_plant)).perform(click());

        onView(withId(R.id.cardview_button_show_response)).perform(click());
        onView(withId(R.id.text_response_llm)).check(matches(not(withText("No Response"))));
        onView(withId(R.id.cardview_button_show_response)).perform(click());
    }

    /**
     * Integration test for sending a request to the llm and sending an adjustment to the llm
     * Works ONLY with valid key!!!
     */
    @Test
    public void sendAdjustment(){
        createAudioTextRequest();
        onView(withId(R.id.cardview_button_show_adjustments)).perform(click());
        onView(withId(R.id.editText_adjustment)).perform(typeText("Fuege der Klasse Name noch ein String Attribute Vorname hinzu"), closeSoftKeyboard());
        onView(withId(R.id.cardview_button_show_adjustments)).perform(click());

        onView(withId(R.id.button_send_to_llm)).perform(click());
        // Auf Antwort warten
        onView(isRoot()).perform(waitFor(5000));

        onView(withId(R.id.cardview_button_show_plant)).perform(click());
        onView(withId(R.id.textView_plantUML)).check(matches(withSubstring("Vorname")));
        onView(withId(R.id.cardview_button_show_plant)).perform(click());
    }

    /**
     * Test for the dropdown function
     */
    @Test
    public void clickDropdown() {
        // Zu Recommendation Fragment wechseln
        onView(withId(R.id.nav_recommendation)).perform(click());

        onView(withId(R.id.cardview_button_show_plant)).perform(click());
        onView(withId(R.id.textView_plantUML)).check(matches(isDisplayed()));
        onView(withId(R.id.textView_plantUML)).check(matches(withText("No Code")));

        onView(withId(R.id.cardview_button_show_adjustments)).perform(click());
        onView(withId(R.id.editText_adjustment)).check(matches(isDisplayed()));
        onView(withId(R.id.editText_adjustment)).check(matches(withText("")));

        onView(withId(R.id.cardview_button_show_response)).perform(click());
        onView(withId(R.id.text_response_llm)).check(matches(isDisplayed()));
        onView(withId(R.id.text_response_llm)).check(matches(withText("No Response")));
    }
}