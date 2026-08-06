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

import com.group3.taamapp.Contract.LoginContract;
import com.group3.taamapp.LoginPage.LoginPresenter;
import com.group3.taamapp.Model.AuthModel;
import com.group3.taamapp.Model.AuthModel.StatusCallback;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;

/**
 * Test LoginPresenter by
 * - checking if the correct set of methods are called in different cases
 * - checking if values get from View are sent to the correct location of Model function
 */
@RunWith(MockitoJUnitRunner.class)
public class LoginPresenterTest {
    @Mock
    private LoginContract.AuthModel mockModel;

    @Mock
    private LoginContract.View mockView;

    @InjectMocks
    private LoginPresenter presenter;

    /**
     * Method usage test applicable in all cases of login()
     */
    private void basicTestForLogin() {
        verify(mockModel).login(any(), any(), any());
        verify(mockView).getEmail();
        verify(mockView).getPassword();
        verify(mockView, never()).toSignUp();
    }

    /**
     * Test usage of methods that are directly called by login()
     */
    @Test
    public void testLogin_methodTriggered() {
        presenter.login();
        basicTestForLogin();
        verify(mockView, never()).toastMakeText(any());
        verify(mockView, never()).toMainPage(any());
    }

    /**
     * Test if email get from View.getEmail by Presenter.login is correctly sent to Model.login
     */
    @Test
    public void testLogin_passEmailInCorrectPlace() {
        String mockEmail = "email";
        when(mockView.getEmail()).thenReturn(mockEmail);
        presenter.login();
        basicTestForLogin();
        verify(mockModel).login(eq(mockEmail), any(), any());
        verify(mockView, never()).toastMakeText(any());
        verify(mockView, never()).toMainPage(any());
    }

    /**
     * Test if password get from View.password by Presenter.login is correctly sent to Model.login
     */
    @Test
    public void testLogin_passPswInCorrectPlace() {
        String mockPsw = "password";
        when(mockView.getPassword()).thenReturn(mockPsw);
        presenter.login();
        basicTestForLogin();
        verify(mockModel).login(any(), eq(mockPsw), any());
        verify(mockView, never()).toastMakeText(any());
        verify(mockView, never()).toMainPage(any());
    }

    /**
     * Test the method usage of callback.onSuccess() sent from Presenter.login to Model.login
     */
    @Test
    public void testLogin_actionWhenSuccess() {
        String mockEmail = "email";
        when(mockView.getEmail()).thenReturn(mockEmail);
        doAnswer(invocation -> {
            StatusCallback<LoginContract.AuthModel.LoginFailure> callback = invocation.getArgument(2);
            callback.onSuccess();
            return null;
        }).when(mockModel).login(any(), any(), any());
        presenter.login();
        basicTestForLogin();
        verify(mockModel).login(eq(mockEmail), any(), any());
        verify(mockView, never()).toastMakeText(any());
        verify(mockView).toMainPage(mockEmail);
    }

    /**
     * Test the method usage of callback.onFailure() sent from Presenter.login to Model.login
     */
    @Test
    public void testLogin_actionWhenFail() {
        doAnswer(invocation -> {
            StatusCallback<LoginContract.AuthModel.LoginFailure> callback = invocation.getArgument(2);
            callback.onFailure(LoginContract.AuthModel.LoginFailure.EMPTY_EMAIL);
            return null;
        }).when(mockModel).login(any(), any(), any());
        presenter.login();
        basicTestForLogin();
        verify(mockView).toastMakeText("Failure: email cannot be empty");
        verify(mockView, never()).toMainPage(any());
    }

    /**
     * Test the method usage of callback.onError() sent from Presenter.login to Model.login
     */
    @Test
    public void testLogin_actionWhenError() {
        String mockErrorMsg = "This is an error";
        doAnswer(invocation -> {
            StatusCallback<LoginContract.AuthModel.LoginFailure> callback = invocation.getArgument(2);
            callback.onError(mockErrorMsg);
            return null;
        }).when(mockModel).login(any(), any(), any());
        presenter.login();
        basicTestForLogin();
        verify(mockView).toastMakeText("Error: " + mockErrorMsg);
        verify(mockView, never()).toMainPage(any());
    }

    /**
     * Test Method usage for toSignUp()
     */
    @Test
    public void testToSignUp_MethodTriggered() {
        presenter.toSignUp();
        verify(mockModel, never()).login(any(), any(), any());
        verify(mockView, never()).toastMakeText(any());
        verify(mockView, never()).getEmail();
        verify(mockView, never()).getPassword();
        verify(mockView).toSignUp();
        verify(mockView, never()).toMainPage(any());
    }
}
