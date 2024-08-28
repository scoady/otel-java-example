import javax.management.*;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import java.lang.management.ManagementFactory;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import com.sun.net.httpserver.HttpServer;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class SimpleHttpServer {

    // MBean interface
    public interface RequestCounterMBean {
        int getRequestCount();
    }

    // MBean implementation
    public static class RequestCounter implements RequestCounterMBean {
        private int requestCount = 0;

        public synchronized void increment() {
            requestCount++;
        }

        @Override
        public synchronized int getRequestCount() {
            return requestCount;
        }
    }

    public static void main(String[] args) throws Exception {
        // Enable JMX
        System.setProperty("java.rmi.server.hostname", "0.0.0.0"); // Bind to all interfaces or use the specific IP of the host
        System.setProperty("com.sun.management.jmxremote.rmi.port", "7199");
        System.setProperty("com.sun.management.jmxremote", "");
        System.setProperty("com.sun.management.jmxremote.port", "7199");
        System.setProperty("com.sun.management.jmxremote.authenticate", "false");
        System.setProperty("com.sun.management.jmxremote.ssl", "false");

        // Create the MBean and register it with the platform MBean server
        RequestCounter requestCounter = new RequestCounter();
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = new ObjectName("com.example.metrics:type=RequestCounter");
        mbs.registerMBean(requestCounter, name);

        // Manually start the RMI registry and JMX connector server
        Registry registry = LocateRegistry.createRegistry(7199);
        JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://0.0.0.0:7199/jmxrmi");
        JMXConnectorServer jmxConnectorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, null, mbs);
        jmxConnectorServer.start();

        // Create the HTTP server
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", exchange -> {
            requestCounter.increment(); // Increment the request counter
            String response = "ok";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        });

        // Start the server
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Server is listening on port 8080...");
    }
}
