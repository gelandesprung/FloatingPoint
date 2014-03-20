package com.example.floatingpoint;

import java.util.List;
import android.app.ActivityManager;
import android.app.ActivityManager.RecentTaskInfo;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class FlyBitch extends Service {

	private static final String TAG="Fly";
	
	private WindowManager windowManager;
	private ImageView chatHead;
	boolean mHasDoubleClicked = false;
	long lastPressTime;
	
	Handler myHandler;
	Runnable mDoubleClick;
	

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void onCreate() {
		super.onCreate();

		myHandler = new Handler(getMainLooper());
		mDoubleClick = new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				exchangeApp();
			}
			
		};
		
        
		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

		chatHead = new ImageView(this);

		chatHead.setImageResource(R.drawable.floating2);

		final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);

		params.gravity = Gravity.TOP | Gravity.LEFT;
		params.x = 0;
		params.y = 100;

		windowManager.addView(chatHead, params);
		try {
			chatHead.setOnTouchListener(new View.OnTouchListener() {
				private WindowManager.LayoutParams paramsF = params;
				private int initialX;
				private int initialY;
				private float initialTouchX;
				private float initialTouchY;

				@Override public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:

						// Get current time in nano seconds.                        
                        
						initialX = paramsF.x;
						initialY = paramsF.y;
						initialTouchX = event.getRawX();
						initialTouchY = event.getRawY();
						break;
					case MotionEvent.ACTION_UP:
						break;
					case MotionEvent.ACTION_MOVE:
						paramsF.x = initialX + (int) (event.getRawX() - initialTouchX);
						paramsF.y = initialY + (int) (event.getRawY() - initialTouchY);
						windowManager.updateViewLayout(chatHead, paramsF);
						break;
					}
					return false;
				}
			});
			
			chatHead.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					// TODO Auto-generated method stub
					long pressTime = System.currentTimeMillis();
					long ret = pressTime - lastPressTime;
					lastPressTime = pressTime;
					Log.d(TAG,""+ret+"="+lastPressTime+" - "+pressTime);
					if (ret <= 300) {
						myHandler.removeCallbacks(mDoubleClick);
						HomeKey();
						return ;
					}
                    // If double click...               
					myHandler.postDelayed(mDoubleClick, 300);
				}
			});
		} catch (Exception e) {
			// TODO: handle exception
		}

	}
	public void onDestroy() {
		super.onDestroy();
		if (chatHead != null) windowManager.removeView(chatHead);
	}
	private void HomeKey() {
    	Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 注意
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
		
/*		final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		PackageManager packageManager = getPackageManager();
		List<ResolveInfo> riList = packageManager.queryIntentActivities(mainIntent, 0);*/
    }
	private void exchangeApp(){
		int i=1;
		ActivityManager am = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
		List<RecentTaskInfo> rti = am.getRecentTasks(8,2);
		final PackageManager pm = getPackageManager();
		while(i<rti.size()){
			RecentTaskInfo info = rti.get(i++);
			Intent intent = new Intent(info.baseIntent);

			if(rti.size()>2 && intent.getComponent().getPackageName().contains("launcher")){
				continue;
			}
			if(info.origActivity!=null){
				intent.setComponent(info.origActivity);
			}
//			Log.d("YANCHAO",intent.getComponent().getPackageName());
			ResolveInfo resolveInfo = pm.resolveActivity(intent, 0);
			if(resolveInfo == null) {
				continue;
			}
			intent.setFlags((intent.getFlags()&~Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)| Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			return ;
		}
	}	

}
