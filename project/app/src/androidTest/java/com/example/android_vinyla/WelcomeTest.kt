package com.example.android_vinyla

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.android_vinyla.R
import com.example.android_vinyla.screens.welcome.WelcomeFragment
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class WelcomeTest {

    private lateinit var scenario: FragmentScenario<WelcomeFragment>

    @Before
    fun setup() {
        scenario = launchFragmentInContainer<WelcomeFragment>()
        scenario.moveToState(Lifecycle.State.STARTED)
    }

    // This test will sometimes fail, can not change sequence of tests, ...
    // ... so an active user could already be logged in at this paint, ...
    // ... making this test fail. Unable to bypass with try catch.
    @Test
    fun welcomeFragmentVisible() {
        Espresso.onView(ViewMatchers.withId(R.id.title_layout)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}