package controllers

import play.api._
import play.api.mvc._
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import model.Tables._

object Tickets extends Controller {
  def list = DBAction { implicit rs =>
    val query = for {
      ((((bt, btm),m),c1),c2) <- BetTicket leftJoin BetTicketMatches on (_.id === _.betTicketId) leftJoin Match on (_._2.matchId === _.id) leftJoin Competitor on (_._2.home === _.id) leftJoin Competitor on (_._1._2.visitor === _.id)
    } yield (bt,m,c1,c2)
    val result = query.list.groupBy(_._1).mapValues(x=>x.groupBy(_._2))
    //val result = query.list.groupBy(x=>(x._1,x._2))    
    Ok(result.mkString("\n"))
  }
}