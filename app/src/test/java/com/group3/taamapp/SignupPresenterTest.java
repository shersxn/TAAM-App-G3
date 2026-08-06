package com.group3.taamapp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.group3.taamapp.Contract.SignupContract;
import com.group3.taamapp.Model.AuthModel;
import com.group3.taamapp.SignupPage.SignupPresenter;
import com.group3.taamapp.Model.AuthModel.StatusCallback;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;

/**
 * Test SignupPresenter by
 * - checking if the correct set of methods are called in different cases
 * - checking if values get from View are sent to the correct location of Model function
 */
@RunWith(MockitoJUnitRunner.class)
public class SignupPresenterTest {
    @Mock
    private SignupContract.AuthModel mockAuthModel;

    @Mock
    private SignupContract.View mockView;

    @InjectMocks
    private SignupPresenter presenter;

    /**
     * Method usage test applicable in all cases of signup()
     */
    private void basicTestForSignup() {
        verify(mockAuthModel).signUp(any(), any(), any(), any());
        verify(mockView).getEmail();
        verify(mockView).getUsername();
        verify(mockView).getPassword();
        verify(mockView, never()).toLogin();
    }

    /**
     * Test usage of methods that are directly called by signup()
     */
    @Test
    public void testSignup_methodTriggered() {
        presenter.signup();
        basicTestForSignup();
    }

    /**
     * Test if email get from View.getEmail by Presenter.signup is correctly sent to Model.signup
     */
    @Test
    public void testSignup_passEmailInCorrectPlace() {
        String mockEmail = "email";
        when(mockView.getEmail()).thenReturn(mockEmail);
        presenter.signup();
        basicTestForSignup();
        verify(mockAuthModel).signUp(eq(mockEmail), any(), any(), any());
    }

    /**
     * Test if username get from View.username by Presenter.signup is correctly sent to Model.signup
     */
    @Test
    public void testSignup_passUsernameInCorrectPlace() {
        String mockUsername = "username";
        when(mockView.getUsername()).thenReturn(mockUsername);
        presenter.signup();
        basicTestForSignup();
        verify(mockAuthModel).signUp(any(), eq(mockUsername), any(), any());
    }

    /**
     * Test if password get from View.password by Presenter.signup is correctly sent to Model.signup
     */
    @Test
    public void testSignup_passPasswordInCorrectPlace() {
        String mockPassword = "password";
        when(mockView.getPassword()).thenReturn(mockPassword);
        presenter.signup();
        basicTestForSignup();
        verify(mockAuthModel).signUp(any(), any(), eq(mockPassword), any());
    }

    /**
     * Test the method usage of callback.onSuccess() sent from Presenter.signup to Model.signup
     */
    @Test
    public void testSignup_whenSuccess() {
        doAnswer(invocation -> {
            StatusCallback<SignupContract.AuthModel.SignUpFailure> callback = invocation.getArgument(3);
            callback.onSuccess();
            return null;
        }).when(mockAuthModel).signUp(any(), any(), any(), any());
        presenter.signup();
        basicTestForSignup();
        verify(mockView).toastMakeText("You have successfully signed up an account.");
    }

    /**
     * Test the method usage of callback.onFailure() sent from Presenter.signup to Model.signup
     */
    @Test
    public void testSignup_whenFailure() {
        doAnswer(invocation -> {
            StatusCallback<SignupContract.AuthModel.SignUpFailure> callback = invocation.getArgument(3);
            callback.onFailure(SignupContract.AuthModel.SignUpFailure.EMPTY_EMAIL);
            return null;
        }).when(mockAuthModel).signUp(any(), any(), any(), any());
        presenter.signup();
        basicTestForSignup();
        verify(mockView).toastMakeText("Failure: email cannot be empty");
    }

    /**
     * Test the method usage of callback.onError() sent from Presenter.signup to Model.signup
     */
    @Test
    public void testSignup_whenError() {
        String mockErrorMsg = "This is an error";
        doAnswer(invocation -> {
            StatusCallback<SignupContract.AuthModel.SignUpFailure> callback = invocation.getArgument(3);
            callback.onError(mockErrorMsg);
            return null;
        }).when(mockAuthModel).signUp(any(), any(), any(), any());
        presenter.signup();
        basicTestForSignup();
        verify(mockView).toastMakeText("Error: " + mockErrorMsg);
    }

    /**
     * Test Method usage for toLoginIn()
     */
    @Test
    public void testToLogin_methodTriggered() {
        presenter.toLogin();
        verify(mockAuthModel, never()).signUp(any(), any(), any(), any());
        verify(mockView, never()).getEmail();
        verify(mockView, never()).getUsername();
        verify(mockView, never()).getPassword();
        verify(mockView).toLogin();
    }
}
