/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paymentchain.customer.business.transactions;

import com.fasterxml.jackson.databind.JsonNode;
import com.paymentchain.customer.respository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import com.paymentchain.customer.entities.Customer;
import com.paymentchain.customer.entities.CustomerProduct;
import com.paymentchain.customer.exception.BussinesRuleException;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;

import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import javax.transaction.Transaction;

/**
 * @author Knoyz
 */
@Service
public class BussinesTransaction {

    @Autowired
    CustomerRepository customerRepository;

    private final WebClient.Builder webClientBuilder;

    public BussinesTransaction(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    // define timeout
    TcpClient tcpClient = TcpClient
            .create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
            .doOnConnected(connection -> {
                connection.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS));
                connection.addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS));
            });

    public Customer getByCode(String code) {
        Customer customer = customerRepository.findByCode(code);
        if (customer.getProducts() != null) {
            List<CustomerProduct> products = customer.getProducts();
            products.forEach(dto -> {
                try {
                    String productName = getProductName(dto.getProductId());
                    dto.setProductName(productName);
                } catch (UnknownHostException ex) {
                    Logger.getLogger(BussinesTransaction.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }
        customer.setTransactions(getTransacctions(customer.getIban()));
        return customer;
    }

    public Customer save(Customer input) throws BussinesRuleException, UnknownHostException {
        if (input.getProducts() != null) {
            for (Iterator<CustomerProduct> it = input.getProducts().iterator(); it.hasNext();) {
                CustomerProduct dto = it.next();
                String productName = getProductName(dto.getProductId());
                if (productName.isBlank()) {
                    BussinesRuleException exception = new BussinesRuleException("1025",
                            "Error de validacion, producto no existe", HttpStatus.PRECONDITION_FAILED);
                    throw exception;
                } else {
                    dto.setCustomer(input);
                }
            }
        }
        Customer save = customerRepository.save(input);
        return save;
    }

    private <T> List<T> getTransacctions(String accountIban) {
        List<T> trasnsactions = new ArrayList<>();
        try {
            WebClient client = webClientBuilder
                    .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)))
                    .baseUrl("http://businessdomain-transactions/transaction")
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .defaultUriVariables(
                            Collections.singletonMap("url", "http://businessdomain-transactions/transaction"))
                    .build();
            List<Object> block = client.method(HttpMethod.GET).uri(uriBuilder -> uriBuilder
                    .path("/transactions")
                    .queryParam("ibanAccount", accountIban)
                    .build())
                    .retrieve().bodyToFlux(Object.class).collectList().block();
            trasnsactions = (List<T>) block;
        } catch (Exception e) {
            return trasnsactions;
        }
        return trasnsactions;
    }

    private String getProductName(long id) throws UnknownHostException {
        String name = null;
        try {
            WebClient client = webClientBuilder
                    .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)))
                    .baseUrl("http://businessdomain-product/product")
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .defaultUriVariables(Collections.singletonMap("url", "http://businessdomain-product/product"))
                    .build();
            JsonNode block = client.method(HttpMethod.GET).uri("/" + id)
                    .retrieve().bodyToMono(JsonNode.class).block();
            name = block.get("name").asText();
        } catch (WebClientResponseException e) {
            HttpStatus statusCode = e.getStatusCode();
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return "";
            } else {
                throw new UnknownHostException(e.getMessage());
            }
        }
        return name;
    }

    public ResponseEntity<Customer> get(long id) throws BussinesRuleException {
        HttpStatus httpStatus = HttpStatus.OK;
        Customer customer;
        Optional<Customer> OptCustomer = customerRepository.findById(id);
        if (OptCustomer.isPresent()) {
            customer = OptCustomer.get();
            if (customer.getProducts() != null) {
                List<CustomerProduct> products = customer.getProducts();
                products.forEach(dto -> {
                    try {
                        String productName = getProductName(dto.getProductId());
                        dto.setProductName(productName);
                    } catch (UnknownHostException ex) {
                        Logger.getLogger(BussinesTransaction.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
            }
            customer.setTransactions(getTransacctions(customer.getIban()));
        } else {
            httpStatus = HttpStatus.NOT_FOUND;
            throw new BussinesRuleException("1026", "id no encontrado", httpStatus);
        }
        return new ResponseEntity<>(customer, httpStatus);
    }

    public ResponseEntity<?> put(String id, Customer input) throws BussinesRuleException {
        HttpStatus httpStatus = HttpStatus.OK;

        Customer customer = customerRepository.findById(Long.valueOf(id)).get();

        if (input.getName().isBlank() ||
                input.getIban().isBlank() ||
                input.getCode().isBlank() ||
                input.getUsername().isBlank() ||
                id.isBlank()) {
            httpStatus = HttpStatus.PRECONDITION_REQUIRED;
            throw new BussinesRuleException("1026", "Faltan datos importantes para la modificacion", httpStatus);
        } else {
            if (customer != null) {
                // solo cambia si existe la entrada de cada dato de cliente
                if (input.getName() != null)
                    customer.setName(input.getName());
                if (input.getCode() != null)
                    customer.setCode(input.getCode());
                if (input.getIban() != null)
                    customer.setIban(input.getIban());
                if (input.getNames() != null)
                    customer.setNames(input.getNames());
                if (input.getUsername() != null)
                    customer.setUsername(input.getUsername());
                if (input.getPhone() != null)
                    customer.setPhone(input.getPhone());
                if (input.getAddress() != null)
                    customer.setAddress(input.getAddress());
                if (input.getProducts() != null)
                    customer.setProducts(input.getProducts());
                if (input.getTransactions() != null)
                    customer.setTransactions(input.getTransactions());
            } else {
                httpStatus = HttpStatus.NO_CONTENT;
                throw new BussinesRuleException("1027", "No se encontro a ningun cliente con ese id", httpStatus);
            }
        }
        return new ResponseEntity<>(customer, httpStatus);
    }

    public ResponseEntity<?> deleteCustomerById(String id) throws BussinesRuleException {
        HttpStatus httpStatus = HttpStatus.OK;
        Optional<Customer> customer = customerRepository.findById(Long.valueOf(id));
        if (customer == null) {
            httpStatus = HttpStatus.NOT_FOUND;
            throw new BussinesRuleException("1027", "No se encontro a ningun cliente con ese id", httpStatus);
        }
        customerRepository.deleteById(Long.valueOf(id));
        return new ResponseEntity<>(customer, httpStatus);
    }
}
