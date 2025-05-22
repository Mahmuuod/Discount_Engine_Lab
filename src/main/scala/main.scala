import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import scala.io.Source
import java.time.Instant
import java.time.{MonthDay ,ZoneId}
import java.sql.{Connection, DriverManager, ResultSet,PreparedStatement}
import java.io.{FileWriter, PrintWriter}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
object main {
  def main(args: Array[String]): Unit = {


    //timestamp,product_name,expiry_date,quantity,unit_price,channel,payment_method
    //timestamp,product_name,expiry_date,quantity,unit_price,daysBetween
    val lines = Source.fromFile("D:\\study\\iti\\Scala\\scala-iti45\\Project\\TRX1000.csv").getLines().toList.tail

    //gets the data in suitable format with some calculations
    val orders = (lines).map(x => {
      val y = x.split(",")
      //converting to time stamp
      val instant = Instant.parse(y(0))
      val timestamp = Timestamp.from(instant)

      val product_name = y(1)
      val dateFormat = new SimpleDateFormat("yyyy-MM-dd") // adjust format as per your date string
      val expiry_date: Date = dateFormat.parse(y(2))
      val quantity = y(3).toInt
      val unit_price = y(4).toDouble
      val transaction_date: Date = new Date(timestamp.getTime)
      val daysBetween = (expiry_date.getTime - transaction_date.getTime) / (1000 * 60 * 60 * 24) //get time returns milli secs
      val wine_cheese = y(1).split(" ")(0)
      val channel = y(5)
      val payment_method = y(6)
      val total = quantity.toDouble * unit_price
      (timestamp, product_name, expiry_date, quantity, unit_price, daysBetween.toInt, wine_cheese, total, channel, payment_method)
    })


    def expirationQualifier(order: (Timestamp, String, Date, Int, Double, Int, String, Double, String, String)): Boolean = {
      val daysBetween = order._6
      val result = if (daysBetween > 0 && daysBetween < 30) {
        true
      }
      else {
        false
      }

      result
    }

    def expirationCalculate(order: (Timestamp, String, Date, Int, Double, Int, String, Double, String, String)): Double = {
      val daysBetween = order._6
      val discount = ((30 - daysBetween) / 100.0)

      discount
    }

    def cheeseWineQualifier(order: (Timestamp, String, Date, Int, Double, Int, String, Double, String, String)): Boolean = {
      val cheese_wine: Boolean = if (order._7.equals("Wine") || order._7.equals("Cheese")) true else false
      cheese_wine
    }

    def cheeseWineCalculate(order: (Timestamp, String, Date, Int, Double, Int, String, Double, String, String)): Double = {
      val cheeseWine: String = order._7
      val discount: Double = if (cheeseWine.equals("Wine")) {
        0.05
      }
      else if (cheeseWine.equals("Cheese")) {
        0.1
      }
      else
        0.0

      discount
    }

    def MerchQualifier(order: (Timestamp, String, Date, Int, Double, Int, String, Double, String, String)): Boolean = {
      val transactionDate = order._1.toInstant.atZone(ZoneId.systemDefault()).toLocalDate

      val targetMonthDay = MonthDay.parse("--03-23") // Note the -- prefix for MonthDay

      val transactionMonthDay = MonthDay.from(transactionDate)

      transactionMonthDay == targetMonthDay
    }

    def MerchCalculate(order: (Timestamp, String, Date, Int, Double, Int, String, Double, String, String)): Double = {
      0.5
    }

    def quantityQualifier(order: (Timestamp, String, Date, Int, Double, Int, String, Double, String, String)): Boolean = {
      val quantity = order._4
      val result = if (quantity > 5) true else false
      result

    }

    def quantityCalculate(order: (Timestamp, String, Date, Int, Double, Int, String, Double, String, String)): Double = {
      val unitPrice = order._5
      val quantity = order._4.toDouble

      val discount: Double = if (quantity >= 6 && quantity <= 9) 0.05
      else if (quantity > 9 && quantity <= 14) 0.07
      else if (quantity >= 15) 0.1
      else 0.0

      discount

    }


    def channelQualifier(order: (Timestamp, String, Date, Int, Double, Int, String, Double, String, String)): Boolean = {
      val channel = order._9
      val result = if (channel.equals("App")) true else false
      result

    }

    def channelCalculate(order: (Timestamp, String, Date, Int, Double, Int, String, Double, String, String)): Double = {
      val quantity = order._4
      val discount: Double = (Math.ceil(quantity.toDouble / 5) * 5 * 0.01).min(0.5)
      discount

    }



  }
}

