package com.vt.chatbox;

import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;

import java.util.List;


public
class CallActivity extends AppCompatActivity {
	
	private static final String APP_KEY     = "enter-application-key";
	private static final String APP_SECRET  = "enter-application-secret";
	private static final String ENVIRONMENT = "sandbox.sinch.com";
	
	private Call        call;
	private TextView    callState;
	private SinchClient sinchClient;
	private Button      button;
	private String      callerId;
	private String      recipientId;
	
	@Override
	protected
	void onCreate ( Bundle savedInstanceState ) {
		super.onCreate ( savedInstanceState );

//		Window window = this.getWindow ( );
//		window.clearFlags ( WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS );
//		window.addFlags ( WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS );
//		window.setStatusBarColor ( this.getResources ( ).getColor ( R.color.colorPrimaryDark ) );
//
		setContentView ( R.layout.activity_call );
		
		sinchClient = Sinch.getSinchClientBuilder ( )
		                   .context ( this )
		                   .applicationKey ( "684eb5e8-c19e-4db1-ae63-61c268688e1b" )
		                   .applicationSecret ( "+AKPHZvxs0a2ZMfP3okiYQ==" )
		                   .environmentHost ( "clientapi.sinch.com" )
		                   .userId ( "159463" )
		                   .build ( );
		
		sinchClient.setSupportCalling ( true );
		sinchClient.startListeningOnActiveConnection ( );
		sinchClient.start ( );
		
		sinchClient.getCallClient ( ).addCallClientListener ( new SinchCallClientListener ( ) );
		
		button    = findViewById ( R.id.button );
		callState = findViewById ( R.id.callState );
		
		button.setOnClickListener ( new View.OnClickListener ( ) {
			@Override
			public
			void onClick ( View view ) {
				if ( call == null ) {
					call = sinchClient.getCallClient ( ).callPhoneNumber ( "+46000000000" );
					call.addCallListener ( new SinchCallListener ( ) );
					button.setText ( "Hang Up" );
				}
				else {
					call.hangup ( );
				}
			}
		} );
	}
	
	private
	class SinchCallListener implements CallListener {
		@Override
		public
		void onCallEnded ( Call endedCall ) {
			call = null;
			SinchError a = endedCall.getDetails ( ).getError ( );
			button.setText ( "Call" );
			callState.setText ( "" );
			setVolumeControlStream ( AudioManager.USE_DEFAULT_STREAM_TYPE );
		}
		
		@Override
		public
		void onCallEstablished ( Call establishedCall ) {
			callState.setText ( "connected" );
			setVolumeControlStream ( AudioManager.STREAM_VOICE_CALL );
		}
		
		@Override
		public
		void onCallProgressing ( Call progressingCall ) {
			callState.setText ( "ringing" );
		}
		
		@Override
		public
		void onShouldSendPushNotification ( Call call , List < PushPair > pushPairs ) {
		}
	}
	
	private
	class SinchCallClientListener implements CallClientListener {
		@Override
		public
		void onIncomingCall ( CallClient callClient , Call incomingCall ) {
			call = incomingCall;
			Toast.makeText ( CallActivity.this , "incoming call" , Toast.LENGTH_SHORT ).show ( );
			call.answer ( );
			call.addCallListener ( new SinchCallListener ( ) );
			button.setText ( "Hang Up" );
		}
	}
}