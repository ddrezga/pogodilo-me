package controllers

import play.api._
import play.api.mvc._
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import model.Tables._

object Tickets extends Controller {

  def list(tId: Int = 1) = DBAction { implicit rs =>
    val query = for {
      ((((bt, btm),m),c1),c2) <- (BetTicket leftJoin BetTicketMatches on (_.id === _.betTicketId)
        leftJoin Match on (_._2.matchId === _.id)
        leftJoin Competitor on (_._2.home === _.id)
        leftJoin Competitor on (_._1._2.visitor === _.id)) if m.tournamentId === tId
    } yield (bt,m,(c1,c2))

    val tournament = Tournament.where(_.id === tId).list().head

    //val result = query.list.groupBy(_._1).mapValues(_ map (x => (x._2, x._3))).mapValues(_ groupBy (_._1)).mapValues(_.mapValues(_ map (_._2)))
    val result = query.list.groupBy(x=>(x._1,x._2)).mapValues(_ map (_._3)).groupBy(_._1._1)
    //Ok(tournament.displayName + "\n\n" + result.mkString("\n\n") + "\n\n" + query.selectStatement)
    Ok(views.html.tickets(result))
  }
}