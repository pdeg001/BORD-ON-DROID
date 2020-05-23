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
public anywheresoftware.b4a.keywords.Common __c = null;
public static anywheresoftware.b4a.objects.Timer _tmrbordlastalive = null;
public static nl.pdeg.bordondroid.base _basefile = null;
public static anywheresoftware.b4a.objects.collections.List _baselist = null;
public static nl.pdeg.bordondroid.mqttconnector _mqttbase = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblbordname = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblviewbord = null;
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
public nl.pdeg.bordondroid.b4xloadingindicator _borddiedprogress = null;
public static long _connecttime = 0L;
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
RDebugUtils.currentLine=6356992;
 //BA.debugLineNum = 6356992;BA.debugLine="Private Sub Activity_KeyPress(KeyCode As Int) As B";
RDebugUtils.currentLine=6356993;
 //BA.debugLineNum = 6356993;BA.debugLine="If KeyCode = KeyCodes.KEYCODE_BACK Then";
if (_keycode==anywheresoftware.b4a.keywords.Common.KeyCodes.KEYCODE_BACK) { 
RDebugUtils.currentLine=6356994;
 //BA.debugLineNum = 6356994;BA.debugLine="tmrBordLastAlive.Enabled = False";
_tmrbordlastalive.setEnabled(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=6356995;
 //BA.debugLineNum = 6356995;BA.debugLine="DisconnectMqtt";
_disconnectmqtt();
RDebugUtils.currentLine=6356996;
 //BA.debugLineNum = 6356996;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
RDebugUtils.currentLine=6356998;
 //BA.debugLineNum = 6356998;BA.debugLine="Return False";
if (true) return anywheresoftware.b4a.keywords.Common.False;
 }else {
RDebugUtils.currentLine=6357000;
 //BA.debugLineNum = 6357000;BA.debugLine="Return False";
if (true) return anywheresoftware.b4a.keywords.Common.False;
 };
RDebugUtils.currentLine=6357002;
 //BA.debugLineNum = 6357002;BA.debugLine="End Sub";
return false;
}
public static String  _disconnectmqtt() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "disconnectmqtt", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "disconnectmqtt", null));}
RDebugUtils.currentLine=6553600;
 //BA.debugLineNum = 6553600;BA.debugLine="Sub DisconnectMqtt";
RDebugUtils.currentLine=6553601;
 //BA.debugLineNum = 6553601;BA.debugLine="If mqttBase.connected Then";
if (_mqttbase._connected /*boolean*/ ) { 
RDebugUtils.currentLine=6553602;
 //BA.debugLineNum = 6553602;BA.debugLine="mqttBase.Disconnect";
_mqttbase._disconnect /*String*/ (null);
 };
RDebugUtils.currentLine=6553604;
 //BA.debugLineNum = 6553604;BA.debugLine="If CheckClientConnected Then";
if (_checkclientconnected()) { 
RDebugUtils.currentLine=6553605;
 //BA.debugLineNum = 6553605;BA.debugLine="mqttBase.Disconnect";
_mqttbase._disconnect /*String*/ (null);
 };
RDebugUtils.currentLine=6553608;
 //BA.debugLineNum = 6553608;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
RDebugUtils.currentModule="main";
RDebugUtils.currentLine=262144;
 //BA.debugLineNum = 262144;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
RDebugUtils.currentLine=262145;
 //BA.debugLineNum = 262145;BA.debugLine="Starter.mainPaused = True";
mostCurrent._starter._mainpaused /*boolean*/  = anywheresoftware.b4a.keywords.Common.True;
RDebugUtils.currentLine=262146;
 //BA.debugLineNum = 262146;BA.debugLine="ResumeConnection(False)";
_resumeconnection(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=262148;
 //BA.debugLineNum = 262148;BA.debugLine="End Sub";
return "";
}
public static String  _resumeconnection(boolean _resume) throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "resumeconnection", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "resumeconnection", new Object[] {_resume}));}
RDebugUtils.currentLine=6225920;
 //BA.debugLineNum = 6225920;BA.debugLine="Sub ResumeConnection(resume As Boolean)";
RDebugUtils.currentLine=6225921;
 //BA.debugLineNum = 6225921;BA.debugLine="If resume Then";
if (_resume) { 
RDebugUtils.currentLine=6225922;
 //BA.debugLineNum = 6225922;BA.debugLine="mqttBase.Connect";
_mqttbase._connect /*String*/ (null);
 }else {
RDebugUtils.currentLine=6225924;
 //BA.debugLineNum = 6225924;BA.debugLine="mqttBase.Disconnect";
_mqttbase._disconnect /*String*/ (null);
 };
RDebugUtils.currentLine=6225927;
 //BA.debugLineNum = 6225927;BA.debugLine="tmrBordLastAlive.Enabled = resume";
_tmrbordlastalive.setEnabled(_resume);
RDebugUtils.currentLine=6225928;
 //BA.debugLineNum = 6225928;BA.debugLine="End Sub";
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
 }else {
 };
RDebugUtils.currentLine=196629;
 //BA.debugLineNum = 196629;BA.debugLine="End Sub";
return "";
}
public static String  _showselectlocationbutton() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "showselectlocationbutton", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "showselectlocationbutton", null));}
RDebugUtils.currentLine=9175040;
 //BA.debugLineNum = 9175040;BA.debugLine="Sub ShowSelectLocationButton";
RDebugUtils.currentLine=9175041;
 //BA.debugLineNum = 9175041;BA.debugLine="pnlLocationList.Visible = baseFile.GetBase.Size >";
mostCurrent._pnllocationlist.setVisible(_basefile._getbase /*anywheresoftware.b4a.objects.collections.List*/ (null).getSize()>1);
RDebugUtils.currentLine=9175042;
 //BA.debugLineNum = 9175042;BA.debugLine="End Sub";
return "";
}
public static void  _addunkownip(String _ip,String _name) throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "addunkownip", false))
	 {Debug.delegate(mostCurrent.activityBA, "addunkownip", new Object[] {_ip,_name}); return;}
ResumableSub_AddUnkownIp rsub = new ResumableSub_AddUnkownIp(null,_ip,_name);
rsub.resume(processBA, null);
}
public static class ResumableSub_AddUnkownIp extends BA.ResumableSub {
public ResumableSub_AddUnkownIp(nl.pdeg.bordondroid.main parent,String _ip,String _name) {
this.parent = parent;
this._ip = _ip;
this._name = _name;
}
nl.pdeg.bordondroid.main parent;
String _ip;
String _name;
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
RDebugUtils.currentLine=6946817;
 //BA.debugLineNum = 6946817;BA.debugLine="Dim bordStatus As bordStatus";
_bordstatus = new nl.pdeg.bordondroid.main._bordstatus();
RDebugUtils.currentLine=6946818;
 //BA.debugLineNum = 6946818;BA.debugLine="bordStatus.Initialize";
_bordstatus.Initialize();
RDebugUtils.currentLine=6946819;
 //BA.debugLineNum = 6946819;BA.debugLine="bordStatus.ip = ip";
_bordstatus.ip /*String*/  = _ip;
RDebugUtils.currentLine=6946820;
 //BA.debugLineNum = 6946820;BA.debugLine="bordStatus.name = name";
_bordstatus.name /*String*/  = _name;
RDebugUtils.currentLine=6946821;
 //BA.debugLineNum = 6946821;BA.debugLine="bordStatus.timeStamp = DateTime.Now";
_bordstatus.timeStamp /*long*/  = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
RDebugUtils.currentLine=6946822;
 //BA.debugLineNum = 6946822;BA.debugLine="bordStatus.alive = True";
_bordstatus.alive /*boolean*/  = anywheresoftware.b4a.keywords.Common.True;
RDebugUtils.currentLine=6946823;
 //BA.debugLineNum = 6946823;BA.debugLine="Starter.serverList.Add(bordStatus)";
parent.mostCurrent._starter._serverlist /*anywheresoftware.b4a.objects.collections.List*/ .Add((Object)(_bordstatus));
RDebugUtils.currentLine=6946824;
 //BA.debugLineNum = 6946824;BA.debugLine="CheckIpInClv(bordStatus)";
_checkipinclv(_bordstatus);
RDebugUtils.currentLine=6946825;
 //BA.debugLineNum = 6946825;BA.debugLine="baseFile.SetBordDiedByName(name, clvServer, True)";
parent._basefile._setborddiedbyname /*String*/ (null,_name,parent.mostCurrent._clvserver,anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=6946827;
 //BA.debugLineNum = 6946827;BA.debugLine="If clvServer.GetSize > 0 Then";
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
RDebugUtils.currentLine=6946828;
 //BA.debugLineNum = 6946828;BA.debugLine="pnlNobords.SetVisibleAnimated(500, False)";
parent.mostCurrent._pnlnobords.SetVisibleAnimated((int) (500),anywheresoftware.b4a.keywords.Common.False);
 if (true) break;

case 5:
//C
this.state = 6;
RDebugUtils.currentLine=6946830;
 //BA.debugLineNum = 6946830;BA.debugLine="pnlNobords.SetVisibleAnimated(500, True)";
parent.mostCurrent._pnlnobords.SetVisibleAnimated((int) (500),anywheresoftware.b4a.keywords.Common.True);
 if (true) break;

case 6:
//C
this.state = -1;
;
RDebugUtils.currentLine=6946834;
 //BA.debugLineNum = 6946834;BA.debugLine="Sleep(400)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "main", "addunkownip"),(int) (400));
this.state = 7;
return;
case 7:
//C
this.state = -1;
;
RDebugUtils.currentLine=6946836;
 //BA.debugLineNum = 6946836;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static String  _checkipinclv(nl.pdeg.bordondroid.main._bordstatus _bord) throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "checkipinclv", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "checkipinclv", new Object[] {_bord}));}
anywheresoftware.b4a.objects.PanelWrapper _p = null;
int _i = 0;
RDebugUtils.currentLine=7208960;
 //BA.debugLineNum = 7208960;BA.debugLine="Sub CheckIpInClv(bord As bordStatus)";
RDebugUtils.currentLine=7208961;
 //BA.debugLineNum = 7208961;BA.debugLine="Dim p As Panel";
_p = new anywheresoftware.b4a.objects.PanelWrapper();
RDebugUtils.currentLine=7208964;
 //BA.debugLineNum = 7208964;BA.debugLine="For i = 0 To clvServer.GetSize-1";
{
final int step2 = 1;
final int limit2 = (int) (mostCurrent._clvserver._getsize()-1);
_i = (int) (0) ;
for (;_i <= limit2 ;_i = _i + step2 ) {
RDebugUtils.currentLine=7208965;
 //BA.debugLineNum = 7208965;BA.debugLine="p = clvServer.GetPanel(i)";
_p.setObject((android.view.ViewGroup)(mostCurrent._clvserver._getpanel(_i).getObject()));
RDebugUtils.currentLine=7208966;
 //BA.debugLineNum = 7208966;BA.debugLine="If p.Tag = bord.name Then";
if ((_p.getTag()).equals((Object)(_bord.name /*String*/ ))) { 
RDebugUtils.currentLine=7208973;
 //BA.debugLineNum = 7208973;BA.debugLine="baseFile.SetPanelLabelItemText(p, \"lblLastCheck";
_basefile._setpanellabelitemtext /*String*/ (null,_p,"lblLastCheck",("Laatste controle "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("time",(Object)(anywheresoftware.b4a.keywords.Common.DateTime.getNow()))+""));
RDebugUtils.currentLine=7208974;
 //BA.debugLineNum = 7208974;BA.debugLine="baseFile.SetBordDiedByName(bord.name, clvServer";
_basefile._setborddiedbyname /*String*/ (null,_bord.name /*String*/ ,mostCurrent._clvserver,_bord.alive /*boolean*/ );
 };
 }
};
RDebugUtils.currentLine=7208993;
 //BA.debugLineNum = 7208993;BA.debugLine="pnlNobords.SetVisibleAnimated(1000, False)";
mostCurrent._pnlnobords.SetVisibleAnimated((int) (1000),anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=7208994;
 //BA.debugLineNum = 7208994;BA.debugLine="clvServer.Add(genUnitList(bord.name, bord.alive),";
mostCurrent._clvserver._add((anywheresoftware.b4a.objects.B4XViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.B4XViewWrapper(), (java.lang.Object)(_genunitlist(_bord.name /*String*/ ,_bord.alive /*boolean*/ ).getObject())),(Object)(""));
RDebugUtils.currentLine=7208995;
 //BA.debugLineNum = 7208995;BA.debugLine="End Sub";
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
RDebugUtils.currentLine=7733249;
 //BA.debugLineNum = 7733249;BA.debugLine="Msgbox2Async(\"Applicatie afsluiten?\", Application";
anywheresoftware.b4a.keywords.Common.Msgbox2Async(BA.ObjectToCharSequence("Applicatie afsluiten?"),BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Application.getLabelName()),"JA","","NEE",anywheresoftware.b4a.keywords.Common.Application.getIcon(),processBA,anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=7733250;
 //BA.debugLineNum = 7733250;BA.debugLine="Wait For Msgbox_Result(Result As Int)";
anywheresoftware.b4a.keywords.Common.WaitFor("msgbox_result", processBA, new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "main", "btncancel_click"), null);
this.state = 5;
return;
case 5:
//C
this.state = 1;
_result = (Integer) result[0];
;
RDebugUtils.currentLine=7733251;
 //BA.debugLineNum = 7733251;BA.debugLine="If Result = DialogResponse.POSITIVE Then";
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
RDebugUtils.currentLine=7733252;
 //BA.debugLineNum = 7733252;BA.debugLine="Activity.Finish";
parent.mostCurrent._activity.Finish();
 if (true) break;

case 4:
//C
this.state = -1;
;
RDebugUtils.currentLine=7733254;
 //BA.debugLineNum = 7733254;BA.debugLine="End Sub";
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
RDebugUtils.currentLine=7864321;
 //BA.debugLineNum = 7864321;BA.debugLine="If edtFloatCode.Text = \"\" Then";
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
RDebugUtils.currentLine=7864322;
 //BA.debugLineNum = 7864322;BA.debugLine="Msgbox2Async(\"Locatie code mag niet leeg zijn\",";
anywheresoftware.b4a.keywords.Common.Msgbox2Async(BA.ObjectToCharSequence("Locatie code mag niet leeg zijn"),BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Application.getLabelName()),"OKE","","",anywheresoftware.b4a.keywords.Common.Application.getIcon(),processBA,anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=7864323;
 //BA.debugLineNum = 7864323;BA.debugLine="Wait For Msgbox_Result(Result As Int)";
anywheresoftware.b4a.keywords.Common.WaitFor("msgbox_result", processBA, new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "main", "btnok_click"), null);
this.state = 17;
return;
case 17:
//C
this.state = 4;
_result = (Integer) result[0];
;
RDebugUtils.currentLine=7864324;
 //BA.debugLineNum = 7864324;BA.debugLine="Return";
if (true) return ;
 if (true) break;

case 4:
//C
this.state = 5;
;
RDebugUtils.currentLine=7864327;
 //BA.debugLineNum = 7864327;BA.debugLine="Dim ime As IME";
_ime = new anywheresoftware.b4a.objects.IME();
RDebugUtils.currentLine=7864328;
 //BA.debugLineNum = 7864328;BA.debugLine="ime.Initialize(Me)";
_ime.Initialize(BA.ObjectToString(main.getObject()));
RDebugUtils.currentLine=7864329;
 //BA.debugLineNum = 7864329;BA.debugLine="Starter.testBaseName = True";
parent.mostCurrent._starter._testbasename /*boolean*/  = anywheresoftware.b4a.keywords.Common.True;
RDebugUtils.currentLine=7864330;
 //BA.debugLineNum = 7864330;BA.debugLine="Starter.selectedLocationCode = edtFloatCode.Text";
parent.mostCurrent._starter._selectedlocationcode /*String*/  = parent.mostCurrent._edtfloatcode._gettext /*String*/ (null);
RDebugUtils.currentLine=7864331;
 //BA.debugLineNum = 7864331;BA.debugLine="CallSub2(Starter, \"SetLastWill\", \"bordpubdied\")";
anywheresoftware.b4a.keywords.Common.CallSubDebug2(processBA,(Object)(parent.mostCurrent._starter.getObject()),"SetLastWill",(Object)("bordpubdied"));
RDebugUtils.currentLine=7864332;
 //BA.debugLineNum = 7864332;BA.debugLine="CallSub2(Starter, \"SetSubBase\", Starter.selectedL";
anywheresoftware.b4a.keywords.Common.CallSubDebug2(processBA,(Object)(parent.mostCurrent._starter.getObject()),"SetSubBase",(Object)(parent.mostCurrent._starter._selectedlocationcode /*String*/ ));
RDebugUtils.currentLine=7864333;
 //BA.debugLineNum = 7864333;BA.debugLine="CallSub2(Starter, \"SetSubString2\", \"/pubbord\")";
anywheresoftware.b4a.keywords.Common.CallSubDebug2(processBA,(Object)(parent.mostCurrent._starter.getObject()),"SetSubString2",(Object)("/pubbord"));
RDebugUtils.currentLine=7864334;
 //BA.debugLineNum = 7864334;BA.debugLine="CallSub2(Starter, \"SetUnsubscribeString2\", \"\")";
anywheresoftware.b4a.keywords.Common.CallSubDebug2(processBA,(Object)(parent.mostCurrent._starter.getObject()),"SetUnsubscribeString2",(Object)(""));
RDebugUtils.currentLine=7864335;
 //BA.debugLineNum = 7864335;BA.debugLine="mqttBase.Initialize";
parent._mqttbase._initialize /*String*/ (null,processBA);
RDebugUtils.currentLine=7864336;
 //BA.debugLineNum = 7864336;BA.debugLine="Sleep(100)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "main", "btnok_click"),(int) (100));
this.state = 18;
return;
case 18:
//C
this.state = 5;
;
RDebugUtils.currentLine=7864337;
 //BA.debugLineNum = 7864337;BA.debugLine="mqttBase.Connect";
parent._mqttbase._connect /*String*/ (null);
RDebugUtils.currentLine=7864339;
 //BA.debugLineNum = 7864339;BA.debugLine="Sleep(1000)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "main", "btnok_click"),(int) (1000));
this.state = 19;
return;
case 19:
//C
this.state = 5;
;
RDebugUtils.currentLine=7864341;
 //BA.debugLineNum = 7864341;BA.debugLine="If mqttBase.connected = False Then";
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
RDebugUtils.currentLine=7864342;
 //BA.debugLineNum = 7864342;BA.debugLine="Msgbox2Async(\"Kan geen verbinding maken met loca";
anywheresoftware.b4a.keywords.Common.Msgbox2Async(BA.ObjectToCharSequence("Kan geen verbinding maken met locatie"),BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Application.getLabelName()),"OKE","","",anywheresoftware.b4a.keywords.Common.Application.getIcon(),processBA,anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=7864343;
 //BA.debugLineNum = 7864343;BA.debugLine="Wait For Msgbox_Result(Result As Int)";
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
RDebugUtils.currentLine=7864345;
 //BA.debugLineNum = 7864345;BA.debugLine="Dim code, description As String";
_code = "";
_description = "";
RDebugUtils.currentLine=7864346;
 //BA.debugLineNum = 7864346;BA.debugLine="lblCurrLocation.Text = edtFloatDescription.Text";
parent.mostCurrent._lblcurrlocation.setText(BA.ObjectToCharSequence(parent.mostCurrent._edtfloatdescription._gettext /*String*/ (null)));
RDebugUtils.currentLine=7864347;
 //BA.debugLineNum = 7864347;BA.debugLine="Starter.selectedLocationDescription = edtFloatDe";
parent.mostCurrent._starter._selectedlocationdescription /*String*/  = parent.mostCurrent._edtfloatdescription._gettext /*String*/ (null);
RDebugUtils.currentLine=7864348;
 //BA.debugLineNum = 7864348;BA.debugLine="code = edtFloatCode.Text";
_code = parent.mostCurrent._edtfloatcode._gettext /*String*/ (null);
RDebugUtils.currentLine=7864349;
 //BA.debugLineNum = 7864349;BA.debugLine="If edtFloatDescription.Text = \"\" Then edtFloatDe";
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
RDebugUtils.currentLine=7864350;
 //BA.debugLineNum = 7864350;BA.debugLine="description = edtFloatDescription.Text";
_description = parent.mostCurrent._edtfloatdescription._gettext /*String*/ (null);
RDebugUtils.currentLine=7864353;
 //BA.debugLineNum = 7864353;BA.debugLine="mqttBase.Disconnect";
parent._mqttbase._disconnect /*String*/ (null);
RDebugUtils.currentLine=7864354;
 //BA.debugLineNum = 7864354;BA.debugLine="pnlLocationCOde.Visible = False";
parent.mostCurrent._pnllocationcode.setVisible(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=7864355;
 //BA.debugLineNum = 7864355;BA.debugLine="baseFile.SetBase(code, description, \"1\")";
parent._basefile._setbase /*String*/ (null,_code,_description,"1");
RDebugUtils.currentLine=7864356;
 //BA.debugLineNum = 7864356;BA.debugLine="InitConnection";
_initconnection();
RDebugUtils.currentLine=7864357;
 //BA.debugLineNum = 7864357;BA.debugLine="ime.HideKeyboard";
_ime.HideKeyboard(mostCurrent.activityBA);
 if (true) break;

case 16:
//C
this.state = -1;
;
RDebugUtils.currentLine=7864359;
 //BA.debugLineNum = 7864359;BA.debugLine="End Sub";
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
RDebugUtils.currentLine=5439489;
 //BA.debugLineNum = 5439489;BA.debugLine="If mqttBase.connected Then mqttBase.Disconnect";
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
RDebugUtils.currentLine=5439490;
 //BA.debugLineNum = 5439490;BA.debugLine="Starter.serverList.Initialize";
parent.mostCurrent._starter._serverlist /*anywheresoftware.b4a.objects.collections.List*/ .Initialize();
RDebugUtils.currentLine=5439491;
 //BA.debugLineNum = 5439491;BA.debugLine="clvServer.Clear";
parent.mostCurrent._clvserver._clear();
RDebugUtils.currentLine=5439492;
 //BA.debugLineNum = 5439492;BA.debugLine="lblCurrLocation.Text = Starter.selectedLocationDe";
parent.mostCurrent._lblcurrlocation.setText(BA.ObjectToCharSequence(parent.mostCurrent._starter._selectedlocationdescription /*String*/ ));
RDebugUtils.currentLine=5439493;
 //BA.debugLineNum = 5439493;BA.debugLine="CallSub2(Starter, \"SetLastWill\", \"bordpubdied\")";
anywheresoftware.b4a.keywords.Common.CallSubDebug2(processBA,(Object)(parent.mostCurrent._starter.getObject()),"SetLastWill",(Object)("bordpubdied"));
RDebugUtils.currentLine=5439494;
 //BA.debugLineNum = 5439494;BA.debugLine="CallSub2(Starter, \"SetSubBase\", Starter.selectedL";
anywheresoftware.b4a.keywords.Common.CallSubDebug2(processBA,(Object)(parent.mostCurrent._starter.getObject()),"SetSubBase",(Object)(parent.mostCurrent._starter._selectedlocationcode /*String*/ ));
RDebugUtils.currentLine=5439495;
 //BA.debugLineNum = 5439495;BA.debugLine="CallSub2(Starter, \"SetSubString2\", \"/pubbord\")";
anywheresoftware.b4a.keywords.Common.CallSubDebug2(processBA,(Object)(parent.mostCurrent._starter.getObject()),"SetSubString2",(Object)("/pubbord"));
RDebugUtils.currentLine=5439496;
 //BA.debugLineNum = 5439496;BA.debugLine="CallSub2(Starter, \"SetUnsubscribeString2\", \"\")";
anywheresoftware.b4a.keywords.Common.CallSubDebug2(processBA,(Object)(parent.mostCurrent._starter.getObject()),"SetUnsubscribeString2",(Object)(""));
RDebugUtils.currentLine=5439497;
 //BA.debugLineNum = 5439497;BA.debugLine="Sleep(0)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "main", "initconnection"),(int) (0));
this.state = 7;
return;
case 7:
//C
this.state = -1;
;
RDebugUtils.currentLine=5439498;
 //BA.debugLineNum = 5439498;BA.debugLine="StartConnection";
_startconnection();
RDebugUtils.currentLine=5439499;
 //BA.debugLineNum = 5439499;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static boolean  _checkclientconnected() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "checkclientconnected", false))
	 {return ((Boolean) Debug.delegate(mostCurrent.activityBA, "checkclientconnected", null));}
RDebugUtils.currentLine=8257536;
 //BA.debugLineNum = 8257536;BA.debugLine="Private Sub CheckClientConnected As Boolean";
RDebugUtils.currentLine=8257537;
 //BA.debugLineNum = 8257537;BA.debugLine="Return CallSub(mqttBase, \"GetClientConnected\")";
if (true) return BA.ObjectToBoolean(anywheresoftware.b4a.keywords.Common.CallSubDebug(processBA,(Object)(_mqttbase),"GetClientConnected"));
RDebugUtils.currentLine=8257538;
 //BA.debugLineNum = 8257538;BA.debugLine="End Sub";
return false;
}
public static boolean  _checkconnecttime() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "checkconnecttime", false))
	 {return ((Boolean) Debug.delegate(mostCurrent.activityBA, "checkconnecttime", null));}
RDebugUtils.currentLine=5701632;
 //BA.debugLineNum = 5701632;BA.debugLine="Sub checkConnectTime As Boolean";
RDebugUtils.currentLine=5701633;
 //BA.debugLineNum = 5701633;BA.debugLine="If connectTime <> -1 Then";
if (_connecttime!=-1) { 
RDebugUtils.currentLine=5701634;
 //BA.debugLineNum = 5701634;BA.debugLine="pnlNobords.Visible = True";
mostCurrent._pnlnobords.setVisible(anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=5701635;
 //BA.debugLineNum = 5701635;BA.debugLine="mqttBase.Disconnect";
_mqttbase._disconnect /*String*/ (null);
RDebugUtils.currentLine=5701636;
 //BA.debugLineNum = 5701636;BA.debugLine="tmrBordLastAlive.Enabled = False";
_tmrbordlastalive.setEnabled(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=5701637;
 //BA.debugLineNum = 5701637;BA.debugLine="pnlNobords.Visible = False";
mostCurrent._pnlnobords.setVisible(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=5701638;
 //BA.debugLineNum = 5701638;BA.debugLine="Msgbox2Async(\"Geen borden gevonden\", Application";
anywheresoftware.b4a.keywords.Common.Msgbox2Async(BA.ObjectToCharSequence("Geen borden gevonden"),BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Application.getLabelName()),"OKE","","",anywheresoftware.b4a.keywords.Common.Application.getIcon(),processBA,anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=5701639;
 //BA.debugLineNum = 5701639;BA.debugLine="Return False";
if (true) return anywheresoftware.b4a.keywords.Common.False;
 };
RDebugUtils.currentLine=5701641;
 //BA.debugLineNum = 5701641;BA.debugLine="Return True";
if (true) return anywheresoftware.b4a.keywords.Common.True;
RDebugUtils.currentLine=5701642;
 //BA.debugLineNum = 5701642;BA.debugLine="End Sub";
return false;
}
public static String  _checkipexits(nl.pdeg.bordondroid.main._message _bord) throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "checkipexits", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "checkipexits", new Object[] {_bord}));}
boolean _bordexists = false;
String _name = "";
nl.pdeg.bordondroid.main._bordstatus _lst = null;
RDebugUtils.currentLine=6815744;
 //BA.debugLineNum = 6815744;BA.debugLine="Sub CheckIpExits(bord As Message)";
RDebugUtils.currentLine=6815745;
 //BA.debugLineNum = 6815745;BA.debugLine="Dim bordExists As Boolean = False";
_bordexists = anywheresoftware.b4a.keywords.Common.False;
RDebugUtils.currentLine=6815747;
 //BA.debugLineNum = 6815747;BA.debugLine="If bord.Body.Length = 0 Then Return";
if (_bord.Body /*String*/ .length()==0) { 
if (true) return "";};
RDebugUtils.currentLine=6815748;
 //BA.debugLineNum = 6815748;BA.debugLine="Dim name As String = bord.Body";
_name = _bord.Body /*String*/ ;
RDebugUtils.currentLine=6815750;
 //BA.debugLineNum = 6815750;BA.debugLine="connectTime = -1";
_connecttime = (long) (-1);
RDebugUtils.currentLine=6815752;
 //BA.debugLineNum = 6815752;BA.debugLine="If Starter.serverList.Size = 0 Then";
if (mostCurrent._starter._serverlist /*anywheresoftware.b4a.objects.collections.List*/ .getSize()==0) { 
RDebugUtils.currentLine=6815753;
 //BA.debugLineNum = 6815753;BA.debugLine="AddUnkownIp(\"\", name)";
_addunkownip("",_name);
RDebugUtils.currentLine=6815754;
 //BA.debugLineNum = 6815754;BA.debugLine="Return";
if (true) return "";
 };
RDebugUtils.currentLine=6815757;
 //BA.debugLineNum = 6815757;BA.debugLine="For Each lst As bordStatus In Starter.serverList";
{
final anywheresoftware.b4a.BA.IterableList group9 = mostCurrent._starter._serverlist /*anywheresoftware.b4a.objects.collections.List*/ ;
final int groupLen9 = group9.getSize()
;int index9 = 0;
;
for (; index9 < groupLen9;index9++){
_lst = (nl.pdeg.bordondroid.main._bordstatus)(group9.Get(index9));
RDebugUtils.currentLine=6815758;
 //BA.debugLineNum = 6815758;BA.debugLine="If lst.name = name Then";
if ((_lst.name /*String*/ ).equals(_name)) { 
RDebugUtils.currentLine=6815759;
 //BA.debugLineNum = 6815759;BA.debugLine="bordExists = True";
_bordexists = anywheresoftware.b4a.keywords.Common.True;
RDebugUtils.currentLine=6815760;
 //BA.debugLineNum = 6815760;BA.debugLine="lst.timeStamp = DateTime.Now";
_lst.timeStamp /*long*/  = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
RDebugUtils.currentLine=6815761;
 //BA.debugLineNum = 6815761;BA.debugLine="lst.alive = True";
_lst.alive /*boolean*/  = anywheresoftware.b4a.keywords.Common.True;
 };
 }
};
RDebugUtils.currentLine=6815765;
 //BA.debugLineNum = 6815765;BA.debugLine="If Not(bordExists) Then";
if (anywheresoftware.b4a.keywords.Common.Not(_bordexists)) { 
RDebugUtils.currentLine=6815766;
 //BA.debugLineNum = 6815766;BA.debugLine="AddUnkownIp(\"\", name)";
_addunkownip("",_name);
RDebugUtils.currentLine=6815767;
 //BA.debugLineNum = 6815767;BA.debugLine="Return";
if (true) return "";
 };
RDebugUtils.currentLine=6815769;
 //BA.debugLineNum = 6815769;BA.debugLine="End Sub";
return "";
}
public static anywheresoftware.b4a.objects.PanelWrapper  _genunitlist(String _name,boolean _alive) throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "genunitlist", false))
	 {return ((anywheresoftware.b4a.objects.PanelWrapper) Debug.delegate(mostCurrent.activityBA, "genunitlist", new Object[] {_name,_alive}));}
anywheresoftware.b4a.objects.PanelWrapper _p = null;
RDebugUtils.currentLine=6750208;
 //BA.debugLineNum = 6750208;BA.debugLine="Sub genUnitList(name As String, alive As Boolean)";
RDebugUtils.currentLine=6750209;
 //BA.debugLineNum = 6750209;BA.debugLine="Dim p As Panel";
_p = new anywheresoftware.b4a.objects.PanelWrapper();
RDebugUtils.currentLine=6750210;
 //BA.debugLineNum = 6750210;BA.debugLine="p.Initialize(Me)";
_p.Initialize(mostCurrent.activityBA,BA.ObjectToString(main.getObject()));
RDebugUtils.currentLine=6750211;
 //BA.debugLineNum = 6750211;BA.debugLine="p.SetLayout(0dip, 0dip, clvServer.AsView.Width, 9";
_p.SetLayout(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)),mostCurrent._clvserver._asview().getWidth(),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (95)));
RDebugUtils.currentLine=6750212;
 //BA.debugLineNum = 6750212;BA.debugLine="p.LoadLayout(\"clvBorden\")";
_p.LoadLayout("clvBorden",mostCurrent.activityBA);
RDebugUtils.currentLine=6750213;
 //BA.debugLineNum = 6750213;BA.debugLine="p.Tag = name";
_p.setTag((Object)(_name));
RDebugUtils.currentLine=6750215;
 //BA.debugLineNum = 6750215;BA.debugLine="lblBordName.Text = name.Trim";
mostCurrent._lblbordname.setText(BA.ObjectToCharSequence(_name.trim()));
RDebugUtils.currentLine=6750216;
 //BA.debugLineNum = 6750216;BA.debugLine="lblLastCheck.Text = $\"Laatste controle $Time{Date";
mostCurrent._lbllastcheck.setText(BA.ObjectToCharSequence(("Laatste controle "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("time",(Object)(anywheresoftware.b4a.keywords.Common.DateTime.getNow()))+"")));
RDebugUtils.currentLine=6750217;
 //BA.debugLineNum = 6750217;BA.debugLine="BordDiedProgress.Show";
mostCurrent._borddiedprogress._show /*String*/ (null);
RDebugUtils.currentLine=6750218;
 //BA.debugLineNum = 6750218;BA.debugLine="If Not(alive) Then";
if (anywheresoftware.b4a.keywords.Common.Not(_alive)) { 
RDebugUtils.currentLine=6750219;
 //BA.debugLineNum = 6750219;BA.debugLine="lblViewBord.Enabled = False";
mostCurrent._lblviewbord.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 };
RDebugUtils.currentLine=6750221;
 //BA.debugLineNum = 6750221;BA.debugLine="Return p";
if (true) return _p;
RDebugUtils.currentLine=6750222;
 //BA.debugLineNum = 6750222;BA.debugLine="End Sub";
return null;
}
public static String  _checklastalivetime() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "checklastalivetime", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "checklastalivetime", null));}
long _currtime = 0L;
long _timediff = 0L;
nl.pdeg.bordondroid.main._bordstatus _bd = null;
RDebugUtils.currentLine=5832704;
 //BA.debugLineNum = 5832704;BA.debugLine="Sub CheckLastAliveTime";
RDebugUtils.currentLine=5832705;
 //BA.debugLineNum = 5832705;BA.debugLine="Dim currTime As Long = DateTime.Now";
_currtime = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
RDebugUtils.currentLine=5832706;
 //BA.debugLineNum = 5832706;BA.debugLine="Dim timeDiff As Long";
_timediff = 0L;
RDebugUtils.currentLine=5832709;
 //BA.debugLineNum = 5832709;BA.debugLine="If lblLastCheck.IsInitialized = False Then";
if (mostCurrent._lbllastcheck.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
RDebugUtils.currentLine=5832710;
 //BA.debugLineNum = 5832710;BA.debugLine="lblNoBord.Visible = False";
mostCurrent._lblnobord.setVisible(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=5832711;
 //BA.debugLineNum = 5832711;BA.debugLine="Return";
if (true) return "";
 };
RDebugUtils.currentLine=5832714;
 //BA.debugLineNum = 5832714;BA.debugLine="lblLastCheck.Text = $\"Laatste controle $Time{Date";
mostCurrent._lbllastcheck.setText(BA.ObjectToCharSequence(("Laatste controle "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("time",(Object)(anywheresoftware.b4a.keywords.Common.DateTime.getNow()))+"")));
RDebugUtils.currentLine=5832716;
 //BA.debugLineNum = 5832716;BA.debugLine="For Each bd As bordStatus In Starter.serverList";
{
final anywheresoftware.b4a.BA.IterableList group8 = mostCurrent._starter._serverlist /*anywheresoftware.b4a.objects.collections.List*/ ;
final int groupLen8 = group8.getSize()
;int index8 = 0;
;
for (; index8 < groupLen8;index8++){
_bd = (nl.pdeg.bordondroid.main._bordstatus)(group8.Get(index8));
RDebugUtils.currentLine=5832717;
 //BA.debugLineNum = 5832717;BA.debugLine="timeDiff = currTime-bd.timeStamp";
_timediff = (long) (_currtime-_bd.timeStamp /*long*/ );
RDebugUtils.currentLine=5832718;
 //BA.debugLineNum = 5832718;BA.debugLine="BordDiedProgress.Show";
mostCurrent._borddiedprogress._show /*String*/ (null);
RDebugUtils.currentLine=5832719;
 //BA.debugLineNum = 5832719;BA.debugLine="If timeDiff >= Starter.serverDied Then";
if (_timediff>=mostCurrent._starter._serverdied /*long*/ ) { 
RDebugUtils.currentLine=5832720;
 //BA.debugLineNum = 5832720;BA.debugLine="bd.alive = False";
_bd.alive /*boolean*/  = anywheresoftware.b4a.keywords.Common.False;
RDebugUtils.currentLine=5832721;
 //BA.debugLineNum = 5832721;BA.debugLine="baseFile.SetBordDiedByName(bd.name, clvServer,";
_basefile._setborddiedbyname /*String*/ (null,_bd.name /*String*/ ,mostCurrent._clvserver,_bd.alive /*boolean*/ );
 }else {
RDebugUtils.currentLine=5832723;
 //BA.debugLineNum = 5832723;BA.debugLine="baseFile.SetBordDiedByName(bd.name, clvServer,";
_basefile._setborddiedbyname /*String*/ (null,_bd.name /*String*/ ,mostCurrent._clvserver,_bd.alive /*boolean*/ );
 };
 }
};
RDebugUtils.currentLine=5832728;
 //BA.debugLineNum = 5832728;BA.debugLine="End Sub";
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
RDebugUtils.currentLine=5505025;
 //BA.debugLineNum = 5505025;BA.debugLine="Dim p As Panel = clvServer.GetPanel(Index)";
_p = new anywheresoftware.b4a.objects.PanelWrapper();
_p.setObject((android.view.ViewGroup)(parent.mostCurrent._clvserver._getpanel(_index).getObject()));
RDebugUtils.currentLine=5505026;
 //BA.debugLineNum = 5505026;BA.debugLine="Dim unit As String";
_unit = "";
RDebugUtils.currentLine=5505028;
 //BA.debugLineNum = 5505028;BA.debugLine="unit = baseFile.GetSelectedLabelTagFromPanel(p, \"";
_unit = parent._basefile._getselectedlabeltagfrompanel /*String*/ (null,_p,"name");
RDebugUtils.currentLine=5505029;
 //BA.debugLineNum = 5505029;BA.debugLine="If baseFile.GetBordAlive(unit) = False Then";
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
RDebugUtils.currentLine=5505030;
 //BA.debugLineNum = 5505030;BA.debugLine="baseFile.ShowCustomToast($\"Bord niet gevonden, c";
parent._basefile._showcustomtoast /*String*/ (null,(Object)(("Bord niet gevonden, controleer of het bord \"online\" is")),anywheresoftware.b4a.keywords.Common.False,anywheresoftware.b4a.keywords.Common.Colors.Red);
RDebugUtils.currentLine=5505031;
 //BA.debugLineNum = 5505031;BA.debugLine="Return";
if (true) return ;
 if (true) break;

case 4:
//C
this.state = -1;
;
RDebugUtils.currentLine=5505034;
 //BA.debugLineNum = 5505034;BA.debugLine="Starter.DiscoveredServer = unit";
parent.mostCurrent._starter._discoveredserver /*String*/  = _unit;
RDebugUtils.currentLine=5505035;
 //BA.debugLineNum = 5505035;BA.debugLine="CallSub2(Starter, \"SetLastWill\", \"recvdied\")";
anywheresoftware.b4a.keywords.Common.CallSubDebug2(processBA,(Object)(parent.mostCurrent._starter.getObject()),"SetLastWill",(Object)("recvdied"));
RDebugUtils.currentLine=5505036;
 //BA.debugLineNum = 5505036;BA.debugLine="CallSub2(Starter, \"SetUnit\", PrepTopicName(unit).";
anywheresoftware.b4a.keywords.Common.CallSubDebug2(processBA,(Object)(parent.mostCurrent._starter.getObject()),"SetUnit",(Object)(_preptopicname(_unit).toLowerCase()));
RDebugUtils.currentLine=5505038;
 //BA.debugLineNum = 5505038;BA.debugLine="DisconnectMqtt";
_disconnectmqtt();
RDebugUtils.currentLine=5505039;
 //BA.debugLineNum = 5505039;BA.debugLine="Sleep(100)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "main", "clvserver_itemclick"),(int) (100));
this.state = 5;
return;
case 5:
//C
this.state = -1;
;
RDebugUtils.currentLine=5505040;
 //BA.debugLineNum = 5505040;BA.debugLine="StartActivity(ServerBoard)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(parent.mostCurrent._serverboard.getObject()));
RDebugUtils.currentLine=5505041;
 //BA.debugLineNum = 5505041;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static String  _preptopicname(String _bord) throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "preptopicname", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "preptopicname", new Object[] {_bord}));}
RDebugUtils.currentLine=7536640;
 //BA.debugLineNum = 7536640;BA.debugLine="Private Sub PrepTopicName(bord As String) As Strin";
RDebugUtils.currentLine=7536641;
 //BA.debugLineNum = 7536641;BA.debugLine="Return bord.Replace(\" \", \"\")";
if (true) return _bord.replace(" ","");
RDebugUtils.currentLine=7536642;
 //BA.debugLineNum = 7536642;BA.debugLine="End Sub";
return "";
}
public static String  _connectionerror() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "connectionerror", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "connectionerror", null));}
RDebugUtils.currentLine=9371648;
 //BA.debugLineNum = 9371648;BA.debugLine="Sub ConnectionError";
RDebugUtils.currentLine=9371649;
 //BA.debugLineNum = 9371649;BA.debugLine="baseFile.ShowCustomToast(\"MQTT Fout\", True, Color";
_basefile._showcustomtoast /*String*/ (null,(Object)("MQTT Fout"),anywheresoftware.b4a.keywords.Common.True,anywheresoftware.b4a.keywords.Common.Colors.Red);
RDebugUtils.currentLine=9371651;
 //BA.debugLineNum = 9371651;BA.debugLine="End Sub";
return "";
}
public static String  _deletedlocationactive() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "deletedlocationactive", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "deletedlocationactive", null));}
RDebugUtils.currentLine=9043968;
 //BA.debugLineNum = 9043968;BA.debugLine="Sub DeletedLocationActive";
RDebugUtils.currentLine=9043969;
 //BA.debugLineNum = 9043969;BA.debugLine="mqttBase.Disconnect";
_mqttbase._disconnect /*String*/ (null);
RDebugUtils.currentLine=9043970;
 //BA.debugLineNum = 9043970;BA.debugLine="clvServer.Clear";
mostCurrent._clvserver._clear();
RDebugUtils.currentLine=9043971;
 //BA.debugLineNum = 9043971;BA.debugLine="getBaseList";
_getbaselist();
RDebugUtils.currentLine=9043972;
 //BA.debugLineNum = 9043972;BA.debugLine="End Sub";
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
RDebugUtils.currentLine=4849665;
 //BA.debugLineNum = 4849665;BA.debugLine="Dim listSize As Int = 0";
_listsize = (int) (0);
RDebugUtils.currentLine=4849667;
 //BA.debugLineNum = 4849667;BA.debugLine="baseList.Initialize";
parent._baselist.Initialize();
RDebugUtils.currentLine=4849668;
 //BA.debugLineNum = 4849668;BA.debugLine="baseList = baseFile.GetBase";
parent._baselist = parent._basefile._getbase /*anywheresoftware.b4a.objects.collections.List*/ (null);
RDebugUtils.currentLine=4849670;
 //BA.debugLineNum = 4849670;BA.debugLine="If CallSub(baseFile, \"CheckBaseListExists\") = Fal";
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
RDebugUtils.currentLine=4849671;
 //BA.debugLineNum = 4849671;BA.debugLine="pnlLocationCOde.Visible = True";
parent.mostCurrent._pnllocationcode.setVisible(anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=4849672;
 //BA.debugLineNum = 4849672;BA.debugLine="Return";
if (true) return ;
 if (true) break;
;
RDebugUtils.currentLine=4849675;
 //BA.debugLineNum = 4849675;BA.debugLine="If baseList.IsInitialized Then";

case 4:
//if
this.state = 7;
if (parent._baselist.IsInitialized()) { 
this.state = 6;
}if (true) break;

case 6:
//C
this.state = 7;
RDebugUtils.currentLine=4849676;
 //BA.debugLineNum = 4849676;BA.debugLine="listSize = baseList.Size";
_listsize = parent._baselist.getSize();
 if (true) break;
;
RDebugUtils.currentLine=4849678;
 //BA.debugLineNum = 4849678;BA.debugLine="If listSize > 1 Then";

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
RDebugUtils.currentLine=4849680;
 //BA.debugLineNum = 4849680;BA.debugLine="pnlLocationList.SetVisibleAnimated(500, True)";
parent.mostCurrent._pnllocationlist.SetVisibleAnimated((int) (500),anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=4849681;
 //BA.debugLineNum = 4849681;BA.debugLine="Sleep(200)";
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
RDebugUtils.currentLine=4849683;
 //BA.debugLineNum = 4849683;BA.debugLine="pnlLocationList.SetVisibleAnimated(500, False)";
parent.mostCurrent._pnllocationlist.SetVisibleAnimated((int) (500),anywheresoftware.b4a.keywords.Common.False);
 if (true) break;

case 12:
//C
this.state = 13;
;
RDebugUtils.currentLine=4849687;
 //BA.debugLineNum = 4849687;BA.debugLine="lblNoBord.Text = \"Wachten op borden...\"";
parent.mostCurrent._lblnobord.setText(BA.ObjectToCharSequence("Wachten op borden..."));
RDebugUtils.currentLine=4849689;
 //BA.debugLineNum = 4849689;BA.debugLine="If baseList.IsInitialized = False Then";
if (true) break;

case 13:
//if
this.state = 20;
if (parent._baselist.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
this.state = 15;
}else 
{RDebugUtils.currentLine=4849691;
 //BA.debugLineNum = 4849691;BA.debugLine="Else If baseList.Size = 1 Then";
if (parent._baselist.getSize()==1) { 
this.state = 17;
}else {
this.state = 19;
}}
if (true) break;

case 15:
//C
this.state = 20;
RDebugUtils.currentLine=4849690;
 //BA.debugLineNum = 4849690;BA.debugLine="pnlLocationCOde.Visible = True";
parent.mostCurrent._pnllocationcode.setVisible(anywheresoftware.b4a.keywords.Common.True);
 if (true) break;

case 17:
//C
this.state = 20;
RDebugUtils.currentLine=4849692;
 //BA.debugLineNum = 4849692;BA.debugLine="Dim loc As locationBord";
_loc = new nl.pdeg.bordondroid.main._locationbord();
RDebugUtils.currentLine=4849693;
 //BA.debugLineNum = 4849693;BA.debugLine="loc.Initialize";
_loc.Initialize();
RDebugUtils.currentLine=4849694;
 //BA.debugLineNum = 4849694;BA.debugLine="loc = baseList.Get(0)";
_loc = (nl.pdeg.bordondroid.main._locationbord)(parent._baselist.Get((int) (0)));
RDebugUtils.currentLine=4849695;
 //BA.debugLineNum = 4849695;BA.debugLine="Starter.selectedLocationCode = loc.code";
parent.mostCurrent._starter._selectedlocationcode /*String*/  = _loc.code /*String*/ ;
RDebugUtils.currentLine=4849696;
 //BA.debugLineNum = 4849696;BA.debugLine="Starter.selectedLocationDescription = loc.descri";
parent.mostCurrent._starter._selectedlocationdescription /*String*/  = _loc.description /*String*/ ;
RDebugUtils.currentLine=4849697;
 //BA.debugLineNum = 4849697;BA.debugLine="lblCurrLocation.Text = loc.description";
parent.mostCurrent._lblcurrlocation.setText(BA.ObjectToCharSequence(_loc.description /*String*/ ));
RDebugUtils.currentLine=4849698;
 //BA.debugLineNum = 4849698;BA.debugLine="InitConnection";
_initconnection();
 if (true) break;

case 19:
//C
this.state = 20;
RDebugUtils.currentLine=4849700;
 //BA.debugLineNum = 4849700;BA.debugLine="StartActivity(SelectLocation)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(parent.mostCurrent._selectlocation.getObject()));
 if (true) break;

case 20:
//C
this.state = -1;
;
RDebugUtils.currentLine=4849702;
 //BA.debugLineNum = 4849702;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static String  _edtfloatcode_textchanged(String _old,String _new) throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "edtfloatcode_textchanged", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "edtfloatcode_textchanged", new Object[] {_old,_new}));}
RDebugUtils.currentLine=8716288;
 //BA.debugLineNum = 8716288;BA.debugLine="Sub edtFloatCode_TextChanged (Old As String, New A";
RDebugUtils.currentLine=8716289;
 //BA.debugLineNum = 8716289;BA.debugLine="If New.Length > 0 Then";
if (_new.length()>0) { 
RDebugUtils.currentLine=8716290;
 //BA.debugLineNum = 8716290;BA.debugLine="btnOk.Enabled = True";
mostCurrent._btnok.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 }else {
RDebugUtils.currentLine=8716292;
 //BA.debugLineNum = 8716292;BA.debugLine="btnOk.Enabled = False";
mostCurrent._btnok.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 };
RDebugUtils.currentLine=8716294;
 //BA.debugLineNum = 8716294;BA.debugLine="End Sub";
return "";
}
public static String  _edtfloatdescription_textchanged(String _old,String _new) throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "edtfloatdescription_textchanged", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "edtfloatdescription_textchanged", new Object[] {_old,_new}));}
RDebugUtils.currentLine=8585216;
 //BA.debugLineNum = 8585216;BA.debugLine="Sub edtFloatDescription_TextChanged (Old As String";
RDebugUtils.currentLine=8585218;
 //BA.debugLineNum = 8585218;BA.debugLine="End Sub";
return "";
}
public static String  _edtlocationcode_textchanged(String _old,String _new) throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "edtlocationcode_textchanged", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "edtlocationcode_textchanged", new Object[] {_old,_new}));}
RDebugUtils.currentLine=7929856;
 //BA.debugLineNum = 7929856;BA.debugLine="Sub edtLocationCode_TextChanged (Old As String, Ne";
RDebugUtils.currentLine=7929857;
 //BA.debugLineNum = 7929857;BA.debugLine="If New.Length > 0 Then";
if (_new.length()>0) { 
RDebugUtils.currentLine=7929858;
 //BA.debugLineNum = 7929858;BA.debugLine="btnOk.Enabled = True";
mostCurrent._btnok.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 }else {
RDebugUtils.currentLine=7929860;
 //BA.debugLineNum = 7929860;BA.debugLine="btnOk.Enabled = False";
mostCurrent._btnok.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 };
RDebugUtils.currentLine=7929862;
 //BA.debugLineNum = 7929862;BA.debugLine="End Sub";
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
RDebugUtils.currentLine=4587521;
 //BA.debugLineNum = 4587521;BA.debugLine="pnlLocationCOde.Visible = False";
parent.mostCurrent._pnllocationcode.setVisible(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=4587522;
 //BA.debugLineNum = 4587522;BA.debugLine="B4XLoadingIndicator1.Show";
parent.mostCurrent._b4xloadingindicator1._show /*String*/ (null);
RDebugUtils.currentLine=4587523;
 //BA.debugLineNum = 4587523;BA.debugLine="B4XLoadingIndicator2.Show";
parent.mostCurrent._b4xloadingindicator2._show /*String*/ (null);
RDebugUtils.currentLine=4587524;
 //BA.debugLineNum = 4587524;BA.debugLine="Sleep(10)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "main", "startconnection"),(int) (10));
this.state = 1;
return;
case 1:
//C
this.state = -1;
;
RDebugUtils.currentLine=4587525;
 //BA.debugLineNum = 4587525;BA.debugLine="pnlNobords.Visible = True";
parent.mostCurrent._pnlnobords.setVisible(anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=4587526;
 //BA.debugLineNum = 4587526;BA.debugLine="clvServer.Clear";
parent.mostCurrent._clvserver._clear();
RDebugUtils.currentLine=4587527;
 //BA.debugLineNum = 4587527;BA.debugLine="mqttBase.Initialize";
parent._mqttbase._initialize /*String*/ (null,processBA);
RDebugUtils.currentLine=4587528;
 //BA.debugLineNum = 4587528;BA.debugLine="mqttBase.Connect";
parent._mqttbase._connect /*String*/ (null);
RDebugUtils.currentLine=4587530;
 //BA.debugLineNum = 4587530;BA.debugLine="baseFile.GetBase";
parent._basefile._getbase /*anywheresoftware.b4a.objects.collections.List*/ (null);
RDebugUtils.currentLine=4587532;
 //BA.debugLineNum = 4587532;BA.debugLine="tmrBordLastAlive.Initialize(\"tmrBordAlive\", 10000";
parent._tmrbordlastalive.Initialize(processBA,"tmrBordAlive",(long) (10000));
RDebugUtils.currentLine=4587533;
 //BA.debugLineNum = 4587533;BA.debugLine="tmrBordLastAlive.Enabled = True";
parent._tmrbordlastalive.setEnabled(anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=4587534;
 //BA.debugLineNum = 4587534;BA.debugLine="connectTime = DateTime.Now";
parent._connecttime = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
RDebugUtils.currentLine=4587536;
 //BA.debugLineNum = 4587536;BA.debugLine="End Sub";
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
RDebugUtils.currentLine=7405568;
 //BA.debugLineNum = 7405568;BA.debugLine="Sub lblViewBord_Click";
RDebugUtils.currentLine=7405569;
 //BA.debugLineNum = 7405569;BA.debugLine="Dim v As View = Sender";
_v = new anywheresoftware.b4a.objects.ConcreteViewWrapper();
_v.setObject((android.view.View)(anywheresoftware.b4a.keywords.Common.Sender(mostCurrent.activityBA)));
RDebugUtils.currentLine=7405570;
 //BA.debugLineNum = 7405570;BA.debugLine="Dim p As Panel = v.Parent";
_p = new anywheresoftware.b4a.objects.PanelWrapper();
_p.setObject((android.view.ViewGroup)(_v.getParent()));
RDebugUtils.currentLine=7405572;
 //BA.debugLineNum = 7405572;BA.debugLine="clvServer_ItemClick (clvServer.GetItemFromView(p)";
_clvserver_itemclick(mostCurrent._clvserver._getitemfromview((anywheresoftware.b4a.objects.B4XViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.B4XViewWrapper(), (java.lang.Object)(_p.getObject()))),anywheresoftware.b4a.keywords.Common.Null);
RDebugUtils.currentLine=7405573;
 //BA.debugLineNum = 7405573;BA.debugLine="End Sub";
return "";
}
public static String  _pnllocation_click() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "pnllocation_click", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "pnllocation_click", null));}
RDebugUtils.currentLine=8060928;
 //BA.debugLineNum = 8060928;BA.debugLine="Sub pnlLocation_Click";
RDebugUtils.currentLine=8060930;
 //BA.debugLineNum = 8060930;BA.debugLine="If CheckClientConnected Then";
if (_checkclientconnected()) { 
RDebugUtils.currentLine=8060931;
 //BA.debugLineNum = 8060931;BA.debugLine="mqttBase.Disconnect";
_mqttbase._disconnect /*String*/ (null);
 };
RDebugUtils.currentLine=8060933;
 //BA.debugLineNum = 8060933;BA.debugLine="StartActivity(locations)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(mostCurrent._locations.getObject()));
RDebugUtils.currentLine=8060934;
 //BA.debugLineNum = 8060934;BA.debugLine="End Sub";
return "";
}
public static String  _pnllocationlist_click() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "pnllocationlist_click", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "pnllocationlist_click", null));}
RDebugUtils.currentLine=8388608;
 //BA.debugLineNum = 8388608;BA.debugLine="Sub pnlLocationList_Click";
RDebugUtils.currentLine=8388609;
 //BA.debugLineNum = 8388609;BA.debugLine="StartActivity(SelectLocation)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(mostCurrent._selectlocation.getObject()));
RDebugUtils.currentLine=8388610;
 //BA.debugLineNum = 8388610;BA.debugLine="End Sub";
return "";
}
public static String  _reconnecttolocation() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "reconnecttolocation", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "reconnecttolocation", null));}
RDebugUtils.currentLine=5308416;
 //BA.debugLineNum = 5308416;BA.debugLine="Sub ReconnectToLocation";
RDebugUtils.currentLine=5308417;
 //BA.debugLineNum = 5308417;BA.debugLine="mqttBase.Initialize";
_mqttbase._initialize /*String*/ (null,processBA);
RDebugUtils.currentLine=5308418;
 //BA.debugLineNum = 5308418;BA.debugLine="CallSub2(Starter, \"SetLastWill\", \"bordpubdied\")";
anywheresoftware.b4a.keywords.Common.CallSubDebug2(processBA,(Object)(mostCurrent._starter.getObject()),"SetLastWill",(Object)("bordpubdied"));
RDebugUtils.currentLine=5308419;
 //BA.debugLineNum = 5308419;BA.debugLine="CallSub2(Starter, \"SetSubBase\", Starter.selectedL";
anywheresoftware.b4a.keywords.Common.CallSubDebug2(processBA,(Object)(mostCurrent._starter.getObject()),"SetSubBase",(Object)(mostCurrent._starter._selectedlocationcode /*String*/ ));
RDebugUtils.currentLine=5308420;
 //BA.debugLineNum = 5308420;BA.debugLine="CallSub2(Starter, \"SetSubString2\", \"/pubbord\")";
anywheresoftware.b4a.keywords.Common.CallSubDebug2(processBA,(Object)(mostCurrent._starter.getObject()),"SetSubString2",(Object)("/pubbord"));
RDebugUtils.currentLine=5308421;
 //BA.debugLineNum = 5308421;BA.debugLine="CallSub2(Starter, \"SetUnsubscribeString2\", \"\")";
anywheresoftware.b4a.keywords.Common.CallSubDebug2(processBA,(Object)(mostCurrent._starter.getObject()),"SetUnsubscribeString2",(Object)(""));
RDebugUtils.currentLine=5308422;
 //BA.debugLineNum = 5308422;BA.debugLine="mqttBase.Connect";
_mqttbase._connect /*String*/ (null);
RDebugUtils.currentLine=5308423;
 //BA.debugLineNum = 5308423;BA.debugLine="End Sub";
return "";
}
public static String  _setbordlastalivetimer() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "setbordlastalivetimer", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "setbordlastalivetimer", null));}
RDebugUtils.currentLine=4521984;
 //BA.debugLineNum = 4521984;BA.debugLine="Sub setBordLastAliveTimer";
RDebugUtils.currentLine=4521985;
 //BA.debugLineNum = 4521985;BA.debugLine="tmrBordLastAlive.Enabled = True";
_tmrbordlastalive.setEnabled(anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=4521987;
 //BA.debugLineNum = 4521987;BA.debugLine="End Sub";
return "";
}
public static String  _shownotconnectedtobroker() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "shownotconnectedtobroker", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "shownotconnectedtobroker", null));}
RDebugUtils.currentLine=6094848;
 //BA.debugLineNum = 6094848;BA.debugLine="Sub ShowNotConnectedToBroker";
RDebugUtils.currentLine=6094849;
 //BA.debugLineNum = 6094849;BA.debugLine="lblNoBord.Text = \"Verbinding borden verbroken\"";
mostCurrent._lblnobord.setText(BA.ObjectToCharSequence("Verbinding borden verbroken"));
RDebugUtils.currentLine=6094850;
 //BA.debugLineNum = 6094850;BA.debugLine="pnlNobords.SetVisibleAnimated(1000, True)";
mostCurrent._pnlnobords.SetVisibleAnimated((int) (1000),anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=6094851;
 //BA.debugLineNum = 6094851;BA.debugLine="End Sub";
return "";
}
public static String  _tmrbordalive_tick() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "tmrbordalive_tick", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "tmrbordalive_tick", null));}
RDebugUtils.currentLine=5570560;
 //BA.debugLineNum = 5570560;BA.debugLine="Sub tmrBordAlive_Tick";
RDebugUtils.currentLine=5570561;
 //BA.debugLineNum = 5570561;BA.debugLine="If Not(checkConnectTime) Then Return";
if (anywheresoftware.b4a.keywords.Common.Not(_checkconnecttime())) { 
if (true) return "";};
RDebugUtils.currentLine=5570562;
 //BA.debugLineNum = 5570562;BA.debugLine="CheckLastAliveTime";
_checklastalivetime();
RDebugUtils.currentLine=5570563;
 //BA.debugLineNum = 5570563;BA.debugLine="End Sub";
return "";
}
}