package com.dataart.order.saga.route;

import com.dataart.order.processor.CheckCustomerProcessor;
import com.dataart.order.processor.CompleteOrderProcessor;
import com.dataart.order.processor.NewOrderProcessor;
import com.dataart.order.saga.command.SagaCommandProcessor;
import com.dataart.order.saga.compensation.SagaCompensationProcessor;
import lombok.RequiredArgsConstructor;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.SagaPropagation;
import org.apache.camel.saga.InMemorySagaService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class OrderSagaRoute extends RouteBuilder {

    private final NewOrderProcessor newOrderProcessor;
    private final CheckCustomerProcessor checkCustomerProcessor;
    private final CompleteOrderProcessor completeOrderProcessor;

    private final Map<String, SagaCommandProcessor> commandProcessorMap;
    private final Map<String, SagaCompensationProcessor> compensationProcessorMap;

    @Override
    public void configure() throws Exception {
        //choose im memory service
        getContext().addService(new InMemorySagaService());
        //Order initialize
        from("direct:orderSaga")
                .process(newOrderProcessor)
                .log(LoggingLevel.INFO, "Id: ${header.transactionId}, Order Received: ${body}")
                .to("seda:asyncCommitSaga");

        //saga initialize
        from("seda:asyncCommitSaga")
                .saga()
                .to("direct:checkCustomer")
                .to("direct:requestInvoice")
                .to("direct:makePayment")
                .to("direct:shipOrder");

        // CHECK CUSTOMER BLACKLIST
        from("direct:checkCustomer")
                .saga()
                .propagation(SagaPropagation.MANDATORY)
                .option("transactionId", header("transactionId"))
                .setBody(body())
                .log("Customer checking")
                .delay(3000)
                .process(checkCustomerProcessor)
                .log("Customer checked");

        // REQUEST INVOICE route
        from("direct:requestInvoice")
                .saga()
                .propagation(SagaPropagation.MANDATORY)
                .option("transactionId", header("transactionId"))
                .option("body", body())
                .compensation("direct:cancelInvoice")
                .log("Invoice creating")
                .delay(3000)
                .process(commandProcessorMap.get("requestInvoiceProcessor"))
                .log("Invoice created");
        // CANCEL INVOICE
        from("direct:cancelInvoice")
                .log("Invoice canceling")
                .process(compensationProcessorMap.get("cancelInvoiceProcessor"))
                .log("Invoice canceled");

        // MAKE PAYMENT route
        from("direct:makePayment")
                .saga()
                .propagation(SagaPropagation.MANDATORY)
                .option("transactionId", header("transactionId"))
                .option("body", body())
                .compensation("direct:refundPayment")
                .log("Payment creating")
                .delay(3000)
                .process(commandProcessorMap.get("makePaymentProcessor"))
                .log("Payment order created");
        // REFUND PAYMENT route
        from("direct:refundPayment")
                .log("Payment refunding")
                .bean(compensationProcessorMap.get("refundPaymentProcessor"))
                .log("Payment refunded");

        // SHIP ORDER route
        from("direct:shipOrder")
                .saga()
                .propagation(SagaPropagation.MANDATORY)
                .option("transactionId", header("transactionId"))
                .option("body", body())
                .compensation("direct:cancelShipping")
                .completion("direct:completeOrder")
                .log("Shipping creating")
                .delay(3000)
                .process(commandProcessorMap.get("shipOrderProcessor"))
                .log("Shipping created");
        // CANCEL SHIPPING
        from("direct:cancelShipping")
                .log("Shipping canceling")
                .process(compensationProcessorMap.get("cancelShippingProcessor"))
                .log("Shipping canceled");

        // COMPLETE ORDER
        from("direct:completeOrder")
                .log("Order completing")
                .process(completeOrderProcessor)
                .log("Order completed")
                .end();
    }
}
