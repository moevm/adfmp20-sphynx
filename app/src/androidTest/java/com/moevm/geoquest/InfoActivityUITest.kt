package com.moevm.geoquest

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import org.junit.Rule
import org.junit.Test


class InfoActivityUITest {

    @get:Rule
    var activityRule: ActivityTestRule<InfoActivity>
            = ActivityTestRule(InfoActivity::class.java)

    @Test
    fun test_TitleIsDisplayed() {
        onView(withId(R.id.title))
            .check(matches(isDisplayed()))
            .check(matches(withText("Как играть?")))
    }


    @Test
    fun test_InfoIsDisplayed() {
        onView(withId(R.id.text_info))
            .check(matches(isDisplayed()))
    }

}
