package com.zq.ml.classification;

import java.util.Random;

import javax.swing.JFrame;

import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.Utils;
import weka.core.converters.ConverterUtils.DataSource;
import weka.gui.treevisualizer.PlaceNode2;
import weka.gui.treevisualizer.TreeVisualizer;

public class TennisClassificationTask {
	public static void main(String[] args) throws Exception {

		DataSource source = new DataSource("dataset/classify/tennis.arff");
		Instances data = source.getDataSet();
		//设置类别所在的列(一般为最后一列)
		data.setClassIndex(data.numAttributes() - 1);
		System.out.println(data.numInstances() + " instances loaded." +" class num:"+data.numClasses()+". attr num:"+data.numAttributes());
		// System.out.println(data.toString());

		/*
		 * 特征选择
		 */
		AttributeSelection attSelect = new AttributeSelection();
		InfoGainAttributeEval eval = new InfoGainAttributeEval();
		Ranker search = new Ranker();
		attSelect.setEvaluator(eval);
		attSelect.setSearch(search);
		attSelect.SelectAttributes(data);
		int[] indices = attSelect.selectedAttributes();
		System.out.println("Selected attributes: " + Utils.arrayToString(indices));

		/*
		 * 创建决策树
		 */
		String[] options = new String[1];
		options[0] = "-U";
		J48 tree = new J48();
		tree.setOptions(options);
		tree.buildClassifier(data);
		System.out.println(tree);
		
		TreeVisualizer tv = new TreeVisualizer(null, tree.graph(), new PlaceNode2());
		JFrame frame = new javax.swing.JFrame("Tree Visualizer");
		frame.setSize(800, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(tv);
		frame.setVisible(true);
		tv.fitToScreen();

		Instance testInstance = new DenseInstance(data.numAttributes()-1);
		//设置属性
		testInstance.setValue(data.attribute("outlook"), "overcast");
		testInstance.setValue(data.attribute("temp"), "hot");
		testInstance.setValue(data.attribute("humidity"), "high");
		testInstance.setValue(data.attribute("windy"), "strong");
		//关联实例数据
		testInstance.setDataset(data);
		
		double label = tree.classifyInstance(testInstance);
		System.out.println(data.classAttribute().value((int) label));
		
		SerializationHelper.write("dataset/classify/decision-tree.model", tree);//训练模型保存文件,下次直接调用

		Classifier cl = new J48();
		Evaluation eval_roc = new Evaluation(data);
		eval_roc.crossValidateModel(cl, data, 10, new Random(1), new Object[] {});
		System.out.println(eval_roc.toSummaryString());
		// Confusion matrix
		double[][] confusionMatrix = eval_roc.confusionMatrix();
		System.out.println(eval_roc.toMatrixString());
		/*
		ThresholdCurve tc = new ThresholdCurve();
		int classIndex = 0;
		Instances result = tc.getCurve(eval_roc.predictions(), classIndex);
		// plot curve
		ThresholdVisualizePanel vmc = new ThresholdVisualizePanel();
		vmc.setROCString("(Area under ROC = " + tc.getROCArea(result) + ")");
		vmc.setName(result.relationName());
		PlotData2D tempd = new PlotData2D(result);
		tempd.setPlotName(result.relationName());
		tempd.addInstanceNumberAttribute();
		// specify which points are connected
		boolean[] cp = new boolean[result.numInstances()];
		for (int n = 1; n < cp.length; n++)
			cp[n] = true;
		tempd.setConnectPoints(cp);

		// add plot
		vmc.addPlot(tempd);
		// display curve
		JFrame frameRoc = new javax.swing.JFrame("ROC Curve");
		frameRoc.setSize(800, 500);
		frameRoc.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frameRoc.getContentPane().add(vmc);
		frameRoc.setVisible(true);
*/
	}
}
