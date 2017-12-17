package com.zq.ml.classification;

import java.util.ArrayList;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils.DataSource;

public class LoadTrainModel {
	public static void main(String[] args) throws Exception {
		
		Classifier decisionTreeModel = (Classifier)SerializationHelper.read("dataset/classify/decision-tree.model");
		
		ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("outlook"));
        attributes.add(new Attribute("temp"));
        attributes.add(new Attribute("humidity"));
        attributes.add(new Attribute("windy"));
        attributes.add(new Attribute("play"));
        DataSource source = new DataSource("dataset/classify/tennis2.arff");
        Instances data = source.getDataSet();
        
        //set instances
//        Instances data = new Instances("repo_popular",attributes,0);
        data.setClassIndex(data.numAttributes() - 1);
        
		Instance testInstance = new DenseInstance(attributes.size());
		//…Ë÷√ Ù–‘
		testInstance.setValue(data.attribute("outlook"), "overcast");
		testInstance.setValue(data.attribute("temp"), "hot");
		testInstance.setValue(data.attribute("humidity"), "high");
		testInstance.setValue(data.attribute("windy"), "strong");
		testInstance.setDataset(data);

		double label = decisionTreeModel.classifyInstance(testInstance);
		System.out.println(data.classAttribute().value((int) label));
	}
}
