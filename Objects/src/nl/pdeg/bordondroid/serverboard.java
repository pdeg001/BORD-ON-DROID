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
public anywheresoftware.b4a.objects.LabelWrapper[] _lbllist = null;
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
public static boolean _waitingfordata = false;
public anywheresoftware.b4a.objects.ImageViewWrapper _imgnodata = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblnodata = null;
public nl.pdeg.bordondroid.main _main = null;
public nl.pdeg.bordondroid.starter _starter = null;
public nl.pdeg.bordondroid.chat _chat = null;
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
String _moyenne = "";
anywheresoftware.b4a.objects.collections.Map _aan_stoot = null;
String _speler = "";
anywheresoftware.b4a.objects.collections.Map _spelduur = null;
String _tijd = "";
anywheresoftware.b4a.objects.collections.Map _beurten = null;
String _aantal = "";
RDebugUtils.currentLine=3407872;
 //BA.debugLineNum = 3407872;BA.debugLine="public Sub UpdateBordWhenClient(data As Message)";
RDebugUtils.currentLine=3407873;
 //BA.debugLineNum = 3407873;BA.debugLine="dataTmr.Enabled = False";
_datatmr.setEnabled(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=3407874;
 //BA.debugLineNum = 3407874;BA.debugLine="imgNoData.SetVisibleAnimated(1000, False)";
mostCurrent._imgnodata.SetVisibleAnimated((int) (1000),anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=3407877;
 //BA.debugLineNum = 3407877;BA.debugLine="lblNoData.Text = $\"U kijkt naar ${Starter.selecte";
mostCurrent._lblnodata.setText(BA.ObjectToCharSequence(("U kijkt naar "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(mostCurrent._starter._selectedbordname /*String*/ ))+"")));
RDebugUtils.currentLine=3407878;
 //BA.debugLineNum = 3407878;BA.debugLine="lblNoData.TextColor = Colors.White";
mostCurrent._lblnodata.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
RDebugUtils.currentLine=3407879;
 //BA.debugLineNum = 3407879;BA.debugLine="lblNoData.Visible = True";
mostCurrent._lblnodata.setVisible(anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=3407881;
 //BA.debugLineNum = 3407881;BA.debugLine="Dim Number, str As String";
_number = "";
_str = "";
RDebugUtils.currentLine=3407882;
 //BA.debugLineNum = 3407882;BA.debugLine="str = data.Body";
_str = _data.Body /*String*/ ;
RDebugUtils.currentLine=3407884;
 //BA.debugLineNum = 3407884;BA.debugLine="parser.Initialize(str)";
mostCurrent._parser.Initialize(_str);
RDebugUtils.currentLine=3407885;
 //BA.debugLineNum = 3407885;BA.debugLine="Dim root As Map = parser.NextObject";
_root = new anywheresoftware.b4a.objects.collections.Map();
_root = mostCurrent._parser.NextObject();
RDebugUtils.currentLine=3407886;
 //BA.debugLineNum = 3407886;BA.debugLine="Dim score As Map = root.Get(\"score\")";
_score = new anywheresoftware.b4a.objects.collections.Map();
_score.setObject((anywheresoftware.b4a.objects.collections.Map.MyMap)(_root.Get((Object)("score"))));
RDebugUtils.currentLine=3407887;
 //BA.debugLineNum = 3407887;BA.debugLine="Dim p1 As Map = score.Get(\"p1\")";
_p1 = new anywheresoftware.b4a.objects.collections.Map();
_p1.setObject((anywheresoftware.b4a.objects.collections.Map.MyMap)(_score.Get((Object)("p1"))));
RDebugUtils.currentLine=3407893;
 //BA.debugLineNum = 3407893;BA.debugLine="Dim p2 As Map = score.Get(\"p2\")";
_p2 = new anywheresoftware.b4a.objects.collections.Map();
_p2.setObject((anywheresoftware.b4a.objects.collections.Map.MyMap)(_score.Get((Object)("p2"))));
RDebugUtils.currentLine=3407898;
 //BA.debugLineNum = 3407898;BA.debugLine="Dim moyenne As String = p2.Get(\"moyenne\")";
_moyenne = BA.ObjectToString(_p2.Get((Object)("moyenne")));
RDebugUtils.currentLine=3407899;
 //BA.debugLineNum = 3407899;BA.debugLine="Dim aan_stoot As Map = score.Get(\"aan_stoot\")";
_aan_stoot = new anywheresoftware.b4a.objects.collections.Map();
_aan_stoot.setObject((anywheresoftware.b4a.objects.collections.Map.MyMap)(_score.Get((Object)("aan_stoot"))));
RDebugUtils.currentLine=3407900;
 //BA.debugLineNum = 3407900;BA.debugLine="Dim speler As String = aan_stoot.Get(\"speler\")";
_speler = BA.ObjectToString(_aan_stoot.Get((Object)("speler")));
RDebugUtils.currentLine=3407901;
 //BA.debugLineNum = 3407901;BA.debugLine="Dim spelduur As Map = score.Get(\"spelduur\")";
_spelduur = new anywheresoftware.b4a.objects.collections.Map();
_spelduur.setObject((anywheresoftware.b4a.objects.collections.Map.MyMap)(_score.Get((Object)("spelduur"))));
RDebugUtils.currentLine=3407902;
 //BA.debugLineNum = 3407902;BA.debugLine="Dim tijd As String = spelduur.Get(\"tijd\")";
_tijd = BA.ObjectToString(_spelduur.Get((Object)("tijd")));
RDebugUtils.currentLine=3407905;
 //BA.debugLineNum = 3407905;BA.debugLine="Dim beurten As Map = score.Get(\"beurten\")";
_beurten = new anywheresoftware.b4a.objects.collections.Map();
_beurten.setObject((anywheresoftware.b4a.objects.collections.Map.MyMap)(_score.Get((Object)("beurten"))));
RDebugUtils.currentLine=3407906;
 //BA.debugLineNum = 3407906;BA.debugLine="Dim aantal As String = beurten.Get(\"aantal\")";
_aantal = BA.ObjectToString(_beurten.Get((Object)("aantal")));
RDebugUtils.currentLine=3407911;
 //BA.debugLineNum = 3407911;BA.debugLine="Number = p1.Get(\"caram\")";
_number = BA.ObjectToString(_p1.Get((Object)("caram")));
RDebugUtils.currentLine=3407912;
 //BA.debugLineNum = 3407912;BA.debugLine="lblP1100.Text = Number.SubString2(0,1)";
mostCurrent._lblp1100.setText(BA.ObjectToCharSequence(_number.substring((int) (0),(int) (1))));
RDebugUtils.currentLine=3407913;
 //BA.debugLineNum = 3407913;BA.debugLine="lblP110.Text = Number.SubString2(1,2)";
mostCurrent._lblp110.setText(BA.ObjectToCharSequence(_number.substring((int) (1),(int) (2))));
RDebugUtils.currentLine=3407914;
 //BA.debugLineNum = 3407914;BA.debugLine="lblP11.Text = Number.SubString2(2,3)";
mostCurrent._lblp11.setText(BA.ObjectToCharSequence(_number.substring((int) (2),(int) (3))));
RDebugUtils.currentLine=3407915;
 //BA.debugLineNum = 3407915;BA.debugLine="Number = p1.Get(\"maken\")";
_number = BA.ObjectToString(_p1.Get((Object)("maken")));
RDebugUtils.currentLine=3407916;
 //BA.debugLineNum = 3407916;BA.debugLine="lblP1Maken100.Text = Number.SubString2(0,1)";
mostCurrent._lblp1maken100.setText(BA.ObjectToCharSequence(_number.substring((int) (0),(int) (1))));
RDebugUtils.currentLine=3407917;
 //BA.debugLineNum = 3407917;BA.debugLine="lblP1Maken10.Text = Number.SubString2(1,2)";
mostCurrent._lblp1maken10.setText(BA.ObjectToCharSequence(_number.substring((int) (1),(int) (2))));
RDebugUtils.currentLine=3407918;
 //BA.debugLineNum = 3407918;BA.debugLine="lblP1Maken1.Text = Number.SubString2(2,3)";
mostCurrent._lblp1maken1.setText(BA.ObjectToCharSequence(_number.substring((int) (2),(int) (3))));
RDebugUtils.currentLine=3407919;
 //BA.debugLineNum = 3407919;BA.debugLine="lblP1Moy.Text = p1.Get(\"moyenne\")";
mostCurrent._lblp1moy.setText(BA.ObjectToCharSequence(_p1.Get((Object)("moyenne"))));
RDebugUtils.currentLine=3407926;
 //BA.debugLineNum = 3407926;BA.debugLine="Number = p2.Get(\"caram\")";
_number = BA.ObjectToString(_p2.Get((Object)("caram")));
RDebugUtils.currentLine=3407927;
 //BA.debugLineNum = 3407927;BA.debugLine="lblP2100.Text = Number.SubString2(0,1)";
mostCurrent._lblp2100.setText(BA.ObjectToCharSequence(_number.substring((int) (0),(int) (1))));
RDebugUtils.currentLine=3407928;
 //BA.debugLineNum = 3407928;BA.debugLine="lblP210.Text = Number.SubString2(1,2)";
mostCurrent._lblp210.setText(BA.ObjectToCharSequence(_number.substring((int) (1),(int) (2))));
RDebugUtils.currentLine=3407929;
 //BA.debugLineNum = 3407929;BA.debugLine="lblP21.Text = Number.SubString2(2,3)";
mostCurrent._lblp21.setText(BA.ObjectToCharSequence(_number.substring((int) (2),(int) (3))));
RDebugUtils.currentLine=3407930;
 //BA.debugLineNum = 3407930;BA.debugLine="Number = p2.Get(\"maken\")";
_number = BA.ObjectToString(_p2.Get((Object)("maken")));
RDebugUtils.currentLine=3407931;
 //BA.debugLineNum = 3407931;BA.debugLine="lblP2Maken100.Text = Number.SubString2(0,1)";
mostCurrent._lblp2maken100.setText(BA.ObjectToCharSequence(_number.substring((int) (0),(int) (1))));
RDebugUtils.currentLine=3407932;
 //BA.debugLineNum = 3407932;BA.debugLine="lblP2Maken10.Text = Number.SubString2(1,2)";
mostCurrent._lblp2maken10.setText(BA.ObjectToCharSequence(_number.substring((int) (1),(int) (2))));
RDebugUtils.currentLine=3407933;
 //BA.debugLineNum = 3407933;BA.debugLine="lblP2Maken1.Text = Number.SubString2(2,3)";
mostCurrent._lblp2maken1.setText(BA.ObjectToCharSequence(_number.substring((int) (2),(int) (3))));
RDebugUtils.currentLine=3407934;
 //BA.debugLineNum = 3407934;BA.debugLine="lblP2Moy.Text = p2.Get(\"moyenne\")";
mostCurrent._lblp2moy.setText(BA.ObjectToCharSequence(_p2.Get((Object)("moyenne"))));
RDebugUtils.currentLine=3407937;
 //BA.debugLineNum = 3407937;BA.debugLine="lblBeurt100.Text = aantal.SubString2(0,1)";
mostCurrent._lblbeurt100.setText(BA.ObjectToCharSequence(_aantal.substring((int) (0),(int) (1))));
RDebugUtils.currentLine=3407938;
 //BA.debugLineNum = 3407938;BA.debugLine="lblBeurt10.Text = aantal.SubString2(1,2)";
mostCurrent._lblbeurt10.setText(BA.ObjectToCharSequence(_aantal.substring((int) (1),(int) (2))));
RDebugUtils.currentLine=3407939;
 //BA.debugLineNum = 3407939;BA.debugLine="lblBeurt1.Text = aantal.SubString2(2,3)";
mostCurrent._lblbeurt1.setText(BA.ObjectToCharSequence(_aantal.substring((int) (2),(int) (3))));
RDebugUtils.currentLine=3407945;
 //BA.debugLineNum = 3407945;BA.debugLine="imgP1Play.Visible = False";
mostCurrent._imgp1play.setVisible(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=3407946;
 //BA.debugLineNum = 3407946;BA.debugLine="imgP2Play.Visible = False";
mostCurrent._imgp2play.setVisible(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=3407947;
 //BA.debugLineNum = 3407947;BA.debugLine="If speler = 1 Then";
if ((_speler).equals(BA.NumberToString(1))) { 
RDebugUtils.currentLine=3407948;
 //BA.debugLineNum = 3407948;BA.debugLine="imgP1Play.Visible = True";
mostCurrent._imgp1play.setVisible(anywheresoftware.b4a.keywords.Common.True);
 }else {
RDebugUtils.currentLine=3407950;
 //BA.debugLineNum = 3407950;BA.debugLine="imgP2Play.Visible = True";
mostCurrent._imgp2play.setVisible(anywheresoftware.b4a.keywords.Common.True);
 };
RDebugUtils.currentLine=3407952;
 //BA.debugLineNum = 3407952;BA.debugLine="End Sub";
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
int _result = 0;

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
RDebugUtils.currentLine=3473409;
 //BA.debugLineNum = 3473409;BA.debugLine="Msgbox2Async($\"Verbinding met ${Starter.selectedB";
anywheresoftware.b4a.keywords.Common.Msgbox2Async(BA.ObjectToCharSequence(("Verbinding met "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(parent.mostCurrent._starter._selectedbordname /*String*/ ))+" verloren"+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(anywheresoftware.b4a.keywords.Common.CRLF))+"Probeer het later nog eens")),BA.ObjectToCharSequence("Bord Op Droid"),"OKE","","",(anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper(), (android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null)),processBA,anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=3473410;
 //BA.debugLineNum = 3473410;BA.debugLine="Wait For Msgbox_Result (Result As Int)";
anywheresoftware.b4a.keywords.Common.WaitFor("msgbox_result", processBA, new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "serverboard", "connectionlost"), null);
this.state = 5;
return;
case 5:
//C
this.state = 1;
_result = (Integer) result[0];
;
RDebugUtils.currentLine=3473411;
 //BA.debugLineNum = 3473411;BA.debugLine="If DialogResponse.POSITIVE Then";
if (true) break;

case 1:
//if
this.state = 4;
if (BA.ObjectToBoolean(anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE)) { 
this.state = 3;
}if (true) break;

case 3:
//C
this.state = 4;
RDebugUtils.currentLine=3473412;
 //BA.debugLineNum = 3473412;BA.debugLine="Starter.Disconnect";
parent.mostCurrent._starter._disconnect /*String*/ ();
RDebugUtils.currentLine=3473413;
 //BA.debugLineNum = 3473413;BA.debugLine="Activity.Finish";
parent.mostCurrent._activity.Finish();
 if (true) break;

case 4:
//C
this.state = -1;
;
RDebugUtils.currentLine=3473416;
 //BA.debugLineNum = 3473416;BA.debugLine="End Sub";
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
this.state = -1;
RDebugUtils.currentLine=3080193;
 //BA.debugLineNum = 3080193;BA.debugLine="mqttGetData.Initialize";
parent._mqttgetdata._initialize /*String*/ (null,processBA);
RDebugUtils.currentLine=3080194;
 //BA.debugLineNum = 3080194;BA.debugLine="dataTmr.Initialize(\"dataTmr\", 1000)";
parent._datatmr.Initialize(processBA,"dataTmr",(long) (1000));
RDebugUtils.currentLine=3080195;
 //BA.debugLineNum = 3080195;BA.debugLine="dataTmr.Enabled = True";
parent._datatmr.setEnabled(anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=3080196;
 //BA.debugLineNum = 3080196;BA.debugLine="Activity.LoadLayout(\"ServerBoard\")";
parent.mostCurrent._activity.LoadLayout("ServerBoard",mostCurrent.activityBA);
RDebugUtils.currentLine=3080198;
 //BA.debugLineNum = 3080198;BA.debugLine="mqttGetData.Connect";
parent._mqttgetdata._connect /*String*/ (null);
RDebugUtils.currentLine=3080200;
 //BA.debugLineNum = 3080200;BA.debugLine="lblNoData.TextColor = Colors.Red";
parent.mostCurrent._lblnodata.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
RDebugUtils.currentLine=3080201;
 //BA.debugLineNum = 3080201;BA.debugLine="imgNoData.SetVisibleAnimated(1000, True)";
parent.mostCurrent._imgnodata.SetVisibleAnimated((int) (1000),anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=3080202;
 //BA.debugLineNum = 3080202;BA.debugLine="lblNoData.SetVisibleAnimated(1000, True)";
parent.mostCurrent._lblnodata.SetVisibleAnimated((int) (1000),anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=3080204;
 //BA.debugLineNum = 3080204;BA.debugLine="Sleep(1000)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "serverboard", "activity_create"),(int) (1000));
this.state = 1;
return;
case 1:
//C
this.state = -1;
;
RDebugUtils.currentLine=3080205;
 //BA.debugLineNum = 3080205;BA.debugLine="mqttGetData.SendMessage(\"data please\")";
parent._mqttgetdata._sendmessage /*String*/ (null,"data please");
RDebugUtils.currentLine=3080206;
 //BA.debugLineNum = 3080206;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static boolean  _activity_keypress(int _keycode) throws Exception{
RDebugUtils.currentModule="serverboard";
if (Debug.shouldDelegate(mostCurrent.activityBA, "activity_keypress", false))
	 {return ((Boolean) Debug.delegate(mostCurrent.activityBA, "activity_keypress", new Object[] {_keycode}));}
RDebugUtils.currentLine=3342336;
 //BA.debugLineNum = 3342336;BA.debugLine="Private Sub Activity_KeyPress(KeyCode As Int) As B";
RDebugUtils.currentLine=3342337;
 //BA.debugLineNum = 3342337;BA.debugLine="If KeyCode = KeyCodes.KEYCODE_BACK Then";
if (_keycode==anywheresoftware.b4a.keywords.Common.KeyCodes.KEYCODE_BACK) { 
RDebugUtils.currentLine=3342339;
 //BA.debugLineNum = 3342339;BA.debugLine="mqttGetData.Disconnect";
_mqttgetdata._disconnect /*String*/ (null);
RDebugUtils.currentLine=3342340;
 //BA.debugLineNum = 3342340;BA.debugLine="Return False";
if (true) return anywheresoftware.b4a.keywords.Common.False;
 }else {
RDebugUtils.currentLine=3342342;
 //BA.debugLineNum = 3342342;BA.debugLine="Return True";
if (true) return anywheresoftware.b4a.keywords.Common.True;
 };
RDebugUtils.currentLine=3342344;
 //BA.debugLineNum = 3342344;BA.debugLine="End Sub";
return false;
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
RDebugUtils.currentModule="serverboard";
RDebugUtils.currentLine=3276800;
 //BA.debugLineNum = 3276800;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
RDebugUtils.currentLine=3276801;
 //BA.debugLineNum = 3276801;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
RDebugUtils.currentModule="serverboard";
if (Debug.shouldDelegate(mostCurrent.activityBA, "activity_resume", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "activity_resume", null));}
RDebugUtils.currentLine=3211264;
 //BA.debugLineNum = 3211264;BA.debugLine="Sub Activity_Resume";
RDebugUtils.currentLine=3211265;
 //BA.debugLineNum = 3211265;BA.debugLine="waitText = $\"Wachten op ${Starter.selectedBordNam";
_waittext = ("Wachten op "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(mostCurrent._starter._selectedbordname /*String*/ ))+"");
RDebugUtils.currentLine=3211266;
 //BA.debugLineNum = 3211266;BA.debugLine="dotCount = 0";
_dotcount = (int) (0);
RDebugUtils.currentLine=3211268;
 //BA.debugLineNum = 3211268;BA.debugLine="End Sub";
return "";
}
public static String  _datatmr_tick() throws Exception{
RDebugUtils.currentModule="serverboard";
if (Debug.shouldDelegate(mostCurrent.activityBA, "datatmr_tick", false))
	 {return ((String) Debug.delegate(mostCurrent.activityBA, "datatmr_tick", null));}
String _dot = "";
int _i = 0;
RDebugUtils.currentLine=3145728;
 //BA.debugLineNum = 3145728;BA.debugLine="Sub dataTmr_Tick";
RDebugUtils.currentLine=3145729;
 //BA.debugLineNum = 3145729;BA.debugLine="Dim dot As String";
_dot = "";
RDebugUtils.currentLine=3145730;
 //BA.debugLineNum = 3145730;BA.debugLine="dotCount=dotCount+1";
_dotcount = (int) (_dotcount+1);
RDebugUtils.currentLine=3145731;
 //BA.debugLineNum = 3145731;BA.debugLine="If dotCount >= 10 Then";
if (_dotcount>=10) { 
RDebugUtils.currentLine=3145732;
 //BA.debugLineNum = 3145732;BA.debugLine="dotCount = 0";
_dotcount = (int) (0);
RDebugUtils.currentLine=3145733;
 //BA.debugLineNum = 3145733;BA.debugLine="lblNoData.Text = waitText";
mostCurrent._lblnodata.setText(BA.ObjectToCharSequence(_waittext));
RDebugUtils.currentLine=3145734;
 //BA.debugLineNum = 3145734;BA.debugLine="Return";
if (true) return "";
 };
RDebugUtils.currentLine=3145736;
 //BA.debugLineNum = 3145736;BA.debugLine="For i = 0 To dotCount";
{
final int step8 = 1;
final int limit8 = _dotcount;
_i = (int) (0) ;
for (;_i <= limit8 ;_i = _i + step8 ) {
RDebugUtils.currentLine=3145737;
 //BA.debugLineNum = 3145737;BA.debugLine="dot = dot &\"*\"";
_dot = _dot+"*";
 }
};
RDebugUtils.currentLine=3145739;
 //BA.debugLineNum = 3145739;BA.debugLine="lblNoData.Text = $\"${dot} ${waitText} ${dot}\"$";
mostCurrent._lblnodata.setText(BA.ObjectToCharSequence((""+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_dot))+" "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_waittext))+" "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_dot))+"")));
RDebugUtils.currentLine=3145740;
 //BA.debugLineNum = 3145740;BA.debugLine="End Sub";
return "";
}
}