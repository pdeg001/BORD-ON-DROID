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
public b4a.example.dateutils _dateutils = null;
public nl.pdeg.bordondroid.locations _locations = null;
public nl.pdeg.bordondroid.serverboard _serverboard = null;
public nl.pdeg.bordondroid.starter _starter = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
vis = vis | (locations.mostCurrent != null);
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
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 48;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 49;BA.debugLine="Activity.LoadLayout(\"main\")";
mostCurrent._activity.LoadLayout("main",mostCurrent.activityBA);
 //BA.debugLineNum = 51;BA.debugLine="baseFile.Initialize";
_basefile._initialize /*String*/ (processBA);
 //BA.debugLineNum = 54;BA.debugLine="lblVersion.Text = \"v\"&Application.VersionName";
mostCurrent._lblversion.setText(BA.ObjectToCharSequence("v"+anywheresoftware.b4a.keywords.Common.Application.getVersionName()));
 //BA.debugLineNum = 55;BA.debugLine="pnlNobords.Visible = False";
mostCurrent._pnlnobords.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 57;BA.debugLine="getBaseList";
_getbaselist();
 //BA.debugLineNum = 59;BA.debugLine="End Sub";
return "";
}
public static boolean  _activity_keypress(int _keycode) throws Exception{
 //BA.debugLineNum = 127;BA.debugLine="Private Sub Activity_KeyPress(KeyCode As Int) As B";
 //BA.debugLineNum = 128;BA.debugLine="If KeyCode = KeyCodes.KEYCODE_BACK Then";
if (_keycode==anywheresoftware.b4a.keywords.Common.KeyCodes.KEYCODE_BACK) { 
 //BA.debugLineNum = 129;BA.debugLine="tmrBordLastAlive.Enabled = False";
_tmrbordlastalive.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 130;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 132;BA.debugLine="Return False";
if (true) return anywheresoftware.b4a.keywords.Common.False;
 }else {
 //BA.debugLineNum = 134;BA.debugLine="Return False";
if (true) return anywheresoftware.b4a.keywords.Common.False;
 };
 //BA.debugLineNum = 136;BA.debugLine="End Sub";
return false;
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 123;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 124;BA.debugLine="DisconnectMqtt";
_disconnectmqtt();
 //BA.debugLineNum = 125;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 117;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 118;BA.debugLine="If Starter.mqttGetBordsActive Then";
if (mostCurrent._starter._mqttgetbordsactive /*boolean*/ ) { 
 //BA.debugLineNum = 119;BA.debugLine="mqttGetBord.Connect";
_mqttgetbord._connect /*String*/ ();
 };
 //BA.debugLineNum = 121;BA.debugLine="End Sub";
return "";
}
public static void  _addunkownip(String _ip,String _name) throws Exception{
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

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
 //BA.debugLineNum = 205;BA.debugLine="Dim bordStatus As bordStatus";
_bordstatus = new nl.pdeg.bordondroid.main._bordstatus();
 //BA.debugLineNum = 206;BA.debugLine="bordStatus.Initialize";
_bordstatus.Initialize();
 //BA.debugLineNum = 207;BA.debugLine="bordStatus.ip = ip";
_bordstatus.ip /*String*/  = _ip;
 //BA.debugLineNum = 208;BA.debugLine="bordStatus.name = name";
_bordstatus.name /*String*/  = _name;
 //BA.debugLineNum = 209;BA.debugLine="bordStatus.timeStamp = DateTime.Now";
_bordstatus.timeStamp /*long*/  = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 210;BA.debugLine="bordStatus.alive = True";
_bordstatus.alive /*boolean*/  = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 211;BA.debugLine="Starter.serverList.Add(bordStatus)";
parent.mostCurrent._starter._serverlist /*anywheresoftware.b4a.objects.collections.List*/ .Add((Object)(_bordstatus));
 //BA.debugLineNum = 212;BA.debugLine="CheckIpInClv(bordStatus)";
_checkipinclv(_bordstatus);
 //BA.debugLineNum = 214;BA.debugLine="If clvServer.GetSize > 0 Then";
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
 //BA.debugLineNum = 215;BA.debugLine="pnlNobords.SetVisibleAnimated(1000, False)";
parent.mostCurrent._pnlnobords.SetVisibleAnimated((int) (1000),anywheresoftware.b4a.keywords.Common.False);
 if (true) break;

case 5:
//C
this.state = 6;
 //BA.debugLineNum = 217;BA.debugLine="pnlNobords.SetVisibleAnimated(1000, True)";
parent.mostCurrent._pnlnobords.SetVisibleAnimated((int) (1000),anywheresoftware.b4a.keywords.Common.True);
 if (true) break;

case 6:
//C
this.state = -1;
;
 //BA.debugLineNum = 219;BA.debugLine="Sleep(1000)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,this,(int) (1000));
this.state = 7;
return;
case 7:
//C
this.state = -1;
;
 //BA.debugLineNum = 221;BA.debugLine="End Sub";
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
 //BA.debugLineNum = 312;BA.debugLine="Msgbox2Async(\"Applicatie afsluiten?\", \"Bord Op Dr";
anywheresoftware.b4a.keywords.Common.Msgbox2Async(BA.ObjectToCharSequence("Applicatie afsluiten?"),BA.ObjectToCharSequence("Bord Op Droid"),"JA","","NEE",(anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper(), (android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null)),processBA,anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 313;BA.debugLine="Wait For Msgbox_Result(Result As Int)";
anywheresoftware.b4a.keywords.Common.WaitFor("msgbox_result", processBA, this, null);
this.state = 5;
return;
case 5:
//C
this.state = 1;
_result = (Integer) result[0];
;
 //BA.debugLineNum = 314;BA.debugLine="If Result = DialogResponse.POSITIVE Then";
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
 //BA.debugLineNum = 315;BA.debugLine="Activity.Finish";
parent.mostCurrent._activity.Finish();
 if (true) break;

case 4:
//C
this.state = -1;
;
 //BA.debugLineNum = 317;BA.debugLine="End Sub";
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

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
 //BA.debugLineNum = 320;BA.debugLine="Starter.testBaseName = True";
parent.mostCurrent._starter._testbasename /*boolean*/  = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 321;BA.debugLine="mqttGetBord.Initialize";
parent._mqttgetbord._initialize /*String*/ (processBA);
 //BA.debugLineNum = 322;BA.debugLine="CallSub2(Starter, \"SetSubBase\", edtLocationCode.T";
anywheresoftware.b4a.keywords.Common.CallSubNew2(processBA,(Object)(parent.mostCurrent._starter.getObject()),"SetSubBase",(Object)(parent.mostCurrent._edtlocationcode.getText()));
 //BA.debugLineNum = 323;BA.debugLine="CallSub(Starter, \"SetSubGetUnits\")";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)(parent.mostCurrent._starter.getObject()),"SetSubGetUnits");
 //BA.debugLineNum = 324;BA.debugLine="mqttGetBord.SetPubBord";
parent._mqttgetbord._setpubbord /*String*/ ();
 //BA.debugLineNum = 325;BA.debugLine="Sleep(100)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,this,(int) (100));
this.state = 7;
return;
case 7:
//C
this.state = 1;
;
 //BA.debugLineNum = 326;BA.debugLine="mqttGetBord.Connect";
parent._mqttgetbord._connect /*String*/ ();
 //BA.debugLineNum = 328;BA.debugLine="Sleep(1000)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,this,(int) (1000));
this.state = 8;
return;
case 8:
//C
this.state = 1;
;
 //BA.debugLineNum = 330;BA.debugLine="If mqttGetBord.connected = False Then";
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
 //BA.debugLineNum = 331;BA.debugLine="Msgbox2Async(\"Kan geen verbinding maken met loca";
anywheresoftware.b4a.keywords.Common.Msgbox2Async(BA.ObjectToCharSequence("Kan geen verbinding maken met locatie"),BA.ObjectToCharSequence("Bord Op Droid"),"OKE","","",(anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper(), (android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null)),processBA,anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 332;BA.debugLine="Wait For Msgbox_Result(Result As Int)";
anywheresoftware.b4a.keywords.Common.WaitFor("msgbox_result", processBA, this, null);
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
 //BA.debugLineNum = 334;BA.debugLine="ToastMessageShow(\"Verbonden met locatie\", True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Verbonden met locatie"),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 335;BA.debugLine="mqttGetBord.Disconnect";
parent._mqttgetbord._disconnect /*String*/ ();
 //BA.debugLineNum = 336;BA.debugLine="pnlLocationCOde.Visible = False";
parent.mostCurrent._pnllocationcode.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 337;BA.debugLine="baseFile.SetBase(edtLocationCode.Text)";
parent._basefile._setbase /*String*/ (parent.mostCurrent._edtlocationcode.getText());
 //BA.debugLineNum = 338;BA.debugLine="StartConnection";
_startconnection();
 if (true) break;

case 6:
//C
this.state = -1;
;
 //BA.debugLineNum = 340;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static String  _checkipexits(String _bord) throws Exception{
boolean _ipfound = false;
String _name = "";
nl.pdeg.bordondroid.main._bordstatus _lst = null;
 //BA.debugLineNum = 160;BA.debugLine="Sub CheckIpExits(bord As String)";
 //BA.debugLineNum = 162;BA.debugLine="Dim ipFound As Boolean = False";
_ipfound = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 163;BA.debugLine="Dim name As String = \"\"";
_name = "";
 //BA.debugLineNum = 165;BA.debugLine="If bord.Length = 0 Then Return";
if (_bord.length()==0) { 
if (true) return "";};
 //BA.debugLineNum = 167;BA.debugLine="name = bord";
_name = _bord;
 //BA.debugLineNum = 169;BA.debugLine="If Starter.serverList.Size = 0 Then";
if (mostCurrent._starter._serverlist /*anywheresoftware.b4a.objects.collections.List*/ .getSize()==0) { 
 //BA.debugLineNum = 170;BA.debugLine="AddUnkownIp(\"\", name)";
_addunkownip("",_name);
 //BA.debugLineNum = 171;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 174;BA.debugLine="For Each lst As bordStatus In Starter.serverList";
{
final anywheresoftware.b4a.BA.IterableList group9 = mostCurrent._starter._serverlist /*anywheresoftware.b4a.objects.collections.List*/ ;
final int groupLen9 = group9.getSize()
;int index9 = 0;
;
for (; index9 < groupLen9;index9++){
_lst = (nl.pdeg.bordondroid.main._bordstatus)(group9.Get(index9));
 //BA.debugLineNum = 175;BA.debugLine="If lst.name = name Then";
if ((_lst.name /*String*/ ).equals(_name)) { 
 //BA.debugLineNum = 176;BA.debugLine="ipFound = True";
_ipfound = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 177;BA.debugLine="lst.timeStamp = DateTime.Now";
_lst.timeStamp /*long*/  = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 178;BA.debugLine="lst.alive = True";
_lst.alive /*boolean*/  = anywheresoftware.b4a.keywords.Common.True;
 };
 }
};
 //BA.debugLineNum = 182;BA.debugLine="ServerAlive";
_serveralive();
 //BA.debugLineNum = 184;BA.debugLine="If Not(ipFound) Then";
if (anywheresoftware.b4a.keywords.Common.Not(_ipfound)) { 
 //BA.debugLineNum = 185;BA.debugLine="AddUnkownIp(\"\", name)";
_addunkownip("",_name);
 //BA.debugLineNum = 186;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 188;BA.debugLine="End Sub";
return "";
}
public static String  _checkipinclv(nl.pdeg.bordondroid.main._bordstatus _bord) throws Exception{
anywheresoftware.b4a.objects.PanelWrapper _p = null;
anywheresoftware.b4a.objects.LabelWrapper _lbl = null;
int _i = 0;
anywheresoftware.b4a.objects.ConcreteViewWrapper _v = null;
 //BA.debugLineNum = 223;BA.debugLine="Sub CheckIpInClv(bord As bordStatus)";
 //BA.debugLineNum = 224;BA.debugLine="Dim p As Panel";
_p = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 225;BA.debugLine="Dim lbl As Label";
_lbl = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 227;BA.debugLine="For i = 0 To clvServer.GetSize-1";
{
final int step3 = 1;
final int limit3 = (int) (mostCurrent._clvserver._getsize()-1);
_i = (int) (0) ;
for (;_i <= limit3 ;_i = _i + step3 ) {
 //BA.debugLineNum = 228;BA.debugLine="p = clvServer.GetPanel(i)";
_p.setObject((android.view.ViewGroup)(mostCurrent._clvserver._getpanel(_i).getObject()));
 //BA.debugLineNum = 229;BA.debugLine="If p.Tag = bord.name Then";
if ((_p.getTag()).equals((Object)(_bord.name /*String*/ ))) { 
 //BA.debugLineNum = 230;BA.debugLine="For Each v As View In p.GetAllViewsRecursive";
_v = new anywheresoftware.b4a.objects.ConcreteViewWrapper();
{
final anywheresoftware.b4a.BA.IterableList group6 = _p.GetAllViewsRecursive();
final int groupLen6 = group6.getSize()
;int index6 = 0;
;
for (; index6 < groupLen6;index6++){
_v.setObject((android.view.View)(group6.Get(index6)));
 //BA.debugLineNum = 231;BA.debugLine="If v.Tag = \"lblLastCheck\" Then";
if ((_v.getTag()).equals((Object)("lblLastCheck"))) { 
 //BA.debugLineNum = 232;BA.debugLine="lbl = v";
_lbl.setObject((android.widget.TextView)(_v.getObject()));
 //BA.debugLineNum = 233;BA.debugLine="lbl.Text = $\"Laatste controle $Time{DateTime.";
_lbl.setText(BA.ObjectToCharSequence(("Laatste controle "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("time",(Object)(anywheresoftware.b4a.keywords.Common.DateTime.getNow()))+"")));
 };
 }
};
 //BA.debugLineNum = 237;BA.debugLine="For Each v As View In p.GetAllViewsRecursive";
_v = new anywheresoftware.b4a.objects.ConcreteViewWrapper();
{
final anywheresoftware.b4a.BA.IterableList group12 = _p.GetAllViewsRecursive();
final int groupLen12 = group12.getSize()
;int index12 = 0;
;
for (; index12 < groupLen12;index12++){
_v.setObject((android.view.View)(group12.Get(index12)));
 //BA.debugLineNum = 238;BA.debugLine="If v.Tag = \"viewbord\" Then";
if ((_v.getTag()).equals((Object)("viewbord"))) { 
 //BA.debugLineNum = 239;BA.debugLine="lbl = v";
_lbl.setObject((android.widget.TextView)(_v.getObject()));
 //BA.debugLineNum = 240;BA.debugLine="If bord.alive = False Then";
if (_bord.alive /*boolean*/ ==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 241;BA.debugLine="lbl.Enabled = False";
_lbl.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 242;BA.debugLine="lbl.TextColor = Colors.Red";
_lbl.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 //BA.debugLineNum = 243;BA.debugLine="Starter.diedIndex = i";
mostCurrent._starter._diedindex /*int*/  = _i;
 }else {
 //BA.debugLineNum = 245;BA.debugLine="lbl.Enabled = True";
_lbl.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 246;BA.debugLine="lbl.TextColor = 0xFF027F00";
_lbl.setTextColor((int) (0xff027f00));
 };
 //BA.debugLineNum = 248;BA.debugLine="Return";
if (true) return "";
 };
 }
};
 };
 }
};
 //BA.debugLineNum = 255;BA.debugLine="pnlNobords.SetVisibleAnimated(1000, False)";
mostCurrent._pnlnobords.SetVisibleAnimated((int) (1000),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 256;BA.debugLine="clvServer.Add(genUnitList(bord.name, bord.alive),";
mostCurrent._clvserver._add((anywheresoftware.b4a.objects.B4XViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.B4XViewWrapper(), (java.lang.Object)(_genunitlist(_bord.name /*String*/ ,_bord.alive /*boolean*/ ).getObject())),(Object)(""));
 //BA.debugLineNum = 257;BA.debugLine="End Sub";
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
anywheresoftware.b4a.objects.LabelWrapper _lbl = null;
anywheresoftware.b4a.objects.ConcreteViewWrapper _c = null;
anywheresoftware.b4a.BA.IterableList group3;
int index3;
int groupLen3;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
 //BA.debugLineNum = 287;BA.debugLine="Dim p As Panel = clvServer.GetPanel(Index)";
_p = new anywheresoftware.b4a.objects.PanelWrapper();
_p.setObject((android.view.ViewGroup)(parent.mostCurrent._clvserver._getpanel(_index).getObject()));
 //BA.debugLineNum = 288;BA.debugLine="Dim lbl As Label";
_lbl = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 291;BA.debugLine="For Each c As View In p.GetAllViewsRecursive";
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
 //BA.debugLineNum = 292;BA.debugLine="If c.Tag = \"name\" Then";
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
 //BA.debugLineNum = 293;BA.debugLine="lbl = c";
_lbl.setObject((android.widget.TextView)(_c.getObject()));
 //BA.debugLineNum = 296;BA.debugLine="CallSub2(Starter, \"SetUnit\", PrepTopicName(lbl.";
anywheresoftware.b4a.keywords.Common.CallSubNew2(processBA,(Object)(parent.mostCurrent._starter.getObject()),"SetUnit",(Object)(_preptopicname(_lbl.getText()).toLowerCase()));
 //BA.debugLineNum = 297;BA.debugLine="Starter.DiscoveredServer = lbl.Text";
parent.mostCurrent._starter._discoveredserver /*String*/  = _lbl.getText();
 //BA.debugLineNum = 298;BA.debugLine="Exit";
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
 //BA.debugLineNum = 302;BA.debugLine="DisconnectMqtt";
_disconnectmqtt();
 //BA.debugLineNum = 303;BA.debugLine="Sleep(100)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,this,(int) (100));
this.state = 11;
return;
case 11:
//C
this.state = -1;
;
 //BA.debugLineNum = 305;BA.debugLine="StartActivity(ServerBoard)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(parent.mostCurrent._serverboard.getObject()));
 //BA.debugLineNum = 306;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static String  _disconnectmqtt() throws Exception{
 //BA.debugLineNum = 138;BA.debugLine="Sub DisconnectMqtt";
 //BA.debugLineNum = 139;BA.debugLine="If mqttGetBord.Connected Then";
if (_mqttgetbord._connected /*boolean*/ ) { 
 //BA.debugLineNum = 140;BA.debugLine="mqttGetBord.Disconnect";
_mqttgetbord._disconnect /*String*/ ();
 };
 //BA.debugLineNum = 143;BA.debugLine="End Sub";
return "";
}
public static String  _edtlocationcode_textchanged(String _old,String _new) throws Exception{
 //BA.debugLineNum = 342;BA.debugLine="Sub edtLocationCode_TextChanged (Old As String, Ne";
 //BA.debugLineNum = 343;BA.debugLine="If New.Length > 0 Then";
if (_new.length()>0) { 
 //BA.debugLineNum = 344;BA.debugLine="btnOk.Enabled = True";
mostCurrent._btnok.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 346;BA.debugLine="btnOk.Enabled = False";
mostCurrent._btnok.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 348;BA.debugLine="End Sub";
return "";
}
public static anywheresoftware.b4a.objects.PanelWrapper  _genunitlist(String _name,boolean _alive) throws Exception{
anywheresoftware.b4a.objects.PanelWrapper _p = null;
 //BA.debugLineNum = 145;BA.debugLine="Sub genUnitList(name As String, alive As Boolean)";
 //BA.debugLineNum = 146;BA.debugLine="Dim p As Panel";
_p = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 147;BA.debugLine="p.Initialize(Me)";
_p.Initialize(mostCurrent.activityBA,BA.ObjectToString(main.getObject()));
 //BA.debugLineNum = 148;BA.debugLine="p.SetLayout(0dip, 0dip, clvServer.AsView.Width, 1";
_p.SetLayout(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)),mostCurrent._clvserver._asview().getWidth(),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (120)));
 //BA.debugLineNum = 149;BA.debugLine="p.LoadLayout(\"clvBorden\")";
_p.LoadLayout("clvBorden",mostCurrent.activityBA);
 //BA.debugLineNum = 150;BA.debugLine="p.Tag = name";
_p.setTag((Object)(_name));
 //BA.debugLineNum = 152;BA.debugLine="lblBordName.Text = name.Trim";
mostCurrent._lblbordname.setText(BA.ObjectToCharSequence(_name.trim()));
 //BA.debugLineNum = 153;BA.debugLine="lblLastCheck.Text = $\"Laatste controle $Time{Date";
mostCurrent._lbllastcheck.setText(BA.ObjectToCharSequence(("Laatste controle "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("time",(Object)(anywheresoftware.b4a.keywords.Common.DateTime.getNow()))+"")));
 //BA.debugLineNum = 154;BA.debugLine="If Not(alive) Then";
if (anywheresoftware.b4a.keywords.Common.Not(_alive)) { 
 //BA.debugLineNum = 155;BA.debugLine="lblViewBord.Enabled = False";
mostCurrent._lblviewbord.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 157;BA.debugLine="Return p";
if (true) return _p;
 //BA.debugLineNum = 158;BA.debugLine="End Sub";
return null;
}
public static String  _getbaselist() throws Exception{
nl.pdeg.bordondroid.main._locationbord _loc = null;
 //BA.debugLineNum = 77;BA.debugLine="Private Sub getBaseList";
 //BA.debugLineNum = 78;BA.debugLine="baseList.Initialize";
_baselist.Initialize();
 //BA.debugLineNum = 79;BA.debugLine="baseList = baseFile.GetBase";
_baselist = _basefile._getbase /*anywheresoftware.b4a.objects.collections.List*/ ();
 //BA.debugLineNum = 81;BA.debugLine="If baseList.IsInitialized = False Then";
if (_baselist.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 82;BA.debugLine="pnlLocationCOde.Visible = True";
mostCurrent._pnllocationcode.setVisible(anywheresoftware.b4a.keywords.Common.True);
 }else if(_baselist.getSize()==1) { 
 //BA.debugLineNum = 84;BA.debugLine="Dim loc As locationBord";
_loc = new nl.pdeg.bordondroid.main._locationbord();
 //BA.debugLineNum = 85;BA.debugLine="loc.Initialize";
_loc.Initialize();
 //BA.debugLineNum = 86;BA.debugLine="loc = baseList.Get(0)";
_loc = (nl.pdeg.bordondroid.main._locationbord)(_baselist.Get((int) (0)));
 //BA.debugLineNum = 87;BA.debugLine="If loc.isdefault = \"1\" Then";
if ((_loc.isdefault /*String*/ ).equals("1")) { 
 //BA.debugLineNum = 88;BA.debugLine="InitConnection(loc.code, loc.description)";
_initconnection(_loc.code /*String*/ ,_loc.description /*String*/ );
 };
 }else {
 //BA.debugLineNum = 91;BA.debugLine="For Each loc As locationBord In baseList";
{
final anywheresoftware.b4a.BA.IterableList group13 = _baselist;
final int groupLen13 = group13.getSize()
;int index13 = 0;
;
for (; index13 < groupLen13;index13++){
_loc = (nl.pdeg.bordondroid.main._locationbord)(group13.Get(index13));
 //BA.debugLineNum = 92;BA.debugLine="If loc.isdefault = \"1\" Then";
if ((_loc.isdefault /*String*/ ).equals("1")) { 
 //BA.debugLineNum = 93;BA.debugLine="InitConnection(loc.code, loc.description)";
_initconnection(_loc.code /*String*/ ,_loc.description /*String*/ );
 //BA.debugLineNum = 94;BA.debugLine="Exit";
if (true) break;
 };
 }
};
 };
 //BA.debugLineNum = 98;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 29;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 31;BA.debugLine="Private lblBordName As Label";
mostCurrent._lblbordname = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 32;BA.debugLine="Private lblViewBord As Label";
mostCurrent._lblviewbord = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 33;BA.debugLine="Private pnlBord As Panel";
mostCurrent._pnlbord = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 34;BA.debugLine="Private lblLastCheck As Label";
mostCurrent._lbllastcheck = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 35;BA.debugLine="Private pnlNobords As Panel";
mostCurrent._pnlnobords = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 37;BA.debugLine="Private lblVersion As Label";
mostCurrent._lblversion = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 38;BA.debugLine="Private pnlLocationCOde As Panel";
mostCurrent._pnllocationcode = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 39;BA.debugLine="Private edtLocationCode As EditText";
mostCurrent._edtlocationcode = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 40;BA.debugLine="Private btnCancel As Button";
mostCurrent._btncancel = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 41;BA.debugLine="Private btnOk As Button";
mostCurrent._btnok = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 42;BA.debugLine="Private pnlLocation As Panel";
mostCurrent._pnllocation = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 44;BA.debugLine="Private clvServer As CustomListView";
mostCurrent._clvserver = new b4a.example3.customlistview();
 //BA.debugLineNum = 45;BA.debugLine="Private lblCurrLocation As Label";
mostCurrent._lblcurrlocation = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 46;BA.debugLine="End Sub";
return "";
}
public static String  _initconnection(String _locationcode,String _location) throws Exception{
 //BA.debugLineNum = 100;BA.debugLine="Private Sub InitConnection(locationCode As String,";
 //BA.debugLineNum = 101;BA.debugLine="lblCurrLocation.Text = location";
mostCurrent._lblcurrlocation.setText(BA.ObjectToCharSequence(_location));
 //BA.debugLineNum = 102;BA.debugLine="CallSub2(Starter, \"SetSubBase\", locationCode)";
anywheresoftware.b4a.keywords.Common.CallSubNew2(processBA,(Object)(mostCurrent._starter.getObject()),"SetSubBase",(Object)(_locationcode));
 //BA.debugLineNum = 103;BA.debugLine="CallSub(Starter, \"SetSubGetUnits\")";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)(mostCurrent._starter.getObject()),"SetSubGetUnits");
 //BA.debugLineNum = 104;BA.debugLine="StartConnection";
_startconnection();
 //BA.debugLineNum = 105;BA.debugLine="End Sub";
return "";
}
public static void  _lblviewbord_click() throws Exception{
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

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
 //BA.debugLineNum = 260;BA.debugLine="Dim v As View = Sender";
_v = new anywheresoftware.b4a.objects.ConcreteViewWrapper();
_v.setObject((android.view.View)(anywheresoftware.b4a.keywords.Common.Sender(mostCurrent.activityBA)));
 //BA.debugLineNum = 261;BA.debugLine="Dim p As Panel = v.Parent";
_p = new anywheresoftware.b4a.objects.PanelWrapper();
_p.setObject((android.view.ViewGroup)(_v.getParent()));
 //BA.debugLineNum = 262;BA.debugLine="Dim lbl As Label";
_lbl = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 265;BA.debugLine="For Each c As View In p.GetAllViewsRecursive";
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
 //BA.debugLineNum = 266;BA.debugLine="If c.Tag = \"name\" Then";
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
 //BA.debugLineNum = 267;BA.debugLine="lbl = c";
_lbl.setObject((android.widget.TextView)(_c.getObject()));
 //BA.debugLineNum = 268;BA.debugLine="Starter.selectedBordName = PrepTopicName(lbl.Te";
parent.mostCurrent._starter._selectedbordname /*String*/  = _preptopicname(_lbl.getText());
 //BA.debugLineNum = 269;BA.debugLine="Starter.DiscoveredServer = lbl.Text";
parent.mostCurrent._starter._discoveredserver /*String*/  = _lbl.getText();
 //BA.debugLineNum = 270;BA.debugLine="Exit";
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
 //BA.debugLineNum = 274;BA.debugLine="DisconnectMqtt";
_disconnectmqtt();
 //BA.debugLineNum = 275;BA.debugLine="Sleep(100)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,this,(int) (100));
this.state = 11;
return;
case 11:
//C
this.state = -1;
;
 //BA.debugLineNum = 277;BA.debugLine="StartActivity(ServerBoard)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(parent.mostCurrent._serverboard.getObject()));
 //BA.debugLineNum = 278;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static String  _pnllocation_click() throws Exception{
 //BA.debugLineNum = 350;BA.debugLine="Sub pnlLocation_Click";
 //BA.debugLineNum = 351;BA.debugLine="StartActivity(locations)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(mostCurrent._locations.getObject()));
 //BA.debugLineNum = 352;BA.debugLine="End Sub";
return "";
}
public static String  _preptopicname(String _bord) throws Exception{
 //BA.debugLineNum = 280;BA.debugLine="Private Sub PrepTopicName(bord As String) As Strin";
 //BA.debugLineNum = 282;BA.debugLine="Return bord.Replace(\" \", \"\")";
if (true) return _bord.replace(" ","");
 //BA.debugLineNum = 283;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        b4a.example.dateutils._process_globals();
main._process_globals();
locations._process_globals();
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
 //BA.debugLineNum = 22;BA.debugLine="Private mqttGetBord As mqttGetBords";
_mqttgetbord = new nl.pdeg.bordondroid.mqttgetbords();
 //BA.debugLineNum = 23;BA.debugLine="Private mqttGetData As mqttGetBordData";
_mqttgetdata = new nl.pdeg.bordondroid.mqttgetborddata();
 //BA.debugLineNum = 24;BA.debugLine="Private tmrBordLastAlive As Timer";
_tmrbordlastalive = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 25;BA.debugLine="Private baseFile As Base";
_basefile = new nl.pdeg.bordondroid.base();
 //BA.debugLineNum = 26;BA.debugLine="Private baseList As List";
_baselist = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 27;BA.debugLine="End Sub";
return "";
}
public static String  _serveralive() throws Exception{
long _msnow = 0L;
nl.pdeg.bordondroid.main._bordstatus _lst = null;
 //BA.debugLineNum = 190;BA.debugLine="Sub ServerAlive";
 //BA.debugLineNum = 192;BA.debugLine="Dim msNow As Long = DateTime.Now";
_msnow = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 193;BA.debugLine="For Each lst As bordStatus In Starter.serverList";
{
final anywheresoftware.b4a.BA.IterableList group2 = mostCurrent._starter._serverlist /*anywheresoftware.b4a.objects.collections.List*/ ;
final int groupLen2 = group2.getSize()
;int index2 = 0;
;
for (; index2 < groupLen2;index2++){
_lst = (nl.pdeg.bordondroid.main._bordstatus)(group2.Get(index2));
 //BA.debugLineNum = 194;BA.debugLine="If(msNow - lst.timeStamp) > Starter.serverDied A";
if ((_msnow-_lst.timeStamp /*long*/ )>mostCurrent._starter._serverdied /*long*/  && _lst.alive /*boolean*/ ==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 195;BA.debugLine="lst.alive = False";
_lst.alive /*boolean*/  = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 196;BA.debugLine="CheckIpInClv(lst)";
_checkipinclv(_lst);
 //BA.debugLineNum = 197;BA.debugLine="Return";
if (true) return "";
 }else {
 //BA.debugLineNum = 199;BA.debugLine="CheckIpInClv(lst)";
_checkipinclv(_lst);
 };
 }
};
 //BA.debugLineNum = 202;BA.debugLine="End Sub";
return "";
}
public static String  _startconnection() throws Exception{
 //BA.debugLineNum = 61;BA.debugLine="Private Sub StartConnection";
 //BA.debugLineNum = 62;BA.debugLine="pnlLocationCOde.Visible = False";
mostCurrent._pnllocationcode.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 63;BA.debugLine="pnlNobords.Visible = True";
mostCurrent._pnlnobords.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 64;BA.debugLine="clvServer.Clear";
mostCurrent._clvserver._clear();
 //BA.debugLineNum = 65;BA.debugLine="mqttGetData.Initialize";
_mqttgetdata._initialize /*String*/ (processBA);
 //BA.debugLineNum = 66;BA.debugLine="mqttGetBord.Initialize";
_mqttgetbord._initialize /*String*/ (processBA);
 //BA.debugLineNum = 67;BA.debugLine="mqttGetBord.SetPubBord";
_mqttgetbord._setpubbord /*String*/ ();
 //BA.debugLineNum = 68;BA.debugLine="mqttGetBord.Connect";
_mqttgetbord._connect /*String*/ ();
 //BA.debugLineNum = 70;BA.debugLine="baseFile.GetBase";
_basefile._getbase /*anywheresoftware.b4a.objects.collections.List*/ ();
 //BA.debugLineNum = 72;BA.debugLine="tmrBordLastAlive.Initialize(\"tmrBordAlive\", 10000";
_tmrbordlastalive.Initialize(processBA,"tmrBordAlive",(long) (10000));
 //BA.debugLineNum = 73;BA.debugLine="tmrBordLastAlive.Enabled = True";
_tmrbordlastalive.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 75;BA.debugLine="End Sub";
return "";
}
public static String  _tmrbordalive_tick() throws Exception{
 //BA.debugLineNum = 108;BA.debugLine="Sub tmrBordAlive_Tick";
 //BA.debugLineNum = 109;BA.debugLine="If Starter.diedIndex > -1 Then";
if (mostCurrent._starter._diedindex /*int*/ >-1) { 
 //BA.debugLineNum = 110;BA.debugLine="clvServer.RemoveAt(Starter.diedIndex)";
mostCurrent._clvserver._removeat(mostCurrent._starter._diedindex /*int*/ );
 //BA.debugLineNum = 111;BA.debugLine="Starter.serverList.RemoveAt(Starter.diedIndex)";
mostCurrent._starter._serverlist /*anywheresoftware.b4a.objects.collections.List*/ .RemoveAt(mostCurrent._starter._diedindex /*int*/ );
 //BA.debugLineNum = 112;BA.debugLine="Starter.diedIndex = -1";
mostCurrent._starter._diedindex /*int*/  = (int) (-1);
 };
 //BA.debugLineNum = 114;BA.debugLine="ServerAlive";
_serveralive();
 //BA.debugLineNum = 115;BA.debugLine="End Sub";
return "";
}
}
