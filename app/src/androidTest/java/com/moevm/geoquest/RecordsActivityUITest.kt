package com.moevm.geoquest

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import org.junit.Rule
import org.junit.Test


class RecordsActivityUITest {

    @get:Rule
    var activityRule: ActivityTestRule<RecordsActivity>
            = ActivityTestRule(RecordsActivity::class.java)

    @Test
    fun test_TitleIsDisplayed() {
        onView(withId(R.id.title))
            .check(matches(isDisplayed()))
    }


    @Test
    fun test_backButton() {
        onView(withId(R.id.back_button))
            .check(matches(isDisplayed()))
            .perform(click())
    }

}
