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
End Sub

Sub Globals
	Private lblLocationCode As Label
	Private lblViewBord As Label
	Private pnlBord As Panel
	Private clvLocation As CustomListView
	Private lblDescription As Label
	Private lblVersion As Label
	Private pnlBack As Panel
End Sub

Sub Activity_Create(FirstTime As Boolean)
	Starter.mainPaused = False
	Activity.LoadLayout("selectlocation")
	lblVersion.Text = Starter.appVersion
	baseFile.Initialize
	CreateLocation
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

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
	
	If code = Starter.selectedLocationCode Then
		Dim tf As Typeface
		Dim clr As Long =  0xFF282BFF
		
		tf = Typeface.CreateNew(Typeface.LoadFromAssets("Baloo2-Regular.ttf"), Typeface.STYLE_ITALIC)
		
		lblLocationCode.Typeface = tf
		lblLocationCode.TextColor = clr
		lblDescription.Typeface = tf
		lblDescription.TextColor = clr
	End If
	
	Return p
End Sub

Sub pnlBord_Click
	Dim p As Panel = Sender
	
	Starter.selectedLocationCode = baseFile.GetSelectedLabelTagFromPanel(p, "code")
	Starter.selectedLocationDescription = baseFile.GetSelectedLabelTagFromPanel(p, "name")
	Activity.Finish	
	
	CallSubDelayed(Main, "InitConnection")
End Sub



Sub pnlBack_Click
	Activity.Finish
End Sub