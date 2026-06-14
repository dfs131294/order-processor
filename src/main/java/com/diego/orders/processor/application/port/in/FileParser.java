package com.diego.orders.processor.application.port.in;

import java.io.InputStream;
import java.util.List;

public interface FileParser<T> {

    boolean supports(String fileType);

    List<T> parse(InputStream inputStream);
}