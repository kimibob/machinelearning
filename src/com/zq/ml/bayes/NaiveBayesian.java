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
    //垃圾邮件中每个词出现的概率  
    private List<Double> p1Vec = null;  
    //垃圾邮件出现的概率  
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
		//训练集
        List<Email> dataSet = initDataSet();  
        //测试集
        List<Email> testSet = new ArrayList<Email>();  
        //随机取前10作为测试样本  
        for (int i = 0; i < 10; i++) {  
            Random random = new Random();  
            int n = random.nextInt(50-i);  
            testSet.add(dataSet.get(n));  
            //从训练样本中删除这10条测试样本  
            dataSet.remove(n);  
        }  
        
        //根据测试集文本建立一个词汇表，把重复的词汇剔除
        Set<String> vocabSet = createVocabList(dataSet);  
        //训练样本  
        trainNB(vocabSet, dataSet);  
          
        int errorCount = 0;  
        for (Email email : testSet) {  
        	System.out.println("预测分类:"+classifyNB(setOfWords2Vec(vocabSet, email)) +"--> 真实分类:" +email.getFlag());
            if (classifyNB(setOfWords2Vec(vocabSet, email)) != email.getFlag()) {  
                ++errorCount;  
            }  
        }  
//      System.out.println("the error rate is: " + (double) errorCount / testSet.size());  
        return (double) errorCount / testSet.size();  
    }  
	
	/** 
     * 初始化数据集 
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
     * 分词，英文的分词相比中文的分词要简单很多，这里使用的分隔符为除单词、数字外的任意字符串 
     * 如果使用中文，则可以使用中科院的一套分词系统，分词效果还算不错 
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
     * 构建单词集，此长度等于向量长度 
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
     * 计算垃圾邮件的数量 
     * 垃圾邮件flag=1，正常邮件flag=0，所以累计值即为训练集中垃圾邮件的数量。
     */  
    private int calSpamNum(List<Email> dataSet) {  
        int time = 0;  
        for (Email email : dataSet) {  
            time += email.getFlag();  
        }  
        return time;  
    } 
    
    /** 
     * 将邮件转换为向量 
     */  
    public List<Integer> setOfWords2Vec(Set<String> vocabSet, Email email) {  
        List<Integer> returnVec = new ArrayList<Integer>();  
        for (String word : vocabSet) {  
            returnVec.add(calWordFreq(word, email));  
        }  
        return returnVec;  
    }  
    
    /** 
     * 计算一个词在某个集合中的出现次数 
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
     * 两个向量相加 
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
     * 统计出现的所有单词数 
     */  
    private int calTotalWordNum(List<Integer> list) {  
        int num = 0;  
        for (Integer integer : list) {  
            num += integer;  
        }  
        return num;  
    }  
    
    public void trainNB(Set<String> vocabSet, List<Email> dataSet) {  
        // 训练文本的数量  
        int numTrainDocs = dataSet.size();  
        // 训练集中垃圾邮件的概率  
        pSpamRatio = (double) calSpamNum(dataSet) / numTrainDocs;  
        //System.out.println("total emails:"+numTrainDocs+" spam emails:"+calSpamNum(dataSet));
        // 记录每个类别下每个词的出现次数  
        List<Integer> p0Num = new ArrayList<Integer>();  
        List<Integer> p1Num = new ArrayList<Integer>();  
        // 记录每个类别下一共出现了多少词,为防止分母为0，所以在此默认值为2  
        double p0Denom = 2.0, p1Denom = 2.0;  
        for (Email email : dataSet) {  
            List<Integer> list = setOfWords2Vec(vocabSet, email);  
            // 如果是垃圾邮件  
            if (email.getFlag() == 1) {  
                p1Num = vecAddVec(p1Num, list);  
                //计算该类别下出现的所有单词数目  
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
     * 计算每个单词在该类别下的出现概率，为防止分子为0，导致朴素贝叶斯公式为0，设置分子的默认值为1 
     */  
    private List<Double> calWordRatio(List<Integer> list, double wordNum) {  
        List<Double> vec = new ArrayList<Double>();  
        for (Integer i : list) {  
            vec.add(Math.log((double)(i+1) / wordNum));  
        }  
        return vec;  
    }  
    
    /** 
     * 比较不同类别 p(w0,w1,w2...wn | ci)*p(ci) 的大小   <br> 
     *  p(w0,w1,w2...wn | ci) = p(w0|ci)*p(w1|ci)*p(w2|ci)... <br> 
     *  由于防止下溢，对中间计算值都取了对数，因此上述公式化为log(p(w0,w1,w2...wn | ci)) + log(p(ci)),即 
     *  化为多个式子相加得到结果 
     * @return 返回概率最大值  
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
