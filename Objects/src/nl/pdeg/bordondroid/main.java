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
public static anywheresoftware.b4a.objects.Timer _vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv4 = null;
public static nl.pdeg.bordondroid.base _v5 = null;
public static anywheresoftware.b4a.objects.collections.List _vvvvvvvv1 = null;
public static nl.pdeg.bordondroid.mqttconnector _vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv3 = null;
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
public static long _vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv5 = 0L;
public anywheresoftware.b4a.objects.LabelWrapper _lblplayer = null;
public b4a.example.dateutils _vvvvvvvv3 = null;
public nl.pdeg.bordondroid.locations _vvvvvvvv5 = null;
public nl.pdeg.bordondroid.selectlocation _vvvvvvvv6 = null;
public nl.pdeg.bordondroid.serverboard _vvvvvvvv7 = null;
public nl.pdeg.bordondroid.starter _vvvvvvvv0 = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
vis = vis | (locations.mostCurrent != null);
vis = vis | (selectlocation.mostCurrent != null);
vis = vis | (serverboard.mostCurrent != null);
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
 //BA.debugLineNum = 50;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 51;BA.debugLine="Activity.LoadLayout(\"main\")";
mostCurrent._activity.LoadLayout("main",mostCurrent.activityBA);
 //BA.debugLineNum = 54;BA.debugLine="If FirstTime Then mqttBaseSubBord.Initialize";
if (_firsttime) { 
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv3._initialize /*String*/ (processBA);};
 //BA.debugLineNum = 56;BA.debugLine="Starter.appVersion = $\"${Application.LabelName} v";
mostCurrent._vvvvvvvv0._vvvv3 /*String*/  = (""+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(anywheresoftware.b4a.keywords.Common.Application.getLabelName()))+" v"+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(anywheresoftware.b4a.keywords.Common.Application.getVersionName()))+"");
 //BA.debugLineNum = 57;BA.debugLine="lblVersion.Text = Starter.appVersion";
mostCurrent._lblversion.setText(BA.ObjectToCharSequence(mostCurrent._vvvvvvvv0._vvvv3 /*String*/ ));
 //BA.debugLineNum = 58;BA.debugLine="baseFile.Initialize";
_v5._initialize /*String*/ (processBA);
 //BA.debugLineNum = 59;BA.debugLine="pnlNobords.Visible = False";
mostCurrent._pnlnobords.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 60;BA.debugLine="End Sub";
return "";
}
public static boolean  _activity_keypress(int _keycode) throws Exception{
 //BA.debugLineNum = 264;BA.debugLine="Private Sub Activity_KeyPress(KeyCode As Int) As B";
 //BA.debugLineNum = 265;BA.debugLine="If KeyCode = KeyCodes.KEYCODE_BACK Then";
if (_keycode==anywheresoftware.b4a.keywords.Common.KeyCodes.KEYCODE_BACK) { 
 //BA.debugLineNum = 266;BA.debugLine="tmrBordLastAlive.Enabled = False";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv4.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 267;BA.debugLine="DisconnectMqtt";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv5();
 //BA.debugLineNum = 268;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 270;BA.debugLine="Return False";
if (true) return anywheresoftware.b4a.keywords.Common.False;
 }else {
 //BA.debugLineNum = 272;BA.debugLine="Return False";
if (true) return anywheresoftware.b4a.keywords.Common.False;
 };
 //BA.debugLineNum = 274;BA.debugLine="End Sub";
return false;
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 77;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 80;BA.debugLine="Starter.mainPaused = True";
mostCurrent._vvvvvvvv0._vvvv6 /*boolean*/  = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 81;BA.debugLine="Starter.pingMqtt = False";
mostCurrent._vvvvvvvv0._vvvv4 /*boolean*/  = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 82;BA.debugLine="ResumeConnection(False)";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv6(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 83;BA.debugLine="mqttBaseSubBord.Disconnect";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv3._vvvvvvvvv3 /*String*/ ();
 //BA.debugLineNum = 84;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 62;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 63;BA.debugLine="ShowSelectLocationButton";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv7();
 //BA.debugLineNum = 64;BA.debugLine="If Starter.mainPaused Then";
if (mostCurrent._vvvvvvvv0._vvvv6 /*boolean*/ ) { 
 //BA.debugLineNum = 65;BA.debugLine="CheckBordInServerlist";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv0();
 //BA.debugLineNum = 66;BA.debugLine="ResumeConnection(True)";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv6(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 67;BA.debugLine="Starter.mainPaused = False";
mostCurrent._vvvvvvvv0._vvvv6 /*boolean*/  = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 68;BA.debugLine="ReconnectToLocation";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv1();
 }else {
 //BA.debugLineNum = 73;BA.debugLine="getBaseList";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv2();
 };
 //BA.debugLineNum = 75;BA.debugLine="End Sub";
return "";
}
public static void  _vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv3(String _ip,String _name,String _players) throws Exception{
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
 //BA.debugLineNum = 352;BA.debugLine="Dim bordStatus As bordStatus";
_bordstatus = new nl.pdeg.bordondroid.main._bordstatus();
 //BA.debugLineNum = 353;BA.debugLine="bordStatus.Initialize";
_bordstatus.Initialize();
 //BA.debugLineNum = 354;BA.debugLine="bordStatus.ip = ip";
_bordstatus.ip /*String*/  = _ip;
 //BA.debugLineNum = 355;BA.debugLine="bordStatus.name = name";
_bordstatus.name /*String*/  = _name;
 //BA.debugLineNum = 356;BA.debugLine="bordStatus.players = players";
_bordstatus.players /*String*/  = _players;
 //BA.debugLineNum = 357;BA.debugLine="bordStatus.timeStamp = DateTime.Now";
_bordstatus.timeStamp /*long*/  = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 358;BA.debugLine="bordStatus.alive = True";
_bordstatus.alive /*boolean*/  = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 360;BA.debugLine="Starter.serverList.Add(bordStatus)";
parent.mostCurrent._vvvvvvvv0._vv7 /*anywheresoftware.b4a.objects.collections.List*/ .Add((Object)(_bordstatus));
 //BA.debugLineNum = 361;BA.debugLine="CheckIpInClv(bordStatus, players)";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv4(_bordstatus,_players);
 //BA.debugLineNum = 362;BA.debugLine="baseFile.SetBordDiedByName(name, clvServer, True)";
parent._v5._vvvvvvv1 /*String*/ (_name,parent.mostCurrent._clvserver,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 364;BA.debugLine="If clvServer.GetSize > 0 Then";
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
 //BA.debugLineNum = 365;BA.debugLine="pnlNobords.SetVisibleAnimated(500, False)";
parent.mostCurrent._pnlnobords.SetVisibleAnimated((int) (500),anywheresoftware.b4a.keywords.Common.False);
 if (true) break;

case 5:
//C
this.state = 6;
 //BA.debugLineNum = 367;BA.debugLine="pnlNobords.SetVisibleAnimated(500, True)";
parent.mostCurrent._pnlnobords.SetVisibleAnimated((int) (500),anywheresoftware.b4a.keywords.Common.True);
 if (true) break;

case 6:
//C
this.state = -1;
;
 //BA.debugLineNum = 371;BA.debugLine="Sleep(400)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,this,(int) (400));
this.state = 7;
return;
case 7:
//C
this.state = -1;
;
 //BA.debugLineNum = 373;BA.debugLine="End Sub";
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
 //BA.debugLineNum = 404;BA.debugLine="Msgbox2Async(\"Applicatie afsluiten?\", Application";
anywheresoftware.b4a.keywords.Common.Msgbox2Async(BA.ObjectToCharSequence("Applicatie afsluiten?"),BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Application.getLabelName()),"JA","","NEE",anywheresoftware.b4a.keywords.Common.Application.getIcon(),processBA,anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 405;BA.debugLine="Wait For Msgbox_Result(Result As Int)";
anywheresoftware.b4a.keywords.Common.WaitFor("msgbox_result", processBA, this, null);
this.state = 5;
return;
case 5:
//C
this.state = 1;
_result = (Integer) result[0];
;
 //BA.debugLineNum = 406;BA.debugLine="If Result = DialogResponse.POSITIVE Then";
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
 //BA.debugLineNum = 407;BA.debugLine="Activity.Finish";
parent.mostCurrent._activity.Finish();
 if (true) break;

case 4:
//C
this.state = -1;
;
 //BA.debugLineNum = 409;BA.debugLine="End Sub";
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
 //BA.debugLineNum = 412;BA.debugLine="If edtFloatCode.Text = \"\" Then";
if (true) break;

case 1:
//if
this.state = 4;
if ((parent.mostCurrent._edtfloatcode._getvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv0 /*String*/ ()).equals("")) { 
this.state = 3;
}if (true) break;

case 3:
//C
this.state = 4;
 //BA.debugLineNum = 413;BA.debugLine="Msgbox2Async(\"Locatie code mag niet leeg zijn\",";
anywheresoftware.b4a.keywords.Common.Msgbox2Async(BA.ObjectToCharSequence("Locatie code mag niet leeg zijn"),BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Application.getLabelName()),"OKE","","",anywheresoftware.b4a.keywords.Common.Application.getIcon(),processBA,anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 414;BA.debugLine="Wait For Msgbox_Result(Result As Int)";
anywheresoftware.b4a.keywords.Common.WaitFor("msgbox_result", processBA, this, null);
this.state = 17;
return;
case 17:
//C
this.state = 4;
_result = (Integer) result[0];
;
 //BA.debugLineNum = 415;BA.debugLine="Return";
if (true) return ;
 if (true) break;

case 4:
//C
this.state = 5;
;
 //BA.debugLineNum = 418;BA.debugLine="Dim ime As IME";
_ime = new anywheresoftware.b4a.objects.IME();
 //BA.debugLineNum = 419;BA.debugLine="ime.Initialize(Me)";
_ime.Initialize(BA.ObjectToString(main.getObject()));
 //BA.debugLineNum = 420;BA.debugLine="Starter.testBaseName = True";
parent.mostCurrent._vvvvvvvv0._vvvv2 /*boolean*/  = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 421;BA.debugLine="Starter.selectedLocationCode = edtFloatCode.Text";
parent.mostCurrent._vvvvvvvv0._vvv0 /*String*/  = parent.mostCurrent._edtfloatcode._getvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv0 /*String*/ ();
 //BA.debugLineNum = 423;BA.debugLine="Starter.SetLastWill(\"bordpubdied\")";
parent.mostCurrent._vvvvvvvv0._setlastwill /*String*/ ("bordpubdied");
 //BA.debugLineNum = 425;BA.debugLine="Starter.SetSubBase(Starter.selectedLocationCode)";
parent.mostCurrent._vvvvvvvv0._setsubbase /*String*/ (parent.mostCurrent._vvvvvvvv0._vvv0 /*String*/ );
 //BA.debugLineNum = 427;BA.debugLine="Starter.SetSubString2(\"/pubbord\")";
parent.mostCurrent._vvvvvvvv0._setsubstring2 /*String*/ ("/pubbord");
 //BA.debugLineNum = 429;BA.debugLine="Starter.SetUnsubscribeString2(\"\")";
parent.mostCurrent._vvvvvvvv0._setunsubscribestring2 /*String*/ ("");
 //BA.debugLineNum = 430;BA.debugLine="mqttBaseSubBord.Initialize";
parent._vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv3._initialize /*String*/ (processBA);
 //BA.debugLineNum = 431;BA.debugLine="Sleep(100)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,this,(int) (100));
this.state = 18;
return;
case 18:
//C
this.state = 5;
;
 //BA.debugLineNum = 432;BA.debugLine="mqttBaseSubBord.Connect";
parent._vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv3._vvvvvvvvv1 /*String*/ ();
 //BA.debugLineNum = 434;BA.debugLine="Sleep(1000)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,this,(int) (1000));
this.state = 19;
return;
case 19:
//C
this.state = 5;
;
 //BA.debugLineNum = 436;BA.debugLine="If mqttBaseSubBord.connected = False Then";
if (true) break;

case 5:
//if
this.state = 16;
if (parent._vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv3._vv5 /*boolean*/ ==anywheresoftware.b4a.keywords.Common.False) { 
this.state = 7;
}else {
this.state = 9;
}if (true) break;

case 7:
//C
this.state = 16;
 //BA.debugLineNum = 437;BA.debugLine="Msgbox2Async(\"Kan geen verbinding maken met loca";
anywheresoftware.b4a.keywords.Common.Msgbox2Async(BA.ObjectToCharSequence("Kan geen verbinding maken met locatie"),BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Application.getLabelName()),"OKE","","",anywheresoftware.b4a.keywords.Common.Application.getIcon(),processBA,anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 438;BA.debugLine="Wait For Msgbox_Result(Result As Int)";
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
 //BA.debugLineNum = 440;BA.debugLine="Dim code, description As String";
_code = "";
_description = "";
 //BA.debugLineNum = 441;BA.debugLine="lblCurrLocation.Text = edtFloatDescription.Text";
parent.mostCurrent._lblcurrlocation.setText(BA.ObjectToCharSequence(parent.mostCurrent._edtfloatdescription._getvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv0 /*String*/ ()));
 //BA.debugLineNum = 442;BA.debugLine="Starter.selectedLocationDescription = edtFloatDe";
parent.mostCurrent._vvvvvvvv0._vvvv1 /*String*/  = parent.mostCurrent._edtfloatdescription._getvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv0 /*String*/ ();
 //BA.debugLineNum = 443;BA.debugLine="code = edtFloatCode.Text";
_code = parent.mostCurrent._edtfloatcode._getvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv0 /*String*/ ();
 //BA.debugLineNum = 444;BA.debugLine="If edtFloatDescription.Text = \"\" Then edtFloatDe";
if (true) break;

case 10:
//if
this.state = 15;
if ((parent.mostCurrent._edtfloatdescription._getvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv0 /*String*/ ()).equals("")) { 
this.state = 12;
;}if (true) break;

case 12:
//C
this.state = 15;
parent.mostCurrent._edtfloatdescription._setvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv0 /*String*/ ("Nieuwe locatie");
if (true) break;

case 15:
//C
this.state = 16;
;
 //BA.debugLineNum = 445;BA.debugLine="description = edtFloatDescription.Text";
_description = parent.mostCurrent._edtfloatdescription._getvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv0 /*String*/ ();
 //BA.debugLineNum = 448;BA.debugLine="mqttBaseSubBord.Disconnect";
parent._vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv3._vvvvvvvvv3 /*String*/ ();
 //BA.debugLineNum = 449;BA.debugLine="pnlLocationCOde.Visible = False";
parent.mostCurrent._pnllocationcode.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 450;BA.debugLine="baseFile.SetBase(code, description, \"1\")";
parent._v5._vvvvvv0 /*String*/ (_code,_description,"1");
 //BA.debugLineNum = 451;BA.debugLine="InitConnection";
_initconnection();
 //BA.debugLineNum = 452;BA.debugLine="ime.HideKeyboard";
_ime.HideKeyboard(mostCurrent.activityBA);
 if (true) break;

case 16:
//C
this.state = -1;
;
 //BA.debugLineNum = 454;BA.debugLine="End Sub";
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
 //BA.debugLineNum = 307;BA.debugLine="Sub CheckBordExists(bord As Message)";
 //BA.debugLineNum = 308;BA.debugLine="Dim bordExists As Boolean = False";
_bordexists = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 309;BA.debugLine="Dim strPlayers As String";
_strplayers = "";
 //BA.debugLineNum = 312;BA.debugLine="If bord.Body.Length = 0 Then Return";
if (_bord.Body /*String*/ .length()==0) { 
if (true) return "";};
 //BA.debugLineNum = 313;BA.debugLine="Dim name As String = bord.Body";
_name = _bord.Body /*String*/ ;
 //BA.debugLineNum = 317;BA.debugLine="strPlayers = name'baseFile.SetPlayertext(name)";
_strplayers = _name;
 //BA.debugLineNum = 318;BA.debugLine="If name.IndexOf(\"|\") > -1 Then";
if (_name.indexOf("|")>-1) { 
 //BA.debugLineNum = 319;BA.debugLine="Dim strData() As String = Regex.Split(\"\\|\", name";
_strdata = anywheresoftware.b4a.keywords.Common.Regex.Split("\\|",_name);
 //BA.debugLineNum = 320;BA.debugLine="name = strData(0)";
_name = _strdata[(int) (0)];
 };
 //BA.debugLineNum = 324;BA.debugLine="connectTime = -1";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv5 = (long) (-1);
 //BA.debugLineNum = 325;BA.debugLine="If Starter.serverList.Size = 0 Then";
if (mostCurrent._vvvvvvvv0._vv7 /*anywheresoftware.b4a.objects.collections.List*/ .getSize()==0) { 
 //BA.debugLineNum = 326;BA.debugLine="AddUnkownIp(\"\", name, strPlayers)";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv3("",_name,_strplayers);
 //BA.debugLineNum = 327;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 330;BA.debugLine="For Each lst As bordStatus In Starter.serverList";
{
final anywheresoftware.b4a.BA.IterableList group15 = mostCurrent._vvvvvvvv0._vv7 /*anywheresoftware.b4a.objects.collections.List*/ ;
final int groupLen15 = group15.getSize()
;int index15 = 0;
;
for (; index15 < groupLen15;index15++){
_lst = (nl.pdeg.bordondroid.main._bordstatus)(group15.Get(index15));
 //BA.debugLineNum = 335;BA.debugLine="If lst.name = name Then";
if ((_lst.name /*String*/ ).equals(_name)) { 
 //BA.debugLineNum = 336;BA.debugLine="bordExists = True";
_bordexists = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 338;BA.debugLine="lst.timeStamp = DateTime.Now";
_lst.timeStamp /*long*/  = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 339;BA.debugLine="lst.alive = True";
_lst.alive /*boolean*/  = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 341;BA.debugLine="baseFile.CheckPlayers(strPlayers, name, clvServ";
_v5._vvvvv4 /*String*/ (_strplayers,_name,mostCurrent._clvserver);
 };
 }
};
 //BA.debugLineNum = 345;BA.debugLine="If Not(bordExists) Then";
if (anywheresoftware.b4a.keywords.Common.Not(_bordexists)) { 
 //BA.debugLineNum = 346;BA.debugLine="AddUnkownIp(\"\", name, strPlayers)";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv3("",_name,_strplayers);
 //BA.debugLineNum = 347;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 349;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv0() throws Exception{
nl.pdeg.bordondroid.main._bordstatus _bs = null;
 //BA.debugLineNum = 86;BA.debugLine="Private Sub CheckBordInServerlist";
 //BA.debugLineNum = 87;BA.debugLine="If Starter.serverList.Size > 0 Then";
if (mostCurrent._vvvvvvvv0._vv7 /*anywheresoftware.b4a.objects.collections.List*/ .getSize()>0) { 
 //BA.debugLineNum = 88;BA.debugLine="clvServer.Clear";
mostCurrent._clvserver._clear();
 //BA.debugLineNum = 89;BA.debugLine="For Each bs As bordStatus In Starter.serverList";
{
final anywheresoftware.b4a.BA.IterableList group3 = mostCurrent._vvvvvvvv0._vv7 /*anywheresoftware.b4a.objects.collections.List*/ ;
final int groupLen3 = group3.getSize()
;int index3 = 0;
;
for (; index3 < groupLen3;index3++){
_bs = (nl.pdeg.bordondroid.main._bordstatus)(group3.Get(index3));
 //BA.debugLineNum = 90;BA.debugLine="clvServer.Add(genUnitList(bs.name, bs.alive, bs";
mostCurrent._clvserver._add((anywheresoftware.b4a.objects.B4XViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.B4XViewWrapper(), (java.lang.Object)(_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv6(_bs.name /*String*/ ,_bs.alive /*boolean*/ ,_bs.players /*String*/ ).getObject())),(Object)(""));
 }
};
 };
 //BA.debugLineNum = 94;BA.debugLine="End Sub";
return "";
}
public static boolean  _vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv7() throws Exception{
 //BA.debugLineNum = 472;BA.debugLine="Private Sub CheckClientConnected As Boolean";
 //BA.debugLineNum = 473;BA.debugLine="Return CallSub(mqttBaseSubBord, \"GetClientConnect";
if (true) return BA.ObjectToBoolean(anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)(_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv3),"GetClientConnected"));
 //BA.debugLineNum = 474;BA.debugLine="End Sub";
return false;
}
public static boolean  _vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv0() throws Exception{
 //BA.debugLineNum = 212;BA.debugLine="Sub checkConnectTime As Boolean";
 //BA.debugLineNum = 213;BA.debugLine="Return True";
if (true) return anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 214;BA.debugLine="If connectTime <> -1 Then";
if (_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv5!=-1) { 
 //BA.debugLineNum = 215;BA.debugLine="Log(\"KWDKWMKDM\")";
anywheresoftware.b4a.keywords.Common.LogImpl("851971","KWDKWMKDM",0);
 //BA.debugLineNum = 216;BA.debugLine="pnlNobords.Visible = True";
mostCurrent._pnlnobords.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 217;BA.debugLine="mqttBaseSubBord.Disconnect";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv3._vvvvvvvvv3 /*String*/ ();
 //BA.debugLineNum = 218;BA.debugLine="tmrBordLastAlive.Enabled = False";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv4.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 219;BA.debugLine="pnlNobords.Visible = False";
mostCurrent._pnlnobords.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 220;BA.debugLine="Msgbox2Async(\"Geen borden gevonden\", Application";
anywheresoftware.b4a.keywords.Common.Msgbox2Async(BA.ObjectToCharSequence("Geen borden gevonden"),BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Application.getLabelName()),"OKE","","",anywheresoftware.b4a.keywords.Common.Application.getIcon(),processBA,anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 221;BA.debugLine="Return False";
if (true) return anywheresoftware.b4a.keywords.Common.False;
 };
 //BA.debugLineNum = 223;BA.debugLine="Return True";
if (true) return anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 224;BA.debugLine="End Sub";
return false;
}
public static String  _vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv4(nl.pdeg.bordondroid.main._bordstatus _bord,String _players) throws Exception{
anywheresoftware.b4a.objects.PanelWrapper _p = null;
int _i = 0;
 //BA.debugLineNum = 375;BA.debugLine="Sub CheckIpInClv(bord As bordStatus, players As St";
 //BA.debugLineNum = 376;BA.debugLine="Dim p As Panel";
_p = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 379;BA.debugLine="For i = 0 To clvServer.GetSize-1";
{
final int step2 = 1;
final int limit2 = (int) (mostCurrent._clvserver._getsize()-1);
_i = (int) (0) ;
for (;_i <= limit2 ;_i = _i + step2 ) {
 //BA.debugLineNum = 380;BA.debugLine="p = clvServer.GetPanel(i)";
_p.setObject((android.view.ViewGroup)(mostCurrent._clvserver._getpanel(_i).getObject()));
 //BA.debugLineNum = 381;BA.debugLine="If p.Tag = bord.name Then";
if ((_p.getTag()).equals((Object)(_bord.name /*String*/ ))) { 
 //BA.debugLineNum = 383;BA.debugLine="baseFile.SetBordDiedByName(bord.name, clvServer";
_v5._vvvvvvv1 /*String*/ (_bord.name /*String*/ ,mostCurrent._clvserver,_bord.alive /*boolean*/ );
 };
 }
};
 //BA.debugLineNum = 388;BA.debugLine="pnlNobords.SetVisibleAnimated(1000, False)";
mostCurrent._pnlnobords.SetVisibleAnimated((int) (1000),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 389;BA.debugLine="clvServer.Add(genUnitList(bord.name, bord.alive,";
mostCurrent._clvserver._add((anywheresoftware.b4a.objects.B4XViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.B4XViewWrapper(), (java.lang.Object)(_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv6(_bord.name /*String*/ ,_bord.alive /*boolean*/ ,_players).getObject())),(Object)(""));
 //BA.debugLineNum = 390;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv1() throws Exception{
long _currtime = 0L;
nl.pdeg.bordondroid.main._bordstatus _bd = null;
long _timediff = 0L;
 //BA.debugLineNum = 226;BA.debugLine="Sub CheckLastAliveTime";
 //BA.debugLineNum = 229;BA.debugLine="If clvServer.Size = 0 Then Return";
if (mostCurrent._clvserver._getsize()==0) { 
if (true) return "";};
 //BA.debugLineNum = 230;BA.debugLine="Dim currTime As Long = DateTime.Now";
_currtime = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 235;BA.debugLine="If lblLastCheck.IsInitialized = False Then";
if (mostCurrent._lbllastcheck.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 236;BA.debugLine="lblNoBord.Visible = False";
mostCurrent._lblnobord.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 237;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 239;BA.debugLine="lblLastCheck.Text = $\"Laatste controle $Time{Date";
mostCurrent._lbllastcheck.setText(BA.ObjectToCharSequence(("Laatste controle "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("time",(Object)(anywheresoftware.b4a.keywords.Common.DateTime.getNow()))+"")));
 //BA.debugLineNum = 242;BA.debugLine="For Each bd As bordStatus In Starter.serverList";
{
final anywheresoftware.b4a.BA.IterableList group8 = mostCurrent._vvvvvvvv0._vv7 /*anywheresoftware.b4a.objects.collections.List*/ ;
final int groupLen8 = group8.getSize()
;int index8 = 0;
;
for (; index8 < groupLen8;index8++){
_bd = (nl.pdeg.bordondroid.main._bordstatus)(group8.Get(index8));
 //BA.debugLineNum = 243;BA.debugLine="Dim timeDiff As Long = currTime-bd.timeStamp";
_timediff = (long) (_currtime-_bd.timeStamp /*long*/ );
 //BA.debugLineNum = 244;BA.debugLine="If timeDiff >= Starter.serverDied Then";
if (_timediff>=mostCurrent._vvvvvvvv0._vv0 /*long*/ ) { 
 //BA.debugLineNum = 245;BA.debugLine="bd.alive = False";
_bd.alive /*boolean*/  = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 246;BA.debugLine="baseFile.SetBordDiedByName(bd.name, clvServer,";
_v5._vvvvvvv1 /*String*/ (_bd.name /*String*/ ,mostCurrent._clvserver,_bd.alive /*boolean*/ );
 }else {
 //BA.debugLineNum = 248;BA.debugLine="baseFile.SetBordDiedByName(bd.name, clvServer,";
_v5._vvvvvvv1 /*String*/ (_bd.name /*String*/ ,mostCurrent._clvserver,_bd.alive /*boolean*/ );
 };
 }
};
 //BA.debugLineNum = 252;BA.debugLine="End Sub";
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
 //BA.debugLineNum = 189;BA.debugLine="Dim p As Panel = clvServer.GetPanel(Index)";
_p = new anywheresoftware.b4a.objects.PanelWrapper();
_p.setObject((android.view.ViewGroup)(parent.mostCurrent._clvserver._getpanel(_index).getObject()));
 //BA.debugLineNum = 190;BA.debugLine="Dim unit As String";
_unit = "";
 //BA.debugLineNum = 192;BA.debugLine="unit = baseFile.GetSelectedLabelTagFromPanel(p, \"";
_unit = parent._v5._vvvvvv3 /*String*/ (_p,"name");
 //BA.debugLineNum = 193;BA.debugLine="If baseFile.GetBordAlive(unit) = False Then";
if (true) break;

case 1:
//if
this.state = 4;
if (parent._v5._vvvvv7 /*boolean*/ (_unit)==anywheresoftware.b4a.keywords.Common.False) { 
this.state = 3;
}if (true) break;

case 3:
//C
this.state = 4;
 //BA.debugLineNum = 194;BA.debugLine="baseFile.ShowCustomToast($\"Bord niet gevonden, c";
parent._v5._vvvvvvv7 /*String*/ ((Object)(("Bord niet gevonden, controleer of het bord \"online\" is")),anywheresoftware.b4a.keywords.Common.False,anywheresoftware.b4a.keywords.Common.Colors.Red);
 //BA.debugLineNum = 195;BA.debugLine="Return";
if (true) return ;
 if (true) break;

case 4:
//C
this.state = -1;
;
 //BA.debugLineNum = 198;BA.debugLine="Starter.mainPaused = True";
parent.mostCurrent._vvvvvvvv0._vvvv6 /*boolean*/  = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 199;BA.debugLine="Starter.DiscoveredServer = unit";
parent.mostCurrent._vvvvvvvv0._vv6 /*String*/  = _unit;
 //BA.debugLineNum = 200;BA.debugLine="CallSub2(Starter, \"SetLastWill\", \"recvdied\")";
anywheresoftware.b4a.keywords.Common.CallSubNew2(processBA,(Object)(parent.mostCurrent._vvvvvvvv0.getObject()),"SetLastWill",(Object)("recvdied"));
 //BA.debugLineNum = 201;BA.debugLine="CallSub2(Starter, \"SetUnit\", PrepTopicName(unit).";
anywheresoftware.b4a.keywords.Common.CallSubNew2(processBA,(Object)(parent.mostCurrent._vvvvvvvv0.getObject()),"SetUnit",(Object)(_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv2(_unit).toLowerCase()));
 //BA.debugLineNum = 202;BA.debugLine="DisconnectMqtt";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv5();
 //BA.debugLineNum = 203;BA.debugLine="Sleep(100)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,this,(int) (100));
this.state = 5;
return;
case 5:
//C
this.state = -1;
;
 //BA.debugLineNum = 204;BA.debugLine="StartActivity(ServerBoard)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(parent.mostCurrent._vvvvvvvv7.getObject()));
 //BA.debugLineNum = 205;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static String  _connectionerror() throws Exception{
 //BA.debugLineNum = 502;BA.debugLine="Sub ConnectionError";
 //BA.debugLineNum = 505;BA.debugLine="End Sub";
return "";
}
public static String  _deletedlocationactive() throws Exception{
 //BA.debugLineNum = 492;BA.debugLine="Sub DeletedLocationActive";
 //BA.debugLineNum = 493;BA.debugLine="mqttBaseSubBord.Disconnect";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv3._vvvvvvvvv3 /*String*/ ();
 //BA.debugLineNum = 494;BA.debugLine="clvServer.Clear";
mostCurrent._clvserver._clear();
 //BA.debugLineNum = 495;BA.debugLine="getBaseList";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv2();
 //BA.debugLineNum = 496;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv5() throws Exception{
 //BA.debugLineNum = 276;BA.debugLine="Sub DisconnectMqtt";
 //BA.debugLineNum = 277;BA.debugLine="If mqttBaseSubBord.connected Then";
if (_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv3._vv5 /*boolean*/ ) { 
 //BA.debugLineNum = 278;BA.debugLine="mqttBaseSubBord.Disconnect";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv3._vvvvvvvvv3 /*String*/ ();
 };
 //BA.debugLineNum = 280;BA.debugLine="If CheckClientConnected Then";
if (_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv7()) { 
 //BA.debugLineNum = 281;BA.debugLine="mqttBaseSubBord.Disconnect";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv3._vvvvvvvvv3 /*String*/ ();
 };
 //BA.debugLineNum = 284;BA.debugLine="End Sub";
return "";
}
public static String  _edtfloatcode_textchanged(String _old,String _new) throws Exception{
 //BA.debugLineNum = 484;BA.debugLine="Sub edtFloatCode_TextChanged (Old As String, New A";
 //BA.debugLineNum = 485;BA.debugLine="If New.Length > 0 Then";
if (_new.length()>0) { 
 //BA.debugLineNum = 486;BA.debugLine="btnOk.Enabled = True";
mostCurrent._btnok.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 488;BA.debugLine="btnOk.Enabled = False";
mostCurrent._btnok.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 490;BA.debugLine="End Sub";
return "";
}
public static String  _edtfloatdescription_textchanged(String _old,String _new) throws Exception{
 //BA.debugLineNum = 480;BA.debugLine="Sub edtFloatDescription_TextChanged (Old As String";
 //BA.debugLineNum = 482;BA.debugLine="End Sub";
return "";
}
public static String  _edtlocationcode_textchanged(String _old,String _new) throws Exception{
 //BA.debugLineNum = 456;BA.debugLine="Sub edtLocationCode_TextChanged (Old As String, Ne";
 //BA.debugLineNum = 457;BA.debugLine="If New.Length > 0 Then";
if (_new.length()>0) { 
 //BA.debugLineNum = 458;BA.debugLine="btnOk.Enabled = True";
mostCurrent._btnok.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 460;BA.debugLine="btnOk.Enabled = False";
mostCurrent._btnok.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 462;BA.debugLine="End Sub";
return "";
}
public static anywheresoftware.b4a.objects.PanelWrapper  _vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv6(String _name,boolean _alive,String _players) throws Exception{
anywheresoftware.b4a.objects.PanelWrapper _p = null;
 //BA.debugLineNum = 286;BA.debugLine="Sub genUnitList(name As String, alive As Boolean,";
 //BA.debugLineNum = 287;BA.debugLine="Dim p As Panel";
_p = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 288;BA.debugLine="p.Initialize(Me)";
_p.Initialize(mostCurrent.activityBA,BA.ObjectToString(main.getObject()));
 //BA.debugLineNum = 289;BA.debugLine="p.SetLayout(0dip, 0dip, clvServer.AsView.Width, 1";
_p.SetLayout(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)),mostCurrent._clvserver._asview().getWidth(),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (125)));
 //BA.debugLineNum = 290;BA.debugLine="p.LoadLayout(\"clvBorden\")";
_p.LoadLayout("clvBorden",mostCurrent.activityBA);
 //BA.debugLineNum = 291;BA.debugLine="p.Tag = name";
_p.setTag((Object)(_name));
 //BA.debugLineNum = 293;BA.debugLine="lblBordName.Text = name.Trim";
mostCurrent._lblbordname.setText(BA.ObjectToCharSequence(_name.trim()));
 //BA.debugLineNum = 294;BA.debugLine="lblLastCheck.Text = $\"Laatste controle $Time{Date";
mostCurrent._lbllastcheck.setText(BA.ObjectToCharSequence(("Laatste controle "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("time",(Object)(anywheresoftware.b4a.keywords.Common.DateTime.getNow()))+"")));
 //BA.debugLineNum = 296;BA.debugLine="lblPLayer.Text = baseFile.SetPlayertext(players)";
mostCurrent._lblplayer.setText(BA.ObjectToCharSequence(_v5._vvvvvvv5 /*Object*/ (_players)));
 //BA.debugLineNum = 298;BA.debugLine="lblPLayer.Tag = \"players\"";
mostCurrent._lblplayer.setTag((Object)("players"));
 //BA.debugLineNum = 300;BA.debugLine="If Not(alive) Then";
if (anywheresoftware.b4a.keywords.Common.Not(_alive)) { 
 //BA.debugLineNum = 301;BA.debugLine="lblBordName.Color = Colors.Red";
mostCurrent._lblbordname.setColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 //BA.debugLineNum = 302;BA.debugLine="lblBordName.TextColor = Colors.White";
mostCurrent._lblbordname.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 };
 //BA.debugLineNum = 304;BA.debugLine="Return p";
if (true) return _p;
 //BA.debugLineNum = 305;BA.debugLine="End Sub";
return null;
}
public static void  _vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv2() throws Exception{
ResumableSub_getBaseList rsub = new ResumableSub_getBaseList(null);
rsub.resume(processBA, null);
}
public static class ResumableSub_getBaseList extends BA.ResumableSub {
public ResumableSub_getBaseList(nl.pdeg.bordondroid.main parent) {
this.parent = parent;
}
nl.pdeg.bordondroid.main parent;
int _listsize = 0;
nl.pdeg.bordondroid.main._locationbord _loc = null;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
 //BA.debugLineNum = 124;BA.debugLine="Dim listSize As Int = 0";
_listsize = (int) (0);
 //BA.debugLineNum = 126;BA.debugLine="baseList.Initialize";
parent._vvvvvvvv1.Initialize();
 //BA.debugLineNum = 127;BA.debugLine="baseList = baseFile.GetBase";
parent._vvvvvvvv1 = parent._v5._vvvvv1 /*anywheresoftware.b4a.objects.collections.List*/ ();
 //BA.debugLineNum = 129;BA.debugLine="If CallSub(baseFile, \"CheckBaseListExists\") = Fal";
if (true) break;

case 1:
//if
this.state = 4;
if ((anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)(parent._v5),"CheckBaseListExists")).equals((Object)(anywheresoftware.b4a.keywords.Common.False))) { 
this.state = 3;
}if (true) break;

case 3:
//C
this.state = 4;
 //BA.debugLineNum = 130;BA.debugLine="pnlLocationCOde.Visible = True";
parent.mostCurrent._pnllocationcode.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 131;BA.debugLine="Return";
if (true) return ;
 if (true) break;
;
 //BA.debugLineNum = 134;BA.debugLine="If baseList.IsInitialized Then";

case 4:
//if
this.state = 7;
if (parent._vvvvvvvv1.IsInitialized()) { 
this.state = 6;
}if (true) break;

case 6:
//C
this.state = 7;
 //BA.debugLineNum = 135;BA.debugLine="listSize = baseList.Size";
_listsize = parent._vvvvvvvv1.getSize();
 if (true) break;
;
 //BA.debugLineNum = 137;BA.debugLine="If listSize > 1 Then";

case 7:
//if
this.state = 12;
if (_listsize>1) { 
this.state = 9;
}else {
this.state = 11;
}if (true) break;

case 9:
//C
this.state = 12;
 //BA.debugLineNum = 139;BA.debugLine="pnlLocationList.SetVisibleAnimated(500, True)";
parent.mostCurrent._pnllocationlist.SetVisibleAnimated((int) (500),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 140;BA.debugLine="Sleep(200)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,this,(int) (200));
this.state = 21;
return;
case 21:
//C
this.state = 12;
;
 if (true) break;

case 11:
//C
this.state = 12;
 //BA.debugLineNum = 142;BA.debugLine="pnlLocationList.SetVisibleAnimated(500, False)";
parent.mostCurrent._pnllocationlist.SetVisibleAnimated((int) (500),anywheresoftware.b4a.keywords.Common.False);
 if (true) break;

case 12:
//C
this.state = 13;
;
 //BA.debugLineNum = 146;BA.debugLine="lblNoBord.Text = \"Wachten op borden...\"";
parent.mostCurrent._lblnobord.setText(BA.ObjectToCharSequence("Wachten op borden..."));
 //BA.debugLineNum = 148;BA.debugLine="If baseList.IsInitialized = False Then";
if (true) break;

case 13:
//if
this.state = 20;
if (parent._vvvvvvvv1.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
this.state = 15;
}else if(parent._vvvvvvvv1.getSize()==1) { 
this.state = 17;
}else {
this.state = 19;
}if (true) break;

case 15:
//C
this.state = 20;
 //BA.debugLineNum = 149;BA.debugLine="pnlLocationCOde.Visible = True";
parent.mostCurrent._pnllocationcode.setVisible(anywheresoftware.b4a.keywords.Common.True);
 if (true) break;

case 17:
//C
this.state = 20;
 //BA.debugLineNum = 151;BA.debugLine="Dim loc As locationBord";
_loc = new nl.pdeg.bordondroid.main._locationbord();
 //BA.debugLineNum = 152;BA.debugLine="loc.Initialize";
_loc.Initialize();
 //BA.debugLineNum = 153;BA.debugLine="loc = baseList.Get(0)";
_loc = (nl.pdeg.bordondroid.main._locationbord)(parent._vvvvvvvv1.Get((int) (0)));
 //BA.debugLineNum = 154;BA.debugLine="Starter.selectedLocationCode = loc.code";
parent.mostCurrent._vvvvvvvv0._vvv0 /*String*/  = _loc.code /*String*/ ;
 //BA.debugLineNum = 155;BA.debugLine="Starter.selectedLocationDescription = loc.descri";
parent.mostCurrent._vvvvvvvv0._vvvv1 /*String*/  = _loc.description /*String*/ ;
 //BA.debugLineNum = 156;BA.debugLine="lblCurrLocation.Text = loc.description";
parent.mostCurrent._lblcurrlocation.setText(BA.ObjectToCharSequence(_loc.description /*String*/ ));
 //BA.debugLineNum = 157;BA.debugLine="InitConnection";
_initconnection();
 if (true) break;

case 19:
//C
this.state = 20;
 //BA.debugLineNum = 159;BA.debugLine="StartActivity(SelectLocation)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(parent.mostCurrent._vvvvvvvv6.getObject()));
 if (true) break;

case 20:
//C
this.state = -1;
;
 //BA.debugLineNum = 161;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 28;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 29;BA.debugLine="Private lblBordName As Label";
mostCurrent._lblbordname = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 30;BA.debugLine="Private pnlBord As Panel";
mostCurrent._pnlbord = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 31;BA.debugLine="Private lblLastCheck As Label";
mostCurrent._lbllastcheck = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 32;BA.debugLine="Private pnlNobords As Panel";
mostCurrent._pnlnobords = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 33;BA.debugLine="Private lblVersion As Label";
mostCurrent._lblversion = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 34;BA.debugLine="Private pnlLocationCOde As Panel";
mostCurrent._pnllocationcode = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 35;BA.debugLine="Private btnCancel As Button";
mostCurrent._btncancel = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 36;BA.debugLine="Private btnOk As Button";
mostCurrent._btnok = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 37;BA.debugLine="Private pnlLocation As Panel";
mostCurrent._pnllocation = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 38;BA.debugLine="Private clvServer As CustomListView";
mostCurrent._clvserver = new b4a.example3.customlistview();
 //BA.debugLineNum = 39;BA.debugLine="Private lblCurrLocation As Label";
mostCurrent._lblcurrlocation = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 40;BA.debugLine="Private lblNoBord As Label";
mostCurrent._lblnobord = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 41;BA.debugLine="Private pnlLocationList As Panel";
mostCurrent._pnllocationlist = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 42;BA.debugLine="Private edtFloatCode As B4XFloatTextField";
mostCurrent._edtfloatcode = new nl.pdeg.bordondroid.b4xfloattextfield();
 //BA.debugLineNum = 43;BA.debugLine="Private edtFloatDescription As B4XFloatTextField";
mostCurrent._edtfloatdescription = new nl.pdeg.bordondroid.b4xfloattextfield();
 //BA.debugLineNum = 44;BA.debugLine="Private B4XLoadingIndicator1 As B4XLoadingIndicat";
mostCurrent._b4xloadingindicator1 = new nl.pdeg.bordondroid.b4xloadingindicator();
 //BA.debugLineNum = 45;BA.debugLine="Private B4XLoadingIndicator2 As B4XLoadingIndicat";
mostCurrent._b4xloadingindicator2 = new nl.pdeg.bordondroid.b4xloadingindicator();
 //BA.debugLineNum = 46;BA.debugLine="Private connectTime As Long";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv5 = 0L;
 //BA.debugLineNum = 47;BA.debugLine="Private lblPLayer As Label";
mostCurrent._lblplayer = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 48;BA.debugLine="End Sub";
return "";
}
public static String  _initconnection() throws Exception{
 //BA.debugLineNum = 174;BA.debugLine="Sub InitConnection";
 //BA.debugLineNum = 175;BA.debugLine="If mqttBaseSubBord.connected Then mqttBaseSubBord";
if (_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv3._vv5 /*boolean*/ ) { 
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv3._vvvvvvvvv3 /*String*/ ();};
 //BA.debugLineNum = 178;BA.debugLine="lblCurrLocation.Text = Starter.selectedLocationDe";
mostCurrent._lblcurrlocation.setText(BA.ObjectToCharSequence(mostCurrent._vvvvvvvv0._vvvv1 /*String*/ ));
 //BA.debugLineNum = 179;BA.debugLine="CallSub2(Starter, \"SetLastWill\", \"bordpubdied\")";
anywheresoftware.b4a.keywords.Common.CallSubNew2(processBA,(Object)(mostCurrent._vvvvvvvv0.getObject()),"SetLastWill",(Object)("bordpubdied"));
 //BA.debugLineNum = 180;BA.debugLine="CallSub2(Starter, \"SetSubBase\", Starter.selectedL";
anywheresoftware.b4a.keywords.Common.CallSubNew2(processBA,(Object)(mostCurrent._vvvvvvvv0.getObject()),"SetSubBase",(Object)(mostCurrent._vvvvvvvv0._vvv0 /*String*/ ));
 //BA.debugLineNum = 181;BA.debugLine="CallSub2(Starter, \"SetSubString2\", \"/pubbord\")";
anywheresoftware.b4a.keywords.Common.CallSubNew2(processBA,(Object)(mostCurrent._vvvvvvvv0.getObject()),"SetSubString2",(Object)("/pubbord"));
 //BA.debugLineNum = 182;BA.debugLine="CallSub2(Starter, \"SetUnsubscribeString2\", \"\")";
anywheresoftware.b4a.keywords.Common.CallSubNew2(processBA,(Object)(mostCurrent._vvvvvvvv0.getObject()),"SetUnsubscribeString2",(Object)(""));
 //BA.debugLineNum = 184;BA.debugLine="StartConnection";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv3();
 //BA.debugLineNum = 185;BA.debugLine="End Sub";
return "";
}
public static String  _lblviewbord_click() throws Exception{
anywheresoftware.b4a.objects.ConcreteViewWrapper _v = null;
anywheresoftware.b4a.objects.PanelWrapper _p = null;
 //BA.debugLineNum = 392;BA.debugLine="Sub lblViewBord_Click";
 //BA.debugLineNum = 393;BA.debugLine="Dim v As View = Sender";
_v = new anywheresoftware.b4a.objects.ConcreteViewWrapper();
_v.setObject((android.view.View)(anywheresoftware.b4a.keywords.Common.Sender(mostCurrent.activityBA)));
 //BA.debugLineNum = 394;BA.debugLine="Dim p As Panel = v.Parent";
_p = new anywheresoftware.b4a.objects.PanelWrapper();
_p.setObject((android.view.ViewGroup)(_v.getParent()));
 //BA.debugLineNum = 396;BA.debugLine="clvServer_ItemClick (clvServer.GetItemFromView(p)";
_clvserver_itemclick(mostCurrent._clvserver._getitemfromview((anywheresoftware.b4a.objects.B4XViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.B4XViewWrapper(), (java.lang.Object)(_p.getObject()))),anywheresoftware.b4a.keywords.Common.Null);
 //BA.debugLineNum = 397;BA.debugLine="End Sub";
return "";
}
public static String  _pnllocation_click() throws Exception{
 //BA.debugLineNum = 464;BA.debugLine="Sub pnlLocation_Click";
 //BA.debugLineNum = 466;BA.debugLine="If CheckClientConnected Then";
if (_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv7()) { 
 //BA.debugLineNum = 467;BA.debugLine="mqttBaseSubBord.Disconnect";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv3._vvvvvvvvv3 /*String*/ ();
 };
 //BA.debugLineNum = 469;BA.debugLine="StartActivity(locations)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(mostCurrent._vvvvvvvv5.getObject()));
 //BA.debugLineNum = 470;BA.debugLine="End Sub";
return "";
}
public static String  _pnllocationlist_click() throws Exception{
 //BA.debugLineNum = 476;BA.debugLine="Sub pnlLocationList_Click";
 //BA.debugLineNum = 477;BA.debugLine="StartActivity(SelectLocation)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(mostCurrent._vvvvvvvv6.getObject()));
 //BA.debugLineNum = 478;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv2(String _bord) throws Exception{
 //BA.debugLineNum = 399;BA.debugLine="Private Sub PrepTopicName(bord As String) As Strin";
 //BA.debugLineNum = 400;BA.debugLine="Return bord.Replace(\" \", \"\")";
if (true) return _bord.replace(" ","");
 //BA.debugLineNum = 401;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        b4a.example.dateutils._process_globals();
main._process_globals();
locations._process_globals();
selectlocation._process_globals();
serverboard._process_globals();
starter._process_globals();
		
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
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv4 = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 23;BA.debugLine="Private baseFile As Base";
_v5 = new nl.pdeg.bordondroid.base();
 //BA.debugLineNum = 24;BA.debugLine="Private baseList As List";
_vvvvvvvv1 = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 25;BA.debugLine="Private mqttBaseSubBord As MqttConnector";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv3 = new nl.pdeg.bordondroid.mqttconnector();
 //BA.debugLineNum = 26;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv1() throws Exception{
 //BA.debugLineNum = 163;BA.debugLine="Sub ReconnectToLocation";
 //BA.debugLineNum = 164;BA.debugLine="mqttBaseSubBord.Initialize";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv3._initialize /*String*/ (processBA);
 //BA.debugLineNum = 165;BA.debugLine="CallSub2(Starter, \"SetLastWill\", \"bordpubdied\")";
anywheresoftware.b4a.keywords.Common.CallSubNew2(processBA,(Object)(mostCurrent._vvvvvvvv0.getObject()),"SetLastWill",(Object)("bordpubdied"));
 //BA.debugLineNum = 166;BA.debugLine="CallSub2(Starter, \"SetSubBase\", Starter.selectedL";
anywheresoftware.b4a.keywords.Common.CallSubNew2(processBA,(Object)(mostCurrent._vvvvvvvv0.getObject()),"SetSubBase",(Object)(mostCurrent._vvvvvvvv0._vvv0 /*String*/ ));
 //BA.debugLineNum = 167;BA.debugLine="CallSub2(Starter, \"SetSubString2\", \"/pubbord\")";
anywheresoftware.b4a.keywords.Common.CallSubNew2(processBA,(Object)(mostCurrent._vvvvvvvv0.getObject()),"SetSubString2",(Object)("/pubbord"));
 //BA.debugLineNum = 168;BA.debugLine="CallSub2(Starter, \"SetUnsubscribeString2\", \"\")";
anywheresoftware.b4a.keywords.Common.CallSubNew2(processBA,(Object)(mostCurrent._vvvvvvvv0.getObject()),"SetUnsubscribeString2",(Object)(""));
 //BA.debugLineNum = 169;BA.debugLine="mqttBaseSubBord.Connect";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv3._vvvvvvvvv1 /*String*/ ();
 //BA.debugLineNum = 170;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv6(boolean _resume) throws Exception{
 //BA.debugLineNum = 260;BA.debugLine="Sub ResumeConnection(resume As Boolean)";
 //BA.debugLineNum = 261;BA.debugLine="tmrBordLastAlive.Enabled = resume";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv4.setEnabled(_resume);
 //BA.debugLineNum = 262;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv4() throws Exception{
 //BA.debugLineNum = 96;BA.debugLine="Sub setBordLastAliveTimer";
 //BA.debugLineNum = 97;BA.debugLine="tmrBordLastAlive.Enabled = True";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv4.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 99;BA.debugLine="End Sub";
return "";
}
public static String  _shownotconnectedtobroker() throws Exception{
 //BA.debugLineNum = 254;BA.debugLine="Sub ShowNotConnectedToBroker";
 //BA.debugLineNum = 255;BA.debugLine="lblNoBord.Text = \"Verbinding borden verbroken\"";
mostCurrent._lblnobord.setText(BA.ObjectToCharSequence("Verbinding borden verbroken"));
 //BA.debugLineNum = 256;BA.debugLine="pnlNobords.SetVisibleAnimated(1000, True)";
mostCurrent._pnlnobords.SetVisibleAnimated((int) (1000),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 257;BA.debugLine="mqttBaseSubBord.Disconnect";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv3._vvvvvvvvv3 /*String*/ ();
 //BA.debugLineNum = 258;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv7() throws Exception{
 //BA.debugLineNum = 498;BA.debugLine="Sub ShowSelectLocationButton";
 //BA.debugLineNum = 499;BA.debugLine="pnlLocationList.Visible = baseFile.GetBase.Size >";
mostCurrent._pnllocationlist.setVisible(_v5._vvvvv1 /*anywheresoftware.b4a.objects.collections.List*/ ().getSize()>1);
 //BA.debugLineNum = 500;BA.debugLine="End Sub";
return "";
}
public static void  _vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv3() throws Exception{
ResumableSub_StartConnection rsub = new ResumableSub_StartConnection(null);
rsub.resume(processBA, null);
}
public static class ResumableSub_StartConnection extends BA.ResumableSub {
public ResumableSub_StartConnection(nl.pdeg.bordondroid.main parent) {
this.parent = parent;
}
nl.pdeg.bordondroid.main parent;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = -1;
 //BA.debugLineNum = 102;BA.debugLine="pnlLocationCOde.Visible = False";
parent.mostCurrent._pnllocationcode.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 103;BA.debugLine="B4XLoadingIndicator1.Show";
parent.mostCurrent._b4xloadingindicator1._show /*String*/ ();
 //BA.debugLineNum = 104;BA.debugLine="B4XLoadingIndicator2.Show";
parent.mostCurrent._b4xloadingindicator2._show /*String*/ ();
 //BA.debugLineNum = 105;BA.debugLine="Sleep(0)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,this,(int) (0));
this.state = 1;
return;
case 1:
//C
this.state = -1;
;
 //BA.debugLineNum = 106;BA.debugLine="pnlNobords.Visible = True";
parent.mostCurrent._pnlnobords.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 107;BA.debugLine="clvServer.Clear";
parent.mostCurrent._clvserver._clear();
 //BA.debugLineNum = 108;BA.debugLine="mqttBaseSubBord.Initialize";
parent._vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv3._initialize /*String*/ (processBA);
 //BA.debugLineNum = 109;BA.debugLine="mqttBaseSubBord.Connect";
parent._vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv3._vvvvvvvvv1 /*String*/ ();
 //BA.debugLineNum = 111;BA.debugLine="baseFile.GetBase";
parent._v5._vvvvv1 /*anywheresoftware.b4a.objects.collections.List*/ ();
 //BA.debugLineNum = 113;BA.debugLine="tmrBordLastAlive.Initialize(\"tmrBordAlive\", 10000";
parent._vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv4.Initialize(processBA,"tmrBordAlive",(long) (10000));
 //BA.debugLineNum = 114;BA.debugLine="tmrBordLastAlive.Enabled = True";
parent._vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv4.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 115;BA.debugLine="connectTime = DateTime.Now";
parent._vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv5 = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 117;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static String  _tmrbordalive_tick() throws Exception{
 //BA.debugLineNum = 207;BA.debugLine="Sub tmrBordAlive_Tick";
 //BA.debugLineNum = 208;BA.debugLine="If Not(checkConnectTime) Then Return";
if (anywheresoftware.b4a.keywords.Common.Not(_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv0())) { 
if (true) return "";};
 //BA.debugLineNum = 209;BA.debugLine="CheckLastAliveTime";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv1();
 //BA.debugLineNum = 210;BA.debugLine="End Sub";
return "";
}
}
