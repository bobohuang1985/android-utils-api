package utils.bobo.com.boboutils.App.call;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.widget.Toast;

/**
 * Created by hgx on 2016/6/13.
 */
public class PhoneCallReceiver extends BroadcastReceiver {
    private int lastCallState  = TelephonyManager.CALL_STATE_IDLE;
    private boolean isIncoming = false;
    private static String contactNum;
    Intent audioRecorderService;


    public PhoneCallReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //如果是去电
        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){
            contactNum = intent.getExtras().getString(Intent.EXTRA_PHONE_NUMBER);
        }else //android.intent.action.PHONE_STATE.查了下android文档，貌似没有专门用于接收来电的action,所以，非去电即来电.
        {
            String state = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
            String phoneNumber = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);

            int stateChange = 0;

            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                //空闲状态
                stateChange =TelephonyManager.CALL_STATE_IDLE;
                if (isIncoming){
                    onIncomingCallEnded(context,phoneNumber);
                }else {
                    onOutgoingCallEnded(context,phoneNumber);
                }
            }else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                //摘机状态
                stateChange = TelephonyManager.CALL_STATE_OFFHOOK;
                if (lastCallState != TelephonyManager.CALL_STATE_RINGING){
                    //如果最近的状态不是来电响铃的话，意味着本次通话是去电
                    isIncoming =false;
                    onOutgoingCallStarted(context,phoneNumber);
                }else {
                    //否则本次通话是来电
                    isIncoming = true;
                    onIncomingCallAnswered(context, phoneNumber);
                }
            }else if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                //来电响铃状态
                stateChange = TelephonyManager.CALL_STATE_RINGING;
                lastCallState = stateChange;
                onIncomingCallReceived(context,contactNum);
            }
        }
    }


    protected void onIncomingCallStarted(Context context,String number){
        Toast.makeText(context,"Incoming call is started number:"+number,Toast.LENGTH_LONG).show();
        context.startService(new Intent(context,AudioRecorderService.class));

    }

    protected void onOutgoingCallStarted(Context context,String number){
        Toast.makeText(context, "Outgoing call is started number:"+number, Toast.LENGTH_LONG).show();
        context.startService(new Intent(context, AudioRecorderService.class));
    }

    protected void onIncomingCallEnded(Context context,String number){
        Toast.makeText(context, "Incoming call is ended number:"+number, Toast.LENGTH_LONG).show();
        context.stopService(new Intent(context, AudioRecorderService.class));
    }

    protected void onOutgoingCallEnded(Context context,String number){
        Toast.makeText(context, "Outgoing call is ended number:"+number, Toast.LENGTH_LONG).show();
        context.stopService(new Intent(context, AudioRecorderService.class));
    }

    protected void onIncomingCallReceived(Context context,String number){
        Toast.makeText(context, "Incoming call is received number:"+number, Toast.LENGTH_LONG).show();
    }
    protected void onIncomingCallAnswered(Context context, String number) {
        Toast.makeText(context, "Incoming call is answered number:"+number, Toast.LENGTH_LONG).show();
    }
}