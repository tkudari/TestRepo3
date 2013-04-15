package com.dashwire.config.ui;

import com.dashwire.base.debug.DashLogger;
import com.dashwire.config.tracking.Tracker;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dashwire.config.R;
import com.dashwire.config.RestClient;
import com.dashwire.config.util.CommonUtils;
import com.dashwire.config.util.CryptoTools;

public class LoginActivity extends BaseActivity {

    protected String TAG = LoginActivity.class.getCanonicalName();
    private LinearLayout titleLayout;
    private LinearLayout bodyLayout;
    private LinearLayout errorMessageLayout;
    private LinearLayout forgotLayout;
    private LinearLayout inputLinearLayout;
    private EditText usernameText;
    private EditText passwordText;
    private TextView forgotPasswordLink;
    private Button nextButton;
    private Button backButton;
    private TextView errorMessageText;

    public void onCreate( Bundle savedInstanceState ) {
        setContentView( R.layout.login );
        super.onCreate( savedInstanceState );

        titleLayout = ( LinearLayout ) findViewById( R.id.title_layout );
        bodyLayout = ( LinearLayout ) findViewById( R.id.body_layout );
        errorMessageLayout = ( LinearLayout ) findViewById( R.id.error_message_layout );
        forgotLayout = ( LinearLayout ) findViewById( R.id.forgot_layout );
        inputLinearLayout = ( LinearLayout ) findViewById( R.id.input_linearLayout );

        usernameText = ( EditText ) findViewById( R.id.username );
        passwordText = ( EditText ) findViewById( R.id.password );
        nextButton = ( Button ) findViewById( R.id.next_button );
        backButton = ( Button ) findViewById( R.id.back_button );
        errorMessageText = ( TextView ) findViewById( R.id.error_message );

        usernameText.setTextColor( R.color.grey );

        usernameText.setOnTouchListener( new OnTouchListener() {

            public boolean onTouch( View view, MotionEvent event ) {
                if ( event.getAction() == MotionEvent.ACTION_UP ) {
                    erraseDefaultText();
                }
                showInputFieldsView();
                return false;
            }

        } );

        passwordText.setOnTouchListener( new OnTouchListener() {

            public boolean onTouch( View view, MotionEvent event ) {
                if ( event.getAction() == MotionEvent.ACTION_UP ) {
                    erraseDefaultText();
                }
                showInputFieldsView();
                return false;
            }

        } );

        usernameText.addTextChangedListener( new TextWatcher() {
            public void afterTextChanged( Editable s ) {
                enableNextButton();
            }

            public void beforeTextChanged( CharSequence s, int start, int count, int after ) {
                errorMessageText.setVisibility( View.GONE );
                usernameText.setTextColor( Color.BLACK );
            }

            public void onTextChanged( CharSequence s, int start, int before, int count ) {
                erraseDefaultText();
            }
        } );

        passwordText.addTextChangedListener( new TextWatcher() {
            public void afterTextChanged( Editable s ) {
                enableNextButton();
            }

            public void beforeTextChanged( CharSequence s, int start, int count, int after ) {
                errorMessageText.setVisibility( View.GONE );
                usernameText.setTextColor( Color.BLACK );
            }

            public void onTextChanged( CharSequence s, int start, int before, int count ) {
            }
        } );

        OverrideBackKeyLayout.setUIActivity( this );

        errorMessageText.setVisibility( View.GONE );

        forgotPasswordLink = ( TextView ) findViewById( R.id.login_forgot_link );
		String linkText = context.getResources().getString( R.string.login_forgot_link );
        CommonUtils.clickify( forgotPasswordLink, linkText, linkText, new ClickSpan.OnClickListener() {
            public void onClick() {
                finish();
                startActivity( CommonUtils.getForgotPasswordActivityIntent( context ) );
            }
        } );

        nextButton.setEnabled( false );
        nextButton.setOnClickListener( new View.OnClickListener() {
            public void onClick( View v ) {
                Tracker.track(context, "login/next");
                showLoginScreenView();
                signin();
            }
        } );

        backButton.setOnClickListener( new View.OnClickListener() {
            public void onClick( View v ) {
                Tracker.track(context, "login/back");
                finish();
            }
        } );
    }

    private void enableNextButton() {
        if ( usernameText.getText().length() > 0 && passwordText.getText().length() > 0 ) {
            nextButton.setEnabled( true );
        } else {
            nextButton.setEnabled( false );
        }
    }

    private void erraseDefaultText() {
        String defaultUsername = this.getResources().getString( R.string.login_username );
        String username = usernameText.getText().toString();

        if ( username.contains( defaultUsername ) ) {
            usernameText.setText( "" );
            passwordText.setText( "" );
        }
    }

    private void signin() {
        if ( CommonUtils.isDataConnectionAvailable( context ) ) {
            usernameText = ( EditText ) findViewById( R.id.username );
            String userName = usernameText.getText().toString().toLowerCase();
            passwordText = ( EditText ) findViewById( R.id.password );
            String password = passwordText.getText().toString();
            String digest = CryptoTools.sha256( userName + ':' + password );
            byte[] key = CryptoTools.sha256Binary( "key:" + digest );
            String encodedKey = Base64.encodeToString( key, Base64.DEFAULT );

            CommonUtils.showProgressDialog( context.getResources().getString( R.string.progress_dialog_authentication ), context );
            new LoginTask(this, encodedKey).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, digest );
        } else {
            finish();
            CommonUtils.startConnectivityErrorActivity( CommonUtils.getLoginActivityIntent( context ).toURI(), "", context );
        }

    }

    public void onBackPressed() {
        if ( titleLayout.getVisibility() == View.VISIBLE ) {
            super.onBackPressed();
        } else {
            showLoginScreenView();
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = ( InputMethodManager ) getSystemService( Context.INPUT_METHOD_SERVICE );
        imm.hideSoftInputFromWindow( usernameText.getWindowToken(), 0 );
        imm.hideSoftInputFromWindow( passwordText.getWindowToken(), 0 );
    }

    public static class LoginTask extends AsyncTask<String, Void, JSONObject> {
        private static final String TAG = "com.dashwire.config.ui.LoginActivity.LoginTask";

		String encodedKey = null;
		LoginActivity loginActivity = null;

		public LoginTask(LoginActivity l, String k) {
			loginActivity = l;
			encodedKey = k;
		}

        @Override
        protected JSONObject doInBackground( final String... digest ) {
            try {

                JSONObject jsonBody = new JSONObject();
                jsonBody.put( "digest", digest[ 0 ] );
                return RestClient.post( RestClient.getHost() + loginActivity.getResources().getString( R.string.uri_sessions ), jsonBody.toString(), loginActivity );

            } catch ( JSONException je ) {
                DashLogger.v(TAG, "Malformed JSON: " + je.getMessage());
            }
            return null;
        }

        
        
        @Override
        protected void onPostExecute( JSONObject jsonObject ) {
            try {
                int returnCode = jsonObject.getInt( RestClient.JSON_HTTP_STATUS_CODE );
                if ( returnCode >= 200 && returnCode < 300 ) {
                    String configId = jsonObject.getString( RestClient.JSON_CONFIG_ID );
                    loginActivity.processConfigId( configId );
                    CommonUtils.hideProgressDialog();
					CommonUtils.setKey( loginActivity, encodedKey );
					loginActivity.startActivity( CommonUtils.getConfigurationActivityIntent( loginActivity ) );
					loginActivity.finish();
                } else if ( returnCode == 401 ) {
					loginActivity.handleInvalidCredentials();
                } else {
					loginActivity.handleInvalidResponse();
                }
            } catch ( JSONException e ) {
                e.printStackTrace();
				loginActivity.handleInvalidResponse();
            }
            super.onPostExecute( jsonObject );
        }
    }

    private void handleInvalidCredentials() {
        CommonUtils.hideProgressDialog();
        TextView errorMessageText = ( TextView ) findViewById( R.id.error_message );
        errorMessageText.setText( context.getResources().getString( R.string.login_invalid_credential ) );
        errorMessageText.setTextColor( Color.RED );
        errorMessageText.setVisibility( View.VISIBLE );
        EditText usernameText = ( EditText ) findViewById( R.id.username );
        usernameText.setTextColor( Color.RED );
    }

    private void handleInvalidResponse() {
        CommonUtils.hideProgressDialog();
        TextView errorMessageText = ( TextView ) findViewById( R.id.error_message );
        errorMessageText.setText( context.getResources().getString( R.string.login_system_error ) );
        errorMessageText.setTextColor( Color.RED );
    }

    private void processConfigId( String configId ) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( this );
        SharedPreferences.Editor editor = settings.edit();
        editor.putString( RestClient.JSON_CONFIG_ID, configId );
        editor.commit();
    }

    private void showLoginScreenView() {
        titleLayout.setVisibility( View.VISIBLE );
        bodyLayout.setVisibility( View.VISIBLE );
        errorMessageLayout.setVisibility( View.VISIBLE );
        forgotLayout.setVisibility( View.VISIBLE );
        inputLinearLayout.setPadding( inputLinearLayout.getPaddingLeft(),60,inputLinearLayout.getPaddingRight(),inputLinearLayout.getPaddingBottom());
        hideKeyboard();
    }

    private void showInputFieldsView() {
        titleLayout.setVisibility( View.GONE );
        bodyLayout.setVisibility( View.GONE );
        errorMessageLayout.setVisibility( View.GONE );
        forgotLayout.setVisibility( View.GONE );
        inputLinearLayout.setPadding( inputLinearLayout.getPaddingLeft(),100,inputLinearLayout.getPaddingRight(),inputLinearLayout.getPaddingBottom());
    }
}
