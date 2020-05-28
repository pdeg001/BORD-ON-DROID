B4A=true
Group=Default Group
ModulesStructureVersion=1
Type=Activity
Version=9.801
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: true
	#IncludeTitle: false
#End Region

Sub Process_Globals
	Dim mqttBase As MqttConnector
	Dim baseFile As Base
	Dim cs As CSBuilder
	Dim lastMessageTime As Long
	Dim lastMessageTimer As Timer
End Sub

Sub Globals
	Dim parser As JSONParser
	Private lblP1Name, lblP2Name As Label
	Private lblP1Maken100, lblP1Maken10, lblP1Maken1 As Label
	Private lblP1100, lblP110, lblP11 As Label
	Private lblP2Maken100, lblP2Maken10, lblP2Maken1 As Label
	Private lblP2100, lblP210, lblP21 As Label
	Private lblP1Moy, lblP2Moy As Label
	Private lblBeurt100, lblBeurt10, lblBeurt1 As Label
	Private imgP2Play, imgP1Play, imgSponsor, imgNoData As ImageView
	Private lblTafelNaam, lblSpelduur As Label
End Sub

Sub Activity_Create(FirstTime As Boolean)
	Starter.mainPaused = False
	If Not (mqttBase.IsInitialized) Then
		mqttBase.Initialize
	End If
	baseFile.Initialize
	CallSub(Starter, "SetSubString")
	
	Activity.LoadLayout("ServerBoard")
'	lastMessageTime = DateTime.Now
	SetImgSponsor
'	lastMessageTimer.Initialize("tmrLastMessase", 120*1000)
'	lastMessageTimer.Enabled = True
	
'	Log($"connect client : " $Time{DateTime.Now}${CRLF}${Starter.SubString}"$)
'	If mqttBase.GetClientConnected = False Then
'		mqttBase.Connect
'	End If
	
	lblTafelNaam.Text = Starter.DiscoveredServer
	
'	Sleep(1000)
'	Log($"connect client : data please" $Time{DateTime.Now}"$)
'	mqttBase.SendMessage("data please")
End Sub

Sub ConnectionLost
	baseFile.createCustomToast("Verbinding met bord verloren", Colors.Red)
	Sleep(2000)
	lastMessageTimer.Enabled = False
	DisconnetMqtt
End Sub

Sub tmrLastMessase_Tick
	If (DateTime.Now-lastMessageTime) >= 120*1000 Then
		mqttBase.SendMessage("data please")
		lblSpelduur.TextColor = Colors.Red
	End If
End Sub

Sub Activity_Resume
	ResumeConnection(True)
End Sub

Sub Activity_Pause (UserClosed As Boolean)
	ResumeConnection(False)
End Sub

Sub ResumeConnection(resume As Boolean)
	If resume Then
		If mqttBase.GetClientConnected Then Return
		lastMessageTime = DateTime.Now
		If mqttBase.GetClientConnected = False Then
			mqttBase.Connect
		End If
		Sleep(500)
		mqttBase.SendMessage("data please")
		lastMessageTimer.Initialize("tmrLastMessase", 120*1000)
		lastMessageTimer.Enabled = True
		lastMessageTime = DateTime.Now
	Else
		mqttBase.Disconnect
		lastMessageTimer.Enabled = False
	End If
	
	lastMessageTimer.Enabled = resume
End Sub

Sub DisconnetMqtt
	If mqttBase.connected Then
		mqttBase.Disconnect
	End If
	Activity.Finish
End Sub

Private Sub Activity_KeyPress(KeyCode As Int) As Boolean
	If KeyCode = KeyCodes.KEYCODE_BACK Then
		DisconnetMqtt
		CallSubDelayed(Main, "setBordLastAliveTimer")
		lastMessageTimer.Enabled = False
		CallSubDelayed(Main, "ReconnectToLocation")
		Return False
	Else
		Return True
	End If
End Sub

public Sub UpdateBordWhenClient(data As Message)
	HideWaitLabel
	lastMessageTime = DateTime.Now
	lblSpelduur.TextColor = Colors.White
	Dim Number, str As String
	str = data.Body
	
	parser.Initialize(str)
	Dim root As Map = parser.NextObject
	Dim score As Map = root.Get("score")
	Dim p1 As Map = score.Get("p1")
	Dim p2 As Map = score.Get("p2")
'	Dim moyenne As String = p2.Get("moyenne")
	Dim aan_stoot As Map = score.Get("aan_stoot")
	Dim speler As String = aan_stoot.Get("speler")
	Dim spelduur As Map = score.Get("spelduur")
	Dim tijd As String = spelduur.Get("tijd")
	Dim beurten As Map = score.Get("beurten")
	Dim aantal As String = beurten.Get("aantal")
	
	lblP1Name.Text = p1.Get("naam")
	Number = p1.Get("caram")
	lblP1100.Text = Number.SubString2(0,1)
	lblP110.Text = Number.SubString2(1,2)
	lblP11.Text = Number.SubString2(2,3)
	Number = p1.Get("maken")
	lblP1Maken100.Text = Number.SubString2(0,1)
	lblP1Maken10.Text = Number.SubString2(1,2)
	lblP1Maken1.Text = Number.SubString2(2,3)
	lblP1Moy.Text = cs.Initialize.Typeface(Typeface.FONTAWESOME).Append(Chr(0xF201)).Append("  ").Append(p1.Get("moyenne")).PopAll
	
	lblP2Name.Text = p2.Get("naam")
	Number = p2.Get("caram")
	lblP2100.Text = Number.SubString2(0,1)
	lblP210.Text = Number.SubString2(1,2)
	lblP21.Text = Number.SubString2(2,3)
	Number = p2.Get("maken")
	lblP2Maken100.Text = Number.SubString2(0,1)
	lblP2Maken10.Text = Number.SubString2(1,2)
	lblP2Maken1.Text = Number.SubString2(2,3)

	cs.Initialize.Append("").Typeface(Typeface.FONTAWESOME).Append(Chr(0xF201)).PopAll
	lblP2Moy.Text = cs.Initialize.Typeface(Typeface.FONTAWESOME).Append(Chr(0xF201)).Append("  ").Append(p2.Get("moyenne")).PopAll
	
	lblBeurt100.Text = aantal.SubString2(0,1)
	lblBeurt10.Text = aantal.SubString2(1,2)
	lblBeurt1.Text = aantal.SubString2(2,3)
	lblSpelduur.Text = tijd'score.Get("spelduur")
	lblSpelduur.Text = cs.Initialize.Typeface(Typeface.FONTAWESOME).Append(Chr(0xF253)).Append("  ").Append(tijd).PopAll
	
	imgP1Play.Visible = False
	imgP2Play.Visible = False
	If speler = 1 Then
		imgP1Play.Visible = True
	Else
		imgP2Play.Visible = True
	End If
End Sub

Public Sub GamedEnded
	lblSpelduur.TextColor = Colors.Red
	Msgbox2Async("Spel beëindigd", Application.LabelName, "OKE", "", "", Application.Icon, False)
End Sub

Private Sub SetImgSponsor
	Dim bmp As Bitmap
	Dim bmpName As String
	Dim nuleen As Int = Rnd(0,3)
	
	If nuleen = 0 Then
		bmpName = "sven1.jpg"
	End If
	If nuleen = 1 Then
		bmpName = "sven_oud.jpg"
	End If
	If nuleen = 2 Then
		bmpName = "biljarter.png"
	End If
	
	bmp = LoadBitmapResize(File.DirAssets, bmpName, imgSponsor.Width, imgSponsor.Height, True)
	imgSponsor.SetBackgroundImage(bmp)
End Sub

'Sub BordDied
'	Msgbox2Async("Verbinding verbroken", Application.LabelName, "OK", "", "", Application.Icon, False)
'End Sub

Sub HideWaitLabel
	If imgNoData.Visible Then
		imgNoData.SetVisibleAnimated(0, False)
		Sleep(300)
	End If
End Sub