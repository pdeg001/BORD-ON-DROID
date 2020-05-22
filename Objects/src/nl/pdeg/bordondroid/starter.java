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
public static anywheresoftware.b4j.objects.MqttAsyncClientWrapper _mqtt = null;
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
public static String _mqttlastwill = "";
public static anywheresoftware.b4a.objects.RuntimePermissions _rp = null;
public static boolean _mqttgetbordsactive = false;
public static boolean _mqttgetborddataactive = false;
public static int _diedindex = 0;
public static String _basefile = "";
public static String _basefilepath = "";
public static String _substring = "";
public static String _subdisconnectstring = "";
public static String _selectedlocationcode = "";
public static String _selectedlocationdescription = "";
public static String _storefolder = "";
public static boolean _testbasename = false;
public static String _appversion = "";
public static boolean _working = false;
public static boolean _brokerconnected = false;
public static long _firstconnecttime = 0L;
public static boolean _mainpaused = false;
public b4a.example.dateutils _dateutils = null;
public nl.pdeg.bordondroid.main _main = null;
public nl.pdeg.bordondroid.locations _locations = null;
public nl.pdeg.bordondroid.serverboard _serverboard = null;
public nl.pdeg.bordondroid.selectlocation _selectlocation = null;
public static String  _setlastwill(String _lastwill) throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "setlastwill", false))
	 {return ((String) Debug.delegate(processBA, "setlastwill", new Object[] {_lastwill}));}
RDebugUtils.currentLine=5505024;
 //BA.debugLineNum = 5505024;BA.debugLine="Sub SetLastWill(lastWill As String)";
RDebugUtils.currentLine=5505025;
 //BA.debugLineNum = 5505025;BA.debugLine="mqttLastWill = lastWill";
_mqttlastwill = _lastwill;
RDebugUtils.currentLine=5505026;
 //BA.debugLineNum = 5505026;BA.debugLine="End Sub";
return "";
}
public static String  _setsubbase(String _basename) throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "setsubbase", false))
	 {return ((String) Debug.delegate(processBA, "setsubbase", new Object[] {_basename}));}
RDebugUtils.currentLine=5832704;
 //BA.debugLineNum = 5832704;BA.debugLine="Private Sub SetSubBase(baseName As String)";
RDebugUtils.currentLine=5832705;
 //BA.debugLineNum = 5832705;BA.debugLine="mqttBase = baseName";
_mqttbase = _basename;
RDebugUtils.currentLine=5832706;
 //BA.debugLineNum = 5832706;BA.debugLine="End Sub";
return "";
}
public static String  _setsubstring2(String _unit) throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "setsubstring2", false))
	 {return ((String) Debug.delegate(processBA, "setsubstring2", new Object[] {_unit}));}
RDebugUtils.currentLine=5701632;
 //BA.debugLineNum = 5701632;BA.debugLine="Public Sub SetSubString2(unit As String)";
RDebugUtils.currentLine=5701633;
 //BA.debugLineNum = 5701633;BA.debugLine="SubString = $\"${mqttName}/${mqttBase}${unit}\"$";
_substring = (""+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_mqttname))+"/"+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_mqttbase))+""+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_unit))+"");
RDebugUtils.currentLine=5701634;
 //BA.debugLineNum = 5701634;BA.debugLine="End Sub";
return "";
}
public static String  _setunsubscribestring2(String _unit) throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "setunsubscribestring2", false))
	 {return ((String) Debug.delegate(processBA, "setunsubscribestring2", new Object[] {_unit}));}
RDebugUtils.currentLine=5767168;
 //BA.debugLineNum = 5767168;BA.debugLine="Public Sub SetUnsubscribeString2(unit As String)";
RDebugUtils.currentLine=5767169;
 //BA.debugLineNum = 5767169;BA.debugLine="subDisconnectString =  $\"${mqttName}/${mqttBase}$";
_subdisconnectstring = (""+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_mqttname))+"/"+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_mqttbase))+""+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_unit))+"/disconnect");
RDebugUtils.currentLine=5767170;
 //BA.debugLineNum = 5767170;BA.debugLine="End Sub";
return "";
}
public static String  _setunit(String _name) throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "setunit", false))
	 {return ((String) Debug.delegate(processBA, "setunit", new Object[] {_name}));}
RDebugUtils.currentLine=5963776;
 //BA.debugLineNum = 5963776;BA.debugLine="Private Sub SetUnit(name As String)";
RDebugUtils.currentLine=5963777;
 //BA.debugLineNum = 5963777;BA.debugLine="mqttUnit = name";
_mqttunit = _name;
RDebugUtils.currentLine=5963778;
 //BA.debugLineNum = 5963778;BA.debugLine="End Sub";
return "";
}
public static String  _setsubstring() throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "setsubstring", false))
	 {return ((String) Debug.delegate(processBA, "setsubstring", null));}
RDebugUtils.currentLine=5636096;
 //BA.debugLineNum = 5636096;BA.debugLine="Public Sub SetSubString";
RDebugUtils.currentLine=5636097;
 //BA.debugLineNum = 5636097;BA.debugLine="SubString = $\"${mqttName}/${mqttBase}/recvdata_${";
_substring = (""+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_mqttname))+"/"+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_mqttbase))+"/recvdata_"+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_mqttunit))+"");
RDebugUtils.currentLine=5636098;
 //BA.debugLineNum = 5636098;BA.debugLine="End Sub";
return "";
}
public static boolean  _application_error(anywheresoftware.b4a.objects.B4AException _error,String _stacktrace) throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "application_error", false))
	 {return ((Boolean) Debug.delegate(processBA, "application_error", new Object[] {_error,_stacktrace}));}
RDebugUtils.currentLine=589824;
 //BA.debugLineNum = 589824;BA.debugLine="Sub Application_Error (Error As Exception, StackTr";
RDebugUtils.currentLine=589825;
 //BA.debugLineNum = 589825;BA.debugLine="Return True";
if (true) return anywheresoftware.b4a.keywords.Common.True;
RDebugUtils.currentLine=589826;
 //BA.debugLineNum = 589826;BA.debugLine="End Sub";
return false;
}
public static void  _connectandreconnect() throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "connectandreconnect", false))
	 {Debug.delegate(processBA, "connectandreconnect", null); return;}
ResumableSub_ConnectAndReconnect rsub = new ResumableSub_ConnectAndReconnect(null);
rsub.resume(processBA, null);
}
public static class ResumableSub_ConnectAndReconnect extends BA.ResumableSub {
public ResumableSub_ConnectAndReconnect(nl.pdeg.bordondroid.starter parent) {
this.parent = parent;
}
nl.pdeg.bordondroid.starter parent;
anywheresoftware.b4j.objects.MqttAsyncClientWrapper.MqttConnectOptionsWrapper _mo = null;
boolean _success = false;

@Override
public void resume(BA ba, Object[] result) throws Exception{
RDebugUtils.currentModule="starter";

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
RDebugUtils.currentLine=5439489;
 //BA.debugLineNum = 5439489;BA.debugLine="Do While working";
if (true) break;

case 1:
//do while
this.state = 32;
while (parent._working) {
this.state = 3;
if (true) break;
}
if (true) break;

case 3:
//C
this.state = 4;
RDebugUtils.currentLine=5439490;
 //BA.debugLineNum = 5439490;BA.debugLine="If mqtt.IsInitialized Then mqtt.Close";
if (true) break;

case 4:
//if
this.state = 9;
if (parent._mqtt.IsInitialized()) { 
this.state = 6;
;}if (true) break;

case 6:
//C
this.state = 9;
parent._mqtt.Close();
if (true) break;

case 9:
//C
this.state = 10;
;
RDebugUtils.currentLine=5439491;
 //BA.debugLineNum = 5439491;BA.debugLine="mqtt.Initialize(\"mqtt\", \"tcp://pdeg3005.mynetgea";
parent._mqtt.Initialize(processBA,"mqtt","tcp://pdeg3005.mynetgear.com:1883","pdeg_"+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Rnd((int) (0),(int) (999999999))));
RDebugUtils.currentLine=5439492;
 //BA.debugLineNum = 5439492;BA.debugLine="Dim mo As MqttConnectOptions";
_mo = new anywheresoftware.b4j.objects.MqttAsyncClientWrapper.MqttConnectOptionsWrapper();
RDebugUtils.currentLine=5439493;
 //BA.debugLineNum = 5439493;BA.debugLine="mo.Initialize(\"\", \"\")";
_mo.Initialize("","");
RDebugUtils.currentLine=5439495;
 //BA.debugLineNum = 5439495;BA.debugLine="mqtt.Connect2(mo)";
parent._mqtt.Connect2((org.eclipse.paho.client.mqttv3.MqttConnectOptions)(_mo.getObject()));
RDebugUtils.currentLine=5439496;
 //BA.debugLineNum = 5439496;BA.debugLine="Wait For Mqtt_Connected (Success As Boolean)";
anywheresoftware.b4a.keywords.Common.WaitFor("mqtt_connected", processBA, new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "starter", "connectandreconnect"), null);
this.state = 33;
return;
case 33:
//C
this.state = 10;
_success = (Boolean) result[0];
;
RDebugUtils.currentLine=5439497;
 //BA.debugLineNum = 5439497;BA.debugLine="If Success Then";
if (true) break;

case 10:
//if
this.state = 31;
if (_success) { 
this.state = 12;
}else {
this.state = 24;
}if (true) break;

case 12:
//C
this.state = 13;
RDebugUtils.currentLine=5439499;
 //BA.debugLineNum = 5439499;BA.debugLine="brokerConnected = True";
parent._brokerconnected = anywheresoftware.b4a.keywords.Common.True;
RDebugUtils.currentLine=5439501;
 //BA.debugLineNum = 5439501;BA.debugLine="CallSub(Main, \"getBaseList\")";
anywheresoftware.b4a.keywords.Common.CallSubDebug(processBA,(Object)(parent.mostCurrent._main.getObject()),"getBaseList");
RDebugUtils.currentLine=5439503;
 //BA.debugLineNum = 5439503;BA.debugLine="Do While working And mqtt.Connected";
if (true) break;

case 13:
//do while
this.state = 16;
while (parent._working && parent._mqtt.getConnected()) {
this.state = 15;
if (true) break;
}
if (true) break;

case 15:
//C
this.state = 13;
RDebugUtils.currentLine=5439504;
 //BA.debugLineNum = 5439504;BA.debugLine="mqtt.Publish2(\"ping\", Array As Byte(0), 1, Fal";
parent._mqtt.Publish2("ping",new byte[]{(byte) (0)},(int) (1),anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=5439505;
 //BA.debugLineNum = 5439505;BA.debugLine="Sleep(5000)";
anywheresoftware.b4a.keywords.Common.Sleep(processBA,new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "starter", "connectandreconnect"),(int) (5000));
this.state = 34;
return;
case 34:
//C
this.state = 13;
;
 if (true) break;

case 16:
//C
this.state = 17;
;
RDebugUtils.currentLine=5439508;
 //BA.debugLineNum = 5439508;BA.debugLine="brokerConnected = False";
parent._brokerconnected = anywheresoftware.b4a.keywords.Common.False;
RDebugUtils.currentLine=5439509;
 //BA.debugLineNum = 5439509;BA.debugLine="CallSub(ServerBoard, \"ConnectionLost\")";
anywheresoftware.b4a.keywords.Common.CallSubDebug(processBA,(Object)(parent.mostCurrent._serverboard.getObject()),"ConnectionLost");
RDebugUtils.currentLine=5439510;
 //BA.debugLineNum = 5439510;BA.debugLine="CallSub(Main, \"ShowNotConnectedToBroker\")";
anywheresoftware.b4a.keywords.Common.CallSubDebug(processBA,(Object)(parent.mostCurrent._main.getObject()),"ShowNotConnectedToBroker");
RDebugUtils.currentLine=5439511;
 //BA.debugLineNum = 5439511;BA.debugLine="If mqtt.IsInitialized Then mqtt.Close";
if (true) break;

case 17:
//if
this.state = 22;
if (parent._mqtt.IsInitialized()) { 
this.state = 19;
;}if (true) break;

case 19:
//C
this.state = 22;
parent._mqtt.Close();
if (true) break;

case 22:
//C
this.state = 31;
;
 if (true) break;

case 24:
//C
this.state = 25;
RDebugUtils.currentLine=5439514;
 //BA.debugLineNum = 5439514;BA.debugLine="If mqtt.IsInitialized Then mqtt.Close";
if (true) break;

case 25:
//if
this.state = 30;
if (parent._mqtt.IsInitialized()) { 
this.state = 27;
;}if (true) break;

case 27:
//C
this.state = 30;
parent._mqtt.Close();
if (true) break;

case 30:
//C
this.state = 31;
;
 if (true) break;

case 31:
//C
this.state = 1;
;
RDebugUtils.currentLine=5439516;
 //BA.debugLineNum = 5439516;BA.debugLine="Sleep(5000)";
anywheresoftware.b4a.keywords.Common.Sleep(processBA,new anywheresoftware.b4a.shell.DebugResumableSub.DelegatableResumableSub(this, "starter", "connectandreconnect"),(int) (5000));
this.state = 35;
return;
case 35:
//C
this.state = 1;
;
 if (true) break;

case 32:
//C
this.state = -1;
;
RDebugUtils.currentLine=5439518;
 //BA.debugLineNum = 5439518;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static String  _getbase() throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "getbase", false))
	 {return ((String) Debug.delegate(processBA, "getbase", null));}
RDebugUtils.currentLine=6160384;
 //BA.debugLineNum = 6160384;BA.debugLine="Public Sub GetBase As String";
RDebugUtils.currentLine=6160385;
 //BA.debugLineNum = 6160385;BA.debugLine="Return mqttGetUnits '$\"${mqttName}/${mqttBase}/\"$";
if (true) return _mqttgetunits;
RDebugUtils.currentLine=6160386;
 //BA.debugLineNum = 6160386;BA.debugLine="End Sub";
return "";
}
public static String  _getbasefilepath() throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "getbasefilepath", false))
	 {return ((String) Debug.delegate(processBA, "getbasefilepath", null));}
RDebugUtils.currentLine=6225920;
 //BA.debugLineNum = 6225920;BA.debugLine="Private Sub GetBaseFilePath As String";
RDebugUtils.currentLine=6225921;
 //BA.debugLineNum = 6225921;BA.debugLine="Return baseFilePath";
if (true) return _basefilepath;
RDebugUtils.currentLine=6225922;
 //BA.debugLineNum = 6225922;BA.debugLine="End Sub";
return "";
}
public static String  _getlastwill() throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "getlastwill", false))
	 {return ((String) Debug.delegate(processBA, "getlastwill", null));}
RDebugUtils.currentLine=5570560;
 //BA.debugLineNum = 5570560;BA.debugLine="Sub GetLastWill As String";
RDebugUtils.currentLine=5570561;
 //BA.debugLineNum = 5570561;BA.debugLine="Return mqttLastWill";
if (true) return _mqttlastwill;
RDebugUtils.currentLine=5570562;
 //BA.debugLineNum = 5570562;BA.debugLine="End Sub";
return "";
}
public static String  _getsubstring() throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "getsubstring", false))
	 {return ((String) Debug.delegate(processBA, "getsubstring", null));}
RDebugUtils.currentLine=5898240;
 //BA.debugLineNum = 5898240;BA.debugLine="Private Sub GetSubString As String";
RDebugUtils.currentLine=5898241;
 //BA.debugLineNum = 5898241;BA.debugLine="Return SubString";
if (true) return _substring;
RDebugUtils.currentLine=5898242;
 //BA.debugLineNum = 5898242;BA.debugLine="End Sub";
return "";
}
public static String  _getsubunits() throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "getsubunits", false))
	 {return ((String) Debug.delegate(processBA, "getsubunits", null));}
RDebugUtils.currentLine=6094848;
 //BA.debugLineNum = 6094848;BA.debugLine="Private Sub GetSubUnits As String";
RDebugUtils.currentLine=6094849;
 //BA.debugLineNum = 6094849;BA.debugLine="Return mqttGetUnits";
if (true) return _mqttgetunits;
RDebugUtils.currentLine=6094850;
 //BA.debugLineNum = 6094850;BA.debugLine="End Sub";
return "";
}
public static String  _service_create() throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "service_create", false))
	 {return ((String) Debug.delegate(processBA, "service_create", null));}
RDebugUtils.currentLine=393216;
 //BA.debugLineNum = 393216;BA.debugLine="Sub Service_Create";
RDebugUtils.currentLine=393217;
 //BA.debugLineNum = 393217;BA.debugLine="serverList.Initialize";
_serverlist.Initialize();
RDebugUtils.currentLine=393218;
 //BA.debugLineNum = 393218;BA.debugLine="storeFolder = rp.GetSafeDirDefaultExternal(\"bod\")";
_storefolder = _rp.GetSafeDirDefaultExternal("bod");
RDebugUtils.currentLine=393220;
 //BA.debugLineNum = 393220;BA.debugLine="baseFile = \"bod.pdg\"";
_basefile = "bod.pdg";
RDebugUtils.currentLine=393221;
 //BA.debugLineNum = 393221;BA.debugLine="baseFilePath = File.Combine(storeFolder, baseFile";
_basefilepath = anywheresoftware.b4a.keywords.Common.File.Combine(_storefolder,_basefile);
RDebugUtils.currentLine=393222;
 //BA.debugLineNum = 393222;BA.debugLine="working = True";
_working = anywheresoftware.b4a.keywords.Common.True;
RDebugUtils.currentLine=393223;
 //BA.debugLineNum = 393223;BA.debugLine="End Sub";
return "";
}
public static String  _service_destroy() throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "service_destroy", false))
	 {return ((String) Debug.delegate(processBA, "service_destroy", null));}
RDebugUtils.currentLine=655360;
 //BA.debugLineNum = 655360;BA.debugLine="Sub Service_Destroy";
RDebugUtils.currentLine=655362;
 //BA.debugLineNum = 655362;BA.debugLine="End Sub";
return "";
}
public static String  _service_start(anywheresoftware.b4a.objects.IntentWrapper _startingintent) throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "service_start", false))
	 {return ((String) Debug.delegate(processBA, "service_start", new Object[] {_startingintent}));}
RDebugUtils.currentLine=458752;
 //BA.debugLineNum = 458752;BA.debugLine="Sub Service_Start (StartingIntent As Intent)";
RDebugUtils.currentLine=458753;
 //BA.debugLineNum = 458753;BA.debugLine="ConnectAndReconnect";
_connectandreconnect();
RDebugUtils.currentLine=458755;
 //BA.debugLineNum = 458755;BA.debugLine="End Sub";
return "";
}
public static String  _setls() throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "setls", false))
	 {return ((String) Debug.delegate(processBA, "setls", null));}
RDebugUtils.currentLine=6291456;
 //BA.debugLineNum = 6291456;BA.debugLine="Public Sub SetLs";
RDebugUtils.currentLine=6291459;
 //BA.debugLineNum = 6291459;BA.debugLine="End Sub";
return "";
}
public static String  _setsubgetunits() throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "setsubgetunits", false))
	 {return ((String) Debug.delegate(processBA, "setsubgetunits", null));}
RDebugUtils.currentLine=6029312;
 //BA.debugLineNum = 6029312;BA.debugLine="Private Sub SetSubGetUnits";
RDebugUtils.currentLine=6029313;
 //BA.debugLineNum = 6029313;BA.debugLine="mqttGetUnits = $\"${mqttName}/${mqttBase}\"$";
_mqttgetunits = (""+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_mqttname))+"/"+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_mqttbase))+"");
RDebugUtils.currentLine=6029314;
 //BA.debugLineNum = 6029314;BA.debugLine="End Sub";
return "";
}
}