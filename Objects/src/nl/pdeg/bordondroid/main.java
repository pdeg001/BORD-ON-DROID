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
public anywheresoftware.b4a.samples.customlistview.customlistview _clvserver = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblbordname = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblviewbord = null;
public anywheresoftware.b4a.objects.PanelWrapper _pnlbord = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbllastcheck = null;
public anywheresoftware.b4a.objects.PanelWrapper _pnlnobords = null;
public nl.pdeg.bordondroid.starter _starter = null;
public nl.pdeg.bordondroid.serverboard _serverboard = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
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
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 36;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 37;BA.debugLine="Activity.LoadLayout(\"main\")";
mostCurrent._activity.LoadLayout("main",mostCurrent.activityBA);
 //BA.debugLineNum = 38;BA.debugLine="clvServer.Clear";
mostCurrent._clvserver._clear();
 //BA.debugLineNum = 39;BA.debugLine="mqttGetData.Initialize";
_mqttgetdata._initialize /*String*/ (processBA);
 //BA.debugLineNum = 40;BA.debugLine="mqttGetBord.Initialize";
_mqttgetbord._initialize /*String*/ (processBA);
 //BA.debugLineNum = 41;BA.debugLine="mqttGetBord.Connect";
_mqttgetbord._connect /*String*/ ();
 //BA.debugLineNum = 43;BA.debugLine="tmrBordLastAlive.Initialize(\"tmrBordAlive\", 10000";
_tmrbordlastalive.Initialize(processBA,"tmrBordAlive",(long) (10000));
 //BA.debugLineNum = 44;BA.debugLine="tmrBordLastAlive.Enabled = True";
_tmrbordlastalive.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 45;BA.debugLine="End Sub";
return "";
}
public static boolean  _activity_keypress(int _keycode) throws Exception{
 //BA.debugLineNum = 61;BA.debugLine="Private Sub Activity_KeyPress(KeyCode As Int) As B";
 //BA.debugLineNum = 62;BA.debugLine="If KeyCode = KeyCodes.KEYCODE_BACK Then";
if (_keycode==anywheresoftware.b4a.keywords.Common.KeyCodes.KEYCODE_BACK) { 
 //BA.debugLineNum = 63;BA.debugLine="tmrBordLastAlive.Enabled = False";
_tmrbordlastalive.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 64;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 66;BA.debugLine="Return False";
if (true) return anywheresoftware.b4a.keywords.Common.False;
 }else {
 //BA.debugLineNum = 68;BA.debugLine="Return False";
if (true) return anywheresoftware.b4a.keywords.Common.False;
 };
 //BA.debugLineNum = 70;BA.debugLine="End Sub";
return false;
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 57;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 58;BA.debugLine="DisconnectMqtt";
_disconnectmqtt();
 //BA.debugLineNum = 59;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 51;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 52;BA.debugLine="If Starter.mqttGetBordsActive Then";
if (mostCurrent._starter._mqttgetbordsactive /*boolean*/ ) { 
 //BA.debugLineNum = 53;BA.debugLine="mqttGetBord.Connect";
_mqttgetbord._connect /*String*/ ();
 };
 //BA.debugLineNum = 55;BA.debugLine="End Sub";
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
 //BA.debugLineNum = 139;BA.debugLine="Dim bordStatus As bordStatus";
_bordstatus = new nl.pdeg.bordondroid.main._bordstatus();
 //BA.debugLineNum = 140;BA.debugLine="bordStatus.Initialize";
_bordstatus.Initialize();
 //BA.debugLineNum = 141;BA.debugLine="bordStatus.ip = ip";
_bordstatus.ip /*String*/  = _ip;
 //BA.debugLineNum = 142;BA.debugLine="bordStatus.name = name";
_bordstatus.name /*String*/  = _name;
 //BA.debugLineNum = 143;BA.debugLine="bordStatus.timeStamp = DateTime.Now";
_bordstatus.timeStamp /*long*/  = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 144;BA.debugLine="bordStatus.alive = True";
_bordstatus.alive /*boolean*/  = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 145;BA.debugLine="Starter.serverList.Add(bordStatus)";
parent.mostCurrent._starter._serverlist /*anywheresoftware.b4a.objects.collections.List*/ .Add((Object)(_bordstatus));
 //BA.debugLineNum = 146;BA.debugLine="CheckIpInClv(bordStatus)";
_checkipinclv(_bordstatus);
 //BA.debugLineNum = 148;BA.debugLine="If clvServer.GetSize > 0 Then";
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
 //BA.debugLineNum = 149;BA.debugLine="pnlNobords.SetVisibleAnimated(1000, False)";
parent.mostCurrent._pnlnobords.SetVisibleAnimated((int) (1000),anywheresoftware.b4a.keywords.Common.False);
 if (true) break;

case 5:
//C
this.state = 6;
 //BA.debugLineNum = 151;BA.debugLine="pnlNobords.SetVisibleAnimated(1000, True)";
parent.mostCurrent._pnlnobords.SetVisibleAnimated((int) (1000),anywheresoftware.b4a.keywords.Common.True);
 if (true) break;

case 6:
//C
this.state = -1;
;
 //BA.debugLineNum = 153;BA.debugLine="Sleep(1000)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,this,(int) (1000));
this.state = 7;
return;
case 7:
//C
this.state = -1;
;
 //BA.debugLineNum = 155;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static String  _checkipexits(String _bord) throws Exception{
boolean _ipfound = false;
String _name = "";
nl.pdeg.bordondroid.main._bordstatus _lst = null;
 //BA.debugLineNum = 94;BA.debugLine="Sub CheckIpExits(bord As String)";
 //BA.debugLineNum = 96;BA.debugLine="Dim ipFound As Boolean = False";
_ipfound = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 97;BA.debugLine="Dim name As String = \"\"";
_name = "";
 //BA.debugLineNum = 99;BA.debugLine="If bord.Length = 0 Then Return";
if (_bord.length()==0) { 
if (true) return "";};
 //BA.debugLineNum = 101;BA.debugLine="name = bord";
_name = _bord;
 //BA.debugLineNum = 103;BA.debugLine="If Starter.serverList.Size = 0 Then";
if (mostCurrent._starter._serverlist /*anywheresoftware.b4a.objects.collections.List*/ .getSize()==0) { 
 //BA.debugLineNum = 104;BA.debugLine="AddUnkownIp(\"\", name)";
_addunkownip("",_name);
 //BA.debugLineNum = 105;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 108;BA.debugLine="For Each lst As bordStatus In Starter.serverList";
{
final anywheresoftware.b4a.BA.IterableList group9 = mostCurrent._starter._serverlist /*anywheresoftware.b4a.objects.collections.List*/ ;
final int groupLen9 = group9.getSize()
;int index9 = 0;
;
for (; index9 < groupLen9;index9++){
_lst = (nl.pdeg.bordondroid.main._bordstatus)(group9.Get(index9));
 //BA.debugLineNum = 109;BA.debugLine="If lst.name = name Then";
if ((_lst.name /*String*/ ).equals(_name)) { 
 //BA.debugLineNum = 110;BA.debugLine="ipFound = True";
_ipfound = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 111;BA.debugLine="lst.timeStamp = DateTime.Now";
_lst.timeStamp /*long*/  = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 112;BA.debugLine="lst.alive = True";
_lst.alive /*boolean*/  = anywheresoftware.b4a.keywords.Common.True;
 };
 }
};
 //BA.debugLineNum = 116;BA.debugLine="ServerAlive";
_serveralive();
 //BA.debugLineNum = 118;BA.debugLine="If Not(ipFound) Then";
if (anywheresoftware.b4a.keywords.Common.Not(_ipfound)) { 
 //BA.debugLineNum = 119;BA.debugLine="AddUnkownIp(\"\", name)";
_addunkownip("",_name);
 //BA.debugLineNum = 120;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 122;BA.debugLine="End Sub";
return "";
}
public static String  _checkipinclv(nl.pdeg.bordondroid.main._bordstatus _bord) throws Exception{
anywheresoftware.b4a.objects.PanelWrapper _p = null;
anywheresoftware.b4a.objects.LabelWrapper _lbl = null;
int _i = 0;
anywheresoftware.b4a.objects.ConcreteViewWrapper _v = null;
 //BA.debugLineNum = 157;BA.debugLine="Sub CheckIpInClv(bord As bordStatus)";
 //BA.debugLineNum = 158;BA.debugLine="Dim p As Panel";
_p = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 159;BA.debugLine="Dim lbl As Label";
_lbl = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 161;BA.debugLine="For i = 0 To clvServer.GetSize-1";
{
final int step3 = 1;
final int limit3 = (int) (mostCurrent._clvserver._getsize()-1);
_i = (int) (0) ;
for (;_i <= limit3 ;_i = _i + step3 ) {
 //BA.debugLineNum = 162;BA.debugLine="p = clvServer.GetPanel(i)";
_p = mostCurrent._clvserver._getpanel(_i);
 //BA.debugLineNum = 163;BA.debugLine="If p.Tag = bord.name Then";
if ((_p.getTag()).equals((Object)(_bord.name /*String*/ ))) { 
 //BA.debugLineNum = 164;BA.debugLine="For Each v As View In p.GetAllViewsRecursive";
_v = new anywheresoftware.b4a.objects.ConcreteViewWrapper();
{
final anywheresoftware.b4a.BA.IterableList group6 = _p.GetAllViewsRecursive();
final int groupLen6 = group6.getSize()
;int index6 = 0;
;
for (; index6 < groupLen6;index6++){
_v.setObject((android.view.View)(group6.Get(index6)));
 //BA.debugLineNum = 165;BA.debugLine="If v.Tag = \"lblLastCheck\" Then";
if ((_v.getTag()).equals((Object)("lblLastCheck"))) { 
 //BA.debugLineNum = 166;BA.debugLine="lbl = v";
_lbl.setObject((android.widget.TextView)(_v.getObject()));
 //BA.debugLineNum = 167;BA.debugLine="lbl.Text = $\"Laatste controle $Time{DateTime.";
_lbl.setText(BA.ObjectToCharSequence(("Laatste controle "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("time",(Object)(anywheresoftware.b4a.keywords.Common.DateTime.getNow()))+"")));
 };
 }
};
 //BA.debugLineNum = 171;BA.debugLine="For Each v As View In p.GetAllViewsRecursive";
_v = new anywheresoftware.b4a.objects.ConcreteViewWrapper();
{
final anywheresoftware.b4a.BA.IterableList group12 = _p.GetAllViewsRecursive();
final int groupLen12 = group12.getSize()
;int index12 = 0;
;
for (; index12 < groupLen12;index12++){
_v.setObject((android.view.View)(group12.Get(index12)));
 //BA.debugLineNum = 172;BA.debugLine="If v.Tag = \"viewbord\" Then";
if ((_v.getTag()).equals((Object)("viewbord"))) { 
 //BA.debugLineNum = 173;BA.debugLine="lbl = v";
_lbl.setObject((android.widget.TextView)(_v.getObject()));
 //BA.debugLineNum = 174;BA.debugLine="If bord.alive = False Then";
if (_bord.alive /*boolean*/ ==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 175;BA.debugLine="lbl.Enabled = False";
_lbl.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 176;BA.debugLine="lbl.TextColor = Colors.Red";
_lbl.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 }else {
 //BA.debugLineNum = 178;BA.debugLine="lbl.Enabled = True";
_lbl.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 179;BA.debugLine="lbl.TextColor = 0xFF027F00";
_lbl.setTextColor((int) (0xff027f00));
 };
 //BA.debugLineNum = 181;BA.debugLine="Return";
if (true) return "";
 };
 }
};
 };
 }
};
 //BA.debugLineNum = 188;BA.debugLine="pnlNobords.SetVisibleAnimated(1000, False)";
mostCurrent._pnlnobords.SetVisibleAnimated((int) (1000),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 189;BA.debugLine="clvServer.Add(genUnitList(bord.name, bord.alive),";
mostCurrent._clvserver._add(_genunitlist(_bord.name /*String*/ ,_bord.alive /*boolean*/ ),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (130)),(Object)(""));
 //BA.debugLineNum = 190;BA.debugLine="End Sub";
return "";
}
public static String  _disconnectmqtt() throws Exception{
 //BA.debugLineNum = 72;BA.debugLine="Sub DisconnectMqtt";
 //BA.debugLineNum = 73;BA.debugLine="If mqttGetBord.Connected Then";
if (_mqttgetbord._connected /*boolean*/ ) { 
 //BA.debugLineNum = 74;BA.debugLine="mqttGetBord.Disconnect";
_mqttgetbord._disconnect /*String*/ ();
 };
 //BA.debugLineNum = 77;BA.debugLine="End Sub";
return "";
}
public static anywheresoftware.b4a.objects.PanelWrapper  _genunitlist(String _name,boolean _alive) throws Exception{
anywheresoftware.b4a.objects.PanelWrapper _p = null;
 //BA.debugLineNum = 79;BA.debugLine="Sub genUnitList(name As String, alive As Boolean)";
 //BA.debugLineNum = 80;BA.debugLine="Dim p As Panel";
_p = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 81;BA.debugLine="p.Initialize(Me)";
_p.Initialize(mostCurrent.activityBA,BA.ObjectToString(main.getObject()));
 //BA.debugLineNum = 82;BA.debugLine="p.SetLayout(0dip, 0dip, clvServer.AsView.Width, 8";
_p.SetLayout(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)),mostCurrent._clvserver._asview().getWidth(),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (85)));
 //BA.debugLineNum = 83;BA.debugLine="p.LoadLayout(\"clvBorden\")";
_p.LoadLayout("clvBorden",mostCurrent.activityBA);
 //BA.debugLineNum = 84;BA.debugLine="p.Tag = name";
_p.setTag((Object)(_name));
 //BA.debugLineNum = 86;BA.debugLine="lblBordName.Text = name.Trim";
mostCurrent._lblbordname.setText(BA.ObjectToCharSequence(_name.trim()));
 //BA.debugLineNum = 87;BA.debugLine="lblLastCheck.Text = $\"Laatste controle $Time{Date";
mostCurrent._lbllastcheck.setText(BA.ObjectToCharSequence(("Laatste controle "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("time",(Object)(anywheresoftware.b4a.keywords.Common.DateTime.getNow()))+"")));
 //BA.debugLineNum = 88;BA.debugLine="If Not(alive) Then";
if (anywheresoftware.b4a.keywords.Common.Not(_alive)) { 
 //BA.debugLineNum = 89;BA.debugLine="lblViewBord.Enabled = False";
mostCurrent._lblviewbord.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 91;BA.debugLine="Return p";
if (true) return _p;
 //BA.debugLineNum = 92;BA.debugLine="End Sub";
return null;
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 26;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 27;BA.debugLine="Private clvServer As CustomListView";
mostCurrent._clvserver = new anywheresoftware.b4a.samples.customlistview.customlistview();
 //BA.debugLineNum = 28;BA.debugLine="Private lblBordName As Label";
mostCurrent._lblbordname = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 29;BA.debugLine="Private lblViewBord As Label";
mostCurrent._lblviewbord = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 30;BA.debugLine="Private pnlBord As Panel";
mostCurrent._pnlbord = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 31;BA.debugLine="Private lblLastCheck As Label";
mostCurrent._lbllastcheck = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 32;BA.debugLine="Private pnlNobords As Panel";
mostCurrent._pnlnobords = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 34;BA.debugLine="End Sub";
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
 //BA.debugLineNum = 193;BA.debugLine="Dim v As View = Sender";
_v = new anywheresoftware.b4a.objects.ConcreteViewWrapper();
_v.setObject((android.view.View)(anywheresoftware.b4a.keywords.Common.Sender(mostCurrent.activityBA)));
 //BA.debugLineNum = 194;BA.debugLine="Dim p As Panel = v.Parent";
_p = new anywheresoftware.b4a.objects.PanelWrapper();
_p.setObject((android.view.ViewGroup)(_v.getParent()));
 //BA.debugLineNum = 195;BA.debugLine="Dim lbl As Label";
_lbl = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 198;BA.debugLine="For Each c As View In p.GetAllViewsRecursive";
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
 //BA.debugLineNum = 199;BA.debugLine="If c.Tag = \"name\" Then";
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
 //BA.debugLineNum = 200;BA.debugLine="lbl = c";
_lbl.setObject((android.widget.TextView)(_c.getObject()));
 //BA.debugLineNum = 201;BA.debugLine="Starter.selectedBordName = PrepTopicName(lbl.Te";
parent.mostCurrent._starter._selectedbordname /*String*/  = _preptopicname(_lbl.getText());
 //BA.debugLineNum = 202;BA.debugLine="Starter.DiscoveredServer = lbl.Text";
parent.mostCurrent._starter._discoveredserver /*String*/  = _lbl.getText();
 //BA.debugLineNum = 203;BA.debugLine="Exit";
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
 //BA.debugLineNum = 207;BA.debugLine="DisconnectMqtt";
_disconnectmqtt();
 //BA.debugLineNum = 208;BA.debugLine="Sleep(100)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,this,(int) (100));
this.state = 11;
return;
case 11:
//C
this.state = -1;
;
 //BA.debugLineNum = 210;BA.debugLine="StartActivity(ServerBoard)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(parent.mostCurrent._serverboard.getObject()));
 //BA.debugLineNum = 211;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static String  _preptopicname(String _bord) throws Exception{
 //BA.debugLineNum = 213;BA.debugLine="Private Sub PrepTopicName(bord As String) As Strin";
 //BA.debugLineNum = 214;BA.debugLine="Log($\"SUBBORD : ${bord.Replace(\" \", \"\")}\"$)";
anywheresoftware.b4a.keywords.Common.LogImpl("8917505",("SUBBORD : "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_bord.replace(" ","")))+""),0);
 //BA.debugLineNum = 215;BA.debugLine="Return bord.Replace(\" \", \"\")";
if (true) return _bord.replace(" ","");
 //BA.debugLineNum = 216;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main._process_globals();
starter._process_globals();
serverboard._process_globals();
		
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
 //BA.debugLineNum = 21;BA.debugLine="Private mqttGetBord As mqttGetBords";
_mqttgetbord = new nl.pdeg.bordondroid.mqttgetbords();
 //BA.debugLineNum = 22;BA.debugLine="Private mqttGetData As mqttGetBordData";
_mqttgetdata = new nl.pdeg.bordondroid.mqttgetborddata();
 //BA.debugLineNum = 23;BA.debugLine="Private tmrBordLastAlive As Timer";
_tmrbordlastalive = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 24;BA.debugLine="End Sub";
return "";
}
public static String  _serveralive() throws Exception{
long _msnow = 0L;
nl.pdeg.bordondroid.main._bordstatus _lst = null;
 //BA.debugLineNum = 124;BA.debugLine="Sub ServerAlive";
 //BA.debugLineNum = 126;BA.debugLine="Dim msNow As Long = DateTime.Now";
_msnow = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 127;BA.debugLine="For Each lst As bordStatus In Starter.serverList";
{
final anywheresoftware.b4a.BA.IterableList group2 = mostCurrent._starter._serverlist /*anywheresoftware.b4a.objects.collections.List*/ ;
final int groupLen2 = group2.getSize()
;int index2 = 0;
;
for (; index2 < groupLen2;index2++){
_lst = (nl.pdeg.bordondroid.main._bordstatus)(group2.Get(index2));
 //BA.debugLineNum = 128;BA.debugLine="If(msNow - lst.timeStamp) > Starter.serverDied A";
if ((_msnow-_lst.timeStamp /*long*/ )>mostCurrent._starter._serverdied /*long*/  && _lst.alive /*boolean*/ ==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 129;BA.debugLine="lst.alive = False";
_lst.alive /*boolean*/  = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 130;BA.debugLine="CheckIpInClv(lst)";
_checkipinclv(_lst);
 //BA.debugLineNum = 131;BA.debugLine="Return";
if (true) return "";
 }else {
 //BA.debugLineNum = 133;BA.debugLine="CheckIpInClv(lst)";
_checkipinclv(_lst);
 };
 }
};
 //BA.debugLineNum = 136;BA.debugLine="End Sub";
return "";
}
public static String  _tmrbordalive_tick() throws Exception{
 //BA.debugLineNum = 47;BA.debugLine="Sub tmrBordAlive_Tick";
 //BA.debugLineNum = 48;BA.debugLine="ServerAlive";
_serveralive();
 //BA.debugLineNum = 49;BA.debugLine="End Sub";
return "";
}
}
