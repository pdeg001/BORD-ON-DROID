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
End Sub

Sub Activity_Create(FirstTime As Boolean)
	Activity.LoadLayout("selectlocation")
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
	p.SetLayout(0dip, 0dip, clvLocation.AsView.Width, 80dip) '190
	p.LoadLayout("clvSelectLocation")
	
	lblLocationCode.Text = code
	lblDescription.Text = description
	Return p
End Sub

Sub pnlBord_Click
	Dim p As Panel = Sender
	Dim lbl As Label
	Dim code, description As String
		
	For Each v As View In p.GetAllViewsRecursive
		If v.Tag = "code" Then
			lbl = v
			code = lbl.Text
			Continue
		End If
		
		If v.Tag = "name" Then
			lbl = v
			description = lbl.Text
		End If
	Next
	
	CallSubDelayed3(Main, "InitConnection", code, description)
	Sleep(0)
	Activity.Finish	
End Sub

