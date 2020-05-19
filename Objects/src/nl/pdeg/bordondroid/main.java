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
vis = vis | (serverboard.mostCurrent != null);
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
public anywheresoftware.b4a.objects.LabelWrapper _lblcurrlocation = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblnobord = null;
public anywheresoftware.b4a.objects.PanelWrapper _pnllocationlist = null;
public nl.pdeg.bordondroid.b4xfloattextfield _edtfloatcode = null;
public nl.pdeg.bordondroid.b4xfloattextfield _edtfloatdescription = null;
public b4a.example.dateutils _dateutils = null;
public nl.pdeg.bordondroid.locations _locations = null;
public nl.pdeg.bordondroid.serverboard _serverboard = null;
public nl.pdeg.bordondroid.starter _starter = null;
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
 //BA.debugLineNum = 131074;BA.debugLine="baseFile.Initialize";
_basefile._initialize /*String*/ (null,processBA);
RDebugUtils.currentLine=131075;
 //BA.debugLineNum = 131075;BA.debugLine="lblVersion.Text = \"v\"&Application.VersionName";
mostCurrent._lblversion.setText(BA.ObjectToCharSequence("v"+anywheresoftware.b4a.keywords.Common.Application.getVersionName()));
RDebugUtils.currentLine=131076;
 //BA.debugLineNum = 131076;BA.debugLine="pnlNobords.Visible = False";
mostCurrent._pnlnobords.setVisible(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=131077;
 //BA.debugLineNum = 131077;BA.debugLine="End Sub";
return "";
}
public static boolean  _activity_keypress(int _keycode) throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "activity_keypress", false))
	 {return ((Boolean) Debug.delegate(mostCurrent.activityBA, "activity_keypress", new Object[] {_keycode}));}
RDebugUtils.currentLine=1114112;
 //BA.debugLineNum = 1114112;BA.debugLine="Private Sub Activity_KeyPress(KeyCode As Int) As B";
RDebugUtils.currentLine=1114113;
 //BA.debugLineNum = 1114113;BA.debugLine="If KeyCode = KeyCodes.KEYCODE_BACK Then";
if (_keycode==anywheresoftware.b4a.keywords.Common.KeyCodes.KEYCODE_BACK) { 
RDebugUtils.currentLine=1114114;
 //BA.debugLineNum = 1114114;BA.debugLine="tmrBordLastAlive.Enabled = False";
_tmrbordlastalive.setEnabled(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=1114115;
 //BA.debugLineNum = 1114115;BA.debugLine="DisconnectMqtt";
_disconnectmqtt();
RDebugUtils.currentLine=1114116;
 //BA.debugLineNum = 1114116;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
RDebugUtils.currentLine=1114118;
 //BA.debugLineNum = 1114118;BA.debugLine="Return False";
if (true) return anywheresoftware.b4a.keywords.Common.False;
 }else {
RDebugUtils.currentLine=1114120;
 //BA.debugLineNum = 1114120;BA.debugLine="Return False";
if (true) return anywheresoftware.b4a.keywords.Common.False;
 };
RDebugUtils.currentLine=1114122;
 //BA.debugLineNum = 1114122;BA.debugLine="End Sub";
return false;
}
public static String  _disconnectmqtt() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "disconnectmqtt", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "disconnectmqtt", null));}
RDebugUtils.currentLine=1179648;
 //BA.debugLineNum = 1179648;BA.debugLine="Sub DisconnectMqtt";
RDebugUtils.currentLine=1179649;
 //BA.debugLineNum = 1179649;BA.debugLine="If mqttGetData.connected Then";
if (_mqttgetdata._connected /*boolean*/ ) { 
RDebugUtils.currentLine=1179650;
 //BA.debugLineNum = 1179650;BA.debugLine="mqttGetData.Disconnect";
_mqttgetdata._disconnect /*String*/ (null);
 };
RDebugUtils.currentLine=1179652;
 //BA.debugLineNum = 1179652;BA.debugLine="If CheckClientConnected Then";
if (_checkclientconnected()) { 
RDebugUtils.currentLine=1179653;
 //BA.debugLineNum = 1179653;BA.debugLine="mqttGetBord.Disconnect";
_mqttgetbord._disconnect /*String*/ (null);
 };
RDebugUtils.currentLine=1179656;
 //BA.debugLineNum = 1179656;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
RDebugUtils.currentModule="main";
RDebugUtils.currentLine=262144;
 //BA.debugLineNum = 262144;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
RDebugUtils.currentLine=262145;
 //BA.debugLineNum = 262145;BA.debugLine="tmrBordLastAlive.Enabled = False";
_tmrbordlastalive.setEnabled(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=262155;
 //BA.debugLineNum = 262155;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "activity_resume", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "activity_resume", null));}
nl.pdeg.bordondroid.main._bordstatus _bd = null;
RDebugUtils.currentLine=196608;
 //BA.debugLineNum = 196608;BA.debugLine="Sub Activity_Resume";
RDebugUtils.currentLine=196611;
 //BA.debugLineNum = 196611;BA.debugLine="If Starter.mqttGetBordsActive Then";
if (mostCurrent._starter._mqttgetbordsactive /*boolean*/ ) { 
RDebugUtils.currentLine=196612;
 //BA.debugLineNum = 196612;BA.debugLine="If Starter.serverList.Size > 0 Then";
if (mostCurrent._starter._serverlist /*anywheresoftware.b4a.objects.collections.List*/ .getSize()>0) { 
RDebugUtils.currentLine=196613;
 //BA.debugLineNum = 196613;BA.debugLine="clvServer.Clear";
mostCurrent._clvserver._clear();
RDebugUtils.currentLine=196614;
 //BA.debugLineNum = 196614;BA.debugLine="pnlNobords.SetVisibleAnimated(100, False)";
mostCurrent._pnlnobords.SetVisibleAnimated((int) (100),anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=196615;
 //BA.debugLineNum = 196615;BA.debugLine="For Each bd As bordStatus In Starter.serverList";
{
final anywheresoftware.b4a.BA.IterableList group5 = mostCurrent._starter._serverlist /*anywheresoftware.b4a.objects.collections.List*/ ;
final int groupLen5 = group5.getSize()
;int index5 = 0;
;
for (; index5 < groupLen5;index5++){
_bd = (nl.pdeg.bordondroid.main._bordstatus)(group5.Get(index5));
RDebugUtils.currentLine=196616;
 //BA.debugLineNum = 196616;BA.debugLine="clvServer.Add(genUnitList(bd.name, bd.alive),";
mostCurrent._clvserver._add((anywheresoftware.b4a.objects.B4XViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.B4XViewWrapper(), (java.lang.Object)(_genunitlist(_bd.name /*String*/ ,_bd.alive /*boolean*/ ).getObject())),(Object)(""));
 }
};
 };
RDebugUtils.currentLine=196619;
 //BA.debugLineNum = 196619;BA.debugLine="If CheckClientConnected = False Then";
if (_checkclientconnected()==anywheresoftware.b4a.keywords.Common.False) { 
RDebugUtils.currentLine=196620;
 //BA.debugLineNum = 196620;BA.debugLine="mqttGetBord.Connect";
_mqttgetbord._connect /*String*/ (null);
 };
 };
RDebugUtils.currentLine=196623;
 //BA.debugLineNum = 196623;BA.debugLine="End Sub";
return "";
}
public static anywheresoftware.b4a.objects.PanelWrapper  _genunitlist(String _name,boolean _alive) throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "genunitlist", false))
	 {return ((anywheresoftware.b4a.objects.PanelWrapper) Debug.delegate(mostCurrent.activityBA, "genunitlist", new Object[] {_name,_alive}));}
anywheresoftware.b4a.objects.PanelWrapper _p = null;
RDebugUtils.currentLine=1245184;
 //BA.debugLineNum = 1245184;BA.debugLine="Sub genUnitList(name As String, alive As Boolean)";
RDebugUtils.currentLine=1245185;
 //BA.debugLineNum = 1245185;BA.debugLine="Dim p As Panel";
_p = new anywheresoftware.b4a.objects.PanelWrapper();
RDebugUtils.currentLine=1245186;
 //BA.debugLineNum = 1245186;BA.debugLine="p.Initialize(Me)";
_p.Initialize(mostCurrent.activityBA,BA.ObjectToString(main.getObject()));
RDebugUtils.currentLine=1245187;
 //BA.debugLineNum = 1245187;BA.debugLine="p.SetLayout(0dip, 0dip, clvServer.AsView.Width, 9";
_p.SetLayout(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)),mostCurrent._clvserver._asview().getWidth(),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (95)));
RDebugUtils.currentLine=1245188;
 //BA.debugLineNum = 1245188;BA.debugLine="p.LoadLayout(\"clvBorden\")";
_p.LoadLayout("clvBorden",mostCurrent.activityBA);
RDebugUtils.currentLine=1245189;
 //BA.debugLineNum = 1245189;BA.debugLine="p.Tag = name";
_p.setTag((Object)(_name));
RDebugUtils.currentLine=1245191;
 //BA.debugLineNum = 1245191;BA.debugLine="lblBordName.Text = name.Trim";
mostCurrent._lblbordname.setText(BA.ObjectToCharSequence(_name.trim()));
RDebugUtils.currentLine=1245192;
 //BA.debugLineNum = 1245192;BA.debugLine="lblLastCheck.Text = $\"Laatste controle $Time{Date";
mostCurrent._lbllastcheck.setText(BA.ObjectToCharSequence(("Laatste controle "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("time",(Object)(anywheresoftware.b4a.keywords.Common.DateTime.getNow()))+"")));
RDebugUtils.currentLine=1245193;
 //BA.debugLineNum = 1245193;BA.debugLine="If Not(alive) Then";
if (anywheresoftware.b4a.keywords.Common.Not(_alive)) { 
RDebugUtils.currentLine=1245194;
 //BA.debugLineNum = 1245194;BA.debugLine="lblViewBord.Enabled = False";
mostCurrent._lblviewbord.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 };
RDebugUtils.currentLine=1245196;
 //BA.debugLineNum = 1245196;BA.debugLine="Return p";
if (true) return _p;
RDebugUtils.currentLine=1245197;
 //BA.debugLineNum = 1245197;BA.debugLine="End Sub";
return null;
}
public static boolean  _checkclientconnected() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "checkclientconnected", false))
	 {return ((Boolean) Debug.delegate(mostCurrent.activityBA, "checkclientconnected", null));}
RDebugUtils.currentLine=2031616;
 //BA.debugLineNum = 2031616;BA.debugLine="Private Sub CheckClientConnected As Boolean";
RDebugUtils.currentLine=2031617;
 //BA.debugLineNum = 2031617;BA.debugLine="Return CallSub(mqttGetBord, \"GetClientConnected\")";
if (true) return BA.ObjectToBoolean(anywheresoftware.b4a.keywords.Common.CallSubDebug(processBA,(Object)(_mqttgetbord),"GetClientConnected"));
RDebugUtils.currentLine=2031618;
 //BA.debugLineNum = 2031618;BA.debugLine="End Sub";
return false;
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
RDebugUtils.currentLine=1441793;
 //BA.debugLineNum = 1441793;BA.debugLine="Dim bordStatus As bordStatus";
_bordstatus = new nl.pdeg.bordondroid.main._bordstatus();
RDebugUtils.currentLine=1441794;
 //BA.debugLineNum = 1441794;BA.debugLine="bordStatus.Initialize";
_bordstatus.Initialize();
RDebugUtils.currentLine=1441795;
 //BA.debugLineNum = 1441795;BA.debugLine="bordStatus.ip = ip";
_bordstatus.ip /*String*/  = _ip;
RDebugUtils.currentLine=1441796;
 //BA.debugLineNum = 1441796;BA.debugLine="bordStatus.name = name";
_bordstatus.name /*String*/  = _name;
RDebugUtils.currentLine=1441797;
 //BA.debugLineNum = 1441797;BA.debugLine="bordStatus.timeStamp = DateTime.Now";
_bordstatus.timeStamp /*long*/  = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
RDebugUtils.currentLine=1441798;
 //BA.debugLineNum = 1441798;BA.debugLine="bordStatus.alive = True";
_bordstatus.alive /*boolean*/  = anywheresoftware.b4a.keywords.Common.True;
RDebugUtils.currentLine=1441799;
 //BA.debugLineNum = 1441799;BA.debugLine="Starter.serverList.Add(bordStatus)";
parent.mostCurrent._starter._serverlist /*anywheresoftware.b4a.objects.collections.List*/ .Add((Object)(_bordstatus));
RDebugUtils.currentLine=1441800;
 //BA.debugLineNum = 1441800;BA.debugLine="CheckIpInClv(bordStatus)";
_checkipinclv(_bordstatus);
RDebugUtils.currentLine=1441802;
 //BA.debugLineNum = 1441802;BA.debugLine="If clvServer.GetSize > 0 Then";
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
RDebugUtils.currentLine=1441803;
 //BA.debugLineNum = 1441803;BA.debugLine="pnlNobords.SetVisibleAnimated(1000, False)";
parent.mostCurrent._pnlnobords.SetVisibleAnimated((int) (1000),anywheresoftware.b4a.keywords.Common.False);
 if (true) break;

case 5:
//C
this.state = 6;
RDebugUtils.currentLine=1441805;
 //BA.debugLineNum = 1441805;BA.debugLine="pnlNobords.SetVisibleAnimated(1000, True)";
parent.mostCurrent._pnlnobords.SetVisibleAnimated((int) (1000),anywheresoftware.b4a.keywords.Common.True);
 if (true) break;

case 6:
//C
this.state = -1;
;
RDebugUtils.currentLine=1441807;
 //BA.debugLineNum = 1441807;BA.debugLine="Sleep(1000)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "main", "addunkownip"),(int) (1000));
this.state = 7;
return;
case 7:
//C
this.state = -1;
;
RDebugUtils.currentLine=1441809;
 //BA.debugLineNum = 1441809;BA.debugLine="End Sub";
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
RDebugUtils.currentLine=1507328;
 //BA.debugLineNum = 1507328;BA.debugLine="Sub CheckIpInClv(bord As bordStatus)";
RDebugUtils.currentLine=1507329;
 //BA.debugLineNum = 1507329;BA.debugLine="Dim p As Panel";
_p = new anywheresoftware.b4a.objects.PanelWrapper();
RDebugUtils.currentLine=1507330;
 //BA.debugLineNum = 1507330;BA.debugLine="Dim lbl As Label";
_lbl = new anywheresoftware.b4a.objects.LabelWrapper();
RDebugUtils.currentLine=1507332;
 //BA.debugLineNum = 1507332;BA.debugLine="For i = 0 To clvServer.GetSize-1";
{
final int step3 = 1;
final int limit3 = (int) (mostCurrent._clvserver._getsize()-1);
_i = (int) (0) ;
for (;_i <= limit3 ;_i = _i + step3 ) {
RDebugUtils.currentLine=1507333;
 //BA.debugLineNum = 1507333;BA.debugLine="p = clvServer.GetPanel(i)";
_p.setObject((android.view.ViewGroup)(mostCurrent._clvserver._getpanel(_i).getObject()));
RDebugUtils.currentLine=1507334;
 //BA.debugLineNum = 1507334;BA.debugLine="If p.Tag = bord.name Then";
if ((_p.getTag()).equals((Object)(_bord.name /*String*/ ))) { 
RDebugUtils.currentLine=1507335;
 //BA.debugLineNum = 1507335;BA.debugLine="For Each v As View In p.GetAllViewsRecursive";
_v = new anywheresoftware.b4a.objects.ConcreteViewWrapper();
{
final anywheresoftware.b4a.BA.IterableList group6 = _p.GetAllViewsRecursive();
final int groupLen6 = group6.getSize()
;int index6 = 0;
;
for (; index6 < groupLen6;index6++){
_v.setObject((android.view.View)(group6.Get(index6)));
RDebugUtils.currentLine=1507336;
 //BA.debugLineNum = 1507336;BA.debugLine="If v.Tag = \"lblLastCheck\" Then";
if ((_v.getTag()).equals((Object)("lblLastCheck"))) { 
RDebugUtils.currentLine=1507337;
 //BA.debugLineNum = 1507337;BA.debugLine="lbl = v";
_lbl.setObject((android.widget.TextView)(_v.getObject()));
RDebugUtils.currentLine=1507338;
 //BA.debugLineNum = 1507338;BA.debugLine="lbl.Text = $\"Laatste controle $Time{DateTime.";
_lbl.setText(BA.ObjectToCharSequence(("Laatste controle "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("time",(Object)(anywheresoftware.b4a.keywords.Common.DateTime.getNow()))+"")));
 };
 }
};
RDebugUtils.currentLine=1507342;
 //BA.debugLineNum = 1507342;BA.debugLine="For Each v As View In p.GetAllViewsRecursive";
_v = new anywheresoftware.b4a.objects.ConcreteViewWrapper();
{
final anywheresoftware.b4a.BA.IterableList group12 = _p.GetAllViewsRecursive();
final int groupLen12 = group12.getSize()
;int index12 = 0;
;
for (; index12 < groupLen12;index12++){
_v.setObject((android.view.View)(group12.Get(index12)));
RDebugUtils.currentLine=1507343;
 //BA.debugLineNum = 1507343;BA.debugLine="If v.Tag = \"viewbord\" Then";
if ((_v.getTag()).equals((Object)("viewbord"))) { 
RDebugUtils.currentLine=1507344;
 //BA.debugLineNum = 1507344;BA.debugLine="lbl = v";
_lbl.setObject((android.widget.TextView)(_v.getObject()));
RDebugUtils.currentLine=1507345;
 //BA.debugLineNum = 1507345;BA.debugLine="If bord.alive = False Then";
if (_bord.alive /*boolean*/ ==anywheresoftware.b4a.keywords.Common.False) { 
RDebugUtils.currentLine=1507346;
 //BA.debugLineNum = 1507346;BA.debugLine="lbl.Enabled = False";
_lbl.setEnabled(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=1507347;
 //BA.debugLineNum = 1507347;BA.debugLine="lbl.TextColor = Colors.Red";
_lbl.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
RDebugUtils.currentLine=1507348;
 //BA.debugLineNum = 1507348;BA.debugLine="Starter.diedIndex = i";
mostCurrent._starter._diedindex /*int*/  = _i;
 }else {
RDebugUtils.currentLine=1507350;
 //BA.debugLineNum = 1507350;BA.debugLine="lbl.Enabled = True";
_lbl.setEnabled(anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=1507351;
 //BA.debugLineNum = 1507351;BA.debugLine="lbl.TextColor = 0xFF027F00";
_lbl.setTextColor((int) (0xff027f00));
 };
RDebugUtils.currentLine=1507353;
 //BA.debugLineNum = 1507353;BA.debugLine="Return";
if (true) return "";
 };
 }
};
 };
 }
};
RDebugUtils.currentLine=1507360;
 //BA.debugLineNum = 1507360;BA.debugLine="pnlNobords.SetVisibleAnimated(1000, False)";
mostCurrent._pnlnobords.SetVisibleAnimated((int) (1000),anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=1507361;
 //BA.debugLineNum = 1507361;BA.debugLine="clvServer.Add(genUnitList(bord.name, bord.alive),";
mostCurrent._clvserver._add((anywheresoftware.b4a.objects.B4XViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.B4XViewWrapper(), (java.lang.Object)(_genunitlist(_bord.name /*String*/ ,_bord.alive /*boolean*/ ).getObject())),(Object)(""));
RDebugUtils.currentLine=1507362;
 //BA.debugLineNum = 1507362;BA.debugLine="End Sub";
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
RDebugUtils.currentLine=1769473;
 //BA.debugLineNum = 1769473;BA.debugLine="Msgbox2Async(\"Applicatie afsluiten?\", \"Bord Op Dr";
anywheresoftware.b4a.keywords.Common.Msgbox2Async(BA.ObjectToCharSequence("Applicatie afsluiten?"),BA.ObjectToCharSequence("Bord Op Droid"),"JA","","NEE",(anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper(), (android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null)),processBA,anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=1769474;
 //BA.debugLineNum = 1769474;BA.debugLine="Wait For Msgbox_Result(Result As Int)";
anywheresoftware.b4a.keywords.Common.WaitFor("msgbox_result", processBA, new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "main", "btncancel_click"), null);
this.state = 5;
return;
case 5:
//C
this.state = 1;
_result = (Integer) result[0];
;
RDebugUtils.currentLine=1769475;
 //BA.debugLineNum = 1769475;BA.debugLine="If Result = DialogResponse.POSITIVE Then";
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
RDebugUtils.currentLine=1769476;
 //BA.debugLineNum = 1769476;BA.debugLine="Activity.Finish";
parent.mostCurrent._activity.Finish();
 if (true) break;

case 4:
//C
this.state = -1;
;
RDebugUtils.currentLine=1769478;
 //BA.debugLineNum = 1769478;BA.debugLine="End Sub";
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
RDebugUtils.currentLine=1835009;
 //BA.debugLineNum = 1835009;BA.debugLine="Starter.testBaseName = True";
parent.mostCurrent._starter._testbasename /*boolean*/  = anywheresoftware.b4a.keywords.Common.True;
RDebugUtils.currentLine=1835010;
 //BA.debugLineNum = 1835010;BA.debugLine="mqttGetBord.Initialize";
parent._mqttgetbord._initialize /*String*/ (null,processBA);
RDebugUtils.currentLine=1835011;
 //BA.debugLineNum = 1835011;BA.debugLine="CallSub2(Starter, \"SetSubBase\", edtLocationCode.T";
anywheresoftware.b4a.keywords.Common.CallSubDebug2(processBA,(Object)(parent.mostCurrent._starter.getObject()),"SetSubBase",(Object)(parent.mostCurrent._edtlocationcode.getText()));
RDebugUtils.currentLine=1835012;
 //BA.debugLineNum = 1835012;BA.debugLine="CallSub(Starter, \"SetSubGetUnits\")";
anywheresoftware.b4a.keywords.Common.CallSubDebug(processBA,(Object)(parent.mostCurrent._starter.getObject()),"SetSubGetUnits");
RDebugUtils.currentLine=1835013;
 //BA.debugLineNum = 1835013;BA.debugLine="mqttGetBord.SetPubBord";
parent._mqttgetbord._setpubbord /*String*/ (null);
RDebugUtils.currentLine=1835014;
 //BA.debugLineNum = 1835014;BA.debugLine="Sleep(100)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "main", "btnok_click"),(int) (100));
this.state = 13;
return;
case 13:
//C
this.state = 1;
;
RDebugUtils.currentLine=1835015;
 //BA.debugLineNum = 1835015;BA.debugLine="mqttGetBord.Connect";
parent._mqttgetbord._connect /*String*/ (null);
RDebugUtils.currentLine=1835017;
 //BA.debugLineNum = 1835017;BA.debugLine="Sleep(1000)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "main", "btnok_click"),(int) (1000));
this.state = 14;
return;
case 14:
//C
this.state = 1;
;
RDebugUtils.currentLine=1835019;
 //BA.debugLineNum = 1835019;BA.debugLine="If mqttGetBord.connected = False Then";
if (true) break;

case 1:
//if
this.state = 12;
if (parent._mqttgetbord._connected /*boolean*/ ==anywheresoftware.b4a.keywords.Common.False) { 
this.state = 3;
}else {
this.state = 5;
}if (true) break;

case 3:
//C
this.state = 12;
RDebugUtils.currentLine=1835020;
 //BA.debugLineNum = 1835020;BA.debugLine="Msgbox2Async(\"Kan geen verbinding maken met loca";
anywheresoftware.b4a.keywords.Common.Msgbox2Async(BA.ObjectToCharSequence("Kan geen verbinding maken met locatie"),BA.ObjectToCharSequence("Bord Op Droid"),"OKE","","",(anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper(), (android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null)),processBA,anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=1835021;
 //BA.debugLineNum = 1835021;BA.debugLine="Wait For Msgbox_Result(Result As Int)";
anywheresoftware.b4a.keywords.Common.WaitFor("msgbox_result", processBA, new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "main", "btnok_click"), null);
this.state = 15;
return;
case 15:
//C
this.state = 12;
_result = (Integer) result[0];
;
 if (true) break;

case 5:
//C
this.state = 6;
RDebugUtils.currentLine=1835023;
 //BA.debugLineNum = 1835023;BA.debugLine="Dim code, description As String";
_code = "";
_description = "";
RDebugUtils.currentLine=1835025;
 //BA.debugLineNum = 1835025;BA.debugLine="code = edtFloatCode.Text";
_code = parent.mostCurrent._edtfloatcode._gettext /*String*/ (null);
RDebugUtils.currentLine=1835026;
 //BA.debugLineNum = 1835026;BA.debugLine="If edtFloatDescription.Text = \"\" Then edtFloatDe";
if (true) break;

case 6:
//if
this.state = 11;
if ((parent.mostCurrent._edtfloatdescription._gettext /*String*/ (null)).equals("")) { 
this.state = 8;
;}if (true) break;

case 8:
//C
this.state = 11;
parent.mostCurrent._edtfloatdescription._settext /*String*/ (null,"Nieuwe locatie");
if (true) break;

case 11:
//C
this.state = 12;
;
RDebugUtils.currentLine=1835027;
 //BA.debugLineNum = 1835027;BA.debugLine="description = edtFloatDescription.Text";
_description = parent.mostCurrent._edtfloatdescription._gettext /*String*/ (null);
RDebugUtils.currentLine=1835028;
 //BA.debugLineNum = 1835028;BA.debugLine="ToastMessageShow(\"Verbonden met locatie\", True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Verbonden met locatie"),anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=1835029;
 //BA.debugLineNum = 1835029;BA.debugLine="mqttGetBord.Disconnect";
parent._mqttgetbord._disconnect /*String*/ (null);
RDebugUtils.currentLine=1835030;
 //BA.debugLineNum = 1835030;BA.debugLine="pnlLocationCOde.Visible = False";
parent.mostCurrent._pnllocationcode.setVisible(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=1835031;
 //BA.debugLineNum = 1835031;BA.debugLine="baseFile.SetBase(code, description, \"1\")";
parent._basefile._setbase /*String*/ (null,_code,_description,"1");
RDebugUtils.currentLine=1835032;
 //BA.debugLineNum = 1835032;BA.debugLine="InitConnection(code, description)";
_initconnection(_code,_description);
 if (true) break;

case 12:
//C
this.state = -1;
;
RDebugUtils.currentLine=1835035;
 //BA.debugLineNum = 1835035;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static String  _initconnection(String _locationcode,String _location) throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "initconnection", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "initconnection", new Object[] {_locationcode,_location}));}
RDebugUtils.currentLine=851968;
 //BA.debugLineNum = 851968;BA.debugLine="Sub InitConnection(locationCode As String, locatio";
RDebugUtils.currentLine=851969;
 //BA.debugLineNum = 851969;BA.debugLine="if mqttGetBord.connected then mqttGetBord.Disconn";
if (_mqttgetbord._connected /*boolean*/ ) { 
_mqttgetbord._disconnect /*String*/ (null);};
RDebugUtils.currentLine=851970;
 //BA.debugLineNum = 851970;BA.debugLine="clvServer.Clear";
mostCurrent._clvserver._clear();
RDebugUtils.currentLine=851971;
 //BA.debugLineNum = 851971;BA.debugLine="lblCurrLocation.Text = location";
mostCurrent._lblcurrlocation.setText(BA.ObjectToCharSequence(_location));
RDebugUtils.currentLine=851972;
 //BA.debugLineNum = 851972;BA.debugLine="CallSub2(Starter, \"SetSubBase\", locationCode)";
anywheresoftware.b4a.keywords.Common.CallSubDebug2(processBA,(Object)(mostCurrent._starter.getObject()),"SetSubBase",(Object)(_locationcode));
RDebugUtils.currentLine=851973;
 //BA.debugLineNum = 851973;BA.debugLine="CallSub(Starter, \"SetSubGetUnits\")";
anywheresoftware.b4a.keywords.Common.CallSubDebug(processBA,(Object)(mostCurrent._starter.getObject()),"SetSubGetUnits");
RDebugUtils.currentLine=851974;
 //BA.debugLineNum = 851974;BA.debugLine="StartConnection";
_startconnection();
RDebugUtils.currentLine=851975;
 //BA.debugLineNum = 851975;BA.debugLine="End Sub";
return "";
}
public static String  _checkipexits(String _bord) throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "checkipexits", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "checkipexits", new Object[] {_bord}));}
boolean _ipfound = false;
String _name = "";
nl.pdeg.bordondroid.main._bordstatus _lst = null;
RDebugUtils.currentLine=1310720;
 //BA.debugLineNum = 1310720;BA.debugLine="Sub CheckIpExits(bord As String)";
RDebugUtils.currentLine=1310722;
 //BA.debugLineNum = 1310722;BA.debugLine="Dim ipFound As Boolean = False";
_ipfound = anywheresoftware.b4a.keywords.Common.False;
RDebugUtils.currentLine=1310723;
 //BA.debugLineNum = 1310723;BA.debugLine="Dim name As String = \"\"";
_name = "";
RDebugUtils.currentLine=1310725;
 //BA.debugLineNum = 1310725;BA.debugLine="If bord.Length = 0 Then Return";
if (_bord.length()==0) { 
if (true) return "";};
RDebugUtils.currentLine=1310727;
 //BA.debugLineNum = 1310727;BA.debugLine="name = bord";
_name = _bord;
RDebugUtils.currentLine=1310729;
 //BA.debugLineNum = 1310729;BA.debugLine="If Starter.serverList.Size = 0 Then";
if (mostCurrent._starter._serverlist /*anywheresoftware.b4a.objects.collections.List*/ .getSize()==0) { 
RDebugUtils.currentLine=1310730;
 //BA.debugLineNum = 1310730;BA.debugLine="AddUnkownIp(\"\", name)";
_addunkownip("",_name);
RDebugUtils.currentLine=1310731;
 //BA.debugLineNum = 1310731;BA.debugLine="Return";
if (true) return "";
 };
RDebugUtils.currentLine=1310734;
 //BA.debugLineNum = 1310734;BA.debugLine="For Each lst As bordStatus In Starter.serverList";
{
final anywheresoftware.b4a.BA.IterableList group9 = mostCurrent._starter._serverlist /*anywheresoftware.b4a.objects.collections.List*/ ;
final int groupLen9 = group9.getSize()
;int index9 = 0;
;
for (; index9 < groupLen9;index9++){
_lst = (nl.pdeg.bordondroid.main._bordstatus)(group9.Get(index9));
RDebugUtils.currentLine=1310735;
 //BA.debugLineNum = 1310735;BA.debugLine="If lst.name = name Then";
if ((_lst.name /*String*/ ).equals(_name)) { 
RDebugUtils.currentLine=1310736;
 //BA.debugLineNum = 1310736;BA.debugLine="ipFound = True";
_ipfound = anywheresoftware.b4a.keywords.Common.True;
RDebugUtils.currentLine=1310737;
 //BA.debugLineNum = 1310737;BA.debugLine="lst.timeStamp = DateTime.Now";
_lst.timeStamp /*long*/  = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
RDebugUtils.currentLine=1310738;
 //BA.debugLineNum = 1310738;BA.debugLine="lst.alive = True";
_lst.alive /*boolean*/  = anywheresoftware.b4a.keywords.Common.True;
 };
 }
};
RDebugUtils.currentLine=1310742;
 //BA.debugLineNum = 1310742;BA.debugLine="ServerAlive";
_serveralive();
RDebugUtils.currentLine=1310744;
 //BA.debugLineNum = 1310744;BA.debugLine="If Not(ipFound) Then";
if (anywheresoftware.b4a.keywords.Common.Not(_ipfound)) { 
RDebugUtils.currentLine=1310745;
 //BA.debugLineNum = 1310745;BA.debugLine="AddUnkownIp(\"\", name)";
_addunkownip("",_name);
RDebugUtils.currentLine=1310746;
 //BA.debugLineNum = 1310746;BA.debugLine="Return";
if (true) return "";
 };
RDebugUtils.currentLine=1310748;
 //BA.debugLineNum = 1310748;BA.debugLine="End Sub";
return "";
}
public static String  _serveralive() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "serveralive", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "serveralive", null));}
long _msnow = 0L;
nl.pdeg.bordondroid.main._bordstatus _lst = null;
RDebugUtils.currentLine=1376256;
 //BA.debugLineNum = 1376256;BA.debugLine="Sub ServerAlive";
RDebugUtils.currentLine=1376258;
 //BA.debugLineNum = 1376258;BA.debugLine="Dim msNow As Long = DateTime.Now";
_msnow = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
RDebugUtils.currentLine=1376259;
 //BA.debugLineNum = 1376259;BA.debugLine="For Each lst As bordStatus In Starter.serverList";
{
final anywheresoftware.b4a.BA.IterableList group2 = mostCurrent._starter._serverlist /*anywheresoftware.b4a.objects.collections.List*/ ;
final int groupLen2 = group2.getSize()
;int index2 = 0;
;
for (; index2 < groupLen2;index2++){
_lst = (nl.pdeg.bordondroid.main._bordstatus)(group2.Get(index2));
RDebugUtils.currentLine=1376260;
 //BA.debugLineNum = 1376260;BA.debugLine="If(msNow - lst.timeStamp) > Starter.serverDied A";
if ((_msnow-_lst.timeStamp /*long*/ )>mostCurrent._starter._serverdied /*long*/  && _lst.alive /*boolean*/ ==anywheresoftware.b4a.keywords.Common.True) { 
RDebugUtils.currentLine=1376261;
 //BA.debugLineNum = 1376261;BA.debugLine="lst.alive = False";
_lst.alive /*boolean*/  = anywheresoftware.b4a.keywords.Common.False;
RDebugUtils.currentLine=1376262;
 //BA.debugLineNum = 1376262;BA.debugLine="CheckIpInClv(lst)";
_checkipinclv(_lst);
RDebugUtils.currentLine=1376263;
 //BA.debugLineNum = 1376263;BA.debugLine="Return";
if (true) return "";
 }else {
RDebugUtils.currentLine=1376265;
 //BA.debugLineNum = 1376265;BA.debugLine="CheckIpInClv(lst)";
_checkipinclv(_lst);
 };
 }
};
RDebugUtils.currentLine=1376268;
 //BA.debugLineNum = 1376268;BA.debugLine="End Sub";
return "";
}
public static String  _checklastalivetime() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "checklastalivetime", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "checklastalivetime", null));}
long _currtime = 0L;
long _timediff = 0L;
nl.pdeg.bordondroid.main._bordstatus _bd = null;
RDebugUtils.currentLine=983040;
 //BA.debugLineNum = 983040;BA.debugLine="Sub CheckLastAliveTime";
RDebugUtils.currentLine=983041;
 //BA.debugLineNum = 983041;BA.debugLine="Dim currTime As Long = DateTime.Now";
_currtime = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
RDebugUtils.currentLine=983042;
 //BA.debugLineNum = 983042;BA.debugLine="Dim timeDiff As Long";
_timediff = 0L;
RDebugUtils.currentLine=983044;
 //BA.debugLineNum = 983044;BA.debugLine="For Each bd As bordStatus In Starter.serverList";
{
final anywheresoftware.b4a.BA.IterableList group3 = mostCurrent._starter._serverlist /*anywheresoftware.b4a.objects.collections.List*/ ;
final int groupLen3 = group3.getSize()
;int index3 = 0;
;
for (; index3 < groupLen3;index3++){
_bd = (nl.pdeg.bordondroid.main._bordstatus)(group3.Get(index3));
RDebugUtils.currentLine=983045;
 //BA.debugLineNum = 983045;BA.debugLine="timeDiff = currTime-bd.timeStamp";
_timediff = (long) (_currtime-_bd.timeStamp /*long*/ );
RDebugUtils.currentLine=983046;
 //BA.debugLineNum = 983046;BA.debugLine="If timeDiff >= Starter.serverDied Then";
if (_timediff>=mostCurrent._starter._serverdied /*long*/ ) { 
RDebugUtils.currentLine=983047;
 //BA.debugLineNum = 983047;BA.debugLine="Log(bd.name & \" died\")";
anywheresoftware.b4a.keywords.Common.LogImpl("8983047",_bd.name /*String*/ +" died",0);
 };
 }
};
RDebugUtils.currentLine=983051;
 //BA.debugLineNum = 983051;BA.debugLine="End Sub";
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
RDebugUtils.currentLine=1703937;
 //BA.debugLineNum = 1703937;BA.debugLine="Dim p As Panel = clvServer.GetPanel(Index)";
_p = new anywheresoftware.b4a.objects.PanelWrapper();
_p.setObject((android.view.ViewGroup)(parent.mostCurrent._clvserver._getpanel(_index).getObject()));
RDebugUtils.currentLine=1703938;
 //BA.debugLineNum = 1703938;BA.debugLine="Dim lbl As Label";
_lbl = new anywheresoftware.b4a.objects.LabelWrapper();
RDebugUtils.currentLine=1703941;
 //BA.debugLineNum = 1703941;BA.debugLine="For Each c As View In p.GetAllViewsRecursive";
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
RDebugUtils.currentLine=1703942;
 //BA.debugLineNum = 1703942;BA.debugLine="If c.Tag = \"name\" Then";
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
RDebugUtils.currentLine=1703943;
 //BA.debugLineNum = 1703943;BA.debugLine="lbl = c";
_lbl.setObject((android.widget.TextView)(_c.getObject()));
RDebugUtils.currentLine=1703946;
 //BA.debugLineNum = 1703946;BA.debugLine="CallSub2(Starter, \"SetUnit\", PrepTopicName(lbl.";
anywheresoftware.b4a.keywords.Common.CallSubDebug2(processBA,(Object)(parent.mostCurrent._starter.getObject()),"SetUnit",(Object)(_preptopicname(_lbl.getText()).toLowerCase()));
RDebugUtils.currentLine=1703947;
 //BA.debugLineNum = 1703947;BA.debugLine="Starter.DiscoveredServer = lbl.Text";
parent.mostCurrent._starter._discoveredserver /*String*/  = _lbl.getText();
RDebugUtils.currentLine=1703948;
 //BA.debugLineNum = 1703948;BA.debugLine="Exit";
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
RDebugUtils.currentLine=1703952;
 //BA.debugLineNum = 1703952;BA.debugLine="DisconnectMqtt";
_disconnectmqtt();
RDebugUtils.currentLine=1703953;
 //BA.debugLineNum = 1703953;BA.debugLine="Sleep(100)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "main", "clvserver_itemclick"),(int) (100));
this.state = 11;
return;
case 11:
//C
this.state = -1;
;
RDebugUtils.currentLine=1703954;
 //BA.debugLineNum = 1703954;BA.debugLine="StartActivity(ServerBoard)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(parent.mostCurrent._serverboard.getObject()));
RDebugUtils.currentLine=1703955;
 //BA.debugLineNum = 1703955;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static String  _preptopicname(String _bord) throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "preptopicname", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "preptopicname", new Object[] {_bord}));}
RDebugUtils.currentLine=1638400;
 //BA.debugLineNum = 1638400;BA.debugLine="Private Sub PrepTopicName(bord As String) As Strin";
RDebugUtils.currentLine=1638401;
 //BA.debugLineNum = 1638401;BA.debugLine="Return bord.Replace(\" \", \"\")";
if (true) return _bord.replace(" ","");
RDebugUtils.currentLine=1638402;
 //BA.debugLineNum = 1638402;BA.debugLine="End Sub";
return "";
}
public static String  _edtfloatcode_textchanged(String _old,String _new) throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "edtfloatcode_textchanged", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "edtfloatcode_textchanged", new Object[] {_old,_new}));}
RDebugUtils.currentLine=2293760;
 //BA.debugLineNum = 2293760;BA.debugLine="Sub edtFloatCode_TextChanged (Old As String, New A";
RDebugUtils.currentLine=2293761;
 //BA.debugLineNum = 2293761;BA.debugLine="If New.Length > 0 Then";
if (_new.length()>0) { 
RDebugUtils.currentLine=2293762;
 //BA.debugLineNum = 2293762;BA.debugLine="btnOk.Enabled = True";
mostCurrent._btnok.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 }else {
RDebugUtils.currentLine=2293764;
 //BA.debugLineNum = 2293764;BA.debugLine="btnOk.Enabled = False";
mostCurrent._btnok.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 };
RDebugUtils.currentLine=2293766;
 //BA.debugLineNum = 2293766;BA.debugLine="End Sub";
return "";
}
public static String  _edtfloatdescription_textchanged(String _old,String _new) throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "edtfloatdescription_textchanged", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "edtfloatdescription_textchanged", new Object[] {_old,_new}));}
RDebugUtils.currentLine=2228224;
 //BA.debugLineNum = 2228224;BA.debugLine="Sub edtFloatDescription_TextChanged (Old As String";
RDebugUtils.currentLine=2228226;
 //BA.debugLineNum = 2228226;BA.debugLine="End Sub";
return "";
}
public static String  _edtlocationcode_textchanged(String _old,String _new) throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "edtlocationcode_textchanged", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "edtlocationcode_textchanged", new Object[] {_old,_new}));}
RDebugUtils.currentLine=1900544;
 //BA.debugLineNum = 1900544;BA.debugLine="Sub edtLocationCode_TextChanged (Old As String, Ne";
RDebugUtils.currentLine=1900545;
 //BA.debugLineNum = 1900545;BA.debugLine="If New.Length > 0 Then";
if (_new.length()>0) { 
RDebugUtils.currentLine=1900546;
 //BA.debugLineNum = 1900546;BA.debugLine="btnOk.Enabled = True";
mostCurrent._btnok.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 }else {
RDebugUtils.currentLine=1900548;
 //BA.debugLineNum = 1900548;BA.debugLine="btnOk.Enabled = False";
mostCurrent._btnok.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 };
RDebugUtils.currentLine=1900550;
 //BA.debugLineNum = 1900550;BA.debugLine="End Sub";
return "";
}
public static void  _errorconnecting() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "errorconnecting", false))
	 {Debug.delegate(mostCurrent.activityBA, "errorconnecting", null); return;}
ResumableSub_ErrorConnecting rsub = new ResumableSub_ErrorConnecting(null);
rsub.resume(processBA, null);
}
public static class ResumableSub_ErrorConnecting extends BA.ResumableSub {
public ResumableSub_ErrorConnecting(nl.pdeg.bordondroid.main parent) {
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
RDebugUtils.currentLine=2097153;
 //BA.debugLineNum = 2097153;BA.debugLine="Msgbox2Async(\"Kan geen verbinding maken met locat";
anywheresoftware.b4a.keywords.Common.Msgbox2Async(BA.ObjectToCharSequence("Kan geen verbinding maken met locatie"+anywheresoftware.b4a.keywords.Common.CRLF+"Controleer locatie code"),BA.ObjectToCharSequence("App naam"),"OKE","","",(anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper(), (android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null)),processBA,anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=2097154;
 //BA.debugLineNum = 2097154;BA.debugLine="Wait For Msgbox_Result (Result As Int)";
anywheresoftware.b4a.keywords.Common.WaitFor("msgbox_result", processBA, new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "main", "errorconnecting"), null);
this.state = 5;
return;
case 5:
//C
this.state = 1;
_result = (Integer) result[0];
;
RDebugUtils.currentLine=2097155;
 //BA.debugLineNum = 2097155;BA.debugLine="If Result = DialogResponse.POSITIVE Then";
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
RDebugUtils.currentLine=2097156;
 //BA.debugLineNum = 2097156;BA.debugLine="mqttGetBord.Disconnect";
parent._mqttgetbord._disconnect /*String*/ (null);
 if (true) break;

case 4:
//C
this.state = -1;
;
RDebugUtils.currentLine=2097158;
 //BA.debugLineNum = 2097158;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static String  _getbaselist() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "getbaselist", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "getbaselist", null));}
int _listsize = 0;
nl.pdeg.bordondroid.main._locationbord _loc = null;
RDebugUtils.currentLine=786432;
 //BA.debugLineNum = 786432;BA.debugLine="Private Sub getBaseList";
RDebugUtils.currentLine=786433;
 //BA.debugLineNum = 786433;BA.debugLine="Dim listSize As Int = 0";
_listsize = (int) (0);
RDebugUtils.currentLine=786435;
 //BA.debugLineNum = 786435;BA.debugLine="baseList.Initialize";
_baselist.Initialize();
RDebugUtils.currentLine=786436;
 //BA.debugLineNum = 786436;BA.debugLine="baseList = baseFile.GetBase";
_baselist = _basefile._getbase /*anywheresoftware.b4a.objects.collections.List*/ (null);
RDebugUtils.currentLine=786437;
 //BA.debugLineNum = 786437;BA.debugLine="If baseList.IsInitialized Then";
if (_baselist.IsInitialized()) { 
RDebugUtils.currentLine=786438;
 //BA.debugLineNum = 786438;BA.debugLine="listSize = baseList.Size";
_listsize = _baselist.getSize();
 };
RDebugUtils.currentLine=786440;
 //BA.debugLineNum = 786440;BA.debugLine="If listSize > 1 Then";
if (_listsize>1) { 
RDebugUtils.currentLine=786441;
 //BA.debugLineNum = 786441;BA.debugLine="pnlLocationList.SetVisibleAnimated(500, True)";
mostCurrent._pnllocationlist.SetVisibleAnimated((int) (500),anywheresoftware.b4a.keywords.Common.True);
 }else {
RDebugUtils.currentLine=786443;
 //BA.debugLineNum = 786443;BA.debugLine="pnlLocationList.SetVisibleAnimated(500, False)";
mostCurrent._pnllocationlist.SetVisibleAnimated((int) (500),anywheresoftware.b4a.keywords.Common.False);
 };
RDebugUtils.currentLine=786446;
 //BA.debugLineNum = 786446;BA.debugLine="lblNoBord.Text = \"Wachten op borden...\"";
mostCurrent._lblnobord.setText(BA.ObjectToCharSequence("Wachten op borden..."));
RDebugUtils.currentLine=786448;
 //BA.debugLineNum = 786448;BA.debugLine="If baseList.IsInitialized = False Then";
if (_baselist.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
RDebugUtils.currentLine=786449;
 //BA.debugLineNum = 786449;BA.debugLine="pnlLocationCOde.Visible = True";
mostCurrent._pnllocationcode.setVisible(anywheresoftware.b4a.keywords.Common.True);
 }else 
{RDebugUtils.currentLine=786450;
 //BA.debugLineNum = 786450;BA.debugLine="Else If baseList.Size = 1 Then";
if (_baselist.getSize()==1) { 
RDebugUtils.currentLine=786451;
 //BA.debugLineNum = 786451;BA.debugLine="Dim loc As locationBord";
_loc = new nl.pdeg.bordondroid.main._locationbord();
RDebugUtils.currentLine=786452;
 //BA.debugLineNum = 786452;BA.debugLine="loc.Initialize";
_loc.Initialize();
RDebugUtils.currentLine=786453;
 //BA.debugLineNum = 786453;BA.debugLine="loc = baseList.Get(0)";
_loc = (nl.pdeg.bordondroid.main._locationbord)(_baselist.Get((int) (0)));
RDebugUtils.currentLine=786455;
 //BA.debugLineNum = 786455;BA.debugLine="InitConnection(loc.code, loc.description)";
_initconnection(_loc.code /*String*/ ,_loc.description /*String*/ );
 }else {
RDebugUtils.currentLine=786458;
 //BA.debugLineNum = 786458;BA.debugLine="StartActivity(SelectLocation)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(mostCurrent._selectlocation.getObject()));
 }}
;
RDebugUtils.currentLine=786471;
 //BA.debugLineNum = 786471;BA.debugLine="End Sub";
return "";
}
public static String  _startconnection() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "startconnection", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "startconnection", null));}
RDebugUtils.currentLine=720896;
 //BA.debugLineNum = 720896;BA.debugLine="Private Sub StartConnection";
RDebugUtils.currentLine=720897;
 //BA.debugLineNum = 720897;BA.debugLine="pnlLocationCOde.Visible = False";
mostCurrent._pnllocationcode.setVisible(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=720898;
 //BA.debugLineNum = 720898;BA.debugLine="pnlNobords.Visible = True";
mostCurrent._pnlnobords.setVisible(anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=720899;
 //BA.debugLineNum = 720899;BA.debugLine="clvServer.Clear";
mostCurrent._clvserver._clear();
RDebugUtils.currentLine=720900;
 //BA.debugLineNum = 720900;BA.debugLine="mqttGetData.Initialize";
_mqttgetdata._initialize /*String*/ (null,processBA);
RDebugUtils.currentLine=720901;
 //BA.debugLineNum = 720901;BA.debugLine="mqttGetBord.Initialize";
_mqttgetbord._initialize /*String*/ (null,processBA);
RDebugUtils.currentLine=720902;
 //BA.debugLineNum = 720902;BA.debugLine="mqttGetBord.SetPubBord";
_mqttgetbord._setpubbord /*String*/ (null);
RDebugUtils.currentLine=720903;
 //BA.debugLineNum = 720903;BA.debugLine="mqttGetBord.Connect";
_mqttgetbord._connect /*String*/ (null);
RDebugUtils.currentLine=720905;
 //BA.debugLineNum = 720905;BA.debugLine="baseFile.GetBase";
_basefile._getbase /*anywheresoftware.b4a.objects.collections.List*/ (null);
RDebugUtils.currentLine=720907;
 //BA.debugLineNum = 720907;BA.debugLine="tmrBordLastAlive.Initialize(\"tmrBordAlive\", 10000";
_tmrbordlastalive.Initialize(processBA,"tmrBordAlive",(long) (10000));
RDebugUtils.currentLine=720908;
 //BA.debugLineNum = 720908;BA.debugLine="tmrBordLastAlive.Enabled = True";
_tmrbordlastalive.setEnabled(anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=720910;
 //BA.debugLineNum = 720910;BA.debugLine="End Sub";
return "";
}
public static String  _lblviewbord_click() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "lblviewbord_click", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "lblviewbord_click", null));}
anywheresoftware.b4a.objects.ConcreteViewWrapper _v = null;
anywheresoftware.b4a.objects.PanelWrapper _p = null;
RDebugUtils.currentLine=1572864;
 //BA.debugLineNum = 1572864;BA.debugLine="Sub lblViewBord_Click";
RDebugUtils.currentLine=1572865;
 //BA.debugLineNum = 1572865;BA.debugLine="Dim v As View = Sender";
_v = new anywheresoftware.b4a.objects.ConcreteViewWrapper();
_v.setObject((android.view.View)(anywheresoftware.b4a.keywords.Common.Sender(mostCurrent.activityBA)));
RDebugUtils.currentLine=1572866;
 //BA.debugLineNum = 1572866;BA.debugLine="Dim p As Panel = v.Parent";
_p = new anywheresoftware.b4a.objects.PanelWrapper();
_p.setObject((android.view.ViewGroup)(_v.getParent()));
RDebugUtils.currentLine=1572868;
 //BA.debugLineNum = 1572868;BA.debugLine="clvServer_ItemClick (clvServer.GetItemFromView(p)";
_clvserver_itemclick(mostCurrent._clvserver._getitemfromview((anywheresoftware.b4a.objects.B4XViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.B4XViewWrapper(), (java.lang.Object)(_p.getObject()))),anywheresoftware.b4a.keywords.Common.Null);
RDebugUtils.currentLine=1572869;
 //BA.debugLineNum = 1572869;BA.debugLine="End Sub";
return "";
}
public static String  _pnllocation_click() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "pnllocation_click", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "pnllocation_click", null));}
RDebugUtils.currentLine=1966080;
 //BA.debugLineNum = 1966080;BA.debugLine="Sub pnlLocation_Click";
RDebugUtils.currentLine=1966081;
 //BA.debugLineNum = 1966081;BA.debugLine="If CheckClientConnected Then";
if (_checkclientconnected()) { 
RDebugUtils.currentLine=1966082;
 //BA.debugLineNum = 1966082;BA.debugLine="mqttGetBord.Disconnect";
_mqttgetbord._disconnect /*String*/ (null);
 };
RDebugUtils.currentLine=1966084;
 //BA.debugLineNum = 1966084;BA.debugLine="StartActivity(locations)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(mostCurrent._locations.getObject()));
RDebugUtils.currentLine=1966085;
 //BA.debugLineNum = 1966085;BA.debugLine="End Sub";
return "";
}
public static String  _pnllocationlist_click() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "pnllocationlist_click", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "pnllocationlist_click", null));}
RDebugUtils.currentLine=2162688;
 //BA.debugLineNum = 2162688;BA.debugLine="Sub pnlLocationList_Click";
RDebugUtils.currentLine=2162689;
 //BA.debugLineNum = 2162689;BA.debugLine="StartActivity(SelectLocation)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(mostCurrent._selectlocation.getObject()));
RDebugUtils.currentLine=2162690;
 //BA.debugLineNum = 2162690;BA.debugLine="End Sub";
return "";
}
public static String  _shownotconnectedtobroker() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "shownotconnectedtobroker", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "shownotconnectedtobroker", null));}
RDebugUtils.currentLine=1048576;
 //BA.debugLineNum = 1048576;BA.debugLine="Sub ShowNotConnectedToBroker";
RDebugUtils.currentLine=1048577;
 //BA.debugLineNum = 1048577;BA.debugLine="lblNoBord.Text = \"Verbinding borden verbroken\"";
mostCurrent._lblnobord.setText(BA.ObjectToCharSequence("Verbinding borden verbroken"));
RDebugUtils.currentLine=1048578;
 //BA.debugLineNum = 1048578;BA.debugLine="pnlNobords.SetVisibleAnimated(1000, True)";
mostCurrent._pnlnobords.SetVisibleAnimated((int) (1000),anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=1048579;
 //BA.debugLineNum = 1048579;BA.debugLine="End Sub";
return "";
}
public static String  _tmrbordalive_tick() throws Exception{
RDebugUtils.currentModule="main";
if (Debug.shouldDelegate(mostCurrent.activityBA, "tmrbordalive_tick", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "tmrbordalive_tick", null));}
RDebugUtils.currentLine=917504;
 //BA.debugLineNum = 917504;BA.debugLine="Sub tmrBordAlive_Tick";
RDebugUtils.currentLine=917505;
 //BA.debugLineNum = 917505;BA.debugLine="CheckLastAliveTime";
_checklastalivetime();
RDebugUtils.currentLine=917506;
 //BA.debugLineNum = 917506;BA.debugLine="Return";
if (true) return "";
RDebugUtils.currentLine=917507;
 //BA.debugLineNum = 917507;BA.debugLine="If Starter.diedIndex > -1 Then";
if (mostCurrent._starter._diedindex /*int*/ >-1) { 
RDebugUtils.currentLine=917508;
 //BA.debugLineNum = 917508;BA.debugLine="clvServer.RemoveAt(Starter.diedIndex)";
mostCurrent._clvserver._removeat(mostCurrent._starter._diedindex /*int*/ );
RDebugUtils.currentLine=917509;
 //BA.debugLineNum = 917509;BA.debugLine="Starter.serverList.RemoveAt(Starter.diedIndex)";
mostCurrent._starter._serverlist /*anywheresoftware.b4a.objects.collections.List*/ .RemoveAt(mostCurrent._starter._diedindex /*int*/ );
RDebugUtils.currentLine=917510;
 //BA.debugLineNum = 917510;BA.debugLine="Starter.diedIndex = -1";
mostCurrent._starter._diedindex /*int*/  = (int) (-1);
 };
RDebugUtils.currentLine=917512;
 //BA.debugLineNum = 917512;BA.debugLine="ServerAlive";
_serveralive();
RDebugUtils.currentLine=917513;
 //BA.debugLineNum = 917513;BA.debugLine="End Sub";
return "";
}
}