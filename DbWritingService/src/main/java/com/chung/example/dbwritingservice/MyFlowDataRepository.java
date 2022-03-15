package com.chung.example.dbwritingservice;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MyFlowDataRepository extends CrudRepository<MyFlowData, Long> {

}
