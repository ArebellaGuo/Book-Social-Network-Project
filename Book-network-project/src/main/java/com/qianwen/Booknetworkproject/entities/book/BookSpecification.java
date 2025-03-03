package com.qianwen.Booknetworkproject.entities.book;

import org.springframework.data.jpa.domain.Specification;

/**
 * This class is to define dynamic SQL generation.
 */
public class BookSpecification {
    // this method return Book obj
    public static Specification<Book> withOwnerId(String ownerEmail) {
        /**
         * Root: class which we search for
         * CriteriaQuery: to create CriteriaQuery constructor used to change query condition
         *      eg: query.SELECT("condtion") -> SELECT conditon
         *          query.distinct(true) -> SELECT DISTINCT
         * CriteriaBuilder: create query conditions -> WHERE
         *
         */
        return (root,
                query,
                criteriaBuilder)
                -> criteriaBuilder.equal(
                        root.get("createdBy"),//get owned field from Book
                ownerEmail); //
    }
}