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
	public static final boolean fullScreen = true;
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

BA.applicationContext.stopService(new android.content.Intent(BA.applicationContext, starter.class));
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
public static nl.pdeg.bordondroid.mqttgetbords _mqttgetbord = null;
public static nl.pdeg.bordondroid.mqttgetborddata _mqttgetdata = null;
public static anywheresoftware.b4a.objects.Timer _tmrbordlastalive = null;
public static nl.pdeg.bordondroid.base _basefile = null;
public static anywheresoftware.b4a.objects.collections.List _baselist = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblbordname = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblviewbord = null;
public anywheresoftware.b4a.objects.PanelWrapper _pnlbord = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbllastcheck = null;
public anywheresoftware.b4a.objects.PanelWrapper _pnlnobords = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblversion = null;
public anywheresoftware.b4a.objects.PanelWrapper _pnllocationcode = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edtlocationcode = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btncancel = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnok = null;
public anywheresoftware.b4a.objects.PanelWrapper _pnllocation = null;
public b4a.example3.customlistview _clvserver = null;
public b4a.example.dateutils _dateutils = null;
public nl.pdeg.bordondroid.locations _locations = null;
public nl.pdeg.bordondroid.serverboard _serverboard = null;
public nl.pdeg.bordondroid.starter _starter = null;
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
 //BA.debugLineNum = 131075;BA.debugLine="baseFile.Initialize";
_basefile._initialize /*String*/ (null,processBA);
RDebugUtils.currentLine=131078;
 //BA.debugLineNum = 131078;BA.debugLine="lblVersion.Text = \"v\"&Application.VersionName";
mostCurrent._lblversion.setText(BA.ObjectToCharSequence("v"+anywheresoftware.b4a.keywords.Common.Application.getVersionName()));
RDebugUtils.currentLine=131079;
 //BA.debugLineNum = 131079;BA.debugLine="pnlNobords.Visible = False";
mostCurrent._pnlnobords.setVisible(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=131081;
 //BA.debugLineNum = 131081;BA.debugLine="getBaseList";
_getbaselist();
RDebugUtils.currentLine=131083;
 //BA.debugLineNum = 131083;BA.debugLine="End Sub";
return "";
}
public static String  _getbaselist() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "getbaselist", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "getbaselist", null));}
nl.pdeg.bordondroid.main._locationbord _loc = null;
RDebugUtils.currentLine=262144;
 //BA.debugLineNum = 262144;BA.debugLine="Private Sub getBaseList";
RDebugUtils.currentLine=262145;
 //BA.debugLineNum = 262145;BA.debugLine="baseList.Initialize";
_baselist.Initialize();
RDebugUtils.currentLine=262146;
 //BA.debugLineNum = 262146;BA.debugLine="baseList = baseFile.GetBase";
_baselist = _basefile._getbase /*anywheresoftware.b4a.objects.collections.List*/ (null);
RDebugUtils.currentLine=262148;
 //BA.debugLineNum = 262148;BA.debugLine="If baseList.IsInitialized = False Then";
if (_baselist.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
RDebugUtils.currentLine=262149;
 //BA.debugLineNum = 262149;BA.debugLine="pnlLocationCOde.Visible = True";
mostCurrent._pnllocationcode.setVisible(anywheresoftware.b4a.keywords.Common.True);
 }else 
{RDebugUtils.currentLine=262150;
 //BA.debugLineNum = 262150;BA.debugLine="Else If baseList.Size = 1 Then";
if (_baselist.getSize()==1) { 
RDebugUtils.currentLine=262151;
 //BA.debugLineNum = 262151;BA.debugLine="Dim loc As locationBord";
_loc = new nl.pdeg.bordondroid.main._locationbord();
RDebugUtils.currentLine=262152;
 //BA.debugLineNum = 262152;BA.debugLine="loc.Initialize";
_loc.Initialize();
RDebugUtils.currentLine=262153;
 //BA.debugLineNum = 262153;BA.debugLine="loc = baseList.Get(0)";
_loc = (nl.pdeg.bordondroid.main._locationbord)(_baselist.Get((int) (0)));
RDebugUtils.currentLine=262154;
 //BA.debugLineNum = 262154;BA.debugLine="If loc.isdefault = \"1\" Then";
if ((_loc.isdefault /*String*/ ).equals("1")) { 
RDebugUtils.currentLine=262155;
 //BA.debugLineNum = 262155;BA.debugLine="InitConnection(loc.code)";
_initconnection(_loc.code /*String*/ );
 };
 }else {
RDebugUtils.currentLine=262158;
 //BA.debugLineNum = 262158;BA.debugLine="For Each loc As locationBord In baseList";
{
final anywheresoftware.b4a.BA.IterableList group13 = _baselist;
final int groupLen13 = group13.getSize()
;int index13 = 0;
;
for (; index13 < groupLen13;index13++){
_loc = (nl.pdeg.bordondroid.main._locationbord)(group13.Get(index13));
RDebugUtils.currentLine=262159;
 //BA.debugLineNum = 262159;BA.debugLine="If loc.isdefault = \"1\" Then";
if ((_loc.isdefault /*String*/ ).equals("1")) { 
RDebugUtils.currentLine=262160;
 //BA.debugLineNum = 262160;BA.debugLine="InitConnection(loc.code)";
_initconnection(_loc.code /*String*/ );
RDebugUtils.currentLine=262161;
 //BA.debugLineNum = 262161;BA.debugLine="Exit";
if (true) break;
 };
 }
};
 }}
;
RDebugUtils.currentLine=262165;
 //BA.debugLineNum = 262165;BA.debugLine="End Sub";
return "";
}
public static boolean  _activity_keypress(int _keycode) throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "activity_keypress", false))
	 {return ((Boolean) Debug.delegate(mostCurrent.activityBA, "activity_keypress", new Object[] {_keycode}));}
RDebugUtils.currentLine=589824;
 //BA.debugLineNum = 589824;BA.debugLine="Private Sub Activity_KeyPress(KeyCode As Int) As B";
RDebugUtils.currentLine=589825;
 //BA.debugLineNum = 589825;BA.debugLine="If KeyCode = KeyCodes.KEYCODE_BACK Then";
if (_keycode==anywheresoftware.b4a.keywords.Common.KeyCodes.KEYCODE_BACK) { 
RDebugUtils.currentLine=589826;
 //BA.debugLineNum = 589826;BA.debugLine="tmrBordLastAlive.Enabled = False";
_tmrbordlastalive.setEnabled(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=589827;
 //BA.debugLineNum = 589827;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
RDebugUtils.currentLine=589829;
 //BA.debugLineNum = 589829;BA.debugLine="Return False";
if (true) return anywheresoftware.b4a.keywords.Common.False;
 }else {
RDebugUtils.currentLine=589831;
 //BA.debugLineNum = 589831;BA.debugLine="Return False";
if (true) return anywheresoftware.b4a.keywords.Common.False;
 };
RDebugUtils.currentLine=589833;
 //BA.debugLineNum = 589833;BA.debugLine="End Sub";
return false;
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
RDebugUtils.currentModule="main";
RDebugUtils.currentLine=524288;
 //BA.debugLineNum = 524288;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
RDebugUtils.currentLine=524289;
 //BA.debugLineNum = 524289;BA.debugLine="DisconnectMqtt";
_disconnectmqtt();
RDebugUtils.currentLine=524290;
 //BA.debugLineNum = 524290;BA.debugLine="End Sub";
return "";
}
public static String  _disconnectmqtt() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "disconnectmqtt", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "disconnectmqtt", null));}
RDebugUtils.currentLine=655360;
 //BA.debugLineNum = 655360;BA.debugLine="Sub DisconnectMqtt";
RDebugUtils.currentLine=655361;
 //BA.debugLineNum = 655361;BA.debugLine="If mqttGetBord.Connected Then";
if (_mqttgetbord._connected /*boolean*/ ) { 
RDebugUtils.currentLine=655362;
 //BA.debugLineNum = 655362;BA.debugLine="mqttGetBord.Disconnect";
_mqttgetbord._disconnect /*String*/ (null);
 };
RDebugUtils.currentLine=655365;
 //BA.debugLineNum = 655365;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "activity_resume", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "activity_resume", null));}
RDebugUtils.currentLine=458752;
 //BA.debugLineNum = 458752;BA.debugLine="Sub Activity_Resume";
RDebugUtils.currentLine=458753;
 //BA.debugLineNum = 458753;BA.debugLine="If Starter.mqttGetBordsActive Then";
if (mostCurrent._starter._mqttgetbordsactive /*boolean*/ ) { 
RDebugUtils.currentLine=458754;
 //BA.debugLineNum = 458754;BA.debugLine="mqttGetBord.Connect";
_mqttgetbord._connect /*String*/ (null);
 };
RDebugUtils.currentLine=458756;
 //BA.debugLineNum = 458756;BA.debugLine="End Sub";
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
RDebugUtils.currentLine=917505;
 //BA.debugLineNum = 917505;BA.debugLine="Dim bordStatus As bordStatus";
_bordstatus = new nl.pdeg.bordondroid.main._bordstatus();
RDebugUtils.currentLine=917506;
 //BA.debugLineNum = 917506;BA.debugLine="bordStatus.Initialize";
_bordstatus.Initialize();
RDebugUtils.currentLine=917507;
 //BA.debugLineNum = 917507;BA.debugLine="bordStatus.ip = ip";
_bordstatus.ip /*String*/  = _ip;
RDebugUtils.currentLine=917508;
 //BA.debugLineNum = 917508;BA.debugLine="bordStatus.name = name";
_bordstatus.name /*String*/  = _name;
RDebugUtils.currentLine=917509;
 //BA.debugLineNum = 917509;BA.debugLine="bordStatus.timeStamp = DateTime.Now";
_bordstatus.timeStamp /*long*/  = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
RDebugUtils.currentLine=917510;
 //BA.debugLineNum = 917510;BA.debugLine="bordStatus.alive = True";
_bordstatus.alive /*boolean*/  = anywheresoftware.b4a.keywords.Common.True;
RDebugUtils.currentLine=917511;
 //BA.debugLineNum = 917511;BA.debugLine="Starter.serverList.Add(bordStatus)";
parent.mostCurrent._starter._serverlist /*anywheresoftware.b4a.objects.collections.List*/ .Add((Object)(_bordstatus));
RDebugUtils.currentLine=917512;
 //BA.debugLineNum = 917512;BA.debugLine="CheckIpInClv(bordStatus)";
_checkipinclv(_bordstatus);
RDebugUtils.currentLine=917514;
 //BA.debugLineNum = 917514;BA.debugLine="If clvServer.GetSize > 0 Then";
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
RDebugUtils.currentLine=917515;
 //BA.debugLineNum = 917515;BA.debugLine="pnlNobords.SetVisibleAnimated(1000, False)";
parent.mostCurrent._pnlnobords.SetVisibleAnimated((int) (1000),anywheresoftware.b4a.keywords.Common.False);
 if (true) break;

case 5:
//C
this.state = 6;
RDebugUtils.currentLine=917517;
 //BA.debugLineNum = 917517;BA.debugLine="pnlNobords.SetVisibleAnimated(1000, True)";
parent.mostCurrent._pnlnobords.SetVisibleAnimated((int) (1000),anywheresoftware.b4a.keywords.Common.True);
 if (true) break;

case 6:
//C
this.state = -1;
;
RDebugUtils.currentLine=917519;
 //BA.debugLineNum = 917519;BA.debugLine="Sleep(1000)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "main", "addunkownip"),(int) (1000));
this.state = 7;
return;
case 7:
//C
this.state = -1;
;
RDebugUtils.currentLine=917521;
 //BA.debugLineNum = 917521;BA.debugLine="End Sub";
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
anywheresoftware.b4a.objects.LabelWrapper _lbl = null;
int _i = 0;
anywheresoftware.b4a.objects.ConcreteViewWrapper _v = null;
RDebugUtils.currentLine=983040;
 //BA.debugLineNum = 983040;BA.debugLine="Sub CheckIpInClv(bord As bordStatus)";
RDebugUtils.currentLine=983041;
 //BA.debugLineNum = 983041;BA.debugLine="Dim p As Panel";
_p = new anywheresoftware.b4a.objects.PanelWrapper();
RDebugUtils.currentLine=983042;
 //BA.debugLineNum = 983042;BA.debugLine="Dim lbl As Label";
_lbl = new anywheresoftware.b4a.objects.LabelWrapper();
RDebugUtils.currentLine=983044;
 //BA.debugLineNum = 983044;BA.debugLine="For i = 0 To clvServer.GetSize-1";
{
final int step3 = 1;
final int limit3 = (int) (mostCurrent._clvserver._getsize()-1);
_i = (int) (0) ;
for (;_i <= limit3 ;_i = _i + step3 ) {
RDebugUtils.currentLine=983045;
 //BA.debugLineNum = 983045;BA.debugLine="p = clvServer.GetPanel(i)";
_p.setObject((android.view.ViewGroup)(mostCurrent._clvserver._getpanel(_i).getObject()));
RDebugUtils.currentLine=983046;
 //BA.debugLineNum = 983046;BA.debugLine="If p.Tag = bord.name Then";
if ((_p.getTag()).equals((Object)(_bord.name /*String*/ ))) { 
RDebugUtils.currentLine=983047;
 //BA.debugLineNum = 983047;BA.debugLine="For Each v As View In p.GetAllViewsRecursive";
_v = new anywheresoftware.b4a.objects.ConcreteViewWrapper();
{
final anywheresoftware.b4a.BA.IterableList group6 = _p.GetAllViewsRecursive();
final int groupLen6 = group6.getSize()
;int index6 = 0;
;
for (; index6 < groupLen6;index6++){
_v.setObject((android.view.View)(group6.Get(index6)));
RDebugUtils.currentLine=983048;
 //BA.debugLineNum = 983048;BA.debugLine="If v.Tag = \"lblLastCheck\" Then";
if ((_v.getTag()).equals((Object)("lblLastCheck"))) { 
RDebugUtils.currentLine=983049;
 //BA.debugLineNum = 983049;BA.debugLine="lbl = v";
_lbl.setObject((android.widget.TextView)(_v.getObject()));
RDebugUtils.currentLine=983050;
 //BA.debugLineNum = 983050;BA.debugLine="lbl.Text = $\"Laatste controle $Time{DateTime.";
_lbl.setText(BA.ObjectToCharSequence(("Laatste controle "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("time",(Object)(anywheresoftware.b4a.keywords.Common.DateTime.getNow()))+"")));
 };
 }
};
RDebugUtils.currentLine=983054;
 //BA.debugLineNum = 983054;BA.debugLine="For Each v As View In p.GetAllViewsRecursive";
_v = new anywheresoftware.b4a.objects.ConcreteViewWrapper();
{
final anywheresoftware.b4a.BA.IterableList group12 = _p.GetAllViewsRecursive();
final int groupLen12 = group12.getSize()
;int index12 = 0;
;
for (; index12 < groupLen12;index12++){
_v.setObject((android.view.View)(group12.Get(index12)));
RDebugUtils.currentLine=983055;
 //BA.debugLineNum = 983055;BA.debugLine="If v.Tag = \"viewbord\" Then";
if ((_v.getTag()).equals((Object)("viewbord"))) { 
RDebugUtils.currentLine=983056;
 //BA.debugLineNum = 983056;BA.debugLine="lbl = v";
_lbl.setObject((android.widget.TextView)(_v.getObject()));
RDebugUtils.currentLine=983057;
 //BA.debugLineNum = 983057;BA.debugLine="If bord.alive = False Then";
if (_bord.alive /*boolean*/ ==anywheresoftware.b4a.keywords.Common.False) { 
RDebugUtils.currentLine=983058;
 //BA.debugLineNum = 983058;BA.debugLine="lbl.Enabled = False";
_lbl.setEnabled(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=983059;
 //BA.debugLineNum = 983059;BA.debugLine="lbl.TextColor = Colors.Red";
_lbl.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
RDebugUtils.currentLine=983060;
 //BA.debugLineNum = 983060;BA.debugLine="Starter.diedIndex = i";
mostCurrent._starter._diedindex /*int*/  = _i;
 }else {
RDebugUtils.currentLine=983062;
 //BA.debugLineNum = 983062;BA.debugLine="lbl.Enabled = True";
_lbl.setEnabled(anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=983063;
 //BA.debugLineNum = 983063;BA.debugLine="lbl.TextColor = 0xFF027F00";
_lbl.setTextColor((int) (0xff027f00));
 };
RDebugUtils.currentLine=983065;
 //BA.debugLineNum = 983065;BA.debugLine="Return";
if (true) return "";
 };
 }
};
 };
 }
};
RDebugUtils.currentLine=983072;
 //BA.debugLineNum = 983072;BA.debugLine="pnlNobords.SetVisibleAnimated(1000, False)";
mostCurrent._pnlnobords.SetVisibleAnimated((int) (1000),anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=983073;
 //BA.debugLineNum = 983073;BA.debugLine="clvServer.Add(genUnitList(bord.name, bord.alive),";
mostCurrent._clvserver._add((anywheresoftware.b4a.objects.B4XViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.B4XViewWrapper(), (java.lang.Object)(_genunitlist(_bord.name /*String*/ ,_bord.alive /*boolean*/ ).getObject())),(Object)(""));
RDebugUtils.currentLine=983074;
 //BA.debugLineNum = 983074;BA.debugLine="End Sub";
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
RDebugUtils.currentLine=1245185;
 //BA.debugLineNum = 1245185;BA.debugLine="Msgbox2Async(\"Applicatie afsluiten?\", \"Bord Op Dr";
anywheresoftware.b4a.keywords.Common.Msgbox2Async(BA.ObjectToCharSequence("Applicatie afsluiten?"),BA.ObjectToCharSequence("Bord Op Droid"),"JA","","NEE",(anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper(), (android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null)),processBA,anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=1245186;
 //BA.debugLineNum = 1245186;BA.debugLine="Wait For Msgbox_Result(Result As Int)";
anywheresoftware.b4a.keywords.Common.WaitFor("msgbox_result", processBA, new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "main", "btncancel_click"), null);
this.state = 5;
return;
case 5:
//C
this.state = 1;
_result = (Integer) result[0];
;
RDebugUtils.currentLine=1245187;
 //BA.debugLineNum = 1245187;BA.debugLine="If Result = DialogResponse.POSITIVE Then";
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
RDebugUtils.currentLine=1245188;
 //BA.debugLineNum = 1245188;BA.debugLine="Activity.Finish";
parent.mostCurrent._activity.Finish();
 if (true) break;

case 4:
//C
this.state = -1;
;
RDebugUtils.currentLine=1245190;
 //BA.debugLineNum = 1245190;BA.debugLine="End Sub";
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
 //BA.debugLineNum = 1310721;BA.debugLine="Starter.testBaseName = True";
parent.mostCurrent._starter._testbasename /*boolean*/  = anywheresoftware.b4a.keywords.Common.True;
RDebugUtils.currentLine=1310722;
 //BA.debugLineNum = 1310722;BA.debugLine="mqttGetBord.Initialize";
parent._mqttgetbord._initialize /*String*/ (null,processBA);
RDebugUtils.currentLine=1310723;
 //BA.debugLineNum = 1310723;BA.debugLine="CallSub2(Starter, \"SetSubBase\", edtLocationCode.T";
anywheresoftware.b4a.keywords.Common.CallSubDebug2(processBA,(Object)(parent.mostCurrent._starter.getObject()),"SetSubBase",(Object)(parent.mostCurrent._edtlocationcode.getText()));
RDebugUtils.currentLine=1310724;
 //BA.debugLineNum = 1310724;BA.debugLine="CallSub(Starter, \"SetSubGetUnits\")";
anywheresoftware.b4a.keywords.Common.CallSubDebug(processBA,(Object)(parent.mostCurrent._starter.getObject()),"SetSubGetUnits");
RDebugUtils.currentLine=1310725;
 //BA.debugLineNum = 1310725;BA.debugLine="mqttGetBord.SetPubBord";
parent._mqttgetbord._setpubbord /*String*/ (null);
RDebugUtils.currentLine=1310726;
 //BA.debugLineNum = 1310726;BA.debugLine="Sleep(100)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "main", "btnok_click"),(int) (100));
this.state = 7;
return;
case 7:
//C
this.state = 1;
;
RDebugUtils.currentLine=1310727;
 //BA.debugLineNum = 1310727;BA.debugLine="mqttGetBord.Connect";
parent._mqttgetbord._connect /*String*/ (null);
RDebugUtils.currentLine=1310729;
 //BA.debugLineNum = 1310729;BA.debugLine="Sleep(1000)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "main", "btnok_click"),(int) (1000));
this.state = 8;
return;
case 8:
//C
this.state = 1;
;
RDebugUtils.currentLine=1310731;
 //BA.debugLineNum = 1310731;BA.debugLine="If mqttGetBord.connected = False Then";
if (true) break;

case 1:
//if
this.state = 6;
if (parent._mqttgetbord._connected /*boolean*/ ==anywheresoftware.b4a.keywords.Common.False) { 
this.state = 3;
}else {
this.state = 5;
}if (true) break;

case 3:
//C
this.state = 6;
RDebugUtils.currentLine=1310732;
 //BA.debugLineNum = 1310732;BA.debugLine="Msgbox2Async(\"Kan geen verbinding maken met loca";
anywheresoftware.b4a.keywords.Common.Msgbox2Async(BA.ObjectToCharSequence("Kan geen verbinding maken met locatie"),BA.ObjectToCharSequence("Bord Op Droid"),"OKE","","",(anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper(), (android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null)),processBA,anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=1310733;
 //BA.debugLineNum = 1310733;BA.debugLine="Wait For Msgbox_Result(Result As Int)";
anywheresoftware.b4a.keywords.Common.WaitFor("msgbox_result", processBA, new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "main", "btnok_click"), null);
this.state = 9;
return;
case 9:
//C
this.state = 6;
_result = (Integer) result[0];
;
 if (true) break;

case 5:
//C
this.state = 6;
RDebugUtils.currentLine=1310735;
 //BA.debugLineNum = 1310735;BA.debugLine="ToastMessageShow(\"Verbonden met locatie\", True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Verbonden met locatie"),anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=1310736;
 //BA.debugLineNum = 1310736;BA.debugLine="mqttGetBord.Disconnect";
parent._mqttgetbord._disconnect /*String*/ (null);
RDebugUtils.currentLine=1310737;
 //BA.debugLineNum = 1310737;BA.debugLine="pnlLocationCOde.Visible = False";
parent.mostCurrent._pnllocationcode.setVisible(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=1310738;
 //BA.debugLineNum = 1310738;BA.debugLine="baseFile.SetBase(edtLocationCode.Text)";
parent._basefile._setbase /*String*/ (null,parent.mostCurrent._edtlocationcode.getText());
RDebugUtils.currentLine=1310739;
 //BA.debugLineNum = 1310739;BA.debugLine="StartConnection";
_startconnection();
 if (true) break;

case 6:
//C
this.state = -1;
;
RDebugUtils.currentLine=1310741;
 //BA.debugLineNum = 1310741;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static String  _startconnection() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "startconnection", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "startconnection", null));}
RDebugUtils.currentLine=196608;
 //BA.debugLineNum = 196608;BA.debugLine="Private Sub StartConnection";
RDebugUtils.currentLine=196609;
 //BA.debugLineNum = 196609;BA.debugLine="pnlLocationCOde.Visible = False";
mostCurrent._pnllocationcode.setVisible(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=196610;
 //BA.debugLineNum = 196610;BA.debugLine="pnlNobords.Visible = True";
mostCurrent._pnlnobords.setVisible(anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=196611;
 //BA.debugLineNum = 196611;BA.debugLine="clvServer.Clear";
mostCurrent._clvserver._clear();
RDebugUtils.currentLine=196612;
 //BA.debugLineNum = 196612;BA.debugLine="mqttGetData.Initialize";
_mqttgetdata._initialize /*String*/ (null,processBA);
RDebugUtils.currentLine=196613;
 //BA.debugLineNum = 196613;BA.debugLine="mqttGetBord.Initialize";
_mqttgetbord._initialize /*String*/ (null,processBA);
RDebugUtils.currentLine=196614;
 //BA.debugLineNum = 196614;BA.debugLine="mqttGetBord.SetPubBord";
_mqttgetbord._setpubbord /*String*/ (null);
RDebugUtils.currentLine=196615;
 //BA.debugLineNum = 196615;BA.debugLine="mqttGetBord.Connect";
_mqttgetbord._connect /*String*/ (null);
RDebugUtils.currentLine=196617;
 //BA.debugLineNum = 196617;BA.debugLine="baseFile.GetBase";
_basefile._getbase /*anywheresoftware.b4a.objects.collections.List*/ (null);
RDebugUtils.currentLine=196619;
 //BA.debugLineNum = 196619;BA.debugLine="tmrBordLastAlive.Initialize(\"tmrBordAlive\", 10000";
_tmrbordlastalive.Initialize(processBA,"tmrBordAlive",(long) (10000));
RDebugUtils.currentLine=196620;
 //BA.debugLineNum = 196620;BA.debugLine="tmrBordLastAlive.Enabled = True";
_tmrbordlastalive.setEnabled(anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=196622;
 //BA.debugLineNum = 196622;BA.debugLine="End Sub";
return "";
}
public static String  _checkipexits(String _bord) throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "checkipexits", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "checkipexits", new Object[] {_bord}));}
boolean _ipfound = false;
String _name = "";
nl.pdeg.bordondroid.main._bordstatus _lst = null;
RDebugUtils.currentLine=786432;
 //BA.debugLineNum = 786432;BA.debugLine="Sub CheckIpExits(bord As String)";
RDebugUtils.currentLine=786434;
 //BA.debugLineNum = 786434;BA.debugLine="Dim ipFound As Boolean = False";
_ipfound = anywheresoftware.b4a.keywords.Common.False;
RDebugUtils.currentLine=786435;
 //BA.debugLineNum = 786435;BA.debugLine="Dim name As String = \"\"";
_name = "";
RDebugUtils.currentLine=786437;
 //BA.debugLineNum = 786437;BA.debugLine="If bord.Length = 0 Then Return";
if (_bord.length()==0) { 
if (true) return "";};
RDebugUtils.currentLine=786439;
 //BA.debugLineNum = 786439;BA.debugLine="name = bord";
_name = _bord;
RDebugUtils.currentLine=786441;
 //BA.debugLineNum = 786441;BA.debugLine="If Starter.serverList.Size = 0 Then";
if (mostCurrent._starter._serverlist /*anywheresoftware.b4a.objects.collections.List*/ .getSize()==0) { 
RDebugUtils.currentLine=786442;
 //BA.debugLineNum = 786442;BA.debugLine="AddUnkownIp(\"\", name)";
_addunkownip("",_name);
RDebugUtils.currentLine=786443;
 //BA.debugLineNum = 786443;BA.debugLine="Return";
if (true) return "";
 };
RDebugUtils.currentLine=786446;
 //BA.debugLineNum = 786446;BA.debugLine="For Each lst As bordStatus In Starter.serverList";
{
final anywheresoftware.b4a.BA.IterableList group9 = mostCurrent._starter._serverlist /*anywheresoftware.b4a.objects.collections.List*/ ;
final int groupLen9 = group9.getSize()
;int index9 = 0;
;
for (; index9 < groupLen9;index9++){
_lst = (nl.pdeg.bordondroid.main._bordstatus)(group9.Get(index9));
RDebugUtils.currentLine=786447;
 //BA.debugLineNum = 786447;BA.debugLine="If lst.name = name Then";
if ((_lst.name /*String*/ ).equals(_name)) { 
RDebugUtils.currentLine=786448;
 //BA.debugLineNum = 786448;BA.debugLine="ipFound = True";
_ipfound = anywheresoftware.b4a.keywords.Common.True;
RDebugUtils.currentLine=786449;
 //BA.debugLineNum = 786449;BA.debugLine="lst.timeStamp = DateTime.Now";
_lst.timeStamp /*long*/  = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
RDebugUtils.currentLine=786450;
 //BA.debugLineNum = 786450;BA.debugLine="lst.alive = True";
_lst.alive /*boolean*/  = anywheresoftware.b4a.keywords.Common.True;
 };
 }
};
RDebugUtils.currentLine=786454;
 //BA.debugLineNum = 786454;BA.debugLine="ServerAlive";
_serveralive();
RDebugUtils.currentLine=786456;
 //BA.debugLineNum = 786456;BA.debugLine="If Not(ipFound) Then";
if (anywheresoftware.b4a.keywords.Common.Not(_ipfound)) { 
RDebugUtils.currentLine=786457;
 //BA.debugLineNum = 786457;BA.debugLine="AddUnkownIp(\"\", name)";
_addunkownip("",_name);
RDebugUtils.currentLine=786458;
 //BA.debugLineNum = 786458;BA.debugLine="Return";
if (true) return "";
 };
RDebugUtils.currentLine=786460;
 //BA.debugLineNum = 786460;BA.debugLine="End Sub";
return "";
}
public static String  _serveralive() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "serveralive", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "serveralive", null));}
long _msnow = 0L;
nl.pdeg.bordondroid.main._bordstatus _lst = null;
RDebugUtils.currentLine=851968;
 //BA.debugLineNum = 851968;BA.debugLine="Sub ServerAlive";
RDebugUtils.currentLine=851970;
 //BA.debugLineNum = 851970;BA.debugLine="Dim msNow As Long = DateTime.Now";
_msnow = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
RDebugUtils.currentLine=851971;
 //BA.debugLineNum = 851971;BA.debugLine="For Each lst As bordStatus In Starter.serverList";
{
final anywheresoftware.b4a.BA.IterableList group2 = mostCurrent._starter._serverlist /*anywheresoftware.b4a.objects.collections.List*/ ;
final int groupLen2 = group2.getSize()
;int index2 = 0;
;
for (; index2 < groupLen2;index2++){
_lst = (nl.pdeg.bordondroid.main._bordstatus)(group2.Get(index2));
RDebugUtils.currentLine=851972;
 //BA.debugLineNum = 851972;BA.debugLine="If(msNow - lst.timeStamp) > Starter.serverDied A";
if ((_msnow-_lst.timeStamp /*long*/ )>mostCurrent._starter._serverdied /*long*/  && _lst.alive /*boolean*/ ==anywheresoftware.b4a.keywords.Common.True) { 
RDebugUtils.currentLine=851973;
 //BA.debugLineNum = 851973;BA.debugLine="lst.alive = False";
_lst.alive /*boolean*/  = anywheresoftware.b4a.keywords.Common.False;
RDebugUtils.currentLine=851974;
 //BA.debugLineNum = 851974;BA.debugLine="CheckIpInClv(lst)";
_checkipinclv(_lst);
RDebugUtils.currentLine=851975;
 //BA.debugLineNum = 851975;BA.debugLine="Return";
if (true) return "";
 }else {
RDebugUtils.currentLine=851977;
 //BA.debugLineNum = 851977;BA.debugLine="CheckIpInClv(lst)";
_checkipinclv(_lst);
 };
 }
};
RDebugUtils.currentLine=851980;
 //BA.debugLineNum = 851980;BA.debugLine="End Sub";
return "";
}
public static anywheresoftware.b4a.objects.PanelWrapper  _genunitlist(String _name,boolean _alive) throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "genunitlist", false))
	 {return ((anywheresoftware.b4a.objects.PanelWrapper) Debug.delegate(mostCurrent.activityBA, "genunitlist", new Object[] {_name,_alive}));}
anywheresoftware.b4a.objects.PanelWrapper _p = null;
RDebugUtils.currentLine=720896;
 //BA.debugLineNum = 720896;BA.debugLine="Sub genUnitList(name As String, alive As Boolean)";
RDebugUtils.currentLine=720897;
 //BA.debugLineNum = 720897;BA.debugLine="Dim p As Panel";
_p = new anywheresoftware.b4a.objects.PanelWrapper();
RDebugUtils.currentLine=720898;
 //BA.debugLineNum = 720898;BA.debugLine="p.Initialize(Me)";
_p.Initialize(mostCurrent.activityBA,BA.ObjectToString(main.getObject()));
RDebugUtils.currentLine=720899;
 //BA.debugLineNum = 720899;BA.debugLine="p.SetLayout(0dip, 0dip, clvServer.AsView.Width, 1";
_p.SetLayout(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)),mostCurrent._clvserver._asview().getWidth(),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (120)));
RDebugUtils.currentLine=720900;
 //BA.debugLineNum = 720900;BA.debugLine="p.LoadLayout(\"clvBorden\")";
_p.LoadLayout("clvBorden",mostCurrent.activityBA);
RDebugUtils.currentLine=720901;
 //BA.debugLineNum = 720901;BA.debugLine="p.Tag = name";
_p.setTag((Object)(_name));
RDebugUtils.currentLine=720903;
 //BA.debugLineNum = 720903;BA.debugLine="lblBordName.Text = name.Trim";
mostCurrent._lblbordname.setText(BA.ObjectToCharSequence(_name.trim()));
RDebugUtils.currentLine=720904;
 //BA.debugLineNum = 720904;BA.debugLine="lblLastCheck.Text = $\"Laatste controle $Time{Date";
mostCurrent._lbllastcheck.setText(BA.ObjectToCharSequence(("Laatste controle "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("time",(Object)(anywheresoftware.b4a.keywords.Common.DateTime.getNow()))+"")));
RDebugUtils.currentLine=720905;
 //BA.debugLineNum = 720905;BA.debugLine="If Not(alive) Then";
if (anywheresoftware.b4a.keywords.Common.Not(_alive)) { 
RDebugUtils.currentLine=720906;
 //BA.debugLineNum = 720906;BA.debugLine="lblViewBord.Enabled = False";
mostCurrent._lblviewbord.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 };
RDebugUtils.currentLine=720908;
 //BA.debugLineNum = 720908;BA.debugLine="Return p";
if (true) return _p;
RDebugUtils.currentLine=720909;
 //BA.debugLineNum = 720909;BA.debugLine="End Sub";
return null;
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
anywheresoftware.b4a.objects.LabelWrapper _lbl = null;
anywheresoftware.b4a.objects.ConcreteViewWrapper _c = null;
anywheresoftware.b4a.BA.IterableList group3;
int index3;
int groupLen3;

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
RDebugUtils.currentLine=1179649;
 //BA.debugLineNum = 1179649;BA.debugLine="Dim p As Panel = clvServer.GetPanel(Index)";
_p = new anywheresoftware.b4a.objects.PanelWrapper();
_p.setObject((android.view.ViewGroup)(parent.mostCurrent._clvserver._getpanel(_index).getObject()));
RDebugUtils.currentLine=1179650;
 //BA.debugLineNum = 1179650;BA.debugLine="Dim lbl As Label";
_lbl = new anywheresoftware.b4a.objects.LabelWrapper();
RDebugUtils.currentLine=1179653;
 //BA.debugLineNum = 1179653;BA.debugLine="For Each c As View In p.GetAllViewsRecursive";
if (true) break;

case 1:
//for
this.state = 8;
_c = new anywheresoftware.b4a.objects.ConcreteViewWrapper();
group3 = _p.GetAllViewsRecursive();
index3 = 0;
groupLen3 = group3.getSize();
this.state = 9;
if (true) break;

case 9:
//C
this.state = 8;
if (index3 < groupLen3) {
this.state = 3;
_c.setObject((android.view.View)(group3.Get(index3)));}
if (true) break;

case 10:
//C
this.state = 9;
index3++;
if (true) break;

case 3:
//C
this.state = 4;
RDebugUtils.currentLine=1179654;
 //BA.debugLineNum = 1179654;BA.debugLine="If c.Tag = \"name\" Then";
if (true) break;

case 4:
//if
this.state = 7;
if ((_c.getTag()).equals((Object)("name"))) { 
this.state = 6;
}if (true) break;

case 6:
//C
this.state = 7;
RDebugUtils.currentLine=1179655;
 //BA.debugLineNum = 1179655;BA.debugLine="lbl = c";
_lbl.setObject((android.widget.TextView)(_c.getObject()));
RDebugUtils.currentLine=1179658;
 //BA.debugLineNum = 1179658;BA.debugLine="CallSub2(Starter, \"SetUnit\", PrepTopicName(lbl.";
anywheresoftware.b4a.keywords.Common.CallSubDebug2(processBA,(Object)(parent.mostCurrent._starter.getObject()),"SetUnit",(Object)(_preptopicname(_lbl.getText()).toLowerCase()));
RDebugUtils.currentLine=1179659;
 //BA.debugLineNum = 1179659;BA.debugLine="Starter.DiscoveredServer = lbl.Text";
parent.mostCurrent._starter._discoveredserver /*String*/  = _lbl.getText();
RDebugUtils.currentLine=1179660;
 //BA.debugLineNum = 1179660;BA.debugLine="Exit";
this.state = 8;
if (true) break;
 if (true) break;

case 7:
//C
this.state = 10;
;
 if (true) break;
if (true) break;

case 8:
//C
this.state = -1;
;
RDebugUtils.currentLine=1179664;
 //BA.debugLineNum = 1179664;BA.debugLine="DisconnectMqtt";
_disconnectmqtt();
RDebugUtils.currentLine=1179665;
 //BA.debugLineNum = 1179665;BA.debugLine="Sleep(100)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "main", "clvserver_itemclick"),(int) (100));
this.state = 11;
return;
case 11:
//C
this.state = -1;
;
RDebugUtils.currentLine=1179667;
 //BA.debugLineNum = 1179667;BA.debugLine="StartActivity(ServerBoard)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(parent.mostCurrent._serverboard.getObject()));
RDebugUtils.currentLine=1179668;
 //BA.debugLineNum = 1179668;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static String  _preptopicname(String _bord) throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "preptopicname", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "preptopicname", new Object[] {_bord}));}
RDebugUtils.currentLine=1114112;
 //BA.debugLineNum = 1114112;BA.debugLine="Private Sub PrepTopicName(bord As String) As Strin";
RDebugUtils.currentLine=1114114;
 //BA.debugLineNum = 1114114;BA.debugLine="Return bord.Replace(\" \", \"\")";
if (true) return _bord.replace(" ","");
RDebugUtils.currentLine=1114115;
 //BA.debugLineNum = 1114115;BA.debugLine="End Sub";
return "";
}
public static String  _edtlocationcode_textchanged(String _old,String _new) throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "edtlocationcode_textchanged", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "edtlocationcode_textchanged", new Object[] {_old,_new}));}
RDebugUtils.currentLine=1376256;
 //BA.debugLineNum = 1376256;BA.debugLine="Sub edtLocationCode_TextChanged (Old As String, Ne";
RDebugUtils.currentLine=1376257;
 //BA.debugLineNum = 1376257;BA.debugLine="If New.Length > 0 Then";
if (_new.length()>0) { 
RDebugUtils.currentLine=1376258;
 //BA.debugLineNum = 1376258;BA.debugLine="btnOk.Enabled = True";
mostCurrent._btnok.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 }else {
RDebugUtils.currentLine=1376260;
 //BA.debugLineNum = 1376260;BA.debugLine="btnOk.Enabled = False";
mostCurrent._btnok.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 };
RDebugUtils.currentLine=1376262;
 //BA.debugLineNum = 1376262;BA.debugLine="End Sub";
return "";
}
public static String  _initconnection(String _locationcode) throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "initconnection", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "initconnection", new Object[] {_locationcode}));}
RDebugUtils.currentLine=327680;
 //BA.debugLineNum = 327680;BA.debugLine="Private Sub InitConnection(locationCode As String)";
RDebugUtils.currentLine=327681;
 //BA.debugLineNum = 327681;BA.debugLine="CallSub2(Starter, \"SetSubBase\", locationCode)";
anywheresoftware.b4a.keywords.Common.CallSubDebug2(processBA,(Object)(mostCurrent._starter.getObject()),"SetSubBase",(Object)(_locationcode));
RDebugUtils.currentLine=327682;
 //BA.debugLineNum = 327682;BA.debugLine="CallSub(Starter, \"SetSubGetUnits\")";
anywheresoftware.b4a.keywords.Common.CallSubDebug(processBA,(Object)(mostCurrent._starter.getObject()),"SetSubGetUnits");
RDebugUtils.currentLine=327683;
 //BA.debugLineNum = 327683;BA.debugLine="StartConnection";
_startconnection();
RDebugUtils.currentLine=327684;
 //BA.debugLineNum = 327684;BA.debugLine="End Sub";
return "";
}
public static void  _lblviewbord_click() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "lblviewbord_click", false))
	 {Debug.delegate(mostCurrent.activityBA, "lblviewbord_click", null); return;}
ResumableSub_lblViewBord_Click rsub = new ResumableSub_lblViewBord_Click(null);
rsub.resume(processBA, null);
}
public static class ResumableSub_lblViewBord_Click extends BA.ResumableSub {
public ResumableSub_lblViewBord_Click(nl.pdeg.bordondroid.main parent) {
this.parent = parent;
}
nl.pdeg.bordondroid.main parent;
anywheresoftware.b4a.objects.ConcreteViewWrapper _v = null;
anywheresoftware.b4a.objects.PanelWrapper _p = null;
anywheresoftware.b4a.objects.LabelWrapper _lbl = null;
anywheresoftware.b4a.objects.ConcreteViewWrapper _c = null;
anywheresoftware.b4a.BA.IterableList group4;
int index4;
int groupLen4;

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
RDebugUtils.currentLine=1048577;
 //BA.debugLineNum = 1048577;BA.debugLine="Dim v As View = Sender";
_v = new anywheresoftware.b4a.objects.ConcreteViewWrapper();
_v.setObject((android.view.View)(anywheresoftware.b4a.keywords.Common.Sender(mostCurrent.activityBA)));
RDebugUtils.currentLine=1048578;
 //BA.debugLineNum = 1048578;BA.debugLine="Dim p As Panel = v.Parent";
_p = new anywheresoftware.b4a.objects.PanelWrapper();
_p.setObject((android.view.ViewGroup)(_v.getParent()));
RDebugUtils.currentLine=1048579;
 //BA.debugLineNum = 1048579;BA.debugLine="Dim lbl As Label";
_lbl = new anywheresoftware.b4a.objects.LabelWrapper();
RDebugUtils.currentLine=1048582;
 //BA.debugLineNum = 1048582;BA.debugLine="For Each c As View In p.GetAllViewsRecursive";
if (true) break;

case 1:
//for
this.state = 8;
_c = new anywheresoftware.b4a.objects.ConcreteViewWrapper();
group4 = _p.GetAllViewsRecursive();
index4 = 0;
groupLen4 = group4.getSize();
this.state = 9;
if (true) break;

case 9:
//C
this.state = 8;
if (index4 < groupLen4) {
this.state = 3;
_c.setObject((android.view.View)(group4.Get(index4)));}
if (true) break;

case 10:
//C
this.state = 9;
index4++;
if (true) break;

case 3:
//C
this.state = 4;
RDebugUtils.currentLine=1048583;
 //BA.debugLineNum = 1048583;BA.debugLine="If c.Tag = \"name\" Then";
if (true) break;

case 4:
//if
this.state = 7;
if ((_c.getTag()).equals((Object)("name"))) { 
this.state = 6;
}if (true) break;

case 6:
//C
this.state = 7;
RDebugUtils.currentLine=1048584;
 //BA.debugLineNum = 1048584;BA.debugLine="lbl = c";
_lbl.setObject((android.widget.TextView)(_c.getObject()));
RDebugUtils.currentLine=1048585;
 //BA.debugLineNum = 1048585;BA.debugLine="Starter.selectedBordName = PrepTopicName(lbl.Te";
parent.mostCurrent._starter._selectedbordname /*String*/  = _preptopicname(_lbl.getText());
RDebugUtils.currentLine=1048586;
 //BA.debugLineNum = 1048586;BA.debugLine="Starter.DiscoveredServer = lbl.Text";
parent.mostCurrent._starter._discoveredserver /*String*/  = _lbl.getText();
RDebugUtils.currentLine=1048587;
 //BA.debugLineNum = 1048587;BA.debugLine="Exit";
this.state = 8;
if (true) break;
 if (true) break;

case 7:
//C
this.state = 10;
;
 if (true) break;
if (true) break;

case 8:
//C
this.state = -1;
;
RDebugUtils.currentLine=1048591;
 //BA.debugLineNum = 1048591;BA.debugLine="DisconnectMqtt";
_disconnectmqtt();
RDebugUtils.currentLine=1048592;
 //BA.debugLineNum = 1048592;BA.debugLine="Sleep(100)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "main", "lblviewbord_click"),(int) (100));
this.state = 11;
return;
case 11:
//C
this.state = -1;
;
RDebugUtils.currentLine=1048594;
 //BA.debugLineNum = 1048594;BA.debugLine="StartActivity(ServerBoard)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(parent.mostCurrent._serverboard.getObject()));
RDebugUtils.currentLine=1048595;
 //BA.debugLineNum = 1048595;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static String  _pnllocation_click() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "pnllocation_click", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "pnllocation_click", null));}
RDebugUtils.currentLine=1441792;
 //BA.debugLineNum = 1441792;BA.debugLine="Sub pnlLocation_Click";
RDebugUtils.currentLine=1441793;
 //BA.debugLineNum = 1441793;BA.debugLine="StartActivity(locations)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(mostCurrent._locations.getObject()));
RDebugUtils.currentLine=1441794;
 //BA.debugLineNum = 1441794;BA.debugLine="End Sub";
return "";
}
public static String  _tmrbordalive_tick() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "tmrbordalive_tick", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "tmrbordalive_tick", null));}
RDebugUtils.currentLine=393216;
 //BA.debugLineNum = 393216;BA.debugLine="Sub tmrBordAlive_Tick";
RDebugUtils.currentLine=393217;
 //BA.debugLineNum = 393217;BA.debugLine="If Starter.diedIndex > -1 Then";
if (mostCurrent._starter._diedindex /*int*/ >-1) { 
RDebugUtils.currentLine=393218;
 //BA.debugLineNum = 393218;BA.debugLine="clvServer.RemoveAt(Starter.diedIndex)";
mostCurrent._clvserver._removeat(mostCurrent._starter._diedindex /*int*/ );
RDebugUtils.currentLine=393219;
 //BA.debugLineNum = 393219;BA.debugLine="Starter.serverList.RemoveAt(Starter.diedIndex)";
mostCurrent._starter._serverlist /*anywheresoftware.b4a.objects.collections.List*/ .RemoveAt(mostCurrent._starter._diedindex /*int*/ );
RDebugUtils.currentLine=393220;
 //BA.debugLineNum = 393220;BA.debugLine="Starter.diedIndex = -1";
mostCurrent._starter._diedindex /*int*/  = (int) (-1);
 };
RDebugUtils.currentLine=393222;
 //BA.debugLineNum = 393222;BA.debugLine="ServerAlive";
_serveralive();
RDebugUtils.currentLine=393223;
 //BA.debugLineNum = 393223;BA.debugLine="End Sub";
return "";
}
}