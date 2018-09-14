package eng.db.integrations

import akka.http.scaladsl.server.ExceptionHandler
import eng.db.integrations.routes.CadastrosRouteProvider
import eng.db.shared.apps.ServerApp
import eng.db.shared.log.LogProvider
import eng.db.shared.messageBroker.MessageBrokerConsumerFactory

object IntegrationsWebServer extends ServerApp {
    override protected def getHandlerInstance()(implicit logProvider: LogProvider, exceptionHandler: ExceptionHandler) =
        MessageBrokerConsumerFactory.getConsumer(new CadastrosRouteProvider())
}
