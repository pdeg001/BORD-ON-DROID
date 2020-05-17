B4A=true
Group=Default Group
ModulesStructureVersion=1
Type=Service
Version=6.5
@EndOfDesignText@
#Region  Service Attributes 
	#StartAtBoot: False
	#ExcludeFromLibrary: True
#End Region

Sub Process_Globals
	Public const port As Int = 1883
	Public const host As String = "pdeg3005.mynetgear.com"
	Public connected As Boolean
	Public DiscoveredServer As String
	Public serverList As List
	Public serverDied As Long = 10000
	Public selectedBordName As String
	Private mqttName As String = "pdeg"
	Private mqttBase As String
	Private mqttUnit As String
	Private mqttGetUnits As String
	Private rp As RuntimePermissions
	Public mqttGetBordsActive, mqttGetBordDataActive As Boolean
	Public diedIndex As Int = -1
	Private SubString, baseFile, baseFilePath As String
	Private storeFolder As String
	Public testBaseName As Boolean = False
	
	
End Sub

Sub Service_Create
	serverList.Initialize
	storeFolder = rp.GetSafeDirDefaultExternal("bod")
	
	baseFile = "bod.pdg"
	baseFilePath = File.Combine(storeFolder, baseFile)
'	CreateBaseFile
End Sub

Sub Service_Start (StartingIntent As Intent)

End Sub

Sub Application_Error (Error As Exception, StackTrace As String) As Boolean
	Return True
End Sub

Sub Service_Destroy

End Sub

Private Sub CreateBaseFile
	If Not(File.Exists(baseFilePath, "")) Then
		File.WriteString(baseFilePath, "", "")
	End If
End Sub

Public Sub SetSubString
	SubString = $"${mqttName}/${mqttBase}/${mqttUnit}"$
End Sub

'set location code
Private Sub SetSubBase(baseName As String)
	mqttBase = baseName
End Sub

Private Sub GetSubString As String
	Return SubString
End Sub

Private Sub SetUnit(name As String)
	mqttUnit = name
End Sub

Private Sub SetSubGetUnits
	mqttGetUnits = $"${mqttName}/${mqttBase}"$
End Sub

Private Sub GetSubUnits As String
	Return mqttGetUnits
End Sub

Public Sub GetBase As String
	Return mqttGetUnits '$"${mqttName}/${mqttBase}/"$
End Sub

Private Sub GetBaseFilePath As String
	Return baseFilePath
End Sub

Public Sub SetLs
'	Dim ph As Phone
'	ph.SetScreenOrientation(0)
End Sub