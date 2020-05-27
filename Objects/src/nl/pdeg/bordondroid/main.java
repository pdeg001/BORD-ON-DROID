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
vis = vis | (locations.mostCurrent != null);
vis = vis | (selectlocation.mostCurrent != null);
vis = vis | (serverboard.mostCurrent != null);
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

BA.applicationContext.stopService(new android.content.Intent(BA.applicationContext, starter.class));
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
public void Initialize() {
IsInitialized = true;
ip = "";
name = "";
timeStamp = 0L;
alive = false;
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
public static nl.pdeg.bordondroid.mqttconnector _mqttbase = null;
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
public b4a.example.dateutils _dateutils = null;
public nl.pdeg.bordondroid.locations _locations = null;
public nl.pdeg.bordondroid.starter _starter = null;
public nl.pdeg.bordondroid.selectlocation _selectlocation = null;
public nl.pdeg.bordondroid.serverboard _serverboard = null;
public static String  _activity_create(boolean _firsttime) throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "activity_create", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "activity_create", new Object[] {_firsttime}));}
RDebugUtils.currentLine=131072;
 //BA.debugLineNum = 131072;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
RDebugUtils.currentLine=131073;
 //BA.debugLineNum = 131073;BA.debugLine="Activity.LoadLayout(\"main\")";
mostCurrent._activity.LoadLayout("main",mostCurrent.activityBA);
RDebugUtils.currentLine=131075;
 //BA.debugLineNum = 131075;BA.debugLine="mqttBase.Initialize";
_mqttbase._initialize /*String*/ (null,processBA);
RDebugUtils.currentLine=131077;
 //BA.debugLineNum = 131077;BA.debugLine="Starter.appVersion = $\"${Application.LabelName} v";
mostCurrent._starter._appversion /*String*/  = (""+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(anywheresoftware.b4a.keywords.Common.Application.getLabelName()))+" v"+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(anywheresoftware.b4a.keywords.Common.Application.getVersionName()))+"");
RDebugUtils.currentLine=131078;
 //BA.debugLineNum = 131078;BA.debugLine="lblVersion.Text = Starter.appVersion";
mostCurrent._lblversion.setText(BA.ObjectToCharSequence(mostCurrent._starter._appversion /*String*/ ));
RDebugUtils.currentLine=131079;
 //BA.debugLineNum = 131079;BA.debugLine="baseFile.Initialize";
_basefile._initialize /*String*/ (null,processBA);
RDebugUtils.currentLine=131080;
 //BA.debugLineNum = 131080;BA.debugLine="pnlNobords.Visible = False";
mostCurrent._pnlnobords.setVisible(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=131081;
 //BA.debugLineNum = 131081;BA.debugLine="End Sub";
return "";
}
public static boolean  _activity_keypress(int _keycode) throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "activity_keypress", false))
	 {return ((Boolean) Debug.delegate(mostCurrent.activityBA, "activity_keypress", new Object[] {_keycode}));}
RDebugUtils.currentLine=19333120;
 //BA.debugLineNum = 19333120;BA.debugLine="Private Sub Activity_KeyPress(KeyCode As Int) As B";
RDebugUtils.currentLine=19333121;
 //BA.debugLineNum = 19333121;BA.debugLine="If KeyCode = KeyCodes.KEYCODE_BACK Then";
if (_keycode==anywheresoftware.b4a.keywords.Common.KeyCodes.KEYCODE_BACK) { 
RDebugUtils.currentLine=19333122;
 //BA.debugLineNum = 19333122;BA.debugLine="tmrBordLastAlive.Enabled = False";
_tmrbordlastalive.setEnabled(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=19333123;
 //BA.debugLineNum = 19333123;BA.debugLine="DisconnectMqtt";
_disconnectmqtt();
RDebugUtils.currentLine=19333124;
 //BA.debugLineNum = 19333124;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
RDebugUtils.currentLine=19333126;
 //BA.debugLineNum = 19333126;BA.debugLine="Return False";
if (true) return anywheresoftware.b4a.keywords.Common.False;
 }else {
RDebugUtils.currentLine=19333128;
 //BA.debugLineNum = 19333128;BA.debugLine="Return False";
if (true) return anywheresoftware.b4a.keywords.Common.False;
 };
RDebugUtils.currentLine=19333130;
 //BA.debugLineNum = 19333130;BA.debugLine="End Sub";
return false;
}
public static String  _disconnectmqtt() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "disconnectmqtt", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "disconnectmqtt", null));}
RDebugUtils.currentLine=19398656;
 //BA.debugLineNum = 19398656;BA.debugLine="Sub DisconnectMqtt";
RDebugUtils.currentLine=19398657;
 //BA.debugLineNum = 19398657;BA.debugLine="If mqttBase.connected Then";
if (_mqttbase._connected /*boolean*/ ) { 
RDebugUtils.currentLine=19398658;
 //BA.debugLineNum = 19398658;BA.debugLine="mqttBase.Disconnect";
_mqttbase._disconnect /*String*/ (null);
 };
RDebugUtils.currentLine=19398660;
 //BA.debugLineNum = 19398660;BA.debugLine="If CheckClientConnected Then";
if (_checkclientconnected()) { 
RDebugUtils.currentLine=19398661;
 //BA.debugLineNum = 19398661;BA.debugLine="mqttBase.Disconnect";
_mqttbase._disconnect /*String*/ (null);
 };
RDebugUtils.currentLine=19398664;
 //BA.debugLineNum = 19398664;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
RDebugUtils.currentModule="main";
RDebugUtils.currentLine=262144;
 //BA.debugLineNum = 262144;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
RDebugUtils.currentLine=262146;
 //BA.debugLineNum = 262146;BA.debugLine="Starter.mainPaused = True";
mostCurrent._starter._mainpaused /*boolean*/  = anywheresoftware.b4a.keywords.Common.True;
RDebugUtils.currentLine=262147;
 //BA.debugLineNum = 262147;BA.debugLine="ResumeConnection(False)";
_resumeconnection(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=262148;
 //BA.debugLineNum = 262148;BA.debugLine="mqttBase.Disconnect";
_mqttbase._disconnect /*String*/ (null);
RDebugUtils.currentLine=262149;
 //BA.debugLineNum = 262149;BA.debugLine="End Sub";
return "";
}
public static String  _resumeconnection(boolean _resume) throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "resumeconnection", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "resumeconnection", new Object[] {_resume}));}
RDebugUtils.currentLine=19267584;
 //BA.debugLineNum = 19267584;BA.debugLine="Sub ResumeConnection(resume As Boolean)";
RDebugUtils.currentLine=19267585;
 //BA.debugLineNum = 19267585;BA.debugLine="tmrBordLastAlive.Enabled = resume";
_tmrbordlastalive.setEnabled(_resume);
RDebugUtils.currentLine=19267586;
 //BA.debugLineNum = 19267586;BA.debugLine="End Sub";
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
RDebugUtils.currentLine=196610;
 //BA.debugLineNum = 196610;BA.debugLine="If Starter.mainPaused Then";
if (mostCurrent._starter._mainpaused /*boolean*/ ) { 
RDebugUtils.currentLine=196611;
 //BA.debugLineNum = 196611;BA.debugLine="ResumeConnection(True)";
_resumeconnection(anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=196612;
 //BA.debugLineNum = 196612;BA.debugLine="Starter.mainPaused = False";
mostCurrent._starter._mainpaused /*boolean*/  = anywheresoftware.b4a.keywords.Common.False;
RDebugUtils.currentLine=196613;
 //BA.debugLineNum = 196613;BA.debugLine="Starter.pingMqtt = True";
mostCurrent._starter._pingmqtt /*boolean*/  = anywheresoftware.b4a.keywords.Common.True;
RDebugUtils.currentLine=196615;
 //BA.debugLineNum = 196615;BA.debugLine="mqttBase.Connect";
_mqttbase._connect /*String*/ (null);
 }else {
 };
RDebugUtils.currentLine=196619;
 //BA.debugLineNum = 196619;BA.debugLine="End Sub";
return "";
}
public static String  _showselectlocationbutton() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "showselectlocationbutton", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "showselectlocationbutton", null));}
RDebugUtils.currentLine=20447232;
 //BA.debugLineNum = 20447232;BA.debugLine="Sub ShowSelectLocationButton";
RDebugUtils.currentLine=20447233;
 //BA.debugLineNum = 20447233;BA.debugLine="pnlLocationList.Visible = baseFile.GetBase.Size >";
mostCurrent._pnllocationlist.setVisible(_basefile._getbase /*anywheresoftware.b4a.objects.collections.List*/ (null).getSize()>1);
RDebugUtils.currentLine=20447234;
 //BA.debugLineNum = 20447234;BA.debugLine="End Sub";
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
RDebugUtils.currentLine=19595265;
 //BA.debugLineNum = 19595265;BA.debugLine="Dim bordStatus As bordStatus";
_bordstatus = new nl.pdeg.bordondroid.main._bordstatus();
RDebugUtils.currentLine=19595266;
 //BA.debugLineNum = 19595266;BA.debugLine="bordStatus.Initialize";
_bordstatus.Initialize();
RDebugUtils.currentLine=19595267;
 //BA.debugLineNum = 19595267;BA.debugLine="bordStatus.ip = ip";
_bordstatus.ip /*String*/  = _ip;
RDebugUtils.currentLine=19595268;
 //BA.debugLineNum = 19595268;BA.debugLine="bordStatus.name = name";
_bordstatus.name /*String*/  = _name;
RDebugUtils.currentLine=19595269;
 //BA.debugLineNum = 19595269;BA.debugLine="bordStatus.timeStamp = DateTime.Now";
_bordstatus.timeStamp /*long*/  = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
RDebugUtils.currentLine=19595270;
 //BA.debugLineNum = 19595270;BA.debugLine="bordStatus.alive = True";
_bordstatus.alive /*boolean*/  = anywheresoftware.b4a.keywords.Common.True;
RDebugUtils.currentLine=19595272;
 //BA.debugLineNum = 19595272;BA.debugLine="Starter.serverList.Add(bordStatus)";
parent.mostCurrent._starter._serverlist /*anywheresoftware.b4a.objects.collections.List*/ .Add((Object)(_bordstatus));
RDebugUtils.currentLine=19595273;
 //BA.debugLineNum = 19595273;BA.debugLine="CheckIpInClv(bordStatus, players)";
_checkipinclv(_bordstatus,_players);
RDebugUtils.currentLine=19595274;
 //BA.debugLineNum = 19595274;BA.debugLine="baseFile.SetBordDiedByName(name, clvServer, True)";
parent._basefile._setborddiedbyname /*String*/ (null,_name,parent.mostCurrent._clvserver,anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=19595276;
 //BA.debugLineNum = 19595276;BA.debugLine="If clvServer.GetSize > 0 Then";
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
RDebugUtils.currentLine=19595277;
 //BA.debugLineNum = 19595277;BA.debugLine="pnlNobords.SetVisibleAnimated(500, False)";
parent.mostCurrent._pnlnobords.SetVisibleAnimated((int) (500),anywheresoftware.b4a.keywords.Common.False);
 if (true) break;

case 5:
//C
this.state = 6;
RDebugUtils.currentLine=19595279;
 //BA.debugLineNum = 19595279;BA.debugLine="pnlNobords.SetVisibleAnimated(500, True)";
parent.mostCurrent._pnlnobords.SetVisibleAnimated((int) (500),anywheresoftware.b4a.keywords.Common.True);
 if (true) break;

case 6:
//C
this.state = -1;
;
RDebugUtils.currentLine=19595283;
 //BA.debugLineNum = 19595283;BA.debugLine="Sleep(400)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "main", "addunkownip"),(int) (400));
this.state = 7;
return;
case 7:
//C
this.state = -1;
;
RDebugUtils.currentLine=19595285;
 //BA.debugLineNum = 19595285;BA.debugLine="End Sub";
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
RDebugUtils.currentLine=19660800;
 //BA.debugLineNum = 19660800;BA.debugLine="Sub CheckIpInClv(bord As bordStatus, players As St";
RDebugUtils.currentLine=19660801;
 //BA.debugLineNum = 19660801;BA.debugLine="Dim p As Panel";
_p = new anywheresoftware.b4a.objects.PanelWrapper();
RDebugUtils.currentLine=19660804;
 //BA.debugLineNum = 19660804;BA.debugLine="For i = 0 To clvServer.GetSize-1";
{
final int step2 = 1;
final int limit2 = (int) (mostCurrent._clvserver._getsize()-1);
_i = (int) (0) ;
for (;_i <= limit2 ;_i = _i + step2 ) {
RDebugUtils.currentLine=19660805;
 //BA.debugLineNum = 19660805;BA.debugLine="p = clvServer.GetPanel(i)";
_p.setObject((android.view.ViewGroup)(mostCurrent._clvserver._getpanel(_i).getObject()));
RDebugUtils.currentLine=19660806;
 //BA.debugLineNum = 19660806;BA.debugLine="If p.Tag = bord.name Then";
if ((_p.getTag()).equals((Object)(_bord.name /*String*/ ))) { 
RDebugUtils.currentLine=19660807;
 //BA.debugLineNum = 19660807;BA.debugLine="baseFile.SetPanelLabelItemText(p, \"lblLastCheck";
_basefile._setpanellabelitemtext /*String*/ (null,_p,"lblLastCheck",("Laatste controle "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("time",(Object)(anywheresoftware.b4a.keywords.Common.DateTime.getNow()))+""));
RDebugUtils.currentLine=19660808;
 //BA.debugLineNum = 19660808;BA.debugLine="baseFile.SetBordDiedByName(bord.name, clvServer";
_basefile._setborddiedbyname /*String*/ (null,_bord.name /*String*/ ,mostCurrent._clvserver,_bord.alive /*boolean*/ );
 };
 }
};
RDebugUtils.currentLine=19660813;
 //BA.debugLineNum = 19660813;BA.debugLine="pnlNobords.SetVisibleAnimated(1000, False)";
mostCurrent._pnlnobords.SetVisibleAnimated((int) (1000),anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=19660814;
 //BA.debugLineNum = 19660814;BA.debugLine="clvServer.Add(genUnitList(bord.name, bord.alive,";
mostCurrent._clvserver._add((anywheresoftware.b4a.objects.B4XViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.B4XViewWrapper(), (java.lang.Object)(_genunitlist(_bord.name /*String*/ ,_bord.alive /*boolean*/ ,_players).getObject())),(Object)(""));
RDebugUtils.currentLine=19660815;
 //BA.debugLineNum = 19660815;BA.debugLine="End Sub";
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
RDebugUtils.currentLine=19857409;
 //BA.debugLineNum = 19857409;BA.debugLine="Msgbox2Async(\"Applicatie afsluiten?\", Application";
anywheresoftware.b4a.keywords.Common.Msgbox2Async(BA.ObjectToCharSequence("Applicatie afsluiten?"),BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Application.getLabelName()),"JA","","NEE",anywheresoftware.b4a.keywords.Common.Application.getIcon(),processBA,anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=19857410;
 //BA.debugLineNum = 19857410;BA.debugLine="Wait For Msgbox_Result(Result As Int)";
anywheresoftware.b4a.keywords.Common.WaitFor("msgbox_result", processBA, new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "main", "btncancel_click"), null);
this.state = 5;
return;
case 5:
//C
this.state = 1;
_result = (Integer) result[0];
;
RDebugUtils.currentLine=19857411;
 //BA.debugLineNum = 19857411;BA.debugLine="If Result = DialogResponse.POSITIVE Then";
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
RDebugUtils.currentLine=19857412;
 //BA.debugLineNum = 19857412;BA.debugLine="Activity.Finish";
parent.mostCurrent._activity.Finish();
 if (true) break;

case 4:
//C
this.state = -1;
;
RDebugUtils.currentLine=19857414;
 //BA.debugLineNum = 19857414;BA.debugLine="End Sub";
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
RDebugUtils.currentLine=19922945;
 //BA.debugLineNum = 19922945;BA.debugLine="If edtFloatCode.Text = \"\" Then";
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
RDebugUtils.currentLine=19922946;
 //BA.debugLineNum = 19922946;BA.debugLine="Msgbox2Async(\"Locatie code mag niet leeg zijn\",";
anywheresoftware.b4a.keywords.Common.Msgbox2Async(BA.ObjectToCharSequence("Locatie code mag niet leeg zijn"),BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Application.getLabelName()),"OKE","","",anywheresoftware.b4a.keywords.Common.Application.getIcon(),processBA,anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=19922947;
 //BA.debugLineNum = 19922947;BA.debugLine="Wait For Msgbox_Result(Result As Int)";
anywheresoftware.b4a.keywords.Common.WaitFor("msgbox_result", processBA, new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "main", "btnok_click"), null);
this.state = 17;
return;
case 17:
//C
this.state = 4;
_result = (Integer) result[0];
;
RDebugUtils.currentLine=19922948;
 //BA.debugLineNum = 19922948;BA.debugLine="Return";
if (true) return ;
 if (true) break;

case 4:
//C
this.state = 5;
;
RDebugUtils.currentLine=19922951;
 //BA.debugLineNum = 19922951;BA.debugLine="Dim ime As IME";
_ime = new anywheresoftware.b4a.objects.IME();
RDebugUtils.currentLine=19922952;
 //BA.debugLineNum = 19922952;BA.debugLine="ime.Initialize(Me)";
_ime.Initialize(BA.ObjectToString(main.getObject()));
RDebugUtils.currentLine=19922953;
 //BA.debugLineNum = 19922953;BA.debugLine="Starter.testBaseName = True";
parent.mostCurrent._starter._testbasename /*boolean*/  = anywheresoftware.b4a.keywords.Common.True;
RDebugUtils.currentLine=19922954;
 //BA.debugLineNum = 19922954;BA.debugLine="Starter.selectedLocationCode = edtFloatCode.Text";
parent.mostCurrent._starter._selectedlocationcode /*String*/  = parent.mostCurrent._edtfloatcode._gettext /*String*/ (null);
RDebugUtils.currentLine=19922956;
 //BA.debugLineNum = 19922956;BA.debugLine="Starter.SetLastWill(\"bordpubdied\")";
parent.mostCurrent._starter._setlastwill /*String*/ ("bordpubdied");
RDebugUtils.currentLine=19922958;
 //BA.debugLineNum = 19922958;BA.debugLine="Starter.SetSubBase(Starter.selectedLocationCode)";
parent.mostCurrent._starter._setsubbase /*String*/ (parent.mostCurrent._starter._selectedlocationcode /*String*/ );
RDebugUtils.currentLine=19922960;
 //BA.debugLineNum = 19922960;BA.debugLine="Starter.SetSubString2(\"/pubbord\")";
parent.mostCurrent._starter._setsubstring2 /*String*/ ("/pubbord");
RDebugUtils.currentLine=19922962;
 //BA.debugLineNum = 19922962;BA.debugLine="Starter.SetUnsubscribeString2(\"\")";
parent.mostCurrent._starter._setunsubscribestring2 /*String*/ ("");
RDebugUtils.currentLine=19922963;
 //BA.debugLineNum = 19922963;BA.debugLine="mqttBase.Initialize";
parent._mqttbase._initialize /*String*/ (null,processBA);
RDebugUtils.currentLine=19922964;
 //BA.debugLineNum = 19922964;BA.debugLine="Sleep(100)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "main", "btnok_click"),(int) (100));
this.state = 18;
return;
case 18:
//C
this.state = 5;
;
RDebugUtils.currentLine=19922965;
 //BA.debugLineNum = 19922965;BA.debugLine="mqttBase.Connect";
parent._mqttbase._connect /*String*/ (null);
RDebugUtils.currentLine=19922967;
 //BA.debugLineNum = 19922967;BA.debugLine="Sleep(1000)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "main", "btnok_click"),(int) (1000));
this.state = 19;
return;
case 19:
//C
this.state = 5;
;
RDebugUtils.currentLine=19922969;
 //BA.debugLineNum = 19922969;BA.debugLine="If mqttBase.connected = False Then";
if (true) break;

case 5:
//if
this.state = 16;
if (parent._mqttbase._connected /*boolean*/ ==anywheresoftware.b4a.keywords.Common.False) { 
this.state = 7;
}else {
this.state = 9;
}if (true) break;

case 7:
//C
this.state = 16;
RDebugUtils.currentLine=19922970;
 //BA.debugLineNum = 19922970;BA.debugLine="Msgbox2Async(\"Kan geen verbinding maken met loca";
anywheresoftware.b4a.keywords.Common.Msgbox2Async(BA.ObjectToCharSequence("Kan geen verbinding maken met locatie"),BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Application.getLabelName()),"OKE","","",anywheresoftware.b4a.keywords.Common.Application.getIcon(),processBA,anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=19922971;
 //BA.debugLineNum = 19922971;BA.debugLine="Wait For Msgbox_Result(Result As Int)";
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
RDebugUtils.currentLine=19922973;
 //BA.debugLineNum = 19922973;BA.debugLine="Dim code, description As String";
_code = "";
_description = "";
RDebugUtils.currentLine=19922974;
 //BA.debugLineNum = 19922974;BA.debugLine="lblCurrLocation.Text = edtFloatDescription.Text";
parent.mostCurrent._lblcurrlocation.setText(BA.ObjectToCharSequence(parent.mostCurrent._edtfloatdescription._gettext /*String*/ (null)));
RDebugUtils.currentLine=19922975;
 //BA.debugLineNum = 19922975;BA.debugLine="Starter.selectedLocationDescription = edtFloatDe";
parent.mostCurrent._starter._selectedlocationdescription /*String*/  = parent.mostCurrent._edtfloatdescription._gettext /*String*/ (null);
RDebugUtils.currentLine=19922976;
 //BA.debugLineNum = 19922976;BA.debugLine="code = edtFloatCode.Text";
_code = parent.mostCurrent._edtfloatcode._gettext /*String*/ (null);
RDebugUtils.currentLine=19922977;
 //BA.debugLineNum = 19922977;BA.debugLine="If edtFloatDescription.Text = \"\" Then edtFloatDe";
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
RDebugUtils.currentLine=19922978;
 //BA.debugLineNum = 19922978;BA.debugLine="description = edtFloatDescription.Text";
_description = parent.mostCurrent._edtfloatdescription._gettext /*String*/ (null);
RDebugUtils.currentLine=19922981;
 //BA.debugLineNum = 19922981;BA.debugLine="mqttBase.Disconnect";
parent._mqttbase._disconnect /*String*/ (null);
RDebugUtils.currentLine=19922982;
 //BA.debugLineNum = 19922982;BA.debugLine="pnlLocationCOde.Visible = False";
parent.mostCurrent._pnllocationcode.setVisible(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=19922983;
 //BA.debugLineNum = 19922983;BA.debugLine="baseFile.SetBase(code, description, \"1\")";
parent._basefile._setbase /*String*/ (null,_code,_description,"1");
RDebugUtils.currentLine=19922984;
 //BA.debugLineNum = 19922984;BA.debugLine="InitConnection";
_initconnection();
RDebugUtils.currentLine=19922985;
 //BA.debugLineNum = 19922985;BA.debugLine="ime.HideKeyboard";
_ime.HideKeyboard(mostCurrent.activityBA);
 if (true) break;

case 16:
//C
this.state = -1;
;
RDebugUtils.currentLine=19922987;
 //BA.debugLineNum = 19922987;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static void  _initconnection() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "initconnection", false))
	 {Debug.delegate(mostCurrent.activityBA, "initconnection", null); return;}
ResumableSub_InitConnection rsub = new ResumableSub_InitConnection(null);
rsub.resume(processBA, null);
}
public static class ResumableSub_InitConnection extends BA.ResumableSub {
public ResumableSub_InitConnection(nl.pdeg.bordondroid.main parent) {
this.parent = parent;
}
nl.pdeg.bordondroid.main parent;

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
RDebugUtils.currentLine=18874369;
 //BA.debugLineNum = 18874369;BA.debugLine="If mqttBase.connected Then mqttBase.Disconnect";
if (true) break;

case 1:
//if
this.state = 6;
if (parent._mqttbase._connected /*boolean*/ ) { 
this.state = 3;
;}if (true) break;

case 3:
//C
this.state = 6;
parent._mqttbase._disconnect /*String*/ (null);
if (true) break;

case 6:
//C
this.state = -1;
;
RDebugUtils.currentLine=18874370;
 //BA.debugLineNum = 18874370;BA.debugLine="Starter.serverList.Initialize";
parent.mostCurrent._starter._serverlist /*anywheresoftware.b4a.objects.collections.List*/ .Initialize();
RDebugUtils.currentLine=18874371;
 //BA.debugLineNum = 18874371;BA.debugLine="clvServer.Clear";
parent.mostCurrent._clvserver._clear();
RDebugUtils.currentLine=18874372;
 //BA.debugLineNum = 18874372;BA.debugLine="lblCurrLocation.Text = Starter.selectedLocationDe";
parent.mostCurrent._lblcurrlocation.setText(BA.ObjectToCharSequence(parent.mostCurrent._starter._selectedlocationdescription /*String*/ ));
RDebugUtils.currentLine=18874373;
 //BA.debugLineNum = 18874373;BA.debugLine="CallSub2(Starter, \"SetLastWill\", \"bordpubdied\")";
anywheresoftware.b4a.keywords.Common.CallSubDebug2(processBA,(Object)(parent.mostCurrent._starter.getObject()),"SetLastWill",(Object)("bordpubdied"));
RDebugUtils.currentLine=18874374;
 //BA.debugLineNum = 18874374;BA.debugLine="CallSub2(Starter, \"SetSubBase\", Starter.selectedL";
anywheresoftware.b4a.keywords.Common.CallSubDebug2(processBA,(Object)(parent.mostCurrent._starter.getObject()),"SetSubBase",(Object)(parent.mostCurrent._starter._selectedlocationcode /*String*/ ));
RDebugUtils.currentLine=18874375;
 //BA.debugLineNum = 18874375;BA.debugLine="CallSub2(Starter, \"SetSubString2\", \"/pubbord\")";
anywheresoftware.b4a.keywords.Common.CallSubDebug2(processBA,(Object)(parent.mostCurrent._starter.getObject()),"SetSubString2",(Object)("/pubbord"));
RDebugUtils.currentLine=18874376;
 //BA.debugLineNum = 18874376;BA.debugLine="CallSub2(Starter, \"SetUnsubscribeString2\", \"\")";
anywheresoftware.b4a.keywords.Common.CallSubDebug2(processBA,(Object)(parent.mostCurrent._starter.getObject()),"SetUnsubscribeString2",(Object)(""));
RDebugUtils.currentLine=18874377;
 //BA.debugLineNum = 18874377;BA.debugLine="Sleep(0)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "main", "initconnection"),(int) (0));
this.state = 7;
return;
case 7:
//C
this.state = -1;
;
RDebugUtils.currentLine=18874378;
 //BA.debugLineNum = 18874378;BA.debugLine="StartConnection";
_startconnection();
RDebugUtils.currentLine=18874379;
 //BA.debugLineNum = 18874379;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static boolean  _checkclientconnected() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "checkclientconnected", false))
	 {return ((Boolean) Debug.delegate(mostCurrent.activityBA, "checkclientconnected", null));}
RDebugUtils.currentLine=20119552;
 //BA.debugLineNum = 20119552;BA.debugLine="Private Sub CheckClientConnected As Boolean";
RDebugUtils.currentLine=20119553;
 //BA.debugLineNum = 20119553;BA.debugLine="Return CallSub(mqttBase, \"GetClientConnected\")";
if (true) return BA.ObjectToBoolean(anywheresoftware.b4a.keywords.Common.CallSubDebug(processBA,(Object)(_mqttbase),"GetClientConnected"));
RDebugUtils.currentLine=20119554;
 //BA.debugLineNum = 20119554;BA.debugLine="End Sub";
return false;
}
public static boolean  _checkconnecttime() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "checkconnecttime", false))
	 {return ((Boolean) Debug.delegate(mostCurrent.activityBA, "checkconnecttime", null));}
RDebugUtils.currentLine=19070976;
 //BA.debugLineNum = 19070976;BA.debugLine="Sub checkConnectTime As Boolean";
RDebugUtils.currentLine=19070977;
 //BA.debugLineNum = 19070977;BA.debugLine="If connectTime <> -1 Then";
if (_connecttime!=-1) { 
RDebugUtils.currentLine=19070978;
 //BA.debugLineNum = 19070978;BA.debugLine="pnlNobords.Visible = True";
mostCurrent._pnlnobords.setVisible(anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=19070979;
 //BA.debugLineNum = 19070979;BA.debugLine="mqttBase.Disconnect";
_mqttbase._disconnect /*String*/ (null);
RDebugUtils.currentLine=19070980;
 //BA.debugLineNum = 19070980;BA.debugLine="tmrBordLastAlive.Enabled = False";
_tmrbordlastalive.setEnabled(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=19070981;
 //BA.debugLineNum = 19070981;BA.debugLine="pnlNobords.Visible = False";
mostCurrent._pnlnobords.setVisible(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=19070982;
 //BA.debugLineNum = 19070982;BA.debugLine="Msgbox2Async(\"Geen borden gevonden\", Application";
anywheresoftware.b4a.keywords.Common.Msgbox2Async(BA.ObjectToCharSequence("Geen borden gevonden"),BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Application.getLabelName()),"OKE","","",anywheresoftware.b4a.keywords.Common.Application.getIcon(),processBA,anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=19070983;
 //BA.debugLineNum = 19070983;BA.debugLine="Return False";
if (true) return anywheresoftware.b4a.keywords.Common.False;
 };
RDebugUtils.currentLine=19070985;
 //BA.debugLineNum = 19070985;BA.debugLine="Return True";
if (true) return anywheresoftware.b4a.keywords.Common.True;
RDebugUtils.currentLine=19070986;
 //BA.debugLineNum = 19070986;BA.debugLine="End Sub";
return false;
}
public static String  _checkipexists(nl.pdeg.bordondroid.main._message _bord) throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "checkipexists", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "checkipexists", new Object[] {_bord}));}
boolean _bordexists = false;
String _strplayers = "";
String _name = "";
String[] _strdata = null;
nl.pdeg.bordondroid.main._bordstatus _lst = null;
RDebugUtils.currentLine=19529728;
 //BA.debugLineNum = 19529728;BA.debugLine="Sub CheckIpExists(bord As Message)";
RDebugUtils.currentLine=19529729;
 //BA.debugLineNum = 19529729;BA.debugLine="Dim bordExists As Boolean = False";
_bordexists = anywheresoftware.b4a.keywords.Common.False;
RDebugUtils.currentLine=19529730;
 //BA.debugLineNum = 19529730;BA.debugLine="Dim strPlayers As String";
_strplayers = "";
RDebugUtils.currentLine=19529733;
 //BA.debugLineNum = 19529733;BA.debugLine="If bord.Body.Length = 0 Then Return";
if (_bord.Body /*String*/ .length()==0) { 
if (true) return "";};
RDebugUtils.currentLine=19529734;
 //BA.debugLineNum = 19529734;BA.debugLine="Dim name As String = bord.Body";
_name = _bord.Body /*String*/ ;
RDebugUtils.currentLine=19529738;
 //BA.debugLineNum = 19529738;BA.debugLine="strPlayers = name'baseFile.SetPlayertext(name)";
_strplayers = _name;
RDebugUtils.currentLine=19529739;
 //BA.debugLineNum = 19529739;BA.debugLine="If name.IndexOf(\"|\") > -1 Then";
if (_name.indexOf("|")>-1) { 
RDebugUtils.currentLine=19529740;
 //BA.debugLineNum = 19529740;BA.debugLine="Dim strData() As String = Regex.Split(\"\\|\", name";
_strdata = anywheresoftware.b4a.keywords.Common.Regex.Split("\\|",_name);
RDebugUtils.currentLine=19529741;
 //BA.debugLineNum = 19529741;BA.debugLine="name = strData(0)";
_name = _strdata[(int) (0)];
 };
RDebugUtils.currentLine=19529745;
 //BA.debugLineNum = 19529745;BA.debugLine="connectTime = -1";
_connecttime = (long) (-1);
RDebugUtils.currentLine=19529747;
 //BA.debugLineNum = 19529747;BA.debugLine="If Starter.serverList.Size = 0 Then";
if (mostCurrent._starter._serverlist /*anywheresoftware.b4a.objects.collections.List*/ .getSize()==0) { 
RDebugUtils.currentLine=19529749;
 //BA.debugLineNum = 19529749;BA.debugLine="AddUnkownIp(\"\", name, strPlayers)";
_addunkownip("",_name,_strplayers);
RDebugUtils.currentLine=19529750;
 //BA.debugLineNum = 19529750;BA.debugLine="Return";
if (true) return "";
 };
RDebugUtils.currentLine=19529753;
 //BA.debugLineNum = 19529753;BA.debugLine="For Each lst As bordStatus In Starter.serverList";
{
final anywheresoftware.b4a.BA.IterableList group15 = mostCurrent._starter._serverlist /*anywheresoftware.b4a.objects.collections.List*/ ;
final int groupLen15 = group15.getSize()
;int index15 = 0;
;
for (; index15 < groupLen15;index15++){
_lst = (nl.pdeg.bordondroid.main._bordstatus)(group15.Get(index15));
RDebugUtils.currentLine=19529754;
 //BA.debugLineNum = 19529754;BA.debugLine="If lst.name = name Then";
if ((_lst.name /*String*/ ).equals(_name)) { 
RDebugUtils.currentLine=19529755;
 //BA.debugLineNum = 19529755;BA.debugLine="bordExists = True";
_bordexists = anywheresoftware.b4a.keywords.Common.True;
RDebugUtils.currentLine=19529756;
 //BA.debugLineNum = 19529756;BA.debugLine="lst.timeStamp = DateTime.Now";
_lst.timeStamp /*long*/  = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
RDebugUtils.currentLine=19529757;
 //BA.debugLineNum = 19529757;BA.debugLine="lst.alive = True";
_lst.alive /*boolean*/  = anywheresoftware.b4a.keywords.Common.True;
RDebugUtils.currentLine=19529759;
 //BA.debugLineNum = 19529759;BA.debugLine="baseFile.CheckPlayers(strPlayers, name, clvServ";
_basefile._checkplayers /*String*/ (null,_strplayers,_name,mostCurrent._clvserver);
 };
 }
};
RDebugUtils.currentLine=19529763;
 //BA.debugLineNum = 19529763;BA.debugLine="If Not(bordExists) Then";
if (anywheresoftware.b4a.keywords.Common.Not(_bordexists)) { 
RDebugUtils.currentLine=19529764;
 //BA.debugLineNum = 19529764;BA.debugLine="AddUnkownIp(\"\", name, strPlayers)";
_addunkownip("",_name,_strplayers);
RDebugUtils.currentLine=19529765;
 //BA.debugLineNum = 19529765;BA.debugLine="Return";
if (true) return "";
 };
RDebugUtils.currentLine=19529767;
 //BA.debugLineNum = 19529767;BA.debugLine="End Sub";
return "";
}
public static anywheresoftware.b4a.objects.PanelWrapper  _genunitlist(String _name,boolean _alive,String _players) throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "genunitlist", false))
	 {return ((anywheresoftware.b4a.objects.PanelWrapper) Debug.delegate(mostCurrent.activityBA, "genunitlist", new Object[] {_name,_alive,_players}));}
anywheresoftware.b4a.objects.PanelWrapper _p = null;
RDebugUtils.currentLine=19464192;
 //BA.debugLineNum = 19464192;BA.debugLine="Sub genUnitList(name As String, alive As Boolean,";
RDebugUtils.currentLine=19464193;
 //BA.debugLineNum = 19464193;BA.debugLine="Dim p As Panel";
_p = new anywheresoftware.b4a.objects.PanelWrapper();
RDebugUtils.currentLine=19464194;
 //BA.debugLineNum = 19464194;BA.debugLine="p.Initialize(Me)";
_p.Initialize(mostCurrent.activityBA,BA.ObjectToString(main.getObject()));
RDebugUtils.currentLine=19464195;
 //BA.debugLineNum = 19464195;BA.debugLine="p.SetLayout(0dip, 0dip, clvServer.AsView.Width, 1";
_p.SetLayout(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)),mostCurrent._clvserver._asview().getWidth(),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (125)));
RDebugUtils.currentLine=19464196;
 //BA.debugLineNum = 19464196;BA.debugLine="p.LoadLayout(\"clvBorden\")";
_p.LoadLayout("clvBorden",mostCurrent.activityBA);
RDebugUtils.currentLine=19464197;
 //BA.debugLineNum = 19464197;BA.debugLine="p.Tag = name";
_p.setTag((Object)(_name));
RDebugUtils.currentLine=19464199;
 //BA.debugLineNum = 19464199;BA.debugLine="lblBordName.Text = name.Trim";
mostCurrent._lblbordname.setText(BA.ObjectToCharSequence(_name.trim()));
RDebugUtils.currentLine=19464200;
 //BA.debugLineNum = 19464200;BA.debugLine="lblLastCheck.Text = $\"Laatste controle $Time{Date";
mostCurrent._lbllastcheck.setText(BA.ObjectToCharSequence(("Laatste controle "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("time",(Object)(anywheresoftware.b4a.keywords.Common.DateTime.getNow()))+"")));
RDebugUtils.currentLine=19464202;
 //BA.debugLineNum = 19464202;BA.debugLine="lblPLayer.Text = baseFile.SetPlayertext(players)";
mostCurrent._lblplayer.setText(BA.ObjectToCharSequence(_basefile._setplayertext /*Object*/ (null,_players)));
RDebugUtils.currentLine=19464204;
 //BA.debugLineNum = 19464204;BA.debugLine="lblPLayer.Tag = \"players\"";
mostCurrent._lblplayer.setTag((Object)("players"));
RDebugUtils.currentLine=19464206;
 //BA.debugLineNum = 19464206;BA.debugLine="If Not(alive) Then";
if (anywheresoftware.b4a.keywords.Common.Not(_alive)) { 
 };
RDebugUtils.currentLine=19464209;
 //BA.debugLineNum = 19464209;BA.debugLine="Return p";
if (true) return _p;
RDebugUtils.currentLine=19464210;
 //BA.debugLineNum = 19464210;BA.debugLine="End Sub";
return null;
}
public static String  _checklastalivetime() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "checklastalivetime", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "checklastalivetime", null));}
long _currtime = 0L;
long _timediff = 0L;
int _clvindex = 0;
int _serverlistindex = 0;
nl.pdeg.bordondroid.main._bordstatus _bd = null;
RDebugUtils.currentLine=19136512;
 //BA.debugLineNum = 19136512;BA.debugLine="Sub CheckLastAliveTime";
RDebugUtils.currentLine=19136513;
 //BA.debugLineNum = 19136513;BA.debugLine="Dim currTime As Long = DateTime.Now";
_currtime = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
RDebugUtils.currentLine=19136514;
 //BA.debugLineNum = 19136514;BA.debugLine="Dim timeDiff As Long";
_timediff = 0L;
RDebugUtils.currentLine=19136515;
 //BA.debugLineNum = 19136515;BA.debugLine="Dim clvIndex, serverListIndex As Int";
_clvindex = 0;
_serverlistindex = 0;
RDebugUtils.currentLine=19136518;
 //BA.debugLineNum = 19136518;BA.debugLine="If lblLastCheck.IsInitialized = False Then";
if (mostCurrent._lbllastcheck.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
RDebugUtils.currentLine=19136519;
 //BA.debugLineNum = 19136519;BA.debugLine="lblNoBord.Visible = False";
mostCurrent._lblnobord.setVisible(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=19136520;
 //BA.debugLineNum = 19136520;BA.debugLine="Return";
if (true) return "";
 };
RDebugUtils.currentLine=19136523;
 //BA.debugLineNum = 19136523;BA.debugLine="lblLastCheck.Text = $\"Laatste controle $Time{Date";
mostCurrent._lbllastcheck.setText(BA.ObjectToCharSequence(("Laatste controle "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("time",(Object)(anywheresoftware.b4a.keywords.Common.DateTime.getNow()))+"")));
RDebugUtils.currentLine=19136525;
 //BA.debugLineNum = 19136525;BA.debugLine="For Each bd As bordStatus In Starter.serverList";
{
final anywheresoftware.b4a.BA.IterableList group9 = mostCurrent._starter._serverlist /*anywheresoftware.b4a.objects.collections.List*/ ;
final int groupLen9 = group9.getSize()
;int index9 = 0;
;
for (; index9 < groupLen9;index9++){
_bd = (nl.pdeg.bordondroid.main._bordstatus)(group9.Get(index9));
RDebugUtils.currentLine=19136526;
 //BA.debugLineNum = 19136526;BA.debugLine="timeDiff = currTime-bd.timeStamp";
_timediff = (long) (_currtime-_bd.timeStamp /*long*/ );
RDebugUtils.currentLine=19136528;
 //BA.debugLineNum = 19136528;BA.debugLine="If timeDiff >= Starter.serverDiedRemove Then";
if (_timediff>=mostCurrent._starter._serverdiedremove /*long*/ ) { 
RDebugUtils.currentLine=19136529;
 //BA.debugLineNum = 19136529;BA.debugLine="clvIndex = baseFile.GetPanelIndexFromBordName(b";
_clvindex = _basefile._getpanelindexfrombordname /*int*/ (null,_bd.name /*String*/ ,mostCurrent._clvserver);
RDebugUtils.currentLine=19136530;
 //BA.debugLineNum = 19136530;BA.debugLine="serverListIndex = baseFile.GetServerlistIndexFr";
_serverlistindex = _basefile._getserverlistindexfromname /*int*/ (null,_bd.name /*String*/ );
RDebugUtils.currentLine=19136531;
 //BA.debugLineNum = 19136531;BA.debugLine="If clvIndex <> -1 Then";
if (_clvindex!=-1) { 
RDebugUtils.currentLine=19136532;
 //BA.debugLineNum = 19136532;BA.debugLine="clvServer.RemoveAt(clvIndex)";
mostCurrent._clvserver._removeat(_clvindex);
 };
RDebugUtils.currentLine=19136534;
 //BA.debugLineNum = 19136534;BA.debugLine="If serverListIndex <> -1 Then";
if (_serverlistindex!=-1) { 
RDebugUtils.currentLine=19136535;
 //BA.debugLineNum = 19136535;BA.debugLine="Starter.serverList.RemoveAt(serverListIndex)";
mostCurrent._starter._serverlist /*anywheresoftware.b4a.objects.collections.List*/ .RemoveAt(_serverlistindex);
 };
RDebugUtils.currentLine=19136537;
 //BA.debugLineNum = 19136537;BA.debugLine="Return";
if (true) return "";
 };
RDebugUtils.currentLine=19136541;
 //BA.debugLineNum = 19136541;BA.debugLine="If timeDiff >= Starter.serverDied Then";
if (_timediff>=mostCurrent._starter._serverdied /*long*/ ) { 
RDebugUtils.currentLine=19136542;
 //BA.debugLineNum = 19136542;BA.debugLine="bd.alive = False";
_bd.alive /*boolean*/  = anywheresoftware.b4a.keywords.Common.False;
RDebugUtils.currentLine=19136543;
 //BA.debugLineNum = 19136543;BA.debugLine="baseFile.SetBordDiedByName(bd.name, clvServer,";
_basefile._setborddiedbyname /*String*/ (null,_bd.name /*String*/ ,mostCurrent._clvserver,_bd.alive /*boolean*/ );
 }else {
RDebugUtils.currentLine=19136545;
 //BA.debugLineNum = 19136545;BA.debugLine="baseFile.SetBordDiedByName(bd.name, clvServer,";
_basefile._setborddiedbyname /*String*/ (null,_bd.name /*String*/ ,mostCurrent._clvserver,_bd.alive /*boolean*/ );
 };
 }
};
RDebugUtils.currentLine=19136549;
 //BA.debugLineNum = 19136549;BA.debugLine="End Sub";
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
RDebugUtils.currentLine=18939905;
 //BA.debugLineNum = 18939905;BA.debugLine="Dim p As Panel = clvServer.GetPanel(Index)";
_p = new anywheresoftware.b4a.objects.PanelWrapper();
_p.setObject((android.view.ViewGroup)(parent.mostCurrent._clvserver._getpanel(_index).getObject()));
RDebugUtils.currentLine=18939906;
 //BA.debugLineNum = 18939906;BA.debugLine="Dim unit As String";
_unit = "";
RDebugUtils.currentLine=18939908;
 //BA.debugLineNum = 18939908;BA.debugLine="unit = baseFile.GetSelectedLabelTagFromPanel(p, \"";
_unit = parent._basefile._getselectedlabeltagfrompanel /*String*/ (null,_p,"name");
RDebugUtils.currentLine=18939909;
 //BA.debugLineNum = 18939909;BA.debugLine="If baseFile.GetBordAlive(unit) = False Then";
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
RDebugUtils.currentLine=18939910;
 //BA.debugLineNum = 18939910;BA.debugLine="baseFile.ShowCustomToast($\"Bord niet gevonden, c";
parent._basefile._showcustomtoast /*String*/ (null,(Object)(("Bord niet gevonden, controleer of het bord \"online\" is")),anywheresoftware.b4a.keywords.Common.False,anywheresoftware.b4a.keywords.Common.Colors.Red);
RDebugUtils.currentLine=18939911;
 //BA.debugLineNum = 18939911;BA.debugLine="Return";
if (true) return ;
 if (true) break;

case 4:
//C
this.state = -1;
;
RDebugUtils.currentLine=18939914;
 //BA.debugLineNum = 18939914;BA.debugLine="Starter.DiscoveredServer = unit";
parent.mostCurrent._starter._discoveredserver /*String*/  = _unit;
RDebugUtils.currentLine=18939915;
 //BA.debugLineNum = 18939915;BA.debugLine="CallSub2(Starter, \"SetLastWill\", \"recvdied\")";
anywheresoftware.b4a.keywords.Common.CallSubDebug2(processBA,(Object)(parent.mostCurrent._starter.getObject()),"SetLastWill",(Object)("recvdied"));
RDebugUtils.currentLine=18939916;
 //BA.debugLineNum = 18939916;BA.debugLine="CallSub2(Starter, \"SetUnit\", PrepTopicName(unit).";
anywheresoftware.b4a.keywords.Common.CallSubDebug2(processBA,(Object)(parent.mostCurrent._starter.getObject()),"SetUnit",(Object)(_preptopicname(_unit).toLowerCase()));
RDebugUtils.currentLine=18939918;
 //BA.debugLineNum = 18939918;BA.debugLine="DisconnectMqtt";
_disconnectmqtt();
RDebugUtils.currentLine=18939919;
 //BA.debugLineNum = 18939919;BA.debugLine="Sleep(100)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "main", "clvserver_itemclick"),(int) (100));
this.state = 5;
return;
case 5:
//C
this.state = -1;
;
RDebugUtils.currentLine=18939920;
 //BA.debugLineNum = 18939920;BA.debugLine="StartActivity(ServerBoard)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(parent.mostCurrent._serverboard.getObject()));
RDebugUtils.currentLine=18939921;
 //BA.debugLineNum = 18939921;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static String  _preptopicname(String _bord) throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "preptopicname", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "preptopicname", new Object[] {_bord}));}
RDebugUtils.currentLine=19791872;
 //BA.debugLineNum = 19791872;BA.debugLine="Private Sub PrepTopicName(bord As String) As Strin";
RDebugUtils.currentLine=19791873;
 //BA.debugLineNum = 19791873;BA.debugLine="Return bord.Replace(\" \", \"\")";
if (true) return _bord.replace(" ","");
RDebugUtils.currentLine=19791874;
 //BA.debugLineNum = 19791874;BA.debugLine="End Sub";
return "";
}
public static String  _connectionerror() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "connectionerror", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "connectionerror", null));}
RDebugUtils.currentLine=20512768;
 //BA.debugLineNum = 20512768;BA.debugLine="Sub ConnectionError";
RDebugUtils.currentLine=20512769;
 //BA.debugLineNum = 20512769;BA.debugLine="baseFile.ShowCustomToast(\"MQTT Fout\", True, Color";
_basefile._showcustomtoast /*String*/ (null,(Object)("MQTT Fout"),anywheresoftware.b4a.keywords.Common.True,anywheresoftware.b4a.keywords.Common.Colors.Red);
RDebugUtils.currentLine=20512771;
 //BA.debugLineNum = 20512771;BA.debugLine="End Sub";
return "";
}
public static String  _deletedlocationactive() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "deletedlocationactive", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "deletedlocationactive", null));}
RDebugUtils.currentLine=20381696;
 //BA.debugLineNum = 20381696;BA.debugLine="Sub DeletedLocationActive";
RDebugUtils.currentLine=20381697;
 //BA.debugLineNum = 20381697;BA.debugLine="mqttBase.Disconnect";
_mqttbase._disconnect /*String*/ (null);
RDebugUtils.currentLine=20381698;
 //BA.debugLineNum = 20381698;BA.debugLine="clvServer.Clear";
mostCurrent._clvserver._clear();
RDebugUtils.currentLine=20381699;
 //BA.debugLineNum = 20381699;BA.debugLine="getBaseList";
_getbaselist();
RDebugUtils.currentLine=20381700;
 //BA.debugLineNum = 20381700;BA.debugLine="End Sub";
return "";
}
public static void  _getbaselist() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "getbaselist", false))
	 {Debug.delegate(mostCurrent.activityBA, "getbaselist", null); return;}
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
RDebugUtils.currentModule="main";

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
RDebugUtils.currentLine=18743297;
 //BA.debugLineNum = 18743297;BA.debugLine="Dim listSize As Int = 0";
_listsize = (int) (0);
RDebugUtils.currentLine=18743299;
 //BA.debugLineNum = 18743299;BA.debugLine="baseList.Initialize";
parent._baselist.Initialize();
RDebugUtils.currentLine=18743300;
 //BA.debugLineNum = 18743300;BA.debugLine="baseList = baseFile.GetBase";
parent._baselist = parent._basefile._getbase /*anywheresoftware.b4a.objects.collections.List*/ (null);
RDebugUtils.currentLine=18743302;
 //BA.debugLineNum = 18743302;BA.debugLine="If CallSub(baseFile, \"CheckBaseListExists\") = Fal";
if (true) break;

case 1:
//if
this.state = 4;
if ((anywheresoftware.b4a.keywords.Common.CallSubDebug(processBA,(Object)(parent._basefile),"CheckBaseListExists")).equals((Object)(anywheresoftware.b4a.keywords.Common.False))) { 
this.state = 3;
}if (true) break;

case 3:
//C
this.state = 4;
RDebugUtils.currentLine=18743303;
 //BA.debugLineNum = 18743303;BA.debugLine="pnlLocationCOde.Visible = True";
parent.mostCurrent._pnllocationcode.setVisible(anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=18743304;
 //BA.debugLineNum = 18743304;BA.debugLine="Return";
if (true) return ;
 if (true) break;
;
RDebugUtils.currentLine=18743307;
 //BA.debugLineNum = 18743307;BA.debugLine="If baseList.IsInitialized Then";

case 4:
//if
this.state = 7;
if (parent._baselist.IsInitialized()) { 
this.state = 6;
}if (true) break;

case 6:
//C
this.state = 7;
RDebugUtils.currentLine=18743308;
 //BA.debugLineNum = 18743308;BA.debugLine="listSize = baseList.Size";
_listsize = parent._baselist.getSize();
 if (true) break;
;
RDebugUtils.currentLine=18743310;
 //BA.debugLineNum = 18743310;BA.debugLine="If listSize > 1 Then";

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
RDebugUtils.currentLine=18743312;
 //BA.debugLineNum = 18743312;BA.debugLine="pnlLocationList.SetVisibleAnimated(500, True)";
parent.mostCurrent._pnllocationlist.SetVisibleAnimated((int) (500),anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=18743313;
 //BA.debugLineNum = 18743313;BA.debugLine="Sleep(200)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "main", "getbaselist"),(int) (200));
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
RDebugUtils.currentLine=18743315;
 //BA.debugLineNum = 18743315;BA.debugLine="pnlLocationList.SetVisibleAnimated(500, False)";
parent.mostCurrent._pnllocationlist.SetVisibleAnimated((int) (500),anywheresoftware.b4a.keywords.Common.False);
 if (true) break;

case 12:
//C
this.state = 13;
;
RDebugUtils.currentLine=18743319;
 //BA.debugLineNum = 18743319;BA.debugLine="lblNoBord.Text = \"Wachten op borden...\"";
parent.mostCurrent._lblnobord.setText(BA.ObjectToCharSequence("Wachten op borden..."));
RDebugUtils.currentLine=18743321;
 //BA.debugLineNum = 18743321;BA.debugLine="If baseList.IsInitialized = False Then";
if (true) break;

case 13:
//if
this.state = 20;
if (parent._baselist.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
this.state = 15;
}else 
{RDebugUtils.currentLine=18743323;
 //BA.debugLineNum = 18743323;BA.debugLine="Else If baseList.Size = 1 Then";
if (parent._baselist.getSize()==1) { 
this.state = 17;
}else {
this.state = 19;
}}
if (true) break;

case 15:
//C
this.state = 20;
RDebugUtils.currentLine=18743322;
 //BA.debugLineNum = 18743322;BA.debugLine="pnlLocationCOde.Visible = True";
parent.mostCurrent._pnllocationcode.setVisible(anywheresoftware.b4a.keywords.Common.True);
 if (true) break;

case 17:
//C
this.state = 20;
RDebugUtils.currentLine=18743324;
 //BA.debugLineNum = 18743324;BA.debugLine="Dim loc As locationBord";
_loc = new nl.pdeg.bordondroid.main._locationbord();
RDebugUtils.currentLine=18743325;
 //BA.debugLineNum = 18743325;BA.debugLine="loc.Initialize";
_loc.Initialize();
RDebugUtils.currentLine=18743326;
 //BA.debugLineNum = 18743326;BA.debugLine="loc = baseList.Get(0)";
_loc = (nl.pdeg.bordondroid.main._locationbord)(parent._baselist.Get((int) (0)));
RDebugUtils.currentLine=18743327;
 //BA.debugLineNum = 18743327;BA.debugLine="Starter.selectedLocationCode = loc.code";
parent.mostCurrent._starter._selectedlocationcode /*String*/  = _loc.code /*String*/ ;
RDebugUtils.currentLine=18743328;
 //BA.debugLineNum = 18743328;BA.debugLine="Starter.selectedLocationDescription = loc.descri";
parent.mostCurrent._starter._selectedlocationdescription /*String*/  = _loc.description /*String*/ ;
RDebugUtils.currentLine=18743329;
 //BA.debugLineNum = 18743329;BA.debugLine="lblCurrLocation.Text = loc.description";
parent.mostCurrent._lblcurrlocation.setText(BA.ObjectToCharSequence(_loc.description /*String*/ ));
RDebugUtils.currentLine=18743330;
 //BA.debugLineNum = 18743330;BA.debugLine="InitConnection";
_initconnection();
 if (true) break;

case 19:
//C
this.state = 20;
RDebugUtils.currentLine=18743332;
 //BA.debugLineNum = 18743332;BA.debugLine="StartActivity(SelectLocation)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(parent.mostCurrent._selectlocation.getObject()));
 if (true) break;

case 20:
//C
this.state = -1;
;
RDebugUtils.currentLine=18743334;
 //BA.debugLineNum = 18743334;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static String  _edtfloatcode_textchanged(String _old,String _new) throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "edtfloatcode_textchanged", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "edtfloatcode_textchanged", new Object[] {_old,_new}));}
RDebugUtils.currentLine=20316160;
 //BA.debugLineNum = 20316160;BA.debugLine="Sub edtFloatCode_TextChanged (Old As String, New A";
RDebugUtils.currentLine=20316161;
 //BA.debugLineNum = 20316161;BA.debugLine="If New.Length > 0 Then";
if (_new.length()>0) { 
RDebugUtils.currentLine=20316162;
 //BA.debugLineNum = 20316162;BA.debugLine="btnOk.Enabled = True";
mostCurrent._btnok.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 }else {
RDebugUtils.currentLine=20316164;
 //BA.debugLineNum = 20316164;BA.debugLine="btnOk.Enabled = False";
mostCurrent._btnok.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 };
RDebugUtils.currentLine=20316166;
 //BA.debugLineNum = 20316166;BA.debugLine="End Sub";
return "";
}
public static String  _edtfloatdescription_textchanged(String _old,String _new) throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "edtfloatdescription_textchanged", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "edtfloatdescription_textchanged", new Object[] {_old,_new}));}
RDebugUtils.currentLine=20250624;
 //BA.debugLineNum = 20250624;BA.debugLine="Sub edtFloatDescription_TextChanged (Old As String";
RDebugUtils.currentLine=20250626;
 //BA.debugLineNum = 20250626;BA.debugLine="End Sub";
return "";
}
public static String  _edtlocationcode_textchanged(String _old,String _new) throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "edtlocationcode_textchanged", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "edtlocationcode_textchanged", new Object[] {_old,_new}));}
RDebugUtils.currentLine=19988480;
 //BA.debugLineNum = 19988480;BA.debugLine="Sub edtLocationCode_TextChanged (Old As String, Ne";
RDebugUtils.currentLine=19988481;
 //BA.debugLineNum = 19988481;BA.debugLine="If New.Length > 0 Then";
if (_new.length()>0) { 
RDebugUtils.currentLine=19988482;
 //BA.debugLineNum = 19988482;BA.debugLine="btnOk.Enabled = True";
mostCurrent._btnok.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 }else {
RDebugUtils.currentLine=19988484;
 //BA.debugLineNum = 19988484;BA.debugLine="btnOk.Enabled = False";
mostCurrent._btnok.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 };
RDebugUtils.currentLine=19988486;
 //BA.debugLineNum = 19988486;BA.debugLine="End Sub";
return "";
}
public static void  _startconnection() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "startconnection", false))
	 {Debug.delegate(mostCurrent.activityBA, "startconnection", null); return;}
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
RDebugUtils.currentModule="main";

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = -1;
RDebugUtils.currentLine=18677761;
 //BA.debugLineNum = 18677761;BA.debugLine="pnlLocationCOde.Visible = False";
parent.mostCurrent._pnllocationcode.setVisible(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=18677762;
 //BA.debugLineNum = 18677762;BA.debugLine="B4XLoadingIndicator1.Show";
parent.mostCurrent._b4xloadingindicator1._show /*String*/ (null);
RDebugUtils.currentLine=18677763;
 //BA.debugLineNum = 18677763;BA.debugLine="B4XLoadingIndicator2.Show";
parent.mostCurrent._b4xloadingindicator2._show /*String*/ (null);
RDebugUtils.currentLine=18677764;
 //BA.debugLineNum = 18677764;BA.debugLine="Sleep(10)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "main", "startconnection"),(int) (10));
this.state = 1;
return;
case 1:
//C
this.state = -1;
;
RDebugUtils.currentLine=18677765;
 //BA.debugLineNum = 18677765;BA.debugLine="pnlNobords.Visible = True";
parent.mostCurrent._pnlnobords.setVisible(anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=18677766;
 //BA.debugLineNum = 18677766;BA.debugLine="clvServer.Clear";
parent.mostCurrent._clvserver._clear();
RDebugUtils.currentLine=18677767;
 //BA.debugLineNum = 18677767;BA.debugLine="mqttBase.Initialize";
parent._mqttbase._initialize /*String*/ (null,processBA);
RDebugUtils.currentLine=18677768;
 //BA.debugLineNum = 18677768;BA.debugLine="mqttBase.Connect";
parent._mqttbase._connect /*String*/ (null);
RDebugUtils.currentLine=18677770;
 //BA.debugLineNum = 18677770;BA.debugLine="baseFile.GetBase";
parent._basefile._getbase /*anywheresoftware.b4a.objects.collections.List*/ (null);
RDebugUtils.currentLine=18677772;
 //BA.debugLineNum = 18677772;BA.debugLine="tmrBordLastAlive.Initialize(\"tmrBordAlive\", 10000";
parent._tmrbordlastalive.Initialize(processBA,"tmrBordAlive",(long) (10000));
RDebugUtils.currentLine=18677773;
 //BA.debugLineNum = 18677773;BA.debugLine="tmrBordLastAlive.Enabled = True";
parent._tmrbordlastalive.setEnabled(anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=18677774;
 //BA.debugLineNum = 18677774;BA.debugLine="connectTime = DateTime.Now";
parent._connecttime = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
RDebugUtils.currentLine=18677776;
 //BA.debugLineNum = 18677776;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static String  _lblviewbord_click() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "lblviewbord_click", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "lblviewbord_click", null));}
anywheresoftware.b4a.objects.ConcreteViewWrapper _v = null;
anywheresoftware.b4a.objects.PanelWrapper _p = null;
RDebugUtils.currentLine=19726336;
 //BA.debugLineNum = 19726336;BA.debugLine="Sub lblViewBord_Click";
RDebugUtils.currentLine=19726337;
 //BA.debugLineNum = 19726337;BA.debugLine="Dim v As View = Sender";
_v = new anywheresoftware.b4a.objects.ConcreteViewWrapper();
_v.setObject((android.view.View)(anywheresoftware.b4a.keywords.Common.Sender(mostCurrent.activityBA)));
RDebugUtils.currentLine=19726338;
 //BA.debugLineNum = 19726338;BA.debugLine="Dim p As Panel = v.Parent";
_p = new anywheresoftware.b4a.objects.PanelWrapper();
_p.setObject((android.view.ViewGroup)(_v.getParent()));
RDebugUtils.currentLine=19726340;
 //BA.debugLineNum = 19726340;BA.debugLine="clvServer_ItemClick (clvServer.GetItemFromView(p)";
_clvserver_itemclick(mostCurrent._clvserver._getitemfromview((anywheresoftware.b4a.objects.B4XViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.B4XViewWrapper(), (java.lang.Object)(_p.getObject()))),anywheresoftware.b4a.keywords.Common.Null);
RDebugUtils.currentLine=19726341;
 //BA.debugLineNum = 19726341;BA.debugLine="End Sub";
return "";
}
public static String  _pnllocation_click() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "pnllocation_click", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "pnllocation_click", null));}
RDebugUtils.currentLine=20054016;
 //BA.debugLineNum = 20054016;BA.debugLine="Sub pnlLocation_Click";
RDebugUtils.currentLine=20054018;
 //BA.debugLineNum = 20054018;BA.debugLine="If CheckClientConnected Then";
if (_checkclientconnected()) { 
RDebugUtils.currentLine=20054019;
 //BA.debugLineNum = 20054019;BA.debugLine="mqttBase.Disconnect";
_mqttbase._disconnect /*String*/ (null);
 };
RDebugUtils.currentLine=20054021;
 //BA.debugLineNum = 20054021;BA.debugLine="StartActivity(locations)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(mostCurrent._locations.getObject()));
RDebugUtils.currentLine=20054022;
 //BA.debugLineNum = 20054022;BA.debugLine="End Sub";
return "";
}
public static String  _pnllocationlist_click() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "pnllocationlist_click", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "pnllocationlist_click", null));}
RDebugUtils.currentLine=20185088;
 //BA.debugLineNum = 20185088;BA.debugLine="Sub pnlLocationList_Click";
RDebugUtils.currentLine=20185089;
 //BA.debugLineNum = 20185089;BA.debugLine="StartActivity(SelectLocation)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(mostCurrent._selectlocation.getObject()));
RDebugUtils.currentLine=20185090;
 //BA.debugLineNum = 20185090;BA.debugLine="End Sub";
return "";
}
public static String  _reconnecttolocation() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "reconnecttolocation", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "reconnecttolocation", null));}
RDebugUtils.currentLine=18808832;
 //BA.debugLineNum = 18808832;BA.debugLine="Sub ReconnectToLocation";
RDebugUtils.currentLine=18808833;
 //BA.debugLineNum = 18808833;BA.debugLine="mqttBase.Initialize";
_mqttbase._initialize /*String*/ (null,processBA);
RDebugUtils.currentLine=18808834;
 //BA.debugLineNum = 18808834;BA.debugLine="CallSub2(Starter, \"SetLastWill\", \"bordpubdied\")";
anywheresoftware.b4a.keywords.Common.CallSubDebug2(processBA,(Object)(mostCurrent._starter.getObject()),"SetLastWill",(Object)("bordpubdied"));
RDebugUtils.currentLine=18808835;
 //BA.debugLineNum = 18808835;BA.debugLine="CallSub2(Starter, \"SetSubBase\", Starter.selectedL";
anywheresoftware.b4a.keywords.Common.CallSubDebug2(processBA,(Object)(mostCurrent._starter.getObject()),"SetSubBase",(Object)(mostCurrent._starter._selectedlocationcode /*String*/ ));
RDebugUtils.currentLine=18808836;
 //BA.debugLineNum = 18808836;BA.debugLine="CallSub2(Starter, \"SetSubString2\", \"/pubbord\")";
anywheresoftware.b4a.keywords.Common.CallSubDebug2(processBA,(Object)(mostCurrent._starter.getObject()),"SetSubString2",(Object)("/pubbord"));
RDebugUtils.currentLine=18808837;
 //BA.debugLineNum = 18808837;BA.debugLine="CallSub2(Starter, \"SetUnsubscribeString2\", \"\")";
anywheresoftware.b4a.keywords.Common.CallSubDebug2(processBA,(Object)(mostCurrent._starter.getObject()),"SetUnsubscribeString2",(Object)(""));
RDebugUtils.currentLine=18808838;
 //BA.debugLineNum = 18808838;BA.debugLine="mqttBase.Connect";
_mqttbase._connect /*String*/ (null);
RDebugUtils.currentLine=18808839;
 //BA.debugLineNum = 18808839;BA.debugLine="End Sub";
return "";
}
public static String  _setbordlastalivetimer() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "setbordlastalivetimer", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "setbordlastalivetimer", null));}
RDebugUtils.currentLine=18612224;
 //BA.debugLineNum = 18612224;BA.debugLine="Sub setBordLastAliveTimer";
RDebugUtils.currentLine=18612225;
 //BA.debugLineNum = 18612225;BA.debugLine="tmrBordLastAlive.Enabled = True";
_tmrbordlastalive.setEnabled(anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=18612227;
 //BA.debugLineNum = 18612227;BA.debugLine="End Sub";
return "";
}
public static String  _shownotconnectedtobroker() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "shownotconnectedtobroker", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "shownotconnectedtobroker", null));}
RDebugUtils.currentLine=19202048;
 //BA.debugLineNum = 19202048;BA.debugLine="Sub ShowNotConnectedToBroker";
RDebugUtils.currentLine=19202049;
 //BA.debugLineNum = 19202049;BA.debugLine="lblNoBord.Text = \"Verbinding borden verbroken\"";
mostCurrent._lblnobord.setText(BA.ObjectToCharSequence("Verbinding borden verbroken"));
RDebugUtils.currentLine=19202050;
 //BA.debugLineNum = 19202050;BA.debugLine="pnlNobords.SetVisibleAnimated(1000, True)";
mostCurrent._pnlnobords.SetVisibleAnimated((int) (1000),anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=19202051;
 //BA.debugLineNum = 19202051;BA.debugLine="End Sub";
return "";
}
public static String  _tmrbordalive_tick() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "tmrbordalive_tick", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "tmrbordalive_tick", null));}
RDebugUtils.currentLine=19005440;
 //BA.debugLineNum = 19005440;BA.debugLine="Sub tmrBordAlive_Tick";
RDebugUtils.currentLine=19005441;
 //BA.debugLineNum = 19005441;BA.debugLine="If Not(checkConnectTime) Then Return";
if (anywheresoftware.b4a.keywords.Common.Not(_checkconnecttime())) { 
if (true) return "";};
RDebugUtils.currentLine=19005442;
 //BA.debugLineNum = 19005442;BA.debugLine="CheckLastAliveTime";
_checklastalivetime();
RDebugUtils.currentLine=19005443;
 //BA.debugLineNum = 19005443;BA.debugLine="End Sub";
return "";
}
}