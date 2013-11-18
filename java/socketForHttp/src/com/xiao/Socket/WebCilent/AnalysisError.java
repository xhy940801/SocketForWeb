package com.xiao.Socket.WebCilent;

public class AnalysisError extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int errorType = 0;

	public AnalysisError()
	{
		// TODO Auto-generated constructor stub
	}

	public AnalysisError(String message)
	{
		super(message);
		// TODO Auto-generated constructor stub
	}

	public AnalysisError(Throwable cause)
	{
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public AnalysisError(String message, Throwable cause)
	{
		super(message, cause);
		// TODO Auto-generated constructor stub
	}
	
	public AnalysisError(String message, int err)
	{
		super(message);
		this.errorType = err;
	}
	
	public void setErrorType(int err)
	{
		this.errorType = err;
	}
	
	public int getErrorType()
	{
		return this.errorType;
	}
}
