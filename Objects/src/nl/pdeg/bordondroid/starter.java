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
public static boolean _working = false;
public static boolean _brokerconnected = false;
public b4a.example.dateutils _dateutils = null;
public nl.pdeg.bordondroid.main _main = null;
public nl.pdeg.bordondroid.locations _locations = null;
public nl.pdeg.bordondroid.serverboard _serverboard = null;
public static boolean  _application_error(anywheresoftware.b4a.objects.B4AException _error,String _stacktrace) throws Exception{
 //BA.debugLineNum = 43;BA.debugLine="Sub Application_Error (Error As Exception, StackTr";
 //BA.debugLineNum = 44;BA.debugLine="Return True";
if (true) return anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 45;BA.debugLine="End Sub";
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
 //BA.debugLineNum = 52;BA.debugLine="Do While working";
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
 //BA.debugLineNum = 53;BA.debugLine="If mqtt.IsInitialized Then mqtt.Close";
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
 //BA.debugLineNum = 54;BA.debugLine="mqtt.Initialize(\"mqtt\", \"tcp://pdeg3005.mynetgea";
parent._mqtt.Initialize(processBA,"mqtt","tcp://pdeg3005.mynetgear.com:1883","pdeg_"+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Rnd((int) (0),(int) (999999999))));
 //BA.debugLineNum = 55;BA.debugLine="Dim mo As MqttConnectOptions";
_mo = new anywheresoftware.b4j.objects.MqttAsyncClientWrapper.MqttConnectOptionsWrapper();
 //BA.debugLineNum = 56;BA.debugLine="mo.Initialize(\"\", \"\")";
_mo.Initialize("","");
 //BA.debugLineNum = 57;BA.debugLine="Log(\"Trying to connect\")";
anywheresoftware.b4a.keywords.Common.LogImpl("829163526","Trying to connect",0);
 //BA.debugLineNum = 58;BA.debugLine="mqtt.Connect2(mo)";
parent._mqtt.Connect2((org.eclipse.paho.client.mqttv3.MqttConnectOptions)(_mo.getObject()));
 //BA.debugLineNum = 59;BA.debugLine="Wait For Mqtt_Connected (Success As Boolean)";
anywheresoftware.b4a.keywords.Common.WaitFor("mqtt_connected", processBA, this, null);
this.state = 33;
return;
case 33:
//C
this.state = 10;
_success = (Boolean) result[0];
;
 //BA.debugLineNum = 60;BA.debugLine="If Success Then";
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
 //BA.debugLineNum = 61;BA.debugLine="Log(\"Mqtt connected\")";
anywheresoftware.b4a.keywords.Common.LogImpl("829163530","Mqtt connected",0);
 //BA.debugLineNum = 62;BA.debugLine="brokerConnected = True";
parent._brokerconnected = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 64;BA.debugLine="CallSub(Main, \"getBaseList\")";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)(parent.mostCurrent._main.getObject()),"getBaseList");
 //BA.debugLineNum = 65;BA.debugLine="Do While working And mqtt.Connected";
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
 //BA.debugLineNum = 66;BA.debugLine="mqtt.Publish2(\"ping\", Array As Byte(0), 1, Fal";
parent._mqtt.Publish2("ping",new byte[]{(byte) (0)},(int) (1),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 67;BA.debugLine="Sleep(5000)";
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
 //BA.debugLineNum = 69;BA.debugLine="Log(\"Disconnected\")";
anywheresoftware.b4a.keywords.Common.LogImpl("829163538","Disconnected",0);
 //BA.debugLineNum = 70;BA.debugLine="brokerConnected = False";
parent._brokerconnected = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 71;BA.debugLine="If mqtt.IsInitialized Then mqtt.Close";
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
 //BA.debugLineNum = 73;BA.debugLine="Log(\"Error connecting.\")";
anywheresoftware.b4a.keywords.Common.LogImpl("829163542","Error connecting.",0);
 //BA.debugLineNum = 74;BA.debugLine="If mqtt.IsInitialized Then mqtt.Close";
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
 //BA.debugLineNum = 76;BA.debugLine="Sleep(5000)";
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
 //BA.debugLineNum = 78;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static void  _mqtt_connected(boolean _success) throws Exception{
}
public static String  _getbase() throws Exception{
 //BA.debugLineNum = 105;BA.debugLine="Public Sub GetBase As String";
 //BA.debugLineNum = 106;BA.debugLine="Return mqttGetUnits '$\"${mqttName}/${mqttBase}/\"$";
if (true) return _mqttgetunits;
 //BA.debugLineNum = 107;BA.debugLine="End Sub";
return "";
}
public static String  _getbasefilepath() throws Exception{
 //BA.debugLineNum = 109;BA.debugLine="Private Sub GetBaseFilePath As String";
 //BA.debugLineNum = 110;BA.debugLine="Return baseFilePath";
if (true) return _basefilepath;
 //BA.debugLineNum = 111;BA.debugLine="End Sub";
return "";
}
public static String  _getsubstring() throws Exception{
 //BA.debugLineNum = 89;BA.debugLine="Private Sub GetSubString As String";
 //BA.debugLineNum = 90;BA.debugLine="Return SubString";
if (true) return _substring;
 //BA.debugLineNum = 91;BA.debugLine="End Sub";
return "";
}
public static String  _getsubunits() throws Exception{
 //BA.debugLineNum = 101;BA.debugLine="Private Sub GetSubUnits As String";
 //BA.debugLineNum = 102;BA.debugLine="Return mqttGetUnits";
if (true) return _mqttgetunits;
 //BA.debugLineNum = 103;BA.debugLine="End Sub";
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
 //BA.debugLineNum = 13;BA.debugLine="Public serverDied As Long = 30000";
_serverdied = (long) (30000);
 //BA.debugLineNum = 14;BA.debugLine="Public selectedBordName As String";
_selectedbordname = "";
 //BA.debugLineNum = 15;BA.debugLine="Private mqttName As String = \"pdeg\"";
_mqttname = "pdeg";
 //BA.debugLineNum = 16;BA.debugLine="Private mqttBase As String";
_mqttbase = "";
 //BA.debugLineNum = 17;BA.debugLine="Private mqttUnit As String";
_mqttunit = "";
 //BA.debugLineNum = 18;BA.debugLine="Private mqttGetUnits As String";
_mqttgetunits = "";
 //BA.debugLineNum = 19;BA.debugLine="Private rp As RuntimePermissions";
_rp = new anywheresoftware.b4a.objects.RuntimePermissions();
 //BA.debugLineNum = 20;BA.debugLine="Public mqttGetBordsActive, mqttGetBordDataActive";
_mqttgetbordsactive = false;
_mqttgetborddataactive = false;
 //BA.debugLineNum = 21;BA.debugLine="Public diedIndex As Int = -1";
_diedindex = (int) (-1);
 //BA.debugLineNum = 22;BA.debugLine="Private SubString, baseFile, baseFilePath As Stri";
_substring = "";
_basefile = "";
_basefilepath = "";
 //BA.debugLineNum = 23;BA.debugLine="Private storeFolder As String";
_storefolder = "";
 //BA.debugLineNum = 24;BA.debugLine="Public testBaseName As Boolean = False";
_testbasename = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 25;BA.debugLine="Dim working, brokerConnected As Boolean";
_working = false;
_brokerconnected = false;
 //BA.debugLineNum = 27;BA.debugLine="End Sub";
return "";
}
public static String  _service_create() throws Exception{
 //BA.debugLineNum = 29;BA.debugLine="Sub Service_Create";
 //BA.debugLineNum = 30;BA.debugLine="serverList.Initialize";
_serverlist.Initialize();
 //BA.debugLineNum = 31;BA.debugLine="storeFolder = rp.GetSafeDirDefaultExternal(\"bod\")";
_storefolder = _rp.GetSafeDirDefaultExternal("bod");
 //BA.debugLineNum = 33;BA.debugLine="baseFile = \"bod.pdg\"";
_basefile = "bod.pdg";
 //BA.debugLineNum = 34;BA.debugLine="baseFilePath = File.Combine(storeFolder, baseFile";
_basefilepath = anywheresoftware.b4a.keywords.Common.File.Combine(_storefolder,_basefile);
 //BA.debugLineNum = 35;BA.debugLine="working = True";
_working = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 36;BA.debugLine="End Sub";
return "";
}
public static String  _service_destroy() throws Exception{
 //BA.debugLineNum = 47;BA.debugLine="Sub Service_Destroy";
 //BA.debugLineNum = 49;BA.debugLine="End Sub";
return "";
}
public static String  _service_start(anywheresoftware.b4a.objects.IntentWrapper _startingintent) throws Exception{
 //BA.debugLineNum = 38;BA.debugLine="Sub Service_Start (StartingIntent As Intent)";
 //BA.debugLineNum = 39;BA.debugLine="ConnectAndReconnect";
_connectandreconnect();
 //BA.debugLineNum = 41;BA.debugLine="End Sub";
return "";
}
public static String  _setls() throws Exception{
 //BA.debugLineNum = 113;BA.debugLine="Public Sub SetLs";
 //BA.debugLineNum = 116;BA.debugLine="End Sub";
return "";
}
public static String  _setsubbase(String _basename) throws Exception{
 //BA.debugLineNum = 85;BA.debugLine="Private Sub SetSubBase(baseName As String)";
 //BA.debugLineNum = 86;BA.debugLine="mqttBase = baseName";
_mqttbase = _basename;
 //BA.debugLineNum = 87;BA.debugLine="End Sub";
return "";
}
public static String  _setsubgetunits() throws Exception{
 //BA.debugLineNum = 97;BA.debugLine="Private Sub SetSubGetUnits";
 //BA.debugLineNum = 98;BA.debugLine="mqttGetUnits = $\"${mqttName}/${mqttBase}\"$";
_mqttgetunits = (""+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_mqttname))+"/"+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_mqttbase))+"");
 //BA.debugLineNum = 99;BA.debugLine="End Sub";
return "";
}
public static String  _setsubstring() throws Exception{
 //BA.debugLineNum = 80;BA.debugLine="Public Sub SetSubString";
 //BA.debugLineNum = 81;BA.debugLine="SubString = $\"${mqttName}/${mqttBase}/${mqttUnit}";
_substring = (""+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_mqttname))+"/"+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_mqttbase))+"/"+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_mqttunit))+"");
 //BA.debugLineNum = 82;BA.debugLine="End Sub";
return "";
}
public static String  _setunit(String _name) throws Exception{
 //BA.debugLineNum = 93;BA.debugLine="Private Sub SetUnit(name As String)";
 //BA.debugLineNum = 94;BA.debugLine="mqttUnit = name";
_mqttunit = _name;
 //BA.debugLineNum = 95;BA.debugLine="End Sub";
return "";
}
}
