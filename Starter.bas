B4A=true
Group=Default Group
ModulesStructureVersion=1
Type=Service
Version=6.5
@EndOfDesignText@
#Region  Service Attributes 
	#StartAtBoot: False
	#ExcludeFromLibrary: True
#End Region

Sub Process_Globals
	
'	Private broker As MqttBroker
	Private client As MqttClient
	Private const port As Int = 1883   '51042
	Private const discoverPort As Int = 51049
	Private serializator As B4XSerializator
	Public connected As Boolean
'	Private brokerStarted As Boolean
	Private users As List
	Public isServer As Boolean
	Public Name As String
	Public DiscoveredServer As String
	Private autodiscover As UDPSocket
'	Private BroadcastTimer As Timer
	Public connectedTmr As Timer
	Private server As ServerSocket 'ignore
	Public serverList As List
	Public serverDied As Long = 10000
	Private p As Phone
	Public selectedBordName As String
	Private topicName As String
End Sub

Sub Service_Create
	connectedTmr.Initialize("connected", 3000)
'	broker.Initialize("", port)
'	broker.DebugLog = False
	users.Initialize
	autodiscover.Initialize("autodiscover",discoverPort , 8192)
	serverList.Initialize
'	BroadcastTimer.Initialize("BroadcastTimer", 5000)
Name = p.Model
End Sub

Sub connected_Tick
	If client.Connected = False Then
		connectedTmr.Enabled = False
		CallSub(ServerBoard, "ConnectionLost")
		'ToastMessageShow("Connectie verloren..", True)
	End If
End Sub

Private Sub BroadcastTimer_Tick
	Dim address As String = GetBroadcastAddress
	If address <> "" Then
		Dim up As UDPPacket
		up.Initialize(serializator.ConvertObjectToBytes(server.GetMyWifiIP), address, discoverPort)
		autodiscover.Send(up)
	End If
End Sub

Private Sub AutoDiscover_PacketArrived (Packet As UDPPacket)
	If connected Then Return
	Try
		Dim bc As ByteConverter
		Dim data(Packet.Length) As Byte
		bc.ArrayCopy(Packet.Data, Packet.Offset, data, 0, Packet.Length)
		Dim ds() As Object = serializator.ConvertBytesToObject(data)
		Log(ds(0))
'		Dim ls As Map 	= serializator.ConvertBytesToObject(data)
		
		CallSub2(Main, "CheckIpExits", ds)
'		If ds <> DiscoveredServer Then
'			DiscoveredServer = ds
'			'Log($"Discovered server: ${DiscoveredServer} $DateTime{DateTime.Now}"$)
'			If DiscoveredServer <> "" Then
'			'	Connect(False)
'			End If
'			'CallSub(Main, "UpdateState")
'		End If
	Catch
		Log(LastException)
	End Try
End Sub

Sub Service_Start (StartingIntent As Intent)

End Sub


Public Sub Connect (AsServer As Boolean)
	Dim host As String' = DiscoveredServer
	host = "pdeg3005.mynetgear.com"
	Log("HOST : " & host)

	If connected Then client.Close
	
	client.Initialize("client", $"tcp://${host}:${port}"$, "android" & Rnd(1, 10000000))
	Dim mo As MqttConnectOptions
	mo.Initialize("", "")
	'this message will be sent if the client is disconnected unexpectedly.
	mo.SetLastWill("all/disconnect", serializator.ConvertObjectToBytes(p.Model), 0, False)
	client.Connect2(mo)
End Sub

Private Sub client_Connected (Success As Boolean)
	Log($"Connected: ${Success}"$)
	If Success Then
		SendMessage("data please")
		connectedTmr.Enabled = True
		connected = True
'		client.Subscribe("all/#", 0)
		client.Subscribe($"${selectedBordName}/#"$, 0)
		
'		client.Publish2("all/connect", serializator.ConvertObjectToBytes(Name), 0, False)
		client.Publish2(selectedBordName, serializator.ConvertObjectToBytes(Name), 0, False)
		
	Else
'		CallSub(ServerBoard, "ConnectionLost")
		'ToastMessageShow("Error connecting: " & LastException, True)
	End If
End Sub

Private Sub client_MessageArrived (Topic As String, Payload() As Byte)
	Log("TOPIC " & Topic)
	Dim receivedObject As Object = serializator.ConvertBytesToObject(Payload)
	If Topic = "all/connect" Or Topic = "all/disconnect" Then
		'new client has connected or disconnected
		Dim newUser As String = receivedObject
		Log(newUser)
		If isServer Then
			Log($"${Topic}: ${newUser}"$)
			Dim index As Int = users.IndexOf(newUser)
			If Topic.EndsWith("connect") And index = -1 Then users.Add(newUser)
			If Topic.EndsWith("disconnect") And index >= 0 Then users.RemoveAt(index)
			client.Publish2("all/users", serializator.ConvertObjectToBytes(users), 0, False)
		End If
	Else if Topic = "all/users" Then
		Dim newUsers As List = receivedObject
'		CallSubDelayed2(Chat, "NewUsers", newUsers) 'this will start the chat activity if it wasn't started yet.
		'CallSubDelayed2(Main, "NewUsers", newUsers)
'		Log($"CONNECTED DEVICES : ${newUsers.Size-1}"$)
	Else
		Dim m As Message = receivedObject
'		CallSub2(Chat, "NewMessage", m)
		Log(m)

		CallSub2(ServerBoard, "UpdateBordWhenClient", m)
	End If
		
End Sub

Public Sub SendMessage(Body As String)
	If connected Then
		client.Publish2("all", CreateMessage(Body), 0, False)
	End If
End Sub

Public Sub Disconnect
'	BroadcastTimer.Enabled = False
	DiscoveredServer = ""
'	CallSub(Main, "UpdateState")
	If connected Then 
		client.Publish2("all/disconnect", serializator.ConvertObjectToBytes(Name), 0, False)
		client.Close
		connected = False
	End If
End Sub

Private Sub CreateMessage(Body As String) As Byte()
	Dim m As Message
	m.Initialize
	m.Body = Body
	m.From = Name
	Return serializator.ConvertObjectToBytes(m)
End Sub

Private Sub client_Disconnected
	connected = False
'	CallSub(Chat, "Disconnected")
'	If isServer Then
''		broker.Stop
''		brokerStarted = False
'	End If
End Sub

'Returns the UDP broadcast address.
'Returns an empty string if not available.
Private Sub GetBroadcastAddress As String
   Dim niIterator As JavaObject
   niIterator = niIterator.InitializeStatic("java.net.NetworkInterface").RunMethod("getNetworkInterfaces", Null)
   Do While niIterator.RunMethod("hasMoreElements", Null)
     Dim ni As JavaObject = niIterator.RunMethod("nextElement", Null)
     If ni.RunMethod("isLoopback", Null) = False Then
       Dim addresses As List = ni.RunMethod("getInterfaceAddresses", Null)
       For Each ia As JavaObject In addresses
         Dim broadcast As Object = ia.RunMethod("getBroadcast", Null)
         If broadcast <> Null Then
           Dim b As String = broadcast
           Return b.SubString(1)
         End If
       Next
     End If
   Loop
   Return ""
End Sub


Sub Application_Error (Error As Exception, StackTrace As String) As Boolean
	Return True
End Sub

Sub Service_Destroy

End Sub
