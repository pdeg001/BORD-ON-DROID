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

public class main extends Activity implements B4AActivity{
	public static main mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = false;
	public static final boolean includeTitle = false;
    public static WeakReference<Activity> previousOne;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mostCurrent = this;
		if (processBA == null) {
			processBA = new BA(this.getApplicationContext(), null, null, "nl.pdeg.bordondroid", "nl.pdeg.bordondroid.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
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
		activityBA = new BA(this, layout, processBA, "nl.pdeg.bordondroid", "nl.pdeg.bordondroid.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "nl.pdeg.bordondroid.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
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
		return main.class;
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
        BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
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
            main mc = mostCurrent;
			if (mc == null || mc != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (main) Resume **");
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
public static anywheresoftware.b4a.objects.Timer _tmrbordlastalive = null;
public static nl.pdeg.bordondroid.base _basefile = null;
public static anywheresoftware.b4a.objects.collections.List _baselist = null;
public static nl.pdeg.bordondroid.mqttconnector _mqttbasesubbord = null;
public static anywheresoftware.b4a.objects.Timer _timeout = null;
public static anywheresoftware.b4a.objects.Timer _timeoutseconds = null;
public static int _secondsleft = 0;
public anywheresoftware.b4a.objects.LabelWrapper _lblbordname = null;
public anywheresoftware.b4a.objects.PanelWrapper _pnlbord = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbllastcheck = null;
public anywheresoftware.b4a.objects.PanelWrapper _pnlnobords = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblversion = null;
public anywheresoftware.b4a.objects.PanelWrapper _pnllocationcode = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btncancel = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnok = null;
public anywheresoftware.b4a.objects.PanelWrapper _pnllocation = null;
public b4a.example3.customlistview _clvserver = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblcurrlocation = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblnobord = null;
public anywheresoftware.b4a.objects.PanelWrapper _pnllocationlist = null;
public nl.pdeg.bordondroid.b4xfloattextfield _edtfloatcode = null;
public nl.pdeg.bordondroid.b4xfloattextfield _edtfloatdescription = null;
public nl.pdeg.bordondroid.b4xloadingindicator _b4xloadingindicator1 = null;
public nl.pdeg.bordondroid.b4xloadingindicator _b4xloadingindicator2 = null;
public static long _connecttime = 0L;
public anywheresoftware.b4a.objects.LabelWrapper _lblplayer = null;
public anywheresoftware.b4a.objects.PanelWrapper _pnlreload = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblaliveindicator = null;
public b4a.example.dateutils _dateutils = null;
public nl.pdeg.bordondroid.starter _starter = null;
public nl.pdeg.bordondroid.serverboard _serverboard = null;
public nl.pdeg.bordondroid.locations _locations = null;
public nl.pdeg.bordondroid.selectlocation _selectlocation = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
vis = vis | (serverboard.mostCurrent != null);
vis = vis | (locations.mostCurrent != null);
vis = vis | (selectlocation.mostCurrent != null);
return vis;}
public static class _message{
public boolean IsInitialized;
public String Body;
public String From;
public void Initialize() {
IsInitialized = true;
Body = "";
From = "";
}
@Override
		public String toString() {
			return BA.TypeToString(this, false);
		}}
public static class _bordstatus{
public boolean IsInitialized;
public String ip;
public String name;
public long timeStamp;
public boolean alive;
public String players;
public void Initialize() {
IsInitialized = true;
ip = "";
name = "";
timeStamp = 0L;
alive = false;
players = "";
}
@Override
		public String toString() {
			return BA.TypeToString(this, false);
		}}
public static class _bordfound{
public boolean IsInitialized;
public String name;
public String ip;
public void Initialize() {
IsInitialized = true;
name = "";
ip = "";
}
@Override
		public String toString() {
			return BA.TypeToString(this, false);
		}}
public static class _locationbord{
public boolean IsInitialized;
public String code;
public String description;
public String isdefault;
public void Initialize() {
IsInitialized = true;
code = "";
description = "";
isdefault = "";
}
@Override
		public String toString() {
			return BA.TypeToString(this, false);
		}}
public static class _pubborddata{
public boolean IsInitialized;
public anywheresoftware.b4a.objects.collections.List data;
public String from;
public void Initialize() {
IsInitialized = true;
data = new anywheresoftware.b4a.objects.collections.List();
from = "";
}
@Override
		public String toString() {
			return BA.TypeToString(this, false);
		}}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 55;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 56;BA.debugLine="Activity.LoadLayout(\"main\")";
mostCurrent._activity.LoadLayout("main",mostCurrent.activityBA);
 //BA.debugLineNum = 58;BA.debugLine="timeOutSeconds.Initialize(\"timeOutSeconds\", 1000)";
_timeoutseconds.Initialize(processBA,"timeOutSeconds",(long) (1000));
 //BA.debugLineNum = 59;BA.debugLine="timeOut.Initialize(\"timeOut\", 20*1000)";
_timeout.Initialize(processBA,"timeOut",(long) (20*1000));
 //BA.debugLineNum = 60;BA.debugLine="tmrBordLastAlive.Initialize(\"tmrBordAlive\", 10000";
_tmrbordlastalive.Initialize(processBA,"tmrBordAlive",(long) (10000));
 //BA.debugLineNum = 61;BA.debugLine="If FirstTime Then mqttBaseSubBord.Initialize";
if (_firsttime) { 
_mqttbasesubbord._initialize /*String*/ (processBA);};
 //BA.debugLineNum = 63;BA.debugLine="Starter.appVersion = $\"${Application.LabelName} v";
mostCurrent._starter._appversion /*String*/  = (""+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(anywheresoftware.b4a.keywords.Common.Application.getLabelName()))+" v"+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(anywheresoftware.b4a.keywords.Common.Application.getVersionName()))+"");
 //BA.debugLineNum = 64;BA.debugLine="lblVersion.Text = Starter.appVersion";
mostCurrent._lblversion.setText(BA.ObjectToCharSequence(mostCurrent._starter._appversion /*String*/ ));
 //BA.debugLineNum = 65;BA.debugLine="baseFile.Initialize";
_basefile._initialize /*String*/ (processBA);
 //BA.debugLineNum = 66;BA.debugLine="pnlNobords.Visible = False";
mostCurrent._pnlnobords.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 67;BA.debugLine="End Sub";
return "";
}
public static boolean  _activity_keypress(int _keycode) throws Exception{
 //BA.debugLineNum = 339;BA.debugLine="Private Sub Activity_KeyPress(KeyCode As Int) As B";
 //BA.debugLineNum = 340;BA.debugLine="If KeyCode = KeyCodes.KEYCODE_BACK Then";
if (_keycode==anywheresoftware.b4a.keywords.Common.KeyCodes.KEYCODE_BACK) { 
 //BA.debugLineNum = 341;BA.debugLine="tmrBordLastAlive.Enabled = False";
_tmrbordlastalive.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 342;BA.debugLine="DisconnectMqtt";
_disconnectmqtt();
 //BA.debugLineNum = 343;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 345;BA.debugLine="Return False";
if (true) return anywheresoftware.b4a.keywords.Common.False;
 }else {
 //BA.debugLineNum = 347;BA.debugLine="Return False";
if (true) return anywheresoftware.b4a.keywords.Common.False;
 };
 //BA.debugLineNum = 349;BA.debugLine="End Sub";
return false;
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 90;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 92;BA.debugLine="Starter.mainPaused = True";
mostCurrent._starter._mainpaused /*boolean*/  = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 93;BA.debugLine="Starter.pingMqtt = False";
mostCurrent._starter._pingmqtt /*boolean*/  = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 94;BA.debugLine="ResumeConnection(False)";
_resumeconnection(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 95;BA.debugLine="mqttBaseSubBord.Disconnect";
_mqttbasesubbord._disconnect /*String*/ ();
 //BA.debugLineNum = 96;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 69;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 70;BA.debugLine="ShowSelectLocationButton";
_showselectlocationbutton();
 //BA.debugLineNum = 71;BA.debugLine="If Starter.locationSelected Then";
if (mostCurrent._starter._locationselected /*boolean*/ ) { 
 //BA.debugLineNum = 72;BA.debugLine="CheckConnected";
_checkconnected();
 //BA.debugLineNum = 73;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 75;BA.debugLine="If Starter.mainPaused Then";
if (mostCurrent._starter._mainpaused /*boolean*/ ) { 
 //BA.debugLineNum = 77;BA.debugLine="CheckBordInServerlist";
_checkbordinserverlist();
 //BA.debugLineNum = 78;BA.debugLine="ResumeConnection(True)";
_resumeconnection(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 79;BA.debugLine="Starter.mainPaused = False";
mostCurrent._starter._mainpaused /*boolean*/  = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 80;BA.debugLine="ReconnectToLocation";
_reconnecttolocation();
 }else {
 //BA.debugLineNum = 85;BA.debugLine="ResumeConnection(True)";
_resumeconnection(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 86;BA.debugLine="getBaseList";
_getbaselist();
 };
 //BA.debugLineNum = 88;BA.debugLine="End Sub";
return "";
}
public static void  _addunkownip(String _ip,String _name,String _players) throws Exception{
ResumableSub_AddUnkownIp rsub = new ResumableSub_AddUnkownIp(null,_ip,_name,_players);
rsub.resume(processBA, null);
}
public static class ResumableSub_AddUnkownIp extends BA.ResumableSub {
public ResumableSub_AddUnkownIp(nl.pdeg.bordondroid.main parent,String _ip,String _name,String _players) {
this.parent = parent;
this._ip = _ip;
this._name = _name;
this._players = _players;
}
nl.pdeg.bordondroid.main parent;
String _ip;
String _name;
String _players;
nl.pdeg.bordondroid.main._bordstatus _bordstatus = null;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
 //BA.debugLineNum = 433;BA.debugLine="Dim bordStatus As bordStatus";
_bordstatus = new nl.pdeg.bordondroid.main._bordstatus();
 //BA.debugLineNum = 434;BA.debugLine="bordStatus.Initialize";
_bordstatus.Initialize();
 //BA.debugLineNum = 435;BA.debugLine="bordStatus.ip = ip";
_bordstatus.ip /*String*/  = _ip;
 //BA.debugLineNum = 436;BA.debugLine="bordStatus.name = name";
_bordstatus.name /*String*/  = _name;
 //BA.debugLineNum = 437;BA.debugLine="bordStatus.players = players";
_bordstatus.players /*String*/  = _players;
 //BA.debugLineNum = 438;BA.debugLine="bordStatus.timeStamp = DateTime.Now";
_bordstatus.timeStamp /*long*/  = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 439;BA.debugLine="bordStatus.alive = True";
_bordstatus.alive /*boolean*/  = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 441;BA.debugLine="Starter.serverList.Add(bordStatus)";
parent.mostCurrent._starter._serverlist /*anywheresoftware.b4a.objects.collections.List*/ .Add((Object)(_bordstatus));
 //BA.debugLineNum = 442;BA.debugLine="CheckIpInClv(bordStatus, players)";
_checkipinclv(_bordstatus,_players);
 //BA.debugLineNum = 443;BA.debugLine="baseFile.SetBordDiedByName(name, clvServer, True)";
parent._basefile._setborddiedbyname /*String*/ (_name,parent.mostCurrent._clvserver,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 445;BA.debugLine="If clvServer.GetSize > 0 Then";
if (true) break;

case 1:
//if
this.state = 6;
if (parent.mostCurrent._clvserver._getsize()>0) { 
this.state = 3;
}else {
this.state = 5;
}if (true) break;

case 3:
//C
this.state = 6;
 //BA.debugLineNum = 446;BA.debugLine="pnlNobords.SetVisibleAnimated(500, False)";
parent.mostCurrent._pnlnobords.SetVisibleAnimated((int) (500),anywheresoftware.b4a.keywords.Common.False);
 if (true) break;

case 5:
//C
this.state = 6;
 //BA.debugLineNum = 448;BA.debugLine="pnlNobords.SetVisibleAnimated(500, True)";
parent.mostCurrent._pnlnobords.SetVisibleAnimated((int) (500),anywheresoftware.b4a.keywords.Common.True);
 if (true) break;

case 6:
//C
this.state = -1;
;
 //BA.debugLineNum = 452;BA.debugLine="Sleep(400)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,this,(int) (400));
this.state = 7;
return;
case 7:
//C
this.state = -1;
;
 //BA.debugLineNum = 454;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static void  _btncancel_click() throws Exception{
ResumableSub_btnCancel_Click rsub = new ResumableSub_btnCancel_Click(null);
rsub.resume(processBA, null);
}
public static class ResumableSub_btnCancel_Click extends BA.ResumableSub {
public ResumableSub_btnCancel_Click(nl.pdeg.bordondroid.main parent) {
this.parent = parent;
}
nl.pdeg.bordondroid.main parent;
int _result = 0;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
 //BA.debugLineNum = 485;BA.debugLine="Msgbox2Async(\"Applicatie afsluiten?\", Application";
anywheresoftware.b4a.keywords.Common.Msgbox2Async(BA.ObjectToCharSequence("Applicatie afsluiten?"),BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Application.getLabelName()),"JA","","NEE",anywheresoftware.b4a.keywords.Common.Application.getIcon(),processBA,anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 486;BA.debugLine="Wait For Msgbox_Result(Result As Int)";
anywheresoftware.b4a.keywords.Common.WaitFor("msgbox_result", processBA, this, null);
this.state = 5;
return;
case 5:
//C
this.state = 1;
_result = (Integer) result[0];
;
 //BA.debugLineNum = 487;BA.debugLine="If Result = DialogResponse.POSITIVE Then";
if (true) break;

case 1:
//if
this.state = 4;
if (_result==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
this.state = 3;
}if (true) break;

case 3:
//C
this.state = 4;
 //BA.debugLineNum = 488;BA.debugLine="Activity.Finish";
parent.mostCurrent._activity.Finish();
 if (true) break;

case 4:
//C
this.state = -1;
;
 //BA.debugLineNum = 490;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static void  _msgbox_result(int _result) throws Exception{
}
public static void  _btnok_click() throws Exception{
ResumableSub_btnOk_Click rsub = new ResumableSub_btnOk_Click(null);
rsub.resume(processBA, null);
}
public static class ResumableSub_btnOk_Click extends BA.ResumableSub {
public ResumableSub_btnOk_Click(nl.pdeg.bordondroid.main parent) {
this.parent = parent;
}
nl.pdeg.bordondroid.main parent;
int _result = 0;
anywheresoftware.b4a.objects.IME _ime = null;
String _code = "";
String _description = "";

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
 //BA.debugLineNum = 493;BA.debugLine="If edtFloatCode.Text = \"\" Then";
if (true) break;

case 1:
//if
this.state = 4;
if ((parent.mostCurrent._edtfloatcode._gettext /*String*/ ()).equals("")) { 
this.state = 3;
}if (true) break;

case 3:
//C
this.state = 4;
 //BA.debugLineNum = 494;BA.debugLine="Msgbox2Async(\"Locatie code mag niet leeg zijn\",";
anywheresoftware.b4a.keywords.Common.Msgbox2Async(BA.ObjectToCharSequence("Locatie code mag niet leeg zijn"),BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Application.getLabelName()),"OKE","","",anywheresoftware.b4a.keywords.Common.Application.getIcon(),processBA,anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 495;BA.debugLine="Wait For Msgbox_Result(Result As Int)";
anywheresoftware.b4a.keywords.Common.WaitFor("msgbox_result", processBA, this, null);
this.state = 17;
return;
case 17:
//C
this.state = 4;
_result = (Integer) result[0];
;
 //BA.debugLineNum = 496;BA.debugLine="Return";
if (true) return ;
 if (true) break;

case 4:
//C
this.state = 5;
;
 //BA.debugLineNum = 499;BA.debugLine="Dim ime As IME";
_ime = new anywheresoftware.b4a.objects.IME();
 //BA.debugLineNum = 500;BA.debugLine="ime.Initialize(Me)";
_ime.Initialize(BA.ObjectToString(main.getObject()));
 //BA.debugLineNum = 501;BA.debugLine="Starter.testBaseName = True";
parent.mostCurrent._starter._testbasename /*boolean*/  = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 502;BA.debugLine="Starter.selectedLocationCode = edtFloatCode.Text";
parent.mostCurrent._starter._selectedlocationcode /*String*/  = parent.mostCurrent._edtfloatcode._gettext /*String*/ ();
 //BA.debugLineNum = 504;BA.debugLine="Starter.SetLastWill(\"bordpubdied\")";
parent.mostCurrent._starter._setlastwill /*String*/ ("bordpubdied");
 //BA.debugLineNum = 506;BA.debugLine="Starter.SetSubBase(Starter.selectedLocationCode)";
parent.mostCurrent._starter._setsubbase /*String*/ (parent.mostCurrent._starter._selectedlocationcode /*String*/ );
 //BA.debugLineNum = 508;BA.debugLine="Starter.SetSubString2(\"/pubbord\")";
parent.mostCurrent._starter._setsubstring2 /*String*/ ("/pubbord");
 //BA.debugLineNum = 510;BA.debugLine="Starter.SetUnsubscribeString2(\"\")";
parent.mostCurrent._starter._setunsubscribestring2 /*String*/ ("");
 //BA.debugLineNum = 511;BA.debugLine="mqttBaseSubBord.Initialize";
parent._mqttbasesubbord._initialize /*String*/ (processBA);
 //BA.debugLineNum = 512;BA.debugLine="Sleep(100)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,this,(int) (100));
this.state = 18;
return;
case 18:
//C
this.state = 5;
;
 //BA.debugLineNum = 513;BA.debugLine="mqttBaseSubBord.Connect";
parent._mqttbasesubbord._connect /*String*/ ();
 //BA.debugLineNum = 515;BA.debugLine="Sleep(1000)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,this,(int) (1000));
this.state = 19;
return;
case 19:
//C
this.state = 5;
;
 //BA.debugLineNum = 517;BA.debugLine="If mqttBaseSubBord.connected = False Then";
if (true) break;

case 5:
//if
this.state = 16;
if (parent._mqttbasesubbord._connected /*boolean*/ ==anywheresoftware.b4a.keywords.Common.False) { 
this.state = 7;
}else {
this.state = 9;
}if (true) break;

case 7:
//C
this.state = 16;
 //BA.debugLineNum = 518;BA.debugLine="Msgbox2Async(\"Kan geen verbinding maken met loca";
anywheresoftware.b4a.keywords.Common.Msgbox2Async(BA.ObjectToCharSequence("Kan geen verbinding maken met locatie"),BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Application.getLabelName()),"OKE","","",anywheresoftware.b4a.keywords.Common.Application.getIcon(),processBA,anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 519;BA.debugLine="Wait For Msgbox_Result(Result As Int)";
anywheresoftware.b4a.keywords.Common.WaitFor("msgbox_result", processBA, this, null);
this.state = 20;
return;
case 20:
//C
this.state = 16;
_result = (Integer) result[0];
;
 if (true) break;

case 9:
//C
this.state = 10;
 //BA.debugLineNum = 521;BA.debugLine="Dim code, description As String";
_code = "";
_description = "";
 //BA.debugLineNum = 522;BA.debugLine="lblCurrLocation.Text = edtFloatDescription.Text";
parent.mostCurrent._lblcurrlocation.setText(BA.ObjectToCharSequence(parent.mostCurrent._edtfloatdescription._gettext /*String*/ ()));
 //BA.debugLineNum = 523;BA.debugLine="Starter.selectedLocationDescription = edtFloatDe";
parent.mostCurrent._starter._selectedlocationdescription /*String*/  = parent.mostCurrent._edtfloatdescription._gettext /*String*/ ();
 //BA.debugLineNum = 524;BA.debugLine="code = edtFloatCode.Text";
_code = parent.mostCurrent._edtfloatcode._gettext /*String*/ ();
 //BA.debugLineNum = 525;BA.debugLine="If edtFloatDescription.Text = \"\" Then edtFloatDe";
if (true) break;

case 10:
//if
this.state = 15;
if ((parent.mostCurrent._edtfloatdescription._gettext /*String*/ ()).equals("")) { 
this.state = 12;
;}if (true) break;

case 12:
//C
this.state = 15;
parent.mostCurrent._edtfloatdescription._settext /*String*/ ("Nieuwe locatie");
if (true) break;

case 15:
//C
this.state = 16;
;
 //BA.debugLineNum = 526;BA.debugLine="description = edtFloatDescription.Text";
_description = parent.mostCurrent._edtfloatdescription._gettext /*String*/ ();
 //BA.debugLineNum = 529;BA.debugLine="mqttBaseSubBord.Disconnect";
parent._mqttbasesubbord._disconnect /*String*/ ();
 //BA.debugLineNum = 530;BA.debugLine="pnlLocationCOde.Visible = False";
parent.mostCurrent._pnllocationcode.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 531;BA.debugLine="baseFile.SetBase(code, description, \"1\")";
parent._basefile._setbase /*String*/ (_code,_description,"1");
 //BA.debugLineNum = 532;BA.debugLine="InitConnection";
_initconnection();
 //BA.debugLineNum = 533;BA.debugLine="ime.HideKeyboard";
_ime.HideKeyboard(mostCurrent.activityBA);
 if (true) break;

case 16:
//C
this.state = -1;
;
 //BA.debugLineNum = 535;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static String  _checkbordexists(nl.pdeg.bordondroid.main._message _bord) throws Exception{
boolean _bordexists = false;
String _strplayers = "";
String _name = "";
String[] _strdata = null;
nl.pdeg.bordondroid.main._bordstatus _lst = null;
 //BA.debugLineNum = 382;BA.debugLine="Sub CheckBordExists(bord As Message)";
 //BA.debugLineNum = 383;BA.debugLine="timeOut.Enabled = False";
_timeout.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 384;BA.debugLine="If lblAliveIndicator.Visible Then";
if (mostCurrent._lblaliveindicator.getVisible()) { 
 //BA.debugLineNum = 385;BA.debugLine="lblAliveIndicator.SetVisibleAnimated(500, False)";
mostCurrent._lblaliveindicator.SetVisibleAnimated((int) (500),anywheresoftware.b4a.keywords.Common.False);
 }else {
 //BA.debugLineNum = 387;BA.debugLine="lblAliveIndicator.SetVisibleAnimated(500, True)";
mostCurrent._lblaliveindicator.SetVisibleAnimated((int) (500),anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 389;BA.debugLine="Dim bordExists As Boolean = False";
_bordexists = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 390;BA.debugLine="Dim strPlayers As String";
_strplayers = "";
 //BA.debugLineNum = 393;BA.debugLine="If bord.Body.Length = 0 Then Return";
if (_bord.Body /*String*/ .length()==0) { 
if (true) return "";};
 //BA.debugLineNum = 394;BA.debugLine="Dim name As String = bord.Body";
_name = _bord.Body /*String*/ ;
 //BA.debugLineNum = 398;BA.debugLine="strPlayers = name'baseFile.SetPlayertext(name)";
_strplayers = _name;
 //BA.debugLineNum = 399;BA.debugLine="If name.IndexOf(\"|\") > -1 Then";
if (_name.indexOf("|")>-1) { 
 //BA.debugLineNum = 400;BA.debugLine="Dim strData() As String = Regex.Split(\"\\|\", name";
_strdata = anywheresoftware.b4a.keywords.Common.Regex.Split("\\|",_name);
 //BA.debugLineNum = 401;BA.debugLine="name = strData(0)";
_name = _strdata[(int) (0)];
 };
 //BA.debugLineNum = 405;BA.debugLine="connectTime = -1";
_connecttime = (long) (-1);
 //BA.debugLineNum = 406;BA.debugLine="If Starter.serverList.Size = 0 Then";
if (mostCurrent._starter._serverlist /*anywheresoftware.b4a.objects.collections.List*/ .getSize()==0) { 
 //BA.debugLineNum = 407;BA.debugLine="AddUnkownIp(\"\", name, strPlayers)";
_addunkownip("",_name,_strplayers);
 //BA.debugLineNum = 408;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 411;BA.debugLine="For Each lst As bordStatus In Starter.serverList";
{
final anywheresoftware.b4a.BA.IterableList group21 = mostCurrent._starter._serverlist /*anywheresoftware.b4a.objects.collections.List*/ ;
final int groupLen21 = group21.getSize()
;int index21 = 0;
;
for (; index21 < groupLen21;index21++){
_lst = (nl.pdeg.bordondroid.main._bordstatus)(group21.Get(index21));
 //BA.debugLineNum = 416;BA.debugLine="If lst.name = name Then";
if ((_lst.name /*String*/ ).equals(_name)) { 
 //BA.debugLineNum = 417;BA.debugLine="bordExists = True";
_bordexists = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 419;BA.debugLine="lst.timeStamp = DateTime.Now";
_lst.timeStamp /*long*/  = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 420;BA.debugLine="lst.alive = True";
_lst.alive /*boolean*/  = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 422;BA.debugLine="baseFile.CheckPlayers(strPlayers, name, clvServ";
_basefile._checkplayers /*String*/ (_strplayers,_name,mostCurrent._clvserver);
 };
 }
};
 //BA.debugLineNum = 426;BA.debugLine="If Not(bordExists) Then";
if (anywheresoftware.b4a.keywords.Common.Not(_bordexists)) { 
 //BA.debugLineNum = 427;BA.debugLine="AddUnkownIp(\"\", name, strPlayers)";
_addunkownip("",_name,_strplayers);
 //BA.debugLineNum = 428;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 430;BA.debugLine="End Sub";
return "";
}
public static String  _checkbordinserverlist() throws Exception{
nl.pdeg.bordondroid.main._bordstatus _bs = null;
 //BA.debugLineNum = 142;BA.debugLine="Private Sub CheckBordInServerlist";
 //BA.debugLineNum = 143;BA.debugLine="If Starter.serverList.Size > 0 Then";
if (mostCurrent._starter._serverlist /*anywheresoftware.b4a.objects.collections.List*/ .getSize()>0) { 
 //BA.debugLineNum = 144;BA.debugLine="clvServer.Clear";
mostCurrent._clvserver._clear();
 //BA.debugLineNum = 145;BA.debugLine="For Each bs As bordStatus In Starter.serverList";
{
final anywheresoftware.b4a.BA.IterableList group3 = mostCurrent._starter._serverlist /*anywheresoftware.b4a.objects.collections.List*/ ;
final int groupLen3 = group3.getSize()
;int index3 = 0;
;
for (; index3 < groupLen3;index3++){
_bs = (nl.pdeg.bordondroid.main._bordstatus)(group3.Get(index3));
 //BA.debugLineNum = 146;BA.debugLine="clvServer.Add(genUnitList(bs.name, bs.alive, bs";
mostCurrent._clvserver._add((anywheresoftware.b4a.objects.B4XViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.B4XViewWrapper(), (java.lang.Object)(_genunitlist(_bs.name /*String*/ ,_bs.alive /*boolean*/ ,_bs.players /*String*/ ).getObject())),(Object)(""));
 }
};
 };
 //BA.debugLineNum = 150;BA.debugLine="End Sub";
return "";
}
public static boolean  _checkclientconnected() throws Exception{
 //BA.debugLineNum = 553;BA.debugLine="Private Sub CheckClientConnected As Boolean";
 //BA.debugLineNum = 554;BA.debugLine="Return CallSub(mqttBaseSubBord, \"GetClientConnect";
if (true) return BA.ObjectToBoolean(anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)(_mqttbasesubbord),"GetClientConnected"));
 //BA.debugLineNum = 555;BA.debugLine="End Sub";
return false;
}
public static String  _checkconnected() throws Exception{
 //BA.debugLineNum = 98;BA.debugLine="Private Sub CheckConnected";
 //BA.debugLineNum = 99;BA.debugLine="If clvServer.Size > 0 And Starter.selectedLocatio";
if (mostCurrent._clvserver._getsize()>0 && (mostCurrent._starter._selectedlocationcode /*String*/ ).equals(mostCurrent._lblcurrlocation.getText())) { 
 //BA.debugLineNum = 100;BA.debugLine="mqttBaseSubBord.Connect";
_mqttbasesubbord._connect /*String*/ ();
 //BA.debugLineNum = 101;BA.debugLine="tmrBordLastAlive.Enabled = True";
_tmrbordlastalive.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 102;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 104;BA.debugLine="secondsLeft = timeOut.Interval/1000";
_secondsleft = (int) (_timeout.getInterval()/(double)1000);
 //BA.debugLineNum = 105;BA.debugLine="Starter.serverList.Initialize";
mostCurrent._starter._serverlist /*anywheresoftware.b4a.objects.collections.List*/ .Initialize();
 //BA.debugLineNum = 106;BA.debugLine="clvServer.Clear";
mostCurrent._clvserver._clear();
 //BA.debugLineNum = 107;BA.debugLine="lblCurrLocation.Text = Starter.selectedLocationDe";
mostCurrent._lblcurrlocation.setText(BA.ObjectToCharSequence(mostCurrent._starter._selectedlocationdescription /*String*/ ));
 //BA.debugLineNum = 108;BA.debugLine="B4XLoadingIndicator1.Show";
mostCurrent._b4xloadingindicator1._show /*String*/ ();
 //BA.debugLineNum = 109;BA.debugLine="B4XLoadingIndicator2.Show";
mostCurrent._b4xloadingindicator2._show /*String*/ ();
 //BA.debugLineNum = 110;BA.debugLine="pnlNobords.Visible = True";
mostCurrent._pnlnobords.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 111;BA.debugLine="timeOutSeconds.Enabled = True";
_timeoutseconds.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 112;BA.debugLine="Starter.locationSelected = False";
mostCurrent._starter._locationselected /*boolean*/  = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 113;BA.debugLine="ResumeConnection(True)";
_resumeconnection(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 114;BA.debugLine="ReconnectToLocation";
_reconnecttolocation();
 //BA.debugLineNum = 115;BA.debugLine="End Sub";
return "";
}
public static boolean  _checkconnecttime() throws Exception{
 //BA.debugLineNum = 286;BA.debugLine="Sub checkConnectTime As Boolean";
 //BA.debugLineNum = 287;BA.debugLine="Return True";
if (true) return anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 288;BA.debugLine="If connectTime <> -1 Then";
if (_connecttime!=-1) { 
 //BA.debugLineNum = 289;BA.debugLine="Log(\"KWDKWMKDM\")";
anywheresoftware.b4a.keywords.Common.LogImpl("71048579","KWDKWMKDM",0);
 //BA.debugLineNum = 290;BA.debugLine="pnlNobords.Visible = True";
mostCurrent._pnlnobords.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 291;BA.debugLine="mqttBaseSubBord.Disconnect";
_mqttbasesubbord._disconnect /*String*/ ();
 //BA.debugLineNum = 292;BA.debugLine="tmrBordLastAlive.Enabled = False";
_tmrbordlastalive.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 293;BA.debugLine="pnlNobords.Visible = False";
mostCurrent._pnlnobords.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 294;BA.debugLine="Msgbox2Async(\"Geen borden gevonden\", Application";
anywheresoftware.b4a.keywords.Common.Msgbox2Async(BA.ObjectToCharSequence("Geen borden gevonden"),BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Application.getLabelName()),"OKE","","",anywheresoftware.b4a.keywords.Common.Application.getIcon(),processBA,anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 295;BA.debugLine="Return False";
if (true) return anywheresoftware.b4a.keywords.Common.False;
 };
 //BA.debugLineNum = 297;BA.debugLine="Return True";
if (true) return anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 298;BA.debugLine="End Sub";
return false;
}
public static String  _checkipinclv(nl.pdeg.bordondroid.main._bordstatus _bord,String _players) throws Exception{
anywheresoftware.b4a.objects.PanelWrapper _p = null;
int _i = 0;
 //BA.debugLineNum = 456;BA.debugLine="Sub CheckIpInClv(bord As bordStatus, players As St";
 //BA.debugLineNum = 457;BA.debugLine="Dim p As Panel";
_p = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 460;BA.debugLine="For i = 0 To clvServer.GetSize-1";
{
final int step2 = 1;
final int limit2 = (int) (mostCurrent._clvserver._getsize()-1);
_i = (int) (0) ;
for (;_i <= limit2 ;_i = _i + step2 ) {
 //BA.debugLineNum = 461;BA.debugLine="p = clvServer.GetPanel(i)";
_p.setObject((android.view.ViewGroup)(mostCurrent._clvserver._getpanel(_i).getObject()));
 //BA.debugLineNum = 462;BA.debugLine="If p.Tag = bord.name Then";
if ((_p.getTag()).equals((Object)(_bord.name /*String*/ ))) { 
 //BA.debugLineNum = 464;BA.debugLine="baseFile.SetBordDiedByName(bord.name, clvServer";
_basefile._setborddiedbyname /*String*/ (_bord.name /*String*/ ,mostCurrent._clvserver,_bord.alive /*boolean*/ );
 };
 }
};
 //BA.debugLineNum = 469;BA.debugLine="pnlNobords.SetVisibleAnimated(1000, False)";
mostCurrent._pnlnobords.SetVisibleAnimated((int) (1000),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 470;BA.debugLine="clvServer.Add(genUnitList(bord.name, bord.alive,";
mostCurrent._clvserver._add((anywheresoftware.b4a.objects.B4XViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.B4XViewWrapper(), (java.lang.Object)(_genunitlist(_bord.name /*String*/ ,_bord.alive /*boolean*/ ,_players).getObject())),(Object)(""));
 //BA.debugLineNum = 471;BA.debugLine="End Sub";
return "";
}
public static String  _checklastalivetime() throws Exception{
long _currtime = 0L;
nl.pdeg.bordondroid.main._bordstatus _bd = null;
long _timediff = 0L;
 //BA.debugLineNum = 300;BA.debugLine="Sub CheckLastAliveTime";
 //BA.debugLineNum = 303;BA.debugLine="If clvServer.Size = 0 Then Return";
if (mostCurrent._clvserver._getsize()==0) { 
if (true) return "";};
 //BA.debugLineNum = 304;BA.debugLine="Dim currTime As Long = DateTime.Now";
_currtime = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 309;BA.debugLine="If lblLastCheck.IsInitialized = False Then";
if (mostCurrent._lbllastcheck.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 310;BA.debugLine="lblNoBord.Visible = False";
mostCurrent._lblnobord.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 311;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 313;BA.debugLine="lblLastCheck.Text = $\"Laatste controle $Time{Date";
mostCurrent._lbllastcheck.setText(BA.ObjectToCharSequence(("Laatste controle "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("time",(Object)(anywheresoftware.b4a.keywords.Common.DateTime.getNow()))+"")));
 //BA.debugLineNum = 316;BA.debugLine="For Each bd As bordStatus In Starter.serverList";
{
final anywheresoftware.b4a.BA.IterableList group8 = mostCurrent._starter._serverlist /*anywheresoftware.b4a.objects.collections.List*/ ;
final int groupLen8 = group8.getSize()
;int index8 = 0;
;
for (; index8 < groupLen8;index8++){
_bd = (nl.pdeg.bordondroid.main._bordstatus)(group8.Get(index8));
 //BA.debugLineNum = 317;BA.debugLine="Dim timeDiff As Long = currTime-bd.timeStamp";
_timediff = (long) (_currtime-_bd.timeStamp /*long*/ );
 //BA.debugLineNum = 318;BA.debugLine="If timeDiff >= Starter.serverDied Then";
if (_timediff>=mostCurrent._starter._serverdied /*long*/ ) { 
 //BA.debugLineNum = 319;BA.debugLine="bd.alive = False";
_bd.alive /*boolean*/  = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 320;BA.debugLine="baseFile.SetBordDiedByName(bd.name, clvServer,";
_basefile._setborddiedbyname /*String*/ (_bd.name /*String*/ ,mostCurrent._clvserver,_bd.alive /*boolean*/ );
 }else {
 //BA.debugLineNum = 322;BA.debugLine="baseFile.SetBordDiedByName(bd.name, clvServer,";
_basefile._setborddiedbyname /*String*/ (_bd.name /*String*/ ,mostCurrent._clvserver,_bd.alive /*boolean*/ );
 };
 }
};
 //BA.debugLineNum = 326;BA.debugLine="End Sub";
return "";
}
public static void  _clvserver_itemclick(int _index,Object _value) throws Exception{
ResumableSub_clvServer_ItemClick rsub = new ResumableSub_clvServer_ItemClick(null,_index,_value);
rsub.resume(processBA, null);
}
public static class ResumableSub_clvServer_ItemClick extends BA.ResumableSub {
public ResumableSub_clvServer_ItemClick(nl.pdeg.bordondroid.main parent,int _index,Object _value) {
this.parent = parent;
this._index = _index;
this._value = _value;
}
nl.pdeg.bordondroid.main parent;
int _index;
Object _value;
anywheresoftware.b4a.objects.PanelWrapper _p = null;
String _unit = "";

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
 //BA.debugLineNum = 263;BA.debugLine="Dim p As Panel = clvServer.GetPanel(Index)";
_p = new anywheresoftware.b4a.objects.PanelWrapper();
_p.setObject((android.view.ViewGroup)(parent.mostCurrent._clvserver._getpanel(_index).getObject()));
 //BA.debugLineNum = 264;BA.debugLine="Dim unit As String";
_unit = "";
 //BA.debugLineNum = 266;BA.debugLine="unit = baseFile.GetSelectedLabelTagFromPanel(p, \"";
_unit = parent._basefile._getselectedlabeltagfrompanel /*String*/ (_p,"name");
 //BA.debugLineNum = 267;BA.debugLine="If baseFile.GetBordAlive(unit) = False Then";
if (true) break;

case 1:
//if
this.state = 4;
if (parent._basefile._getbordalive /*boolean*/ (_unit)==anywheresoftware.b4a.keywords.Common.False) { 
this.state = 3;
}if (true) break;

case 3:
//C
this.state = 4;
 //BA.debugLineNum = 268;BA.debugLine="baseFile.ShowCustomToast($\"Bord niet gevonden, c";
parent._basefile._showcustomtoast /*String*/ ((Object)(("Bord niet gevonden, controleer of het bord \"online\" is")),anywheresoftware.b4a.keywords.Common.False,anywheresoftware.b4a.keywords.Common.Colors.Red);
 //BA.debugLineNum = 269;BA.debugLine="Return";
if (true) return ;
 if (true) break;

case 4:
//C
this.state = -1;
;
 //BA.debugLineNum = 272;BA.debugLine="Starter.mainPaused = True";
parent.mostCurrent._starter._mainpaused /*boolean*/  = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 273;BA.debugLine="Starter.DiscoveredServer = unit";
parent.mostCurrent._starter._discoveredserver /*String*/  = _unit;
 //BA.debugLineNum = 274;BA.debugLine="CallSub2(Starter, \"SetLastWill\", \"recvdied\")";
anywheresoftware.b4a.keywords.Common.CallSubNew2(processBA,(Object)(parent.mostCurrent._starter.getObject()),"SetLastWill",(Object)("recvdied"));
 //BA.debugLineNum = 275;BA.debugLine="CallSub2(Starter, \"SetUnit\", PrepTopicName(unit).";
anywheresoftware.b4a.keywords.Common.CallSubNew2(processBA,(Object)(parent.mostCurrent._starter.getObject()),"SetUnit",(Object)(_preptopicname(_unit).toLowerCase()));
 //BA.debugLineNum = 276;BA.debugLine="DisconnectMqtt";
_disconnectmqtt();
 //BA.debugLineNum = 277;BA.debugLine="Sleep(100)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,this,(int) (100));
this.state = 5;
return;
case 5:
//C
this.state = -1;
;
 //BA.debugLineNum = 278;BA.debugLine="StartActivity(ServerBoard)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(parent.mostCurrent._serverboard.getObject()));
 //BA.debugLineNum = 279;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static String  _connectionerror() throws Exception{
 //BA.debugLineNum = 583;BA.debugLine="Sub ConnectionError";
 //BA.debugLineNum = 586;BA.debugLine="End Sub";
return "";
}
public static String  _deletedlocationactive() throws Exception{
 //BA.debugLineNum = 573;BA.debugLine="Sub DeletedLocationActive";
 //BA.debugLineNum = 574;BA.debugLine="mqttBaseSubBord.Disconnect";
_mqttbasesubbord._disconnect /*String*/ ();
 //BA.debugLineNum = 575;BA.debugLine="clvServer.Clear";
mostCurrent._clvserver._clear();
 //BA.debugLineNum = 576;BA.debugLine="getBaseList";
_getbaselist();
 //BA.debugLineNum = 577;BA.debugLine="End Sub";
return "";
}
public static String  _disconnectmqtt() throws Exception{
 //BA.debugLineNum = 351;BA.debugLine="Sub DisconnectMqtt";
 //BA.debugLineNum = 352;BA.debugLine="If mqttBaseSubBord.connected Then";
if (_mqttbasesubbord._connected /*boolean*/ ) { 
 //BA.debugLineNum = 353;BA.debugLine="mqttBaseSubBord.Disconnect";
_mqttbasesubbord._disconnect /*String*/ ();
 };
 //BA.debugLineNum = 355;BA.debugLine="If CheckClientConnected Then";
if (_checkclientconnected()) { 
 //BA.debugLineNum = 356;BA.debugLine="mqttBaseSubBord.Disconnect";
_mqttbasesubbord._disconnect /*String*/ ();
 };
 //BA.debugLineNum = 359;BA.debugLine="End Sub";
return "";
}
public static String  _edtfloatcode_textchanged(String _old,String _new) throws Exception{
 //BA.debugLineNum = 565;BA.debugLine="Sub edtFloatCode_TextChanged (Old As String, New A";
 //BA.debugLineNum = 566;BA.debugLine="If New.Length > 0 Then";
if (_new.length()>0) { 
 //BA.debugLineNum = 567;BA.debugLine="btnOk.Enabled = True";
mostCurrent._btnok.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 569;BA.debugLine="btnOk.Enabled = False";
mostCurrent._btnok.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 571;BA.debugLine="End Sub";
return "";
}
public static String  _edtfloatdescription_textchanged(String _old,String _new) throws Exception{
 //BA.debugLineNum = 561;BA.debugLine="Sub edtFloatDescription_TextChanged (Old As String";
 //BA.debugLineNum = 563;BA.debugLine="End Sub";
return "";
}
public static String  _edtlocationcode_textchanged(String _old,String _new) throws Exception{
 //BA.debugLineNum = 537;BA.debugLine="Sub edtLocationCode_TextChanged (Old As String, Ne";
 //BA.debugLineNum = 538;BA.debugLine="If New.Length > 0 Then";
if (_new.length()>0) { 
 //BA.debugLineNum = 539;BA.debugLine="btnOk.Enabled = True";
mostCurrent._btnok.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 541;BA.debugLine="btnOk.Enabled = False";
mostCurrent._btnok.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 543;BA.debugLine="End Sub";
return "";
}
public static anywheresoftware.b4a.objects.PanelWrapper  _genunitlist(String _name,boolean _alive,String _players) throws Exception{
anywheresoftware.b4a.objects.PanelWrapper _p = null;
 //BA.debugLineNum = 361;BA.debugLine="Sub genUnitList(name As String, alive As Boolean,";
 //BA.debugLineNum = 362;BA.debugLine="Dim p As Panel";
_p = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 363;BA.debugLine="p.Initialize(Me)";
_p.Initialize(mostCurrent.activityBA,BA.ObjectToString(main.getObject()));
 //BA.debugLineNum = 364;BA.debugLine="p.SetLayout(0dip, 0dip, clvServer.AsView.Width, 1";
_p.SetLayout(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)),mostCurrent._clvserver._asview().getWidth(),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (125)));
 //BA.debugLineNum = 365;BA.debugLine="p.LoadLayout(\"clvBorden\")";
_p.LoadLayout("clvBorden",mostCurrent.activityBA);
 //BA.debugLineNum = 366;BA.debugLine="p.Tag = name";
_p.setTag((Object)(_name));
 //BA.debugLineNum = 368;BA.debugLine="lblBordName.Text = name.Trim";
mostCurrent._lblbordname.setText(BA.ObjectToCharSequence(_name.trim()));
 //BA.debugLineNum = 369;BA.debugLine="lblLastCheck.Text = $\"Laatste controle $Time{Date";
mostCurrent._lbllastcheck.setText(BA.ObjectToCharSequence(("Laatste controle "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("time",(Object)(anywheresoftware.b4a.keywords.Common.DateTime.getNow()))+"")));
 //BA.debugLineNum = 371;BA.debugLine="lblPLayer.Text = baseFile.SetPlayertext(players)";
mostCurrent._lblplayer.setText(BA.ObjectToCharSequence(_basefile._setplayertext /*Object*/ (_players)));
 //BA.debugLineNum = 373;BA.debugLine="lblPLayer.Tag = \"players\"";
mostCurrent._lblplayer.setTag((Object)("players"));
 //BA.debugLineNum = 375;BA.debugLine="If Not(alive) Then";
if (anywheresoftware.b4a.keywords.Common.Not(_alive)) { 
 //BA.debugLineNum = 376;BA.debugLine="lblBordName.Color = Colors.Red";
mostCurrent._lblbordname.setColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 //BA.debugLineNum = 377;BA.debugLine="lblBordName.TextColor = Colors.White";
mostCurrent._lblbordname.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 };
 //BA.debugLineNum = 379;BA.debugLine="Return p";
if (true) return _p;
 //BA.debugLineNum = 380;BA.debugLine="End Sub";
return null;
}
public static String  _getbaselist() throws Exception{
int _listsize = 0;
nl.pdeg.bordondroid.main._locationbord _loc = null;
 //BA.debugLineNum = 184;BA.debugLine="Private Sub getBaseList";
 //BA.debugLineNum = 185;BA.debugLine="Dim listSize As Int = 0";
_listsize = (int) (0);
 //BA.debugLineNum = 187;BA.debugLine="baseList.Initialize";
_baselist.Initialize();
 //BA.debugLineNum = 188;BA.debugLine="baseList = baseFile.GetBase";
_baselist = _basefile._getbase /*anywheresoftware.b4a.objects.collections.List*/ ();
 //BA.debugLineNum = 190;BA.debugLine="If CallSub(baseFile, \"CheckBaseListExists\") = Fal";
if ((anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)(_basefile),"CheckBaseListExists")).equals((Object)(anywheresoftware.b4a.keywords.Common.False))) { 
 //BA.debugLineNum = 191;BA.debugLine="pnlLocationCOde.Visible = True";
mostCurrent._pnllocationcode.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 192;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 195;BA.debugLine="If baseList.IsInitialized Then";
if (_baselist.IsInitialized()) { 
 //BA.debugLineNum = 196;BA.debugLine="listSize = baseList.Size";
_listsize = _baselist.getSize();
 };
 //BA.debugLineNum = 198;BA.debugLine="If listSize > 1 Then";
if (_listsize>1) { 
 //BA.debugLineNum = 200;BA.debugLine="pnlLocationList.SetVisibleAnimated(500, True)";
mostCurrent._pnllocationlist.SetVisibleAnimated((int) (500),anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 203;BA.debugLine="pnlLocationList.SetVisibleAnimated(500, False)";
mostCurrent._pnllocationlist.SetVisibleAnimated((int) (500),anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 207;BA.debugLine="lblNoBord.Text = \"Wachten op borden\"";
mostCurrent._lblnobord.setText(BA.ObjectToCharSequence("Wachten op borden"));
 //BA.debugLineNum = 209;BA.debugLine="If baseList.IsInitialized = False Then";
if (_baselist.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 210;BA.debugLine="pnlLocationCOde.Visible = True";
mostCurrent._pnllocationcode.setVisible(anywheresoftware.b4a.keywords.Common.True);
 }else if(mostCurrent._clvserver._getsize()>0) { 
 }else if(_baselist.getSize()==1) { 
 //BA.debugLineNum = 214;BA.debugLine="Dim loc As locationBord";
_loc = new nl.pdeg.bordondroid.main._locationbord();
 //BA.debugLineNum = 215;BA.debugLine="loc.Initialize";
_loc.Initialize();
 //BA.debugLineNum = 216;BA.debugLine="loc = baseList.Get(0)";
_loc = (nl.pdeg.bordondroid.main._locationbord)(_baselist.Get((int) (0)));
 //BA.debugLineNum = 217;BA.debugLine="Starter.selectedLocationCode = loc.code";
mostCurrent._starter._selectedlocationcode /*String*/  = _loc.code /*String*/ ;
 //BA.debugLineNum = 218;BA.debugLine="Starter.selectedLocationDescription = loc.descri";
mostCurrent._starter._selectedlocationdescription /*String*/  = _loc.description /*String*/ ;
 //BA.debugLineNum = 219;BA.debugLine="lblCurrLocation.Text = loc.description";
mostCurrent._lblcurrlocation.setText(BA.ObjectToCharSequence(_loc.description /*String*/ ));
 //BA.debugLineNum = 220;BA.debugLine="InitConnection";
_initconnection();
 }else {
 //BA.debugLineNum = 222;BA.debugLine="StartActivity(SelectLocation)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(mostCurrent._selectlocation.getObject()));
 };
 //BA.debugLineNum = 224;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 31;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 32;BA.debugLine="Private lblBordName As Label";
mostCurrent._lblbordname = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 33;BA.debugLine="Private pnlBord As Panel";
mostCurrent._pnlbord = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 34;BA.debugLine="Private lblLastCheck As Label";
mostCurrent._lbllastcheck = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 35;BA.debugLine="Private pnlNobords As Panel";
mostCurrent._pnlnobords = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 36;BA.debugLine="Private lblVersion As Label";
mostCurrent._lblversion = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 37;BA.debugLine="Private pnlLocationCOde As Panel";
mostCurrent._pnllocationcode = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 38;BA.debugLine="Private btnCancel As Button";
mostCurrent._btncancel = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 39;BA.debugLine="Private btnOk As Button";
mostCurrent._btnok = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 40;BA.debugLine="Private pnlLocation As Panel";
mostCurrent._pnllocation = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 41;BA.debugLine="Private clvServer As CustomListView";
mostCurrent._clvserver = new b4a.example3.customlistview();
 //BA.debugLineNum = 42;BA.debugLine="Private lblCurrLocation As Label";
mostCurrent._lblcurrlocation = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 43;BA.debugLine="Private lblNoBord As Label";
mostCurrent._lblnobord = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 44;BA.debugLine="Private pnlLocationList As Panel";
mostCurrent._pnllocationlist = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 45;BA.debugLine="Private edtFloatCode As B4XFloatTextField";
mostCurrent._edtfloatcode = new nl.pdeg.bordondroid.b4xfloattextfield();
 //BA.debugLineNum = 46;BA.debugLine="Private edtFloatDescription As B4XFloatTextField";
mostCurrent._edtfloatdescription = new nl.pdeg.bordondroid.b4xfloattextfield();
 //BA.debugLineNum = 47;BA.debugLine="Private B4XLoadingIndicator1 As B4XLoadingIndicat";
mostCurrent._b4xloadingindicator1 = new nl.pdeg.bordondroid.b4xloadingindicator();
 //BA.debugLineNum = 48;BA.debugLine="Private B4XLoadingIndicator2 As B4XLoadingIndicat";
mostCurrent._b4xloadingindicator2 = new nl.pdeg.bordondroid.b4xloadingindicator();
 //BA.debugLineNum = 49;BA.debugLine="Private connectTime As Long";
_connecttime = 0L;
 //BA.debugLineNum = 50;BA.debugLine="Private lblPLayer As Label";
mostCurrent._lblplayer = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 51;BA.debugLine="Private pnlReload As Panel";
mostCurrent._pnlreload = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 52;BA.debugLine="Private lblAliveIndicator As Label";
mostCurrent._lblaliveindicator = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 53;BA.debugLine="End Sub";
return "";
}
public static String  _initconnection() throws Exception{
 //BA.debugLineNum = 248;BA.debugLine="Sub InitConnection";
 //BA.debugLineNum = 249;BA.debugLine="If mqttBaseSubBord.connected Then mqttBaseSubBord";
if (_mqttbasesubbord._connected /*boolean*/ ) { 
_mqttbasesubbord._disconnect /*String*/ ();};
 //BA.debugLineNum = 252;BA.debugLine="lblCurrLocation.Text = Starter.selectedLocationDe";
mostCurrent._lblcurrlocation.setText(BA.ObjectToCharSequence(mostCurrent._starter._selectedlocationdescription /*String*/ ));
 //BA.debugLineNum = 253;BA.debugLine="CallSub2(Starter, \"SetLastWill\", \"bordpubdied\")";
anywheresoftware.b4a.keywords.Common.CallSubNew2(processBA,(Object)(mostCurrent._starter.getObject()),"SetLastWill",(Object)("bordpubdied"));
 //BA.debugLineNum = 254;BA.debugLine="CallSub2(Starter, \"SetSubBase\", Starter.selectedL";
anywheresoftware.b4a.keywords.Common.CallSubNew2(processBA,(Object)(mostCurrent._starter.getObject()),"SetSubBase",(Object)(mostCurrent._starter._selectedlocationcode /*String*/ ));
 //BA.debugLineNum = 255;BA.debugLine="CallSub2(Starter, \"SetSubString2\", \"/pubbord\")";
anywheresoftware.b4a.keywords.Common.CallSubNew2(processBA,(Object)(mostCurrent._starter.getObject()),"SetSubString2",(Object)("/pubbord"));
 //BA.debugLineNum = 256;BA.debugLine="CallSub2(Starter, \"SetUnsubscribeString2\", \"\")";
anywheresoftware.b4a.keywords.Common.CallSubNew2(processBA,(Object)(mostCurrent._starter.getObject()),"SetUnsubscribeString2",(Object)(""));
 //BA.debugLineNum = 258;BA.debugLine="StartConnection";
_startconnection();
 //BA.debugLineNum = 259;BA.debugLine="End Sub";
return "";
}
public static String  _lblviewbord_click() throws Exception{
anywheresoftware.b4a.objects.ConcreteViewWrapper _v = null;
anywheresoftware.b4a.objects.PanelWrapper _p = null;
 //BA.debugLineNum = 473;BA.debugLine="Sub lblViewBord_Click";
 //BA.debugLineNum = 474;BA.debugLine="Dim v As View = Sender";
_v = new anywheresoftware.b4a.objects.ConcreteViewWrapper();
_v.setObject((android.view.View)(anywheresoftware.b4a.keywords.Common.Sender(mostCurrent.activityBA)));
 //BA.debugLineNum = 475;BA.debugLine="Dim p As Panel = v.Parent";
_p = new anywheresoftware.b4a.objects.PanelWrapper();
_p.setObject((android.view.ViewGroup)(_v.getParent()));
 //BA.debugLineNum = 477;BA.debugLine="clvServer_ItemClick (clvServer.GetItemFromView(p)";
_clvserver_itemclick(mostCurrent._clvserver._getitemfromview((anywheresoftware.b4a.objects.B4XViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.B4XViewWrapper(), (java.lang.Object)(_p.getObject()))),anywheresoftware.b4a.keywords.Common.Null);
 //BA.debugLineNum = 478;BA.debugLine="End Sub";
return "";
}
public static String  _pnllocation_click() throws Exception{
 //BA.debugLineNum = 545;BA.debugLine="Sub pnlLocation_Click";
 //BA.debugLineNum = 547;BA.debugLine="If CheckClientConnected Then";
if (_checkclientconnected()) { 
 //BA.debugLineNum = 548;BA.debugLine="mqttBaseSubBord.Disconnect";
_mqttbasesubbord._disconnect /*String*/ ();
 };
 //BA.debugLineNum = 550;BA.debugLine="StartActivity(locations)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(mostCurrent._locations.getObject()));
 //BA.debugLineNum = 551;BA.debugLine="End Sub";
return "";
}
public static String  _pnllocationlist_click() throws Exception{
 //BA.debugLineNum = 557;BA.debugLine="Sub pnlLocationList_Click";
 //BA.debugLineNum = 558;BA.debugLine="StartActivity(SelectLocation)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(mostCurrent._selectlocation.getObject()));
 //BA.debugLineNum = 559;BA.debugLine="End Sub";
return "";
}
public static String  _pnlreload_click() throws Exception{
 //BA.debugLineNum = 588;BA.debugLine="Sub pnlReload_Click";
 //BA.debugLineNum = 589;BA.debugLine="timeOutSeconds.Enabled = True";
_timeoutseconds.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 590;BA.debugLine="clvServer.Clear";
mostCurrent._clvserver._clear();
 //BA.debugLineNum = 591;BA.debugLine="Starter.serverList.Initialize";
mostCurrent._starter._serverlist /*anywheresoftware.b4a.objects.collections.List*/ .Initialize();
 //BA.debugLineNum = 592;BA.debugLine="mqttBaseSubBord.Disconnect";
_mqttbasesubbord._disconnect /*String*/ ();
 //BA.debugLineNum = 593;BA.debugLine="InitConnection";
_initconnection();
 //BA.debugLineNum = 595;BA.debugLine="End Sub";
return "";
}
public static String  _preptopicname(String _bord) throws Exception{
 //BA.debugLineNum = 480;BA.debugLine="Private Sub PrepTopicName(bord As String) As Strin";
 //BA.debugLineNum = 481;BA.debugLine="Return bord.Replace(\" \", \"\")";
if (true) return _bord.replace(" ","");
 //BA.debugLineNum = 482;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        b4a.example.dateutils._process_globals();
main._process_globals();
starter._process_globals();
serverboard._process_globals();
locations._process_globals();
selectlocation._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 16;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 17;BA.debugLine="Type Message (Body As String, From As String)";
;
 //BA.debugLineNum = 18;BA.debugLine="Type bordStatus(ip As String, name As String, tim";
;
 //BA.debugLineNum = 19;BA.debugLine="Type bordFound(name As String, ip As String)";
;
 //BA.debugLineNum = 20;BA.debugLine="Type locationBord(code As String, description As";
;
 //BA.debugLineNum = 21;BA.debugLine="Type pubBordData(data As List, from As String)";
;
 //BA.debugLineNum = 22;BA.debugLine="Private tmrBordLastAlive As Timer";
_tmrbordlastalive = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 23;BA.debugLine="Private baseFile As Base";
_basefile = new nl.pdeg.bordondroid.base();
 //BA.debugLineNum = 24;BA.debugLine="Private baseList As List";
_baselist = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 25;BA.debugLine="Private mqttBaseSubBord As MqttConnector";
_mqttbasesubbord = new nl.pdeg.bordondroid.mqttconnector();
 //BA.debugLineNum = 26;BA.debugLine="Private timeOut As Timer";
_timeout = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 27;BA.debugLine="Private timeOutSeconds As Timer";
_timeoutseconds = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 28;BA.debugLine="Private secondsLeft As Int";
_secondsleft = 0;
 //BA.debugLineNum = 29;BA.debugLine="End Sub";
return "";
}
public static String  _reconnecttolocation() throws Exception{
 //BA.debugLineNum = 236;BA.debugLine="Sub ReconnectToLocation";
 //BA.debugLineNum = 237;BA.debugLine="timeOut.Enabled = True";
_timeout.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 238;BA.debugLine="mqttBaseSubBord.Initialize";
_mqttbasesubbord._initialize /*String*/ (processBA);
 //BA.debugLineNum = 239;BA.debugLine="CallSub2(Starter, \"SetLastWill\", \"bordpubdied\")";
anywheresoftware.b4a.keywords.Common.CallSubNew2(processBA,(Object)(mostCurrent._starter.getObject()),"SetLastWill",(Object)("bordpubdied"));
 //BA.debugLineNum = 240;BA.debugLine="CallSub2(Starter, \"SetSubBase\", Starter.selectedL";
anywheresoftware.b4a.keywords.Common.CallSubNew2(processBA,(Object)(mostCurrent._starter.getObject()),"SetSubBase",(Object)(mostCurrent._starter._selectedlocationcode /*String*/ ));
 //BA.debugLineNum = 241;BA.debugLine="CallSub2(Starter, \"SetSubString2\", \"/pubbord\")";
anywheresoftware.b4a.keywords.Common.CallSubNew2(processBA,(Object)(mostCurrent._starter.getObject()),"SetSubString2",(Object)("/pubbord"));
 //BA.debugLineNum = 242;BA.debugLine="CallSub2(Starter, \"SetUnsubscribeString2\", \"\")";
anywheresoftware.b4a.keywords.Common.CallSubNew2(processBA,(Object)(mostCurrent._starter.getObject()),"SetUnsubscribeString2",(Object)(""));
 //BA.debugLineNum = 243;BA.debugLine="mqttBaseSubBord.Connect";
_mqttbasesubbord._connect /*String*/ ();
 //BA.debugLineNum = 244;BA.debugLine="End Sub";
return "";
}
public static String  _resumeconnection(boolean _resume) throws Exception{
 //BA.debugLineNum = 334;BA.debugLine="Sub ResumeConnection(resume As Boolean)";
 //BA.debugLineNum = 335;BA.debugLine="tmrBordLastAlive.Interval = 10*1000";
_tmrbordlastalive.setInterval((long) (10*1000));
 //BA.debugLineNum = 336;BA.debugLine="tmrBordLastAlive.Enabled = resume";
_tmrbordlastalive.setEnabled(_resume);
 //BA.debugLineNum = 337;BA.debugLine="End Sub";
return "";
}
public static String  _setbordlastalivetimer() throws Exception{
 //BA.debugLineNum = 152;BA.debugLine="Sub setBordLastAliveTimer";
 //BA.debugLineNum = 153;BA.debugLine="tmrBordLastAlive.Enabled = True";
_tmrbordlastalive.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 155;BA.debugLine="End Sub";
return "";
}
public static String  _shownotconnectedtobroker() throws Exception{
 //BA.debugLineNum = 328;BA.debugLine="Sub ShowNotConnectedToBroker";
 //BA.debugLineNum = 329;BA.debugLine="lblNoBord.Text = \"Verbinding borden verbroken\"";
mostCurrent._lblnobord.setText(BA.ObjectToCharSequence("Verbinding borden verbroken"));
 //BA.debugLineNum = 330;BA.debugLine="pnlNobords.SetVisibleAnimated(1000, True)";
mostCurrent._pnlnobords.SetVisibleAnimated((int) (1000),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 331;BA.debugLine="mqttBaseSubBord.Disconnect";
_mqttbasesubbord._disconnect /*String*/ ();
 //BA.debugLineNum = 332;BA.debugLine="End Sub";
return "";
}
public static String  _showselectlocationbutton() throws Exception{
 //BA.debugLineNum = 579;BA.debugLine="Sub ShowSelectLocationButton";
 //BA.debugLineNum = 580;BA.debugLine="pnlLocationList.Visible = baseFile.GetBase.Size >";
mostCurrent._pnllocationlist.setVisible(_basefile._getbase /*anywheresoftware.b4a.objects.collections.List*/ ().getSize()>1);
 //BA.debugLineNum = 581;BA.debugLine="End Sub";
return "";
}
public static String  _startconnection() throws Exception{
 //BA.debugLineNum = 157;BA.debugLine="Private Sub StartConnection";
 //BA.debugLineNum = 159;BA.debugLine="lblNoBord.Text = $\"Wachten op borden (${\"20\"})\"$";
mostCurrent._lblnobord.setText(BA.ObjectToCharSequence(("Wachten op borden ("+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)("20"))+")")));
 //BA.debugLineNum = 160;BA.debugLine="secondsLeft = timeOut.Interval/1000";
_secondsleft = (int) (_timeout.getInterval()/(double)1000);
 //BA.debugLineNum = 161;BA.debugLine="timeOut.Enabled = True";
_timeout.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 162;BA.debugLine="timeOutSeconds.Enabled = True";
_timeoutseconds.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 163;BA.debugLine="pnlLocationCOde.Visible = False";
mostCurrent._pnllocationcode.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 164;BA.debugLine="B4XLoadingIndicator1.Show";
mostCurrent._b4xloadingindicator1._show /*String*/ ();
 //BA.debugLineNum = 165;BA.debugLine="B4XLoadingIndicator2.Show";
mostCurrent._b4xloadingindicator2._show /*String*/ ();
 //BA.debugLineNum = 167;BA.debugLine="pnlNobords.Visible = True";
mostCurrent._pnlnobords.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 168;BA.debugLine="clvServer.Clear";
mostCurrent._clvserver._clear();
 //BA.debugLineNum = 169;BA.debugLine="mqttBaseSubBord.Initialize";
_mqttbasesubbord._initialize /*String*/ (processBA);
 //BA.debugLineNum = 170;BA.debugLine="mqttBaseSubBord.Connect";
_mqttbasesubbord._connect /*String*/ ();
 //BA.debugLineNum = 172;BA.debugLine="baseFile.GetBase";
_basefile._getbase /*anywheresoftware.b4a.objects.collections.List*/ ();
 //BA.debugLineNum = 175;BA.debugLine="tmrBordLastAlive.Enabled = True";
_tmrbordlastalive.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 176;BA.debugLine="connectTime = DateTime.Now";
_connecttime = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 178;BA.debugLine="End Sub";
return "";
}
public static String  _startselectedlocation() throws Exception{
nl.pdeg.bordondroid.main._locationbord _loc = null;
 //BA.debugLineNum = 226;BA.debugLine="Public Sub StartSelectedLocation";
 //BA.debugLineNum = 227;BA.debugLine="Dim loc As locationBord";
_loc = new nl.pdeg.bordondroid.main._locationbord();
 //BA.debugLineNum = 228;BA.debugLine="loc.Initialize";
_loc.Initialize();
 //BA.debugLineNum = 229;BA.debugLine="loc = baseList.Get(0)";
_loc = (nl.pdeg.bordondroid.main._locationbord)(_baselist.Get((int) (0)));
 //BA.debugLineNum = 232;BA.debugLine="lblCurrLocation.Text = loc.description";
mostCurrent._lblcurrlocation.setText(BA.ObjectToCharSequence(_loc.description /*String*/ ));
 //BA.debugLineNum = 233;BA.debugLine="InitConnection";
_initconnection();
 //BA.debugLineNum = 234;BA.debugLine="End Sub";
return "";
}
public static String  _timeout_tick() throws Exception{
 //BA.debugLineNum = 131;BA.debugLine="Private Sub timeOut_Tick";
 //BA.debugLineNum = 132;BA.debugLine="timeOutSeconds.Enabled = False";
_timeoutseconds.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 133;BA.debugLine="timeOut.Enabled = False";
_timeout.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 134;BA.debugLine="lblNoBord.Text = $\"Wachten op borden\"$";
mostCurrent._lblnobord.setText(BA.ObjectToCharSequence(("Wachten op borden")));
 //BA.debugLineNum = 135;BA.debugLine="Msgbox2Async(\"Geen borden gevonden of aktief\", Ap";
anywheresoftware.b4a.keywords.Common.Msgbox2Async(BA.ObjectToCharSequence("Geen borden gevonden of aktief"),BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Application.getLabelName()),"OKE","","",anywheresoftware.b4a.keywords.Common.Application.getIcon(),processBA,anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 136;BA.debugLine="If pnlNobords.Visible Then";
if (mostCurrent._pnlnobords.getVisible()) { 
 //BA.debugLineNum = 137;BA.debugLine="pnlNobords.SetVisibleAnimated(1500, False)";
mostCurrent._pnlnobords.SetVisibleAnimated((int) (1500),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 138;BA.debugLine="DisconnectMqtt";
_disconnectmqtt();
 };
 //BA.debugLineNum = 140;BA.debugLine="End Sub";
return "";
}
public static void  _timeoutseconds_tick() throws Exception{
ResumableSub_timeOutSeconds_Tick rsub = new ResumableSub_timeOutSeconds_Tick(null);
rsub.resume(processBA, null);
}
public static class ResumableSub_timeOutSeconds_Tick extends BA.ResumableSub {
public ResumableSub_timeOutSeconds_Tick(nl.pdeg.bordondroid.main parent) {
this.parent = parent;
}
nl.pdeg.bordondroid.main parent;
String _dispseconds = "";

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
 //BA.debugLineNum = 118;BA.debugLine="Dim dispSeconds As String";
_dispseconds = "";
 //BA.debugLineNum = 120;BA.debugLine="secondsLeft = secondsLeft -1";
parent._secondsleft = (int) (parent._secondsleft-1);
 //BA.debugLineNum = 121;BA.debugLine="If secondsLeft < 10 Then";
if (true) break;

case 1:
//if
this.state = 6;
if (parent._secondsleft<10) { 
this.state = 3;
}else {
this.state = 5;
}if (true) break;

case 3:
//C
this.state = 6;
 //BA.debugLineNum = 122;BA.debugLine="dispSeconds = $\"0${secondsLeft}\"$";
_dispseconds = ("0"+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(parent._secondsleft))+"");
 if (true) break;

case 5:
//C
this.state = 6;
 //BA.debugLineNum = 124;BA.debugLine="dispSeconds = $\"${secondsLeft}\"$";
_dispseconds = (""+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(parent._secondsleft))+"");
 if (true) break;

case 6:
//C
this.state = -1;
;
 //BA.debugLineNum = 127;BA.debugLine="lblNoBord.Text = $\"Wachten op borden (${dispSecon";
parent.mostCurrent._lblnobord.setText(BA.ObjectToCharSequence(("Wachten op borden ("+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_dispseconds))+")")));
 //BA.debugLineNum = 128;BA.debugLine="Sleep(0)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,this,(int) (0));
this.state = 7;
return;
case 7:
//C
this.state = -1;
;
 //BA.debugLineNum = 129;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static String  _tmrbordalive_tick() throws Exception{
 //BA.debugLineNum = 281;BA.debugLine="Sub tmrBordAlive_Tick";
 //BA.debugLineNum = 282;BA.debugLine="If Not(checkConnectTime) Then Return";
if (anywheresoftware.b4a.keywords.Common.Not(_checkconnecttime())) { 
if (true) return "";};
 //BA.debugLineNum = 283;BA.debugLine="CheckLastAliveTime";
_checklastalivetime();
 //BA.debugLineNum = 284;BA.debugLine="End Sub";
return "";
}
}
