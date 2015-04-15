package com.elex.mahout;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.common.RandomUtils;

public class RecommenderIntro {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
//		.class.getClassLoader().getResourceAsStream("conf.properties")
		
		DataModel model = new FileDataModel(new File("E:/intro.csv"));
		userCF(model);
		itemCF(model);
		
	}
	public static void recommenderEvaluator() throws Exception{
		RandomUtils.useTestSeed();
		DataModel model = new FileDataModel(new File(""));
		RecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();
	}
	
	public static void userCF(DataModel model) throws TasteException{
//		UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
		UserSimilarity similarity = new EuclideanDistanceSimilarity(model);
//		UserNeighborhood neighborhood = new NearestNUserNeighborhood(3, similarity, model);
		ThresholdUserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);
		Recommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
		Iterator iter = model.getUserIDs();
		while(iter.hasNext()){
			List<RecommendedItem> recommendations = recommender.recommend((Long)iter.next(), 3);
			System.out.println(recommendations.size() + "\t" + "user");
			for(RecommendedItem re: recommendations){
				System.out.println(re.getItemID() + "\t" + re.getValue());
			}
		}
	}
	public static void itemCF(DataModel model) throws TasteException{
//		ItemSimilarity similarity = new PearsonCorrelationSimilarity(model);
		ItemSimilarity similarity = new EuclideanDistanceSimilarity(model);
		Recommender recommender = new GenericItemBasedRecommender(model, similarity);
		Iterator iter = model.getUserIDs();
		while(iter.hasNext()){
			List<RecommendedItem> recommendations = recommender.recommend((Long)iter.next(), 3);
			System.out.println(recommendations.size() + "\t" +"item");
			for(RecommendedItem re: recommendations){
				System.out.println(re.getItemID() + "\t" + re.getValue());
			} 
		}
	}
}
