package com.example.android_vinyla

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.android_vinyla.screens.main.MainFragment
import com.example.android_vinyla.screens.register.RegisterFragment
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class MainTest {

    private lateinit var scenario: FragmentScenario<MainFragment>

    @Before
    fun setup() {
        scenario = launchFragmentInContainer<MainFragment>()
        scenario.moveToState(Lifecycle.State.STARTED)
    }

    @Test
    fun mainFragmentVisible() {
        Espresso.onView(ViewMatchers.withId(R.id.layout_main))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun main_testSettingsButton() {
        Espresso.onView(ViewMatchers.withId(R.id.main_settings_layout))
            .check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)))

        Espresso.onView(ViewMatchers.withId(R.id.main_settings_button)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.main_settings_layout))
            .check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))

        Espresso.onView(ViewMatchers.withId(R.id.main_settings_close_imageButton))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.main_settings_layout))
            .check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)))

        Espresso.onView(ViewMatchers.withId(R.id.main_settings_button)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.main_settings_layout))
            .check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))

        Espresso.onView(ViewMatchers.withId(R.id.main_settings_layout)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.main_settings_layout))
            .check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
    }

    @Test
    fun main_testCreateStationButtonWithNoAlbumsSelected_doesNothing() {
        Espresso.onView(ViewMatchers.withId(R.id.main_create_station_button))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.main_redirect_layout))
            .check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
    }
}