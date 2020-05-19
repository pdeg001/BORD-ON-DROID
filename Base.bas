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
	If CheckBaseListExists Then
		Return serializator.ConvertBytesToObject(File.ReadBytes(baseFile, ""))
	Else
		Return Null
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