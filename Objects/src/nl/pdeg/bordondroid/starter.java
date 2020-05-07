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
public static anywheresoftware.b4j.objects.MqttBroker _broker = null;
public static anywheresoftware.b4j.objects.MqttAsyncClientWrapper _client = null;
public static int _port = 0;
public static int _discoverport = 0;
public static anywheresoftware.b4a.randomaccessfile.B4XSerializator _serializator = null;
public static boolean _connected = false;
public static boolean _brokerstarted = false;
public static anywheresoftware.b4a.objects.collections.List _users = null;
public static boolean _isserver = false;
public static String _name = "";
public static String _discoveredserver = "";
public static anywheresoftware.b4a.objects.SocketWrapper.UDPSocket _autodiscover = null;
public static anywheresoftware.b4a.objects.Timer _broadcasttimer = null;
public static anywheresoftware.b4a.objects.SocketWrapper.ServerSocketWrapper _server = null;
public nl.pdeg.bordondroid.main _main = null;
public nl.pdeg.bordondroid.chat _chat = null;
public nl.pdeg.bordondroid.serverboard _serverboard = null;
public static String  _connect(boolean _asserver) throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "connect", false))
	 {return ((String) Debug.delegate(processBA, "connect", new Object[] {_asserver}));}
String _host = "";
anywheresoftware.b4j.objects.MqttAsyncClientWrapper.MqttConnectOptionsWrapper _mo = null;
RDebugUtils.currentLine=917504;
 //BA.debugLineNum = 917504;BA.debugLine="Public Sub Connect (AsServer As Boolean)";
RDebugUtils.currentLine=917505;
 //BA.debugLineNum = 917505;BA.debugLine="Dim host As String = DiscoveredServer";
_host = _discoveredserver;
RDebugUtils.currentLine=917506;
 //BA.debugLineNum = 917506;BA.debugLine="isServer = AsServer";
_isserver = _asserver;
RDebugUtils.currentLine=917507;
 //BA.debugLineNum = 917507;BA.debugLine="If isServer Then";
if (_isserver) { 
RDebugUtils.currentLine=917508;
 //BA.debugLineNum = 917508;BA.debugLine="If brokerStarted = False Then";
if (_brokerstarted==anywheresoftware.b4a.keywords.Common.False) { 
RDebugUtils.currentLine=917509;
 //BA.debugLineNum = 917509;BA.debugLine="broker.Start";
_broker.Start();
RDebugUtils.currentLine=917510;
 //BA.debugLineNum = 917510;BA.debugLine="brokerStarted = True";
_brokerstarted = anywheresoftware.b4a.keywords.Common.True;
 };
RDebugUtils.currentLine=917512;
 //BA.debugLineNum = 917512;BA.debugLine="users.Clear";
_users.Clear();
RDebugUtils.currentLine=917513;
 //BA.debugLineNum = 917513;BA.debugLine="host = \"127.0.0.1\"";
_host = "127.0.0.1";
 };
RDebugUtils.currentLine=917515;
 //BA.debugLineNum = 917515;BA.debugLine="BroadcastTimer.Enabled = isServer";
_broadcasttimer.setEnabled(_isserver);
RDebugUtils.currentLine=917516;
 //BA.debugLineNum = 917516;BA.debugLine="If connected Then client.Close";
if (_connected) { 
_client.Close();};
RDebugUtils.currentLine=917518;
 //BA.debugLineNum = 917518;BA.debugLine="client.Initialize(\"client\", $\"tcp://${host}:${por";
_client.Initialize(processBA,"client",("tcp://"+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_host))+":"+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_port))+""),"android"+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (10000000))));
RDebugUtils.currentLine=917519;
 //BA.debugLineNum = 917519;BA.debugLine="Dim mo As MqttConnectOptions";
_mo = new anywheresoftware.b4j.objects.MqttAsyncClientWrapper.MqttConnectOptionsWrapper();
RDebugUtils.currentLine=917520;
 //BA.debugLineNum = 917520;BA.debugLine="mo.Initialize(\"\", \"\")";
_mo.Initialize("","");
RDebugUtils.currentLine=917522;
 //BA.debugLineNum = 917522;BA.debugLine="mo.SetLastWill(\"all/disconnect\", serializator.Con";
_mo.SetLastWill("all/disconnect",_serializator.ConvertObjectToBytes((Object)(_name)),(int) (0),anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=917523;
 //BA.debugLineNum = 917523;BA.debugLine="client.Connect2(mo)";
_client.Connect2((org.eclipse.paho.client.mqttv3.MqttConnectOptions)(_mo.getObject()));
RDebugUtils.currentLine=917524;
 //BA.debugLineNum = 917524;BA.debugLine="End Sub";
return "";
}
public static boolean  _application_error(anywheresoftware.b4a.objects.B4AException _error,String _stacktrace) throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "application_error", false))
	 {return ((Boolean) Debug.delegate(processBA, "application_error", new Object[] {_error,_stacktrace}));}
RDebugUtils.currentLine=1441792;
 //BA.debugLineNum = 1441792;BA.debugLine="Sub Application_Error (Error As Exception, StackTr";
RDebugUtils.currentLine=1441793;
 //BA.debugLineNum = 1441793;BA.debugLine="Return True";
if (true) return anywheresoftware.b4a.keywords.Common.True;
RDebugUtils.currentLine=1441794;
 //BA.debugLineNum = 1441794;BA.debugLine="End Sub";
return false;
}
public static String  _autodiscover_packetarrived(anywheresoftware.b4a.objects.SocketWrapper.UDPSocket.UDPPacket _packet) throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "autodiscover_packetarrived", false))
	 {return ((String) Debug.delegate(processBA, "autodiscover_packetarrived", new Object[] {_packet}));}
anywheresoftware.b4a.agraham.byteconverter.ByteConverter _bc = null;
byte[] _data = null;
String _ds = "";
RDebugUtils.currentLine=786432;
 //BA.debugLineNum = 786432;BA.debugLine="Private Sub AutoDiscover_PacketArrived (Packet As";
RDebugUtils.currentLine=786433;
 //BA.debugLineNum = 786433;BA.debugLine="Try";
try {RDebugUtils.currentLine=786434;
 //BA.debugLineNum = 786434;BA.debugLine="Dim bc As ByteConverter";
_bc = new anywheresoftware.b4a.agraham.byteconverter.ByteConverter();
RDebugUtils.currentLine=786435;
 //BA.debugLineNum = 786435;BA.debugLine="Dim data(Packet.Length) As Byte";
_data = new byte[_packet.getLength()];
;
RDebugUtils.currentLine=786436;
 //BA.debugLineNum = 786436;BA.debugLine="bc.ArrayCopy(Packet.Data, Packet.Offset, data, 0";
_bc.ArrayCopy((Object)(_packet.getData()),_packet.getOffset(),(Object)(_data),(int) (0),_packet.getLength());
RDebugUtils.currentLine=786437;
 //BA.debugLineNum = 786437;BA.debugLine="Dim ds As String = serializator.ConvertBytesToOb";
_ds = BA.ObjectToString(_serializator.ConvertBytesToObject(_data));
RDebugUtils.currentLine=786438;
 //BA.debugLineNum = 786438;BA.debugLine="Log(\"Discovered server: \" & DiscoveredServer)";
anywheresoftware.b4a.keywords.Common.LogImpl("2786438","Discovered server: "+_discoveredserver,0);
RDebugUtils.currentLine=786439;
 //BA.debugLineNum = 786439;BA.debugLine="If ds <> DiscoveredServer Then";
if ((_ds).equals(_discoveredserver) == false) { 
RDebugUtils.currentLine=786440;
 //BA.debugLineNum = 786440;BA.debugLine="DiscoveredServer = ds";
_discoveredserver = _ds;
RDebugUtils.currentLine=786441;
 //BA.debugLineNum = 786441;BA.debugLine="Log(\"Discovered server: \" & DiscoveredServer)";
anywheresoftware.b4a.keywords.Common.LogImpl("2786441","Discovered server: "+_discoveredserver,0);
RDebugUtils.currentLine=786442;
 //BA.debugLineNum = 786442;BA.debugLine="If DiscoveredServer <> \"\" Then";
if ((_discoveredserver).equals("") == false) { 
RDebugUtils.currentLine=786443;
 //BA.debugLineNum = 786443;BA.debugLine="Connect(False)";
_connect(anywheresoftware.b4a.keywords.Common.False);
 };
 };
 } 
       catch (Exception e15) {
			processBA.setLastException(e15);RDebugUtils.currentLine=786448;
 //BA.debugLineNum = 786448;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("2786448",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(processBA)),0);
 };
RDebugUtils.currentLine=786450;
 //BA.debugLineNum = 786450;BA.debugLine="End Sub";
return "";
}
public static String  _broadcasttimer_tick() throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "broadcasttimer_tick", false))
	 {return ((String) Debug.delegate(processBA, "broadcasttimer_tick", null));}
String _address = "";
anywheresoftware.b4a.objects.SocketWrapper.UDPSocket.UDPPacket _up = null;
RDebugUtils.currentLine=720896;
 //BA.debugLineNum = 720896;BA.debugLine="Private Sub BroadcastTimer_Tick";
RDebugUtils.currentLine=720897;
 //BA.debugLineNum = 720897;BA.debugLine="Dim address As String = GetBroadcastAddress";
_address = _getbroadcastaddress();
RDebugUtils.currentLine=720898;
 //BA.debugLineNum = 720898;BA.debugLine="If address <> \"\" Then";
if ((_address).equals("") == false) { 
RDebugUtils.currentLine=720899;
 //BA.debugLineNum = 720899;BA.debugLine="Dim up As UDPPacket";
_up = new anywheresoftware.b4a.objects.SocketWrapper.UDPSocket.UDPPacket();
RDebugUtils.currentLine=720900;
 //BA.debugLineNum = 720900;BA.debugLine="up.Initialize(serializator.ConvertObjectToBytes(";
_up.Initialize(_serializator.ConvertObjectToBytes((Object)(_server.GetMyWifiIP())),_address,_discoverport);
RDebugUtils.currentLine=720901;
 //BA.debugLineNum = 720901;BA.debugLine="autodiscover.Send(up)";
_autodiscover.Send(_up);
 };
RDebugUtils.currentLine=720903;
 //BA.debugLineNum = 720903;BA.debugLine="End Sub";
return "";
}
public static String  _getbroadcastaddress() throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "getbroadcastaddress", false))
	 {return ((String) Debug.delegate(processBA, "getbroadcastaddress", null));}
anywheresoftware.b4j.object.JavaObject _niiterator = null;
anywheresoftware.b4j.object.JavaObject _ni = null;
anywheresoftware.b4a.objects.collections.List _addresses = null;
anywheresoftware.b4j.object.JavaObject _ia = null;
Object _broadcast = null;
String _b = "";
RDebugUtils.currentLine=1376256;
 //BA.debugLineNum = 1376256;BA.debugLine="Private Sub GetBroadcastAddress As String";
RDebugUtils.currentLine=1376257;
 //BA.debugLineNum = 1376257;BA.debugLine="Dim niIterator As JavaObject";
_niiterator = new anywheresoftware.b4j.object.JavaObject();
RDebugUtils.currentLine=1376258;
 //BA.debugLineNum = 1376258;BA.debugLine="niIterator = niIterator.InitializeStatic(\"java.";
_niiterator.setObject((java.lang.Object)(_niiterator.InitializeStatic("java.net.NetworkInterface").RunMethod("getNetworkInterfaces",(Object[])(anywheresoftware.b4a.keywords.Common.Null))));
RDebugUtils.currentLine=1376259;
 //BA.debugLineNum = 1376259;BA.debugLine="Do While niIterator.RunMethod(\"hasMoreElements\"";
while (BA.ObjectToBoolean(_niiterator.RunMethod("hasMoreElements",(Object[])(anywheresoftware.b4a.keywords.Common.Null)))) {
RDebugUtils.currentLine=1376260;
 //BA.debugLineNum = 1376260;BA.debugLine="Dim ni As JavaObject = niIterator.RunMethod(\"";
_ni = new anywheresoftware.b4j.object.JavaObject();
_ni.setObject((java.lang.Object)(_niiterator.RunMethod("nextElement",(Object[])(anywheresoftware.b4a.keywords.Common.Null))));
RDebugUtils.currentLine=1376261;
 //BA.debugLineNum = 1376261;BA.debugLine="If ni.RunMethod(\"isLoopback\", Null) = False T";
if ((_ni.RunMethod("isLoopback",(Object[])(anywheresoftware.b4a.keywords.Common.Null))).equals((Object)(anywheresoftware.b4a.keywords.Common.False))) { 
RDebugUtils.currentLine=1376262;
 //BA.debugLineNum = 1376262;BA.debugLine="Dim addresses As List = ni.RunMethod(\"getIn";
_addresses = new anywheresoftware.b4a.objects.collections.List();
_addresses.setObject((java.util.List)(_ni.RunMethod("getInterfaceAddresses",(Object[])(anywheresoftware.b4a.keywords.Common.Null))));
RDebugUtils.currentLine=1376263;
 //BA.debugLineNum = 1376263;BA.debugLine="For Each ia As JavaObject In addresses";
_ia = new anywheresoftware.b4j.object.JavaObject();
{
final anywheresoftware.b4a.BA.IterableList group7 = _addresses;
final int groupLen7 = group7.getSize()
;int index7 = 0;
;
for (; index7 < groupLen7;index7++){
_ia.setObject((java.lang.Object)(group7.Get(index7)));
RDebugUtils.currentLine=1376264;
 //BA.debugLineNum = 1376264;BA.debugLine="Dim broadcast As Object = ia.RunMethod(\"g";
_broadcast = _ia.RunMethod("getBroadcast",(Object[])(anywheresoftware.b4a.keywords.Common.Null));
RDebugUtils.currentLine=1376265;
 //BA.debugLineNum = 1376265;BA.debugLine="If broadcast <> Null Then";
if (_broadcast!= null) { 
RDebugUtils.currentLine=1376266;
 //BA.debugLineNum = 1376266;BA.debugLine="Dim b As String = broadcast";
_b = BA.ObjectToString(_broadcast);
RDebugUtils.currentLine=1376267;
 //BA.debugLineNum = 1376267;BA.debugLine="Return b.SubString(1)";
if (true) return _b.substring((int) (1));
 };
 }
};
 };
 }
;
RDebugUtils.currentLine=1376272;
 //BA.debugLineNum = 1376272;BA.debugLine="Return \"\"";
if (true) return "";
RDebugUtils.currentLine=1376273;
 //BA.debugLineNum = 1376273;BA.debugLine="End Sub";
return "";
}
public static String  _client_connected(boolean _success) throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "client_connected", false))
	 {return ((String) Debug.delegate(processBA, "client_connected", new Object[] {_success}));}
RDebugUtils.currentLine=983040;
 //BA.debugLineNum = 983040;BA.debugLine="Private Sub client_Connected (Success As Boolean)";
RDebugUtils.currentLine=983041;
 //BA.debugLineNum = 983041;BA.debugLine="Log($\"Connected: ${Success}\"$)";
anywheresoftware.b4a.keywords.Common.LogImpl("2983041",("Connected: "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_success))+""),0);
RDebugUtils.currentLine=983042;
 //BA.debugLineNum = 983042;BA.debugLine="If Success Then";
if (_success) { 
RDebugUtils.currentLine=983043;
 //BA.debugLineNum = 983043;BA.debugLine="connected = True";
_connected = anywheresoftware.b4a.keywords.Common.True;
RDebugUtils.currentLine=983044;
 //BA.debugLineNum = 983044;BA.debugLine="client.Subscribe(\"all/#\", 0)";
_client.Subscribe("all/#",(int) (0));
RDebugUtils.currentLine=983045;
 //BA.debugLineNum = 983045;BA.debugLine="client.Publish2(\"all/connect\", serializator.Conv";
_client.Publish2("all/connect",_serializator.ConvertObjectToBytes((Object)(_name)),(int) (0),anywheresoftware.b4a.keywords.Common.False);
 }else {
RDebugUtils.currentLine=983047;
 //BA.debugLineNum = 983047;BA.debugLine="ToastMessageShow(\"Error connecting: \" & LastExce";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Error connecting: "+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(processBA))),anywheresoftware.b4a.keywords.Common.True);
 };
RDebugUtils.currentLine=983049;
 //BA.debugLineNum = 983049;BA.debugLine="End Sub";
return "";
}
public static String  _client_disconnected() throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "client_disconnected", false))
	 {return ((String) Debug.delegate(processBA, "client_disconnected", null));}
RDebugUtils.currentLine=1310720;
 //BA.debugLineNum = 1310720;BA.debugLine="Private Sub client_Disconnected";
RDebugUtils.currentLine=1310721;
 //BA.debugLineNum = 1310721;BA.debugLine="connected = False";
_connected = anywheresoftware.b4a.keywords.Common.False;
RDebugUtils.currentLine=1310723;
 //BA.debugLineNum = 1310723;BA.debugLine="If isServer Then";
if (_isserver) { 
RDebugUtils.currentLine=1310724;
 //BA.debugLineNum = 1310724;BA.debugLine="broker.Stop";
_broker.Stop();
RDebugUtils.currentLine=1310725;
 //BA.debugLineNum = 1310725;BA.debugLine="brokerStarted = False";
_brokerstarted = anywheresoftware.b4a.keywords.Common.False;
 };
RDebugUtils.currentLine=1310727;
 //BA.debugLineNum = 1310727;BA.debugLine="End Sub";
return "";
}
public static String  _client_messagearrived(String _topic,byte[] _payload) throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "client_messagearrived", false))
	 {return ((String) Debug.delegate(processBA, "client_messagearrived", new Object[] {_topic,_payload}));}
Object _receivedobject = null;
String _newuser = "";
int _index = 0;
anywheresoftware.b4a.objects.collections.List _newusers = null;
nl.pdeg.bordondroid.main._message _m = null;
RDebugUtils.currentLine=1048576;
 //BA.debugLineNum = 1048576;BA.debugLine="Private Sub client_MessageArrived (Topic As String";
RDebugUtils.currentLine=1048577;
 //BA.debugLineNum = 1048577;BA.debugLine="Dim receivedObject As Object = serializator.Conve";
_receivedobject = _serializator.ConvertBytesToObject(_payload);
RDebugUtils.currentLine=1048578;
 //BA.debugLineNum = 1048578;BA.debugLine="If Topic = \"all/connect\" Or Topic = \"all/disconne";
if ((_topic).equals("all/connect") || (_topic).equals("all/disconnect")) { 
RDebugUtils.currentLine=1048580;
 //BA.debugLineNum = 1048580;BA.debugLine="Dim newUser As String = receivedObject";
_newuser = BA.ObjectToString(_receivedobject);
RDebugUtils.currentLine=1048581;
 //BA.debugLineNum = 1048581;BA.debugLine="If isServer Then";
if (_isserver) { 
RDebugUtils.currentLine=1048582;
 //BA.debugLineNum = 1048582;BA.debugLine="Log($\"${Topic}: ${newUser}\"$)";
anywheresoftware.b4a.keywords.Common.LogImpl("21048582",(""+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_topic))+": "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_newuser))+""),0);
RDebugUtils.currentLine=1048583;
 //BA.debugLineNum = 1048583;BA.debugLine="Dim index As Int = users.IndexOf(newUser)";
_index = _users.IndexOf((Object)(_newuser));
RDebugUtils.currentLine=1048584;
 //BA.debugLineNum = 1048584;BA.debugLine="If Topic.EndsWith(\"connect\") And index = -1 The";
if (_topic.endsWith("connect") && _index==-1) { 
_users.Add((Object)(_newuser));};
RDebugUtils.currentLine=1048585;
 //BA.debugLineNum = 1048585;BA.debugLine="If Topic.EndsWith(\"disconnect\") And index >= 0";
if (_topic.endsWith("disconnect") && _index>=0) { 
_users.RemoveAt(_index);};
RDebugUtils.currentLine=1048586;
 //BA.debugLineNum = 1048586;BA.debugLine="client.Publish2(\"all/users\", serializator.Conve";
_client.Publish2("all/users",_serializator.ConvertObjectToBytes((Object)(_users.getObject())),(int) (0),anywheresoftware.b4a.keywords.Common.False);
 };
 }else 
{RDebugUtils.currentLine=1048588;
 //BA.debugLineNum = 1048588;BA.debugLine="Else if Topic = \"all/users\" Then";
if ((_topic).equals("all/users")) { 
RDebugUtils.currentLine=1048589;
 //BA.debugLineNum = 1048589;BA.debugLine="Dim newUsers As List = receivedObject";
_newusers = new anywheresoftware.b4a.objects.collections.List();
_newusers.setObject((java.util.List)(_receivedobject));
 }else {
RDebugUtils.currentLine=1048592;
 //BA.debugLineNum = 1048592;BA.debugLine="Dim m As Message = receivedObject";
_m = (nl.pdeg.bordondroid.main._message)(_receivedobject);
RDebugUtils.currentLine=1048595;
 //BA.debugLineNum = 1048595;BA.debugLine="CallSub2(ServerBoard, \"UpdateBordWhenClient\", m)";
anywheresoftware.b4a.keywords.Common.CallSubDebug2(processBA,(Object)(mostCurrent._serverboard.getObject()),"UpdateBordWhenClient",(Object)(_m));
 }}
;
RDebugUtils.currentLine=1048598;
 //BA.debugLineNum = 1048598;BA.debugLine="End Sub";
return "";
}
public static byte[]  _createmessage(String _body) throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "createmessage", false))
	 {return ((byte[]) Debug.delegate(processBA, "createmessage", new Object[] {_body}));}
nl.pdeg.bordondroid.main._message _m = null;
RDebugUtils.currentLine=1245184;
 //BA.debugLineNum = 1245184;BA.debugLine="Private Sub CreateMessage(Body As String) As Byte(";
RDebugUtils.currentLine=1245185;
 //BA.debugLineNum = 1245185;BA.debugLine="Dim m As Message";
_m = new nl.pdeg.bordondroid.main._message();
RDebugUtils.currentLine=1245186;
 //BA.debugLineNum = 1245186;BA.debugLine="m.Initialize";
_m.Initialize();
RDebugUtils.currentLine=1245187;
 //BA.debugLineNum = 1245187;BA.debugLine="m.Body = Body";
_m.Body /*String*/  = _body;
RDebugUtils.currentLine=1245188;
 //BA.debugLineNum = 1245188;BA.debugLine="m.From = Name";
_m.From /*String*/  = _name;
RDebugUtils.currentLine=1245189;
 //BA.debugLineNum = 1245189;BA.debugLine="Return serializator.ConvertObjectToBytes(m)";
if (true) return _serializator.ConvertObjectToBytes((Object)(_m));
RDebugUtils.currentLine=1245190;
 //BA.debugLineNum = 1245190;BA.debugLine="End Sub";
return null;
}
public static String  _disconnect() throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "disconnect", false))
	 {return ((String) Debug.delegate(processBA, "disconnect", null));}
RDebugUtils.currentLine=1179648;
 //BA.debugLineNum = 1179648;BA.debugLine="Public Sub Disconnect";
RDebugUtils.currentLine=1179649;
 //BA.debugLineNum = 1179649;BA.debugLine="BroadcastTimer.Enabled = False";
_broadcasttimer.setEnabled(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=1179650;
 //BA.debugLineNum = 1179650;BA.debugLine="DiscoveredServer = \"\"";
_discoveredserver = "";
RDebugUtils.currentLine=1179652;
 //BA.debugLineNum = 1179652;BA.debugLine="If connected Then";
if (_connected) { 
RDebugUtils.currentLine=1179653;
 //BA.debugLineNum = 1179653;BA.debugLine="client.Publish2(\"all/disconnect\", serializator.C";
_client.Publish2("all/disconnect",_serializator.ConvertObjectToBytes((Object)(_name)),(int) (0),anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=1179654;
 //BA.debugLineNum = 1179654;BA.debugLine="client.Close";
_client.Close();
 };
RDebugUtils.currentLine=1179656;
 //BA.debugLineNum = 1179656;BA.debugLine="End Sub";
return "";
}
public static String  _sendmessage(String _body) throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "sendmessage", false))
	 {return ((String) Debug.delegate(processBA, "sendmessage", new Object[] {_body}));}
RDebugUtils.currentLine=1114112;
 //BA.debugLineNum = 1114112;BA.debugLine="Public Sub SendMessage(Body As String)";
RDebugUtils.currentLine=1114113;
 //BA.debugLineNum = 1114113;BA.debugLine="If connected Then";
if (_connected) { 
RDebugUtils.currentLine=1114114;
 //BA.debugLineNum = 1114114;BA.debugLine="client.Publish2(\"all\", CreateMessage(Body), 0, F";
_client.Publish2("all",_createmessage(_body),(int) (0),anywheresoftware.b4a.keywords.Common.False);
 };
RDebugUtils.currentLine=1114116;
 //BA.debugLineNum = 1114116;BA.debugLine="End Sub";
return "";
}
public static String  _service_create() throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "service_create", false))
	 {return ((String) Debug.delegate(processBA, "service_create", null));}
RDebugUtils.currentLine=655360;
 //BA.debugLineNum = 655360;BA.debugLine="Sub Service_Create";
RDebugUtils.currentLine=655361;
 //BA.debugLineNum = 655361;BA.debugLine="broker.Initialize(\"\", port)";
_broker.Initialize("",_port);
RDebugUtils.currentLine=655362;
 //BA.debugLineNum = 655362;BA.debugLine="broker.DebugLog = False";
_broker.setDebugLog(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=655363;
 //BA.debugLineNum = 655363;BA.debugLine="users.Initialize";
_users.Initialize();
RDebugUtils.currentLine=655364;
 //BA.debugLineNum = 655364;BA.debugLine="autodiscover.Initialize(\"autodiscover\",discoverPo";
_autodiscover.Initialize(processBA,"autodiscover",_discoverport,(int) (8192));
RDebugUtils.currentLine=655365;
 //BA.debugLineNum = 655365;BA.debugLine="BroadcastTimer.Initialize(\"BroadcastTimer\", 1000)";
_broadcasttimer.Initialize(processBA,"BroadcastTimer",(long) (1000));
RDebugUtils.currentLine=655366;
 //BA.debugLineNum = 655366;BA.debugLine="End Sub";
return "";
}
public static String  _service_destroy() throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "service_destroy", false))
	 {return ((String) Debug.delegate(processBA, "service_destroy", null));}
RDebugUtils.currentLine=1507328;
 //BA.debugLineNum = 1507328;BA.debugLine="Sub Service_Destroy";
RDebugUtils.currentLine=1507330;
 //BA.debugLineNum = 1507330;BA.debugLine="End Sub";
return "";
}
public static String  _service_start(anywheresoftware.b4a.objects.IntentWrapper _startingintent) throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "service_start", false))
	 {return ((String) Debug.delegate(processBA, "service_start", new Object[] {_startingintent}));}
RDebugUtils.currentLine=851968;
 //BA.debugLineNum = 851968;BA.debugLine="Sub Service_Start (StartingIntent As Intent)";
RDebugUtils.currentLine=851970;
 //BA.debugLineNum = 851970;BA.debugLine="End Sub";
return "";
}
}