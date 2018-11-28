package com.erik.ikeashoppinglist.demo.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface SearchableRepository<T, K> extends JpaRepository<T, K>, JpaSpecificationExecutor<T> {

}
