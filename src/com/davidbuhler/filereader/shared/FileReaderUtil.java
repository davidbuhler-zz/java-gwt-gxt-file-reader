package com.davidbuhler.filereader.shared;

public class FileReaderUtil
{
	public static boolean IsNullOrEmptyString(final String string)
	{
		return string == null || string.isEmpty() || string.trim().isEmpty();
	}
}
