package com.zq.ml.bayes;

import java.util.List;

public class Email {
	//�ִʼ���
	private List<String> wordList;
	//�Ƿ������ʼ�
	private int flag;

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public List<String> getWordList() {
		return wordList;
	}

	public void setWordList(List<String> wordList) {
		this.wordList = wordList;
	}
}
