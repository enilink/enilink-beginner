package net.enilink.beginner.web

import scala.language.implicitConversions
import net.enilink.lift.sitemap.Menus
import net.enilink.lift.sitemap.Menus._
import net.liftweb.http.S
import net.liftweb.sitemap.LocPath.stringToLocPath
import net.liftweb.sitemap.Menu
import net.liftweb.sitemap.SiteMap
import net.enilink.lift.util.Globals
import net.liftweb.common.Full
import net.enilink.komma.core.URIs
import java.util.HashMap
import net.liftweb.http.Req
import net.liftweb.http.LiftRules
import net.enilink.lift.sitemap.HideIfInactive

/**
 * This is the main class of the web module. It sets up and tears down the application.
 */
class LiftModule {
  implicit val app = "beginner"

  def sitemapMutator: SiteMap => SiteMap = {
    val menu = application(app, app :: Nil, List(
      appMenu("index", S ? "Index", List("index")) submenus List(
        appMenu("projects", S ? "Projects", List("projects")) submenus List(
          appMenu("experiments", S ? "Experiments", List("experiments")) >> HideIfInactive))))

    Menus.sitemapMutator(menu :: Nil)
  }

  val DEFAULT_MODEL_URI = URIs.createURI("http://example.org/models/beginner/")

  def boot {
    val reloadModel = false

    // create default default model on start up if it does not already exist
    Globals.contextModelSet.vend map {
      ms =>
        try {
          ms.getUnitOfWork.begin
          val model = ms.createModel(DEFAULT_MODEL_URI)
          if (reloadModel) {
            // reload model on each restart
            //            model.getManager.clear
            //            model.setLoaded(false)
            //            model.load(new HashMap)
          }
        } finally {
          ms.getUnitOfWork.end
        }
    }

    Globals.contextModelRules.prepend {
      case Req(`app` :: _, _, _) if !S.param("model").isDefined => Full(DEFAULT_MODEL_URI)
    }
  }

  def shutdown {
  }
}