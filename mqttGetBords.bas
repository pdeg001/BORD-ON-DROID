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
	
End Sub

'Initializes the object. You can add parameters to this method if needed.
Public Sub Initialize
	
End Sub


Public Sub Connect ()
	If connected Then client.Close
	
	client.Initialize("client", $"tcp://${host}:${port}"$, phone.Model & Rnd(1, 10000000))
	Dim mo As MqttConnectOptions
	mo.Initialize("", "")
	'this message will be sent if the client is disconnected unexpectedly.
	mo.SetLastWill("pubBord/disconnect", serializator.ConvertObjectToBytes(phone.Model), 0, False)
	client.Connect2(mo)
End Sub

Public Sub Disconnect
	If connected Then
		client.Publish2("pubBord/disconnect", serializator.ConvertObjectToBytes(phone.Model), 0, False)
		client.Close
		connected = False
	End If
End Sub

Private Sub client_Connected (Success As Boolean)

	If Success Then
		connected = True
		client.Subscribe("pubBord/#", 0)
	Else
		ToastMessageShow("Error connecting: " & LastException, True)
	End If
End Sub

Private Sub client_MessageArrived (Topic As String, Payload() As Byte)
	Log($"Connected: ${Topic}"$)
	Dim receivedObject As Object = serializator.ConvertBytesToObject(Payload)
	Dim m As String = receivedObject

	If Topic = "pubBord" Then
		CallSub2(Main, "CheckIpExits", m)
	End If
		
End Sub


Public Sub SendMessage(Body As String)
	If connected Then
		client.Publish2("all", CreateMessage(Body), 0, False)
	End If
End Sub


Private Sub CreateMessage(Body As String) As Byte()
	Dim m As Message
	m.Initialize
	m.Body = Body
	m.From = "Name"
	Return serializator.ConvertObjectToBytes(m)
End Sub