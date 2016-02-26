package model

import java.net.URI

import com.datumbox.applications.nlp.TextClassifier
import com.datumbox.common.dataobjects.Record
import com.datumbox.common.persistentstorage.ConfigurationFactory
import com.datumbox.common.utilities.RandomGenerator
import com.datumbox.framework.machinelearning.classification.MultinomialNaiveBayes
import com.datumbox.framework.machinelearning.common.bases.mlmodels.BaseMLmodel
import com.datumbox.framework.machinelearning.featureselection.categorical.ChisquareSelect
import com.datumbox.framework.utilities.text.extractors.NgramsExtractor

import scala.collection.JavaConverters._
import scala.collection.immutable.HashMap

/**
  * Created by Jake on 16/02/2016.
  */
class SentimentClassifer(tweets: List[Tweet]) {

  def getSentimentOfTweets(): Unit = {
    val seed = 42L
    RandomGenerator.setGlobalSeed(seed.asInstanceOf[java.lang.Long])

    val databaseConf = ConfigurationFactory.INMEMORY.getConfiguration

    val dataset = new HashMap[Object, URI]()
    val positive = getClass.getResource("/resources\\polarity\\positive_training_set.txt").toURI
    val negative = getClass.getResource("/resources\\polarity\\negative_training_set.txt").toURI
    val neutral = getClass.getResource("/resources\\polarity\\neutral_training_set.txt").toURI

    dataset.+("positive" -> positive)
    dataset.+("negative" -> negative)
    dataset.+("neutral" -> neutral)

    val trainingParameters: TextClassifier.TrainingParameters = new TextClassifier.TrainingParameters
    trainingParameters.setMLmodelClass(classOf[MultinomialNaiveBayes])
    trainingParameters.setMLmodelTrainingParameters(new MultinomialNaiveBayes.TrainingParameters)

    trainingParameters.setDataTransformerClass(null)
    trainingParameters.setDataTransformerTrainingParameters(null)

    trainingParameters.setFeatureSelectionClass(classOf[ChisquareSelect])
    trainingParameters.setFeatureSelectionTrainingParameters(new ChisquareSelect.TrainingParameters)

    trainingParameters.setTextExtractorClass(classOf[NgramsExtractor])
    trainingParameters.setTextExtractorParameters(new NgramsExtractor.Parameters)

    val textClassifier = new TextClassifier("SentimentAnalysis", databaseConf)
    textClassifier.fit(dataset.asJava, trainingParameters)

    val validationMetrics: BaseMLmodel.ValidationMetrics = textClassifier.validate(dataset.asJava)
    textClassifier.setValidationMetrics(validationMetrics)

    println("!!! " + tweets.size)

    for (tweet <- tweets) {
      println(TweetCleaner.cleanText(tweet.text))
      val record: Record = textClassifier.predict(TweetCleaner.cleanText(tweet.text))
      printf("Tweet = %s\n", tweet.text)
      printf("Sentiment = %s\n", record.getYPredicted)
      println()
    }

    textClassifier.erase()
  }

  /*def setupTrainingParameters(): TextClassifier.TrainingParameters = {
    val trainingParameters: TextClassifier.TrainingParameters = new TextClassifier.TrainingParameters
    trainingParameters.setMLmodelClass(classOf[MultinomialNaiveBayes])
    trainingParameters.setMLmodelTrainingParameters(new MultinomialNaiveBayes.TrainingParameters)

    trainingParameters.setDataTransformerClass(null)
    trainingParameters.setDataTransformerTrainingParameters(null)

    trainingParameters.setFeatureSelectionClass(classOf[ChisquareSelect])
    trainingParameters.setFeatureSelectionTrainingParameters(new ChisquareSelect.TrainingParameters)

    trainingParameters.setTextExtractorClass(classOf[NgramsExtractor])
    trainingParameters.setTextExtractorParameters(new NgramsExtractor.Parameters)

    trainingParameters
  }

  def setupDataSets(): HashMap[Object, URI] = {
    val dataset = new HashMap[Object, URI]()
    try {
      val positive = getClass.getResource("/resources\\polarity\\positive_training_set.txt").toURI
      val negative = getClass.getResource("/resources\\polarity\\negative_training_set.txt").toURI
      val neutral = getClass.getResource("/resources\\polarity\\neutral_training_set.txt").toURI

      dataset.+("positive" -> positive)
      dataset.+("negative" -> negative)
      dataset.+("neutral" -> neutral)
    } catch {
      case e: Exception => {
        println("!!! RATS !!!")
        e.printStackTrace()
      }
    }
    dataset
  }*/
}


