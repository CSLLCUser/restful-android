package mn.aug.restfulandroid.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;


/**
 * Twitter API
 *
 * @author jeremyhaberman
 */
public class TwitterServiceHelper {

	public static String ACTION_REQUEST_RESULT = "REQUEST_RESULT";
	public static String EXTRA_REQUEST_ID = "EXTRA_REQUEST_ID";
	public static String EXTRA_RESULT_CODE = "EXTRA_RESULT_CODE";

	private static final String REQUEST_ID = "REQUEST_ID";
	private static final String profileHashkey = "PROFILE";
	private static final String timelineHashkey = "TIMELINE";

	private static Object lock = new Object();

	private static TwitterServiceHelper instance;

	//TODO: refactor the key
	private Map<String,Long> requests = new HashMap<String,Long>();
	private Context ctx;

	private TwitterServiceHelper(Context ctx){
		this.ctx = ctx.getApplicationContext();
	}

	public static TwitterServiceHelper getInstance(Context ctx){
		synchronized (lock) {
			if(instance == null){
				instance = new TwitterServiceHelper(ctx);			
			}
		}

		return instance;		
	}

	public long getTimeline() {

		long requestId = generateRequestID();
		requests.put(timelineHashkey, requestId);

		ResultReceiver serviceCallback = new ResultReceiver(null){
			@Override
			protected void onReceiveResult(int resultCode, Bundle resultData) {
				handleGetTimelineResponse(resultCode, resultData);
			}
		};

		Intent intent = new Intent(this.ctx, TwitterService.class);
		intent.putExtra(TwitterService.METHOD_EXTRA, TwitterService.METHOD_GET);
		intent.putExtra(TwitterService.RESOURCE_TYPE_EXTRA, TwitterService.RESOURCE_TYPE_TIMELINE);
		intent.putExtra(TwitterService.SERVICE_CALLBACK, serviceCallback);
		intent.putExtra(REQUEST_ID, requestId);

		this.ctx.startService(intent);
		
		return requestId;		
	}
	
	public long getProfile(){

		if(requests.containsKey(profileHashkey)){
			return requests.get(profileHashkey);
		}

		long requestId = generateRequestID();
		requests.put(profileHashkey, requestId);

		ResultReceiver serviceCallback = new ResultReceiver(null){

			@Override
			protected void onReceiveResult(int resultCode, Bundle resultData) {
				handleGetProfileResponse(resultCode, resultData);
			}

		};

		Intent intent = new Intent(this.ctx, TwitterService.class);
		intent.putExtra(TwitterService.METHOD_EXTRA, TwitterService.METHOD_GET);
		intent.putExtra(TwitterService.RESOURCE_TYPE_EXTRA, TwitterService.RESOURCE_TYPE_PROFILE);
		intent.putExtra(TwitterService.SERVICE_CALLBACK, serviceCallback);
		intent.putExtra(REQUEST_ID, requestId);

		this.ctx.startService(intent);

		return requestId;
	}

	private long generateRequestID() {
		long requestId = UUID.randomUUID().getLeastSignificantBits();
		return requestId;
	}

	public boolean isRequestPending(long requestId){
		return this.requests.containsValue(requestId);
	}

	private void handleGetProfileResponse(int resultCode, Bundle resultData){


		Intent origIntent = (Intent)resultData.getParcelable(TwitterService.ORIGINAL_INTENT_EXTRA);

		if(origIntent != null){
			long requestId = origIntent.getLongExtra(REQUEST_ID, 0);

			requests.remove(profileHashkey);

			Intent resultBroadcast = new Intent(ACTION_REQUEST_RESULT);
			resultBroadcast.putExtra(EXTRA_REQUEST_ID, requestId);
			resultBroadcast.putExtra(EXTRA_RESULT_CODE, resultCode);

			ctx.sendBroadcast(resultBroadcast);

		}
	}

	private void handleGetTimelineResponse(int resultCode, Bundle resultData){

		Intent origIntent = (Intent)resultData.getParcelable(TwitterService.ORIGINAL_INTENT_EXTRA);

		if(origIntent != null){
			long requestId = origIntent.getLongExtra(REQUEST_ID, 0);

			requests.remove(timelineHashkey);

			Intent resultBroadcast = new Intent(ACTION_REQUEST_RESULT);
			resultBroadcast.putExtra(EXTRA_REQUEST_ID, requestId);
			resultBroadcast.putExtra(EXTRA_RESULT_CODE, resultCode);

			ctx.sendBroadcast(resultBroadcast);
		}
	}	
}
