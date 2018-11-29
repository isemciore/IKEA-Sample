package com.erik.ikeashoppinglist.demo.respository;

import com.erik.ikeashoppinglist.demo.misc_com_fmt.SearchCriteria;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SearchSpecificationBuilder<T> {
    private final List<SearchCriteria> params;

    public SearchSpecificationBuilder() {
        params = new ArrayList<>();
    }

    public SearchSpecificationBuilder with(String key, String operation, Object value, String matchType) {
        params.add(new SearchCriteria(key, operation, value, matchType));
        return this;
    }

    public Specification<T> build() {
        if (params.isEmpty()) {
            return null;
        }

        List<Specification> specs = params.stream()
                .map(SearchSpecification<T>::new)
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
        }
        return result;
    }
}
