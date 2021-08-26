package br.com.github.kalilventura.repository;

import br.com.github.kalilventura.model.ProductEventKey;
import br.com.github.kalilventura.model.ProductEventLog;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@EnableScan
public interface ProductEventLogRepository extends CrudRepository <ProductEventLog, ProductEventKey>{

    List<ProductEventLog> findAllByPk(String code);

    List<ProductEventLog> findAllByPkAndSkStartsWith(String code, String eventType);
}