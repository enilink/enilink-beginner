package net.enilink.beginner.web



import scala.language.implicitConversions
import net.enilink.komma.core.URIs
import net.enilink.platform.lift.sitemap.HideIfInactive
import net.enilink.platform.lift.sitemap.Menus
import net.enilink.platform.lift.sitemap.Menus._
import net.enilink.platform.lift.util.Globals
import net.liftweb.common.Full
import net.liftweb.http.{LiftRules, RedirectResponse, Req, S}
import net.liftweb.sitemap.SiteMap

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
    // add DOM LDP service
    LiftRules.dispatch.append(LDPServiceExample)
  }

//  def boot {
//    // redirect to index
//    LiftRules.statelessDispatch.prepend {
//      case Req(`app` :: Nil, _, _) => {
//        () => Full(RedirectResponse(s"/$app/"))
//      }
//    }
//
//    // add DOM LDP service
//    LiftRules.dispatch.append(LDPServiceExample)
//  }


  def shutdown {
  }
}