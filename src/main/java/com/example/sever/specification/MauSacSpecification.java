package com.example.sever.specification;

import com.example.sever.entity.MauSac;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;


public class MauSacSpecification {
    public static Specification<MauSac> filterByKeywordAndTrangThai(String keyword, Integer trangThai) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (trangThai != null) {
                predicates.add(cb.equal(root.get("trangThai"), trangThai));
            }

            if (keyword != null && !keyword.trim().isEmpty()) {
                String[] terms = keyword.trim().split("\\s+");
                List<Predicate> keywordPredicates = new ArrayList<>();

                for (String term : terms) {
                    String likeTerm = "%" + term.toLowerCase() + "%";
                    keywordPredicates.add(cb.or(
                            cb.like(cb.lower(root.get("idMauSac")), likeTerm),
                            cb.like(cb.lower(root.get("ten")), likeTerm)
                    ));
                }

                predicates.add(cb.and(keywordPredicates.toArray(new Predicate[0])));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
