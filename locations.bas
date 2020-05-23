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
	Private pnlLocatie As Panel
	Private clvLocation As CustomListView
	Private pnlEditLocation As Panel
	
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
	Private ime As IME
	Private pnlNew As Panel
	Private lblEdtLocatie As Label
	Private pnlDeleteLocation As Panel
''	Private refreshList As Boolean
	Private lblVersion As Label
	Private pnlDelete As Panel
	Private pnlBack As Panel
End Sub

Sub Activity_Create(FirstTime As Boolean)
	Activity.LoadLayout("locations")
	lblVersion.Text = Starter.appVersion
	baseFile.Initialize
	ime.Initialize(Me)
	GetLocations
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub Activity_KeyPress (KeyCode As Int) As Boolean
	If KeyCode = KeyCodes.KEYCODE_BACK Then
		If pnlEditLocation.Visible Then
			btnEditCancel_Click
			Return True
		End If
	End If
	Return False
End Sub

Private Sub GetLocations
	Dim baseList As List = baseFile.GetBase
	clvLocation.Clear
	
	For Each loc As locationBord In baseList
		clvLocation.Add(CreateLocatie(loc.code, loc.description), "")
	Next
	
End Sub

Sub CreateLocatie(code As String, description As String) As Panel
	Starter.mainPaused = False
	Dim p As Panel
	p.Initialize(Me)
	p.SetLayout(0dip, 0dip, clvLocation.AsView.Width, 160dip) '190
	p.LoadLayout("clvLocation")
	p.Tag = code
	
	lblLocatie.Text = code
	lblDescription.Text = description
	Return p
End Sub

Sub clvLocation_ItemClick (Index As Int, Value As Object)
	SetEditFields(clvLocation.GetPanel(Index))
	pnlEditLocation.SetVisibleAnimated(500, True)
	lblEdtLocatie.Text = "Locatie bewerken"	
	edtDescription.RequestFocusAndShowKeyboard
	
End Sub

Private Sub SetEditFields(p As Panel)
	edtCode.Text = baseFile.GetSelectedLabelTagFromPanel(p, "code")
	currentCodeEdit = edtCode.Text
	edtDescription.Text = baseFile.GetSelectedLabelTagFromPanel(p, "description")
End Sub

Sub btnEditSave_Click
	If edtCode.Text = "" Then
		Msgbox2Async("Locatie mag niet leeg zijn", Application.LabelName, "OKE", "", "", Application.Icon, False)
		Wait For Msgbox_Result (Result As Int)
		If Result = DialogResponse.POSITIVE Then
			edtCode.Text = currentCodeEdit
			Return
		End If
	End If
	
	If currentCodeEdit = "new" Then
		If baseFile.LocationExist(edtCode.Text) Then
			Msgbox2Async("Locatie code bestaat al", Application.LabelName, "OKE", "", "", Application.Icon, False)
			Wait For Msgbox_Result (Result As Int)
			If Result = DialogResponse.POSITIVE Then
				Return
			End If
		Else
			baseFile.SetBase(edtCode.Text, edtDescription.Text, "0")
		End If
	Else
		baseFile.ModifyLocation(currentCodeEdit, edtCode.Text, edtDescription.Text, chkEdtDefault.Checked)
	End If
	
''	refreshList = True
	GetLocations
	btnEditCancel_Click
''	refreshList = False
End Sub

Sub btnEditCancel_Click
	ime.HideKeyboard
	pnlEditLocation.SetVisibleAnimated(500, False)
	currentCodeEdit = ""
	edtCode.Text = ""
	edtDescription.Text = ""
	
End Sub

Sub pnlNew_Click
	currentCodeEdit = "new"
	lblEdtLocatie.Text = "Locatie toevoegen"
	pnlEditLocation.SetVisibleAnimated(500, True)
	edtCode.RequestFocusAndShowKeyboard
End Sub

Private Sub DeleteLocation(v As View)
	Dim p As Panel = v.Parent
	Dim code As String = baseFile.GetSelectedLabelTagFromPanel(p, "code")
	baseFile.DeleteBase(code)
	
	If CallSub(Starter, "GetUnit") = code Then
		CallSub(Main, "DeletedLocationActive")
	End If
End Sub

Sub pnlDelete_Click
	Dim v As View = Sender
	Dim p As Panel = v.Parent
		
	Msgbox2Async($"Locatie ${p.tag} verwijderen?"$, Application.LabelName, "JA", "", "NEE", Application.Icon, False)
	Wait For Msgbox_Result (Result As Int)
	If Result = DialogResponse.POSITIVE Then
		DeleteLocation(v)
		clvLocation.RemoveAt(baseFile.GetClvPanelIndex(p, clvLocation))
		baseFile.ShowCustomToast("Locatie verwijderd", False, Colors.Green)
	End If
End Sub

Sub pnlBack_Click
	Activity.Finish
End Sub