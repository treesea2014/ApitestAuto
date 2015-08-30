package com.gxb.server.api.beans;

public class InputData {
	//测试ID
	public String ID;
	//测试名称
	public String testCase;
	//接口简要描述
	public String description 	;
	//请求地址host
	public String host;
	//请求尾缀
	public String callSuff;
	//请求类型
	public String callType;
	//authScheme
	public String authScheme;
	//AuthCreds
	public String authCreds;
	//
	public String accept;
	public String contentType;
	public String body;
	public String ActualResponse;
	public String ExpectResponse;
	public String log;
	public String IsPass;
	
	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getTestCase() {
		return testCase;
	}

	public void setTestCase(String testCase) {
		this.testCase = testCase;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getCallSuff() {
		return callSuff;
	}

	public void setCallSuff(String callSuff) {
		this.callSuff = callSuff;
	}

	public String getCallType() {
		return callType;
	}

	public void setCallType(String callType) {
		this.callType = callType;
	}

	public String getAuthScheme() {
		return authScheme;
	}

	public void setAuthScheme(String authScheme) {
		this.authScheme = authScheme;
	}

	public String getAuthCreds() {
		return authCreds;
	}

	public void setAuthCreds(String authCreds) {
		this.authCreds = authCreds;
	}

	public String getAccept() {
		return accept;
	}

	public void setAccept(String accept) {
		this.accept = accept;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getActualResponse() {
		return ActualResponse;
	}

	public void setActualResponse(String actualResponse) {
		ActualResponse = actualResponse;
	}

	public String getExpectResponse() {
		return ExpectResponse;
	}

	public void setExpectResponse(String expectResponse) {
		ExpectResponse = expectResponse;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}

	public String getIsPass() {
		return IsPass;
	}

	public void setIsPass(String isPass) {
		IsPass = isPass;
	}

	@Override
	public String toString() {
		return "InputData [ID=" + ID + ", testCase=" + testCase
				+ ", description=" + description + ", host=" + host
				+ ", callSuff=" + callSuff + ", callType=" + callType
				+ ", authScheme=" + authScheme + ", authCreds=" + authCreds
				+ ", accept=" + accept + ", contentType=" + contentType
				+ ", body=" + body + ", ActualResponse=" + ActualResponse
				+ ", ExpectResponse=" + ExpectResponse + ", log=" + log
				+ ", IsPass=" + IsPass + "]";
	}
	

		

}
