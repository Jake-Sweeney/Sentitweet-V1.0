package controllers

import twitter4j.conf.ConfigurationBuilder
import twitter4j.{Twitter, TwitterFactory}

/**
  * Created by Jake on 19/01/2016.
  */
trait TwitterInstance {

  private val configurationBuilder = new ConfigurationBuilder
  configurationBuilder.setDebugEnabled(false)
    .setOAuthConsumerKey("YNEquCHXfSR0wV0fSrCiIUige")
    .setOAuthConsumerSecret("DTGJm51lL9Z1elQrTatIqA4FWTIFqiOChXeWHgRD5QTSO34w9b")
    .setOAuthAccessToken("290017343-IFcgtcnJsGhNYpLtwDLQ8Mdc9H68sTpoAAWBPSqb")
    .setOAuthAccessTokenSecret("5grRID2Bz0ZY7kITQ2MS3ESOy3uilcC4wXGgbpfphrrIm")
  val twitter: Twitter = new TwitterFactory(configurationBuilder.build()).getInstance()
}
