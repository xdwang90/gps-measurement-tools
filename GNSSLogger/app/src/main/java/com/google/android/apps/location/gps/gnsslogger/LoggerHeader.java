package com.google.android.apps.location.gps.gnsslogger;
import java.io.BufferedReader;
import java.io.IOException;

public class LoggerHeader {
	//Read the Loggerfile header
	public LoggerHeader( BufferedReader IFile ) throws IOException{
		String line;
		for (int i=0;i<11;i++)
		{
			line = IFile.readLine();
		}
	}
}
