package com.jhuster.videoserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import android.util.Log;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public class VideoServer extends NanoHTTPD {

	public static final int DEFAULT_SERVER_PORT = 8080;
	public static final String TAG = VideoServer.class.getSimpleName();

	private String mVideoFilePath;

	public VideoServer(String filepath, int width, int height, int port) {
		super(DEFAULT_SERVER_PORT);
		mVideoFilePath = filepath;
	}

	@Override
	public Response serve(IHTTPSession session) {
		Log.d(TAG, "OnRequest: " + session.getUri());
		return responseVideoStream(session);
	}

	public Response responseVideoStream(IHTTPSession session) {
		StringBuffer path = new StringBuffer("/storage/sdcard0/Android/data/com.softtanck.hlsdownload/cache/");
		if (session.getUri().contains(".m3u8")) {
			path.append("prog_index.m3u8");
		} else if (session.getUri().contains("1")) {
			path.append("fileSequence1.ts");
		} else if (session.getUri().contains("2")) {
			path.append("fileSequence2.ts");
		} else {
			path.append("fileSequence0.ts");
		}
		try {
			File file = new File(path.toString());
			FileInputStream fis = new FileInputStream(file);
			return new NanoHTTPD.Response(Status.OK, "audio/x-mpegurl", fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return response404(session, mVideoFilePath);
		}
	}

	public Response response404(IHTTPSession session, String url) {
		StringBuilder builder = new StringBuilder();
		builder.append("<!DOCTYPE html><html><body>");
		builder.append("Sorry, Can't Found " + url + " !");
		builder.append("</body></html>\n");
		return new Response(builder.toString() + "fuck");
	}

	protected String getQuotaStr(String text) {
		return "\"" + text + "\"";
	}

}
