package com.speedata.libid2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.fastjson.JSON;

import android.serialport.DeviceControl;
import android.serialport.SerialPort;
import android.util.Log;

public class Identity extends CordovaPlugin {

	private final String LOG_TAG = "Identity";
	private CallbackContext callbackContext;
	private static final String CANCELLED = "cancelled";
	IID2Service iid2Service = IDManager.getInstance();

	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);
	}

	@Override
	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContextID) throws JSONException {
		this.callbackContext = callbackContextID;
		if (action.equals("readIdentity")) {
			// scan();
			iid2Service.getIDInfor(false);
		} else if (action.equals("releaseIdentityDev")) {
			try {
				iid2Service.releaseDev();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (action.equals("initIdentityDev")) {
			initID(args);
		}
		PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
		result.setKeepCallback(true);
		this.callbackContext.sendPluginResult(result);
		Log.d(LOG_TAG, "----setKeepCallback---");
		return true;
	}

	private void callback(final PluginResult.Status state,
			final boolean isKeepCallback, final JSONObject data) {
		PluginResult result;
		if (data != null) {
			result = new PluginResult(state, data);
		} else {
			result = new PluginResult(state);
		}
		result.setKeepCallback(isKeepCallback);
		callbackContext.sendPluginResult(result);
		Log.d(LOG_TAG,
				"callbackContext.isFinished():" + callbackContext.isFinished());
	}

	private void callErrorback(String error) {
		PluginResult result;
		result = new PluginResult(PluginResult.Status.ERROR, error);
		result.setKeepCallback(false);
		this.callbackContext.sendPluginResult(result);
	}

	// 构造参数 createScanParms
	private JSONObject createScanParms(List<String> tagList)
			throws JSONException {
		JSONObject obj = new JSONObject();
		JSONArray jsArray = new JSONArray(tagList);
		obj.put(CANCELLED, false);
		obj.put("tags", jsArray);
		return obj;
	}

	public void scan() {

	}

	private void initID(JSONArray args) {
		iid2Service = IDManager.getInstance();
		DeviceControl.PowerType powerType = DeviceControl.PowerType.MAIN;
		try {
			String serial = SerialPort.SERIAL_TTYMT1;
			int gpio = 94;
			int type = 0;
			try {
				serial = args.get(0).toString();
				type = args.getInt(1);
				gpio = args.getInt(2);
			} catch (JSONException e1) {
				e1.printStackTrace();
				callErrorback("模块初始化参数错误" + e1.getMessage());
			}
			switch (type) {
			case 0:
				powerType = DeviceControl.PowerType.MAIN;
				break;
			case 1:
				powerType = DeviceControl.PowerType.EXPAND;
				break;
			case 2:
				powerType = DeviceControl.PowerType.MAIN_AND_EXPAND;
				break;

			default:
				break;
			}
			boolean result = iid2Service.initDev(cordova.getActivity(),
					new IDReadCallBack() {
						@Override
						public void callBack(IDInfor infor) {
							// TODO 返回
							ArrayList<String> result = new ArrayList<String>();
							JSONObject obj;
							try {
								String json = JSON.toJSONString(infor);// 将java对象转换为json对象
								result.add("jsonString" + json);
								obj = createScanParms(result);
								callback(PluginResult.Status.OK, true, obj);
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}, serial, 115200, powerType
					// , 88, 6);
					, gpio);
			// }, SerialPort.SERIAL_TTYMT1, 115200,
			// DeviceControl.PowerType.MAIN
			// // , 88, 6);
			// , 94);
			if (!result) {
				// 模块初始化失败
				callErrorback("模块初始化失败");
			} else {
				callback(PluginResult.Status.OK, true, null);
			}
		} catch (IOException e) {
			e.printStackTrace();
			callErrorback("模块初始化失败" + e.getMessage());
		}
	}
}
