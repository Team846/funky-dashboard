package com.lynbrookrobotics.funkydashboard;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Cancellable;
import akka.http.impl.util.WebsocketConstructor;
import akka.http.javadsl.model.ContentTypes;
import akka.http.javadsl.model.ws.Message;
import akka.http.javadsl.model.ws.TextMessage;
import akka.http.javadsl.server.HttpApp;
import akka.http.javadsl.server.Route;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonValue;
import com.typesafe.config.ConfigFactory;

import scala.Tuple2;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;
import scala.runtime.BoxedUnit;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class FunkyDashboard extends HttpApp {
    public static void main(String[] args) throws IOException {
        ActorSystem system = ActorSystem.create("server", ConfigFactory.parseFile(new File("application-server.conf")));

        FunkyDashboard dashboard = new FunkyDashboard();
        dashboard.bindRoute("localhost", 8080, system);

        Random random = new Random();

        dashboard.datasetGroup("hello").addDataset(new TimeSeriesNumeric<>("Current Time", random::nextDouble));
        dashboard.datasetGroup("byebye").addDataset(new TimeSeriesNumeric<>("Current Time Nanos", random::nextInt));

        dashboard.attachBlackbox(system.actorFor("akka.tcp://blackbox@localhost:2552/user/receiver"), system);

        System.out.println("Press enter to quit");
        System.in.read();
        system.shutdown();
    }

    private LinkedList<DatasetGroup> datasetGroups = new LinkedList<>();

    public DatasetGroup datasetGroup(String name) {
        return datasetGroups.stream().filter(elem -> elem.getName().equals(name)).findAny().
            orElseGet(() -> {
                DatasetGroup created = new DatasetGroup(name);
                datasetGroups.add(created);
                return created;
            });
    }

    private JsonValue datasetsJSON() {
        return datasetGroups.stream().map(DatasetGroup::toJSON).reduce(
            Json.array().asArray(),
            JsonArray::add,
            (acc1, acc2) -> {
                throw new RuntimeException("Merging arrays is not yet implemented");
            }
        );
    }

    private JsonValue currentDatasetsJSON() {
        return datasetGroups.stream().reduce(
                Json.object(),
                (acc, datasetGroup) -> acc.add(datasetGroup.getName(), datasetGroup.currentDatasetValues()),
                (acc1, acc2) -> {
                    throw new RuntimeException("Merging objects is not yet implemented");
                }
        );
    }

    Source<Tuple2<Long, String>, Cancellable> outJSON = Source.tick(
            new FiniteDuration(0, TimeUnit.MILLISECONDS),
            new FiniteDuration(125, TimeUnit.MILLISECONDS),
            0
    ).map(tick -> Tuple2.apply(System.currentTimeMillis(), currentDatasetsJSON().toString()));

    Source<Message, Cancellable> source =
            outJSON.map(v -> TextMessage.create(v._2));

    public void attachBlackbox(ActorRef target, ActorSystem system) {
        outJSON.map(t -> t._1 + " " + t._2).runWith(
                Sink.actorRef(target, ""),
                ActorMaterializer.create(system)
        );
    }

    public void outputText(ActorSystem system) {
        outJSON.map(t -> t._2).runForeach(
                System.out::println,
                ActorMaterializer.create(system)
        );
    }

    @Override
    public Route createRoute() {
        return route(
            get(
                pathSingleSlash().route(
                    getFromResource("META-INF/resources/index.html")
                ),
                pathPrefix("").route(
                    encodeResponse(getFromResourceDirectory("META-INF/resources"))
                ),
                path("datasets.json").route(
                    handleWith(ctx ->
                        ctx.complete(
                            ContentTypes.APPLICATION_JSON,
                            datasetsJSON().toString()
                        ))
                ),
                path("datastream").route(
                    handleWith(ctx ->
                        ctx.complete(WebsocketConstructor.handleWebsocketRequestWith(
                            ctx.request(),
                            source,
                            Sink.ignore()
                        ))
                    )
                )
            )
        );
    }
}
