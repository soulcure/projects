package com.mykj.comm.util;

public class Version
{
	public int majorVer = 0;
	public int minorVer = 0;
	public int revisionVer = 0;
	
	private Exception e = new Exception("Version Class Error");
	
	public Version(String strVer) throws Exception
	{
		if(strVer == null)
		{
			throw e;
		}
		if(strVer.length() != 5)
		{
			throw e;
		}
		String[] s = strVer.split("\\.");
		if(s.length != 3)
		{
			throw e;
		}
		
		this.majorVer = Integer.parseInt(s[0]);
		this.minorVer = Integer.parseInt(s[1]);
		this.revisionVer = Integer.parseInt(s[2]);
	}
	
	public boolean isLess(Version ver)
	{
		if(ver != null)
		{
			if(this.majorVer < ver.majorVer)
			{
				return true;
			}
			else if(this.majorVer > ver.majorVer)
			{
				return false;
			}
			if(this.minorVer < ver.minorVer)
			{
				return true;
			}
			else if(this.minorVer > ver.minorVer)
			{
				return false;
			}
			if(this.revisionVer < ver.revisionVer)
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean isEqual(Version ver)
	{
		if(ver != null)
		{
			if(this.majorVer == ver.majorVer && this.minorVer == ver.minorVer && this.revisionVer == ver.revisionVer)
			{
				return true;
			}
		}
		return false;
	}
}
