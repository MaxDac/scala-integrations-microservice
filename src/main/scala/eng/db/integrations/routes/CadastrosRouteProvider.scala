package eng.db.integrations.routes

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.stream.ActorMaterializer
import eng.db.dataaccess.base.DataAccessComponent
import eng.db.integrations.Marshallers
import eng.db.integrations.objects.{CategoriaUso, Endereco, Fornecimento, Manouver}
import eng.db.shared.log.LogProvider
import eng.db.shared.webservices.RouteProvider

import scala.concurrent.ExecutionContext

class CadastrosRouteProvider extends RouteProvider with Marshallers {

    override def getRoutes()(implicit logProvider: LogProvider, system: ActorSystem, materializer: ActorMaterializer, ec: ExecutionContext): Option[Route] = None

    override def getWhitelistedRoutes()(implicit logProvider: LogProvider, system: ActorSystem, materializer: ActorMaterializer, ec: ExecutionContext): Option[Route] = Some(
        pathPrefix("Cadastros") {
            path("CategorieUso") {
                get {
                    onSuccess(CategoriaUso.getAllQuery().perform()) { cts =>
                        complete(cts)
                    }
                }
            } ~ pathPrefix("Endereco") {
                path("") {
                    post {
                        entity(as[Endereco]) { endereco =>
                            onSuccess(Endereco.getFromDataQuery(endereco).perform()) { keys =>
                                complete(keys)
                            }
                        }
                    }
                } ~ path ("Fornecimento") {
                    post {
                        entity(as[Endereco]) { endereco =>
                            onSuccess(Fornecimento.getFromAddressQuery(endereco).perform()) { forn =>
                                complete(forn.head)
                            }
                        }
                    }
                }
            } ~ pathPrefix("Fornecimento" / Segment) { code =>
                path("Manouvers") {
                    get {
                        onSuccess(DataAccessComponent scope { dac =>
                            val forn = Fornecimento.getByCodeQuery(code).performWithin(dac).headOption
                            forn match {
                                case Some(f) => Fornecimento.getManouverOnItemQuery(dac, f)
                                case _ => Seq.empty[Manouver]
                            }
                        }) { manouvers =>
                            complete(manouvers)
                        }
                    }
                }
            }
        }
    )
}
