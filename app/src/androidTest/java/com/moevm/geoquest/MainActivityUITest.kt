package com.moevm.geoquest

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.rule.ActivityTestRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.EnvironmentVariables


class MainActivityUITest {

    @get:Rule
    var activityRule: ActivityTestRule<MainActivity>
            = ActivityTestRule(MainActivity::class.java)
    private val environmentVariables = EnvironmentVariables()


    @Before
    fun setUp(){
        environmentVariables.set("test_mode", "true")
    }


    @After
    fun tearDown(){
        environmentVariables.set("test_mode", "")
    }

    @Test
    fun test_elementsIsDisplayed() {
        onView(withId(R.id.bottom_navigation))
            .check(matches(isDisplayed()))
        onView(withId(R.id.bottom_navigation_quests))
            .check(matches(isDisplayed()))
        onView(withId(R.id.bottom_navigation_map))
            .check(matches(isDisplayed()))
        onView(withId(R.id.bottom_navigation_profile))
            .check(matches(isDisplayed()))
        onView(withId(R.id.fragment_container))
            .check(matches(isDisplayed()))
    }


    @Test
    fun test_QuestsFragmentElements() {
        onView(withId(R.id.bottom_navigation))
            .check(matches(isDisplayed()))
        onView(withId(R.id.bottom_navigation_quests))
            .check(matches(isDisplayed()))
            .perform(click())   // go to MainActivity.QuestFragment
        // check it
        onView(withId(R.id.quest_list_title))
            .check(matches(isDisplayed()))
        onView(withId(R.id.info_button))
            .check(matches(isDisplayed()))
    }


    @Test
    fun test_MapFragmentElements() {
        onView(withId(R.id.bottom_navigation))
            .check(matches(isDisplayed()))
        onView(withId(R.id.bottom_navigation_map))
            .check(matches(isDisplayed()))
            .perform(click())   // go to MainActivity.MapFragment
        // check it
        onView(withId(R.id.mapFragment))
            .check(matches(isDisplayed()))
    }
}
