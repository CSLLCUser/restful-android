package mn.aug.restfulandroid.rest;

import java.net.URI;

import mn.aug.restfulandroid.rest.RestMethodFactory.Method;
import mn.aug.restfulandroid.rest.resource.Profile;

import org.json.JSONObject;

import android.content.Context;

public class GetProfileRestMethod extends AbstractRestMethod<Profile> {
	
	private Context mContext;

	private static final URI PROFILE_URI = URI
			.create("https://api.twitter.com/1/account/verify_credentials.json");
	
	public GetProfileRestMethod(Context context) {
		mContext = context.getApplicationContext();
	}

	@Override
	protected Request buildRequest() {

		return new Request(Method.GET, PROFILE_URI, null, null);
	}

	@Override
	protected Profile parseResponseBody(String responseBody) throws Exception {

		JSONObject json = new JSONObject(responseBody);
		return new Profile(json);

	}

	@Override
	protected Context getContext() {
		return mContext;
	}

}
