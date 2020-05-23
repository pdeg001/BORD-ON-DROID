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
public static anywheresoftware.b4j.objects.MqttAsyncClientWrapper _v6 = null;
public static int _v7 = 0;
public static String _v0 = "";
public static boolean _vv1 = false;
public static String _vv2 = "";
public static anywheresoftware.b4a.objects.collections.List _vv3 = null;
public static long _vv4 = 0L;
public static String _vv5 = "";
public static String _vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv0 = "";
public static String _vvvv6 = "";
public static String _vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv7 = "";
public static String _vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv4 = "";
public static String _vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv6 = "";
public static anywheresoftware.b4a.objects.RuntimePermissions _vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv1 = null;
public static boolean _vv6 = false;
public static boolean _vv7 = false;
public static int _vv0 = 0;
public static String _v5 = "";
public static String _vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv5 = "";
public static String _vvv1 = "";
public static String _vvv2 = "";
public static String _vvv3 = "";
public static String _vvv4 = "";
public static String _vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv2 = "";
public static boolean _vvv5 = false;
public static String _vvv6 = "";
public static boolean _vvv7 = false;
public static long _vvv0 = 0L;
public static boolean _vvvv1 = false;
public b4a.example.dateutils _vvvvvvv2 = null;
public nl.pdeg.bordondroid.main _vvvvvvv3 = null;
public nl.pdeg.bordondroid.locations _vvvvvvv4 = null;
public nl.pdeg.bordondroid.selectlocation _vvvvvvv6 = null;
public nl.pdeg.bordondroid.serverboard _vvvvvvv7 = null;
public static boolean  _application_error(anywheresoftware.b4a.objects.B4AException _error,String _stacktrace) throws Exception{
 //BA.debugLineNum = 49;BA.debugLine="Sub Application_Error (Error As Exception, StackTr";
 //BA.debugLineNum = 50;BA.debugLine="Return True";
if (true) return anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 51;BA.debugLine="End Sub";
return false;
}
public static void  _vvvv2() throws Exception{
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
 //BA.debugLineNum = 58;BA.debugLine="Do While working";
if (true) break;

case 1:
//do while
this.state = 32;
while (parent._vvv7) {
this.state = 3;
if (true) break;
}
if (true) break;

case 3:
//C
this.state = 4;
 //BA.debugLineNum = 59;BA.debugLine="If mqtt.IsInitialized Then mqtt.Close";
if (true) break;

case 4:
//if
this.state = 9;
if (parent._v6.IsInitialized()) { 
this.state = 6;
;}if (true) break;

case 6:
//C
this.state = 9;
parent._v6.Close();
if (true) break;

case 9:
//C
this.state = 10;
;
 //BA.debugLineNum = 60;BA.debugLine="mqtt.Initialize(\"mqtt\", \"tcp://pdeg3005.mynetgea";
parent._v6.Initialize(processBA,"mqtt","tcp://pdeg3005.mynetgear.com:1883","pdeg_"+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Rnd((int) (0),(int) (999999999))));
 //BA.debugLineNum = 61;BA.debugLine="Dim mo As MqttConnectOptions";
_mo = new anywheresoftware.b4j.objects.MqttAsyncClientWrapper.MqttConnectOptionsWrapper();
 //BA.debugLineNum = 62;BA.debugLine="mo.Initialize(\"\", \"\")";
_mo.Initialize("","");
 //BA.debugLineNum = 64;BA.debugLine="mqtt.Connect2(mo)";
parent._v6.Connect2((org.eclipse.paho.client.mqttv3.MqttConnectOptions)(_mo.getObject()));
 //BA.debugLineNum = 65;BA.debugLine="Wait For Mqtt_Connected (Success As Boolean)";
anywheresoftware.b4a.keywords.Common.WaitFor("mqtt_connected", processBA, this, null);
this.state = 33;
return;
case 33:
//C
this.state = 10;
_success = (Boolean) result[0];
;
 //BA.debugLineNum = 66;BA.debugLine="If Success Then";
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
 //BA.debugLineNum = 70;BA.debugLine="CallSub(Main, \"getBaseList\")";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)(parent.mostCurrent._vvvvvvv3.getObject()),"getBaseList");
 //BA.debugLineNum = 72;BA.debugLine="Do While working And mqtt.Connected";
if (true) break;

case 13:
//do while
this.state = 16;
while (parent._vvv7 && parent._v6.getConnected()) {
this.state = 15;
if (true) break;
}
if (true) break;

case 15:
//C
this.state = 13;
 //BA.debugLineNum = 73;BA.debugLine="mqtt.Publish2(\"ping\", Array As Byte(0), 1, Fal";
parent._v6.Publish2("ping",new byte[]{(byte) (0)},(int) (1),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 74;BA.debugLine="Sleep(5000)";
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
 //BA.debugLineNum = 78;BA.debugLine="CallSub(ServerBoard, \"ConnectionLost\")";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)(parent.mostCurrent._vvvvvvv7.getObject()),"ConnectionLost");
 //BA.debugLineNum = 79;BA.debugLine="CallSub(Main, \"ShowNotConnectedToBroker\")";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)(parent.mostCurrent._vvvvvvv3.getObject()),"ShowNotConnectedToBroker");
 //BA.debugLineNum = 80;BA.debugLine="If mqtt.IsInitialized Then mqtt.Close";
if (true) break;

case 17:
//if
this.state = 22;
if (parent._v6.IsInitialized()) { 
this.state = 19;
;}if (true) break;

case 19:
//C
this.state = 22;
parent._v6.Close();
if (true) break;

case 22:
//C
this.state = 31;
;
 if (true) break;

case 24:
//C
this.state = 25;
 //BA.debugLineNum = 83;BA.debugLine="If mqtt.IsInitialized Then mqtt.Close";
if (true) break;

case 25:
//if
this.state = 30;
if (parent._v6.IsInitialized()) { 
this.state = 27;
;}if (true) break;

case 27:
//C
this.state = 30;
parent._v6.Close();
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
 //BA.debugLineNum = 85;BA.debugLine="Sleep(5000)";
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
 //BA.debugLineNum = 87;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static void  _mqtt_connected(boolean _success) throws Exception{
}
public static String  _vvvv3() throws Exception{
 //BA.debugLineNum = 134;BA.debugLine="Public Sub GetBase As String";
 //BA.debugLineNum = 135;BA.debugLine="Return mqttGetUnits '$\"${mqttName}/${mqttBase}/\"$";
if (true) return _vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv4;
 //BA.debugLineNum = 136;BA.debugLine="End Sub";
return "";
}
public static String  _getbasefilepath() throws Exception{
 //BA.debugLineNum = 138;BA.debugLine="Private Sub GetBaseFilePath As String";
 //BA.debugLineNum = 139;BA.debugLine="Return baseFilePath";
if (true) return _vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv5;
 //BA.debugLineNum = 140;BA.debugLine="End Sub";
return "";
}
public static String  _vvvv4() throws Exception{
 //BA.debugLineNum = 93;BA.debugLine="Sub GetLastWill As String";
 //BA.debugLineNum = 94;BA.debugLine="Return mqttLastWill";
if (true) return _vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv6;
 //BA.debugLineNum = 95;BA.debugLine="End Sub";
return "";
}
public static String  _getunit() throws Exception{
 //BA.debugLineNum = 122;BA.debugLine="Private Sub GetUnit As String";
 //BA.debugLineNum = 123;BA.debugLine="Return mqttUnit";
if (true) return _vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv7;
 //BA.debugLineNum = 124;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 6;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 7;BA.debugLine="Dim mqtt As MqttClient";
_v6 = new anywheresoftware.b4j.objects.MqttAsyncClientWrapper();
 //BA.debugLineNum = 8;BA.debugLine="Public const port As Int = 1883";
_v7 = (int) (1883);
 //BA.debugLineNum = 9;BA.debugLine="Public const host As String = \"pdeg3005.mynetgear";
_v0 = BA.__b (new byte[] {46,39,35,127,120,100,62,108,125,50,125,99,58,53,107,127,33,42,35,122,50,115}, 151471);
 //BA.debugLineNum = 10;BA.debugLine="Public connected As Boolean";
_vv1 = false;
 //BA.debugLineNum = 11;BA.debugLine="Public DiscoveredServer As String";
_vv2 = "";
 //BA.debugLineNum = 12;BA.debugLine="Public serverList As List";
_vv3 = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 13;BA.debugLine="Public serverDied As Long = 30000";
_vv4 = (long) (30000);
 //BA.debugLineNum = 14;BA.debugLine="Public selectedBordName As String";
_vv5 = "";
 //BA.debugLineNum = 15;BA.debugLine="Private mqttName As String = \"pdeg\"";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv0 = BA.__b (new byte[] {46,39,-33,58}, 287248);
 //BA.debugLineNum = 16;BA.debugLine="Private mqttBase As String";
_vvvv6 = "";
 //BA.debugLineNum = 17;BA.debugLine="Private mqttUnit As String";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv7 = "";
 //BA.debugLineNum = 18;BA.debugLine="Private mqttGetUnits As String";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv4 = "";
 //BA.debugLineNum = 19;BA.debugLine="Private mqttLastWill As String";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv6 = "";
 //BA.debugLineNum = 20;BA.debugLine="Private rp As RuntimePermissions";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv1 = new anywheresoftware.b4a.objects.RuntimePermissions();
 //BA.debugLineNum = 21;BA.debugLine="Public mqttGetBordsActive, mqttGetBordDataActive";
_vv6 = false;
_vv7 = false;
 //BA.debugLineNum = 22;BA.debugLine="Public diedIndex As Int = -1";
_vv0 = (int) (-1);
 //BA.debugLineNum = 23;BA.debugLine="Private baseFile, baseFilePath As String";
_v5 = "";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv5 = "";
 //BA.debugLineNum = 24;BA.debugLine="Public SubString, subDisconnectString, selectedLo";
_vvv1 = "";
_vvv2 = "";
_vvv3 = "";
_vvv4 = "";
 //BA.debugLineNum = 25;BA.debugLine="Private storeFolder As String";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv2 = "";
 //BA.debugLineNum = 26;BA.debugLine="Public testBaseName As Boolean = False";
_vvv5 = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 27;BA.debugLine="Public appVersion As String";
_vvv6 = "";
 //BA.debugLineNum = 29;BA.debugLine="Dim working As Boolean";
_vvv7 = false;
 //BA.debugLineNum = 30;BA.debugLine="Public firstConnectTime As Long";
_vvv0 = 0L;
 //BA.debugLineNum = 31;BA.debugLine="Public mainPaused As Boolean";
_vvvv1 = false;
 //BA.debugLineNum = 33;BA.debugLine="End Sub";
return "";
}
public static String  _service_create() throws Exception{
 //BA.debugLineNum = 35;BA.debugLine="Sub Service_Create";
 //BA.debugLineNum = 36;BA.debugLine="serverList.Initialize";
_vv3.Initialize();
 //BA.debugLineNum = 37;BA.debugLine="storeFolder = rp.GetSafeDirDefaultExternal(\"bod\")";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv2 = _vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv1.GetSafeDirDefaultExternal("bod");
 //BA.debugLineNum = 39;BA.debugLine="baseFile = \"bod.pdg\"";
_v5 = "bod.pdg";
 //BA.debugLineNum = 40;BA.debugLine="baseFilePath = File.Combine(storeFolder, baseFile";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv5 = anywheresoftware.b4a.keywords.Common.File.Combine(_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv2,_v5);
 //BA.debugLineNum = 41;BA.debugLine="working = True";
_vvv7 = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 42;BA.debugLine="End Sub";
return "";
}
public static String  _service_destroy() throws Exception{
 //BA.debugLineNum = 53;BA.debugLine="Sub Service_Destroy";
 //BA.debugLineNum = 55;BA.debugLine="End Sub";
return "";
}
public static String  _service_start(anywheresoftware.b4a.objects.IntentWrapper _startingintent) throws Exception{
 //BA.debugLineNum = 44;BA.debugLine="Sub Service_Start (StartingIntent As Intent)";
 //BA.debugLineNum = 45;BA.debugLine="ConnectAndReconnect";
_vvvv2();
 //BA.debugLineNum = 47;BA.debugLine="End Sub";
return "";
}
public static String  _setlastwill(String _lastwill) throws Exception{
 //BA.debugLineNum = 89;BA.debugLine="Sub SetLastWill(lastWill As String)";
 //BA.debugLineNum = 90;BA.debugLine="mqttLastWill = lastWill";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv6 = _lastwill;
 //BA.debugLineNum = 91;BA.debugLine="End Sub";
return "";
}
public static String  _vvvv5() throws Exception{
 //BA.debugLineNum = 142;BA.debugLine="Public Sub SetLs";
 //BA.debugLineNum = 145;BA.debugLine="End Sub";
return "";
}
public static String  _setsubbase(String _basename) throws Exception{
 //BA.debugLineNum = 110;BA.debugLine="Private Sub SetSubBase(baseName As String)";
 //BA.debugLineNum = 111;BA.debugLine="mqttBase = baseName";
_vvvv6 = _basename;
 //BA.debugLineNum = 112;BA.debugLine="End Sub";
return "";
}
public static String  _setsubstring() throws Exception{
 //BA.debugLineNum = 97;BA.debugLine="Public Sub SetSubString";
 //BA.debugLineNum = 98;BA.debugLine="SubString = $\"${mqttName}/${mqttBase}/recvdata_${";
_vvv1 = (""+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv0))+"/"+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_vvvv6))+"/recvdata_"+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv7))+"");
 //BA.debugLineNum = 99;BA.debugLine="End Sub";
return "";
}
public static String  _setsubstring2(String _unit) throws Exception{
 //BA.debugLineNum = 101;BA.debugLine="Public Sub SetSubString2(unit As String)";
 //BA.debugLineNum = 102;BA.debugLine="SubString = $\"${mqttName}/${mqttBase}${unit}\"$";
_vvv1 = (""+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv0))+"/"+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_vvvv6))+""+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_unit))+"");
 //BA.debugLineNum = 103;BA.debugLine="End Sub";
return "";
}
public static String  _setunit(String _name) throws Exception{
 //BA.debugLineNum = 118;BA.debugLine="Private Sub SetUnit(name As String)";
 //BA.debugLineNum = 119;BA.debugLine="mqttUnit = name";
_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv7 = _name;
 //BA.debugLineNum = 120;BA.debugLine="End Sub";
return "";
}
public static String  _setunsubscribestring2(String _unit) throws Exception{
 //BA.debugLineNum = 105;BA.debugLine="Public Sub SetUnsubscribeString2(unit As String)";
 //BA.debugLineNum = 106;BA.debugLine="subDisconnectString =  $\"${mqttName}/${mqttBase}$";
_vvv2 = (""+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv0))+"/"+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_vvvv6))+""+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_unit))+"/disconnect");
 //BA.debugLineNum = 107;BA.debugLine="End Sub";
return "";
}
}
