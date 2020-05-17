package nl.pdeg.bordondroid;


import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.objects.ServiceHelper;
import anywheresoftware.b4a.debug.*;

public class starter extends  android.app.Service{
	public static class starter_BR extends android.content.BroadcastReceiver {

		@Override
		public void onReceive(android.content.Context context, android.content.Intent intent) {
            BA.LogInfo("** Receiver (starter) OnReceive **");
			android.content.Intent in = new android.content.Intent(context, starter.class);
			if (intent != null)
				in.putExtra("b4a_internal_intent", intent);
            ServiceHelper.StarterHelper.startServiceFromReceiver (context, in, true, anywheresoftware.b4a.ShellBA.class);
		}

	}
    static starter mostCurrent;
	public static BA processBA;
    private ServiceHelper _service;
    public static Class<?> getObject() {
		return starter.class;
	}
	@Override
	public void onCreate() {
        super.onCreate();
        mostCurrent = this;
        if (processBA == null) {
		    processBA = new anywheresoftware.b4a.ShellBA(this, null, null, "nl.pdeg.bordondroid", "nl.pdeg.bordondroid.starter");
            if (BA.isShellModeRuntimeCheck(processBA)) {
                processBA.raiseEvent2(null, true, "SHELL", false);
		    }
            try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            processBA.loadHtSubs(this.getClass());
            ServiceHelper.init();
        }
        _service = new ServiceHelper(this);
        processBA.service = this;
        
        if (BA.isShellModeRuntimeCheck(processBA)) {
			processBA.raiseEvent2(null, true, "CREATE", true, "nl.pdeg.bordondroid.starter", processBA, _service, anywheresoftware.b4a.keywords.Common.Density);
		}
        if (!true && ServiceHelper.StarterHelper.startFromServiceCreate(processBA, false) == false) {
				
		}
		else {
            processBA.setActivityPaused(false);
            BA.LogInfo("*** Service (starter) Create ***");
            processBA.raiseEvent(null, "service_create");
        }
        processBA.runHook("oncreate", this, null);
        if (true) {
			ServiceHelper.StarterHelper.runWaitForLayouts();
		}
    }
		@Override
	public void onStart(android.content.Intent intent, int startId) {
		onStartCommand(intent, 0, 0);
    }
    @Override
    public int onStartCommand(final android.content.Intent intent, int flags, int startId) {
    	if (ServiceHelper.StarterHelper.onStartCommand(processBA, new Runnable() {
            public void run() {
                handleStart(intent);
            }}))
			;
		else {
			ServiceHelper.StarterHelper.addWaitForLayout (new Runnable() {
				public void run() {
                    processBA.setActivityPaused(false);
                    BA.LogInfo("** Service (starter) Create **");
                    processBA.raiseEvent(null, "service_create");
					handleStart(intent);
                    ServiceHelper.StarterHelper.removeWaitForLayout();
				}
			});
		}
        processBA.runHook("onstartcommand", this, new Object[] {intent, flags, startId});
		return android.app.Service.START_NOT_STICKY;
    }
    public void onTaskRemoved(android.content.Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        if (true)
            processBA.raiseEvent(null, "service_taskremoved");
            
    }
    private void handleStart(android.content.Intent intent) {
    	BA.LogInfo("** Service (starter) Start **");
    	java.lang.reflect.Method startEvent = processBA.htSubs.get("service_start");
    	if (startEvent != null) {
    		if (startEvent.getParameterTypes().length > 0) {
    			anywheresoftware.b4a.objects.IntentWrapper iw = ServiceHelper.StarterHelper.handleStartIntent(intent, _service, processBA);
    			processBA.raiseEvent(null, "service_start", iw);
    		}
    		else {
    			processBA.raiseEvent(null, "service_start");
    		}
    	}
    }
	
	@Override
	public void onDestroy() {
        super.onDestroy();
        if (true) {
            BA.LogInfo("** Service (starter) Destroy (ignored)**");
        }
        else {
            BA.LogInfo("** Service (starter) Destroy **");
		    processBA.raiseEvent(null, "service_destroy");
            processBA.service = null;
		    mostCurrent = null;
		    processBA.setActivityPaused(true);
            processBA.runHook("ondestroy", this, null);
        }
	}

@Override
	public android.os.IBinder onBind(android.content.Intent intent) {
		return null;
	}
public anywheresoftware.b4a.keywords.Common __c = null;
public static int _port = 0;
public static String _host = "";
public static boolean _connected = false;
public static String _discoveredserver = "";
public static anywheresoftware.b4a.objects.collections.List _serverlist = null;
public static long _serverdied = 0L;
public static String _selectedbordname = "";
public static String _mqttname = "";
public static String _mqttbase = "";
public static String _mqttunit = "";
public static String _mqttgetunits = "";
public static anywheresoftware.b4a.objects.RuntimePermissions _rp = null;
public static boolean _mqttgetbordsactive = false;
public static boolean _mqttgetborddataactive = false;
public static int _diedindex = 0;
public static String _substring = "";
public static String _basefile = "";
public static String _basefilepath = "";
public static String _storefolder = "";
public static boolean _testbasename = false;
public b4a.example.dateutils _dateutils = null;
public nl.pdeg.bordondroid.main _main = null;
public nl.pdeg.bordondroid.locations _locations = null;
public nl.pdeg.bordondroid.serverboard _serverboard = null;
public static String  _setsubbase(String _basename) throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "setsubbase", false))
	 {return ((String) Debug.delegate(processBA, "setsubbase", new Object[] {_basename}));}
RDebugUtils.currentLine=4915200;
 //BA.debugLineNum = 4915200;BA.debugLine="Private Sub SetSubBase(baseName As String)";
RDebugUtils.currentLine=4915201;
 //BA.debugLineNum = 4915201;BA.debugLine="mqttBase = baseName";
_mqttbase = _basename;
RDebugUtils.currentLine=4915202;
 //BA.debugLineNum = 4915202;BA.debugLine="End Sub";
return "";
}
public static String  _setsubgetunits() throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "setsubgetunits", false))
	 {return ((String) Debug.delegate(processBA, "setsubgetunits", null));}
RDebugUtils.currentLine=5111808;
 //BA.debugLineNum = 5111808;BA.debugLine="Private Sub SetSubGetUnits";
RDebugUtils.currentLine=5111809;
 //BA.debugLineNum = 5111809;BA.debugLine="mqttGetUnits = $\"${mqttName}/${mqttBase}\"$";
_mqttgetunits = (""+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_mqttname))+"/"+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_mqttbase))+"");
RDebugUtils.currentLine=5111810;
 //BA.debugLineNum = 5111810;BA.debugLine="End Sub";
return "";
}
public static String  _setunit(String _name) throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "setunit", false))
	 {return ((String) Debug.delegate(processBA, "setunit", new Object[] {_name}));}
RDebugUtils.currentLine=5046272;
 //BA.debugLineNum = 5046272;BA.debugLine="Private Sub SetUnit(name As String)";
RDebugUtils.currentLine=5046273;
 //BA.debugLineNum = 5046273;BA.debugLine="mqttUnit = name";
_mqttunit = _name;
RDebugUtils.currentLine=5046274;
 //BA.debugLineNum = 5046274;BA.debugLine="End Sub";
return "";
}
public static String  _getbasefilepath() throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "getbasefilepath", false))
	 {return ((String) Debug.delegate(processBA, "getbasefilepath", null));}
RDebugUtils.currentLine=5308416;
 //BA.debugLineNum = 5308416;BA.debugLine="Private Sub GetBaseFilePath As String";
RDebugUtils.currentLine=5308417;
 //BA.debugLineNum = 5308417;BA.debugLine="Return baseFilePath";
if (true) return _basefilepath;
RDebugUtils.currentLine=5308418;
 //BA.debugLineNum = 5308418;BA.debugLine="End Sub";
return "";
}
public static String  _getsubstring() throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "getsubstring", false))
	 {return ((String) Debug.delegate(processBA, "getsubstring", null));}
RDebugUtils.currentLine=4980736;
 //BA.debugLineNum = 4980736;BA.debugLine="Private Sub GetSubString As String";
RDebugUtils.currentLine=4980737;
 //BA.debugLineNum = 4980737;BA.debugLine="Return SubString";
if (true) return _substring;
RDebugUtils.currentLine=4980738;
 //BA.debugLineNum = 4980738;BA.debugLine="End Sub";
return "";
}
public static String  _getbase() throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "getbase", false))
	 {return ((String) Debug.delegate(processBA, "getbase", null));}
RDebugUtils.currentLine=5242880;
 //BA.debugLineNum = 5242880;BA.debugLine="Public Sub GetBase As String";
RDebugUtils.currentLine=5242881;
 //BA.debugLineNum = 5242881;BA.debugLine="Return mqttGetUnits '$\"${mqttName}/${mqttBase}/\"$";
if (true) return _mqttgetunits;
RDebugUtils.currentLine=5242882;
 //BA.debugLineNum = 5242882;BA.debugLine="End Sub";
return "";
}
public static String  _getsubunits() throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "getsubunits", false))
	 {return ((String) Debug.delegate(processBA, "getsubunits", null));}
RDebugUtils.currentLine=5177344;
 //BA.debugLineNum = 5177344;BA.debugLine="Private Sub GetSubUnits As String";
RDebugUtils.currentLine=5177345;
 //BA.debugLineNum = 5177345;BA.debugLine="Return mqttGetUnits";
if (true) return _mqttgetunits;
RDebugUtils.currentLine=5177346;
 //BA.debugLineNum = 5177346;BA.debugLine="End Sub";
return "";
}
public static String  _setsubstring() throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "setsubstring", false))
	 {return ((String) Debug.delegate(processBA, "setsubstring", null));}
RDebugUtils.currentLine=4849664;
 //BA.debugLineNum = 4849664;BA.debugLine="Public Sub SetSubString";
RDebugUtils.currentLine=4849665;
 //BA.debugLineNum = 4849665;BA.debugLine="SubString = $\"${mqttName}/${mqttBase}/${mqttUnit}";
_substring = (""+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_mqttname))+"/"+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_mqttbase))+"/"+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_mqttunit))+"");
RDebugUtils.currentLine=4849666;
 //BA.debugLineNum = 4849666;BA.debugLine="End Sub";
return "";
}
public static boolean  _application_error(anywheresoftware.b4a.objects.B4AException _error,String _stacktrace) throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "application_error", false))
	 {return ((Boolean) Debug.delegate(processBA, "application_error", new Object[] {_error,_stacktrace}));}
RDebugUtils.currentLine=4653056;
 //BA.debugLineNum = 4653056;BA.debugLine="Sub Application_Error (Error As Exception, StackTr";
RDebugUtils.currentLine=4653057;
 //BA.debugLineNum = 4653057;BA.debugLine="Return True";
if (true) return anywheresoftware.b4a.keywords.Common.True;
RDebugUtils.currentLine=4653058;
 //BA.debugLineNum = 4653058;BA.debugLine="End Sub";
return false;
}
public static String  _createbasefile() throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "createbasefile", false))
	 {return ((String) Debug.delegate(processBA, "createbasefile", null));}
RDebugUtils.currentLine=4784128;
 //BA.debugLineNum = 4784128;BA.debugLine="Private Sub CreateBaseFile";
RDebugUtils.currentLine=4784129;
 //BA.debugLineNum = 4784129;BA.debugLine="If Not(File.Exists(baseFilePath, \"\")) Then";
if (anywheresoftware.b4a.keywords.Common.Not(anywheresoftware.b4a.keywords.Common.File.Exists(_basefilepath,""))) { 
RDebugUtils.currentLine=4784130;
 //BA.debugLineNum = 4784130;BA.debugLine="File.WriteString(baseFilePath, \"\", \"\")";
anywheresoftware.b4a.keywords.Common.File.WriteString(_basefilepath,"","");
 };
RDebugUtils.currentLine=4784132;
 //BA.debugLineNum = 4784132;BA.debugLine="End Sub";
return "";
}
public static String  _service_create() throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "service_create", false))
	 {return ((String) Debug.delegate(processBA, "service_create", null));}
RDebugUtils.currentLine=4521984;
 //BA.debugLineNum = 4521984;BA.debugLine="Sub Service_Create";
RDebugUtils.currentLine=4521985;
 //BA.debugLineNum = 4521985;BA.debugLine="serverList.Initialize";
_serverlist.Initialize();
RDebugUtils.currentLine=4521986;
 //BA.debugLineNum = 4521986;BA.debugLine="storeFolder = rp.GetSafeDirDefaultExternal(\"bod\")";
_storefolder = _rp.GetSafeDirDefaultExternal("bod");
RDebugUtils.currentLine=4521988;
 //BA.debugLineNum = 4521988;BA.debugLine="baseFile = \"bod.pdg\"";
_basefile = "bod.pdg";
RDebugUtils.currentLine=4521989;
 //BA.debugLineNum = 4521989;BA.debugLine="baseFilePath = File.Combine(storeFolder, baseFile";
_basefilepath = anywheresoftware.b4a.keywords.Common.File.Combine(_storefolder,_basefile);
RDebugUtils.currentLine=4521991;
 //BA.debugLineNum = 4521991;BA.debugLine="End Sub";
return "";
}
public static String  _service_destroy() throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "service_destroy", false))
	 {return ((String) Debug.delegate(processBA, "service_destroy", null));}
RDebugUtils.currentLine=4718592;
 //BA.debugLineNum = 4718592;BA.debugLine="Sub Service_Destroy";
RDebugUtils.currentLine=4718594;
 //BA.debugLineNum = 4718594;BA.debugLine="End Sub";
return "";
}
public static String  _service_start(anywheresoftware.b4a.objects.IntentWrapper _startingintent) throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "service_start", false))
	 {return ((String) Debug.delegate(processBA, "service_start", new Object[] {_startingintent}));}
RDebugUtils.currentLine=4587520;
 //BA.debugLineNum = 4587520;BA.debugLine="Sub Service_Start (StartingIntent As Intent)";
RDebugUtils.currentLine=4587522;
 //BA.debugLineNum = 4587522;BA.debugLine="End Sub";
return "";
}
}