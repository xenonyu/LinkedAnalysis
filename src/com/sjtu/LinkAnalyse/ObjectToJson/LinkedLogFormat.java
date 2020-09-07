package com.sjtu.LinkAnalyse.ObjectToJson;

public class LinkedLogFormat {
	private String proName;
    private LogFormat logFormat;
    private LinkedLogFormat pre = null;
    private LinkedLogFormat nex = null;

    public LinkedLogFormat(LogFormat logFormat) {
        this.logFormat = logFormat;
    }
    public LinkedLogFormat() {
    }

    public LinkedLogFormat getPre() {
        return pre;
    }

    public void setPre(LinkedLogFormat pre) {
        this.pre = pre;
    }

    public LinkedLogFormat getNex() {
        return nex;
    }

    public void setNex(LinkedLogFormat nex) {
        this.nex = nex;
    }
	public LogFormat getLogFormat() {
		return logFormat;
	}
	public void setLogFormat(LogFormat logFormat) {
		this.logFormat = logFormat;
	}
    
}
