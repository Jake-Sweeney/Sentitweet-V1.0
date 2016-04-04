import model.{Sentiment, SentimentClassiferModel}
import org.scalatest.{Matchers, FunSpec}

/**
  * Created by Jake on 02/04/2016.
  */
class SentimentClassifierModel extends FunSpec with Matchers {

  describe("Tweet Sentiment Classifier") {

    it("should return POSITIVE when tweet has POSiTIVE emotion") {
      val tweet = "Hope everyone is having a wonderful #EasterWeekend"
      val sentiment = SentimentClassiferModel.mainSentiment(tweet)
      sentiment should be(Sentiment.POSITIVE)
    }

    it("should return NEGATIVE when tweet has NEGATIVE emotion") {
      val tweet = "I'm feeling awful this #EasterWeekend"
      val sentiment = SentimentClassiferModel.mainSentiment(tweet)
      sentiment should be(Sentiment.NEGATIVE)
    }

    it("should return NEUTRAL when tweet has NO emotion") {
      val tweet = "This weekend is #EasterWeekend"
      val sentiment = SentimentClassiferModel.mainSentiment(tweet)
      sentiment should be(Sentiment.NEUTRAL)
    }

    it("should return NEGATIVE when tweet has a NEGATED POSITIVE emotion") {
      val tweet = "Today was not a good day"
      val sentiment = SentimentClassiferModel.mainSentiment(tweet)
      sentiment should be(Sentiment.NEGATIVE)
    }

  }

}
