B4A=true
Group=Default Group
ModulesStructureVersion=1
Type=Activity
Version=9.801
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: False
	#IncludeTitle: False
#End Region

Sub Process_Globals
	Dim baseFile As Base
	Private clsFunc As ClassSetFontSize
End Sub

Sub Globals
	Private lblLocationCode As Label
	Private lblViewBord As Label
	Private pnlBord As Panel
	Private clvLocation As CustomListView
	Private lblDescription As Label
	Private lblVersion As Label
	Private pnlBack As Panel
	
	Private lblEye As Label
End Sub

Sub Activity_Create(FirstTime As Boolean)
	clsFunc.Initialize
	Starter.mainPaused = False
	Activity.LoadLayout("selectlocation")
	clsFunc.ResetUserFontScale(Activity)
	lblVersion.Text = Starter.appVersion
	baseFile.Initialize
	CreateLocation
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Private Sub Activity_KeyPress(KeyCode As Int) As Boolean
	If Starter.selectedLocationCode = "" Then
		baseFile.createCustomToast("Selecteer een locatie", 0xFF008080)
		Return True
	End If
	If KeyCode = KeyCodes.KEYCODE_BACK Then
		Starter.locationSelected = True
		Return False
	Else
		Return True
	End If
End Sub

Sub CreateLocation
	Dim baseList As List = baseFile.GetBase
	clvLocation.Clear
	
	For Each loc As locationBord In baseList
		clvLocation.Add(CreateLocatie(loc.code, loc.description), "")
	Next
End Sub

Sub CreateLocatie(code As String, description As String) As Panel
	Dim p As Panel
	p.Initialize(Me)
	p.SetLayout(0dip, 0dip, clvLocation.AsView.Width, 85dip) '190
	p.LoadLayout("clvSelectLocation")
'	Log($"ACTIVE UNIT ${Starter.selectedLocationCode}"$)
	lblLocationCode.Text = code
	lblDescription.Text = description
	
	lblEye.Visible = code = Starter.selectedLocationCode
	clsFunc.ResetUserFontScale(p)
	Return p
End Sub

Sub pnlBord_Click
	Dim p As Panel = Sender
	Starter.locationSelected = True
	Starter.selectedLocationCode = baseFile.GetSelectedLabelTagFromPanel(p, "code")
	Starter.selectedLocationDescription = baseFile.GetSelectedLabelTagFromPanel(p, "name")
	Activity.Finish	
	
	'CallSubDelayed(Main, "InitConnection")
End Sub



Sub pnlBack_Click
	If Starter.selectedLocationCode = "" Then
		baseFile.createCustomToast("Selecteer een locatie", 0xFF008080)
		Return
	End If
	Activity.Finish
End Sub