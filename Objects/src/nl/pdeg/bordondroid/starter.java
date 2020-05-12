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
public static String _topicname = "";
public nl.pdeg.bordondroid.main _main = null;
public nl.pdeg.bordondroid.chat _chat = null;
public nl.pdeg.bordondroid.serverboard _serverboard = null;
public static boolean  _application_error(anywheresoftware.b4a.objects.B4AException _error,String _stacktrace) throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "application_error", false))
	 {return ((Boolean) Debug.delegate(processBA, "application_error", new Object[] {_error,_stacktrace}));}
RDebugUtils.currentLine=1835008;
 //BA.debugLineNum = 1835008;BA.debugLine="Sub Application_Error (Error As Exception, StackTr";
RDebugUtils.currentLine=1835009;
 //BA.debugLineNum = 1835009;BA.debugLine="Return True";
if (true) return anywheresoftware.b4a.keywords.Common.True;
RDebugUtils.currentLine=1835010;
 //BA.debugLineNum = 1835010;BA.debugLine="End Sub";
return false;
}
public static String  _autodiscover_packetarrived(anywheresoftware.b4a.objects.SocketWrapper.UDPSocket.UDPPacket _packet) throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "autodiscover_packetarrived", false))
	 {return ((String) Debug.delegate(processBA, "autodiscover_packetarrived", new Object[] {_packet}));}
anywheresoftware.b4a.agraham.byteconverter.ByteConverter _bc = null;
byte[] _data = null;
Object[] _ds = null;
RDebugUtils.currentLine=1179648;
 //BA.debugLineNum = 1179648;BA.debugLine="Private Sub AutoDiscover_PacketArrived (Packet As";
RDebugUtils.currentLine=1179649;
 //BA.debugLineNum = 1179649;BA.debugLine="If connected Then Return";
if (_connected) { 
if (true) return "";};
RDebugUtils.currentLine=1179650;
 //BA.debugLineNum = 1179650;BA.debugLine="Try";
try {RDebugUtils.currentLine=1179651;
 //BA.debugLineNum = 1179651;BA.debugLine="Dim bc As ByteConverter";
_bc = new anywheresoftware.b4a.agraham.byteconverter.ByteConverter();
RDebugUtils.currentLine=1179652;
 //BA.debugLineNum = 1179652;BA.debugLine="Dim data(Packet.Length) As Byte";
_data = new byte[_packet.getLength()];
;
RDebugUtils.currentLine=1179653;
 //BA.debugLineNum = 1179653;BA.debugLine="bc.ArrayCopy(Packet.Data, Packet.Offset, data, 0";
_bc.ArrayCopy((Object)(_packet.getData()),_packet.getOffset(),(Object)(_data),(int) (0),_packet.getLength());
RDebugUtils.currentLine=1179654;
 //BA.debugLineNum = 1179654;BA.debugLine="Dim ds() As Object = serializator.ConvertBytesTo";
_ds = (Object[])(_serializator.ConvertBytesToObject(_data));
RDebugUtils.currentLine=1179655;
 //BA.debugLineNum = 1179655;BA.debugLine="Log(ds(0))";
anywheresoftware.b4a.keywords.Common.LogImpl("11179655",BA.ObjectToString(_ds[(int) (0)]),0);
RDebugUtils.currentLine=1179658;
 //BA.debugLineNum = 1179658;BA.debugLine="CallSub2(Main, \"CheckIpExits\", ds)";
anywheresoftware.b4a.keywords.Common.CallSubDebug2(processBA,(Object)(mostCurrent._main.getObject()),"CheckIpExits",(Object)(_ds));
 } 
       catch (Exception e10) {
			processBA.setLastException(e10);RDebugUtils.currentLine=1179668;
 //BA.debugLineNum = 1179668;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("11179668",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(processBA)),0);
 };
RDebugUtils.currentLine=1179670;
 //BA.debugLineNum = 1179670;BA.debugLine="End Sub";
return "";
}
public static String  _broadcasttimer_tick() throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "broadcasttimer_tick", false))
	 {return ((String) Debug.delegate(processBA, "broadcasttimer_tick", null));}
String _address = "";
anywheresoftware.b4a.objects.SocketWrapper.UDPSocket.UDPPacket _up = null;
RDebugUtils.currentLine=1114112;
 //BA.debugLineNum = 1114112;BA.debugLine="Private Sub BroadcastTimer_Tick";
RDebugUtils.currentLine=1114113;
 //BA.debugLineNum = 1114113;BA.debugLine="Dim address As String = GetBroadcastAddress";
_address = _getbroadcastaddress();
RDebugUtils.currentLine=1114114;
 //BA.debugLineNum = 1114114;BA.debugLine="If address <> \"\" Then";
if ((_address).equals("") == false) { 
RDebugUtils.currentLine=1114115;
 //BA.debugLineNum = 1114115;BA.debugLine="Dim up As UDPPacket";
_up = new anywheresoftware.b4a.objects.SocketWrapper.UDPSocket.UDPPacket();
RDebugUtils.currentLine=1114116;
 //BA.debugLineNum = 1114116;BA.debugLine="up.Initialize(serializator.ConvertObjectToBytes(";
_up.Initialize(_serializator.ConvertObjectToBytes((Object)(_server.GetMyWifiIP())),_address,_discoverport);
RDebugUtils.currentLine=1114117;
 //BA.debugLineNum = 1114117;BA.debugLine="autodiscover.Send(up)";
_autodiscover.Send(_up);
 };
RDebugUtils.currentLine=1114119;
 //BA.debugLineNum = 1114119;BA.debugLine="End Sub";
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
RDebugUtils.currentLine=1769472;
 //BA.debugLineNum = 1769472;BA.debugLine="Private Sub GetBroadcastAddress As String";
RDebugUtils.currentLine=1769473;
 //BA.debugLineNum = 1769473;BA.debugLine="Dim niIterator As JavaObject";
_niiterator = new anywheresoftware.b4j.object.JavaObject();
RDebugUtils.currentLine=1769474;
 //BA.debugLineNum = 1769474;BA.debugLine="niIterator = niIterator.InitializeStatic(\"java.";
_niiterator.setObject((java.lang.Object)(_niiterator.InitializeStatic("java.net.NetworkInterface").RunMethod("getNetworkInterfaces",(Object[])(anywheresoftware.b4a.keywords.Common.Null))));
RDebugUtils.currentLine=1769475;
 //BA.debugLineNum = 1769475;BA.debugLine="Do While niIterator.RunMethod(\"hasMoreElements\"";
while (BA.ObjectToBoolean(_niiterator.RunMethod("hasMoreElements",(Object[])(anywheresoftware.b4a.keywords.Common.Null)))) {
RDebugUtils.currentLine=1769476;
 //BA.debugLineNum = 1769476;BA.debugLine="Dim ni As JavaObject = niIterator.RunMethod(\"";
_ni = new anywheresoftware.b4j.object.JavaObject();
_ni.setObject((java.lang.Object)(_niiterator.RunMethod("nextElement",(Object[])(anywheresoftware.b4a.keywords.Common.Null))));
RDebugUtils.currentLine=1769477;
 //BA.debugLineNum = 1769477;BA.debugLine="If ni.RunMethod(\"isLoopback\", Null) = False T";
if ((_ni.RunMethod("isLoopback",(Object[])(anywheresoftware.b4a.keywords.Common.Null))).equals((Object)(anywheresoftware.b4a.keywords.Common.False))) { 
RDebugUtils.currentLine=1769478;
 //BA.debugLineNum = 1769478;BA.debugLine="Dim addresses As List = ni.RunMethod(\"getIn";
_addresses = new anywheresoftware.b4a.objects.collections.List();
_addresses.setObject((java.util.List)(_ni.RunMethod("getInterfaceAddresses",(Object[])(anywheresoftware.b4a.keywords.Common.Null))));
RDebugUtils.currentLine=1769479;
 //BA.debugLineNum = 1769479;BA.debugLine="For Each ia As JavaObject In addresses";
_ia = new anywheresoftware.b4j.object.JavaObject();
{
final anywheresoftware.b4a.BA.IterableList group7 = _addresses;
final int groupLen7 = group7.getSize()
;int index7 = 0;
;
for (; index7 < groupLen7;index7++){
_ia.setObject((java.lang.Object)(group7.Get(index7)));
RDebugUtils.currentLine=1769480;
 //BA.debugLineNum = 1769480;BA.debugLine="Dim broadcast As Object = ia.RunMethod(\"g";
_broadcast = _ia.RunMethod("getBroadcast",(Object[])(anywheresoftware.b4a.keywords.Common.Null));
RDebugUtils.currentLine=1769481;
 //BA.debugLineNum = 1769481;BA.debugLine="If broadcast <> Null Then";
if (_broadcast!= null) { 
RDebugUtils.currentLine=1769482;
 //BA.debugLineNum = 1769482;BA.debugLine="Dim b As String = broadcast";
_b = BA.ObjectToString(_broadcast);
RDebugUtils.currentLine=1769483;
 //BA.debugLineNum = 1769483;BA.debugLine="Return b.SubString(1)";
if (true) return _b.substring((int) (1));
 };
 }
};
 };
 }
;
RDebugUtils.currentLine=1769488;
 //BA.debugLineNum = 1769488;BA.debugLine="Return \"\"";
if (true) return "";
RDebugUtils.currentLine=1769489;
 //BA.debugLineNum = 1769489;BA.debugLine="End Sub";
return "";
}
public static String  _client_connected(boolean _success) throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "client_connected", false))
	 {return ((String) Debug.delegate(processBA, "client_connected", new Object[] {_success}));}
RDebugUtils.currentLine=1376256;
 //BA.debugLineNum = 1376256;BA.debugLine="Private Sub client_Connected (Success As Boolean)";
RDebugUtils.currentLine=1376257;
 //BA.debugLineNum = 1376257;BA.debugLine="Log($\"Connected: ${Success}\"$)";
anywheresoftware.b4a.keywords.Common.LogImpl("11376257",("Connected: "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_success))+""),0);
RDebugUtils.currentLine=1376258;
 //BA.debugLineNum = 1376258;BA.debugLine="If Success Then";
if (_success) { 
RDebugUtils.currentLine=1376259;
 //BA.debugLineNum = 1376259;BA.debugLine="SendMessage(\"data please\")";
_sendmessage("data please");
RDebugUtils.currentLine=1376260;
 //BA.debugLineNum = 1376260;BA.debugLine="connectedTmr.Enabled = True";
_connectedtmr.setEnabled(anywheresoftware.b4a.keywords.Common.True);
RDebugUtils.currentLine=1376261;
 //BA.debugLineNum = 1376261;BA.debugLine="connected = True";
_connected = anywheresoftware.b4a.keywords.Common.True;
RDebugUtils.currentLine=1376263;
 //BA.debugLineNum = 1376263;BA.debugLine="client.Subscribe($\"${selectedBordName}/#\"$, 0)";
_client.Subscribe((""+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_selectedbordname))+"/#"),(int) (0));
RDebugUtils.currentLine=1376266;
 //BA.debugLineNum = 1376266;BA.debugLine="client.Publish2(selectedBordName, serializator.C";
_client.Publish2(_selectedbordname,_serializator.ConvertObjectToBytes((Object)(_name)),(int) (0),anywheresoftware.b4a.keywords.Common.False);
 }else {
 };
RDebugUtils.currentLine=1376272;
 //BA.debugLineNum = 1376272;BA.debugLine="End Sub";
return "";
}
public static String  _sendmessage(String _body) throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "sendmessage", false))
	 {return ((String) Debug.delegate(processBA, "sendmessage", new Object[] {_body}));}
RDebugUtils.currentLine=1507328;
 //BA.debugLineNum = 1507328;BA.debugLine="Public Sub SendMessage(Body As String)";
RDebugUtils.currentLine=1507329;
 //BA.debugLineNum = 1507329;BA.debugLine="If connected Then";
if (_connected) { 
RDebugUtils.currentLine=1507330;
 //BA.debugLineNum = 1507330;BA.debugLine="client.Publish2(\"all\", CreateMessage(Body), 0, F";
_client.Publish2("all",_createmessage(_body),(int) (0),anywheresoftware.b4a.keywords.Common.False);
 };
RDebugUtils.currentLine=1507332;
 //BA.debugLineNum = 1507332;BA.debugLine="End Sub";
return "";
}
public static String  _client_disconnected() throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "client_disconnected", false))
	 {return ((String) Debug.delegate(processBA, "client_disconnected", null));}
RDebugUtils.currentLine=1703936;
 //BA.debugLineNum = 1703936;BA.debugLine="Private Sub client_Disconnected";
RDebugUtils.currentLine=1703937;
 //BA.debugLineNum = 1703937;BA.debugLine="connected = False";
_connected = anywheresoftware.b4a.keywords.Common.False;
RDebugUtils.currentLine=1703943;
 //BA.debugLineNum = 1703943;BA.debugLine="End Sub";
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
RDebugUtils.currentLine=1441792;
 //BA.debugLineNum = 1441792;BA.debugLine="Private Sub client_MessageArrived (Topic As String";
RDebugUtils.currentLine=1441793;
 //BA.debugLineNum = 1441793;BA.debugLine="Log(\"TOPIC \" & Topic)";
anywheresoftware.b4a.keywords.Common.LogImpl("11441793","TOPIC "+_topic,0);
RDebugUtils.currentLine=1441794;
 //BA.debugLineNum = 1441794;BA.debugLine="Dim receivedObject As Object = serializator.Conve";
_receivedobject = _serializator.ConvertBytesToObject(_payload);
RDebugUtils.currentLine=1441795;
 //BA.debugLineNum = 1441795;BA.debugLine="If Topic = \"all/connect\" Or Topic = \"all/disconne";
if ((_topic).equals("all/connect") || (_topic).equals("all/disconnect")) { 
RDebugUtils.currentLine=1441797;
 //BA.debugLineNum = 1441797;BA.debugLine="Dim newUser As String = receivedObject";
_newuser = BA.ObjectToString(_receivedobject);
RDebugUtils.currentLine=1441798;
 //BA.debugLineNum = 1441798;BA.debugLine="Log(newUser)";
anywheresoftware.b4a.keywords.Common.LogImpl("11441798",_newuser,0);
RDebugUtils.currentLine=1441799;
 //BA.debugLineNum = 1441799;BA.debugLine="If isServer Then";
if (_isserver) { 
RDebugUtils.currentLine=1441800;
 //BA.debugLineNum = 1441800;BA.debugLine="Log($\"${Topic}: ${newUser}\"$)";
anywheresoftware.b4a.keywords.Common.LogImpl("11441800",(""+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_topic))+": "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_newuser))+""),0);
RDebugUtils.currentLine=1441801;
 //BA.debugLineNum = 1441801;BA.debugLine="Dim index As Int = users.IndexOf(newUser)";
_index = _users.IndexOf((Object)(_newuser));
RDebugUtils.currentLine=1441802;
 //BA.debugLineNum = 1441802;BA.debugLine="If Topic.EndsWith(\"connect\") And index = -1 The";
if (_topic.endsWith("connect") && _index==-1) { 
_users.Add((Object)(_newuser));};
RDebugUtils.currentLine=1441803;
 //BA.debugLineNum = 1441803;BA.debugLine="If Topic.EndsWith(\"disconnect\") And index >= 0";
if (_topic.endsWith("disconnect") && _index>=0) { 
_users.RemoveAt(_index);};
RDebugUtils.currentLine=1441804;
 //BA.debugLineNum = 1441804;BA.debugLine="client.Publish2(\"all/users\", serializator.Conve";
_client.Publish2("all/users",_serializator.ConvertObjectToBytes((Object)(_users.getObject())),(int) (0),anywheresoftware.b4a.keywords.Common.False);
 };
 }else 
{RDebugUtils.currentLine=1441806;
 //BA.debugLineNum = 1441806;BA.debugLine="Else if Topic = \"all/users\" Then";
if ((_topic).equals("all/users")) { 
RDebugUtils.currentLine=1441807;
 //BA.debugLineNum = 1441807;BA.debugLine="Dim newUsers As List = receivedObject";
_newusers = new anywheresoftware.b4a.objects.collections.List();
_newusers.setObject((java.util.List)(_receivedobject));
 }else {
RDebugUtils.currentLine=1441812;
 //BA.debugLineNum = 1441812;BA.debugLine="Dim m As Message = receivedObject";
_m = (nl.pdeg.bordondroid.main._message)(_receivedobject);
RDebugUtils.currentLine=1441814;
 //BA.debugLineNum = 1441814;BA.debugLine="Log(m)";
anywheresoftware.b4a.keywords.Common.LogImpl("11441814",BA.ObjectToString(_m),0);
RDebugUtils.currentLine=1441816;
 //BA.debugLineNum = 1441816;BA.debugLine="CallSub2(ServerBoard, \"UpdateBordWhenClient\", m)";
anywheresoftware.b4a.keywords.Common.CallSubDebug2(processBA,(Object)(mostCurrent._serverboard.getObject()),"UpdateBordWhenClient",(Object)(_m));
 }}
;
RDebugUtils.currentLine=1441819;
 //BA.debugLineNum = 1441819;BA.debugLine="End Sub";
return "";
}
public static String  _connect(boolean _asserver) throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "connect", false))
	 {return ((String) Debug.delegate(processBA, "connect", new Object[] {_asserver}));}
String _host = "";
anywheresoftware.b4j.objects.MqttAsyncClientWrapper.MqttConnectOptionsWrapper _mo = null;
RDebugUtils.currentLine=1310720;
 //BA.debugLineNum = 1310720;BA.debugLine="Public Sub Connect (AsServer As Boolean)";
RDebugUtils.currentLine=1310721;
 //BA.debugLineNum = 1310721;BA.debugLine="Dim host As String' = DiscoveredServer";
_host = "";
RDebugUtils.currentLine=1310722;
 //BA.debugLineNum = 1310722;BA.debugLine="host = \"pdeg3005.mynetgear.com\"";
_host = "pdeg3005.mynetgear.com";
RDebugUtils.currentLine=1310723;
 //BA.debugLineNum = 1310723;BA.debugLine="Log(\"HOST : \" & host)";
anywheresoftware.b4a.keywords.Common.LogImpl("11310723","HOST : "+_host,0);
RDebugUtils.currentLine=1310725;
 //BA.debugLineNum = 1310725;BA.debugLine="If connected Then client.Close";
if (_connected) { 
_client.Close();};
RDebugUtils.currentLine=1310727;
 //BA.debugLineNum = 1310727;BA.debugLine="client.Initialize(\"client\", $\"tcp://${host}:${por";
_client.Initialize(processBA,"client",("tcp://"+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_host))+":"+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_port))+""),"android"+BA.NumberToString(anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (10000000))));
RDebugUtils.currentLine=1310728;
 //BA.debugLineNum = 1310728;BA.debugLine="Dim mo As MqttConnectOptions";
_mo = new anywheresoftware.b4j.objects.MqttAsyncClientWrapper.MqttConnectOptionsWrapper();
RDebugUtils.currentLine=1310729;
 //BA.debugLineNum = 1310729;BA.debugLine="mo.Initialize(\"\", \"\")";
_mo.Initialize("","");
RDebugUtils.currentLine=1310731;
 //BA.debugLineNum = 1310731;BA.debugLine="mo.SetLastWill(\"all/disconnect\", serializator.Con";
_mo.SetLastWill("all/disconnect",_serializator.ConvertObjectToBytes((Object)(_p.getModel())),(int) (0),anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=1310732;
 //BA.debugLineNum = 1310732;BA.debugLine="client.Connect2(mo)";
_client.Connect2((org.eclipse.paho.client.mqttv3.MqttConnectOptions)(_mo.getObject()));
RDebugUtils.currentLine=1310733;
 //BA.debugLineNum = 1310733;BA.debugLine="End Sub";
return "";
}
public static String  _connected_tick() throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "connected_tick", false))
	 {return ((String) Debug.delegate(processBA, "connected_tick", null));}
RDebugUtils.currentLine=1048576;
 //BA.debugLineNum = 1048576;BA.debugLine="Sub connected_Tick";
RDebugUtils.currentLine=1048577;
 //BA.debugLineNum = 1048577;BA.debugLine="If client.Connected = False Then";
if (_client.getConnected()==anywheresoftware.b4a.keywords.Common.False) { 
RDebugUtils.currentLine=1048578;
 //BA.debugLineNum = 1048578;BA.debugLine="connectedTmr.Enabled = False";
_connectedtmr.setEnabled(anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=1048579;
 //BA.debugLineNum = 1048579;BA.debugLine="CallSub(ServerBoard, \"ConnectionLost\")";
anywheresoftware.b4a.keywords.Common.CallSubDebug(processBA,(Object)(mostCurrent._serverboard.getObject()),"ConnectionLost");
 };
RDebugUtils.currentLine=1048582;
 //BA.debugLineNum = 1048582;BA.debugLine="End Sub";
return "";
}
public static byte[]  _createmessage(String _body) throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "createmessage", false))
	 {return ((byte[]) Debug.delegate(processBA, "createmessage", new Object[] {_body}));}
nl.pdeg.bordondroid.main._message _m = null;
RDebugUtils.currentLine=1638400;
 //BA.debugLineNum = 1638400;BA.debugLine="Private Sub CreateMessage(Body As String) As Byte(";
RDebugUtils.currentLine=1638401;
 //BA.debugLineNum = 1638401;BA.debugLine="Dim m As Message";
_m = new nl.pdeg.bordondroid.main._message();
RDebugUtils.currentLine=1638402;
 //BA.debugLineNum = 1638402;BA.debugLine="m.Initialize";
_m.Initialize();
RDebugUtils.currentLine=1638403;
 //BA.debugLineNum = 1638403;BA.debugLine="m.Body = Body";
_m.Body /*String*/  = _body;
RDebugUtils.currentLine=1638404;
 //BA.debugLineNum = 1638404;BA.debugLine="m.From = Name";
_m.From /*String*/  = _name;
RDebugUtils.currentLine=1638405;
 //BA.debugLineNum = 1638405;BA.debugLine="Return serializator.ConvertObjectToBytes(m)";
if (true) return _serializator.ConvertObjectToBytes((Object)(_m));
RDebugUtils.currentLine=1638406;
 //BA.debugLineNum = 1638406;BA.debugLine="End Sub";
return null;
}
public static String  _disconnect() throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "disconnect", false))
	 {return ((String) Debug.delegate(processBA, "disconnect", null));}
RDebugUtils.currentLine=1572864;
 //BA.debugLineNum = 1572864;BA.debugLine="Public Sub Disconnect";
RDebugUtils.currentLine=1572866;
 //BA.debugLineNum = 1572866;BA.debugLine="DiscoveredServer = \"\"";
_discoveredserver = "";
RDebugUtils.currentLine=1572868;
 //BA.debugLineNum = 1572868;BA.debugLine="If connected Then";
if (_connected) { 
RDebugUtils.currentLine=1572869;
 //BA.debugLineNum = 1572869;BA.debugLine="client.Publish2(\"all/disconnect\", serializator.C";
_client.Publish2("all/disconnect",_serializator.ConvertObjectToBytes((Object)(_name)),(int) (0),anywheresoftware.b4a.keywords.Common.False);
RDebugUtils.currentLine=1572870;
 //BA.debugLineNum = 1572870;BA.debugLine="client.Close";
_client.Close();
RDebugUtils.currentLine=1572871;
 //BA.debugLineNum = 1572871;BA.debugLine="connected = False";
_connected = anywheresoftware.b4a.keywords.Common.False;
 };
RDebugUtils.currentLine=1572873;
 //BA.debugLineNum = 1572873;BA.debugLine="End Sub";
return "";
}
public static String  _service_create() throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "service_create", false))
	 {return ((String) Debug.delegate(processBA, "service_create", null));}
RDebugUtils.currentLine=983040;
 //BA.debugLineNum = 983040;BA.debugLine="Sub Service_Create";
RDebugUtils.currentLine=983041;
 //BA.debugLineNum = 983041;BA.debugLine="connectedTmr.Initialize(\"connected\", 3000)";
_connectedtmr.Initialize(processBA,"connected",(long) (3000));
RDebugUtils.currentLine=983044;
 //BA.debugLineNum = 983044;BA.debugLine="users.Initialize";
_users.Initialize();
RDebugUtils.currentLine=983045;
 //BA.debugLineNum = 983045;BA.debugLine="autodiscover.Initialize(\"autodiscover\",discoverPo";
_autodiscover.Initialize(processBA,"autodiscover",_discoverport,(int) (8192));
RDebugUtils.currentLine=983046;
 //BA.debugLineNum = 983046;BA.debugLine="serverList.Initialize";
_serverlist.Initialize();
RDebugUtils.currentLine=983048;
 //BA.debugLineNum = 983048;BA.debugLine="Name = p.Model";
_name = _p.getModel();
RDebugUtils.currentLine=983049;
 //BA.debugLineNum = 983049;BA.debugLine="End Sub";
return "";
}
public static String  _service_destroy() throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "service_destroy", false))
	 {return ((String) Debug.delegate(processBA, "service_destroy", null));}
RDebugUtils.currentLine=1900544;
 //BA.debugLineNum = 1900544;BA.debugLine="Sub Service_Destroy";
RDebugUtils.currentLine=1900546;
 //BA.debugLineNum = 1900546;BA.debugLine="End Sub";
return "";
}
public static String  _service_start(anywheresoftware.b4a.objects.IntentWrapper _startingintent) throws Exception{
RDebugUtils.currentModule="starter";
if (Debug.shouldDelegate(processBA, "service_start", false))
	 {return ((String) Debug.delegate(processBA, "service_start", new Object[] {_startingintent}));}
RDebugUtils.currentLine=1245184;
 //BA.debugLineNum = 1245184;BA.debugLine="Sub Service_Start (StartingIntent As Intent)";
RDebugUtils.currentLine=1245186;
 //BA.debugLineNum = 1245186;BA.debugLine="End Sub";
return "";
}
}