package com.sweetlilmre.jarverificationspoofer; 
 

import java.security.cert.Certificate;
import java.util.Hashtable;

import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.XSharedPreferences;

public class JarVerificationSpoofer implements IXposedHookZygoteInit {
	private XSharedPreferences _prefs;
	  	
	@Override
	public void initZygote(StartupParam startupParam) throws Throwable {
	    this._prefs = new XSharedPreferences(JarVerificationSpoofer.class.getPackage().getName());

		final Class<?> verifierEntry = XposedHelpers.findClass("java.util.jar.JarVerifier$VerifierEntry", JarVerificationSpoofer.class.getClassLoader());
		
		XposedHelpers.findAndHookMethod(verifierEntry, "verify", new XC_MethodHook () {
			@SuppressWarnings("unchecked")
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				JarVerificationSpoofer.this._prefs.makeWorldReadable();
				JarVerificationSpoofer.this._prefs.reload();
				
				if (JarVerificationSpoofer.this._prefs.getBoolean("enable_spoof", true)) {
					XposedBridge.log("JAR verification spoof enabled");
					String name = (String) XposedHelpers.getObjectField(param.thisObject, "name");
					Certificate[] certificates = (Certificate[]) XposedHelpers.getObjectField(param.thisObject, "certificates");
					Hashtable<String, Certificate[]> verifiedEntries = null;
					
					try {
						verifiedEntries = (Hashtable<String, Certificate[]>) XposedHelpers.findField(param.thisObject.getClass(), "verifiedEntries").get(param.thisObject);
					} catch (NoSuchFieldError e) {
						XposedBridge.log("'verifiedEntries' not found in VerifierEntry, searching in surrounding class");
						verifiedEntries = (Hashtable<String, Certificate[]>) XposedHelpers.getObjectField(XposedHelpers.getSurroundingThis(param.thisObject), "verifiedEntries");
					}
					
					verifiedEntries.put(name, certificates);
					param.setResult(null);
				} else {
					XposedBridge.log("JAR verification spoof disabled");
				}
			}
		});		
		
	}
}