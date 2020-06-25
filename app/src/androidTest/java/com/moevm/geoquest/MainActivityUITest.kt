package com.moevm.geoquest

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.EnvironmentVariables


class MainActivityUITest {

    @get:Rule
    var activityRule: ActivityTestRule<MainActivity>
            = ActivityTestRule(MainActivity::class.java)


    @Test
    fun test_elementsIsDisplayed()
    {
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
    fun test_QuestsFragmentElements()
    {
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
    fun test_MapFragmentElements()
    {
        onView(withId(R.id.bottom_navigation))
            .check(matches(isDisplayed()))
        onView(withId(R.id.bottom_navigation_map))
            .check(matches(isDisplayed()))
            .perform(click())   // go to MainActivity.MapFragment
        // check it
        onView(withId(R.id.mapFragment))
            .check(matches(isDisplayed()))
    }


    @Test
    fun test_ProfileFragmentElements()
    {
        onView(withId(R.id.bottom_navigation))
            .check(matches(isDisplayed()))
        onView(withId(R.id.bottom_navigation_profile))
            .check(matches(isDisplayed()))
            .perform(click())   // go to MainActivity.ProfileFragment
        // check it
        onView(withId(R.id.profile_title))
            .check(matches(isDisplayed()))
        onView(withId(R.id.completed_quests_title))
            .check(matches(isDisplayed()))
        onView(withId(R.id.exit_button))
            .check(matches(isDisplayed()))
    }


    @Test
    fun test_InfoFromQuestList()
    {
        onView(withId(R.id.bottom_navigation))
            .check(matches(isDisplayed()))
        onView(withId(R.id.bottom_navigation_quests))
            .check(matches(isDisplayed()))
            .perform(click())   // go to MainActivity.QuestFragment
        onView(withId(R.id.info_button))
            .check(matches(isDisplayed()))
            .perform(click())   // go to InfoActivity
        // check it
        onView(withId(R.id.title))
            .check(matches(isDisplayed()))
        onView(withId(R.id.text_info))
            .check(matches(isDisplayed()))
        onView(withId(R.id.back_button))
            .check(matches(isDisplayed()))
            .perform(click())   // return to MainActivity.QuestFragment
        // check MainActivity.QuestFragment
        onView(withId(R.id.quest_list_title))
            .check(matches(isDisplayed()))
    }


    @Test
    fun test_SelectQuestAndApprove()
    {
        onView(withId(R.id.bottom_navigation))
            .check(matches(isDisplayed()))
        onView(withId(R.id.bottom_navigation_quests))
            .check(matches(isDisplayed()))
            .perform(click())   // go to MainActivity.QuestFragment
        // check it
        onView(withText("Name 1"))
            .check(matches(isDisplayed()))
            .perform(click())   // select quest. go to dialog
        onView(withId(android.R.id.button1))
            .perform(click())   // "yes" in dialog and return MainActivity.MapFragment
        // check it
        onView(withId(R.id.mapFragment))
            .check(matches(isDisplayed()))
        // return to MainActivity.QuestFragment and check current quest
        onView(withId(R.id.bottom_navigation_quests))
            .check(matches(isDisplayed()))
            .perform(click())
        onView(withId(R.id.give_up_quest))
            .check(matches(isDisplayed()))
        onView(withId(R.id.current_quest_container))
            .check(matches(isDisplayed()))
    }


    @Test
    fun test_CheckSelectedQuest()
    {
        onView(withId(R.id.bottom_navigation_quests))
            .check(matches(isDisplayed()))
            .perform(click())   // go to MainActivity.QuestFragment
        // check it
        onView(withText("Name 1"))
            .check(matches(isDisplayed()))
            .perform(click())   // select quest. go to dialog
        onView(withId(android.R.id.button1))
            .perform(click())   // "yes" in dialog and return MainActivity.MapFragment
        // check it
        onView(withId(R.id.mapFragment))
            .check(matches(isDisplayed()))
        onView(withId(R.id.bottom_navigation_quests))
            .check(matches(isDisplayed()))
            .perform(click())   // return to MainActivity.QuestFragment
        onView(withId(R.id.current_quest_container))
            .check(matches(isDisplayed()))
        onView(withText(R.string.give_up_quest))
            .check(matches(isDisplayed()))
        onView(withId(R.id.current_quest_container))
            .check(matches(isDisplayed()))
    }


    @Test
    fun test_SelectQuestAndCancel()
    {
        onView(withId(R.id.bottom_navigation))
            .check(matches(isDisplayed()))
        onView(withId(R.id.bottom_navigation_quests))
            .check(matches(isDisplayed()))
            .perform(click())   // go to MainActivity.QuestFragment
        // check it
        onView(withText("Name 1"))
            .check(matches(isDisplayed()))
            .perform(click())   // select quest. go to dialog
        onView(withId(android.R.id.button2))
            .perform(click())   // "no" in dialog and return MainActivity.QuestFragment
        // check it, no current quest
        onView(withId(R.id.quest_list_title))
            .check(matches(isDisplayed()))
        onView(withId(R.id.info_button))
            .check(matches(isDisplayed()))
        onView(withId(R.id.current_quest_container))
            .check(matches(not(isDisplayed())))
        onView(withText(R.string.give_up_quest))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.current_quest_container))
            .check(matches(not(isDisplayed())))
    }


    @Test
    fun test_SelectQuestAndGiveUp()
    {
        onView(withId(R.id.bottom_navigation_quests))
            .check(matches(isDisplayed()))
            .perform(click())   // go to MainActivity.QuestFragment
        // check it
        onView(withText("Name 1"))
            .check(matches(isDisplayed()))
            .perform(click())   // select quest. go to dialog
        onView(withId(android.R.id.button1))
            .perform(click())   // "yes" in dialog and return MainActivity.MapFragment
        // check it
        onView(withId(R.id.mapFragment))
            .check(matches(isDisplayed()))
        onView(withId(R.id.bottom_navigation_quests))
            .check(matches(isDisplayed()))
            .perform(click())   // return to MainActivity.QuestFragment
        onView(withId(R.id.current_quest_container))
            .check(matches(isDisplayed()))
        onView(withText(R.string.give_up_quest))
            .check(matches(isDisplayed()))
            .perform(click())   // giveup quest. go to dialog
        onView(withId(android.R.id.button1))
            .perform(click())   // "yes" in dialog and return MainActivity.QuestFragment
        onView(withId(R.id.current_quest_container))
            .check(matches(not(isDisplayed())))
    }


    @Test
    fun test_SeeRecordsTable()
    {
        onView(withId(R.id.bottom_navigation_profile))
            .check(matches(isDisplayed()))
            .perform(click())
        onView(withText("Completed Quest Name"))
            .check(matches(isDisplayed()))
            .perform(click())   // go to Records table
        //check it
        onView(withId(R.id.title))
            .check(matches(isDisplayed()))
        onView(withId(R.id.back_button))
            .check(matches(isDisplayed()))
            .perform(click())   // return to MainActivity.ProfileFragment
        //check it
        onView(withId(R.id.profile_title))
            .check(matches(isDisplayed()))
        onView(withId(R.id.completed_quests_title))
            .check(matches(isDisplayed()))
    }


    @Test
    fun test_Logout()
    {
        onView(withId(R.id.bottom_navigation))
            .check(matches(isDisplayed()))
        onView(withId(R.id.bottom_navigation_profile))
            .check(matches(isDisplayed()))
            .perform(click())   // go to MainActivity.ProfileFragment
        // check it
        onView(withId(R.id.profile_title))
            .check(matches(isDisplayed()))
        onView(withId(R.id.exit_button))
            .check(matches(isDisplayed()))
            .perform(click())   // go to return to LoginActivity
        // check LoginActivity
        onView(withId(R.id.login_title))
            .check(matches(isDisplayed()))
    }

}
