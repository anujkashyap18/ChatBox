package com.vt.chatbox.Notification;

import android.app.job.JobParameters;
import android.app.job.JobService;

public class MyJobService extends JobService {
	
	@Override
	public boolean onStartJob( JobParameters job ) {
		
		new MyFirebaseMessagingService();
		return false; // Answers the question: "Is there still work going on?"
	}
	
	@Override
	public boolean onStopJob( JobParameters job ) {
		return false; // Answers the question: "Should this job be retried?"
		
	}
}
