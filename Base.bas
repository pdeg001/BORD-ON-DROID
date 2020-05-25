B4A=true
Group=Default Group
ModulesStructureVersion=1
Type=Class
Version=9.801
@EndOfDesignText@
Sub Class_Globals
	Private baseList As List
	Private baseFile As String
	Private serializator As B4XSerializator
End Sub

Public Sub Initialize
	baseFile = CallSub(Starter, "GetBaseFilePath")
	baseList.Initialize
End Sub

Public Sub GetBase As List
	Dim lstDummy As List
	
	
	If CheckBaseListExists Then
		Return serializator.ConvertBytesToObject(File.ReadBytes(baseFile, ""))
	Else
		lstDummy.Initialize
		Return lstDummy
	End If
End Sub

Public Sub SetBase(baseName As String, description As String, isDefault As String)
	Dim loc As locationBord
	Dim lst As List
	
	loc.Initialize
	lst.Initialize
	
	loc.code = baseName
	loc.description = description
	loc.isdefault = isDefault
	
	If CheckBaseListExists Then
		lst = serializator.ConvertBytesToObject(File.ReadBytes(baseFile, ""))
	End If
	lst.Add(loc)

	writeList(lst)
End Sub

Private Sub CheckBaseListExists As Boolean
	If File.Exists(baseFile, "") Then
		Return True
	End If
	Return False
End Sub

Public Sub ModifyLocation(oldLocation As String, newLocation As String, description As String, isDefault As Boolean)
	Dim modList As List = GetBase
	
	For Each Location As locationBord In modList
		If Location.code = oldLocation Then
			Location.code = newLocation
			Location.description = description
			If isDefault Then
				Location.isdefault = "1"
			Else
				Location.isdefault = "0"
			End If
			Exit
		End If
	Next
	writeList(modList)
End Sub

Public Sub LocationExist(locationCode As String) As Boolean
	Dim modList As List = GetBase
	
	For Each location As locationBord In modList
		If location.code = locationCode Then
			Return True
		End If
	Next
	Return False
End Sub

Private Sub writeList(lst As List)
	serializator.ConvertObjectToBytes(lst)
	File.WriteBytes(baseFile, "", serializator.ConvertObjectToBytes(lst))
End Sub

Public Sub DeleteBase(locationCode As String)
	Dim modList As List = GetBase
	Dim locBord As locationBord
	
	For i = 0 To modList.Size -1
		locBord = modList.Get(i)
		If locBord.code = locationCode Then
			modList.RemoveAt(i)
			writeList(modList)
			Exit
		End If
	Next
End Sub

Public Sub SetBordDiedByName(bordName As String, clv As CustomListView, alive As Boolean)' As Boolean
	Dim p As Panel

	For i = 0 To clv.Size -1
		p = clv.GetPanel(i)
		SetBordOffline(GetPanelFromTag("name", bordName, p), alive)
	Next
End Sub

Private Sub SetBordOffline(p As Panel, alive As Boolean)
	Dim lbl As Label
	
	If p.IsInitialized = False Then Return
	For Each v As View In p.GetAllViewsRecursive
		If v.Tag = "viewbord" Then
			lbl = v
			If alive Then
				lbl.SetVisibleAnimated(200, True)
				Sleep(0)
				Exit
			Else
				lbl.SetVisibleAnimated(200, False)
				Sleep(0)
				Exit
			End If
		End If
		
	Next
End Sub

Sub createCustomToast(txt As String, color As String)
	Dim cs As CSBuilder
	cs.Initialize.Typeface(Typeface.LoadFromAssets("Baloo2-Regular.ttf")).Color(Colors.White).Size(16).Append(txt).PopAll
	ShowCustomToast(cs, False, color)
End Sub

Sub ShowCustomToast(Text As Object, LongDuration As Boolean, BackgroundColor As Int)
	Dim ctxt As JavaObject
	ctxt.InitializeContext
	Dim duration As Int
	If LongDuration Then duration = 1 Else duration = 0
	Dim toast As JavaObject
	toast = toast.InitializeStatic("android.widget.Toast").RunMethod("makeText", Array(ctxt, Text, duration))
	Dim v As View = toast.RunMethod("getView", Null)
	Dim cd As ColorDrawable
	cd.Initialize(BackgroundColor, 20dip)
	v.Background = cd
	
	'uncomment to show toast in the center:
	'  toast.RunMethod("setGravity", Array( _
	' Bit.Or(Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL), 0, 0))
	toast.RunMethod("show", Null)
End Sub

Sub GetSelectedLabelTagFromPanel(p As Panel, strTag As String) As String
	Dim lbl As Label
	
	For Each v As B4XView In p.GetAllViewsRecursive
		If v.Tag = "" Then Continue
		
		If v.Tag = strTag And v Is Label Then
			lbl = v
			Return lbl.Text
		End If
	Next
	Return ""
End Sub

''Sub SetSelectedTagFromPanel(p As Panel, strTag As String) As String
''	Dim lbl As Label
''	
''	For Each v As B4XView In p.GetAllViewsRecursive
''		If v.Tag = "" Then Continue
''		
''		If v.Tag = strTag And v Is Label Then
''			lbl = v
''			Return lbl.Text
''		End If
''	Next
''	Return ""
''End Sub

Sub GetClvPanelIndex(p As Panel, clv As CustomListView) As Int
	Return clv.GetItemFromView(p)
End Sub

Sub GetPanelFromTag(tagName As String, bordName As String, p As Panel) As Panel
	Dim lbl As Label
	For Each v As View In p.GetAllViewsRecursive
		If v.Tag = tagName Then
			lbl = v
			If lbl.Text <> bordName Then Continue
			Return p
			Exit
		End If
	Next
	Return Null
End Sub

Sub GetBordAlive(name As String) As Boolean
	For Each lst As bordStatus In Starter.serverList
		If lst.name = name Then
			Return lst.alive
		End If
	Next
	Return False
End Sub

Sub SetPanelLabelItemText(p As Panel, strTag As String, itemText As String)
	Dim lbl As Label
	
	For Each v As B4XView In p.GetAllViewsRecursive
		If v.Tag = "" Then Continue
		
		If v.Tag = strTag And v Is Label Then
			lbl = v
			lbl.Text = itemText
			Exit
		End If
	Next
End Sub

'bord name is unique
Sub GetPanelIndexFromBordName(name As String, clv As CustomListView) As Int
	Dim pnl As Panel
	Dim lbl As Label
	For i = 0 To clv.Size - 1
		pnl = clv.GetPanel(i)
		
		For Each v As View In pnl.GetAllViewsRecursive
			If v.Tag = "name" Then
				lbl = v
				If lbl.Text = name Then
					Return clv.GetItemFromView(pnl)
					Exit
				End If
			End If
		Next
	Next
	Return -1
End Sub

Sub GetServerlistIndexFromName(name As String) As Int
	Dim bd As bordStatus
	
	For i = 0 To Starter.serverList.Size -1
		bd = Starter.serverList.Get(i)
		If bd.name = name Then
			Return i
			Exit
		End If
	Next
	Return -1
End Sub


