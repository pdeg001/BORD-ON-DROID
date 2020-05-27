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
public static String  _gamedended() throws Exception{
RDebugUtils.currentModule="serverboard";
if (Debug.shouldDelegate(mostCurrent.activityBA, "gamedended", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "gamedended", null));}
RDebugUtils.currentLine=26017792;
 //BA.debugLineNum = 26017792;BA.debugLine="Public Sub GamedEnded";
RDebugUtils.currentLine=26017793;
 //BA.debugLineNum = 26017793;BA.debugLine="lblSpelduur.TextColor = Colors.Red";
mostCurrent._lblspelduur.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
RDebugUtils.currentLine=26017794;
 //BA.debugLineNum = 26017794;BA.debugLine="Msgbox2Async(\"Spel beëindigd\", Application.LabelN";
anywheresoftware.b4a.keywords.Common.Msgbox2Async(BA.ObjectToCharSequence("Spel beëindigd"),BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Application.getLabelName()),"OKE","","",anywheresoftware.b4a.keywords.Common.Application.getIcon(),processBA,anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=26017795;
 //BA.debugLineNum = 26017795;BA.debugLine="End Sub";
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
RDebugUtils.currentLine=25952256;
 //BA.debugLineNum = 25952256;BA.debugLine="public Sub UpdateBordWhenClient(data As Message)";
RDebugUtils.currentLine=25952257;
 //BA.debugLineNum = 25952257;BA.debugLine="HideWaitLabel";
_hidewaitlabel();
RDebugUtils.currentLine=25952258;
 //BA.debugLineNum = 25952258;BA.debugLine="lastMessageTime = DateTime.Now";
_lastmessagetime = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
RDebugUtils.currentLine=25952259;
 //BA.debugLineNum = 25952259;BA.debugLine="lblSpelduur.TextColor = Colors.White";
mostCurrent._lblspelduur.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
RDebugUtils.currentLine=25952260;
 //BA.debugLineNum = 25952260;BA.debugLine="Dim Number, str As String";
_number = "";
_str = "";
RDebugUtils.currentLine=25952261;
 //BA.debugLineNum = 25952261;BA.debugLine="str = data.Body";
_str = _data.Body /*String*/ ;
RDebugUtils.currentLine=25952263;
 //BA.debugLineNum = 25952263;BA.debugLine="parser.Initialize(str)";
mostCurrent._parser.Initialize(_str);
RDebugUtils.currentLine=25952264;
 //BA.debugLineNum = 25952264;BA.debugLine="Dim root As Map = parser.NextObject";
_root = new anywheresoftware.b4a.objects.collections.Map();
_root = mostCurrent._parser.NextObject();
RDebugUtils.currentLine=25952265;
 //BA.debugLineNum = 25952265;BA.debugLine="Dim score As Map = root.Get(\"score\")";
_score = new anywheresoftware.b4a.objects.collections.Map();
_score.setObject((anywheresoftware.b4a.objects.collections.Map.MyMap)(_root.Get((Object)("score"))));
RDebugUtils.currentLine=25952266;
 //BA.debugLineNum = 25952266;BA.debugLine="Dim p1 As Map = score.Get(\"p1\")";
_p1 = new anywheresoftware.b4a.objects.collections.Map();
_p1.setObject((anywheresoftware.b4a.objects.collections.Map.MyMap)(_score.Get((Object)("p1"))));
RDebugUtils.currentLine=25952267;
 //BA.debugLineNum = 25952267;BA.debugLine="Dim p2 As Map = score.Get(\"p2\")";
_p2 = new anywheresoftware.b4a.objects.collections.Map();
_p2.setObject((anywheresoftware.b4a.objects.collections.Map.MyMap)(_score.Get((Object)("p2"))));
RDebugUtils.currentLine=25952269;
 //BA.debugLineNum = 25952269;BA.debugLine="Dim aan_stoot As Map = score.Get(\"aan_stoot\")";
_aan_stoot = new anywheresoftware.b4a.objects.collections.Map();
_aan_stoot.setObject((anywheresoftware.b4a.objects.collections.Map.MyMap)(_score.Get((Object)("aan_stoot"))));
RDebugUtils.currentLine=25952270;
 //BA.debugLineNum = 25952270;BA.debugLine="Dim speler As String = aan_stoot.Get(\"speler\")";
_speler = BA.ObjectToString(_aan_stoot.Get((Object)("speler")));
RDebugUtils.currentLine=25952271;
 //BA.debugLineNum = 25952271;BA.debugLine="Dim spelduur As Map = score.Get(\"spelduur\")";
_spelduur = new anywheresoftware.b4a.objects.collections.Map();
_spelduur.setObject((anywheresoftware.b4a.objects.collections.Map.MyMap)(_score.Get((Object)("spelduur"))));
RDebugUtils.currentLine=25952272;
 //BA.debugLineNum = 25952272;BA.debugLine="Dim tijd As String = spelduur.Get(\"tijd\")";
_tijd = BA.ObjectToString(_spelduur.Get((Object)("tijd")));
RDebugUtils.currentLine=25952273;
 //BA.debugLineNum = 25952273;BA.debugLine="Dim beurten As Map = score.Get(\"beurten\")";
_beurten = new anywheresoftware.b4a.objects.collections.Map();
_beurten.setObject((anywheresoftware.b4a.objects.collections.Map.MyMap)(_score.Get((Object)("beurten"))));
RDebugUtils.currentLine=25952274;
 //BA.debugLineNum = 25952274;BA.debugLine="Dim aantal As String = beurten.Get(\"aantal\")";
_aantal = BA.ObjectToString(_beurten.Get((Object)("aantal")));
RDebugUtils.currentLine=25952276;
 //BA.debugLineNum = 25952276;BA.debugLine="lblP1Name.Text = p1.Get(\"naam\")";
mostCurrent._lblp1name.setText(BA.ObjectToCharSequence(_p1.Get((Object)("naam"))));
RDebugUtils.currentLine=25952277;
 //BA.debugLineNum = 25952277;BA.debugLine="Number = p1.Get(\"caram\")";
_number = BA.ObjectToString(_p1.Get((Object)("caram")));
RDebugUtils.currentLine=25952278;
 //BA.debugLineNum = 25952278;BA.debugLine="lblP1100.Text = Number.SubString2(0,1)";
mostCurrent._lblp1100.setText(BA.ObjectToCharSequence(_number.substring((int) (0),(int) (1))));
RDebugUtils.currentLine=25952279;
 //BA.debugLineNum = 25952279;BA.debugLine="lblP110.Text = Number.SubString2(1,2)";
mostCurrent._lblp110.setText(BA.ObjectToCharSequence(_number.substring((int) (1),(int) (2))));
RDebugUtils.currentLine=25952280;
 //BA.debugLineNum = 25952280;BA.debugLine="lblP11.Text = Number.SubString2(2,3)";
mostCurrent._lblp11.setText(BA.ObjectToCharSequence(_number.substring((int) (2),(int) (3))));
RDebugUtils.currentLine=25952281;
 //BA.debugLineNum = 25952281;BA.debugLine="Number = p1.Get(\"maken\")";
_number = BA.ObjectToString(_p1.Get((Object)("maken")));
RDebugUtils.currentLine=25952282;
 //BA.debugLineNum = 25952282;BA.debugLine="lblP1Maken100.Text = Number.SubString2(0,1)";
mostCurrent._lblp1maken100.setText(BA.ObjectToCharSequence(_number.substring((int) (0),(int) (1))));
RDebugUtils.currentLine=25952283;
 //BA.debugLineNum = 25952283;BA.debugLine="lblP1Maken10.Text = Number.SubString2(1,2)";
mostCurrent._lblp1maken10.setText(BA.ObjectToCharSequence(_number.substring((int) (1),(int) (2))));
RDebugUtils.currentLine=25952284;
 //BA.debugLineNum = 25952284;BA.debugLine="lblP1Maken1.Text = Number.SubString2(2,3)";
mostCurrent._lblp1maken1.setText(BA.ObjectToCharSequence(_number.substring((int) (2),(int) (3))));
RDebugUtils.currentLine=25952285;
 //BA.debugLineNum = 25952285;BA.debugLine="lblP1Moy.Text = cs.Initialize.Typeface(Typeface.F";
mostCurrent._lblp1moy.setText(BA.ObjectToCharSequence(_cs.Initialize().Typeface(anywheresoftware.b4a.keywords.Common.Typeface.getFONTAWESOME()).Append(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Chr((int) (0xf201)))).Append(BA.ObjectToCharSequence("  ")).Append(BA.ObjectToCharSequence(_p1.Get((Object)("moyenne")))).PopAll().getObject()));
RDebugUtils.currentLine=25952287;
 //BA.debugLineNum = 25952287;BA.debugLine="lblP2Name.Text = p2.Get(\"naam\")";
mostCurrent._lblp2name.setText(BA.ObjectToCharSequence(_p2.Get((Object)("naam"))));
RDebugUtils.currentLine=25952288;
 //BA.debugLineNum = 25952288;BA.debugLine="Number = p2.Get(\"caram\")";
_number = BA.ObjectToString(_p2.Get((Object)("caram")));
RDebugUtils.currentLine=25952289;
 //BA.debugLineNum = 25952289;BA.debugLine="lblP2100.Text = Number.SubString2(0,1)";
mostCurrent._lblp2100.setText(BA.ObjectToCharSequence(_number.substring((int) (0),(int) (1))));
RDebugUtils.currentLine=25952290;
 //BA.debugLineNum = 25952290;BA.debugLine="lblP210.Text = Number.SubString2(1,2)";
mostCurrent._lblp210.setText(BA.ObjectToCharSequence(_number.substring((int) (1),(int) (2))));
RDebugUtils.currentLine=25952291;
 //BA.debugLineNum = 25952291;BA.debugLine="lblP21.Text = Number.SubString2(2,3)";
mostCurrent._lblp21.setText(BA.ObjectToCharSequence(_number.substring((int) (2),(int) (3))));
RDebugUtils.currentLine=25952292;
 //BA.debugLineNum = 25952292;BA.debugLine="Number = p2.Get(\"maken\")";
_number = BA.ObjectToString(_p2.Get((Object)("maken")));
RDebugUtils.currentLine=25952293;
 //BA.debugLineNum = 25952293;BA.debugLine="lblP2Maken100.Text = Number.SubString2(0,1)";
mostCurrent._lblp2maken100.setText(BA.ObjectToCharSequence(_number.substring((int) (0),(int) (1))));
RDebugUtils.currentLine=25952294;
 //BA.debugLineNum = 25952294;BA.debugLine="lblP2Maken10.Text = Number.SubString2(1,2)";
mostCurrent._lblp2maken10.setText(BA.ObjectToCharSequence(_number.substring((int) (1),(int) (2))));
RDebugUtils.currentLine=25952295;
 //BA.debugLineNum = 25952295;BA.debugLine="lblP2Maken1.Text = Number.SubString2(2,3)";
mostCurrent._lblp2maken1.setText(BA.ObjectToCharSequence(_number.substring((int) (2),(int) (3))));
RDebugUtils.currentLine=25952297;
 //BA.debugLineNum = 25952297;BA.debugLine="cs.Initialize.Append(\"\").Typeface(Typeface.FONTAW";
_cs.Initialize().Append(BA.ObjectToCharSequence("")).Typeface(anywheresoftware.b4a.keywords.Common.Typeface.getFONTAWESOME()).Append(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Chr((int) (0xf201)))).PopAll();
RDebugUtils.currentLine=25952298;
 //BA.debugLineNum = 25952298;BA.debugLine="lblP2Moy.Text = cs.Initialize.Typeface(Typeface.F";
mostCurrent._lblp2moy.setText(BA.ObjectToCharSequence(_cs.Initialize().Typeface(anywheresoftware.b4a.keywords.Common.Typeface.getFONTAWESOME()).Append(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Chr((int) (0xf201)))).Append(BA.ObjectToCharSequence("  ")).Append(BA.ObjectToCharSequence(_p2.Get((Object)("moyenne")))).PopAll().getObject()));
RDebugUtils.currentLine=25952300;
 //BA.debugLineNum = 25952300;BA.debugLine="lblBeurt100.Text = aantal.SubString2(0,1)";
mostCurrent._lblbeurt100.setText(BA.ObjectToCharSequence(_aantal.substring((int) (0),(int) (1))));
RDebugUtils.currentLine=25952301;
 //BA.debugLineNum = 25952301;BA.debugLine="lblBeurt10.Text = aantal.SubString2(1,2)";
mostCurrent._lblbeurt10.setText(BA.ObjectToCharSequence(_aantal.substring((int) (1),(int) (2))));
RDebugUtils.currentLine=25952302;
 //BA.debugLineNum = 25952302;BA.debugLine="lblBeurt1.Text = aantal.SubString2(2,3)";
mostCurrent._lblbeurt1.setText(BA.ObjectToCharSequence(_aantal.substring((int) (2),(int) (3))));
RDebugUtils.currentLine=25952303;
 //BA.debugLineNum = 25952303;BA.debugLine="lblSpelduur.Text = tijd'score.Get(\"spelduur\")";
mostCurrent._lblspelduur.setText(BA.ObjectToCharSequence(_tijd));
RDebugUtils.currentLine=25952304;
 //BA.debugLineNum = 25952304;BA.debugLine="lblSpelduur.Text = cs.Initialize.Typeface(Typefac";
mostCurrent._lblspelduur.setText(BA.ObjectToCharSequence(_cs.Initialize().Typeface(anywheresoftware.b4a.keywords.Common.Typeface.getFONTAWESOME()).Append(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.Chr((int) (0xf253)))).Append(BA.ObjectToCharSequence("  ")).Append(BA.ObjectToCharSequence(_tijd)).PopAll().getObject()));
RDebugUtils.currentLine=25952306;
 //BA.debugLineNum = 25952306;BA.debugLine="imgP1Play.Visible = False";
mostCurrent._imgp1play.setVisible(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=25952307;
 //BA.debugLineNum = 25952307;BA.debugLine="imgP2Play.Visible = False";
mostCurrent._imgp2play.setVisible(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=25952308;
 //BA.debugLineNum = 25952308;BA.debugLine="If speler = 1 Then";
if ((_speler).equals(BA.NumberToString(1))) { 
RDebugUtils.currentLine=25952309;
 //BA.debugLineNum = 25952309;BA.debugLine="imgP1Play.Visible = True";
mostCurrent._imgp1play.setVisible(anywheresoftware.b4a.keywords.Common.True);
 }else {
RDebugUtils.currentLine=25952311;
 //BA.debugLineNum = 25952311;BA.debugLine="imgP2Play.Visible = True";
mostCurrent._imgp2play.setVisible(anywheresoftware.b4a.keywords.Common.True);
 };
RDebugUtils.currentLine=25952313;
 //BA.debugLineNum = 25952313;BA.debugLine="End Sub";
return "";
}
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
RDebugUtils.currentLine=25493505;
 //BA.debugLineNum = 25493505;BA.debugLine="baseFile.createCustomToast(\"Verbinding met bord v";
parent._basefile._createcustomtoast /*String*/ (null,"Verbinding met bord verloren",BA.NumberToString(anywheresoftware.b4a.keywords.Common.Colors.Red));
RDebugUtils.currentLine=25493506;
 //BA.debugLineNum = 25493506;BA.debugLine="Sleep(2000)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "serverboard", "connectionlost"),(int) (2000));
this.state = 1;
return;
case 1:
//C
this.state = -1;
;
RDebugUtils.currentLine=25493507;
 //BA.debugLineNum = 25493507;BA.debugLine="lastMessageTimer.Enabled = False";
parent._lastmessagetimer.setEnabled(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=25493508;
 //BA.debugLineNum = 25493508;BA.debugLine="DisconnetMqtt";
_disconnetmqtt();
RDebugUtils.currentLine=25493509;
 //BA.debugLineNum = 25493509;BA.debugLine="End Sub";
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
RDebugUtils.currentLine=25427969;
 //BA.debugLineNum = 25427969;BA.debugLine="Starter.mainPaused = False";
parent.mostCurrent._starter._mainpaused /*boolean*/  = anywheresoftware.b4a.keywords.Common.False;
RDebugUtils.currentLine=25427970;
 //BA.debugLineNum = 25427970;BA.debugLine="If Not (mqttBase.IsInitialized) Then";
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
RDebugUtils.currentLine=25427971;
 //BA.debugLineNum = 25427971;BA.debugLine="mqttBase.Initialize";
parent._mqttbase._initialize /*String*/ (null,processBA);
 if (true) break;

case 4:
//C
this.state = -1;
;
RDebugUtils.currentLine=25427973;
 //BA.debugLineNum = 25427973;BA.debugLine="baseFile.Initialize";
parent._basefile._initialize /*String*/ (null,processBA);
RDebugUtils.currentLine=25427974;
 //BA.debugLineNum = 25427974;BA.debugLine="CallSub(Starter, \"SetSubString\")";
anywheresoftware.b4a.keywords.Common.CallSubDebug(processBA,(Object)(parent.mostCurrent._starter.getObject()),"SetSubString");
RDebugUtils.currentLine=25427976;
 //BA.debugLineNum = 25427976;BA.debugLine="Activity.LoadLayout(\"ServerBoard\")";
parent.mostCurrent._activity.LoadLayout("ServerBoard",mostCurrent.activityBA);
RDebugUtils.currentLine=25427977;
 //BA.debugLineNum = 25427977;BA.debugLine="lastMessageTime = DateTime.Now";
parent._lastmessagetime = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
RDebugUtils.currentLine=25427978;
 //BA.debugLineNum = 25427978;BA.debugLine="SetImgSponsor";
_setimgsponsor();
RDebugUtils.currentLine=25427979;
 //BA.debugLineNum = 25427979;BA.debugLine="lastMessageTimer.Initialize(\"tmrLastMessase\", 120";
parent._lastmessagetimer.Initialize(processBA,"tmrLastMessase",(long) (120*1000));
RDebugUtils.currentLine=25427980;
 //BA.debugLineNum = 25427980;BA.debugLine="lastMessageTimer.Enabled = True";
parent._lastmessagetimer.setEnabled(anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=25427983;
 //BA.debugLineNum = 25427983;BA.debugLine="mqttBase.Connect";
parent._mqttbase._connect /*String*/ (null);
RDebugUtils.currentLine=25427985;
 //BA.debugLineNum = 25427985;BA.debugLine="lblTafelNaam.Text = Starter.DiscoveredServer";
parent.mostCurrent._lbltafelnaam.setText(BA.ObjectToCharSequence(parent.mostCurrent._starter._discoveredserver /*String*/ ));
RDebugUtils.currentLine=25427987;
 //BA.debugLineNum = 25427987;BA.debugLine="Sleep(1000)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "serverboard", "activity_create"),(int) (1000));
this.state = 5;
return;
case 5:
//C
this.state = -1;
;
RDebugUtils.currentLine=25427988;
 //BA.debugLineNum = 25427988;BA.debugLine="mqttBase.SendMessage(\"data please\")";
parent._mqttbase._sendmessage /*String*/ (null,"data please");
RDebugUtils.currentLine=25427989;
 //BA.debugLineNum = 25427989;BA.debugLine="End Sub";
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
RDebugUtils.currentLine=26083328;
 //BA.debugLineNum = 26083328;BA.debugLine="Private Sub SetImgSponsor";
RDebugUtils.currentLine=26083329;
 //BA.debugLineNum = 26083329;BA.debugLine="Dim bmp As Bitmap";
_bmp = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
RDebugUtils.currentLine=26083330;
 //BA.debugLineNum = 26083330;BA.debugLine="Dim bmpName As String";
_bmpname = "";
RDebugUtils.currentLine=26083331;
 //BA.debugLineNum = 26083331;BA.debugLine="Dim nuleen As Int = Rnd(0,3)";
_nuleen = anywheresoftware.b4a.keywords.Common.Rnd((int) (0),(int) (3));
RDebugUtils.currentLine=26083333;
 //BA.debugLineNum = 26083333;BA.debugLine="If nuleen = 0 Then";
if (_nuleen==0) { 
RDebugUtils.currentLine=26083334;
 //BA.debugLineNum = 26083334;BA.debugLine="bmpName = \"sven1.jpg\"";
_bmpname = "sven1.jpg";
 };
RDebugUtils.currentLine=26083336;
 //BA.debugLineNum = 26083336;BA.debugLine="If nuleen = 1 Then";
if (_nuleen==1) { 
RDebugUtils.currentLine=26083337;
 //BA.debugLineNum = 26083337;BA.debugLine="bmpName = \"sven_oud.jpg\"";
_bmpname = "sven_oud.jpg";
 };
RDebugUtils.currentLine=26083339;
 //BA.debugLineNum = 26083339;BA.debugLine="If nuleen = 2 Then";
if (_nuleen==2) { 
RDebugUtils.currentLine=26083340;
 //BA.debugLineNum = 26083340;BA.debugLine="bmpName = \"biljarter.png\"";
_bmpname = "biljarter.png";
 };
RDebugUtils.currentLine=26083343;
 //BA.debugLineNum = 26083343;BA.debugLine="bmp = LoadBitmapResize(File.DirAssets, bmpName, i";
_bmp = anywheresoftware.b4a.keywords.Common.LoadBitmapResize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),_bmpname,mostCurrent._imgsponsor.getWidth(),mostCurrent._imgsponsor.getHeight(),anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=26083344;
 //BA.debugLineNum = 26083344;BA.debugLine="imgSponsor.SetBackgroundImage(bmp)";
mostCurrent._imgsponsor.SetBackgroundImageNew((android.graphics.Bitmap)(_bmp.getObject()));
RDebugUtils.currentLine=26083345;
 //BA.debugLineNum = 26083345;BA.debugLine="End Sub";
return "";
}
public static boolean  _activity_keypress(int _keycode) throws Exception{
RDebugUtils.currentModule="serverboard";
if (Debug.shouldDelegate(mostCurrent.activityBA, "activity_keypress", false))
	 {return ((Boolean) Debug.delegate(mostCurrent.activityBA, "activity_keypress", new Object[] {_keycode}));}
RDebugUtils.currentLine=25886720;
 //BA.debugLineNum = 25886720;BA.debugLine="Private Sub Activity_KeyPress(KeyCode As Int) As B";
RDebugUtils.currentLine=25886721;
 //BA.debugLineNum = 25886721;BA.debugLine="If KeyCode = KeyCodes.KEYCODE_BACK Then";
if (_keycode==anywheresoftware.b4a.keywords.Common.KeyCodes.KEYCODE_BACK) { 
RDebugUtils.currentLine=25886722;
 //BA.debugLineNum = 25886722;BA.debugLine="DisconnetMqtt";
_disconnetmqtt();
RDebugUtils.currentLine=25886723;
 //BA.debugLineNum = 25886723;BA.debugLine="CallSubDelayed(Main, \"setBordLastAliveTimer\")";
anywheresoftware.b4a.keywords.Common.CallSubDelayed(processBA,(Object)(mostCurrent._main.getObject()),"setBordLastAliveTimer");
RDebugUtils.currentLine=25886724;
 //BA.debugLineNum = 25886724;BA.debugLine="lastMessageTimer.Enabled = False";
_lastmessagetimer.setEnabled(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=25886725;
 //BA.debugLineNum = 25886725;BA.debugLine="CallSubDelayed(Main, \"ReconnectToLocation\")";
anywheresoftware.b4a.keywords.Common.CallSubDelayed(processBA,(Object)(mostCurrent._main.getObject()),"ReconnectToLocation");
RDebugUtils.currentLine=25886726;
 //BA.debugLineNum = 25886726;BA.debugLine="Return False";
if (true) return anywheresoftware.b4a.keywords.Common.False;
 }else {
RDebugUtils.currentLine=25886728;
 //BA.debugLineNum = 25886728;BA.debugLine="Return True";
if (true) return anywheresoftware.b4a.keywords.Common.True;
 };
RDebugUtils.currentLine=25886730;
 //BA.debugLineNum = 25886730;BA.debugLine="End Sub";
return false;
}
public static String  _disconnetmqtt() throws Exception{
RDebugUtils.currentModule="serverboard";
if (Debug.shouldDelegate(mostCurrent.activityBA, "disconnetmqtt", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "disconnetmqtt", null));}
RDebugUtils.currentLine=25821184;
 //BA.debugLineNum = 25821184;BA.debugLine="Sub DisconnetMqtt";
RDebugUtils.currentLine=25821185;
 //BA.debugLineNum = 25821185;BA.debugLine="If mqttBase.connected Then";
if (_mqttbase._connected /*boolean*/ ) { 
RDebugUtils.currentLine=25821186;
 //BA.debugLineNum = 25821186;BA.debugLine="mqttBase.Disconnect";
_mqttbase._disconnect /*String*/ (null);
 };
RDebugUtils.currentLine=25821188;
 //BA.debugLineNum = 25821188;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
RDebugUtils.currentLine=25821189;
 //BA.debugLineNum = 25821189;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
RDebugUtils.currentModule="serverboard";
RDebugUtils.currentLine=25690112;
 //BA.debugLineNum = 25690112;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
RDebugUtils.currentLine=25690113;
 //BA.debugLineNum = 25690113;BA.debugLine="ResumeConnection(False)";
_resumeconnection(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=25690114;
 //BA.debugLineNum = 25690114;BA.debugLine="End Sub";
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
RDebugUtils.currentLine=25755649;
 //BA.debugLineNum = 25755649;BA.debugLine="If resume Then";
if (true) break;

case 1:
//if
this.state = 12;
if (_resume) { 
this.state = 3;
}else {
this.state = 11;
}if (true) break;

case 3:
//C
this.state = 4;
RDebugUtils.currentLine=25755650;
 //BA.debugLineNum = 25755650;BA.debugLine="If mqttBase.GetClientConnected Then Return";
if (true) break;

case 4:
//if
this.state = 9;
if (parent._mqttbase._getclientconnected /*boolean*/ (null)) { 
this.state = 6;
;}if (true) break;

case 6:
//C
this.state = 9;
if (true) return ;
if (true) break;

case 9:
//C
this.state = 12;
;
RDebugUtils.currentLine=25755651;
 //BA.debugLineNum = 25755651;BA.debugLine="lastMessageTime = DateTime.Now";
parent._lastmessagetime = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
RDebugUtils.currentLine=25755652;
 //BA.debugLineNum = 25755652;BA.debugLine="mqttBase.Connect";
parent._mqttbase._connect /*String*/ (null);
RDebugUtils.currentLine=25755653;
 //BA.debugLineNum = 25755653;BA.debugLine="Sleep(500)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "serverboard", "resumeconnection"),(int) (500));
this.state = 13;
return;
case 13:
//C
this.state = 12;
;
RDebugUtils.currentLine=25755654;
 //BA.debugLineNum = 25755654;BA.debugLine="mqttBase.SendMessage(\"data please\")";
parent._mqttbase._sendmessage /*String*/ (null,"data please");
RDebugUtils.currentLine=25755655;
 //BA.debugLineNum = 25755655;BA.debugLine="lastMessageTimer.Initialize(\"tmrLastMessase\", 12";
parent._lastmessagetimer.Initialize(processBA,"tmrLastMessase",(long) (120*1000));
 if (true) break;

case 11:
//C
this.state = 12;
RDebugUtils.currentLine=25755657;
 //BA.debugLineNum = 25755657;BA.debugLine="mqttBase .Disconnect";
parent._mqttbase._disconnect /*String*/ (null);
 if (true) break;

case 12:
//C
this.state = -1;
;
RDebugUtils.currentLine=25755660;
 //BA.debugLineNum = 25755660;BA.debugLine="lastMessageTimer.Enabled = resume";
parent._lastmessagetimer.setEnabled(_resume);
RDebugUtils.currentLine=25755661;
 //BA.debugLineNum = 25755661;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static String  _activity_resume() throws Exception{
RDebugUtils.currentModule="serverboard";
if (Debug.shouldDelegate(mostCurrent.activityBA, "activity_resume", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "activity_resume", null));}
RDebugUtils.currentLine=25624576;
 //BA.debugLineNum = 25624576;BA.debugLine="Sub Activity_Resume";
RDebugUtils.currentLine=25624577;
 //BA.debugLineNum = 25624577;BA.debugLine="ResumeConnection(True)";
_resumeconnection(anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=25624578;
 //BA.debugLineNum = 25624578;BA.debugLine="End Sub";
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
RDebugUtils.currentLine=26148865;
 //BA.debugLineNum = 26148865;BA.debugLine="If imgNoData.Visible Then";
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
RDebugUtils.currentLine=26148866;
 //BA.debugLineNum = 26148866;BA.debugLine="imgNoData.SetVisibleAnimated(0, False)";
parent.mostCurrent._imgnodata.SetVisibleAnimated((int) (0),anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=26148867;
 //BA.debugLineNum = 26148867;BA.debugLine="Sleep(300)";
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
RDebugUtils.currentLine=26148869;
 //BA.debugLineNum = 26148869;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static String  _tmrlastmessase_tick() throws Exception{
RDebugUtils.currentModule="serverboard";
if (Debug.shouldDelegate(mostCurrent.activityBA, "tmrlastmessase_tick", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "tmrlastmessase_tick", null));}
RDebugUtils.currentLine=25559040;
 //BA.debugLineNum = 25559040;BA.debugLine="Sub tmrLastMessase_Tick";
RDebugUtils.currentLine=25559041;
 //BA.debugLineNum = 25559041;BA.debugLine="If (DateTime.Now-lastMessageTime) >= 120*1000 The";
if ((anywheresoftware.b4a.keywords.Common.DateTime.getNow()-_lastmessagetime)>=120*1000) { 
RDebugUtils.currentLine=25559042;
 //BA.debugLineNum = 25559042;BA.debugLine="mqttBase.SendMessage(\"data please\")";
_mqttbase._sendmessage /*String*/ (null,"data please");
RDebugUtils.currentLine=25559043;
 //BA.debugLineNum = 25559043;BA.debugLine="lblSpelduur.TextColor = Colors.Red";
mostCurrent._lblspelduur.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 };
RDebugUtils.currentLine=25559045;
 //BA.debugLineNum = 25559045;BA.debugLine="End Sub";
return "";
}
}