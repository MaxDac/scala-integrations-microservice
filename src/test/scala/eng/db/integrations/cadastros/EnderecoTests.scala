package eng.db.integrations.cadastros

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import eng.db.integrations.Marshallers
import eng.db.integrations.objects.{Endereco, Fornecimento}
import eng.db.integrations.routes.CadastrosRouteProvider
import eng.db.shared.base.{BackEndError, BaseTypesMarshallers, JsonConvert, QueryKey}
import eng.db.shared.messageBroker.DispatchProperties
import eng.db.shared.webservices.AkkaInternalDispatcher
import org.scalatest.AsyncFlatSpec
import spray.json.DeserializationException

import scala.concurrent.ExecutionContextExecutor

class EnderecoTests extends AsyncFlatSpec with Marshallers with BaseTypesMarshallers {

    import eng.db.shared.messageBroker.DispatchPropertiesJsonProtocol._
    import eng.db.shared.webservices.DefaultExceptionHandlers._

    "The endereco web method" should "get the item correspondent to the input address" in {

        implicit val system: ActorSystem = ActorSystem("test-system")
        implicit val materializer: ActorMaterializer = ActorMaterializer()
        implicit val executionContext: ExecutionContextExecutor = system.dispatcher

        val dispatcher = new AkkaInternalDispatcher(None, new CadastrosRouteProvider().getWhitelistedRoutes())

        try {
            val endereco = Endereco(
                Some("40"),
                Some("00040000000885"),
                Some("4842"),
                None,
                None
            )
            val body = JsonConvert(endereco).asJson()
            val result = dispatcher.ask(DispatchProperties(
                None,
                None,
                "POST".encode,
                "/Cadastros/Endereco",
                Some(body)))
            result.map(a => {
                val message = a.map(_.toChar).mkString
                //                println(s"The response is: $message")
                val response = JsonConvert.fromString[Seq[QueryKey]](message)

                assert(response.length === 2)
                assert(response.filter(e => e.id === "SB6789110").length === 1)
                assert(response.filter(e => e.id === "SB5576567").length === 1)
            })
        }
        catch {
            case ex: Exception => assert(false, ex.toString)
        }
    }
}
