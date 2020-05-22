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
	Private refreshList As Boolean
	Private lblVersion As Label
	Private pnlDelete As Panel
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
		clvLocation.Add(CreateLocatie(loc.code, loc.description, loc.isdefault, baseList.Size), "")
	Next
	
End Sub

Sub CreateLocatie(code As String, description As String, isDefault As String, listSize As Int) As Panel
	Starter.mainPaused = False
	Dim p As Panel
	p.Initialize(Me)
	p.SetLayout(0dip, 0dip, clvLocation.AsView.Width, 150dip) '190
	p.LoadLayout("clvLocation")
	
	lblLocatie.Text = code
	lblDescription.Text = description
'	chkDefault.Checked = isDefault = 1
'	If listSize = 1 Then
'		chkDefault.Enabled = False
'	End If
	Return p
End Sub

Sub clvLocation_ItemClick (Index As Int, Value As Object)
	SetEditFields(clvLocation.GetPanel(Index))
	pnlEditLocation.SetVisibleAnimated(500, True)
	lblEdtLocatie.Text = "Locatie bewerken"	
	edtDescription.RequestFocusAndShowKeyboard
	
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
			End If
		End If
	Next
	
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
	
	refreshList = True
	GetLocations
	btnEditCancel_Click
	refreshList = False
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

'Sub chkDefault_CheckedChange(Checked As Boolean)
'	If refreshList Then Return
'	
'	Dim chk As CheckBox = Sender
'	Dim p As Panel = chk.Parent
'	Dim selectedCode As String
'	
'	refreshList = True
'
'	Dim pnl As Panel = clvLocation.GetPanel(clvLocation.GetItemFromView(p))	
'	'GET SELECTED CODE
'	selectedCode = GetSelectedCode(p, "code")
'	Log("SELECTED CODE : " & selectedCode)
'	
'	RemoveDefaultLocation
'	
'	baseFile.ModifyLocation(selectedCode, selectedCode, GetSelectedCode(pnl,"description"), True)
'	chk.Checked = True
'	'GetLocations
'	refreshList = False
'End Sub

Private Sub GetSelectedCode(p As Panel, strTag As String) As String
	Log(strTag)
	Dim lbl As Label
	Dim chk As B4XView
	
	For Each v As B4XView In p.GetAllViewsRecursive
		If v Is CheckBox Then
			chk = v
			chk.Checked = False
			Return ""
			Exit
		End If
		
		If v.Tag = "" Then Continue
		
		If v.Tag = strTag And v Is Label Then
			lbl = v
			Return lbl.Text
		End If
	Next
	Return ""
End Sub

'Private Sub RemoveDefaultLocation
'	Dim p As Panel
'	Dim code, description As String
'	refreshList = True
'	For i = 0 To clvLocation.Size -1
'		
'		Log("WDMKWMD - " &i)
'		p = clvLocation.GetPanel(i)	
''		code = GetSelectedCode(p, "code")
''		description = GetSelectedCode(p, "description")
'		GetSelectedCode(p, "isdefault")
''		baseFile.ModifyLocation(code, code, description, False)
'	Next
'	refreshList = False
'End Sub

'Sub pnlDeleteLocation_Click
'	Dim v As View = Sender
'	
'	Msgbox2Async("Locatie verwijderen?", Application.LabelName, "JA", "", "NEE", Application.Icon, False)
'	Wait For Msgbox_Result (Result As Int)
'	If Result = DialogResponse.POSITIVE Then
'		DeleteLocation(v)
'	End If
'End Sub

Private Sub DeleteLocation(v As View)
	Dim p As Panel = v.Parent
	Dim code As String =GetSelectedCode(p, "code")
	baseFile.DeleteBase(code)
End Sub




Sub pnlDelete_Click
	Dim v As View = Sender
	
	Msgbox2Async("Locatie verwijderen?", Application.LabelName, "JA", "", "NEE", Application.Icon, False)
	Wait For Msgbox_Result (Result As Int)
	If Result = DialogResponse.POSITIVE Then
		DeleteLocation(v)
	End If
End Sub