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
			processBA = new anywheresoftware.b4a.ShellBA(this.getApplicationContext(), null, null, "nl.pdeg.bordondroid", "nl.pdeg.bordondroid.main");
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



public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        b4a.example.dateutils._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}
public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
vis = vis | (serverboard.mostCurrent != null);
vis = vis | (locations.mostCurrent != null);
vis = vis | (selectlocation.mostCurrent != null);
return vis;}

private static BA killProgramHelper(BA ba) {
    if (ba == null)
        return null;
    anywheresoftware.b4a.BA.SharedProcessBA sharedProcessBA = ba.sharedProcessBA;
    if (sharedProcessBA == null || sharedProcessBA.activityBA == null)
        return null;
    return sharedProcessBA.activityBA.get();
}
public static void killProgram() {
     {
            Activity __a = null;
            if (main.previousOne != null) {
				__a = main.previousOne.get();
			}
            else {
                BA ba = killProgramHelper(main.mostCurrent == null ? null : main.mostCurrent.processBA);
                if (ba != null) __a = ba.activity;
            }
            if (__a != null)
				__a.finish();}

BA.applicationContext.stopService(new android.content.Intent(BA.applicationContext, starter.class));
 {
            Activity __a = null;
            if (serverboard.previousOne != null) {
				__a = serverboard.previousOne.get();
			}
            else {
                BA ba = killProgramHelper(serverboard.mostCurrent == null ? null : serverboard.mostCurrent.processBA);
                if (ba != null) __a = ba.activity;
            }
            if (__a != null)
				__a.finish();}

 {
            Activity __a = null;
            if (locations.previousOne != null) {
				__a = locations.previousOne.get();
			}
            else {
                BA ba = killProgramHelper(locations.mostCurrent == null ? null : locations.mostCurrent.processBA);
                if (ba != null) __a = ba.activity;
            }
            if (__a != null)
				__a.finish();}

 {
            Activity __a = null;
            if (selectlocation.previousOne != null) {
				__a = selectlocation.previousOne.get();
			}
            else {
                BA ba = killProgramHelper(selectlocation.mostCurrent == null ? null : selectlocation.mostCurrent.processBA);
                if (ba != null) __a = ba.activity;
            }
            if (__a != null)
				__a.finish();}

}
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
public nl.pdeg.bordondroid.b4xloadingindicatorbiljartball _b4xloadingindicatorbiljartball1 = null;
public nl.pdeg.bordondroid.b4xloadingindicatorbiljartball _b4xloadingindicatorbiljartball2 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbllastupdate = null;
public b4a.example.dateutils _dateutils = null;
public nl.pdeg.bordondroid.starter _starter = null;
public nl.pdeg.bordondroid.serverboard _serverboard = null;
public nl.pdeg.bordondroid.locations _locations = null;
public nl.pdeg.bordondroid.selectlocation _selectlocation = null;
public static String  _activity_create(boolean _firsttime) throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "activity_create", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "activity_create", new Object[] {_firsttime}));}
RDebugUtils.currentLine=131072;
 //BA.debugLineNum = 131072;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
RDebugUtils.currentLine=131073;
 //BA.debugLineNum = 131073;BA.debugLine="Activity.LoadLayout(\"main\")";
mostCurrent._activity.LoadLayout("main",mostCurrent.activityBA);
RDebugUtils.currentLine=131074;
 //BA.debugLineNum = 131074;BA.debugLine="Log(GetDeviceLayoutValues.ApproximateScreenSize)";
anywheresoftware.b4a.keywords.Common.LogImpl("4131074",BA.NumberToString(anywheresoftware.b4a.keywords.Common.GetDeviceLayoutValues(mostCurrent.activityBA).getApproximateScreenSize()),0);
RDebugUtils.currentLine=131076;
 //BA.debugLineNum = 131076;BA.debugLine="timeOutSeconds.Initialize(\"timeOutSeconds\", 1000)";
_timeoutseconds.Initialize(processBA,"timeOutSeconds",(long) (1000));
RDebugUtils.currentLine=131077;
 //BA.debugLineNum = 131077;BA.debugLine="timeOut.Initialize(\"timeOut\", 20*1000)";
_timeout.Initialize(processBA,"timeOut",(long) (20*1000));
RDebugUtils.currentLine=131078;
 //BA.debugLineNum = 131078;BA.debugLine="tmrBordLastAlive.Initialize(\"tmrBordAlive\", 10000";
_tmrbordlastalive.Initialize(processBA,"tmrBordAlive",(long) (10000));
RDebugUtils.currentLine=131079;
 //BA.debugLineNum = 131079;BA.debugLine="If FirstTime Then mqttBaseSubBord.Initialize";
if (_firsttime) { 
_mqttbasesubbord._initialize /*String*/ (null,processBA);};
RDebugUtils.currentLine=131081;
 //BA.debugLineNum = 131081;BA.debugLine="Starter.appVersion = $\"${Application.LabelName} v";
mostCurrent._starter._appversion /*String*/  = (""+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(anywheresoftware.b4a.keywords.Common.Application.getLabelName()))+" v"+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(anywheresoftware.b4a.keywords.Common.Application.getVersionName()))+"");
RDebugUtils.currentLine=131082;
 //BA.debugLineNum = 131082;BA.debugLine="lblVersion.Text = Starter.appVersion";
mostCurrent._lblversion.setText(BA.ObjectToCharSequence(mostCurrent._starter._appversion /*String*/ ));
RDebugUtils.currentLine=131083;
 //BA.debugLineNum = 131083;BA.debugLine="baseFile.Initialize";
_basefile._initialize /*String*/ (null,processBA);
RDebugUtils.currentLine=131084;
 //BA.debugLineNum = 131084;BA.debugLine="pnlNobords.Visible = False";
mostCurrent._pnlnobords.setVisible(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=131085;
 //BA.debugLineNum = 131085;BA.debugLine="End Sub";
return "";
}
public static boolean  _activity_keypress(int _keycode) throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "activity_keypress", false))
	 {return ((Boolean) Debug.delegate(mostCurrent.activityBA, "activity_keypress", new Object[] {_keycode}));}
RDebugUtils.currentLine=1703936;
 //BA.debugLineNum = 1703936;BA.debugLine="Private Sub Activity_KeyPress(KeyCode As Int) As B";
RDebugUtils.currentLine=1703937;
 //BA.debugLineNum = 1703937;BA.debugLine="If KeyCode = KeyCodes.KEYCODE_BACK Then";
if (_keycode==anywheresoftware.b4a.keywords.Common.KeyCodes.KEYCODE_BACK) { 
RDebugUtils.currentLine=1703938;
 //BA.debugLineNum = 1703938;BA.debugLine="tmrBordLastAlive.Enabled = False";
_tmrbordlastalive.setEnabled(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=1703939;
 //BA.debugLineNum = 1703939;BA.debugLine="DisconnectMqtt";
_disconnectmqtt();
RDebugUtils.currentLine=1703940;
 //BA.debugLineNum = 1703940;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
RDebugUtils.currentLine=1703942;
 //BA.debugLineNum = 1703942;BA.debugLine="Return False";
if (true) return anywheresoftware.b4a.keywords.Common.False;
 }else {
RDebugUtils.currentLine=1703944;
 //BA.debugLineNum = 1703944;BA.debugLine="Return False";
if (true) return anywheresoftware.b4a.keywords.Common.False;
 };
RDebugUtils.currentLine=1703946;
 //BA.debugLineNum = 1703946;BA.debugLine="End Sub";
return false;
}
public static String  _disconnectmqtt() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "disconnectmqtt", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "disconnectmqtt", null));}
RDebugUtils.currentLine=1769472;
 //BA.debugLineNum = 1769472;BA.debugLine="Sub DisconnectMqtt";
RDebugUtils.currentLine=1769473;
 //BA.debugLineNum = 1769473;BA.debugLine="If mqttBaseSubBord.connected Then";
if (_mqttbasesubbord._connected /*boolean*/ ) { 
RDebugUtils.currentLine=1769474;
 //BA.debugLineNum = 1769474;BA.debugLine="mqttBaseSubBord.Disconnect";
_mqttbasesubbord._disconnect /*String*/ (null);
 };
RDebugUtils.currentLine=1769476;
 //BA.debugLineNum = 1769476;BA.debugLine="If CheckClientConnected Then";
if (_checkclientconnected()) { 
RDebugUtils.currentLine=1769477;
 //BA.debugLineNum = 1769477;BA.debugLine="mqttBaseSubBord.Disconnect";
_mqttbasesubbord._disconnect /*String*/ (null);
 };
RDebugUtils.currentLine=1769480;
 //BA.debugLineNum = 1769480;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
RDebugUtils.currentModule="main";
RDebugUtils.currentLine=262144;
 //BA.debugLineNum = 262144;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
RDebugUtils.currentLine=262145;
 //BA.debugLineNum = 262145;BA.debugLine="timeOutSeconds.Enabled = False";
_timeoutseconds.setEnabled(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=262146;
 //BA.debugLineNum = 262146;BA.debugLine="timeOut.Enabled = False";
_timeout.setEnabled(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=262147;
 //BA.debugLineNum = 262147;BA.debugLine="If pnlNobords.Visible Then";
if (mostCurrent._pnlnobords.getVisible()) { 
RDebugUtils.currentLine=262148;
 //BA.debugLineNum = 262148;BA.debugLine="pnlNobords.SetVisibleAnimated(0, False)";
mostCurrent._pnlnobords.SetVisibleAnimated((int) (0),anywheresoftware.b4a.keywords.Common.False);
 };
RDebugUtils.currentLine=262152;
 //BA.debugLineNum = 262152;BA.debugLine="Starter.mainPaused = True";
mostCurrent._starter._mainpaused /*boolean*/  = anywheresoftware.b4a.keywords.Common.True;
RDebugUtils.currentLine=262153;
 //BA.debugLineNum = 262153;BA.debugLine="Starter.pingMqtt = False";
mostCurrent._starter._pingmqtt /*boolean*/  = anywheresoftware.b4a.keywords.Common.False;
RDebugUtils.currentLine=262154;
 //BA.debugLineNum = 262154;BA.debugLine="ResumeConnection(False)";
_resumeconnection(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=262155;
 //BA.debugLineNum = 262155;BA.debugLine="mqttBaseSubBord.Disconnect";
_mqttbasesubbord._disconnect /*String*/ (null);
RDebugUtils.currentLine=262157;
 //BA.debugLineNum = 262157;BA.debugLine="If UserClosed Then";
if (_userclosed) { 
RDebugUtils.currentLine=262158;
 //BA.debugLineNum = 262158;BA.debugLine="Starter.mainPaused = False";
mostCurrent._starter._mainpaused /*boolean*/  = anywheresoftware.b4a.keywords.Common.False;
RDebugUtils.currentLine=262159;
 //BA.debugLineNum = 262159;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 };
RDebugUtils.currentLine=262162;
 //BA.debugLineNum = 262162;BA.debugLine="End Sub";
return "";
}
public static String  _resumeconnection(boolean _resume) throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "resumeconnection", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "resumeconnection", new Object[] {_resume}));}
RDebugUtils.currentLine=1638400;
 //BA.debugLineNum = 1638400;BA.debugLine="Sub ResumeConnection(resume As Boolean)";
RDebugUtils.currentLine=1638401;
 //BA.debugLineNum = 1638401;BA.debugLine="tmrBordLastAlive.Interval = 10*1000";
_tmrbordlastalive.setInterval((long) (10*1000));
RDebugUtils.currentLine=1638402;
 //BA.debugLineNum = 1638402;BA.debugLine="tmrBordLastAlive.Enabled = resume";
_tmrbordlastalive.setEnabled(_resume);
RDebugUtils.currentLine=1638403;
 //BA.debugLineNum = 1638403;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "activity_resume", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "activity_resume", null));}
RDebugUtils.currentLine=196608;
 //BA.debugLineNum = 196608;BA.debugLine="Sub Activity_Resume";
RDebugUtils.currentLine=196609;
 //BA.debugLineNum = 196609;BA.debugLine="ShowSelectLocationButton";
_showselectlocationbutton();
RDebugUtils.currentLine=196611;
 //BA.debugLineNum = 196611;BA.debugLine="If Starter.locationSelected Then";
if (mostCurrent._starter._locationselected /*boolean*/ ) { 
RDebugUtils.currentLine=196612;
 //BA.debugLineNum = 196612;BA.debugLine="CheckConnected";
_checkconnected();
RDebugUtils.currentLine=196613;
 //BA.debugLineNum = 196613;BA.debugLine="Return";
if (true) return "";
 };
RDebugUtils.currentLine=196615;
 //BA.debugLineNum = 196615;BA.debugLine="If Starter.mainPaused Then";
if (mostCurrent._starter._mainpaused /*boolean*/ ) { 
RDebugUtils.currentLine=196617;
 //BA.debugLineNum = 196617;BA.debugLine="CheckBordInServerlist";
_checkbordinserverlist();
RDebugUtils.currentLine=196618;
 //BA.debugLineNum = 196618;BA.debugLine="ResumeConnection(True)";
_resumeconnection(anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=196619;
 //BA.debugLineNum = 196619;BA.debugLine="Starter.mainPaused = False";
mostCurrent._starter._mainpaused /*boolean*/  = anywheresoftware.b4a.keywords.Common.False;
RDebugUtils.currentLine=196620;
 //BA.debugLineNum = 196620;BA.debugLine="ReconnectToLocation";
_reconnecttolocation();
 }else {
RDebugUtils.currentLine=196625;
 //BA.debugLineNum = 196625;BA.debugLine="ResumeConnection(True)";
_resumeconnection(anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=196626;
 //BA.debugLineNum = 196626;BA.debugLine="getBaseList";
_getbaselist();
 };
RDebugUtils.currentLine=196628;
 //BA.debugLineNum = 196628;BA.debugLine="End Sub";
return "";
}
public static String  _showselectlocationbutton() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "showselectlocationbutton", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "showselectlocationbutton", null));}
RDebugUtils.currentLine=2818048;
 //BA.debugLineNum = 2818048;BA.debugLine="Sub ShowSelectLocationButton";
RDebugUtils.currentLine=2818049;
 //BA.debugLineNum = 2818049;BA.debugLine="pnlLocationList.Visible = baseFile.GetBase.Size >";
mostCurrent._pnllocationlist.setVisible(_basefile._getbase /*anywheresoftware.b4a.objects.collections.List*/ (null).getSize()>1);
RDebugUtils.currentLine=2818050;
 //BA.debugLineNum = 2818050;BA.debugLine="End Sub";
return "";
}
public static String  _checkconnected() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "checkconnected", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "checkconnected", null));}
RDebugUtils.currentLine=720896;
 //BA.debugLineNum = 720896;BA.debugLine="Private Sub CheckConnected";
RDebugUtils.currentLine=720897;
 //BA.debugLineNum = 720897;BA.debugLine="If clvServer.Size > 0 And Starter.selectedLocatio";
if (mostCurrent._clvserver._getsize()>0 && (mostCurrent._starter._selectedlocationcode /*String*/ ).equals(mostCurrent._lblcurrlocation.getText())) { 
RDebugUtils.currentLine=720898;
 //BA.debugLineNum = 720898;BA.debugLine="mqttBaseSubBord.Connect";
_mqttbasesubbord._connect /*String*/ (null);
RDebugUtils.currentLine=720899;
 //BA.debugLineNum = 720899;BA.debugLine="tmrBordLastAlive.Enabled = True";
_tmrbordlastalive.setEnabled(anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=720900;
 //BA.debugLineNum = 720900;BA.debugLine="Return";
if (true) return "";
 };
RDebugUtils.currentLine=720902;
 //BA.debugLineNum = 720902;BA.debugLine="secondsLeft = timeOut.Interval/1000";
_secondsleft = (int) (_timeout.getInterval()/(double)1000);
RDebugUtils.currentLine=720903;
 //BA.debugLineNum = 720903;BA.debugLine="Starter.serverList.Initialize";
mostCurrent._starter._serverlist /*anywheresoftware.b4a.objects.collections.List*/ .Initialize();
RDebugUtils.currentLine=720904;
 //BA.debugLineNum = 720904;BA.debugLine="clvServer.Clear";
mostCurrent._clvserver._clear();
RDebugUtils.currentLine=720905;
 //BA.debugLineNum = 720905;BA.debugLine="lblCurrLocation.Text = Starter.selectedLocationDe";
mostCurrent._lblcurrlocation.setText(BA.ObjectToCharSequence(mostCurrent._starter._selectedlocationdescription /*String*/ ));
RDebugUtils.currentLine=720906;
 //BA.debugLineNum = 720906;BA.debugLine="B4XLoadingIndicatorBiljartBall1.Show";
mostCurrent._b4xloadingindicatorbiljartball1._show /*String*/ (null);
RDebugUtils.currentLine=720907;
 //BA.debugLineNum = 720907;BA.debugLine="B4XLoadingIndicatorBiljartBall2.Show";
mostCurrent._b4xloadingindicatorbiljartball2._show /*String*/ (null);
RDebugUtils.currentLine=720908;
 //BA.debugLineNum = 720908;BA.debugLine="pnlNobords.Visible = True";
mostCurrent._pnlnobords.setVisible(anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=720909;
 //BA.debugLineNum = 720909;BA.debugLine="timeOutSeconds.Enabled = True";
_timeoutseconds.setEnabled(anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=720910;
 //BA.debugLineNum = 720910;BA.debugLine="Starter.locationSelected = False";
mostCurrent._starter._locationselected /*boolean*/  = anywheresoftware.b4a.keywords.Common.False;
RDebugUtils.currentLine=720911;
 //BA.debugLineNum = 720911;BA.debugLine="ResumeConnection(True)";
_resumeconnection(anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=720912;
 //BA.debugLineNum = 720912;BA.debugLine="ReconnectToLocation";
_reconnecttolocation();
RDebugUtils.currentLine=720913;
 //BA.debugLineNum = 720913;BA.debugLine="End Sub";
return "";
}
public static String  _checkbordinserverlist() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "checkbordinserverlist", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "checkbordinserverlist", null));}
nl.pdeg.bordondroid.main._bordstatus _bs = null;
RDebugUtils.currentLine=917504;
 //BA.debugLineNum = 917504;BA.debugLine="Private Sub CheckBordInServerlist";
RDebugUtils.currentLine=917505;
 //BA.debugLineNum = 917505;BA.debugLine="If Starter.serverList.Size > 0 Then";
if (mostCurrent._starter._serverlist /*anywheresoftware.b4a.objects.collections.List*/ .getSize()>0) { 
RDebugUtils.currentLine=917506;
 //BA.debugLineNum = 917506;BA.debugLine="clvServer.Clear";
mostCurrent._clvserver._clear();
RDebugUtils.currentLine=917507;
 //BA.debugLineNum = 917507;BA.debugLine="For Each bs As bordStatus In Starter.serverList";
{
final anywheresoftware.b4a.BA.IterableList group3 = mostCurrent._starter._serverlist /*anywheresoftware.b4a.objects.collections.List*/ ;
final int groupLen3 = group3.getSize()
;int index3 = 0;
;
for (; index3 < groupLen3;index3++){
_bs = (nl.pdeg.bordondroid.main._bordstatus)(group3.Get(index3));
RDebugUtils.currentLine=917508;
 //BA.debugLineNum = 917508;BA.debugLine="clvServer.Add(genUnitList(bs.name, bs.alive, bs";
mostCurrent._clvserver._add((anywheresoftware.b4a.objects.B4XViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.B4XViewWrapper(), (java.lang.Object)(_genunitlist(_bs.name /*String*/ ,_bs.alive /*boolean*/ ,_bs.players /*String*/ ).getObject())),(Object)(""));
 }
};
 };
RDebugUtils.currentLine=917512;
 //BA.debugLineNum = 917512;BA.debugLine="End Sub";
return "";
}
public static String  _reconnecttolocation() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "reconnecttolocation", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "reconnecttolocation", null));}
RDebugUtils.currentLine=1179648;
 //BA.debugLineNum = 1179648;BA.debugLine="Sub ReconnectToLocation";
RDebugUtils.currentLine=1179649;
 //BA.debugLineNum = 1179649;BA.debugLine="timeOut.Enabled = True";
_timeout.setEnabled(anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=1179650;
 //BA.debugLineNum = 1179650;BA.debugLine="mqttBaseSubBord.Initialize";
_mqttbasesubbord._initialize /*String*/ (null,processBA);
RDebugUtils.currentLine=1179651;
 //BA.debugLineNum = 1179651;BA.debugLine="CallSub2(Starter, \"SetLastWill\", \"bordpubdied\")";
anywheresoftware.b4a.keywords.Common.CallSubDebug2(processBA,(Object)(mostCurrent._starter.getObject()),"SetLastWill",(Object)("bordpubdied"));
RDebugUtils.currentLine=1179652;
 //BA.debugLineNum = 1179652;BA.debugLine="CallSub2(Starter, \"SetSubBase\", Starter.selectedL";
anywheresoftware.b4a.keywords.Common.CallSubDebug2(processBA,(Object)(mostCurrent._starter.getObject()),"SetSubBase",(Object)(mostCurrent._starter._selectedlocationcode /*String*/ ));
RDebugUtils.currentLine=1179653;
 //BA.debugLineNum = 1179653;BA.debugLine="CallSub2(Starter, \"SetSubString2\", \"/pubbord\")";
anywheresoftware.b4a.keywords.Common.CallSubDebug2(processBA,(Object)(mostCurrent._starter.getObject()),"SetSubString2",(Object)("/pubbord"));
RDebugUtils.currentLine=1179654;
 //BA.debugLineNum = 1179654;BA.debugLine="CallSub2(Starter, \"SetUnsubscribeString2\", \"\")";
anywheresoftware.b4a.keywords.Common.CallSubDebug2(processBA,(Object)(mostCurrent._starter.getObject()),"SetUnsubscribeString2",(Object)(""));
RDebugUtils.currentLine=1179655;
 //BA.debugLineNum = 1179655;BA.debugLine="mqttBaseSubBord.Connect";
_mqttbasesubbord._connect /*String*/ (null);
RDebugUtils.currentLine=1179656;
 //BA.debugLineNum = 1179656;BA.debugLine="End Sub";
return "";
}
public static String  _getbaselist() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "getbaselist", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "getbaselist", null));}
int _listsize = 0;
nl.pdeg.bordondroid.main._locationbord _loc = null;
RDebugUtils.currentLine=1048576;
 //BA.debugLineNum = 1048576;BA.debugLine="Private Sub getBaseList";
RDebugUtils.currentLine=1048577;
 //BA.debugLineNum = 1048577;BA.debugLine="Dim listSize As Int = 0";
_listsize = (int) (0);
RDebugUtils.currentLine=1048579;
 //BA.debugLineNum = 1048579;BA.debugLine="baseList.Initialize";
_baselist.Initialize();
RDebugUtils.currentLine=1048580;
 //BA.debugLineNum = 1048580;BA.debugLine="baseList = baseFile.GetBase";
_baselist = _basefile._getbase /*anywheresoftware.b4a.objects.collections.List*/ (null);
RDebugUtils.currentLine=1048587;
 //BA.debugLineNum = 1048587;BA.debugLine="If baseList.Size = 0 Then";
if (_baselist.getSize()==0) { 
RDebugUtils.currentLine=1048588;
 //BA.debugLineNum = 1048588;BA.debugLine="pnlLocationCOde.Visible = True";
mostCurrent._pnllocationcode.setVisible(anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=1048589;
 //BA.debugLineNum = 1048589;BA.debugLine="Return";
if (true) return "";
 };
RDebugUtils.currentLine=1048592;
 //BA.debugLineNum = 1048592;BA.debugLine="If baseList.IsInitialized Then";
if (_baselist.IsInitialized()) { 
RDebugUtils.currentLine=1048593;
 //BA.debugLineNum = 1048593;BA.debugLine="listSize = baseList.Size";
_listsize = _baselist.getSize();
 };
RDebugUtils.currentLine=1048595;
 //BA.debugLineNum = 1048595;BA.debugLine="If listSize > 1 Then";
if (_listsize>1) { 
RDebugUtils.currentLine=1048596;
 //BA.debugLineNum = 1048596;BA.debugLine="pnlLocationList.SetVisibleAnimated(500, True)";
mostCurrent._pnllocationlist.SetVisibleAnimated((int) (500),anywheresoftware.b4a.keywords.Common.True);
 }else {
RDebugUtils.currentLine=1048598;
 //BA.debugLineNum = 1048598;BA.debugLine="pnlLocationList.SetVisibleAnimated(500, False)";
mostCurrent._pnllocationlist.SetVisibleAnimated((int) (500),anywheresoftware.b4a.keywords.Common.False);
 };
RDebugUtils.currentLine=1048601;
 //BA.debugLineNum = 1048601;BA.debugLine="lblNoBord.Text = \"Wachten op borden\"";
mostCurrent._lblnobord.setText(BA.ObjectToCharSequence("Wachten op borden"));
RDebugUtils.currentLine=1048603;
 //BA.debugLineNum = 1048603;BA.debugLine="If baseList.IsInitialized = False Then";
if (_baselist.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
RDebugUtils.currentLine=1048604;
 //BA.debugLineNum = 1048604;BA.debugLine="pnlLocationCOde.Visible = True";
mostCurrent._pnllocationcode.setVisible(anywheresoftware.b4a.keywords.Common.True);
 }else 
{RDebugUtils.currentLine=1048605;
 //BA.debugLineNum = 1048605;BA.debugLine="Else If clvServer.Size > 0 Then";
if (mostCurrent._clvserver._getsize()>0) { 
 }else 
{RDebugUtils.currentLine=1048607;
 //BA.debugLineNum = 1048607;BA.debugLine="Else If baseList.Size = 1 Then";
if (_baselist.getSize()==1) { 
RDebugUtils.currentLine=1048608;
 //BA.debugLineNum = 1048608;BA.debugLine="Dim loc As locationBord";
_loc = new nl.pdeg.bordondroid.main._locationbord();
RDebugUtils.currentLine=1048609;
 //BA.debugLineNum = 1048609;BA.debugLine="loc.Initialize";
_loc.Initialize();
RDebugUtils.currentLine=1048610;
 //BA.debugLineNum = 1048610;BA.debugLine="loc = baseList.Get(0)";
_loc = (nl.pdeg.bordondroid.main._locationbord)(_baselist.Get((int) (0)));
RDebugUtils.currentLine=1048611;
 //BA.debugLineNum = 1048611;BA.debugLine="Starter.selectedLocationCode = loc.code";
mostCurrent._starter._selectedlocationcode /*String*/  = _loc.code /*String*/ ;
RDebugUtils.currentLine=1048612;
 //BA.debugLineNum = 1048612;BA.debugLine="Starter.selectedLocationDescription = loc.descri";
mostCurrent._starter._selectedlocationdescription /*String*/  = _loc.description /*String*/ ;
RDebugUtils.currentLine=1048613;
 //BA.debugLineNum = 1048613;BA.debugLine="lblCurrLocation.Text = loc.description";
mostCurrent._lblcurrlocation.setText(BA.ObjectToCharSequence(_loc.description /*String*/ ));
RDebugUtils.currentLine=1048614;
 //BA.debugLineNum = 1048614;BA.debugLine="InitConnection";
_initconnection();
 }else {
RDebugUtils.currentLine=1048616;
 //BA.debugLineNum = 1048616;BA.debugLine="StartActivity(SelectLocation)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(mostCurrent._selectlocation.getObject()));
 }}}
;
RDebugUtils.currentLine=1048618;
 //BA.debugLineNum = 1048618;BA.debugLine="End Sub";
return "";
}
public static void  _addunkownip(String _ip,String _name,String _players) throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "addunkownip", false))
	 {Debug.delegate(mostCurrent.activityBA, "addunkownip", new Object[] {_ip,_name,_players}); return;}
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
RDebugUtils.currentModule="main";

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
RDebugUtils.currentLine=1966081;
 //BA.debugLineNum = 1966081;BA.debugLine="Dim bordStatus As bordStatus";
_bordstatus = new nl.pdeg.bordondroid.main._bordstatus();
RDebugUtils.currentLine=1966082;
 //BA.debugLineNum = 1966082;BA.debugLine="bordStatus.Initialize";
_bordstatus.Initialize();
RDebugUtils.currentLine=1966083;
 //BA.debugLineNum = 1966083;BA.debugLine="bordStatus.ip = ip";
_bordstatus.ip /*String*/  = _ip;
RDebugUtils.currentLine=1966084;
 //BA.debugLineNum = 1966084;BA.debugLine="bordStatus.name = name";
_bordstatus.name /*String*/  = _name;
RDebugUtils.currentLine=1966085;
 //BA.debugLineNum = 1966085;BA.debugLine="bordStatus.players = players";
_bordstatus.players /*String*/  = _players;
RDebugUtils.currentLine=1966086;
 //BA.debugLineNum = 1966086;BA.debugLine="bordStatus.timeStamp = DateTime.Now";
_bordstatus.timeStamp /*long*/  = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
RDebugUtils.currentLine=1966087;
 //BA.debugLineNum = 1966087;BA.debugLine="bordStatus.alive = True";
_bordstatus.alive /*boolean*/  = anywheresoftware.b4a.keywords.Common.True;
RDebugUtils.currentLine=1966089;
 //BA.debugLineNum = 1966089;BA.debugLine="Starter.serverList.Add(bordStatus)";
parent.mostCurrent._starter._serverlist /*anywheresoftware.b4a.objects.collections.List*/ .Add((Object)(_bordstatus));
RDebugUtils.currentLine=1966090;
 //BA.debugLineNum = 1966090;BA.debugLine="CheckIpInClv(bordStatus, players)";
_checkipinclv(_bordstatus,_players);
RDebugUtils.currentLine=1966091;
 //BA.debugLineNum = 1966091;BA.debugLine="baseFile.SetBordDiedByName(name, clvServer, True)";
parent._basefile._setborddiedbyname /*String*/ (null,_name,parent.mostCurrent._clvserver,anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=1966093;
 //BA.debugLineNum = 1966093;BA.debugLine="If clvServer.GetSize > 0 Then";
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
RDebugUtils.currentLine=1966094;
 //BA.debugLineNum = 1966094;BA.debugLine="pnlNobords.SetVisibleAnimated(500, False)";
parent.mostCurrent._pnlnobords.SetVisibleAnimated((int) (500),anywheresoftware.b4a.keywords.Common.False);
 if (true) break;

case 5:
//C
this.state = 6;
RDebugUtils.currentLine=1966096;
 //BA.debugLineNum = 1966096;BA.debugLine="pnlNobords.SetVisibleAnimated(500, True)";
parent.mostCurrent._pnlnobords.SetVisibleAnimated((int) (500),anywheresoftware.b4a.keywords.Common.True);
 if (true) break;

case 6:
//C
this.state = -1;
;
RDebugUtils.currentLine=1966100;
 //BA.debugLineNum = 1966100;BA.debugLine="Sleep(400)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "main", "addunkownip"),(int) (400));
this.state = 7;
return;
case 7:
//C
this.state = -1;
;
RDebugUtils.currentLine=1966102;
 //BA.debugLineNum = 1966102;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static String  _checkipinclv(nl.pdeg.bordondroid.main._bordstatus _bord,String _players) throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "checkipinclv", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "checkipinclv", new Object[] {_bord,_players}));}
anywheresoftware.b4a.objects.PanelWrapper _p = null;
int _i = 0;
RDebugUtils.currentLine=2031616;
 //BA.debugLineNum = 2031616;BA.debugLine="Sub CheckIpInClv(bord As bordStatus, players As St";
RDebugUtils.currentLine=2031617;
 //BA.debugLineNum = 2031617;BA.debugLine="Dim p As Panel";
_p = new anywheresoftware.b4a.objects.PanelWrapper();
RDebugUtils.currentLine=2031620;
 //BA.debugLineNum = 2031620;BA.debugLine="For i = 0 To clvServer.GetSize-1";
{
final int step2 = 1;
final int limit2 = (int) (mostCurrent._clvserver._getsize()-1);
_i = (int) (0) ;
for (;_i <= limit2 ;_i = _i + step2 ) {
RDebugUtils.currentLine=2031621;
 //BA.debugLineNum = 2031621;BA.debugLine="p = clvServer.GetPanel(i)";
_p = (anywheresoftware.b4a.objects.PanelWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.PanelWrapper(), (android.view.ViewGroup)(mostCurrent._clvserver._getpanel(_i).getObject()));
RDebugUtils.currentLine=2031622;
 //BA.debugLineNum = 2031622;BA.debugLine="If p.Tag = bord.name Then";
if ((_p.getTag()).equals((Object)(_bord.name /*String*/ ))) { 
RDebugUtils.currentLine=2031624;
 //BA.debugLineNum = 2031624;BA.debugLine="baseFile.SetBordDiedByName(bord.name, clvServer";
_basefile._setborddiedbyname /*String*/ (null,_bord.name /*String*/ ,mostCurrent._clvserver,_bord.alive /*boolean*/ );
 };
 }
};
RDebugUtils.currentLine=2031629;
 //BA.debugLineNum = 2031629;BA.debugLine="pnlNobords.SetVisibleAnimated(1000, False)";
mostCurrent._pnlnobords.SetVisibleAnimated((int) (1000),anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=2031630;
 //BA.debugLineNum = 2031630;BA.debugLine="clvServer.Add(genUnitList(bord.name, bord.alive,";
mostCurrent._clvserver._add((anywheresoftware.b4a.objects.B4XViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.B4XViewWrapper(), (java.lang.Object)(_genunitlist(_bord.name /*String*/ ,_bord.alive /*boolean*/ ,_players).getObject())),(Object)(""));
RDebugUtils.currentLine=2031631;
 //BA.debugLineNum = 2031631;BA.debugLine="End Sub";
return "";
}
public static void  _btncancel_click() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "btncancel_click", false))
	 {Debug.delegate(mostCurrent.activityBA, "btncancel_click", null); return;}
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
RDebugUtils.currentModule="main";

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
RDebugUtils.currentLine=2228225;
 //BA.debugLineNum = 2228225;BA.debugLine="Msgbox2Async(\"Applicatie afsluiten?\", Application";
anywheresoftware.b4a.keywords.Common.Msgbox2Async(BA.ObjectToCharSequence("Applicatie afsluiten?"),BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Application.getLabelName()),"JA","","NEE",anywheresoftware.b4a.keywords.Common.Application.getIcon(),processBA,anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=2228226;
 //BA.debugLineNum = 2228226;BA.debugLine="Wait For Msgbox_Result(Result As Int)";
anywheresoftware.b4a.keywords.Common.WaitFor("msgbox_result", processBA, new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "main", "btncancel_click"), null);
this.state = 5;
return;
case 5:
//C
this.state = 1;
_result = (Integer) result[0];
;
RDebugUtils.currentLine=2228227;
 //BA.debugLineNum = 2228227;BA.debugLine="If Result = DialogResponse.POSITIVE Then";
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
RDebugUtils.currentLine=2228228;
 //BA.debugLineNum = 2228228;BA.debugLine="Activity.Finish";
parent.mostCurrent._activity.Finish();
 if (true) break;

case 4:
//C
this.state = -1;
;
RDebugUtils.currentLine=2228230;
 //BA.debugLineNum = 2228230;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static void  _btnok_click() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "btnok_click", false))
	 {Debug.delegate(mostCurrent.activityBA, "btnok_click", null); return;}
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
RDebugUtils.currentModule="main";

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
RDebugUtils.currentLine=2293761;
 //BA.debugLineNum = 2293761;BA.debugLine="If edtFloatCode.Text = \"\" Then";
if (true) break;

case 1:
//if
this.state = 4;
if ((parent.mostCurrent._edtfloatcode._gettext /*String*/ (null)).equals("")) { 
this.state = 3;
}if (true) break;

case 3:
//C
this.state = 4;
RDebugUtils.currentLine=2293762;
 //BA.debugLineNum = 2293762;BA.debugLine="Msgbox2Async(\"Locatie code mag niet leeg zijn\",";
anywheresoftware.b4a.keywords.Common.Msgbox2Async(BA.ObjectToCharSequence("Locatie code mag niet leeg zijn"),BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Application.getLabelName()),"OKE","","",anywheresoftware.b4a.keywords.Common.Application.getIcon(),processBA,anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=2293763;
 //BA.debugLineNum = 2293763;BA.debugLine="Wait For Msgbox_Result(Result As Int)";
anywheresoftware.b4a.keywords.Common.WaitFor("msgbox_result", processBA, new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "main", "btnok_click"), null);
this.state = 17;
return;
case 17:
//C
this.state = 4;
_result = (Integer) result[0];
;
RDebugUtils.currentLine=2293764;
 //BA.debugLineNum = 2293764;BA.debugLine="Return";
if (true) return ;
 if (true) break;

case 4:
//C
this.state = 5;
;
RDebugUtils.currentLine=2293767;
 //BA.debugLineNum = 2293767;BA.debugLine="Dim ime As IME";
_ime = new anywheresoftware.b4a.objects.IME();
RDebugUtils.currentLine=2293768;
 //BA.debugLineNum = 2293768;BA.debugLine="ime.Initialize(Me)";
_ime.Initialize(BA.ObjectToString(main.getObject()));
RDebugUtils.currentLine=2293769;
 //BA.debugLineNum = 2293769;BA.debugLine="Starter.testBaseName = True";
parent.mostCurrent._starter._testbasename /*boolean*/  = anywheresoftware.b4a.keywords.Common.True;
RDebugUtils.currentLine=2293770;
 //BA.debugLineNum = 2293770;BA.debugLine="Starter.selectedLocationCode = edtFloatCode.Text";
parent.mostCurrent._starter._selectedlocationcode /*String*/  = parent.mostCurrent._edtfloatcode._gettext /*String*/ (null);
RDebugUtils.currentLine=2293772;
 //BA.debugLineNum = 2293772;BA.debugLine="Starter.SetLastWill(\"bordpubdied\")";
parent.mostCurrent._starter._setlastwill /*String*/ ("bordpubdied");
RDebugUtils.currentLine=2293774;
 //BA.debugLineNum = 2293774;BA.debugLine="Starter.SetSubBase(Starter.selectedLocationCode)";
parent.mostCurrent._starter._setsubbase /*String*/ (parent.mostCurrent._starter._selectedlocationcode /*String*/ );
RDebugUtils.currentLine=2293776;
 //BA.debugLineNum = 2293776;BA.debugLine="Starter.SetSubString2(\"/pubbord\")";
parent.mostCurrent._starter._setsubstring2 /*String*/ ("/pubbord");
RDebugUtils.currentLine=2293778;
 //BA.debugLineNum = 2293778;BA.debugLine="Starter.SetUnsubscribeString2(\"\")";
parent.mostCurrent._starter._setunsubscribestring2 /*String*/ ("");
RDebugUtils.currentLine=2293779;
 //BA.debugLineNum = 2293779;BA.debugLine="mqttBaseSubBord.Initialize";
parent._mqttbasesubbord._initialize /*String*/ (null,processBA);
RDebugUtils.currentLine=2293780;
 //BA.debugLineNum = 2293780;BA.debugLine="Sleep(100)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "main", "btnok_click"),(int) (100));
this.state = 18;
return;
case 18:
//C
this.state = 5;
;
RDebugUtils.currentLine=2293781;
 //BA.debugLineNum = 2293781;BA.debugLine="mqttBaseSubBord.Connect";
parent._mqttbasesubbord._connect /*String*/ (null);
RDebugUtils.currentLine=2293783;
 //BA.debugLineNum = 2293783;BA.debugLine="Sleep(1000)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "main", "btnok_click"),(int) (1000));
this.state = 19;
return;
case 19:
//C
this.state = 5;
;
RDebugUtils.currentLine=2293785;
 //BA.debugLineNum = 2293785;BA.debugLine="If mqttBaseSubBord.connected = False Then";
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
RDebugUtils.currentLine=2293786;
 //BA.debugLineNum = 2293786;BA.debugLine="Msgbox2Async(\"Kan geen verbinding maken met loca";
anywheresoftware.b4a.keywords.Common.Msgbox2Async(BA.ObjectToCharSequence("Kan geen verbinding maken met locatie"),BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Application.getLabelName()),"OKE","","",anywheresoftware.b4a.keywords.Common.Application.getIcon(),processBA,anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=2293787;
 //BA.debugLineNum = 2293787;BA.debugLine="Wait For Msgbox_Result(Result As Int)";
anywheresoftware.b4a.keywords.Common.WaitFor("msgbox_result", processBA, new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "main", "btnok_click"), null);
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
RDebugUtils.currentLine=2293789;
 //BA.debugLineNum = 2293789;BA.debugLine="Dim code, description As String";
_code = "";
_description = "";
RDebugUtils.currentLine=2293790;
 //BA.debugLineNum = 2293790;BA.debugLine="lblCurrLocation.Text = edtFloatDescription.Text";
parent.mostCurrent._lblcurrlocation.setText(BA.ObjectToCharSequence(parent.mostCurrent._edtfloatdescription._gettext /*String*/ (null)));
RDebugUtils.currentLine=2293791;
 //BA.debugLineNum = 2293791;BA.debugLine="Starter.selectedLocationDescription = edtFloatDe";
parent.mostCurrent._starter._selectedlocationdescription /*String*/  = parent.mostCurrent._edtfloatdescription._gettext /*String*/ (null);
RDebugUtils.currentLine=2293792;
 //BA.debugLineNum = 2293792;BA.debugLine="code = edtFloatCode.Text";
_code = parent.mostCurrent._edtfloatcode._gettext /*String*/ (null);
RDebugUtils.currentLine=2293793;
 //BA.debugLineNum = 2293793;BA.debugLine="If edtFloatDescription.Text = \"\" Then edtFloatDe";
if (true) break;

case 10:
//if
this.state = 15;
if ((parent.mostCurrent._edtfloatdescription._gettext /*String*/ (null)).equals("")) { 
this.state = 12;
;}if (true) break;

case 12:
//C
this.state = 15;
parent.mostCurrent._edtfloatdescription._settext /*String*/ (null,"Nieuwe locatie");
if (true) break;

case 15:
//C
this.state = 16;
;
RDebugUtils.currentLine=2293794;
 //BA.debugLineNum = 2293794;BA.debugLine="description = edtFloatDescription.Text";
_description = parent.mostCurrent._edtfloatdescription._gettext /*String*/ (null);
RDebugUtils.currentLine=2293797;
 //BA.debugLineNum = 2293797;BA.debugLine="mqttBaseSubBord.Disconnect";
parent._mqttbasesubbord._disconnect /*String*/ (null);
RDebugUtils.currentLine=2293798;
 //BA.debugLineNum = 2293798;BA.debugLine="pnlLocationCOde.Visible = False";
parent.mostCurrent._pnllocationcode.setVisible(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=2293799;
 //BA.debugLineNum = 2293799;BA.debugLine="baseFile.SetBase(code, description, \"1\")";
parent._basefile._setbase /*String*/ (null,_code,_description,"1");
RDebugUtils.currentLine=2293800;
 //BA.debugLineNum = 2293800;BA.debugLine="InitConnection";
_initconnection();
RDebugUtils.currentLine=2293801;
 //BA.debugLineNum = 2293801;BA.debugLine="ime.HideKeyboard";
_ime.HideKeyboard(mostCurrent.activityBA);
 if (true) break;

case 16:
//C
this.state = -1;
;
RDebugUtils.currentLine=2293803;
 //BA.debugLineNum = 2293803;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static String  _initconnection() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "initconnection", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "initconnection", null));}
RDebugUtils.currentLine=1245184;
 //BA.debugLineNum = 1245184;BA.debugLine="Sub InitConnection";
RDebugUtils.currentLine=1245185;
 //BA.debugLineNum = 1245185;BA.debugLine="If mqttBaseSubBord.connected Then mqttBaseSubBord";
if (_mqttbasesubbord._connected /*boolean*/ ) { 
_mqttbasesubbord._disconnect /*String*/ (null);};
RDebugUtils.currentLine=1245188;
 //BA.debugLineNum = 1245188;BA.debugLine="lblCurrLocation.Text = Starter.selectedLocationDe";
mostCurrent._lblcurrlocation.setText(BA.ObjectToCharSequence(mostCurrent._starter._selectedlocationdescription /*String*/ ));
RDebugUtils.currentLine=1245189;
 //BA.debugLineNum = 1245189;BA.debugLine="CallSub2(Starter, \"SetLastWill\", \"bordpubdied\")";
anywheresoftware.b4a.keywords.Common.CallSubDebug2(processBA,(Object)(mostCurrent._starter.getObject()),"SetLastWill",(Object)("bordpubdied"));
RDebugUtils.currentLine=1245190;
 //BA.debugLineNum = 1245190;BA.debugLine="CallSub2(Starter, \"SetSubBase\", Starter.selectedL";
anywheresoftware.b4a.keywords.Common.CallSubDebug2(processBA,(Object)(mostCurrent._starter.getObject()),"SetSubBase",(Object)(mostCurrent._starter._selectedlocationcode /*String*/ ));
RDebugUtils.currentLine=1245191;
 //BA.debugLineNum = 1245191;BA.debugLine="CallSub2(Starter, \"SetSubString2\", \"/pubbord\")";
anywheresoftware.b4a.keywords.Common.CallSubDebug2(processBA,(Object)(mostCurrent._starter.getObject()),"SetSubString2",(Object)("/pubbord"));
RDebugUtils.currentLine=1245192;
 //BA.debugLineNum = 1245192;BA.debugLine="CallSub2(Starter, \"SetUnsubscribeString2\", \"\")";
anywheresoftware.b4a.keywords.Common.CallSubDebug2(processBA,(Object)(mostCurrent._starter.getObject()),"SetUnsubscribeString2",(Object)(""));
RDebugUtils.currentLine=1245194;
 //BA.debugLineNum = 1245194;BA.debugLine="StartConnection";
_startconnection();
RDebugUtils.currentLine=1245195;
 //BA.debugLineNum = 1245195;BA.debugLine="End Sub";
return "";
}
public static String  _checkbordexists(nl.pdeg.bordondroid.main._message _bord) throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "checkbordexists", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "checkbordexists", new Object[] {_bord}));}
boolean _bordexists = false;
String _strplayers = "";
String _name = "";
String[] _strdata = null;
nl.pdeg.bordondroid.main._bordstatus _lst = null;
RDebugUtils.currentLine=1900544;
 //BA.debugLineNum = 1900544;BA.debugLine="Sub CheckBordExists(bord As Message)";
RDebugUtils.currentLine=1900545;
 //BA.debugLineNum = 1900545;BA.debugLine="timeOut.Enabled = False";
_timeout.setEnabled(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=1900546;
 //BA.debugLineNum = 1900546;BA.debugLine="timeOutSeconds.Enabled = False";
_timeoutseconds.setEnabled(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=1900547;
 //BA.debugLineNum = 1900547;BA.debugLine="If lblAliveIndicator.Visible Then";
if (mostCurrent._lblaliveindicator.getVisible()) { 
RDebugUtils.currentLine=1900548;
 //BA.debugLineNum = 1900548;BA.debugLine="lblAliveIndicator.SetVisibleAnimated(500, False)";
mostCurrent._lblaliveindicator.SetVisibleAnimated((int) (500),anywheresoftware.b4a.keywords.Common.False);
 }else {
RDebugUtils.currentLine=1900550;
 //BA.debugLineNum = 1900550;BA.debugLine="lblAliveIndicator.SetVisibleAnimated(500, True)";
mostCurrent._lblaliveindicator.SetVisibleAnimated((int) (500),anywheresoftware.b4a.keywords.Common.True);
 };
RDebugUtils.currentLine=1900552;
 //BA.debugLineNum = 1900552;BA.debugLine="Dim bordExists As Boolean = False";
_bordexists = anywheresoftware.b4a.keywords.Common.False;
RDebugUtils.currentLine=1900553;
 //BA.debugLineNum = 1900553;BA.debugLine="Dim strPlayers As String";
_strplayers = "";
RDebugUtils.currentLine=1900556;
 //BA.debugLineNum = 1900556;BA.debugLine="If bord.Body.Length = 0 Then Return";
if (_bord.Body /*String*/ .length()==0) { 
if (true) return "";};
RDebugUtils.currentLine=1900557;
 //BA.debugLineNum = 1900557;BA.debugLine="Dim name As String = bord.Body";
_name = _bord.Body /*String*/ ;
RDebugUtils.currentLine=1900561;
 //BA.debugLineNum = 1900561;BA.debugLine="strPlayers = name'baseFile.SetPlayertext(name)";
_strplayers = _name;
RDebugUtils.currentLine=1900562;
 //BA.debugLineNum = 1900562;BA.debugLine="If name.IndexOf(\"|\") > -1 Then";
if (_name.indexOf("|")>-1) { 
RDebugUtils.currentLine=1900563;
 //BA.debugLineNum = 1900563;BA.debugLine="Dim strData() As String = Regex.Split(\"\\|\", name";
_strdata = anywheresoftware.b4a.keywords.Common.Regex.Split("\\|",_name);
RDebugUtils.currentLine=1900564;
 //BA.debugLineNum = 1900564;BA.debugLine="name = strData(0)";
_name = _strdata[(int) (0)];
 };
RDebugUtils.currentLine=1900568;
 //BA.debugLineNum = 1900568;BA.debugLine="connectTime = -1";
_connecttime = (long) (-1);
RDebugUtils.currentLine=1900569;
 //BA.debugLineNum = 1900569;BA.debugLine="If Starter.serverList.Size = 0 Then";
if (mostCurrent._starter._serverlist /*anywheresoftware.b4a.objects.collections.List*/ .getSize()==0) { 
RDebugUtils.currentLine=1900570;
 //BA.debugLineNum = 1900570;BA.debugLine="AddUnkownIp(\"\", name, strPlayers)";
_addunkownip("",_name,_strplayers);
RDebugUtils.currentLine=1900571;
 //BA.debugLineNum = 1900571;BA.debugLine="Return";
if (true) return "";
 };
RDebugUtils.currentLine=1900574;
 //BA.debugLineNum = 1900574;BA.debugLine="For Each lst As bordStatus In Starter.serverList";
{
final anywheresoftware.b4a.BA.IterableList group22 = mostCurrent._starter._serverlist /*anywheresoftware.b4a.objects.collections.List*/ ;
final int groupLen22 = group22.getSize()
;int index22 = 0;
;
for (; index22 < groupLen22;index22++){
_lst = (nl.pdeg.bordondroid.main._bordstatus)(group22.Get(index22));
RDebugUtils.currentLine=1900579;
 //BA.debugLineNum = 1900579;BA.debugLine="If lst.name = name Then";
if ((_lst.name /*String*/ ).equals(_name)) { 
RDebugUtils.currentLine=1900580;
 //BA.debugLineNum = 1900580;BA.debugLine="bordExists = True";
_bordexists = anywheresoftware.b4a.keywords.Common.True;
RDebugUtils.currentLine=1900582;
 //BA.debugLineNum = 1900582;BA.debugLine="lst.timeStamp = DateTime.Now";
_lst.timeStamp /*long*/  = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
RDebugUtils.currentLine=1900583;
 //BA.debugLineNum = 1900583;BA.debugLine="lst.alive = True";
_lst.alive /*boolean*/  = anywheresoftware.b4a.keywords.Common.True;
RDebugUtils.currentLine=1900585;
 //BA.debugLineNum = 1900585;BA.debugLine="baseFile.CheckPlayers(strPlayers, name, clvServ";
_basefile._checkplayers /*String*/ (null,_strplayers,_name,mostCurrent._clvserver);
 };
 }
};
RDebugUtils.currentLine=1900589;
 //BA.debugLineNum = 1900589;BA.debugLine="If Not(bordExists) Then";
if (anywheresoftware.b4a.keywords.Common.Not(_bordexists)) { 
RDebugUtils.currentLine=1900590;
 //BA.debugLineNum = 1900590;BA.debugLine="AddUnkownIp(\"\", name, strPlayers)";
_addunkownip("",_name,_strplayers);
RDebugUtils.currentLine=1900591;
 //BA.debugLineNum = 1900591;BA.debugLine="Return";
if (true) return "";
 };
RDebugUtils.currentLine=1900593;
 //BA.debugLineNum = 1900593;BA.debugLine="End Sub";
return "";
}
public static anywheresoftware.b4a.objects.PanelWrapper  _genunitlist(String _name,boolean _alive,String _players) throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "genunitlist", false))
	 {return ((anywheresoftware.b4a.objects.PanelWrapper) Debug.delegate(mostCurrent.activityBA, "genunitlist", new Object[] {_name,_alive,_players}));}
anywheresoftware.b4a.objects.PanelWrapper _p = null;
String _currplayertext = "";
RDebugUtils.currentLine=1835008;
 //BA.debugLineNum = 1835008;BA.debugLine="Sub genUnitList(name As String, alive As Boolean,";
RDebugUtils.currentLine=1835009;
 //BA.debugLineNum = 1835009;BA.debugLine="Dim p As Panel";
_p = new anywheresoftware.b4a.objects.PanelWrapper();
RDebugUtils.currentLine=1835010;
 //BA.debugLineNum = 1835010;BA.debugLine="Dim currPlayerText As String";
_currplayertext = "";
RDebugUtils.currentLine=1835012;
 //BA.debugLineNum = 1835012;BA.debugLine="p.Initialize(Me)";
_p.Initialize(mostCurrent.activityBA,BA.ObjectToString(main.getObject()));
RDebugUtils.currentLine=1835013;
 //BA.debugLineNum = 1835013;BA.debugLine="p.SetLayout(0dip, 0dip, clvServer.AsView.Width, 1";
_p.SetLayout(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)),mostCurrent._clvserver._asview().getWidth(),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (125)));
RDebugUtils.currentLine=1835014;
 //BA.debugLineNum = 1835014;BA.debugLine="p.LoadLayout(\"clvBorden\")";
_p.LoadLayout("clvBorden",mostCurrent.activityBA);
RDebugUtils.currentLine=1835015;
 //BA.debugLineNum = 1835015;BA.debugLine="p.Tag = name";
_p.setTag((Object)(_name));
RDebugUtils.currentLine=1835017;
 //BA.debugLineNum = 1835017;BA.debugLine="lblBordName.Text = name.Trim";
mostCurrent._lblbordname.setText(BA.ObjectToCharSequence(_name.trim()));
RDebugUtils.currentLine=1835018;
 //BA.debugLineNum = 1835018;BA.debugLine="lblLastCheck.Text = $\"Laatste controle $Time{Date";
mostCurrent._lbllastcheck.setText(BA.ObjectToCharSequence(("Laatste controle "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("time",(Object)(anywheresoftware.b4a.keywords.Common.DateTime.getNow()))+"")));
RDebugUtils.currentLine=1835020;
 //BA.debugLineNum = 1835020;BA.debugLine="lblLastUpdate.Text = baseFile.SetLastUpdateText";
mostCurrent._lbllastupdate.setText(BA.ObjectToCharSequence(_basefile._setlastupdatetext /*Object*/ (null)));
RDebugUtils.currentLine=1835021;
 //BA.debugLineNum = 1835021;BA.debugLine="lblPLayer.Text = baseFile.SetPlayertext(players)";
mostCurrent._lblplayer.setText(BA.ObjectToCharSequence(_basefile._setplayertext /*Object*/ (null,_players)));
RDebugUtils.currentLine=1835023;
 //BA.debugLineNum = 1835023;BA.debugLine="lblPLayer.Tag = \"players\"";
mostCurrent._lblplayer.setTag((Object)("players"));
RDebugUtils.currentLine=1835025;
 //BA.debugLineNum = 1835025;BA.debugLine="If Not(alive) Then";
if (anywheresoftware.b4a.keywords.Common.Not(_alive)) { 
RDebugUtils.currentLine=1835026;
 //BA.debugLineNum = 1835026;BA.debugLine="lblBordName.Color = Colors.Red";
mostCurrent._lblbordname.setColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
RDebugUtils.currentLine=1835027;
 //BA.debugLineNum = 1835027;BA.debugLine="lblBordName.TextColor = Colors.White";
mostCurrent._lblbordname.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 };
RDebugUtils.currentLine=1835029;
 //BA.debugLineNum = 1835029;BA.debugLine="Return p";
if (true) return _p;
RDebugUtils.currentLine=1835030;
 //BA.debugLineNum = 1835030;BA.debugLine="End Sub";
return null;
}
public static boolean  _checkclientconnected() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "checkclientconnected", false))
	 {return ((Boolean) Debug.delegate(mostCurrent.activityBA, "checkclientconnected", null));}
RDebugUtils.currentLine=2490368;
 //BA.debugLineNum = 2490368;BA.debugLine="Private Sub CheckClientConnected As Boolean";
RDebugUtils.currentLine=2490369;
 //BA.debugLineNum = 2490369;BA.debugLine="Return CallSub(mqttBaseSubBord, \"GetClientConnect";
if (true) return BA.ObjectToBoolean(anywheresoftware.b4a.keywords.Common.CallSubDebug(processBA,(Object)(_mqttbasesubbord),"GetClientConnected"));
RDebugUtils.currentLine=2490370;
 //BA.debugLineNum = 2490370;BA.debugLine="End Sub";
return false;
}
public static boolean  _checkconnecttime() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "checkconnecttime", false))
	 {return ((Boolean) Debug.delegate(mostCurrent.activityBA, "checkconnecttime", null));}
RDebugUtils.currentLine=1441792;
 //BA.debugLineNum = 1441792;BA.debugLine="Sub checkConnectTime As Boolean";
RDebugUtils.currentLine=1441793;
 //BA.debugLineNum = 1441793;BA.debugLine="Return True";
if (true) return anywheresoftware.b4a.keywords.Common.True;
RDebugUtils.currentLine=1441794;
 //BA.debugLineNum = 1441794;BA.debugLine="If connectTime <> -1 Then";
if (_connecttime!=-1) { 
RDebugUtils.currentLine=1441795;
 //BA.debugLineNum = 1441795;BA.debugLine="Log(\"KWDKWMKDM\")";
anywheresoftware.b4a.keywords.Common.LogImpl("41441795","KWDKWMKDM",0);
RDebugUtils.currentLine=1441796;
 //BA.debugLineNum = 1441796;BA.debugLine="pnlNobords.Visible = True";
mostCurrent._pnlnobords.setVisible(anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=1441797;
 //BA.debugLineNum = 1441797;BA.debugLine="mqttBaseSubBord.Disconnect";
_mqttbasesubbord._disconnect /*String*/ (null);
RDebugUtils.currentLine=1441798;
 //BA.debugLineNum = 1441798;BA.debugLine="tmrBordLastAlive.Enabled = False";
_tmrbordlastalive.setEnabled(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=1441799;
 //BA.debugLineNum = 1441799;BA.debugLine="pnlNobords.Visible = False";
mostCurrent._pnlnobords.setVisible(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=1441800;
 //BA.debugLineNum = 1441800;BA.debugLine="Msgbox2Async(\"Geen borden gevonden\", Application";
anywheresoftware.b4a.keywords.Common.Msgbox2Async(BA.ObjectToCharSequence("Geen borden gevonden"),BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Application.getLabelName()),"OKE","","",anywheresoftware.b4a.keywords.Common.Application.getIcon(),processBA,anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=1441801;
 //BA.debugLineNum = 1441801;BA.debugLine="Return False";
if (true) return anywheresoftware.b4a.keywords.Common.False;
 };
RDebugUtils.currentLine=1441803;
 //BA.debugLineNum = 1441803;BA.debugLine="Return True";
if (true) return anywheresoftware.b4a.keywords.Common.True;
RDebugUtils.currentLine=1441804;
 //BA.debugLineNum = 1441804;BA.debugLine="End Sub";
return false;
}
public static String  _checklastalivetime() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "checklastalivetime", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "checklastalivetime", null));}
long _currtime = 0L;
nl.pdeg.bordondroid.main._bordstatus _bd = null;
long _timediff = 0L;
RDebugUtils.currentLine=1507328;
 //BA.debugLineNum = 1507328;BA.debugLine="Sub CheckLastAliveTime";
RDebugUtils.currentLine=1507331;
 //BA.debugLineNum = 1507331;BA.debugLine="If clvServer.Size = 0 Then Return";
if (mostCurrent._clvserver._getsize()==0) { 
if (true) return "";};
RDebugUtils.currentLine=1507332;
 //BA.debugLineNum = 1507332;BA.debugLine="Dim currTime As Long = DateTime.Now";
_currtime = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
RDebugUtils.currentLine=1507337;
 //BA.debugLineNum = 1507337;BA.debugLine="If lblLastCheck.IsInitialized = False Then";
if (mostCurrent._lbllastcheck.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
RDebugUtils.currentLine=1507338;
 //BA.debugLineNum = 1507338;BA.debugLine="lblNoBord.Visible = False";
mostCurrent._lblnobord.setVisible(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=1507339;
 //BA.debugLineNum = 1507339;BA.debugLine="Return";
if (true) return "";
 };
RDebugUtils.currentLine=1507341;
 //BA.debugLineNum = 1507341;BA.debugLine="lblLastCheck.Text = $\"Laatste controle $Time{Date";
mostCurrent._lbllastcheck.setText(BA.ObjectToCharSequence(("Laatste controle "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("time",(Object)(anywheresoftware.b4a.keywords.Common.DateTime.getNow()))+"")));
RDebugUtils.currentLine=1507344;
 //BA.debugLineNum = 1507344;BA.debugLine="For Each bd As bordStatus In Starter.serverList";
{
final anywheresoftware.b4a.BA.IterableList group8 = mostCurrent._starter._serverlist /*anywheresoftware.b4a.objects.collections.List*/ ;
final int groupLen8 = group8.getSize()
;int index8 = 0;
;
for (; index8 < groupLen8;index8++){
_bd = (nl.pdeg.bordondroid.main._bordstatus)(group8.Get(index8));
RDebugUtils.currentLine=1507345;
 //BA.debugLineNum = 1507345;BA.debugLine="Dim timeDiff As Long = currTime-bd.timeStamp";
_timediff = (long) (_currtime-_bd.timeStamp /*long*/ );
RDebugUtils.currentLine=1507346;
 //BA.debugLineNum = 1507346;BA.debugLine="If timeDiff >= Starter.serverDied Then";
if (_timediff>=mostCurrent._starter._serverdied /*long*/ ) { 
RDebugUtils.currentLine=1507347;
 //BA.debugLineNum = 1507347;BA.debugLine="bd.alive = False";
_bd.alive /*boolean*/  = anywheresoftware.b4a.keywords.Common.False;
RDebugUtils.currentLine=1507348;
 //BA.debugLineNum = 1507348;BA.debugLine="baseFile.SetBordDiedByName(bd.name, clvServer,";
_basefile._setborddiedbyname /*String*/ (null,_bd.name /*String*/ ,mostCurrent._clvserver,_bd.alive /*boolean*/ );
 }else {
RDebugUtils.currentLine=1507350;
 //BA.debugLineNum = 1507350;BA.debugLine="baseFile.SetBordDiedByName(bd.name, clvServer,";
_basefile._setborddiedbyname /*String*/ (null,_bd.name /*String*/ ,mostCurrent._clvserver,_bd.alive /*boolean*/ );
 };
 }
};
RDebugUtils.currentLine=1507354;
 //BA.debugLineNum = 1507354;BA.debugLine="End Sub";
return "";
}
public static void  _clvserver_itemclick(int _index,Object _value) throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "clvserver_itemclick", false))
	 {Debug.delegate(mostCurrent.activityBA, "clvserver_itemclick", new Object[] {_index,_value}); return;}
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
RDebugUtils.currentModule="main";

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
RDebugUtils.currentLine=1310721;
 //BA.debugLineNum = 1310721;BA.debugLine="Dim p As Panel = clvServer.GetPanel(Index)";
_p = new anywheresoftware.b4a.objects.PanelWrapper();
_p = (anywheresoftware.b4a.objects.PanelWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.PanelWrapper(), (android.view.ViewGroup)(parent.mostCurrent._clvserver._getpanel(_index).getObject()));
RDebugUtils.currentLine=1310722;
 //BA.debugLineNum = 1310722;BA.debugLine="Dim unit As String";
_unit = "";
RDebugUtils.currentLine=1310724;
 //BA.debugLineNum = 1310724;BA.debugLine="unit = baseFile.GetSelectedLabelTagFromPanel(p, \"";
_unit = parent._basefile._getselectedlabeltagfrompanel /*String*/ (null,_p,"name");
RDebugUtils.currentLine=1310725;
 //BA.debugLineNum = 1310725;BA.debugLine="If baseFile.GetBordAlive(unit) = False Then";
if (true) break;

case 1:
//if
this.state = 4;
if (parent._basefile._getbordalive /*boolean*/ (null,_unit)==anywheresoftware.b4a.keywords.Common.False) { 
this.state = 3;
}if (true) break;

case 3:
//C
this.state = 4;
RDebugUtils.currentLine=1310726;
 //BA.debugLineNum = 1310726;BA.debugLine="baseFile.ShowCustomToast($\"Bord niet gevonden, c";
parent._basefile._showcustomtoast /*String*/ (null,(Object)(("Bord niet gevonden, controleer of het bord \"online\" is")),anywheresoftware.b4a.keywords.Common.False,anywheresoftware.b4a.keywords.Common.Colors.Red);
RDebugUtils.currentLine=1310727;
 //BA.debugLineNum = 1310727;BA.debugLine="Return";
if (true) return ;
 if (true) break;

case 4:
//C
this.state = -1;
;
RDebugUtils.currentLine=1310730;
 //BA.debugLineNum = 1310730;BA.debugLine="Starter.mainPaused = True";
parent.mostCurrent._starter._mainpaused /*boolean*/  = anywheresoftware.b4a.keywords.Common.True;
RDebugUtils.currentLine=1310731;
 //BA.debugLineNum = 1310731;BA.debugLine="Starter.DiscoveredServer = unit";
parent.mostCurrent._starter._discoveredserver /*String*/  = _unit;
RDebugUtils.currentLine=1310732;
 //BA.debugLineNum = 1310732;BA.debugLine="CallSub2(Starter, \"SetLastWill\", \"recvdied\")";
anywheresoftware.b4a.keywords.Common.CallSubDebug2(processBA,(Object)(parent.mostCurrent._starter.getObject()),"SetLastWill",(Object)("recvdied"));
RDebugUtils.currentLine=1310733;
 //BA.debugLineNum = 1310733;BA.debugLine="CallSub2(Starter, \"SetUnit\", PrepTopicName(unit).";
anywheresoftware.b4a.keywords.Common.CallSubDebug2(processBA,(Object)(parent.mostCurrent._starter.getObject()),"SetUnit",(Object)(_preptopicname(_unit).toLowerCase()));
RDebugUtils.currentLine=1310734;
 //BA.debugLineNum = 1310734;BA.debugLine="DisconnectMqtt";
_disconnectmqtt();
RDebugUtils.currentLine=1310735;
 //BA.debugLineNum = 1310735;BA.debugLine="Sleep(100)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "main", "clvserver_itemclick"),(int) (100));
this.state = 5;
return;
case 5:
//C
this.state = -1;
;
RDebugUtils.currentLine=1310736;
 //BA.debugLineNum = 1310736;BA.debugLine="StartActivity(ServerBoard)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(parent.mostCurrent._serverboard.getObject()));
RDebugUtils.currentLine=1310737;
 //BA.debugLineNum = 1310737;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static String  _preptopicname(String _bord) throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "preptopicname", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "preptopicname", new Object[] {_bord}));}
RDebugUtils.currentLine=2162688;
 //BA.debugLineNum = 2162688;BA.debugLine="Private Sub PrepTopicName(bord As String) As Strin";
RDebugUtils.currentLine=2162689;
 //BA.debugLineNum = 2162689;BA.debugLine="Return bord.Replace(\" \", \"\")";
if (true) return _bord.replace(" ","");
RDebugUtils.currentLine=2162690;
 //BA.debugLineNum = 2162690;BA.debugLine="End Sub";
return "";
}
public static String  _connectionerror() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "connectionerror", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "connectionerror", null));}
RDebugUtils.currentLine=2883584;
 //BA.debugLineNum = 2883584;BA.debugLine="Sub ConnectionError";
RDebugUtils.currentLine=2883587;
 //BA.debugLineNum = 2883587;BA.debugLine="End Sub";
return "";
}
public static String  _deletedlocationactive() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "deletedlocationactive", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "deletedlocationactive", null));}
RDebugUtils.currentLine=2752512;
 //BA.debugLineNum = 2752512;BA.debugLine="Sub DeletedLocationActive";
RDebugUtils.currentLine=2752513;
 //BA.debugLineNum = 2752513;BA.debugLine="mqttBaseSubBord.Disconnect";
_mqttbasesubbord._disconnect /*String*/ (null);
RDebugUtils.currentLine=2752514;
 //BA.debugLineNum = 2752514;BA.debugLine="clvServer.Clear";
mostCurrent._clvserver._clear();
RDebugUtils.currentLine=2752515;
 //BA.debugLineNum = 2752515;BA.debugLine="getBaseList";
_getbaselist();
RDebugUtils.currentLine=2752516;
 //BA.debugLineNum = 2752516;BA.debugLine="End Sub";
return "";
}
public static String  _edtfloatcode_textchanged(String _old,String _new) throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "edtfloatcode_textchanged", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "edtfloatcode_textchanged", new Object[] {_old,_new}));}
RDebugUtils.currentLine=2686976;
 //BA.debugLineNum = 2686976;BA.debugLine="Sub edtFloatCode_TextChanged (Old As String, New A";
RDebugUtils.currentLine=2686977;
 //BA.debugLineNum = 2686977;BA.debugLine="If New.Length > 0 Then";
if (_new.length()>0) { 
RDebugUtils.currentLine=2686978;
 //BA.debugLineNum = 2686978;BA.debugLine="btnOk.Enabled = True";
mostCurrent._btnok.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 }else {
RDebugUtils.currentLine=2686980;
 //BA.debugLineNum = 2686980;BA.debugLine="btnOk.Enabled = False";
mostCurrent._btnok.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 };
RDebugUtils.currentLine=2686982;
 //BA.debugLineNum = 2686982;BA.debugLine="End Sub";
return "";
}
public static String  _edtfloatdescription_textchanged(String _old,String _new) throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "edtfloatdescription_textchanged", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "edtfloatdescription_textchanged", new Object[] {_old,_new}));}
RDebugUtils.currentLine=2621440;
 //BA.debugLineNum = 2621440;BA.debugLine="Sub edtFloatDescription_TextChanged (Old As String";
RDebugUtils.currentLine=2621442;
 //BA.debugLineNum = 2621442;BA.debugLine="End Sub";
return "";
}
public static String  _edtlocationcode_textchanged(String _old,String _new) throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "edtlocationcode_textchanged", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "edtlocationcode_textchanged", new Object[] {_old,_new}));}
RDebugUtils.currentLine=2359296;
 //BA.debugLineNum = 2359296;BA.debugLine="Sub edtLocationCode_TextChanged (Old As String, Ne";
RDebugUtils.currentLine=2359297;
 //BA.debugLineNum = 2359297;BA.debugLine="If New.Length > 0 Then";
if (_new.length()>0) { 
RDebugUtils.currentLine=2359298;
 //BA.debugLineNum = 2359298;BA.debugLine="btnOk.Enabled = True";
mostCurrent._btnok.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 }else {
RDebugUtils.currentLine=2359300;
 //BA.debugLineNum = 2359300;BA.debugLine="btnOk.Enabled = False";
mostCurrent._btnok.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 };
RDebugUtils.currentLine=2359302;
 //BA.debugLineNum = 2359302;BA.debugLine="End Sub";
return "";
}
public static String  _startconnection() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "startconnection", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "startconnection", null));}
RDebugUtils.currentLine=983040;
 //BA.debugLineNum = 983040;BA.debugLine="Private Sub StartConnection";
RDebugUtils.currentLine=983042;
 //BA.debugLineNum = 983042;BA.debugLine="lblNoBord.Text = $\"Wachten op borden (${\"20\"})\"$";
mostCurrent._lblnobord.setText(BA.ObjectToCharSequence(("Wachten op borden ("+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)("20"))+")")));
RDebugUtils.currentLine=983043;
 //BA.debugLineNum = 983043;BA.debugLine="secondsLeft = timeOut.Interval/1000";
_secondsleft = (int) (_timeout.getInterval()/(double)1000);
RDebugUtils.currentLine=983044;
 //BA.debugLineNum = 983044;BA.debugLine="timeOut.Enabled = True";
_timeout.setEnabled(anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=983045;
 //BA.debugLineNum = 983045;BA.debugLine="timeOutSeconds.Enabled = True";
_timeoutseconds.setEnabled(anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=983046;
 //BA.debugLineNum = 983046;BA.debugLine="pnlLocationCOde.Visible = False";
mostCurrent._pnllocationcode.setVisible(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=983047;
 //BA.debugLineNum = 983047;BA.debugLine="B4XLoadingIndicatorBiljartBall1.Show";
mostCurrent._b4xloadingindicatorbiljartball1._show /*String*/ (null);
RDebugUtils.currentLine=983048;
 //BA.debugLineNum = 983048;BA.debugLine="B4XLoadingIndicatorBiljartBall2.Show";
mostCurrent._b4xloadingindicatorbiljartball2._show /*String*/ (null);
RDebugUtils.currentLine=983050;
 //BA.debugLineNum = 983050;BA.debugLine="pnlNobords.Visible = True";
mostCurrent._pnlnobords.setVisible(anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=983051;
 //BA.debugLineNum = 983051;BA.debugLine="clvServer.Clear";
mostCurrent._clvserver._clear();
RDebugUtils.currentLine=983052;
 //BA.debugLineNum = 983052;BA.debugLine="mqttBaseSubBord.Initialize";
_mqttbasesubbord._initialize /*String*/ (null,processBA);
RDebugUtils.currentLine=983053;
 //BA.debugLineNum = 983053;BA.debugLine="mqttBaseSubBord.Connect";
_mqttbasesubbord._connect /*String*/ (null);
RDebugUtils.currentLine=983055;
 //BA.debugLineNum = 983055;BA.debugLine="baseFile.GetBase";
_basefile._getbase /*anywheresoftware.b4a.objects.collections.List*/ (null);
RDebugUtils.currentLine=983058;
 //BA.debugLineNum = 983058;BA.debugLine="tmrBordLastAlive.Enabled = True";
_tmrbordlastalive.setEnabled(anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=983059;
 //BA.debugLineNum = 983059;BA.debugLine="connectTime = DateTime.Now";
_connecttime = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
RDebugUtils.currentLine=983061;
 //BA.debugLineNum = 983061;BA.debugLine="End Sub";
return "";
}
public static String  _lblviewbord_click() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "lblviewbord_click", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "lblviewbord_click", null));}
anywheresoftware.b4a.objects.ConcreteViewWrapper _v = null;
anywheresoftware.b4a.objects.PanelWrapper _p = null;
RDebugUtils.currentLine=2097152;
 //BA.debugLineNum = 2097152;BA.debugLine="Sub lblViewBord_Click";
RDebugUtils.currentLine=2097153;
 //BA.debugLineNum = 2097153;BA.debugLine="Dim v As View = Sender";
_v = new anywheresoftware.b4a.objects.ConcreteViewWrapper();
_v = (anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(anywheresoftware.b4a.keywords.Common.Sender(mostCurrent.activityBA)));
RDebugUtils.currentLine=2097154;
 //BA.debugLineNum = 2097154;BA.debugLine="Dim p As Panel = v.Parent";
_p = new anywheresoftware.b4a.objects.PanelWrapper();
_p = (anywheresoftware.b4a.objects.PanelWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.PanelWrapper(), (android.view.ViewGroup)(_v.getParent()));
RDebugUtils.currentLine=2097156;
 //BA.debugLineNum = 2097156;BA.debugLine="clvServer_ItemClick (clvServer.GetItemFromView(p)";
_clvserver_itemclick(mostCurrent._clvserver._getitemfromview((anywheresoftware.b4a.objects.B4XViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.B4XViewWrapper(), (java.lang.Object)(_p.getObject()))),anywheresoftware.b4a.keywords.Common.Null);
RDebugUtils.currentLine=2097157;
 //BA.debugLineNum = 2097157;BA.debugLine="End Sub";
return "";
}
public static String  _pnllocation_click() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "pnllocation_click", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "pnllocation_click", null));}
RDebugUtils.currentLine=2424832;
 //BA.debugLineNum = 2424832;BA.debugLine="Sub pnlLocation_Click";
RDebugUtils.currentLine=2424834;
 //BA.debugLineNum = 2424834;BA.debugLine="If CheckClientConnected Then";
if (_checkclientconnected()) { 
RDebugUtils.currentLine=2424835;
 //BA.debugLineNum = 2424835;BA.debugLine="mqttBaseSubBord.Disconnect";
_mqttbasesubbord._disconnect /*String*/ (null);
 };
RDebugUtils.currentLine=2424837;
 //BA.debugLineNum = 2424837;BA.debugLine="StartActivity(locations)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(mostCurrent._locations.getObject()));
RDebugUtils.currentLine=2424838;
 //BA.debugLineNum = 2424838;BA.debugLine="End Sub";
return "";
}
public static String  _pnllocationlist_click() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "pnllocationlist_click", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "pnllocationlist_click", null));}
RDebugUtils.currentLine=2555904;
 //BA.debugLineNum = 2555904;BA.debugLine="Sub pnlLocationList_Click";
RDebugUtils.currentLine=2555905;
 //BA.debugLineNum = 2555905;BA.debugLine="StartActivity(SelectLocation)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(mostCurrent._selectlocation.getObject()));
RDebugUtils.currentLine=2555906;
 //BA.debugLineNum = 2555906;BA.debugLine="End Sub";
return "";
}
public static String  _pnlreload_click() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "pnlreload_click", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "pnlreload_click", null));}
RDebugUtils.currentLine=2949120;
 //BA.debugLineNum = 2949120;BA.debugLine="Sub pnlReload_Click";
RDebugUtils.currentLine=2949121;
 //BA.debugLineNum = 2949121;BA.debugLine="timeOutSeconds.Enabled = True";
_timeoutseconds.setEnabled(anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=2949122;
 //BA.debugLineNum = 2949122;BA.debugLine="clvServer.Clear";
mostCurrent._clvserver._clear();
RDebugUtils.currentLine=2949123;
 //BA.debugLineNum = 2949123;BA.debugLine="Starter.serverList.Initialize";
mostCurrent._starter._serverlist /*anywheresoftware.b4a.objects.collections.List*/ .Initialize();
RDebugUtils.currentLine=2949124;
 //BA.debugLineNum = 2949124;BA.debugLine="mqttBaseSubBord.Disconnect";
_mqttbasesubbord._disconnect /*String*/ (null);
RDebugUtils.currentLine=2949125;
 //BA.debugLineNum = 2949125;BA.debugLine="InitConnection";
_initconnection();
RDebugUtils.currentLine=2949127;
 //BA.debugLineNum = 2949127;BA.debugLine="End Sub";
return "";
}
public static String  _shownotconnectedtobroker() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "shownotconnectedtobroker", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "shownotconnectedtobroker", null));}
RDebugUtils.currentLine=1572864;
 //BA.debugLineNum = 1572864;BA.debugLine="Sub ShowNotConnectedToBroker";
RDebugUtils.currentLine=1572865;
 //BA.debugLineNum = 1572865;BA.debugLine="lblNoBord.Text = \"Verbinding borden verbroken\"";
mostCurrent._lblnobord.setText(BA.ObjectToCharSequence("Verbinding borden verbroken"));
RDebugUtils.currentLine=1572866;
 //BA.debugLineNum = 1572866;BA.debugLine="pnlNobords.SetVisibleAnimated(1000, True)";
mostCurrent._pnlnobords.SetVisibleAnimated((int) (1000),anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=1572867;
 //BA.debugLineNum = 1572867;BA.debugLine="mqttBaseSubBord.Disconnect";
_mqttbasesubbord._disconnect /*String*/ (null);
RDebugUtils.currentLine=1572868;
 //BA.debugLineNum = 1572868;BA.debugLine="End Sub";
return "";
}
public static String  _startselectedlocation() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "startselectedlocation", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "startselectedlocation", null));}
nl.pdeg.bordondroid.main._locationbord _loc = null;
RDebugUtils.currentLine=1114112;
 //BA.debugLineNum = 1114112;BA.debugLine="Public Sub StartSelectedLocation";
RDebugUtils.currentLine=1114113;
 //BA.debugLineNum = 1114113;BA.debugLine="Dim loc As locationBord";
_loc = new nl.pdeg.bordondroid.main._locationbord();
RDebugUtils.currentLine=1114114;
 //BA.debugLineNum = 1114114;BA.debugLine="loc.Initialize";
_loc.Initialize();
RDebugUtils.currentLine=1114115;
 //BA.debugLineNum = 1114115;BA.debugLine="loc = baseList.Get(0)";
_loc = (nl.pdeg.bordondroid.main._locationbord)(_baselist.Get((int) (0)));
RDebugUtils.currentLine=1114118;
 //BA.debugLineNum = 1114118;BA.debugLine="lblCurrLocation.Text = loc.description";
mostCurrent._lblcurrlocation.setText(BA.ObjectToCharSequence(_loc.description /*String*/ ));
RDebugUtils.currentLine=1114119;
 //BA.debugLineNum = 1114119;BA.debugLine="InitConnection";
_initconnection();
RDebugUtils.currentLine=1114120;
 //BA.debugLineNum = 1114120;BA.debugLine="End Sub";
return "";
}
public static String  _timeout_tick() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "timeout_tick", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "timeout_tick", null));}
RDebugUtils.currentLine=851968;
 //BA.debugLineNum = 851968;BA.debugLine="Private Sub timeOut_Tick";
RDebugUtils.currentLine=851969;
 //BA.debugLineNum = 851969;BA.debugLine="timeOutSeconds.Enabled = False";
_timeoutseconds.setEnabled(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=851970;
 //BA.debugLineNum = 851970;BA.debugLine="timeOut.Enabled = False";
_timeout.setEnabled(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=851971;
 //BA.debugLineNum = 851971;BA.debugLine="lblAliveIndicator.Visible = False";
mostCurrent._lblaliveindicator.setVisible(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=851972;
 //BA.debugLineNum = 851972;BA.debugLine="lblNoBord.Text = $\"Wachten op borden\"$";
mostCurrent._lblnobord.setText(BA.ObjectToCharSequence(("Wachten op borden")));
RDebugUtils.currentLine=851973;
 //BA.debugLineNum = 851973;BA.debugLine="Msgbox2Async(\"Geen borden gevonden of aktief\", Ap";
anywheresoftware.b4a.keywords.Common.Msgbox2Async(BA.ObjectToCharSequence("Geen borden gevonden of aktief"),BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Application.getLabelName()),"OKE","","",anywheresoftware.b4a.keywords.Common.Application.getIcon(),processBA,anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=851974;
 //BA.debugLineNum = 851974;BA.debugLine="If pnlNobords.Visible Then";
if (mostCurrent._pnlnobords.getVisible()) { 
RDebugUtils.currentLine=851975;
 //BA.debugLineNum = 851975;BA.debugLine="pnlNobords.SetVisibleAnimated(1500, False)";
mostCurrent._pnlnobords.SetVisibleAnimated((int) (1500),anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=851976;
 //BA.debugLineNum = 851976;BA.debugLine="DisconnectMqtt";
_disconnectmqtt();
 };
RDebugUtils.currentLine=851978;
 //BA.debugLineNum = 851978;BA.debugLine="End Sub";
return "";
}
public static void  _timeoutseconds_tick() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "timeoutseconds_tick", false))
	 {Debug.delegate(mostCurrent.activityBA, "timeoutseconds_tick", null); return;}
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
RDebugUtils.currentModule="main";

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
RDebugUtils.currentLine=786433;
 //BA.debugLineNum = 786433;BA.debugLine="Dim dispSeconds As String";
_dispseconds = "";
RDebugUtils.currentLine=786435;
 //BA.debugLineNum = 786435;BA.debugLine="secondsLeft = secondsLeft -1";
parent._secondsleft = (int) (parent._secondsleft-1);
RDebugUtils.currentLine=786436;
 //BA.debugLineNum = 786436;BA.debugLine="If secondsLeft <= 0 Then";
if (true) break;

case 1:
//if
this.state = 4;
if (parent._secondsleft<=0) { 
this.state = 3;
}if (true) break;

case 3:
//C
this.state = 4;
RDebugUtils.currentLine=786437;
 //BA.debugLineNum = 786437;BA.debugLine="timeOut_Tick";
_timeout_tick();
RDebugUtils.currentLine=786438;
 //BA.debugLineNum = 786438;BA.debugLine="Return";
if (true) return ;
 if (true) break;
;
RDebugUtils.currentLine=786441;
 //BA.debugLineNum = 786441;BA.debugLine="If secondsLeft < 10 Then";

case 4:
//if
this.state = 9;
if (parent._secondsleft<10) { 
this.state = 6;
}else {
this.state = 8;
}if (true) break;

case 6:
//C
this.state = 9;
RDebugUtils.currentLine=786442;
 //BA.debugLineNum = 786442;BA.debugLine="dispSeconds = $\"0${secondsLeft}\"$";
_dispseconds = ("0"+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(parent._secondsleft))+"");
 if (true) break;

case 8:
//C
this.state = 9;
RDebugUtils.currentLine=786444;
 //BA.debugLineNum = 786444;BA.debugLine="dispSeconds = $\"${secondsLeft}\"$";
_dispseconds = (""+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(parent._secondsleft))+"");
 if (true) break;

case 9:
//C
this.state = -1;
;
RDebugUtils.currentLine=786447;
 //BA.debugLineNum = 786447;BA.debugLine="lblNoBord.Text = $\"Wachten op borden (${dispSecon";
parent.mostCurrent._lblnobord.setText(BA.ObjectToCharSequence(("Wachten op borden ("+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_dispseconds))+")")));
RDebugUtils.currentLine=786448;
 //BA.debugLineNum = 786448;BA.debugLine="Sleep(0)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "main", "timeoutseconds_tick"),(int) (0));
this.state = 10;
return;
case 10:
//C
this.state = -1;
;
RDebugUtils.currentLine=786449;
 //BA.debugLineNum = 786449;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static String  _tmrbordalive_tick() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "tmrbordalive_tick", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "tmrbordalive_tick", null));}
RDebugUtils.currentLine=1376256;
 //BA.debugLineNum = 1376256;BA.debugLine="Sub tmrBordAlive_Tick";
RDebugUtils.currentLine=1376257;
 //BA.debugLineNum = 1376257;BA.debugLine="If Not(checkConnectTime) Then Return";
if (anywheresoftware.b4a.keywords.Common.Not(_checkconnecttime())) { 
if (true) return "";};
RDebugUtils.currentLine=1376258;
 //BA.debugLineNum = 1376258;BA.debugLine="CheckLastAliveTime";
_checklastalivetime();
RDebugUtils.currentLine=1376259;
 //BA.debugLineNum = 1376259;BA.debugLine="End Sub";
return "";
}
}