package mn.aug.restfulandroid.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import mn.aug.restfulandroid.R;
import mn.aug.restfulandroid.activity.base.RESTfulActivity;
import mn.aug.restfulandroid.security.AuthorizationManager;

public class LoginActivity extends RESTfulActivity {

	private AuthorizationManager mOAuthManager;

	private Button mButtonLogin;
	private ProgressBar mProgressIndicator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		setContentResId(R.layout.login);

		super.onCreate(savedInstanceState);

		mOAuthManager = AuthorizationManager.getInstance();

		mProgressIndicator = (ProgressBar) findViewById(R.id.progress_indicator);

		mButtonLogin = (Button) findViewById(R.id.button_login);
		mButtonLogin.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				mProgressIndicator.setVisibility(View.VISIBLE);
				mButtonLogin.setVisibility(View.INVISIBLE);
				authorize();
			}
		});

	}

	private void startHomeActivity() {
		Intent startHomeActivity = new Intent(this, TimelineActivity.class);
		startActivity(startHomeActivity);
		finish();
	}

	/**
	 * Authorizes app for use with Twitter.
	 */
	void authorize() {
		AuthorizeTask authorizeTask = new AuthorizeTask();
		authorizeTask.execute((Void[]) null);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Check to see if we're resuming after having authenticated with
		// Twitter
		if (getIntent().getData() != null) {
			mOAuthManager.setAccessToken(getIntent());

			if (mOAuthManager.loggedIn()) {
				startHomeActivity();
			} else {
				Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
				mButtonLogin.setVisibility(View.VISIBLE);
			}
		} else {
			// No Intent with a callback Uri was found, so let's just see if
			// we've already logged in, and if so start the Home activity
			if (mOAuthManager.loggedIn()) {
				startHomeActivity();
			} else {
				mButtonLogin.setVisibility(View.VISIBLE);
			}
		}
	}

	class AuthorizeTask extends AsyncTask<Void, Void, Uri> {

		@Override
		protected Uri doInBackground(Void... objects) {
			return mOAuthManager.getAuthorizationUrl();
		}

		@Override
		protected void onPostExecute(Uri uri) {
			Intent openAuthUrl = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(openAuthUrl);
		}
	}

	@Override
	protected void refresh() {
		// n/a
	}

}
