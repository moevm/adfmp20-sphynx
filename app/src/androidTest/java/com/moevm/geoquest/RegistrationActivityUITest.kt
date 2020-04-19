package com.moevm.geoquest

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.Test


class RegistrationActivityUITest {

    @get:Rule
    var activityRule: ActivityTestRule<RegistrationActivity>
            = ActivityTestRule(RegistrationActivity::class.java)

    @Test
    fun test_elementsIsDisplayed() {
        onView(withId(R.id.title))
            .check(matches(isDisplayed()))
        onView(withId(R.id.login_input_text))
            .check(matches(isDisplayed()))
        onView(withId(R.id.password_input_text))
            .check(matches(isDisplayed()))
        onView(withId(R.id.password_confirmation_text))
            .check(matches(isDisplayed()))
    }


    @Test
    fun test_badRegistration() {
        onView(withId(R.id.login_input_text))
            .check(matches(isDisplayed()))
            .perform(replaceText("mail@mail.com"))
        onView(withId(R.id.password_input_text))
            .check(matches(isDisplayed()))
            .perform(replaceText("badpass"))
        onView(withId(R.id.password_confirmation_text))
            .check(matches(isDisplayed()))
            .perform(replaceText("badpass"))
        onView(withId(R.id.sign_in_button))
            .check(matches(isDisplayed()))
            .perform(click())
        onView(withText("Authentication failed."))
            .inRoot(RootMatchers.withDecorView(Matchers.not(Matchers.`is`(activityRule.activity.window.decorView))))
            .check(matches(isDisplayed()))
    }

}
