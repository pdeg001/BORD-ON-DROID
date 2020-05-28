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
'	Private mqtt As MqttClient
	'Private pingMqtt As Boolean
	Private cs As CSBuilder
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
'		If v.Tag = "viewbord" Then
		If v.Tag = "name" Then
			lbl = v
			If alive Then
				lbl.Color = 0x6C00DD45
			'	lbl.SetVisibleAnimated(200, True)
				Sleep(0)
				Exit
			Else
				lbl.SetColorAnimated(2000, 0x6C912223, 0x6CDD0001)
			'	lbl.SetVisibleAnimated(200, False)
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

Sub GetBordAlive(Name As String) As Boolean
	For Each lst As bordStatus In Starter.serverList
		If lst.name = Name Then
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
Sub GetPanelIndexFromBordName(Name As String, clv As CustomListView) As Int
	Dim pnl As Panel
	Dim lbl As Label
	For i = 0 To clv.Size - 1
		pnl = clv.GetPanel(i)
		
		For Each v As View In pnl.GetAllViewsRecursive
			If v.Tag = "name" Then
				lbl = v
				If lbl.Text = Name Then
					Return clv.GetItemFromView(pnl)
					Exit
				End If
			End If
		Next
	Next
	Return -1
End Sub

Sub GetServerlistIndexFromName(Name As String) As Int
	Dim bd As bordStatus
	
	For i = 0 To Starter.serverList.Size -1
		bd = Starter.serverList.Get(i)
		If bd.name = Name Then
			Return i
			Exit
		End If
	Next
	Return -1
End Sub

Sub CheckPlayers(players As String, bordName As String, clv As CustomListView)
	Dim index As Int = GetServerlistIndexFromName(bordName)
	Dim pnl As Panel = clv.GetPanel(index)
	Dim lbl As Label
	
	For Each v As View In pnl.GetAllViewsRecursive
		If v.Tag = "players" Then
			lbl = v
			lbl.Text = SetPlayertext(players)
		End If
	Next
End Sub

Sub NameToCamelCase(name As String) As String
	Dim nameList() As String = Regex.Split(" ", name)
	If nameList.Length = 2 Then
		nameList(0) = SetFirstLetterUpperCase(nameList(0))
		nameList(1) = SetFirstLetterUpperCase(nameList(1))
		Return $"${nameList(0)} ${nameList(1)}"$
	End If
	If nameList.Length = 3 Then
		nameList(0) = SetFirstLetterUpperCase(nameList(0))
		nameList(2) = SetFirstLetterUpperCase(nameList(2))
		Return $"${nameList(0)} ${nameList(1).ToLowerCase} ${nameList(2)}"$
	End If

	Return name
End Sub

Private Sub SetFirstLetterUpperCase(str As String) As String
	str = str.ToLowerCase
	Dim m As Matcher = Regex.Matcher("\b(\w)", str)
	Do While m.Find
		Dim i As Int = m.GetStart(1)
		str = str.SubString2(0, i) & str.SubString2(i, i + 1).ToUpperCase & str.SubString(i + 1)
	Loop
	
	Return str
End Sub

'Sub ConnectAndReconnect
'	Do While pingMqtt
'		If mqtt.IsInitialized Then mqtt.Close
'		mqtt.Initialize("mqtt", $"${Starter.host}:${Starter.port}"$, "pdeg_" & Rnd(0, 999999999))
'		Dim mo As MqttConnectOptions
'		mo.Initialize("", "")
'		mqtt.Connect2(mo)
'
'		Wait For Mqtt_Connected (Success As Boolean)
'		If Success Then
'			Do While pingMqtt And mqtt.Connected
'				mqtt.Publish2("ping", Array As Byte(0), 1, False) 'change the ping topic as needed
''				Log($"Mqtt $DateTime{DateTime.Now}"$)
'				Sleep(5000)
'			Loop
'			
'			Log("Disconnected")
'			
'			If mqtt.IsInitialized Then mqtt.Close
'		Else
'			Log("Error connecting.")
'			If mqtt.IsInitialized Then mqtt.Close
'		End If
'		Sleep(5000)
'	Loop
'End Sub

Sub SetPlayertext(data As String) As Object
	Dim nameP1, nameP2, aanstoot As String
	Dim caromP1, caromP2 As Int
	Dim strData() As String = Regex.Split("\|", data)
	Dim fnt As Typeface = Typeface.LoadFromAssets("materialdesignicons-webfont.ttf") 'Chr(0xf130)
	Dim icon As String = Chr(0xfcbf)
	Dim noIcon As String = Chr(0)
	Dim iconSize As Int = 20
	Dim iconColor As Long = 0xFF00772C
	

	caromP1 = strData(3)
	caromP2 = strData(4)
	aanstoot = strData(5)
	nameP1 = NameToCamelCase(strData(1))
	nameP2 = NameToCamelCase(strData(2))
	
	cs.Initialize
	If caromP1 > caromP2 Then
		If aanstoot = "1" Then
			cs.Color(Colors.Blue).Typeface(Typeface.DEFAULT).Append($"${strData(3)}${TAB}${TAB}${nameP1}   "$).Color(iconColor).Typeface(fnt).Size(iconSize).Append(icon).pop
			cs.Color(Colors.Black).Typeface(Typeface.DEFAULT).Append($"${CRLF}${strData(4)}${TAB}${TAB}${nameP2}"$).Typeface(fnt).Size(iconSize).Append(noIcon).PopAll
		Else
			cs.Color(Colors.Blue).Typeface(Typeface.DEFAULT).Append($"${strData(3)}${TAB}${TAB}${nameP1}"$).Size(iconSize).Append(noIcon).pop
			cs.Color(Colors.Black).Typeface(Typeface.DEFAULT).Append($"${CRLF}${strData(4)}${TAB}${TAB}${nameP2}   "$).Color(iconColor).Typeface(fnt).Size(iconSize).Append(icon).PopAll
		End If
	Else If caromP2 > caromP1 Then
		If aanstoot = "1" Then
			cs.Color(Colors.Black).Typeface(Typeface.DEFAULT).Append($"${strData(3)}${TAB}${TAB}${nameP1}   "$).Color(iconColor).Typeface(fnt).Size(iconSize).Append(icon).pop
			cs.Color(Colors.Blue).Typeface(Typeface.DEFAULT).Append($"${CRLF}"$ & $"${strData(4)}${TAB}${TAB}${nameP2}"$ ).Size(iconSize).Append(noIcon).PopAll
		Else
			cs.Color(Colors.Black).Typeface(Typeface.DEFAULT).Append($"${strData(3)}${TAB}${TAB}${nameP1}"$).Size(iconSize).Append(noIcon).pop
			cs.Color(Colors.Blue).Typeface(Typeface.DEFAULT).Append($"${CRLF}"$ & $"${strData(4)}${TAB}${TAB}${nameP2}   "$ ).Color(iconColor).Typeface(fnt).Size(iconSize).Append(icon).PopAll
		End If
	Else
		If aanstoot = "1" Then
			cs.Color(Colors.Black).Typeface(Typeface.DEFAULT).Append($"${strData(3)}${TAB}${TAB}${nameP1}   "$).Color(iconColor).Typeface(fnt).Size(iconSize).Append(icon).pop
			cs.Typeface(Typeface.DEFAULT).Append($"${CRLF}"$ & $"${strData(4)}${TAB}${TAB}${nameP2}"$ ).Size(iconSize).Append(noIcon).PopAll
		Else
			cs.Color(Colors.Black).Typeface(Typeface.DEFAULT).Append($"${strData(3)}${TAB}${TAB}${nameP1}"$).Size(iconSize).Append(noIcon).pop
			cs.Typeface(Typeface.DEFAULT).Append($"${CRLF}"$ & $"${strData(4)}${TAB}${TAB}${nameP2}   "$ ).Color(iconColor).Typeface(fnt).Size(iconSize).Append(icon).PopAll
		End If
	End If
	
	Return cs
End Sub

Public Sub shadowLayer(lbl As View, Radius As Float, dx As Float, dy As Float, Color As Int)
	Dim jo = lbl As JavaObject
	jo.RunMethod("setShadowLayer", Array(Radius, dx, dy , Color))

End Sub
