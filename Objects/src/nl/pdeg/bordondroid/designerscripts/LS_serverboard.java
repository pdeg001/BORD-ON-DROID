package nl.pdeg.bordondroid.designerscripts;
import anywheresoftware.b4a.objects.TextViewWrapper;
import anywheresoftware.b4a.objects.ImageViewWrapper;
import anywheresoftware.b4a.BA;


public class LS_serverboard{

public static void LS_general(java.util.LinkedHashMap<String, anywheresoftware.b4a.keywords.LayoutBuilder.ViewWrapperAndAnchor> views, int width, int height, float scale) {
anywheresoftware.b4a.keywords.LayoutBuilder.setScaleRate(0.3);
anywheresoftware.b4a.keywords.LayoutBuilder.setScaleRate((100d / 100 * height)/(100d / 100 * width));
anywheresoftware.b4a.keywords.LayoutBuilder.scaleAll(views);
anywheresoftware.b4a.keywords.LayoutBuilder.scaleView(views.get("lblspelduur"));
anywheresoftware.b4a.keywords.LayoutBuilder.scaleView(views.get("lbltafelnaam"));
anywheresoftware.b4a.keywords.LayoutBuilder.scaleView(views.get("imgnodata"));
//BA.debugLineNum = 9;BA.debugLine="AutoScale(lblNoData)"[ServerBoard/General script]
anywheresoftware.b4a.keywords.LayoutBuilder.scaleView(views.get("lblnodata"));
//BA.debugLineNum = 10;BA.debugLine="AutoScale(pnlMain)"[ServerBoard/General script]
anywheresoftware.b4a.keywords.LayoutBuilder.scaleView(views.get("pnlmain"));
//BA.debugLineNum = 11;BA.debugLine="AutoScale(imgP1Play)"[ServerBoard/General script]
anywheresoftware.b4a.keywords.LayoutBuilder.scaleView(views.get("imgp1play"));
//BA.debugLineNum = 12;BA.debugLine="AutoScale(imgP2Play)"[ServerBoard/General script]
anywheresoftware.b4a.keywords.LayoutBuilder.scaleView(views.get("imgp2play"));
//BA.debugLineNum = 13;BA.debugLine="AutoScale(lblP1Moy)"[ServerBoard/General script]
anywheresoftware.b4a.keywords.LayoutBuilder.scaleView(views.get("lblp1moy"));
//BA.debugLineNum = 14;BA.debugLine="AutoScale(lblP2Moy)"[ServerBoard/General script]
anywheresoftware.b4a.keywords.LayoutBuilder.scaleView(views.get("lblp2moy"));
//BA.debugLineNum = 15;BA.debugLine="AutoScale(lblMoyBg)"[ServerBoard/General script]
anywheresoftware.b4a.keywords.LayoutBuilder.scaleView(views.get("lblmoybg"));
//BA.debugLineNum = 17;BA.debugLine="AutoScale(imgSponsor)"[ServerBoard/General script]
anywheresoftware.b4a.keywords.LayoutBuilder.scaleView(views.get("imgsponsor"));
//BA.debugLineNum = 18;BA.debugLine="AutoScale(lblP1Maken)"[ServerBoard/General script]
anywheresoftware.b4a.keywords.LayoutBuilder.scaleView(views.get("lblp1maken"));
//BA.debugLineNum = 19;BA.debugLine="AutoScale(lblP1Carom)"[ServerBoard/General script]
anywheresoftware.b4a.keywords.LayoutBuilder.scaleView(views.get("lblp1carom"));
//BA.debugLineNum = 20;BA.debugLine="AutoScale(lblP1Name)"[ServerBoard/General script]
anywheresoftware.b4a.keywords.LayoutBuilder.scaleView(views.get("lblp1name"));
//BA.debugLineNum = 21;BA.debugLine="AutoScale(Panel2)"[ServerBoard/General script]
anywheresoftware.b4a.keywords.LayoutBuilder.scaleView(views.get("panel2"));
//BA.debugLineNum = 22;BA.debugLine="AutoScale(Panel1)"[ServerBoard/General script]
anywheresoftware.b4a.keywords.LayoutBuilder.scaleView(views.get("panel1"));
//BA.debugLineNum = 23;BA.debugLine="AutoScale(pn_p1_carom_ph)"[ServerBoard/General script]
anywheresoftware.b4a.keywords.LayoutBuilder.scaleView(views.get("pn_p1_carom_ph"));
//BA.debugLineNum = 24;BA.debugLine="AutoScale(Panel3)"[ServerBoard/General script]
anywheresoftware.b4a.keywords.LayoutBuilder.scaleView(views.get("panel3"));
//BA.debugLineNum = 25;BA.debugLine="AutoScale(lblP1Maken100)"[ServerBoard/General script]
anywheresoftware.b4a.keywords.LayoutBuilder.scaleView(views.get("lblp1maken100"));
//BA.debugLineNum = 26;BA.debugLine="AutoScale(lblP1Maken10)"[ServerBoard/General script]
anywheresoftware.b4a.keywords.LayoutBuilder.scaleView(views.get("lblp1maken10"));
//BA.debugLineNum = 27;BA.debugLine="AutoScale(lblP1Maken1)"[ServerBoard/General script]
anywheresoftware.b4a.keywords.LayoutBuilder.scaleView(views.get("lblp1maken1"));
//BA.debugLineNum = 28;BA.debugLine="AutoScale(lblP1100)"[ServerBoard/General script]
anywheresoftware.b4a.keywords.LayoutBuilder.scaleView(views.get("lblp1100"));
//BA.debugLineNum = 29;BA.debugLine="AutoScale(lblP110)"[ServerBoard/General script]
anywheresoftware.b4a.keywords.LayoutBuilder.scaleView(views.get("lblp110"));
//BA.debugLineNum = 30;BA.debugLine="AutoScale(lblP11)"[ServerBoard/General script]
anywheresoftware.b4a.keywords.LayoutBuilder.scaleView(views.get("lblp11"));
//BA.debugLineNum = 32;BA.debugLine="AutoScale(lblBeurten)"[ServerBoard/General script]
anywheresoftware.b4a.keywords.LayoutBuilder.scaleView(views.get("lblbeurten"));
//BA.debugLineNum = 33;BA.debugLine="AutoScale(lblBeurt100)"[ServerBoard/General script]
anywheresoftware.b4a.keywords.LayoutBuilder.scaleView(views.get("lblbeurt100"));
//BA.debugLineNum = 34;BA.debugLine="AutoScale(lblBeurt10)"[ServerBoard/General script]
anywheresoftware.b4a.keywords.LayoutBuilder.scaleView(views.get("lblbeurt10"));
//BA.debugLineNum = 35;BA.debugLine="AutoScale(lblBeurt1)"[ServerBoard/General script]
anywheresoftware.b4a.keywords.LayoutBuilder.scaleView(views.get("lblbeurt1"));
//BA.debugLineNum = 36;BA.debugLine="AutoScale(pnlBeurt)"[ServerBoard/General script]
anywheresoftware.b4a.keywords.LayoutBuilder.scaleView(views.get("pnlbeurt"));
//BA.debugLineNum = 38;BA.debugLine="AutoScale(lblP2Name)"[ServerBoard/General script]
anywheresoftware.b4a.keywords.LayoutBuilder.scaleView(views.get("lblp2name"));
//BA.debugLineNum = 39;BA.debugLine="AutoScale(lblP2Maken)"[ServerBoard/General script]
anywheresoftware.b4a.keywords.LayoutBuilder.scaleView(views.get("lblp2maken"));
//BA.debugLineNum = 40;BA.debugLine="AutoScale(lblP2Carom)"[ServerBoard/General script]
anywheresoftware.b4a.keywords.LayoutBuilder.scaleView(views.get("lblp2carom"));
//BA.debugLineNum = 41;BA.debugLine="AutoScale(lblP2Maken100)"[ServerBoard/General script]
anywheresoftware.b4a.keywords.LayoutBuilder.scaleView(views.get("lblp2maken100"));
//BA.debugLineNum = 42;BA.debugLine="AutoScale(lblP2Maken10)"[ServerBoard/General script]
anywheresoftware.b4a.keywords.LayoutBuilder.scaleView(views.get("lblp2maken10"));
//BA.debugLineNum = 43;BA.debugLine="AutoScale(lblP2Maken1)"[ServerBoard/General script]
anywheresoftware.b4a.keywords.LayoutBuilder.scaleView(views.get("lblp2maken1"));
//BA.debugLineNum = 44;BA.debugLine="AutoScale(lblP2100)"[ServerBoard/General script]
anywheresoftware.b4a.keywords.LayoutBuilder.scaleView(views.get("lblp2100"));
//BA.debugLineNum = 45;BA.debugLine="AutoScale(lblP210)"[ServerBoard/General script]
anywheresoftware.b4a.keywords.LayoutBuilder.scaleView(views.get("lblp210"));
//BA.debugLineNum = 46;BA.debugLine="AutoScale(lblP21)"[ServerBoard/General script]
anywheresoftware.b4a.keywords.LayoutBuilder.scaleView(views.get("lblp21"));

}
}