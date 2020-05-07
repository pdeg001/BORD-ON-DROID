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
	'These global variables will be declared once when the application starts.
	'These variables can be accessed from all modules.

End Sub

Sub Globals
	Dim parser As JSONParser

	Private lblP1Name As Label
	Private lblP2Name As Label
	Private lblP1Maken100 As Label
	Private lblP1Maken10 As Label
	Private lblP1Maken1 As Label
	Private lblP1100 As Label
	Private lblP110 As Label
	Private lblP11 As Label
	Private lblP2Maken100 As Label
	Private lblP2Maken10 As Label
	Private lblP2Maken1 As Label
	Private lblP2100 As Label
	Private lblP210 As Label
	Private lblP1Moy As Label
	Private lblP2Moy As Label
	Private lblP21 As Label
	Private lblBeurt100 As Label
	Private lblBeurt10 As Label
	Private lblBeurt1 As Label
	Private imgP2Play As ImageView
	Private imgP1Play As ImageView
End Sub

Sub Activity_Create(FirstTime As Boolean)
	Activity.LoadLayout("ServerBoard")
	Log(100%Y/100%X)

End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

public Sub UpdateBordWhenClient(data As Message)
	Dim number, str As String
	
	str = data.Body
	
	parser.Initialize(str)
	Dim root As Map = parser.NextObject
	Dim score As Map = root.Get("score")
	Dim p1 As Map = score.Get("p1")
'	Dim caram As String = p1.Get("caram")
'	Dim percentage As String = p1.Get("percentage")
'	Dim naam As String = p1.Get("naam")
'	Dim maken As String = p1.Get("maken")
'	Dim moyenne As String = p1.Get("moyenne")
	Dim p2 As Map = score.Get("p2")
'	Dim caram As String = p2.Get("caram")
'	Dim percentage As String = p2.Get("percentage")
'	Dim naam As String = p2.Get("naam")
'	Dim maken As String = p2.Get("maken")
	Dim moyenne As String = p2.Get("moyenne")
	Dim aan_stoot As Map = score.Get("aan_stoot")
	Dim speler As String = aan_stoot.Get("speler")
	Dim spelduur As Map = score.Get("spelduur")
	Dim tijd As String = spelduur.Get("tijd")
'	Dim autoinnings As Map = score.Get("autoinnings")
'	Dim value As String = autoinnings.Get("value")
	Dim beurten As Map = score.Get("beurten")
	Dim aantal As String = beurten.Get("aantal")
	
	
	
'	lbl_player_one_name.Text = p1.Get("naam")
	number = p1.Get("caram")
	lblP1100.Text = number.SubString2(0,1)
	lblP110.Text = number.SubString2(1,2)
	lblP11.Text = number.SubString2(2,3)
	number = p1.Get("maken")
	lblP1Maken100.Text = number.SubString2(0,1)
	lblP1Maken10.Text = number.SubString2(1,2)
	lblP1Maken1.Text = number.SubString2(2,3)
	lblP1Moy.Text = p1.Get("moyenne")
'	lbl_player_one_perc.Text = p1.Get("percentage")
	
'	funcScorebord.p1_progress = ( p1.Get("caram")/p1.Get("maken"))*100
'	funcScorebord.p2_progress = ( p2.Get("caram")/p2.Get("maken"))*100
	
'	lbl_player_two_name.Text = p2.Get("naam")
	number = p2.Get("caram")
	lblP2100.Text = number.SubString2(0,1)
	lblP210.Text = number.SubString2(1,2)
	lblP21.Text = number.SubString2(2,3)
	number = p2.Get("maken")
	lblP2Maken100.Text = number.SubString2(0,1)
	lblP2Maken10.Text = number.SubString2(1,2)
	lblP2Maken1.Text = number.SubString2(2,3)
	lblP2Moy.Text = p2.Get("moyenne")
'	lbl_player_two_perc.Text = p2.Get("percentage")
	
	lblBeurt100.Text = aantal.SubString2(0,1)
	lblBeurt10.Text = aantal.SubString2(1,2)
	lblBeurt1.Text = aantal.SubString2(2,3)
'	lbl_innings.Text = aantal'score.Get("beurten")
'	lbl_partij_duur.Text = tijd'score.Get("spelduur")
	'setProgress(p1_progressBar, p1_progress)
	
'	CallSub(funcScorebord, "SetProgressBarForMirror")
	imgP1Play.Visible = False
	imgP2Play.Visible = False
	If speler = 1 Then
		imgP1Play.Visible = True
	Else
		imgP2Play.Visible = True
	End If
End Sub


