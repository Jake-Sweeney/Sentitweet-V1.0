package model

import java.util.regex.Pattern

/**
  * Created by Jake on 16/02/2016.
  */

object TweetCleaner {

  def cleanText(text: String): String = {
    var cleanText = replaceEscapedCharacters(text)
    cleanText = removeURLsFromText(cleanText)
    cleanText = removeReTweetTagFromText(cleanText)
    cleanText = removeEmojisFromText(cleanText)
    cleanText = removeHandlersFromText(cleanText)
    cleanText
  }

  def replaceEscapedCharacters(text: String) : String = {
    var cleanText = text.replace("\n", "")
    cleanText = cleanText.replace("\t", "")
    cleanText
  }

  def removeURLsFromText(text: String) : String = {
    val pattern = "(\\s*:?\\s*htt\\S+)".r
    val textWithURLRemoved = pattern replaceAllIn (text, "")
    textWithURLRemoved
  }

  def removeReTweetTagFromText(text: String): String = {
    val pattern = "^RT\\s+@\\S+:\\s+".r
    val textWithRetweetTagRemoved = pattern replaceFirstIn (text, "")
    textWithRetweetTagRemoved
  }

  def removeEmojisFromText(text: String) : String = {
    var utf8Text = ""
    try {
      val utf8bytes = text.getBytes("UTF-8")
      utf8Text = new String(utf8bytes, "UTF-8")
    } catch {
      case e: UnsupportedOperationException => e.printStackTrace()
    }
    val unicodeOutliers = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
      Pattern.UNICODE_CASE | Pattern.CANON_EQ | Pattern.CASE_INSENSITIVE)
    val unicodeOutlierMatcher = unicodeOutliers.matcher(utf8Text)
    utf8Text = unicodeOutlierMatcher.replaceAll("")
    utf8Text
  }

  def removeHandlersFromText(text: String) : String = {
    val pattern = "\\s*@\\S+\\s*".r
    val textWithHandlersRemoved = pattern replaceAllIn(text, " ")
    textWithHandlersRemoved
  }
}
