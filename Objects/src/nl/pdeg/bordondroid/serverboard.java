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
public static nl.pdeg.bordondroid.mqttgetborddata _mqttgetdata = null;
public static anywheresoftware.b4a.objects.Timer _datatmr = null;
public static int _dotcount = 0;
public static String _waittext = "";
public static anywheresoftware.b4a.objects.CSBuilder _cs = null;
public static String _substring = "";
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
public anywheresoftware.b4a.objects.LabelWrapper _lblp1moy = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblp2moy = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblp21 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblbeurt100 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblbeurt10 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblbeurt1 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _imgp2play = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _imgp1play = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _imgnodata = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblnodata = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _imgsponsor = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbltafelnaam = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblspelduur = null;
public b4a.example.dateutils _dateutils = null;
public nl.pdeg.bordondroid.main _main = null;
public nl.pdeg.bordondroid.locations _locations = null;
public nl.pdeg.bordondroid.starter _starter = null;
public static String  _gamedended() throws Exception{
RDebugUtils.currentModule="serverboard";
if (Debug.shouldDelegate(mostCurrent.activityBA, "gamedended", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "gamedended", null));}
RDebugUtils.currentLine=4259840;
 //BA.debugLineNum = 4259840;BA.debugLine="Public Sub GamedEnded";
RDebugUtils.currentLine=4259841;
 //BA.debugLineNum = 4259841;BA.debugLine="lblSpelduur.TextColor = Colors.Red";
mostCurrent._lblspelduur.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
RDebugUtils.currentLine=4259843;
 //BA.debugLineNum = 4259843;BA.debugLine="End Sub";
return "";
}
public static void  _updatebordwhenclient(nl.pdeg.bordondroid.main._message _data) throws Exception{
RDebugUtils.currentModule="serverboard";
if (Debug.shouldDelegate(mostCurrent.activityBA, "updatebordwhenclient", false))
	 {Debug.delegate(mostCurrent.activityBA, "updatebordwhenclient", new Object[] {_data}); return;}
ResumableSub_UpdateBordWhenClient rsub = new ResumableSub_UpdateBordWhenClient(null,_data);
rsub.resume(processBA, null);
}
public static class ResumableSub_UpdateBordWhenClient extends BA.ResumableSub {
public ResumableSub_UpdateBordWhenClient(nl.pdeg.bordondroid.serverboard parent,nl.pdeg.bordondroid.main._message _data) {
this.parent = parent;
this._data = _data;
}
nl.pdeg.bordondroid.serverboard parent;
nl.pdeg.bordondroid.main._message _data;
String _number = "";
String _str = "";
anywheresoftware.b4a.objects.collections.Map _root = null;
anywheresoftware.b4a.objects.collections.Map _score = null;
anywheresoftware.b4a.objects.collections.Map _p1 = null;
anywheresoftware.b4a.objects.collections.Map _p2 = null;
String _moyenne = "";
anywheresoftware.b4a.objects.collections.Map _aan_stoot = null;
String _speler = "";
anywheresoftware.b4a.objects.collections.Map _spelduur = null;
String _tijd = "";
anywheresoftware.b4a.objects.collections.Map _beurten = null;
String _aantal = "";

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
RDebugUtils.currentLine=4194305;
 //BA.debugLineNum = 4194305;BA.debugLine="If imgNoData.Visible Then";
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
RDebugUtils.currentLine=4194306;
 //BA.debugLineNum = 4194306;BA.debugLine="dataTmr.Enabled = False";
parent._datatmr.setEnabled(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=4194307;
 //BA.debugLineNum = 4194307;BA.debugLine="imgNoData.SetVisibleAnimated(1000, False)";
parent.mostCurrent._imgnodata.SetVisibleAnimated((int) (1000),anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=4194311;
 //BA.debugLineNum = 4194311;BA.debugLine="Sleep(1200)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "serverboard", "updatebordwhenclient"),(int) (1200));
this.state = 11;
return;
case 11:
//C
this.state = 4;
;
 if (true) break;

case 4:
//C
this.state = 5;
;
RDebugUtils.currentLine=4194313;
 //BA.debugLineNum = 4194313;BA.debugLine="lblSpelduur.TextColor = Colors.White";
parent.mostCurrent._lblspelduur.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
RDebugUtils.currentLine=4194314;
 //BA.debugLineNum = 4194314;BA.debugLine="Dim Number, str As String";
_number = "";
_str = "";
RDebugUtils.currentLine=4194315;
 //BA.debugLineNum = 4194315;BA.debugLine="str = data.Body";
_str = _data.Body /*String*/ ;
RDebugUtils.currentLine=4194317;
 //BA.debugLineNum = 4194317;BA.debugLine="parser.Initialize(str)";
parent.mostCurrent._parser.Initialize(_str);
RDebugUtils.currentLine=4194318;
 //BA.debugLineNum = 4194318;BA.debugLine="Dim root As Map = parser.NextObject";
_root = new anywheresoftware.b4a.objects.collections.Map();
_root = parent.mostCurrent._parser.NextObject();
RDebugUtils.currentLine=4194319;
 //BA.debugLineNum = 4194319;BA.debugLine="Dim score As Map = root.Get(\"score\")";
_score = new anywheresoftware.b4a.objects.collections.Map();
_score.setObject((anywheresoftware.b4a.objects.collections.Map.MyMap)(_root.Get((Object)("score"))));
RDebugUtils.currentLine=4194320;
 //BA.debugLineNum = 4194320;BA.debugLine="Dim p1 As Map = score.Get(\"p1\")";
_p1 = new anywheresoftware.b4a.objects.collections.Map();
_p1.setObject((anywheresoftware.b4a.objects.collections.Map.MyMap)(_score.Get((Object)("p1"))));
RDebugUtils.currentLine=4194326;
 //BA.debugLineNum = 4194326;BA.debugLine="Dim p2 As Map = score.Get(\"p2\")";
_p2 = new anywheresoftware.b4a.objects.collections.Map();
_p2.setObject((anywheresoftware.b4a.objects.collections.Map.MyMap)(_score.Get((Object)("p2"))));
RDebugUtils.currentLine=4194331;
 //BA.debugLineNum = 4194331;BA.debugLine="Dim moyenne As String = p2.Get(\"moyenne\")";
_moyenne = BA.ObjectToString(_p2.Get((Object)("moyenne")));
RDebugUtils.currentLine=4194332;
 //BA.debugLineNum = 4194332;BA.debugLine="Dim aan_stoot As Map = score.Get(\"aan_stoot\")";
_aan_stoot = new anywheresoftware.b4a.objects.collections.Map();
_aan_stoot.setObject((anywheresoftware.b4a.objects.collections.Map.MyMap)(_score.Get((Object)("aan_stoot"))));
RDebugUtils.currentLine=4194333;
 //BA.debugLineNum = 4194333;BA.debugLine="Dim speler As String = aan_stoot.Get(\"speler\")";
_speler = BA.ObjectToString(_aan_stoot.Get((Object)("speler")));
RDebugUtils.currentLine=4194334;
 //BA.debugLineNum = 4194334;BA.debugLine="Dim spelduur As Map = score.Get(\"spelduur\")";
_spelduur = new anywheresoftware.b4a.objects.collections.Map();
_spelduur.setObject((anywheresoftware.b4a.objects.collections.Map.MyMap)(_score.Get((Object)("spelduur"))));
RDebugUtils.currentLine=4194335;
 //BA.debugLineNum = 4194335;BA.debugLine="Dim tijd As String = spelduur.Get(\"tijd\")";
_tijd = BA.ObjectToString(_spelduur.Get((Object)("tijd")));
RDebugUtils.currentLine=4194338;
 //BA.debugLineNum = 4194338;BA.debugLine="Dim beurten As Map = score.Get(\"beurten\")";
_beurten = new anywheresoftware.b4a.objects.collections.Map();
_beurten.setObject((anywheresoftware.b4a.objects.collections.Map.MyMap)(_score.Get((Object)("beurten"))));
RDebugUtils.currentLine=4194339;
 //BA.debugLineNum = 4194339;BA.debugLine="Dim aantal As String = beurten.Get(\"aantal\")";
_aantal = BA.ObjectToString(_beurten.Get((Object)("aantal")));
RDebugUtils.currentLine=4194343;
 //BA.debugLineNum = 4194343;BA.debugLine="lblP1Name.Text = p1.Get(\"naam\")";
parent.mostCurrent._lblp1name.setText(BA.ObjectToCharSequence(_p1.Get((Object)("naam"))));
RDebugUtils.currentLine=4194344;
 //BA.debugLineNum = 4194344;BA.debugLine="Number = p1.Get(\"caram\")";
_number = BA.ObjectToString(_p1.Get((Object)("caram")));
RDebugUtils.currentLine=4194345;
 //BA.debugLineNum = 4194345;BA.debugLine="lblP1100.Text = Number.SubString2(0,1)";
parent.mostCurrent._lblp1100.setText(BA.ObjectToCharSequence(_number.substring((int) (0),(int) (1))));
RDebugUtils.currentLine=4194346;
 //BA.debugLineNum = 4194346;BA.debugLine="lblP110.Text = Number.SubString2(1,2)";
parent.mostCurrent._lblp110.setText(BA.ObjectToCharSequence(_number.substring((int) (1),(int) (2))));
RDebugUtils.currentLine=4194347;
 //BA.debugLineNum = 4194347;BA.debugLine="lblP11.Text = Number.SubString2(2,3)";
parent.mostCurrent._lblp11.setText(BA.ObjectToCharSequence(_number.substring((int) (2),(int) (3))));
RDebugUtils.currentLine=4194348;
 //BA.debugLineNum = 4194348;BA.debugLine="Number = p1.Get(\"maken\")";
_number = BA.ObjectToString(_p1.Get((Object)("maken")));
RDebugUtils.currentLine=4194349;
 //BA.debugLineNum = 4194349;BA.debugLine="lblP1Maken100.Text = Number.SubString2(0,1)";
parent.mostCurrent._lblp1maken100.setText(BA.ObjectToCharSequence(_number.substring((int) (0),(int) (1))));
RDebugUtils.currentLine=4194350;
 //BA.debugLineNum = 4194350;BA.debugLine="lblP1Maken10.Text = Number.SubString2(1,2)";
parent.mostCurrent._lblp1maken10.setText(BA.ObjectToCharSequence(_number.substring((int) (1),(int) (2))));
RDebugUtils.currentLine=4194351;
 //BA.debugLineNum = 4194351;BA.debugLine="lblP1Maken1.Text = Number.SubString2(2,3)";
parent.mostCurrent._lblp1maken1.setText(BA.ObjectToCharSequence(_number.substring((int) (2),(int) (3))));
RDebugUtils.currentLine=4194353;
 //BA.debugLineNum = 4194353;BA.debugLine="lblP1Moy.Text = cs.Initialize.Typeface(Typeface.F";
parent.mostCurrent._lblp1moy.setText(BA.ObjectToCharSequence(parent._cs.Initialize().Typeface(anywheresoftware.b4a.keywords.Common.Typeface.getFONTAWESOME()).Append(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Chr((int) (0xf201)))).Append(BA.ObjectToCharSequence("  ")).Append(BA.ObjectToCharSequence(_p1.Get((Object)("moyenne")))).PopAll().getObject()));
RDebugUtils.currentLine=4194359;
 //BA.debugLineNum = 4194359;BA.debugLine="lblP2Name.Text = p2.Get(\"naam\")";
parent.mostCurrent._lblp2name.setText(BA.ObjectToCharSequence(_p2.Get((Object)("naam"))));
RDebugUtils.currentLine=4194360;
 //BA.debugLineNum = 4194360;BA.debugLine="Number = p2.Get(\"caram\")";
_number = BA.ObjectToString(_p2.Get((Object)("caram")));
RDebugUtils.currentLine=4194361;
 //BA.debugLineNum = 4194361;BA.debugLine="lblP2100.Text = Number.SubString2(0,1)";
parent.mostCurrent._lblp2100.setText(BA.ObjectToCharSequence(_number.substring((int) (0),(int) (1))));
RDebugUtils.currentLine=4194362;
 //BA.debugLineNum = 4194362;BA.debugLine="lblP210.Text = Number.SubString2(1,2)";
parent.mostCurrent._lblp210.setText(BA.ObjectToCharSequence(_number.substring((int) (1),(int) (2))));
RDebugUtils.currentLine=4194363;
 //BA.debugLineNum = 4194363;BA.debugLine="lblP21.Text = Number.SubString2(2,3)";
parent.mostCurrent._lblp21.setText(BA.ObjectToCharSequence(_number.substring((int) (2),(int) (3))));
RDebugUtils.currentLine=4194364;
 //BA.debugLineNum = 4194364;BA.debugLine="Number = p2.Get(\"maken\")";
_number = BA.ObjectToString(_p2.Get((Object)("maken")));
RDebugUtils.currentLine=4194365;
 //BA.debugLineNum = 4194365;BA.debugLine="lblP2Maken100.Text = Number.SubString2(0,1)";
parent.mostCurrent._lblp2maken100.setText(BA.ObjectToCharSequence(_number.substring((int) (0),(int) (1))));
RDebugUtils.currentLine=4194366;
 //BA.debugLineNum = 4194366;BA.debugLine="lblP2Maken10.Text = Number.SubString2(1,2)";
parent.mostCurrent._lblp2maken10.setText(BA.ObjectToCharSequence(_number.substring((int) (1),(int) (2))));
RDebugUtils.currentLine=4194367;
 //BA.debugLineNum = 4194367;BA.debugLine="lblP2Maken1.Text = Number.SubString2(2,3)";
parent.mostCurrent._lblp2maken1.setText(BA.ObjectToCharSequence(_number.substring((int) (2),(int) (3))));
RDebugUtils.currentLine=4194368;
 //BA.debugLineNum = 4194368;BA.debugLine="cs.Initialize.Append(\"\").Typeface(Typeface.FONTAW";
parent._cs.Initialize().Append(BA.ObjectToCharSequence("")).Typeface(anywheresoftware.b4a.keywords.Common.Typeface.getFONTAWESOME()).Append(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Chr((int) (0xf201)))).PopAll();
RDebugUtils.currentLine=4194375;
 //BA.debugLineNum = 4194375;BA.debugLine="lblP2Moy.Text = cs.Initialize.Typeface(Typeface.F";
parent.mostCurrent._lblp2moy.setText(BA.ObjectToCharSequence(parent._cs.Initialize().Typeface(anywheresoftware.b4a.keywords.Common.Typeface.getFONTAWESOME()).Append(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Chr((int) (0xf201)))).Append(BA.ObjectToCharSequence("  ")).Append(BA.ObjectToCharSequence(_p2.Get((Object)("moyenne")))).PopAll().getObject()));
RDebugUtils.currentLine=4194379;
 //BA.debugLineNum = 4194379;BA.debugLine="lblBeurt100.Text = aantal.SubString2(0,1)";
parent.mostCurrent._lblbeurt100.setText(BA.ObjectToCharSequence(_aantal.substring((int) (0),(int) (1))));
RDebugUtils.currentLine=4194380;
 //BA.debugLineNum = 4194380;BA.debugLine="lblBeurt10.Text = aantal.SubString2(1,2)";
parent.mostCurrent._lblbeurt10.setText(BA.ObjectToCharSequence(_aantal.substring((int) (1),(int) (2))));
RDebugUtils.currentLine=4194381;
 //BA.debugLineNum = 4194381;BA.debugLine="lblBeurt1.Text = aantal.SubString2(2,3)";
parent.mostCurrent._lblbeurt1.setText(BA.ObjectToCharSequence(_aantal.substring((int) (2),(int) (3))));
RDebugUtils.currentLine=4194383;
 //BA.debugLineNum = 4194383;BA.debugLine="lblSpelduur.Text = tijd'score.Get(\"spelduur\")";
parent.mostCurrent._lblspelduur.setText(BA.ObjectToCharSequence(_tijd));
RDebugUtils.currentLine=4194384;
 //BA.debugLineNum = 4194384;BA.debugLine="lblSpelduur.Text = cs.Initialize.Typeface(Typefac";
parent.mostCurrent._lblspelduur.setText(BA.ObjectToCharSequence(parent._cs.Initialize().Typeface(anywheresoftware.b4a.keywords.Common.Typeface.getFONTAWESOME()).Append(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Chr((int) (0xf253)))).Append(BA.ObjectToCharSequence("  ")).Append(BA.ObjectToCharSequence(_tijd)).PopAll().getObject()));
RDebugUtils.currentLine=4194388;
 //BA.debugLineNum = 4194388;BA.debugLine="imgP1Play.Visible = False";
parent.mostCurrent._imgp1play.setVisible(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=4194389;
 //BA.debugLineNum = 4194389;BA.debugLine="imgP2Play.Visible = False";
parent.mostCurrent._imgp2play.setVisible(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=4194390;
 //BA.debugLineNum = 4194390;BA.debugLine="If speler = 1 Then";
if (true) break;

case 5:
//if
this.state = 10;
if ((_speler).equals(BA.NumberToString(1))) { 
this.state = 7;
}else {
this.state = 9;
}if (true) break;

case 7:
//C
this.state = 10;
RDebugUtils.currentLine=4194391;
 //BA.debugLineNum = 4194391;BA.debugLine="imgP1Play.Visible = True";
parent.mostCurrent._imgp1play.setVisible(anywheresoftware.b4a.keywords.Common.True);
 if (true) break;

case 9:
//C
this.state = 10;
RDebugUtils.currentLine=4194393;
 //BA.debugLineNum = 4194393;BA.debugLine="imgP2Play.Visible = True";
parent.mostCurrent._imgp2play.setVisible(anywheresoftware.b4a.keywords.Common.True);
 if (true) break;

case 10:
//C
this.state = -1;
;
RDebugUtils.currentLine=4194395;
 //BA.debugLineNum = 4194395;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
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
anywheresoftware.b4a.phone.Phone _p = null;

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
RDebugUtils.currentLine=3801089;
 //BA.debugLineNum = 3801089;BA.debugLine="Dim p As Phone";
_p = new anywheresoftware.b4a.phone.Phone();
RDebugUtils.currentLine=3801090;
 //BA.debugLineNum = 3801090;BA.debugLine="If Not (mqttGetData.IsInitialized) Then";
if (true) break;

case 1:
//if
this.state = 4;
if (anywheresoftware.b4a.keywords.Common.Not(parent._mqttgetdata.IsInitialized /*boolean*/ ())) { 
this.state = 3;
}if (true) break;

case 3:
//C
this.state = 4;
RDebugUtils.currentLine=3801091;
 //BA.debugLineNum = 3801091;BA.debugLine="mqttGetData.Initialize";
parent._mqttgetdata._initialize /*String*/ (null,processBA);
 if (true) break;

case 4:
//C
this.state = -1;
;
RDebugUtils.currentLine=3801094;
 //BA.debugLineNum = 3801094;BA.debugLine="CallSub(Starter, \"SetSubString\")";
anywheresoftware.b4a.keywords.Common.CallSubDebug(processBA,(Object)(parent.mostCurrent._starter.getObject()),"SetSubString");
RDebugUtils.currentLine=3801095;
 //BA.debugLineNum = 3801095;BA.debugLine="mqttGetData.SetSub";
parent._mqttgetdata._setsub /*String*/ (null);
RDebugUtils.currentLine=3801097;
 //BA.debugLineNum = 3801097;BA.debugLine="p.SetScreenOrientation(0)";
_p.SetScreenOrientation(processBA,(int) (0));
RDebugUtils.currentLine=3801098;
 //BA.debugLineNum = 3801098;BA.debugLine="Activity.LoadLayout(\"ServerBoard\")";
parent.mostCurrent._activity.LoadLayout("ServerBoard",mostCurrent.activityBA);
RDebugUtils.currentLine=3801099;
 //BA.debugLineNum = 3801099;BA.debugLine="SetImg";
_setimg();
RDebugUtils.currentLine=3801101;
 //BA.debugLineNum = 3801101;BA.debugLine="dataTmr.Initialize(\"dataTmr\", 1000)";
parent._datatmr.Initialize(processBA,"dataTmr",(long) (1000));
RDebugUtils.currentLine=3801102;
 //BA.debugLineNum = 3801102;BA.debugLine="mqttGetData.Connect";
parent._mqttgetdata._connect /*String*/ (null);
RDebugUtils.currentLine=3801104;
 //BA.debugLineNum = 3801104;BA.debugLine="imgNoData.SetVisibleAnimated(1, True)";
parent.mostCurrent._imgnodata.SetVisibleAnimated((int) (1),anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=3801105;
 //BA.debugLineNum = 3801105;BA.debugLine="lblTafelNaam.Text = Starter.DiscoveredServer";
parent.mostCurrent._lbltafelnaam.setText(BA.ObjectToCharSequence(parent.mostCurrent._starter._discoveredserver /*String*/ ));
RDebugUtils.currentLine=3801107;
 //BA.debugLineNum = 3801107;BA.debugLine="Sleep(1000)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "serverboard", "activity_create"),(int) (1000));
this.state = 5;
return;
case 5:
//C
this.state = -1;
;
RDebugUtils.currentLine=3801108;
 //BA.debugLineNum = 3801108;BA.debugLine="mqttGetData.SendMessage(\"data please\")";
parent._mqttgetdata._sendmessage /*String*/ (null,"data please");
RDebugUtils.currentLine=3801109;
 //BA.debugLineNum = 3801109;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static String  _setimg() throws Exception{
RDebugUtils.currentModule="serverboard";
if (Debug.shouldDelegate(mostCurrent.activityBA, "setimg", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "setimg", null));}
anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _bmp = null;
int _nuleen = 0;
RDebugUtils.currentLine=4390912;
 //BA.debugLineNum = 4390912;BA.debugLine="Private Sub SetImg";
RDebugUtils.currentLine=4390913;
 //BA.debugLineNum = 4390913;BA.debugLine="Dim bmp As Bitmap";
_bmp = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
RDebugUtils.currentLine=4390914;
 //BA.debugLineNum = 4390914;BA.debugLine="Dim nuleen As Int = Rnd(0,2)";
_nuleen = anywheresoftware.b4a.keywords.Common.Rnd((int) (0),(int) (2));
RDebugUtils.currentLine=4390916;
 //BA.debugLineNum = 4390916;BA.debugLine="If nuleen = 0 Then";
if (_nuleen==0) { 
RDebugUtils.currentLine=4390917;
 //BA.debugLineNum = 4390917;BA.debugLine="bmp = LoadBitmapResize(File.DirAssets, \"sven1.jp";
_bmp = anywheresoftware.b4a.keywords.Common.LoadBitmapResize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"sven1.jpg",mostCurrent._imgsponsor.getWidth(),mostCurrent._imgsponsor.getHeight(),anywheresoftware.b4a.keywords.Common.True);
 }else {
RDebugUtils.currentLine=4390919;
 //BA.debugLineNum = 4390919;BA.debugLine="bmp = LoadBitmapResize(File.DirAssets, \"sven_oud";
_bmp = anywheresoftware.b4a.keywords.Common.LoadBitmapResize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"sven_oud.jpg",mostCurrent._imgsponsor.getWidth(),mostCurrent._imgsponsor.getHeight(),anywheresoftware.b4a.keywords.Common.True);
 };
RDebugUtils.currentLine=4390921;
 //BA.debugLineNum = 4390921;BA.debugLine="imgSponsor.SetBackgroundImage(bmp)";
mostCurrent._imgsponsor.SetBackgroundImageNew((android.graphics.Bitmap)(_bmp.getObject()));
RDebugUtils.currentLine=4390922;
 //BA.debugLineNum = 4390922;BA.debugLine="End Sub";
return "";
}
public static boolean  _activity_keypress(int _keycode) throws Exception{
RDebugUtils.currentModule="serverboard";
if (Debug.shouldDelegate(mostCurrent.activityBA, "activity_keypress", false))
	 {return ((Boolean) Debug.delegate(mostCurrent.activityBA, "activity_keypress", new Object[] {_keycode}));}
RDebugUtils.currentLine=4128768;
 //BA.debugLineNum = 4128768;BA.debugLine="Private Sub Activity_KeyPress(KeyCode As Int) As B";
RDebugUtils.currentLine=4128769;
 //BA.debugLineNum = 4128769;BA.debugLine="If KeyCode = KeyCodes.KEYCODE_BACK Then";
if (_keycode==anywheresoftware.b4a.keywords.Common.KeyCodes.KEYCODE_BACK) { 
RDebugUtils.currentLine=4128770;
 //BA.debugLineNum = 4128770;BA.debugLine="DisconnetMqtt";
_disconnetmqtt();
RDebugUtils.currentLine=4128771;
 //BA.debugLineNum = 4128771;BA.debugLine="Return False";
if (true) return anywheresoftware.b4a.keywords.Common.False;
 }else {
RDebugUtils.currentLine=4128773;
 //BA.debugLineNum = 4128773;BA.debugLine="Return True";
if (true) return anywheresoftware.b4a.keywords.Common.True;
 };
RDebugUtils.currentLine=4128775;
 //BA.debugLineNum = 4128775;BA.debugLine="End Sub";
return false;
}
public static String  _disconnetmqtt() throws Exception{
RDebugUtils.currentModule="serverboard";
if (Debug.shouldDelegate(mostCurrent.activityBA, "disconnetmqtt", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "disconnetmqtt", null));}
RDebugUtils.currentLine=4063232;
 //BA.debugLineNum = 4063232;BA.debugLine="Sub DisconnetMqtt";
RDebugUtils.currentLine=4063233;
 //BA.debugLineNum = 4063233;BA.debugLine="If mqttGetData.connected Then";
if (_mqttgetdata._connected /*boolean*/ ) { 
RDebugUtils.currentLine=4063234;
 //BA.debugLineNum = 4063234;BA.debugLine="mqttGetData.Disconnect";
_mqttgetdata._disconnect /*String*/ (null);
 };
RDebugUtils.currentLine=4063236;
 //BA.debugLineNum = 4063236;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
RDebugUtils.currentLine=4063237;
 //BA.debugLineNum = 4063237;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
RDebugUtils.currentModule="serverboard";
RDebugUtils.currentLine=3997696;
 //BA.debugLineNum = 3997696;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
RDebugUtils.currentLine=3997698;
 //BA.debugLineNum = 3997698;BA.debugLine="If mqttGetData.connected Then";
if (_mqttgetdata._connected /*boolean*/ ) { 
RDebugUtils.currentLine=3997699;
 //BA.debugLineNum = 3997699;BA.debugLine="mqttGetData.Disconnect";
_mqttgetdata._disconnect /*String*/ (null);
 };
RDebugUtils.currentLine=3997701;
 //BA.debugLineNum = 3997701;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
RDebugUtils.currentLine=3997702;
 //BA.debugLineNum = 3997702;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
RDebugUtils.currentModule="serverboard";
if (Debug.shouldDelegate(mostCurrent.activityBA, "activity_resume", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "activity_resume", null));}
RDebugUtils.currentLine=3932160;
 //BA.debugLineNum = 3932160;BA.debugLine="Sub Activity_Resume";
RDebugUtils.currentLine=3932161;
 //BA.debugLineNum = 3932161;BA.debugLine="waitText = $\"Wachten op ${Starter.DiscoveredServe";
_waittext = ("Wachten op "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(mostCurrent._starter._discoveredserver /*String*/ ))+"");
RDebugUtils.currentLine=3932162;
 //BA.debugLineNum = 3932162;BA.debugLine="dotCount = 0";
_dotcount = (int) (0);
RDebugUtils.currentLine=3932164;
 //BA.debugLineNum = 3932164;BA.debugLine="End Sub";
return "";
}
public static String  _datatmr_tick() throws Exception{
RDebugUtils.currentModule="serverboard";
if (Debug.shouldDelegate(mostCurrent.activityBA, "datatmr_tick", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "datatmr_tick", null));}
String _dot = "";
int _i = 0;
RDebugUtils.currentLine=3866624;
 //BA.debugLineNum = 3866624;BA.debugLine="Sub dataTmr_Tick";
RDebugUtils.currentLine=3866625;
 //BA.debugLineNum = 3866625;BA.debugLine="Dim dot As String";
_dot = "";
RDebugUtils.currentLine=3866626;
 //BA.debugLineNum = 3866626;BA.debugLine="dotCount=dotCount+1";
_dotcount = (int) (_dotcount+1);
RDebugUtils.currentLine=3866627;
 //BA.debugLineNum = 3866627;BA.debugLine="If dotCount >= 10 Then";
if (_dotcount>=10) { 
RDebugUtils.currentLine=3866628;
 //BA.debugLineNum = 3866628;BA.debugLine="dotCount = 0";
_dotcount = (int) (0);
RDebugUtils.currentLine=3866630;
 //BA.debugLineNum = 3866630;BA.debugLine="Return";
if (true) return "";
 };
RDebugUtils.currentLine=3866632;
 //BA.debugLineNum = 3866632;BA.debugLine="For i = 0 To dotCount";
{
final int step7 = 1;
final int limit7 = _dotcount;
_i = (int) (0) ;
for (;_i <= limit7 ;_i = _i + step7 ) {
RDebugUtils.currentLine=3866633;
 //BA.debugLineNum = 3866633;BA.debugLine="dot = dot &\"*\"";
_dot = _dot+"*";
 }
};
RDebugUtils.currentLine=3866636;
 //BA.debugLineNum = 3866636;BA.debugLine="End Sub";
return "";
}
public static String  _gamedinprogress() throws Exception{
RDebugUtils.currentModule="serverboard";
if (Debug.shouldDelegate(mostCurrent.activityBA, "gamedinprogress", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "gamedinprogress", null));}
RDebugUtils.currentLine=4325376;
 //BA.debugLineNum = 4325376;BA.debugLine="Public Sub GamedInProgress";
RDebugUtils.currentLine=4325379;
 //BA.debugLineNum = 4325379;BA.debugLine="End Sub";
return "";
}
}