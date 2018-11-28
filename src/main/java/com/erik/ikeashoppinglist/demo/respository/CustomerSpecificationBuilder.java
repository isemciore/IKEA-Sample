package com.erik.ikeashoppinglist.demo.respository;

import com.erik.ikeashoppinglist.demo.entity.Customer;
import com.erik.ikeashoppinglist.demo.misc_com_fmt.SearchCriteria;
import org.springframework.data.jpa.domain.Specification;
import java.util.*;
import java.util.stream.Collectors;

public class CustomerSpecificationBuilder {
    private final List<SearchCriteria> params;

    public CustomerSpecificationBuilder() {
        params = new ArrayList<SearchCriteria>();
    }

    public CustomerSpecificationBuilder with(String key, String operation, Object value, String match_type) {
        params.add(new SearchCriteria(key, operation, value, match_type));
        return this;
    }

    public Specification<Customer> build() {
        if (params.size() == 0) {
            return null;
        }

        List<Specification> specs = params.stream()
                .map(CustomerSpecification::new)
                .collect(Collectors.toList());

        Specification result = specs.get(0);

        for (int i = 1; i < params.size(); i++) {
            if (",&".contains(String.valueOf(params.get(i-1).getMatch_type()))){
                result = Specification.where(result).and(specs.get(i));
            } else if ("|".contains(String.valueOf(params.get(i-1).getMatch_type()))){
                result = Specification.where(result).or(specs.get(i));
            } else if ("!".contains(String.valueOf(params.get(i-1).getMatch_type()))){
                result = Specification.not(specs.get(i)).and(result);
            }
            System.out.println("Pause here");
            //result = params.get(i).isOrPredicate() ? Specification.where(result).or(specs.get(i)):
            // Specification.where(result).and(specs.get(i));
        }
        return result;
    }
}
