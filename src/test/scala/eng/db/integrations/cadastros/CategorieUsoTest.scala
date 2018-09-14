package eng.db.integrations.cadastros

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import eng.db.integrations.Marshallers
import eng.db.integrations.objects.CategoriaUso
import eng.db.integrations.routes.CadastrosRouteProvider
import eng.db.shared.base.{BackEndError, BaseTypesMarshallers}
import eng.db.shared.messageBroker.DispatchProperties
import eng.db.shared.webservices.AkkaInternalDispatcher
import org.scalatest.AsyncFlatSpec
import spray.json._

import scala.concurrent.ExecutionContextExecutor

//noinspection NameBooleanParameters
class CategorieUsoTest extends AsyncFlatSpec with Marshallers with BaseTypesMarshallers {

    import eng.db.shared.messageBroker.DispatchPropertiesJsonProtocol._
    import eng.db.shared.webservices.DefaultExceptionHandlers._

    "The categorie uso web method" should "get all the items available for integrations" in {

        implicit val system: ActorSystem = ActorSystem("test-system")
        implicit val materializer: ActorMaterializer = ActorMaterializer()
        implicit val executionContext: ExecutionContextExecutor = system.dispatcher

        val dispatcher = new AkkaInternalDispatcher(None, new CadastrosRouteProvider().getWhitelistedRoutes())

        try {
            val result = dispatcher.ask(DispatchProperties(
                None,
                None,
                "GET".encode,
                "/Cadastros/CategorieUso",
                None))
            result.map(a => {
                val message = a.map(_.toChar).mkString
                //                println(s"The response is: $message")
                assert(message !== "")

                val jsValue = message.parseJson
                try {
                    val deserialized = jsValue.convertTo[Seq[CategoriaUso]]
                    assert(deserialized.length === 5)
                }
                catch {
                    case _: DeserializationException =>
                        val error = jsValue.convertTo[BackEndError]
                        assert(false, s"Error: ${error.code} = ${error.description}")
                    case ex: Exception => assert(false, s"Deserialization error: ${ex.toString}")
                }
            })
        }
        catch {
            case ex: Exception => assert(false, ex.toString)
        }
    }
}
