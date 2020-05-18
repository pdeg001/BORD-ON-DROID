package nl.pdeg.bordondroid;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class serverboard extends Activity implements B4AActivity{
	public static serverboard mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = true;
	public static final boolean includeTitle = false;
    public static WeakReference<Activity> previousOne;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mostCurrent = this;
		if (processBA == null) {
			processBA = new BA(this.getApplicationContext(), null, null, "nl.pdeg.bordondroid", "nl.pdeg.bordondroid.serverboard");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (serverboard).");
				p.finish();
			}
		}
        processBA.setActivityPaused(true);
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
        WaitForLayout wl = new WaitForLayout();
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(this, processBA, wl, false))
		    BA.handler.postDelayed(wl, 5);

	}
	static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "nl.pdeg.bordondroid", "nl.pdeg.bordondroid.serverboard");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "nl.pdeg.bordondroid.serverboard", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (serverboard) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (serverboard) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEventFromUI(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return serverboard.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeydown", this, new Object[] {keyCode, event}))
            return true;
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeyup", this, new Object[] {keyCode, event}))
            return true;
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null)
            return;
        if (this != mostCurrent)
			return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        BA.LogInfo("** Activity (serverboard) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        if (mostCurrent != null)
            processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        processBA.setActivityPaused(true);
        mostCurrent = null;
        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
            serverboard mc = mostCurrent;
			if (mc == null || mc != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (serverboard) Resume **");
            if (mc != mostCurrent)
                return;
		    processBA.raiseEvent(mc._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        for (int i = 0;i < permissions.length;i++) {
            Object[] o = new Object[] {permissions[i], grantResults[i] == 0};
            processBA.raiseEventFromDifferentThread(null,null, 0, "activity_permissionresult", true, o);
        }
            
    }

public anywheresoftware.b4a.keywords.Common __c = null;
public static nl.pdeg.bordondroid.mqttgetborddata _mqttgetdata = null;
public static anywheresoftware.b4a.objects.Timer _datatmr = null;
public static int _dotcount = 0;
public static String _waittext = "";
public static anywheresoftware.b4a.objects.CSBuilder _cs = null;
public static String _substring = "";
public static long _lastmessagetime = 0L;
public static anywheresoftware.b4a.objects.Timer _lastmessagetimer = null;
public anywheresoftware.b4a.objects.collections.JSONParser _parser = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblp1name = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblp2name = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblp1maken100 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblp1maken10 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblp1maken1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblp1100 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblp110 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblp11 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblp2maken100 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblp2maken10 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblp2maken1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblp2100 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblp210 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblp1moy = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblp2moy = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblp21 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblbeurt100 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblbeurt10 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblbeurt1 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _imgp2play = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _imgp1play = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _imgnodata = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblnodata = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _imgsponsor = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbltafelnaam = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblspelduur = null;
public anywheresoftware.b4a.objects.PanelWrapper _pnlsponsor = null;
public b4a.example.dateutils _dateutils = null;
public nl.pdeg.bordondroid.main _main = null;
public nl.pdeg.bordondroid.locations _locations = null;
public nl.pdeg.bordondroid.starter _starter = null;

public static void initializeProcessGlobals() {
             try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
}
public static void  _activity_create(boolean _firsttime) throws Exception{
ResumableSub_Activity_Create rsub = new ResumableSub_Activity_Create(null,_firsttime);
rsub.resume(processBA, null);
}
public static class ResumableSub_Activity_Create extends BA.ResumableSub {
public ResumableSub_Activity_Create(nl.pdeg.bordondroid.serverboard parent,boolean _firsttime) {
this.parent = parent;
this._firsttime = _firsttime;
}
nl.pdeg.bordondroid.serverboard parent;
boolean _firsttime;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
 //BA.debugLineNum = 56;BA.debugLine="If Not (mqttGetData.IsInitialized) Then";
if (true) break;

case 1:
//if
this.state = 4;
if (anywheresoftware.b4a.keywords.Common.Not(parent._mqttgetdata.IsInitialized /*boolean*/ ())) { 
this.state = 3;
}if (true) break;

case 3:
//C
this.state = 4;
 //BA.debugLineNum = 57;BA.debugLine="mqttGetData.Initialize";
parent._mqttgetdata._initialize /*String*/ (processBA);
 if (true) break;

case 4:
//C
this.state = -1;
;
 //BA.debugLineNum = 60;BA.debugLine="CallSub(Starter, \"SetSubString\")";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)(parent.mostCurrent._starter.getObject()),"SetSubString");
 //BA.debugLineNum = 61;BA.debugLine="mqttGetData.SetSub";
parent._mqttgetdata._setsub /*String*/ ();
 //BA.debugLineNum = 63;BA.debugLine="Activity.LoadLayout(\"ServerBoard\")";
parent.mostCurrent._activity.LoadLayout("ServerBoard",mostCurrent.activityBA);
 //BA.debugLineNum = 64;BA.debugLine="lastMessageTimer.Initialize(\"tmrLastMessase\", 120";
parent._lastmessagetimer.Initialize(processBA,"tmrLastMessase",(long) (120*1000));
 //BA.debugLineNum = 65;BA.debugLine="lastMessageTimer.Enabled = True";
parent._lastmessagetimer.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 67;BA.debugLine="imgNoData.BringToFront";
parent.mostCurrent._imgnodata.BringToFront();
 //BA.debugLineNum = 68;BA.debugLine="SetImg";
_setimg();
 //BA.debugLineNum = 70;BA.debugLine="dataTmr.Initialize(\"dataTmr\", 1000)";
parent._datatmr.Initialize(processBA,"dataTmr",(long) (1000));
 //BA.debugLineNum = 71;BA.debugLine="mqttGetData.Connect";
parent._mqttgetdata._connect /*String*/ ();
 //BA.debugLineNum = 73;BA.debugLine="imgNoData.SetVisibleAnimated(1, True)";
parent.mostCurrent._imgnodata.SetVisibleAnimated((int) (1),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 74;BA.debugLine="lblTafelNaam.Text = Starter.DiscoveredServer";
parent.mostCurrent._lbltafelnaam.setText(BA.ObjectToCharSequence(parent.mostCurrent._starter._discoveredserver /*String*/ ));
 //BA.debugLineNum = 76;BA.debugLine="Sleep(1000)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,this,(int) (1000));
this.state = 5;
return;
case 5:
//C
this.state = -1;
;
 //BA.debugLineNum = 77;BA.debugLine="mqttGetData.SendMessage(\"data please\")";
parent._mqttgetdata._sendmessage /*String*/ ("data please");
 //BA.debugLineNum = 78;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static boolean  _activity_keypress(int _keycode) throws Exception{
 //BA.debugLineNum = 123;BA.debugLine="Private Sub Activity_KeyPress(KeyCode As Int) As B";
 //BA.debugLineNum = 124;BA.debugLine="If KeyCode = KeyCodes.KEYCODE_BACK Then";
if (_keycode==anywheresoftware.b4a.keywords.Common.KeyCodes.KEYCODE_BACK) { 
 //BA.debugLineNum = 125;BA.debugLine="lastMessageTimer.Enabled = False";
_lastmessagetimer.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 126;BA.debugLine="DisconnetMqtt";
_disconnetmqtt();
 //BA.debugLineNum = 127;BA.debugLine="Return False";
if (true) return anywheresoftware.b4a.keywords.Common.False;
 }else {
 //BA.debugLineNum = 129;BA.debugLine="Return True";
if (true) return anywheresoftware.b4a.keywords.Common.True;
 };
 //BA.debugLineNum = 131;BA.debugLine="End Sub";
return false;
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 107;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 109;BA.debugLine="If mqttGetData.connected Then";
if (_mqttgetdata._connected /*boolean*/ ) { 
 //BA.debugLineNum = 110;BA.debugLine="mqttGetData.Disconnect";
_mqttgetdata._disconnect /*String*/ ();
 };
 //BA.debugLineNum = 112;BA.debugLine="lastMessageTimer.Enabled = False";
_lastmessagetimer.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 113;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 114;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 101;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 102;BA.debugLine="waitText = $\"Wachten op ${Starter.DiscoveredServe";
_waittext = ("Wachten op "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(mostCurrent._starter._discoveredserver /*String*/ ))+"");
 //BA.debugLineNum = 103;BA.debugLine="dotCount = 0";
_dotcount = (int) (0);
 //BA.debugLineNum = 105;BA.debugLine="End Sub";
return "";
}
public static String  _datatmr_tick() throws Exception{
String _dot = "";
int _i = 0;
 //BA.debugLineNum = 87;BA.debugLine="Sub dataTmr_Tick";
 //BA.debugLineNum = 88;BA.debugLine="Dim dot As String";
_dot = "";
 //BA.debugLineNum = 89;BA.debugLine="dotCount=dotCount+1";
_dotcount = (int) (_dotcount+1);
 //BA.debugLineNum = 90;BA.debugLine="If dotCount >= 10 Then";
if (_dotcount>=10) { 
 //BA.debugLineNum = 91;BA.debugLine="dotCount = 0";
_dotcount = (int) (0);
 //BA.debugLineNum = 93;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 95;BA.debugLine="For i = 0 To dotCount";
{
final int step7 = 1;
final int limit7 = _dotcount;
_i = (int) (0) ;
for (;_i <= limit7 ;_i = _i + step7 ) {
 //BA.debugLineNum = 96;BA.debugLine="dot = dot &\"*\"";
_dot = _dot+"*";
 }
};
 //BA.debugLineNum = 99;BA.debugLine="End Sub";
return "";
}
public static String  _disconnetmqtt() throws Exception{
 //BA.debugLineNum = 116;BA.debugLine="Sub DisconnetMqtt";
 //BA.debugLineNum = 117;BA.debugLine="If mqttGetData.connected Then";
if (_mqttgetdata._connected /*boolean*/ ) { 
 //BA.debugLineNum = 118;BA.debugLine="mqttGetData.Disconnect";
_mqttgetdata._disconnect /*String*/ ();
 };
 //BA.debugLineNum = 120;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 121;BA.debugLine="End Sub";
return "";
}
public static String  _gamedended() throws Exception{
 //BA.debugLineNum = 219;BA.debugLine="Public Sub GamedEnded";
 //BA.debugLineNum = 220;BA.debugLine="lblSpelduur.TextColor = Colors.Red";
mostCurrent._lblspelduur.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 //BA.debugLineNum = 222;BA.debugLine="End Sub";
return "";
}
public static String  _gamedinprogress() throws Exception{
 //BA.debugLineNum = 224;BA.debugLine="Public Sub GamedInProgress";
 //BA.debugLineNum = 227;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 17;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 19;BA.debugLine="Dim parser As JSONParser";
mostCurrent._parser = new anywheresoftware.b4a.objects.collections.JSONParser();
 //BA.debugLineNum = 20;BA.debugLine="Private lblP1Name As Label";
mostCurrent._lblp1name = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 21;BA.debugLine="Private lblP2Name As Label";
mostCurrent._lblp2name = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 22;BA.debugLine="Private lblP1Maken100 As Label";
mostCurrent._lblp1maken100 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 23;BA.debugLine="Private lblP1Maken10 As Label";
mostCurrent._lblp1maken10 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Private lblP1Maken1 As Label";
mostCurrent._lblp1maken1 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Private lblP1100 As Label";
mostCurrent._lblp1100 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Private lblP110 As Label";
mostCurrent._lblp110 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Private lblP11 As Label";
mostCurrent._lblp11 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 28;BA.debugLine="Private lblP2Maken100 As Label";
mostCurrent._lblp2maken100 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 29;BA.debugLine="Private lblP2Maken10 As Label";
mostCurrent._lblp2maken10 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 30;BA.debugLine="Private lblP2Maken1 As Label";
mostCurrent._lblp2maken1 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 31;BA.debugLine="Private lblP2100 As Label";
mostCurrent._lblp2100 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 32;BA.debugLine="Private lblP210 As Label";
mostCurrent._lblp210 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 33;BA.debugLine="Private lblP1Moy As Label";
mostCurrent._lblp1moy = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 34;BA.debugLine="Private lblP2Moy As Label";
mostCurrent._lblp2moy = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 35;BA.debugLine="Private lblP21 As Label";
mostCurrent._lblp21 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 36;BA.debugLine="Private lblBeurt100 As Label";
mostCurrent._lblbeurt100 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 37;BA.debugLine="Private lblBeurt10 As Label";
mostCurrent._lblbeurt10 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 38;BA.debugLine="Private lblBeurt1 As Label";
mostCurrent._lblbeurt1 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 39;BA.debugLine="Private imgP2Play As ImageView";
mostCurrent._imgp2play = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 40;BA.debugLine="Private imgP1Play As ImageView";
mostCurrent._imgp1play = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 46;BA.debugLine="Private imgNoData As ImageView";
mostCurrent._imgnodata = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 47;BA.debugLine="Private lblNoData As Label";
mostCurrent._lblnodata = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 48;BA.debugLine="Private imgSponsor As ImageView";
mostCurrent._imgsponsor = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 50;BA.debugLine="Private lblTafelNaam As Label";
mostCurrent._lbltafelnaam = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 51;BA.debugLine="Private lblSpelduur As Label";
mostCurrent._lblspelduur = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 52;BA.debugLine="Private pnlSponsor As Panel";
mostCurrent._pnlsponsor = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 53;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 6;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 7;BA.debugLine="Dim mqttGetData As mqttGetBordData";
_mqttgetdata = new nl.pdeg.bordondroid.mqttgetborddata();
 //BA.debugLineNum = 8;BA.debugLine="Dim dataTmr As Timer";
_datatmr = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 9;BA.debugLine="Dim dotCount As Int = 0";
_dotcount = (int) (0);
 //BA.debugLineNum = 10;BA.debugLine="Dim waitText As String";
_waittext = "";
 //BA.debugLineNum = 11;BA.debugLine="Dim cs As CSBuilder";
_cs = new anywheresoftware.b4a.objects.CSBuilder();
 //BA.debugLineNum = 12;BA.debugLine="Private SubString As String";
_substring = "";
 //BA.debugLineNum = 13;BA.debugLine="Dim lastMessageTime As Long";
_lastmessagetime = 0L;
 //BA.debugLineNum = 14;BA.debugLine="Dim lastMessageTimer As Timer";
_lastmessagetimer = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 15;BA.debugLine="End Sub";
return "";
}
public static String  _setimg() throws Exception{
anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _bmp = null;
int _nuleen = 0;
 //BA.debugLineNum = 229;BA.debugLine="Private Sub SetImg";
 //BA.debugLineNum = 230;BA.debugLine="Dim bmp As Bitmap";
_bmp = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
 //BA.debugLineNum = 231;BA.debugLine="Dim nuleen As Int = Rnd(0,2)";
_nuleen = anywheresoftware.b4a.keywords.Common.Rnd((int) (0),(int) (2));
 //BA.debugLineNum = 233;BA.debugLine="If nuleen = 0 Then";
if (_nuleen==0) { 
 //BA.debugLineNum = 234;BA.debugLine="bmp = LoadBitmapResize(File.DirAssets, \"sven1.jp";
_bmp = anywheresoftware.b4a.keywords.Common.LoadBitmapResize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"sven1.jpg",mostCurrent._imgsponsor.getWidth(),mostCurrent._imgsponsor.getHeight(),anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 236;BA.debugLine="bmp = LoadBitmapResize(File.DirAssets, \"sven_oud";
_bmp = anywheresoftware.b4a.keywords.Common.LoadBitmapResize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"sven_oud.jpg",mostCurrent._imgsponsor.getWidth(),mostCurrent._imgsponsor.getHeight(),anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 238;BA.debugLine="imgSponsor.SetBackgroundImage(bmp)";
mostCurrent._imgsponsor.SetBackgroundImageNew((android.graphics.Bitmap)(_bmp.getObject()));
 //BA.debugLineNum = 239;BA.debugLine="End Sub";
return "";
}
public static String  _tmrlastmessase_tick() throws Exception{
 //BA.debugLineNum = 80;BA.debugLine="Sub tmrLastMessase_Tick";
 //BA.debugLineNum = 81;BA.debugLine="If (DateTime.Now-lastMessageTime) >= 120*1000 The";
if ((anywheresoftware.b4a.keywords.Common.DateTime.getNow()-_lastmessagetime)>=120*1000) { 
 //BA.debugLineNum = 82;BA.debugLine="ToastMessageShow(\"PPPPPP\", True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("PPPPPP"),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 83;BA.debugLine="lblSpelduur.TextColor = Colors.Red";
mostCurrent._lblspelduur.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 };
 //BA.debugLineNum = 85;BA.debugLine="End Sub";
return "";
}
public static void  _updatebordwhenclient(nl.pdeg.bordondroid.main._message _data) throws Exception{
ResumableSub_UpdateBordWhenClient rsub = new ResumableSub_UpdateBordWhenClient(null,_data);
rsub.resume(processBA, null);
}
public static class ResumableSub_UpdateBordWhenClient extends BA.ResumableSub {
public ResumableSub_UpdateBordWhenClient(nl.pdeg.bordondroid.serverboard parent,nl.pdeg.bordondroid.main._message _data) {
this.parent = parent;
this._data = _data;
}
nl.pdeg.bordondroid.serverboard parent;
nl.pdeg.bordondroid.main._message _data;
String _number = "";
String _str = "";
anywheresoftware.b4a.objects.collections.Map _root = null;
anywheresoftware.b4a.objects.collections.Map _score = null;
anywheresoftware.b4a.objects.collections.Map _p1 = null;
anywheresoftware.b4a.objects.collections.Map _p2 = null;
String _moyenne = "";
anywheresoftware.b4a.objects.collections.Map _aan_stoot = null;
String _speler = "";
anywheresoftware.b4a.objects.collections.Map _spelduur = null;
String _tijd = "";
anywheresoftware.b4a.objects.collections.Map _beurten = null;
String _aantal = "";

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
 //BA.debugLineNum = 134;BA.debugLine="If imgNoData.Visible Then";
if (true) break;

case 1:
//if
this.state = 4;
if (parent.mostCurrent._imgnodata.getVisible()) { 
this.state = 3;
}if (true) break;

case 3:
//C
this.state = 4;
 //BA.debugLineNum = 135;BA.debugLine="dataTmr.Enabled = False";
parent._datatmr.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 136;BA.debugLine="imgNoData.SetVisibleAnimated(1000, False)";
parent.mostCurrent._imgnodata.SetVisibleAnimated((int) (1000),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 140;BA.debugLine="Sleep(1200)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,this,(int) (1200));
this.state = 11;
return;
case 11:
//C
this.state = 4;
;
 if (true) break;

case 4:
//C
this.state = 5;
;
 //BA.debugLineNum = 142;BA.debugLine="lblSpelduur.TextColor = Colors.White";
parent.mostCurrent._lblspelduur.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 143;BA.debugLine="Dim Number, str As String";
_number = "";
_str = "";
 //BA.debugLineNum = 144;BA.debugLine="str = data.Body";
_str = _data.Body /*String*/ ;
 //BA.debugLineNum = 146;BA.debugLine="parser.Initialize(str)";
parent.mostCurrent._parser.Initialize(_str);
 //BA.debugLineNum = 147;BA.debugLine="Dim root As Map = parser.NextObject";
_root = new anywheresoftware.b4a.objects.collections.Map();
_root = parent.mostCurrent._parser.NextObject();
 //BA.debugLineNum = 148;BA.debugLine="Dim score As Map = root.Get(\"score\")";
_score = new anywheresoftware.b4a.objects.collections.Map();
_score.setObject((anywheresoftware.b4a.objects.collections.Map.MyMap)(_root.Get((Object)("score"))));
 //BA.debugLineNum = 149;BA.debugLine="Dim p1 As Map = score.Get(\"p1\")";
_p1 = new anywheresoftware.b4a.objects.collections.Map();
_p1.setObject((anywheresoftware.b4a.objects.collections.Map.MyMap)(_score.Get((Object)("p1"))));
 //BA.debugLineNum = 155;BA.debugLine="Dim p2 As Map = score.Get(\"p2\")";
_p2 = new anywheresoftware.b4a.objects.collections.Map();
_p2.setObject((anywheresoftware.b4a.objects.collections.Map.MyMap)(_score.Get((Object)("p2"))));
 //BA.debugLineNum = 160;BA.debugLine="Dim moyenne As String = p2.Get(\"moyenne\")";
_moyenne = BA.ObjectToString(_p2.Get((Object)("moyenne")));
 //BA.debugLineNum = 161;BA.debugLine="Dim aan_stoot As Map = score.Get(\"aan_stoot\")";
_aan_stoot = new anywheresoftware.b4a.objects.collections.Map();
_aan_stoot.setObject((anywheresoftware.b4a.objects.collections.Map.MyMap)(_score.Get((Object)("aan_stoot"))));
 //BA.debugLineNum = 162;BA.debugLine="Dim speler As String = aan_stoot.Get(\"speler\")";
_speler = BA.ObjectToString(_aan_stoot.Get((Object)("speler")));
 //BA.debugLineNum = 163;BA.debugLine="Dim spelduur As Map = score.Get(\"spelduur\")";
_spelduur = new anywheresoftware.b4a.objects.collections.Map();
_spelduur.setObject((anywheresoftware.b4a.objects.collections.Map.MyMap)(_score.Get((Object)("spelduur"))));
 //BA.debugLineNum = 164;BA.debugLine="Dim tijd As String = spelduur.Get(\"tijd\")";
_tijd = BA.ObjectToString(_spelduur.Get((Object)("tijd")));
 //BA.debugLineNum = 167;BA.debugLine="Dim beurten As Map = score.Get(\"beurten\")";
_beurten = new anywheresoftware.b4a.objects.collections.Map();
_beurten.setObject((anywheresoftware.b4a.objects.collections.Map.MyMap)(_score.Get((Object)("beurten"))));
 //BA.debugLineNum = 168;BA.debugLine="Dim aantal As String = beurten.Get(\"aantal\")";
_aantal = BA.ObjectToString(_beurten.Get((Object)("aantal")));
 //BA.debugLineNum = 172;BA.debugLine="lblP1Name.Text = p1.Get(\"naam\")";
parent.mostCurrent._lblp1name.setText(BA.ObjectToCharSequence(_p1.Get((Object)("naam"))));
 //BA.debugLineNum = 173;BA.debugLine="Number = p1.Get(\"caram\")";
_number = BA.ObjectToString(_p1.Get((Object)("caram")));
 //BA.debugLineNum = 174;BA.debugLine="lblP1100.Text = Number.SubString2(0,1)";
parent.mostCurrent._lblp1100.setText(BA.ObjectToCharSequence(_number.substring((int) (0),(int) (1))));
 //BA.debugLineNum = 175;BA.debugLine="lblP110.Text = Number.SubString2(1,2)";
parent.mostCurrent._lblp110.setText(BA.ObjectToCharSequence(_number.substring((int) (1),(int) (2))));
 //BA.debugLineNum = 176;BA.debugLine="lblP11.Text = Number.SubString2(2,3)";
parent.mostCurrent._lblp11.setText(BA.ObjectToCharSequence(_number.substring((int) (2),(int) (3))));
 //BA.debugLineNum = 177;BA.debugLine="Number = p1.Get(\"maken\")";
_number = BA.ObjectToString(_p1.Get((Object)("maken")));
 //BA.debugLineNum = 178;BA.debugLine="lblP1Maken100.Text = Number.SubString2(0,1)";
parent.mostCurrent._lblp1maken100.setText(BA.ObjectToCharSequence(_number.substring((int) (0),(int) (1))));
 //BA.debugLineNum = 179;BA.debugLine="lblP1Maken10.Text = Number.SubString2(1,2)";
parent.mostCurrent._lblp1maken10.setText(BA.ObjectToCharSequence(_number.substring((int) (1),(int) (2))));
 //BA.debugLineNum = 180;BA.debugLine="lblP1Maken1.Text = Number.SubString2(2,3)";
parent.mostCurrent._lblp1maken1.setText(BA.ObjectToCharSequence(_number.substring((int) (2),(int) (3))));
 //BA.debugLineNum = 182;BA.debugLine="lblP1Moy.Text = cs.Initialize.Typeface(Typeface.F";
parent.mostCurrent._lblp1moy.setText(BA.ObjectToCharSequence(parent._cs.Initialize().Typeface(anywheresoftware.b4a.keywords.Common.Typeface.getFONTAWESOME()).Append(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Chr((int) (0xf201)))).Append(BA.ObjectToCharSequence("  ")).Append(BA.ObjectToCharSequence(_p1.Get((Object)("moyenne")))).PopAll().getObject()));
 //BA.debugLineNum = 188;BA.debugLine="lblP2Name.Text = p2.Get(\"naam\")";
parent.mostCurrent._lblp2name.setText(BA.ObjectToCharSequence(_p2.Get((Object)("naam"))));
 //BA.debugLineNum = 189;BA.debugLine="Number = p2.Get(\"caram\")";
_number = BA.ObjectToString(_p2.Get((Object)("caram")));
 //BA.debugLineNum = 190;BA.debugLine="lblP2100.Text = Number.SubString2(0,1)";
parent.mostCurrent._lblp2100.setText(BA.ObjectToCharSequence(_number.substring((int) (0),(int) (1))));
 //BA.debugLineNum = 191;BA.debugLine="lblP210.Text = Number.SubString2(1,2)";
parent.mostCurrent._lblp210.setText(BA.ObjectToCharSequence(_number.substring((int) (1),(int) (2))));
 //BA.debugLineNum = 192;BA.debugLine="lblP21.Text = Number.SubString2(2,3)";
parent.mostCurrent._lblp21.setText(BA.ObjectToCharSequence(_number.substring((int) (2),(int) (3))));
 //BA.debugLineNum = 193;BA.debugLine="Number = p2.Get(\"maken\")";
_number = BA.ObjectToString(_p2.Get((Object)("maken")));
 //BA.debugLineNum = 194;BA.debugLine="lblP2Maken100.Text = Number.SubString2(0,1)";
parent.mostCurrent._lblp2maken100.setText(BA.ObjectToCharSequence(_number.substring((int) (0),(int) (1))));
 //BA.debugLineNum = 195;BA.debugLine="lblP2Maken10.Text = Number.SubString2(1,2)";
parent.mostCurrent._lblp2maken10.setText(BA.ObjectToCharSequence(_number.substring((int) (1),(int) (2))));
 //BA.debugLineNum = 196;BA.debugLine="lblP2Maken1.Text = Number.SubString2(2,3)";
parent.mostCurrent._lblp2maken1.setText(BA.ObjectToCharSequence(_number.substring((int) (2),(int) (3))));
 //BA.debugLineNum = 198;BA.debugLine="cs.Initialize.Append(\"\").Typeface(Typeface.FONTAW";
parent._cs.Initialize().Append(BA.ObjectToCharSequence("")).Typeface(anywheresoftware.b4a.keywords.Common.Typeface.getFONTAWESOME()).Append(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Chr((int) (0xf201)))).PopAll();
 //BA.debugLineNum = 199;BA.debugLine="lblP2Moy.Text = cs.Initialize.Typeface(Typeface.F";
parent.mostCurrent._lblp2moy.setText(BA.ObjectToCharSequence(parent._cs.Initialize().Typeface(anywheresoftware.b4a.keywords.Common.Typeface.getFONTAWESOME()).Append(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Chr((int) (0xf201)))).Append(BA.ObjectToCharSequence("  ")).Append(BA.ObjectToCharSequence(_p2.Get((Object)("moyenne")))).PopAll().getObject()));
 //BA.debugLineNum = 201;BA.debugLine="lblBeurt100.Text = aantal.SubString2(0,1)";
parent.mostCurrent._lblbeurt100.setText(BA.ObjectToCharSequence(_aantal.substring((int) (0),(int) (1))));
 //BA.debugLineNum = 202;BA.debugLine="lblBeurt10.Text = aantal.SubString2(1,2)";
parent.mostCurrent._lblbeurt10.setText(BA.ObjectToCharSequence(_aantal.substring((int) (1),(int) (2))));
 //BA.debugLineNum = 203;BA.debugLine="lblBeurt1.Text = aantal.SubString2(2,3)";
parent.mostCurrent._lblbeurt1.setText(BA.ObjectToCharSequence(_aantal.substring((int) (2),(int) (3))));
 //BA.debugLineNum = 205;BA.debugLine="lblSpelduur.Text = tijd'score.Get(\"spelduur\")";
parent.mostCurrent._lblspelduur.setText(BA.ObjectToCharSequence(_tijd));
 //BA.debugLineNum = 206;BA.debugLine="lblSpelduur.Text = cs.Initialize.Typeface(Typefac";
parent.mostCurrent._lblspelduur.setText(BA.ObjectToCharSequence(parent._cs.Initialize().Typeface(anywheresoftware.b4a.keywords.Common.Typeface.getFONTAWESOME()).Append(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Chr((int) (0xf253)))).Append(BA.ObjectToCharSequence("  ")).Append(BA.ObjectToCharSequence(_tijd)).PopAll().getObject()));
 //BA.debugLineNum = 210;BA.debugLine="imgP1Play.Visible = False";
parent.mostCurrent._imgp1play.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 211;BA.debugLine="imgP2Play.Visible = False";
parent.mostCurrent._imgp2play.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 212;BA.debugLine="If speler = 1 Then";
if (true) break;

case 5:
//if
this.state = 10;
if ((_speler).equals(BA.NumberToString(1))) { 
this.state = 7;
}else {
this.state = 9;
}if (true) break;

case 7:
//C
this.state = 10;
 //BA.debugLineNum = 213;BA.debugLine="imgP1Play.Visible = True";
parent.mostCurrent._imgp1play.setVisible(anywheresoftware.b4a.keywords.Common.True);
 if (true) break;

case 9:
//C
this.state = 10;
 //BA.debugLineNum = 215;BA.debugLine="imgP2Play.Visible = True";
parent.mostCurrent._imgp2play.setVisible(anywheresoftware.b4a.keywords.Common.True);
 if (true) break;

case 10:
//C
this.state = -1;
;
 //BA.debugLineNum = 217;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
}
