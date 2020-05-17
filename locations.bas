B4A=true
Group=Default Group
ModulesStructureVersion=1
Type=Activity
Version=9.801
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: true
		#IncludeTitle: False
#End Region

Sub Process_Globals
	Dim ph As Phone
	Dim baseFile As Base
End Sub

Sub Globals
	'These global variables will be redeclared each time the activity is created.
	'These variables can only be accessed from this module.

	Private pnlLocatie As Panel
	'Private clvLocation As CustomListView
	Private clvLocation As CustomListView
	Private pnlEditLocation As Panel
	Private chkDefault As CheckBox
	
	Private lblLocatie As Label
	Private lblDescription As Label
	Private lblEditLocation As Label
	Private btnEditSave As Button
	Private btnEditCancel As Button
	Private edtCode As B4XFloatTextField
	Private edtDescription As B4XFloatTextField
	Private lblDelete As Label
	Private chkEdtDefault As CheckBox
	Private currentCodeEdit As String
End Sub

Sub Activity_Create(FirstTime As Boolean)
ph.SetScreenOrientation(1)
	Activity.LoadLayout("locations")
	baseFile.Initialize
	GetLocations
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Private Sub GetLocations
	Dim baseList As List = baseFile.GetBase
	clvLocation.Clear
	For Each loc As locationBord In baseList
		clvLocation.Add(CreateLocatie(loc.code, loc.description, loc.isdefault), "")
	Next
	
End Sub


Sub CreateLocatie(code As String, description As String, isDefault As String) As Panel
	Dim p As Panel
	p.Initialize(Me)
	p.SetLayout(0dip, 0dip, clvLocation.AsView.Width, 160dip) '190
	p.LoadLayout("clvLocation")
	
	Log(description)
	lblLocatie.Text = code
	lblDescription.Text = description
	chkDefault.Checked = isDefault = 1
	
	Return p
End Sub

Sub clvLocation_ItemClick (Index As Int, Value As Object)
	SetEditFields(clvLocation.GetPanel(Index))
	pnlEditLocation.SetVisibleAnimated(500, True)	
End Sub

Private Sub SetEditFields(p As Panel)
	Dim lbl As Label
	Dim chk As CheckBox
	For Each v As View In p.GetAllViewsRecursive
		If v.Tag = "code" Then
			lbl = v
			edtCode.Text = lbl.Text
			currentCodeEdit = lbl.Text
		End If
		If v.Tag = "description" Then
			lbl = v
			edtDescription.Text = lbl.Text
		End If
		If v.Tag = "isdefault" Then
			chk = v
			chkEdtDefault.Checked = chk.Checked
			If clvLocation.Size = 1 Then
				chkEdtDefault.Checked = True
				chkEdtDefault.Enabled = False
			End If
		End If
	Next
	
End Sub

Sub btnEditSave_Click
	baseFile.ModifyLocation(currentCodeEdit, edtCode.Text, edtDescription.Text, chkEdtDefault.Checked)
	GetLocations
	btnEditCancel_Click
End Sub

Sub btnEditCancel_Click
	pnlEditLocation.SetVisibleAnimated(1000, False)
	currentCodeEdit = ""
End Sub