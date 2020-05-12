B4A=true
Group=Default Group
ModulesStructureVersion=1
Type=Class
Version=9.801
@EndOfDesignText@
Sub Class_Globals
	Private client As MqttClient
	Private connected As Boolean
	Private serializator As B4XSerializator
	Private phone As Phone
	
	Private const port As Int = 1883
	Private host As String = "pdeg3005.mynetgear.com"
	Private subBord As String
	Private subBordDisconnect As String
	
End Sub

Public Sub Initialize
	subBord = $"${Starter.selectedBordName}/#"$
	subBordDisconnect = $"${Starter.selectedBordName}/disconnect"$
End Sub

Public Sub Connect ()
	If connected Then client.Close
	
	client.Initialize("client", $"tcp://${host}:${port}"$, phone.Model & Rnd(1, 10000000))
	Dim mo As MqttConnectOptions
	mo.Initialize("", "")
	'this message will be sent if the client is disconnected unexpectedly.
	mo.SetLastWill(subBordDisconnect, serializator.ConvertObjectToBytes(phone.Model), 0, False)
	client.Connect2(mo)
End Sub

Private Sub client_Connected (Success As Boolean)
	Log("Connected " & Success)
	If Success Then
		connected = True
		client.Subscribe(subBord, 0)
	Else
		ToastMessageShow("Error connecting: " & LastException, True)
	End If
End Sub

Public Sub Disconnect
	If connected Then
		client.Publish2(subBordDisconnect, serializator.ConvertObjectToBytes(phone.Model), 0, False)
		client.Close
		connected = False
	End If
End Sub

Private Sub client_MessageArrived (Topic As String, Payload() As Byte)
	Log($"$Time{DateTime.now} Connected: ${Topic} SUB-BORD: ${Starter.selectedBordName}"$)
	Dim receivedObject As Object = serializator.ConvertBytesToObject(Payload)
	Dim m As Message = receivedObject
	
	If Topic = Starter.selectedBordName Then
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



