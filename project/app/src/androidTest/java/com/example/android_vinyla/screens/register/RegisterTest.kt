package com.example.android_vinyla.screens.register

import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
//import androidx.test.ext.junit.rules.activityScenarioRule
import com.example.android_vinyla.MainActivity
import com.example.android_vinyla.screens.login.LoginFragment
import com.example.android_vinyla.R
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class RegisterTest {

    private lateinit var scenario: FragmentScenario<RegisterFragment>

    @Before
    fun setup() {
        scenario = launchFragmentInContainer<RegisterFragment>()
        scenario.moveToState(Lifecycle.State.STARTED)
    }

    @Test
    fun registerFragmentVisible() {
        onView(withId(R.id.layout_register)).check(matches(isDisplayed()))
    }

    @Test
    fun register_emptyEmailValidPasswords_staysAtFirstRegistrationPage(){
        onView(withId(R.id.register_email_input)).perform(typeText(""))
        onView(withId(R.id.register_password_input)).perform(typeText("Password@123"))
        onView(withId(R.id.register_password_confirmation_input)).perform(typeText("Password@123"))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.register_next_button)).perform(click())
        onView(withId(R.id.register_next_button)).check(matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        onView(withId(R.id.register_signup_button)).check(matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(withId(R.id.register_email_input)).check(matches(hasErrorText("Incorrect email!")))
    }

    // Parameterized Tests from Jupiter not available in Espresso?
    @Test
    fun register_invalidEmailsCorrectPasswords_staysAtFirstRegistrationPage() {
        val emails = arrayOf("a", "email", "email@", "email@example", "email.com", "email@.com", "@", "@example.com")
        onView(withId(R.id.register_password_input)).perform(typeText("Password@123"))
        onView(withId(R.id.register_password_confirmation_input)).perform(typeText("Password@123"))
        for(email in emails){
            onView(withId(R.id.register_email_input)).perform(clearText())
            onView(withId(R.id.register_email_input)).perform(typeText(email))
            Espresso.closeSoftKeyboard()
            onView(withId(R.id.register_next_button)).perform(click())
            onView(withId(R.id.register_next_button)).check(matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
            onView(withId(R.id.register_signup_button)).check(matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
            onView(withId(R.id.register_email_input)).check(matches(hasErrorText("Incorrect email!")))
        }
    }

    @Test
    fun register_invalidPasswords_staysAtFirstRegistrationPage(){
        val passwords = arrayOf("", "a@1", "Password@", "Password1", "PasswordPassword")
        onView(withId(R.id.register_email_input)).perform(typeText("user@example.com"))
        for(password in passwords){
            onView(withId(R.id.register_password_input)).perform(clearText())
            onView(withId(R.id.register_password_confirmation_input)).perform(clearText())
            onView(withId(R.id.register_password_input)).perform(typeText(password))
            onView(withId(R.id.register_password_confirmation_input)).perform(typeText(password))
            Espresso.closeSoftKeyboard()
            onView(withId(R.id.register_next_button)).perform(click())
            onView(withId(R.id.register_next_button)).check(matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
            onView(withId(R.id.register_signup_button)).check(matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
            onView(withId(R.id.register_password_input)).check(matches(hasErrorText("Password must meet the requirements of at least 8 characters, 1 lowercase, 1 uppercase, 1 numeric & 1 special character!")))
        }
    }

    @Test
    fun register_noMatchingPasswords_staysAtFirstRegistrationPage(){
        onView(withId(R.id.register_email_input)).perform(typeText("user@example.com"))
        onView(withId(R.id.register_password_input)).perform(typeText("Password1234@"))
        onView(withId(R.id.register_password_confirmation_input)).perform(typeText("Password"))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.register_next_button)).perform(click())
        onView(withId(R.id.register_password_confirmation_input)).check(matches(hasErrorText("The passwords do not match!")))

    }

    @Test
    fun register_emailAlreadyInUse_staysAtFirstRegistrationPage(){
        onView(withId(R.id.register_email_input)).perform(typeText("webiv@hogent.be"))
        onView(withId(R.id.register_password_input)).perform(typeText("Password1234@"))
        onView(withId(R.id.register_password_confirmation_input)).perform(typeText("Password1234@"))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.register_next_button)).perform(click())
        Thread.sleep(2000)
        onView(withId(R.id.register_email_input)).check(matches(hasErrorText("The given email address is already in use!")))
    }

    @Test
    fun register_validCredentials_goesToSecondRegistrationPage(){
        onView(withId(R.id.register_email_input)).perform(typeText("hopefullynobodyusesthisemail@example.vinyla"))
        onView(withId(R.id.register_password_input)).perform(typeText("Password1234@"))
        onView(withId(R.id.register_password_confirmation_input)).perform(typeText("Password1234@"))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.register_next_button)).perform(click())
        Thread.sleep(2000)
        onView(withId(R.id.register_next_button)).check(matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(withId(R.id.register_signup_button)).check(matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @Test
    fun register_emptyNames_staysAtFirstRegistrationPage(){
        onView(withId(R.id.register_email_input)).perform(typeText("hopefullynobodyusesthisemail@example.vinyla"))
        onView(withId(R.id.register_password_input)).perform(typeText("Password1234@"))
        onView(withId(R.id.register_password_confirmation_input)).perform(typeText("Password1234@"))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.register_next_button)).perform(click())
        Thread.sleep(2000)
        onView(withId(R.id.register_next_button)).check(matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(withId(R.id.register_signup_button)).check(matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        onView(withId(R.id.register_signup_button)).perform(click())
        onView(withId(R.id.register_signup_button)).check(matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        onView(withId(R.id.register_firstname_input)).check(matches(hasErrorText("Cannot be empty!")))
        onView(withId(R.id.register_lastname_input)).check(matches(hasErrorText("Cannot be empty!")))
    }

    @Test
    fun register_clickBackFromStep2_returnsToStep1(){
        onView(withId(R.id.register_email_input)).perform(typeText("hopefullynobodyusesthisemail@example.vinyla"))
        onView(withId(R.id.register_password_input)).perform(typeText("Password1234@"))
        onView(withId(R.id.register_password_confirmation_input)).perform(typeText("Password1234@"))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.register_next_button)).perform(click())
        Thread.sleep(2000)
        onView(withId(R.id.register_back_button_step2)).perform(click())
        onView(withId(R.id.register_next_button)).check(matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        onView(withId(R.id.register_signup_button)).check(matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
    }
}