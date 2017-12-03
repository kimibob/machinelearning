package com.zq.ml.bayes;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class NaiveBayesian {

	private List<Double> p0Vec = null;  
    //�����ʼ���ÿ���ʳ��ֵĸ���  
    private List<Double> p1Vec = null;  
    //�����ʼ����ֵĸ���  
    private double pSpamRatio;  
	
    public static void main(String[] args) {  
        NaiveBayesian bayesian = new NaiveBayesian();  
        double d = 0;  
        for (int i = 0; i < 1; i++) {  
            d +=bayesian.startNB();  
        }  
        System.out.println("total error rate is: " + d / 50);  
    } 
    
	public double startNB() {
		//ѵ����
        List<Email> dataSet = initDataSet();  
        //���Լ�
        List<Email> testSet = new ArrayList<Email>();  
        //���ȡǰ10��Ϊ��������  
        for (int i = 0; i < 10; i++) {  
            Random random = new Random();  
            int n = random.nextInt(50-i);  
            testSet.add(dataSet.get(n));  
            //��ѵ��������ɾ����10����������  
            dataSet.remove(n);  
        }  
        
        //���ݲ��Լ��ı�����һ���ʻ�����ظ��Ĵʻ��޳�
        Set<String> vocabSet = createVocabList(dataSet);  
        //ѵ������  
        trainNB(vocabSet, dataSet);  
          
        int errorCount = 0;  
        for (Email email : testSet) {  
        	System.out.println("Ԥ�����:"+classifyNB(setOfWords2Vec(vocabSet, email)) +"--> ��ʵ����:" +email.getFlag());
            if (classifyNB(setOfWords2Vec(vocabSet, email)) != email.getFlag()) {  
                ++errorCount;  
            }  
        }  
//      System.out.println("the error rate is: " + (double) errorCount / testSet.size());  
        return (double) errorCount / testSet.size();  
    }  
	
	/** 
     * ��ʼ�����ݼ� 
     */  
	public List<Email> initDataSet() {
        List<Email> dataSet = new ArrayList<Email>();  
        BufferedReader bufferedReader1 = null;  
        BufferedReader bufferedReader2 = null;  
        try {  
            for (int i = 1; i < 26; i++) {  
                bufferedReader1 = new BufferedReader(new InputStreamReader(  
                        new FileInputStream(  
                                "D:\\DevelopTools\\workspace\\machinelearning\\dataset\\email\\ham\\"+ i + ".txt")));  
                StringBuilder sb1 = new StringBuilder();  
                String string = null;  
                while ((string = bufferedReader1.readLine()) != null) {  
                    sb1.append(string);  
                }  
                Email hamEmail = new Email();  
                hamEmail.setWordList(textParse(sb1.toString()));  
                hamEmail.setFlag(0);  
  
                bufferedReader2 = new BufferedReader(new InputStreamReader(  
                        new FileInputStream(  
                                "D:\\DevelopTools\\workspace\\machinelearning\\dataset\\email\\spam\\" + i + ".txt")));  
                StringBuilder sb2 = new StringBuilder();  
                while ((string = bufferedReader2.readLine()) != null) {  
                    sb2.append(string);  
                }  
                Email spamEmail = new Email();  
                spamEmail.setWordList(textParse(sb2.toString()));  
                spamEmail.setFlag(1);  
  
                dataSet.add(hamEmail);  
                dataSet.add(spamEmail);  
            }  
            return dataSet;  
        } catch (Exception e) {  
            e.printStackTrace();  
            throw new RuntimeException();  
        } finally {  
            try {  
                bufferedReader1.close();  
                bufferedReader2.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
    }
	
	/** 
     * �ִʣ�Ӣ�ĵķִ�������ĵķִ�Ҫ�򵥺ܶ࣬����ʹ�õķָ���Ϊ�����ʡ�������������ַ��� 
     * ���ʹ�����ģ������ʹ���п�Ժ��һ�׷ִ�ϵͳ���ִ�Ч�����㲻�� 
     *  
     */  
    private List<String> textParse(String originalString) {  
        String[] s = originalString.split("\\W");  
        List<String> wordList = new ArrayList<String>();  
        for (String string : s) {  
            if (string.contains(" ")) {  
                continue;  
            }  
            if (string.length() > 2) {  
                wordList.add(string.toLowerCase());  
            }  
        }  
        return wordList;  
    }  
	
    /** 
     * �������ʼ����˳��ȵ����������� 
     */  
    public Set<String> createVocabList(List<Email> dataSet) {  
        Set<String> set = new LinkedHashSet<String>();  
        for (Email email : dataSet) {
            for (String string : email.getWordList()) {
                set.add(string);
            }
        }
//        int i =0 ;
//        for (String string : set) {
//			System.out.println(i++ +":"+string);
//		}
        return set;  
    }  
	
    /** 
     * ���������ʼ������� 
     * �����ʼ�flag=1�������ʼ�flag=0�������ۼ�ֵ��Ϊѵ�����������ʼ���������
     */  
    private int calSpamNum(List<Email> dataSet) {  
        int time = 0;  
        for (Email email : dataSet) {  
            time += email.getFlag();  
        }  
        return time;  
    } 
    
    /** 
     * ���ʼ�ת��Ϊ���� 
     */  
    public List<Integer> setOfWords2Vec(Set<String> vocabSet, Email email) {  
        List<Integer> returnVec = new ArrayList<Integer>();  
        for (String word : vocabSet) {  
            returnVec.add(calWordFreq(word, email));  
        }  
        return returnVec;  
    }  
    
    /** 
     * ����һ������ĳ�������еĳ��ִ��� 
     */  
    private int calWordFreq(String word, Email email) {  
        int num = 0;  
        for (String string : email.getWordList()) {  
            if (string.equals(word)) {  
                ++num;  
            }  
        }  
        return num;  
    }  
    
    /** 
     * ����������� 
     */  
    private List<Integer> vecAddVec(List<Integer> vec1,  
            List<Integer> vec2) {  
        if (vec1.size() == 0) {  
            return vec2;  
        }  
        List<Integer> list = new ArrayList<Integer>();  
        for (int i = 0; i < vec1.size(); i++) {  
            list.add(vec1.get(i) + vec2.get(i));  
        }  
        return list;  
    }  
    
    /** 
     * ͳ�Ƴ��ֵ����е����� 
     */  
    private int calTotalWordNum(List<Integer> list) {  
        int num = 0;  
        for (Integer integer : list) {  
            num += integer;  
        }  
        return num;  
    }  
    
    public void trainNB(Set<String> vocabSet, List<Email> dataSet) {  
        // ѵ���ı�������  
        int numTrainDocs = dataSet.size();  
        // ѵ�����������ʼ��ĸ���  
        pSpamRatio = (double) calSpamNum(dataSet) / numTrainDocs;  
        //System.out.println("total emails:"+numTrainDocs+" spam emails:"+calSpamNum(dataSet));
        // ��¼ÿ�������ÿ���ʵĳ��ִ���  
        List<Integer> p0Num = new ArrayList<Integer>();  
        List<Integer> p1Num = new ArrayList<Integer>();  
        // ��¼ÿ�������һ�������˶��ٴ�,Ϊ��ֹ��ĸΪ0�������ڴ�Ĭ��ֵΪ2  
        double p0Denom = 2.0, p1Denom = 2.0;  
        for (Email email : dataSet) {  
            List<Integer> list = setOfWords2Vec(vocabSet, email);  
            // ����������ʼ�  
            if (email.getFlag() == 1) {  
                p1Num = vecAddVec(p1Num, list);  
                //���������³��ֵ����е�����Ŀ  
                p1Denom += calTotalWordNum(list);  
            }else {  
                p0Num = vecAddVec(p0Num, list);  
                p0Denom += calTotalWordNum(list);  
            }  
        }  
        p0Vec = calWordRatio(p0Num, p0Denom);  
        p1Vec = calWordRatio(p1Num, p1Denom);  
    }  
    
    /** 
     * ����ÿ�������ڸ�����µĳ��ָ��ʣ�Ϊ��ֹ����Ϊ0���������ر�Ҷ˹��ʽΪ0�����÷��ӵ�Ĭ��ֵΪ1 
     */  
    private List<Double> calWordRatio(List<Integer> list, double wordNum) {  
        List<Double> vec = new ArrayList<Double>();  
        for (Integer i : list) {  
            vec.add(Math.log((double)(i+1) / wordNum));  
        }  
        return vec;  
    }  
    
    /** 
     * �Ƚϲ�ͬ��� p(w0,w1,w2...wn | ci)*p(ci) �Ĵ�С   <br> 
     *  p(w0,w1,w2...wn | ci) = p(w0|ci)*p(w1|ci)*p(w2|ci)... <br> 
     *  ���ڷ�ֹ���磬���м����ֵ��ȡ�˶��������������ʽ��Ϊlog(p(w0,w1,w2...wn | ci)) + log(p(ci)),�� 
     *  ��Ϊ���ʽ����ӵõ���� 
     * @return ���ظ������ֵ  
     */  
    public int classifyNB(List<Integer> emailVec) {  
        double p0 = calProbabilityByClass(p0Vec, emailVec) + Math.log(1 - pSpamRatio);  
        double p1 = calProbabilityByClass(p1Vec, emailVec) + Math.log(pSpamRatio);  
        if (p0 > p1) {  
            return 0;  
        }else {  
            return 1;  
        }  
    }  
    
    private double calProbabilityByClass(List<Double> vec,List<Integer> emailVec) {  
        double sum = 0.0;  
        for (int i = 0; i < vec.size(); i++) {  
            sum += (vec.get(i) * emailVec.get(i));  
        }  
        return sum;  
    }  
    
}
