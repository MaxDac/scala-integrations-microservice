package eng.db.integrations.cadastros

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import eng.db.dataaccess.base.DataAccessComponent
import eng.db.integrations.Marshallers
import eng.db.integrations.objects._
import eng.db.integrations.routes.CadastrosRouteProvider
import eng.db.shared.base._
import eng.db.shared.messageBroker.DispatchProperties
import eng.db.shared.webservices.AkkaInternalDispatcher
import org.scalatest.AsyncFlatSpec
import spray.json._

import scala.concurrent.ExecutionContextExecutor

//noinspection NameBooleanParameters
class FornecimentoTest extends AsyncFlatSpec with Marshallers with BaseTypesMarshallers {

    import eng.db.shared.messageBroker.DispatchPropertiesJsonProtocol._
    import eng.db.shared.webservices.DefaultExceptionHandlers._

    "The fornecimento web method" should "get the item correspondent to the input address" in {

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
                "/Cadastros/Endereco/Fornecimento",
                Some(body)))
            result.map(a => {
                val message = a.map(_.toChar).mkString
                //                println(s"The response is: $message")
                val response = JsonConvert.fromString[Fornecimento](message)
                assert(response.id === "SBHoyj")
                assert(response.code === "269429760001")

                val jsValue = message.parseJson
                try {
                    val deserialized = jsValue.convertTo[Fornecimento]
                    assert(!deserialized.code.isEmpty)
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

    "The query" should "return the correct fornecimento id" in {
//        implicit val ec: ExecutionContext = ExecutionContext.global
        Fornecimento.getByCode("262937069001") map { f => assert(f.id === "SBHRpr") }
    }

    "The query" should "return manouvers" in {
//        implicit val ec: ExecutionContext = ExecutionContext.global
        DataAccessComponent scope { dac =>
            Fornecimento.getManouverOnItemQuery(dac, Fornecimento(id = "SBE"))
        } map { m => {
            assert(m.length === 1)
            assert(m.head.description === "NENHUMA MANOBRA EM ANDAMENTO")
            assert(m.head.date.get.getTime === DbConstants.initialTimestamp.getTime)
        }}
    }

    "The routes" should "return the manouvers" in {
        implicit val system: ActorSystem = ActorSystem("test-system")
        implicit val materializer: ActorMaterializer = ActorMaterializer()
        implicit val executionContext: ExecutionContextExecutor = system.dispatcher

        val dispatcher = new AkkaInternalDispatcher(None, new CadastrosRouteProvider().getWhitelistedRoutes())

        try {
            val fornecimentoCode = "1082001"
            val result = dispatcher.ask(DispatchProperties(
                None,
                None,
                "GET".encode,
                s"/Cadastros/Fornecimento/$fornecimentoCode/Manouvers",
                None
            ))
            result.map(a => {
                val message = a.map(_.toChar).mkString
                //                println(s"The response is: $message")
                val response = JsonConvert.fromString[Seq[Manouver]](message)
                assert(response.length === 1)
            })
        }
        catch {
            case ex: Exception => assert(false, ex.toString)
        }
    }

//    "The method" should "return all the manouvers for the fornecimento" is {
//        implicit val system: ActorSystem = ActorSystem("test-system")
//        implicit val materializer: ActorMaterializer = ActorMaterializer()
//        implicit val executionContext: ExecutionContextExecutor = system.dispatcher
//
//        val dispatcher = new AkkaInternalDispatcher(None, new CadastrosRouteProvider().getWhitelistedRoutes())
//    }
}
