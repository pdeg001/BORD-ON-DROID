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
'	Dim mqttGetData As mqttGetBordData
	Dim mqttBase As MqttConnector
	Dim baseFile As Base
	Dim dataTmr As Timer
	Dim dotCount As Int = 0
'	Dim waitText As String 
	Dim cs As CSBuilder
'Private SubString As String
	Dim lastMessageTime As Long
	Dim lastMessageTimer As Timer
End Sub

Sub Globals
'	Dim lblList() As Label
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
	If Not (mqttBase.IsInitialized) Then
		mqttBase.Initialize
	End If
	baseFile.Initialize
	CallSub(Starter, "SetSubString")
'	mqttBase.SetSub
	
	Activity.LoadLayout("ServerBoard")
	lastMessageTimer.Initialize("tmrLastMessase", 120*1000)
	lastMessageTimer.Enabled = True
	
	imgNoData.BringToFront
	SetImg
	
	dataTmr.Initialize("dataTmr", 1000)
	mqttBase.Connect
	
	imgNoData.SetVisibleAnimated(1, True)
	lblTafelNaam.Text = Starter.DiscoveredServer
	
	Sleep(1000)
	mqttBase.SendMessage("data please")
End Sub

Sub ConnectionLost
	'ToastMessageShow("Verbinding met bord verloren", True)
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

Sub dataTmr_Tick
	Dim dot As String
	dotCount=dotCount+1
	If dotCount >= 10 Then
		dotCount = 0
		'lblNoData.Text = Starter.DiscoveredServer
		Return
	End If
	For i = 0 To dotCount
		dot = dot &"*"
	Next
'	lblNoData.Text = $"${dot} ${waitText} ${dot}"$
End Sub

Sub Activity_Resume
'	waitText = $"Wachten op ${Starter.DiscoveredServer}"$
	dotCount = 0
'	Starter.SendMessage("data please")
End Sub

Sub Activity_Pause (UserClosed As Boolean)
'	Log("pause")
	If mqttBase.connected Then
		mqttBase.Disconnect
	End If
	lastMessageTimer.Enabled = False
	Activity.Finish
End Sub

Sub DisconnetMqtt
	If mqttBase.connected Then
		mqttBase.Disconnect
	End If
	Activity.Finish
End Sub

Private Sub Activity_KeyPress(KeyCode As Int) As Boolean
	If KeyCode = KeyCodes.KEYCODE_BACK Then
		CallSubDelayed(Main, "setBordLastAliveTimer")
		lastMessageTimer.Enabled = False
		DisconnetMqtt
		
		Return False
	Else
		Return True
	End If
End Sub

public Sub UpdateBordWhenClient(data As Message)
	If imgNoData.Visible Then
		dataTmr.Enabled = False
		imgNoData.SetVisibleAnimated(1000, False)
		Sleep(1200)
	End If
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

Public Sub GamedInProgress
'	lblNoData.TextColor = Colors.White
'	lblNoData.Text = $"U kijkt naar ${Starter.DiscoveredServer}"$
End Sub

Private Sub SetImg
	Dim bmp As Bitmap
	Dim nuleen As Int = Rnd(0,2)
	
	If nuleen = 0 Then
		bmp = LoadBitmapResize(File.DirAssets, "sven1.jpg", imgSponsor.Width, imgSponsor.Height, True)
	Else
		bmp = LoadBitmapResize(File.DirAssets, "sven_oud.jpg", imgSponsor.Width, imgSponsor.Height, True)
	End If
	imgSponsor.SetBackgroundImage(bmp)
End Sub

Sub BordDied
	Msgbox2Async("Verbinding verbroken", Application.LabelName, "OK", "", "", Application.Icon, False)
End Sub