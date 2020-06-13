package nl.pdeg.bordondroid.designerscripts;
import anywheresoftware.b4a.objects.TextViewWrapper;
import anywheresoftware.b4a.objects.ImageViewWrapper;
import anywheresoftware.b4a.BA;


public class LS_serverboard{

public static void LS_general(java.util.LinkedHashMap<String, anywheresoftware.b4a.keywords.LayoutBuilder.ViewWrapperAndAnchor> views, int width, int height, float scale) {
anywheresoftware.b4a.keywords.LayoutBuilder.setScaleRate(0.3);
if ((anywheresoftware.b4a.keywords.LayoutBuilder.getScreenSize()>=6d)) { 
;
anywheresoftware.b4a.keywords.LayoutBuilder.setScaleRate((100d / 100 * height)/(100d / 100 * width));
;}else{ 
;
anywheresoftware.b4a.keywords.LayoutBuilder.setScaleRate(1d);
;};
//BA.debugLineNum = 10;BA.debugLine="AutoScaleAll"[ServerBoard/General script]
anywheresoftware.b4a.keywords.LayoutBuilder.scaleAll(views);

}
}