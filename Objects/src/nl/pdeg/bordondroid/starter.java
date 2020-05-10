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
public static anywheresoftware.b4j.objects.MqttAsyncClientWrapper _client = null;
public static int _port = 0;
public static int _discoverport = 0;
public static anywheresoftware.b4a.randomaccessfile.B4XSerializator _serializator = null;
public static boolean _connected = false;
public static anywheresoftware.b4a.objects.collections.List _users = null;
public static boolean _isserver = false;
public static String _name = "";
public static String _discoveredserver = "";
public static anywheresoftware.b4a.objects.SocketWrapper.UDPSocket _autodiscover = null;
public static anywheresoftware.b4a.objects.Timer _connectedtmr = null;
public static anywheresoftware.b4a.objects.SocketWrapper.ServerSocketWrapper _server = null;
public static anywheresoftware.b4a.objects.collections.List _serverlist = null;
public static long _serverdied = 0L;
public static anywheresoftware.b4a.phone.Phone _p = null;
public static String _selectedbordname = "";
public nl.pdeg.bordondroid.main _main = null;
public nl.pdeg.bordondroid.chat _chat = null;
public nl.pdeg.bordondroid.serverboard _serverboard = null;
public static boolean  _application_error(anywheresoftware.b4a.objects.B4AException _error,String _stacktrace) throws Exception{
 //BA.debugLineNum = 207;BA.debugLine="Sub Application_Error (Error As Exception, StackTr";
 //BA.debugLineNum = 208;BA.debugLine="Return True";
if (true) return anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 209;BA.debugLine="End Sub";
return false;
}
public static String  _autodiscover_packetarrived(anywheresoftware.b4a.objects.SocketWrapper.UDPSocket.UDPPacket _packet) throws Exception{
anywheresoftware.b4a.agraham.byteconverter.ByteConverter _bc = null;
byte[] _data = null;
Object[] _ds = null;
 //BA.debugLineNum = 56;BA.debugLine="Private Sub AutoDiscover_PacketArrived (Packet As";
 //BA.debugLineNum = 57;BA.debugLine="If connected Then Return";
if (_connected) { 
if (true) return "";};
 //BA.debugLineNum = 58;BA.debugLine="Try";
try { //BA.debugLineNum = 59;BA.debugLine="Dim bc As ByteConverter";
_bc = new anywheresoftware.b4a.agraham.byteconverter.ByteConverter();
 //BA.debugLineNum = 60;BA.debugLine="Dim data(Packet.Length) As Byte";
_data = new byte[_packet.getLength()];
;
 //BA.debugLineNum = 61;BA.debugLine="bc.ArrayCopy(Packet.Data, Packet.Offset, data, 0";
_bc.ArrayCopy((Object)(_packet.getData()),_packet.getOffset(),(Object)(_data),(int) (0),_packet.getLength());
 //BA.debugLineNum = 62;BA.debugLine="Dim ds() As Object = serializator.ConvertBytesTo";
_ds = (Object[])(_serializator.ConvertBytesToObject(_data));
 //BA.debugLineNum = 66;BA.debugLine="CallSub2(Main, \"CheckIpExits\", ds)";
anywheresoftware.b4a.keywords.Common.CallSubNew2(processBA,(Object)(mostCurrent._main.getObject()),"CheckIpExits",(Object)(_ds));
 } 
       catch (Exception e9) {
			processBA.setLastException(e9); //BA.debugLineNum = 76;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("21245204",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(processBA)),0);
 };
 //BA.debugLineNum = 78;BA.debugLine="End Sub";
return "";
}
public static String  _broadcasttimer_tick() throws Exception{
String _address = "";
anywheresoftware.b4a.objects.SocketWrapper.UDPSocket.UDPPacket _up = null;
 //BA.debugLineNum = 47;BA.debugLine="Private Sub BroadcastTimer_Tick";
 //BA.debugLineNum = 48;BA.debugLine="Dim address As String = GetBroadcastAddress";
_address = _getbroadcastaddress();
 //BA.debugLineNum = 49;BA.debugLine="If address <> \"\" Then";
if ((_address).equals("") == false) { 
 //BA.debugLineNum = 50;BA.debugLine="Dim up As UDPPacket";
_up = new anywheresoftware.b4a.objects.SocketWrapper.UDPSocket.UDPPacket();
 //BA.debugLineNum = 51;BA.debugLine="up.Initialize(serializator.ConvertObjectToBytes(";
_up.Initialize(_serializator.ConvertObjectToBytes((Object)(_server.GetMyWifiIP())),_address,_discoverport);
 //BA.debugLineNum = 52;BA.debugLine="autodiscover.Send(up)";
_autodiscover.Send(_up);
 };
 //BA.debugLineNum = 54;BA.debugLine="End Sub";
return "";
}
public static String  _client_connected(boolean _success) throws Exception{
 //BA.debugLineNum = 109;BA.debugLine="Private Sub client_Connected (Success As Boolean)";
 //BA.debugLineNum = 111;BA.debugLine="If Success Then";
if (_success) { 
 //BA.debugLineNum = 112;BA.debugLine="SendMessage(\"data please\")";
_sendmessage("data please");
 //BA.debugLineNum = 113;BA.debugLine="connectedTmr.Enabled = True";
_connectedtmr.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 114;BA.debugLine="connected = True";
_connected = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 115;BA.debugLine="client.Subscribe(\"all/#\", 0)";
_client.Subscribe("all/#",(int) (0));
 //BA.debugLineNum = 116;BA.debugLine="client.Publish2(\"all/connect\", serializator.Conv";
_client.Publish2("all/connect",_serializator.ConvertObjectToBytes((Object)(_name)),(int) (0),anywheresoftware.b4a.keywords.Common.False);
 }else {
 };
 //BA.debugLineNum = 122;BA.debugLine="End Sub";
return "";
}
public static String  _client_disconnected() throws Exception{
 //BA.debugLineNum = 176;BA.debugLine="Private Sub client_Disconnected";
 //BA.debugLineNum = 177;BA.debugLine="connected = False";
_connected = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 183;BA.debugLine="End Sub";
return "";
}
public static String  _client_messagearrived(String _topic,byte[] _payload) throws Exception{
Object _receivedobject = null;
String _newuser = "";
int _index = 0;
anywheresoftware.b4a.objects.collections.List _newusers = null;
nl.pdeg.bordondroid.main._message _m = null;
 //BA.debugLineNum = 124;BA.debugLine="Private Sub client_MessageArrived (Topic As String";
 //BA.debugLineNum = 125;BA.debugLine="Dim receivedObject As Object = serializator.Conve";
_receivedobject = _serializator.ConvertBytesToObject(_payload);
 //BA.debugLineNum = 126;BA.debugLine="If Topic = \"all/connect\" Or Topic = \"all/disconne";
if ((_topic).equals("all/connect") || (_topic).equals("all/disconnect")) { 
 //BA.debugLineNum = 128;BA.debugLine="Dim newUser As String = receivedObject";
_newuser = BA.ObjectToString(_receivedobject);
 //BA.debugLineNum = 129;BA.debugLine="Log(newUser)";
anywheresoftware.b4a.keywords.Common.LogImpl("21441797",_newuser,0);
 //BA.debugLineNum = 130;BA.debugLine="If isServer Then";
if (_isserver) { 
 //BA.debugLineNum = 131;BA.debugLine="Log($\"${Topic}: ${newUser}\"$)";
anywheresoftware.b4a.keywords.Common.LogImpl("21441799",(""+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_topic))+": "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_newuser))+""),0);
 //BA.debugLineNum = 132;BA.debugLine="Dim index As Int = users.IndexOf(newUser)";
_index = _users.IndexOf((Object)(_newuser));
 //BA.debugLineNum = 133;BA.debugLine="If Topic.EndsWith(\"connect\") And index = -1 The";
if (_topic.endsWith("connect") && _index==-1) { 
_users.Add((Object)(_newuser));};
 //BA.debugLineNum = 134;BA.debugLine="If Topic.EndsWith(\"disconnect\") And index >= 0";
if (_topic.endsWith("disconnect") && _index>=0) { 
_users.RemoveAt(_index);};
 //BA.debugLineNum = 135;BA.debugLine="client.Publish2(\"all/users\", serializator.Conve";
_client.Publish2("all/users",_serializator.ConvertObjectToBytes((Object)(_users.getObject())),(int) (0),anywheresoftware.b4a.keywords.Common.False);
 };
 }else if((_topic).equals("all/users")) { 
 //BA.debugLineNum = 138;BA.debugLine="Dim newUsers As List = receivedObject";
_newusers = new anywheresoftware.b4a.objects.collections.List();
_newusers.setObject((java.util.List)(_receivedobject));
 }else {
 //BA.debugLineNum = 143;BA.debugLine="Dim m As Message = receivedObject";
_m = (nl.pdeg.bordondroid.main._message)(_receivedobject);
 //BA.debugLineNum = 146;BA.debugLine="CallSub2(ServerBoard, \"UpdateBordWhenClient\", m)";
anywheresoftware.b4a.keywords.Common.CallSubNew2(processBA,(Object)(mostCurrent._serverboard.getObject()),"UpdateBordWhenClient",(Object)(_m));
 };
 //BA.debugLineNum = 149;BA.debugLine="End Sub";
return "";
}
public static String  _connect(boolean _asserver) throws Exception{
String _host = "";
anywheresoftware.b4j.objects.MqttAsyncClientWrapper.MqttConnectOptionsWrapper _mo = null;
 //BA.debugLineNum = 85;BA.debugLine="Public Sub Connect (AsServer As Boolean)";
 //BA.debugLineNum = 86;BA.debugLine="Dim host As String = DiscoveredServer";
_host = _discoveredserver;
 //BA.debugLineNum = 99;BA.debugLine="If connected Then client.Close";
if (_connected) { 
_client.Close();};
 //BA.debugLineNum = 101;BA.debugLine="client.Initialize(\"client\", $\"tcp://${host}:${por";
_client.Initialize(processBA,"client",("tcp://"+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_host))+":"+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_port))+""),"android"+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (10000000))));
 //BA.debugLineNum = 102;BA.debugLine="Dim mo As MqttConnectOptions";
_mo = new anywheresoftware.b4j.objects.MqttAsyncClientWrapper.MqttConnectOptionsWrapper();
 //BA.debugLineNum = 103;BA.debugLine="mo.Initialize(\"\", \"\")";
_mo.Initialize("","");
 //BA.debugLineNum = 105;BA.debugLine="mo.SetLastWill(\"all/disconnect\", serializator.Con";
_mo.SetLastWill("all/disconnect",_serializator.ConvertObjectToBytes((Object)(_p.getModel())),(int) (0),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 106;BA.debugLine="client.Connect2(mo)";
_client.Connect2((org.eclipse.paho.client.mqttv3.MqttConnectOptions)(_mo.getObject()));
 //BA.debugLineNum = 107;BA.debugLine="End Sub";
return "";
}
public static String  _connected_tick() throws Exception{
 //BA.debugLineNum = 39;BA.debugLine="Sub connected_Tick";
 //BA.debugLineNum = 40;BA.debugLine="If client.Connected = False Then";
if (_client.getConnected()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 41;BA.debugLine="connectedTmr.Enabled = False";
_connectedtmr.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 42;BA.debugLine="CallSub(ServerBoard, \"ConnectionLost\")";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)(mostCurrent._serverboard.getObject()),"ConnectionLost");
 };
 //BA.debugLineNum = 45;BA.debugLine="End Sub";
return "";
}
public static byte[]  _createmessage(String _body) throws Exception{
nl.pdeg.bordondroid.main._message _m = null;
 //BA.debugLineNum = 168;BA.debugLine="Private Sub CreateMessage(Body As String) As Byte(";
 //BA.debugLineNum = 169;BA.debugLine="Dim m As Message";
_m = new nl.pdeg.bordondroid.main._message();
 //BA.debugLineNum = 170;BA.debugLine="m.Initialize";
_m.Initialize();
 //BA.debugLineNum = 171;BA.debugLine="m.Body = Body";
_m.Body /*String*/  = _body;
 //BA.debugLineNum = 172;BA.debugLine="m.From = Name";
_m.From /*String*/  = _name;
 //BA.debugLineNum = 173;BA.debugLine="Return serializator.ConvertObjectToBytes(m)";
if (true) return _serializator.ConvertObjectToBytes((Object)(_m));
 //BA.debugLineNum = 174;BA.debugLine="End Sub";
return null;
}
public static String  _disconnect() throws Exception{
 //BA.debugLineNum = 157;BA.debugLine="Public Sub Disconnect";
 //BA.debugLineNum = 159;BA.debugLine="DiscoveredServer = \"\"";
_discoveredserver = "";
 //BA.debugLineNum = 161;BA.debugLine="If connected Then";
if (_connected) { 
 //BA.debugLineNum = 162;BA.debugLine="client.Publish2(\"all/disconnect\", serializator.C";
_client.Publish2("all/disconnect",_serializator.ConvertObjectToBytes((Object)(_name)),(int) (0),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 163;BA.debugLine="client.Close";
_client.Close();
 //BA.debugLineNum = 164;BA.debugLine="connected = False";
_connected = anywheresoftware.b4a.keywords.Common.False;
 };
 //BA.debugLineNum = 166;BA.debugLine="End Sub";
return "";
}
public static String  _getbroadcastaddress() throws Exception{
anywheresoftware.b4j.object.JavaObject _niiterator = null;
anywheresoftware.b4j.object.JavaObject _ni = null;
anywheresoftware.b4a.objects.collections.List _addresses = null;
anywheresoftware.b4j.object.JavaObject _ia = null;
Object _broadcast = null;
String _b = "";
 //BA.debugLineNum = 187;BA.debugLine="Private Sub GetBroadcastAddress As String";
 //BA.debugLineNum = 188;BA.debugLine="Dim niIterator As JavaObject";
_niiterator = new anywheresoftware.b4j.object.JavaObject();
 //BA.debugLineNum = 189;BA.debugLine="niIterator = niIterator.InitializeStatic(\"java.";
_niiterator.setObject((java.lang.Object)(_niiterator.InitializeStatic("java.net.NetworkInterface").RunMethod("getNetworkInterfaces",(Object[])(anywheresoftware.b4a.keywords.Common.Null))));
 //BA.debugLineNum = 190;BA.debugLine="Do While niIterator.RunMethod(\"hasMoreElements\"";
while (BA.ObjectToBoolean(_niiterator.RunMethod("hasMoreElements",(Object[])(anywheresoftware.b4a.keywords.Common.Null)))) {
 //BA.debugLineNum = 191;BA.debugLine="Dim ni As JavaObject = niIterator.RunMethod(\"";
_ni = new anywheresoftware.b4j.object.JavaObject();
_ni.setObject((java.lang.Object)(_niiterator.RunMethod("nextElement",(Object[])(anywheresoftware.b4a.keywords.Common.Null))));
 //BA.debugLineNum = 192;BA.debugLine="If ni.RunMethod(\"isLoopback\", Null) = False T";
if ((_ni.RunMethod("isLoopback",(Object[])(anywheresoftware.b4a.keywords.Common.Null))).equals((Object)(anywheresoftware.b4a.keywords.Common.False))) { 
 //BA.debugLineNum = 193;BA.debugLine="Dim addresses As List = ni.RunMethod(\"getIn";
_addresses = new anywheresoftware.b4a.objects.collections.List();
_addresses.setObject((java.util.List)(_ni.RunMethod("getInterfaceAddresses",(Object[])(anywheresoftware.b4a.keywords.Common.Null))));
 //BA.debugLineNum = 194;BA.debugLine="For Each ia As JavaObject In addresses";
_ia = new anywheresoftware.b4j.object.JavaObject();
{
final anywheresoftware.b4a.BA.IterableList group7 = _addresses;
final int groupLen7 = group7.getSize()
;int index7 = 0;
;
for (; index7 < groupLen7;index7++){
_ia.setObject((java.lang.Object)(group7.Get(index7)));
 //BA.debugLineNum = 195;BA.debugLine="Dim broadcast As Object = ia.RunMethod(\"g";
_broadcast = _ia.RunMethod("getBroadcast",(Object[])(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 196;BA.debugLine="If broadcast <> Null Then";
if (_broadcast!= null) { 
 //BA.debugLineNum = 197;BA.debugLine="Dim b As String = broadcast";
_b = BA.ObjectToString(_broadcast);
 //BA.debugLineNum = 198;BA.debugLine="Return b.SubString(1)";
if (true) return _b.substring((int) (1));
 };
 }
};
 };
 }
;
 //BA.debugLineNum = 203;BA.debugLine="Return \"\"";
if (true) return "";
 //BA.debugLineNum = 204;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 6;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 9;BA.debugLine="Private client As MqttClient";
_client = new anywheresoftware.b4j.objects.MqttAsyncClientWrapper();
 //BA.debugLineNum = 10;BA.debugLine="Private const port As Int = 51042";
_port = (int) (51042);
 //BA.debugLineNum = 11;BA.debugLine="Private const discoverPort As Int = 51049";
_discoverport = (int) (51049);
 //BA.debugLineNum = 12;BA.debugLine="Private serializator As B4XSerializator";
_serializator = new anywheresoftware.b4a.randomaccessfile.B4XSerializator();
 //BA.debugLineNum = 13;BA.debugLine="Public connected As Boolean";
_connected = false;
 //BA.debugLineNum = 15;BA.debugLine="Private users As List";
_users = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 16;BA.debugLine="Public isServer As Boolean";
_isserver = false;
 //BA.debugLineNum = 17;BA.debugLine="Public Name As String";
_name = "";
 //BA.debugLineNum = 18;BA.debugLine="Public DiscoveredServer As String";
_discoveredserver = "";
 //BA.debugLineNum = 19;BA.debugLine="Private autodiscover As UDPSocket";
_autodiscover = new anywheresoftware.b4a.objects.SocketWrapper.UDPSocket();
 //BA.debugLineNum = 21;BA.debugLine="Public connectedTmr As Timer";
_connectedtmr = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 22;BA.debugLine="Private server As ServerSocket 'ignore";
_server = new anywheresoftware.b4a.objects.SocketWrapper.ServerSocketWrapper();
 //BA.debugLineNum = 23;BA.debugLine="Public serverList As List";
_serverlist = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 24;BA.debugLine="Public serverDied As Long = 10000";
_serverdied = (long) (10000);
 //BA.debugLineNum = 25;BA.debugLine="Private p As Phone";
_p = new anywheresoftware.b4a.phone.Phone();
 //BA.debugLineNum = 26;BA.debugLine="Public selectedBordName As String";
_selectedbordname = "";
 //BA.debugLineNum = 27;BA.debugLine="End Sub";
return "";
}
public static String  _sendmessage(String _body) throws Exception{
 //BA.debugLineNum = 151;BA.debugLine="Public Sub SendMessage(Body As String)";
 //BA.debugLineNum = 152;BA.debugLine="If connected Then";
if (_connected) { 
 //BA.debugLineNum = 153;BA.debugLine="client.Publish2(\"all\", CreateMessage(Body), 0, F";
_client.Publish2("all",_createmessage(_body),(int) (0),anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 155;BA.debugLine="End Sub";
return "";
}
public static String  _service_create() throws Exception{
 //BA.debugLineNum = 29;BA.debugLine="Sub Service_Create";
 //BA.debugLineNum = 30;BA.debugLine="connectedTmr.Initialize(\"connected\", 3000)";
_connectedtmr.Initialize(processBA,"connected",(long) (3000));
 //BA.debugLineNum = 33;BA.debugLine="users.Initialize";
_users.Initialize();
 //BA.debugLineNum = 34;BA.debugLine="autodiscover.Initialize(\"autodiscover\",discoverPo";
_autodiscover.Initialize(processBA,"autodiscover",_discoverport,(int) (8192));
 //BA.debugLineNum = 35;BA.debugLine="serverList.Initialize";
_serverlist.Initialize();
 //BA.debugLineNum = 37;BA.debugLine="End Sub";
return "";
}
public static String  _service_destroy() throws Exception{
 //BA.debugLineNum = 211;BA.debugLine="Sub Service_Destroy";
 //BA.debugLineNum = 213;BA.debugLine="End Sub";
return "";
}
public static String  _service_start(anywheresoftware.b4a.objects.IntentWrapper _startingintent) throws Exception{
 //BA.debugLineNum = 80;BA.debugLine="Sub Service_Start (StartingIntent As Intent)";
 //BA.debugLineNum = 82;BA.debugLine="End Sub";
return "";
}
}
