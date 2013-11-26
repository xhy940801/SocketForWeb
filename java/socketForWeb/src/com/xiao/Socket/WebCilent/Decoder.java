package com.xiao.Socket.WebCilent;

public interface Decoder
{
	public byte[] decode(byte[] bytes) throws AnalysisError;
}
