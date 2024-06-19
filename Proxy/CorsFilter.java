import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class CorsFilter extends Filter {
    @Override
    public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
        // Add CORS headers for all requests
        Headers headers = exchange.getResponseHeaders();
        headers.add("Access-Control-Allow-Origin", "*");
        headers.add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        headers.add("Access-Control-Allow-Headers", "Content-Type, Authorization");

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            // For OPTIONS requests, just return with 200 OK status
            exchange.sendResponseHeaders(200, -1);
            return;
        }

        // Continue processing other requests
        chain.doFilter(exchange);
    }

    @Override
    public String description() {
        return "CORS filter";
    }
}