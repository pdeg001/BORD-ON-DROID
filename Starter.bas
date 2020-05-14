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
	Public mqttGetBordsActive, mqttGetBordDataActive As Boolean
End Sub

Sub Service_Create
	serverList.Initialize
End Sub

Sub Service_Start (StartingIntent As Intent)

End Sub

Sub Application_Error (Error As Exception, StackTrace As String) As Boolean
	Return True
End Sub

Sub Service_Destroy

End Sub
