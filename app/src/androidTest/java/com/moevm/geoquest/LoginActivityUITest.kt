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


class LoginActivityUITest {

    @get:Rule
    var activityRule: ActivityTestRule<LoginActivity>
            = ActivityTestRule(LoginActivity::class.java)

    @Test
    fun test_elementsIsDisplayed() {
        onView(withId(R.id.login_title))
            .check(matches(isDisplayed()))
        onView(withId(R.id.image))
            .check(matches(isDisplayed()))
        onView(withId(R.id.login_input))
            .check(matches(isDisplayed()))
        onView(withId(R.id.login_input_text))
            .check(matches(isDisplayed()))
        onView(withId(R.id.password_input))
            .check(matches(isDisplayed()))
        onView(withId(R.id.password_input_text))
            .check(matches(isDisplayed()))
        onView(withId(R.id.sign_in_button))
            .check(matches(isDisplayed()))
        onView(withId(R.id.sign_in_google_button))
            .check(matches(isDisplayed()))
        onView(withId(R.id.sign_up_layout))
            .check(matches(isDisplayed()))
    }


    @Test
    fun test_badSignIn()
    {
        onView(withId(R.id.login_input_text))
            .check(matches(isDisplayed()))
            .perform(replaceText("notmail"))
        onView(withId(R.id.password_input_text))
            .check(matches(isDisplayed()))
            .perform(replaceText("notpass"))
        onView(withId(R.id.sign_in_button))
            .check(matches(isDisplayed()))
            .perform(click())
        onView(withText("Authentication failed."))
            .inRoot(RootMatchers.withDecorView(Matchers.not(Matchers.`is`(activityRule.activity.window.decorView))))
            .check(matches(isDisplayed()))
    }

    @Test
    fun test_goToSignUpLogin()
    {
        onView(withId(R.id.sign_up_button))
            .check(matches(isDisplayed()))
            .perform(click())   // go to RegistrationActivity
        // check it
        onView(withId(R.id.title))
            .check(matches(isDisplayed()))
        onView(withId(R.id.login_input_text))
            .check(matches(isDisplayed()))
        onView(withId(R.id.password_input_text))
            .check(matches(isDisplayed()))
        onView(withId(R.id.password_confirmation_text))
            .check(matches(isDisplayed()))
    }


}
