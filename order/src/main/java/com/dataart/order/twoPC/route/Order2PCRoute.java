package com.dataart.order.twoPC.route;

import com.dataart.order.processor.CheckCustomerProcessor;
import com.dataart.order.processor.CompleteOrderProcessor;
import com.dataart.order.processor.NewOrderProcessor;
import com.dataart.order.twoPC.exception.TransactionPrepareException;
import com.dataart.order.twoPC.phase.commit.CommitPhaseProcessor;
import com.dataart.order.twoPC.phase.prepare.PreparePhaseProcessor;
import com.dataart.order.twoPC.phase.rollback.RollbackPhaseProcessor;
import lombok.RequiredArgsConstructor;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class Order2PCRoute extends RouteBuilder {


    private final NewOrderProcessor newOrderProcessor;
    private final CompleteOrderProcessor completeOrderProcessor;
    private final CheckCustomerProcessor checkCustomerProcessor;

    private final Map<String, PreparePhaseProcessor> preparePhaseProcessorMap;
    private final Map<String, CommitPhaseProcessor> commitPhaseProcessorMap;
    private final Map<String, RollbackPhaseProcessor> rollbackPhaseProcessorMap;

    @Override
    public void configure() throws Exception {
        //Order initialize
        from("direct:order2PC")
                .process(newOrderProcessor)
                .log(LoggingLevel.INFO, "Id: ${header.transactionId}, Order Received: ${body}")
                .to("seda:checkCustomer2PC");

        // CHECK CUSTOMER BLACKLIST
        from("seda:checkCustomer2PC")
                .log("Customer checking")
                .delay(3000)
                .process(checkCustomerProcessor)
                .log("Customer checked")
                .to("direct:prepare2PC");

        // FIRST PHASE
        from("direct:prepare2PC")
                .log("First Phase started")
                .process(preparePhaseProcessorMap.get("prepareProductProcessor"))
                .process(preparePhaseProcessorMap.get("prepareWalletProcessor"))
                .process(preparePhaseProcessorMap.get("prepareShipmentProcessor"))
                .log("First Phase finished")
                .onException(TransactionPrepareException.class).handled(true).to("direct:rollback2PC").end()
                .to("direct:commit2PC")
                .end();

        // SECOND PHASE
        from("direct:commit2PC")
                .log("Second Phase started")
                .process(commitPhaseProcessorMap.get("commitProductProcessor"))
                .process(commitPhaseProcessorMap.get("commitWalletProcessor"))
                .process(commitPhaseProcessorMap.get("commitShipmentProcessor"))
                .log("Second Phase finished")
                .to("direct:completeOrder2PC");

        // ROLLBACK PHASE
        from("direct:rollback2PC")
                .log("Rollback Phase started")
                .process(rollbackPhaseProcessorMap.get("rollbackProductProcessor"))
                .process(rollbackPhaseProcessorMap.get("rollbackWalletProcessor"))
                //Rollback Order table
                .process(rollbackPhaseProcessorMap.get("rollbackOrderProcessor"))
                .log("Rollback Phase finished");

        // COMPLETE ORDER
        from("direct:completeOrder2PC")
                .log("Order completing")
                .process(completeOrderProcessor)
                .log("Order completed")
                .end();
    }
}
