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
            ServiceHelper.StarterHelper.startServiceFromReceiver (context, in, true, BA.class);
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
		    processBA = new BA(this, null, null, "nl.pdeg.bordondroid", "nl.pdeg.bordondroid.starter");
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
	}public anywheresoftware.b4a.keywords.Common __c = null;
public static anywheresoftware.b4j.objects.MqttAsyncClientWrapper _mqtt = null;
public static int _port = 0;
public static String _host = "";
public static boolean _connected = false;
public static String _discoveredserver = "";
public static anywheresoftware.b4a.objects.collections.List _serverlist = null;
public static long _serverdied = 0L;
public static long _serverdiedremove = 0L;
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
public static boolean _pingmqtt = false;
public static long _firstconnecttime = 0L;
public static boolean _mainpaused = false;
public b4a.example.dateutils _dateutils = null;
public nl.pdeg.bordondroid.main _main = null;
public nl.pdeg.bordondroid.locations _locations = null;
public nl.pdeg.bordondroid.selectlocation _selectlocation = null;
public nl.pdeg.bordondroid.serverboard _serverboard = null;
public static boolean  _application_error(anywheresoftware.b4a.objects.B4AException _error,String _stacktrace) throws Exception{
 //BA.debugLineNum = 50;BA.debugLine="Sub Application_Error (Error As Exception, StackTr";
 //BA.debugLineNum = 51;BA.debugLine="Return True";
if (true) return anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 52;BA.debugLine="End Sub";
return false;
}
public static void  _connectandreconnect() throws Exception{
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

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
 //BA.debugLineNum = 59;BA.debugLine="Do While pingMqtt";
if (true) break;

case 1:
//do while
this.state = 32;
while (parent._pingmqtt) {
this.state = 3;
if (true) break;
}
if (true) break;

case 3:
//C
this.state = 4;
 //BA.debugLineNum = 60;BA.debugLine="If mqtt.IsInitialized Then mqtt.Close";
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
 //BA.debugLineNum = 61;BA.debugLine="mqtt.Initialize(\"mqtt\", \"tcp://pdeg3005.mynetgea";
parent._mqtt.Initialize(processBA,"mqtt","tcp://pdeg3005.mynetgear.com:1883","pdeg_"+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Rnd((int) (0),(int) (999999999))));
 //BA.debugLineNum = 62;BA.debugLine="Dim mo As MqttConnectOptions";
_mo = new anywheresoftware.b4j.objects.MqttAsyncClientWrapper.MqttConnectOptionsWrapper();
 //BA.debugLineNum = 63;BA.debugLine="mo.Initialize(\"\", \"\")";
_mo.Initialize("","");
 //BA.debugLineNum = 65;BA.debugLine="mqtt.Connect2(mo)";
parent._mqtt.Connect2((org.eclipse.paho.client.mqttv3.MqttConnectOptions)(_mo.getObject()));
 //BA.debugLineNum = 66;BA.debugLine="Wait For Mqtt_Connected (Success As Boolean)";
anywheresoftware.b4a.keywords.Common.WaitFor("mqtt_connected", processBA, this, null);
this.state = 33;
return;
case 33:
//C
this.state = 10;
_success = (Boolean) result[0];
;
 //BA.debugLineNum = 67;BA.debugLine="If Success Then";
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
 //BA.debugLineNum = 68;BA.debugLine="CallSub(Main, \"getBaseList\")";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)(parent.mostCurrent._main.getObject()),"getBaseList");
 //BA.debugLineNum = 70;BA.debugLine="Do While pingMqtt And mqtt.Connected";
if (true) break;

case 13:
//do while
this.state = 16;
while (parent._pingmqtt && parent._mqtt.getConnected()) {
this.state = 15;
if (true) break;
}
if (true) break;

case 15:
//C
this.state = 13;
 //BA.debugLineNum = 71;BA.debugLine="mqtt.Publish2(\"ping\", Array As Byte(0), 1, Fal";
parent._mqtt.Publish2("ping",new byte[]{(byte) (0)},(int) (1),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 73;BA.debugLine="Sleep(5000)";
anywheresoftware.b4a.keywords.Common.Sleep(processBA,this,(int) (5000));
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
 //BA.debugLineNum = 76;BA.debugLine="Log(\"Disconnected\")";
anywheresoftware.b4a.keywords.Common.LogImpl("56029330","Disconnected",0);
 //BA.debugLineNum = 78;BA.debugLine="CallSub(ServerBoard, \"ConnectionLost\")";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)(parent.mostCurrent._serverboard.getObject()),"ConnectionLost");
 //BA.debugLineNum = 79;BA.debugLine="CallSub(Main, \"ShowNotConnectedToBroker\")";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)(parent.mostCurrent._main.getObject()),"ShowNotConnectedToBroker");
 //BA.debugLineNum = 80;BA.debugLine="serverList.Initialize";
parent._serverlist.Initialize();
 //BA.debugLineNum = 81;BA.debugLine="If mqtt.IsInitialized Then mqtt.Close";
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
 //BA.debugLineNum = 83;BA.debugLine="Log(\"Error connecting.\")";
anywheresoftware.b4a.keywords.Common.LogImpl("56029337","Error connecting.",0);
 //BA.debugLineNum = 84;BA.debugLine="If mqtt.IsInitialized Then mqtt.Close";
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
 //BA.debugLineNum = 86;BA.debugLine="Sleep(5000)";
anywheresoftware.b4a.keywords.Common.Sleep(processBA,this,(int) (5000));
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
 //BA.debugLineNum = 88;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static void  _mqtt_connected(boolean _success) throws Exception{
}
public static String  _getbase() throws Exception{
 //BA.debugLineNum = 135;BA.debugLine="Public Sub GetBase As String";
 //BA.debugLineNum = 136;BA.debugLine="Return mqttGetUnits '$\"${mqttName}/${mqttBase}/\"$";
if (true) return _mqttgetunits;
 //BA.debugLineNum = 137;BA.debugLine="End Sub";
return "";
}
public static String  _getbasefilepath() throws Exception{
 //BA.debugLineNum = 139;BA.debugLine="Public Sub GetBaseFilePath As String";
 //BA.debugLineNum = 140;BA.debugLine="Return baseFilePath";
if (true) return _basefilepath;
 //BA.debugLineNum = 141;BA.debugLine="End Sub";
return "";
}
public static String  _getlastwill() throws Exception{
 //BA.debugLineNum = 94;BA.debugLine="Public Sub GetLastWill As String";
 //BA.debugLineNum = 95;BA.debugLine="Return mqttLastWill";
if (true) return _mqttlastwill;
 //BA.debugLineNum = 96;BA.debugLine="End Sub";
return "";
}
public static String  _getunit() throws Exception{
 //BA.debugLineNum = 123;BA.debugLine="Public Sub GetUnit As String";
 //BA.debugLineNum = 124;BA.debugLine="Return mqttUnit";
if (true) return _mqttunit;
 //BA.debugLineNum = 125;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 6;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 7;BA.debugLine="Dim mqtt As MqttClient";
_mqtt = new anywheresoftware.b4j.objects.MqttAsyncClientWrapper();
 //BA.debugLineNum = 8;BA.debugLine="Public const port As Int = 1883";
_port = (int) (1883);
 //BA.debugLineNum = 9;BA.debugLine="Public const host As String = \"pdeg3005.mynetgear";
_host = "pdeg3005.mynetgear.com";
 //BA.debugLineNum = 10;BA.debugLine="Public connected As Boolean";
_connected = false;
 //BA.debugLineNum = 11;BA.debugLine="Public DiscoveredServer As String";
_discoveredserver = "";
 //BA.debugLineNum = 12;BA.debugLine="Public serverList As List";
_serverlist = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 13;BA.debugLine="Public serverDied As Long = 10000";
_serverdied = (long) (10000);
 //BA.debugLineNum = 14;BA.debugLine="Public serverDiedRemove As Long = 20000";
_serverdiedremove = (long) (20000);
 //BA.debugLineNum = 15;BA.debugLine="Public selectedBordName As String";
_selectedbordname = "";
 //BA.debugLineNum = 16;BA.debugLine="Private mqttName As String = \"pdeg\"";
_mqttname = "pdeg";
 //BA.debugLineNum = 17;BA.debugLine="Private mqttBase As String";
_mqttbase = "";
 //BA.debugLineNum = 18;BA.debugLine="Private mqttUnit As String";
_mqttunit = "";
 //BA.debugLineNum = 19;BA.debugLine="Private mqttGetUnits As String";
_mqttgetunits = "";
 //BA.debugLineNum = 20;BA.debugLine="Private mqttLastWill As String";
_mqttlastwill = "";
 //BA.debugLineNum = 21;BA.debugLine="Private rp As RuntimePermissions";
_rp = new anywheresoftware.b4a.objects.RuntimePermissions();
 //BA.debugLineNum = 22;BA.debugLine="Public mqttGetBordsActive, mqttGetBordDataActive";
_mqttgetbordsactive = false;
_mqttgetborddataactive = false;
 //BA.debugLineNum = 23;BA.debugLine="Public diedIndex As Int = -1";
_diedindex = (int) (-1);
 //BA.debugLineNum = 24;BA.debugLine="Private baseFile, baseFilePath As String";
_basefile = "";
_basefilepath = "";
 //BA.debugLineNum = 25;BA.debugLine="Public SubString, subDisconnectString, selectedLo";
_substring = "";
_subdisconnectstring = "";
_selectedlocationcode = "";
_selectedlocationdescription = "";
 //BA.debugLineNum = 26;BA.debugLine="Private storeFolder As String";
_storefolder = "";
 //BA.debugLineNum = 27;BA.debugLine="Public testBaseName As Boolean = False";
_testbasename = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 28;BA.debugLine="Public appVersion As String";
_appversion = "";
 //BA.debugLineNum = 30;BA.debugLine="Dim pingMqtt As Boolean";
_pingmqtt = false;
 //BA.debugLineNum = 31;BA.debugLine="Public firstConnectTime As Long";
_firstconnecttime = 0L;
 //BA.debugLineNum = 32;BA.debugLine="Public mainPaused As Boolean";
_mainpaused = false;
 //BA.debugLineNum = 34;BA.debugLine="End Sub";
return "";
}
public static String  _service_create() throws Exception{
 //BA.debugLineNum = 36;BA.debugLine="Sub Service_Create";
 //BA.debugLineNum = 37;BA.debugLine="serverList.Initialize";
_serverlist.Initialize();
 //BA.debugLineNum = 38;BA.debugLine="storeFolder = rp.GetSafeDirDefaultExternal(\"bod\")";
_storefolder = _rp.GetSafeDirDefaultExternal("bod");
 //BA.debugLineNum = 40;BA.debugLine="baseFile = \"bod.pdg\"";
_basefile = "bod.pdg";
 //BA.debugLineNum = 41;BA.debugLine="baseFilePath = File.Combine(storeFolder, baseFile";
_basefilepath = anywheresoftware.b4a.keywords.Common.File.Combine(_storefolder,_basefile);
 //BA.debugLineNum = 42;BA.debugLine="pingMqtt = True";
_pingmqtt = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 43;BA.debugLine="ConnectAndReconnect";
_connectandreconnect();
 //BA.debugLineNum = 44;BA.debugLine="End Sub";
return "";
}
public static String  _service_destroy() throws Exception{
 //BA.debugLineNum = 54;BA.debugLine="Sub Service_Destroy";
 //BA.debugLineNum = 56;BA.debugLine="End Sub";
return "";
}
public static String  _service_start(anywheresoftware.b4a.objects.IntentWrapper _startingintent) throws Exception{
 //BA.debugLineNum = 46;BA.debugLine="Sub Service_Start (StartingIntent As Intent)";
 //BA.debugLineNum = 48;BA.debugLine="End Sub";
return "";
}
public static String  _setlastwill(String _lastwill) throws Exception{
 //BA.debugLineNum = 90;BA.debugLine="Public Sub SetLastWill(lastWill As String)";
 //BA.debugLineNum = 91;BA.debugLine="mqttLastWill = lastWill";
_mqttlastwill = _lastwill;
 //BA.debugLineNum = 92;BA.debugLine="End Sub";
return "";
}
public static String  _setls() throws Exception{
 //BA.debugLineNum = 143;BA.debugLine="Public Sub SetLs";
 //BA.debugLineNum = 146;BA.debugLine="End Sub";
return "";
}
public static String  _setsubbase(String _basename) throws Exception{
 //BA.debugLineNum = 111;BA.debugLine="Public Sub SetSubBase(baseName As String)";
 //BA.debugLineNum = 112;BA.debugLine="mqttBase = baseName";
_mqttbase = _basename;
 //BA.debugLineNum = 113;BA.debugLine="End Sub";
return "";
}
public static String  _setsubstring() throws Exception{
 //BA.debugLineNum = 98;BA.debugLine="Public Sub SetSubString";
 //BA.debugLineNum = 99;BA.debugLine="SubString = $\"${mqttName}/${mqttBase}/recvdata_${";
_substring = (""+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_mqttname))+"/"+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_mqttbase))+"/recvdata_"+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_mqttunit))+"");
 //BA.debugLineNum = 100;BA.debugLine="End Sub";
return "";
}
public static String  _setsubstring2(String _unit) throws Exception{
 //BA.debugLineNum = 102;BA.debugLine="Public Sub SetSubString2(unit As String)";
 //BA.debugLineNum = 103;BA.debugLine="SubString = $\"${mqttName}/${mqttBase}${unit}\"$";
_substring = (""+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_mqttname))+"/"+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_mqttbase))+""+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_unit))+"");
 //BA.debugLineNum = 104;BA.debugLine="End Sub";
return "";
}
public static String  _setunit(String _name) throws Exception{
 //BA.debugLineNum = 119;BA.debugLine="Public Sub SetUnit(name As String)";
 //BA.debugLineNum = 120;BA.debugLine="mqttUnit = name";
_mqttunit = _name;
 //BA.debugLineNum = 121;BA.debugLine="End Sub";
return "";
}
public static String  _setunsubscribestring2(String _unit) throws Exception{
 //BA.debugLineNum = 106;BA.debugLine="Public Sub SetUnsubscribeString2(unit As String)";
 //BA.debugLineNum = 107;BA.debugLine="subDisconnectString =  $\"${mqttName}/${mqttBase}$";
_subdisconnectstring = (""+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_mqttname))+"/"+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_mqttbase))+""+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_unit))+"/disconnect");
 //BA.debugLineNum = 108;BA.debugLine="End Sub";
return "";
}
}
