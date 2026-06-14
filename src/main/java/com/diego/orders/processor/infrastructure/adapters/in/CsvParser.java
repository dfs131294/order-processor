package com.diego.orders.processor.infrastructure.adapters.in;

import com.diego.orders.processor.application.dto.PedidoDTO;
import com.diego.orders.processor.application.port.in.FileParser;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class CsvParser implements FileParser<PedidoDTO> {

    @Override
    public boolean supports(String fileType) {
        return false;
    }

    @Override
    public List<PedidoDTO> parse(InputStream inputStream) {
        try (
                Reader reader = new InputStreamReader(
                        inputStream,
                        StandardCharsets.UTF_8
                )
        ) {
            CsvToBean<PedidoDTO> csvToBean =
                    new CsvToBeanBuilder<PedidoDTO>(reader)
                            .withType(PedidoDTO.class)
                            .withIgnoreLeadingWhiteSpace(true)
                            .withThrowExceptions(false)
                            .build();

            List<PedidoDTO> orders = csvToBean.parse();
            for (int i = 0; i < orders.size(); i++) {
                orders.get(i).setPosicion(i + 1);
            }
            return orders;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}