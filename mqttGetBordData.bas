B4A=true
Group=Default Group
ModulesStructureVersion=1
Type=Class
Version=9.801
@EndOfDesignText@
Sub Class_Globals
	Private client As MqttClient
	Public connected As Boolean
	Private serializator As B4XSerializator
	Private phone As Phone
	
'	Public subto As String
'	Private subBordData As String
	Private subBordDataDisconnect As String
	Private subBord As String
	
End Sub

Public Sub Initialize
'	subBordData = $"pubdata/#"$
'	subBordDataDisconnect = $"${subBord}/disconnect"$
End Sub

Public Sub SetSub
	subBord = CallSub(Starter, "GetSubString")
	subBordDataDisconnect = $"${subBord}/disconnect"$
End Sub

Public Sub Connect ()
	If connected Then client.Close
	
	client.Initialize("client", $"tcp://${Starter.host}:${Starter.port}"$, phone.Model & Rnd(1, 10000000))
	Dim mo As MqttConnectOptions
	mo.Initialize("", "")
	'this message will be sent if the client is disconnected unexpectedly.
	mo.SetLastWill(subBordDataDisconnect, serializator.ConvertObjectToBytes(phone.Model), 0, False)
	client.Connect2(mo)
End Sub

Private Sub client_Connected (Success As Boolean)
'	Log("Connected " & Success)
	If Success Then
		connected = True
		'client.Subscribe(Starter.selectedBordName, 0)
		client.Subscribe(subBord, 0)
	Else
		ToastMessageShow("Error connecting: " & LastException, True)
	End If
End Sub

Public Sub Disconnect
'	Log("CLOSE DATA")
	If client.connected Then
		'client.Publish2(subBordDataDisconnect, serializator.ConvertObjectToBytes(phone.Model), 0, False)
		client.Unsubscribe(subBord)
		client.Close
		connected = False
	End If
End Sub

Private Sub client_MessageArrived (Topic As Object, Payload() As Byte)
'	Log($"DATA RECEIVED : $Time{DateTime.Now}"$)
	
	Dim receivedObject As Object = serializator.ConvertBytesToObject(Payload)
	Dim m As Message = receivedObject
'	Log(m.Body)
	If m.From.IndexOf("game-ended") > -1 Then
		CallSub(ServerBoard, "GamedEnded")
		Return
	End If
	If m.Body = "data please" Then Return
	
	If Topic = subBord Then
		ServerBoard.lastMessageTime = DateTime.Now
		CallSubDelayed(ServerBoard, "GamedInProgress")
		CallSub2(ServerBoard, "UpdateBordWhenClient", m)
	End If
End Sub

Public Sub SendMessage(Body As String)
	If connected Then
		client.Publish2(subBord, CreateMessage(Body), 0, False)
	End If
End Sub

Private Sub CreateMessage(Body As String) As Byte()
	Dim m As Message
	m.Initialize
	m.Body = Body
	m.From = phone.Model
	Return serializator.ConvertObjectToBytes(m)
End Sub



