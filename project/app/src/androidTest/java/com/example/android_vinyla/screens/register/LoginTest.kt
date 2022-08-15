package com.example.android_vinyla.screens.register

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.android_vinyla.R
import com.example.android_vinyla.screens.login.LoginFragment
import com.example.android_vinyla.screens.welcome.WelcomeFragment
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class LoginTest {

    private lateinit var scenario: FragmentScenario<LoginFragment>

    @Before
    fun setup() {
        scenario = launchFragmentInContainer<LoginFragment>()
        scenario.moveToState(Lifecycle.State.STARTED)
    }

    @Test
    fun loginFragmentVisible() {
        Espresso.onView(ViewMatchers.withId(R.id.layout_login)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun login_incorrectCredentials_staysOnLoginScreen(){
        Espresso.onView(ViewMatchers.withId(R.id.login_email_input)).perform(ViewActions.typeText("email@example.vinyla"))
        Espresso.closeSoftKeyboard()
        Espresso.onView(ViewMatchers.withId(R.id.login_button)).perform(ViewActions.click())
        Thread.sleep(2000)
        Espresso.onView(ViewMatchers.withId(R.id.login_password_input)).check(ViewAssertions.matches(ViewMatchers.hasErrorText("The email address or password is incorrect.")))
        Espresso.onView(ViewMatchers.withId(R.id.layout_login)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}