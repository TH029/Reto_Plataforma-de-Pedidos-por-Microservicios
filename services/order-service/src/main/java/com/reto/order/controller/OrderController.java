import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    private final OrderProducer producer;

    public OrderController(OrderProducer producer) {
        this.producer = producer;
    }

    @GetMapping("/test-order")
    public String testOrder() {
        producer.sendOrder("Pedido creado desde order-service");
        return "Mensaje enviado a RabbitMQ";
    }
}