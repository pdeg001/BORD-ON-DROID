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
	Dim mqtt As MqttClient
	Public const port As Int = 1883
	Public const host As String = "pdeg3005.mynetgear.com"
	Public connected As Boolean
	Public DiscoveredServer As String
	Public serverList As List
	Public serverDied As Long = 30000
	Public selectedBordName As String
	Private mqttName As String = "pdeg"
	Private mqttBase As String
	Private mqttUnit As String
	Private mqttGetUnits As String
	Private mqttLastWill As String
	Private rp As RuntimePermissions
	Public mqttGetBordsActive, mqttGetBordDataActive As Boolean
	Public diedIndex As Int = -1
	Private baseFile, baseFilePath As String
	Public SubString, subDisconnectString, selectedLocationCode, selectedLocationDescription As String
	Private storeFolder As String
	Public testBaseName As Boolean = False
	Public appVersion As String
	Dim working, brokerConnected As Boolean
	Public firstConnectTime As Long
	Public mainPaused as Boolean
	
End Sub

Sub Service_Create
	serverList.Initialize
	storeFolder = rp.GetSafeDirDefaultExternal("bod")
	
	baseFile = "bod.pdg"
	baseFilePath = File.Combine(storeFolder, baseFile)
	working = True
End Sub

Sub Service_Start (StartingIntent As Intent)
	ConnectAndReconnect

End Sub

Sub Application_Error (Error As Exception, StackTrace As String) As Boolean
	Return True
End Sub

Sub Service_Destroy

End Sub

Sub ConnectAndReconnect
	Do While working
		If mqtt.IsInitialized Then mqtt.Close
		mqtt.Initialize("mqtt", "tcp://pdeg3005.mynetgear.com:1883", "pdeg_" & Rnd(0, 999999999))
		Dim mo As MqttConnectOptions
		mo.Initialize("", "")
'		Log("Trying to connect")
		mqtt.Connect2(mo)
		Wait For Mqtt_Connected (Success As Boolean)
		If Success Then
'			Log("Mqtt connected")
			brokerConnected = True
	
			CallSub(Main, "getBaseList")
			'CallSub(Main, "StartConnection")
			Do While working And mqtt.Connected
				mqtt.Publish2("ping", Array As Byte(0), 1, False) 'change the ping topic as needed
				Sleep(5000)
			Loop
'			Log("Disconnected")
			brokerConnected = False
			CallSub(ServerBoard, "ConnectionLost")
			CallSub(Main, "ShowNotConnectedToBroker")
			If mqtt.IsInitialized Then mqtt.Close
		Else
'			Log("Error connecting.")
			If mqtt.IsInitialized Then mqtt.Close
		End If
		Sleep(5000)
	Loop
End Sub

Sub SetLastWill(lastWill As String)
	mqttLastWill = lastWill
End Sub

Sub GetLastWill As String
	Return mqttLastWill	
End Sub

Public Sub SetSubString
	SubString = $"${mqttName}/${mqttBase}/recvdata_${mqttUnit}"$
End Sub

Public Sub SetSubString2(unit As String)
	SubString = $"${mqttName}/${mqttBase}${unit}"$
End Sub

Public Sub SetUnsubscribeString2(unit As String)
	subDisconnectString =  $"${mqttName}/${mqttBase}${unit}/disconnect"$
End Sub

'set location code
Private Sub SetSubBase(baseName As String)
	mqttBase = baseName
End Sub

Private Sub GetSubString As String
	Return SubString
End Sub

Private Sub SetUnit(name As String)
	mqttUnit = name
End Sub

Private Sub SetSubGetUnits
	mqttGetUnits = $"${mqttName}/${mqttBase}"$
End Sub

Private Sub GetSubUnits As String
	Return mqttGetUnits
End Sub

Public Sub GetBase As String
	Return mqttGetUnits '$"${mqttName}/${mqttBase}/"$
End Sub

Private Sub GetBaseFilePath As String
	Return baseFilePath
End Sub

Public Sub SetLs
'	Dim ph As Phone
'	ph.SetScreenOrientation(0)
End Sub