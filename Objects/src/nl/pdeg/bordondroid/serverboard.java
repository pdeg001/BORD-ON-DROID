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
			processBA = new anywheresoftware.b4a.ShellBA(this.getApplicationContext(), null, null, "nl.pdeg.bordondroid", "nl.pdeg.bordondroid.serverboard");
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



public static void initializeProcessGlobals() {
             try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
}
public anywheresoftware.b4a.keywords.Common __c = null;
public static nl.pdeg.bordondroid.mqttconnector _mqttbase = null;
public static nl.pdeg.bordondroid.base _basefile = null;
public static anywheresoftware.b4a.objects.CSBuilder _cs = null;
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
public anywheresoftware.b4a.objects.LabelWrapper _lblp21 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblp1moy = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblp2moy = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblbeurt100 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblbeurt10 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblbeurt1 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _imgp2play = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _imgp1play = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _imgsponsor = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _imgnodata = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbltafelnaam = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblspelduur = null;
public b4a.example.dateutils _dateutils = null;
public nl.pdeg.bordondroid.main _main = null;
public nl.pdeg.bordondroid.locations _locations = null;
public nl.pdeg.bordondroid.starter _starter = null;
public nl.pdeg.bordondroid.selectlocation _selectlocation = null;
public static void  _connectionlost() throws Exception{
RDebugUtils.currentModule="serverboard";
if (Debug.shouldDelegate(mostCurrent.activityBA, "connectionlost", false))
	 {Debug.delegate(mostCurrent.activityBA, "connectionlost", null); return;}
ResumableSub_ConnectionLost rsub = new ResumableSub_ConnectionLost(null);
rsub.resume(processBA, null);
}
public static class ResumableSub_ConnectionLost extends BA.ResumableSub {
public ResumableSub_ConnectionLost(nl.pdeg.bordondroid.serverboard parent) {
this.parent = parent;
}
nl.pdeg.bordondroid.serverboard parent;

@Override
public void resume(BA ba, Object[] result) throws Exception{
RDebugUtils.currentModule="serverboard";

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = -1;
RDebugUtils.currentLine=11010049;
 //BA.debugLineNum = 11010049;BA.debugLine="baseFile.createCustomToast(\"Verbinding met bord v";
parent._basefile._createcustomtoast /*String*/ (null,"Verbinding met bord verloren",BA.NumberToString(anywheresoftware.b4a.keywords.Common.Colors.Red));
RDebugUtils.currentLine=11010050;
 //BA.debugLineNum = 11010050;BA.debugLine="Sleep(2000)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "serverboard", "connectionlost"),(int) (2000));
this.state = 1;
return;
case 1:
//C
this.state = -1;
;
RDebugUtils.currentLine=11010051;
 //BA.debugLineNum = 11010051;BA.debugLine="lastMessageTimer.Enabled = False";
parent._lastmessagetimer.setEnabled(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=11010052;
 //BA.debugLineNum = 11010052;BA.debugLine="DisconnetMqtt";
_disconnetmqtt();
RDebugUtils.currentLine=11010053;
 //BA.debugLineNum = 11010053;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static String  _gamedended() throws Exception{
RDebugUtils.currentModule="serverboard";
if (Debug.shouldDelegate(mostCurrent.activityBA, "gamedended", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "gamedended", null));}
RDebugUtils.currentLine=11599872;
 //BA.debugLineNum = 11599872;BA.debugLine="Public Sub GamedEnded";
RDebugUtils.currentLine=11599873;
 //BA.debugLineNum = 11599873;BA.debugLine="lblSpelduur.TextColor = Colors.Red";
mostCurrent._lblspelduur.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
RDebugUtils.currentLine=11599874;
 //BA.debugLineNum = 11599874;BA.debugLine="Msgbox2Async(\"Spel beëindigd\", Application.LabelN";
anywheresoftware.b4a.keywords.Common.Msgbox2Async(BA.ObjectToCharSequence("Spel beëindigd"),BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Application.getLabelName()),"OKE","","",anywheresoftware.b4a.keywords.Common.Application.getIcon(),processBA,anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=11599875;
 //BA.debugLineNum = 11599875;BA.debugLine="End Sub";
return "";
}
public static String  _updatebordwhenclient(nl.pdeg.bordondroid.main._message _data) throws Exception{
RDebugUtils.currentModule="serverboard";
if (Debug.shouldDelegate(mostCurrent.activityBA, "updatebordwhenclient", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "updatebordwhenclient", new Object[] {_data}));}
String _number = "";
String _str = "";
anywheresoftware.b4a.objects.collections.Map _root = null;
anywheresoftware.b4a.objects.collections.Map _score = null;
anywheresoftware.b4a.objects.collections.Map _p1 = null;
anywheresoftware.b4a.objects.collections.Map _p2 = null;
anywheresoftware.b4a.objects.collections.Map _aan_stoot = null;
String _speler = "";
anywheresoftware.b4a.objects.collections.Map _spelduur = null;
String _tijd = "";
anywheresoftware.b4a.objects.collections.Map _beurten = null;
String _aantal = "";
RDebugUtils.currentLine=11534336;
 //BA.debugLineNum = 11534336;BA.debugLine="public Sub UpdateBordWhenClient(data As Message)";
RDebugUtils.currentLine=11534337;
 //BA.debugLineNum = 11534337;BA.debugLine="HideWaitLabel";
_hidewaitlabel();
RDebugUtils.currentLine=11534339;
 //BA.debugLineNum = 11534339;BA.debugLine="lblSpelduur.TextColor = Colors.White";
mostCurrent._lblspelduur.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
RDebugUtils.currentLine=11534340;
 //BA.debugLineNum = 11534340;BA.debugLine="Dim Number, str As String";
_number = "";
_str = "";
RDebugUtils.currentLine=11534341;
 //BA.debugLineNum = 11534341;BA.debugLine="str = data.Body";
_str = _data.Body /*String*/ ;
RDebugUtils.currentLine=11534343;
 //BA.debugLineNum = 11534343;BA.debugLine="parser.Initialize(str)";
mostCurrent._parser.Initialize(_str);
RDebugUtils.currentLine=11534344;
 //BA.debugLineNum = 11534344;BA.debugLine="Dim root As Map = parser.NextObject";
_root = new anywheresoftware.b4a.objects.collections.Map();
_root = mostCurrent._parser.NextObject();
RDebugUtils.currentLine=11534345;
 //BA.debugLineNum = 11534345;BA.debugLine="Dim score As Map = root.Get(\"score\")";
_score = new anywheresoftware.b4a.objects.collections.Map();
_score.setObject((anywheresoftware.b4a.objects.collections.Map.MyMap)(_root.Get((Object)("score"))));
RDebugUtils.currentLine=11534346;
 //BA.debugLineNum = 11534346;BA.debugLine="Dim p1 As Map = score.Get(\"p1\")";
_p1 = new anywheresoftware.b4a.objects.collections.Map();
_p1.setObject((anywheresoftware.b4a.objects.collections.Map.MyMap)(_score.Get((Object)("p1"))));
RDebugUtils.currentLine=11534347;
 //BA.debugLineNum = 11534347;BA.debugLine="Dim p2 As Map = score.Get(\"p2\")";
_p2 = new anywheresoftware.b4a.objects.collections.Map();
_p2.setObject((anywheresoftware.b4a.objects.collections.Map.MyMap)(_score.Get((Object)("p2"))));
RDebugUtils.currentLine=11534349;
 //BA.debugLineNum = 11534349;BA.debugLine="Dim aan_stoot As Map = score.Get(\"aan_stoot\")";
_aan_stoot = new anywheresoftware.b4a.objects.collections.Map();
_aan_stoot.setObject((anywheresoftware.b4a.objects.collections.Map.MyMap)(_score.Get((Object)("aan_stoot"))));
RDebugUtils.currentLine=11534350;
 //BA.debugLineNum = 11534350;BA.debugLine="Dim speler As String = aan_stoot.Get(\"speler\")";
_speler = BA.ObjectToString(_aan_stoot.Get((Object)("speler")));
RDebugUtils.currentLine=11534351;
 //BA.debugLineNum = 11534351;BA.debugLine="Dim spelduur As Map = score.Get(\"spelduur\")";
_spelduur = new anywheresoftware.b4a.objects.collections.Map();
_spelduur.setObject((anywheresoftware.b4a.objects.collections.Map.MyMap)(_score.Get((Object)("spelduur"))));
RDebugUtils.currentLine=11534352;
 //BA.debugLineNum = 11534352;BA.debugLine="Dim tijd As String = spelduur.Get(\"tijd\")";
_tijd = BA.ObjectToString(_spelduur.Get((Object)("tijd")));
RDebugUtils.currentLine=11534353;
 //BA.debugLineNum = 11534353;BA.debugLine="Dim beurten As Map = score.Get(\"beurten\")";
_beurten = new anywheresoftware.b4a.objects.collections.Map();
_beurten.setObject((anywheresoftware.b4a.objects.collections.Map.MyMap)(_score.Get((Object)("beurten"))));
RDebugUtils.currentLine=11534354;
 //BA.debugLineNum = 11534354;BA.debugLine="Dim aantal As String = beurten.Get(\"aantal\")";
_aantal = BA.ObjectToString(_beurten.Get((Object)("aantal")));
RDebugUtils.currentLine=11534356;
 //BA.debugLineNum = 11534356;BA.debugLine="lblP1Name.Text = p1.Get(\"naam\")";
mostCurrent._lblp1name.setText(BA.ObjectToCharSequence(_p1.Get((Object)("naam"))));
RDebugUtils.currentLine=11534357;
 //BA.debugLineNum = 11534357;BA.debugLine="Number = p1.Get(\"caram\")";
_number = BA.ObjectToString(_p1.Get((Object)("caram")));
RDebugUtils.currentLine=11534358;
 //BA.debugLineNum = 11534358;BA.debugLine="lblP1100.Text = Number.SubString2(0,1)";
mostCurrent._lblp1100.setText(BA.ObjectToCharSequence(_number.substring((int) (0),(int) (1))));
RDebugUtils.currentLine=11534359;
 //BA.debugLineNum = 11534359;BA.debugLine="lblP110.Text = Number.SubString2(1,2)";
mostCurrent._lblp110.setText(BA.ObjectToCharSequence(_number.substring((int) (1),(int) (2))));
RDebugUtils.currentLine=11534360;
 //BA.debugLineNum = 11534360;BA.debugLine="lblP11.Text = Number.SubString2(2,3)";
mostCurrent._lblp11.setText(BA.ObjectToCharSequence(_number.substring((int) (2),(int) (3))));
RDebugUtils.currentLine=11534361;
 //BA.debugLineNum = 11534361;BA.debugLine="Number = p1.Get(\"maken\")";
_number = BA.ObjectToString(_p1.Get((Object)("maken")));
RDebugUtils.currentLine=11534362;
 //BA.debugLineNum = 11534362;BA.debugLine="lblP1Maken100.Text = Number.SubString2(0,1)";
mostCurrent._lblp1maken100.setText(BA.ObjectToCharSequence(_number.substring((int) (0),(int) (1))));
RDebugUtils.currentLine=11534363;
 //BA.debugLineNum = 11534363;BA.debugLine="lblP1Maken10.Text = Number.SubString2(1,2)";
mostCurrent._lblp1maken10.setText(BA.ObjectToCharSequence(_number.substring((int) (1),(int) (2))));
RDebugUtils.currentLine=11534364;
 //BA.debugLineNum = 11534364;BA.debugLine="lblP1Maken1.Text = Number.SubString2(2,3)";
mostCurrent._lblp1maken1.setText(BA.ObjectToCharSequence(_number.substring((int) (2),(int) (3))));
RDebugUtils.currentLine=11534365;
 //BA.debugLineNum = 11534365;BA.debugLine="lblP1Moy.Text = cs.Initialize.Typeface(Typeface.F";
mostCurrent._lblp1moy.setText(BA.ObjectToCharSequence(_cs.Initialize().Typeface(anywheresoftware.b4a.keywords.Common.Typeface.getFONTAWESOME()).Append(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Chr((int) (0xf201)))).Append(BA.ObjectToCharSequence("  ")).Append(BA.ObjectToCharSequence(_p1.Get((Object)("moyenne")))).PopAll().getObject()));
RDebugUtils.currentLine=11534367;
 //BA.debugLineNum = 11534367;BA.debugLine="lblP2Name.Text = p2.Get(\"naam\")";
mostCurrent._lblp2name.setText(BA.ObjectToCharSequence(_p2.Get((Object)("naam"))));
RDebugUtils.currentLine=11534368;
 //BA.debugLineNum = 11534368;BA.debugLine="Number = p2.Get(\"caram\")";
_number = BA.ObjectToString(_p2.Get((Object)("caram")));
RDebugUtils.currentLine=11534369;
 //BA.debugLineNum = 11534369;BA.debugLine="lblP2100.Text = Number.SubString2(0,1)";
mostCurrent._lblp2100.setText(BA.ObjectToCharSequence(_number.substring((int) (0),(int) (1))));
RDebugUtils.currentLine=11534370;
 //BA.debugLineNum = 11534370;BA.debugLine="lblP210.Text = Number.SubString2(1,2)";
mostCurrent._lblp210.setText(BA.ObjectToCharSequence(_number.substring((int) (1),(int) (2))));
RDebugUtils.currentLine=11534371;
 //BA.debugLineNum = 11534371;BA.debugLine="lblP21.Text = Number.SubString2(2,3)";
mostCurrent._lblp21.setText(BA.ObjectToCharSequence(_number.substring((int) (2),(int) (3))));
RDebugUtils.currentLine=11534372;
 //BA.debugLineNum = 11534372;BA.debugLine="Number = p2.Get(\"maken\")";
_number = BA.ObjectToString(_p2.Get((Object)("maken")));
RDebugUtils.currentLine=11534373;
 //BA.debugLineNum = 11534373;BA.debugLine="lblP2Maken100.Text = Number.SubString2(0,1)";
mostCurrent._lblp2maken100.setText(BA.ObjectToCharSequence(_number.substring((int) (0),(int) (1))));
RDebugUtils.currentLine=11534374;
 //BA.debugLineNum = 11534374;BA.debugLine="lblP2Maken10.Text = Number.SubString2(1,2)";
mostCurrent._lblp2maken10.setText(BA.ObjectToCharSequence(_number.substring((int) (1),(int) (2))));
RDebugUtils.currentLine=11534375;
 //BA.debugLineNum = 11534375;BA.debugLine="lblP2Maken1.Text = Number.SubString2(2,3)";
mostCurrent._lblp2maken1.setText(BA.ObjectToCharSequence(_number.substring((int) (2),(int) (3))));
RDebugUtils.currentLine=11534377;
 //BA.debugLineNum = 11534377;BA.debugLine="cs.Initialize.Append(\"\").Typeface(Typeface.FONTAW";
_cs.Initialize().Append(BA.ObjectToCharSequence("")).Typeface(anywheresoftware.b4a.keywords.Common.Typeface.getFONTAWESOME()).Append(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Chr((int) (0xf201)))).PopAll();
RDebugUtils.currentLine=11534378;
 //BA.debugLineNum = 11534378;BA.debugLine="lblP2Moy.Text = cs.Initialize.Typeface(Typeface.F";
mostCurrent._lblp2moy.setText(BA.ObjectToCharSequence(_cs.Initialize().Typeface(anywheresoftware.b4a.keywords.Common.Typeface.getFONTAWESOME()).Append(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Chr((int) (0xf201)))).Append(BA.ObjectToCharSequence("  ")).Append(BA.ObjectToCharSequence(_p2.Get((Object)("moyenne")))).PopAll().getObject()));
RDebugUtils.currentLine=11534380;
 //BA.debugLineNum = 11534380;BA.debugLine="lblBeurt100.Text = aantal.SubString2(0,1)";
mostCurrent._lblbeurt100.setText(BA.ObjectToCharSequence(_aantal.substring((int) (0),(int) (1))));
RDebugUtils.currentLine=11534381;
 //BA.debugLineNum = 11534381;BA.debugLine="lblBeurt10.Text = aantal.SubString2(1,2)";
mostCurrent._lblbeurt10.setText(BA.ObjectToCharSequence(_aantal.substring((int) (1),(int) (2))));
RDebugUtils.currentLine=11534382;
 //BA.debugLineNum = 11534382;BA.debugLine="lblBeurt1.Text = aantal.SubString2(2,3)";
mostCurrent._lblbeurt1.setText(BA.ObjectToCharSequence(_aantal.substring((int) (2),(int) (3))));
RDebugUtils.currentLine=11534383;
 //BA.debugLineNum = 11534383;BA.debugLine="lblSpelduur.Text = tijd'score.Get(\"spelduur\")";
mostCurrent._lblspelduur.setText(BA.ObjectToCharSequence(_tijd));
RDebugUtils.currentLine=11534384;
 //BA.debugLineNum = 11534384;BA.debugLine="lblSpelduur.Text = cs.Initialize.Typeface(Typefac";
mostCurrent._lblspelduur.setText(BA.ObjectToCharSequence(_cs.Initialize().Typeface(anywheresoftware.b4a.keywords.Common.Typeface.getFONTAWESOME()).Append(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Chr((int) (0xf253)))).Append(BA.ObjectToCharSequence("  ")).Append(BA.ObjectToCharSequence(_tijd)).PopAll().getObject()));
RDebugUtils.currentLine=11534386;
 //BA.debugLineNum = 11534386;BA.debugLine="imgP1Play.Visible = False";
mostCurrent._imgp1play.setVisible(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=11534387;
 //BA.debugLineNum = 11534387;BA.debugLine="imgP2Play.Visible = False";
mostCurrent._imgp2play.setVisible(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=11534388;
 //BA.debugLineNum = 11534388;BA.debugLine="If speler = 1 Then";
if ((_speler).equals(BA.NumberToString(1))) { 
RDebugUtils.currentLine=11534389;
 //BA.debugLineNum = 11534389;BA.debugLine="imgP1Play.Visible = True";
mostCurrent._imgp1play.setVisible(anywheresoftware.b4a.keywords.Common.True);
 }else {
RDebugUtils.currentLine=11534391;
 //BA.debugLineNum = 11534391;BA.debugLine="imgP2Play.Visible = True";
mostCurrent._imgp2play.setVisible(anywheresoftware.b4a.keywords.Common.True);
 };
RDebugUtils.currentLine=11534393;
 //BA.debugLineNum = 11534393;BA.debugLine="End Sub";
return "";
}
public static void  _activity_create(boolean _firsttime) throws Exception{
RDebugUtils.currentModule="serverboard";
if (Debug.shouldDelegate(mostCurrent.activityBA, "activity_create", false))
	 {Debug.delegate(mostCurrent.activityBA, "activity_create", new Object[] {_firsttime}); return;}
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
RDebugUtils.currentModule="serverboard";

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
RDebugUtils.currentLine=10944513;
 //BA.debugLineNum = 10944513;BA.debugLine="Starter.mainPaused = False";
parent.mostCurrent._starter._mainpaused /*boolean*/  = anywheresoftware.b4a.keywords.Common.False;
RDebugUtils.currentLine=10944514;
 //BA.debugLineNum = 10944514;BA.debugLine="If Not (mqttBase.IsInitialized) Then";
if (true) break;

case 1:
//if
this.state = 4;
if (anywheresoftware.b4a.keywords.Common.Not(parent._mqttbase.IsInitialized /*boolean*/ ())) { 
this.state = 3;
}if (true) break;

case 3:
//C
this.state = 4;
RDebugUtils.currentLine=10944515;
 //BA.debugLineNum = 10944515;BA.debugLine="mqttBase.Initialize";
parent._mqttbase._initialize /*String*/ (null,processBA);
 if (true) break;

case 4:
//C
this.state = -1;
;
RDebugUtils.currentLine=10944517;
 //BA.debugLineNum = 10944517;BA.debugLine="baseFile.Initialize";
parent._basefile._initialize /*String*/ (null,processBA);
RDebugUtils.currentLine=10944518;
 //BA.debugLineNum = 10944518;BA.debugLine="CallSub(Starter, \"SetSubString\")";
anywheresoftware.b4a.keywords.Common.CallSubDebug(processBA,(Object)(parent.mostCurrent._starter.getObject()),"SetSubString");
RDebugUtils.currentLine=10944520;
 //BA.debugLineNum = 10944520;BA.debugLine="Activity.LoadLayout(\"ServerBoard\")";
parent.mostCurrent._activity.LoadLayout("ServerBoard",mostCurrent.activityBA);
RDebugUtils.currentLine=10944521;
 //BA.debugLineNum = 10944521;BA.debugLine="SetImgSponsor";
_setimgsponsor();
RDebugUtils.currentLine=10944522;
 //BA.debugLineNum = 10944522;BA.debugLine="lastMessageTimer.Initialize(\"tmrLastMessase\", 120";
parent._lastmessagetimer.Initialize(processBA,"tmrLastMessase",(long) (120*1000));
RDebugUtils.currentLine=10944523;
 //BA.debugLineNum = 10944523;BA.debugLine="lastMessageTimer.Enabled = True";
parent._lastmessagetimer.setEnabled(anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=10944526;
 //BA.debugLineNum = 10944526;BA.debugLine="mqttBase.Connect";
parent._mqttbase._connect /*String*/ (null);
RDebugUtils.currentLine=10944528;
 //BA.debugLineNum = 10944528;BA.debugLine="lblTafelNaam.Text = Starter.DiscoveredServer";
parent.mostCurrent._lbltafelnaam.setText(BA.ObjectToCharSequence(parent.mostCurrent._starter._discoveredserver /*String*/ ));
RDebugUtils.currentLine=10944530;
 //BA.debugLineNum = 10944530;BA.debugLine="Sleep(1000)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "serverboard", "activity_create"),(int) (1000));
this.state = 5;
return;
case 5:
//C
this.state = -1;
;
RDebugUtils.currentLine=10944531;
 //BA.debugLineNum = 10944531;BA.debugLine="mqttBase.SendMessage(\"data please\")";
parent._mqttbase._sendmessage /*String*/ (null,"data please");
RDebugUtils.currentLine=10944532;
 //BA.debugLineNum = 10944532;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static String  _setimgsponsor() throws Exception{
RDebugUtils.currentModule="serverboard";
if (Debug.shouldDelegate(mostCurrent.activityBA, "setimgsponsor", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "setimgsponsor", null));}
anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _bmp = null;
String _bmpname = "";
int _nuleen = 0;
RDebugUtils.currentLine=27131904;
 //BA.debugLineNum = 27131904;BA.debugLine="Private Sub SetImgSponsor";
RDebugUtils.currentLine=27131905;
 //BA.debugLineNum = 27131905;BA.debugLine="Dim bmp As Bitmap";
_bmp = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
RDebugUtils.currentLine=27131906;
 //BA.debugLineNum = 27131906;BA.debugLine="Dim bmpName As String";
_bmpname = "";
RDebugUtils.currentLine=27131907;
 //BA.debugLineNum = 27131907;BA.debugLine="Dim nuleen As Int = Rnd(0,2)";
_nuleen = anywheresoftware.b4a.keywords.Common.Rnd((int) (0),(int) (2));
RDebugUtils.currentLine=27131909;
 //BA.debugLineNum = 27131909;BA.debugLine="If nuleen = 0 Then";
if (_nuleen==0) { 
RDebugUtils.currentLine=27131910;
 //BA.debugLineNum = 27131910;BA.debugLine="bmpName = \"sven1.jpg\"";
_bmpname = "sven1.jpg";
 }else {
RDebugUtils.currentLine=27131912;
 //BA.debugLineNum = 27131912;BA.debugLine="bmpName = \"sven_oud.jpg\"";
_bmpname = "sven_oud.jpg";
 };
RDebugUtils.currentLine=27131915;
 //BA.debugLineNum = 27131915;BA.debugLine="bmp = LoadBitmapResize(File.DirAssets, bmpName, i";
_bmp = anywheresoftware.b4a.keywords.Common.LoadBitmapResize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),_bmpname,mostCurrent._imgsponsor.getWidth(),mostCurrent._imgsponsor.getHeight(),anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=27131916;
 //BA.debugLineNum = 27131916;BA.debugLine="imgSponsor.SetBackgroundImage(bmp)";
mostCurrent._imgsponsor.SetBackgroundImageNew((android.graphics.Bitmap)(_bmp.getObject()));
RDebugUtils.currentLine=27131917;
 //BA.debugLineNum = 27131917;BA.debugLine="End Sub";
return "";
}
public static boolean  _activity_keypress(int _keycode) throws Exception{
RDebugUtils.currentModule="serverboard";
if (Debug.shouldDelegate(mostCurrent.activityBA, "activity_keypress", false))
	 {return ((Boolean) Debug.delegate(mostCurrent.activityBA, "activity_keypress", new Object[] {_keycode}));}
RDebugUtils.currentLine=11468800;
 //BA.debugLineNum = 11468800;BA.debugLine="Private Sub Activity_KeyPress(KeyCode As Int) As B";
RDebugUtils.currentLine=11468801;
 //BA.debugLineNum = 11468801;BA.debugLine="If KeyCode = KeyCodes.KEYCODE_BACK Then";
if (_keycode==anywheresoftware.b4a.keywords.Common.KeyCodes.KEYCODE_BACK) { 
RDebugUtils.currentLine=11468802;
 //BA.debugLineNum = 11468802;BA.debugLine="CallSubDelayed(Main, \"setBordLastAliveTimer\")";
anywheresoftware.b4a.keywords.Common.CallSubDelayed(processBA,(Object)(mostCurrent._main.getObject()),"setBordLastAliveTimer");
RDebugUtils.currentLine=11468803;
 //BA.debugLineNum = 11468803;BA.debugLine="lastMessageTimer.Enabled = False";
_lastmessagetimer.setEnabled(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=11468804;
 //BA.debugLineNum = 11468804;BA.debugLine="DisconnetMqtt";
_disconnetmqtt();
RDebugUtils.currentLine=11468805;
 //BA.debugLineNum = 11468805;BA.debugLine="CallSubDelayed(Main, \"ReconnectToLocation\")";
anywheresoftware.b4a.keywords.Common.CallSubDelayed(processBA,(Object)(mostCurrent._main.getObject()),"ReconnectToLocation");
RDebugUtils.currentLine=11468806;
 //BA.debugLineNum = 11468806;BA.debugLine="Return False";
if (true) return anywheresoftware.b4a.keywords.Common.False;
 }else {
RDebugUtils.currentLine=11468808;
 //BA.debugLineNum = 11468808;BA.debugLine="Return True";
if (true) return anywheresoftware.b4a.keywords.Common.True;
 };
RDebugUtils.currentLine=11468810;
 //BA.debugLineNum = 11468810;BA.debugLine="End Sub";
return false;
}
public static String  _disconnetmqtt() throws Exception{
RDebugUtils.currentModule="serverboard";
if (Debug.shouldDelegate(mostCurrent.activityBA, "disconnetmqtt", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "disconnetmqtt", null));}
RDebugUtils.currentLine=11403264;
 //BA.debugLineNum = 11403264;BA.debugLine="Sub DisconnetMqtt";
RDebugUtils.currentLine=11403265;
 //BA.debugLineNum = 11403265;BA.debugLine="If mqttBase.connected Then";
if (_mqttbase._connected /*boolean*/ ) { 
RDebugUtils.currentLine=11403266;
 //BA.debugLineNum = 11403266;BA.debugLine="mqttBase.Disconnect";
_mqttbase._disconnect /*String*/ (null);
 };
RDebugUtils.currentLine=11403268;
 //BA.debugLineNum = 11403268;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
RDebugUtils.currentLine=11403269;
 //BA.debugLineNum = 11403269;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
RDebugUtils.currentModule="serverboard";
RDebugUtils.currentLine=11272192;
 //BA.debugLineNum = 11272192;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
RDebugUtils.currentLine=11272193;
 //BA.debugLineNum = 11272193;BA.debugLine="ResumeConnection(False)";
_resumeconnection(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=11272194;
 //BA.debugLineNum = 11272194;BA.debugLine="End Sub";
return "";
}
public static void  _resumeconnection(boolean _resume) throws Exception{
RDebugUtils.currentModule="serverboard";
if (Debug.shouldDelegate(mostCurrent.activityBA, "resumeconnection", false))
	 {Debug.delegate(mostCurrent.activityBA, "resumeconnection", new Object[] {_resume}); return;}
ResumableSub_ResumeConnection rsub = new ResumableSub_ResumeConnection(null,_resume);
rsub.resume(processBA, null);
}
public static class ResumableSub_ResumeConnection extends BA.ResumableSub {
public ResumableSub_ResumeConnection(nl.pdeg.bordondroid.serverboard parent,boolean _resume) {
this.parent = parent;
this._resume = _resume;
}
nl.pdeg.bordondroid.serverboard parent;
boolean _resume;

@Override
public void resume(BA ba, Object[] result) throws Exception{
RDebugUtils.currentModule="serverboard";

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
RDebugUtils.currentLine=11337729;
 //BA.debugLineNum = 11337729;BA.debugLine="If resume Then";
if (true) break;

case 1:
//if
this.state = 6;
if (_resume) { 
this.state = 3;
}else {
this.state = 5;
}if (true) break;

case 3:
//C
this.state = 6;
RDebugUtils.currentLine=11337730;
 //BA.debugLineNum = 11337730;BA.debugLine="mqttBase.Connect";
parent._mqttbase._connect /*String*/ (null);
RDebugUtils.currentLine=11337731;
 //BA.debugLineNum = 11337731;BA.debugLine="Sleep(500)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "serverboard", "resumeconnection"),(int) (500));
this.state = 7;
return;
case 7:
//C
this.state = 6;
;
RDebugUtils.currentLine=11337732;
 //BA.debugLineNum = 11337732;BA.debugLine="mqttBase.SendMessage(\"data please\")";
parent._mqttbase._sendmessage /*String*/ (null,"data please");
 if (true) break;

case 5:
//C
this.state = 6;
RDebugUtils.currentLine=11337734;
 //BA.debugLineNum = 11337734;BA.debugLine="mqttBase .Disconnect";
parent._mqttbase._disconnect /*String*/ (null);
 if (true) break;

case 6:
//C
this.state = -1;
;
RDebugUtils.currentLine=11337737;
 //BA.debugLineNum = 11337737;BA.debugLine="lastMessageTimer.Enabled = resume";
parent._lastmessagetimer.setEnabled(_resume);
RDebugUtils.currentLine=11337738;
 //BA.debugLineNum = 11337738;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static String  _activity_resume() throws Exception{
RDebugUtils.currentModule="serverboard";
if (Debug.shouldDelegate(mostCurrent.activityBA, "activity_resume", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "activity_resume", null));}
RDebugUtils.currentLine=11206656;
 //BA.debugLineNum = 11206656;BA.debugLine="Sub Activity_Resume";
RDebugUtils.currentLine=11206657;
 //BA.debugLineNum = 11206657;BA.debugLine="ResumeConnection(True)";
_resumeconnection(anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=11206658;
 //BA.debugLineNum = 11206658;BA.debugLine="End Sub";
return "";
}
public static String  _borddied() throws Exception{
RDebugUtils.currentModule="serverboard";
if (Debug.shouldDelegate(mostCurrent.activityBA, "borddied", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "borddied", null));}
RDebugUtils.currentLine=11796480;
 //BA.debugLineNum = 11796480;BA.debugLine="Sub BordDied";
RDebugUtils.currentLine=11796481;
 //BA.debugLineNum = 11796481;BA.debugLine="Msgbox2Async(\"Verbinding verbroken\", Application.";
anywheresoftware.b4a.keywords.Common.Msgbox2Async(BA.ObjectToCharSequence("Verbinding verbroken"),BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Application.getLabelName()),"OK","","",anywheresoftware.b4a.keywords.Common.Application.getIcon(),processBA,anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=11796482;
 //BA.debugLineNum = 11796482;BA.debugLine="End Sub";
return "";
}
public static void  _hidewaitlabel() throws Exception{
RDebugUtils.currentModule="serverboard";
if (Debug.shouldDelegate(mostCurrent.activityBA, "hidewaitlabel", false))
	 {Debug.delegate(mostCurrent.activityBA, "hidewaitlabel", null); return;}
ResumableSub_HideWaitLabel rsub = new ResumableSub_HideWaitLabel(null);
rsub.resume(processBA, null);
}
public static class ResumableSub_HideWaitLabel extends BA.ResumableSub {
public ResumableSub_HideWaitLabel(nl.pdeg.bordondroid.serverboard parent) {
this.parent = parent;
}
nl.pdeg.bordondroid.serverboard parent;

@Override
public void resume(BA ba, Object[] result) throws Exception{
RDebugUtils.currentModule="serverboard";

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
RDebugUtils.currentLine=28049409;
 //BA.debugLineNum = 28049409;BA.debugLine="If imgNoData.Visible Then";
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
RDebugUtils.currentLine=28049410;
 //BA.debugLineNum = 28049410;BA.debugLine="imgNoData.SetVisibleAnimated(0, False)";
parent.mostCurrent._imgnodata.SetVisibleAnimated((int) (0),anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=28049411;
 //BA.debugLineNum = 28049411;BA.debugLine="Sleep(300)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "serverboard", "hidewaitlabel"),(int) (300));
this.state = 5;
return;
case 5:
//C
this.state = 4;
;
 if (true) break;

case 4:
//C
this.state = -1;
;
RDebugUtils.currentLine=28049413;
 //BA.debugLineNum = 28049413;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static String  _tmrlastmessase_tick() throws Exception{
RDebugUtils.currentModule="serverboard";
if (Debug.shouldDelegate(mostCurrent.activityBA, "tmrlastmessase_tick", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "tmrlastmessase_tick", null));}
RDebugUtils.currentLine=11075584;
 //BA.debugLineNum = 11075584;BA.debugLine="Sub tmrLastMessase_Tick";
RDebugUtils.currentLine=11075585;
 //BA.debugLineNum = 11075585;BA.debugLine="If (DateTime.Now-lastMessageTime) >= 120*1000 The";
if ((anywheresoftware.b4a.keywords.Common.DateTime.getNow()-_lastmessagetime)>=120*1000) { 
RDebugUtils.currentLine=11075586;
 //BA.debugLineNum = 11075586;BA.debugLine="mqttBase.SendMessage(\"data please\")";
_mqttbase._sendmessage /*String*/ (null,"data please");
RDebugUtils.currentLine=11075587;
 //BA.debugLineNum = 11075587;BA.debugLine="lblSpelduur.TextColor = Colors.Red";
mostCurrent._lblspelduur.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 };
RDebugUtils.currentLine=11075589;
 //BA.debugLineNum = 11075589;BA.debugLine="End Sub";
return "";
}
}