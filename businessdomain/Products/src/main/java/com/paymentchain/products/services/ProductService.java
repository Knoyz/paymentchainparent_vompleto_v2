package com.paymentchain.products.services;

import com.paymentchain.products.entities.Product;
import com.paymentchain.products.exeptions.BussinesRuleException;
import com.paymentchain.products.respository.ProductRepository;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.tcp.TcpClient;

import java.util.concurrent.TimeUnit;

@Service
public class ProductService {

    @Autowired
    ProductRepository productRepository;

    public Product save(Product input) throws BussinesRuleException {
        Product product;
        if (input.getCode()  == null || input.getName() == null) {
            BussinesRuleException exception = new BussinesRuleException("1025", "Error de validacion, nombre o codigo de producto no existe", HttpStatus.PRECONDITION_FAILED);
            throw exception;
        } else {
            product = productRepository.save(input);
        }
        return product;
    }
}
