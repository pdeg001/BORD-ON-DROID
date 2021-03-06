﻿B4A=true
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
'	Private subBordDataDisconnect As String
''	Private subBord As String
End Sub

Public Sub Initialize
End Sub

'Public Sub SetSub
'	subBord = CallSub(Starter, "GetSubString")
'	subBordDataDisconnect = $"${subBord}/disconnect"$
'End Sub

Public Sub Connect ()
	If connected Then client.Close
	
	client.Initialize("client", $"tcp://${Starter.host}:${Starter.port}"$, phone.Model & Rnd(1, 10000000))
	Dim mo As MqttConnectOptions
	mo.Initialize("", "")
	'this message will be sent if the client is disconnected unexpectedly.
	mo.SetLastWill(Starter.subDisconnectString, serializator.ConvertObjectToBytes(phone.Model), 0, False)
	client.Connect2(mo)
End Sub

Private Sub client_Connected (Success As Boolean)
	If Success Then
		connected = True
		'client.Subscribe(Starter.selectedBordName, 0)
		client.Subscribe(Starter.SubString, 0)
	Else
'		ToastMessageShow("Error connecting +++ : " & LastException, True)
'		Log("SUBSTRING = " &Starter.SubString)
		ProcessConnectError
	End If
End Sub

Public Sub Disconnect
	If client.connected Then
		client.Unsubscribe(Starter.SubString)
		client.Close
		connected = False
	End If
End Sub

Private Sub client_MessageArrived (Topic As Object, Payload() As Byte)
	Dim passedTopic As String =$"${Topic}"$
	Dim receivedObject As Object = serializator.ConvertBytesToObject(Payload)
	Dim m As Message = receivedObject
	Dim strFrom As String = $"${m.From}"$
	
	If strFrom.IndexOf("recvdied") > -1 Then
		CallSubDelayed(ServerBoard, "BordDied")
		Return
	End If
	If m.Body = "data please" Then Return
	
	If m.From.IndexOf("game-ended") > -1 Then
		CallSub(ServerBoard, "GamedEnded")
		Return
	End If
	
	If passedTopic.IndexOf("recvdata") > -1 Then
		ServerBoard.lastMessageTime = DateTime.Now
''		CallSubDelayed(ServerBoard, "GamedInProgress")
		CallSub2(ServerBoard, "UpdateBordWhenClient", m)
	End If
	
	If passedTopic.IndexOf("pubbord") > -1 Then
		CallSub2(Main, "CheckIpExits", m)
	End If
	
End Sub

Public Sub SendMessage(Body As String)
	If connected Then
		client.Publish2(Starter.SubString, CreateMessage(Body), 0, False)
	End If
End Sub

Private Sub CreateMessage(Body As String) As Byte()
	Dim m As Message
	m.Initialize
	m.Body = Body
	m.From = phone.Model
	Return serializator.ConvertObjectToBytes(m)
End Sub

Sub GetClientConnected As Boolean
	Return connected
End Sub

Sub ProcessConnectError
	If Starter.SubString.IndexOf("pubbord") > -1 Then
		CallSub(Main, "ConnectionError")
	End If
End Sub

